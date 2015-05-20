package de.cau.cs.kieler.magicdraw.layout;

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

import com.nomagic.magicdraw.core.options.AbstractDiagramLayouterOptionsGroup;
import com.nomagic.magicdraw.uml.symbols.DiagramPresentationElement;
import com.nomagic.magicdraw.uml.symbols.layout.DiagramLayouter;
import com.nomagic.magicdraw.uml.symbols.layout.UMLGraph;

import de.cau.cs.kieler.magicdraw.config.IKIELERLayoutConfiguration;
import de.cau.cs.kieler.magicdraw.config.KIELERLayoutConfigurator;
import de.cau.cs.kieler.magicdraw.generator.KIELERMagicDrawReader;

public class KIELERDiagramLayouter implements DiagramLayouter {

    public boolean canLayout(DiagramPresentationElement dpe) {
        IKIELERLayoutConfiguration config = KIELERLayoutConfigurator.getLayoutConfig(dpe);
        return (config != null);
//        return ((config != null) || (config == null));

    }

    public void drawLayoutResults(UMLGraph graph) {
        // TODO Auto-generated method stub
    }

    public String getOptionsID() {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean layout(AbstractDiagramLayouterOptionsGroup layoutOptionGroup,
            DiagramPresentationElement dpe,
            @SuppressWarnings("deprecation") com.nomagic.magicdraw.commands.MacroCommand macro) {
        if (dpe == null) {
            throw new RuntimeException("No DiagramPresentationElement found for layouting!");
        }

        // Generate KGraph using MagicDrawReader
        KIELERMagicDrawReader reader = new KIELERMagicDrawReader();
        reader.generateKGraph(dpe);
        
        // Serialize KGraph for layout through WebService
        String kGraphPre = KIELERMagicDrawReader.serialize(reader.getkGraphRoot());
        
        System.out.println(kGraphPre);
//
//        // Layout KGraph through WebService
//        String layouted =
//                KIELERLayoutKWebSHandler.layout(kGraphPre,
//                        KIELERLayoutConfigurator.getLayoutConfig(dpe));
//
//        System.out.println(layouted);
//        // Generate new KGraph from layouted String representation
//        KNode kGraph = KIELERMagicDrawReader.deserialize(layouted);
//
//        KIELERMagicDrawUpdater.applyLayout(reader.getElementsByID(), kGraph);

        return true;
    }


}
