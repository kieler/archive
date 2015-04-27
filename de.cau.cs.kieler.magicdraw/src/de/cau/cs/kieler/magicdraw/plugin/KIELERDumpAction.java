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
package de.cau.cs.kieler.magicdraw.plugin;

import java.awt.event.ActionEvent;

import javax.swing.KeyStroke;

import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.ui.actions.DefaultDiagramAction;
import com.nomagic.magicdraw.uml.symbols.DiagramPresentationElement;
import com.nomagic.magicdraw.uml.symbols.PresentationElement;
import com.nomagic.magicdraw.uml.symbols.paths.PathElement;
import com.nomagic.magicdraw.uml.symbols.shapes.ShapeElement;
import com.nomagic.magicdraw.uml.symbols.shapes.TreeView;

/**
 * Simple processor that dumps relevant layout information to the console output. Used to gain a
 * better understanding of the internal structure of Presentation Elements and Paths in MagicDraw.
 * 
 * @author nbw
 * 
 */
public class KIELERDumpAction extends DefaultDiagramAction {

    private static final long serialVersionUID = -638897077658893781L;

    /**
     * Creates a new KielerDumpAction.
     * 
     * @param actionID
     *            Internal ActionID to identify the action
     * @param actionName
     *            Action Name in menu
     * @param stroke
     *            Keystroke to activate action
     * @param group
     *            Activation group of action
     */
    public KIELERDumpAction(String actionID, String actionName, KeyStroke stroke, String group) {
        super(actionID, actionName, stroke, group);
    }

    /**
     * Triggers the output of the diagram data
     * 
     * @param e
     *            {@link ActionEvent} that caused the action
     */
    public void actionPerformed(ActionEvent e) {
        // Grab the currently active diagram and open it to make it available to the layouter
        DiagramPresentationElement dia = Application.getInstance().getProject().getActiveDiagram();
        dia.open();
        System.out.println("<<<---+++ KIELER Diagram Information - Begin +++--->>>");
        writeData(dia, 1);
        System.out.println("<<<---+++ KIELER Diagram Information - End +++--->>>");

    }

    /**
     * Recursively writes the diagram layout information to the standard console. The recursion
     * depth can be adjusted to print only high-level components.
     * 
     * @param root
     *            The current main element to print and check for child elements
     * @param depth
     */
    private void writeData(PresentationElement root, int depth) {
        StringBuilder result = new StringBuilder();
        result.append(root.getHumanName());
        if (root instanceof ShapeElement) {
            result.append(" @ ");
            result.append(root.getBounds().toString());
            if (root instanceof TreeView) {
                TreeView tree = (TreeView) root;
                result.append(" bundeling ");
                for (PathElement path : tree.getConnectedPathElements()) {
                    result.append(path.getHumanName()).append(", ");
                }
            }
        } else if (root instanceof PathElement) {
            PathElement rootPath = (PathElement) root;
            result.append(" between ");
            result.append(rootPath.getSupplier().getHumanName());
            result.append(" and ");
            result.append(rootPath.getClient().getHumanName());
        }

        System.out.println(result.toString());

        if (root.getPresentationElements().size() > 0 && depth > 0) {
            for (PresentationElement element : root.getPresentationElements()) {
                writeData(element, depth - 1);
            }
        }
    }

}
