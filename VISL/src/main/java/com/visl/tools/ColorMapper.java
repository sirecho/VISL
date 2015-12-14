package com.visl.tools;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

/**
 * Maps RGB values to color names.
 */
public class ColorMapper {
    
    // 11 basic colors (Berlin & Kaye, 1969)
    public static final String RED = "RED";
    public static final String BLACK = "BLACK";
    public static final String BLUE = "BLUE";
    public static final String BROWN = "BROWN";
    public static final String GRAY = "GRAY";
    public static final String GREEN = "GREEN";
    public static final String ORANGE = "ORANGE";
    public static final String PINK = "PINK";
    public static final String PURPLE = "PURPLE";
    public static final String WHITE = "WHITE";
    public static final String YELLOW = "YELLOW";
    
    private static HashMap<String, Color> colors = new HashMap<String, Color>() {
        {
            put(RED, Color.RED);
            put(BLACK, Color.BLACK);
            put(BLUE, Color.BLUE);
            put(BROWN, new Color(165, 42, 42));
            put(GRAY, Color.GRAY);
            put(GREEN, Color.GREEN);
            put(ORANGE, Color.ORANGE);
            put(PINK, Color.PINK);
            put(PURPLE, new Color(128, 0, 128));
            put(WHITE, Color.WHITE);
            put(YELLOW, Color.YELLOW);
        }  
    };
    
    /**
     * Get the name of a color based on RGB value.
     * 
     * Returns the name of the predefined color that is closest to the given 
     * color. Colors are represented as points in an RGB space and compared
     * by calculating their geometric distance from each other.
     * 
     * @param color
     * @return The closest basic color for the given color.
     */
    public static String getColorName(Color color) {
        double minDistance = Double.MAX_VALUE;
        String closestColor = "";
        
        double red = color.getRed();
        double green = color.getGreen();
        double blue = color.getBlue();
        
        for (Map.Entry<String, Color> e : colors.entrySet()) {
            double r = e.getValue().getRed();
            double g = e.getValue().getGreen();
            double b = e.getValue().getBlue();
            
            double distance = Math.sqrt(Math.pow(r-red, 2)+Math.pow(g-green, 2)+Math.pow(b-blue, 2));
            
            if (distance < minDistance) {
                minDistance = distance;
                closestColor = e.getKey();
            }
        }
        
        return closestColor;
    }
    
}
