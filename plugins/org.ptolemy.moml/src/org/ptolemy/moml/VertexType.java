/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.ptolemy.moml;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

import org.eclipse.emf.ecore.util.FeatureMap;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Vertex Type</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.ptolemy.moml.VertexType#getGroup <em>Group</em>}</li>
 *   <li>{@link org.ptolemy.moml.VertexType#getConfigure <em>Configure</em>}</li>
 *   <li>{@link org.ptolemy.moml.VertexType#getDoc <em>Doc</em>}</li>
 *   <li>{@link org.ptolemy.moml.VertexType#getLocation <em>Location</em>}</li>
 *   <li>{@link org.ptolemy.moml.VertexType#getProperty <em>Property</em>}</li>
 *   <li>{@link org.ptolemy.moml.VertexType#getRename <em>Rename</em>}</li>
 *   <li>{@link org.ptolemy.moml.VertexType#getName <em>Name</em>}</li>
 *   <li>{@link org.ptolemy.moml.VertexType#getPathTo <em>Path To</em>}</li>
 *   <li>{@link org.ptolemy.moml.VertexType#getValue <em>Value</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.ptolemy.moml.MomlPackage#getVertexType()
 * @model extendedMetaData="name='vertex_._type' kind='elementOnly'"
 * @generated
 */
public interface VertexType extends EObject {
    /**
     * Returns the value of the '<em><b>Group</b></em>' attribute list.
     * The list contents are of type {@link org.eclipse.emf.ecore.util.FeatureMap.Entry}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Group</em>' attribute list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Group</em>' attribute list.
     * @see org.ptolemy.moml.MomlPackage#getVertexType_Group()
     * @model unique="false" dataType="org.eclipse.emf.ecore.EFeatureMapEntry" many="true"
     *        extendedMetaData="kind='group' name='group:0'"
     * @generated
     */
    FeatureMap getGroup();

    /**
     * Returns the value of the '<em><b>Configure</b></em>' containment reference list.
     * The list contents are of type {@link org.ptolemy.moml.ConfigureType}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Configure</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Configure</em>' containment reference list.
     * @see org.ptolemy.moml.MomlPackage#getVertexType_Configure()
     * @model containment="true" transient="true" volatile="true" derived="true"
     *        extendedMetaData="kind='element' name='configure' namespace='##targetNamespace' group='group:0'"
     * @generated
     */
    EList<ConfigureType> getConfigure();

    /**
     * Returns the value of the '<em><b>Doc</b></em>' containment reference list.
     * The list contents are of type {@link org.ptolemy.moml.DocType}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Doc</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Doc</em>' containment reference list.
     * @see org.ptolemy.moml.MomlPackage#getVertexType_Doc()
     * @model containment="true" transient="true" volatile="true" derived="true"
     *        extendedMetaData="kind='element' name='doc' namespace='##targetNamespace' group='group:0'"
     * @generated
     */
    EList<DocType> getDoc();

    /**
     * Returns the value of the '<em><b>Location</b></em>' containment reference list.
     * The list contents are of type {@link org.ptolemy.moml.LocationType}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Location</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Location</em>' containment reference list.
     * @see org.ptolemy.moml.MomlPackage#getVertexType_Location()
     * @model containment="true" transient="true" volatile="true" derived="true"
     *        extendedMetaData="kind='element' name='location' namespace='##targetNamespace' group='group:0'"
     * @generated
     */
    EList<LocationType> getLocation();

    /**
     * Returns the value of the '<em><b>Property</b></em>' containment reference list.
     * The list contents are of type {@link org.ptolemy.moml.PropertyType}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Property</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Property</em>' containment reference list.
     * @see org.ptolemy.moml.MomlPackage#getVertexType_Property()
     * @model containment="true" transient="true" volatile="true" derived="true"
     *        extendedMetaData="kind='element' name='property' namespace='##targetNamespace' group='group:0'"
     * @generated
     */
    EList<PropertyType> getProperty();

    /**
     * Returns the value of the '<em><b>Rename</b></em>' containment reference list.
     * The list contents are of type {@link org.ptolemy.moml.RenameType}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Rename</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Rename</em>' containment reference list.
     * @see org.ptolemy.moml.MomlPackage#getVertexType_Rename()
     * @model containment="true" transient="true" volatile="true" derived="true"
     *        extendedMetaData="kind='element' name='rename' namespace='##targetNamespace' group='group:0'"
     * @generated
     */
    EList<RenameType> getRename();

    /**
     * Returns the value of the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Name</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Name</em>' attribute.
     * @see #setName(String)
     * @see org.ptolemy.moml.MomlPackage#getVertexType_Name()
     * @model required="true"
     *        extendedMetaData="kind='attribute' name='name' namespace='##targetNamespace'"
     * @generated
     */
    String getName();

    /**
     * Sets the value of the '{@link org.ptolemy.moml.VertexType#getName <em>Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Name</em>' attribute.
     * @see #getName()
     * @generated
     */
    void setName(String value);

    /**
     * Returns the value of the '<em><b>Path To</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Path To</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Path To</em>' attribute.
     * @see #setPathTo(String)
     * @see org.ptolemy.moml.MomlPackage#getVertexType_PathTo()
     * @model extendedMetaData="kind='attribute' name='pathTo' namespace='##targetNamespace'"
     * @generated
     */
    String getPathTo();

    /**
     * Sets the value of the '{@link org.ptolemy.moml.VertexType#getPathTo <em>Path To</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Path To</em>' attribute.
     * @see #getPathTo()
     * @generated
     */
    void setPathTo(String value);

    /**
     * Returns the value of the '<em><b>Value</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Value</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Value</em>' attribute.
     * @see #setValue(String)
     * @see org.ptolemy.moml.MomlPackage#getVertexType_Value()
     * @model extendedMetaData="kind='attribute' name='value' namespace='##targetNamespace'"
     * @generated
     */
    String getValue();

    /**
     * Sets the value of the '{@link org.ptolemy.moml.VertexType#getValue <em>Value</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Value</em>' attribute.
     * @see #getValue()
     * @generated
     */
    void setValue(String value);

} // VertexType
