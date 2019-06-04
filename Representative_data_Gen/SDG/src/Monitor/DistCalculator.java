package Monitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

import org.apache.commons.math3.ml.distance.CanberraDistance;
import org.apache.commons.math3.ml.distance.EuclideanDistance;
import org.apache.commons.math3.ml.distance.ManhattanDistance;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;

import util.OCLForJAVA;

public class DistCalculator {

	
	public  LinkedList<EObject> roots ;
	public  LinkedList<HistogramClass> hitogramsForClasses;
	public  LinkedList<Histogram> hitogramsForAttributes;
	public  LinkedList<Histogram> hitogramsForAssociations;
	
	
	public DistCalculator() {
		super();
		roots = new LinkedList<EObject>();
	}
	public DistCalculator(LinkedList<EObject> roots, LinkedList<HistogramClass> hitogramsForClasses,
			LinkedList<Histogram> hitogramsForAttributes, LinkedList<Histogram> hitogramsForAssociations) {
		super();
		this.roots = roots;
		this.hitogramsForClasses = hitogramsForClasses;
		this.hitogramsForAttributes = hitogramsForAttributes;
		this.hitogramsForAssociations = hitogramsForAssociations;
	}
	public LinkedList<EObject> getRoots() {
		return roots;
	}
	public void setRoots(LinkedList<EObject> roots) {
		this.roots = roots;
	}
	public LinkedList<HistogramClass> getHitogramsForClasses() {
		return hitogramsForClasses;
	}
	public void setHitogramsForClasses(LinkedList<HistogramClass> hitogramsForClasses) {
		this.hitogramsForClasses = hitogramsForClasses;
	}
	public LinkedList<Histogram> getHitogramsForAttributes() {
		return hitogramsForAttributes;
	}
	public void setHitogramsForAttributes(LinkedList<Histogram> hitogramsForAttributes) {
		this.hitogramsForAttributes = hitogramsForAttributes;
	}
	public LinkedList<Histogram> getHitogramsForAssociations() {
		return hitogramsForAssociations;
	}
	public void setHitogramsForAssociations(LinkedList<Histogram> hitogramsForAssociations) {
		this.hitogramsForAssociations = hitogramsForAssociations;
	}
	public LinkedList<HistogramClass> saveClasses() {
		LinkedList<HistogramClass> res = new LinkedList<HistogramClass>();
		
		if(roots.size()>0)
		{
		for (HistogramClass histogramClass : hitogramsForClasses) {
		HistogramClass container = histogramClass.getEmptyClone();
		if(histogramClass.callingRef==null&&histogramClass.root.getName().equals("Tax_Case"))
		{
		for (EObject root : roots) {
			for (int i = 0; i < container.values.size(); i++) {
			EClass c = container.values.get(i);
			String toBeEvaluated = "self.oclIsKindOf("+c.getName()+")";
			OCLForJAVA.init("",root);
			try {
			boolean belong = OCLForJAVA.evaluateBoolean(root, toBeEvaluated, "temp");
			if(belong)
			{
				container.probabilities.set(i, container.probabilities.get(i)+1);
				container.frequencies.set(i, container.frequencies.get(i)+1);
			}
			} catch (Exception e) {e.printStackTrace();}
			}
			}
		container.CalculateProba();
		}
		else 
		{
			OCLForJAVA.init("",roots.get(0));
			for (int i = 0; i < container.values.size(); i++) {
				if(container.values.size()>1)
				{
				EClass c = container.values.get(i);
				String toBeEvaluated ="Set{}";
				    Collection<EObject> newRoots = new LinkedList<EObject>();
					if(!container.condition.trim().equalsIgnoreCase("true"))
				    toBeEvaluated = container.context.getName()+".allInstances()->select("+container.condition.replace("self.", "")+")";
					else if(container.callingRef!=null) toBeEvaluated = container.callingRef.getEContainingClass().getName()+".allInstances()";
					else toBeEvaluated = c.getName()+".allInstances()";

				     newRoots = OCLForJAVA.evaluateECollection(roots.get(0),toBeEvaluated,"newRoots", !container.condition.trim().equalsIgnoreCase("true")? container.context.getName():c.getName(),"Set"); 
		
				     if(newRoots!=null)
				     for (EObject root : newRoots) {
				    	 if(container.callingRef!=null)
				    	toBeEvaluated = "self."+container.callingRef.getName()+".oclAsSet()->iterate(inc;  acc:Bag("+container.callingRef.getEType().getName()+")=Bag{}  |"
				    			+ " if(inc.oclIsKindOf("+c.getName()+")) then acc->including(inc) else acc endif)->size()";
				    	 else toBeEvaluated = "self.oclIsKindOf("+c.getName()+")";

						OCLForJAVA.init("",root);
						
						 if(container.callingRef==null)
						 {
						try {
						boolean belong = OCLForJAVA.evaluateBoolean(root, toBeEvaluated, "temp");
						if(belong)
						{
							container.probabilities.set(i, container.probabilities.get(i)+1);
							container.frequencies.set(i, container.frequencies.get(i)+1);
						}
						} catch (Exception e) {e.printStackTrace();} 
						}
						 else {
								try {
							 int count = OCLForJAVA.evaluateIntOut(root, toBeEvaluated, "count");
							 container.probabilities.set(i, container.probabilities.get(i)+count);
							 container.frequencies.set(i,  container.frequencies.get(i)+count);
								} catch (Exception e) {e.printStackTrace();} 
						 }
						}
				    
				
				}
			
			}
			
			
			    
		container.CalculateProba();	
			
		}
		res.add(container);
		}
		
		}
		
		/*for (int i = 0; i < res.size(); i++) {
			if(res.get(i).isEmpty())
			{res.remove(i);
			i--;
				
			}
		}
		*/
		return res;
		
		
	}
	public LinkedList<Histogram> saveAssociations() {
		// adapted to the example!
		LinkedList<Histogram> res = new LinkedList<Histogram>();
		
		if(roots.size()>0)
		{
		for (Histogram histogramAss : hitogramsForAssociations) {
			
		OCLForJAVA.init("",roots.get(0));
		Histogram container = histogramAss.getEmptyClone();
		EReference ref = ((EReference)container.getElem());
	
		   EClass c = (EClass) ref.getEContainingClass();
 		   Collection<EObject> newRoots = new LinkedList<EObject>();
 		   String toBeEvaluated = c.getName()+".allInstances()";
 		   if(!container.condition.equals("true"))
			 toBeEvaluated = container.context.getName()+".allInstances()->select("+container.condition.replace("self.", "")+")";
		     newRoots = OCLForJAVA.evaluateECollection(roots.get(0),toBeEvaluated,"newRoots",container.condition.equals("true")?c.getName():container.context.getName(),"Set"); 

			for (int i = 0; i < container.values.size(); i++) {
				String bin = container.values.get(i);
				
				  for (EObject root : newRoots) {
					  OCLForJAVA.init("",root);
					  if(!bin.contains(".."))
				   toBeEvaluated = "self."+ref.getName()+"->size() = "+bin;
					  else 
					  {
						  try{
						  	String value=bin.trim().replace("[", "").replace("]", "").replace("..", "-");
							String [] temp=value.split("-");
							double start=-1;
							double end=-1;
							start= Double.valueOf(temp[0].trim());
							end= Double.valueOf(temp[1].trim());
						  toBeEvaluated = "self."+ref.getName()+"->size() >= "+start+" and self."+ref.getName()+"->size() <= "+end;
						  }
						  catch(Exception w){w.printStackTrace();}
						
					  }
						boolean belong = OCLForJAVA.evaluateBoolean(root, toBeEvaluated, "belong");
						if(belong)
						{
							container.probabilities.set(i, String.valueOf(Double.valueOf(container.probabilities.get(i))+1));
							container.frequencies.set(i, container.frequencies.get(i)+1);
						}
				
				  }
			
			}
	
		container.CalculateProba();		
		res.add(container);
		}
		
		}
		

		return res;
	}
	
