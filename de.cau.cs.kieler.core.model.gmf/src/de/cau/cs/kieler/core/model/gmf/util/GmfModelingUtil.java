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
package de.cau.cs.kieler.core.model.gmf.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.gef.EditPart;
import org.eclipse.gmf.runtime.diagram.core.util.ViewUtil;
import org.eclipse.gmf.runtime.diagram.ui.editparts.DiagramEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.LabelEditPart;
import org.eclipse.gmf.runtime.diagram.ui.parts.DiagramEditor;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

import de.cau.cs.kieler.core.model.gmf.GmfFrameworkBridge;
import de.cau.cs.kieler.core.util.Maybe;

/**
 * Utility class with static methods to handle EMF models and GEF EditParts.
 * 
 * @author haf, cmot
 * 
 * XXX for most functions use {@link de.cau.cs.kieler.core.model.GraphicalFrameworkService} instead
 */
public final class GmfModelingUtil {

    /** private constructor to avoid instantiation. */
    private GmfModelingUtil() {
    }

    
    /**
     * Get the model from a given GMF editor.
     *
     * @param diagramEditor the diagram editor
     * @return the model
     */
    public static EObject getModelFromGmfEditor(final DiagramEditor diagramEditor) {
        EObject model = null;
        
        View notationElement = ((View) ((DiagramEditor) diagramEditor).getDiagramEditPart()
                .getModel());
        
        if (notationElement == null) {
            return null;
        }
        
        model = (EObject) notationElement.getElement();

        return model;
    } 
    
    
    /**
     * Find an GEF EditPart that corresponds to an semantic model EObject. EObjects are used to
     * address objects that are exchanged between the plugins of the View Management as well as
     * other plugins. EObjects provide the ability for semantical addressing of other objects such
     * as children of an object or similar cases.
     * 
     * @author haf
     * @param eObject
     *            the semantic object
     * @param rootEditPart
     *            the root EditPart so start the search. May be null, then the current active editor
     *            is used
     * @return the corresponding EditPart
     * @deprecated use {@link IGraphicalFrameworkBridge#getEditPart(IEditorPart, Object)} instead
     */
    public static EditPart getEditPart(final EObject eObject, final EditPart rootEditPart) {
        // if (cachedEditParts2 == null) {
        // // if hashmap is not initialized, create it
        // cachedEditParts2 = new HashMap<EObject, EditPart>();
        // } else {
        // // try to get from hashmap first
        // if (cachedEditParts2.containsKey(eObject)) {
        // return cachedEditParts2.get(eObject);
        // }
        // }

        try {
            EditPart rootEP = rootEditPart;
            if (rootEP == null) {
                DiagramEditor editor = (DiagramEditor) PlatformUI.getWorkbench()
                        .getActiveWorkbenchWindow().getActivePage().getActiveEditor();
                DiagramEditPart dep = editor.getDiagramEditPart();
                rootEP = dep;
            }
            EditPart editPart = findEditPart(rootEP, eObject);
            if (editPart == null) {
                rootEP.getViewer().getEditPartRegistry().get(eObject);
            }
            // have to search registry manually
            if (editPart == null) {
                @SuppressWarnings("unchecked")
                Collection<Object> editParts = rootEP.getViewer().getEditPartRegistry().values();
                for (Object object : editParts) {
                    editPart = (EditPart) object;
                    EObject model = ((View) ((EditPart) object).getModel()).getElement();
                    if (model == eObject) {
                        // search the most valid parent
                        // this is necessary because inner EditParts may also
                        // reference the same
                        // model, e.g.
                        // TransitionTriggerAndEffectsEditPart has
                        // TransitionImpl as model element
                        // however, the parent
                        // TransitionEditPart also has TransitionImpl as model
                        // element
                        // so there are multiple EditParts that have the same
                        // EObject. Here we will
                        // return only the outermost parent EditPart
                        while (editPart.getParent() != null) {
                            EditPart parentPart = editPart.getParent();
                            Object view = parentPart.getModel();
                            if (view instanceof View) {
                                EObject parentModel = ((View) view).getElement();
                                if (parentModel == eObject) {
                                    editPart = parentPart;
                                }
                            } else {
                                break;
                            } // a Root diagram edit part has no real view, so
                              // we will stop
                              // searching there
                        }
                        // cachedEditParts2.put(eObject, editPart);
                        return editPart;
                    }
                }
            }
            // cachedEditParts2.put(eObject, editPart);
            return editPart;
        } catch (Exception e) {
            /* nothing, we simply return null if it cannot be found */
        }
        return null;
    }

