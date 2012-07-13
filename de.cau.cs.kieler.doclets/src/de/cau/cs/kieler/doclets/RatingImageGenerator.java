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

import javax.imageio.ImageIO;

import de.cau.cs.kieler.doclets.model.CodeRating;


/**
 * Generator for images that show the relative rating counts.
 *
 * @author msp
 */
public class RatingImageGenerator {
    
    /** prefix for output file names. */
    private static final String FILE_PREFIX = "relr_";
    /** width for generated images. */
    private static final int IMG_WIDTH = 150;
    /** height for generated images (must be even). */
    private static final int IMG_HEIGHT = 14;
    /** colors for the rating image.  */
    private static final int[][] RATING_COLORS = {
        {255, 60, 60},
        {240, 240, 40},
        {35, 250, 35},
        {90, 90, 255}
    };
    
    /** path to the destination folder. */
    private String destinationPath;
    
    /**
     * Generate an image file with relative rating counts for a given project.
     * 
     * @param projectName name of the project
     * @param ratings relative rating counts
     * @param ratedClasses total number of rated classes for the project
     * @throws IOException if writing fails
     */
    public void generate(final String projectName, final int[] ratings, final int ratedClasses)
            throws IOException {
        // create output file and write header
        File outFile = new File(destinationPath, getFileName(projectName));
        outFile.createNewFile();
        
        // create image data
        BufferedImage image = new BufferedImage(IMG_WIDTH, IMG_HEIGHT, BufferedImage.TYPE_3BYTE_BGR);
        WritableRaster raster = image.getRaster();
        int offset = 0;
        for (int i = 0; i < ratings.length; i++) {
            CodeRating rating = CodeRating.values()[i];
            offset += fillRatingArea(raster, offset, rating,
                    (float) ratings[i] / ratedClasses);
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
    private int fillRatingArea(final WritableRaster raster, final int offset,
            final CodeRating rating, final float ratingRate) {
        int size = Math.round(ratingRate * IMG_WIDTH);
        if (offset + size > IMG_WIDTH) {
            size = IMG_WIDTH - offset;
        }
        int ratingIndex = rating.ordinal() / 2;
        if (rating.isProposed()) {
            for (int x = offset; x < offset + size; x++) {
                for (int y = 0; y < IMG_HEIGHT; y += 2) {
                    raster.setPixel(x, y, darken(RATING_COLORS[ratingIndex], x, y));
                    raster.setPixel(x, y + 1, darken(RATING_COLORS[ratingIndex + 1], x, y + 1));
                }
            }
        } else {
            for (int x = offset; x < offset + size; x++) {
                for (int y = 0; y < IMG_HEIGHT; y++) {
                    raster.setPixel(x, y, darken(RATING_COLORS[ratingIndex], x, y));
                }
            }
        }
        return size;
    }
    
    /** factor for darkening towards the bottom of the image. */
    private static final float BOTTOM_DARKEN = 0.65f;
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
    
    /**
     * Generates the file name for the given project.
     * 
     * @param projectName project name
     * @return file name for the project's relative ratings image
     */
    public String getFileName(final String projectName) {
        return FILE_PREFIX + projectName + ".png";
    }
    
    /**
     * Sets the destination path.
     *
     * @param thedestinationPath the destination path to set
     */
    public void setDestinationPath(final String thedestinationPath) {
        this.destinationPath = thedestinationPath;
    }
    
}