	public LinkedList<Histogram> saveAttributes() {
		// adapted to the example!
		LinkedList<Histogram> res = new LinkedList<Histogram>();
		
		if(roots.size()>0)
		{
		for (Histogram histogramAss : hitogramsForAttributes) {
			
		OCLForJAVA.init("",roots.get(0));
		Histogram container = histogramAss.getEmptyClone();
		EAttribute att = ((EAttribute)container.getElem());
	
		   EClass c = (EClass) att.getEContainingClass();
 		   Collection<EObject> newRoots = new LinkedList<EObject>();
 		   String toBeEvaluated = c.getName()+".allInstances()";
 		   if(!container.condition.equals("true"))
			 toBeEvaluated = container.context.getName()+".allInstances()->select("+container.condition.replace("self.", "")+")";
		     newRoots = OCLForJAVA.evaluateECollection(roots.get(0),toBeEvaluated,"newRoots",container.condition.equals("true")?c.getName():container.context.getName(),"Set"); 

			for (int i = 0; i < container.values.size(); i++) {
				String bin = container.values.get(i);
				
				  for (EObject root : newRoots) {
					  OCLForJAVA.init("",root);
					  if(!bin.contains(".."))
				   toBeEvaluated = "self."+att.getName()+" = "+bin;
					  else 
					  {
						  try{
						  	String value=bin.trim().replace("[", "").replace("]", "").replace("..", "-");
							String [] temp=value.split("-");
							double start=-1;
							double end=-1;
							start= Double.valueOf(temp[0].trim());
							end= Double.valueOf(temp[1].trim());
						  toBeEvaluated = "self."+att.getName()+" >= "+start+" and self."+att.getName()+" <= "+end;
						  }
						  catch(Exception w){w.printStackTrace();}
						
					  }
						boolean belong = OCLForJAVA.evaluateBoolean(root, toBeEvaluated, "belong");
						if(belong)
						{
							container.probabilities.set(i, String.valueOf(Double.valueOf(container.probabilities.get(i))+1));
							container.frequencies.set(i, container.frequencies.get(i)+1);
						}
				
				  }
			
			}
	
		container.CalculateProba();		
		res.add(container);
		}
		
		}
		

		return res;
	}
	
