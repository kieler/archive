/*
 * KIELER - Kiel Integrated Environment for Layout Eclipse RichClient
 *
 * http://www.informatik.uni-kiel.de/rtsys/kieler/
 * 
 * Copyright 2011 by
 * + Christian-Albrechts-University of Kiel
 *   + Department of Computer Science
 *     + Real-Time and Embedded Systems Group
 * 
 * This code is provided under the terms of the Eclipse Public License (EPL).
 * See the file epl-v10.html for the license text.
 */
package de.cau.cs.kieler.kiml.render;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import de.cau.cs.kieler.kiml.util.KimlUtil;
import java.util.List;

import de.cau.cs.kieler.core.kgraph.KEdge;
import de.cau.cs.kieler.core.kgraph.KLabel;
import de.cau.cs.kieler.core.kgraph.KNode;
import de.cau.cs.kieler.core.kgraph.KPort;
import de.cau.cs.kieler.core.math.KVector;
import de.cau.cs.kieler.kiml.klayoutdata.KEdgeLayout;
import de.cau.cs.kieler.kiml.klayoutdata.KInsets;
import de.cau.cs.kieler.kiml.klayoutdata.KPoint;
import de.cau.cs.kieler.kiml.klayoutdata.KShapeLayout;

/**
 * A rendering tool for painting KIML layout graphs.
 * 
 * @author msp
 */
public class LayoutGraphRenderer {

    /** length of edge arrows. */
    private static final float ARROW_LENGTH = 7.0f;
    /** width of edge arrows. */
    private static final float ARROW_WIDTH = 5.0f;    

    /** background color. */
    private static final Color BACKGROUND_COLOR = new Color(255, 255, 255);
    /** border color for nodes. */
    private static final Color NODE_BORDER_COLOR = new Color(10, 57, 14, 100);
    /** fill color for nodes. */
    private static final Color NODE_FILL_COLOR = new Color(87, 197, 133, 100);
    /** font used for node labels. */
    private static final Font NODE_FONT = new Font("SansSerif", Font.BOLD, 10);
    /** background color for labels. */
    private static final Color LABEL_BACK_COLOR = new Color(243, 255, 199);
    /** color used for ports. */
    private static final Color PORT_COLOR = new Color(4, 17, 69, 230);
    /** font used for port labels. */
    private static final Font PORT_FONT = new Font("SansSerif", Font.PLAIN, 6);
    /** color used for edges. */
    private static final Color EDGE_COLOR = new Color(49, 77, 114, 230);
    /** font used for edge labels. */
    private static final Font EDGE_FONT  = new Font("SansSerif", Font.PLAIN, 8);

