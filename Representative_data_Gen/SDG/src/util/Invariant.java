package util;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.ocl.ecore.Constraint;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Interface;

import Monitor.ProfileConstraintExtractor;

public class Invariant {
	
	public String toString()
	{
		return "["+(context==null?"null":context.getName())+"]"+expression;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Invariant other = (Invariant) obj;
		if (context == null) {
			if (other.context != null)
				return false;
		} else if (context!=other.context)
			return false;
		if (expression == null) {
			if (other.expression != null)
				return false;
		} else if (!expression.equals(other.expression))
			return false;
		return true;
	}
	public Classifier context;
	public String expression;
	
	
	
	
	public Invariant() {
		super();
		context = null;
	
	}
	public Invariant(org.eclipse.uml2.uml.Class context, String expression) {
		super();
		this.context = context;
		this.expression = expression;
	}
	
	public Invariant(Classifier context, String expression) {
		super();
		if(context==null)
			System.out.println();
		this.context = context;
		this.expression = expression;
	}
	
	public Invariant(EClass context, String expression) {
		super();
	
		if(context!=null)
		{
		this.context = (Classifier) ProfileConstraintExtractor.getCorrespondant(context,ProfileConstraintExtractor.realClasses);
	
		if(this.context==null)
		{
			this.context = (org.eclipse.uml2.uml.Class) ProfileConstraintExtractor.getCorrespondant(context,ProfileConstraintExtractor.realClasses);
		}
		}
			this.expression = expression;
	}
	


	
	public Invariant(Constraint constraint) {
		super();
		if(!constraint.getConstrainedElements().isEmpty())
		this.context = (org.eclipse.uml2.uml.Class) ProfileConstraintExtractor.getCorrespondant(((EClass)constraint.getConstrainedElements().get(0)),ProfileConstraintExtractor.realClasses);
		else this.context = null;
		this.expression = constraint.getSpecification().getBodyExpression().toString();
	}

	public Classifier getContext() {
		return context;
	}
	public void setContext(org.eclipse.uml2.uml.Class context) {
		this.context = context;
	}
	public String getexpression() {
		return expression;
	}
	public void setexpression(String expression) {
		this.expression = expression;
	}

}
