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

import com.sun.javadoc.MethodDoc
import java.util.List

/**
 * Descriptor class for an extensions, provides several methods to conveniently extract information.
 * 
 * @author uru
 */
@Data
class ExtensionDescr {

    /** the unterlying javadoc element. */
    MethodDoc methodDoc

    /** the classification of this extension, e.g. "ptolemy". */
    String classification

    /** the physical source file this extension is defined in. */
    String containingFile

    def name() {
        methodDoc.name
    }

    def firstParamType() {
        methodDoc.parameters.head.typeName
    }

    def firstParamName() {
        methodDoc.parameters.head.name
    }

    def paramTypes() {
        methodDoc.parameters.tail.map [ p |
            p.typeName
        ]
    }

    def paramNames() {
        methodDoc.parameters.tail.map [ p |
            p.name
        ]
    }

    def String returnType() {
        methodDoc.returnType.simpleTypeName
    }

    /**
     * @prefix a prefix that is prepended to the id
     * @return an identifier string that uniquely identifies this extensions,
     *         i.e., it includes from which file this extension comes, the type on which
     *         the extension is invoked and all its parameters.
     */
    def String genId(String prefix) {
        val id = prefix + containingFile + name + firstParamType + paramTypes.join("")

        return id.replaceAll("\\.", "")
    }

    def List<String> categories() {
        methodDoc.tags(KRenderingExtensionsDoclet.TAG_EXTENSION_CATEGORY).map[tag|tag.text]
    }

    /**
     * @param lines number of lines that are returned at most
     * @return a snippet from the first specified example, if existent
     */
    def String firstExample(int lines) {
        val code = methodDoc.tags(KRenderingExtensionsDoclet.TAG_EXAMPLE)
        val example = if (code.length > 0) {
                val text = code.head.text
                text.split("\n")
                .take(lines)
                .map[it]
                .join("\\n")
                .replace("\"", "\\\"") // escape quotes, otherwise json string syntax breaks
            } else {
                ""
            } 
        example
    }

    /**
     * @return html
     */
    def methodSig() {
        val params = methodDoc.parameters.tail.map [ p |
            p.typeName
        ].join(", ")

        '''
            <span style="font-size: 0.7em" class="text-muted pull-right">«containingFile»</span>
            «methodDoc.name»(<em>«params»</em>): «returnType»
        '''
    }

    /**
     * @return html code
     */
    def description() {
        val comment = methodDoc.commentText.trim

        '''
            <p>
                «comment»
                «if (!categories.isEmpty) {
                '''
                    <span style="font-size: 0.7em" class="text-muted pull-right">
                    Categories: «categories.join(", ")»
                    </span>
                '''
            }»
            </p>
        '''
    }

    /**
     * @return html code
     */
    def String code() {
        val code = methodDoc.tags(KRenderingExtensionsDoclet.TAG_EXAMPLE)

        if (code.length > 0)
            {
                '''<h6>Example Usage</h6>'''
            } + code.map [ example |
                val text = example.text.replaceAll("<pre>", "").replaceAll("</pre>", "")
                '''
                    <pre class="prettyprint lang-xtend">
                ''' + " " + text.split("\n").map[it].join("\n") + '''
                    </pre>
                '''
            ].join("\n")
    }

}