    /**
     * Finds an editpart given a starting editpart and an EObject. Won't find connections.
     * 
     * @param epBegin
     *            where to begin looking
     * @param theElement
     *            the element to look for
     * @author haf
     * @return the editPart that was found or null
     **/
    private static EditPart findEditPart(final EditPart epBegin, final EObject theElement) {
        if (theElement == null || epBegin == null) {
            return null;
        }

        final View view = (View) ((IAdaptable) epBegin).getAdapter(View.class);

        if (view != null) {
            EObject el = ViewUtil.resolveSemanticElement(view);

            if ((el != null) && el.equals(theElement)) {
                return epBegin;
            }
        }

        for (Object child : epBegin.getChildren()) {
            if (child instanceof EditPart) {
                EditPart elementEP = findEditPart((EditPart) child, theElement);
                if (elementEP != null) {
                    return elementEP;
                }
            }
        }
        return null;
    }

    /**
     * Finds ALL edit parts connected to the given semantic element.
     * 
     * @param dep
     *            the root edit part
     * @param theElement
     *            the element to look for
     * @return the list of results, may be empty
     */
    @SuppressWarnings("unchecked")
    public static List<EditPart> getEditParts(final DiagramEditPart dep, final EObject theElement) {
        List<EditPart> result = new LinkedList<EditPart>();
        Collection<Object> editParts = dep.getViewer().getEditPartRegistry().values();
        for (Object object : editParts) {
            if (object instanceof EditPart) {
                EditPart editPart = (EditPart) object;
                Object objModel = editPart.getModel();
                if (objModel instanceof View) {
                    EObject model = ((View) objModel).getElement();
                    if (model == theElement) {
                        result.add(editPart);
                    }
                }
            }
        }
        return result;
    }

    /**
     * Find an EditPart corresponding to the given EObject in the DiagramEditPart.
     * 
     * @param dep
     *            the DiagramEditPart to search in
     * @param theElement
     *            the EObject to find
     * @return the EditPart, or {@code null} if none was found
     * @deprecated use {@link IGraphicalFrameworkBridge#getEditPart(IEditorPart, Object)} instead
     */
    public static EditPart getEditPart(final DiagramEditPart dep, final EObject theElement) {
        // attempted fix for the concurrent modification exception on delete
        final Maybe<EditPart> maybe = new Maybe<EditPart>();
        // FIXME mmu: temporary disabled using the UI thread to test slowness on haf's Mac
//        MonitoredOperation.runInUI(new Runnable() {
//            public void run() {
                if (theElement == null) {
                    return null;
                }
                EditPart found = dep.findEditPart(null, theElement);
                if (found != null) {
                    maybe.set(found);
//                    return;
                } else {
                    // the list always contains ConnectionEditParts
                    List<?> connections = dep.getConnections();
                    for (Object connection : connections) {
                        if (connection instanceof EditPart) {
                            EditPart ep = (EditPart) connection;
                            if (theElement.equals(((View) ep.getModel()).getElement())) {
                                maybe.set(ep);
//                                return;
                            }
                        }
                    }
                }
//            }
//        }, true);
        return maybe.get();
    }

    /**
     * Find an EditPart corresponding to the given EObject in an arbitrary editor. However, only GMF
     * editors are supported at the moment.
     * 
     * @param editorPart
     *            an editor part
     * @param element
     *            the EObject to find
     * @return the EditPart, or {@code null} if none was found
     * @deprecated use {@link IGraphicalFrameworkBridge#getEditPart(IEditorPart, Object)} instead
     */
    public static EditPart getEditPart(final IEditorPart editorPart, final EObject element) {
        if (editorPart instanceof DiagramEditor) {
            return getEditPart(((DiagramEditor) editorPart).getDiagramEditPart(), element);
        } else {
            return null;
        }
    }

    /**
     * Get all objects that are direct or indirect children of the given root EObject corresponding
     * to the given EditPart if they are of the specified type.
     * 
     * @author haf
     * 
     * @param eObjectClass
     *            The type of object
     * @param rootEditPart
     *            The root object
     * @return Collection of found EObject matching the type
     */
    public static Collection<EObject> getAllByType(final EClassifier eObjectClass,
            final EditPart rootEditPart) {
        EObject rootObject = ((View) rootEditPart.getModel()).getElement();
        TreeIterator<Object> iterator = EcoreUtil.getAllContents(rootObject, true);
        Collection<EObject> elements = EcoreUtil.getObjectsByType(iterator2Collection(iterator),
                eObjectClass);
        return elements;
    }

