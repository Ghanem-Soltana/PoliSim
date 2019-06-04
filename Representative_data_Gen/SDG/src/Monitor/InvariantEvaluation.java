package Monitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.ocl.common.OCLCommon;
import org.eclipse.ocl.ecore.Constraint;
import org.eclipse.ocl.utilities.ExpressionInOCL;

import Main.GenerateSeeds;
import snt.oclsolver.distance.ClassDiagramTestData;
import snt.oclsolver.eocl.EclipseOCLWrapper;
import util.CreateTaxpayers;
import util.Invariant;
import util.OCLForJAVA;
import util.OclAST;

@SuppressWarnings("unused")
public class InvariantEvaluation {
	
	EObject theObject;
	List<Invariant> constraitns;
	List<String> resultats;

	public InvariantEvaluation(EObject theObject) {
		super();
		this.theObject = theObject;
		this.constraitns = new LinkedList<Invariant>();
		this.resultats =  new LinkedList<String>();
	}
	
	public InvariantEvaluation(EObject theInstance, List<Invariant> invariants) {
		super();
		this.theObject = theInstance;
		this.constraitns = new ArrayList<Invariant>();
		this.constraitns.addAll(invariants);
		this.resultats =  new LinkedList<String>();
		for (Invariant inv : invariants) {
			this.resultats.add("false");		
		}
	}


	public EObject getTheObject() {
		return theObject;
	}
	public void setTheObject(EObject theObject) {
		this.theObject = theObject;
	}
	public List<Invariant> getConstraitns() {
		return constraitns;
	}
	public void setConstraitns(List<Invariant> constraitns) {
		this.constraitns = constraitns;
		resultats.clear();
		for (Invariant constraint : constraitns) {
			resultats.add("false");
		}
		
	}
	public List<String> getResultats() {
		return resultats;
	}
	public void setResultats(List<String> resultats) {
		this.resultats = resultats;
	}
	
	public String toString()
	{
		String res = "";
		res = res + theObject +"\n";
		int i = 0;
		for (int j = 0; j < constraitns.size(); j++) {	
		i++;
		Invariant constraint = constraitns.get(j);
		res = res+"\n Invariant " + i +"\n";
		res = res+ "["+constraint.getContext().getName()+"]\n";
		res = res+  constraint.getexpression()+"\n";
		res = res+ "RES= "+resultats.get(j)+"\n";

		}
		return res;
	}
	
	public int getNBOk()
	{int res=0;
		for (String temp : resultats) {
			if(temp.equals("true"))
				res++;
		}
		return res;
	}
	
	public int getNBFails()
	{
		int all = resultats.size();
		return all - getNBOk();
	}
	
	public LinkedList<Invariant> getUnsatisfiedConstraints()
	{
		LinkedList<Invariant> res = new LinkedList<Invariant>();
		for (int i = 0; i < resultats.size(); i++) {
			String r = resultats.get(i);
			if(!r.equals("true"))
			res.add(constraitns.get(i));
		}
		return res;
	}

	public LinkedList<Invariant> getSatisfiedConstraints()
	{
		LinkedList<Invariant> res = new LinkedList<Invariant>();
		for (int i = 0; i < resultats.size(); i++) {
			String r = resultats.get(i);
			if(r.equals("true"))
			res.add(constraitns.get(i));
		}
		return res;
	}

	public void updateEvaluation() {
		int pos = -1;
		for (Invariant c : constraitns) {
			//System.out.println(c);
			 pos++;
          	 EClass contextCls = (EClass)ProfileConstraintExtractor.getEVersion(c.getContext());
         	 EList<EObject>  sub = extractReleventObjects2(theObject,contextCls);
         	 boolean test = true;
         	 if(sub!=null)
         	 {
         	 Iterator<EObject> iterator = sub.iterator();
			OCLForJAVA.newInstance();
         	 while(iterator.hasNext()&&test==true)
         	 {
         		 EObject tempEObject = iterator.next();
           		OCLForJAVA.init("",tempEObject);

         		 try{
         			 
         		 String expr =c.getexpression();
         		 if(expr!=null)
         		 test = CreateTaxpayers.evaluateQueryClean(tempEObject, expr).equals("true");

         		 }catch(Exception e){e.getCause();}
         	 }
         	resultats.set(pos, String.valueOf(test));
         	 }
         	 else resultats.set(pos, String.valueOf(true));
		}
		
	}
	


	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void extractEveryThingFromRoot(EObject me, EList<EObject> res)
	{
		
		if(!res.contains(me))
		{
			res.add(me);
			try{
			EClass classe = me.eClass();
			EList<EReference> refs = classe.getEAllContainments();
			for (EReference ref: refs) {
				BasicEList<EObject> insideMe = new BasicEList<EObject>();

			if(ref.isMany())
			{List temp = (List)me.eGet(ref);
			if(temp!=null)
				insideMe.addAll(temp);}
			else 
			{EObject actualObject = (EObject)me.eGet(ref);
			if(actualObject!=null)
			insideMe.add(actualObject);}
			for (EObject eObject : insideMe) {
				
				extractEveryThingFromRoot(eObject,res);
				
			}
			}
			
			}catch(Exception e){e.printStackTrace();}
		}
	
	}
	
	
	private EList<EObject> extractReleventObjects2(EObject me, EClass c) {
		EList<EObject>  res = new BasicEList<EObject>();
		OCLForJAVA.newInstance();
		OCLForJAVA.init("",me);
		String toBeEvaluated = c.getName()+".allInstances()";
		Collection<EObject> temp = OCLForJAVA.evaluateECollection(me, toBeEvaluated, "test", c.getName(), "Set");
		res.addAll(temp);
		
		return res;
	}

	private EList<EObject> extractReleventObjects(EObject me, EClass c) {
		EList<EObject>  res = new BasicEList<EObject>();
		EList<EObject> insideMe = new BasicEList<EObject>();
		extractEveryThingFromRoot(me, insideMe);
		for (EObject obj : insideMe) {	
			EClass myClass = ((EClass)obj.eClass());
			boolean pass = false;
			if(myClass.getName().equals(c.getName()))
			pass = true;
			else {
				 EList<EClass> fouch2 = new BasicEList<EClass>();
				 getAllSuperTypes(myClass,fouch2);
				 for (EClass eClass : fouch2) {
					if(eClass.getName().equals(c.getName()))
					{
					pass=true;
					}
				}
				
			}
			
		   
			if(pass)
			res.add(obj);
		}
			return res;
	}



	public void getAllSuperTypes(EClass c,EList<EClass> res)
	{
		
		int initSize = res.size();
		if(!res.contains(c))
		res.add(c);
	
		EList<EClass> fouch2 = c.getESuperTypes();
		fouch2.removeAll(res);
		res.addAll(fouch2);
		int newSize = res.size();
		if(initSize!=newSize)
			for (int i = initSize==0?0:initSize-1; i < res.size(); i++) {
			EClass eClass=res.get(i);
	   getAllSuperTypes(eClass,res);
		
		}
	}

public static void diplayList (List<?> in)
{
	System.out.println("**Start display");
	for (Object object : in) {
		if(object instanceof Constraint)
		{Constraint c = (Constraint)object;
		System.out.println( ((EClass)c.getConstrainedElements().get(0)).getName()+"--"+c.getSpecification().getBodyExpression().toString());
		}
		else 
		System.out.println(object);
	}
	System.out.println("**End display");
}

public boolean isValid() {
	return getNBFails()==0;
}
}
