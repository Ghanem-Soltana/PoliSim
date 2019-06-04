package Monitor;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;

import util.OclAST;

public class DiffUtil {

	
	public EObject target;
	public EObject mutated;
	public EList<DiffContainer> diffrences;
	
	public DiffUtil() {

		this.diffrences = new  BasicEList<DiffContainer>();
	}

	public DiffUtil(EObject target, EObject mutated) {
		super();
		this.target = target;
		this.mutated = mutated;
		this.diffrences = new  BasicEList<DiffContainer>();
	}
	
	public DiffUtil(EObject target, EObject mutated, EList<DiffContainer> diffrences) {
		super();
		this.target = target;
		this.mutated = mutated;
		this.diffrences = diffrences;
	}
	
	public EObject getTarget() {
		return target;
	}
	public void setTarget(EObject target) {
		this.target = target;
	}
	public EObject getMutated() {
		return mutated;
	}
	public void setMutated(EObject mutated) {
		this.mutated = mutated;
	}
	public EList<DiffContainer> getDiffrences() {
		return diffrences;
	}
	public void setDiffrences(EList<DiffContainer> diffrences) {
		this.diffrences = diffrences;
	} 
	

	public void doDif()
	{   diffrences.clear();
		exploreRoot(mutated,target,new BasicEList<EObject>(),null,diffrences);
		
	}
	
	
	@SuppressWarnings("unchecked")
	public static void exploreRoot(EObject rootMutated, EObject rootDesired, EList<EObject> visited, EObject callingObj, EList<DiffContainer> diffrences2)
	{   if(rootMutated!=null && rootDesired !=null)
		if(!visited.contains(rootMutated))
		{ visited.add(rootMutated);
		EClass myClass = rootMutated.eClass();
		if(myClass!=null)
		{
			
		EClass desiredClass = rootDesired.eClass();
		if(desiredClass!=myClass)
		{
			DiffContainer temp = new DiffContainer(rootMutated, rootDesired, null, myClass.getName(), desiredClass.getName());
			diffrences2.add(temp);
		}
			
			
			
			
		EList<EAttribute> myAttributes = myClass.getEAllAttributes();
		for (EAttribute eAttribute : myAttributes) {
			if(eAttribute.isID()==false)
			try{
			Object valueMutated = rootMutated.eGet(eAttribute);
			Object valueDesired = rootDesired.eGet(eAttribute);
			if(valueMutated!=null&&valueDesired!=null)
			if(OclAST.getClassByName(OclAST.allClasses, eAttribute.getEType().getName())==null)
			{
			if(!valueMutated.toString().equals(valueDesired.toString()))
			{
				DiffContainer temp = new DiffContainer(rootMutated, rootDesired, (EStructuralFeature)eAttribute, valueMutated.toString(), valueDesired.toString());
				diffrences2.add(temp);
			}
			}
			else {
				
				exploreRoot((EObject)valueMutated, (EObject) valueDesired, visited, rootMutated,diffrences2);
			}
			}catch(Exception w){//w.printStackTrace();
			}
		}
		
		 EList<EReference> myReferences = myClass.getEAllReferences();
		 
		 for (EReference ref : myReferences) 
			try{
				System.out.println(ref);
			if(!ref.isMany())
			{   EObject mutOb = (EObject)rootMutated.eGet(ref);
			    EObject DesiredOb = (EObject)rootDesired.eGet(ref);
			    
			    if(mutOb!=null && DesiredOb!=null)
			    exploreRoot(mutOb, DesiredOb, visited, rootMutated,diffrences2);
			    else
			    {
			    	if(mutOb==null && DesiredOb==null)
			    	return;
			    	if(mutOb==null)
			    	{DiffContainer temp = new DiffContainer(rootMutated, rootDesired, (EStructuralFeature)ref, "null", DesiredOb.toString());
					diffrences2.add(temp);}
			    	else 
			    	{DiffContainer temp = new DiffContainer(rootMutated, rootDesired, (EStructuralFeature)ref, mutOb.toString(), "null");
					diffrences2.add(temp);}
			    }

			}
			else {
				List<EObject> newObs = (List<EObject>)rootMutated.eGet(ref);
				List<EObject> newObsDesired = (List<EObject>)rootDesired.eGet(ref);
				if(newObs.size()!=newObsDesired.size())
				{	
				DiffContainer temp = new DiffContainer(rootMutated, rootDesired, (EStructuralFeature)ref, String.valueOf(newObs.size()), String.valueOf(newObsDesired.size()));
				diffrences2.add(temp);
					
				}
					
					
				for (EObject mutOb : newObs) {
					EObject DesiredOb = getCorrespondentEObjectById(rootDesired,newObsDesired,mutOb);
				    if(mutOb!=null && DesiredOb!=null)
					    exploreRoot(mutOb, DesiredOb, visited, rootMutated,diffrences2);
					    else
					    {   if(mutOb==null && DesiredOb==null)
					    	return;
					    	if(mutOb==null)
					    	{DiffContainer temp = new DiffContainer(rootMutated, rootDesired, (EStructuralFeature)ref, "null", DesiredOb.toString());
							diffrences2.add(temp);}
					    	else 
					    	{DiffContainer temp = new DiffContainer(rootMutated, rootDesired, (EStructuralFeature)ref, mutOb.toString(), "null");
							diffrences2.add(temp);}
					    }

				}
			}
		}
		 catch(Exception w){}
		 
		}
		}
	}
	
	
	private static EObject getCorrespondentEObjectById(EObject rootDesired, List<EObject> newObsDesired, EObject mutOb) {

		EList<EAttribute> myAtts = mutOb.eClass().getEAllAttributes();
		boolean find = false;
		int i =0;
		EAttribute theID = null;
		while (i<myAtts.size()&&find==false)
		{
			if(myAtts.get(i).isID())
			{
				find = true;
				theID = myAtts.get(i);
			}
			else i++;
		}
		
		if(theID==null)
		return null;
		else 
		{
			Object id = mutOb.eGet(theID);
			if(id!=null)
			{
		    find = false;
		    i=0;
		    while (i<newObsDesired.size()&&find==false) {
				if(newObsDesired.get(i).eGet(theID)!=null)
				if(newObsDesired.get(i).eGet(theID).toString().trim().equals(id.toString()))
				{return newObsDesired.get(i);}
				 i++;
			}
			}
		    return null;
		}
	}

