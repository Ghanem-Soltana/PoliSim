
package adapters;

import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.util.BasicExtendedMetaData.EClassifierExtendedMetaData.Holder;

public abstract class EEnumAdapter<C> implements EEnum, Holder {

	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((origClass == null) ? 0 : origClass.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof EEnumAdapter))
			return false;
		@SuppressWarnings("rawtypes")
		EEnumAdapter other = (EEnumAdapter) obj;
		if (origClass == null) {
			if (other.origClass != null)
				return false;
		} else if (!origClass.equals(other.origClass))
			return false;
		return true;
	}
	
	protected C origClass;
	
	public EEnumAdapter (C newClass){
		if (newClass == null) throw new NullPointerException("original class is null");
		origClass = newClass;
	}

}
