package com.visl.tools;


import com.google.common.io.Files;
import com.ochafik.lang.jnaerator.runtime.NativeSize;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import com.visl.exceptions.InvalidDimensionsException;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import net.sourceforge.tess4j.ITessAPI;
import net.sourceforge.tess4j.TessAPI;
import net.sourceforge.tess4j.TessAPI1;
import net.sourceforge.tess4j.util.ImageIOHelper;
import org.opencv.core.Core;
import org.opencv.core.CvException;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;


public class ImageTools {
    
    private static Logger log = Logger.getLogger(ImageTools.class.getName());

	/**
	 * Takes 2 images and return a similarity score
	 * @param pathFirst: Path of first image
	 * @param pathTwo: : Path of second image
	 * @return similarity percentage
	 */
	public static float compareImages(String pathFirst, String pathTwo) throws FileNotFoundException {
            Mat first = Highgui.imread(pathFirst, Highgui.CV_LOAD_IMAGE_COLOR);
                if (first.empty()) {
                    throw new FileNotFoundException("Could not load image "+pathFirst);
                }
                
    		Mat second = Highgui.imread(pathTwo, Highgui.CV_LOAD_IMAGE_COLOR);
                if (second.empty()) {
                    throw new FileNotFoundException("Could not load image "+pathTwo);
                }

		// Calculating minimum rows and columns
		int desRows = first.rows() > second.rows() ? second.rows() : first.rows();
		int desCols = first.cols() > second.cols() ? second.cols() : first.cols();
		
		//Error check
		if (desRows <= 70 || desCols <= 70)
		{
			return -1;
		}
		
		// Resizing larger image to the size of smaller one for comparison purposes
                Size sz = new Size(desCols, desRows);

		Mat firstResized = new Mat();
		Mat secondResized = new Mat();

                Imgproc.resize(first, firstResized, sz);
		Imgproc.resize(second, secondResized, sz);

		Mat result1 = new Mat();
		Mat result2 = new Mat();
		float similarityScore;

		// Obtain differences in image
                Core.subtract(firstResized, secondResized, result1);
		Core.subtract(secondResized,firstResized, result2);

		// See how many pixels are different
		int magnitudeOfDifference = Math.max(Core.countNonZero(result1),Core.countNonZero(result2));

		// Total Pixels
		int size = result1.rows() * result1.cols();

		// Report similarity
		if (magnitudeOfDifference == 0) {
			similarityScore = 0;
		} else {
			similarityScore = (((float) magnitudeOfDifference / (float) size)) * 100;
		}

                first.release();
                second.release();
		firstResized.release();
		secondResized.release();
		result1.release();
		result2.release();
                
		System.out.println("Similarity Score: "+(100 - similarityScore));
		return similarityScore;
	}
        
        public static boolean hasSubImage(String source, String template, boolean exact) throws FileNotFoundException {
            verifyFile(source);
            verifyFile(template);
            
            int match_method = Imgproc.TM_CCORR_NORMED;
            
            Mat img = Highgui.imread(source, 1);
            Mat templ = Highgui.imread(template, 1);
            Mat result = new Mat();
            
            if (templ.cols() > img.cols() || templ.rows() > img.rows()) {
                log.log(Level.WARNING, "Template image is larger than the source!");
                return false;
            }
            
            int result_cols =  img.cols() - templ.cols() + 1;
            int result_rows = img.rows() - templ.rows() + 1;
            
            result.create( result_rows, result_cols, CvType.CV_32FC1);
            
            try {
                Imgproc.matchTemplate( img, templ, result, match_method );
                //opencv_core.normalize( result, result, 0, 1, opencv_core.NORM_MINMAX, -1, new Mat() );
            } catch (CvException ex) {
                Logger.getLogger(ImageTools.class.getName()).log(Level.SEVERE, "Template matching error!", ex);
                return false;
            }
            
            
            
            Core.MinMaxLocResult mmRes = Core.minMaxLoc(result);
            
            
            System.out.println("MINVAL: "+mmRes.minVal);
            System.out.println("MAXVAL: "+mmRes.maxVal);
            System.out.println("MAXLOC: "+mmRes.maxLoc.toString());
            System.out.println("MINLOC: "+mmRes.minLoc.toString());
            
            if (exact) {
                return mmRes.maxVal == 1.0 && mmRes.maxLoc.x == 0.0 && 
                       mmRes.maxLoc.y == 0.0 && getImageColors(img).equals(getImageColors(templ));
            } else {
                Mat submat = img.submat(new Rect((int) mmRes.maxLoc.x, (int) mmRes.maxLoc.y, templ.width(), templ.height()));
                return mmRes.maxVal == 1.0 && getImageColors(submat).equals(getImageColors(templ));
            }
        }
        
