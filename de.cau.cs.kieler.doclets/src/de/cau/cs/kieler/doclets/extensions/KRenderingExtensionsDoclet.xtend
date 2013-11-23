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
import com.google.common.collect.Lists
import com.google.common.collect.Multimap
import com.google.common.io.Files
import com.sun.javadoc.MethodDoc
import com.sun.javadoc.RootDoc
import de.cau.cs.kieler.doclets.Util
import java.io.File
import java.util.Collection
import java.util.List
import java.util.Map.Entry
import org.eclipse.xtext.xbase.lib.Functions.Function1

/**
 * Doclet to create an overview page for all available KLighD extensions.
 * 
 * @author uru
 */
class KRenderingExtensionsDoclet {

    public static val TAG_EXTENSION = "@containsExtensions"
    public static val TAG_EXAMPLE = "@example"
    public static val TAG_EXTENSION_CATEGORY = "@extensionCategory"

    private static val DEFAULT_CATEGORY = "default" 
    
    private static val DOC_ROOT = "./extensions/"

    /** Holds all extension methods. */
    private static List<ExtensionDescr> extensions = Lists.newLinkedList


   static def <T,S> Multimap<S, T> groupBy(Iterable<T> iterable, Function1<T, S> fun) {
        
        val Multimap<S, T> map = HashMultimap.create
        
        iterable.forEach [ e |
            val key = fun.apply(e)
            map.put(key, e)
        ]
        
        return map
    }


