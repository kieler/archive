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
 */

package de.cau.cs.kieler.kaom.karma.ptolemy.figurecreation;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.StringTokenizer;

import org.eclipse.draw2d.BorderLayout;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.Panel;
import org.eclipse.draw2d.PolygonShape;
import org.eclipse.draw2d.PolylineShape;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.Shape;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.PrecisionPoint;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gmf.runtime.draw2d.ui.internal.figures.ImageFigureEx;
import org.eclipse.gmf.runtime.gef.ui.figures.NodeFigure;
import org.eclipse.gmf.runtime.gef.ui.internal.figures.CircleFigure;
import org.eclipse.gmf.runtime.gef.ui.internal.figures.OvalFigure;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Class for creating a draw2d figure out of an svg document.
 * 
 * @author ckru
 * @kieler.ignore (excluded from review process)
 */
//this class needs some internal draw2d figures
@SuppressWarnings("restriction")
public final class FigureParser {

    /**
     * This is a utility class and thus the constructor is hidden.
     */
    private FigureParser() {

    }

    /**
     * Create an draw2d figure out of an svg document.
     * 
     * @param doc
     *            the svg document
     * @return the draw2d figure equivalent to the svg
     */
    public static IFigure createFigure(final Document doc) {
        Element svgElement = (Element) doc.getElementsByTagName("svg").item(0);
        // Make an invisible container to hold the visible figures because we don't know the
        // structure of the svg.
        IFigure rootFigure = new Panel();
        rootFigure.getBounds().setSize(
                new Dimension((int) Math.abs(Float.parseFloat(svgElement.getAttribute("width"))),
                        (int) Math.abs(Float.parseFloat(svgElement.getAttribute("height")))));
        rootFigure = buildFigure(svgElement, rootFigure);
        return rootFigure;
    }

