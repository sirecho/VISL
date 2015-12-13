package com.visl;

import com.visl.tools.ImageTools;
import com.visl.tools.TextTools;
import com.visl.tools.Word;
import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Represents an element on a web page. 
 * An element can correspond to an actual DOM element on the web page,
 * or may simply correspond to a specific region of the web page.
 */
public class Element {
    
    private static Logger log = Logger.getLogger(Element.class.getName());
    
    private String XPath;
    private String imagePath;
    
    private int x, y, width, height;

    public Element() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public Element(String XPath) {
        this.XPath = XPath;
    }
    
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
     * Check if the element contains an image corresponding to the given 
     * reference image.
     * 
     * @param imagePath
     * 
     * @return      True if the element contains the image, false otherwise.
     */
    public boolean hasExactImage(String imagePath) {
        return hasImage(imagePath, true);
    }
    
    public boolean containsImage(String imagePath) {
        return hasImage(imagePath, false);
    }
    
    private boolean hasImage(String imagePath, boolean exact) {
        try {
            log.log(Level.INFO, "Comparing images");
            return ImageTools.hasSubImage(this.imagePath, imagePath, exact);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Element.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }        
    }
    
    /**
     * Check if the element contains an image corresponding to the given 
     * reference image at the given resolution.
     * 
     * @param reference The reference image.
     * @param sizeX The width of the image.
     * @param sizeY The height of the image.
     * 
     * @return      True if the element contains the image with the correct 
     *              resolution, false otherwise.
     */    
    public boolean hasImage(Image reference, int sizeX, int sizeY) {
        try {
            log.log(Level.INFO, "Comparing images");
            
            if (!ImageTools.getImageColors(imagePath).equals(ImageTools.getImageColors(this.imagePath))) {
                log.log(Level.WARNING, "Images have different colors");
                return false;
            }
            return ImageTools.hasSubImage(this.imagePath, imagePath, true);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Element.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    /**
     * Check if the element contains the given text string.
     * 
     * @param text The text string.
     * 
     * @return      True if the element contains the string, false otherwise.
     */
    public boolean hasText(String text) {
        String imageText = TextTools.getTextFromImage(this.imagePath);
        return imageText.regionMatches(0, text, 0, text.length());
    }    
    
    
    /**
     * Check if the text in the element exactly matches the given text string.
     * 
     * @param text The text string.
     * 
     * @return      True if the text in the element exactly matches the string, 
     *              false otherwise.
     */
    public boolean hasExactText(String text) {
        return text.equals(TextTools.getTextFromImage(this.imagePath));
    }
    
    /**
     * Check if the text in the element has the given color.
     * 
     * @param color The color in the sRGB color space.
     * 
     * @return      True if the text has the given color, false otherwise.
     */
    public boolean hasTextColor(Color color) {
        return false;
    }
    
    /**
     * Check if the text in the element has the font size.
     * 
     * Compares the given font size to the font size identified by
     * Tesseract +/- 1px.
     * 
     * @param fontSize The expected font size.
     * 
     * @return      True if the text has the given font size, false otherwise.
     */    
    public boolean hasFontSize(int fontSize) {
        
        for (ArrayList<Word> line : TextTools.getLines(imagePath)) {
            for (Word word : line) {
                if (word.getFontSize() < fontSize-1 || word.getFontSize() > fontSize+1) {
                    return false;
                }
            }
        }
        
        return true;
    }
    
    /**
     * Check if text within the element is aligned according to the alignment 
     * type.
     * 
     * @param alignmentType The alignment type.
     * 
     * @return      True if the text is correctly aligned, false otherwise.
     */
    public boolean hasTextAligned(String alignmentType) {
        
        // get lines
        // For each line: store left value of first word, right of last
        
        return false;
    }
}
