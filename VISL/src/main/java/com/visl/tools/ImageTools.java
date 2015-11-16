package com.visl.tools;

import java.io.FileNotFoundException;
import static org.bytedeco.javacpp.opencv_core.countNonZero;
import static org.bytedeco.javacpp.opencv_imgcodecs.CV_LOAD_IMAGE_GRAYSCALE;
import static org.bytedeco.javacpp.opencv_core.Mat;
import static org.bytedeco.javacpp.opencv_core.Size;
import static org.bytedeco.javacpp.opencv_core.subtract;
import static org.bytedeco.javacpp.opencv_imgcodecs.imread;
import static org.bytedeco.javacpp.opencv_imgproc.resize;
import static org.bytedeco.javacpp.opencv_core.Rect;
import java.io.IOException;
import org.bytedeco.javacpp.opencv_imgcodecs;


public class ImageTools {
	
	public static void main(String[] args) throws IOException {
		String pathFirst = "/Users/shivangibansal/Desktop/panda1.jpeg";
		String pathSecond = "/Users/shivangibansal/Desktop/panda1.jpeg";
		compareImages(pathFirst,pathSecond);
	}

	/**
	 * Takes 2 images and return a similarity score
	 * @param pathFirst: Path of first image
	 * @param pathTwo: : Path of second image
	 * @return similarity percentage
	 */
	public static float compareImages(String pathFirst, String pathTwo) throws FileNotFoundException {
		Mat first = imread(pathFirst, CV_LOAD_IMAGE_GRAYSCALE);
                if (first.data() == null) {
                    throw new FileNotFoundException("Could not load image "+pathFirst);
                }
                
    		Mat second = imread(pathTwo, CV_LOAD_IMAGE_GRAYSCALE);
                if (second.data() == null) {
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

		resize(first, firstResized, sz);
		resize(second, secondResized, sz);

		Mat result1 = new Mat();
		Mat result2 = new Mat();
		float similarityScore;

		// Obtain differences in image
		subtract(firstResized, secondResized, result1);
		subtract(secondResized,firstResized, result2);

		// See how many pixels are different
		int magnitudeOfDifference = Math.max(countNonZero(result1),countNonZero(result2));

		// Total Pixels
		int size = result1.rows() * result1.cols();

		// Report similarity
		if (magnitudeOfDifference == 0) {
			similarityScore = 0;
		} else {
			similarityScore = (((float) magnitudeOfDifference / (float) size)) * 100;
		}

		first._deallocate();
		second._deallocate();
		firstResized._deallocate();
		secondResized._deallocate();
		result1._deallocate();
		result2._deallocate();
		sz.deallocate();
		System.out.println("Similarity Score: "+(100 - similarityScore));
		return similarityScore;
	}
        
        public static boolean cropImage(String inPath, String outPath, int x, int y, int w, int h) {
            Mat image = imread(inPath);
            Rect rect = new Rect(x, y, w, h);
            Mat cropped = new Mat(image, rect);
            return opencv_imgcodecs.imwrite(outPath, cropped);
        }
}