/*
 * KIELER - Kiel Integrated Environment for Layout Eclipse RichClient
 *
 * http://www.informatik.uni-kiel.de/rtsys/kieler/
 * 
 * Copyright 2013 by
 * + Kiel University
 *   + Department of Computer Science
 *     + Real-Time and Embedded Systems Group
 * 
 * This code is provided under the terms of the Eclipse Public License (EPL).
 * See the file epl-v10.html for the license text.
 */
package de.cau.cs.kieler.doclets.extensions

/**
 * Generates a help page which for instance contains information on how 
 * to annotate extension classes to make them visible within this documentation.
 * 
 * @author uru
 */
class ExtensionsHelpPage {
    
    
    def extensionAnnotations() {
        '''
        <h1>How to Annotate Extension Classes</h1>
        
        <h2>Class</h2>
        <p>
            To make a class available to this documentation add a <code>@containsExtensions</code>
            tag to the class' javadoc.
        </p>
        
        «example(
        '''
            /**
             * @author me
             * @containsExtensions
             */
            class MyExtensionsClass {
                [...]
            }
        '''
        )»
        
        <h3>Classification</h3>
        <p>
            It is possible to group different extension classes together, for example, all of Ptolemy's
            extensions belong to the same logic block. To create such a classification just add an
            identifier to the <code>@containsExtensions</code> tag. The default classification is
            <em>default</em>.
        </p>
        
        «example(
        '''
            /**
             * @containsExtensions ptolemy
             */
            class AnnotationExtensions {
                [...]
            }
        ''')»
        
        <h2>Method</h2>
        <p>
            All methods of an extension class are grouped by the class of their first parameter. For each 
            class a section is created within the documentation that lists all available extensions for 
            thiss type. The documentation text equals the javadoc text of the respective method.
        </p>
        
        <div class="alert alert-info">
            It is possible to use arbitrary html within the documentation text.
        </div>
        
        «example(
        '''
            /** 
             * Retrieves the rendering element from the <em>library</em> that has been identified with
             * the <code>id</code> string.  
             */
            def KRenderingRef getFromLibrary(KRenderingLibrary library, String id) {
                [...]
            }
        '''
        )»
        
        <h3>Example</h3>
        <p>
            It is possible to attach small examples to an extension's documentation that illustrate how 
            the extension is supposed to be used. For this, add a <code>@example</code> tag to the
            javadoc, followed by the preformatted example. You can add multiple example tags.
        </p>
        
        <div class="alert alert-info">
            You can surround the example with <code>&lt;pre&gt;</code> so that automatic 
            code formatting does not break its indentation.
        </div>
        
        «example(
        '''
            /**
             * Convenient creation of color objects. Allows several names (red, blue, black, etc) 
             * and hex strings (#00ff00). 
             * 
             * @example
             * rectangle.background = "black".color
             * rectangle.foreground = "#00ff00".color
             */
            def KColor getColor(String name) {
                [...]
            }
        '''
        )»
        
        <h3>Category</h3>
        <p>
            By default the extensions are grouped by the type of the class they are applied to. However,
            sometimes it is useful to group them by their purpose, for instance <em>styling</em> or 
            <em>composing</em> extensions. For this add a <code>@extensionCategory</code> tag to the 
            javadoc followed by a category identifier. A method can be categorized under multiple 
            categories, just add multiple tags.
        </p>
        
        «example(
        '''
            /*
             * .. a styling method ..
             * @extensionCategory style
             */
            def KRendering getHairCut(KRendering ren) {
                [...]
            }
             
             /*
             * .. a composing method ..
             * @extensionCategory composing
             */
            def KContainerRendering getNewHouse(KContainerRendering ren) {
                [...]
            }
        '''
        )»
        
        
        '''
    }
    
    def example(String code) {
        '''
        <div>
            <div class="text-muted">Example</div>
            <pre class="prettyprint lang-xtend">
                «code»
            </pre>
        </div>
        '''        
    }
}