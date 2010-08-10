/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.ptolemy.moml.Moml.util;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;

import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;

import org.eclipse.emf.ecore.EObject;

import org.ptolemy.moml.Moml.*;

/**
 * <!-- begin-user-doc -->
 * The <b>Adapter Factory</b> for the model.
 * It provides an adapter <code>createXXX</code> method for each class of the model.
 * <!-- end-user-doc -->
 * @see org.ptolemy.moml.Moml.MomlPackage
 * @generated
 */
public class MomlAdapterFactory extends AdapterFactoryImpl {
    /**
     * The cached model package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected static MomlPackage modelPackage;

    /**
     * Creates an instance of the adapter factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public MomlAdapterFactory() {
        if (modelPackage == null) {
            modelPackage = MomlPackage.eINSTANCE;
        }
    }

    /**
     * Returns whether this factory is applicable for the type of the object.
     * <!-- begin-user-doc -->
     * This implementation returns <code>true</code> if the object is either the model's package or is an instance object of the model.
     * <!-- end-user-doc -->
     * @return whether this factory is applicable for the type of the object.
     * @generated
     */
    @Override
    public boolean isFactoryForType(Object object) {
        if (object == modelPackage) {
            return true;
        }
        if (object instanceof EObject) {
            return ((EObject)object).eClass().getEPackage() == modelPackage;
        }
        return false;
    }

    /**
     * The switch that delegates to the <code>createXXX</code> methods.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected MomlSwitch<Adapter> modelSwitch =
        new MomlSwitch<Adapter>() {
            @Override
            public Adapter caseAny(Any object) {
                return createAnyAdapter();
            }
            @Override
            public Adapter caseClassType(ClassType object) {
                return createClassTypeAdapter();
            }
            @Override
            public Adapter caseConfigureType(ConfigureType object) {
                return createConfigureTypeAdapter();
            }
            @Override
            public Adapter caseDeleteEntityType(DeleteEntityType object) {
                return createDeleteEntityTypeAdapter();
            }
            @Override
            public Adapter caseDeletePortType(DeletePortType object) {
                return createDeletePortTypeAdapter();
            }
            @Override
            public Adapter caseDeletePropertyType(DeletePropertyType object) {
                return createDeletePropertyTypeAdapter();
            }
            @Override
            public Adapter caseDeleteRelationType(DeleteRelationType object) {
                return createDeleteRelationTypeAdapter();
            }
            @Override
            public Adapter caseDirectorType(DirectorType object) {
                return createDirectorTypeAdapter();
            }
            @Override
            public Adapter caseDocType(DocType object) {
                return createDocTypeAdapter();
            }
            @Override
            public Adapter caseDocumentRoot(DocumentRoot object) {
                return createDocumentRootAdapter();
            }
            @Override
            public Adapter caseEntityType(EntityType object) {
                return createEntityTypeAdapter();
            }
            @Override
            public Adapter caseGroupType(GroupType object) {
                return createGroupTypeAdapter();
            }
            @Override
            public Adapter caseImportType(ImportType object) {
                return createImportTypeAdapter();
            }
            @Override
            public Adapter caseInputType(InputType object) {
                return createInputTypeAdapter();
            }
            @Override
            public Adapter caseLinkType(LinkType object) {
                return createLinkTypeAdapter();
            }
            @Override
            public Adapter caseLocationType(LocationType object) {
                return createLocationTypeAdapter();
            }
            @Override
            public Adapter caseModelType(ModelType object) {
                return createModelTypeAdapter();
            }
            @Override
            public Adapter casePortType(PortType object) {
                return createPortTypeAdapter();
            }
            @Override
            public Adapter casePropertyType(PropertyType object) {
                return createPropertyTypeAdapter();
            }
            @Override
            public Adapter caseRelationType(RelationType object) {
                return createRelationTypeAdapter();
            }
            @Override
            public Adapter caseRenameType(RenameType object) {
                return createRenameTypeAdapter();
            }
            @Override
            public Adapter caseRenditionType(RenditionType object) {
                return createRenditionTypeAdapter();
            }
            @Override
            public Adapter caseUnlinkType(UnlinkType object) {
                return createUnlinkTypeAdapter();
            }
            @Override
            public Adapter caseVertexType(VertexType object) {
                return createVertexTypeAdapter();
            }
            @Override
            public Adapter defaultCase(EObject object) {
                return createEObjectAdapter();
            }
        };

    /**
     * Creates an adapter for the <code>target</code>.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param target the object to adapt.
     * @return the adapter for the <code>target</code>.
     * @generated
     */
    @Override
    public Adapter createAdapter(Notifier target) {
        return modelSwitch.doSwitch((EObject)target);
    }


