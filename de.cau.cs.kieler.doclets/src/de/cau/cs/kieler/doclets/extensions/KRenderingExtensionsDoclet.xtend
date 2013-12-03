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
import com.sun.javadoc.RootDoc
import de.cau.cs.kieler.doclets.Util
import java.io.File
import java.util.Collection
import java.util.List
import org.eclipse.xtext.xbase.lib.Functions.Function1
import com.sun.javadoc.LanguageVersion

/**
 * Doclet to create an overview page for all available KLighD extensions.
 * 
 * @author uru
 */
class KRenderingExtensionsDoclet {

    // the used javadoc tags
    public static val TAG_EXTENSION = "@containsExtensions"
    public static val TAG_EXAMPLE = "@example"
    public static val TAG_EXTENSION_CATEGORY = "@extensionCategory"
    
    public static val TAG_LAYOUT_OPTIONS = "@containsLayoutOptions"

    // default category
    private static val DEFAULT_CATEGORY = "default" 
    
    // where to copy all the files to
    private static val DOC_ROOT = "./extensions/"

    /** Holds all extension methods. */
    private static List<ExtensionDescr> extensions = Lists.newLinkedList
    
    /** Holds all layout options */
    private static List<LayoutOptionDescr> layoutOptions = Lists.newLinkedList

    // some extensions for convenience
    extension ExtensionsContent = new ExtensionsContent
    extension ExtensionsTypeaheadJson = new ExtensionsTypeaheadJson

    def <T,S> Multimap<S, T> groupBy(Iterable<T> iterable, Function1<T, S> fun) {
        
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
        new KRenderingExtensionsDoclet().startInt(rootDoc)
    }
    
