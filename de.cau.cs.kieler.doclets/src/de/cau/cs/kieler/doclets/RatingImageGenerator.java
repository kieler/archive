/*
 * KIELER - Kiel Integrated Environment for Layout Eclipse RichClient
 *
 * http://www.informatik.uni-kiel.de/rtsys/kieler/
 * 
 * Copyright 2009 by
 * + Christian-Albrechts-University of Kiel
 *   + Department of Computer Science
 *     + Real-Time and Embedded Systems Group
 * 
 * This code is provided under the terms of the Eclipse Public License (EPL).
 * See the file epl-v10.html for the license text.
 */
package de.cau.cs.kieler.doclets;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.imageio.ImageIO;

import de.cau.cs.kieler.doclets.htmlgen.BasicHtmlWriter;
import de.cau.cs.kieler.doclets.model.CodeRating;
import de.cau.cs.kieler.doclets.model.DesignRating;
import de.cau.cs.kieler.doclets.model.Plugin;
import de.cau.cs.kieler.doclets.model.Project;


/**
 * Generator for images that show the relative rating counts.
 *
 * @author msp
 */
public class RatingImageGenerator {
    /** width for generated images. */
    private static final int IMG_WIDTH = 150;
    /** height for generated images (must be even). */
    private static final int IMG_HEIGHT = 14;
    /** colors for the design rating image.  */
    private static final int[][] DESIGN_RATING_COLORS = {
        {255, 60, 60},
        {35, 250, 35}
    };
    /** colors for the code rating image.  */
    private static final int[][] CODE_RATING_COLORS = {
        {255, 60, 60},
        {240, 240, 40},
        {35, 250, 35},
        {90, 90, 255}
    };
    

    /**
     * Generates the rating graphs for projects, plugins, and the overview page.
     * 
     * @param projects map mapping project names to projects.
     * @param destinationFolder folder to place the graphs in.
     * @throws Exception if something bad happens.
     */
    public void generateRatingGraphs(final Map<String, Project> projects, final File destinationFolder)
            throws Exception {
        
        // Arrays for aggregated ratings for the overview page
        int[] designRatings = new int[DesignRating.values().length];
        int[] codeRatings = new int[CodeRating.values().length];
        
        // Iterate through the projects
        for (Project project : projects.values()) {
            // Iterate through the plugins
            for (Plugin plugin : project.getPlugins().values()) {
                // Generate plugin design review graph
                generateDesignRatingGraph(destinationFolder,
                        BasicHtmlWriter.generateGraphFileName(plugin, false),
                        plugin.getStatsDesign());
                
                // Generate plugin code review graph
                generateCodeRatingGraph(destinationFolder,
                        BasicHtmlWriter.generateGraphFileName(plugin, true),
                        plugin.getStatsCode());
            }

            // Generate project design review graph
            generateDesignRatingGraph(destinationFolder,
                    BasicHtmlWriter.generateGraphFileName(project, false),
                    project.getStatsDesign());
            
            // Generate project code review graph
            generateCodeRatingGraph(destinationFolder,
                    BasicHtmlWriter.generateGraphFileName(project, true),
                    project.getStatsCode());
            
            // Add aggregated project values for final statistic
            int[] projectDesignRatings = project.getStatsDesign();
            for (int i = 0; i < projectDesignRatings.length; i++) {
                designRatings[i] += projectDesignRatings[i];
            }

            int[] projectCodeRatings = project.getStatsCode();
            for (int i = 0; i < projectCodeRatings.length; i++) {
                codeRatings[i] += projectCodeRatings[i];
            }
        }
        
        // Generate overview graphs
        generateDesignRatingGraph(destinationFolder,
                BasicHtmlWriter.generateGraphFileName(null, false),
                designRatings);
        generateCodeRatingGraph(destinationFolder,
                BasicHtmlWriter.generateGraphFileName(null, true),
                codeRatings);
    }

    
    /////////////////////////////////////////////////////////////////////////////
    // DESIGN RATING IMAGE GENERATION
    
    /**
     * Generate an image file with relative rating counts for a given project.
     * 
     * @param destinationFolder folder to place the graph in.
     * @param fileName name of the output file.
     * @param ratings relative rating counts.
     * @throws IOException if writing fails.
     */
    private void generateDesignRatingGraph(final File destinationFolder, final String fileName,
            final int[] ratings) throws IOException {
        
        // Create output file and write header
        File outFile = new File(destinationFolder, fileName);
        outFile.createNewFile();
        
        // Count how many ratings there are
        int ratingCount = 0;
        for (int i = 0; i < ratings.length; i++) {
            ratingCount += ratings[i];
        }
        
        // Create image data
        BufferedImage image = new BufferedImage(IMG_WIDTH, IMG_HEIGHT, BufferedImage.TYPE_3BYTE_BGR);
        WritableRaster raster = image.getRaster();
        int offset = 0;
        for (int i = 0; i < ratings.length; i++) {
            DesignRating rating = DesignRating.values()[i];
            offset += fillDesignRatingArea(raster, offset, rating,
                    (float) ratings[i] / ratingCount);
        }
        
        // write image to file 
        ImageIO.write(image, "png", outFile);
    }
    
