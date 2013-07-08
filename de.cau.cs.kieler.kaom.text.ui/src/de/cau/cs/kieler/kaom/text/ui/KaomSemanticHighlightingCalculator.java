/*
 * KIELER - Kiel Integrated Environment for Layout Eclipse RichClient
 *
 * http://www.informatik.uni-kiel.de/rtsys/kieler/
 * 
 * Copyright 2010 by
 * + Christian-Albrechts-University of Kiel
 *   + Department of Computer Science
 *     + Real-Time and Embedded Systems Group
 * 
 * This code is provided under the terms of the Eclipse Public License (EPL).
 * See the file epl-v10.html for the license text.
 */
package de.cau.cs.kieler.kaom.text.ui;

import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.syntaxcoloring.IHighlightedPositionAcceptor;
import org.eclipse.xtext.ui.editor.syntaxcoloring.ISemanticHighlightingCalculator;

import de.cau.cs.kieler.core.annotations.text.ui.AnnotationsSemanticHighlightingCalculator;

/**
 * Custom {@link ISemanticHighlightingCalculator} contributing to the KAOM editor.
 * The required method {@link ISemanticHighlightingCalculator#provideHighlightingFor(XtextResource, IHighlightedPositionAcceptor)}
 * will be provided by the {@link AnnotationsSemanticHighlightingCalculator} for efficiency purposes
 * and delegates to {@link AnnotationsSemanticHighlightingCalculator#provideHighlightingFor(AbstractNode, IHighlightedPositionAcceptor)}
 * that can be specialized in order to added KAOM specific highlighting configurations. 
 * 
 * @author chsch
 * @kieler.ignore (excluded from review process)
 */
public class KaomSemanticHighlightingCalculator extends AnnotationsSemanticHighlightingCalculator {

//    @Inject
//    private KitsGrammarAccess g;
//
//    public void provideHighlightingFor(AbstractNode node, IHighlightedPositionAcceptor acceptor) {
//       // EObject grammarElement = node.getGrammarElement();
//       super.provideHighlightingFor(node, acceptor);
//    }
    
}
