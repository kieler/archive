package de.cau.cs.kieler.doclets.extensions

import com.google.common.base.Charsets
import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.google.common.io.Files
import com.sun.javadoc.MethodDoc
import com.sun.javadoc.RootDoc
import java.io.File
import java.util.Map.Entry
import java.util.Collection

class KRenderingExtensionsDoclet {

    static val KRENDERING_EXTENSION_INDICATOR = "@KRenderingExtensions"

    static Multimap<String, MethodDoc> extensionsMap = HashMultimap.create

    /** 
      * Main Entry point of doclets
      */
    static def boolean start(RootDoc rootDoc) {

        // collect all extensions of classes annotated with @KRenderingExtensions
        rootDoc.classes.filter[it.tags(KRENDERING_EXTENSION_INDICATOR).length > 0].forEach [ clazz |
            // collect all non static methods with at least one parameter
            clazz.methods.filter[!it.static].filter[it.parameters.length > 0].forEach [ method |
                val firstParam = method.parameters.get(0)
                extensionsMap.put(firstParam.typeName, method)
            ]
        ]
        
        println("\n\nGENERATING !!!!")
        
        println(extensionsMap.keySet)
        
        
        // generate the html 
        Files.write(genHtml(), new File("extensions.html") , Charsets.UTF_8)

        println("\n\nGENERATING !!!!")

        return true
    }
   
    
    /* ------------------------------------------------------
     *                      Skeleton
     */
    static def String genHtml() {
        '''
            <!DOCTYPE doctype PUBLIC '-//w3c//dtd html 4.0 transitional//en'>
            <html>
                <head>
                    <title>KIELER KRendering Extensions</title>
                    <link href="http://netdna.bootstrapcdn.com/bootstrap/3.0.1/css/bootstrap.min.css" rel="stylesheet">
                    <script src="http://netdna.bootstrapcdn.com/bootstrap/3.0.1/js/bootstrap.min.js"></script>
                </head>
                <body>
                    <div class="container">
                        <div class="row">
                            <!-- navigation -->
                            <div class="col-md-2">
                                «navigation»
                            </div>
                            <!-- content -->
                            <div class="col-md-10">
                                <div class="jumbotron">
                                    <h1>KIELER KRendering Extensions</h1>
                                    <p>
                                        Here you find all the wonderful extension methods for KLighD'a diagram syntheses.
                                        The are so much fun!
                                    </p>
                                </div>
                                «content»
                            </div>
                        </div>
                    </div>
                </body>
            </html>
        '''
    }
    
    /* ------------------------------------------------------
     *                      Navigation
     */
    static def navigation() {
        '''
        <div class="affix">
            <ul id="navi" class="nav">
                «navigationItems»
            </ul>
        </div>
        '''       
    }
    
    static def navigationItems() {
        extensionsMap.asMap.entrySet.map [ entry |
            '''
            <li>
                <a href="#«entry.key»">«entry.key»</a>
            </li>
            '''
            //                «entry.value.navigationChilds»
        ].join("\n")
    }
    
    static def navigationChilds(Collection<MethodDoc> methods) {
        '''
        <ul class="nav">
        '''
        +
        methods.map['''
            <li>
                <a href="#«it.name»">«it.name»</a>
            </li>
        '''].join
        +
        '''
        </ul>
        '''
    }
    
    
    /* ------------------------------------------------------
     *                      Content
     */
    static def content() {
       extensionsMap.asMap.entrySet.map [ entry |
            contentTable(entry)
       ].join("\n")
    }

    
    static def contentTable(Entry<String, Collection<MethodDoc>> entry) {
        val clazz = entry.key
        '''
        <h2 id="«clazz»">«clazz»</h2>
        <div class="table-responsive">
            <table class="table table-hover table-bordered table-striped">
                <thead>
                    <th>Name</th>
                    <th>Description</th>
                </thead>
                «contentItems(entry)»
            </table>
        </div>
        '''
    }

    static def contentItems(Entry<String, Collection<MethodDoc>> entry) {
        entry.value.map [
            '''
                <tr id="«it.name»">
                    <td>«it.methodSig»</td>
                    <td>
                        «it.description»
                        «it.code»
                    </td>
                </tr>
            '''
        ].join("\n")
    }
    
    static def methodSig(MethodDoc doc) {
        val params = doc.parameters.tail.map [ p | 
            p.typeName
        ].join(", ")

        
        '''
            «doc.name»(<em>«params»</em>)
        '''
    }
    
    static def description(MethodDoc doc) {
        val descr = doc.tags("@description")
       
        if (descr.length > 0) {
            val text = descr.get(0).text
            '''
                <p>
                    «text»
                </p>
                
            '''
        } else {
            ''''''
        }
    }
    
    static def code(MethodDoc doc) {
        val code = doc.tags("@example")

        if (code.length > 0) {
            val text = code.get(0).text.trim
            '''
                <pre>
                «text»
                </pre>
                
            '''
        } else {
            ''''''
        }
    }
}