	public void calculteDistancesClasses(LinkedList<HistogramClass> temp) {
		for (HistogramClass histogramClass : temp) {
			
			HistogramClass corr = getCorrependantClass(histogramClass);
			if(corr!=null)
			{
				 double [] tab11=makeArray(histogramClass.probabilities);
			 	 double [] tab22=makeArray(corr.probabilities);
			 	 
			 	 if(tab11.length==tab22.length)
			 	 {
			 	  /*double [] all=concat(tab11, tab22); 
				     normaliser(all);
				     for (int j = 0; j < all.length; j++) {
						if(j<tab11.length)
						tab11[j] = all[j];
						else tab22[j-tab11.length] = all[j];
					}*/
				    
				 	double dist = distance(tab11,tab22);
				 	corr.distance = dist;
				 	histogramClass.distance = dist;
				 	
			 	 }
			}
			
			
		}
		
	}
	
	
	public void calculteDistancesAttributes(LinkedList<Histogram> temp) {
		for (Histogram histogram : temp) {
			
			Histogram corr = getCorrependantAttribute(histogram);
			if(corr!=null)
			{
				 double [] tab11=makeArray(histogram.probabilities);
			 	 double [] tab22=makeArray(corr.probabilities);
			 	 
			 	 if(tab11.length==tab22.length)
			 	 {
			 	  /*double [] all=concat(tab11, tab22); 
				     normaliser(all);
				     for (int j = 0; j < all.length; j++) {
						if(j<tab11.length)
						tab11[j] = all[j];
						else tab22[j-tab11.length] = all[j];
					}*/
				    
				 	double dist = distance(tab11,tab22);
				 	corr.distance = dist;
				 	histogram.distance = dist;
				 	
			 	 }
			}
			
			
		}
		
	}
	
	
	public void calculteDistancesAssociations(LinkedList<Histogram> temp) {
		for (Histogram histogram : temp) {
			
			Histogram corr = getCorrependantAssociation(histogram);
			if(corr!=null)
			{
				 double [] tab11=makeArray(histogram.probabilities);
			 	 double [] tab22=makeArray(corr.probabilities);
			 	 
			 	 if(tab11.length==tab22.length)
			 	 {
			 	  /*double [] all=concat(tab11, tab22); 
				     normaliser(all);
				     for (int j = 0; j < all.length; j++) {
						if(j<tab11.length)
						tab11[j] = all[j];
						else tab22[j-tab11.length] = all[j];
					}*/
				    
				 	double dist = distance(tab11,tab22);
				 	corr.distance = dist;
				 	histogram.distance = dist;
				 	
			 	 }
			}
			
			
		}
		
	}
	
	
	
	
	
