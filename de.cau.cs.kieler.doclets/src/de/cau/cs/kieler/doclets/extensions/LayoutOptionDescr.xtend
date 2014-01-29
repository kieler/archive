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

import com.sun.javadoc.FieldDoc
import com.sun.javadoc.ClassDoc

/**
 * @author uru
 */
@Data
class LayoutOptionDescr {
    
    ClassDoc containingClass
    
    String category
    
    FieldDoc doc
    
    def name() {
        return doc.name
    }
    
    /**
     * @return an artificial id used in html links.
     */
    def getHrefId() {
        return (containingClass.name + category + doc.name).replace(".", "").replace("_", "")
    }
    
    /**
     * @return the actual 'id' of the property, e.g. "de.cau.cs.kieler.spacing".
     */
    def String getId() {
        
        var id = "unknown";
        
        try {
            // really ugly :)
            val field = doc.getClass().getSuperclass().getSuperclass().getDeclaredField("tree");
            field.setAccessible(true);
            id = field.get(doc).toString
            
            id = id.substring(id.indexOf("\"") + 1, id.length())
            id = id.substring(0, id.indexOf("\""));
            
            
        } catch (Exception e) {
            e.printStackTrace();
        }

        return id
    }
    
    /**
     * @return the last segment of the id, e.g. for "de.cau.cs.kieler.spacing" it returns "spacing".
     */
    def getLastIdSegment() {
        val id = getId
        return id.substring(id.lastIndexOf("."))
    }
    
    def String getPropertyType() {
        
        val paraType = doc.type.asParameterizedType
        if (paraType != null) {
           
           val propType = paraType.typeArguments.map [ arg |
               // check for inner generic types
               val innerParaType = arg.asParameterizedType
               if (innerParaType != null) {
                   val inner = innerParaType.typeArguments.map[it.typeName].join(", ")
                   arg.typeName + "<" + inner + ">"                   
               } else {
                   arg.typeName
               }
           ]
           
           if(!propType.isEmpty) {
               return propType.head
           }
           
        } 
        
        return ""
    }
    
    def title() {
        '''
        <small><b>«name»</b></small> 
        <code>"«getId»"</code> 
        <code>«getPropertyType.replace("<", "&lt;").replace(">", "&gt;")»</code>
        '''
    }
    
    def description() {
        '''
        <p>
            «doc.commentText»
        </p>
        '''
    }
    
}