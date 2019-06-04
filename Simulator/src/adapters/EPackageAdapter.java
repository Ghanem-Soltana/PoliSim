
package adapters;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.util.BasicExtendedMetaData.EPackageExtendedMetaData.Holder;


public abstract class EPackageAdapter<P> implements EPackage, Holder {


	protected P origPackage;
	
	public EPackageAdapter (P newPackage){
		origPackage = newPackage;
	}
	

	public P getOriginalPackage(){
		return origPackage;
	}
	
	

}
