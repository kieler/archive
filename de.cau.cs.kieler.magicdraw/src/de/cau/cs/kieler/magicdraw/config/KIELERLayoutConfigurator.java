/*
 * KIELER - Kiel Integrated Environment for Layout Eclipse RichClient
 *
 * http://www.informatik.uni-kiel.de/rtsys/kieler/
 * 
 * Copyright 2015 by
 * + Christian-Albrechts-University of Kiel
 *   + Department of Computer Science
 *     + Real-Time and Embedded Systems Group
 * 
 * This code is provided under the terms of the Eclipse Public License (EPL).
 * See the file epl-v10.html for the license text.
 */
package de.cau.cs.kieler.magicdraw.config;

import com.nomagic.magicdraw.uml.DiagramTypeConstants;
import com.nomagic.magicdraw.uml.symbols.DiagramPresentationElement;

/**
 * Factory for different layout configurations, depending on diagram type
 * 
 * @author nbw
 */
public class KIELERLayoutConfigurator {
    public static IKIELERLayoutConfiguration getLayoutConfig(DiagramPresentationElement dpe) {
        String dt = dpe.getDiagramType().getType();

        if (dt.equals(DiagramTypeConstants.UML_CLASS_DIAGRAM)) {
            // First option is class diagram layouting
            return new KIELERClassDiagrammConfiguration();
        } else if (dt.equals(DiagramTypeConstants.UML_STATECHART_DIAGRAM)) {
            // State diagrams are pretty similar to stuff we already have.
            // Let's try that next
            return new KIELERStateChartDiagramConfiguration();
        } else if (dt.equals(DiagramTypeConstants.UML_USECASE_DIAGRAM)) {
            // Layouting Use Case diagrams seems to be not that feasible with our infrastructure.
            // Here is a try, nonetheless
            return new KIELERUseCaseDiagramConfiguration();
        } else {
            return null;
        }

    }
}
