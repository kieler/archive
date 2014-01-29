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

import java.lang.reflect.Method;

import com.google.common.collect.ImmutableList;
import com.sun.javadoc.DocErrorReporter;
import com.sun.javadoc.LanguageVersion;
import com.sun.javadoc.RootDoc;

import de.cau.cs.kieler.doclets.extensions.KRenderingExtensionsDoclet;
import de.cau.cs.kieler.doclets.rating.RatingDoclet;

/**
 * Executes all child doclets that are specified {@value #DOCLETS} list.
 * 
 * @author uru
 */
public final class CompoundDoclet {

    private static final ImmutableList<Class<?>> DOCLETS = ImmutableList.of(RatingDoclet.class,
            KRenderingExtensionsDoclet.class);

    private CompoundDoclet() {
    }

    /**
     * Generates documentation for the given root.
     * 
     * @param rootDoc
     *            the root doc
     * @return true on success
     */
    public static boolean start(final RootDoc rootDoc) {

        boolean success = true;

        // iterate all doclets
        for (Class<?> doclet : DOCLETS) {

            try {
                // get the doclet's start method
                Method m = doclet.getMethod("start", RootDoc.class);
                // invoke the method
                Object result = m.invoke(null, rootDoc);

                success &= (Boolean) result;

            } catch (Exception e) {
                System.err.println("Problem occurred in doclet " + doclet.getCanonicalName());
                e.printStackTrace();
            }

        }

        return success;
    }

    /**
     * Check that options have the correct arguments.
     * 
     * @param options
     *            array of options
     * @param reporter
     *            utility to print argument errors
     * @return true if the options are valid
     */
    public static boolean validOptions(final String[][] options, final DocErrorReporter reporter) {

        for (Class<?> doclet : DOCLETS) {

            try {
                // get the doclet's validOptions method
                Method m =
                        doclet.getMethod("validOptions", String[][].class, DocErrorReporter.class);
                // invoke the method
                Object result = m.invoke(null, options, reporter);

                // if any child doclet reports false, reject the options
                if (!((Boolean) result)) {
                    return false;
                }

            } catch (NoSuchMethodException e) {
                reporter.printWarning("No 'validOptions' method exists for doclet "
                        + doclet.getCanonicalName());
            } catch (Exception e) {
                reporter.printError("Problem occured during 'validOptions for doclet "
                        + doclet.getCanonicalName());
            }

        }

        return true;
    }

    /**
     * Check for doclet-added options. Returns the number of arguments you must specify on the
     * command line for the given option.
     * 
     * @param option
     *            a command line option
     * @return number of arguments on the command line for an option including the option name
     *         itself
     */
    public static int optionLength(final String option) {

        for (Class<?> doclet : DOCLETS) {

            try {
                // get the doclet's optionLength method
                Method m = doclet.getMethod("optionLength", String.class);
                // invoke the method
                Object result = m.invoke(null, option);

                Integer value = (Integer) result;

                // if the child reports a value greater than 0 we use it
                if (value != 0) {
                    return value;
                }

            } catch (Exception e) {
                // silent
            }

        }

        return 0;
    }

    /**
     * Return the version of the Java Programming Language supported by
     * this doclet. 
     * 
     * @return the language version supported by this doclet
     */
    public static LanguageVersion languageVersion() {
        return LanguageVersion.JAVA_1_5;
    }
}
