package de.cau.cs.kieler.magicdraw.plugin;

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

import java.awt.event.ActionEvent;

import javax.swing.KeyStroke;

import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.ui.actions.DefaultDiagramAction;
import com.nomagic.magicdraw.uml.symbols.DiagramPresentationElement;

import de.cau.cs.kieler.magicdraw.adapter.KielerDiagramLayouter;

/**
 * Action to trigger layout process on current diagram.
 * Activated by menu entry
 *  
 * @author nbw
 */
public class KIELERLayoutDiagramAction extends DefaultDiagramAction {

    private static final long serialVersionUID = -5692250542012149294L;

    /**
     * Creates a new KielerConnectorDiagramAction.
     *  
     * @param actionID Internal ActionID to identify the action
     * @param actionName Action Name in menu 
     * @param stroke Keystroke to activate action
     * @param group Activation group of action
     */
    public KIELERLayoutDiagramAction(String actionID, String actionName, KeyStroke stroke,
            String group) {
        super(actionID, actionName, stroke, group);
    }

    /**
     * Executes the layout on the active diagram.
     * 
     * @param e {@link ActionEvent} that caused the action
     */
    public void actionPerformed(ActionEvent e) {
        // Grab the currently active diagram and open it to make it available to the layouter
        DiagramPresentationElement dia = Application.getInstance().getProject().getActiveDiagram();
        dia.open();
        // Try to apply layout on the current diagram 
        dia.layout(true, new KielerDiagramLayouter());
    }

}
