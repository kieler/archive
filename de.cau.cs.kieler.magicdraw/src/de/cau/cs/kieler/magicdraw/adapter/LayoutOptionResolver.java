/*
 * KIELER - Kiel Integrated Environment for Layout Eclipse RichClient
 *
 * http://www.informatik.uni-kiel.de/rtsys/kieler/
 *
 * Copyright 2014 by
 * + Christian-Albrechts-University of Kiel
 *   + Department of Computer Science
 *     + Real-Time and Embedded Systems Group
 *
 * This code is provided under the terms of the Eclipse Public License (EPL).
 * See the file epl-v10.html for the license text.
 */
package de.cau.cs.kieler.magicdraw.adapter;

import static de.cau.cs.kieler.kiml.options.LayoutOptions.ADDITIONAL_PORT_SPACE;
import static de.cau.cs.kieler.kiml.options.LayoutOptions.ALGORITHM;
import static de.cau.cs.kieler.kiml.options.LayoutOptions.ALIGNMENT;
import static de.cau.cs.kieler.kiml.options.LayoutOptions.ANIMATE;
import static de.cau.cs.kieler.kiml.options.LayoutOptions.BEND_POINTS;
import static de.cau.cs.kieler.kiml.options.LayoutOptions.COMMENT_BOX;
import static de.cau.cs.kieler.kiml.options.LayoutOptions.DIRECTION;
import static de.cau.cs.kieler.kiml.options.LayoutOptions.EDGE_LABEL_PLACEMENT;
import static de.cau.cs.kieler.kiml.options.LayoutOptions.EDGE_ROUTING;
import static de.cau.cs.kieler.kiml.options.LayoutOptions.EDGE_TYPE;
import static de.cau.cs.kieler.kiml.options.LayoutOptions.EXPAND_NODES;
import static de.cau.cs.kieler.kiml.options.LayoutOptions.FONT_NAME;
import static de.cau.cs.kieler.kiml.options.LayoutOptions.FONT_SIZE;
import static de.cau.cs.kieler.kiml.options.LayoutOptions.HYPERNODE;
import static de.cau.cs.kieler.kiml.options.LayoutOptions.INTERACTIVE;
import static de.cau.cs.kieler.kiml.options.LayoutOptions.JUNCTION_POINTS;
import static de.cau.cs.kieler.kiml.options.LayoutOptions.LABEL_SPACING;
import static de.cau.cs.kieler.kiml.options.LayoutOptions.LAYOUT_HIERARCHY;
import static de.cau.cs.kieler.kiml.options.LayoutOptions.MARGINS;
import static de.cau.cs.kieler.kiml.options.LayoutOptions.MIN_HEIGHT;
import static de.cau.cs.kieler.kiml.options.LayoutOptions.MIN_WIDTH;
import static de.cau.cs.kieler.kiml.options.LayoutOptions.NODE_LABEL_PLACEMENT;
import static de.cau.cs.kieler.kiml.options.LayoutOptions.NO_LAYOUT;
import static de.cau.cs.kieler.kiml.options.LayoutOptions.OFFSET;
import static de.cau.cs.kieler.kiml.options.LayoutOptions.PORT_ANCHOR;
import static de.cau.cs.kieler.kiml.options.LayoutOptions.PORT_CONSTRAINTS;
import static de.cau.cs.kieler.kiml.options.LayoutOptions.PORT_INDEX;
import static de.cau.cs.kieler.kiml.options.LayoutOptions.PORT_LABEL_PLACEMENT;
import static de.cau.cs.kieler.kiml.options.LayoutOptions.PORT_SIDE;
import static de.cau.cs.kieler.kiml.options.LayoutOptions.POSITION;
import static de.cau.cs.kieler.kiml.options.LayoutOptions.SEPARATE_CC;
import static de.cau.cs.kieler.kiml.options.LayoutOptions.SIZE_CONSTRAINT;
import static de.cau.cs.kieler.kiml.options.LayoutOptions.SIZE_OPTIONS;
import static de.cau.cs.kieler.kiml.options.LayoutOptions.SPACING;
import static de.cau.cs.kieler.kiml.options.LayoutOptions.THICKNESS;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import de.cau.cs.kieler.core.kgraph.EMapPropertyHolder;
import de.cau.cs.kieler.core.math.KVector;
import de.cau.cs.kieler.core.math.KVectorChain;
import de.cau.cs.kieler.core.properties.IProperty;
import de.cau.cs.kieler.core.properties.Property;
import de.cau.cs.kieler.core.util.Pair;
import de.cau.cs.kieler.kiml.UnsupportedGraphException;
import de.cau.cs.kieler.kiml.options.Alignment;
import de.cau.cs.kieler.kiml.options.Direction;
import de.cau.cs.kieler.kiml.options.EdgeLabelPlacement;
import de.cau.cs.kieler.kiml.options.EdgeRouting;
import de.cau.cs.kieler.kiml.options.EdgeType;
import de.cau.cs.kieler.kiml.options.NodeLabelPlacement;
import de.cau.cs.kieler.kiml.options.PortConstraints;
import de.cau.cs.kieler.kiml.options.PortLabelPlacement;
import de.cau.cs.kieler.kiml.options.PortSide;
import de.cau.cs.kieler.kiml.options.SizeConstraint;
import de.cau.cs.kieler.kiml.options.SizeOptions;
import de.cau.cs.kieler.kiml.util.nodespacing.Spacing.Margins;

