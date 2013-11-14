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
import de.cau.cs.kieler.doclets.Util

/**
 * Doclet to create an overview page for all available KLighD extensions.
 * 
 * @author uru
 */
class KRenderingExtensionsDoclet {

    public static val TAG_EXTENSION = "@containsExtensions"
    public static val TAG_EXAMPLE = "@example"
    public static val TAG_EXTENSION_CATEGORY = "@extensionType"

    private static val DEFAULT_CATEGORY = "default"    
    
    private static val DOC_ROOT = "./extensions/"

    /** Maps classes to their extensions, e.g. KRendering -> setGridPlacementData. */
    static Multimap<String, MethodDoc> clazzMap = HashMultimap.create
    
    /** Maps global classifications to extensions, e.g. Ptolemy -> [all ptolemy extensions]. */
    static Map<String, Multimap<String, MethodDoc>> classificationMap = Maps.newHashMap
    
    /** Maps certain categories to extensions, e.g. compositional extensions -> addRectangle */
    static Multimap<String, MethodDoc> categoryMap = HashMultimap.create
    
    /** Holds the corresponding extension file for a (class, method) pair (e.g. KRenderingExtensions) */
    static Map<Pair<String, String>, String> extensionFileMap = Maps.newHashMap

    /** 
      * Main Entry point of doclets
      */
    static def boolean start(RootDoc rootDoc) {

         // add a map for the default category
        classificationMap.put(DEFAULT_CATEGORY, HashMultimap.create) 

        // collect all extensions of classes annotated with @KRenderingExtensions
        rootDoc.classes.filter[it.tags(TAG_EXTENSION).length > 0].forEach [ clazz |
            
            // check for a category
            val categoryString = clazz.tags(TAG_EXTENSION).get(0).text
            
            // get the correct map
            val map = if (categoryString.trim.nullOrEmpty) {
                    classificationMap.get(DEFAULT_CATEGORY)
                } else {
                    if(!classificationMap.containsKey(categoryString)) {
                        classificationMap.put(categoryString, HashMultimap.create)
                    }
                    classificationMap.get(categoryString)
                }
                
            
            // collect all non static methods with at least one parameter
            clazz.methods.filter[!it.static].filter[it.parameters.length > 0].forEach [ method |
                var firstParam = method.parameters.get(0)
                
                // class -> extension method (for the global classification)
                map.put(firstParam.typeName, method)

                // class -> extension method (superset)
                clazzMap.put(firstParam.typeName, method)
                
                // extension -> containing class file   
                extensionFileMap.put(Pair.of(firstParam.typeName, method.name), clazz.name)
                
                // extension category -> extension method
                val category = method.tags(TAG_EXTENSION_CATEGORY).map[tag | tag.text]
                if (!category.empty) {
                    categoryMap.put(category.head, method)
                }
            ]
        ]
        
        // make sure the root folder exists
        val docRoot = new File(DOC_ROOT)
        if(!docRoot.exists) {
            docRoot.mkdirs
        }
        
        // copy resourcces
        val util = new Util
        
        val css = new File(DOC_ROOT, "css")
        css.mkdirs
        util.copyResource("bootstrap-3.0.2.min.css", css)
        util.copyResource("prettify.css", css)
        
        val js = new File(DOC_ROOT, "js")
        js.mkdirs
        util.copyResource("bootstrap-3.0.2.min.js", js)
        util.copyResource("jquery-1.10.2.min.js", js)
        util.copyResource("prettify.js", js)
        util.copyResource("xtend-lang.js", js)
        util.copyResource("jquery.syntaxhighlighter.min.js", js)
        
        // generate the root html page 
        Files.write(genRootPage(), new File(DOC_ROOT + "index.html") , Charsets.UTF_8)
        
        // generate the categories page 
        Files.write(genCategoriesPage(), new File(DOC_ROOT + "categories.html") , Charsets.UTF_8)

        // generate a page for each class for which extensions exist
        //clazzMap.asMap.entrySet.forEach [ entry | 
        //    val file = new File(DOC_ROOT + entry.key + ".html")
        //    Files.write(genClazzPage(entry), file, Charsets.UTF_8)
        //]

        return true
    }
    
    static def genRootPage() {
        contentRoot.withSkeleton
    }
    
    static def genClazzPage(Entry<String, Collection<MethodDoc>> entry) {
        contentClass(entry).withSkeleton
    }
   
   static def genCategoriesPage() {
       contentCategories.withSkeleton
   }
    
    /* ------------------------------------------------------
     *                      HTML Skeleton
     */
     