    /**
     * Fills the area with colors for the current rating.
     * 
     * @param raster image raster for drawing
     * @param offset horizontal offset in the raster
     * @param rating rating constant for which to draw
     * @param ratingRate relative rate for the given rating
     * @return the number of horizontal pixels used for the current rating
     */
    private int fillDesignRatingArea(final WritableRaster raster, final int offset,
            final DesignRating rating, final float ratingRate) {
        
        int size = Math.round(ratingRate * IMG_WIDTH);
        if (offset + size > IMG_WIDTH) {
            size = IMG_WIDTH - offset;
        }
        int ratingIndex = rating.ordinal() / 2;
        if (rating.isProposed()) {
            for (int x = offset; x < offset + size; x++) {
                for (int y = 0; y < IMG_HEIGHT; y += 2) {
                    raster.setPixel(x, y, darken(DESIGN_RATING_COLORS[ratingIndex], x, y));
                    raster.setPixel(x, y + 1, darken(DESIGN_RATING_COLORS[ratingIndex + 1], x, y + 1));
                }
            }
        } else {
            for (int x = offset; x < offset + size; x++) {
                for (int y = 0; y < IMG_HEIGHT; y++) {
                    raster.setPixel(x, y, darken(DESIGN_RATING_COLORS[ratingIndex], x, y));
                }
            }
        }
        return size;
    }

    
    /////////////////////////////////////////////////////////////////////////////
    // CODE RATING IMAGE GENERATION
    
    /**
     * Generate an image file with relative rating counts for a given project.
     * 
     * @param destinationFolder folder to place the graph in.
     * @param fileName name of the output file.
     * @param ratings relative rating counts.
     * @throws IOException if writing fails.
     */
    private void generateCodeRatingGraph(final File destinationFolder, final String fileName,
            final int[] ratings) throws IOException {
        
        // Create output file and write header
        File outFile = new File(destinationFolder, fileName);
        outFile.createNewFile();
        
        // Count how many ratings there are
        int ratingCount = 0;
        for (int i = 0; i < ratings.length; i++) {
            ratingCount += ratings[i];
        }
        
        // Create image data
        BufferedImage image = new BufferedImage(IMG_WIDTH, IMG_HEIGHT, BufferedImage.TYPE_3BYTE_BGR);
        WritableRaster raster = image.getRaster();
        int offset = 0;
        for (int i = 0; i < ratings.length; i++) {
            CodeRating rating = CodeRating.values()[i];
            offset += fillCodeRatingArea(raster, offset, rating,
                    (float) ratings[i] / ratingCount);
        }
        
        // write image to file 
        ImageIO.write(image, "png", outFile);
    }
    
    /**
     * Fills the area with colors for the current rating.
     * 
     * @param raster image raster for drawing
     * @param offset horizontal offset in the raster
     * @param rating rating constant for which to draw
     * @param ratingRate relative rate for the given rating
     * @return the number of horizontal pixels used for the current rating
     */
    private int fillCodeRatingArea(final WritableRaster raster, final int offset,
            final CodeRating rating, final float ratingRate) {
        
        int size = Math.round(ratingRate * IMG_WIDTH);
        if (offset + size > IMG_WIDTH) {
            size = IMG_WIDTH - offset;
        }
        int ratingIndex = rating.ordinal() / 2;
        if (rating.isProposed()) {
            for (int x = offset; x < offset + size; x++) {
                for (int y = 0; y < IMG_HEIGHT; y += 2) {
                    raster.setPixel(x, y, darken(CODE_RATING_COLORS[ratingIndex], x, y));
                    raster.setPixel(x, y + 1, darken(CODE_RATING_COLORS[ratingIndex + 1], x, y + 1));
                }
            }
        } else {
            for (int x = offset; x < offset + size; x++) {
                for (int y = 0; y < IMG_HEIGHT; y++) {
                    raster.setPixel(x, y, darken(CODE_RATING_COLORS[ratingIndex], x, y));
                }
            }
        }
        return size;
    }

    
    /////////////////////////////////////////////////////////////////////////////
    // COLOR COMPUTATION
    
    /** factor for darkening towards the bottom of the image. */
    private static final float BOTTOM_DARKEN = 0.6f;
    /** factor for darkening on the border of the image. */
    private static final float BORDER_DARKEN = 0.5f;
    
    /**
     * Darkens a given color based on the current position.
     * 
     * @param color a color
     * @param x x coordinate
     * @param y y coordinate
     * @return a darkened color
     */
    private static int[] darken(final int[] color, final int x, final int y) {
        int[] result = new int[color.length];
        float factor = BOTTOM_DARKEN + (IMG_HEIGHT - (float) y) / IMG_HEIGHT * (1 - BOTTOM_DARKEN);
        if (x == 0 || y == 0 || x == IMG_WIDTH - 1 || y == IMG_HEIGHT - 1) {
            factor *= BORDER_DARKEN;
        }
        for (int i = 0; i < color.length; i++) {
            result[i] = Math.round(color[i] * factor);
        }
        return result;
    }
}
