package Monitor;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

public class DiffContainer {

	public EObject rootMutated;
	public EObject rootDesired;
	public EStructuralFeature feature;
	public String valueMutated;
	public String valueDesired;

	
	public DiffContainer() {
		super();
		rootMutated = null;
		rootMutated = null;
		feature = null;	
	}
	
	
	
	public DiffContainer(EObject rootMutated, EObject rootDesired, EStructuralFeature feature, String valueMutated, String valueDesired) {
		super();
		this.rootMutated = rootMutated;
		this.rootDesired = rootDesired;
		this.feature = feature;
		this.valueMutated = valueMutated;
		this.valueDesired = valueDesired;
	}



	public EObject getRootMutated() {
		return rootMutated;
	}
	public void setRootMutated(EObject rootMutated) {
		this.rootMutated = rootMutated;
	}
	public EObject getRootDesired() {
		return rootDesired;
	}
	public void setRootDesired(EObject rootDesired) {
		this.rootDesired = rootDesired;
	}
	public EStructuralFeature getFeature() {
		return feature;
	}
	public void setFeature(EStructuralFeature feature) {
		this.feature = feature;
	}
	public String getValueMutated() {
		return valueMutated;
	}
	public void setValueMutated(String valueMutated) {
		this.valueMutated = valueMutated;
	}
	public String getValueDesired() {
		return valueDesired;
	}
	public void setValueDesired(String valueDesired) {
		this.valueDesired = valueDesired;
	}


 public String toString ()
 {
	 String res = "[\n";
	 res = res + "rootMutated = " + rootMutated+ "\n";
	 res = res + "rootDesired = " + rootDesired+ "\n";
	 res = res + "feature = " + feature+ "\n";
	 res = res + "valueMutated =" + valueMutated+ "\n";
	 res = res + "valueDesired =" + valueDesired+ "\n";	 
	 res = res + "]\n";
	 return res;
	 
 }

}
