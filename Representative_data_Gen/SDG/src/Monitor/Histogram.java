package Monitor;

import java.math.BigDecimal;
import java.util.LinkedList;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.ENamedElement;

public class Histogram {

	

     public String condition;
	 public ENamedElement elem;
	 public EList<String> values;
	 public EList<String>probabilities;
	 public LinkedList<Integer>frequencies;
	 public EClass context;
	 public double distance = 1;
	 
	 

		public Histogram() {
			super();
			this.condition = "true";
			this.elem = null;
			this.values = new BasicEList<String>();
			this.probabilities =  new BasicEList<String>();
			frequencies = new LinkedList<Integer>();
			context=null;
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
	public ENamedElement getElem() {
		return elem;
	}
	public void setElem(ENamedElement elem) {
		this.elem = elem;
	}
	public EList<String> getValues() {
		return values;
	}
	public void setValues(EList<String> values) {
		this.values = values;
	}
	public EList<String> getProbabilities() {
		return probabilities;
	}
	public void setProbabilities(EList<String> probabilities) {
		this.probabilities = probabilities;
	}
	
	public boolean equals(Histogram other)
	{  boolean res;
	    if(context!=null && other.context==null)
	    return false;
		if(context == null)
		res= condition.trim().equals(other.condition.trim()) &&  other.context == null && values.containsAll(other.values) && values.size()==other.values.size();
		else res= condition.trim().equals(other.condition.trim()) &&  other.context.getName().equals(context.getName()) && values.containsAll(other.values) && values.size()==other.values.size();
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


	public String toString()
	{    String res = "---"+elem.getName() + "==>";
		 res = res +  "["+condition+" ("+(context==null?"":context.getName())+")|";
		 for (String classe : values) {
			 res = res + classe +";";
		}
		  res = res + "|Prob-";
		 for (String double1 : probabilities) {
			 res = res + double1 +";";
		}
		 
		 
		  res = res + "|Freq-";
			 for (Integer int1 : frequencies) {
				 res = res + int1 +";";
			}
		 
		
		 res = res+ "]\n";
		 return res;
	}
	
	public double sum()
	{
		double res=0;
		

		
		for (int i = 0; i < probabilities.size(); i++) {
			res=res+ Double.valueOf(String.valueOf(probabilities.get(i)).replace(",", ".")).doubleValue();
		 Double truncatedDouble=new BigDecimal(res ).setScale(50, BigDecimal.ROUND_HALF_UP).doubleValue();
		 res=truncatedDouble;
		}
		if(Math.ceil(res)==1||Math.floor(res)==1||Math.round(res)==1)
			return 1.0;
		if(Math.ceil(res)==100||Math.floor(res)==100||Math.round(res)==100)
			return 100.0;
		
		return res;
	}

	public boolean isEmpty() {
		if(probabilities==null)
		return true;
		if(probabilities.size()==0)
		return true;
		boolean res = sum()==0.0;
		return res;
		
	}
	
	public void CalculateProba() {
		flashFrequencies();
		double total = sum();
		if(total!=0)
		for (int i = 0; i < probabilities.size(); i++) {
			double a = Double.valueOf(String.valueOf(probabilities.get(i)).replace(",", ".")).doubleValue() ;
			double avrg = a/total;
			probabilities.set(i, String.valueOf(avrg));
			
		}
		else for (int i = 0; i < probabilities.size(); i++) {
			probabilities.set(i, "");
		}
		
	}

	
	public Histogram getEmptyClone() {
		Histogram res = new Histogram();
		res.setCondition(condition);
		res.setValues(values);
		 EList<String>temp = new BasicEList<String>();
		 for (int i = 0; i < probabilities.size(); i++) {
			temp.add("0");
		}
		 res.setProbabilities(temp);
		 LinkedList<Integer>temp2 = new LinkedList<Integer>();
		 for (int i = 0; i < frequencies.size(); i++) {
			temp2.add(new Integer(0));
		}
		 res.setFrequencies(temp2);
		 res.setContext(context);
		 res.setElem(elem);
		return res;
	}
	
	public void CalculateProbaFromFrequencies() {
		double total = 0;
		for (Integer integer : frequencies) {
			total= total + integer;
		}
		
		if(total!=0)
		for (int i = 0; i < frequencies.size(); i++) {
			double a = Double.valueOf(String.valueOf(probabilities.get(i)).replace(",", ".")).doubleValue() ;
			double avrg = a/total;
			probabilities.set(i, new Double(avrg).toString());	
		}
		else for (int i = 0; i < probabilities.size(); i++) {
			probabilities.set(i, "0");
		}
		
	}

	
	public void flashFrequencies() {
		frequencies.clear();
		for (int i = 0; i < probabilities.size(); i++) {
			frequencies.add(0);
		}
		for (int i = 0; i < probabilities.size(); i++) {
			double a = Double.valueOf(String.valueOf(probabilities.get(i)).replace(",", ".")).doubleValue() ;
			frequencies.set(i, (int)a);
			
		}
		
	}


	public int getIndexValue(String value) {
		for (int i = 0; i < values.size(); i++) {
			if(value.equals(values.get(i)))
				return i;
		}
		return -1;
	}
}