import de.cau.cs.kieler.magicdraw.adapter.KGraphMagicDrawProperties.MagicDrawElementType;
import static de.cau.cs.kieler.magicdraw.adapter.KGraphMagicDrawProperties.MAGICDRAW_ID;
import static de.cau.cs.kieler.magicdraw.adapter.KGraphMagicDrawProperties.MAGICDRAW_TYPE;

/**
 * GWT does not support any of Java's reflection mechanisms. Hence we have to 
 * most of the job to map layout options to their respective types by hand.
 * 
 * @author uru
 */
public final class LayoutOptionResolver {

    /** A set to assure that non-unique suffixes are only used once. */
    private static final Set<String> SUFFIX_SET = Sets.newHashSet();
    
    private static final Pair<Set<String>, Map<String, IProperty<?>>> STRING_TYPES = createTypesSet(
            ALGORITHM,
            FONT_NAME
            );
    
    private static final Pair<Set<String>, Map<String, IProperty<?>>> INT_TYPES = createTypesSet(
            PORT_INDEX,
            FONT_SIZE,
            MAGICDRAW_ID
            );
    
    private static final Pair<Set<String>, Map<String, IProperty<?>>> BOOLEAN_TYPES = createTypesSet(
            ANIMATE,
            COMMENT_BOX,
            NO_LAYOUT,
            EXPAND_NODES,
            INTERACTIVE,
            LAYOUT_HIERARCHY,
            SEPARATE_CC,
            HYPERNODE
            );
    
    private static final Pair<Set<String>, Map<String, IProperty<?>>> FLOAT_TYPES = createTypesSet(
            MIN_WIDTH,
            MIN_HEIGHT,
            LABEL_SPACING,
            THICKNESS,
            OFFSET,
            SPACING
            );

    private static final Pair<Set<String>, Map<String, IProperty<?>>> ENUM_TYPES = createTypesSet(
            PORT_SIDE,
            ALIGNMENT,
            DIRECTION,
            EDGE_ROUTING,
            EDGE_LABEL_PLACEMENT,
            EDGE_TYPE,
            PORT_CONSTRAINTS,
            PORT_LABEL_PLACEMENT,
            MAGICDRAW_TYPE
            );
    
    private static final Pair<Set<String>, Map<String, IProperty<?>>> ENUMSET_TYPES = createTypesSet(
            NODE_LABEL_PLACEMENT,
            SIZE_CONSTRAINT,
            SIZE_OPTIONS
            );
    
    private static final Pair<Set<String>, Map<String, IProperty<?>>> OTHER_TYPES = createTypesSet(
            ADDITIONAL_PORT_SPACE,
            BEND_POINTS,
            JUNCTION_POINTS, 
            MARGINS,
            PORT_ANCHOR, 
            POSITION
            );
    
    private LayoutOptionResolver() {
    }
    
    /**
     * Convenience method to create the id->type mappings of the layout options. 
     */
    private static Pair<Set<String>, Map<String, IProperty<?>>> createTypesSet(
            final IProperty<?>... props) {
        Set<String> set = Sets.newHashSet();
        Map<String, IProperty<?>> map = Maps.newHashMap();
        for (IProperty<?> p : props) {
            String id = p.getId();
            set.add(id);
            map.put(id, p);
            
            // we also allow options to be selected by their suffix only
            String suffix = id.substring(id.lastIndexOf(".") + 1, id.length());
            if (!SUFFIX_SET.contains(suffix)) {
                set.add(suffix);
                map.put(suffix, p);
                // remember the suffix so that we don't use it twice
                SUFFIX_SET.add(suffix);
            }
        }
        
        Set<String> iset = ImmutableSet.copyOf(set);
        Map<String, IProperty<?>> imap = ImmutableMap.copyOf(map);
        return Pair.of(iset, imap);
    }