    /**
     * Convert an Iterator to a Collection. Useful if some method returns only an iterator but some
     * other method takes a Collection as input to iterate over that collection. However, it has
     * linear runtime and many such transformations should be avoided.
     * 
     * @author haf
     * @param <T>
     *            the base type
     * @param iter
     *            The input Iterator
     * @return A Collection containing all elements of the Iterator.
     */
    public static <T> Collection<T> iterator2Collection(final Iterator<T> iter) {
        ArrayList<T> list = new ArrayList<T>();
        for (; iter.hasNext();) {
            T item = iter.next();
            list.add(item);
        }
        return list;
    }

    /**
     * Returns a list of the EObjects currently selected in the diagram.
     * 
     * @return A List of EObjects
     */
    public static List<EObject> getModelElementsFromSelection() {
        if (PlatformUI.getWorkbench() != null
                && PlatformUI.getWorkbench().getActiveWorkbenchWindow() != null
                && PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService() != null) {
            ISelection sel = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                    .getSelectionService().getSelection();
            LinkedList<EObject> eo = new LinkedList<EObject>();
            if (sel instanceof StructuredSelection) {
                Iterator<?> it = ((StructuredSelection) sel).iterator();
                while (it.hasNext()) {
                    Object next = it.next();
                    if (next instanceof EditPart) {
                        Object model = ((EditPart) next).getModel();
                        if (model instanceof View && ((View) model).getElement() != null) {
                            // null check because Notes in a Diagram do not have semantic
                            // counterpart
                            eo.add(((View) model).getElement());
                        }
                    }
                }
            }
            return eo;
        }
        return null;
    }

    /**
     * Find an GEF EditPart that corresponds to an semantic model EObject. Needs to be called from
     * the UI thread.
     * 
     * @param eObject
     *            the semantic object
     * @return the corresponding EditPart
     * 
     *         TODO: search of transition edit parts iterates all edit parts and will take linear
     *         time. You should improve this, by maybe build a local cache hash map
     * @deprecated use {@link IGraphicalFrameworkBridge#getEditPart(Object)} instead
     */
    public static EditPart getEditPart(final EObject eObject) {
        try {
            DiagramEditor editor = (DiagramEditor) PlatformUI.getWorkbench()
                    .getActiveWorkbenchWindow().getActivePage().getActiveEditor();
            DiagramEditPart dep = editor.getDiagramEditPart();
            EditPart editPart = dep.findEditPart(dep, eObject);
            if (editPart == null) {
                Object o = dep.getViewer().getEditPartRegistry().get(eObject);
                if (o instanceof EditPart) {
                    editPart = (EditPart) o;
                }
            }
            // have to search registry manually
            if (editPart == null) {
                @SuppressWarnings("unchecked")
                Collection<Object> editParts = dep.getViewer().getEditPartRegistry().values();
                for (Object object : editParts) {
                    try {
                        EditPart theEditPart = (EditPart) object;
                        EObject model = ((View) theEditPart.getModel()).getElement();
                        if (model == eObject) {
                            // search the most valid parent
                            // this is necessary because inner EditParts may
                            // also
                            // reference the same model, e.g.
                            // TransitionTriggerAndEffectsEditPart has
                            // TransitionImpl as model element
                            // however, the parent
                            // TransitionEditPart also has TransitionImpl as
                            // model
                            // element
                            // so there are multiple EditParts that have the
                            // same
                            // EObject. Here we will
                            // return only the outermost parent EditPart
                            while (theEditPart.getParent() != null) {
                                EditPart parentPart = theEditPart.getParent();
                                Object view = parentPart.getModel();
                                if (view instanceof View) {
                                    EObject parentModel = ((View) view).getElement();
                                    if (parentModel == eObject) {
                                        theEditPart = parentPart;
                                    }
                                } else {
                                    break;
                                } // a Root diagram edit part has no real view,
                                  // so
                                  // we will stop searching there
                            }
                            return theEditPart;
                        }

                    } catch (Exception e) {
                        /* nothing, go on with the next element */
                    }
                }
            }
            return editPart;
        } catch (Exception e) {
            e.printStackTrace();
            /* nothing, we simply return null if it cannot be found */
        }
        return null;
    }
    
    /**
     * Returns the label edit part of the given node.
     * 
     * @param editorPart a workbench part
     * @param node a node object
     * @return the first label edit part, or {@code null} if none is found
     */
    public static EditPart getLabel(final IWorkbenchPart editorPart, final EObject node) {
        EditPart nodeEditPart = new GmfFrameworkBridge().getEditPart(editorPart, node);
        if (nodeEditPart != null) {
            for (Object child : nodeEditPart.getChildren()) {
                if (child instanceof LabelEditPart) {
                    return (LabelEditPart) child;
                }
            }
        }
        return null;
    }

}