    /**
     * Creates a new adapter for an object of class '{@link org.ptolemy.moml.Moml.Any <em>Any</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.ptolemy.moml.Moml.Any
     * @generated
     */
    public Adapter createAnyAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.ptolemy.moml.Moml.ClassType <em>Class Type</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.ptolemy.moml.Moml.ClassType
     * @generated
     */
    public Adapter createClassTypeAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.ptolemy.moml.Moml.ConfigureType <em>Configure Type</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.ptolemy.moml.Moml.ConfigureType
     * @generated
     */
    public Adapter createConfigureTypeAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.ptolemy.moml.Moml.DeleteEntityType <em>Delete Entity Type</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.ptolemy.moml.Moml.DeleteEntityType
     * @generated
     */
    public Adapter createDeleteEntityTypeAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.ptolemy.moml.Moml.DeletePortType <em>Delete Port Type</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.ptolemy.moml.Moml.DeletePortType
     * @generated
     */
    public Adapter createDeletePortTypeAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.ptolemy.moml.Moml.DeletePropertyType <em>Delete Property Type</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.ptolemy.moml.Moml.DeletePropertyType
     * @generated
     */
    public Adapter createDeletePropertyTypeAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.ptolemy.moml.Moml.DeleteRelationType <em>Delete Relation Type</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.ptolemy.moml.Moml.DeleteRelationType
     * @generated
     */
    public Adapter createDeleteRelationTypeAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.ptolemy.moml.Moml.DirectorType <em>Director Type</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.ptolemy.moml.Moml.DirectorType
     * @generated
     */
    public Adapter createDirectorTypeAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.ptolemy.moml.Moml.DocType <em>Doc Type</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.ptolemy.moml.Moml.DocType
     * @generated
     */
    public Adapter createDocTypeAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.ptolemy.moml.Moml.DocumentRoot <em>Document Root</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.ptolemy.moml.Moml.DocumentRoot
     * @generated
     */
    public Adapter createDocumentRootAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.ptolemy.moml.Moml.EntityType <em>Entity Type</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.ptolemy.moml.Moml.EntityType
     * @generated
     */
    public Adapter createEntityTypeAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.ptolemy.moml.Moml.GroupType <em>Group Type</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.ptolemy.moml.Moml.GroupType
     * @generated
     */
    public Adapter createGroupTypeAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.ptolemy.moml.Moml.ImportType <em>Import Type</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.ptolemy.moml.Moml.ImportType
     * @generated
     */
    public Adapter createImportTypeAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.ptolemy.moml.Moml.InputType <em>Input Type</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.ptolemy.moml.Moml.InputType
     * @generated
     */
    public Adapter createInputTypeAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.ptolemy.moml.Moml.LinkType <em>Link Type</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.ptolemy.moml.Moml.LinkType
     * @generated
     */
    public Adapter createLinkTypeAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.ptolemy.moml.Moml.LocationType <em>Location Type</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.ptolemy.moml.Moml.LocationType
     * @generated
     */
    public Adapter createLocationTypeAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.ptolemy.moml.Moml.ModelType <em>Model Type</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.ptolemy.moml.Moml.ModelType
     * @generated
     */
    public Adapter createModelTypeAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.ptolemy.moml.Moml.PortType <em>Port Type</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.ptolemy.moml.Moml.PortType
     * @generated
     */
    public Adapter createPortTypeAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.ptolemy.moml.Moml.PropertyType <em>Property Type</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.ptolemy.moml.Moml.PropertyType
     * @generated
     */
    public Adapter createPropertyTypeAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.ptolemy.moml.Moml.RelationType <em>Relation Type</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.ptolemy.moml.Moml.RelationType
     * @generated
     */
    public Adapter createRelationTypeAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.ptolemy.moml.Moml.RenameType <em>Rename Type</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.ptolemy.moml.Moml.RenameType
     * @generated
     */
    public Adapter createRenameTypeAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.ptolemy.moml.Moml.RenditionType <em>Rendition Type</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.ptolemy.moml.Moml.RenditionType
     * @generated
     */
    public Adapter createRenditionTypeAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.ptolemy.moml.Moml.UnlinkType <em>Unlink Type</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.ptolemy.moml.Moml.UnlinkType
     * @generated
     */
    public Adapter createUnlinkTypeAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.ptolemy.moml.Moml.VertexType <em>Vertex Type</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.ptolemy.moml.Moml.VertexType
     * @generated
     */
    public Adapter createVertexTypeAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for the default case.
     * <!-- begin-user-doc -->
     * This default implementation returns null.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @generated
     */
    public Adapter createEObjectAdapter() {
        return null;
    }

} //MomlAdapterFactory
