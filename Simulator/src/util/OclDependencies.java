
package util;


import java.util.LinkedList;
import java.util.HashMap;
import java.util.List;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EParameter;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.ocl.Environment;
import org.eclipse.ocl.ecore.CallOperationAction;
import org.eclipse.ocl.ecore.Constraint;
import org.eclipse.ocl.ecore.EcoreEnvironmentFactory;
import org.eclipse.ocl.ecore.OCL;
import org.eclipse.ocl.ecore.SendSignalAction;
import org.eclipse.ocl.expressions.BooleanLiteralExp;
import org.eclipse.ocl.expressions.CollectionItem;
import org.eclipse.ocl.expressions.CollectionLiteralExp;
import org.eclipse.ocl.expressions.IntegerLiteralExp;
import org.eclipse.ocl.expressions.IteratorExp;
import org.eclipse.ocl.expressions.NullLiteralExp;
import org.eclipse.ocl.expressions.OperationCallExp;
import org.eclipse.ocl.expressions.PropertyCallExp;
import org.eclipse.ocl.expressions.RealLiteralExp;
import org.eclipse.ocl.expressions.StringLiteralExp;
import org.eclipse.ocl.expressions.TypeExp;
import org.eclipse.ocl.expressions.Variable;
import org.eclipse.ocl.expressions.VariableExp;
import org.eclipse.ocl.helper.OCLHelper;
import org.eclipse.ocl.utilities.AbstractVisitor;
import org.eclipse.ocl.utilities.ExpressionInOCL;
import org.eclipse.ocl.utilities.UMLReflection;




public class OclDependencies extends AbstractVisitor<String, EClassifier, EOperation, EStructuralFeature, EEnumLiteral, EParameter, EObject, CallOperationAction, SendSignalAction, Constraint> {

  public static Environment<?, EClassifier, EOperation, EStructuralFeature, EEnumLiteral, EParameter, EObject, CallOperationAction, SendSignalAction, Constraint, ?, ?> env = null;
  public static  UMLReflection<?, EClassifier, EOperation, EStructuralFeature, EEnumLiteral, EParameter, EObject, CallOperationAction, SendSignalAction, Constraint> uml;
  public static OCL ocl;
  public static OCLHelper<EClassifier, EOperation, EStructuralFeature, Constraint> helper;
  public static EmfModelReader reader;
  public static LinkedList<EClass> allClasses;
  public static LinkedList<EOperation> listeEAllOperation = new LinkedList<EOperation>();
  
  public static LinkedList<EObject> orderedListe = new LinkedList<EObject>();
  public static LinkedList<EClass> listeEClass = new LinkedList<EClass>();
  public static LinkedList<EOperation> listeEOperation = new LinkedList<EOperation>();
  public static LinkedList<EAttribute> listeEAttribute = new LinkedList<EAttribute>();
  public static LinkedList<EEnum> listeEnum = new LinkedList<EEnum>();
  public static LinkedList<EReference> listeEReference = new LinkedList<EReference>();
  public static HashMap<String, LinkedList<EObject>> refrencesNeeds ;
  public static HashMap<String, LinkedList<EObject>> attributesNeeds ;
  
  public static EReference ref=null;
  public static EAttribute att=null;
  public static EClass classe=null;
 
  
	public static void safeAdd(LinkedList<EObject> actualNeeds, LinkedList<EObject> additionalNeeds)
	{
		for (EObject eObject : additionalNeeds) {
			if(!actualNeeds.contains(eObject))
				actualNeeds.add(eObject);
		}
	}
  
  public static EReference getReferenceByName(EClass c, EReference ref)
  {
	  EReference res=null;
	  int i=0;
	  boolean find = false;
	  EList<EReference> liste = c.getEAllReferences();
	  while(i<liste.size()&&find==false)
	  {
		  if(liste.get(i).getName().equals(ref.getName()))
		  {
			  find=true;
			  res=liste.get(i);
		  }
		  else i++;
	  }
	  return res;
  }
  
  
  public static EAttribute getAttributeByName(EClass c, EAttribute att)
  {
	  EAttribute res=null;
	  int i=0;
	  boolean find = false;
	  EList<EAttribute> liste = c.getEAllAttributes();
	  while(i<liste.size()&&find==false)
	  {
		  if(liste.get(i).getName().equals(att.getName()))
		  {
			  find=true;
			  res=liste.get(i);
		  }
		  else i++;
	  }
	  return res;
  }
  
