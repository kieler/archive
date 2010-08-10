/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.ptolemy.moml.Moml;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Import Type</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.ptolemy.moml.Moml.ImportType#getBase <em>Base</em>}</li>
 *   <li>{@link org.ptolemy.moml.Moml.ImportType#getSource <em>Source</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.ptolemy.moml.Moml.MomlPackage#getImportType()
 * @model extendedMetaData="name='import_._type' kind='empty'"
 * @generated
 */
public interface ImportType extends EObject {
    /**
     * Returns the value of the '<em><b>Base</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Base</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Base</em>' attribute.
     * @see #setBase(String)
     * @see org.ptolemy.moml.Moml.MomlPackage#getImportType_Base()
     * @model extendedMetaData="kind='attribute' name='base' namespace='##targetNamespace'"
     * @generated
     */
    String getBase();

    /**
     * Sets the value of the '{@link org.ptolemy.moml.Moml.ImportType#getBase <em>Base</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Base</em>' attribute.
     * @see #getBase()
     * @generated
     */
    void setBase(String value);

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
     * @see org.ptolemy.moml.Moml.MomlPackage#getImportType_Source()
     * @model required="true"
     *        extendedMetaData="kind='attribute' name='source' namespace='##targetNamespace'"
     * @generated
     */
    String getSource();

    /**
     * Sets the value of the '{@link org.ptolemy.moml.Moml.ImportType#getSource <em>Source</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Source</em>' attribute.
     * @see #getSource()
     * @generated
     */
    void setSource(String value);

} // ImportType
