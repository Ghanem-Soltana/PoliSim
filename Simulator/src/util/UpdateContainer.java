package util;

import org.eclipse.emf.ecore.EObject;

public class UpdateContainer {
	
	public String ocl;
	public EObject input;
	public EObject obj;
	public String itteratoreName;
	public String value;
	public String type;
	public String old;
	public String getOcl() {
		return ocl;
	}
	public void setOcl(String ocl) {
		this.ocl = ocl;
	}
	public EObject getInput() {
		return input;
	}
	public void setInput(EObject input) {
		this.input = input;
	}
	public EObject getObj() {
		return obj;
	}
	public void setObj(EObject obj) {
		this.obj = obj;
	}
	public String getItteratoreName() {
		return itteratoreName;
	}
	public void setItteratoreName(String itteratoreName) {
		this.itteratoreName = itteratoreName;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getOld() {
		return old;
	}
	public void setOld(String old) {
		this.old = old;
	}
	public UpdateContainer(String ocl, EObject input, EObject obj, String itteratoreName, String value, String type,
			String old) {
		super();
		this.ocl = ocl;
		this.input = input;
		this.obj = obj;
		this.itteratoreName = itteratoreName;
		this.value = value;
		this.type = type;
		this.old = old;
	}
	@Override
	public String toString() {
		return "UpdateContainer [ocl=" + ocl + ", input=" + input + ", obj=" + obj + ", itteratoreName="
				+ itteratoreName + ", value=" + value + ", type=" + type + ", old=" + old + "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((input == null) ? 0 : input.hashCode());
		result = prime * result + ((itteratoreName == null) ? 0 : itteratoreName.hashCode());
		result = prime * result + ((obj == null) ? 0 : obj.hashCode());
		result = prime * result + ((ocl == null) ? 0 : ocl.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UpdateContainer other = (UpdateContainer) obj;
		if (input == null) {
			if (other.input != null)
				return false;
		} else if (!input.equals(other.input))
			return false;
		if (itteratoreName == null) {
			if (other.itteratoreName != null)
				return false;
		} else if (!itteratoreName.equals(other.itteratoreName))
			return false;
		if (this.obj == null) {
			if (other.obj != null)
				return false;
		} else if (!this.obj.equals(other.obj))
			return false;
		if (ocl == null) {
			if (other.ocl != null)
				return false;
		} else if (!ocl.equals(other.ocl))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}
	
	
	
	
	

}
