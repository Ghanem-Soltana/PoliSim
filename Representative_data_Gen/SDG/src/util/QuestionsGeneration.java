package util;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.LinkedList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.ocl.ParserException;
import org.eclipse.ocl.ecore.OCL;
import org.eclipse.ocl.expressions.OCLExpression;
import org.eclipse.ocl.helper.OCLHelper;
import org.eclipse.ocl.utilities.ExpressionInOCL;
import org.eclipse.uml2.uml.Association;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Constraint;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.Enumeration;
import org.eclipse.uml2.uml.EnumerationLiteral;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.Stereotype;

import adapters.EPackageAdapter;


public class QuestionsGeneration {


	public static  LinkedList<EObject> all;
	public static EList<org.eclipse.uml2.uml.Element> realAll;
	public static  LinkedList<org.eclipse.uml2.uml.Element> realClasses;
	public static  LinkedList<org.eclipse.uml2.uml.Element> realEnumerations;
	public static  LinkedList<org.eclipse.uml2.uml.Element> realAttributes;
	public static  LinkedList<org.eclipse.uml2.uml.Element> realAssociations;
	public static String profileName="DomainModelProfile::";	    
    public static HashMap<String, org.eclipse.uml2.uml.Element>	correspendance;
    public static HashMap<String,EObject>	eVersion;
    public static HashMap<String, LinkedList<EObject>> refrencesNeeds;
    public String ext;
    public static int count=1;
	private Logger logger ;
	public static LinkedList<EClass> tratedEClassDistribution;

	

	
	public QuestionsGeneration(ResourceSet resourceSet, String ext)
	{
		
		this.ext=ext;
		all=OclAST.orderedListe;
		if(ext.endsWith("uml"))
		{
		
		Model m = (Model) ((EPackageAdapter<?>)OclAST.reader.getPackages().get(0)).getOriginalPackage();
    	realAll = m.allOwnedElements();
    	realClasses = new LinkedList<org.eclipse.uml2.uml.Element>();
    	realEnumerations= new LinkedList<org.eclipse.uml2.uml.Element>();
    	realAttributes= new LinkedList<org.eclipse.uml2.uml.Element>();
    	realAssociations= new LinkedList<org.eclipse.uml2.uml.Element>();
    	filter(realAll);
    	correspendance=new HashMap<String, org.eclipse.uml2.uml.Element>();
    	eVersion=new HashMap<String, EObject>();
    	refrencesNeeds=new HashMap<String, LinkedList<EObject>>();
    	OCL ocl = org.eclipse.ocl.ecore.OCL.newInstance();
		OCLHelper<EClassifier, EOperation, EStructuralFeature, org.eclipse.ocl.ecore.Constraint> helper = ocl.createOCLHelper();
        OclDependencies.allClasses = OclAST.allClasses;
        OclDependencies.reader=OclAST.reader;
        OclDependencies.listeEAllOperation = OclAST.listeEAllOperation;
        OclDependencies.helper=helper;
        OclDependencies.ocl=ocl;
        this.logger = Logger.getLogger("QuestionsGeneration");
        logger.trace("*********** Legal expert data election process - "+ new Date().toString()+" ***********");
        count=1;
        tratedEClassDistribution = new LinkedList<EClass>();
     

		}
	    
	
	}
	
	public static LinkedList<EObject> getNeedsForRef(EReference ref)
	{
		LinkedList<EObject> res=new LinkedList<EObject>();

        EList<Stereotype> relatedSteryotypes = sortSteryotypes(getAllSteryotypesFromModel(ref));
        for (Stereotype stereotype : relatedSteryotypes) {

        	if(stereotype.getName().equals("upDownCorrolation"))
        	{  
        		Class context = (Class)getValue(ref, stereotype,"parentContext");
        	
        		if(context!=null && !ref.getEType().getName().equals(context.getName()) && ref.getEContainingClass().getName().equals(context.getName()))
        		{
        			@SuppressWarnings("unchecked")
					EList<Constraint> constraints=(EList<Constraint>)getValue(ref, stereotype,"triggerCondition");
        				   for (Constraint c : constraints) {
        		        	
							try {
								OclDependencies.ref=ref;
								OclDependencies.classe=null;
								OclDependencies.att=null;
							    OclDependencies oclVisitor = OclDependencies.getInstance();  
							    OclDependencies.helper.setContext(getEVersion(context));  
								org.eclipse.ocl.ecore.Constraint tempConstraint = OclDependencies.helper.createInvariant(c.getSpecification().stringValue());
								ExpressionInOCL<?, ?> oclExpression = (ExpressionInOCL<?, ?>) tempConstraint.getSpecification();
								oclExpression.accept(oclVisitor);

							} catch (ParserException e) {e.printStackTrace();}
 
        		        }
     			
					
        			
        		
        		}
        	}
        }
        	
		return res;
	}
	
	public static LinkedList<EObject> getNeedsForRef(EAttribute att)
	{
		LinkedList<EObject> res=new LinkedList<EObject>();

        EList<Stereotype> relatedSteryotypes = sortSteryotypes(getAllSteryotypesFromModel(att));
        for (Stereotype stereotype : relatedSteryotypes) {

        	if(stereotype.getName().equals("upDownCorrolation"))
        	{  
        		Class context = (Class)getValue(att, stereotype,"parentContext");
        	
        		if(context!=null)
        		{
        			@SuppressWarnings("unchecked")
					EList<Constraint> constraints=(EList<Constraint>)getValue(att, stereotype,"triggerCondition");
        	
        				   for (Constraint c : constraints) {
        		        	
							try {
								OclDependencies.ref=null;
								OclDependencies.classe=null;
								OclDependencies.att=att;
							    OclDependencies oclVisitor = OclDependencies.getInstance();  
							    OclDependencies.helper.setContext(getEVersion(context));  
								org.eclipse.ocl.ecore.Constraint tempConstraint = OclDependencies.helper.createInvariant(c.getSpecification().stringValue());
								ExpressionInOCL<?, ?> oclExpression = (ExpressionInOCL<?, ?>) tempConstraint.getSpecification();
								oclExpression.accept(oclVisitor);

							} catch (ParserException e) {e.printStackTrace();}
 
        		        }
     			
					
        			
        		
        		}
        	}
        }
        	
		return res;
	}

	
	public static LinkedList<EReference> resolveEReferencesTesnion(LinkedList<EReference> references)
	{
		
		LinkedList<EReference> res=new LinkedList<EReference>();
		
		for (int i = 0; i < references.size(); i++) {
			EReference ref=references.get(i);
		    getNeedsForRef(ref);
		    LinkedList<EObject> dep=OclDependencies.refrencesNeeds.get(ref.toString());
			if(dep==null)
			{
				res.add(0,ref);
			}
			else {
				if(dep.size()==0)
					res.add(0,ref);
				else res.add(ref);
			}
		}
		

		
		return res;
	}
	
	
	public static LinkedList<EAttribute> resolveEAttributesTesnion(LinkedList<EAttribute> atts)
	{
		
		LinkedList<EAttribute> res=new LinkedList<EAttribute>();
		
		for (int i = 0; i < atts.size(); i++) {
			EAttribute att=atts.get(i);
		    getNeedsForRef(att);
		    LinkedList<EObject> dep=OclDependencies.attributesNeeds.get(att.toString());
			if(dep==null)
			{
				res.add(0,att);
			}
			else {
				if(dep.size()==0)
					res.add(0,att);
				else res.add(att);
			}
		}
		
	
		
		return res;
	}
	