        public static boolean cropImage(String inPath, String outPath, int x, int y, int w, int h) throws InvalidDimensionsException {            
            Mat image = Highgui.imread(inPath);
            
            if (x+w > image.size().width || y+h > image.size().height || 
                x < 0 || y < 0 || w < 0 || h < 0 ) {
                
                throw new InvalidDimensionsException(
                    "Cannot crop image because the given dimensions are invalid!\n"+
                    "Crop data:\n"+
                    "x:"+x+" y:"+y+" w:"+w+" h:"+h+"\n"+
                    "Original image size: "+image.size().width+"x"+image.size().height
                );
            }
            
            Rect rect = new Rect(x, y, w, h);
            Mat cropped = new Mat(image, rect);
            return Highgui.imwrite(outPath, cropped);
        }
        
        public static ArrayList<Color> getImageColors(String imgPath) throws FileNotFoundException {
            verifyFile(imgPath);
            log.log(Level.INFO, "Checking colors of {0}", imgPath);
            Mat image = Highgui.imread(imgPath);
            return getImageColors(image);
        }        
        
        public static ArrayList<Color> getImageColors(Mat image) throws FileNotFoundException {
            HashMap<String, Integer> colorCount = new HashMap<>();
            
            int highestValue = 0;
            String highestColor = "";

            for (int i=0; i<image.cols(); i++) {
                for (int j=0; j<image.rows(); j++) {
                    double[] pixel = image.get(j, i);
                    String color = Arrays.toString(pixel);
                    
                    Integer cCount = colorCount.get(color);
                    if (cCount != null) {
                        colorCount.put(color, cCount+1);
                        
                        if (cCount > highestValue) {
                            highestValue = cCount;
                            highestColor = color;
                        }
                    } else {
                        colorCount.put(color, 1);
                    }
                }
            }
            
            int secondHighestValue = 0;
            String secondHighestColor = "";
            for (Map.Entry<String, Integer> entry : colorCount.entrySet()) {
                if (entry.getValue() > secondHighestValue && entry.getValue() < highestValue) {
                    secondHighestValue = entry.getValue();
                    secondHighestColor = entry.getKey();
                }
            }
            
            System.out.println("MOST FREQ OCCURING COLOR: "+highestColor+" ("+highestValue+")");
            System.out.println("SECOND MOST FREQ OCCURING COLOR: "+secondHighestColor+" ("+secondHighestValue+")");
            
            try {
                String[] rgb = highestColor.substring(1, highestColor.length()-1).split(", ");
                Color mostOccuringColor = new Color((int) Float.parseFloat(rgb[2]), (int) Float.parseFloat(rgb[1]), (int) Float.parseFloat(rgb[0]));
                rgb = secondHighestColor.substring(1, highestColor.length()-1).split(", ");
                Color secondMostOccuringColor = new Color((int) Float.parseFloat(rgb[2]), (int) Float.parseFloat(rgb[1]), (int) Float.parseFloat(rgb[0]));

                return new ArrayList<Color>() {{ add(mostOccuringColor); add(secondMostOccuringColor);}};
            
            } catch (StringIndexOutOfBoundsException ex) {
                // No color
                return new ArrayList<Color>() {{ add(Color.BLACK); add(Color.BLACK);}};
            }
        }
        
        private static void verifyFile(String path) throws FileNotFoundException {
            File f = new File(path);
            if (!f.isFile()) {
                log.log(Level.SEVERE, "{0} is not a file!", path);
                throw new FileNotFoundException("File "+path+" not found.");
            }
        }
}