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
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import static net.sourceforge.tess4j.TessAPI.INSTANCE;
import net.sourceforge.tess4j.util.ImageIOHelper;

public class TextTools {
    
    private static final String TESSDATA_DIR = "/usr/share/tesseract/tessdata";
    public static final double STD_ALIGNMENT_THRESHOLD = 1;
    public enum Alignment {Left, Right, Center, Justified};


    /**
     * Takes an image and extracts text from it using OCR.
     * @param imagePath Path of the image
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
            e.printStackTrace();
        }
        
        return result;
    }
    
    /**
     * Get the Levensthein distance between two strings.
     *
     * Algorithm from https://en.wikibooks.org/wiki/Algorithm_Implementation/Strings/Levenshtein_distance#Java
     * 
     * @param a a string
     * @param b a string
     * @return the Levensthein distance between the strings.
     */
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
     * Reads text from an image and returns a map of how the text is aligned
     * and its mean value.
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
    public static Map<Alignment,Float> getAlignments(String imagePath) {
        ArrayList<ArrayList<Word>> lines = TextTools.getLines(imagePath);
        ArrayList<Word> lastLine = lines.remove(lines.size()-1);
        Map<Alignment, Float> allButTheLast = getAlignments(lines);
        lines.add(lastLine);
        Map<Alignment, Float> all = getAlignments(lines);
        
        // Return justified if all but the last lines are justified and the 
        // last line is left aligned.
        if (allButTheLast.containsKey(Alignment.Justified) && all.containsKey(Alignment.Left)) {
            return allButTheLast;
        } else {
            return getAlignments(lines);
        }
    }
    