	public static LinkedList<EAttribute> purifyLinkedList(LinkedList<EAttribute> in)
	{
		LinkedList<EAttribute> res= new LinkedList<EAttribute>();
		HashMap<String, EAttribute> map = new HashMap<String, EAttribute>();
		
		for (EAttribute eObject : in) {
			if(eObject.isDerived()==false)
			map.put(eObject.toString(), eObject);
		}
		Set<Entry<String, EAttribute>> it = map.entrySet();
		Iterator<Entry<String, EAttribute>> itt = it.iterator();
		while (itt.hasNext()) {
			Map.Entry<java.lang.String, EAttribute> entry = (Map.Entry<java.lang.String, EAttribute>) itt.next();
			res.add(entry.getValue());
			
		}
		return res;
	}

	
	  public EObject generate(EPackage ePackage,String ruleName, EClass context, EObject callingParent, EReference callingRef,LinkedList<EObject> instances)
	    {     
		  

	    	  EClass choosenEClass=null;
	    	  choosenEClass=getElu(context);
	    	  if(choosenEClass==null)
	    		  choosenEClass=context;
	    	  

	    	  EObject contextIntance=null;
	    	  LinkedList<EClass> children = getChildrenClasses(context);
	    	  if (context.isAbstract()==false) {
	    		  children.add(context);}
	    	
	    	  
	    	  if(tratedEClassDistribution.contains(context)==false)
	    	  {
  
	    	  String chFact="";
	    	  for (int i = 0; i < children.size(); i++) {
	    		if(children.size()-1==i)
	    		{	chFact=chFact+children.get(i).getName();}
	    		else
	    		{
	    		  if(i==children.size()-2)
	    			  chFact=chFact+children.get(i).getName()+" or ";
	    		else
	    			chFact=chFact+children.get(i).getName()+", ";
	    		}
			}
	    	  
	    	  String chQuest="";
	    	  for (int i = 0; i < children.size(); i++) {

	    		if(children.size()-1==i)
	    		{	chQuest=chQuest+children.get(i).getName()+"(s)";}
	    		else
	    		{
	    		  if(i==children.size()-2)
	    			  chQuest=chQuest+children.get(i).getName()+"(s)"+", and ";
	    		else
	    			chQuest=chQuest+children.get(i).getName()+"(s)"+", ";
	    		}
	    		
	    
			}
	    	  
	    	  if(children.size()>1)
	    	  {
	    	  logger.trace("");
	    	  if(callingParent==null)
	    	  logger.trace("Fact:"+count+"= A/An "+ context.getName()+" can belong to one of the following categories: "+chFact);
	    	  else 
	    	  logger.trace("Fact:"+count+"= A/An "+ context.getName()+" (derived from a/an "+OclAST.reader.getBaseClass(callingParent.eClass()).getName()+") can belong to one of the following categories: "+chFact);
	    	  logger.trace("Question:"+count+"= What is the distribution of "+ context.getName()+"(s) over "+chQuest);
	    	  logger.trace("");
	    	  count++;
	    	
	    	  }
	    	  
	    	
	    	 
	    	  LinkedList<EAttribute> attributs=new LinkedList<EAttribute>();
	    	  
	    	  for (int i = 0; i < children.size(); i++) {
				attributs.addAll(getReleventAttributes(children.get(i), all));
			}
	    	  attributs = purifyLinkedList(attributs);
	    	  
	    	  
	    	 
	    	  
	    	  
	
	    	
		    	  if(attributs.size()>0)
		    	  {
		    	  chQuest="";
		    	  for (int i = 0; i < attributs.size(); i++) {

		    		if(attributs.size()-1==i)
		    		{	chQuest=chQuest+attributs.get(i).getName();}
		    		else
		    		{
		    		  if(i==attributs.size()-2)
		    			  chQuest=chQuest+attributs.get(i).getName()+", and ";
		    		else
		    			chQuest=chQuest+attributs.get(i).getName()+", ";
		    		}
		    		
		    	  }
		    	  
		    	  
		    	  logger.trace("");
		    	  if(callingParent==null)
		    	  logger.trace("Fact:"+count+"= A/An "+ context.getName()+" is specified by these information: "+chQuest);
		    	  else 
		    	  logger.trace("Fact:"+count+"= A/An "+ context.getName()+"(derived from a/an "+OclAST.reader.getBaseClass(callingParent.eClass()).getName()+") is specified by these information: "+chQuest);
		    	  
		    	  int x=1;
		    	  for (int i = 0; i < attributs.size(); i++) {
		    		  EAttribute att= attributs.get(i);
		    		  if(att.eContainer()==context|| context.getEAllSuperTypes().contains( att.eContainer()))
		    		  {
		    		  if(x==1)
		    		  logger.trace("*Globaly speaking:");
		    		  if(att.getEType().getName().equals("EBoolean")||att.getEType().getName().equals("Boolean"))
		    	      {logger.trace("-Question:"+count+"."+(x)+"=  What is the portion of "+context.getName()+"(s) who/that verify: "+att.getName());
		    	      x++;}
		    		  if(att.getEType().getName().equals("EReal")||att.getEType().getName().equals("EInt")||att.getEType().getName().equals("EDouble"))
		    		  {logger.trace("-Question:"+count+"."+(x)+"=  What is the distribution of "+att.getName()+" over "+context.getName()+"(s)"); 
		    		  x++;}
		    		  if(att.getEType() instanceof EEnum)
		    		  {
		    		  logger.trace("-For "+att.getName());	
		    		  EList<EEnumLiteral> literals= ((EEnum)att.getEType()).getELiterals();
		    		  for (int j = 0; j < literals.size(); j++) 
			    	  logger.trace("--Question:"+count+"."+(x)+"."+(j+1)+"=  What is the probability of having "+literals.get(j).getName()); 
	    			x++;
		    		  }  
		    		  
		    		  }
		    	  }
		    	  
		    	  boolean first=true;
		    	  for (int i = 0; i < attributs.size(); i++) {
		    		  EAttribute att= attributs.get(i);
		    		  if((att.eContainer()==context|| context.getEAllSuperTypes().contains( att.eContainer()))==false)
		    		  {
		    		  if(first)
		    		  {
		    		  logger.trace("*Specifically speaking: when a/an "+context.getName()+" is a/an "+att.getEContainingClass().getName()+":");
		    		  first=false;
		    		  }
		    		  if(att.getEType().getName().equals("EBoolean")||att.getEType().getName().equals("Boolean"))
		    	      {logger.trace("-Question:"+count+"."+(x)+"=  What is the portion of "+context.getName()+"(s) who/that verify: "+att.getName());
		    	      x++;}
		    		  if(att.getEType().getName().equals("EReal")||att.getEType().getName().equals("EInt")||att.getEType().getName().equals("EDouble"))
		    		  {logger.trace("-Question:"+count+"."+(x)+"=  What is the distribution of "+att.getName()+" over "+context.getName()+"(s)"); 
		    		  x++;}
		    		  if(att.getEType() instanceof EEnum)
		    		  {
		    		  logger.trace("-For "+att.getName());	
		    		  EList<EEnumLiteral> literals= ((EEnum)att.getEType()).getELiterals();
		    		  for (int j = 0; j < literals.size(); j++) 
			    	  logger.trace("--Question:"+count+"."+(x)+"."+(j+1)+"=  What is the probability of having "+literals.get(j).getName()); 
	    			x++;
		    		  }  
		    		  
		    		  }
		    	  }
		    	  
		    	  
		    	  logger.trace("");
		    	  count++;
		    	  }
	    	  }
	    	  
	    	  contextIntance = ((EPackage)ePackage).getEFactoryInstance().create(choosenEClass);
		    	  tratedEClassDistribution.add(context);  
	          
	    	  LinkedList<EReference> references=getRefrences(context);
	    	  LinkedList<EReference>  sortedReferences=resolveEReferencesTesnion(references);
	          references=sortedReferences;
	          
	    	 for (int i = 0; i <references.size(); i++) {
	    		 
	             

	    		     LinkedList<EObject> linkedToMe=new LinkedList<EObject>();
	    		     EReference ref = references.get(i);
	    			 EClass cible= ref. getEReferenceType();
	    			 int lower = ref.getLowerBound();
	    			
	    			 
	    			 if(ext.endsWith("uml"))
	    			 {
	    			String value=handelAssociationsDependencies(ref, contextIntance, callingParent!=null?callingParent:contextIntance,1);
		    		boolean lowerTreated=!value.equals("Non");
		    			
	    			 if(lowerTreated)
	    			lower=Double.valueOf(value).intValue(); 
	    			else
	    			 {
	    			 Constraint cons=getConstraintFromSteryotyped(ref, "lowerBound"); 
	    			 if(cons!=null)
	    			 lower = Double.valueOf(handelMultFromConstraint(cons,contextIntance,callingParent!=null?callingParent:contextIntance)).intValue();
	    			 }
	    			 }
	    			 
	    			 int maximumBound=30;
	    			 int upper= ref.getUpperBound()==-1?FiledGenerator.randomIntegerRange(lower, FiledGenerator.randomIntegerRange(lower,maximumBound)):ref.getUpperBound();
	    			
	    			 if(ext.endsWith("uml"))
	    			 {
	    			 String value=handelAssociationsDependencies(ref, contextIntance, callingParent!=null?callingParent:contextIntance,2);
	    			 boolean upperTreated=!value.equals("Non");
	    			 if(upperTreated)
	    			 upper=Double.valueOf(value).intValue(); 
	    			 else
	    			 {
	    			 Constraint cons1= getConstraintFromSteryotyped(ref, "upperBound");
	    			 if(cons1!=null)
	    			 upper = Double.valueOf(handelMultFromConstraint(cons1,contextIntance,callingParent!=null?callingParent:contextIntance)).intValue();
	    			 }
	    			 }
	    			 
	    			 if(upper<lower)
		    			 System.err.println("lower("+lower+") and upper("+upper+") bounds inconsitent for "+ref);
	    		//	 else
	    		//	 System.out.println("Association of "+context.getName()+ " = " +ref.getName() +"["+cible.getName()+","+lower+","+upper+"]");

	    			 
	    			 int shouldPass=1;
	        		 if(callingRef!=null)
	        		 {
	        			 if(callingRef.equals(ref))
	        				shouldPass=2;
	        			 
	        			 if(ref.getEOpposite()!=null)
	        				 if(ref.getEOpposite().equals(callingRef))
	        					 shouldPass=3;
	        		 }
	        		 
	        		 if(shouldPass==3)
	        		 {
	        		contextIntance.eSet(ref, callingParent);
	        		 }
	        		 
	        		 if(shouldPass==1)
	        		 {

	    			 //ensure minimum
	    			 for (int j = 0; j < lower; j++) {
	    			 linkedToMe.add(generate(ePackage,ruleName,cible,contextIntance, ref,instances));
	    			 
	    			 if(ref.isMany())
	        			 contextIntance.eSet(ref, linkedToMe);
	    			 
	    			 if(ref.isMany()==false && lower !=0) 
	    		     contextIntance.eSet(ref, linkedToMe.get(0));	
	    			 }
	    			 
	    			
	    			 
	    			 //ensure maximum
	    			 if(lower!=upper)
	    			 {
	    				 for (int j = lower; j < upper; j++) {
	    	    			 linkedToMe.add(generate(ePackage,ruleName,cible,contextIntance,ref,instances));
	    	    			 if(ref.isMany())
	        	    			 contextIntance.eSet(ref, linkedToMe);
	        				 if(ref.isMany()==false && lower ==0 && upper ==1) 
	        	    		     contextIntance.eSet(ref, linkedToMe.get(0));	
	    	    			 }
	    				 
	    				 
	    			 }
	    			 
	    	
	    			 
	    			 }
	    	 
	    			
	    			 
	    	 }
	    	    
	    	    if(callingRef ==null)
	    		instances.add(0,contextIntance);
	    	    else {
	    	    	if (callingRef.isContainment()==false)
	    	    		instances.add(0,contextIntance);
	    	    }
				return contextIntance;
			}
	    
	  
	  public Constraint getConstraintFromSteryotyped(EReference ref, String name)
		{
			Constraint res=null;
	        EList<Stereotype> relatedSteryotypes = getAllSteryotypesFromModel(ref);
	        for (Stereotype stereotype : relatedSteryotypes) {
	 
				if(stereotype.getName().equals(name))
				{       	
					Class target =  (Class) getValue(ref, stereotype,"target");
			
					if(ref.getEType().getName().equals(target.getName()))
					{
					
					Constraint constraint =  (Constraint) getValue(ref, stereotype,"constraint");
				
					res= constraint;
					}
				}
			
			}
			return res;
		}
		