	public double calculteAgregatedManhaten(LinkedList<HistogramClass> popHistClasses, LinkedList<Histogram> popHistAttributes,LinkedList<Histogram> popHistAssociations) {
		// TODO Auto-generated method stub

		//System.out.println();
		
		ManhattanDistance man = new ManhattanDistance();
		double dist =0;
		int nb = 0;
		
		for (HistogramClass histogramClass : popHistClasses) {
			HistogramClass corr = getCorrependantClass(histogramClass);
			if(corr!=null)
			{
				if(!histogramClass.getRoot().getName().contains("Tax_Case"))
				{
				 double [] tab11=makeArray(histogramClass.probabilities);
			 	 double [] tab22=makeArray(corr.probabilities);	 
			 	 if(summ(tab11)!=0.0&&histogramClass.condition.trim().equals("true"))
			 	 {
			 	 double increment = man.compute(tab11,tab22);
			   	 dist = dist + increment;
				 System.out.println(histogramClass);
			   	 System.out.println(extract(tab11));
			 	 System.out.println("vs.");
			 	 System.out.println(extract(tab22));
			 	 }
			   	 nb++;
			   
				} else nb++;
			}
			}
		
		
			for (Histogram histogram : popHistAttributes) {
				
				Histogram corr1 = getCorrependantAssociation(histogram);
				if(corr1!=null)
				{	 
					 double [] tab11=makeArray(histogram.probabilities);
				 	 double [] tab22=makeArray(corr1.probabilities);
				 	 if(summ(tab11)!=0.0&&histogram.condition.trim().equals("true"))
				 	 {
				 	 dist = dist + man.compute(tab11, tab22);
				 	 }
				 	 nb++;
			
				}
				}
			
			
			for (Histogram histogram : popHistAssociations) {
				Histogram corr2 = getCorrependantAssociation(histogram);
				if(corr2!=null)
				{
					
					 double [] tab11=makeArray(histogram.probabilities);
				 	 double [] tab22=makeArray(corr2.probabilities);
				 	if(summ(tab11)!=0.0&&histogram.condition.trim().equals("true"))
				 	 {
				 	 dist = dist + man.compute(tab11, tab22);
				 	 }
				 	 nb++;
			
				}
				}
		
			


		return dist/nb;
	}
	public double calculteCanbera(LinkedList<HistogramClass> popHistClasses, LinkedList<Histogram> popHistAttributes,LinkedList<Histogram> popHistAssociations) {
		// TODO Auto-generated method stub
		
		CanberraDistance can = new CanberraDistance();
		double dist = 0;
		int nb =0;
		//System.out.println();
		
		for (HistogramClass histogramClass : popHistClasses) {
			HistogramClass corr = getCorrependantClass(histogramClass);
			if(corr!=null)
			{
				if(!histogramClass.getRoot().getName().contains("Tax_Case"))
				{
				 double [] tab11=makeArray(histogramClass.probabilities);
			 	 double [] tab22=makeArray(corr.probabilities);	 
			 	 if(summ(tab11)!=0.0&&histogramClass.condition.trim().equals("true"))
			 	 {
			 	 double increment = can.compute(tab11,tab22);
			   	 dist = dist + increment;
				 System.out.println(histogramClass);
			   	 System.out.println(extract(tab11));
			 	 System.out.println("vs.");
			 	 System.out.println(extract(tab22));
			 	 }
			   	 nb++;
			   
				} else nb++;
			}
			}
			
			for (Histogram histogram : popHistAttributes) {
				
				Histogram corr1 = getCorrependantAssociation(histogram);
				if(corr1!=null)
				{	 
					 double [] tab11=makeArray(histogram.probabilities);
				 	 double [] tab22=makeArray(corr1.probabilities);
				 	 if(summ(tab11)!=0.0&&histogram.condition.trim().equals("true"))
				 	 {
				 	 dist = dist + can.compute(tab11, tab22);
				 	 }
				 	 nb++;
			
				}
				}
			
			
			for (Histogram histogram : popHistAssociations) {
				Histogram corr2 = getCorrependantAssociation(histogram);
				if(corr2!=null)
				{
					
					 double [] tab11=makeArray(histogram.probabilities);
				 	 double [] tab22=makeArray(corr2.probabilities);
				 	 if(summ(tab11)!=0.0&&histogram.condition.trim().equals("true"))
				 	 {
				 	 dist = dist + can.compute(tab11, tab22);
				 	 }
				 	 nb++;
			
				}
				}


		
		

		return dist/nb;
	}
	
