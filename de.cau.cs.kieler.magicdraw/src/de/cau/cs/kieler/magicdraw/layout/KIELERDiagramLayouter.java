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

import de.cau.cs.kieler.core.kgraph.KNode;
import de.cau.cs.kieler.magicdraw.config.IKIELERLayoutConfiguration;
import de.cau.cs.kieler.magicdraw.config.KIELERLayoutConfigurator;
import de.cau.cs.kieler.magicdraw.generator.KIELERMagicDrawReader;

/**
 * Main diagram layouter. The general approach is first collecting the MagicDraw elements into flat
 * lists and then generating a new KGraph from these lists. The mapping between the MagicDraw
 * components and the elements of the KGraph is stored and later used to apply the KIELER layout to
 * the MagicDraw diagramm. The layout is then calculated by the KIELER web service (KWebS) which can
 * either run on the local machine or 
 * 
 * @author nbw
 *
 */
public class KIELERDiagramLayouter implements DiagramLayouter {

    public boolean canLayout(DiagramPresentationElement dpe) {
        IKIELERLayoutConfiguration config = KIELERLayoutConfigurator.getLayoutConfig(dpe);
        return (config != null);
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
        reader.transformToKGraph(dpe);

        // Serialize KGraph for layout through WebService
        String kGraphPre = KIELERMagicDrawReader.serialize(reader.getkGraphRoot());

        // Layout KGraph through WebService
        String layouted =
                KIELERLayoutKWebSHandler.layout(kGraphPre,
                        KIELERLayoutConfigurator.getLayoutConfig(dpe));

        // Generate new KGraph from layouted String representation
        KNode kGraph = KIELERMagicDrawReader.deserialize(layouted);
        
        // Update positions with data from new KGraph
//        KIELERMagicDrawUpdater.applyLayout(reader.getElementsByID(), kGraph);

        return true;
    }

}