		public Constraint getConstraintFromSteryotyped(EReference ref,Constraint constraintEntrante, String name)
		{
			Constraint res=null;
	        EList<Stereotype> relatedSteryotypes = getAllSteryotypesFromModel(constraintEntrante);
	        for (Stereotype stereotype : relatedSteryotypes) {
	 
				if(stereotype.getName().equals(name))
				{       	
					Class target =  (Class) getValue(constraintEntrante, stereotype,"target");
			
					if(ref.getEType().getName().equals(target.getName()))
					{
					
					Constraint constraint =  (Constraint) getValue(constraintEntrante, stereotype,"constraint");
				
					res= constraint;
					}
				}
			
			}
			return res;
		}
		
		public static EClass getEVersion(Class c)
		{
			EClass res=(EClass) eVersion.get(c.toString());
			if(res==null)
			{
			 for (EClass classe : OclAST.allClasses) {
				if(classe.getName().equals(c.getName()))
				{   
					res=classe;
					eVersion.put(c.toString(), classe);
					return res;
				}
			}
			}
			return res;
		}
		
		@SuppressWarnings("unchecked")
		public String handelMultFromConstraint(Constraint cons,EObject contextIntance, EObject parentInstance)
		{   
			String res="";
			boolean done=false;
	        EList<Stereotype> relatedSteryotypes = sortSteryotypes(getAllSteryotypesFromModel(cons));
	        for (Stereotype stereotype : relatedSteryotypes) {
	        	
	        	
	        	
	        	if(stereotype.getName().equals("upDownCorrolation"))
	        	{  
	        		Class context = (Class)getValue(cons, stereotype,"parentContext");
	        	
	        		if(context!=null)
	        		{
	        			EList<Constraint> constraints=(EList<Constraint>)getValue(cons, stereotype,"triggerCondition");
	        			for (int i = 0; (i < constraints.size())&&(done==false); i++) {
	        				Constraint constraint = constraints.get(i);
	        				if(evaluateQuery(contextIntance,getEVersion(context),constraint.getSpecification().stringValue()).equals("true"))
	        				{
	        				String value=handelMultFromConstraint(constraint,contextIntance,parentInstance);
	        				done=true;
	        				res=value;
	        				}
						}
	        			
	        		
	        		}
	        	}
	        	
	        	
				if(stereotype.getName().equals("fixed") && done==false)
				{
					String value = String.valueOf( getValue(cons, stereotype,"value"));
					done=true;
					res=value;
				}
				
				if(stereotype.getName().equals("range") && done==false)
				{
					String maximum = String.valueOf( getValue(cons, stereotype,"maximum"));
					String minimum = String.valueOf( getValue(cons, stereotype,"minimum"));
					done=true;
					String value = String.valueOf( FiledGenerator.randomDoubleRange(Double.valueOf(minimum).intValue(), Double.valueOf(maximum).intValue(),2));
					res= value;
		
				}
						
				if(stereotype.getName().equals("weightedSelection") && done==false)
				{
					try{
					EList<String> values = (EList<String>)  getValue(cons, stereotype,"values");
					EList<String>probabilities = (EList<String>) getValue(cons, stereotype,"probabilities");
					if(values.size()!=probabilities.size() )
						System.err.println("Size of values and probabilities is differenet ==>"+cons.getName());
					
					else if(somme(probabilities)>1.001)
						System.err.println("Somme probabilities are not equal to 1==>"+cons.getName());
					else
						done=true;
				
					
					String value= getfromProba (values,probabilities);
					res=value;
					}catch(Exception e)
					{e.printStackTrace();}
		
				}
				
				if(stereotype.getName().equals("distribution") && done==false)
				{
					EnumerationLiteral lit =  (EnumerationLiteral) getValue(cons, stereotype,"name");
		            String name= lit.getName();
		            if(name.trim().equalsIgnoreCase("randomGaussian"))
		            {
		            	double mean =  (double)getValue(cons, stereotype,"mean");
		            	double variance =  (double)getValue(cons, stereotype,"variance");
		            	int precision = (int) getValue(cons, stereotype,"precision");
		            	done=true;
		            	String value= String.valueOf(FiledGenerator.randomGaussian(mean, variance,precision));
		    			res=value;
		            	
		            }
		
				}
				
			}
			return res;
		}
		
		
		