     static def withSkeleton(CharSequence content) {
          '''
            <!DOCTYPE HTML>
            <html>
                <head>
                    <title>KIELER KRendering Extensions</title>
                    <meta charset="UTF-8">
                    <link href="css/bootstrap-3.0.2.min.css" rel="stylesheet" type="text/css">
                    <link href="css/prettify.css" rel="stylesheet" type="text/css">
                    <script src="js/jquery-1.10.2.min.js" type="text/javascript"></script>
                    <script src="js/bootstrap-3.0.2.min.js" type="text/javascript"></script>
                    <script src="js/prettify.js" type="text/javascript"></script>
                    <script src="js/jquery.syntaxhighlighter.min.js" type="text/javascript"></script>
                    <script src="js/xtend-lang.js" type="text/javascript"></script>
                    <style type="text/css">
                        .content {
                            margin-top: 50px;
                        }
                        .panel-group {
                            margin-bottom: 10px;
                        }
                        .k-panel {
                            padding: 10px;
                        }
                        .k-table {
                            margin-bottom: 0px;
                        }
                        .no-padding {
                            padding: 0px;
                        }
                        .k-title > a {
                            color: inherit; 
                        }
                        /* Line numbers on every line. */
                        li.L0, li.L1, li.L2, li.L3,
                        li.L5, li.L6, li.L7, li.L8 {
                            list-style-type: decimal !important
                        }
                    </style>
                    <script type="text/javascript">
                        (function(jQuery) {
                            jQuery( document ).ready( function() {
                                //prettyPrint();
                                $.SyntaxHighlighter.init();
                            });
                        }(jQuery))
                    </script>
                </head>
                <body>
                    <!-- navigation -->
                    «navigation»
                    <!-- content -->
                    <div class="content">
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
                        <a class="navbar-brand" href="index.html">KIELER</a>
                    </div>
                    
                    <!-- The actual nav bar -->
                    <nav class="collapse navbar-collapse" id="navbar-collapse">
                        <ul class="nav navbar-nav">
                            «navigationClassifications»
                            «navigationCategories»
                        </ul>
                    </nav>
                </div>
            </header>
        '''    
    }
    
    static def navigationClassifications() {
        classificationMap.entrySet.sortBy[it.key].map [ entry |
            val heading = entry.key.toFirstUpper + " Extensions"
            '''
                <li class="dropdown">
                    <a href="#" class="dropdown-toggle" data-toggle="dropdown">«heading»<b class="caret"></b></a>
                    <ul class="dropdown-menu">
                        «navigationItems(entry.key, entry.value)»
                    </ul>
                </li>
            '''
        ].join("\n")
    }
    
    static def navigationItems(String classification, Multimap<String, MethodDoc> mmap) {
        mmap.asMap.entrySet.sortBy[it.key].map [ entry |
            '''
                <li>
                  <a href="index.html#«classification»«entry.key»">«entry.key»</a>
                </li>
            '''
        ].join("\n")
    }
    
    static def navigationCategories() {
        '''
            <li class="dropdown">
                <a href="#" class="dropdown-toggle" data-toggle="dropdown">Categories<b class="caret"></b></a>
                <ul class="dropdown-menu">
                    «navigationCategoriesItems»
                </ul>    
            </li>
        '''
    }
    
    static def navigationCategoriesItems() {
        categoryMap.asMap.entrySet.sortBy[it.key].map [ entry |
            '''
                <li>
                    <a href="categories.html#«entry.key»">«entry.key»</a>
                </li>
            '''
        ].join("\n")
    }


    /* ------------------------------------------------------
     *                      Root Content
     */
    static def contentRoot() {
        classificationMap.entrySet.sortBy[it.key].map[ entry |
            '''
                <h1>«entry.key.toFirstUpper»</h1>
            '''
            + 
            entry.value.asMap.entrySet.sortBy[it.key].map [ centry |
                contentRootTable(entry.key, centry)
            ].join("\n")
        ].join("\n")
    }
    
    
    static def contentRootTable(String idPrefix, Entry<String, Collection<MethodDoc>> entry) {
        val clazz = entry.key
        '''
            <div id="«idPrefix»«clazz»" class="panel-group">
              <div class="panel panel-default">
                <div class="panel-heading">
                  <h4 class="panel-title">
                    <a data-toggle="collapse" href="#collapse«idPrefix»«clazz»">
                      «clazz»
                    </a>
                  </h4>
                </div>
                <div id="collapse«idPrefix»«clazz»" class="panel-collapse collapse">
                  <div class="panel-body k-panel">
                     <div class="table-responsive">
                        <table class="table table-hover table-striped table-condensed k-table">
                            «contentRootItems(idPrefix, entry)»
                        </table>
                     </div>
                  </div>
                </div>
              </div>
            </div>
        '''
    }
    
