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
 * A representation of the model object '<em><b>Class Type</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.ptolemy.moml.ClassType#getGroup <em>Group</em>}</li>
 *   <li>{@link org.ptolemy.moml.ClassType#getClass_ <em>Class</em>}</li>
 *   <li>{@link org.ptolemy.moml.ClassType#getConfigure <em>Configure</em>}</li>
 *   <li>{@link org.ptolemy.moml.ClassType#getDeleteEntity <em>Delete Entity</em>}</li>
 *   <li>{@link org.ptolemy.moml.ClassType#getDeletePort <em>Delete Port</em>}</li>
 *   <li>{@link org.ptolemy.moml.ClassType#getDeleteRelation <em>Delete Relation</em>}</li>
 *   <li>{@link org.ptolemy.moml.ClassType#getDirector <em>Director</em>}</li>
 *   <li>{@link org.ptolemy.moml.ClassType#getDisplay <em>Display</em>}</li>
 *   <li>{@link org.ptolemy.moml.ClassType#getDoc <em>Doc</em>}</li>
 *   <li>{@link org.ptolemy.moml.ClassType#getEntity <em>Entity</em>}</li>
 *   <li>{@link org.ptolemy.moml.ClassType#getGroup1 <em>Group1</em>}</li>
 *   <li>{@link org.ptolemy.moml.ClassType#getImport <em>Import</em>}</li>
 *   <li>{@link org.ptolemy.moml.ClassType#getInput <em>Input</em>}</li>
 *   <li>{@link org.ptolemy.moml.ClassType#getLink <em>Link</em>}</li>
 *   <li>{@link org.ptolemy.moml.ClassType#getPort <em>Port</em>}</li>
 *   <li>{@link org.ptolemy.moml.ClassType#getProperty <em>Property</em>}</li>
 *   <li>{@link org.ptolemy.moml.ClassType#getRelation <em>Relation</em>}</li>
 *   <li>{@link org.ptolemy.moml.ClassType#getRename <em>Rename</em>}</li>
 *   <li>{@link org.ptolemy.moml.ClassType#getRendition <em>Rendition</em>}</li>
 *   <li>{@link org.ptolemy.moml.ClassType#getUnlink <em>Unlink</em>}</li>
 *   <li>{@link org.ptolemy.moml.ClassType#getExtends <em>Extends</em>}</li>
 *   <li>{@link org.ptolemy.moml.ClassType#getName <em>Name</em>}</li>
 *   <li>{@link org.ptolemy.moml.ClassType#getSource <em>Source</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.ptolemy.moml.MomlPackage#getClassType()
 * @model extendedMetaData="name='class_._type' kind='elementOnly'"
 * @generated
 */
public interface ClassType extends EObject {
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
	 * @see org.ptolemy.moml.MomlPackage#getClassType_Group()
	 * @model unique="false" dataType="org.eclipse.emf.ecore.EFeatureMapEntry" many="true"
	 *        extendedMetaData="kind='group' name='group:0'"
	 * @generated
	 */
    FeatureMap getGroup();

    /**
	 * Returns the value of the '<em><b>Class</b></em>' containment reference list.
	 * The list contents are of type {@link org.ptolemy.moml.ClassType}.
	 * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Class</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
	 * @return the value of the '<em>Class</em>' containment reference list.
	 * @see org.ptolemy.moml.MomlPackage#getClassType_Class()
	 * @model containment="true" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='class' namespace='##targetNamespace' group='group:0'"
	 * @generated
	 */
    EList<ClassType> getClass_();

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
	 * @see org.ptolemy.moml.MomlPackage#getClassType_Configure()
	 * @model containment="true" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='configure' namespace='##targetNamespace' group='group:0'"
	 * @generated
	 */
    EList<ConfigureType> getConfigure();

    /**
	 * Returns the value of the '<em><b>Delete Entity</b></em>' containment reference list.
	 * The list contents are of type {@link org.ptolemy.moml.DeleteEntityType}.
	 * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Delete Entity</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
	 * @return the value of the '<em>Delete Entity</em>' containment reference list.
	 * @see org.ptolemy.moml.MomlPackage#getClassType_DeleteEntity()
	 * @model containment="true" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='deleteEntity' namespace='##targetNamespace' group='group:0'"
	 * @generated
	 */
    EList<DeleteEntityType> getDeleteEntity();

    /**
	 * Returns the value of the '<em><b>Delete Port</b></em>' containment reference list.
	 * The list contents are of type {@link org.ptolemy.moml.DeletePortType}.
	 * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Delete Port</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
	 * @return the value of the '<em>Delete Port</em>' containment reference list.
	 * @see org.ptolemy.moml.MomlPackage#getClassType_DeletePort()
	 * @model containment="true" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='deletePort' namespace='##targetNamespace' group='group:0'"
	 * @generated
	 */
    EList<DeletePortType> getDeletePort();

    /**
	 * Returns the value of the '<em><b>Delete Relation</b></em>' containment reference list.
	 * The list contents are of type {@link org.ptolemy.moml.DeleteRelationType}.
	 * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Delete Relation</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
	 * @return the value of the '<em>Delete Relation</em>' containment reference list.
	 * @see org.ptolemy.moml.MomlPackage#getClassType_DeleteRelation()
	 * @model containment="true" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='deleteRelation' namespace='##targetNamespace' group='group:0'"
	 * @generated
	 */
    EList<DeleteRelationType> getDeleteRelation();

    /**
	 * Returns the value of the '<em><b>Director</b></em>' containment reference list.
	 * The list contents are of type {@link org.ptolemy.moml.DirectorType}.
	 * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Director</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
	 * @return the value of the '<em>Director</em>' containment reference list.
	 * @see org.ptolemy.moml.MomlPackage#getClassType_Director()
	 * @model containment="true" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='director' namespace='##targetNamespace' group='group:0'"
	 * @generated
	 */
    EList<DirectorType> getDirector();

    /**
	 * Returns the value of the '<em><b>Display</b></em>' containment reference list.
	 * The list contents are of type {@link org.ptolemy.moml.DisplayType}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Display</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Display</em>' containment reference list.
	 * @see org.ptolemy.moml.MomlPackage#getClassType_Display()
	 * @model containment="true" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='display' namespace='##targetNamespace' group='group:0'"
	 * @generated
	 */
	EList<DisplayType> getDisplay();

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
	 * @see org.ptolemy.moml.MomlPackage#getClassType_Doc()
	 * @model containment="true" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='doc' namespace='##targetNamespace' group='group:0'"
	 * @generated
	 */
    EList<DocType> getDoc();

    /**
	 * Returns the value of the '<em><b>Entity</b></em>' containment reference list.
	 * The list contents are of type {@link org.ptolemy.moml.EntityType}.
	 * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Entity</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
	 * @return the value of the '<em>Entity</em>' containment reference list.
	 * @see org.ptolemy.moml.MomlPackage#getClassType_Entity()
	 * @model containment="true" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='entity' namespace='##targetNamespace' group='group:0'"
	 * @generated
	 */
    EList<EntityType> getEntity();

    /**
	 * Returns the value of the '<em><b>Group1</b></em>' containment reference list.
	 * The list contents are of type {@link org.ptolemy.moml.GroupType}.
	 * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Group1</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
	 * @return the value of the '<em>Group1</em>' containment reference list.
	 * @see org.ptolemy.moml.MomlPackage#getClassType_Group1()
	 * @model containment="true" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='group' namespace='##targetNamespace' group='group:0'"
	 * @generated
	 */
    EList<GroupType> getGroup1();

    /**
	 * Returns the value of the '<em><b>Import</b></em>' containment reference list.
	 * The list contents are of type {@link org.ptolemy.moml.ImportType}.
	 * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Import</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
	 * @return the value of the '<em>Import</em>' containment reference list.
	 * @see org.ptolemy.moml.MomlPackage#getClassType_Import()
	 * @model containment="true" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='import' namespace='##targetNamespace' group='group:0'"
	 * @generated
	 */
    EList<ImportType> getImport();

    /**
	 * Returns the value of the '<em><b>Input</b></em>' containment reference list.
	 * The list contents are of type {@link org.ptolemy.moml.InputType}.
	 * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Input</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
	 * @return the value of the '<em>Input</em>' containment reference list.
	 * @see org.ptolemy.moml.MomlPackage#getClassType_Input()
	 * @model containment="true" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='input' namespace='##targetNamespace' group='group:0'"
	 * @generated
	 */
    EList<InputType> getInput();

    /**
	 * Returns the value of the '<em><b>Link</b></em>' containment reference list.
	 * The list contents are of type {@link org.ptolemy.moml.LinkType}.
	 * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Link</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
	 * @return the value of the '<em>Link</em>' containment reference list.
	 * @see org.ptolemy.moml.MomlPackage#getClassType_Link()
	 * @model containment="true" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='link' namespace='##targetNamespace' group='group:0'"
	 * @generated
	 */
    EList<LinkType> getLink();

    /**
	 * Returns the value of the '<em><b>Port</b></em>' containment reference list.
	 * The list contents are of type {@link org.ptolemy.moml.PortType}.
	 * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Port</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
	 * @return the value of the '<em>Port</em>' containment reference list.
	 * @see org.ptolemy.moml.MomlPackage#getClassType_Port()
	 * @model containment="true" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='port' namespace='##targetNamespace' group='group:0'"
	 * @generated
	 */
    EList<PortType> getPort();

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
	 * @see org.ptolemy.moml.MomlPackage#getClassType_Property()
	 * @model containment="true" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='property' namespace='##targetNamespace' group='group:0'"
	 * @generated
	 */
    EList<PropertyType> getProperty();

    /**
	 * Returns the value of the '<em><b>Relation</b></em>' containment reference list.
	 * The list contents are of type {@link org.ptolemy.moml.RelationType}.
	 * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Relation</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
	 * @return the value of the '<em>Relation</em>' containment reference list.
	 * @see org.ptolemy.moml.MomlPackage#getClassType_Relation()
	 * @model containment="true" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='relation' namespace='##targetNamespace' group='group:0'"
	 * @generated
	 */
    EList<RelationType> getRelation();

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
	 * @see org.ptolemy.moml.MomlPackage#getClassType_Rename()
	 * @model containment="true" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='rename' namespace='##targetNamespace' group='group:0'"
	 * @generated
	 */
    EList<RenameType> getRename();

    /**
	 * Returns the value of the '<em><b>Rendition</b></em>' containment reference list.
	 * The list contents are of type {@link org.ptolemy.moml.RenditionType}.
	 * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Rendition</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
	 * @return the value of the '<em>Rendition</em>' containment reference list.
	 * @see org.ptolemy.moml.MomlPackage#getClassType_Rendition()
	 * @model containment="true" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='rendition' namespace='##targetNamespace' group='group:0'"
	 * @generated
	 */
    EList<RenditionType> getRendition();

    /**
	 * Returns the value of the '<em><b>Unlink</b></em>' containment reference list.
	 * The list contents are of type {@link org.ptolemy.moml.UnlinkType}.
	 * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Unlink</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
	 * @return the value of the '<em>Unlink</em>' containment reference list.
	 * @see org.ptolemy.moml.MomlPackage#getClassType_Unlink()
	 * @model containment="true" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='unlink' namespace='##targetNamespace' group='group:0'"
	 * @generated
	 */
    EList<UnlinkType> getUnlink();

    /**
	 * Returns the value of the '<em><b>Extends</b></em>' attribute.
	 * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Extends</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
	 * @return the value of the '<em>Extends</em>' attribute.
	 * @see #setExtends(String)
	 * @see org.ptolemy.moml.MomlPackage#getClassType_Extends()
	 * @model extendedMetaData="kind='attribute' name='extends' namespace='##targetNamespace'"
	 * @generated
	 */
    String getExtends();

    /**
	 * Sets the value of the '{@link org.ptolemy.moml.ClassType#getExtends <em>Extends</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Extends</em>' attribute.
	 * @see #getExtends()
	 * @generated
	 */
	void setExtends(String value);

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
	 * @see org.ptolemy.moml.MomlPackage#getClassType_Name()
	 * @model required="true"
	 *        extendedMetaData="kind='attribute' name='name' namespace='##targetNamespace'"
	 * @generated
	 */
    String getName();

    /**
	 * Sets the value of the '{@link org.ptolemy.moml.ClassType#getName <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	void setName(String value);

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
	 * @see org.ptolemy.moml.MomlPackage#getClassType_Source()
	 * @model extendedMetaData="kind='attribute' name='source' namespace='##targetNamespace'"
	 * @generated
	 */
    String getSource();

    /**
	 * Sets the value of the '{@link org.ptolemy.moml.ClassType#getSource <em>Source</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Source</em>' attribute.
	 * @see #getSource()
	 * @generated
	 */
	void setSource(String value);

} // ClassType