	public String extract(double [] tab11)
	{
		String res = "";
		for (double d : tab11) {
			res =res+d+"-";
		}
		return res;
	}
	
	public double summ(double [] tab11)
	{
		double res = 0;
		for (double d : tab11) {
			res =res+d;
		}
		return res;
	}
	
	
	public double calculteAgregated(LinkedList<HistogramClass> popHistClasses, LinkedList<Histogram> popHistAttributes,LinkedList<Histogram> popHistAssociations) {
		// TODO Auto-generated method stub
		
		
		
		EuclideanDistance euc = new EuclideanDistance();
		double dist = 0;
		int nb =0;
		
			for (HistogramClass histogramClass : popHistClasses) {
			HistogramClass corr = getCorrependantClass(histogramClass);
			if(corr!=null)
			{
				if(!histogramClass.getRoot().getName().contains("Tax_Case"))
				{
				 double [] tab11=makeArray(histogramClass.probabilities);
			 	 double [] tab22=makeArray(corr.probabilities);	 
			 	 if(summ(tab11)!=0.0&&histogramClass.condition.equals("true"))
			 	 {
			 	 double increment = euc.compute(tab11,tab22);
			   	 dist = dist + increment;
			   	 System.out.println(histogramClass);
			   	 System.out.println(extract(tab11));
			 	 System.out.println("vs.");
			 	 System.out.println(extract(tab22));
			 	 }
			   	 nb++;
	
				}
				else nb++;
			}
			}
			
			for (Histogram histogram : popHistAttributes) {
				
				Histogram corr1 = getCorrependantAssociation(histogram);
				if(corr1!=null)
				{	 
					 double [] tab11=makeArray(histogram.probabilities);
				 	 double [] tab22=makeArray(corr1.probabilities);
				 	 if(summ(tab11)!=0.0&&histogram.condition.equals("true"))
				 	 {
				 	 dist = dist + euc.compute(tab11,tab22);
				
				     System.out.println(histogram);
				   	 System.out.println(extract(tab11));
				 	 System.out.println("vs.");
				 	 System.out.println(extract(tab22));
				 	 }
				   	 nb++;
				}
				}
			
			
			for (Histogram histogram : popHistAssociations) {
				Histogram corr2 = getCorrependantAssociation(histogram);
				if(corr2!=null)
				{
					
					 double [] tab11=makeArray(histogram.probabilities);
				 	 double [] tab22=makeArray(corr2.probabilities);
				 	 if(summ(tab11)!=0.0&&histogram.condition.equals("true"))
				 	 {
					 dist = dist + euc.compute(tab11,tab22);
				     System.out.println(histogram);
				   	 System.out.println(extract(tab11));
				 	 System.out.println("vs.");
				 	 System.out.println(extract(tab22));
				 	 }
				 	 nb++;
				}
				}
		
			

		return dist/nb;
	}
	
	
	

	private double[] makeArrayFromList(ArrayList<double[]> p1) {
		int size = 0;
		for (double[] ds : p1) {
			size = size+ds.length;
		}
		double[] res = new double[size];
		
		
		int pos = 0;
		for (double[] ds : p1) {
			for (int j = 0; j < ds.length; j++) {
				res[pos]= Double.valueOf(ds[j]).doubleValue();
				pos = pos + 1;
			}
			
		}
		return res;
	}
	
	
	private double[] makeArray(LinkedList<Double> probabilities) {
		double[] res = new double[probabilities.size()];
		for (int i = 0; i < res.length; i++) {
			res[i]= Double.valueOf(probabilities.get(i)).doubleValue();
		}
		return res;
	}
	
	private double[] makeArray(EList<String> probabilities) {
		double[] res = new double[probabilities.size()];
		for (int i = 0; i < res.length; i++) {
			if(probabilities.get(i) ==null)
				System.out.println();
			else if(probabilities.get(i).toString().equals(""))
				System.out.println();
			if(probabilities.get(i).equals(""))
				probabilities.set(i, "0");
			res[i]= Double.valueOf(probabilities.get(i)).doubleValue();
		}
		return res;
	}
	
