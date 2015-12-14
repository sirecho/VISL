package com.visl.tools;

import com.visl.exceptions.InvalidDimensionsException;
import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.opencv.core.Core;
import org.opencv.core.CvException;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

/**
 * Contains functions for image invariants.
 */
public class ImageTools {
    
    private static Logger log = Logger.getLogger(ImageTools.class.getName());

	/**
	 * Takes two images and return a similarity score
	 * @param pathFirst: Path of first image
	 * @param pathTwo: : Path of second image
	 * @return similarity percentage
         * @throws java.io.FileNotFoundException
	 */
	public static float compareImages(String pathFirst, String pathTwo, int width, int height) throws FileNotFoundException {
            Mat first = Highgui.imread(pathFirst, Highgui.CV_LOAD_IMAGE_GRAYSCALE);
                if (first.empty()) {
                    throw new FileNotFoundException("Could not load image "+pathFirst);
                }
                
    		Mat second = Highgui.imread(pathTwo, Highgui.CV_LOAD_IMAGE_GRAYSCALE);
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
                
                log.log(Level.INFO, "Rows x Cols: {0} x {1}", new Object[]{desRows, desCols});
                
                if (desRows != height || desCols != width)
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
		return 100-similarityScore;
	}
                
        public static boolean hasSubImageSize(String source, String template, int width, int height, boolean exact) throws FileNotFoundException {
            verifyFile(source);
            verifyFile(template);
            
            Mat img = Highgui.imread(source, 1);
            Mat templ = Highgui.imread(template, 1);
            Mat resized = new Mat();
            
            Imgproc.resize(templ, resized, new Size(width, height));
            return hasSubImage(img, resized, exact);
        }
        
        
        /**
         * Check if the template image is a subset of the source image, or
         * if they match exactly.
         * 
         * Compares the template to all possible sub-regions of the source
         * image and checks if the most dominant colors of both images match.
         * 
         * @param source the source image.
         * @param template the template image.
         * @param exact perform an exact match (no sub-region matching).
         * @return true if template is a subset of source and colors match. 
         */        
        public static boolean hasSubImage(String source, String template, boolean exact) throws FileNotFoundException {
            verifyFile(source);
            verifyFile(template);
            
            Mat img = Highgui.imread(source, 1);
            Mat templ = Highgui.imread(template, 1);
            
            return hasSubImage(img, templ, exact);
        }
        
        /**
         * Check if the template matrix is a subset of the source matrix, or
         * if they match exactly.
         * 
         * Compares the template to all possible sub-regions of the source
         * and checks if the most dominant colors of both matrices match.
         * 
         * @param source the source matrix.
         * @param template the template matrix.
         * @param exact perform an exact match (no sub-region matching).
         * @return true if template is a subset of source and colors match. 
         */
        public static boolean hasSubImage(Mat source, Mat template, boolean exact) {
                        
            int match_method = Imgproc.TM_CCORR_NORMED;
            Mat result = new Mat();
            
            if (template.cols() > source.cols() || template.rows() > source.rows()) {
                log.log(Level.WARNING, "Template image is larger than the source!");
                return false;
            }
            
            int result_cols =  source.cols() - template.cols() + 1;
            int result_rows = source.rows() - template.rows() + 1;
            
            result.create( result_rows, result_cols, CvType.CV_32FC1);
            
            try {
                Imgproc.matchTemplate( source, template, result, match_method );
                //opencv_core.normalize( result, result, 0, 1, opencv_core.NORM_MINMAX, -1, new Mat() );
            } catch (CvException ex) {
                Logger.getLogger(ImageTools.class.getName()).log(Level.SEVERE, "Template matching error!", ex);
                return false;
            }
            
            // Get the location of the highest and lowest match
            Core.MinMaxLocResult mmRes = Core.minMaxLoc(result);
            
            log.log(Level.INFO, "MINVAL: {0} MAXVAL: {1} MAXLOC: {2} MINLOC: {3}", new Object[]{mmRes.minVal, mmRes.maxVal, mmRes.maxLoc, mmRes.minLoc});
            
            if (exact) {
                return mmRes.maxVal > 0.99 && mmRes.maxLoc.x == 0 && 
                       mmRes.maxLoc.y == 0 && getImageColors(source).equals(getImageColors(template));
            } else {
                // Create a submatrix to compare colors of the template and the matching sub region
                Mat submat = source.submat(new Rect((int) mmRes.maxLoc.x, (int) mmRes.maxLoc.y, template.width(), template.height()));
                return mmRes.maxVal > 0.99 && getImageColors(submat).equals(getImageColors(template));
            }
        }
        
        /**
         * Crops an image and writes the result to a new file.
         * 
         * @param inPath the original image path
         * @param outPath the cropped image path
         * @param x the X-coordinate of the top-left corner of the crop.
         * @param y the Y-coordinate of the top-left corner of the crop.
         * @param w the width of the crop.
         * @param h the height of the crop.
         * @return true if the cropped image was saved successfully.
         * @throws InvalidDimensionsException 
         */
        public static boolean cropImage(String inPath, String outPath, int x, int y, int w, int h) throws InvalidDimensionsException {            
            Mat image = Highgui.imread(inPath);
            
            // Check that the specified dimensions are legal
            if (x+w > image.size().width || y+h > image.size().height || 
                x < 0 || y < 0 || w < 0 || h < 0 ) {
                
                throw new InvalidDimensionsException (
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
        
        public static ArrayList<Color> getImageColors(String imgPath) {
            log.log(Level.INFO, "Checking colors of {0}", imgPath);
            Mat image = Highgui.imread(imgPath);
            return getImageColors(image);
        }        
        
        public static ArrayList<Color> getImageColors(Mat image) {
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
            
            log.log(Level.INFO, "MOST FREQ OCCURING COLOR: {0} ({1})", new Object[]{highestColor, highestValue});
            log.log(Level.INFO, "SECOND MOST FREQ OCCURING COLOR: {0} ({1})", new Object[]{secondHighestColor, secondHighestValue});
            
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