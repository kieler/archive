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
package de.cau.cs.kieler.magicdraw.generator;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;
import com.nomagic.magicdraw.uml.symbols.PresentationElement;
import com.nomagic.magicdraw.uml.symbols.shapes.ActorView;
import com.nomagic.magicdraw.uml.symbols.shapes.ComponentView;
import com.nomagic.magicdraw.uml.symbols.shapes.NoteView;
import com.nomagic.magicdraw.uml.symbols.shapes.ShapeElement;
import com.nomagic.magicdraw.uml.symbols.shapes.UseCaseView;

import de.cau.cs.kieler.core.kgraph.KGraphElement;
import de.cau.cs.kieler.core.kgraph.KNode;

/**
 * Facade to encapsulate all {@link IKIELERShapeElementHandler} implementations behind.
 * 
 * @author nbw
 */
public class KIELERShapeElementHandler implements IKIELERShapeElementHandler<ShapeElement> {

    /**
     * Map to determine which handler implementation to use when encountering a specific
     * ShapeElement.
     */
    private Map<Class<?>, IKIELERShapeElementHandler<?>> handlerMap;

    /**
     * Generic handler to use when encountering something that doesn't need specific handling.
     */
    private GenericShapeElementHandler generic;

    /**
     * Generate the facade and populate the map with all known elements.
     */
    public KIELERShapeElementHandler() {
        // A simple generic handler can be used for unknown types
        generic = new GenericShapeElementHandler();
        // Store the special handlers in a HashMap to look the specific handler up later
        handlerMap = Maps.newHashMap();
        handlerMap.put(ComponentView.class, new ComponentViewHandler());
    }

    /**
     * <p>
     * Check the type of ShapeElement and delegate the work to the appropriate handler.
     * </p>
     * <p>
     * This unfortunately needs an "unsafe" cast which is not really unsafe, but handled like one by
     * the compiler. Each element is specifically cast to its own type.
     * </p>
     */
    public <T extends ShapeElement> void addElementToKGraph(ShapeElement pe, KGraphBuilder builder) {

        // Get the concrete class of the ShapeElement
        @SuppressWarnings("unchecked")
        Class<T> clazz = (Class<T>) pe.getClass();
        if (handlerMap.containsKey(clazz)) {
            // If the mapping contains the class, there should be a corresponding handler prepared
            // in the map. This handler should especially be able to handle the concrete type.
            @SuppressWarnings("unchecked")
            IKIELERShapeElementHandler<T> handler =
                    (IKIELERShapeElementHandler<T>) handlerMap.get(clazz);
            // Delegate to the handler
            handler.addElementToKGraph(clazz.cast(pe), builder);
        } else {
            // If the class has no dedicated handler use the generic instead
            generic.addElementToKGraph(pe, builder);
        }
    }
}