    /**
     * Create an image for the given layout graph.
     * 
     * @param layoutGraph a layout graph
     * @return an image of the layout graph
     */
    public RenderedImage createImage(final KNode layoutGraph) {
        Rectangle rect = getRect(layoutGraph.getData(KShapeLayout.class), new KVector());
        BufferedImage image = new BufferedImage(rect.x + rect.width + 2, rect.y + rect.height + 2,
                BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D graphics = image.createGraphics();
        graphics.setBackground(BACKGROUND_COLOR);
        graphics.clearRect(rect.x, rect.y, rect.width, rect.height);
        
        // paint the top layout node with its children
        KVector offset = new KVector(rect.x, rect.y);
        paintLayoutNode(layoutGraph, graphics, offset);
        
        return image;
    }
    
    /**
     * Create a rectangle for the given shape layout.
     * 
     * @param shapeLayout a shape layout
     * @param offset offset to add to shape coordinates
     * @return a new rectangle
     */
    private Rectangle getRect(final KShapeLayout shapeLayout, final KVector offset) {
        if (shapeLayout != null) {
            return new Rectangle(Math.round(shapeLayout.getXpos() + (float) offset.x),
                    Math.round(shapeLayout.getYpos() + (float) offset.y),
                    Math.round(shapeLayout.getWidth()),
                    Math.round(shapeLayout.getHeight()));
        }
        return null;
    }

    /**
     * Paints a layout node.
     * 
     * @param layoutNode layout node to paint
     * @param graphics the graphics context used to paint
     * @param offset offset to be added to relative coordinates
     */
    private void paintLayoutNode(final KNode layoutNode, final Graphics2D graphics,
            final KVector offset) {
        // paint ports of the layout node
        graphics.setFont(PORT_FONT);
        graphics.setColor(PORT_COLOR);
        for (KPort port : layoutNode.getPorts()) {
            Rectangle rect = getRect(port.getData(KShapeLayout.class), offset);
            graphics.fill(rect);
            if (port.getLabel() != null && port.getLabel().getText() != null) {
                KVector portOffset = new KVector(rect.x, rect.y);
                rect = getRect(port.getLabel().getData(KShapeLayout.class), portOffset);
                graphics.setColor(LABEL_BACK_COLOR);
                graphics.fill(rect);
                graphics.setColor(PORT_COLOR);
                graphics.drawString(port.getLabel().getText(), rect.x, rect.y);
            }
        }

        // add insets to offset value
        KInsets insets = layoutNode.getData(KShapeLayout.class).getInsets();
        KVector subOffset = new KVector(offset);
        if (insets != null) {
            subOffset.translate(insets.getLeft(), insets.getTop());
        }

        // paint sub layout nodes
        for (KNode child : layoutNode.getChildren()) {
            Rectangle rect = getRect(child.getData(KShapeLayout.class), subOffset);
            graphics.setColor(NODE_FILL_COLOR);
            graphics.fill(rect);
            graphics.setColor(NODE_BORDER_COLOR);
            graphics.draw(rect);
            KVector childOffset = new KVector(rect.x, rect.y);
            paintLayoutNode(child, graphics, childOffset);
            if (child.getLabel() != null && child.getLabel().getText() != null) {
                graphics.setFont(NODE_FONT);
                rect = getRect(child.getLabel().getData(KShapeLayout.class), childOffset);
                graphics.setColor(LABEL_BACK_COLOR);
                graphics.fill(rect);
                graphics.setColor(NODE_BORDER_COLOR);
                graphics.drawString(child.getLabel().getText(), rect.x, rect.y);
            }

            // paint edges, deactivate label painting for incoming edges
            graphics.setColor(EDGE_COLOR);
            graphics.setFont(EDGE_FONT);
            for (KEdge edge : child.getOutgoingEdges()) {
                paintEdge(edge, graphics);
            }
        }
    }

    /**
     * Paints an edge.
     * 
     * @param edge edge to paint
     * @param graphics the graphics context used to paint
     */
    private void paintEdge(final KEdge edge, final Graphics2D graphics) {
        // calculate an offset for edge coordinates
        KVector offset = new KVector();
        KNode parent = edge.getSource();
        if (!KimlUtil.isDescendant(edge.getTarget(), parent)) {
            parent = parent.getParent();
        }
        KimlUtil.toAbsolute(offset, parent);
        
        KEdgeLayout edgeLayout = edge.getData(KEdgeLayout.class);
        KPoint sourcePoint = edgeLayout.getSourcePoint();
        KPoint targetPoint = edgeLayout.getTargetPoint();
        List<KPoint> bendPoints = edgeLayout.getBendPoints();
        KPoint lastPoint = sourcePoint;
        for (KPoint point : bendPoints) {
            graphics.drawLine((int) Math.round(lastPoint.getX() + offset.x),
                    (int) Math.round(lastPoint.getY() + offset.y),
                    (int) Math.round(point.getX() + offset.x),
                    (int) Math.round(point.getY() + offset.y));
            lastPoint = point;
        }
        graphics.drawLine((int) Math.round(lastPoint.getX() + offset.x),
                (int) Math.round(lastPoint.getY() + offset.y),
                (int) Math.round(targetPoint.getX() + offset.x),
                (int) Math.round(targetPoint.getY() + offset.y));
        // draw an arrow at the last segment of the connection
        Polygon arrowPoly = makeArrow(lastPoint, targetPoint, offset);
        if (arrowPoly != null) {
            graphics.fillPolygon(arrowPoly);
        }

        for (KLabel edgeLabel : edge.getLabels()) {
            if (edgeLabel.getText() != null) {
                Rectangle rect = getRect(edgeLabel.getData(KShapeLayout.class), offset);
                graphics.setColor(LABEL_BACK_COLOR);
                graphics.fill(rect);
                graphics.setColor(EDGE_COLOR);
                graphics.drawString(edgeLabel.getText(), rect.x, rect.y);
            }
        }
    }
    
    // CHECKSTYLEOFF MagicNumber

    /**
     * Constructs a polygon that forms an arrow.
     * 
     * @param point1 source point
     * @param point2 target point
     * @param offset offset value to be added to coordinates
     * @return the arrow polygon, or null if the given source and target points are equal
     */
    private Polygon makeArrow(final KPoint point1, final KPoint point2, final KVector offset) {
        if (!(point1.getX() == point2.getX() && point1.getY() == point2.getY())) {
            int[] arrowx = new int[3];
            int[] arrowy = new int[3];
            arrowx[0] = (int) Math.round(point2.getX() + offset.x);
            arrowy[0] = (int) Math.round(point2.getY() + offset.y);

            float vectX = point1.getX() - point2.getX();
            float vectY = point1.getY() - point2.getY();
            float length = (float) Math.sqrt(vectX * vectX + vectY * vectY);
            float normX = vectX / length;
            float normY = vectY / length;
            float neckX = point2.getX() + ARROW_LENGTH * normX;
            float neckY = point2.getY() + ARROW_LENGTH * normY;
            float orthX = normY * ARROW_WIDTH / 2;
            float orthY = -normX * ARROW_WIDTH / 2;

            arrowx[1] = (int) Math.round(neckX + orthX + offset.x);
            arrowy[1] = (int) Math.round(neckY + orthY + offset.y);
            arrowx[2] = (int) Math.round(neckX - orthX + offset.x);
            arrowy[2] = (int) Math.round(neckY - orthY + offset.y);
            return new Polygon(arrowx, arrowy, 3);
        } else {
            return null;
        }
    }

}