    static def genId(String prefix, MethodDoc extsn) {
        prefix + extsn.name + extsn.parameters.head.typeName + extsn.parameters.tail.map [ p |
            p.typeName
        ].join("").replaceAll("\\.", "")
    }
    
    static def contentRootItems(String idPrefix, Entry<String, Collection<MethodDoc>> entry) {
        entry.value.sortBy[it.name].map [ extsn |
            
            val id = idPrefix.genId(extsn)
            
            '''
                <tr>
                    <td>
                        <div class="panel-heading k-title no-padding">
                            <a data-toggle="collapse" href="#collapse«id»">
                                 «extsn.methodSig»
                            </a>
                        </div>
                        <div id="collapse«id»" class="panel-collapse collapse">
                            <div class="panel-body">
                               «extsn.description»
                               «extsn.code»
                             </div>
                         </div>
                    </td>
                </tr>
            '''
        ].join("\n")
    }
    
    /* ------------------------------------------------------
     *                      Categories Content
     */
     static def contentCategories() {
         '''
            <h1>Categories</h1>
         '''
         +
         categoryMap.asMap.entrySet.sortBy[it.key].map [ entry |
            '''
                «contentCategoriesTable(entry.key, entry)»
            '''
         ].join("\n")
     }
     
     static def contentCategoriesTable(String idPrefix, Entry<String, Collection<MethodDoc>> entry) {
        val clazz = entry.key
        '''
            <div id="«idPrefix»«clazz»" class="panel-group">
              <div class="panel panel-default">
                <div class="panel-heading">
                  <h4 class="panel-title">
                    <a data-toggle="collapse" href="#collapse«idPrefix»«clazz»">
                      «clazz»
                    </a>
                  </h4>
                </div>
                <div id="collapse«idPrefix»«clazz»" class="panel-collapse collapse">
                  <div class="panel-body k-panel">
                     <div class="table-responsive">
                        <table class="table table-hover table-striped table-condensed k-table">
                            «contentCategoriesItems(idPrefix, entry)»
                        </table>
                     </div>
                  </div>
                </div>
              </div>
            </div>
        '''
    }
    
    
    
    static def contentCategoriesItems(String idPrefix, Entry<String, Collection<MethodDoc>> entry) {
        entry.value.sortBy[it.name].map [ extsn |
            
            val id = idPrefix.genId(extsn)
            val firstParam = extsn.parameters.head.typeName
            
            '''
                <tr>
                    <td>
                        <div class="panel-heading k-title no-padding">
                            <a data-toggle="collapse" href="#collapse«id»">
                                 <em>«firstParam»</em> «extsn.methodSig»
                            </a>
                        </div>
                        <div id="collapse«id»" class="panel-collapse collapse">
                            <div class="panel-body">
                               «extsn.description»
                               «extsn.code»
                             </div>
                         </div>
                        <!--a href="«entry.key».html#«extsn.name»">
                            «extsn.methodSig»
                        </a-->
                    </td>
                </tr>
            '''
        ].join("\n")
    }
    
    /* ------------------------------------------------------
     *                  Class Content
     */
     static def contentClass(Entry<String, Collection<MethodDoc>> entry) {
         
         '''
            <h1>«entry.key»</h1>
         '''
         +
         // first a condensed table
         contentClassTable(entry)
         +
         '''
            <h1>Detailed List</h1>
         '''
         + 
         // second a detailed list with all extensions
         contentClassDetailedList(entry)
     }
     
     static def contentClassTable(Entry<String, Collection<MethodDoc>> entry) {
         '''
            <div class="table-responsive">
                <table class="table table-hover table-bordered table-striped table-condensed">
                    <thead>
                        <th>Name</th>
                    </thead>
                    «contentClassItems(entry)»
                </table>
            </div>
        '''
     }
     
     static def contentClassItems(Entry<String, Collection<MethodDoc>> entry) {
        entry.value.sortBy[it.name].map [
            '''
                <tr>
                    <td>
                        <a href="#«it.name»">
                            «it.methodSig»
                        </a>
                    </td>
                </tr>
            '''
        ].join("\n")
    }
    
     
    static def contentClassDetailedList(Entry<String, Collection<MethodDoc>> entry) {
        '''
            «contentItemsPanel(entry)»
        '''
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

        val firstParam = doc.parameters.head.typeName // head always exists
        val fileName = extensionFileMap.get(Pair.of(firstParam, doc.name))
        '''
            <span style="font-size: 0.7em" class="text-muted pull-right">«fileName»</span>
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
            val text = code.get(0).text.replaceAll("<pre>", "").replaceAll("</pre>", "")
            '''
                <h6>Example Usage</h6>
                <pre class="highlight linenums lang-xtend">
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
     
}
