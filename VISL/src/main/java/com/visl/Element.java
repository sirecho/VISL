package com.visl;

import com.visl.tools.ImageTools;
import java.awt.Color;
import java.io.FileNotFoundException;

/** Represents an element on a web page. 
 * An element can correspond to an actual DOM element on the web page,
 * or may simply correspond to a specific region of the web page.
 */
public class Element {
    
    private String XPath;
    private String CSSPath;
    private String imagePath;
    
    private int x, y, width, height;
    
    public enum Alignment {LEFT, RIGHT, CENTER, JUSTIFIED};
    
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
     * @param reference The reference image.
     * 
     * @return      True if the element contains the image, false otherwise.
     */
    public boolean hasImage(String imagePath) {
        
        float similarity = 0F;
        try {
            similarity = ImageTools.compareImages(this.imagePath, imagePath);
        } catch (FileNotFoundException e) {
            System.out.println(e.getLocalizedMessage());
        }
        
        return similarity == 0;
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
        return false;
    }
    
    /**
     * Check if the element contains the given text string.
     * 
     * @param text The text string.
     * 
     * @return      True if the element contains the string, false otherwise.
     */
    public boolean hasText(String text) {
        return false;
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
        return false;
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
     * @param fontSize The expected font size.
     * 
     * @return      True if the text has the given font size, false otherwise.
     */    
    public boolean hasFontSize(int fontSize) {
        return false;
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
        return false;
    }    
}