	public HistogramClass getCorrependantClass(HistogramClass histogramClass) {
		for (HistogramClass histogram : hitogramsForClasses) {
			if(histogram.equals(histogramClass))
				return histogram;
		}
		return null;
	}
	
	public Histogram getCorrependantAttribute(Histogram histogram1) {
		for (Histogram histogram : hitogramsForAttributes) {
			if(histogram.equals(histogram1))
				return histogram;
		}
		return null;
	}
	
	
	
	public Histogram getCorrependantAssociation(Histogram histogram1) {
		for (Histogram histogram : hitogramsForAssociations) {
			if(histogram.equals(histogram1))
				return histogram;
		}
		return null;
	}
	
	public static double[] concat(double[] a, double[] b) {
		   int aLen = a.length;
		   int bLen = b.length;
		   double[] c= new double[aLen+bLen];
		   System.arraycopy(a, 0, c, 0, aLen);
		   System.arraycopy(b, 0, c, aLen, bLen);
		   return c;
		}
	
	
	public static void normaliser(double[]tab)
	{
		double max= 1;
		double min = 0;
		double range = max - min;
		for (int i = 0; i < tab.length; i++) {
			tab[i]= (tab[i]-min)/(range);
		}
		
	}
	
	public static double getMax(double[]tab)
	{
		double res=tab[0];
		for (int i = 0; i < tab.length; i++) {
			if(tab[i]>res)
			res=tab[i];
		}
		return res;
	}
	
	
	public static double getMin(double[]tab)
	{
		double res=tab[0];
		for (int i = 0; i < tab.length; i++) {
			if(tab[i]<res)
			res=tab[i];
		}
		return res;
	}
	
	public static double distance(double[]tab1,double tab2[])
	{
		
		 double [] all=concat(tab1, tab2); 
	     normaliser(all);
	     for (int j = 0; j < all.length; j++) {
			if(j<tab1.length)
			tab1[j] = all[j];
			else tab2[j-tab1.length] = all[j];
		}
		
		
		double res=0;
		for (int i = 0; i < tab2.length; i++) {
			double diff =tab1[i]-tab2[i];
			double pow = Math.pow(diff,2);
			res=res + pow;
		}
		
		
		res = Math.sqrt(res);
		res = res / Math.sqrt(tab2.length);
		if(res>1)
			return 1.0;
		return res;
		
	}
	
	public static void extractEveryThingFromRoot(EObject me, EList<EObject> res)
	{
		if(!res.contains(me))
		{
			res.add(me);
			EList<EObject> insideMe = me.eContents();
			for (EObject eObject : insideMe) {
				extractEveryThingFromRoot(eObject,res);
			}
		}
	}
	
	public LinkedList<Histogram> getMyBinsFromAssoc(EObject me) {
		LinkedList<Histogram> res = new LinkedList<Histogram>();
		
		if(me!=null)
		{
		for (Histogram histogramAss : hitogramsForAssociations) {
			
		OCLForJAVA.init("",me);
		Histogram container = histogramAss.getEmptyClone();
		EReference ref = ((EReference)container.getElem());
		EClass c = (EClass) ref.getEContainingClass();
		Collection<EObject> newRoots = new LinkedList<EObject>();
		EList<EObject> insideMe = new BasicEList<EObject>();
		extractEveryThingFromRoot(me, insideMe);
		for (EObject obj : insideMe) {	
			EClass myClass = ((EClass)obj.eClass());
			boolean pass = false;
			if(myClass.getName().equals(c.getName()))
			pass = true;
			else {
				 EList<EClass> fouch2 = myClass.getESuperTypes();
				 for (EClass eClass : fouch2) {
					if(eClass.getName().equals(c.getName()))
					pass=true;
				}
				 if(pass==false)
				 {
					 EList<EClass> fouch1 = c.getESuperTypes();
					 for (EClass eClass : fouch1) {
						if(eClass.getName().equals(c.getName()))
						pass=true;
					}
				 }
			}
			
		   
			if(pass)
			newRoots.add(obj);
		}
			for (int i = 0; i < container.values.size(); i++) {
				String bin = container.values.get(i);
				
				for (EObject root : newRoots) {
				OCLForJAVA.init("",root);
				String toBeEvaluated="0";
					  if(!bin.contains(".."))
					  toBeEvaluated = "self."+ref.getName()+"->size() = "+bin;
					  else 
					  {
						  try{
						  	String value=bin.trim().replace("[", "").replace("]", "").replace("..", "-");
							String [] temp=value.split("-");
							double start=-1;
							double end=-1;
							start= Double.valueOf(temp[0].trim());
							end= Double.valueOf(temp[1].trim());
						    toBeEvaluated = "self."+ref.getName()+"->size() >= "+start+" and self."+ref.getName()+"->size() <= "+end;
						  }
						  catch(Exception w){w.printStackTrace();}
						
					  }
						boolean belong = OCLForJAVA.evaluateBoolean(root, toBeEvaluated, "belong");
						if(belong)
						{
							container.probabilities.set(i, String.valueOf(Double.valueOf(container.probabilities.get(i))+1));
							container.frequencies.set(i, container.frequencies.get(i)+1);
						}
				
				}
			
			}
		res.add(container);
		}
		}
		
		/*for (int i = 0; i < res.size(); i++) {
			if(res.get(i).isEmpty())
			{res.remove(i);
			i--;
				
			}
		}*/
		return res;
	}
	
