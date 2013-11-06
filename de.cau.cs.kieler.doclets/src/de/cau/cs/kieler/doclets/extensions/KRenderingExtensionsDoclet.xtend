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

import com.google.common.base.Charsets
import com.google.common.collect.HashMultimap
import com.google.common.collect.Maps
import com.google.common.collect.Multimap
import com.google.common.io.Files
import com.sun.javadoc.MethodDoc
import com.sun.javadoc.RootDoc
import java.io.File
import java.util.Collection
import java.util.Map
import java.util.Map.Entry

/**
 * Doclet to create an overview page for all available KLighD extensions.
 * 
 * @author uru
 */
class KRenderingExtensionsDoclet {

    public static val TAG_EXTENSION = "@containsExtensions"
    public static val TAG_EXAMPLE = "@example"

    private static val DEFAULT_CATEGORY = "default"    
    
    private static val FILE_NAME = "../extensions.html"

    static Map<String, Multimap<String, MethodDoc>> categoryMap = Maps.newHashMap
    
    /** Holds the corresponding extension file for a method (e.g. KRenderingExtensions) */
    static Map<String, String> extensionFileMap = Maps.newHashMap

    /** 
      * Main Entry point of doclets
      */
    static def boolean start(RootDoc rootDoc) {

         // add a map for the default category
        categoryMap.put(DEFAULT_CATEGORY, HashMultimap.create) 

        // collect all extensions of classes annotated with @KRenderingExtensions
        rootDoc.classes.filter[it.tags(TAG_EXTENSION).length > 0].forEach [ clazz |
            
            // check for a category
            val categoryString = clazz.tags(TAG_EXTENSION).get(0).text
            
            // get the correct map
            val map = if (categoryString.trim.nullOrEmpty) {
                    categoryMap.get(DEFAULT_CATEGORY)
                } else {
                    if(!categoryMap.containsKey(categoryString)) {
                        categoryMap.put(categoryString, HashMultimap.create)
                    }
                    categoryMap.get(categoryString)
                }
                
            
            // collect all non static methods with at least one parameter
            clazz.methods.filter[!it.static].filter[it.parameters.length > 0].forEach [ method |
                var firstParam = method.parameters.get(0)
                // TODO handle a type variable as parameter type
                map.put(firstParam.typeName, method)   
                extensionFileMap.put(method.name, clazz.name)
            ]
        ]
        
        // generate the html 
        Files.write(genHtml(), new File(FILE_NAME) , Charsets.UTF_8)

        return true
    }
   
    
    /* ------------------------------------------------------
     *                      HTML Skeleton
     */
    static def String genHtml() {
        '''
            <!DOCTYPE doctype PUBLIC '-//w3c//dtd html 4.0 transitional//en'>
            <html>
                <head>
                    <title>KIELER KRendering Extensions</title>
                    <link href="http://netdna.bootstrapcdn.com/bootstrap/3.0.1/css/bootstrap.min.css" rel="stylesheet">
                    <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"></script>
                    <script src="http://netdna.bootstrapcdn.com/bootstrap/3.0.1/js/bootstrap.min.js"></script>
                    <style>
                        body {
                            margin-top: 20px;
                        }
                    </style>
                </head>
                <body>
                    <!-- navigation -->
                    «navigation»
                    <!-- content -->
                    <div class="container">
                        <div class="row">
                            <div class="col-md-12">
                                <div class="jumbotron">
                                    <h1>KIELER KRendering Extensions</h1>
                                    <p>
                                        Here you find all the wonderful extension methods for KLighD's diagram syntheses.
                                        They are so much fun!
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
        <header class="navbar navbar-inverse navbar-fixed-top bs-docs-nav" role="banner">
            <div class="container">
                <!-- Brand and toggle get grouped for better mobile display -->
                <div class="navbar-header">
                    <button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#navbar-collapse">
                        <span class="sr-only">Toggle navigation</span>
                        <span class="icon-bar"></span>
                        <span class="icon-bar"></span>
                        <span class="icon-bar"></span>
                    </button>
                    <a class="navbar-brand" href="#">KIELER</a>
                </div>
                
                <!-- The actual nav bar -->
                <nav class="collapse navbar-collapse" id="navbar-collapse">
                    <ul class="nav navbar-nav">
                        «navigationCategories»
                    </ul>
                </nav>
            </div>
        </header>
        '''    
    }
    
    static def navigationCategories() {
        categoryMap.entrySet.sortBy[it.key].map [ entry |
            val heading = entry.key.toFirstUpper + " Extensions"
            '''
            <li class="dropdown">
                <a href="#" class="dropdown-toggle" data-toggle="dropdown">«heading»<b class="caret"></b></a>
                <ul class="dropdown-menu">
                    «entry.value.navigationItems»
                </ul>
            </li>
            '''
        ].join("\n")
    }
    
    static def navigationItems(Multimap<String, MethodDoc> mmap) {
        mmap.asMap.entrySet.sortBy[it.key].map [ entry |
            '''
            <li>
                <a href="#«entry.key»">«entry.key»</a>
            </li>
            '''
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
        
        categoryMap.entrySet.sortBy[it.key].map[ entry |
            '''
            <h1>«entry.key.toFirstUpper»</h1>
            '''
            + 
            entry.value.asMap.entrySet.sortBy[it.key].map [ centry |
                //contentTable(centry)
                contentPanels(centry)
            ].join("\n")
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
    
    static def contentPanels(Entry<String, Collection<MethodDoc>> entry) {
        val clazz = entry.key
        '''
        <h2 id="«clazz»">«clazz»</h2>
        «contentItemsPanel(entry)»
        '''
    }

    static def contentItems(Entry<String, Collection<MethodDoc>> entry) {
        entry.value.sortBy[it.name].map [
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
    
    static def contentItemsPanel(Entry<String, Collection<MethodDoc>> entry) {
        entry.value.sortBy[it.name].map [
            '''
            <div id="«it.name»" class="panel panel-default">
              <div class="panel-heading">
                <h3 class="panel-title">«it.methodSig»</h3>
              </div>
              <div class="panel-body">
                «it.description»
                «it.code»
              </div>
            </div>
            '''
        ].join("\n")
    }
    
    static def methodSig(MethodDoc doc) {
        val params = doc.parameters.tail.map [ p | 
            p.typeName
        ].join(", ")

        
        '''
            <span style="font-size: 0.7em" class="text-muted pull-right">«extensionFileMap.get(doc.name)»</span>
            «doc.name»(<em>«params»</em>)
        '''
    }
    
    static def description(MethodDoc doc) {
        val comment = doc.commentText.trim
        
        '''
        <p>
            «comment»
        </p>
        '''
    }
    
    static def code(MethodDoc doc) {
        val code = doc.tags(TAG_EXAMPLE)

        if (code.length > 0) {
            val text = code.get(0).text.trim
            if (text.contains("<pre>")) {
                return text
            } else {
                '''
                    <pre>
                    «text»
                    </pre>
                    
                '''
            }
        } else {
            ''''''
        }
    }
}
