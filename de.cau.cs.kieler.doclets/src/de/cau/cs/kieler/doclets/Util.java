/*
 * KIELER - Kiel Integrated Environment for Layout Eclipse RichClient
 *
 * http://www.informatik.uni-kiel.de/rtsys/kieler/
 * 
 * Copyright 2013 by
 * + Christian-Albrechts-University of Kiel
 *   + Department of Computer Science
 *     + Real-Time and Embedded Systems Group
 * 
 * This code is provided under the terms of the Eclipse Public License (EPL).
 * See the file epl-v10.html for the license text.
 */
package de.cau.cs.kieler.doclets;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author uru
 * 
 */
public final class Util {

 
    /**
     * Size of the buffer used to copy resource files.
     */
    private static final int READ_BUFFER_SIZE = 1024;

    public void copyResource(final String resName, final File resFolder)
            throws IOException {
        File outFile = new File(resFolder, resName);

        InputStream resIn = this.getClass().getClassLoader().getResourceAsStream(resName);
        if (resIn == null) {
            throw new FileNotFoundException("Resource '" + resName + "' was not found.");
        }
        OutputStream resOut = new FileOutputStream(outFile, false);

        byte[] buffer = new byte[READ_BUFFER_SIZE];
        int readBytes = 0;

        while ((readBytes = resIn.read(buffer)) > 0) {
            resOut.write(buffer, 0, readBytes);
        }

        resIn.close();
        resOut.close();
    }

}
