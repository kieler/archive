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
 * @author nbw
 * 
 */
public class KIELERShapeElementHandler implements IKIELERShapeElementHandler<ShapeElement> {

    private Map<Class<?>, IKIELERShapeElementHandler<?>> handlerMap;

    public KIELERShapeElementHandler() {
        GenericShapeElementHandler generic = new GenericShapeElementHandler();
        handlerMap = Maps.newHashMap();
        handlerMap.put(ComponentView.class, new ComponentViewHandler());
        handlerMap.put(ActorView.class, generic);
        handlerMap.put(NoteView.class, generic);
        handlerMap.put(UseCaseView.class, generic);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public <T extends ShapeElement> void addElementToKGraph(ShapeElement pe, KNode kGraphRoot,
            Map<PresentationElement, KGraphElement> elementsMapping,
            List<PresentationElement> elementsByID) {
        
        Class<T> clazz = (Class<T>) pe.getClass();
        if (handlerMap.containsKey(clazz)) {
            IKIELERShapeElementHandler<T> handler =
                    (IKIELERShapeElementHandler<T>) handlerMap.get(clazz);
            handler.addElementToKGraph(clazz.cast(pe), kGraphRoot, elementsMapping, elementsByID);
        }

    }
}