  public static EClass getClassByName(LinkedList<EClass> liste, EClass c)
  {
	  EClass res=null;
	  int i=0;
	  boolean find = false;
	  while(i<liste.size()&&find==false)
	  {
		  if(liste.get(i).getName().equals(c.getName()))
		  {
			  find=true;
			  res=liste.get(i);
		  }
		  else i++;
	  }
	  return res;
  }


public static OclDependencies getInstance() {
	    
	    Environment<EPackage, EClassifier, EOperation, EStructuralFeature, EEnumLiteral, EParameter, EObject, CallOperationAction, SendSignalAction, Constraint, EClass, EObject> auxEnv = EcoreEnvironmentFactory.INSTANCE.createEnvironment();    
	    return new OclDependencies(auxEnv);
	  }

  public static OclDependencies getInstance(
      Environment<?, EClassifier, EOperation, EStructuralFeature, EEnumLiteral, EParameter, EObject, CallOperationAction, SendSignalAction, Constraint, ?, ?> env) {
	  return new OclDependencies(env);
  }  
  

  @SuppressWarnings("static-access")
protected OclDependencies(Environment<?, EClassifier, EOperation, EStructuralFeature, EEnumLiteral, EParameter, EObject, CallOperationAction, SendSignalAction, Constraint, ?, ?> env) {
	    this.env = env;
	    uml = (env == null) ? null : env.getUMLReflection();
  }

  public static void updateNeeds(EObject element,EObject toBeAdded)
  {
	  if(element instanceof EReference)
	  {
		  LinkedList<EObject> actualneeds= refrencesNeeds.get(element.toString());
		if(actualneeds==null)
			actualneeds=new LinkedList<EObject>();
		int initalSize=actualneeds.size();
		addClean(actualneeds, toBeAdded);
		int finalSize=actualneeds.size();
		if(finalSize!=initalSize)
		{
		refrencesNeeds.remove(element.toString());
		refrencesNeeds.put(element.toString(), actualneeds);
		}
	  }
	  
	  if(element instanceof EAttribute)
	  {
		  LinkedList<EObject> actualneeds= attributesNeeds.get(element.toString());
		if(actualneeds==null)
			actualneeds=new LinkedList<EObject>();
		int initalSize=actualneeds.size();
		addClean(actualneeds, toBeAdded);
		int finalSize=actualneeds.size();
		if(finalSize!=initalSize)
		{
		attributesNeeds.remove(element.toString());
		attributesNeeds.put(element.toString(), actualneeds);
		}
	  }
  }
  
  public static void addClean (LinkedList<EObject> liste, EObject temp)
  {
	  if(!liste.contains(temp))
	  liste.add(temp);
	  
	
  }
  
  public static void addEOperation (LinkedList<EOperation> liste, EOperation temp)
  {
	  if(!liste.contains(temp))
	   liste.add(temp);
	  
	
  }
  
  public void addEClass (LinkedList<EClass> liste, EClass temp)
  {
	  if(!liste.contains(temp))
	  {
		  liste.add(temp);
		  orderedListe.add(temp);
			 if(ref!=null)
			updateNeeds(ref,temp);
			 if(att!=null)
			updateNeeds(att,temp);

	
	  }
  }

  public void addEReference (LinkedList<EReference> liste, EReference temp)
  {
	  if(!liste.contains(temp))
	  {
		  liste.add(temp);
		  orderedListe.add(temp);
		  if(ref!=null)
		  updateNeeds(ref,temp);
		  if(att!=null)
		  updateNeeds(att,temp);
	  }
  }
  
  public void addEAttribute (LinkedList<EAttribute> liste, EAttribute temp)
  {
	  if(!liste.contains(temp))
	  {
		  liste.add(temp);
		  orderedListe.add(temp);
		  if(ref!=null)
		   updateNeeds(ref,temp);
		  if(att!=null)
		 updateNeeds(att,temp);
	  }
  }
  
  public static void addEEnum (LinkedList<EEnum> liste, EEnum temp)
  {
	  if(!liste.contains(temp))
	  liste.add(temp);
	  
	
  }
  

  
	public static boolean notExistIn(EOperation op, List <EOperation> listeOperations)
	{boolean res = true;
	int i = 0;
	while(res==true&&i<listeOperations.size())
	{EOperation temp = listeOperations.get(i);
	
		if(compareOperation(temp,op))
			res=false;
		else i++;
	}
	return res;
	}


  @Override
  public String visitExpressionInOCL(ExpressionInOCL<EClassifier, EParameter> expression) {
	  //pass
    return super.visitExpressionInOCL(expression);
    
  }

  @Override
  public String visitIntegerLiteralExp(IntegerLiteralExp<EClassifier> literalExp) {
	//pass

    return super.visitIntegerLiteralExp(literalExp);
  }

