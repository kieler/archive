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
    
    
     def methodSig() {
        val params = methodDoc.parameters.tail.map [ p | 
            p.typeName
        ].join(", ")

//        val firstParam = doc.parameters.head.typeName // head always exists

        // TODO MEGA INEFFICIENT
//        val files = extensions.groupBy [ it.firstParamType ].get(firstParam).groupBy [ it.name ].get(doc.name)
//        val fileName = files.map[it.containingFile].join(", ")
        
//        val fileName = extensionFileMap.get(Pair.of(firstParam, doc.name))
        '''
            <span style="font-size: 0.7em" class="text-muted pull-right">«containingFile»</span>
            «methodDoc.name»(<em>«params»</em>)
        '''
    }
    
    def description() {
        val comment = methodDoc.commentText.trim
        
        '''
            <p>
                «comment»
            </p>
        '''
    }
    
    def String code() {
        val code = methodDoc.tags(KRenderingExtensionsDoclet.TAG_EXAMPLE)

        if (code.length > 0) {
            val text = code.get(0).text.replaceAll("<pre>", "").replaceAll("</pre>", "")
            '''
                <h6>Example Usage</h6>
                <pre class="highlight lang-xtend">
            '''
            +
            text
            +            
            '''
                </pre>
            '''
        } else {
            ''''''
        }
    }
    
    def String genId(String prefix) {

        val id = prefix + containingFile + name + firstParamType + paramTypeNames.join("")
        
        return id.replaceAll("\\.", "")
    }
}
