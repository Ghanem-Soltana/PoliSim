
package adapters.umlImpl;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Property;

import adapters.EAttributeAdapter;



public class EAttributeUMLAdapter extends EAttributeAdapter<Property> implements EStructuralFeature{

	protected Resource owningResource;
	
	public EAttributeUMLAdapter(Property newAttribute, Resource owningResource) {
		super(newAttribute);
		this.owningResource=owningResource;
	}

	@Override
	public int getLowerBound() {
		return origAttribute.getLower();
	}

	@Override
	public int getUpperBound() {
		return origAttribute.getUpper();
	}

	@Override
	public boolean isMany() {
		return origAttribute.isMultivalued();
	}

	//ICI
	@Override
	public EClassifier getEType() {
		
		  EClassifier data = EDatatypeUtil.convertFromString(origAttribute.getType().getName()+ " " + origAttribute.getType()); 
    
        if(data!=null)
			return ((EResourceUMLAdapter)owningResource).getClassIfNotExists(EDatatypeUtil.convertFromString(origAttribute.getType().getName()));
     
        if (origAttribute.getType() instanceof Class ) 
			return ((EResourceUMLAdapter)owningResource).getClassIfNotExists(new EClassUMLAdapter((Class)origAttribute.getType(),owningResource));
        if (origAttribute.getType() instanceof Classifier ) 
        return ((EResourceUMLAdapter)owningResource).getClassIfNotExists(new EClassifierUMLAdapter((Classifier) origAttribute.getType(),owningResource));
        return null;

	}

	@Override
	public String getName() {
		return origAttribute.getName();
	}

	@Override
	public EList<EAnnotation> getEAnnotations() {
		EList<EAnnotation> result = new BasicEList<EAnnotation>();
			for (EAnnotation annot : origAttribute.getEAnnotations())
				result.add(new EAnnotationUMLAdapter(annot,owningResource));
		return result;
	}

	@Override
	public EAnnotation getEAnnotation(String source) {
		if (origAttribute.getEAnnotation(source) != null)
			return new EAnnotationUMLAdapter(origAttribute.getEAnnotation(source),owningResource);
		return null;
	}

	@Override
	public EDataType getEAttributeType() {
		String name = origAttribute.getType().getName();
		return (EDataType)EDatatypeUtil.convertFromString(name);
	}

	@Override
	public EClass getEContainingClass() {
		return (EClass) ((EResourceUMLAdapter)owningResource).getClassIfNotExists(new EClassUMLAdapter(origAttribute.getClass_(),owningResource));
	}

	@Override
	public EObject eContainer() {
		return ((EResourceUMLAdapter)owningResource).getClassIfNotExists(new EClassUMLAdapter((Class) origAttribute.eContainer(),owningResource));
		
	}
	
	@Override
	public boolean isDerived() {
		return origAttribute.isDerived();
	}
	
	@Override
	public boolean isID() {
		return origAttribute.isID();
	}
	
	@Override
	public int getFeatureID() {
		return EcorePackage.EATTRIBUTE;
	}
	
	@Override
	public boolean isChangeable() {
		return true;
	}
	
	@Override
	public java.lang.Class<?> getContainerClass() {
    return null;
	}
	

}
