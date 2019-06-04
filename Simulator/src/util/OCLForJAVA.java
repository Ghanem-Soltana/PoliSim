package util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EParameter;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.ocl.ParserException;
import org.eclipse.ocl.Query;
import org.eclipse.ocl.cst.CSTNode;
import org.eclipse.ocl.expressions.ExpressionsFactory;
import org.eclipse.ocl.expressions.OCLExpression;
import org.eclipse.ocl.expressions.Variable;
import org.eclipse.ocl.parser.OCLAnalyzer;

import adapters.umlImpl.EDatatypeUtil;

import org.eclipse.ocl.util.TypeUtil;




public class OCLForJAVA {
	
public static ArrayList<Container> queue = new ArrayList<Container>();
public static HashMap<String, String>cacheDuplicant = new HashMap<String, String>();
public static HashMap<String, String>cacheIsRefTOSelf = new HashMap<String, String>();
public static HashMap<String, String>cacheIsRefTOName = new HashMap<String, String>();
public static HashMap<String, String>cacheReplaceByName= new HashMap<String, String>();
public static HashMap<String, EAttribute>cacheAttributesByName= new HashMap<String, EAttribute>();
public static ArrayList<UpdateContainer> modifiedFields=new ArrayList<UpdateContainer>();
public static HashMap<String, String> collectionOCL = new HashMap<String, String>();
public static HashMap<String, String> objectOCL = new HashMap<String, String>();
public static HashMap<String, String> cacheCollectionOCL = new HashMap<String, String>();
public static HashMap<String, String> cacheObjectOCL = new HashMap<String, String>();


public static ArrayList<String> fieldsNames=new ArrayList<String>();

public static String ADName="";

public static void newInstance()
{
	cacheDuplicant = new HashMap<String, String>();
	cacheIsRefTOSelf = new HashMap<String, String>();
	cacheIsRefTOName = new HashMap<String, String>();
	cacheReplaceByName= new HashMap<String, String>();
	cacheAttributesByName= new HashMap<String, EAttribute>();
	modifiedFields=new ArrayList<UpdateContainer>();
	cacheCollectionOCL = new HashMap<String, String>();
	cacheObjectOCL = new HashMap<String, String>();
	ADName="";

}


public static Collection<EObject> evaluateECollection(String toBeEvaluated,String inputName, String containedType, String colelctionType)
{

	Collection<EObject>  res1=new LinkedHashSet<EObject>();
	toBeEvaluated=replaceCollections(toBeEvaluated);
	toBeEvaluated=replaceObjects(toBeEvaluated);
	
	try {
	OCLExpression<EClassifier> query=null;
	query = OclAST.helper.createQuery(toBeEvaluated);
	Query<EClassifier, EClass, EObject> eval = OclAST.ocl.createQuery(query);
	Object res=eval.evaluate();


	res1=(Collection<EObject>)res;

	// create a variable declaring 
	 Variable<EClassifier, EParameter> var =ExpressionsFactory.eINSTANCE.createVariable();
	 var.setName(inputName);
	 if(colelctionType.equals("Sequence"))
	 var.setType(TypeUtil.resolveSequenceType(OclAST.env, OclAST.ocl.getEnvironment().getTypeResolver().resolve(OclAST.getClassByName(OclAST.allClasses, containedType))));
	 else  if(colelctionType.equals("Bag"))
		 var.setType(TypeUtil.resolveBagType(OclAST.env,  OclAST.ocl.getEnvironment().getTypeResolver().resolve(OclAST.getClassByName(OclAST.allClasses, containedType))));
	 else  if(colelctionType.equals("Set"))
		 var.setType(TypeUtil.resolveSetType(OclAST.env,  OclAST.ocl.getEnvironment().getTypeResolver().resolve(OclAST.getClassByName(OclAST.allClasses, containedType))));
	 else  if(colelctionType.equals("OrderedSet"))
		 var.setType(TypeUtil.resolveOrderedSetType(OclAST.env,  OclAST.ocl.getEnvironment().getTypeResolver().resolve(OclAST.getClassByName(OclAST.allClasses, containedType))));
	 
 
	 // add it to the global OCL environment
	 try{
	 OclAST.ocl.getEnvironment().addElement(inputName, var, true);
	 }catch(Exception e){e.printStackTrace();}

	 // add variable to evalaution Envirement
	 try{
	 if(OclAST.ocl.getEvaluationEnvironment().getValueOf(inputName)==null)
	 OclAST.ocl.getEvaluationEnvironment().add(inputName,res);
	 }
	 catch(Exception e)
	 {OclAST.ocl.getEvaluationEnvironment().replace(inputName,res);
	 //e.printStackTrace();
	 }
	 
	} catch (Exception e) {
	System.err.println("OCL++> "+toBeEvaluated);
	e.printStackTrace();
	}
	return res1;
}

//Evaluation 
public static Collection<EObject> evaluateECollection(Collection<EObject> context,String toBeEvaluated,String inputName, String containedType, String colelctionType)
{

	Collection<EObject>  res1=new LinkedHashSet<EObject>();
	toBeEvaluated=replaceCollections(toBeEvaluated);
	toBeEvaluated=replaceObjects(toBeEvaluated);
	
	try {
	OCLExpression<EClassifier> query=null;
	query = OclAST.helper.createQuery(toBeEvaluated);

	Object res=OclAST.ocl.evaluate(context, query);


	res1=(Collection<EObject>)res;

	// create a variable declaring 
	 Variable<EClassifier, EParameter> var =ExpressionsFactory.eINSTANCE.createVariable();
	 var.setName(inputName);
	 if(colelctionType.equals("Sequence"))
	 var.setType(TypeUtil.resolveSequenceType(OclAST.env, OclAST.ocl.getEnvironment().getTypeResolver().resolve(OclAST.getClassByName(OclAST.allClasses, containedType))));
	 else  if(colelctionType.equals("Bag"))
		 var.setType(TypeUtil.resolveBagType(OclAST.env,  OclAST.ocl.getEnvironment().getTypeResolver().resolve(OclAST.getClassByName(OclAST.allClasses, containedType))));
	 else  if(colelctionType.equals("Set"))
		 var.setType(TypeUtil.resolveSetType(OclAST.env,  OclAST.ocl.getEnvironment().getTypeResolver().resolve(OclAST.getClassByName(OclAST.allClasses, containedType))));
	 else  if(colelctionType.equals("OrderedSet"))
		 var.setType(TypeUtil.resolveOrderedSetType(OclAST.env,  OclAST.ocl.getEnvironment().getTypeResolver().resolve(OclAST.getClassByName(OclAST.allClasses, containedType))));
	 
 
	 // add it to the global OCL environment
	 try{
	 OclAST.ocl.getEnvironment().addElement(inputName, var, true);
	 }catch(Exception e){e.printStackTrace();}

	 // add variable to evalaution Envirement
	 try{
	 if(OclAST.ocl.getEvaluationEnvironment().getValueOf(inputName)==null)
	 OclAST.ocl.getEvaluationEnvironment().add(inputName,res);
	 }
	 catch(Exception e)
	 {OclAST.ocl.getEvaluationEnvironment().replace(inputName,res);
	 e.printStackTrace();
	 }
	 
	} catch (Exception e) {
	System.err.println("OCL++> "+toBeEvaluated);
	e.printStackTrace();
	}
	return res1;
}

@SuppressWarnings("unchecked")
public static Collection<EObject> evaluateECollection(EObject context,String toBeEvaluated,String inputName, String containedType, String colelctionType)
{

	Collection<EObject>  res1=new LinkedHashSet<EObject>();
	toBeEvaluated=replaceCollections(toBeEvaluated);
	toBeEvaluated=replaceObjects(toBeEvaluated);
	collectionOCL.put(ADName+"#"+inputName, toBeEvaluated);
	
	try {
	OCLExpression<EClassifier> query=null;
	query = OclAST.helper.createQuery(toBeEvaluated);

	Object res=OclAST.ocl.evaluate(context, query);


	res1=(Collection<EObject>)res;

	// create a variable declaring 
	 Variable<EClassifier, EParameter> var =ExpressionsFactory.eINSTANCE.createVariable();
	 var.setName(inputName);
	 if(colelctionType.equals("Sequence"))
	 var.setType(TypeUtil.resolveSequenceType(OclAST.env, OclAST.ocl.getEnvironment().getTypeResolver().resolve(OclAST.getClassByName(OclAST.allClasses, containedType))));
	 else  if(colelctionType.equals("Bag"))
		 var.setType(TypeUtil.resolveBagType(OclAST.env,  OclAST.ocl.getEnvironment().getTypeResolver().resolve(OclAST.getClassByName(OclAST.allClasses, containedType))));
	 else  if(colelctionType.equals("Set"))
		 var.setType(TypeUtil.resolveSetType(OclAST.env,  OclAST.ocl.getEnvironment().getTypeResolver().resolve(OclAST.getClassByName(OclAST.allClasses, containedType))));
	 else  if(colelctionType.equals("OrderedSet"))
		 var.setType(TypeUtil.resolveOrderedSetType(OclAST.env,  OclAST.ocl.getEnvironment().getTypeResolver().resolve(OclAST.getClassByName(OclAST.allClasses, containedType))));
	 
 
	 // add it to the global OCL environment
	 try{
	 OclAST.ocl.getEnvironment().addElement(inputName, var, true);
	 }catch(Exception e){e.printStackTrace();}

	 // add variable to evalaution Envirement
	 try{
	 if(OclAST.ocl.getEvaluationEnvironment().getValueOf(inputName)==null)
	 OclAST.ocl.getEvaluationEnvironment().add(inputName,res);
	 }
	 catch(Exception e)
	 {OclAST.ocl.getEvaluationEnvironment().replace(inputName,res);
	 e.printStackTrace();
	 }
	 
	} catch (Exception e) {
	System.err.println("OCL++> "+toBeEvaluated);
	e.printStackTrace();
	}
	return res1;
}






public static EClass getType(String name)
{     EClass res=null;
      if(name.equals(""))
    	  return res;
	  LinkedList<EClass> classes = OclAST.allClasses;
	  for (EClass eClass : classes) {
		if(name.trim().equals(eClass.getName()))
			return eClass;
	}
	  return res;
}