    /**
     * Tries to parse the passed {@code value} according to the type of the layout option registered
     * for the {@code id}. If this is successful, the property is set for the passed {@code element}.
     * 
     * @param element
     *            the graph's element for which the property should be set
     * @param id
     *            the id of the layout option
     * @param value
     *            the value to parse
     * @param override
     *            whether to override existing properties
     * @throws UnsupportedGraphException
     *             in case anything went wrong. This might be due to the id not being registered or
     *             the value being of an invalid format.
     */
    // SUPPRESS CHECKSTYLE NEXT 1 MethodLength
    @SuppressWarnings("unchecked")
    public static void setOption(final EMapPropertyHolder element, final String id,
            final String value, final boolean override) {

        if (!override) {
            // do not override any existing property
            if (element.getAllProperties().containsKey(new DummyProperty(id))) {
                return;
            }
        }
        
        if (STRING_TYPES.getFirst().contains(id)) {
            /*----------------------------------------------------------------
             *          STRING TYPE
             */
            if (value == null) {
                throw new UnsupportedGraphException("Invalid string format for property '" + id
                        + "' (" + value + ").");
            }
            IProperty<String> p = (IProperty<String>) STRING_TYPES.getSecond().get(id);
            
            element.setProperty(p, value);
            return;

        } else if (INT_TYPES.getFirst().contains(id)) {
            /*----------------------------------------------------------------
             *          INT TYPE
             */
            try {
                IProperty<Integer> p = (IProperty<Integer>) INT_TYPES.getSecond().get(id);
                // check that it's a proper integer
                Integer val = Integer.parseInt(value);
                element.setProperty(p, val);
                return;
                
            } catch (NumberFormatException nfe) {
                throw new UnsupportedGraphException("Invalid integer format for property '" + id
                        + "' (" + value + ").");
            }

        } else if (BOOLEAN_TYPES.getFirst().contains(id)) {
            /*----------------------------------------------------------------
             *          BOOLEAN TYPE
             */
            IProperty<Boolean> p = (IProperty<Boolean>) BOOLEAN_TYPES.getSecond().get(id);
            Boolean val = Boolean.parseBoolean(value);
            element.setProperty(p, val);
            return;

        } else if (FLOAT_TYPES.getFirst().contains(id)) {
            /*----------------------------------------------------------------
             *          FLOAT TYPE
             */
            try {
            	IProperty<Float> p = (IProperty<Float>) FLOAT_TYPES.getSecond().get(id);
                // check that it's a proper float
                Float val = Float.parseFloat(value);
                element.setProperty(p, val);
                return;
                
            } catch (NumberFormatException nfe) {
                throw new UnsupportedGraphException("Invalid float format for property '" + id
                        + "' (" + value + ").");
            }
            
        } else if (ENUM_TYPES.getFirst().contains(id)) {
            /*----------------------------------------------------------------
             *          ENUM TYPE
             */
            if (value == null) {
                throw new UnsupportedGraphException("Invalid enum format for property '" + id
                        + "' (" + value + ").");
            }
            
            Enum<?> enumeration = null;
       
            try {
                if (equalsIdOrSuffix(PORT_SIDE, id)) {
                    enumeration = PortSide.valueOf(value);
                } else if (equalsIdOrSuffix(ALIGNMENT, id)) {
                    enumeration = Alignment.valueOf(value);
                } else if (equalsIdOrSuffix(DIRECTION, id)) {
                    enumeration = Direction.valueOf(value);
                } else if (equalsIdOrSuffix(EDGE_ROUTING, id)) {
                    enumeration = EdgeRouting.valueOf(value);
                } else if (equalsIdOrSuffix(PORT_CONSTRAINTS, id)) {
                    enumeration = PortConstraints.valueOf(value);
                } else if (equalsIdOrSuffix(PORT_LABEL_PLACEMENT, id)) {
                    enumeration = PortLabelPlacement.valueOf(value);
                } else if (equalsIdOrSuffix(EDGE_TYPE, id)) {
                    enumeration = EdgeType.valueOf(value);
                } else if (equalsIdOrSuffix(EDGE_LABEL_PLACEMENT, id)) {
                    enumeration = EdgeLabelPlacement.valueOf(value);
                } else if (equalsIdOrSuffix(MAGICDRAW_TYPE, id)) {
                    enumeration = MagicDrawElementType.valueOf(value);
                }
                
            } catch (Exception e) {
                throw new UnsupportedGraphException("Invalid enum format for property '" + id
                        + "' (" + value + ").");
            }
            
            // set the property
            IProperty<Enum<?>> p = (IProperty<Enum<?>>) ENUM_TYPES.getSecond().get(id);
            element.setProperty(p, enumeration);
            return;
            
        } else if (ENUMSET_TYPES.getFirst().contains(id)) {
            /*----------------------------------------------------------------
             *          ENUMSET TYPE
             */
            if (value == null) {
                throw new UnsupportedGraphException("Invalid enum format for property '" + id
                        + "' (" + value + ").");
            }
            
            // we cannot use generics here, otherwise we would have to 
            // parameterize the set for each enum individually
            @SuppressWarnings("rawtypes")
            EnumSet set = null;
            
            // break the value string into its different components and iterate over them;
            // the string will be of the form "[a, b, c]"
            String[] components = value.split("[\\[\\]\\s,]+");
            for (String component : components) {
                // Check for empty strings
                if (component.trim().length() == 0) {
                    continue;
                }
                
                if (equalsIdOrSuffix(NODE_LABEL_PLACEMENT, id)) {
                    if (set == null) {
                        set = EnumSet.noneOf(NodeLabelPlacement.class);
                    }
                    set.add(NodeLabelPlacement.valueOf(component));

                } else if (equalsIdOrSuffix(SIZE_CONSTRAINT, id)) {
                    if (set == null) {
                        set = EnumSet.noneOf(SizeConstraint.class);
                    }
                    set.add(SizeConstraint.valueOf(component));

                } else if (equalsIdOrSuffix(SIZE_OPTIONS, id)) {
                    if (set == null) {
                        set = EnumSet.noneOf(SizeOptions.class);
                    }
                    set.add(SizeOptions.valueOf(component));
                }
            }
            
            // finished collecting, add the property
            // again we cannot use generics on EnumSet
            @SuppressWarnings("rawtypes")
            IProperty<EnumSet> p = (IProperty<EnumSet>) ENUMSET_TYPES.getSecond().get(id);
            element.setProperty(p, set);
            
            return;
            
        } else if (OTHER_TYPES.getFirst().contains(id)) {
            /*----------------------------------------------------------------
             *          OTHER TYPE
             */
            // CHECKSTYLEOFF RightCurly for the else if
            if (value == null) {
                throw new UnsupportedGraphException("Invalid _other_ format for property '" + id
                        + "' (" + value + ").");
            }
            // KVector
            if (equalsIdOrSuffix(POSITION, id) || equalsIdOrSuffix(PORT_ANCHOR, id)) {
                try {
                    KVector v = new KVector();
                    v.parse(value);
                    IProperty<KVector> p = (IProperty<KVector>) OTHER_TYPES.getSecond().get(id);
                    element.setProperty(p, v);
                    return;

                } catch (IllegalArgumentException exception) {
                    throw new UnsupportedGraphException("Invalid KVector format for property '" + id
                            + "' " + value + ".");
                }
            } 
            // KVectorChain
            else if (equalsIdOrSuffix(BEND_POINTS, id) || equalsIdOrSuffix(JUNCTION_POINTS, id)) {
                try {
                    KVectorChain vc = new KVectorChain();
                    vc.parse(value);
                    IProperty<KVectorChain> p =
                            (IProperty<KVectorChain>) OTHER_TYPES.getSecond().get(id);
                    element.setProperty(p, vc);
                    return;

                } catch (IllegalArgumentException exception) {
                    throw new UnsupportedGraphException(
                            "Invalid KVectorChain format for property '" + id + "' " + value + ".");
                }
            }
            // Margins
            else if (equalsIdOrSuffix(MARGINS, id) || equalsIdOrSuffix(ADDITIONAL_PORT_SPACE, id)) {
                try {
                    Margins margins = new Margins();
                    margins.parse(value);
                    IProperty<Margins> p = 
                            (IProperty<Margins>) OTHER_TYPES.getSecond().get(id);
                    element.setProperty(p, margins);
                    return;
                } catch (IllegalArgumentException exception) {
                    throw new UnsupportedGraphException(
                            "Invalid Margins format for property '" + id + "' " + value + ".");
                }
            }
        }
        
        throw new UnsupportedGraphException("Unsupported layout option '" + id + "' (" + value + ").");
    }

    /**
     * @return true if the {@code prop}'s id or its suffix equal {@code idOrSuffix}.
     */
    private static boolean equalsIdOrSuffix(final IProperty<?> prop, final String idOrSuffix) {
        return prop.getId().equals(idOrSuffix)
                || (prop.getId().endsWith(idOrSuffix) && (idOrSuffix.length() == prop.getId()
                        .length() || prop.getId().charAt(
                        prop.getId().length() - idOrSuffix.length() - 1) == '.'));
    }

    /**
     * A container property to check if a property id exists on a graph element without actually
     * using it.
     */
    private static class DummyProperty extends Property<Object> {
        public DummyProperty(final String theid) {
            super(theid);
        }
    }

}