		public String handelAssociationsDependencies(EReference ref, EObject contextIntance,EObject parentInstance, int multi)
		{
			String res="Non";
			boolean done=false;
	        EList<Stereotype> relatedSteryotypes = sortSteryotypes(getAllSteryotypesFromModel(ref));
	        for (Stereotype stereotype : relatedSteryotypes) {
	        	
	        	
	        	if(stereotype.getName().equals("upDownCorrolation"))
	        	{  
	        		Class context = (Class)getValue(ref, stereotype,"parentContext");
	        	
	        		if(context!=null && !ref.getEType().getName().equals(context.getName()) && ref.getEContainingClass().getName().equals(context.getName()))
	        		{
	        			@SuppressWarnings("unchecked")
						EList<Constraint> constraints=(EList<Constraint>)getValue(ref, stereotype,"triggerCondition");
	        			for (int i = 0; (i < constraints.size())&&(done==false); i++) {
	        				Constraint constraint = constraints.get(i);
	        				if(evaluateQuery(contextIntance,getEVersion(context),constraint.getSpecification().stringValue()).equals("true"))
	        				{
	        				Constraint cons=null;
	        				if(multi==1)
	        				 cons=getConstraintFromSteryotyped(ref, constraint, "lowerBound"); 
	        				else if(multi==2)
	        					cons=getConstraintFromSteryotyped(ref, constraint, "upperBound"); 
	        				
	   	    			 if(cons!=null)
	   	    				res = handelMultFromConstraint(cons,contextIntance,parentInstance);
	        				
	        				done=true;
	        				}
						}
	        			
	        		
	        		}
	        	}
	        }
	    return res;   
		}
	
