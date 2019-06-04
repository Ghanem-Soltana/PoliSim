
package adapters.umlImpl;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.uml2.uml.Enumeration;

import adapters.EClassAdapter;


public class EEnumUMLAdapter extends EClassAdapter<Enumeration> implements EEnum{

	protected Resource owningResource;
	public EEnumUMLAdapter(Enumeration newClass, Resource owningResource) {
		super(newClass);
		this.owningResource = owningResource;
		
	}
	@Override
	public boolean isSerializable() {
		return true;
	}
	@Override
	public void setSerializable(boolean value) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public int getClassifierID() {
	
		 return EcorePackage.EENUM;
	}
	@Override
	public EStructuralFeature getEStructuralFeature(String featureName) {
		throw new UnsupportedOperationException();
	}
	@Override
	public EList<EEnumLiteral> getELiterals() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public EEnumLiteral getEEnumLiteral(String name) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public EEnumLiteral getEEnumLiteral(int value) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public EEnumLiteral getEEnumLiteralByLiteral(String literal) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public EPackage getEPackage() {
		throw new UnsupportedOperationException();
	}
	@Override
	public String getName() {	
		return origClass.getName();
	}
	@Override
	public EList<EAnnotation> getEAnnotations() {
		throw new UnsupportedOperationException();
	}
	@Override
	public EAnnotation getEAnnotation(String source) {
		throw new UnsupportedOperationException();
	}
	@Override
	public EClass eClass() {
		throw new UnsupportedOperationException();
	}
	@Override
	public EObject eContainer() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public boolean isAbstract() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public EList<EClass> getESuperTypes() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public EList<EClass> getEAllSuperTypes() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public EList<EAttribute> getEAttributes() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public EList<EAttribute> getEAllAttributes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EList<EReference> getEReferences() {
	
		return new BasicEList<EReference>();
	}
	@Override
	public EList<EReference> getEAllReferences() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public EList<EReference> getEAllContainments() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public EList<EOperation> getEOperations() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public EList<EOperation> getEAllOperations() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public boolean isSuperTypeOf(EClass someClass) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public EList<EStructuralFeature> getEStructuralFeatures() {
		throw new UnsupportedOperationException();
	}
	@Override
	public EList<EStructuralFeature> getEAllStructuralFeatures() {
		throw new UnsupportedOperationException();
	}

	

}
