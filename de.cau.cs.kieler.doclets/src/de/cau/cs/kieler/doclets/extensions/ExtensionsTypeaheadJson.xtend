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
package de.cau.cs.kieler.doclets.extensions

/**
 * @author uru
 */
class ExtensionsTypeaheadJson {
    
    /* ------------------------------------------------------
     *                      Typeahead Content
     */
     
     /**
      * CARE: typeahead REQUIRES 
      *  - the keys to be in quotes
      *  - only use double quotes, no single quotes
      */
     def String typeAheadJson(Iterable<ExtensionDescr> extensions) {
        '''['''
        +
        extensions.map [ ext |
            val exampleText = ext.firstExample(5)
            '''
                {
                    "value": "«ext.name»",
                    "tokens": ["«ext.name»"],
                    "firstParam": "«ext.firstParamType»",
                    "params": [ «ext.paramTypes.map[''' "«it»" '''].join(", ")» ],
                    "returnType": "«ext.returnType»",
                    "file": "«ext.containingFile»",
                    "hrefid": "«ext.genId(ext.classification)»"
                    «if(!exampleText.nullOrEmpty)
                    ''',
                    "example": {
                        "text": "«exampleText»"
                    }
                    '''
                    »
                }'''
        ].join(",\n")
        +
        ''']'''
    }
} 