	  public LinkedList<EClass> getChildrenClasses(EClass c)
	  {
		  LinkedList<EClass> res=(LinkedList<EClass>) OclAST.reader.getClassSubtypes(OclAST.allClasses, c);
		 if(res==null)
			 return new LinkedList<EClass>();
		 else return res;
		 
	   }
		 
	  public EClass getElu(EClass c)
	  {
		EClass res=null;
		LinkedList<EClass> children = getChildrenClasses(c);
		if(c.isAbstract()==false)
		res=c;
		EList<Stereotype> ster = getAllSteryotypesFromModel(c);
		EClass elu=null;
		if(contains(ster, "selectionAsMain"))
			elu=chooseEClassFromProba(c,children);
		else
			elu=chooseEClassFromProba(null,children);
		
		if(elu!=null)
			if(elu!=c)
			res=getElu(elu);
			
		return res;
	  }
	  
	  public static EClass chooseEClassFromProba( EClass c,LinkedList<EClass> children)
	  {  
		  LinkedList<EClass> values= new LinkedList<EClass>();
		  LinkedList<Double> probabilities = new LinkedList<Double>();

		  if(c!=null)
		  {values.add(c);
		  EList<Stereotype> ster = getAllSteryotypesFromModel(c);
		  Stereotype probSter=getSteryotypeByName(ster, "selectionAsMain");
		  probabilities.add(Double.valueOf(String.valueOf(getValue(c, probSter, "probability"))));}
		  
		  for (EClass child : children) {
			  EList<Stereotype> ster = getAllSteryotypesFromModel(child);
			  Stereotype childStery=getSteryotypeByName(ster, "selectionAsChild");
			  if(childStery!=null)
			  {
				  values.add(child);
				  probabilities.add(Double.valueOf(String.valueOf(getValue(child, childStery, "probability"))));
			  }
			
		}
		  
		  
		  if(values.size()==0)
				return null;
			else
			{

			if(values.size()!=probabilities.size() )
			{
				System.err.println("Size of values and probabilities is differenet  (values="+values.size()+",probabilities"+probabilities.size()+")==>"+values);
			    return null;
			}
			else if(somme(probabilities)>1.001)
			{
				System.err.println("Somme of probabilities not equals 1==>"+values);
				return null;
			}

			double p = Math.random();
			double cumulativeProbability = 0.0;
			for (int i = 0; i < probabilities.size(); i++) {
			    cumulativeProbability=  cumulativeProbability+ Double.valueOf(String.valueOf(probabilities.get(i)));
			    if (p <= cumulativeProbability && Double.valueOf(String.valueOf(probabilities.get(i))) != 0) {
			        return values.get(i);
			    }
			}
			
			return null;
			}
		  
		  
	  }
	  

	
	public static Object getValue(EObject obj, Stereotype stereotype, String propertyName)
	{
		Object res=null;
		org.eclipse.uml2.uml.Element correspondant=null;
		if(obj instanceof Constraint)
		correspondant=(Element) obj;	
		else
			correspondant=correspendance.get(obj.toString());
		if(correspondant==null)
		{
    	if(obj instanceof EClass)
    	correspondant = getCorrespondant (obj,realClasses);
    	else if(obj instanceof EAttribute)
    		   correspondant = getCorrespondant (obj,realAttributes);
    		else if(obj instanceof EReference)
    			correspondant = getCorrespondant (obj,realAssociations);
    		else if(obj instanceof EEnum || obj instanceof EEnumLiteral)
    			correspondant = getCorrespondant (obj,realEnumerations);
    			correspendance.put(obj.toString(), correspondant);
		}
    	res= correspondant.getValue(stereotype, propertyName);
    	
		return res;
	
	}


