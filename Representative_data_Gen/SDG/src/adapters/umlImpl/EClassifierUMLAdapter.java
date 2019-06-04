
package adapters.umlImpl;


import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.uml2.uml.Classifier;

import adapters.EClassifierAdapter;



public class EClassifierUMLAdapter extends EClassifierAdapter<Classifier> {

	protected Resource owningResource;
	public EClassifierUMLAdapter(Classifier newClassifier, Resource owningResource) {
		super(newClassifier);
		this.owningResource = owningResource;
	}

	@Override
	public String getName() {
		return origClassifier.getName();
	}

	@Override
	public EPackage getEPackage() {
	//	Assert.isNotNull(origClassifier.getPackage(),"NULL Package" );
		return new EPackageUMLAdapter(origClassifier.getPackage(),owningResource);
	}

	@Override
	public Class<?> getInstanceClass() {
		return origClassifier.getClass();
	}

	@Override
	public String getInstanceClassName() {
		return origClassifier.getClass().getName();
	}

	@Override
	public EObject eContainer() {
		return origClassifier.eContainer();
	}

}
