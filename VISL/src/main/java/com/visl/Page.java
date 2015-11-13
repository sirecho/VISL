package com.visl;

import java.util.Collection;

/** Represents a web page. */
public class Page {
    
    public enum Alignment {LEFT, RIGHT, CENTER};
    
    /**
     * Returns a portion of the web page as an element.
     * 
     * @param x1 X-coordinate of top-left corner.
     * @param y1 Y-coordinate of top-left corner.
     * @param x2 X-coordinate of bottom-right corner.
     * @param y2 Y-coordinate of bottom-right corner.
     * 
     * @return      An element representing this portion of the web page
     *              or null if invalid coordinates are given.
     * 
     * TODO: Implement this feature.
     */    
    public Element getPortion(int x1, int y1, int x2, int y2) {
        return null;
    }
    
    /**
     * Returns an element corresponding to the xPath/CSS selector if present.
     * 
     * @param path The xPath or CSS selector corresponding to the element.
     * 
     * @return      The element or false if no such element is found.
     * 
     * TODO: Implement this feature.
     */    
    public Element getElement(String path) {
        return new Element();
    }
    
    /**
     * Get the entire page as an Element.
     * 
     * @return      The entire page as an element.
     */
    public Element getPage() {
        return new Element();
    }
    

    /**
     * Checks whether the text in the given elements are aligned according
     * to the given alignment type.
     * 
     * @param elements A collections of elements to be checked.
     * @param alignment The expected alignment type.
     * 
     * @return      true if the text is correctly aligned, false otherwise.
     * 
     * TODO: Implement this feature.
     */    
    public boolean hasTextAligned(Collection<Element> elements, Alignment alignment) {
        return false;
    }
}