    /**
     * Returns a map of how a set of sentences are aligned and the mean value
     * if the alignment coordinates.
     * 
     * The text is read line by line and the left, right and center position
     * of each line is compared. The alignment is determined by calculating
     * the standard deviation of the left, right and center positions of each
     * line. If the lines all start at the same left position +/- the threshold,
     * we say that the text is left aligned.
     * 
     * @param lines a list of lines
     * @return A list of all the alignments that are valid for the text.
     */    
    public static Map<Alignment,Float> getAlignments(ArrayList<ArrayList<Word>> lines) {
        
        Map<Alignment,Float> resultMap = new HashMap<>();
        ArrayList<Integer> leftPositions = new ArrayList<>();
        int sumLeftPositions = 0;
        ArrayList<Integer> rightPositions = new ArrayList<>();
        int sumRightPositions = 0;
        ArrayList<Float> centerPositions = new ArrayList<>();
        int sumCenterPositions = 0;
        
        for (ArrayList<Word> line : lines) {
            Word leftmost = line.get(0);
            Word rightmost = line.get(line.size()-1);
            
            int left = leftmost.getLeft();
            int right = rightmost.getRight();
            float center = (((float)right - left) / 2) + left;
            
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
        
        // Add all alignments that are valid for the text
        if (stdLeft <= STD_ALIGNMENT_THRESHOLD) {
            resultMap.put(Alignment.Left,meanLeft);
        }
        
        if (stdRight <= STD_ALIGNMENT_THRESHOLD) {
            resultMap.put(Alignment.Right,meanRight);
        }
                
        if (stdCenter <= STD_ALIGNMENT_THRESHOLD) {
            resultMap.put(Alignment.Center,meanCenter);
        }
        
        if (stdLeft <= STD_ALIGNMENT_THRESHOLD && 
            stdRight <= STD_ALIGNMENT_THRESHOLD) {
        	resultMap.put(Alignment.Justified,meanCenter);
        }
        
        return resultMap;
    }
        
    public static class TessDllAPIImpl implements TessAPI {
        public TessAPI getInstance() {
            return INSTANCE;
        }

        @Override
        public String TessVersion() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void TessDeleteText(Pointer pntr) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void TessDeleteTextArray(PointerByReference pbr) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void TessDeleteIntArray(IntBuffer ib) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public TessResultRenderer TessTextRendererCreate() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public TessResultRenderer TessHOcrRendererCreate() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public TessResultRenderer TessPDFRendererCreate(String string) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public TessResultRenderer TessUnlvRendererCreate() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public TessResultRenderer TessBoxTextRendererCreate() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void TessDeleteResultRenderer(TessResultRenderer trr) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void TessResultRendererInsert(TessResultRenderer trr, TessResultRenderer trr1) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public TessResultRenderer TessResultRendererNext(TessResultRenderer trr) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int TessResultRendererBeginDocument(TessResultRenderer trr, String string) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int TessResultRendererAddImage(TessResultRenderer trr, PointerByReference pbr) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int TessResultRendererAddError(TessResultRenderer trr, PointerByReference pbr) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int TessResultRendererEndDocument(TessResultRenderer trr) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int TessResultRendererGetOutput(TessResultRenderer trr, PointerByReference pbr, IntByReference ibr) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Pointer TessResultRendererTypename(TessResultRenderer trr) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Pointer TessResultRendererExtention(TessResultRenderer trr) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Pointer TessResultRendererTitle(TessResultRenderer trr) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int TessResultRendererImageNum(TessResultRenderer trr) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public TessBaseAPI TessBaseAPICreate() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void TessBaseAPIDelete(TessBaseAPI tbapi) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void TessBaseAPISetInputName(TessBaseAPI tbapi, String string) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public String TessBaseAPIGetInputName(TessBaseAPI tbapi) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int TessBaseAPIGetSourceYResolution(TessBaseAPI tbapi) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public String TessBaseAPIGetDatapath(TessBaseAPI tbapi) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void TessBaseAPISetOutputName(TessBaseAPI tbapi, String string) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int TessBaseAPISetVariable(TessBaseAPI tbapi, String string, String string1) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int TessBaseAPIGetIntVariable(TessBaseAPI tbapi, String string, IntBuffer ib) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int TessBaseAPIGetBoolVariable(TessBaseAPI tbapi, String string, IntBuffer ib) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int TessBaseAPIGetDoubleVariable(TessBaseAPI tbapi, String string, DoubleBuffer db) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public String TessBaseAPIGetStringVariable(TessBaseAPI tbapi, String string) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void TessBaseAPIPrintVariablesToFile(TessBaseAPI tbapi, String string) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int TessBaseAPIInit1(TessBaseAPI tbapi, String string, String string1, int i, PointerByReference pbr, int i1) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int TessBaseAPIInit2(TessBaseAPI tbapi, String string, String string1, int i) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int TessBaseAPIInit3(TessBaseAPI tbapi, String string, String string1) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int TessBaseAPIInit4(TessBaseAPI tbapi, String string, String string1, int i, PointerByReference pbr, int i1, PointerByReference pbr1, PointerByReference pbr2, NativeSize ns, int i2) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public String TessBaseAPIGetInitLanguagesAsString(TessBaseAPI tbapi) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public PointerByReference TessBaseAPIGetLoadedLanguagesAsVector(TessBaseAPI tbapi) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public PointerByReference TessBaseAPIGetAvailableLanguagesAsVector(TessBaseAPI tbapi) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int TessBaseAPIInitLangMod(TessBaseAPI tbapi, String string, String string1) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void TessBaseAPIInitForAnalysePage(TessBaseAPI tbapi) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void TessBaseAPIReadConfigFile(TessBaseAPI tbapi, String string, int i) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void TessBaseAPISetPageSegMode(TessBaseAPI tbapi, int i) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int TessBaseAPIGetPageSegMode(TessBaseAPI tbapi) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Pointer TessBaseAPIRect(TessBaseAPI tbapi, ByteBuffer bb, int i, int i1, int i2, int i3, int i4, int i5) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void TessBaseAPIClearAdaptiveClassifier(TessBaseAPI tbapi) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void TessBaseAPISetImage(TessBaseAPI tbapi, ByteBuffer bb, int i, int i1, int i2, int i3) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void TessBaseAPISetSourceResolution(TessBaseAPI tbapi, int i) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void TessBaseAPISetRectangle(TessBaseAPI tbapi, int i, int i1, int i2, int i3) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int TessBaseAPIGetThresholdedImageScaleFactor(TessBaseAPI tbapi) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void TessBaseAPIDumpPGM(TessBaseAPI tbapi, String string) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public TessPageIterator TessBaseAPIAnalyseLayout(TessBaseAPI tbapi) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int TessBaseAPIRecognize(TessBaseAPI tbapi, ETEXT_DESC e) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int TessBaseAPIRecognizeForChopTest(TessBaseAPI tbapi, ETEXT_DESC e) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public TessResultIterator TessBaseAPIGetIterator(TessBaseAPI tbapi) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public TessMutableIterator TessBaseAPIGetMutableIterator(TessBaseAPI tbapi) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Pointer TessBaseAPIProcessPages(TessBaseAPI tbapi, String string, String string1, int i) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int TessBaseAPIProcessPages1(TessBaseAPI tbapi, String string, String string1, int i, TessResultRenderer trr) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Pointer TessBaseAPIGetUTF8Text(TessBaseAPI tbapi) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Pointer TessBaseAPIGetHOCRText(TessBaseAPI tbapi, int i) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Pointer TessBaseAPIGetBoxText(TessBaseAPI tbapi, int i) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Pointer TessBaseAPIGetUNLVText(TessBaseAPI tbapi) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int TessBaseAPIMeanTextConf(TessBaseAPI tbapi) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public IntByReference TessBaseAPIAllWordConfidences(TessBaseAPI tbapi) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int TessBaseAPIAdaptToWordStr(TessBaseAPI tbapi, int i, String string) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void TessBaseAPIClear(TessBaseAPI tbapi) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void TessBaseAPIEnd(TessBaseAPI tbapi) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int TessBaseAPIIsValidWord(TessBaseAPI tbapi, String string) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int TessBaseAPIGetTextDirection(TessBaseAPI tbapi, IntBuffer ib, FloatBuffer fb) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void TessBaseAPIClearPersistentCache(TessBaseAPI tbapi) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public String TessBaseAPIGetUnichar(TessBaseAPI tbapi, int i) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void TessPageIteratorDelete(TessPageIterator tpi) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public TessPageIterator TessPageIteratorCopy(TessPageIterator tpi) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void TessPageIteratorBegin(TessPageIterator tpi) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int TessPageIteratorNext(TessPageIterator tpi, int i) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int TessPageIteratorIsAtBeginningOf(TessPageIterator tpi, int i) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int TessPageIteratorIsAtFinalElement(TessPageIterator tpi, int i, int i1) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int TessPageIteratorBoundingBox(TessPageIterator tpi, int i, IntBuffer ib, IntBuffer ib1, IntBuffer ib2, IntBuffer ib3) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int TessPageIteratorBlockType(TessPageIterator tpi) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int TessPageIteratorBaseline(TessPageIterator tpi, int i, IntBuffer ib, IntBuffer ib1, IntBuffer ib2, IntBuffer ib3) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void TessPageIteratorOrientation(TessPageIterator tpi, IntBuffer ib, IntBuffer ib1, IntBuffer ib2, FloatBuffer fb) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void TessResultIteratorDelete(TessResultIterator tri) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public TessResultIterator TessResultIteratorCopy(TessResultIterator tri) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public TessPageIterator TessResultIteratorGetPageIterator(TessResultIterator tri) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public TessPageIterator TessResultIteratorGetPageIteratorConst(TessResultIterator tri) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Pointer TessResultIteratorGetUTF8Text(TessResultIterator tri, int i) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public float TessResultIteratorConfidence(TessResultIterator tri, int i) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public String TessResultIteratorWordFontAttributes(TessResultIterator tri, IntBuffer ib, IntBuffer ib1, IntBuffer ib2, IntBuffer ib3, IntBuffer ib4, IntBuffer ib5, IntBuffer ib6, IntBuffer ib7) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int TessResultIteratorWordIsFromDictionary(TessResultIterator tri) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int TessResultIteratorWordIsNumeric(TessResultIterator tri) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int TessResultIteratorSymbolIsSuperscript(TessResultIterator tri) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int TessResultIteratorSymbolIsSubscript(TessResultIterator tri) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int TessResultIteratorSymbolIsDropcap(TessResultIterator tri) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
}