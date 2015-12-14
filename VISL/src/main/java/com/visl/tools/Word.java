package com.visl.tools;

/**
 * Represents a word recognized by the OCR tool.
 */
public class Word {
    private String str;
    private int pointSize;
    private int left;
    private int top;
    private int right;
    private int bottom;

    public Word(String str, int pointSize, int left, int top, int right, int bottom) {
        this.str = str;
        this.pointSize = pointSize;
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }
    
    /**
     * Returns the font size of the word, as recognized by the OCR tool.
     * 
     * This value might differ from the actual font size depending on the
     * how the OCR tool performs.
     * 
     * @return the font size.
     */
    public int getFontSize() {
        return pointSize+bottom-top;
    }

    /**
     * Returns the string as recognized by the OCR tool.
     * 
     * This value might differ from the actual string depending on the
     * how the OCR tool performs.
     * 
     * @return the string.
     */    
    public String getStr() {
        return str;
    }

    /**
     * Returns the height of the word, as recognized by the OCR tool.
     * 
     * This value might differ from the actual height depending on the
     * how the OCR tool performs.
     * 
     * @return the pixel height.
     */    
    public int getSize() {
        return bottom-top;
    }
    
    
    /**
     * Returns the leftmost X-coordinate of the word, as recognized by the OCR tool.
     * 
     * This value might differ from the actual coordinate depending on the
     * how the OCR tool performs.
     * 
     * @return the leftmost X-coordinate.
     */
    public int getLeft() {
        return left;
    }

    /**
     * Returns the lowest Y-coordinate of the word, as recognized by the OCR tool.
     * 
     * This value might differ from the actual coordinate depending on the
     * how the OCR tool performs.
     * 
     * @return the lowest Y-coordinate.
     */    
    public int getTop() {
        return top;
    }

    /**
     * Returns the rightmost X-coordinate of the word, as recognized by the OCR tool.
     * 
     * This value might differ from the actual coordinate depending on the
     * how the OCR tool performs.
     * 
     * @return the rightmost X-coordinate.
     */    
    public int getRight() {
        return right;
    }

    /**
     * Returns the highest Y-coordinate of the word, as recognized by the OCR tool.
     * 
     * This value might differ from the actual coordinate depending on the
     * how the OCR tool performs.
     * 
     * @return the highest Y-coordinate.
     */        
    public int getBottom() {
        return bottom;
    }
}
