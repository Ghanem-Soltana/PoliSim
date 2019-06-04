
package adapters.umlImpl;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EParameter;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.BasicExtendedMetaData.EPackageExtendedMetaData;
import org.eclipse.emf.ecore.util.BasicExtendedMetaData.EPackageExtendedMetaData.Holder;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.util.UMLUtil;
import org.eclipse.uml2.uml.util.UMLUtil.UML2EcoreConverter;

import adapters.EPackageAdapter;


public class EPackageUMLAdapter extends EPackageAdapter<Package>  {

	protected Resource owningResource;
	public EPackage pck;
	public EPackage getPck() {
		return pck;
	}

	public void setPck(EPackage pck) {
		this.pck = pck;
	}

	public EPackageUMLAdapter(Package newPackage, Resource owningResource) {
		super(newPackage);
		this.owningResource =owningResource;
		
		Map<String, String> options = new HashMap<String, String>();
		options.put(UML2EcoreConverter.OPTION__SUPER_CLASS_ORDER, UMLUtil.OPTION__IGNORE);
		options.put(UML2EcoreConverter.OPTION__CAMEL_CASE_NAMES, UMLUtil.OPTION__IGNORE);
		options.put(UML2EcoreConverter.OPTION__COMMENTS, UMLUtil.OPTION__IGNORE);
		options.put(UML2EcoreConverter.OPTION__ANNOTATION_DETAILS, UMLUtil.OPTION__PROCESS);
		options.put(UML2EcoreConverter.OPTION__DERIVED_FEATURES, UMLUtil.OPTION__PROCESS);
		options.put(UML2EcoreConverter.OPTION__DUPLICATE_FEATURE_INHERITANCE, UMLUtil.OPTION__PROCESS);
		options.put(UML2EcoreConverter.OPTION__DUPLICATE_OPERATION_INHERITANCE, UMLUtil.OPTION__PROCESS);
		options.put(UML2EcoreConverter.OPTION__ECORE_TAGGED_VALUES, UMLUtil.OPTION__PROCESS);
		options.put(UML2EcoreConverter.OPTION__INVARIANT_CONSTRAINTS, UMLUtil.OPTION__IGNORE);
		options.put(UML2EcoreConverter.OPTION__INVOCATION_DELEGATES, UMLUtil.OPTION__PROCESS);
		options.put(UML2EcoreConverter.OPTION__LINE_SEPARATOR, UMLUtil.OPTION__PROCESS);
		options.put(UML2EcoreConverter.OPTION__VALIDATION_DELEGATES, UMLUtil.OPTION__PROCESS);
		options.put(UML2EcoreConverter.OPTION__UNTYPED_PROPERTIES, UMLUtil.OPTION__IGNORE);
		options.put(UML2EcoreConverter.OPTION__UNION_PROPERTIES, UMLUtil.OPTION__PROCESS);
		options.put(UML2EcoreConverter.OPTION__SUBSETTING_PROPERTIES, UMLUtil.OPTION__PROCESS);
		options.put(UML2EcoreConverter.OPTION__REDEFINING_PROPERTIES, UMLUtil.OPTION__PROCESS);
		options.put(UML2EcoreConverter.OPTION__REDEFINING_OPERATIONS, UMLUtil.OPTION__PROCESS);
		options.put(UML2EcoreConverter.OPTION__PROPERTY_DEFAULT_EXPRESSIONS, UMLUtil.OPTION__PROCESS);
		options.put(UML2EcoreConverter.OPTION__OPPOSITE_ROLE_NAMES, UMLUtil.OPTION__PROCESS);
		options.put(UML2EcoreConverter.OPTION__OPERATION_BODIES, UMLUtil.OPTION__PROCESS);
		options.put(UML2EcoreConverter.OPTION__UNTYPED_PROPERTIES, UMLUtil.OPTION__PROCESS);
		options.put(UML2EcoreConverter.OPTION__NON_API_INVARIANTS, UMLUtil.OPTION__PROCESS);
		options.put(UML2EcoreConverter.OPTION__DUPLICATE_OPERATIONS, UMLUtil.OPTION__PROCESS);
		options.put(UML2EcoreConverter.OPTION__DUPLICATE_FEATURES, UMLUtil.OPTION__PROCESS);
		Collection<EPackage> p =UMLUtil.convertToEcore(origPackage, options);
		Iterator<EPackage> x = p.iterator();
	    pck = x.next();
	    EList<EClassifier> liste = pck.getEClassifiers();
	    for (EClassifier eClassifier : liste) {

	    	if(eClassifier instanceof EClass)
	    	{
	    	EList<EAttribute> atts = ((EClass) eClassifier).getEAllAttributes();
	    	for (EAttribute eAttribute : atts) {
	    		if(eAttribute.getEType()!=null)
					if((eAttribute.getEType()  instanceof EDataType) && !(eAttribute.getEType() instanceof EClass) && ! (eAttribute.getEType() instanceof EEnum))
				eAttribute.setEType((EDatatypeUtil.convertFromType(eAttribute.getEType())));
			}
	    	EList<EOperation> operations = ((EClass)eClassifier).getEAllOperations();
			for (EOperation eOperation : operations) {
				if(eOperation.getEType()!=null)
					if((eOperation.getEType()  instanceof EDataType) && !(eOperation.getEType() instanceof EClass) && ! (eOperation.getEType() instanceof EEnum))
				eOperation.setEType((EDatatypeUtil.convertFromType(eOperation.getEType())));
				
				
				EList<EParameter> params = eOperation.getEParameters();
				for (EParameter eParameter : params) {
					 if(eParameter.getEType()!=null)
							if((eParameter.getEType()  instanceof EDataType) && !(eParameter.getEType() instanceof EClass) && ! (eParameter.getEType() instanceof EEnum))
						 eParameter.setEType((EDatatypeUtil.convertFromType(eParameter.getEType())));
				}
			
			}
	    	}
		}
	   
	  //  System.exit(0);
		
		
	}
	
	

