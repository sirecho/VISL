/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.visl.tools;

import net.sourceforge.tess4j.*;
import java.io.*;

/**
 *
 * @author echo
 */
public class TextTools {
	
    public static void main(String[] args) throws IOException {
            String imagePath = "/Users/shivangibansal/Desktop/helloworld.gif";
            String text = "hello";
            getTextFromImage(imagePath);
    }

    /**
     * Takes an image and extracts text from it using OCR.
     * @param imagePath: Path of the image
     * @return String The text found in the image
     */
    public static String getTextFromImage(String imagePath) {
        // Creating Tesseract Interface
        String result = "";
        Tesseract instance = new Tesseract();
        instance.setDatapath("/usr/local/share/tessdata");
        File imageFile = new File(imagePath);
        
        // doOCR method of Tesseract gets the text from the captured Screenshot
        try {
            result = instance.doOCR(imageFile);
            System.out.println(result);
        } catch (TesseractException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return result;
    }
}