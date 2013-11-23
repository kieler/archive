package de.cau.cs.kieler.doclets.extensions

import com.sun.javadoc.MethodDoc
import java.util.List

@Data
class ExtensionDescr {

    MethodDoc methodDoc
    
    String classification
    
    List<String> category

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

    def paramTypeNames() {
        methodDoc.parameters.tail.map [ p |
            p.name
        ]
    }
    
    
}
