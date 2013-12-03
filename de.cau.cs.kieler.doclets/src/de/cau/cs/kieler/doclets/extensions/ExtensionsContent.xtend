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

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import java.util.Collection
import org.eclipse.xtext.xbase.lib.Functions.Function1
import java.util.Map.Entry

/**
 * @author uru
 */
class ExtensionsContent {
    
    
   static def <T,S> Multimap<S, T> groupBy(Iterable<T> iterable, Function1<T, S> fun) {
        
        val Multimap<S, T> map = HashMultimap.create
        
        iterable.forEach [ e |
            val key = fun.apply(e)
            map.put(key, e)
        ]
        
        return map
    }
    
    /* ------------------------------------------------------
     *                      Root Content
     */
     def String contentRoot(Multimap<String, ExtensionDescr> classificationMap) {
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
    
    
    def contentRootTable(String idPrefix, Entry<String, Collection<ExtensionDescr>> entry) {
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
    
    def contentRootItems(String idPrefix, Entry<String, Collection<ExtensionDescr>> entry) {
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
     def contentCategories(Multimap<String, ExtensionDescr> categoryMap) {
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
     
     def contentCategoriesTable(String idPrefix, Entry<String, Collection<ExtensionDescr>> entry) {
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
    
    def contentCategoriesItems(String idPrefix, Entry<String, Collection<ExtensionDescr>> entry) {
        entry.value.sortBy[it.name].map [ extsn |
            
            val id = extsn.genId(idPrefix)
            
            '''
                <tr>
                    <td>
                        <div class="panel-heading k-title no-padding">
                            <a data-toggle="collapse" href="#collapse«id»">
                                 «extsn.firstParamType».«extsn.methodSig»
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
     *                      Layout Options
     */
     def String contentLayoutOptions(Multimap<String, LayoutOptionDescr> layoutOptions) {
         layoutOptions.asMap.entrySet.map [ entry |
             '''
                <h1>«entry.key»</h1>
                «entry.value.sortBy[it.name].map[it.layoutOptionsItem].join("\n")»
             '''
         ].join("\n")
     }
    
    
    def layoutOptionsItem(LayoutOptionDescr doc) {
        val id = doc.getHrefId
        '''
             <div id="«doc.name»" class="panel-group">
               <a id="«id»"></a>
               <div class="panel panel-default">
                 <div class="panel-heading">
                   <h4 class="panel-title">
                     <a data-toggle="collapse" href="#collapse«id»">
                       «doc.title»
                     </a>
                   </h4>
                 </div>
                 <div id="collapse«id»" class="panel-collapse collapse">
                    <div class="panel-body k-panel">
                        «doc.description»
                    </div>
                 </div>
               </div>
             </div>
        '''
    }
}