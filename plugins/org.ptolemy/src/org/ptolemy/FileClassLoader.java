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

package org.ptolemy;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

/**
 * The FileClassLoader is a class loader that files can be added directly by
 * passing them as opened input streams together with a name identifier.
 * 
 * @author Christian Motika - cmot AT informatik.uni-kiel.de
 * @kieler.rating 2010-11-11 yellow
 * 
 */
public class FileClassLoader extends ClassLoader {

    /** The class hash map saves all added classes. */
    HashMap<String, Class> classMap;

    // ------------------------------------------------------------------------

    /**
     * Instantiates a new file class loader with a parent.
     * 
     * @param parent the parent
     */
    public FileClassLoader(ClassLoader parent) {
        super(parent);
        classMap = new HashMap<String, Class>();
    }

    // ------------------------------------------------------------------------

    /**
     * Adds a new class with a name (identifier) and an input stream of the
     * opened *.class file.
     * 
     * @param name the identifier name
     * @param inputStream the input stream of the class file
     * @return true, if successful added, false if the class is already loaded
     * @throws ClassNotFoundException the class not found exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public boolean addClass(String name, InputStream inputStream)
            throws ClassNotFoundException, IOException {
        // First lookup whether the class is already added
        if (classMap.containsKey(name)) {
            return false;
        }

        // Load class data from file and save in byte array
        byte data[] = loadClassData(inputStream);

        // Convert byte array to Class
        Class<?> classInstance = defineClass(name, data, 0, data.length);
        if (classInstance == null) {
            throw new ClassNotFoundException(name);
        }

        // Add the class
        classMap.put(name, classInstance);

        return true;
    }

    // ------------------------------------------------------------------------

    /* (non-Javadoc)
     * @see java.lang.ClassLoader#loadClass(java.lang.String, boolean)
     */
    @Override
    protected Class<?> loadClass(String name, boolean resolve)
            throws ClassNotFoundException {

        // Since all support classes of loaded class use same class loader
        // must check subclass cache of classes for things like Object
        Class<?> c = super.loadClass(name, resolve);

        if (c == null) {
            if (classMap.containsKey(name)) {
                c = classMap.get(name);
            } else {
                throw new ClassNotFoundException(name);
            }
        }

        // Resolve class definition if appropriate
        if (resolve) {
            resolveClass(c);
        }

        // Return class just created
        return c;
    }
    
    // ------------------------------------------------------------------------

    /**
     * Load class binary data as byte array from the opened stream.
     * 
     * @param inputStream the input stream
     * @return the byte[]
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private byte[] loadClassData(InputStream inputStream) throws IOException {

        // Get size of class file
        int size = (int) inputStream.available();

        // Reserve space to read
        byte buff[] = new byte[size];

        // Get stream to read from
        DataInputStream dis = new DataInputStream(inputStream);

        // Read in data
        dis.readFully(buff);

        // close stream
        dis.close();

        // return data
        return buff;
    }
}
