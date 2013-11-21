/**
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
package de.cau.cs.kieler.doclets.extensions;

import org.eclipse.xtend2.lib.StringConcatenation;

/**
 * Generates a help page which for instance contains information on how
 * to annotate extension classes to make them visible within this documentation.
 * 
 * @author uru
 */
@SuppressWarnings("all")
public class ExtensionsHelpPage {
  public CharSequence extensionAnnotations() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("<h1>How to Annotate Extension Classes</h1>");
    _builder.newLine();
    _builder.newLine();
    _builder.append("<h2>Class</h2>");
    _builder.newLine();
    _builder.append("<p>");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("To make a class available to this documentation add a <code>@containsExtensions</code>");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("tag to the class\' javadoc.");
    _builder.newLine();
    _builder.append("</p>");
    _builder.newLine();
    _builder.newLine();
    StringConcatenation _builder_1 = new StringConcatenation();
    _builder_1.append("/**");
    _builder_1.newLine();
    _builder_1.append(" ");
    _builder_1.append("* @author me");
    _builder_1.newLine();
    _builder_1.append(" ");
    _builder_1.append("* @containsExtensions");
    _builder_1.newLine();
    _builder_1.append(" ");
    _builder_1.append("*/");
    _builder_1.newLine();
    _builder_1.append("class MyExtensionsClass {");
    _builder_1.newLine();
    _builder_1.append("    ");
    _builder_1.append("[...]");
    _builder_1.newLine();
    _builder_1.append("}");
    _builder_1.newLine();
    CharSequence _example = this.example(_builder_1.toString());
    _builder.append(_example, "");
    _builder.newLineIfNotEmpty();
    _builder.newLine();
    _builder.append("<h3>Classification</h3>");
    _builder.newLine();
    _builder.append("<p>");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("It is possible to group different extension classes together, for example, all of Ptolemy\'s");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("extensions belong to the same logic block. To create such a classification just add an");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("identifier to the <code>@containsExtensions</code> tag.");
    _builder.newLine();
    _builder.append("</p>");
    _builder.newLine();
    _builder.newLine();
    StringConcatenation _builder_2 = new StringConcatenation();
    _builder_2.append("/**");
    _builder_2.newLine();
    _builder_2.append(" ");
    _builder_2.append("* @containsExtensions ptolemy");
    _builder_2.newLine();
    _builder_2.append(" ");
    _builder_2.append("*/");
    _builder_2.newLine();
    _builder_2.append("class AnnotationExtensions {");
    _builder_2.newLine();
    _builder_2.append("    ");
    _builder_2.append("[...]");
    _builder_2.newLine();
    _builder_2.append("}");
    _builder_2.newLine();
    CharSequence _example_1 = this.example(_builder_2.toString());
    _builder.append(_example_1, "");
    _builder.newLineIfNotEmpty();
    _builder.newLine();
    _builder.append("<h2>Method</h2>");
    _builder.newLine();
    _builder.append("<p>");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("All methods of an extension class are grouped by the class of their first parameter. For each ");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("class a section is created within the documentation that lists all available extensions for ");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("thiss type. The documentation text equals the javadoc text of the respective method.");
    _builder.newLine();
    _builder.append("</p>");
    _builder.newLine();
    _builder.newLine();
    _builder.append("<div class=\"alert alert-info\">");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("It is possible to use arbitrary html within the documentation text.");
    _builder.newLine();
    _builder.append("</div>");
    _builder.newLine();
    _builder.newLine();
    StringConcatenation _builder_3 = new StringConcatenation();
    _builder_3.append("/** ");
    _builder_3.newLine();
    _builder_3.append(" ");
    _builder_3.append("* Retrieves the rendering element from the <em>library</em> that has been identified with");
    _builder_3.newLine();
    _builder_3.append(" ");
    _builder_3.append("* the <code>id</code> string.  ");
    _builder_3.newLine();
    _builder_3.append(" ");
    _builder_3.append("*/");
    _builder_3.newLine();
    _builder_3.append("def KRenderingRef getFromLibrary(KRenderingLibrary library, String id) {");
    _builder_3.newLine();
    _builder_3.append("    ");
    _builder_3.append("[...]");
    _builder_3.newLine();
    _builder_3.append("}");
    _builder_3.newLine();
    CharSequence _example_2 = this.example(_builder_3.toString());
    _builder.append(_example_2, "");
    _builder.newLineIfNotEmpty();
    _builder.newLine();
    _builder.append("<h3>Example</h3>");
    _builder.newLine();
    _builder.append("<p>");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("It is possible to attach a small example to an extension\'s documentation that illustrates how ");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("the extension is supposed to be used. For this, add a <code>@example</code> tag to the");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("javadoc, followed by the preformatted example.");
    _builder.newLine();
    _builder.append("</p>");
    _builder.newLine();
    _builder.newLine();
    _builder.append("<div class=\"alert alert-info\">");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("You can surround the example with <code>&lt;pre&gt;</code> so that automatic ");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("code formatting does not break the example.");
    _builder.newLine();
    _builder.append("</div>");
    _builder.newLine();
    _builder.newLine();
    StringConcatenation _builder_4 = new StringConcatenation();
    _builder_4.append("/**");
    _builder_4.newLine();
    _builder_4.append(" ");
    _builder_4.append("* Convenient creation of color objects. Allows several names (red, blue, black, etc) ");
    _builder_4.newLine();
    _builder_4.append(" ");
    _builder_4.append("* and hex strings (#00ff00). ");
    _builder_4.newLine();
    _builder_4.append(" ");
    _builder_4.append("* ");
    _builder_4.newLine();
    _builder_4.append(" ");
    _builder_4.append("* @example");
    _builder_4.newLine();
    _builder_4.append(" ");
    _builder_4.append("* rectangle.background = \"black\".color");
    _builder_4.newLine();
    _builder_4.append(" ");
    _builder_4.append("* rectangle.foreground = \"#00ff00\".color");
    _builder_4.newLine();
    _builder_4.append(" ");
    _builder_4.append("*/");
    _builder_4.newLine();
    _builder_4.append("def KColor getColor(String name) {");
    _builder_4.newLine();
    _builder_4.append("    ");
    _builder_4.append("[...]");
    _builder_4.newLine();
    _builder_4.append("}");
    _builder_4.newLine();
    CharSequence _example_3 = this.example(_builder_4.toString());
    _builder.append(_example_3, "");
    _builder.newLineIfNotEmpty();
    _builder.newLine();
    _builder.append("<h3>Category</h3>");
    _builder.newLine();
    _builder.append("<p>");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("By default the extensions are grouped by the type of the class they are applied to. However,");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("sometimes it is useful to group them by their purpose, for instance <em>styling</em> or ");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("<em>composing</em> extensions. For this add a <code>@extensionCategory</code> tag to the ");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("javadoc followed by a category identifier. At the moment only one category is possible.");
    _builder.newLine();
    _builder.append("</p>");
    _builder.newLine();
    _builder.newLine();
    StringConcatenation _builder_5 = new StringConcatenation();
    _builder_5.append("/*");
    _builder_5.newLine();
    _builder_5.append(" ");
    _builder_5.append("* .. a styling method ..");
    _builder_5.newLine();
    _builder_5.append(" ");
    _builder_5.append("* @extensionCategory style");
    _builder_5.newLine();
    _builder_5.append(" ");
    _builder_5.append("*/");
    _builder_5.newLine();
    _builder_5.append("def KRendering getHairCut(KRendering ren) {");
    _builder_5.newLine();
    _builder_5.append("    ");
    _builder_5.append("[...]");
    _builder_5.newLine();
    _builder_5.append("}");
    _builder_5.newLine();
    _builder_5.append(" ");
    _builder_5.newLine();
    _builder_5.append(" ");
    _builder_5.append("/*");
    _builder_5.newLine();
    _builder_5.append(" ");
    _builder_5.append("* .. a composing method ..");
    _builder_5.newLine();
    _builder_5.append(" ");
    _builder_5.append("* @extensionCategory composing");
    _builder_5.newLine();
    _builder_5.append(" ");
    _builder_5.append("*/");
    _builder_5.newLine();
    _builder_5.append("def KContainerRendering getNewHouse(KContainerRendering ren) {");
    _builder_5.newLine();
    _builder_5.append("    ");
    _builder_5.append("[...]");
    _builder_5.newLine();
    _builder_5.append("}");
    _builder_5.newLine();
    CharSequence _example_4 = this.example(_builder_5.toString());
    _builder.append(_example_4, "");
    _builder.newLineIfNotEmpty();
    _builder.newLine();
    _builder.newLine();
    return _builder;
  }
  
  public CharSequence example(final String code) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("<div>");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("<div class=\"text-muted\">Example</div>");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("<pre class=\"highlight linenums lang-xtend\">");
    _builder.newLine();
    _builder.append("        ");
    _builder.append(code, "        ");
    _builder.newLineIfNotEmpty();
    _builder.append("    ");
    _builder.append("</pre>");
    _builder.newLine();
    _builder.append("</div>");
    _builder.newLine();
    return _builder;
  }
}