    /**
     * Parsing the svg and creating figures accordingly. Builds a hirachical structure.
     * 
     * @param root
     *            the top level svg element.
     * @param parentFigure
     *            an invisible figure as container for the actual figures
     * @return a hirachical figure representing the svg
     */
    private static IFigure buildFigure(final Element root, final IFigure parentFigure) {
        NodeList childList = root.getChildNodes();
        for (int i = 0; i < childList.getLength(); i++) {
            Node child = childList.item(i);
            if (child instanceof Element) {
                Element childElement = (Element) child;
                String tag = childElement.getTagName();
                // make a RectangleFigure from a rectangle element
                if (tag.equals("rect")) {
                    RectangleFigure figure = new RectangleFigure();
                    Double x = Double.parseDouble(childElement.getAttribute("x"));
                    Double y = Double.parseDouble(childElement.getAttribute("y"));
                    Double width = Double.parseDouble(childElement.getAttribute("width"));
                    Double height = Double.parseDouble(childElement.getAttribute("height"));
                    String style = (String) childElement.getAttribute("style");
                    figure.setBounds(new Rectangle(new PrecisionPoint(x, y), new Dimension(
                            (int) Math.abs(width), (int) Math.abs(height))));
                    applyStyle(figure, style);
                    parentFigure.add(buildFigure(childElement, figure));
                    // make a CircleFigure from a circle element.
                    // structure is different between draw2d and svg so positions are a bit hacked
                } else if (tag.equals("circle")) {

                    Double x = Double.parseDouble(childElement.getAttribute("cx"));
                    Double y = Double.parseDouble(childElement.getAttribute("cy"));
                    Double r = Double.parseDouble(childElement.getAttribute("r"));
                    String style = (String) childElement.getAttribute("style");
                    CircleFigure figure = new CircleFigure(r.intValue());
                    figure.getBounds().setLocation(new PrecisionPoint((x + 1 - r), (y + 1 - r)));
                    figure.getBounds().setSize((r.intValue() * 2), (r.intValue() * 2));
                    applyStyle(figure, style);
                    parentFigure.add(buildFigure(childElement, figure));
                    // make a CircleFigure from a ellipse element.
                    // structure is different between draw2d and svg so positions are a bit hacked
                } else if (tag.equals("ellipse")) {
                    OvalFigure figure = new OvalFigure();
                    Double x = Double.parseDouble(childElement.getAttribute("cx"));
                    Double y = Double.parseDouble(childElement.getAttribute("cy"));
                    Double rx = Double.parseDouble(childElement.getAttribute("rx"));
                    Double ry = Double.parseDouble(childElement.getAttribute("ry"));
                    String style = (String) childElement.getAttribute("style");
                    figure.getBounds().setLocation(new PrecisionPoint(x + 1 - rx, y + 1 - ry));
                    figure.getBounds().setSize(
                            new Dimension((int) Math.abs(rx * 2), (int) Math.abs(ry * 2)));
                    applyStyle(figure, style);
                    parentFigure.add(buildFigure(childElement, figure));
                    // make a PolyLineShape from a line element
                } else if (tag.equals("line")) {
                    Double x1 = Double.parseDouble(childElement.getAttribute("x1"));
                    Double y1 = Double.parseDouble(childElement.getAttribute("y1"));
                    Double x2 = Double.parseDouble(childElement.getAttribute("x2"));
                    Double y2 = Double.parseDouble(childElement.getAttribute("y2"));
                    String style = (String) childElement.getAttribute("style");
                    PolylineShape figure = new PolylineShape();
                    figure.setStart(new PrecisionPoint(x1, y1));
                    figure.setEnd(new PrecisionPoint(x2, y2));
                    applyStyle(figure, style);
                    parentFigure.add(buildFigure(childElement, figure));
                    figure.getBounds().setSize(figure.getParent().getBounds().getSize().getCopy());
                    // make a PolylineShape from a polyline element.
                } else if (tag.equals("polyline")) {
                    String allpoints = childElement.getAttribute("points");
                    String style = (String) childElement.getAttribute("style");
                    String[] pointsarray = allpoints.split(" +");
                    PointList pointList = new PointList();
                    for (String coords : pointsarray) {
                        String[] coordsarray = coords.split(",");
                        Double x = Double.parseDouble(coordsarray[0]);
                        Double y = Double.parseDouble(coordsarray[1]);
                        pointList.addPoint(new PrecisionPoint(x, y));
                    }
                    PolylineShape figure = new PolylineShape();
                    figure.setPoints(pointList);
                    applyStyle(figure, style);
                    parentFigure.add(buildFigure(childElement, figure));
                    figure.getBounds().setSize(figure.getParent().getBounds().getSize().getCopy());
                    // make a PolygonShape from a polygon element
                } else if (tag.equals("polygon")) {
                    String allpoints = childElement.getAttribute("points");
                    String style = (String) childElement.getAttribute("style");
                    String[] pointsarray = allpoints.split(" +");
                    PointList pointList = new PointList();
                    for (String coords : pointsarray) {
                        String[] coordsarray = coords.split(",");
                        Double x = Double.parseDouble(coordsarray[0]);
                        Double y = Double.parseDouble(coordsarray[1]);
                        pointList.addPoint(new PrecisionPoint(x, y));
                    }
                    PolygonShape figure = new PolygonShape();
                    figure.setPoints(pointList);
                    applyStyle(figure, style);
                    parentFigure.add(buildFigure(childElement, figure));
                    figure.getBounds().setSize(figure.getParent().getBounds().getSize().getCopy());
                    // make a Label from a text element
                    // TODO weird behavior of y value
                } else if (tag.equals("text")) {
                    Double x = Double.parseDouble(childElement.getAttribute("x"));
                    Double y = Double.parseDouble(childElement.getAttribute("y"));
                    String style = (String) childElement.getAttribute("style");
                    String text = childElement.getTextContent();
                    text = text.replaceAll("\n", "");
                    text = text.trim();
                    Label figure = new Label();
                    figure.setText(text);
                    applyTextStyle(figure, style);
                    figure.getBounds()
                            .setLocation(
                                    new PrecisionPoint(x, y
                                            - (figure.getTextBounds().getSize().height - 2)));
                    figure.getBounds().setSize(figure.getTextBounds().getSize());
                    figure.setLayoutManager(new BorderLayout());
                    parentFigure.add(buildFigure(childElement, figure));
                    // make an ImageFigureEx out of an image element
                } else if (tag.equals("image")) {
                    Double x = Double.parseDouble(childElement.getAttribute("x"));
                    Double y = Double.parseDouble(childElement.getAttribute("y"));
                    Double width = Double.parseDouble(childElement.getAttribute("width"));
                    Double height = Double.parseDouble(childElement.getAttribute("height"));
                    String link = (String) childElement.getAttribute("xlink:href");
                    String style = (String) childElement.getAttribute("style");
                    URL url = ClassLoader.getSystemResource(link);
                    if (url == null) {
                        try {
                            url = new URL(link);
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                    }
                    Image img = null;
                    ImageFigureEx figure = null;
                    try {
                        if (url != null) {
                            img = new Image(null, url.openStream());
                            figure = new ImageFigureEx(img);
                        }
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    if (figure != null) {
                        figure.setBounds(new Rectangle(new PrecisionPoint(x, y), new PrecisionPoint(
                            width, height)));
                    }
                    applyStyle(figure, style);
                    parentFigure.add(buildFigure(childElement, figure));

                }

            }
        }
        return parentFigure;
    }

    /**
     * Applys the style attribute of the svg element to the figure.
     * 
     * @param figure
     *            the figure whoose style to set
     * @param style
     *            the style as a string
     */
    private static void applyStyle(final IFigure figure, final String style) {
        if (style != null) {
            StringTokenizer t = new StringTokenizer(style, ";");

            while (t.hasMoreTokens()) {
                String string = t.nextToken().trim();
                int index = string.indexOf(":");
                String name = string.substring(0, index);
                String value = string.substring(index + 1);
                // fill might be background, stroke foreground. Works fine so far.
                if (name.equals("fill")) {
                    figure.setBackgroundColor(lookupColor(value));
                } else if (name.equals("stroke")) {
                    figure.setForegroundColor(lookupColor(value));
                } else if (name.equals("stroke-width")) {
                    Float width = Float.parseFloat(value);
                    if (figure instanceof Shape) {
                        ((Shape) figure).setLineWidth(width.intValue());
                    } else if (figure instanceof NodeFigure) {
                        ((NodeFigure) figure).setLineWidth(width.intValue());
                    }
                }
            }
        }
    }

    /**
     * Applys the style attribute of the svg element to the figure.
     * 
     * @param figure
     *            the figure whoose style to set
     * @param style
     *            the style as a string
     */
    private static void applyTextStyle(final IFigure figure, final String style) {
        if (style != null) {
            StringTokenizer t = new StringTokenizer(style, ";");

            while (t.hasMoreTokens()) {
                String string = t.nextToken().trim();
                int index = string.indexOf(":");
                String name = string.substring(0, index);
                String value = string.substring(index + 1);
                // foreground color determines the text color
                if (name.equals("fill")) {
                    figure.setForegroundColor(lookupColor(value));
                    // some hacked size stuff without having a fitting font.
                } else if (name.equals("font-size")) {
                    int size = Integer.parseInt(value);
                    if (figure instanceof Label) {
                        FontData[] fonts = PlatformUI.getWorkbench().getDisplay()
                                .getFontList("arial", true);
                        FontData fd = fonts[0];
                        fd.setHeight(size - 2);
                        Font font = new Font(PlatformUI.getWorkbench().getDisplay(), fd);
                        ((Label) figure).setFont(font);
                    }
                }
                // TODO set a font.Problem: Svg has an attribute that loosely describes the font
                // family.
                // This has to be mapped to existing fonts on a specific system.
            }
        }
    }

    /**
     * Make a draw2d color object out of a color name.
     * 
     * @param color
     *            string representation of a color
     * @return the color described by the string. black if not found.
     */
    private static Color lookupColor(final String color) {
        String s = color.toLowerCase();

        if (s.equals("black")) {
            return ColorConstants.black;
        } else if (s.equals("blue")) {
            return ColorConstants.blue;
        } else if (s.equals("cyan")) {
            return ColorConstants.cyan;
        } else if (s.equals("darkgray") || s.equals("darkgrey")) {
            return ColorConstants.darkGray;
        } else if (s.equals("lightgray") || s.equals("lightgrey")) {
            return ColorConstants.lightGray;
        } else if (s.equals("gray") || s.equals("grey")) {
            return ColorConstants.gray;
        } else if (s.equals("green")) {
            return ColorConstants.green;
        } else if (s.equals("orange")) {
            return ColorConstants.orange;
        } else if (s.equals("red")) {
            return ColorConstants.red;
        } else if (s.equals("white")) {
            return ColorConstants.white;
        } else if (s.equals("yellow")) {
            return ColorConstants.yellow;
        } else {
            Color c = ColorConstants.black;
            return c;
        }
    }
}
