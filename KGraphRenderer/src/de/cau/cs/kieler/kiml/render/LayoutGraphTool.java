/*
 * KIELER - Kiel Integrated Environment for Layout Eclipse RichClient
 *
 * http://www.informatik.uni-kiel.de/rtsys/kieler/
 * 
 * Copyright 2011 by
 * + Christian-Albrechts-University of Kiel
 *   + Department of Computer Science
 *     + Real-Time and Embedded Systems Group
 * 
 * This code is provided under the terms of the Eclipse Public License (EPL).
 * See the file epl-v10.html for the license text.
 */
package de.cau.cs.kieler.kiml.render;

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collections;

import javax.imageio.ImageIO;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import de.cau.cs.kieler.core.kgraph.KNode;

/**
 * Command-line tool for rendering layout graphs.
 *
 * @author msp
 */
public final class LayoutGraphTool {
    
    /** Hide default constructor to prevent instantiation. */
    private LayoutGraphTool() { }
    
    /** copyright and usage information for the rendering tool. */
    private static final String USAGE = "KGraph Rendering Tool\n"
            + "  Copyright 2011 by\n"
            + "  + Christian-Albrechts-University of Kiel\n"
            + "    + Department of Computer Science\n"
            + "      + Real-Time and Embedded Systems Group\n\n"
            + "Usage:  kgraph <inputFile>\n"
            + "The output is written to the current directory and named like the input file";

    /**
     * The main function.
     * 
     * @param args command-line arguments
     */
    public static void main(final String[] args) {
        if (args.length != 1) {
            System.out.println(USAGE);
        } else {
            String inputFileName = args[0];
            try {
                process(inputFileName);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }
    
    /**
     * Read the given input file, render an image, and write it to an output file.
     * 
     * @param inputFileName an input file
     * @throws IOException if processing fails
     */
    private static void process(final String inputFileName) throws IOException {
        ResourceSet resourceSet = new ResourceSetImpl();
        resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(
                Resource.Factory.Registry.DEFAULT_EXTENSION, new XMIResourceFactoryImpl());
        File inputFile = new File(inputFileName);
        URI fileURI = URI.createFileURI(inputFile.getAbsolutePath());
        Resource resource = resourceSet.getResource(fileURI, true);
        resource.load(Collections.EMPTY_MAP);
        if (resource.getContents().size() != 1) {
            throw new RuntimeException("The input file must contain exactly one KGraph model.");
        }
        EObject content = resource.getContents().get(0);
        if (!(content instanceof KNode)) {
            throw new RuntimeException("The input model must be an instance of KNode.");
        }
        
        LayoutGraphRenderer renderer = new LayoutGraphRenderer();
        RenderedImage image = renderer.createImage((KNode) content);
        
        String fileName = inputFile.getName();
        int extDelimIndex = fileName.lastIndexOf('.');
        if (extDelimIndex >= 0) {
            fileName = fileName.substring(0, extDelimIndex);
        }
        ImageIO.write(image, "png", new File(fileName + ".png"));
    }

}
