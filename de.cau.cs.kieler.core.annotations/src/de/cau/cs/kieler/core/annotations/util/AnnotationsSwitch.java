/**
 * KIELER - Kiel Integrated Environment for Layout Eclipse RichClient
 * 
 * http://www.informatik.uni-kiel.de/rtsys/kieler/
 * 
 * Copyright 2010 by
 * + Christian-Albrechts-University of Kiel
 *   + Department of Computer Science
 *     + Real-Time and Embedded Systems Group
 * 
 * This code is provided under the terms of the Eclipse Public License (EPL).
 * See the file epl-v10.html for the license text.
 */
package de.cau.cs.kieler.core.annotations.util;

import de.cau.cs.kieler.core.annotations.*;

import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * The <b>Switch</b> for the model's inheritance hierarchy.
 * It supports the call {@link #doSwitch(EObject) doSwitch(object)}
 * to invoke the <code>caseXXX</code> method for each class of the model,
 * starting with the actual class of the object
 * and proceeding up the inheritance hierarchy
 * until a non-null result is returned,
 * which is the result of the switch.
 * <!-- end-user-doc -->
 * @see de.cau.cs.kieler.core.annotations.AnnotationsPackage
 * @generated
 */
public class AnnotationsSwitch<T> {
    /**
	 * The cached model package
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    protected static AnnotationsPackage modelPackage;

    /**
	 * Creates an instance of the switch.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    public AnnotationsSwitch() {
		if (modelPackage == null) {
			modelPackage = AnnotationsPackage.eINSTANCE;
		}
	}

    /**
	 * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @return the first non-null result returned by a <code>caseXXX</code> call.
	 * @generated
	 */
    public T doSwitch(EObject theEObject) {
		return doSwitch(theEObject.eClass(), theEObject);
	}

    /**
	 * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @return the first non-null result returned by a <code>caseXXX</code> call.
	 * @generated
	 */
    protected T doSwitch(EClass theEClass, EObject theEObject) {
		if (theEClass.eContainer() == modelPackage) {
			return doSwitch(theEClass.getClassifierID(), theEObject);
		}
		else {
			List<EClass> eSuperTypes = theEClass.getESuperTypes();
			return
				eSuperTypes.isEmpty() ?
					defaultCase(theEObject) :
					doSwitch(eSuperTypes.get(0), theEObject);
		}
	}

    /**
	 * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @return the first non-null result returned by a <code>caseXXX</code> call.
	 * @generated
	 */
    protected T doSwitch(int classifierID, EObject theEObject) {
		switch (classifierID) {
			case AnnotationsPackage.NAMED_OBJECT: {
				NamedObject namedObject = (NamedObject)theEObject;
				T result = caseNamedObject(namedObject);
				if (result == null) result = caseAnnotatable(namedObject);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case AnnotationsPackage.ANNOTATABLE: {
				Annotatable annotatable = (Annotatable)theEObject;
				T result = caseAnnotatable(annotatable);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case AnnotationsPackage.ANNOTATION: {
				Annotation annotation = (Annotation)theEObject;
				T result = caseAnnotation(annotation);
				if (result == null) result = caseNamedObject(annotation);
				if (result == null) result = caseAnnotatable(annotation);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case AnnotationsPackage.STRING_ANNOTATION: {
				StringAnnotation stringAnnotation = (StringAnnotation)theEObject;
				T result = caseStringAnnotation(stringAnnotation);
				if (result == null) result = caseAnnotation(stringAnnotation);
				if (result == null) result = caseNamedObject(stringAnnotation);
				if (result == null) result = caseAnnotatable(stringAnnotation);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case AnnotationsPackage.REFERENCE_ANNOTATION: {
				ReferenceAnnotation referenceAnnotation = (ReferenceAnnotation)theEObject;
				T result = caseReferenceAnnotation(referenceAnnotation);
				if (result == null) result = caseAnnotation(referenceAnnotation);
				if (result == null) result = caseNamedObject(referenceAnnotation);
				if (result == null) result = caseAnnotatable(referenceAnnotation);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case AnnotationsPackage.BOOLEAN_ANNOTATION: {
				BooleanAnnotation booleanAnnotation = (BooleanAnnotation)theEObject;
				T result = caseBooleanAnnotation(booleanAnnotation);
				if (result == null) result = caseAnnotation(booleanAnnotation);
				if (result == null) result = caseNamedObject(booleanAnnotation);
				if (result == null) result = caseAnnotatable(booleanAnnotation);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case AnnotationsPackage.INT_ANNOTATION: {
				IntAnnotation intAnnotation = (IntAnnotation)theEObject;
				T result = caseIntAnnotation(intAnnotation);
				if (result == null) result = caseAnnotation(intAnnotation);
				if (result == null) result = caseNamedObject(intAnnotation);
				if (result == null) result = caseAnnotatable(intAnnotation);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case AnnotationsPackage.FLOAT_ANNOTATION: {
				FloatAnnotation floatAnnotation = (FloatAnnotation)theEObject;
				T result = caseFloatAnnotation(floatAnnotation);
				if (result == null) result = caseAnnotation(floatAnnotation);
				if (result == null) result = caseNamedObject(floatAnnotation);
				if (result == null) result = caseAnnotatable(floatAnnotation);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case AnnotationsPackage.CONTAINMENT_ANNOTATION: {
				ContainmentAnnotation containmentAnnotation = (ContainmentAnnotation)theEObject;
				T result = caseContainmentAnnotation(containmentAnnotation);
				if (result == null) result = caseAnnotation(containmentAnnotation);
				if (result == null) result = caseNamedObject(containmentAnnotation);
				if (result == null) result = caseAnnotatable(containmentAnnotation);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case AnnotationsPackage.IMPORT_ANNOTATION: {
				ImportAnnotation importAnnotation = (ImportAnnotation)theEObject;
				T result = caseImportAnnotation(importAnnotation);
				if (result == null) result = caseAnnotation(importAnnotation);
				if (result == null) result = caseNamedObject(importAnnotation);
				if (result == null) result = caseAnnotatable(importAnnotation);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case AnnotationsPackage.TYPED_STRING_ANNOTATION: {
				TypedStringAnnotation typedStringAnnotation = (TypedStringAnnotation)theEObject;
				T result = caseTypedStringAnnotation(typedStringAnnotation);
				if (result == null) result = caseStringAnnotation(typedStringAnnotation);
				if (result == null) result = caseAnnotation(typedStringAnnotation);
				if (result == null) result = caseNamedObject(typedStringAnnotation);
				if (result == null) result = caseAnnotatable(typedStringAnnotation);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			default: return defaultCase(theEObject);
		}
	}

    /**
	 * Returns the result of interpreting the object as an instance of '<em>Named Object</em>'.
	 * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Named Object</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
    public T caseNamedObject(NamedObject object) {
		return null;
	}

    /**
	 * Returns the result of interpreting the object as an instance of '<em>Annotatable</em>'.
	 * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Annotatable</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
    public T caseAnnotatable(Annotatable object) {
		return null;
	}

    /**
	 * Returns the result of interpreting the object as an instance of '<em>Annotation</em>'.
	 * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Annotation</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
    public T caseAnnotation(Annotation object) {
		return null;
	}

    /**
	 * Returns the result of interpreting the object as an instance of '<em>String Annotation</em>'.
	 * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>String Annotation</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
    public T caseStringAnnotation(StringAnnotation object) {
		return null;
	}

    /**
	 * Returns the result of interpreting the object as an instance of '<em>Reference Annotation</em>'.
	 * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Reference Annotation</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
    public T caseReferenceAnnotation(ReferenceAnnotation object) {
		return null;
	}

    /**
	 * Returns the result of interpreting the object as an instance of '<em>Boolean Annotation</em>'.
	 * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Boolean Annotation</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
    public T caseBooleanAnnotation(BooleanAnnotation object) {
		return null;
	}

    /**
	 * Returns the result of interpreting the object as an instance of '<em>Int Annotation</em>'.
	 * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Int Annotation</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
    public T caseIntAnnotation(IntAnnotation object) {
		return null;
	}

    /**
	 * Returns the result of interpreting the object as an instance of '<em>Float Annotation</em>'.
	 * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Float Annotation</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
    public T caseFloatAnnotation(FloatAnnotation object) {
		return null;
	}

    /**
	 * Returns the result of interpreting the object as an instance of '<em>Containment Annotation</em>'.
	 * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Containment Annotation</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
    public T caseContainmentAnnotation(ContainmentAnnotation object) {
		return null;
	}

    /**
	 * Returns the result of interpreting the object as an instance of '<em>Import Annotation</em>'.
	 * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Import Annotation</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
    public T caseImportAnnotation(ImportAnnotation object) {
		return null;
	}

    /**
	 * Returns the result of interpreting the object as an instance of '<em>Typed String Annotation</em>'.
	 * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Typed String Annotation</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
    public T caseTypedStringAnnotation(TypedStringAnnotation object) {
		return null;
	}

    /**
	 * Returns the result of interpreting the object as an instance of '<em>EObject</em>'.
	 * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch, but this is the last case anyway.
     * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>EObject</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject)
	 * @generated
	 */
    public T defaultCase(EObject object) {
		return null;
	}

} //AnnotationsSwitch
