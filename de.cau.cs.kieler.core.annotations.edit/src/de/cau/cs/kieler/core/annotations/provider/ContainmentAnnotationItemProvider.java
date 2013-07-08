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
package de.cau.cs.kieler.core.annotations.provider;


import de.cau.cs.kieler.core.annotations.AnnotationsFactory;
import de.cau.cs.kieler.core.annotations.AnnotationsPackage;
import de.cau.cs.kieler.core.annotations.ContainmentAnnotation;

import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;

import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.edit.provider.IEditingDomainItemProvider;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.IItemPropertySource;
import org.eclipse.emf.edit.provider.IStructuredItemContentProvider;
import org.eclipse.emf.edit.provider.ITreeItemContentProvider;
import org.eclipse.emf.edit.provider.ViewerNotification;

/**
 * This is the item provider adapter for a {@link de.cau.cs.kieler.core.annotations.ContainmentAnnotation} object.
 * <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * @generated
 */
public class ContainmentAnnotationItemProvider
    extends AnnotationItemProvider
    implements
        IEditingDomainItemProvider,
        IStructuredItemContentProvider,
        ITreeItemContentProvider,
        IItemLabelProvider,
        IItemPropertySource {
    /**
	 * This constructs an instance from a factory and a notifier.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    public ContainmentAnnotationItemProvider(AdapterFactory adapterFactory) {
		super(adapterFactory);
	}

    /**
	 * This returns the property descriptors for the adapted class.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    @Override
    public List<IItemPropertyDescriptor> getPropertyDescriptors(Object object) {
		if (itemPropertyDescriptors == null) {
			super.getPropertyDescriptors(object);

		}
		return itemPropertyDescriptors;
	}

    /**
	 * This specifies how to implement {@link #getChildren} and is used to deduce an appropriate feature for an
	 * {@link org.eclipse.emf.edit.command.AddCommand}, {@link org.eclipse.emf.edit.command.RemoveCommand} or
	 * {@link org.eclipse.emf.edit.command.MoveCommand} in {@link #createCommand}.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    @Override
    public Collection<? extends EStructuralFeature> getChildrenFeatures(Object object) {
		if (childrenFeatures == null) {
			super.getChildrenFeatures(object);
			childrenFeatures.add(AnnotationsPackage.Literals.CONTAINMENT_ANNOTATION__OBJECT);
		}
		return childrenFeatures;
	}

    /**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    @Override
    protected EStructuralFeature getChildFeature(Object object, Object child) {
		// Check the type of the specified child object and return the proper feature to use for
		// adding (see {@link AddCommand}) it as a child.

		return super.getChildFeature(object, child);
	}

    /**
	 * This returns ContainmentAnnotation.gif.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    @Override
    public Object getImage(Object object) {
		return overlayImage(object, getResourceLocator().getImage("full/obj16/ContainmentAnnotation"));
	}

    /**
	 * This returns the label text for the adapted class.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    @Override
    public String getText(Object object) {
		String label = ((ContainmentAnnotation)object).getName();
		return label == null || label.length() == 0 ?
			getString("_UI_ContainmentAnnotation_type") :
			getString("_UI_ContainmentAnnotation_type") + " " + label;
	}

    /**
	 * This handles model notifications by calling {@link #updateChildren} to update any cached
	 * children and by creating a viewer notification, which it passes to {@link #fireNotifyChanged}.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    @Override
    public void notifyChanged(Notification notification) {
		updateChildren(notification);

		switch (notification.getFeatureID(ContainmentAnnotation.class)) {
			case AnnotationsPackage.CONTAINMENT_ANNOTATION__OBJECT:
				fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(), true, false));
				return;
		}
		super.notifyChanged(notification);
	}

    /**
	 * This adds {@link org.eclipse.emf.edit.command.CommandParameter}s describing the children
	 * that can be created under this object.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    @Override
    protected void collectNewChildDescriptors(Collection<Object> newChildDescriptors, Object object) {
		super.collectNewChildDescriptors(newChildDescriptors, object);

		newChildDescriptors.add
			(createChildParameter
				(AnnotationsPackage.Literals.CONTAINMENT_ANNOTATION__OBJECT,
				 AnnotationsFactory.eINSTANCE.createAnnotation()));

		newChildDescriptors.add
			(createChildParameter
				(AnnotationsPackage.Literals.CONTAINMENT_ANNOTATION__OBJECT,
				 AnnotationsFactory.eINSTANCE.createStringAnnotation()));

		newChildDescriptors.add
			(createChildParameter
				(AnnotationsPackage.Literals.CONTAINMENT_ANNOTATION__OBJECT,
				 AnnotationsFactory.eINSTANCE.createReferenceAnnotation()));

		newChildDescriptors.add
			(createChildParameter
				(AnnotationsPackage.Literals.CONTAINMENT_ANNOTATION__OBJECT,
				 AnnotationsFactory.eINSTANCE.createBooleanAnnotation()));

		newChildDescriptors.add
			(createChildParameter
				(AnnotationsPackage.Literals.CONTAINMENT_ANNOTATION__OBJECT,
				 AnnotationsFactory.eINSTANCE.createIntAnnotation()));

		newChildDescriptors.add
			(createChildParameter
				(AnnotationsPackage.Literals.CONTAINMENT_ANNOTATION__OBJECT,
				 AnnotationsFactory.eINSTANCE.createFloatAnnotation()));

		newChildDescriptors.add
			(createChildParameter
				(AnnotationsPackage.Literals.CONTAINMENT_ANNOTATION__OBJECT,
				 AnnotationsFactory.eINSTANCE.createContainmentAnnotation()));

		newChildDescriptors.add
			(createChildParameter
				(AnnotationsPackage.Literals.CONTAINMENT_ANNOTATION__OBJECT,
				 AnnotationsFactory.eINSTANCE.createImportAnnotation()));

		newChildDescriptors.add
			(createChildParameter
				(AnnotationsPackage.Literals.CONTAINMENT_ANNOTATION__OBJECT,
				 AnnotationsFactory.eINSTANCE.createTypedStringAnnotation()));

		newChildDescriptors.add
			(createChildParameter
				(AnnotationsPackage.Literals.CONTAINMENT_ANNOTATION__OBJECT,
				 EcoreFactory.eINSTANCE.createEAttribute()));

		newChildDescriptors.add
			(createChildParameter
				(AnnotationsPackage.Literals.CONTAINMENT_ANNOTATION__OBJECT,
				 EcoreFactory.eINSTANCE.createEAnnotation()));

		newChildDescriptors.add
			(createChildParameter
				(AnnotationsPackage.Literals.CONTAINMENT_ANNOTATION__OBJECT,
				 EcoreFactory.eINSTANCE.createEClass()));

		newChildDescriptors.add
			(createChildParameter
				(AnnotationsPackage.Literals.CONTAINMENT_ANNOTATION__OBJECT,
				 EcoreFactory.eINSTANCE.createEDataType()));

		newChildDescriptors.add
			(createChildParameter
				(AnnotationsPackage.Literals.CONTAINMENT_ANNOTATION__OBJECT,
				 EcoreFactory.eINSTANCE.createEEnum()));

		newChildDescriptors.add
			(createChildParameter
				(AnnotationsPackage.Literals.CONTAINMENT_ANNOTATION__OBJECT,
				 EcoreFactory.eINSTANCE.createEEnumLiteral()));

		newChildDescriptors.add
			(createChildParameter
				(AnnotationsPackage.Literals.CONTAINMENT_ANNOTATION__OBJECT,
				 EcoreFactory.eINSTANCE.createEFactory()));

		newChildDescriptors.add
			(createChildParameter
				(AnnotationsPackage.Literals.CONTAINMENT_ANNOTATION__OBJECT,
				 EcoreFactory.eINSTANCE.createEObject()));

		newChildDescriptors.add
			(createChildParameter
				(AnnotationsPackage.Literals.CONTAINMENT_ANNOTATION__OBJECT,
				 EcoreFactory.eINSTANCE.createEOperation()));

		newChildDescriptors.add
			(createChildParameter
				(AnnotationsPackage.Literals.CONTAINMENT_ANNOTATION__OBJECT,
				 EcoreFactory.eINSTANCE.createEPackage()));

		newChildDescriptors.add
			(createChildParameter
				(AnnotationsPackage.Literals.CONTAINMENT_ANNOTATION__OBJECT,
				 EcoreFactory.eINSTANCE.createEParameter()));

		newChildDescriptors.add
			(createChildParameter
				(AnnotationsPackage.Literals.CONTAINMENT_ANNOTATION__OBJECT,
				 EcoreFactory.eINSTANCE.createEReference()));

		newChildDescriptors.add
			(createChildParameter
				(AnnotationsPackage.Literals.CONTAINMENT_ANNOTATION__OBJECT,
				 EcoreFactory.eINSTANCE.create(EcorePackage.Literals.ESTRING_TO_STRING_MAP_ENTRY)));

		newChildDescriptors.add
			(createChildParameter
				(AnnotationsPackage.Literals.CONTAINMENT_ANNOTATION__OBJECT,
				 EcoreFactory.eINSTANCE.createEGenericType()));

		newChildDescriptors.add
			(createChildParameter
				(AnnotationsPackage.Literals.CONTAINMENT_ANNOTATION__OBJECT,
				 EcoreFactory.eINSTANCE.createETypeParameter()));
	}

				/**
	 * This returns the label text for {@link org.eclipse.emf.edit.command.CreateChildCommand}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getCreateChildText(Object owner, Object feature, Object child, Collection<?> selection) {
		Object childFeature = feature;
		Object childObject = child;

		boolean qualify =
			childFeature == AnnotationsPackage.Literals.ANNOTATABLE__ANNOTATIONS ||
			childFeature == AnnotationsPackage.Literals.CONTAINMENT_ANNOTATION__OBJECT;

		if (qualify) {
			return getString
				("_UI_CreateChild_text2",
				 new Object[] { getTypeText(childObject), getFeatureText(childFeature), getTypeText(owner) });
		}
		return super.getCreateChildText(owner, feature, child, selection);
	}

}