	public LinkedList<Histogram> getMyBinsFromAtt(EObject me) {
		LinkedList<Histogram> res = new LinkedList<Histogram>();
		
		if(me!=null)
		{
		for (Histogram histogramAss : this.hitogramsForAttributes) {
			
		OCLForJAVA.init("",me);
		Histogram container = histogramAss.getEmptyClone();
		EAttribute att = ((EAttribute)container.getElem());
		EClass c = (EClass) att.getEContainingClass();
		Collection<EObject> newRoots = new LinkedList<EObject>();
		EList<EObject> insideMe = new BasicEList<EObject>();
		extractEveryThingFromRoot(me, insideMe);
		for (EObject obj : insideMe) {	
			EClass myClass = ((EClass)obj.eClass());
			boolean pass = false;
			if(myClass.getName().equals(c.getName()))
			pass = true;
			else {
				 EList<EClass> fouch2 = myClass.getESuperTypes();
				 for (EClass eClass : fouch2) {
					if(eClass.getName().equals(c.getName()))
					pass=true;
				}
				 if(pass==false)
				 {
					 EList<EClass> fouch1 = c.getESuperTypes();
					 for (EClass eClass : fouch1) {
						if(eClass.getName().equals(c.getName()))
						pass=true;
					}
				 }
			}
			
		   
			if(pass)
			if(!newRoots.contains(obj))
			newRoots.add(obj);
		}
			for (int i = 0; i < container.values.size(); i++) {
				String bin = container.values.get(i);
				
				for (EObject root : newRoots) {
				OCLForJAVA.init("",root);
				String toBeEvaluated="0";
					  if(!bin.contains(".."))
					  toBeEvaluated = "self."+att.getName()+" = "+bin;
					  else 
					  {
						  try{
						  	String value=bin.trim().replace("[", "").replace("]", "").replace("..", "-");
							String [] temp=value.split("-");
							double start=-1;
							double end=-1;
							start= Double.valueOf(temp[0].trim());
							end= Double.valueOf(temp[1].trim());
						    toBeEvaluated = "self."+att.getName()+" >= "+start+" and self."+att.getName()+" <= "+end;
						  }
						  catch(Exception w){w.printStackTrace();}
						
					  }
						boolean belong = OCLForJAVA.evaluateBoolean(root, toBeEvaluated, "belong");
						if(belong)
						{
							container.probabilities.set(i, String.valueOf(Double.valueOf(container.probabilities.get(i))+1));
							container.frequencies.set(i, container.frequencies.get(i)+1);
						}
				
				}
			
			}
		res.add(container);
		}
		}
		
		/*for (int i = 0; i < res.size(); i++) {
			if(res.get(i).isEmpty())
			{res.remove(i);
			i--;
				
			}
		}*/
		
		
		return res;
	}
	