	@SuppressWarnings("deprecation")
	public static Object castValue(EAttribute att, String value)
	{   
		 String stringType = att.getEType().getName();
     	if (stringType.equals("EString"))
			return new String(value);
     	else 
     		if (stringType.contains("EChar"))
     			return new String(value).charAt(0);
    	if (stringType.contains("Char"))
 			return new String(value).charAt(0);
		else if (stringType.contains("EBoolean"))
				return Boolean.valueOf(value);
		else if (stringType.contains("Boolean"))
			return Boolean.valueOf(value);
		else if (stringType.contains("EByte"))
			   return Byte.valueOf(value);
		else if (stringType.equals("EBigInteger"))
				return new BigInteger(BigInteger.valueOf(Double.valueOf(value).longValue()).toByteArray());
		else if (stringType.equals("EBigDecimal"))
				return new BigDecimal(value.toCharArray());
		else if (stringType.contains("ELong"))
			return Long.parseLong(value);
		else  if (stringType.contains("EDouble"))
			return Double.valueOf(value);
		else  if (stringType.contains("Double"))
			return Double.valueOf(value);
		else  if (stringType.contains("Real"))
			return Double.valueOf(value);
		else if (stringType.contains("EInt"))
			return Double.valueOf(value).intValue();
		else if (stringType.contains("int"))
			return Double.valueOf(value).intValue();
    	else if (stringType.contains("Integer"))
			return Double.valueOf(value).intValue();
    		else  if(stringType.toLowerCase().contains("date"))
    			return new Date ("1/1/2014");
    	// no need for dates I will split them up
    	else if(att.getEType() instanceof EEnum)
    		{ 
    		if(!value.contains("#"))
    			value = att.getEType().getName() +"#"+value;
    		String tab [] = value.split("#");
    		String enumName = tab[0];
    		String LiteralName = tab[1];

    		EEnum enumeration = OclAST.getEnumerationByName(OclAST.allEnumerations,enumName);
    		EEnumLiteral res = enumeration.getEEnumLiteralByLiteral(LiteralName);
    		return res;

  		}
    	
		else return null;
         
	}
	

}
