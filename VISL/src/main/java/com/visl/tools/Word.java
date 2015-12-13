/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.visl.tools;

/**
 *
 * @author echo
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
    
    public int getFontSize() {
        return pointSize+bottom-top;
    }

    public String getStr() {
        return str;
    }

    public int getSize() {
        return bottom-top;
    }

    public int getLeft() {
        return left;
    }

    public int getTop() {
        return top;
    }

    public int getRight() {
        return right;
    }

    public int getBottom() {
        return bottom;
    }
    
    
}