	public static EObject evaluateEObject(EObject context,String toBeEvaluated,String inputName, String type_Name)
	{
		
		EObject res1=null;
		toBeEvaluated=replaceCollections(toBeEvaluated);
		toBeEvaluated=replaceObjects(toBeEvaluated);
		objectOCL.put(ADName+"#"+inputName, toBeEvaluated);
		
try {
	OCLExpression<EClassifier> query=null;
	query = OclAST.helper.createQuery(toBeEvaluated);
	Object res=OclAST.ocl.evaluate(context, query);
	res1=(EObject) res;
	
	
	//OCLAnalyzer a = new OCLAnalyzer(OclAST.ocl.getEnvironment(), "context Tax_Payer inv inv1:"+toBeEvaluated.trim());
	//CSTNode csTree = a.parseConcreteSyntax();
	//a.parseAST(cst, ConstraintKind.in);
	
		// create a variable declaring 
		 Variable<EClassifier, EParameter> var =ExpressionsFactory.eINSTANCE.createVariable();
		 var.setName(inputName);
		 EClass type=getType(type_Name);
		 if(type!=null)
		 var.setType(type);

		 
		 
		 if(res1!=null)
		 {
			 if(res1.eContainer()!=null)
			 {
		 if( res1.eContainer().eClass().getName().equals("EEnum")==false)
		 var.setType(OclAST.ocl.getEnvironment().getTypeResolver().resolve(OclAST.getClassByName(OclAST.allClasses, res1.eContainer().eClass().getName())) );
		 else 
		 {
			 String ch= res1.eContainer().toString();
			 String name= ch.substring(ch.indexOf(":")+1,ch.indexOf(")")).trim();
			 var.setType(OclAST.ocl.getEnvironment().getTypeResolver().resolve(OclAST.getEnumerationByName(OclAST.allEnumerations, name)) );
		 }
			 }
		 }
	  
		 // add it to the global OCL environment
		 try{
		 OclAST.ocl.getEnvironment().addElement(inputName, var, true);
		 }catch(Exception e){}

		 // add variable to evalaution Envirement
		 try{
			 if(OclAST.ocl.getEvaluationEnvironment().getValueOf(inputName)==null)
			 OclAST.ocl.getEvaluationEnvironment().add(inputName,res);
		 }
		 catch(Exception e)
		 {OclAST.ocl.getEvaluationEnvironment().replace(inputName,res);}

		
   
} catch (Exception e) {
	System.err.println("OCL++> "+toBeEvaluated);
	e.printStackTrace();
}
	return res1;
	}

	
	public static String evaluateString(EObject context,String toBeEvaluated,String inputName)
	{ String res1="";
	toBeEvaluated=replaceCollections(toBeEvaluated);
	toBeEvaluated=replaceObjects(toBeEvaluated);
	try {
		OCLExpression<EClassifier> query=null;
		query = OclAST.helper.createQuery(toBeEvaluated);
		 Object res = OclAST.ocl.evaluate(context, query);
		
		res1=String.valueOf(res);
		
		 if( OclAST.helper.getOCL().getEvaluationEnvironment().getValueOf(inputName)==null)
			 try {
				 OclAST.helper.getOCL().getEvaluationEnvironment().add(inputName,res);
			} catch (IllegalArgumentException e) {
				 OclAST.helper.getOCL().getEvaluationEnvironment().replace(inputName,res);
			}
			
	    	 else  OclAST.helper.getOCL().getEvaluationEnvironment().replace(inputName, res);
		 
		// create a variable declaring 
		 Variable<EClassifier, EParameter> var =ExpressionsFactory.eINSTANCE.createVariable();
		 var.setName(inputName);
		 var.setType(EDatatypeUtil.convertFromString("String"));
	   
		 // add it to the global OCL environment
		 try{
		
		 OclAST.ocl.getEnvironment().addElement(inputName, var, true);
		 }catch(Exception e){}

		 // add variable to evalaution Envirement
		 try{

		if( OclAST.ocl.getEvaluationEnvironment().getValueOf(inputName)==null)
		 OclAST.ocl.getEvaluationEnvironment().add(inputName,res);
		 }
		 catch(Exception e)
		 {OclAST.ocl.getEvaluationEnvironment().replace(inputName,res);}

	} catch (Exception e) {
		System.err.println("OCL++> "+toBeEvaluated);
		e.printStackTrace();
	}

	return res1;
	}	

public static int evaluateInt(EObject context,String toBeEvaluated,String inputName)
{ int res1=0;
toBeEvaluated=replaceCollections(toBeEvaluated);
toBeEvaluated=replaceObjects(toBeEvaluated);
try {
	OCLExpression<EClassifier> query=null;
	query = OclAST.helper.createQuery(toBeEvaluated);
	 Object res = OclAST.ocl.evaluate(context, query);
	
	res1=Double.valueOf(String.valueOf(res)).intValue();
	
	 if( OclAST.helper.getOCL().getEvaluationEnvironment().getValueOf(inputName)==null)
		 OclAST.helper.getOCL().getEvaluationEnvironment().add(inputName,res);
    	 else  OclAST.helper.getOCL().getEvaluationEnvironment().replace(inputName, res);
	 
	// create a variable declaring 
	 Variable<EClassifier, EParameter> var =ExpressionsFactory.eINSTANCE.createVariable();
	 var.setName(inputName);
	 var.setType(EDatatypeUtil.convertFromString("EInt"));
   
	 // add it to the global OCL environment
	 try{
	 OclAST.ocl.getEnvironment().addElement(inputName, var, true);
	 }catch(Exception e){}

	 // add variable to evalaution Envirement
	 try{
		 if(OclAST.ocl.getEvaluationEnvironment().getValueOf(inputName)==null)
	 OclAST.ocl.getEvaluationEnvironment().add(inputName,res);
	 }
	 catch(Exception e)
	 { 
		OclAST.ocl.getEvaluationEnvironment().replace(inputName,res);}

} catch (Exception e) {
	System.err.println("OCL++> "+toBeEvaluated);
	e.printStackTrace();
}

return res1;
}




public static double evaluateDouble(EObject context,String toBeEvaluated,String inputName)
{ double res1=0;
toBeEvaluated=replaceCollections(toBeEvaluated);
toBeEvaluated=replaceObjects(toBeEvaluated);
try {
	OCLExpression<EClassifier> query=null;
	query = OclAST.helper.createQuery(toBeEvaluated);
	Object res=OclAST.ocl.evaluate(context, query);
	res1=Double.valueOf(String.valueOf(res)).doubleValue();
	
	// create a variable declaring 
		 Variable<EClassifier, EParameter> var =ExpressionsFactory.eINSTANCE.createVariable();
		 var.setName(inputName);
		 var.setType(EDatatypeUtil.convertFromString("EDouble"));
	   
		 // add it to the global OCL environment
		 try{
		 OclAST.ocl.getEnvironment().addElement(inputName, var, true);
		 }catch(Exception e){}

		 try{
		 if( OclAST.ocl.getEvaluationEnvironment().getValueOf(inputName)==null)
		 OclAST.ocl.getEvaluationEnvironment().add(inputName,res);
		 }
		 catch(Exception e)
		 {OclAST.ocl.getEvaluationEnvironment().replace(inputName,res);}
   
} catch (Exception e) {
//if(ADName.equals("")=false){
	System.out.println("OCL++> "+toBeEvaluated);
	e.printStackTrace();
//}
}


return res1;
}

public static boolean evaluateBoolean(EObject context,String toBeEvaluated,String inputName)
{ boolean res1=false;

toBeEvaluated=replaceCollections(toBeEvaluated);
toBeEvaluated=replaceObjects(toBeEvaluated);
try {
	OCLExpression<EClassifier> query=null;
	query = OclAST.helper.createQuery(toBeEvaluated);
	Object res=OclAST.ocl.evaluate(context, query);
	String ch=String.valueOf(res);
	if(ch.trim().equals("true"))
		res1=true;

	
	// create a variable declaring 
	 Variable<EClassifier, EParameter> var =ExpressionsFactory.eINSTANCE.createVariable();
	 var.setName(inputName);
	 var.setType(EDatatypeUtil.convertFromString("boolean"));
  
	 // add it to the global OCL environment
	 try{
	 OclAST.ocl.getEnvironment().addElement(inputName, var, true);
	 }catch(Exception e){e.printStackTrace();}

	 try{
     if(OclAST.ocl.getEvaluationEnvironment().getValueOf(inputName)==null)
	 OclAST.ocl.getEvaluationEnvironment().add(inputName,res);
	 }
	 catch(Exception e)
	 {OclAST.ocl.getEvaluationEnvironment().replace(inputName,res);
	// e.printStackTrace();
	 }

   
} catch (Exception e) {
	System.err.println("OCL++> "+toBeEvaluated);
	e.printStackTrace();}

return res1;
}





//Updates
public static void updateEFeature(EObject context, String feature, String value) {
	String ocl=feature.substring(0,feature.lastIndexOf("."));
	EObject temp= evaluateEObject(context, ocl,"updateEFeature","");
	EClass classe=temp.eClass();
	String attributeName=feature.substring(feature.lastIndexOf(".")+1);

	String old=OCLForJAVA.evaluateString(context, feature, "old");
	UpdateContainer modif=new UpdateContainer(feature, context, null, "", String.valueOf(value),"boolean",old);
	if(modifiedFields.contains(modif))
	{    old=new String(modifiedFields.get(modifiedFields.indexOf(modif)).getOld());
		 modifiedFields.remove(modif);
	}   else old=new String(String.valueOf(evaluateBoolean(context, feature,""))); 
	     modif.setOld(old);
		 modifiedFields.add(modif);
	
	if(fieldsNames.contains(feature)==false)
				fieldsNames.add(feature);
	temp.eSet(getEAttributeByName(classe,attributeName), value);

}
public static void updateEFeature(EObject context, String feature, EObject value) {
	String ocl=feature.substring(0,feature.lastIndexOf("."));
	EObject temp= evaluateEObject(context, ocl,"updateEFeature","");
	EClass classe=temp.eClass();
	String attributeName=feature.substring(feature.lastIndexOf(".")+1);

	String old=OCLForJAVA.evaluateString(context, feature, "old");
	UpdateContainer modif=new UpdateContainer(feature, context, null, "", String.valueOf(value),"boolean",old);
	if(modifiedFields.contains(modif))
	{    old=new String(modifiedFields.get(modifiedFields.indexOf(modif)).getOld());
		 modifiedFields.remove(modif);
	}   else old=new String(String.valueOf(evaluateBoolean(context, feature,""))); 
	     modif.setOld(old);
		 modifiedFields.add(modif);
	
	if(fieldsNames.contains(feature)==false)
				fieldsNames.add(feature);
	temp.eSet(getEReferenceByName(classe,attributeName), value);

}


public static void updateEFeature(EObject context, String feature, boolean value) {
	String ocl=feature.substring(0,feature.lastIndexOf("."));
	EObject temp= evaluateEObject(context, ocl,"updateEFeature","");
	EClass classe=temp.eClass();
	String attributeName=feature.substring(feature.lastIndexOf(".")+1);

	String old=OCLForJAVA.evaluateString(context, feature, "old");
	UpdateContainer modif=new UpdateContainer(feature, context, null, "", String.valueOf(value),"boolean",old);
	if(modifiedFields.contains(modif))
	{    old=new String(modifiedFields.get(modifiedFields.indexOf(modif)).getOld());
		 modifiedFields.remove(modif);
	}   else old=new String(String.valueOf(evaluateBoolean(context, feature,""))); 
	     modif.setOld(old);
		 modifiedFields.add(modif);
	
	if(fieldsNames.contains(feature)==false)
				fieldsNames.add(feature);
	temp.eSet(getEAttributeByName(classe,attributeName), value);

}



public static void updateEFeature(EObject context, String feature, int value) {
	String ocl=feature.substring(0,feature.lastIndexOf("."));
	EObject temp= evaluateEObject(context, ocl,"updateEFeature","");
	EClass classe=temp.eClass();
	String attributeName=feature.substring(feature.lastIndexOf(".")+1);
	
	String old=OCLForJAVA.evaluateString(context, feature, "old");
	UpdateContainer modif=new UpdateContainer(feature, context, null, "", String.valueOf(value),"int",old);
	if(modifiedFields.contains(modif))
	{    old=new String(modifiedFields.get(modifiedFields.indexOf(modif)).getOld());
		 modifiedFields.remove(modif);
	}    else old=new String(String.valueOf(evaluateInt(context, feature,"")));
	     modif.setOld(old);
		 modifiedFields.add(modif);
	temp.eSet(getEAttributeByName(classe,attributeName), value);
	
	if(fieldsNames.contains(feature)==false)
				fieldsNames.add(feature);

	}







public static void updateEFeature(EObject context, String feature, double value) {
	String ocl=feature.substring(0,feature.lastIndexOf("."));
	
	EObject temp= evaluateEObject(context, ocl,"","");
	EClass classe=temp.eClass();
	String attributeName=feature.substring(feature.lastIndexOf(".")+1);
	
	String old=OCLForJAVA.evaluateString(context, feature, "old");
	EObject obj= evaluateEObject(context, feature.substring(0,feature.indexOf(".")),"updateEFeature","");
	UpdateContainer modif=new UpdateContainer(feature, context, obj, "", String.valueOf(value),"double",old);
	if(modifiedFields.contains(modif))
	{    old=new String(modifiedFields.get(modifiedFields.indexOf(modif)).getOld());
		 modifiedFields.remove(modif);
	}   else old=new String(String.valueOf(evaluateDouble(context, feature,""))); 
	     modif.setOld(old);
		 modifiedFields.add(modif);
	
	if(fieldsNames.contains(feature)==false)
				fieldsNames.add(feature);
	temp.eSet(getEAttributeByName(classe,attributeName), value);
	}



public static EAttribute getEAttributeByName(EClass c,String name)
{ EAttribute res=null;

EAttribute cut=cacheAttributesByName.get(c.toString()+"#"+name);

if(cut!=null)
return cut;

for (EAttribute att : c.getEAllAttributes()) {
if(att.getName().trim().equals(name.trim()))	
{cacheAttributesByName.put(c.toString()+"#"+name,att);
return att;
}
}

return res;
}

public static EReference getEReferenceByName(EClass c,String name)
{ EReference res=null;



for (EReference att : c.getEAllReferences()) {
if(att.getName().trim().equals(name.trim()))	
{
return att;
}
}

return res;
}

//Operations



public static Object evaluate(String toBeEvaluated, EObject obj)
{
	OCLExpression<EClassifier> query=null;
try {
	query = OclAST.helper.createQuery(toBeEvaluated);
	return OclAST.ocl.evaluate(obj, query);
} catch (ParserException e) 
{System.err.println("OCL++> "+toBeEvaluated); 
System.err.println("Object++> "+obj); 
e.printStackTrace();}
return null;
}

public static boolean containsRef(String name,String toBeEvaluated)
{
	boolean res=false;
	for (int i = 0; i < queue.size(); i++) {
		if(queue.get(i).getItteratorName().equals(name)==false&&isReferringToName(queue.get(i).getItteratorName(), toBeEvaluated))
			return true;
	}
	return res;
}

//with cache
public static boolean isReferringToSelf(String toBeEvaluated)
{
boolean res=false;
String name="self";

String cut=cacheIsRefTOSelf.get(ADName+"#"+toBeEvaluated);
if(cut!=null)
return cut.equals("true");

if(toBeEvaluated.contains(name)==false)
{	cacheIsRefTOSelf.put(ADName+"#"+toBeEvaluated,String.valueOf(res));
	return res;
}


int index=0;
	while (index<toBeEvaluated.length()&&index!=-1) {
		index=toBeEvaluated.indexOf(name,index);
		
		if(index!=-1)
		if(isValidName(name,toBeEvaluated,index))
			return true;
		else index++;
			
	}	

cacheIsRefTOSelf.put(ADName+"#"+toBeEvaluated,String.valueOf(res));
return res;
}


//with cache
public static String replaceCollections(String toBeEvaluated)
{  
	
	
String cut=cacheCollectionOCL.get(ADName+"#"+toBeEvaluated);
if(cut!=null)
return cut;

	String res=toBeEvaluated;
	boolean stop=false;
	while(stop==false)
	{stop=true;
	for(Map.Entry<String, String> entry : collectionOCL.entrySet()){
		String id=entry.getKey().substring(entry.getKey().indexOf('#')+1);
		if(isReferringToName(id, res))
		{
			res=replaceByNameInteligent (res,id,entry.getValue());
			stop=false;
		}
	}
	}

	
	cacheCollectionOCL.put(ADName+"#"+toBeEvaluated, res);
	
	return res;
}


//with cache
public static String replaceObjects(String toBeEvaluated)
{  
	
if(ADName.equals(""))
	return toBeEvaluated;
String cut=cacheObjectOCL.get(ADName+"#"+toBeEvaluated);
if(cut!=null)
return cut;

	String res=toBeEvaluated;
	boolean stop=false;
	while(stop==false)
	{stop=true;
	for(Map.Entry<String, String> entry : objectOCL.entrySet()){
		String id=entry.getKey().substring(entry.getKey().indexOf('#')+1);
		if(isReferringToNameObject(id, res))
		{
			res=replaceByNameInteligent (res,id,entry.getValue());
			stop=false;
		}
	}
	}

	
	cacheObjectOCL.put(ADName+"#"+toBeEvaluated, res);
	
	return res;
}


public static String replaceByNameInteligent(String toBeEvaluated,String name, String oclCollection)
{
	  
	if(toBeEvaluated.contains(name)==false)
		return toBeEvaluated;
	
 
	int index=0;
	while (index<toBeEvaluated.length()&&index!=-1) {
		index=toBeEvaluated.indexOf(name,index);
		
		if(index!=-1)
		if(isValidName(name,toBeEvaluated,index))
		{   
			String p1="",p2="";
			if(index!=0)
			p1=toBeEvaluated.substring(0,index);
			if(index+name.length()<toBeEvaluated.length())
			p2=toBeEvaluated.substring(index+name.length());

				
			String temp="";
			temp=p1+oclCollection+p2;
			toBeEvaluated=temp;
			index=0;
		}
		else index++;
			
	}	
	
	  	
	return toBeEvaluated;
	
	}

//with cache

/*public static String replaceBySelf(String toBeEvaluated,String name)
{
	 String res="";
	 String savtoBeEvaluated=new String (toBeEvaluated);
	 String cut=cacheReplaceByName.get(ADName+"#"+savtoBeEvaluated+"#"+name);
	  if(cut!=null)
		  return cut;
	  
	if(toBeEvaluated.contains(name)==false)
	{	cacheReplaceByName.put(ADName+"#"+savtoBeEvaluated+"#"+name, toBeEvaluated);
		return toBeEvaluated;
	}
 
	int index=0;
	while (index<toBeEvaluated.length()&&index!=-1) {
		index=toBeEvaluated.indexOf(name,index);
		
		if(index!=-1)
		if(isValidName(name,toBeEvaluated,index))
		{   
			String p1="",p2="";
			if(index!=0)
			p1=toBeEvaluated.substring(0,index);
			if(index+name.length()<toBeEvaluated.length())
			p2=toBeEvaluated.substring(index+name.length());

				
			String temp="";
			temp=p1+"self"+p2;
			toBeEvaluated=temp;
			index=0;
		}
		else index++;
			
	}	
	
	  	String [] decoup = res.split(" in ");
		
	if(decoup.length==1)
	{cacheReplaceByName.put(ADName+"#"+savtoBeEvaluated+"#"+name, toBeEvaluated);
	return toBeEvaluated;
	}
	else
	{
	res=decoup[decoup.length-1];
	for (int i = 0; i < decoup.length-1; i++) {
		res=decoup[i]+" in "+res;
	}
	}
	cacheReplaceByName.put(ADName+"#"+savtoBeEvaluated+"#"+name, res);
	return res;
	}
	*/



public static boolean isValidName(String name,String ocl,int index)
{
	boolean res=true;

	if(index!=0)
	res=isOkChar(ocl.charAt(index-1))&&ocl.charAt(index-1)!='.';
	
	if(res)
	if(index+name.length()<ocl.length())
	 res=res&&isOkChar(ocl.charAt(index+name.length()));
			
	
	return res;
}

public static boolean isOkChar(char c)
{
	boolean res=true;
	if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z'))
	    return false;

	if (c>= '0' && c<= '9')
		return false;

	if(c=='_'||c=='@')
		return false;
    
	return res;
}

/*public static int getPosition(EObject obj, Collection<EObject> liste)
{
	int res=-1;
	Iterator<EObject> it = liste.iterator();
	while (it.hasNext()) {
		res++;
		EObject eObject = (EObject) it.next();
		if(eObject.equals(obj))
			return res;
	}

	return -1;
}*/

/*public static String createDynamicOCL(String name,String toBeEvaluated)
{
	String res="";
	boolean stop=false;

	int i=0;
	while (stop==false&&i<queue.size()) {
   
		if(isReferringToName(queue.get(i).getItteratorName(), toBeEvaluated))
		{ 	Container container=queue.get(i);
		    String cleanDef=removeDuplicatedDefenition(container.getCollectionOCL(),res);
			res=res+cleanDef;
			int pos=container.getI();
			res=res+" let "+container.getItteratorName()+":"+container.getType().replace("OrderedSet(", "").replace("Set(", "").replace(")", "")+"="+container.getCollectionName()+"->at("+pos+") in ";
		}

  if(queue.get(i).getItteratorName().equals(name))
	stop=true;
   	i++;
	}
	
	return res+" "+toBeEvaluated;
}
*/

//with cache
public static String removeDuplicatedDefenition(String newOCl,String ocl)
{String res="";

String cut=	cacheDuplicant.get(ADName+"#"+newOCl+"#"+ocl);
if(cut!=null)
 return cut;

if(ocl.equals(""))
{
cacheDuplicant.put(ADName+"#"+newOCl+"#"+ocl, newOCl);
return newOCl;
}


String tabNew[]=newOCl.split(" in");
String tabOld[]=ocl.split(" in");

for (int i = 0; i < tabNew.length; i++) {
	int j=0;
	boolean find = false;
	while(j<tabOld.length&&find==false)
	{
		if(tabOld[j].trim().equals(tabNew[i].trim()))		
			find=true;
		else j++;
	}
	
	if(find==false)
	{
		res=res+tabNew[i]+" in ";
	}
}
cacheDuplicant.put(ADName+"#"+newOCl+"#"+ocl, res);
return res;
}





//with cache
public static boolean isReferringToName(String name, String toBeEvaluated)
{
boolean res=false;

String cut= cacheIsRefTOName.get(ADName+"#"+name+"#"+toBeEvaluated);
if(cut!=null)
return cut.equals("true");

	
if(toBeEvaluated.contains(name)==false)
{	cacheIsRefTOName .put(ADName+"#"+name+"#"+toBeEvaluated, "false");
	return false;
}



int index=0;
	while (index<toBeEvaluated.length()&&index!=-1) {
		index=toBeEvaluated.indexOf(name,index);
		
		if(index!=-1)
		if(isValidName(name,toBeEvaluated,index))
			return true;
		else index++;
			
	}	
cacheIsRefTOName.put(ADName+"#"+name+"#"+toBeEvaluated, String.valueOf(res));
return res;
}



public static Container getContext(String ocl)
{ 
Container res=null;
int i=0;
while(i<queue.size())
{  if(isReferringToName(queue.get(i).getItteratorName(),ocl))
	return queue.get(i);
		else
	i++;
}
return res;
}


public static boolean isReferringToNameObject(String name, String toBeEvaluated)
{
boolean res=false;
if(name.trim().equals(""))
	return false;
int index=0;
	while (index<toBeEvaluated.length()&&index!=-1) {
		index=toBeEvaluated.indexOf(name,index);
		
		if(index!=-1)
		if(isValidName(name,toBeEvaluated,index))
			return true;
		else index++;
			
	}	
return res;
}




/*public static int getIdFromCollection(EObject obj, Collection<EObject> liste)
{
	int res=-1;

	Iterator<EObject> itt = liste.iterator();
	while (itt.hasNext()) {
		res++;
		EObject temp= itt.next();
		if(temp.equals(obj))
			return res;
		
		
	}
	
	if(res==liste.size())
		return -1;
	
	return res;
}
*/

public static int firstTime( ArrayList<Container> queue,Container x)
{
	for (int i = 0; i < queue.size(); i++) {
	if(queue.get(i).getItteratorName().equals(x.getItteratorName())&&queue.get(i).getCollectionName().equals(x.getCollectionName()))
		return i;
	}
	return -1;
}

public static void newIteration(Container x)
{   int pos=firstTime(queue,x);
	if(pos==-1)
	queue.add(x);
	else
	{
	queue.set(pos, x);
	}	
	
	String ItteratorName=x.getItteratorName();
	EObject itterator=x.getItterator();
	
			 // create a variable declaring 
			 Variable<EClassifier, EParameter> var =ExpressionsFactory.eINSTANCE.createVariable();
			 var.setName(ItteratorName);
			 var.setType(itterator.eClass());
		   
			 // add it to the global OCL environment
			 try{
			 OclAST.ocl.getEnvironment().addElement(ItteratorName, var, true);
			 }catch(Exception e){}

			 // add variable to evalaution Envirement
			 if(OclAST.ocl.getEvaluationEnvironment().getValueOf(ItteratorName)==null)
			 OclAST.ocl.getEvaluationEnvironment().add(ItteratorName,itterator);
			 else 
			 OclAST.ocl.getEvaluationEnvironment().replace(ItteratorName,itterator);
}

public static void init(String cuurentAd)
{
ADName=cuurentAd;
queue=new ArrayList<Container>();
collectionOCL= new HashMap<String, String>();
objectOCL= new HashMap<String, String>();
}

public static void init(String cuurentAd,EObject input)
{
OclAST.ocl.dispose();
OclAST.helper.setContext(input.eClass());
ADName=cuurentAd;
queue=new ArrayList<Container>();}




public static void iterationExit()
{queue.remove(queue.size()-1);}




public static Collection<EObject> evaluateECollection(EList<EObject> elements, String toBeEvaluated, String inputName,
		String containedType, String colelctionType) {

	Collection<EObject>  res1=new LinkedHashSet<EObject>();
	toBeEvaluated=replaceCollections(toBeEvaluated);
	toBeEvaluated=replaceObjects(toBeEvaluated);
	collectionOCL.put(ADName+"#"+inputName, toBeEvaluated);
	
	try {
	OCLExpression<EClassifier> query=null;
	query = OclAST.helper.createQuery(toBeEvaluated);

	Object res=OclAST.ocl.evaluate(elements, query);

	

	// create a variable declaring 
	 Variable<EClassifier, EParameter> var =ExpressionsFactory.eINSTANCE.createVariable();
	 var.setName(inputName);
	 if(colelctionType.equals("Sequence"))
	 var.setType(TypeUtil.resolveSequenceType(OclAST.env, OclAST.ocl.getEnvironment().getTypeResolver().resolve(OclAST.getClassByName(OclAST.allClasses, containedType))));
	 else  if(colelctionType.equals("Bag"))
		 var.setType(TypeUtil.resolveBagType(OclAST.env,  OclAST.ocl.getEnvironment().getTypeResolver().resolve(OclAST.getClassByName(OclAST.allClasses, containedType))));
	 else  if(colelctionType.equals("Set"))
		 var.setType(TypeUtil.resolveSetType(OclAST.env,  OclAST.ocl.getEnvironment().getTypeResolver().resolve(OclAST.getClassByName(OclAST.allClasses, containedType))));
	 else  if(colelctionType.equals("OrderedSet"))
		 var.setType(TypeUtil.resolveOrderedSetType(OclAST.env,  OclAST.ocl.getEnvironment().getTypeResolver().resolve(OclAST.getClassByName(OclAST.allClasses, containedType))));
	 
 
	 // add it to the global OCL environment
	 try{
	 OclAST.ocl.getEnvironment().addElement(inputName, var, true);
	 }catch(Exception e){}

	 // add variable to evalaution Envirement
	 try{
	 OclAST.ocl.getEvaluationEnvironment().add(inputName,res);
	 }
	 catch(Exception e)
	 {OclAST.ocl.getEvaluationEnvironment().replace(inputName,res);}
	 
	} catch (Exception e) {
	System.err.println("OCL++> "+toBeEvaluated);
	e.printStackTrace();
	}
	return res1;
}



}