    static def LanguageVersion languageVersion() {
        return LanguageVersion.JAVA_1_5
    }

    
    def boolean startInt(RootDoc rootDoc) {

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
                // assemble a descriptor object
                val descr = new ExtensionDescr(method, classifiString, clazz.name)
                extensions += descr             
            ]
        ]
        
        // collect the layout options
        rootDoc.classes.filter[it.tags(TAG_LAYOUT_OPTIONS).length > 0].forEach [ clazz |
            
            val category = clazz.tags(TAG_LAYOUT_OPTIONS).head.text?:"general"
            
            clazz.fields.filter[ it.static && it.final ].forEach [ fieldDoc |
                val opt = new LayoutOptionDescr(clazz, category, fieldDoc)
                layoutOptions += opt
            ]
        ]
        
        println("No Layout options : " + layoutOptions.size)
        
        // make sure the root folder exists
        val docRoot = new File(DOC_ROOT)
        if(!docRoot.exists) {
            docRoot.mkdirs
        }
        
        // copy resourcces
        val util = new Util
        
        // css 
        val css = new File(DOC_ROOT, "css")
        css.mkdirs
        util.copyResource("bootstrap-3.0.2.min.css", css)
        util.copyResource("typeahead-bootstrap.css", css)
        util.copyResource("extensions.css", css)
        util.copyResource("prettify.min.css", css)
        
        // js
        val js = new File(DOC_ROOT, "js")
        js.mkdirs
        util.copyResource("bootstrap-3.0.2.min.js", js)
        util.copyResource("jquery-1.10.2.min.js", js)
        util.copyResource("typeahead.min.js", js)
        util.copyResource("prettify.min.js", js)
        util.copyResource("xtend-lang.js", js)
        util.copyResource("hogan-2.0.0.js", js)
        util.copyResource("extensions.js", js)
        
        // generate the root html page 
        Files.write(indexPage(), new File(DOC_ROOT + "index.html") , Charsets.UTF_8)
        Files.write(genRootPage(), new File(DOC_ROOT + "classes.html") , Charsets.UTF_8)
        // generate the categories page 
        Files.write(genCategoriesPage(), new File(DOC_ROOT + "categories.html") , Charsets.UTF_8)
        // write layout options page
        Files.write(genLayoutOptionsPage(), new File(DOC_ROOT + "layoutopts.html") , Charsets.UTF_8)
        
        // json data
        Files.write(typeAheadJson(extensions), new File(DOC_ROOT + "extensions.json") , Charsets.UTF_8)
        Files.write(typeAheadJsonLayoutOpts(layoutOptions), new File(DOC_ROOT + "layoutopts.json") , Charsets.UTF_8)
        
        // generate a help page
        Files.write(new ExtensionsHelpPage().extensionAnnotations.withSkeleton, 
            new File(DOC_ROOT + "help.html") , Charsets.UTF_8
        )

        return true
    }
    
    def genRootPage() {
        // group extensions by classification
        val classifiMap = extensions.groupBy [ it.classification ]
        
        contentRoot(classifiMap).withSkeleton
    }
    
    def genCategoriesPage() {
       // group by category
       val categoriesMap = extensions.groupBy [ it.categories.head?: "none" ]

       contentCategories(categoriesMap).withSkeleton
    }
    
    def genLayoutOptionsPage() {
        val layoutOptsMap = layoutOptions.groupBy [ it.category ]
        
        contentLayoutOptions(layoutOptsMap).withSkeleton
    }
    
    /* ------------------------------------------------------
     *                      HTML Skeleton
     */
     
     def withSkeleton(CharSequence content) {
          '''
            <!DOCTYPE HTML>
            <html>
                <head>
                    <title>KIELER KRendering Extensions</title>
                    <meta charset="UTF-8">
                    <link href="css/bootstrap-3.0.2.min.css" rel="stylesheet" type="text/css">
                    <link href="css/typeahead-bootstrap.css" rel="stylesheet" type="text/css">
                    <link href="css/prettify.min.css" rel="stylesheet" type="text/css">
                    <link href="css/extensions.css" rel="stylesheet" type="text/css">
                    
                    <script src="js/jquery-1.10.2.min.js" type="text/javascript"></script>
                    <script src="js/bootstrap-3.0.2.min.js" type="text/javascript"></script>
                    <script src="js/typeahead.min.js" type="text/javascript"></script>
                    <script src="js/prettify.min.js" type="text/javascript"></script>
                    <script src="js/xtend-lang.js" type="text/javascript"></script>
                    <script src="js/hogan-2.0.0.js" type="text/javascript"></script>
                    <script src="js/extensions.js" type="text/javascript"></script>
                </head>
                <body>
                    <!-- navigation -->
                    «navigation»
                    <!-- content -->
                    <div class="content">
                        <div class="container">
                            <div class="row">
                                <div class="col-md-12">
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
     def indexPage() {
        '''
            <div class="container">
                <div class="row">
                    <h1 class="h1-index">KIELER KRendering Extensions</h1>
                    <div class="col-md-6 col-md-offset-3">  
                        <input class="form-control typeahead" type="text" 
                            placeholder="Find Extension ..." role="search">
                    </div>
                </div>
            </div>
        '''.withSkeleton
    }
    
    /* ------------------------------------------------------
     *                      Navigation
     */
     def navigation() {
        val classifiMap = extensions.groupBy [ it.classification ]
        val categoriesMap = extensions.groupBy [ it.categories.head?: "none" ]
        categoriesMap.removeAll("none")
        '''
            <header class="navbar navbar-inverse navbar-fixed-top" role="navigation">
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
                            <li><a href="layoutopts.html">Layout Options</a></li>
                            <li><a href="help.html">Help</a></li>
                        </ul>
                        <ul class="nav navbar-nav navbar-right navbar-form">
                            <input class="navbar-right form-control typeahead-bar" type="text" 
                                placeholder="Find Extension ..." role="search">
                        </ul>
                    </nav>
                </div>
            </header>
        '''    
    }
    
    def navigationClassifications(Multimap<String, ExtensionDescr> classificationMap) {
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
    
    def navigationItems(String classification, Collection<ExtensionDescr> extsns) {
        val byFirstParam = extsns.groupBy [ it.firstParamType ]
        byFirstParam.keySet.toList.sortBy[it].map [ entry |
            '''
                <li>
                  <a href="classes.html#collapse«classification»«entry»">«entry»</a>
                </li>
            '''
        ].join("\n")
    }
    
    def navigationCategories(Multimap<String, ExtensionDescr> categoryMap) {
        '''
            <li class="dropdown">
                <a href="#" class="dropdown-toggle" data-toggle="dropdown">Categories<b class="caret"></b></a>
                <ul class="dropdown-menu">
                    «navigationCategoriesItems(categoryMap)»
                </ul>    
            </li>
        '''
    }
    
    def navigationCategoriesItems(Multimap<String, ExtensionDescr> categoryMap) {
        categoryMap.asMap.entrySet.sortBy[it.key].map [ entry |
            '''
                <li>
                    <a href="categories.html#collapse«entry.key»">«entry.key»</a>
                </li>
            '''
        ].join("\n")
    }
     
}
