/*
 * KIELER - Kiel Integrated Environment for Layout Eclipse RichClient
 *
 * http://www.informatik.uni-kiel.de/rtsys/kieler/
 * 
 * Copyright 2009 by
 * + Christian-Albrechts-University of Kiel
 *   + Department of Computer Science
 *     + Real-Time and Embedded Systems Group
 * 
 * This code is provided under the terms of the Eclipse Public License (EPL).
 * See the file epl-v10.html for the license text.
 * 
 *****************************************************************************/
package de.cau.cs.kieler.core.model.figures;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.RoundedRectangle;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * A rounded rectangle with a double border.
 * 
 * @author schm
 * @kieler.ignore We don't care for GMF anymore.
 */
public class DoubleRoundedRectangle extends RoundedRectangle {

    /** width of the double rectangle border. */
    public static final int BORDER_WIDTH = 3;

    /**
     * The constructor.
     */
    public DoubleRoundedRectangle() {
        super();
    }

    /**
     * Draw the outline twice.
     * 
     * @param graphics the graphics object
     */
    protected void outlineShape(final Graphics graphics) {
        int width = getLineWidth();
        int distance = Math.max(width + 1, BORDER_WIDTH);
        Rectangle rect = new Rectangle();
        Rectangle bounds = getBounds();
        rect.x = bounds.x + width / 2;
        rect.y = bounds.y + width / 2;
        rect.width = bounds.width - width;
        rect.height = bounds.height - width;
        // calculate corners according to current dimensions
        int cornerWidth = Math.min(getCornerDimensions().width, bounds.width);
        int cornerHeight = Math.min(getCornerDimensions().height, bounds.height);

        graphics.drawRoundRectangle(rect, cornerWidth, cornerHeight);
        // Draw the second rectangle inside the first one
        rect.x += distance;
        rect.y += distance;
        rect.width -= 2 * distance;
        rect.height -= 2 * distance;
        graphics.drawRoundRectangle(rect, cornerWidth - distance * BORDER_WIDTH,
                cornerHeight - distance * BORDER_WIDTH);
    }
    
}