	public static EList<Stereotype> getAllSteryotypesFromModel(EObject obj)
    {
    	EList<Stereotype> res= null;
    	org.eclipse.uml2.uml.Element correspondant=null;
		if(obj instanceof Constraint)
		correspondant=(Element) obj;	
		else
			correspondant=correspendance.get(obj.toString());
	
    	if(correspondant==null)
    	{
    	if(obj instanceof EClass)
    	correspondant = getCorrespondant (obj,realClasses);
    	else if(obj instanceof EAttribute)
    		   correspondant = getCorrespondant (obj,realAttributes);
    		else if(obj instanceof EReference)
    			correspondant = getCorrespondant (obj,realAssociations);
    		else
    			if(obj instanceof EEnum || obj instanceof EEnumLiteral)
    		    correspondant = getCorrespondant (obj,realEnumerations);
    			correspendance.put(obj.toString(), correspondant);
    	}

    	if(correspondant == null) return null;
     	res=correspondant.getAppliedStereotypes();
    	return res;
    }
    
    
    public static org.eclipse.uml2.uml.Element getCorrespondant(EObject obj, LinkedList<org.eclipse.uml2.uml.Element> liste)
    { 
    	org.eclipse.uml2.uml.Element res=correspendance.get(obj.toString());;
    	
    	if(res==null)
    	{
    	if(obj instanceof EClass)
    	{
    		for (org.eclipse.uml2.uml.Element element : liste) {
				if( ((org.eclipse.uml2.uml.Class)element).getName().trim().equals(((EClass) obj).getName()))
				   {correspendance.put(obj.toString(), element);
					  return element;
				   }
			}
    	}
    	
     	if(obj instanceof EEnum)
    	{
    		for (org.eclipse.uml2.uml.Element element : liste) {
				if( ((org.eclipse.uml2.uml.Enumeration)element).getName().trim().equals(((EEnum) obj).getName()))
				   {correspendance.put(obj.toString(), element);
					  return element;
				   }
			}
    	}
     	
    	if(obj instanceof EEnumLiteral)
    	{
    		EEnum container = (EEnum) ((EEnumLiteral)obj).eContainer();
    		org.eclipse.uml2.uml.Enumeration realContainer= (Enumeration) getCorrespondant(container, liste);
    		for (org.eclipse.uml2.uml.Element element : realContainer.getOwnedLiterals())
    			if( ((org.eclipse.uml2.uml.EnumerationLiteral)element).getName().trim().equals(((EEnumLiteral) obj).getName()))
    			   {correspendance.put(obj.toString(), element);
					  return element;
				   }
    		
    	}
    	
    	
    	if(obj instanceof EAttribute)
    	{
    		for (org.eclipse.uml2.uml.Element element : liste) {
    		  if(((org.eclipse.uml2.uml.Property)element).isAttribute())
			    if( ((org.eclipse.uml2.uml.Property)element).getName().trim().equals(((EAttribute) obj).getName()))
				  if( ((org.eclipse.uml2.uml.Property)element).getType().getName().trim().equals(((EAttribute) obj).getEType().getName()))
					  if( ((org.eclipse.uml2.uml.Class)((org.eclipse.uml2.uml.Property)element).getOwner()).getName().equals(((EClass)(((EAttribute) obj).eContainer())).getName()))
					   {correspendance.put(obj.toString(), element);
						  return element;
					   }
			}
    		
    	}
    		
    	}
    	
      	if(obj instanceof EReference)
    	{
    		for (org.eclipse.uml2.uml.Element element : liste) {
    			EReference ref= (EReference)obj;
    			Association ass =(Association)element;
    			Property member1=ass.getMemberEnds().get(0);
    			Property member2=ass.getMemberEnds().get(1);
    			String targetNameRef=ref.getEType().getName();
    			String sourceNameRef=((EClass)ref.eContainer()).getName();
    			
    			if((member1.getType().getName().equals(targetNameRef)&& member2.getType().getName().equals(sourceNameRef)))
    			if( (member1.getName().trim().equals(ref.getName())))
    			if( (member1.getLower()==ref.getLowerBound() && member1.getUpper()==ref.getUpperBound()))
    			return element;
    			
    			if(member2.getType().getName().equals(targetNameRef)&& member1.getType().getName().equals(sourceNameRef))
    			if( member2.getName().trim().equals(ref.getName()))
    	    	if((member2.getLower()==ref.getLowerBound() && member2.getUpper()==ref.getUpperBound()))
    				return element;
    			
    		
			}
    		
    	}
    	
    	return res;
    }
    
    public static void filter (EList<org.eclipse.uml2.uml.Element> liste)
    {

    	
    		for (org.eclipse.uml2.uml.Element element : liste) {
    			if (element instanceof org.eclipse.uml2.uml.Class)
    				realClasses.add(element);
    			
    			if (element instanceof org.eclipse.uml2.uml.Property)
    				realAttributes.add(element);
    			
    			if (element instanceof org.eclipse.uml2.uml.Association)
    				realAssociations.add(element);
    			
    			if (element instanceof org.eclipse.uml2.uml.Enumeration)
    				realEnumerations.add(element);
		
			}
    	
    	
 
    }
	
	  
	  public LinkedList<EClass> getClassPossibilities(EClass c)
	  {
		  LinkedList<EClass> res=(LinkedList<EClass>) OclAST.reader.getClassSubtypes(OclAST.allClasses, c);
		 if(res==null)
			 return new LinkedList<EClass>();
		 
		  for (int i = 0; i < res.size(); i++) {
			if(isALeaf(res.get(i))==false)
			{
				res.remove(i);
				i--;
			}
		}
		 
		  return res;
	  }

	  public boolean isALeaf(EClass c)
	  {
		  boolean res=true;
		  int i=0;
		  
		  while (res&&i<OclAST.allClasses.size()) {
			 if(OclAST.allClasses.get(i).getESuperTypes().contains(c))
				 res=false;
			  i++;
			
		}
	
		  
		  return res;
	  }
	  
	public static boolean isAttributeNeeded(EAttribute att, EClass eClass)
	{boolean res= att.eContainer().toString().equals(eClass.toString());
	 if(res)
		return res;
	 else
	 {   
		 for (EClass c : eClass.getEAllSuperTypes()) {
			 if(att.eContainer().toString().equals(c.toString()))	
				 return true;
		}
		 return res;
	 }
	}
	
	public static LinkedList<EAttribute> getReleventAttributes(EClass eClass, LinkedList<EObject> liste)
	{
		LinkedList<EAttribute> res =new LinkedList<EAttribute>();
		for (int i = 0; i <liste.size(); i++) {
			if(liste.get(i) instanceof EAttribute)
				if(isAttributeNeeded((EAttribute) liste.get(i),eClass))
					if(res.contains((EAttribute) liste.get(i))==false)
					res.add((EAttribute) liste.get(i));
				
		}
		return res;
	}
	
	public static LinkedList<EReference>getRefrences(EClass c)
	{
		LinkedList<EReference> res= new LinkedList<EReference>();
		for (int i = 0; i < all.size(); i++) {
			if(all.get(i) instanceof EReference)
			{
				EReference ref= (EReference)all.get(i);
				if(ref.eContainer().equals(c))
					if(res.contains(res)==false)
					res.add(ref);
			}
			
		}
		return res;
	}
	
	public static Stereotype getSteryotypeByName( EList<Stereotype> relatedSteryotypes, String name)
	{     
		int i =0;
		while (i<relatedSteryotypes.size()) {
			if(relatedSteryotypes.get(i).getName().trim().equals(name.trim()))
				return relatedSteryotypes.get(i);
			else i++;
		}
		return null;
	}
	
	public static boolean contains( EList<Stereotype> relatedSteryotypes, String name)
	{
		boolean res=false;
		int i =0;
		while (i<relatedSteryotypes.size()&&res==false) {
			if(relatedSteryotypes.get(i).getName().trim().equals(name.trim()))
				res=true;
			else i++;
		}
		return res;
	}
	
