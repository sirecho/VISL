package com.visl;

import com.visl.tools.ColorMapper;
import com.visl.tools.ImageTools;
import com.visl.tools.TextTools;
import com.visl.tools.Word;
import java.awt.Color;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Represents an element on a web page.
 * An element can correspond to an actual DOM element on the web page,
 * or may simply correspond to a specific region of the web page.
 */
public class Element {
    
    private static Logger log = Logger.getLogger(Element.class.getName());
    
    // Maximum allowed average levensthein distance for text comparison
    private static final double MAX_AVG_LEVENSTHEIN = 0.2;
    
    // Maximum allowed threshold for checking font size
    private static final double FONT_SZ_THRESHOLD = 2;
    
    // Element attributes
    private String XPath;
    private String imagePath;
    private int x, y, width, height;
    
    /**
     * Create an element with the given XPath.
     * @param XPath the new element's XPath
     */
    public Element(String XPath) {
        this.XPath = XPath;
    }

    public Element() {}

    /**
     * Get the X-coordinate of the element.
     * @return The X-coordinate of the element.
     */
    public int getX() {
        return x;
    }

    /**
     * Get the Y-coordinate of the element.
     * @return The Y-coordinate of the element.
     */
    public int getY() {
        return y;
    }
    
    /**
     * Set the path to the image corresponding to the element.
     * @param imagePath the image path.
     */
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
    
