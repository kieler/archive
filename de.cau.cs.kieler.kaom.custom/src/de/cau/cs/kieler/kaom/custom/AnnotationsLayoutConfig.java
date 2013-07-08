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
package de.cau.cs.kieler.kaom.custom;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.emf.ecore.EObject;

import de.cau.cs.kieler.core.annotations.Annotatable;
import de.cau.cs.kieler.core.annotations.Annotation;
import de.cau.cs.kieler.core.annotations.AnnotationsFactory;
import de.cau.cs.kieler.core.annotations.BooleanAnnotation;
import de.cau.cs.kieler.core.annotations.FloatAnnotation;
import de.cau.cs.kieler.core.annotations.IntAnnotation;
import de.cau.cs.kieler.core.annotations.StringAnnotation;
import de.cau.cs.kieler.core.properties.IProperty;
import de.cau.cs.kieler.kiml.ILayoutData;
import de.cau.cs.kieler.kiml.LayoutOptionData;
import de.cau.cs.kieler.kiml.LayoutDataService;
import de.cau.cs.kieler.kiml.config.SemanticLayoutConfig;
import de.cau.cs.kieler.kiml.options.LayoutOptions;

/**
 * A semantic layout configurator for annotations. This enables to specify layout option values in
 * the annotations of KAOM elements. 
 *
 * @author msp
 * @kieler.design proposed by msp
 * @kieler.rating yellow 2013-07-01 review KI-38 by cds, uru
 */
public class AnnotationsLayoutConfig extends SemanticLayoutConfig {
    
    /** the priority for annotations layout configurations. */
    public static final int PRIORITY = 20;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int getPriority() {
        return PRIORITY;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected IProperty<?>[] getAffectedOptions(final EObject semanticElem) {
        if (semanticElem instanceof Annotatable) {
            // run though all annotations and check whether their name matches some layout option id
            LayoutDataService dataService = LayoutDataService.getInstance();
            List<LayoutOptionData<?>> data = new LinkedList<LayoutOptionData<?>>();
            for (Annotation annotation : ((Annotatable) semanticElem).getAnnotations()) {
                LayoutOptionData<?> option = dataService.getOptionDataBySuffix(annotation.getName());
                if (option != null) {
                    data.add(option);
                }
            }
            return data.toArray(new IProperty<?>[data.size()]);
        }
        return null;
    }

    /**
     * Return an annotation that matches the given identifier, if present. If an annotation is
     * found that matches only the last segment (separated by '.') of the identifier, and no
     * perfect match is present, the partially matching annotation is returned.
     * 
     * @param annotatable an annotatable object
     * @param optionId a layout option identifier
     * @return an annotation, or {@code null}
     */
    private Annotation getAnnotation(final Annotatable annotatable, final String optionId) {
        Annotation result = annotatable.getAnnotation(optionId);
        if (result != null) {
            return result;
        }
        int dotIndex = optionId.lastIndexOf('.');
        if (dotIndex >= 0) {
            return annotatable.getAnnotation(optionId.substring(dotIndex + 1));
        }
        return null;
    }
    
    /**
     * Return the value of the given annotation.
     * 
     * @param annotation an annotation
     * @param optionData the layout option data
     * @return the annotation value, or {@code null}
     */
    private Object getValue(final Annotation annotation, final LayoutOptionData<?> optionData) {
        if (annotation instanceof StringAnnotation) {
            String value = ((StringAnnotation) annotation).getValue();
            if (optionData.equals(LayoutOptions.ALGORITHM)) {
                LayoutDataService dataService = LayoutDataService.getInstance();
                // The string value of the "algorithm" layout option can either refer to a layout
                // algorithm identifier or a layout type identifier. It is sufficient for the
                // annotation to contain only a suffix of the identifier. This code checks whether
                // there is a layout algorithm or type that matches the annotation value.
                ILayoutData layoutData = dataService.getAlgorithmDataBySuffix(value);
                if (layoutData != null) {
                    return layoutData.getId();
                }
                layoutData = dataService.getTypeDataBySuffix(value);
                if (layoutData != null) {
                    return layoutData.getId();
                }
            } else {
                return optionData.parseValue(value);
            }
        } else if (annotation instanceof IntAnnotation) {
            if (optionData.getType() == LayoutOptionData.Type.INT) {
                return ((IntAnnotation) annotation).getValue();
            }
        } else if (annotation instanceof FloatAnnotation) {
            if (optionData.getType() == LayoutOptionData.Type.FLOAT) {
                return ((FloatAnnotation) annotation).getValue();
            }
        } else if (annotation instanceof BooleanAnnotation) {
            if (optionData.getType() == LayoutOptionData.Type.BOOLEAN) {
                return ((BooleanAnnotation) annotation).isValue();
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Object getSemanticValue(final EObject semanticElem,
            final LayoutOptionData<?> layoutOption) {
        if (semanticElem instanceof Annotatable) {
            Annotation annotation = getAnnotation((Annotatable) semanticElem, layoutOption.getId());
            if (annotation != null) {
                return getValue(annotation, layoutOption);
            }
        }
        return null;
    }
    
    /**
     * Set the value of the given annotation.
     * 
     * @param annotation an annotation
     * @param optionData the layout option data
     * @param value the new annotation value
     */
    private void setValue(final Annotation annotation, final LayoutOptionData<?> optionData,
            final Object value) {
        if (annotation instanceof StringAnnotation) {
            ((StringAnnotation) annotation).setValue(value.toString());
        } else if (annotation instanceof IntAnnotation) {
            if (optionData.getType() == LayoutOptionData.Type.INT && value instanceof Integer) {
                ((IntAnnotation) annotation).setValue((Integer) value);
            } else {
                throw new IllegalStateException("The integer annotation " + annotation.getName()
                        + " does not fit to the corresponding layout option.");
            }
        } else if (annotation instanceof FloatAnnotation) {
            if (optionData.getType() == LayoutOptionData.Type.FLOAT && value instanceof Float) {
                ((FloatAnnotation) annotation).setValue((Float) value);
            } else {
                throw new IllegalStateException("The float annotation " + annotation.getName()
                        + " does not fit to the corresponding layout option.");
            }
        } else if (annotation instanceof BooleanAnnotation) {
            if (optionData.getType() == LayoutOptionData.Type.BOOLEAN && value instanceof Boolean) {
                ((BooleanAnnotation) annotation).setValue((Boolean) value);
            } else {
                throw new IllegalStateException("The boolean annotation " + annotation.getName()
                        + " does not fit to the corresponding layout option.");
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setSemanticValue(final EObject semanticElem,
            final LayoutOptionData<?> layoutOption, final Object value) {
        if (semanticElem instanceof Annotatable) {
            Annotatable annotatable = (Annotatable) semanticElem;
            if (value == null) {
                annotatable.removeAllAnnotations(layoutOption.getId());
            } else {
                Annotation annotation = getAnnotation(annotatable, layoutOption.getId());
                if (annotation != null) {
                    setValue(annotation, layoutOption, value);
                } else {
                    StringAnnotation newAnnot = AnnotationsFactory.eINSTANCE.createStringAnnotation();
                    newAnnot.setName(layoutOption.getId());
                    newAnnot.setValue(value.toString());
                    annotatable.getAnnotations().add(newAnnot);
                }
            }
        }
    }

}