	public void createRandomAttribute(EAttribute att, EObject contextIntance)
	{
		 String stringType = att.getEType().getName();
         Object value = null;
       //  System.err.println("Generated attribute "+ att.getName() +" - "+stringType);

         if(att.getEType() instanceof EEnum)
         { EEnum enumuration = (EEnum) att.getEType();
           EList<Stereotype> relatedSteryotypes = getAllSteryotypesFromModel(enumuration);
           int indexLit = -1;
           if(contains(relatedSteryotypes,"irregularDistribution")==false)
           {
           if(enumuration.getELiterals().size()>0)
           indexLit=FiledGenerator.randomIntegerRange(0, enumuration.getELiterals().size()-1);
           
           if(indexLit==-1)
       	  value = null; 
           else 
           value = enumuration.getELiterals().get(indexLit);
           }
           else
           {
        	   EList<Double> probabilities = getPropabilitiesForEnumeration(enumuration.getELiterals());  
        	   value= getLiteralFromProba (enumuration.getELiterals(),probabilities);
               
           }
           
         }   
      else
     	if (stringType.equals("EString"))
			value = RandomStringUtils.randomAlphanumeric(30).toUpperCase();
     	else 
     		if (stringType.contains("EChar"))
     			value = RandomStringUtils.randomAlphanumeric(1).toUpperCase().charAt(0);
		else if (stringType.contains("EBoolean"))
				value = FiledGenerator.randomBoolean();
		else if (stringType.contains("EByte")){
			   int cast = (Integer)FiledGenerator.randomIntegerRange(0,1);
				value = new Byte(String.valueOf(cast)) ;}
		else if (stringType.equals("EBigInteger"))
				value = new BigInteger(BigInteger.valueOf(Long.parseLong(String.valueOf(FiledGenerator.randomInteger()))).toByteArray());
		else if (stringType.equals("EBigDecimal"))
				value = new BigDecimal(String.valueOf(FiledGenerator.randomDoubleRange(0, 10000,2)).toCharArray());
		else if (stringType.contains("ELong"))
			value = Long.parseLong(String.valueOf(FiledGenerator.randomDoubleRange(0, 10000,2)));
		else  if (stringType.contains("EDouble"))
			value = FiledGenerator.randomDoubleRange( 0,10000,2);
		else value = FiledGenerator.randomIntegerRange(0,Integer.MAX_VALUE);
         
        contextIntance.eSet(att, value);
		
	}
	
	public static Object castValue(EAttribute att, String value)
	{
		 String stringType = att.getEType().getName();

    
     	if (stringType.equals("EString"))
			return value;
     	else 
     		if (stringType.contains("EChar"))
     			return value.charAt(0);
		else if (stringType.contains("EBoolean"))
				return Boolean.valueOf(value);
		else if (stringType.contains("EByte"))
			   return Byte.valueOf(value);
		else if (stringType.equals("EBigInteger"))
				return new BigInteger(BigInteger.valueOf(Long.parseLong(String.valueOf(value))).toByteArray());
		else if (stringType.equals("EBigDecimal"))
				return new BigDecimal(value.toCharArray());
		else if (stringType.contains("ELong"))
			return Long.parseLong(value);
		else  if (stringType.contains("EDouble"))
			return Double.valueOf(value);
		else if (stringType.contains("EInt"))
			return Double.valueOf(value).intValue();
		else return null;
         
	}
	
	public String generateValue(EAttribute att,String minimum, String maximum)
	{
		String res="null";
		if(att.getEType().getName().contains("EDouble")||att.getEType().getName().contains("EBigDecimal"))
		return String.valueOf(FiledGenerator.randomDoubleRange(Double.valueOf(minimum), Double.valueOf(maximum), 2));
		if(att.getEType().getName().contains("EInt")||att.getEType().getName().contains("ELong")||att.getEType().getName().contains("EBigInteger"))
		return String.valueOf(FiledGenerator.randomIntegerRange(Double.valueOf(minimum).intValue(), Double.valueOf(maximum).intValue()));
		return res;
	}
	
	public String treatValues (String value)
	{
		String res="";
		if( (value.trim().startsWith("[")&&value.trim().endsWith("]"))==false)
			return value;
		if( (value.trim().startsWith("[")&&value.trim().endsWith("]"))==true)
		{
			if(value.trim().contains(".."))
			{
				value=value.trim().replace("[", "").replace("]", "").replace("..", "-");
				
				String [] temp=value.split("-");
				double start= Double.valueOf(temp[0].trim());
				
				double end= Double.valueOf(temp[1].trim());
				return String.valueOf(FiledGenerator.randomDoubleRange(start, end, 2));
			}
			
			if(value.trim().contains("-"))
			{
				value=value.trim().replace("[", "").replace("]", "");
				
				String [] temp=value.split("-");
				double start= Double.valueOf(temp[0].trim());
				
				double end= Double.valueOf(temp[1].trim());
				return String.valueOf(FiledGenerator.randomDoubleRange(start, end, 2));
			}
			
			if(value.trim().contains(","))
			{
				String [] temp=value.trim().replace("[", "").replace("]", "").split(",");
				int index=FiledGenerator.randomIntegerRange(0, temp.length-1);
				return temp[index].trim();
			}
		}

		
		return res;
	}
	
	public static EList<Double> getPropabilitiesForEnumeration(EList<EEnumLiteral> values)
	{
    EList<Double> res = new  BasicEList<Double>();
	if(values.size()==0)
	return null;
	else
	{
		for (int i = 0; i <values.size(); i++) {
			
			EList<Stereotype> ster = getAllSteryotypesFromModel(values.get(i));
		
			if(ster!=null)
			for (Stereotype stereotype : ster) {
				if(stereotype.getName().equals("selectionFrequency"))
				res.add(new Double(	String.valueOf(getValue(values.get(i), stereotype,"value"))));
					
			}
		}
	
	return res;	
	}
		
	}
	
	public static EEnumLiteral getLiteralFromProba(EList<EEnumLiteral> values, EList<?> probabilities)
	{ 

	if(values.size()==0)
		return null;
	else
	{

	if(values.size()!=probabilities.size() )
	{
		System.err.println("Size of values and probabilities is differenet  (values="+values.size()+",probabilities"+probabilities.size()+")==>"+values);
	    return null;
	}
	else if(somme(probabilities)>1.001)
	{
		System.err.println("Somme probabilities are not equal to 1==>"+values);
		return null;
	}
	
	double p = Math.random();
	double cumulativeProbability = 0.0;
	for (int i = 0; i < probabilities.size(); i++) {
	    cumulativeProbability=  cumulativeProbability+ Double.valueOf(String.valueOf(probabilities.get(i)));
	    if (p <= cumulativeProbability && Double.valueOf(String.valueOf(probabilities.get(i))) != 0) {
	        return values.get(i);
	    }
	}
	
	
	}
	
	return null;
	}
	