    /**
     * Define the position and size of the element.
     * 
     * @param x Top-left x-coordinate
     * @param y Top-left y-coordinate
     * @param width Element width
     * @param height Element height
     */
    public void setPositionAndSize(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    
    /**
     * Check if the element's image exactly matches the given reference 
     * image.
     * 
     * The match is determined using OpenCV's template matching algorithm and
     * by comparing the most dominant color of the images.
     * 
     * @param imagePath path to the reference image.
     * @return True if the images are identical, false otherwise.
     */
    public boolean hasExactImage(String imagePath) {
        return hasImage(imagePath, true);
    }

    /**
     * Check if the element contains an image which exactly matches the given 
     * reference image.
     * 
     * Compares the reference image to all possible sub-regions of the 
     * element's image. The reference image cannot be larger than the element's
     * image.
     * 
     * The match is determined using OpenCV's template matching algorithm and
     * by comparing the most dominant color of the images.
     * 
     * @param imagePath path to the reference image.
     * @return True if the element contains a matching image, false otherwise.
     */    
    public boolean containsImage(String imagePath) {
        return hasImage(imagePath, false);
    }
    
    /**
     * Checks if the element's image matches, or is a subset of, the given
     * reference image.
     * 
     * If the 'exact' parameter is true, the function will perform an exact
     * match. Otherwise, it will try to match all possible sub-regions.
     * 
     * @param imagePath the path to the reference image.
     * @param exact switch to decide the type of matching.
     * @return true if the reference image matches or is a subset of the 
     * element's image , false otherwise.
     */
    private boolean hasImage(String imagePath, boolean exact) {
        try {
            log.log(Level.INFO, "Comparing images");
            return ImageTools.hasSubImage(this.imagePath, imagePath, exact);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Element.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    public boolean hasResizedImage(String imagePath, int width, int height) {
        try {
            return ImageTools.compareImages(this.imagePath, imagePath, width, height) > 60.0;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Element.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    /**
     * Check if the element contains the given text string.
     * 
     * Compares the given string to all possible substrings of the text in
     * the element's image.
     * 
     * A match is determined by using a predefined threshold of the Levensthein
     * distance between the given string, and the string returned by the OCR
     * tool.
     * 
     * @param text The text string.
     * @return True if the element contains the string, false otherwise.
     */
    public boolean hasText(String text) {
        
        String imageText = TextTools.getTextFromImage(this.imagePath);
        int delta = imageText.length() - text.length();
        
        if (delta > 0) {
            int smallestDistance = Integer.MAX_VALUE;

            for (int i=0; i<=delta; i++) {
                int ld = TextTools.getLevenstheinDistance(text, imageText.substring(i, imageText.length()-delta+i));
                smallestDistance = Math.min(smallestDistance, ld);
            }

            double avg_lsd = (float) smallestDistance/text.length();
            log.log(Level.INFO, "Average Levensthein distance is {0}", avg_lsd);

            return avg_lsd < MAX_AVG_LEVENSTHEIN;
        } else {
            return false;
        }
    }
    
    
    /**
     * Check if the text in the element exactly matches the given text string.
     * 
     * A match is determined by using a predefined threshold of the Levensthein
     * distance between the given string, and the string returned by the OCR
     * tool.     
     * 
     * @param text The text string.
     * @return      True if the text in the element exactly matches the string, 
     *              false otherwise.
     */
    public boolean hasExactText(String text) {
        String imageText = TextTools.getTextFromImage(this.imagePath);
        int ld = TextTools.getLevenstheinDistance(text, imageText);
        
        double avg_lsd = ((float) ld/text.length());
        log.log(Level.INFO, "Average Levensthein distance is {0}", avg_lsd);
        
        return avg_lsd < MAX_AVG_LEVENSTHEIN;
    }
    
    /**
     * Check if the text in the element has the given color.
     * 
     * Verifies that the foreground color, i.e. the second most dominant color,
     * of the element's image is the same as the color specified.
     * 
     * This function supports the 11 basic colors in the English language.
     * 
     * @param color The name of the color.
     * @return True if the text has the given color, false otherwise.
     * @see ColorMapper
     */
    public boolean hasTextColor(String color) {
        ArrayList<Color> colors = ImageTools.getImageColors(imagePath);
        try {
            Color background = colors.get(0);
            Color foreground = colors.get(1);
            return color.toUpperCase().equals(ColorMapper.getColorName(foreground));
        } catch (IndexOutOfBoundsException ex) {
            return false;
        }
    }
    
    /**
     * Check if the text in the element has the font size.
     * 
     * Compares the given font size to the font size identified by the OCR tool
     * plus a predefined threshold.
     * 
     * @param fontSize The expected font size.
     * @return True if the text has the given font size, false otherwise.
     */    
    public boolean hasFontSize(int fontSize) {
        log.log(Level.INFO,"Comparing font size");
        
        int fontSizeSum = 0;
        int numWords = 0;
        
        for (ArrayList<Word> line : TextTools.getLines(imagePath)) {
            for (Word word : line) {
                fontSizeSum += word.getFontSize();
                ++numWords;
            }
        }
        
        float avgFontSize = (float) fontSizeSum / numWords;
        
        log.log(Level.INFO, "Average font size is {0}", avgFontSize);
        
        return avgFontSize <= fontSize+FONT_SZ_THRESHOLD;
    }
    
    /**
     * Check if text within the element is aligned according to the alignment 
     * type.
     * 
     * @param alignment The alignment type.
     * 
     * @return      True if the text is correctly aligned, false otherwise.
     */
    public boolean hasTextAligned(TextTools.Alignment alignment) {
        
        for (TextTools.Alignment a : TextTools.getAlignments(imagePath).keySet()) {
            if (alignment.equals(a)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**  
     * Checks whether the text of two elements are aligned with respect to
     * each other.
     * 
     * Reads text elements from both elements and returns true if they are 
     * aligned according to the specified alignment and with respect to each
     * other.
     * 
     * @param element the second element.
     * @param alignment the expected alignment type.
     * @return true if the text in both elements are aligned with the correct
     * alignment type, false otherwise.
     */
    public boolean isAligned(Element element,TextTools.Alignment alignment)
    {
        Map<TextTools.Alignment,Float> firstElementResult = new HashMap<>();
    	Map<TextTools.Alignment,Float> secondElementResult = new HashMap<>();
        log.log(Level.INFO, "Getting alignments for first element: {0}", this.imagePath);
    	firstElementResult = TextTools.getAlignments(this.imagePath);
        log.log(Level.INFO, "Getting alignments for second element: {0}", element.imagePath);
    	secondElementResult = TextTools.getAlignments(element.imagePath);
    	// Get absolute coordinate value
    	int firstX = this.getX();
    	int secondX = element.getX();
        if (!firstElementResult.containsKey(alignment) || !secondElementResult.containsKey(alignment))
                return false;
        // Add absolute x coordinate to the mean value
        Float firstMeanValue = firstElementResult.get(alignment) + firstX;
        Float secondMeanValue = secondElementResult.get(alignment) + secondX;
        log.log(Level.INFO, "Mean x-coords: {0}, {1}", new Object[]{firstMeanValue, secondMeanValue});
        if (Math.abs(firstMeanValue - secondMeanValue) < TextTools.STD_ALIGNMENT_THRESHOLD)
                return true;
        return false; 
    }    
}
