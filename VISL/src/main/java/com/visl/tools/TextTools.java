/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.visl.tools;

import com.ochafik.lang.jnaerator.runtime.NativeSize;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import java.awt.image.BufferedImage;
import net.sourceforge.tess4j.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import static net.sourceforge.tess4j.TessAPI.INSTANCE;
import net.sourceforge.tess4j.util.ImageIOHelper;

public class TextTools {
    
    private static final String TESSDATA_DIR = "/usr/share/tesseract/tessdata";
    private static double STD_ALIGNMENT_THRESHOLD = 1;
    public enum Alignment {Left, Right, Center, Justified};


    /**
     * Takes an image and extracts text from it using OCR.
     * @param imagePath: Path of the image
     * @return String The text found in the image
     */
    public static String getTextFromImage(String imagePath) {
        // Creating Tesseract Interface
        String result = "";
        Tesseract instance = new Tesseract();
        instance.setDatapath("/usr/local/share/tessdata");
        File imageFile = new File(imagePath);
        
        // do OCR method of Tesseract gets the text from the captured Screenshot
        try {
            result = instance.doOCR(imageFile);
            System.out.println(result);
        } catch (TesseractException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return result;
    }
    
    // Algorithm from https://en.wikibooks.org/wiki/Algorithm_Implementation/Strings/Levenshtein_distance#Java
    public static int getLevenstheinDistance(String a, String b) {
        int len0 = a.length() + 1;                                                     
        int len1 = b.length() + 1;                                                     

        // the array of distances                                                       
        int[] cost = new int[len0];                                                     
        int[] newcost = new int[len0];                                                  

        // initial cost of skipping prefix in String s0                                 
        for (int i = 0; i < len0; i++) cost[i] = i;                                     

        // dynamically computing the array of distances                                  

        // transformation cost for each letter in s1                                    
        for (int j = 1; j < len1; j++) {                                                
            // initial cost of skipping prefix in String s1                             
            newcost[0] = j;                                                             

            // transformation cost for each letter in s0                                
            for(int i = 1; i < len0; i++) {                                             
                // matching current letters in both strings                             
                int match = (a.charAt(i - 1) == b.charAt(j - 1)) ? 0 : 1;             

                // computing cost for each transformation                               
                int cost_replace = cost[i - 1] + match;                                 
                int cost_insert  = cost[i] + 1;                                         
                int cost_delete  = newcost[i - 1] + 1;                                  

                // keep minimum cost                                                    
                newcost[i] = Math.min(Math.min(cost_insert, cost_delete), cost_replace);
            }                                                                           

            // swap cost/newcost arrays                                                 
            int[] swap = cost; cost = newcost; newcost = swap;                          
        }                                                                               

        // the distance is the cost for transforming all letters in both strings        
        return cost[len0 - 1];
    }
    
    /**
     * Get words from an image.
     * 
     * Iterates over all words in an image and returns a list of all words with
     * their identified properties.
     * 
     * @param imagePath The path to the image.
     * @return words ArrayList of words
     * @throws Exception 
     */
    public static ArrayList<ArrayList<Word>> getLines(String imagePath) {
        ArrayList<Word> line = new ArrayList<>();
        ArrayList<ArrayList<Word>> lines = new ArrayList<>();
        lines.add(line);        

        try {          
            String lang = "eng";
            File tiff = new File(imagePath);
            BufferedImage image = ImageIO.read(new FileInputStream(tiff)); // require jai-imageio lib to read TIFF
            ByteBuffer buf = ImageIOHelper.convertImageData(image);
            int bpp = image.getColorModel().getPixelSize();
            int bytespp = bpp / 8;
            int bytespl = (int) Math.ceil(image.getWidth() * bpp / 8.0);
            TessAPI api = new TessDllAPIImpl().getInstance();
            TessAPI.TessBaseAPI handle = api.TessBaseAPICreate();
            
            api.TessBaseAPIInit3(handle, TESSDATA_DIR, lang);
            api.TessBaseAPISetPageSegMode(handle, TessAPI.TessPageSegMode.PSM_AUTO);
            api.TessBaseAPISetImage(handle, buf, image.getWidth(), image.getHeight(), bytespp, bytespl);
            api.TessBaseAPIRecognize(handle, null);
            
            TessAPI.TessResultIterator ri = api.TessBaseAPIGetIterator(handle);
            TessAPI.TessPageIterator pi = api.TessResultIteratorGetPageIterator(ri);
            api.TessPageIteratorBegin(pi);
            
            Word lastWord = null;
            
            do {
                Pointer ptr = api.TessResultIteratorGetUTF8Text(ri, TessAPI1.TessPageIteratorLevel.RIL_WORD);
                String str = ptr.getString(0);
                IntBuffer leftB = IntBuffer.allocate(1);
                IntBuffer topB = IntBuffer.allocate(1);
                IntBuffer rightB = IntBuffer.allocate(1);
                IntBuffer bottomB = IntBuffer.allocate(1);
                api.TessPageIteratorBoundingBox(pi, TessAPI.TessPageIteratorLevel.RIL_WORD, leftB, topB, rightB, bottomB);
                int left = leftB.get();
                int top = topB.get();
                int right = rightB.get();
                int bottom = bottomB.get();
                
                IntBuffer boldB = IntBuffer.allocate(1);
                IntBuffer italicB = IntBuffer.allocate(1);
                IntBuffer underlinedB = IntBuffer.allocate(1);
                IntBuffer monospaceB = IntBuffer.allocate(1);
                IntBuffer serifB = IntBuffer.allocate(1);
                IntBuffer smallcapsB = IntBuffer.allocate(1);
                IntBuffer pointSizeB = IntBuffer.allocate(1);
                IntBuffer fontIdB = IntBuffer.allocate(1);
                String fontName = api.TessResultIteratorWordFontAttributes(ri, boldB, italicB, underlinedB,
                        monospaceB, serifB, smallcapsB, pointSizeB, fontIdB);
                boolean bold = boldB.get() == TessAPI.TRUE;
                boolean italic = italicB.get() == TessAPI.TRUE;
                boolean underlined = underlinedB.get() == TessAPI.TRUE;
                boolean monospace = monospaceB.get() == TessAPI.TRUE;
                boolean serif = serifB.get() == TessAPI.TRUE;
                boolean smallcaps = smallcapsB.get() == TessAPI.TRUE;
                int pointSize = pointSizeB.get();
                int fontId = fontIdB.get();
                
                Word word = new Word(str, pointSize, left, top, right, bottom);
                
                // If the new word starts below the bottom of the last word,
                // we have a new line.
                if (lastWord != null && word.getTop() > lastWord.getBottom()) {
                    line = new ArrayList<>();
                    lines.add(line);
                }
                
                line.add(word);
                lastWord = word;
                System.out.println("NEW WORD: "+word.getTop()+" "+word.getLeft()+" "+word.getBottom()+" "+word.getRight());
            } while (api.TessPageIteratorNext(pi, TessAPI.TessPageIteratorLevel.RIL_WORD) == TessAPI.TRUE);
        } catch (IOException ex) {
            Logger.getLogger(TextTools.class.getName()).log(Level.SEVERE, "Could not open file "+imagePath, ex);
        }
        
        return lines;
    }
    
    /**
     * Reads text from an image and returns a list of how the text is aligned.
     * 
     * The text is read line by line and the left, right and center position
     * of each line is compared. The alignment is determined by calculating
     * the standard deviation of the left, right and center positions of each
     * line. If the lines all start at the same left position +/- the threshold,
     * we say that the text is left aligned.
     * 
     * @param imagePath
     * @return A list of all the alignments that are valid for the text.
     */
    public static ArrayList<Alignment> getAlignments(String imagePath) {
        
        ArrayList<Integer> leftPositions = new ArrayList<>();
        int sumLeftPositions = 0;
        ArrayList<Integer> rightPositions = new ArrayList<>();
        int sumRightPositions = 0;
        ArrayList<Float> centerPositions = new ArrayList<>();
        int sumCenterPositions = 0;
        
        for (ArrayList<Word> line : TextTools.getLines(imagePath)) {
            Word leftmost = line.get(0);
            Word rightmost = line.get(line.size()-1);
            
            int left = leftmost.getLeft();
            int right = rightmost.getRight();
            float center = (right - left) / 2;
            
            sumLeftPositions += left;
            sumRightPositions += right;
            sumCenterPositions += center;
            
            leftPositions.add(left);
            rightPositions.add(right);
            centerPositions.add(center);
        }
        
        float meanLeft = (float) sumLeftPositions / leftPositions.size();
        float meanRight = (float) sumRightPositions / rightPositions.size();
        float meanCenter = (float) sumCenterPositions / centerPositions.size();
        
        double varianceLeft = 0;
        double varianceRight = 0;
        double varianceCenter = 0;
        
        for (int i=0; i<leftPositions.size(); i++) {
            varianceLeft += Math.pow(leftPositions.get(i)-meanLeft, 2);
            varianceRight += Math.pow(rightPositions.get(i)-meanRight, 2);
            varianceCenter += Math.pow(centerPositions.get(i)-meanCenter, 2);
        }
        
        double stdLeft = Math.sqrt(varianceLeft / leftPositions.size());
        double stdRight = Math.sqrt(varianceRight / rightPositions.size());
        double stdCenter = Math.sqrt(varianceCenter / centerPositions.size());
        
        System.out.println("Left std: "+stdLeft);
        System.out.println("Right std: "+stdRight);
        System.out.println("Center std: "+stdCenter);
        
        ArrayList<Alignment> alignments = new ArrayList<>();
        
        // Add all alignments that are valid for the text
        if (stdLeft <= STD_ALIGNMENT_THRESHOLD) {
            alignments.add(Alignment.Left);
        }
        
        if (stdRight <= STD_ALIGNMENT_THRESHOLD) {
            alignments.add(Alignment.Right);
        }
                
        if (stdCenter <= STD_ALIGNMENT_THRESHOLD) {
            alignments.add(Alignment.Center);
        }
        
        // TODO: add support for justified alignment
        // The text is justified if both left and right std is below the 
        // threshold.
        
        return alignments;
    }
        
    public static class TessDllAPIImpl implements TessAPI {
        public TessAPI getInstance() {
            return INSTANCE;
        }

        @Override
        public String TessVersion() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void TessDeleteText(Pointer pntr) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void TessDeleteTextArray(PointerByReference pbr) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void TessDeleteIntArray(IntBuffer ib) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public ITessAPI.TessResultRenderer TessTextRendererCreate() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public ITessAPI.TessResultRenderer TessHOcrRendererCreate() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public ITessAPI.TessResultRenderer TessPDFRendererCreate(String string) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public ITessAPI.TessResultRenderer TessUnlvRendererCreate() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public ITessAPI.TessResultRenderer TessBoxTextRendererCreate() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void TessDeleteResultRenderer(ITessAPI.TessResultRenderer trr) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void TessResultRendererInsert(ITessAPI.TessResultRenderer trr, ITessAPI.TessResultRenderer trr1) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public ITessAPI.TessResultRenderer TessResultRendererNext(ITessAPI.TessResultRenderer trr) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public int TessResultRendererBeginDocument(ITessAPI.TessResultRenderer trr, String string) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public int TessResultRendererAddImage(ITessAPI.TessResultRenderer trr, PointerByReference pbr) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public int TessResultRendererAddError(ITessAPI.TessResultRenderer trr, PointerByReference pbr) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public int TessResultRendererEndDocument(ITessAPI.TessResultRenderer trr) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public int TessResultRendererGetOutput(ITessAPI.TessResultRenderer trr, PointerByReference pbr, IntByReference ibr) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public Pointer TessResultRendererTypename(ITessAPI.TessResultRenderer trr) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public Pointer TessResultRendererExtention(ITessAPI.TessResultRenderer trr) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public Pointer TessResultRendererTitle(ITessAPI.TessResultRenderer trr) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public int TessResultRendererImageNum(ITessAPI.TessResultRenderer trr) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public ITessAPI.TessBaseAPI TessBaseAPICreate() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void TessBaseAPIDelete(ITessAPI.TessBaseAPI tbapi) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void TessBaseAPISetInputName(ITessAPI.TessBaseAPI tbapi, String string) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public String TessBaseAPIGetInputName(ITessAPI.TessBaseAPI tbapi) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public int TessBaseAPIGetSourceYResolution(ITessAPI.TessBaseAPI tbapi) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public String TessBaseAPIGetDatapath(ITessAPI.TessBaseAPI tbapi) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void TessBaseAPISetOutputName(ITessAPI.TessBaseAPI tbapi, String string) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public int TessBaseAPISetVariable(ITessAPI.TessBaseAPI tbapi, String string, String string1) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public int TessBaseAPIGetIntVariable(ITessAPI.TessBaseAPI tbapi, String string, IntBuffer ib) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public int TessBaseAPIGetBoolVariable(ITessAPI.TessBaseAPI tbapi, String string, IntBuffer ib) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public int TessBaseAPIGetDoubleVariable(ITessAPI.TessBaseAPI tbapi, String string, DoubleBuffer db) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public String TessBaseAPIGetStringVariable(ITessAPI.TessBaseAPI tbapi, String string) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void TessBaseAPIPrintVariablesToFile(ITessAPI.TessBaseAPI tbapi, String string) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public int TessBaseAPIInit1(ITessAPI.TessBaseAPI tbapi, String string, String string1, int i, PointerByReference pbr, int i1) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public int TessBaseAPIInit2(ITessAPI.TessBaseAPI tbapi, String string, String string1, int i) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public int TessBaseAPIInit3(ITessAPI.TessBaseAPI tbapi, String string, String string1) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public int TessBaseAPIInit4(ITessAPI.TessBaseAPI tbapi, String string, String string1, int i, PointerByReference pbr, int i1, PointerByReference pbr1, PointerByReference pbr2, NativeSize ns, int i2) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public String TessBaseAPIGetInitLanguagesAsString(ITessAPI.TessBaseAPI tbapi) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public PointerByReference TessBaseAPIGetLoadedLanguagesAsVector(ITessAPI.TessBaseAPI tbapi) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public PointerByReference TessBaseAPIGetAvailableLanguagesAsVector(ITessAPI.TessBaseAPI tbapi) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public int TessBaseAPIInitLangMod(ITessAPI.TessBaseAPI tbapi, String string, String string1) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void TessBaseAPIInitForAnalysePage(ITessAPI.TessBaseAPI tbapi) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void TessBaseAPIReadConfigFile(ITessAPI.TessBaseAPI tbapi, String string, int i) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void TessBaseAPISetPageSegMode(ITessAPI.TessBaseAPI tbapi, int i) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public int TessBaseAPIGetPageSegMode(ITessAPI.TessBaseAPI tbapi) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public Pointer TessBaseAPIRect(ITessAPI.TessBaseAPI tbapi, ByteBuffer bb, int i, int i1, int i2, int i3, int i4, int i5) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void TessBaseAPIClearAdaptiveClassifier(ITessAPI.TessBaseAPI tbapi) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void TessBaseAPISetImage(ITessAPI.TessBaseAPI tbapi, ByteBuffer bb, int i, int i1, int i2, int i3) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void TessBaseAPISetSourceResolution(ITessAPI.TessBaseAPI tbapi, int i) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void TessBaseAPISetRectangle(ITessAPI.TessBaseAPI tbapi, int i, int i1, int i2, int i3) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public int TessBaseAPIGetThresholdedImageScaleFactor(ITessAPI.TessBaseAPI tbapi) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void TessBaseAPIDumpPGM(ITessAPI.TessBaseAPI tbapi, String string) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public ITessAPI.TessPageIterator TessBaseAPIAnalyseLayout(ITessAPI.TessBaseAPI tbapi) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public int TessBaseAPIRecognize(ITessAPI.TessBaseAPI tbapi, ITessAPI.ETEXT_DESC e) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public int TessBaseAPIRecognizeForChopTest(ITessAPI.TessBaseAPI tbapi, ITessAPI.ETEXT_DESC e) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public ITessAPI.TessResultIterator TessBaseAPIGetIterator(ITessAPI.TessBaseAPI tbapi) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public ITessAPI.TessMutableIterator TessBaseAPIGetMutableIterator(ITessAPI.TessBaseAPI tbapi) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public Pointer TessBaseAPIProcessPages(ITessAPI.TessBaseAPI tbapi, String string, String string1, int i) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public int TessBaseAPIProcessPages1(ITessAPI.TessBaseAPI tbapi, String string, String string1, int i, ITessAPI.TessResultRenderer trr) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public Pointer TessBaseAPIGetUTF8Text(ITessAPI.TessBaseAPI tbapi) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public Pointer TessBaseAPIGetHOCRText(ITessAPI.TessBaseAPI tbapi, int i) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public Pointer TessBaseAPIGetBoxText(ITessAPI.TessBaseAPI tbapi, int i) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public Pointer TessBaseAPIGetUNLVText(ITessAPI.TessBaseAPI tbapi) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public int TessBaseAPIMeanTextConf(ITessAPI.TessBaseAPI tbapi) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public IntByReference TessBaseAPIAllWordConfidences(ITessAPI.TessBaseAPI tbapi) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public int TessBaseAPIAdaptToWordStr(ITessAPI.TessBaseAPI tbapi, int i, String string) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void TessBaseAPIClear(ITessAPI.TessBaseAPI tbapi) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void TessBaseAPIEnd(ITessAPI.TessBaseAPI tbapi) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public int TessBaseAPIIsValidWord(ITessAPI.TessBaseAPI tbapi, String string) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public int TessBaseAPIGetTextDirection(ITessAPI.TessBaseAPI tbapi, IntBuffer ib, FloatBuffer fb) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void TessBaseAPIClearPersistentCache(ITessAPI.TessBaseAPI tbapi) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public String TessBaseAPIGetUnichar(ITessAPI.TessBaseAPI tbapi, int i) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void TessPageIteratorDelete(ITessAPI.TessPageIterator tpi) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public ITessAPI.TessPageIterator TessPageIteratorCopy(ITessAPI.TessPageIterator tpi) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void TessPageIteratorBegin(ITessAPI.TessPageIterator tpi) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public int TessPageIteratorNext(ITessAPI.TessPageIterator tpi, int i) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public int TessPageIteratorIsAtBeginningOf(ITessAPI.TessPageIterator tpi, int i) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public int TessPageIteratorIsAtFinalElement(ITessAPI.TessPageIterator tpi, int i, int i1) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public int TessPageIteratorBoundingBox(ITessAPI.TessPageIterator tpi, int i, IntBuffer ib, IntBuffer ib1, IntBuffer ib2, IntBuffer ib3) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public int TessPageIteratorBlockType(ITessAPI.TessPageIterator tpi) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public int TessPageIteratorBaseline(ITessAPI.TessPageIterator tpi, int i, IntBuffer ib, IntBuffer ib1, IntBuffer ib2, IntBuffer ib3) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void TessPageIteratorOrientation(ITessAPI.TessPageIterator tpi, IntBuffer ib, IntBuffer ib1, IntBuffer ib2, FloatBuffer fb) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void TessResultIteratorDelete(ITessAPI.TessResultIterator tri) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public ITessAPI.TessResultIterator TessResultIteratorCopy(ITessAPI.TessResultIterator tri) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public ITessAPI.TessPageIterator TessResultIteratorGetPageIterator(ITessAPI.TessResultIterator tri) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public ITessAPI.TessPageIterator TessResultIteratorGetPageIteratorConst(ITessAPI.TessResultIterator tri) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public Pointer TessResultIteratorGetUTF8Text(ITessAPI.TessResultIterator tri, int i) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public float TessResultIteratorConfidence(ITessAPI.TessResultIterator tri, int i) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public String TessResultIteratorWordFontAttributes(ITessAPI.TessResultIterator tri, IntBuffer ib, IntBuffer ib1, IntBuffer ib2, IntBuffer ib3, IntBuffer ib4, IntBuffer ib5, IntBuffer ib6, IntBuffer ib7) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public int TessResultIteratorWordIsFromDictionary(ITessAPI.TessResultIterator tri) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public int TessResultIteratorWordIsNumeric(ITessAPI.TessResultIterator tri) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public int TessResultIteratorSymbolIsSuperscript(ITessAPI.TessResultIterator tri) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public int TessResultIteratorSymbolIsSubscript(ITessAPI.TessResultIterator tri) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public int TessResultIteratorSymbolIsDropcap(ITessAPI.TessResultIterator tri) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }
}