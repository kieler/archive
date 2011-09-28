/*
 * KIELER - Kiel Integrated Environment for Layout Eclipse RichClient
 *
 * http://www.informatik.uni-kiel.de/rtsys/kieler/
 * 
 * Copyright 2010 by
 * + Christian-Albrechts-University of Kiel
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
import java.io.FilenameFilter;
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
     * The domains folder.
     */
    private File domainsFolder;
    
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
        
        // Construct the domains folder
        File ptolemyFolder = new File(srcFolder, "ptolemy");
        if (!ptolemyFolder.isDirectory()) {
            throw new IOException("ptolemy/domains folder not found.");
        }
        
        domainsFolder = new File(ptolemyFolder, "domains");
        
        if (!domainsFolder.isDirectory()) {
            throw new IOException("ptolemy/domains folder not found.");
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
            System.out.println("An error occurred:");
            System.out.println(e.getMessage());
            
            e.printStackTrace();
        }
    }
    
    
    /**
     * Starts the extraction.
     * 
     * @throws IOException if something goes wrong copying the files.
     */
    public void extract() throws IOException {
        // Iterate through the domain folders
        File domainFolder;
        File demosFolder;
        String[] items;
        
        items = domainsFolder.list();
        for (String item : items) {
            domainFolder = new File(domainsFolder, item);
            
            if (domainFolder.isDirectory()) {
                // We have found a domain folder, try to find its demo folder
                demosFolder = new File(domainFolder, "demo");
                
                if (demosFolder.isDirectory()) {
                    extractDemos(domainFolder, demosFolder);
                }
            }
        }
    }
    
    
    /**
     * Extracts all demos in the given demo folder.
     * 
     * @param domainFolder the folder containing the demos folder.
     * @param demosFolder the folder containing other folders with demos.
     * @throws IOException if something goes wrong copying the files.
     */
    private void extractDemos(File domainFolder, File demosFolder) throws IOException {
        File demoFolder;
        String[] items;
        
        items = demosFolder.list();
        for (String item : items) {
            demoFolder = new File(demosFolder, item);
            
            if (demoFolder.isDirectory()) {
                // We have found a folder containing actual demos!
                extractActualDemos(domainFolder, demoFolder);
            }
        }
    }
    
    /**
     * Extracts all .xml files from the given folder.
     * 
     * @param domainFolder folder of the domain containing the demo.
     * @param demoFolder the actual demo folder.
     * @throws IOException if something goes wrong copying the files.
     */
    private void extractActualDemos(File domainFolder, File demoFolder) throws IOException {
        File demoFile;
        File dstDomainFolder;
        File dstFile;
        String dstFileName;
        String[] items;
        
        // Get a list of xml files
        items = demoFolder.list(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".xml");
            }
        });
        
        if (items.length == 0) {
            return;
        }
        
        // Create a folder for the domain
        dstDomainFolder = new File(dstFolder, domainFolder.getName());
        if (dstDomainFolder.isFile()) {
            throw new IOException("Cannot create folder '" + dstDomainFolder.getAbsolutePath()
                    + "' because a file of the same name already exists.");
        } else if (!dstDomainFolder.exists()) {
            if (!dstDomainFolder.mkdir()) {
                throw new IOException("Cannot create folder '" + dstDomainFolder.getAbsolutePath()
                        + "'.");
            }
        }
        
        for (String item : items) {
            // We know that it's an xml file
            demoFile = new File(demoFolder, item);
            
            dstFileName = domainFolder.getName() + "_" + demoFile.getName();
            
            System.out.println("Copying... " + dstFileName);
            dstFile = new File(dstDomainFolder, dstFileName);
            
            // Try to copy the file
            copy(demoFile, dstFile);
        }
    }
    
    /**
     * Copies src to dst.
     * 
     * @param src the source file.
     * @param dst the destination file.
     * @throws IOException if anything goes wrong.
     */
    void copy(File src, File dst) throws IOException {
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