	public LinkedList<HistogramClass> getMyBinsFromClass(EObject me) {
		LinkedList<HistogramClass> res = new LinkedList<HistogramClass>();
		
		if(me!=null)
		{
		for (HistogramClass histogramClass : hitogramsForClasses) {
		HistogramClass container = histogramClass.getEmptyClone();
		if(histogramClass.callingRef==null&&histogramClass.root.getName().equals("Tax_Case"))
		{

			for (int i = 0; i < container.values.size(); i++) {
			EClass c = container.values.get(i);
			String toBeEvaluated = "self.oclIsKindOf("+c.getName()+")";
			OCLForJAVA.init("",me);
			try {
			boolean belong = OCLForJAVA.evaluateBoolean(me, toBeEvaluated, "temp");
			if(belong)
			{
				container.probabilities.set(i, container.probabilities.get(i)+1);
				container.frequencies.set(i, container.frequencies.get(i)+1);
			}
			} catch (Exception e) {e.printStackTrace();}
			}
		
		}
		else 
		{
			OCLForJAVA.init("",me);
			for (int i = 0; i < container.values.size(); i++) {
				if(container.values.size()>1)
				{
					
				
				EClass c = container.values.get(i);
				if(container.callingRef!=null)
				 c=container.callingRef.getEContainingClass();
				if(!container.condition.trim().equalsIgnoreCase("true"))
				c=container.callingRef.getEContainingClass();
			
				
				LinkedList<EObject> newRoots = new LinkedList<EObject>();
				EList<EObject> insideMe = new BasicEList<EObject>();
				extractEveryThingFromRoot(me, insideMe);
				for (EObject obj : insideMe) {	
					EClass myClass = ((EClass)obj.eClass());
					boolean pass = false;
					if(myClass.getName().equals(c.getName()))
					pass = true;
					else {
						 EList<EClass> fouch2 = myClass.getESuperTypes();
						 for (EClass eClass : fouch2) {
							if(eClass.getName().equals(c.getName()))
							pass=true;
						}
						 if(pass==false)
						 {
							 EList<EClass> fouch1 = c.getESuperTypes();
							 for (EClass eClass : fouch1) {
								if(eClass.getName().equals(c.getName()))
								pass=true;
							}
						 }
					}
					
				   
					if(pass)
					if(!newRoots.contains(obj))
					newRoots.add(obj);
				}
				
				c = container.values.get(i);
				
					if(!container.condition.trim().equalsIgnoreCase("true"))
						for (int j = 0; j < newRoots.size(); j++) {
							EObject eObject = newRoots.get(j);
							OCLForJAVA.init("",eObject);
							boolean test = OCLForJAVA.evaluateBoolean(eObject, container.condition.replace("self.", ""), "test");
							if(test==false)
							{
								newRoots.remove(j);
								j--;
							}
						}
					
					
					for (EObject root : newRoots) {
						String toBeEvaluated="0";
					
				    	 if(container.callingRef!=null)
				    	toBeEvaluated = "self."+container.callingRef.getName()+".oclAsSet()->iterate(inc;  acc:Bag("+container.callingRef.getEType().getName()+")=Bag{}  |"
				    			+ " if(inc.oclIsKindOf("+c.getName()+")) then acc->including(inc) else acc endif)->size()";
				    	 else toBeEvaluated = "self.oclIsKindOf("+c.getName()+")";

						OCLForJAVA.init("",root);
						
						 if(container.callingRef==null)
						 {
						try {
						boolean belong = OCLForJAVA.evaluateBoolean(root, toBeEvaluated, "temp");
						if(belong)
						{
							container.probabilities.set(i, container.probabilities.get(i)+1);
							container.frequencies.set(i, container.frequencies.get(i)+1);
						}
						} catch (Exception e) {e.printStackTrace();} 
						}
						 else {
								try {
							 int count = OCLForJAVA.evaluateIntOut(root, toBeEvaluated, "count");
							 container.probabilities.set(i, container.probabilities.get(i)+count);
							 container.frequencies.set(i, (int)(container.frequencies.get(i)+count));
								} catch (Exception e) {e.printStackTrace();} 
						 }
						}
				    
				
				}
			
			}
			
		}
		res.add(container);
		}
	}
		
		/*for (int i = 0; i < res.size(); i++) {
			if(res.get(i).isEmpty())
			{res.remove(i);
			i--;
				
			}
		}*/
		return res;
	}

	
}