    /** 
      * Main Entry point of doclets
      */
    static def boolean start(RootDoc rootDoc) {

        // collect all extensions of classes annotated with @KRenderingExtensions
        rootDoc.classes.filter[it.tags(TAG_EXTENSION).length > 0].forEach [ clazz |
            
            // check for a classification
            val classifiString = if(clazz.tags(TAG_EXTENSION).get(0).text.trim.nullOrEmpty){
                DEFAULT_CATEGORY
            } else {
                clazz.tags(TAG_EXTENSION).get(0).text
            } 
             
            // collect all non static methods with at least one parameter
            clazz.methods.filter[!it.static].filter[it.parameters.length > 0].forEach [ method |

                // extension category -> extension method
                val categories = method.tags(TAG_EXTENSION_CATEGORY).map[tag | tag.text]
                
                // assemble a descriptor object
                val descr = new ExtensionDescr(method, classifiString, categories, clazz.name)
                extensions += descr             
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
        util.copyResource("typeahead-bootstrap.css", css)
        
        val js = new File(DOC_ROOT, "js")
        js.mkdirs
        util.copyResource("bootstrap-3.0.2.min.js", js)
        util.copyResource("jquery-1.10.2.min.js", js)
        util.copyResource("typeahead.min.js", js)
        util.copyResource("prettify.js", js)
        util.copyResource("xtend-lang.js", js)
        util.copyResource("jquery.syntaxhighlighter.min.js", js)
        util.copyResource("hogan-2.0.0.js", js)
        
        // generate the root html page 
        Files.write(indexPage(), new File(DOC_ROOT + "index.html") , Charsets.UTF_8)
        
        Files.write(genRootPage(), new File(DOC_ROOT + "classes.html") , Charsets.UTF_8)
        
        // generate the categories page 
        Files.write(genCategoriesPage(), new File(DOC_ROOT + "categories.html") , Charsets.UTF_8)

        // json data
        Files.write(typeAheadJson(), new File(DOC_ROOT + "extensions.json") , Charsets.UTF_8)

        // generate a help page
        Files.write(new ExtensionsHelpPage().extensionAnnotations.withSkeleton, 
            new File(DOC_ROOT + "help.html") , Charsets.UTF_8
        )

        return true
    }
    
    static def genRootPage() {
        // group extensions by classification
        val classifiMap = extensions.groupBy [ it.classification ]
        
        contentRoot(classifiMap).withSkeleton
    }
    
   static def genCategoriesPage() {
       
       val categoriesMap = extensions.groupBy [ it.category.head?: "none" ]

       contentCategories(categoriesMap).withSkeleton
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
                    <link href="css/typeahead-bootstrap.css" rel="stylesheet" type="text/css">
                    <link href="css/prettify.css" rel="stylesheet" type="text/css">
                    <script src="js/jquery-1.10.2.min.js" type="text/javascript"></script>
                    <script src="js/bootstrap-3.0.2.min.js" type="text/javascript"></script>
                    <script src="js/typeahead.min.js" type="text/javascript"></script>
                    <script src="js/prettify.js" type="text/javascript"></script>
                    <script src="js/jquery.syntaxhighlighter.min.js" type="text/javascript"></script>
                    <script src="js/xtend-lang.js" type="text/javascript"></script>
                    <script src="js/hogan-2.0.0.js" type="text/javascript"></script>
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
                                // pretty printing
                                $.SyntaxHighlighter.init();
                                $('input.typeahead').typeahead({
                                    name: 'find',
                                    local: «typeAheadJson»,
                                    template:  ['<p>{{value}}</p>',
                                                '<p class="text-muted" style="float: right; position: relative;">{{file}}</p>'
                                                //'<p class="text-small repo-description">{{params}}</p>'
                                               ].join(''),
                                    engine: Hogan,
                                    limit: 10
                                });
                                
                                $('.typeahead').bind('typeahead:selected', function(obj, datum, name) { 
                                    console.log("Selected: " + datum.value);
                                    location.href = "classes.html#collapse" + datum.hrefid;
                                });
                                
                                // fix
                                $('.tt-query').css('background-color','#fff');
                                
                                // make sure we open what is in the url
                                var col = location.hash.substr(1);
                                if (col) {
                                    console.log(col);
                                    $('#' + col).collapse();
                                    $('#' + col).parents('.panel-collapse.collapse').collapse();
                                }
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
     *                      Index
     */
    static def indexPage() {
        '''
            <div class="container">
                <div class="row">
                    <div class="col-md-6 col-md-offset-3">  
                        <input class="form-control typeahead" type="text" placeholder="Find Extension ...">
                    </div>
                </div>
            </div>
        '''.withSkeleton
    }
    
    /* ------------------------------------------------------
     *                      Navigation
     */
    static def navigation() {
        val classifiMap = extensions.groupBy [ it.classification ]
        val categoriesMap = extensions.groupBy [ it.category.head?: "none" ]
        categoriesMap.removeAll("none")
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
                            «navigationClassifications(classifiMap)»
                            «navigationCategories(categoriesMap)»
                            <li><a href="help.html">Help</a></li>
                        </ul>
                    </nav>
                </div>
            </header>
        '''    
    }
    
    static def navigationClassifications(Multimap<String, ExtensionDescr> classificationMap) {
        classificationMap.asMap.entrySet.sortBy[it.key].map [ entry |
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
    
    //static def navigationItems(String classification, Multimap<String, MethodDoc> mmap) {
    static def navigationItems(String classification, Collection<ExtensionDescr> extsns) {
        val byFirstParam = extsns.groupBy [ it.firstParamType ]
        byFirstParam.keySet.toList.sortBy[it].map [ entry |
            '''
                <li>
                  <a href="classes.html#«classification»«entry»">«entry»</a>
                </li>
            '''
        ].join("\n")
    }
    
    static def navigationCategories(Multimap<String, ExtensionDescr> categoryMap) {
        '''
            <li class="dropdown">
                <a href="#" class="dropdown-toggle" data-toggle="dropdown">Categories<b class="caret"></b></a>
                <ul class="dropdown-menu">
                    «navigationCategoriesItems(categoryMap)»
                </ul>    
            </li>
        '''
    }
    
    static def navigationCategoriesItems(Multimap<String, ExtensionDescr> categoryMap) {
        categoryMap.asMap.entrySet.sortBy[it.key].map [ entry |
            '''
                <li>
                    <a href="categories.html#«entry.key»">«entry.key»</a>
                </li>
            '''
        ].join("\n")
    }


    /* ------------------------------------------------------
     *                      Typeahead Content
     */
    static def typeAheadJson() {
        '''['''
        +
        extensions.map [ ext |
            '''
        {
            value: '«ext.name»',
            tokens: ['«ext.name»'],
            firstParam: '«ext.firstParamType»',
            params: [ «ext.paramTypes.map[''' '«it»' '''].join(",")» ],
            file: '«ext.containingFile»',
            hrefid: '«ext.genId(ext.classification)»'
        }
        '''
        ].join(",\n")
        +
        ''']'''
    }
    

    /* ------------------------------------------------------
     *                      Root Content
     */
    static def contentRoot(Multimap<String, ExtensionDescr> classificationMap) {
        classificationMap.asMap.entrySet.sortBy[it.key].map[ entry |
            '''
                <h1>«entry.key.toFirstUpper»</h1>
            '''
            + 
            // extensions are grouped by their classification
            // now group them by their first parameter
            entry.value.groupBy[it.firstParamType].asMap.entrySet.sortBy[it.key].map [ centry |
                contentRootTable(entry.key, centry)
            ].join("\n")
        ].join("\n")
    }
    
    
    static def contentRootTable(String idPrefix, Entry<String, Collection<ExtensionDescr>> entry) {
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
    
    static def contentRootItems(String idPrefix, Entry<String, Collection<ExtensionDescr>> entry) {
        entry.value.sortBy[it.name].map [ extsn |
            
            val id = extsn.genId(idPrefix)
            
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
     static def contentCategories(Multimap<String, ExtensionDescr> categoryMap) {
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
     
     static def contentCategoriesTable(String idPrefix, Entry<String, Collection<ExtensionDescr>> entry) {
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
    
    
    
    static def contentCategoriesItems(String idPrefix, Entry<String, Collection<ExtensionDescr>> entry) {
        entry.value.sortBy[it.name].map [ extsn |
            
            val id = extsn.genId(idPrefix)
            
            '''
                <tr>
                    <td>
                        <div class="panel-heading k-title no-padding">
                            <a data-toggle="collapse" href="#collapse«id»">
                                 <em>«extsn.firstParamType»</em> «extsn.methodSig»
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

   
     
}
