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
 * @author nbw
 *
 */
public class KIELERLayoutConfigurator {
    public static IKIELERLayoutConfiguration getLayoutConfig(DiagramPresentationElement dpe) {
        String dt = dpe.getDiagramType().getType();
        
        if (dt.equals(DiagramTypeConstants.UML_CLASS_DIAGRAM)) {
            return new KIELERClassDiagrammConfiguration();
        } else if (dt.equals(DiagramTypeConstants.UML_USECASE_DIAGRAM)) {
            return new KIELERUseCaseDiagramConfiguration();
        } else {
            return null;
        }
        
    }
}
