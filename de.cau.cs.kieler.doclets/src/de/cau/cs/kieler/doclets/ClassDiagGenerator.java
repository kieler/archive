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
package de.cau.cs.kieler.doclets;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.uml2.uml.Association;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Enumeration;
import org.eclipse.uml2.uml.EnumerationLiteral;
import org.eclipse.uml2.uml.Generalization;
import org.eclipse.uml2.uml.Interface;
import org.eclipse.uml2.uml.LiteralInteger;
import org.eclipse.uml2.uml.LiteralUnlimitedNatural;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.Operation;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.PrimitiveType;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.UMLFactory;
import org.eclipse.uml2.uml.VisibilityKind;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.ParameterizedType;
import com.sun.javadoc.ProgramElementDoc;
import com.sun.javadoc.RootDoc;

/**
 * Generator for UML class diagrams.
 *
 * @author msp
 */
public class ClassDiagGenerator {
    
    /** the destination file where output is written. */
    private String destinationFile;
    
    /**
     * Generate a UML2 model from the given Javadoc data.
     * 
     * @param rootDoc the root doc
     * @throws IOException if the generator fails
     */
    public void generate(final RootDoc rootDoc) throws IOException {
        UMLFactory umlFactory = UMLFactory.eINSTANCE;
        Model model = umlFactory.createModel();
        model.setName("KIELER");

        // create primitive types
        Map<String, PrimitiveType> primitiveTypeMap = createPrimitiveTypes(model);        
        
        // analyze the Java classes
        Map<String, Package> packageMap = new HashMap<String, Package>();
        Map<String, Classifier> classMap = new HashMap<String, Classifier>();
        for (ClassDoc classDoc : rootDoc.classes()) {
            if (!primitiveTypeMap.containsKey(classDoc.simpleTypeName())) {
                Classifier clazz = createClass(umlFactory, classDoc, model, packageMap);
                if (clazz != null) {
                    classMap.put(classDoc.qualifiedName(), clazz);
                }
            }
        }
        
        // analyze relations between classes
        for (ClassDoc classDoc : rootDoc.classes()) {
            Classifier clazz = classMap.get(classDoc.qualifiedName());
            if (clazz != null) {
                // create generalizations to superclass or superinterfaces
                if (classDoc.superclassType() != null) {
                    Classifier superClazz = classMap.get(classDoc.superclassType()
                            .qualifiedTypeName());
                    if (superClazz != null
                            && !superClazz.getName().equals("Object")) {
                        Generalization generalization = umlFactory.createGeneralization();
                        generalization.setGeneral(superClazz);
                        generalization.setSpecific(clazz);
                    }
                }
                for (com.sun.javadoc.Type superType : classDoc.interfaceTypes()) {
                    Classifier superClazz = classMap.get(superType.qualifiedTypeName());
                    if (superClazz != null) {
                        Generalization generalization = umlFactory.createGeneralization();
                        generalization.setGeneral(superClazz);
                        generalization.setSpecific(clazz);
                    }
                }
                
                // create associations and properties
                for (FieldDoc fieldDoc : classDoc.fields()) {
                    if (fieldDoc.constantValue() == null) {
                        Property property = createProperty(umlFactory, fieldDoc, clazz,
                                classMap, primitiveTypeMap);
                        if (property != null) {
                            if (clazz instanceof Class) {
                                ((Class) clazz).getOwnedAttributes().add(property);
                            } else if (clazz instanceof Interface) {
                                ((Interface) clazz).getOwnedAttributes().add(property);
                            } else if (clazz instanceof Enumeration) {
                                ((Enumeration) clazz).getOwnedAttributes().add(property);
                            }
                        }
                    }
                }
                
                // create operations
                for (MethodDoc methodDoc : classDoc.methods()) {
                    if (methodDoc.isPublic()) {
                        Operation operation = createOperation(umlFactory, methodDoc,
                                classMap, primitiveTypeMap);
                        if (operation != null) {
                            if (clazz instanceof Class) {
                                ((Class) clazz).getOwnedOperations().add(operation);
                            } else if (clazz instanceof Interface) {
                                ((Interface) clazz).getOwnedOperations().add(operation);
                            } else if (clazz instanceof Enumeration) {
                                ((Enumeration) clazz).getOwnedOperations().add(operation);
                            }
                        }
                    }
                }
            }
        }
        
        writeModel(model);
    }
    
