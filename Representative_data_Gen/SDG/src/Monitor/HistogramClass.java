package Monitor;

import java.util.LinkedList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.EReference;

public class HistogramClass {
	
	

	 public String condition;
	 public LinkedList<EClass> values;
	 public LinkedList<Double>probabilities;
	 public LinkedList<Integer>frequencies;
	 public EClass context;
	 public EClass root;
	 public EReference callingRef;
	 public double distance = 1;
	
	

		public HistogramClass() {
			super();
			this.condition = "true";
			this.values = new  LinkedList<EClass> ();
			this.probabilities =  new LinkedList<Double>();
			this.frequencies = new LinkedList<Integer>();
			this.context=null;
			this.callingRef=null;
			this.root = null;
		}
		
		
		public HistogramClass(String condition, ENamedElement elem,  LinkedList<EClass>  values, LinkedList<Double> probabilities,LinkedList<Integer> frequencies,EClass context) {
			super();
			this.condition = condition;
			this.values = values;
			this.probabilities = probabilities;
			this.frequencies = frequencies;
			this.context=context;
			this.callingRef=null;
			this.root = null;
		}
		

	
	public  HistogramClass getEmptyClone()
	{
		
		HistogramClass res = new HistogramClass();
		res.setCondition(condition);
		res.setValues(values);
		 LinkedList<Double>temp = new LinkedList<Double>();
		 for (int i = 0; i < probabilities.size(); i++) {
			temp.add(new Double(0));
		}
		 res.setProbabilities(temp);
		 LinkedList<Integer>temp2 = new LinkedList<Integer>();
		 for (int i = 0; i < frequencies.size(); i++) {
			temp2.add(new Integer(0));
		}
		 res.setFrequencies(temp2);
		 res.setContext(context);
		 res.setRoot(root); 
		 res.setCallingRef(callingRef);
		return res;
	}
	public LinkedList<Integer> getFrequencies() {
		return frequencies;
	}


	public void setFrequencies(LinkedList<Integer> frequencies) {
		this.frequencies = frequencies;
	}


	public double getDistance() {
		return distance;
	}


	public void setDistance(double distance) {
		this.distance = distance;
	}


	public EClass getRoot() {
		return root;
	}

	public void setRoot(EClass root) {
		this.root = root;
	}

	public EReference getCallingRef() {
		return callingRef;
	}

	public void setCallingRef(EReference callingRef) {
		this.callingRef = callingRef;
	}

		public EClass getContext() {
		return context;
	}

	public void setContext(EClass context) {
		this.context = context;
	}
	
	
	public String getCondition() {
		return condition;
	}
	public void setCondition(String condition) {
		this.condition = condition;
	}

	public  LinkedList<EClass>  getValues() {
		return values;
	}
	public void setValues( LinkedList<EClass>  values) {
		this.values = values;
	}
	
	
	public LinkedList<Double> getProbabilities() {
		return probabilities;
	}
	public void setProbabilities(LinkedList<Double> probabilities) {
		this.probabilities = probabilities;
	}

	public boolean equals(HistogramClass other)
	{  boolean res;
	    if(context!=null && other.context==null)
	    return false;
		if(context == null)
		res=   condition.trim().equals(other.condition.trim()) &&  other.context == null && values.containsAll(other.values) && values.size()==other.values.size();
		else res =  condition.trim().equals(other.condition.trim()) &&  other.context.getName().equals(context.getName()) && values.containsAll(other.values) && values.size()==other.values.size();
	   return res;
	}
	
	public String toString()
	{
		 String res = "---"+ (root==null? "null":root.getName())+ "==>["+condition+" ("+(context==null?"":context.getName())+")|";
		 for (EClass classe : values) {
			 res = res + classe.getName() +";";
		}
		  res = res + "|Prob-";
		 for (Double double1 : probabilities) {
			 res = res + double1 +";";
		}
		 
		  res = res + "|Freq-";
			 for (Integer int1 : frequencies) {
				 res = res + int1 +";";
			}
		 
		
		 res = res+ "]\n";
		 return res;
	}


	public void CalculateProba() {
		flashFrequencies();
		double total = ProfileConstraintExtractor.Sum(probabilities);
		if(total!=0)
		for (int i = 0; i < probabilities.size(); i++) {
			double a = probabilities.get(i).doubleValue();
			double avrg = a/total;
			probabilities.set(i, new Double(avrg));
			
		}
		else for (int i = 0; i < probabilities.size(); i++) {
			probabilities.set(i, new Double(0));
		}
		
	}
	
	
	public void CalculateProbaFromFrequencies() {
		double total = 0;
		for (Integer integer : frequencies) {
			total= total + integer;
		}
		
		if(total!=0)
		for (int i = 0; i < frequencies.size(); i++) {
			double a = frequencies.get(i).doubleValue();
			double avrg = a/total;
			probabilities.set(i, new Double(avrg));	
		}
		else for (int i = 0; i < probabilities.size(); i++) {
			probabilities.set(i, new Double(0));
		}
		
	}


	public boolean isEmpty() {
		if(probabilities==null)
		return true;
		if(probabilities.size()==0)
		return true;
		boolean res = ProfileConstraintExtractor.Sum(probabilities)==0.0;
		return res;
		
	}


	public void flashFrequencies() {
		frequencies.clear();
		for (int i = 0; i < probabilities.size(); i++) {
			frequencies.add(0);
		}
		for (int i = 0; i < probabilities.size(); i++) {
			double a = probabilities.get(i).doubleValue();
			frequencies.set(i, (int)a);
			
		}
		
	}


	public int getIndexValue(EClass value) {

			for (int i = 0; i < values.size(); i++) {
				EClass classe = values.get(i);
			
			if(classe.getName().equals(value.getName()))
				return i;
		}
		return -1;
	}
}