  @Override
  public String visitRealLiteralExp(RealLiteralExp<EClassifier> literalExp) {
	//pass
    return super.visitRealLiteralExp(literalExp);
  }

  @Override
  public String visitStringLiteralExp(StringLiteralExp<EClassifier> literalExp) {
	//pass
    return super.visitStringLiteralExp(literalExp);
  }

  @Override
  public String visitBooleanLiteralExp(BooleanLiteralExp<EClassifier> literalExp) {
	//pass
	  
    return super.visitBooleanLiteralExp(literalExp);
  }

  @Override
  public String visitTypeExp(TypeExp<EClassifier> t) { //done - classes
	 
	 if (t.getReferredType() instanceof EClass)
     addEClass(listeEClass, (EClass) t.getReferredType());
 
    return super.visitTypeExp(t);
  }  
  
 

  @Override
  protected String handleCollectionItem(CollectionItem<EClassifier> item, String itemResult) {
	  //done - CollectionItem 
	  if(item.getType() instanceof EClass)
	  addEClass(listeEClass, (EClass)item.getType());

	  
    return super.handleCollectionItem(item, itemResult);
  }

  @Override
  protected String handleCollectionLiteralExp(CollectionLiteralExp<EClassifier> literalExp, List<String> partResults) {
   //item handelled in handleCollectionItem 
    return super.handleCollectionLiteralExp(literalExp, partResults);
  }  

  @Override
  public String visitVariableExp(VariableExp<EClassifier, EParameter> v) {
    //done
    return super.visitVariableExp(v);
  }

  @Override
  protected String handleVariable(Variable<EClassifier, EParameter> variable, String initResult) {
	    //done
    return super.handleVariable(variable, initResult);
  }

  @Override
  protected String handlePropertyCallExp(PropertyCallExp<EClassifier, EStructuralFeature> callExp, String sourceResult, List<String> qualifierResults) {    
	
	  if (callExp.getReferredProperty().eContainer() instanceof EClass)
    addEClass(listeEClass, (EClass) callExp.getReferredProperty().eContainer());
   
    if(callExp.getReferredProperty().getEType() instanceof EClass)
     addEClass(listeEClass, (EClass) callExp.getReferredProperty().getEType());
 
     if(callExp.getReferredProperty().getEType() instanceof EEnum)
	  addEEnum(listeEnum, (EEnum) callExp.getReferredProperty().getEType());
	 
     
     
	     if(callExp.getReferredProperty() instanceof EAttribute)
	 { 
	    	 
	 
	 EAttribute att = (EAttribute) callExp.getReferredProperty();
	
	 if(listeEAttribute.contains(att)==false)
	 {
     addEAttribute(listeEAttribute, att);
  
   
	 if(att.isDerived())
	 { 
	    EList<EAnnotation> annotationList = att.getEAnnotations();
		EClass context = att.getEContainingClass();
	
		if (annotationList != null)
			for (EAnnotation ea : annotationList)
			{
				if (ea.getSource().endsWith("Pivot"))
				{
					
					String val = ea.getDetails().get("derivation");	
					try{
				   helper.setAttributeContext(context, att);	   
				   helper.createQuery(val.trim()).accept(this);
					}catch (Exception e)
					{;e.printStackTrace();}									    
					
				}
				
				
			}
	 }
	 }
	 
	 
	 }
	 if(callExp.getReferredProperty() instanceof EReference)
		 addEReference(listeEReference, (EReference) callExp.getReferredProperty() );
	 
		 return super.handlePropertyCallExp(callExp, sourceResult, qualifierResults);
  }  

  @Override
  public String visitIteratorExp(IteratorExp<EClassifier, EParameter> callExp) {
	 if(callExp.getIterator().get(0) instanceof EClass)
	addEClass(listeEClass, (EClass) callExp.getIterator().get(0));

	
  
    return super.visitIteratorExp(callExp);
    
    
  }
  
  @Override
  public String visitNullLiteralExp(NullLiteralExp<EClassifier> literalExp) {
	//  done
	
    return super.visitNullLiteralExp(literalExp);
  }

@Override
  protected String handleIteratorExp(IteratorExp<EClassifier, EParameter> callExp, String sourceResult, List<String> variableResults, String bodyResult) {    
	 if(callExp.getIterator().get(0) instanceof EClass)
	addEClass(listeEClass, (EClass) callExp.getIterator().get(0));
	
   return super.handleIteratorExp(callExp, sourceResult, variableResults, bodyResult);

  }

  @Override
  protected String handleConstraint(Constraint constraint, String specificationResult) {
	  //done
	 return super.handleConstraint(constraint, specificationResult);
  }

