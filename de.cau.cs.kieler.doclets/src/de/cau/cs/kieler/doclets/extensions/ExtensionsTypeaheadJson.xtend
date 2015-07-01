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

import com.google.common.collect.Lists

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
            val tokens = Lists.newArrayList(ext.name , ext.name.stripGetOrSet)  
            tokens += tokens.map[t | "e:" + t]
            
            '''
                {
                    "value": "«ext.name»",
                    "tokens": [«tokens.map["\"" + it + "\""].join(",")»],
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
    
    def String typeAheadJsonLayoutOpts(Iterable<LayoutOptionDescr> layoutOpts) {
        '''['''
        +
        layoutOpts.map [ opt |
            val tokens = Lists.newArrayList(opt.name , opt.name.replace("_", " "),  
                    opt.id, opt.lastIdSegment)
            tokens += tokens.map[t | "l:" + t]
            
            '''
            {
                "value": "«opt.name»",
                "tokens": [«tokens.map["\"" + it + "\""].join(",")»],
                "id": "«opt.id»",
                "type": "«opt.propertyType»",
                "hrefid": "«opt.getHrefId»" 
            }
            '''
        ].join(",\n")
        +       
        ''']'''
    }
    
    
    def String stripGetOrSet(String s) {
        if (s.startsWith("get") || s.startsWith("set")) {
            return s.substring(3, s.length)
        }
    }
} 