	public String getfromProba(EList<String> values, EList<String> probabilities)
	{   
	
		double p = Math.random();
		double cumulativeProbability = 0.0;
		for (int i = 0; i < probabilities.size(); i++) {
		    cumulativeProbability=  cumulativeProbability+ Double.valueOf(String.valueOf(probabilities.get(i)));
		    if (p <= cumulativeProbability && Double.valueOf(String.valueOf(probabilities.get(i))) != 0) {
		        return treatValues(values.get(i));
		    }
		}
		
		return null;

	}
	
	public static double somme(EList<?> probabilities)
	{
		double res=0;
		for (int i = 0; i < probabilities.size(); i++) {
			res=res+ Double.valueOf(String.valueOf(probabilities.get(i))).doubleValue();
		}
		return res;
	}
	public static double somme(LinkedList<?> probabilities)
	{
		double res=0;
		for (int i = 0; i < probabilities.size(); i++) {
			res=res+ Double.valueOf(String.valueOf(probabilities.get(i))).doubleValue();
		}
		return res;
	}
	
	public static EList<Stereotype> sortSteryotypes(EList<Stereotype> relatedSteryotypes)
	{  EList<Stereotype> res= new BasicEList<Stereotype>();
		LinkedList<String>  importantNames=new LinkedList<String>();
		importantNames.add("upDownCorrolation");
		
		for (int i = 0; i < relatedSteryotypes.size(); i++) {
			if(importantNames.contains(relatedSteryotypes.get(i).getName()))
			res.add(0,relatedSteryotypes.get(i));
			else
			res.add(relatedSteryotypes.get(i));
			
		}
		return res;
	}
	

	
	@SuppressWarnings("unchecked")
	public boolean handelAttributesSteryotyps(EAttribute att, EObject contextIntance,EObject parentInstance)
	{
		boolean res=false;
        EList<Stereotype> relatedSteryotypes = sortSteryotypes(getAllSteryotypesFromModel(att));
        for (Stereotype stereotype : relatedSteryotypes) {
        	
        	
        	if(stereotype.getName().equals("upDownCorrolation"))
        	{  
        		Class context = (Class)getValue(att, stereotype,"parentContext");
        	
        		if(context!=null)
        		{
        			EList<Constraint> constraints=(EList<Constraint>)getValue(att, stereotype,"triggerCondition");
        			for (int i = 0; (i < constraints.size())&&(res==false); i++) {
        				Constraint constraint = constraints.get(i);
        				if(evaluateQuery(parentInstance,getEVersion(context),constraint.getSpecification().stringValue()).equals("true"))
        				{
        				String value=handelMultFromConstraint(constraint,contextIntance,parentInstance);
        				res=true;
        				contextIntance.eSet(att, castValue(att,value));
        				}
					}
        			
        		
        		}
        	}
        	
        	
			if(stereotype.getName().equals("fixed")&& res==false)
			{
				String value = String.valueOf( getValue(att, stereotype,"value"));
				res=true;
				contextIntance.eSet(att, castValue(att,value));
			}
			
			if(stereotype.getName().equals("range") && res==false)
			{
				String maximum = String.valueOf( getValue(att, stereotype,"maximum"));
				String minimum = String.valueOf( getValue(att, stereotype,"minimum"));
				res=true;
				String value= generateValue (att,minimum,maximum);
				contextIntance.eSet(att, castValue(att,value));
	
			}
					
			if(stereotype.getName().equals("weightedSelection") && res==false)
			{
				try{
				EList<String> values = (EList<String>)  getValue(att, stereotype,"values");
				EList<String>probabilities = (EList<String>) getValue(att, stereotype,"probabilities");
				if(values.size()!=probabilities.size() )
					System.err.println("Size of values and probabilities is differenet ==>"+att.getName());
				
				else if(somme(probabilities)>1.001)
					System.err.println("Somme probabilities are not equal to 1==>"+att.getName());
				else
					res=true;
			
				
				String value= getfromProba (values,probabilities);
				contextIntance.eSet(att, castValue(att,value));
				}catch(Exception e)
				{e.printStackTrace();}
	
			}
			
			if(stereotype.getName().equals("distribution") && res==false)
			{
				EnumerationLiteral lit =  (EnumerationLiteral) getValue(att, stereotype,"name");
	            String name= lit.getName();
	            if(name.trim().equalsIgnoreCase("randomGaussian"))
	            {
	            	double mean =  (double)getValue(att, stereotype,"mean");
	            	double variance =  (double)getValue(att, stereotype,"variance");
	            	int precision = (int) getValue(att, stereotype,"precision");
	            	res=true;
	            	String value= String.valueOf(FiledGenerator.randomGaussian(mean, variance,precision));
					contextIntance.eSet(att, castValue(att,value));
	            	
	            }
	
			}
			
		}
		return res;
	}
	
	
	public static String evaluateQuery (EObject instance, EClass classifier , String queryString)
	{
		String res="false";

		try {
			OclDependencies.helper.setContext(classifier);
		    OCLExpression<EClassifier> query= OclDependencies.helper.createQuery(queryString);
			res=String.valueOf( OclDependencies.ocl.evaluate(instance, query));		
			//String ch =String.valueOf(OclDependencies.ocl.evaluate(instance, OclDependencies.helper.createQuery("self")));	
			System.err.println("Query==>"+queryString);
			System.err.println("Res==>"+res);
	
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
        return res;
		
	}

  
    public static void save(String name, LinkedList<EObject> inst){
    
    ResourceSetImpl resourceSet = new ResourceSetImpl(); 
    resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl()); 
    final File f = new File("");
    final String dossierPath = f.getAbsolutePath().replace("\\", "//");
    final String apiSamplePath = dossierPath +"//Instances//"; 
    final String file_name=name+".xmi";
    String path = "file://" + apiSamplePath + file_name;
    URI uri = URI.createURI(path); 
    Resource resource = resourceSet.createResource(uri); 
    for (EObject eObject : inst) {
    	resource.getContents().add(eObject); 
	}
    
    try {
		resource.save(null);
	} catch (IOException e) {
		e.printStackTrace();
	} }




}