  @Override
  protected String handleExpressionInOCL(ExpressionInOCL<EClassifier, EParameter> callExp, String contextResult, String resultResult, List<String> parameterResults, String bodyResult) {
	  
	  return super.handleExpressionInOCL(callExp, contextResult, resultResult, parameterResults, bodyResult);    
	  
  }
  
  
  @Override
  protected String handleOperationCallExp(OperationCallExp<EClassifier, EOperation> callExp, String sourceResult, List<String> argumentResults) {
	    
	
	    EOperation op = callExp.getReferredOperation();
	    
	    if(notExistIn(op,listeEAllOperation)==false)
	    {// recursive situations
	    	
	    	if(notExistIn(op, listeEOperation))
	    	{
	    
	    addEOperation(listeEOperation, op);	


	    
	    if (op.eContainer() instanceof EClass)
	        addEClass(listeEClass, (EClass) op.eContainer());

	    
	    if (op.getEContainingClass() instanceof EClass)
	        addEClass(listeEClass, (EClass) op.getEContainingClass());

	   
	    if (op.getEType() instanceof EClass)
	        addEClass(listeEClass, (EClass) op.getEType());

	 

	    	    // Add the overwrriden operations although we do not have them
	  
				EList<EAnnotation> annotationList = op.getEAnnotations();
				EClass context = op.getEContainingClass();
				if (annotationList != null)
					for (EAnnotation ea : annotationList)
						if (ea.getSource().endsWith("Pivot"))
						{
						
							String val = ea.getDetails().get("body");	
							try{
						   helper.setOperationContext(context, op);
						   
						   helper.createQuery(val.trim()).accept(this);
						   
						   handelOverwrriden(op,context);
						   
							}catch (Exception e)
							{;e.printStackTrace();}									    
							
						}
	    }
			
	    }			
	    

	    
	//  System.out.println(callExp +"-->"+sourceResult);
    return super.handleOperationCallExp(callExp, sourceResult, argumentResults);
  }
  

  public static boolean compareOperation(EOperation op1,EOperation op2)
  {

	  return op1.getName().trim().equals(op2.getName().trim()) && op1.getEType().getName().toString().trim().equals(op2.getEType().getName().toString().trim()) && compareParameters(op1.getEParameters(),op2.getEParameters())&& ((EClass) op1.eContainer()).getName().trim().equals(((EClass) op2.eContainer()).getName().trim());
  }
  
  //Support for operations retrieving 
  
  public void handelOverwrriden(EOperation op, EClass context)
  {
	  LinkedList<EClass> subTypes = (LinkedList<EClass>)reader.getClassSubtypes(allClasses, context);
	  
	  if (subTypes != null)
	  for (int i = 0; i < subTypes.size(); i++) {
		  LinkedList<EOperation> operations = (LinkedList<EOperation>)(reader.getClassOperations((EClass)subTypes.get(i)));
		  if (operations !=null)
		  {
			 boolean res=false;
			 int x=0;
			 while(x<operations.size()&&res==false)
			 { if(operations.get(x).getName().equals(op.getName())&&operations.get(x).getEType().equals(op.getEType()))
				 res=true;
				 x++;
			 }
			       if(res)
				  if (compareParameters(op.getEParameters(), operations.get(i).getEParameters()))
				  {	    EOperation op1=operations.get(i);
					    EList<EAnnotation> annotationList = op1.getEAnnotations();
						EClass context1 = op1.getEContainingClass();
						if (annotationList != null)
							for (EAnnotation ea : annotationList)
								if (ea.getSource().endsWith("Pivot"))
								{
								
									String val = ea.getDetails().get("body");	
								
									try{
										 
								   helper.setOperationContext(context1, op1);
								  
								   helper.createQuery(val).accept(this);
								   
								   handelOverwrriden(op1,context1);
								   
									}catch (Exception e)
									{;e.printStackTrace();}									    
									
								}
				  }
	  }
  }}

 public static boolean  compareParameters(EList<EParameter> liste1 ,EList<EParameter> liste2 )
 {
	 boolean res= true;
	 
	 if(liste1.size()!=liste2.size())
		 return false;
	 else 
	 {
	 int i=0;
	 while (i<liste1.size() && res)
	 {
		 int j=0;
		 boolean find = false;
		 while (j<liste2.size()&&find==false)
		 {
			 if(liste1.get(i).toString().equals(liste2.get(j).toString()))
				 find=true;
			 j++;
		 }
		 
		 if (find==false)
			 res=false;
		 i++;
	 }
	 }
	 return res;
 }

}

