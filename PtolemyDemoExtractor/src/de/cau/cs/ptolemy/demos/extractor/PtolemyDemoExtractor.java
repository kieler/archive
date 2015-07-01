/*
 * KIELER - Kiel Integrated Environment for Layout Eclipse RichClient
 *
 * http://www.informatik.uni-kiel.de/rtsys/kieler/
 * 
 * Copyright 2010 by
 * + Kiel University
 *   + Department of Computer Science
 *     + Real-Time and Embedded Systems Group
 * 
 * This code is provided under the terms of the Eclipse Public License (EPL).
 * See the file epl-v10.html for the license text.
 */

package de.cau.cs.ptolemy.demos.extractor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


/**
 * Extracts the demo files from a Ptolemy2 repository. This program takes two arguments:
 * 
 *  1. The first denotes the path to the folder containing the local copy of the Ptolemy2
 *     repository. (the folder that contains a ptolemy folder)
 *  2. The second denotes the path where the files should be saved.
 * 
 * To assemble the demos, the folders in ptolemy/domains are traversed. If a folder
 * contains a subfolder named "demo", we traverse each subfolder it contains and copy
 * each .xml file we find to the target directory with the following file name:
 * 
 *   {domain}/{domain}_{filename}
 * 
 * @author cds
 */
public class PtolemyDemoExtractor {
    
    /**
     * The source folder, containing the ptolemy directory.
     */
    private File srcFolder;
    
    /**
     * The destination folder.
     */
    private File dstFolder;
    
    
    /**
     * Constructs a new instance.
     * 
     * @param src source folder.
     * @param dst destination folder.
     * @throws IOException if the folders do not exist.
     */
    public PtolemyDemoExtractor(String src, String dst) throws IOException {
        // Construct the source folder
        srcFolder = new File(src);
        
        if (!srcFolder.isDirectory()) {
            throw new IOException("source folder must be a directory.");
        }
        
        // Construct the destination folder
        dstFolder = new File(dst);
        
        if (!dstFolder.isDirectory()) {
            throw new IOException("destination folder must be a directory.");
        }
        
        if (!dstFolder.canWrite()) {
            throw new IOException("destination folder must be writable.");
        }
    }
    
    
    /**
     * Entry point. Constructs a new extractor and lets it do its work.
     * 
     * @param args the arguments.
     */
    public static void main(String[] args) {
        // We expect exactly two arguments
        if (args.length != 2) {
            printUsageInfo();
            return;
        }
        
        try {
            PtolemyDemoExtractor extractor = new PtolemyDemoExtractor(args[0], args[1]);
            extractor.extract();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    /**
     * Starts the extraction.
     * 
     * @throws IOException if something goes wrong copying the files.
     */
    public void extract() throws IOException {
        extractDemos(srcFolder, null, null);
    }
    
    
    /**
     * Extracts all demos in the given folder and its subfolders.
     * 
     * @param folder a folder
     * @param domainName the Ptolemy domain name, or null
     * @param prefix the file prefix, or null
     * @throws IOException if something goes wrong copying the files.
     */
    private void extractDemos(File folder, String domainName, String prefix) throws IOException {
        String[] items = folder.list();
        for (String item : items) {
            File file = new File(folder, item);
            
            if (file.isDirectory()) {
                if (file.getName().equals("demo")) {
                    extractDemos(file, folder.getName().toLowerCase(), "");
                } else if (domainName != null) {
                    extractDemos(file, domainName, prefix + file.getName().toLowerCase() + "_");
                } else {
                    extractDemos(file, null, null);
                }
            } else if (file.getName().endsWith(".xml") && domainName != null) {
                extractActualDemo(file, domainName, prefix);
            }
        }
    }
    
    /**
     * Extracts a demo file.
     * 
     * @param file the file
     * @param domainName the domain name, determining the destination folder
     * @param prefix the prefix to add to the destination file
     * @throws IOException if something goes wrong copying the files.
     */
    private void extractActualDemo(File file, String domainName, String prefix) throws IOException {
        // Create a folder for the domain
        File dstDomainFolder = new File(dstFolder, domainName);
        if (dstDomainFolder.isFile()) {
            throw new IOException("Cannot create folder '" + dstDomainFolder.getAbsolutePath()
                    + "' because a file of the same name already exists.");
        } else if (!dstDomainFolder.exists()) {
            if (!dstDomainFolder.mkdir()) {
                throw new IOException("Cannot create folder '" + dstDomainFolder.getAbsolutePath()
                        + "'.");
            }
        }
        
        // rename it to .moml
        int dotIndex = file.getName().lastIndexOf('.');
        if (dotIndex < 0) {
            throw new IllegalStateException("File has no extension.");
        }
        String dstFileName = prefix + file.getName().substring(0, dotIndex) + ".moml";
        
        System.out.println("Copying... " + dstFileName);
        File dstFile = new File(dstDomainFolder, dstFileName);
        
        // Try to copy the file
        copy(file, dstFile);
    }
    
    /**
     * Copies src to dst.
     * 
     * @param src the source file.
     * @param dst the destination file.
     * @throws IOException if anything goes wrong.
     */
    private void copy(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);
        
        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        
        // Close the streams
        in.close();
        out.close();
    }
    
    
    /**
     * Prints usage information about the program.
     */
    private static void printUsageInfo() {
        System.out.println("Usage: command <ptolemy_repo_folder> <target_folder>");
    }
    
}