    /**
     * Create a UML class.
     * 
     * @param umlFactory factory for object creation
     * @param classDoc class documentation by Javadoc
     * @param model the UML model where packages are put
     * @param packageMap map of package names to UML packages
     * @return a classifier for the Java class, or {@code null} if none could be created
     */
    private Classifier createClass(final UMLFactory umlFactory, final ClassDoc classDoc,
            final Model model, final Map<String, Package> packageMap) {
        if (classDoc.isPublic() && classDoc.name() != null && classDoc.name().length() > 0) {
            // create package
            PackageDoc packageDoc = classDoc.containingPackage();
            Package pakkage = packageMap.get(packageDoc.name());
            if (pakkage == null) {
                pakkage = umlFactory.createPackage();
                pakkage.setName(packageDoc.name());
                model.getPackagedElements().add(pakkage);
                packageMap.put(packageDoc.name(), pakkage);
            }
            
            // create class
            Classifier clazz;
            if (classDoc.isInterface()) {
                clazz = umlFactory.createInterface();
            } else if (classDoc.isEnum()) {
                // create enumeration literals
                Enumeration enumeration = umlFactory.createEnumeration();
                for (FieldDoc enumConst : classDoc.enumConstants()) {
                    EnumerationLiteral literal = umlFactory.createEnumerationLiteral();
                    literal.setName(enumConst.name());
                    enumeration.getOwnedLiterals().add(literal);
                }
                clazz = enumeration;
            } else {
                clazz = umlFactory.createClass();
            }
            clazz.setPackage(pakkage);
            clazz.setName(classDoc.name());
            clazz.setIsAbstract(classDoc.isAbstract());
            clazz.setVisibility(getVisibility(classDoc));
            pakkage.getPackagedElements().add(clazz);
            return clazz;
        }
        return null;
    }
    
    /**
     * Create a UML property.
     * 
     * @param umlFactory factory for object creation
     * @param fieldDoc field documentation by Javadoc
     * @param clazz the UML class that holds the property
     * @param classMap map of type names to UML classes
     * @param primitiveTypeMap map of type names to UML primitive types
     * @return a property for the Java field, or {@code null} if none could be created
     */
    private Property createProperty(final UMLFactory umlFactory, final FieldDoc fieldDoc,
            final Classifier clazz, final Map<String, Classifier> classMap,
            final Map<String, PrimitiveType> primitiveTypeMap) {
        ClassDoc type = fieldDoc.type().asClassDoc();
        if (type != null && fieldDoc.name() != null && fieldDoc.name().length() > 0) {
            Classifier typeClazz = null;
            String typeName = type.typeName();
            boolean isList = typeName.endsWith("List") || typeName.endsWith("Collection")
                    || typeName.endsWith("Set");
            if (isList) {
                // handle type arguments of generic types
                ParameterizedType paramType = fieldDoc.type().asParameterizedType();
                if (paramType != null && paramType.typeArguments().length > 0) {
                    ClassDoc typeArg = paramType.typeArguments()[0].asClassDoc();
                    typeClazz = classMap.get(typeArg.qualifiedName());
                }
            }
            if (typeClazz == null) {
                typeClazz = classMap.get(type.qualifiedName());
                isList = false;
            }
            Property property = null;
            if (typeClazz != null) {
                // create an association and property for the field
                Association association = umlFactory.createAssociation();
                clazz.getPackage().getPackagedElements().add(association);
                property = umlFactory.createProperty();
                property.setType(typeClazz);
                property.setAssociation(association);
                // set lower value for property
                LiteralInteger lowerValue = umlFactory.createLiteralInteger();
                lowerValue.setValue(0);
                property.setLowerValue(lowerValue);
                // set upper value for property
                LiteralUnlimitedNatural upperValue = umlFactory
                        .createLiteralUnlimitedNatural();
                upperValue.setValue(isList ? -1 : 1);
                property.setUpperValue(upperValue);
            } else if (fieldDoc.isPublic()) {
                // handle primitive types as class members
                PrimitiveType primitiveType = primitiveTypeMap.get(
                        fieldDoc.type().simpleTypeName());
                if (primitiveType != null) {
                    property = umlFactory.createProperty();
                    property.setType(primitiveType);
                }
            }
            if (property != null) {
                property.setName(fieldDoc.name());
                property.setVisibility(getVisibility(fieldDoc));
                return property;
            }
        }
        return null;
    }
    
