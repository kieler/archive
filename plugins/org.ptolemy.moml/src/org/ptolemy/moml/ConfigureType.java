/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.ptolemy.moml;

import org.eclipse.emf.ecore.EObject;

import org.eclipse.emf.ecore.util.FeatureMap;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Configure Type</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.ptolemy.moml.ConfigureType#getMixed <em>Mixed</em>}</li>
 *   <li>{@link org.ptolemy.moml.ConfigureType#getSource <em>Source</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.ptolemy.moml.MomlPackage#getConfigureType()
 * @model extendedMetaData="name='configure_._type' kind='mixed'"
 * @generated
 */
public interface ConfigureType extends EObject {
    /**
	 * Returns the value of the '<em><b>Mixed</b></em>' attribute list.
	 * The list contents are of type {@link org.eclipse.emf.ecore.util.FeatureMap.Entry}.
	 * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Mixed</em>' attribute list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
	 * @return the value of the '<em>Mixed</em>' attribute list.
	 * @see org.ptolemy.moml.MomlPackage#getConfigureType_Mixed()
	 * @model unique="false" dataType="org.eclipse.emf.ecore.EFeatureMapEntry" many="true"
	 *        extendedMetaData="kind='elementWildcard' name=':mixed'"
	 * @generated
	 */
    FeatureMap getMixed();

    /**
	 * Returns the value of the '<em><b>Source</b></em>' attribute.
	 * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Source</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
	 * @return the value of the '<em>Source</em>' attribute.
	 * @see #setSource(String)
	 * @see org.ptolemy.moml.MomlPackage#getConfigureType_Source()
	 * @model extendedMetaData="kind='attribute' name='source' namespace='##targetNamespace'"
	 * @generated
	 */
    String getSource();

    /**
	 * Sets the value of the '{@link org.ptolemy.moml.ConfigureType#getSource <em>Source</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Source</em>' attribute.
	 * @see #getSource()
	 * @generated
	 */
	void setSource(String value);

} // ConfigureType
