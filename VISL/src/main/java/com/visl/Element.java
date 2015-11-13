package com.visl;

import java.awt.Color;

/** Represents an element on a web page. 
 * An element can correspond to an actual DOM element on the web page,
 * or may simply correspond to a specific region of the web page.
 */
public class Element {
    
    public enum Alignment {LEFT, RIGHT, CENTER, JUSTIFIED};
    
    /**
     * Check if the element contains an image corresponding to the given 
     * reference image.
     * 
     * @param reference The reference image.
     * 
     * @return      True if the element contains the image, false otherwise.
     */
    public boolean hasImage(Image reference) {
        return true;
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