    /**
     * Create a UML operation.
     * 
     * @param umlFactory factory for object creation
     * @param methodDoc method documentation by Javadoc
     * @param classMap map of type names to UML classes
     * @param primitiveTypeMap map of type names to UML primitive types
     * @return an operation for the Java method, or {@code null} if none could be created
     */
    private Operation createOperation(final UMLFactory umlFactory, final MethodDoc methodDoc,
            final Map<String, Classifier> classMap,
            final Map<String, PrimitiveType> primitiveTypeMap) {
        if (methodDoc.name() != null && methodDoc.name().length() > 0) {
            Operation operation = umlFactory.createOperation();
            operation.setName(methodDoc.name());
            // set return type of the method
            org.eclipse.uml2.uml.Type returnType = classMap.get(methodDoc.returnType()
                    .qualifiedTypeName());
            if (returnType == null) {
                returnType = primitiveTypeMap.get(methodDoc.returnType().simpleTypeName());
            }
            if (returnType != null) {
                operation.setType(returnType);
            }
            // set method parameters
            for (com.sun.javadoc.Parameter docParam : methodDoc.parameters()) {
                if (docParam.name() != null && docParam.name().length() > 0) {
                    org.eclipse.uml2.uml.Parameter umlParam = umlFactory.createParameter();
                    umlParam.setName(docParam.name());
                    org.eclipse.uml2.uml.Type paramType = classMap.get(
                            docParam.type().qualifiedTypeName());
                    if (paramType == null) {
                        paramType = primitiveTypeMap.get(docParam.type().simpleTypeName());
                    }
                    if (paramType != null) {
                        umlParam.setType(paramType);
                    }
                    operation.getOwnedParameters().add(umlParam);
                }
            }
            operation.setVisibility(getVisibility(methodDoc));
            return operation;
        }
        return null;
    }
    
    /** the primitive type names. */
    private static final String[] PRIMITIVE_TYPES = new String[] {
        "int", "boolean", "float", "double", "String"
    };
    
    /**
     * Creates a map of primitive types.
     * 
     * @param model the UML model to store types
     * @return a map of primitive types
     */
    private Map<String, PrimitiveType> createPrimitiveTypes(final Model model) {
        Map<String, PrimitiveType> typeMap = new HashMap<String, PrimitiveType>();
        for (String name : PRIMITIVE_TYPES) {
            PrimitiveType type = UMLFactory.eINSTANCE.createPrimitiveType();
            type.setName(name);
            model.getPackagedElements().add(type);
            typeMap.put(name, type);
        }
        return typeMap;
    }
    
    /**
     * Determine the visibility of an element.
     * 
     * @param element an element
     * @return the element's visibility
     */
    private static VisibilityKind getVisibility(final ProgramElementDoc element) {
        if (element.isPublic()) {
            return VisibilityKind.PUBLIC_LITERAL;
        } else if (element.isProtected()) {
            return VisibilityKind.PROTECTED_LITERAL;
        } else if (element.isPackagePrivate()) {
            return VisibilityKind.PACKAGE_LITERAL;
        } else {
            return VisibilityKind.PRIVATE_LITERAL;
        }
    }
    
    /**
     * Sets the destination file.
     *
     * @param thedestinationFile the destination file to set
     */
    public void setDestinationFile(final String thedestinationFile) {
        this.destinationFile = thedestinationFile;
    }
    
    /**
     * Write the given UML model to the destination file.
     * 
     * @param model a UML model
     * @throws IOException if writing to the destination file fails
     */
    private void writeModel(final Model model) throws IOException {
        // Create a resource set.
        ResourceSet resourceSet = new ResourceSetImpl();

        // Register the default resource factory -- only needed for stand-alone!
        resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(
                Resource.Factory.Registry.DEFAULT_EXTENSION, new XMIResourceFactoryImpl());

        // Get the URI of the model file.
        URI fileURI = URI.createFileURI(new File(destinationFile).getAbsolutePath());

        // Create a resource for this file.
        Resource resource = resourceSet.createResource(fileURI);

        // Add the UML model to the contents.
        resource.getContents().add(model);

        // Save the contents of the resource to the file system.
        resource.save(Collections.EMPTY_MAP);
    }

}