	@Override
	public String getName() {
		return pck.getName();
	}

	@Override
	public EList<EAnnotation> getEAnnotations() {
		
		return pck.getEAnnotations();
	}

	public EAnnotation getEAnnotation(String source) {
		
		return pck.getEAnnotation(source);
	}

	@Override
	public Resource eResource() {
		return owningResource;
	}

	@Override
	public String getNsPrefix() {
		return pck.getNsPrefix();
	}

	@Override
	public EList<EClassifier> getEClassifiers() {
			return pck.getEClassifiers();
	}

	@Override
	public EList<EPackage> getESubpackages() {
		return pck.getESubpackages();
	}
	@Override
	public EPackage getESuperPackage() {
		return pck.getESuperPackage();
	}

	@Override
	public String getNsURI() {
		return pck.getNsURI();
	}

	@Override
	public EClassifier getEClassifier(String name) {
		return pck.getEClassifier(name);
	}

	@Override
	public EObject eContainer() {
		return pck.eContainer();
	}

	@Override
	public EFactory getEFactoryInstance() {		
		return pck.getEFactoryInstance();
	}


	@Override
	public int hashCode() {
		return pck.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return pck.equals(obj);
	}
	
	

	@Override
	public void setName(String value) {
		pck.setName(value);

	}


	@Override
	public  EClass eClass(){
		return pck.eClass();
	}


	@Override
	public EStructuralFeature eContainingFeature() {
		return pck.eContainingFeature();
	}

	@Override
	public EReference eContainmentFeature() {
		return pck.eContainmentFeature();
	}

	@Override
	public EList<EObject> eContents() {
		return pck.eContents();
	}

	@Override
	public TreeIterator<EObject> eAllContents() {
		return pck.eAllContents();
	}

	@Override
	public boolean eIsProxy() {
		return pck.eIsProxy();
	}

	@Override
	public EList<EObject> eCrossReferences() {
		return pck.eCrossReferences();
	}

	@Override
	public Object eGet(EStructuralFeature feature) {
		return pck.eGet(feature);
	}

	@Override
	public Object eGet(EStructuralFeature feature, boolean resolve) {
		return pck.eGet(feature, resolve);
	}

	@Override
	public void eSet(EStructuralFeature feature, Object newValue) {
		pck.eSet(feature, newValue);
	}

	@Override
	public boolean eIsSet(EStructuralFeature feature) {
	 return pck.eIsSet(feature);
	}

	@Override
	public void eUnset(EStructuralFeature feature) {
		pck.eUnset(feature);

	}

	@Override
	public Object eInvoke(EOperation operation, EList<?> arguments)
			throws InvocationTargetException {
		return pck.eInvoke(operation, arguments);

	}

	@Override
	public EList<Adapter> eAdapters() {
		return pck.eAdapters();

	}

	@Override
	public boolean eDeliver() {
		return pck.eDeliver();
	}

	@Override
	public void eSetDeliver(boolean deliver) {
		pck.eSetDeliver(deliver);


	}

	@Override
	public void eNotify(Notification notification) {
		pck.eNotify(notification);

	}


	@Override
	public void setNsURI(String value) {
		pck.setNsURI(value);
	}



	@Override
	public void setNsPrefix(String value) {
		pck.setNsPrefix(value);


	}	

	@Override
	public void setEFactoryInstance(EFactory value) {
		pck.setEFactoryInstance(value);;
	}

	@Override
	public void setExtendedMetaData(
			EPackageExtendedMetaData ePackageExtendedMetaData) {
		
		((Holder) pck).setExtendedMetaData(ePackageExtendedMetaData);
		}
	@Override
	public EPackageExtendedMetaData getExtendedMetaData() {
	 return ((Holder) pck).getExtendedMetaData() ;
	}
	



}
