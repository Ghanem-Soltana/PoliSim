package util;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.math3.distribution.AbstractRealDistribution;
import org.apache.commons.math3.distribution.UniformRealDistribution;
import org.apache.commons.math3.random.JDKRandomGenerator;
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
import org.eclipse.emf.ecore.util.BasicExtendedMetaData;
import org.eclipse.emf.ecore.util.ExtendedMetaData;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.ocl.ParserException;
import org.eclipse.ocl.ecore.OCL;
import org.eclipse.ocl.helper.OCLHelper;
import org.eclipse.ocl.utilities.ExpressionInOCL;
import org.eclipse.uml2.uml.Association;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Constraint;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.Enumeration;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.Stereotype;
import org.eclipse.uml2.uml.internal.resource.UMLResourceFactoryImpl;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.traverse.TopologicalOrderIterator;

import temp.Main;
import Main.GeneratorThread;
import adapters.EPackageAdapter;

import com.google.common.base.CharMatcher;


@SuppressWarnings("restriction")
public class CreateTaxpayers {


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
    public static HashMap<String, Integer> perturbationMax;
    public static TopologicalOrderIterator<String, DefaultEdge> orderIterator;
    //caches
    public static HashMap<String, LinkedList<EObject>>cache_getNeedsForRef;
    public static HashMap<String, LinkedList<EObject>> cache_getNeedsForAtt;
    public static HashMap<String, Integer> cache_PosInOrder;
    public static HashMap<String, String> cache_requires;
    public static HashMap<String, LinkedList<EClass>> cache_getDirectChildrenClasses;
    public static HashMap<String, LinkedList<EAttribute>> cache_getReleventAttributes;
    public static HashMap<String, LinkedList<EReference>> cache_getRefrences;
    public static HashMap<EReference, Integer> count_Recursion;
    public static HashMap<String, String> cache_identifiers;
    public static HashMap<EAttribute, Integer[]> actualStatusHistogramsForAttributes;
    public static HashMap<EAttribute, EList<String>> desiredHistogramsForAttributes;
    public static HashMap<Class, String> desiredProbaForClasses;
    public static HashMap<Class, Integer> actualStatusProbForClasses;
    
	private static Logger logger = Logger.getLogger("Taxpayers");
	protected int maxAllowedRecurison = 100;
	public static Map<EClass, Set<EObject>> extents;
	public static LinkedList<EReference> safeEReferences;
	public static LinkedList<EReference> oppositeEReferences;
	public static LinkedList<EReference> unsafeEReferences;
	public static LinkedList<EReference> listeEReferences;
	public static EPackage pck;
	public CreateTaxpayers(ResourceSet resourceSet, int count,EPackage pck,Model m)
	{        //caches
    	
		 if(count==1)
			{
		CreateTaxpayers.pck = pck;
	
		all=OclAST.orderedListe;
    	realAll = m.allOwnedElements();
    	realClasses = new LinkedList<org.eclipse.uml2.uml.Element>();
    	realEnumerations= new LinkedList<org.eclipse.uml2.uml.Element>();
    	realAttributes= new LinkedList<org.eclipse.uml2.uml.Element>();
    	realAssociations= new LinkedList<org.eclipse.uml2.uml.Element>();
    	perturbationMax=new HashMap<String, Integer>();
    	filter(realAll);
    	correspendance=new HashMap<String, org.eclipse.uml2.uml.Element>();
    	eVersion=new HashMap<String, EObject>();
    	count_Recursion = new HashMap<EReference, Integer>();
    
     
       
       logger.trace("*********** Gen at "+ new Date().toString()+" ***********");
       FiledGenerator.cache_distributions = new HashMap<String, AbstractRealDistribution>();
       cache_getNeedsForRef=new HashMap<String, LinkedList<EObject>>();
       cache_getNeedsForAtt=new HashMap<String, LinkedList<EObject>>();
       cache_PosInOrder=new HashMap<String, Integer>();
       cache_requires = new HashMap<String, String>();
       cache_getDirectChildrenClasses=new HashMap<String, LinkedList<EClass>>();
       cache_getReleventAttributes=new HashMap<String, LinkedList<EAttribute>>();
       cache_getRefrences= new HashMap<String, LinkedList<EReference>>();
       cache_identifiers = new HashMap<String, String>();
        OclDependencies.refrencesNeeds= new HashMap<String, LinkedList<EObject>>();
        OclDependencies.attributesNeeds = new HashMap<String, LinkedList<EObject>>();
   		OCL ocl = org.eclipse.ocl.ecore.OCL.newInstance();
   		OCLHelper<EClassifier, EOperation, EStructuralFeature, org.eclipse.ocl.ecore.Constraint> helper = ocl.createOCLHelper();
   		OclDependencies.allClasses = OclAST.allClasses;
   		OclDependencies.reader=OclAST.reader;
   		OclDependencies.listeEAllOperation = OclAST.listeEAllOperation;
   		OclDependencies.helper=helper;
   		OclDependencies.ocl=ocl;
   		extents = new HashMap<EClass, Set <EObject>>();
   		safeEReferences = new LinkedList<EReference>();
   		oppositeEReferences = new LinkedList<EReference>();
   		unsafeEReferences  = new LinkedList<EReference>();
   		listeEReferences = new LinkedList<EReference>();
   		LinkedList<EClass> tempClasses = new LinkedList<EClass>();
   		for (EClass c : OclAST.listeEClass) {
   			tempClasses.add(c);
   			tempClasses.addAll(getAllChildrenClasses(c));
		}
   		removeDuplicatClasses(tempClasses);
   		OclAST.listeEClass.clear();
   		OclAST.listeEClass.addAll(tempClasses);
   		if(GeneratorThread.needSlicing==false)
   		{
   		for (EObject eObject : pck.getEClassifiers()) {
   		if(eObject instanceof EClass)
   		{
   		EList<EReference> refs = ((EClass) eObject).getEAllReferences();
		listeEReferences.addAll(refs);
   		}
   		}
   		}
   		else 
   		{
   			listeEReferences = OclAST.listeEReference;

   			for (EObject eObject : pck.getEClassifiers()) {
   		   		if(eObject instanceof EClass)
   		   		{
   		   			if(OclAST.listeEClass.contains(eObject))
   		   			{
   		   				
   		   				EList<EReference> refs = ((EClass) eObject).getEAllReferences();
   		   				LinkedList<EReference> toBeRemoved = new LinkedList<EReference>();
   		   				LinkedList<EReference> toBeAdded = new LinkedList<EReference>();
   		   				for (EReference realRef : refs) {

   		   				for (EReference eReference : listeEReferences) {
							if(eReference.getEContainingClass().getName().equals( ((EClass) eObject).getName()) && realRef.getName().equals(eReference.getName()) && realRef.getEType().getName().equals(eReference.getEType().getName()))
							{
								toBeRemoved.add(eReference);
								toBeAdded.add(realRef);  
							}
						} 
   		   				
   		   				}
   		   				
   		   				listeEReferences.removeAll(toBeRemoved);
   		   				listeEReferences.addAll(toBeAdded);
   		   			
   		   				
   		   			}
   		   		}
   		   		}
   		   
   		}
   		
   		
   		
   		actualStatusHistogramsForAttributes = new HashMap<EAttribute, Integer[]>();
   		desiredHistogramsForAttributes = new HashMap<EAttribute, EList<String>>();
   		desiredProbaForClasses = new HashMap<Class, String>();
   	    actualStatusProbForClasses = new HashMap<Class, Integer>();
   		for (Element eClass : realClasses) {
			if(eClass instanceof Class)
			{
				EList<Stereotype> class_steryotypes = eClass.getAppliedStereotypes();
		    	 if(contains(class_steryotypes, "probabilistic type"))
		    	 {
		    		 Stereotype ster=getSteryotypeByName(class_steryotypes, "probabilistic type");
		    		 String proba=String.valueOf(eClass.getValue(ster, "frequency")).replace(",", ".");
		    		 desiredProbaForClasses.put((Class)eClass, proba);
		    	 }
		    	 
		    	 actualStatusProbForClasses.put((Class)eClass, 0);
				
			}
		}
   		
   	   
		}
        removeDuplicat(listeEReferences);
	
	}
	
	
	public static void removeDuplicatEObject( LinkedList<EObject> list)
	{
		
		if(list!=null)
		{
		LinkedHashSet<EObject> s = new LinkedHashSet<EObject>(list);
		list=new LinkedList<EObject>();
		list.addAll(s);
		}
	}
	
	public static void removeDuplicatObject( LinkedList<Object> list)
	{
		
		if(list!=null)
		{
		LinkedHashSet<Object> s = new LinkedHashSet<Object>(list);
		list=new LinkedList<Object>();
		list.addAll(s);
		}
	}

	public static void removeDuplicat( LinkedList<EReference> list)
	{
		
		if(list!=null)
		{
		LinkedHashSet<EReference> s = new LinkedHashSet<EReference>(list);
		list=new LinkedList<EReference>();
		list.addAll(s);
		}
	}
	
	public static void removeDuplicatClasses( LinkedList<EClass> list)
	{
		if(list!=null)
		{
		LinkedHashSet<EClass> s = new LinkedHashSet<EClass>(list);
		list=new LinkedList<EClass>();
		list.addAll(s);
		}
	}
	
	
	public void cleanReferences() {
		removeDuplicat(safeEReferences);
		removeDuplicat(oppositeEReferences);
		removeDuplicat(unsafeEReferences);
		for (int i = 0; i < safeEReferences.size(); i++) {
			if(safeEReferences.get(i)==null)
			{
			 safeEReferences.remove(i);
			 i--;
			}
		}
		
		for (int i = 0; i < unsafeEReferences.size(); i++) {
			if(unsafeEReferences.get(i)==null)
			{
				unsafeEReferences.remove(i);
			 i--;
			}
		}
		
		for (int i = 0; i < oppositeEReferences.size(); i++) {
			if(oppositeEReferences.get(i)==null)
			{
				oppositeEReferences.remove(i);
			 i--;
			}
		}
		
	}
	
	
	
	public void clusterReferences(EClass startingPointClass, LinkedList<EClass> treated,EReference callingRef) {

		if(oppositeEReferences.contains(callingRef))
			return;
		
		if(callingRef==null)
		{
			  treated.add(startingPointClass);
		   	 //already sorted
			  
	    	  LinkedList<EReference> references=getRefrences(startingPointClass);    
	    
	    		 
	    		 for (EReference eReference : references) {
	    			 clusterReferences((EClass)eReference.getEType(),  treated, eReference);
				}
	    		 
	    		LinkedList<EClass> children = getDirectChildrenClasses(startingPointClass);
	    		children.remove(startingPointClass);
	    
	    		for (EClass eClass : children) {
	    			 LinkedList<EReference>   references2=getRefrences(eClass);    
	    	    	 
	    	
	    	    		 for (EReference eReference : references2) {
	    	    			 clusterReferences(eClass,  treated, eReference);
	    				}
	    	    		 
				}
	    		
	    		/*
	       		EList<EClass> parents = getAllSuperTypes(startingPointClass);
	       		parents.remove(startingPointClass);
	    		for (EClass eClass : parents) {
	    			 LinkedList<EReference>  references3=getRefrences(eClass);    
	    	    	 
	    		  		 for (EReference eReference : references3) {
	    	    			 clusterReferences(eClass,  treated, eReference);
	    				}
	    	    		 
				}*/
			
		}
		else 
		{
			EReference oppositeCalling=callingRef.getEOpposite();	
		
	
			
		    //System.out.println(callingRef.getEType().getName());
			boolean find = false;
			int i = 0;
			while (i<treated.size()&&find ==false)
			if(treated.get(i).getName().equals(startingPointClass.getName()))
				find = true;
			else i++;
				
			if(!find)
			{
				
				
				
				
			
				if(!unsafeEReferences.contains(callingRef)&&!oppositeEReferences.contains(callingRef)&&!safeEReferences.contains(callingRef))
				{
				 safeEReferences.add(callingRef);

					if(oppositeCalling!=null)
					{
						if(!unsafeEReferences.contains(oppositeCalling)&&!safeEReferences.contains(oppositeCalling)&&!oppositeEReferences.contains(oppositeCalling))
						{
							oppositeEReferences.add(oppositeCalling);
						}
					}
	
				
				}
			
				
			
			if(treated.contains(startingPointClass)==false)
				treated.add(startingPointClass);
		
			  LinkedList<EReference> references=getRefrences(startingPointClass);    
		    	 
		    		 
		    		 for (EReference eReference : references) {
		    			 clusterReferences((EClass)eReference.getEType(),  treated, eReference);
					}
		    		 
		    		LinkedList<EClass> children = getDirectChildrenClasses(startingPointClass);
		    		children.remove(startingPointClass);
		    	
		    		for (EClass eClass : children) {
		    			 LinkedList<EReference>  references2=getRefrences(eClass);    
		    	    	 
		    			  		 for (EReference eReference : references2) {
		    	    			 clusterReferences(eClass,  treated, eReference);
		    				}
		    	    		 
					}
					
			}
			else 
			{
				
				if(!safeEReferences.contains(callingRef)&&!oppositeEReferences.contains(callingRef)&&!unsafeEReferences.contains(callingRef))
				unsafeEReferences.add(callingRef);
				if(oppositeCalling!=null)
				{
					if(!unsafeEReferences.contains(oppositeCalling)&&!safeEReferences.contains(oppositeCalling)&&!oppositeEReferences.contains(oppositeCalling))
					{
						oppositeEReferences.add(oppositeCalling);
					}
				}
			}
			
		}
		

	}

	// Cannot cache this
	  public EObject generate(EPackage ePackage,String ruleName, EClass context, EObject callingParent, EReference callingRef,LinkedList<EObject> instances, EClass root, HashMap<EReference,LinkedList<EObject>> missing,LinkedList<EObject> visitedElements)
	    { 
		  
	

		EObject picked=null;
		if(callingRef!=null)
		 picked=  pickAnObjectFromRef(callingRef,callingParent);
		
		if(picked!=null)
		return picked;

	         boolean needAdjustement = isConflicting(context, callingRef);

	    	 EClass choosenEClass=null;
	    	 if(callingRef==null)
	    	  choosenEClass=getElu(context,needAdjustement);
	    	 else 
	    	 {EList<Stereotype> ref_steryotypes = getAllSteryotypesFromModel(callingRef);
	    	 if(contains(ref_steryotypes, "type dependency")==false)
	    		 choosenEClass=getElu(context,needAdjustement);
	    	 else
	    	 {   
	    		 

	    		 choosenEClass= getEluFromSeryotypes(context,ref_steryotypes,callingRef,callingParent);

	    	 	  if(choosenEClass==null)
	    	 	   choosenEClass=getElu(context,needAdjustement);
		    	   else choosenEClass=getElu(choosenEClass,needAdjustement);
	    	 }
	    	   
	    	 }
	    	  if(choosenEClass==null)
	    		  choosenEClass=context;
	    	  
	    	  EObject contextIntance=null;	 
	    	  LinkedList<EAttribute> attributs=null;
	    	  updateActualForChossenClass(choosenEClass);
	    	  if(needAdjustement)
	    	  updateClassProba(context,choosenEClass);
	    	   
	    	  

	    	  contextIntance = ((EPackage)ePackage).getEFactoryInstance().create(choosenEClass);
	    	  visitedElements.add(context);
	    	 
	    	  // update backword
	  	    if(callingRef!=null&&callingParent!=null)
    	    { 
    		if(safeEReferences.contains(callingRef))
    		updateLink(callingRef,callingParent,contextIntance);

    		}

	  	   
	     //already sorted
	    	  
	  	      attributs=getReleventAttributes(choosenEClass, all); 
	  	      LinkedList<EAttribute> attributesNow = new LinkedList<EAttribute>();
	  	      LinkedList<EAttribute> attributesAfter = new LinkedList<EAttribute>();
	    	  for (int i = 0; i <attributs.size(); i++) { 
	    	  EAttribute att = attributs.get(i);
	          LinkedList<EObject> dep = new LinkedList<EObject>();
	          if(getNeedsForAtt(att)!=null)
	          dep.addAll(getNeedsForAtt(att));
	    	  breakGeneralization(dep,context);
	    	  removeDuplicatEObject(dep);
	    	  if(dep.size()==0||att.isDerived())
	    		 attributesNow.add(attributs.get(i));
	    	  else 
	    	  {
	    		 boolean completed = checkIsCompleted (dep,visitedElements);
	    		 if(completed)
	    		attributesNow.add(attributs.get(i));
	    		else attributesAfter.add(attributs.get(i));
	    		  
	    		
	    	  }
	    	  }

	    	 
	    	  
	    	  //attributes of now
	    	  for (int i = 0; i <attributesNow.size(); i++) { 
	          EAttribute att = (EAttribute) attributesNow.get(i); 
	          visitedElements.add(att);
	          	if(att.isDerived()==false)
	          	{	boolean done=false;
	          	    done= handelAttributesSteryotyps(att,contextIntance,callingParent!=null?callingParent:contextIntance);
	          		if(done==false)
	          			createRandomAttribute(att,contextIntance,callingParent!=null?callingParent:contextIntance);
	          	}
	          }
	    	  for (int i = 0; i <attributesAfter.size(); i++) { 
		          EAttribute att = (EAttribute) attributesAfter.get(i); 
		          	if(att.isDerived()==false)
		          	{	boolean done=false;
		          	    done= handelAttributesSteryotypsWithoutDep(att,contextIntance,callingParent!=null?callingParent:contextIntance);
		          		if(done==false)
		          			createRandomAttribute(att,contextIntance,callingParent!=null?callingParent:contextIntance);
		          	}
		          }
	    	  removeDuplicatEObject(visitedElements);
	    	  
	    	  
		    	 //already sorted
	    	  LinkedList<EReference> references=getRefrences(choosenEClass);    
	    	
	    	  
	    	  
	    	  
	//HAck 
	    	  
	    		 for (int i = 0; i <references.size(); i++) {
	    			 EReference ref = references.get(i);
	    			 if(ref.getEOpposite()!=null)
        				 if(ref.getEOpposite().equals(callingRef))
        				   { references.add(0,references.get(i));
        					 references.remove(i+1);
        					
        				 }
 			
	    					 
	    		 }
	    		 removeDuplicat(references);
	    		
	   
	      
	    	 for (int i = 0; i <references.size(); i++) {
	    		 
	             

	    		     LinkedList<EObject> linkedToMe=new LinkedList<EObject>();
	    		     EReference ref = references.get(i);
	    		     visitedElements.add(ref);
	    			 EClass cible= ref. getEReferenceType();
	    			 int lower = ref.getLowerBound();
	    			 int upper = ref.getUpperBound();
	    			 if(upper==-1)
	    		     upper=1;
	    			 lower= FiledGenerator.randomIntegerRange(lower, upper);
	    			 


	    			 
	    			String value=handelAssociationsDependencies(ref, contextIntance, callingParent!=null?callingParent:contextIntance);
		    		boolean multiTreated=!value.equals("Non");
		    			
	    			 if(multiTreated)
	    			lower=Double.valueOf(value).intValue(); 
	    			else
	    			 {
	    			 EList<Constraint> cons=getConstraintsFromSteryotyped(ref, "multiplicity"); 
	    			 if(cons!=null)
	    			 if(cons.size()>0)
	    			 lower = Double.valueOf(handelMultFromConstraints(cons,contextIntance,callingParent!=null?callingParent:contextIntance,"int")).intValue();
	    			 }
	    			 int shouldPass=-1;
	    			 if(safeEReferences.contains(ref))
	    			 shouldPass=1;
	    			 else {if(oppositeEReferences.contains(ref))
	    				 shouldPass=3;
	    			 else 
	    				 if(unsafeEReferences.contains(ref))
	    				 shouldPass=2;
	    			 }
	    			 
	    			 
	    			//unsafe
	        		 if(shouldPass==2)
	        		 {   
	        			 int count = takeFromTheStack(contextIntance,(EClass)ref.getEType(),lower,ref);
	        			 // take from the pool
	        			 if(count < lower)
	        			 {
	        			 count =  takeFromThePool(contextIntance,(EClass)ref.getEType(),lower,ref,count,instances); 
	        			 
	        			 if(count<lower)
	        			 {
	        				 int recursionLeft ;
	        				 if(count_Recursion.get(ref)==null)
	        				 {
	        				 recursionLeft = maxAllowedRecurison;
	        				 count_Recursion.put(ref, 0);
	        				 }
	        				 else recursionLeft =count_Recursion.get(ref);
	        				 
	        				 if(lower-count>recursionLeft)
	        				 {
	        				 System.out.println("Unsafe reference "+ref.getEContainingClass().getName()+"==>"+ref.getEType().getName()+" exceded allowed recursion");
	        				 }
	        				 else 
	        				 {
	        					 LinkedList<EObject> condidates = new LinkedList<EObject>();
	        					 for (int j = 0; j < lower-count && count_Recursion.get(ref)<=maxAllowedRecurison; j++) {
	   
	        						 count_Recursion.replace(ref, count_Recursion.get(ref)+1);
									condidates.add(generate(ePackage, ruleName, (EClass)ref.getEType(), contextIntance, ref, instances,root,missing,visitedElements));
									
								}
	     
	        					 
	        					 updateLink(ref, contextIntance, condidates);
	        					 EReference theOpposite = ref.getEOpposite();
	        		        		if(theOpposite!=null)
	        		        		{   updateLink(theOpposite,condidates,contextIntance);}
	        		        		
	        		        		
	        				 }
	        			 }
	        			 
	        			 }
	        		 }
	        		 
	        		 
	        		 
	        		

	        		 //opposite
	        		 	 if(shouldPass==3)
	        		 {
	        		 		
	        	   if(ref.getEOpposite().equals(callingRef))
	        	    {
	        		if(callingParent!=null)
	        		{
	        			if(safeEReferences.contains(callingRef))
	        			{
	        		updateLink(ref,contextIntance,callingParent);
	        		//Doube update 
	        		EReference theOpposite = ref.getEOpposite();
	        		if(theOpposite!=null)
	        		{
	        			if(callingParent!=null)
	        			updateLink(theOpposite,callingParent,contextIntance);		
	        		}
	        		}
	        		}
	   
	        		
	        		 
	        		if(ref.isMany())
	           			 {
	        			

	           				EClassifier typeToComplete = ref.getEType();
	           			
	           				if(typeToComplete instanceof EClass)
	           				{
	           					boolean isOppOk=false;
	           					List actualLinks=(List)contextIntance.eGet(ref);
	           					int actualSize=actualLinks.size();
	           					isOppOk=actualSize>=lower;
	           					
	           					
	           					if(isOppOk==false)
	           					{
	           					EReference TheOpposite = ref.getEOpposite();
	           					if(TheOpposite!=null)
	           					{
	           						LinkedList<EObject> objects = missing.get(TheOpposite);
	           						if(objects==null)
	           						{
	           							objects = new LinkedList<EObject>();
	           							for (int j = 0; j < lower-actualSize; j++) {
	           								objects.add(contextIntance);
										}
	           						
	           							missing.put(TheOpposite, objects);
	           						}else
	           						{
	           							for (int j = 0; j < lower-actualSize; j++) {
	           								objects.add(contextIntance);
										}
	           						
	           							missing.remove(TheOpposite);
	           							missing.put(TheOpposite, objects);
	           						}
	           					}
	           					}
	           					
	           				
	           				
	           				 	
	           				
	           				}
	           				
	           			 }
	           		 
	           		 
	    	    	    
	    	    	  
	    	    	    
	        		 
	       
	        		 }}
	        		  
	    	    	    
	        		 
	        		 if(shouldPass==1)
	        		 {

	    			 //ensure minimum
	        	
	        		
	    			 for (int j = 0; j < lower; j++) {
	    			 boolean generate = false;
	    		     LinkedList<EObject> unsatObjects = missing.get(ref);
	    		     LinkedList<EObject> tempUnsatObjects = new LinkedList<EObject>();
	    		     if(unsatObjects!=null)
	    		     tempUnsatObjects.addAll(unsatObjects);
	    		     
	    		     if(unsatObjects==null)
	    		    	generate = true;
	    		     else{ 
	    		    	 if(ref.isMany()==false)
	    		    	 { EObject actualObject = (EObject)contextIntance.eGet(ref);
	    		    	 tempUnsatObjects.remove(actualObject);
	    		    	 }
	    		    	 else {
	    		    		 List actualObjects = (List)contextIntance.eGet(ref);
	    		    		 tempUnsatObjects.removeAll(actualObjects);
	    		    	 }
	    		    	 
	    		    	 if(tempUnsatObjects.size()==0)
	    		    	 generate = true;
	    		     }
	    			
	    		     if(generate)
			    	 linkedToMe.add(generate(ePackage,ruleName,cible,contextIntance, ref,instances,root,missing,visitedElements));
	    		     else 
	    		     {
	    		     int indx = new Random().nextInt(tempUnsatObjects.size());
	    		     linkedToMe.add(tempUnsatObjects.get(indx));
	    		     unsatObjects.remove(tempUnsatObjects.get(indx));
	    		     }
			    	 
    				 if(ref.isMany())
			    	        contextIntance.eSet(ref, linkedToMe);
			    	if(ref.isMany()==false && lower !=0) 
			         contextIntance.eSet(ref, linkedToMe.get(0));

	    			 }
	    			 
	    			
	    			  
	 	    	    EReference theOpposite = ref.getEOpposite();
	 	    	    if(theOpposite!=null)
					updateLink(theOpposite, linkedToMe, contextIntance);

			 	    			 
	    			 }		
	        		 
	        		 
	        		 //attributes after
	        	  	  for (int xx = 0; xx <attributesAfter.size(); xx++) { 
	        	    	  EAttribute att = attributesAfter.get(xx);
	        	          LinkedList<EObject> dep = getNeedsForAtt(att);
	        	          if(dep == null)
	        	        	dep = new LinkedList<EObject>();
	        	    	  breakGeneralization(dep,context);
	        	    	  removeDuplicatEObject(dep);
	        	    		 boolean completed = checkIsCompleted (dep,visitedElements);
	        	    		 if(completed)
	        	    		 {
	        	    			   	if(att.isDerived()==false)
	        	    	          	{
	        	    			   		handelAttributesSteryotypsAfter(att,contextIntance,callingParent!=null?callingParent:contextIntance);
	        	    	          	}
	        	    			   	visitedElements.add(attributesAfter.get(xx));
	        	    			   	attributesAfter.remove(xx);
	        	    			   	xx--;
	        
	        	    		 }
	        	  	  	}
	        	  	  removeDuplicatEObject(visitedElements);
	        		 
	        		 }
	    	    
	    	 
	    	 //attributes after
   	  	  for (int xx = 0; xx <attributesAfter.size(); xx++) { 
   	    	  EAttribute att = attributesAfter.get(xx);
   	          LinkedList<EObject> dep = getNeedsForAtt(att);
   	          if(dep == null)
   	        	dep = new LinkedList<EObject>();
   	    	  breakGeneralization(dep,context);
   	    	  removeDuplicatEObject(dep);
   	    		 boolean completed = checkIsCompleted (dep,visitedElements);
   	    		 if(completed)
   	    		 {
   	    		 	if(att.isDerived()==false)
    	          	{
    			   		handelAttributesSteryotypsAfter(att,contextIntance,callingParent!=null?callingParent:contextIntance);
    	          	}
   	    			   	visitedElements.add(attributesAfter.get(xx));
   	    			 	attributesAfter.remove(xx);
	    			   	xx--;
   	    		 }
   	  	  	}
   	  	  removeDuplicatEObject(visitedElements);
	    	 
	    	 
	    	    if(callingRef ==null)
	    		instances.add(0,contextIntance);
	    	    else {
	    	    	if (callingRef.isContainment()==false)
	    	    		instances.add(0,contextIntance);
	    	    }
	    	    
	    	    
        	    	  
        		 
        		 
	    	    return contextIntance;
			}
	  
	  private void updateActualForChossenClass(EClass choosenEClass) {
		if(choosenEClass!=null)
		{
			EList<EClass> supers = getAllSuperTypes(choosenEClass);
			LinkedList<Class> all = new LinkedList<Class>();
			for (EClass class1 : supers) {
			all.add((Class)getCorrespondant(class1, realClasses));
			}
			Class c = (Class)getCorrespondant(choosenEClass, realClasses);
			all.add(c);
			
			for (Class class1 : all) {
			if(actualStatusProbForClasses.get(class1)!=null)
			actualStatusProbForClasses.replace(class1, actualStatusProbForClasses.get(c)+1);
			else actualStatusProbForClasses.put(class1, 1);
			}
			
		}
		
	}


	private boolean checkIsCompleted( LinkedList<EObject> dep, LinkedList<EObject> visitedElements) {
		boolean res = true;
		int i=0;
		while(i<dep.size()&&res==true)
		{
			boolean treated = false;
			int j=0;
			while(j<visitedElements.size()&&treated==false)
			{ 
				if(dep.get(i) instanceof EClass&&visitedElements.get(j)instanceof EClass)
				{
					if(visitedElements.get(j)==dep.get(i))
						treated=true;
					else
				if(getAllChildrenClasses((EClass)visitedElements.get(j)).contains(dep.get(i)))
					treated=true;
				}
				else {
					if(dep.get(i)==visitedElements.get(j))
						treated=true;
				
				}
			j++;
			}
			if(treated==false)
			return false;
		i++;
		}
		return res;
	}


	private void filterByType(LinkedList<EObject> linkedObjects, EClassifier eType) {
	for (int j = 0; j < linkedObjects.size(); j++) {
		
		 EObject eObject = linkedObjects.get(j);
		 LinkedList<EClass> subtypes = getAllChildrenClasses(eObject.eClass());
		 subtypes.add(eObject.eClass());
		 if(!subtypes.contains(eType))
		 {
			 linkedObjects.remove(j);
		 	j--;
		 }
	}
	}
	  
	  public int takeFromTheStack(EObject contextIntance, EClass type, int lower, EReference ref)
	  {

			 LinkedList<EObject>linkedObjects = new LinkedList<EObject>();
			 getAllObjects(contextIntance,linkedObjects,type,new LinkedList<EObject>());
			 
			 // take from the stack
			 int count =0;
			 while(linkedObjects.size()!=0 && count< lower)
			 {   
				int idx1 = new Random().nextInt(linkedObjects.size());
				EObject condidate = ((EObject)linkedObjects.get(idx1));
				EReference theOpposite = ref.getEOpposite();
				boolean is_ok=true;
				if(theOpposite!=null)
				{
				try{
					if(theOpposite.getUpperBound()==1)
					{
						EObject temp = (EObject)condidate.eGet(theOpposite);
						if(temp!=null)
						is_ok = false;
					}
					else {
						List temp = (List)condidate.eGet(theOpposite);
						if(temp!=null)
						if(temp.size()==theOpposite.getUpperBound())
						is_ok = false;
					}
				}catch(Exception e){e.printStackTrace();}
				}
				
				if(is_ok)
				{
				//Doube update 
				updateLink(ref,contextIntance,condidate);
        		if(theOpposite!=null)
        		updateLink(theOpposite,condidate,contextIntance);
        		count ++;
				}
        		
				linkedObjects.remove(idx1);

			 }
			 return count;
			 
	  }
	  
	  
	  
	  public int takeFromThePool(EObject contextIntance, EClass type, int lower, EReference ref, int oldCount, LinkedList<EObject> instances)
	  {

             int count = oldCount;
			 LinkedList<EObject>linkedObjects = new LinkedList<EObject>();
			 
			 for (EObject eObject : instances) {
     			getAllObjects(eObject,linkedObjects,type,new LinkedList<EObject>());
					}
		
			 
     			 
     			 List get =(List) contextIntance.eGet(ref);
     			 EObject temp=null;
     			 if(ref.getUpperBound()==1)
						{
						    temp = (EObject)contextIntance.eGet(ref);
						}
						else {
						    get = (List)contextIntance.eGet(ref);
						}
     			 if(get!=null)
     			 linkedObjects.removeAll(get);
     			 if(temp!=null)
     			 linkedObjects.remove(temp);
     			 
     			 
     			 
     			 while(linkedObjects.size()!=0 && count< lower)
     			 {  int idx1 = new Random().nextInt(linkedObjects.size());
						EObject condidate = ((EObject)linkedObjects.get(idx1));
						EReference theOpposite = ref.getEOpposite();
						boolean is_ok=true;
						if(theOpposite!=null)
						try{
							if(theOpposite.getUpperBound()==1)
							{
								EObject temp1 = (EObject)condidate.eGet(theOpposite);
								if(temp1!=null)
								is_ok = false;
							}
							else {
								List temp1 = (List)condidate.eGet(theOpposite);
								if(temp1!=null)
								if(temp1.size()==theOpposite.getUpperBound())
								is_ok = false;
							}
						}catch(Exception e){e.printStackTrace();}
						
						
						if(is_ok)
						{
						//Doube update 
						updateLink(ref,contextIntance,condidate);
		        		if(theOpposite!=null)
		        		updateLink(theOpposite,condidate,contextIntance);
		        		count ++;
						}
		        		
						linkedObjects.remove(idx1);

     			 }
     			 
     			 
     			 return count;
     			 
	  }
	  
		private void getAllObjects(EObject contextIntance,LinkedList<EObject> res,LinkedList<EObject> sav) {
			if(sav.contains(contextIntance))
				return ;
			else sav.add(contextIntance);
			if(!res.contains(contextIntance))
			{  
			
				EList<EObject> links = new BasicEList<EObject>();
				EList<EReference> myRefs = contextIntance.eClass().getEReferences();
				for (EReference ref : myRefs) {
					
				if(ref.getUpperBound()!=1)
				{
				List temp = ((List)contextIntance.eGet(ref));
		   		if(temp==null)
		   		temp = new BasicEList<EObject>();
		   		links.addAll(temp);
		   		
				}
				else {
					EObject temp = (EObject)contextIntance.eGet(ref);
					if(temp!=null)
					links.add(temp);
				}
		   		

				for (EObject eObject : links) {
						if(!res.contains(eObject))
							res.add(eObject);
						getAllObjects(eObject,res,sav);				
				}
				}
			}
			
		} 
		
	

	private void getAllObjects(EObject contextIntance,LinkedList<EObject> res, EClass eType,LinkedList<EObject> sav) {
		if(sav.contains(contextIntance))
			return ;
		else sav.add(contextIntance);
		if(!res.contains(contextIntance))
		{  
		 EList<EClass> superTypes = contextIntance.eClass().getEAllSuperTypes();

		 if(superTypes.contains(eType)||contextIntance.eClass().equals(eType))
		 {
			res.add(contextIntance);
		 }
	
			EList<EObject> links = new BasicEList<EObject>();
			EList<EReference> myRefs = contextIntance.eClass().getEReferences();
			
			for (EReference ref : myRefs) {
				
			if(ref.getUpperBound()!=1)
			{
			List temp = ((List)contextIntance.eGet(ref));
	
	   		links.addAll(temp);
			}
			else {
				EObject temp = (EObject)contextIntance.eGet(ref);
				if(temp!=null)
				links.add(temp);
			}
	   		
		
			for (EObject eObject : links) {
					getAllObjects(eObject,res,eType,sav);				
			}
			}
		}
		
	} 

	private EClass convertClassForPackage(EClass choosenEClass, EPackage ePackage) {
		  if (ePackage.getEClassifiers() != null)
		    for (EClassifier c : ePackage.getEClassifiers())
		      if (c instanceof EClass) 
		       if(c.getName().equals(choosenEClass.getName()))
		    	   return (EClass) c;
		return null;
	}

	private EReference getRefByName(EObject copyOfFiller, String name) {
		  EClass eClass = copyOfFiller.eClass();
	        for (int j = 0, size = eClass.getFeatureCount(); j < size; ++j)
	        {
	          EStructuralFeature eStructuralFeature = eClass.getEStructuralFeature(j);
	          if (eStructuralFeature.isChangeable() && !eStructuralFeature.isDerived())
	          {
	            if (eStructuralFeature instanceof EReference)
	            {
	              EReference eReference = (EReference)eStructuralFeature;
	              if(eReference.getName().equals(name))
	            	  return eReference;
	            }
	          }
	        }
	        return null;
	}

	public Collection<EObject> transformToCollection(LinkedList<EObject> instances)
	  {
		 Collection<EObject> res =  new LinkedHashSet<EObject>();
		
		 for (EObject eObject : instances) {
			res.add(eObject);
		}
		 return res;
	  }
	  
	
	public void updateLink(EReference ref, EObject contextIntance, EObject otherObject )
	{
		if(ref.getUpperBound()==1)
   		{
   		contextIntance.eSet(ref, otherObject);

		}
   		else 
   		{
   		
   		List temp = (List)contextIntance.eGet(ref);
   		temp.add(otherObject);
   	    LinkedList<EObject> container = new LinkedList<EObject>();
   	    if(temp!=null)
   	    container.addAll(temp);
   	    if(otherObject!=null)
   	    container.add(otherObject);
   		contextIntance.eSet(ref, container);
   		temp = (List)contextIntance.eGet(ref);


   		}
	}
	
	public void updateLink(EReference ref, LinkedList<EObject> contextIntances, EObject otherObject )
	{
		
		for (EObject contextIntance : contextIntances) {
			
		
		if(ref.getUpperBound()==1)
   		{
   		contextIntance.eSet(ref, otherObject);
   		}
   		else 
   		{
   		
   			List temp = (List)contextIntance.eGet(ref);
   	   		temp.add(otherObject);
   	   	    LinkedList<EObject> container = new LinkedList<EObject>();
   	   	    if(temp!=null)
   	   	    container.addAll(temp);
   	   	    if(otherObject!=null)
   	   	    container.add(otherObject);
   	    	contextIntance.eSet(ref, container);
   
   		}
		}
	}
	
	
	public void updateLink(EReference ref, EObject contextIntance, LinkedList<EObject> otherObjects )
	{
		if(ref.getUpperBound()==1)
   		{
		if(otherObjects!=null)
		if(otherObjects.size()>0)
		if(otherObjects.get(0)!=null)
   		contextIntance.eSet(ref, otherObjects.get(0));
   		}
   		else 
   		{
   		
   		List temp = (List)contextIntance.eGet(ref);
   	   LinkedList<EObject> container = new LinkedList<EObject>();
   	   	if(temp!=null)
   	   	container.addAll(temp);
   	   	if(otherObjects!=null)
   	   	container.addAll(otherObjects);
   		contextIntance.eSet(ref, temp);
   		}
	}
	
	
	
	
	
		public EObject pickAnObjectFromRef(EReference ref,EObject parentInstance)
		{
			boolean done=false;
			EObject res=null;
	        EList<Stereotype> relatedSteryotypes = sortSteryotypes(getAllSteryotypesFromModel(ref));
	        Stereotype stereotype=null;
			for (int ixx = 0; ixx < relatedSteryotypes.size() && done==false; ixx++) {
	        	 stereotype=relatedSteryotypes.get(ixx);   	
	  
	        	
	        	if(stereotype.getName().equals("use existing")  && done==false)
	        	{
	 
	        		
	        		
	            	boolean toBeExecuted=true;
	            	try{
	            		double execution_proba= Double.valueOf((Double)getValue(ref, stereotype,"reuseProbability")).doubleValue();
	            		UniformRealDistribution dis= new UniformRealDistribution(0,1);
	            		double chance = dis.sample();
	            		toBeExecuted = execution_proba >=chance;
	            	}
	            	catch(Exception ex){ex.printStackTrace();}
	            	
	            	if(toBeExecuted)
	            	{
	            		
	        		Class context = (Class)getValue(ref, stereotype,"context");

	        		if(isTheContext(parentInstance, getEVersion(context)))
	        		{
	        			    OCLForJAVA.init("", parentInstance);	
	        			    Constraint c = 	(Constraint)getValue(ref, stereotype,"eligibleCondidatesQuery");
	        			    String toBeEvaluated = c.getSpecification().stringValue();	
	        			    Collection<EObject> selectable = OCLForJAVA.evaluateECollection(parentInstance, toBeEvaluated, "temp", "Physical_Person", "Set");
	    					if(selectable!=null)
	    					if(selectable.size()>0)
	    					{
	    						int index = FiledGenerator.randomIntegerRange(0, selectable.size()-1);
	    						res=(EObject)selectable.toArray()[index];
	    					}
					
	        		}
	        	}
			}
		}
			
	        	return res;
	        	
		}
		
		  public EList<Constraint> getConstraintsFromSteryotyped(EAttribute att, String name)
			{
			    EList<Constraint>  res=new BasicEList<Constraint>();
		        EList<Stereotype> relatedSteryotypes = getAllSteryotypesFromModel(att);
		        for (Stereotype stereotype : relatedSteryotypes) {
		 
					if(stereotype.getName().equals(name))
					{       	
						
						Class target =  null;
						if(name.equals("multiplicity"))
							target =  (Class) getValue(att, stereotype,"targetMember");
						else
						 target =  (Class) getValue(att, stereotype,"context");
						
						if(target==null&&!isPremitive(att))
						{System.err.println("Target Member for multiplicity of att "+att.getName()+" needs to be specified!");}
						else
						{
							
							if(isPremitive(att))
							{

							@SuppressWarnings("unchecked")
							EList<Constraint> constraint =  (EList<Constraint>) getValue(att, stereotype,"constraint");
						if(constraint!=null)
						res= constraint;
						
		
							}
					}
					}
				}
				return res;
			}
	  
	  public EList<Constraint> getConstraintsFromSteryotyped(EReference ref, String name)
		{
		    EList<Constraint>  res=new BasicEList<Constraint>();
	        EList<Stereotype> relatedSteryotypes = getAllSteryotypesFromModel(ref);
	        for (Stereotype stereotype : relatedSteryotypes) {
	 
				if(stereotype.getName().equals(name))
				{       	
					
					Class target =  null;
					if(name.equals("multiplicity"))
						target =  (Class) getValue(ref, stereotype,"targetMember");
					else
					 target =  (Class) getValue(ref, stereotype,"context");
			
					if(ref.getEType().getName().equals(target.getName()))
					{
					
						@SuppressWarnings("unchecked")
						EList<Constraint> constraint =  (EList<Constraint>) getValue(ref, stereotype,"constraint");
					if(constraint!=null)
					res= constraint;
					}
					else {
						if(name.equals("multiplicity"))
						{
							EReference theOppEReference=ref.getEOpposite();
							if(ref!=null)
							{
								target =(Class)getCorrespondant(theOppEReference.getEType(), realClasses);
								@SuppressWarnings("unchecked")
								EList<Constraint> constraint =  (EList<Constraint>) getValue(ref, stereotype,"opposite");
								if(constraint!=null)
								res= constraint;
							}
						}
					}
				}
			
			}
			return res;
		}
	
	//cached
	public  LinkedList<EObject> getNeedsForRef(EReference ref)
	{

		LinkedList<EObject> cut = cache_getNeedsForRef.get(ref.toString());
		if(cut!=null)
		return cut;
		
        EList<Stereotype> relatedSteryotypes = sortSteryotypes(getAllSteryotypesFromModel(ref));
        for (Stereotype stereotype : relatedSteryotypes) {

        	if(stereotype.getName().equals("value dependency") || stereotype.getName().equals("type dependency"))   
        	{  
        		Class context = (Class)getValue(ref, stereotype,"context");
        	
        		if(context!=null && !ref.getEType().getName().equals(context.getName()) && ref.getEContainingClass().getName().equals(context.getName()))
        		{
        			@SuppressWarnings("unchecked")
					EList<Constraint> constraints=(EList<Constraint>)getValue(ref, stereotype,"OCLtrigger");
        				   for (Constraint c : constraints) {
        		        	
							try {
								OclDependencies.ref=ref;
								OclDependencies.classe=null;
								OclDependencies.att=null;
							    OclDependencies oclVisitor = OclDependencies.getInstance();  
							    OclDependencies.helper.setContext(getEVersion(context));  
								org.eclipse.ocl.ecore.Constraint tempConstraint = OclDependencies.helper.createInvariant(CharMatcher.ASCII.retainFrom(c.getSpecification().stringValue()));
								ExpressionInOCL<?, ?> oclExpression = (ExpressionInOCL<?, ?>) tempConstraint.getSpecification();
								oclExpression.accept(oclVisitor);

							} catch (ParserException e) {e.printStackTrace();}
 
        		        }
       		
        		}
        	}
        	
        	if(stereotype.getName().equals("probabilistic type"))
        	{	Class context = (Class)getValue(ref, stereotype,"context");
        		String OCL=(String)getValue(ref, stereotype,"OCLtrigger");
        		try {
					OclDependencies.ref=ref;
					OclDependencies.classe=null;
					OclDependencies.att=null;
				    OclDependencies oclVisitor = OclDependencies.getInstance();  
				    OclDependencies.helper.setContext(getEVersion(context));  
					org.eclipse.ocl.ecore.Constraint tempConstraint = OclDependencies.helper.createInvariant(OCL);
					ExpressionInOCL<?, ?> oclExpression = (ExpressionInOCL<?, ?>) tempConstraint.getSpecification();
					oclExpression.accept(oclVisitor);

				} catch (ParserException e) {e.printStackTrace();}
        	}
        	
         	if(stereotype.getName().equals("use existing"))
        	{	Class context = (Class)getValue(ref, stereotype,"context");
        		Constraint c = 	(Constraint)getValue(ref, stereotype,"eligibleCondidatesQuery");
        		String OCL=c.getSpecification().stringValue();	
        		try {
					OclDependencies.ref=ref;
					OclDependencies.classe=null;
					OclDependencies.att=null;
				    OclDependencies oclVisitor = OclDependencies.getInstance();  
				    OclDependencies.helper.setContext(getEVersion(context));  
				    org.eclipse.ocl.ecore.Constraint tempConstraint = OclDependencies.helper.createInvariant("("+OCL+")->size()>1");
					ExpressionInOCL<?, ?> oclExpression = (ExpressionInOCL<?, ?>) tempConstraint.getSpecification();
					oclExpression.accept(oclVisitor);

					

				} catch (ParserException e) {e.printStackTrace();}
        	}
        }
        
        
        
        EClass TargetClass = (EClass)ref.getEType();       
        LinkedList<EClass> equivalentClasses = Graphs.getEquivelentClasses(TargetClass);
       for (EClass eClass : equivalentClasses) {
    	   EList<EAttribute> temp = eClass.getEAllAttributes();
    	   
    	   for (EAttribute eAttribute : temp) {
    		   getNeedsForAtt(eAttribute);
           	;
           	
           	if(OclDependencies.refrencesNeeds.get(ref.toString())==null)
           		OclDependencies.refrencesNeeds.put(ref.toString(), new LinkedList<EObject>());
           	
           	if(OclDependencies.attributesNeeds.get(eAttribute.toString())!=null)
           		OclDependencies.refrencesNeeds.get(ref.toString()).addAll(OclDependencies.attributesNeeds.get(eAttribute.toString()));

		}
    	  
	}
    
       
       
      cache_getNeedsForRef.put(ref.toString(),  OclDependencies.refrencesNeeds.get(ref.toString()));
      return OclDependencies.refrencesNeeds.get(ref.toString());

	}
	
	//cached
	@SuppressWarnings("unchecked")
	public LinkedList<EObject> getNeedsForAtt(EAttribute att)
	{

		LinkedList<EObject> cut = cache_getNeedsForAtt.get(att.toString());
		if(cut!=null)
		return cut;
	

        EList<Stereotype> relatedSteryotypes = sortSteryotypes(getAllSteryotypesFromModel(att));
        for (Stereotype stereotype : relatedSteryotypes) {

          	if(stereotype.getName().equals("use existing"))
        	{  
        		Class context = (Class)getValue(att, stereotype,"context");
        		if(context!=null)
        		{
        			
         		Constraint c = 	(Constraint)getValue(att, stereotype,"eligibleCondidatesQuery");
         		String toBeEvaluated=c.getSpecification().stringValue();	
        		findDependencies(att, context, toBeEvaluated);
        		}
        	}

        	if(stereotype.getName().equals("value dependency")||stereotype.getName().equals("type dependency"))
        	{
        		
        		Class context = (Class)getValue(att, stereotype,"context");
        	
        		if(context!=null)
        		{

					EList<Constraint> constraints=(EList<Constraint>)getValue(att, stereotype,"OCLtrigger");
					
	
					for (int i = 0; i < constraints.size(); i++) {
							   findDependencies(att, context, CharMatcher.ASCII.retainFrom(constraints.get(i).getSpecification().stringValue()));
        					   getNeedsForcVIAConstraint(att, constraints.get(i));
        		        
      		
					}
        	
        				
        		}
        	}
        	

        	
			if(stereotype.getName().equals("fixed value"))
			{

				boolean isOCL =  (boolean) getValue(att, stereotype,"isOCL");
				if(isOCL)
				{
				String value = String.valueOf( getValue(att, stereotype,"value"));
           		Class context = (Class)getValue(att, stereotype,"context");
				findDependencies(att, context, value);	
				}
			}
			
			if(stereotype.getName().equals("from uniform range"))
			{
				boolean isOCL =  (boolean) getValue(att, stereotype,"isOCL");
				if(isOCL)
				{
				String minimum = String.valueOf( getValue(att, stereotype,"lowerbound"));
				String maximum =String.valueOf( getValue(att, stereotype,"upperbound"));
	           	Class context = (Class)getValue(att, stereotype,"context");
				findDependencies(att, context, minimum);	
				findDependencies(att, context, maximum);	
				}
			}
			
			
			if(stereotype.getName().equals("from distribution"))
			{
				boolean isOCL =  (boolean) getValue(att, stereotype,"isOCL");
				if(isOCL)
				{
				EList<String>values = (EList<String>)getValue(att, stereotype,"parametersValues");
	           	Class context = (Class)getValue(att, stereotype,"context");
	           	for (String value : values) {
	           		findDependencies(att, context, value);	
				}
		
				}
			}
			

			if(stereotype.getName().equals("from histogram"))
			{

				boolean isOCL =  (boolean) getValue(att, stereotype,"isOCL");
				if(isOCL)
				{
				EList<String> values = (EList<String>)  getValue(att, stereotype,"bins");
           		Class context = (Class)getValue(att, stereotype,"context");
           		
           		
           		
           		for (int i = 0; i < values.size(); i++) {
					String value = values.get(i);
			
           		if( (value.trim().startsWith("[")&&value.trim().endsWith("]"))==false)
           			findDependencies(att, context, value);
           		else
        		{
        			if(value.trim().contains(".."))
        			{
        				value=value.trim().replace("[", "").replace("]", "").replace("..", "-");
        				String [] temp=value.split("-");
        				findDependencies(att, context, temp[0].trim());
        				findDependencies(att, context, temp[1].trim());

        			}
        			
        			if(value.trim().contains("-"))
        			{
        				value=value.trim().replace("[", "").replace("]", "");
        				String [] temp=value.split("-");
        				findDependencies(att, context, temp[0].trim());
        				findDependencies(att, context, temp[1].trim());    				

        			}
        			
        			if(value.trim().contains(","))
        			{
        				String [] temp=value.trim().replace("[", "").replace("]", "").split(",");
        				for (int j = 0; j < temp.length; j++) 
        					findDependencies(att, context, temp[j].trim());
						
        				
        				
        			}
        		}
           		}

				}
        }
        }
        
        
   cache_getNeedsForAtt.put(att.toString(), OclDependencies.attributesNeeds.get(att.toString()));
   return OclDependencies.attributesNeeds.get(att.toString());
	}

	
	
	//No need to cache this 
	public static void findDependencies (EObject att, Class context, String ch)
	{
	   if(ch.trim().equals("")==false)
	   {
		if(att instanceof EAttribute)
		{
		OclDependencies.ref=null;
		OclDependencies.classe=null;
		OclDependencies.att=(EAttribute)att;
		}
		
		if(att instanceof EReference)
		{
			OclDependencies.ref=(EReference)att;
			OclDependencies.classe=null;
			OclDependencies.att=null;
		}
		
		if(att instanceof EClass)
		{
			OclDependencies.ref=null;
			OclDependencies.classe=(EClass) att;
			OclDependencies.att=null;
		}
		OclDependencies oclVisitor = OclDependencies.getInstance();  
	    OclDependencies.helper.setContext(getEVersion(context));  
		org.eclipse.ocl.ecore.Constraint tempConstraint;
		try {
			tempConstraint = OclDependencies.helper.createInvariant("("+ch+")"+".oclIsUndefined()");
			tempConstraint.setName("tempConstraint");
			ExpressionInOCL<?, ?> oclExpression = (ExpressionInOCL<?, ?>) tempConstraint.getSpecification();
			oclExpression.accept(oclVisitor);
		} catch (ParserException e) {
			e.printStackTrace();
		}
	   }
		
	}
	
	
	
	//No need to cache this 
	@SuppressWarnings("unchecked")
	public static void getNeedsForcVIAConstraint(EObject att,Constraint c)
	{


        EList<Stereotype> relatedSteryotypes = sortSteryotypes(getAllSteryotypesFromModel(c));
        for (Stereotype stereotype : relatedSteryotypes) {

        	
			if(stereotype.getName().equals("fixed value"))
			{

				boolean isOCL = (boolean)getValue(c, stereotype,"isOCL");
				if(isOCL)
				{
				String value = String.valueOf( getValue(c, stereotype,"value"));
           		Class context = (Class)getValue(c, stereotype,"context");
				findDependencies(att, context, value);	
				}
			}
			
			if(stereotype.getName().equals("from uniform range"))
			{
				boolean isOCL = (boolean)getValue(c, stereotype,"isOCL");
				if(isOCL)
				{
				String minimum = String.valueOf( getValue(c, stereotype,"lowerbound"));
				String maximum = String.valueOf( getValue(c, stereotype,"upperbound"));
	           	Class context = (Class)getValue(c, stereotype,"context");
				findDependencies(att, context, minimum);	
				findDependencies(att, context, maximum);	
				}
			}
			
			
			if(stereotype.getName().equals("from histogram"))
			{

				boolean isOCL =  (boolean) getValue(c, stereotype,"isOCL");
				if(isOCL)
				{
				EList<String> values = (EList<String>)  getValue(c, stereotype,"bins");
           		Class context = (Class)getValue(c, stereotype,"context");
           		
           		
           		
           		for (int i = 0; i < values.size(); i++) {
					String value = values.get(i);
			
           		if( (value.trim().startsWith("[")&&value.trim().endsWith("]"))==false)
           			findDependencies(c, context, value);
           		else
        		{
        			if(value.trim().contains(".."))
        			{
        				value=value.trim().replace("[", "").replace("]", "").replace("..", "-");
        				String [] temp=value.split("-");
        				findDependencies(att, context, temp[0].trim());
        				findDependencies(att, context, temp[1].trim());

        			}
        			
        			if(value.trim().contains("-"))
        			{
        				value=value.trim().replace("[", "").replace("]", "");
        				String [] temp=value.split("-");
        				findDependencies(att, context, temp[0].trim());
        				findDependencies(att, context, temp[1].trim());    				

        			}
        			
        			if(value.trim().contains(","))
        			{
        				String [] temp=value.trim().replace("[", "").replace("]", "").split(",");
        				for (int j = 0; j < temp.length; j++) 
        					findDependencies(att, context, temp[j].trim());
						
        				
        				
        			}
        		}
           		}

				}
        }
			
			
			if(stereotype.getName().equals("from distribution"))
			{
				boolean isOCL = (boolean)getValue(c, stereotype,"isOCL");
				if(isOCL)
				{
				EList<String>values = (EList<String>)getValue(c, stereotype,"parametersValues");
	           	Class context = (Class)getValue(c, stereotype,"context");
	           	for (String value : values) {
	           		findDependencies(att, context, value);	
				}
		
				}
			}
        }
        	

	}
	
	//No need to cache this 
	public void resolveEReferencesTesnion(LinkedList<EReference> references)
	{
		 	
	   if(references==null)
		references= new LinkedList<EReference>();
		
	   for (int i = 0; i < references.size()-1; i++) {
		    for (int j = i+1; j < references.size(); j++) {
		       EReference refI= references.get(i);
			   EClass classeI = (EClass)refI.getEType();
			   String nameI = classeI.getName();
				
			   EReference refJ = references.get(j);
			   EClass classeJ = (EClass)refJ.getEType();
			   String nameJ = classeJ.getName();
			   
			   int posI= getPosInOrder(nameI);
			   int posJ= getPosInOrder(nameJ);
			   
			   // 0 is the highest priority 
			   if(posI > posJ)
			   {
				   references.add(i+1,refJ);
				   references.remove(i);
				   references.add(j+1,refI);
				   references.remove(j);
			   }
			   
			 	
		}
	}

	}
	
	//No need to cache this
	public void resolveEAttributesTesnion(LinkedList<EAttribute> atts)
	{
		
		if(atts==null)
		atts=new LinkedList<EAttribute>();
		
		for (int i = 0; i < atts.size()-1; i++) 
		{	for(int j=i+1 ; j<atts.size();j++){
			EAttribute attI=atts.get(i);
			getNeedsForAtt(attI);
			EAttribute attJ=atts.get(j);
			getNeedsForAtt(attJ);

                if(requires(attI,attJ))
                {
                atts.add(i+1,attJ);
                atts.remove(i);
                atts.add(j+1,attI);
                atts.remove(j);
                	
                }

		}
		}
	
		
	}
	
	//Cached
	public int getPosInOrder(String name)
	{
		int  res= -1;
		Integer cut= cache_PosInOrder.get(name);
		if(cut!=null)
		return cut.intValue();
		
		String v;
		
		CreateTaxpayers.orderIterator = new TopologicalOrderIterator<String, DefaultEdge>(GeneratorThread.g);
	
		 while (orderIterator.hasNext()) {
				
			 res++;
			 v = orderIterator.next();
			 if(v.contains(name))
			 {  cache_PosInOrder.put(name, new Integer(res));
				return res;
			 }
	       }
		
		cache_PosInOrder.put(name, new Integer(res));
		return res;
	}
	
	
	
	//cached
	public boolean requires (EAttribute attI, EAttribute attJ)
	{
	    boolean res=false;
	    
	    
	    String cut = cache_requires.get(attI.toString()+"#"+attJ.toString());
	    if(cut!=null)
	    return cut.equals("true");
	    LinkedList<EObject> temp = OclDependencies.attributesNeeds.get(attI.toString()); 
	    if(temp==null)
	    { Iterator<Map.Entry <String, LinkedList<EObject>>> it =  OclDependencies.attributesNeeds.entrySet().iterator();
	    while (it.hasNext()&&temp==null) {
	        Map.Entry <String, LinkedList<EObject>> pair = (Map.Entry<String, LinkedList<EObject>>)it.next();
	        if(pair.getKey().contains(attI.getName()) )
	        		temp =pair.getValue();
	        it.remove(); 
	    }
	    }
		LinkedList<EObject> depI=purifyDependenciesForAttributes(attI,temp);
	   
	    if(depI.size()==0)
	    {
	    cache_requires.put(attI.toString()+"#"+attJ.toString(),"false");
	    return false;
	    }
	    else
	    	if(exitsIn(attJ,depI))
	    	{	cache_requires.put(attI.toString()+"#"+attJ.toString(),"true");
	    		return true;
	    	}
	    
	   cache_requires.put(attI.toString()+"#"+attJ.toString(),String.valueOf(res));
	   return res;
	}

// No need to cache this
	public static boolean exitsIn(EAttribute att, 	LinkedList<EObject> liste)
	{
		boolean res=false;
		int i=0;
		while (res==false && i<liste.size()) {
			if(liste.get(i) instanceof EAttribute)
			{
				if(((EAttribute)liste.get(i)).getName().trim().equals(att.getName()) && ((EAttribute)liste.get(i)).getEContainingClass().getName().trim().equals(att.getEContainingClass().getName().trim()))
					res=true;
			}
			
			i++;
			
		}
		return res;
	}

	
	// No need to cache this
	public static  LinkedList<EObject> purifyDependenciesForAttributes(EAttribute att, LinkedList<EObject> dep)
	{
		 LinkedList<EObject> res = new LinkedList<EObject>();
		 if(dep==null) return res;
		 res.addAll(dep);
		 for (int i = 0; i < res.size(); i++) {
			 boolean delete=false;
			 if(res.get(i) instanceof EAttribute ==false)
				 delete=true;
			
			 if(delete)
			 {
				 res.remove(i);
				 i--;
			 }
			
		}
		 return res;
	}
	

	
	//cached
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
	
	

		// Is already quick
		public Constraint getConstraintFromSteryotyped(EReference ref,Constraint constraintEntrante, String name)
		{
			Constraint res=null;
	        EList<Stereotype> relatedSteryotypes = getAllSteryotypesFromModel(constraintEntrante);
	        for (Stereotype stereotype : relatedSteryotypes) {
	 
				if(stereotype.getName().equals(name))
				{       	
					Class target =  (Class) getValue(constraintEntrante, stereotype,"context");
			
					if(ref.getEType().getName().equals(target.getName()))
					{
					
					Constraint constraint =  (Constraint) getValue(constraintEntrante, stereotype,"constraint");
				
					res= constraint;
					}
				}
			
			}
			return res;
		}
		
		//Cannot be cached
		@SuppressWarnings("unchecked")
		public String handelMultFromConstraints(EList<Constraint> allCons,EObject contextIntance, EObject parentInstance, String type)
		{   
			

			String res="";
			boolean done=false;
			for (int z = 0; z < allCons.size() && done == false; z++) {
				Constraint cons= allCons.get(z);
			
			
			
			
	        EList<Stereotype> relatedSteryotypes = sortSteryotypes(getAllSteryotypesFromModel(cons));
	        Stereotype stereotype=null;
			for (int ixx = 0; ixx < relatedSteryotypes.size() && done==false; ixx++) {
	        	 stereotype=relatedSteryotypes.get(ixx);   	
	        	
	     
	        	
	        	if(stereotype.getName().equals("value dependency"))
	        	{  
	        		Class context = (Class)getValue(cons, stereotype,"context");
	        	
	        		if(context!=null)
	        		{
	        			EList<Constraint> constraints=(EList<Constraint>)getValue(cons, stereotype,"OCLtrigger");
	        			for (int i = 0; (i < constraints.size())&&(done==false); i++) {
	        				Constraint constraint = constraints.get(i);
	        				boolean is_the_parent_context= isTheContext(parentInstance, getEVersion(context));
	        				boolean isThe_Instance= isTheContext(contextIntance, getEVersion(context));
	        				if(isThe_Instance && is_the_parent_context)
	        				{

	        					if(evaluateQuery(contextIntance,getEVersion(context),CharMatcher.ASCII.retainFrom(constraint.getSpecification().stringValue())).equals("true"))
		        				{
		        				EList<Constraint> container = new BasicEList<Constraint>();
		        				container.add(constraint);
		        				String value=handelMultFromConstraints(container,contextIntance,parentInstance,type);
		        				
		        				done=true;
		        				res=value;
		        				}
	        				}
	        				else 
	        				{
	        				if(is_the_parent_context)
	        				{
	        				if(evaluateQuery(parentInstance,getEVersion(context),CharMatcher.ASCII.retainFrom(constraint.getSpecification().stringValue())).equals("true"))
	        				{
	        				EList<Constraint> container = new BasicEList<Constraint>();
	        				container.add(constraint);
	        				String value=handelMultFromConstraints(container,contextIntance,parentInstance,type);
	        				done=true;
	        				res=value;
	        				}
	        				}
	        				else{	if(is_the_parent_context)
	        				{
	        					if(evaluateQuery(contextIntance,getEVersion(context),CharMatcher.ASCII.retainFrom(constraint.getSpecification().stringValue())).equals("true"))
		        				{
		        				EList<Constraint> container = new BasicEList<Constraint>();
		        				container.add(constraint);
		        				String value=handelMultFromConstraints(container,contextIntance,parentInstance,type);
		        				done=true;
		        				res=value;
		        				}
	        				}
	        					
	        				}
	        				}
						}
	        			
	        		
	        		}
	        	}
	        	
	        
	         	
	    	
	        	
				if(stereotype.getName().equals("fixed value") && done==false)
				{

					String value = String.valueOf( getValue(cons, stereotype,"value"));	
			    	boolean isOCL = (boolean)getValue(cons, stereotype,"isOCL");
					done=true;
					if(isOCL==false)
					res=value;
					else 
					{
					Class contextTemp = (Class)getValue(cons, stereotype,"context");
					EClass context = getEVersion(contextTemp);
					
					
					if(isTheContext(contextIntance, context))
					{
						OCLForJAVA.init("", contextIntance);
						res= OCLForJAVA.evaluateString(contextIntance, value, "evaluation");
					}
					
					else
					{   
					if(isTheContext(parentInstance, context))
					{
					OCLForJAVA.init("", parentInstance);
					res= OCLForJAVA.evaluateString(parentInstance, value, "evaluation");
					}
					else {System.err.println("Wrong context for steryotype fixed - Multiplicities - " +cons.getSpecification().toString()); done = false;}
					}
					}
				
				}
				
				if(stereotype.getName().equals("from uniform range") && done==false)
				{  
				   
					String maximum = String.valueOf( getValue(cons, stereotype,"upperbound"));
					String minimum = (String.valueOf( getValue(cons, stereotype,"lowerbound")));
					String precision = String.valueOf( getValue(cons, stereotype,"precision"));
					boolean isOCL = (boolean)getValue(cons, stereotype,"isOCL");
					done=true;
					if(isOCL==false)
					{
			
						res = String.valueOf( FiledGenerator.randomDoubleRange(Double.valueOf(minimum).intValue(), Double.valueOf(maximum).intValue(),Double.valueOf(precision).intValue()));
						res =String.valueOf( castValue(res,type));
					}
					else
					{
						
						Class contextTemp = (Class)getValue(cons, stereotype,"context");
						EClass context = getEVersion(contextTemp);
						
						if(isTheContext(contextIntance, context))
						{OCLForJAVA.init("", contextIntance);
						String minEvaluation= OCLForJAVA.evaluateString(contextIntance, minimum, "evaluation");
						String maxEvaluation= OCLForJAVA.evaluateString(contextIntance, maximum, "evaluation");
						res = String.valueOf( FiledGenerator.randomDoubleRange(Double.valueOf(minEvaluation).intValue(), Double.valueOf(maxEvaluation).intValue(),Double.valueOf(precision).intValue()));
						res =String.valueOf( castValue(res,type));
						}
						else
						{  
							if(isTheContext(parentInstance, context))
							{
							OCLForJAVA.init("", parentInstance);
							String minEvaluation= OCLForJAVA.evaluateString(parentInstance, minimum, "evaluation");
							String maxEvaluation= OCLForJAVA.evaluateString(parentInstance, maximum, "evaluation");

								res = String.valueOf( FiledGenerator.randomDoubleRange(Double.valueOf(minEvaluation).intValue(), Double.valueOf(maxEvaluation).intValue(),Double.valueOf(precision).intValue()));
								res =String.valueOf( castValue(res,type));
							}
						else{ System.err.println("Wrong context for steryotype range - Multiplicities - " +cons.getSpecification().toString());
						done = false;
						}
						}
						}	
					}
		
				
						
				if(stereotype.getName().equals("from histogram") && done==false)
				{
					try{
					EList<String> values = (EList<String>)  getValue(cons, stereotype,"bins");
					EList<String>probabilities = (EList<String>) getValue(cons, stereotype,"frequencies");
					
		    		
					double totalprob = Sum(probabilities);
					boolean isOCL = (boolean)getValue(cons, stereotype,"isOCL");
					int precision = (int)getValue(cons, stereotype,"precision");
					if(values.size()!=probabilities.size() )
						System.err.println("Size of values and probabilities is differenet ==>"+cons.getName());
					
					else
					{ 
					   if(totalprob!=1&&totalprob!=100)
						System.err.println("Sum probabilities is not equal to 1==>"+cons.getName());
					else
						done=true;
					}
				
					EObject theObject =null;
					if(isOCL)
					{	Class contextTemp = (Class)getValue(cons, stereotype,"context");		    
						EClass context = getEVersion(contextTemp);
						if(isTheContext(contextIntance, context))
						{OCLForJAVA.init("", contextIntance);
						theObject = contextIntance;
						}
						
						else 
							if(isTheContext(parentInstance, context))
							{	OCLForJAVA.init("", parentInstance);
								theObject = parentInstance;
							}
						else  {System.err.println("Wrong context for steryotype range - contraint - (handelAttributesStereotypes) " +cons.getName()+ " class "+cons.getContext().getName());done = false;}
						
			
						
					}
					
					String value= getfromProba (values,probabilities,isOCL, theObject, precision,totalprob);
					res=value;
					

					}catch(Exception e)
					{e.printStackTrace();}
		
				}
				
				
				if(stereotype.getName().equals("from barchart") && done==false)
				{
					try{
					
					EList<org.eclipse.uml2.uml.internal.impl.EnumerationLiteralImpl > values = (EList<org.eclipse.uml2.uml.internal.impl.EnumerationLiteralImpl>)  getValue(cons, stereotype,"categories");
					
					EEnum enumeration =null;
					boolean same = true;
					if(values.size()==0)
						System.out.println("No literal specified for bar chart steryotype cons: " +cons.getName() +" class "+cons.getContext().getName());
					else 
					{
		
					EEnum base = (EEnum) values.get(0).eContainer();
					int x=1;
					while (x<values.size()&& same == true) {
						if(base.getName().equals(  ((values.get(x).getClassifier())).getName())==false)
							same=false;
							x++;
						
					}
				
					}
					
					if(! same)
						System.out.println("The literals of the steryotype bar chat do not belon to the same enumeration cons: " +cons.getName() +" class "+cons.getContext().getName());
				
					
					if(values.size()>0)
					enumeration = (EEnum) values.get(0).eContainer();
					EList<EEnumLiteral> literals = enumeration.getELiterals();
					
					
					for (int i = 0; i < values.size(); i++) {
						int j= 0;
						boolean find = false;
						String target_name= values.get(i).getName();
						
						while (find==false && j < literals.size()) {
							
							if(literals.get(j).getName().equals(target_name))
								find = true;
							else 
								j++;
							
						}
						
						
						if(j!=i)
						{
						literals.add(i+1,literals.get(j));
						literals.remove(i);
						
						literals.add(j+1,literals.get(i));
						literals.remove(j);
						
						}
						
					}
					
					EList<String>probabilities = (EList<String>) getValue(cons, stereotype,"frequencies");
					double totalprob = Sum(probabilities);
					if(values.size()!=probabilities.size() )
						System.err.println("Size of values and probabilities is differenet ==>"+cons.getName());
					else 
					{ 
					   if(totalprob!=1&&totalprob!=100)
						System.err.println("Sum probabilities is not equal to 1==>"+cons.getName());
					else
						done=true;
					}
					
					EEnumLiteral value= getEnumerationfromProba (literals,probabilities,totalprob);
					
					res= ((EEnum)value.eContainer()).getName()+"#"+value.getName();
					}catch(Exception e)
					{e.printStackTrace();}
		
				}
				
				

				if(stereotype.getName().equals("from distribution") && done==false)
				{
						done=true;
		            	int precision = (int) getValue(cons, stereotype,"precision");
		            	BigDecimal bd = new BigDecimal(FiledGenerator.distribution(cons,stereotype,contextIntance, parentInstance)).setScale(precision, RoundingMode.HALF_EVEN);
						res= String.valueOf(bd.doubleValue());
						
				}
							
			}
			}
			return res;
		}
		
		
		//Cannot be cached
		public String handelAssociationsDependencies(EReference ref, EObject contextIntance,EObject parentInstance)
		{
			String res="Non";
			boolean done=false;
	        EList<Stereotype> relatedSteryotypes = sortSteryotypes(getAllSteryotypesFromModel(ref));
	        Stereotype stereotype=null;
			for (int ixx = 0; ixx < relatedSteryotypes.size() && done==false; ixx++) {
	        	 stereotype=relatedSteryotypes.get(ixx);   	
	        	
	    
	        	
	        	if(stereotype.getName().equals("value dependency"))
	        	{  
	        		Class context = (Class)getValue(ref, stereotype,"context");
	        	
	        	
	        		if(context!=null && !ref.getEType().getName().equals(context.getName()) && ref.getEContainingClass().getName().equals(context.getName()))
	        		{
	        			@SuppressWarnings("unchecked")
						EList<Constraint> constraints=(EList<Constraint>)getValue(ref, stereotype,"OCLtrigger");
	        			for (int i = 0; (i < constraints.size())&&(done==false); i++) {
	        				Constraint constraint = constraints.get(i);
	        				if(isTheContext(contextIntance, getEVersion(context)))
	        				if(evaluateQuery(contextIntance,getEVersion(context),CharMatcher.ASCII.retainFrom(constraint.getSpecification().stringValue())).equals("true"))
	        				{
	        				EList<Constraint> container = new BasicEList<Constraint>();
		        			container.add(constraint);
	   	    				res = handelMultFromConstraints(container,contextIntance,parentInstance,"int");
	        				
	        				done=true;
	        				}
						}
	        			
	        		
	        		}
	        	}
	        }
	    return res;   
		}
		
	 //Used only once
	  public static LinkedList<EClass> getAllChildrenClasses(EClass c)
	  {
		  LinkedList<EClass> res=(LinkedList<EClass>) OclAST.reader.getClassSubtypes(OclAST.allClasses, c);
		 if(res==null)
			 return new LinkedList<EClass>();
		 else 
		 {
			 
			 for (int i = 0; i < res.size(); i++) {
				 LinkedList<EClass> propagation = getAllChildrenClasses(res.get(i));
				 res.addAll(propagation);
			}
			 
			 return res;
		 }
			
		 
	   }
	  
	  //Cached
	  public static LinkedList<EClass> getDirectChildrenClasses(EClass c)
	  {
		  
		  LinkedList<EClass> cut = cache_getDirectChildrenClasses.get(c.toString());
		  if(cut!=null)
			  return cut;
		  
		  LinkedList<EClass> res=(LinkedList<EClass>) OclAST.reader.getClassSubtypes(OclAST.allClasses, c);
		 if(res==null)
		 {   cache_getDirectChildrenClasses.put(c.toString(), new LinkedList<EClass>());
			 return new LinkedList<EClass>();
		 }
		 
		 else 
		 {	cache_getDirectChildrenClasses.put(c.toString(), res);
			 return res;
		 }
	   }
	  
	  //Cannot be cached
	  @SuppressWarnings("unchecked")
	public static EClass getEluFromSeryotypes(EClass c, EList<Stereotype> sters,EReference ref, EObject parent)
	  {
		  EClass res= null;
		  
		  boolean done = false;
			  
		  
		for (int i = 0; i <sters.size(); i++) {
			Stereotype stereotype=sters.get(i);
			
			
			if(done==false && stereotype.getName().equals("type dependency"))
			{
			
			EClass context = getEVersion( (Class)getValue(ref, stereotype,"context"));
			
			EList<Constraint> constraints = (EList<Constraint>)getValue(ref, stereotype,"OCLtrigger");
			
			for (int j = 0; j < constraints.size() && done == false; j++) {

    		String ocl= constraints.get(j).getSpecification().stringValue();
    		if(isTheContext(parent,context))
				if(evaluateQuery(parent,context,ocl).equals("true"))
				{   
					
					EList<Stereotype> consSterys = getAllSteryotypesFromModel(constraints.get(j));
					EList<Class> values = (EList<Class>)  getValue(constraints.get(j), getSteryotypeByName(consSterys, "type dependency spec"),"possibleTypes");
					EList<String>probabilities = (EList<String>) getValue(constraints.get(j), getSteryotypeByName(consSterys, "type dependency spec"),"frequencies");
					double totalprob = Sum(probabilities);
					if(values.size()!=probabilities.size() )
						{System.err.println("Size of values and probabilities is differenet ==>"+ref.toString());}
					else 
					{ 
					   if(totalprob!=1&&totalprob!=100)
						{System.err.println("Sum probabilities is not equal to 1==>"+ref.toString());}
						
					else
					{
						res= getEVersion(getClassfromProba (values,probabilities,totalprob));
				
						if(res!=null)
						done = true;
					}
					}
			
					
				}
		}
		 	
		}}
        	
    				
        	
		  return res;
	  }
	
	  private static boolean isConflicting(EClass c, EReference ref) {
		  if(ref==null||c==null)
			  return false;
		  EList<Stereotype> ref_steryotypes = getAllSteryotypesFromModel(ref);
		  if(!contains(ref_steryotypes, "type dependency"))
			  return false;
		  
		  
		LinkedList<EClass> children = getAllChildrenClasses(c);
		LinkedList<EClass> all = new LinkedList<EClass>();
		all.add(c);
		all.addAll(children);
		for (EClass eClass : all) {
			EList<Stereotype> class_steryotypes = getAllSteryotypesFromModel(eClass);
			 if(contains(class_steryotypes, "probabilistic type"))
				 return true;
				 
		}
		return false;
	}


	//Cannot be Cached
	  public EClass getElu(EClass c, boolean needAdjustement)
	  {
		  
		EClass res=null;
		LinkedList<EClass> children = getDirectChildrenClasses(c);
		if(c!=null)
		if(c.isAbstract()==false&&c.isInterface()==false)
		res=c;
		EClass elu=null;

		if(children.size()==0)
		{

		return c;
		}
			elu=chooseEClassFromProba(c,children);

		if(elu!=null)
			if(elu!=c)
			res=getElu(elu,needAdjustement);
		
	
		return res;
	  }
	  
	private static void updateClassProba(EClass c, EClass res) {
	
		  LinkedList<EClass> values= new LinkedList<EClass>();
		  LinkedList<Double> probabilities = new LinkedList<Double>();
		  LinkedList<EClass> children = getDirectChildrenClasses(c);
	
		  
		  for (EClass child : children) {
			  EList<Stereotype> ster = getAllSteryotypesFromModel(child);
			  Stereotype childStery=getSteryotypeByName(ster, "probabilistic type");
			  if(childStery!=null)
			  {
				  values.add(child);
				  probabilities.add(Double.valueOf(String.valueOf(getValue(child, childStery, "frequency")).replace(",", ".")));
			  }
			
		}
		  
		  
		  if(c!=null)
		  {
			  
			 if(c.isAbstract()==false && c.isInterface()==false)
			 {
			  double childrenProba=Sum(probabilities);
			  if(childrenProba<1)
			  {
			  values.add(c);
			  probabilities.add(1.0-childrenProba);
			  }
			 }
		  }
		  
		  
		  
		  
		  if(values.size()==0)
		  {
			  int nb_Non_abstract=0;
			  for (EClass child : children) 
				  if(child.isAbstract()==false&&child.isInterface()==false)
				  {
				  nb_Non_abstract++;
				  values.add(child);
				  }
			  if(c!=null)
			  { if(c.isAbstract()==false && c.isInterface()==false)
			  {
				  values.add(c); nb_Non_abstract++;
			  }
			  }
	
			  for (int i = 0; i < nb_Non_abstract; i++) {
				probabilities.add(1.0/nb_Non_abstract);
			}
			  
		  }
		  
		  
		  if(values.size()==0)
			  return ;
			else
			{

			if(values.size()!=probabilities.size() )
			{
				System.err.println("Size of values and probabilities is differenet (chooseEClassFromProba) (values="+values.size()+",probabilities"+probabilities.size()+")==>"+values);
			    return;
			}
			else if(Sum(probabilities)>1)
			{

				double portion= 1.0/probabilities.size();
				for (int i = 0; i < probabilities.size(); i++) {
					probabilities.set(i,portion);
				}
			}
			}
		  //TODO hack
// for (EClass eClass : values) {
			 // if(eClass.getName().equals("Tax_Payer"))
			//	return;
			
//		}
		  //TODO update histogram
		  
		    Integer[] actual = new Integer[values.size()];
		    EList<String>TempProbabilities =new BasicEList<String>();
		    for (int i = 0; i < actual.length; i++) {
		    	Integer value = actualStatusProbForClasses.get(getCorrespondant(values.get(i),realClasses));
				actual[i]=value;
				
				TempProbabilities.add(String.valueOf(probabilities.get(i)));
			}
	
	    	Double [] actualFrequencies = getActualFrequencies(actual);
	    	EList<String> desiredFrequencies = new BasicEList<String>();
	    	for (EClass classe : values) {
				String tempV = desiredProbaForClasses.get((Class)getCorrespondant(classe, realClasses));
				if(tempV==null)
				tempV="-1";
				desiredFrequencies.add(tempV);	
				} 
	    	double total = 0;
	    	for (String string : desiredFrequencies) {
				if(!string.equals("-1"))
					total = Double.valueOf(string).doubleValue();
			}
	    	for (int i = 0; i < desiredFrequencies.size(); i++) {
			String string = desiredFrequencies.get(i);	
	    		if(string.equals("-1"))
	    			desiredFrequencies.set(i, String.valueOf(1>total?1-total:0));
			}
	    	Double [] balance = new Double [actualFrequencies.length];
	    	for (int i = 0; i < balance.length; i++) {
				balance[i]=Double.valueOf(desiredFrequencies.get(i)).doubleValue()-actualFrequencies[i];
				if(balance[i]<0)
				balance[i]=0.0;
			}
	    	
	    	
	    	 EList<String> newProba = new BasicEList<String>();
	    	 normelize(balance,desiredFrequencies);
	    	 for (int i = 0; i < balance.length; i++) {
				newProba.add(String.valueOf(balance[i]));
			}
	    	 normelize2(newProba,desiredFrequencies);
	    
	    	 for (int i = 0; i < values.size(); i++) {
				EClass classe = children.get(i);
				EList<Stereotype> steryotypes = getAllSteryotypesFromModel(classe);
		    	 if(contains(steryotypes, "probabilistic type"))
		    	getCorrespondant(classe,realClasses).setValue(getSteryotypeByName(steryotypes, "probabilistic type"), "frequency", newProba.get(i));
			}
	    	
	    
	    	
		}
		
	




	//Cannot be Cached
	  public static EClass chooseEClassFromProba( EClass c,LinkedList<EClass> children)
	  {  
		  
		  

		  LinkedList<EClass> values= new LinkedList<EClass>();
		  LinkedList<Double> probabilities = new LinkedList<Double>();

	
		  
		  for (EClass child : children) {
			  EList<Stereotype> ster = getAllSteryotypesFromModel(child);
			  Stereotype childStery=getSteryotypeByName(ster, "probabilistic type");
			  if(childStery!=null)
			  {
				  values.add(child);
				  probabilities.add(Double.valueOf(String.valueOf(getValue(child, childStery, "frequency")).replace(",", ".")));
			  }
			
		}
		  
		  
		  if(c!=null)
		  {
			  
			 if(c.isAbstract()==false && c.isInterface()==false)
			 {
			  double childrenProba=Sum(probabilities);
			  if(childrenProba<1)
			  {
			  values.add(c);
			  probabilities.add(1.0-childrenProba);
			  }
			 }
		  }
		  
		  
		  
		  
		  if(values.size()==0)
		  {
			  int nb_Non_abstract=0;
			  for (EClass child : children) 
				  if(child.isAbstract()==false&&child.isInterface()==false)
				  {
				  nb_Non_abstract++;
				  values.add(child);
				  }
			  if(c!=null)
			  { if(c.isAbstract()==false && c.isInterface()==false)
			  {
				  values.add(c); nb_Non_abstract++;
			  }
			  }
	
			  for (int i = 0; i < nb_Non_abstract; i++) {
				probabilities.add(1.0/nb_Non_abstract);
			}
			  
		  }
		  
		  
		  if(values.size()==0)
			  return null;
			else
			{

			if(values.size()!=probabilities.size() )
			{
				System.err.println("Size of values and probabilities is differenet (chooseEClassFromProba) (values="+values.size()+",probabilities"+probabilities.size()+")==>"+values);
			    return null;
			}
			else if(Sum(probabilities)>1)
			{

				double portion= 1.0/probabilities.size();
				for (int i = 0; i < probabilities.size(); i++) {
					probabilities.set(i,portion);
				}
			}

			double p =  new JDKRandomGenerator().nextDouble();
			double cumulativeProbability = 0.0;
			for (int i = 0; i < probabilities.size(); i++) {
			    cumulativeProbability=  cumulativeProbability+ Double.valueOf(String.valueOf(probabilities.get(i)).replace(",", "."));
			    if (p <= cumulativeProbability && Double.valueOf(String.valueOf(probabilities.get(i)).replace(",", ".")) != 0) {
			        return values.get(i);
			    }
			}
			
			return null;
			}
		  
		  
	  }
	  

	//Cached
	@SuppressWarnings("unchecked")
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
    		else  if(obj instanceof EEnum)
    		    correspondant = getCorrespondant (obj,realEnumerations);
    			 if(obj instanceof EEnumLiteral)
    			  correspondant = getCorrespondantForLiterals ((EEnumLiteral) obj);
    			 
    			 if(obj instanceof EEnum ==false && obj instanceof EEnumLiteral==false)
    			 correspendance.put(obj.toString(), correspondant);
		}
    	res= correspondant.getValue(stereotype, propertyName);
    	
    	if (res instanceof String)
    		res= CharMatcher.ASCII.retainFrom(String.valueOf(res));
    	if(res instanceof EList<?>)
    	{
    		if(((EList<?>)res).size()>0)
    		{
    			if (((EList<?>)res).get(0) instanceof String)
    				for (int i = 0; i < ((EList<?>)res).size(); i++) {
    					((EList<String>)res).set(i, CharMatcher.ASCII.retainFrom(String.valueOf(((EList<?>)res).get(i))));
					}
    		}
    	}
    		
    		
    		
		return res;
	
	}
	
	//Cached
	public static void setValue(EObject obj, Stereotype stereotype, String propertyName,String newValue)
	{

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
    		else if(obj instanceof EEnum)
    		    correspondant = getCorrespondant (obj,realEnumerations);
    			 if(obj instanceof EEnumLiteral)
    			  correspondant = getCorrespondantForLiterals ((EEnumLiteral) obj);
    			 
    			 if(obj instanceof EEnum ==false && obj instanceof EEnumLiteral==false)
    			 correspendance.put(obj.toString(), correspondant);
		}
    	correspondant.setValue(stereotype, propertyName, newValue);
    	
	
	}


	//it is better to not chache this
	public static EList<Stereotype> getAllSteryotypesFromModel(EObject obj)
    {
    	EList<Stereotype> res= new BasicEList<Stereotype>();
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
    			if(obj instanceof EEnum)
    		    correspondant = getCorrespondant (obj,realEnumerations);
    			 if(obj instanceof EEnumLiteral)
    			  correspondant = getCorrespondantForLiterals ((EEnumLiteral) obj);
    			 
    			 if(obj instanceof EEnum ==false && obj instanceof EEnumLiteral==false)
    			 correspendance.put(obj.toString(), correspondant);
    			
    	}

    	if(correspondant == null) return res;
     	res=correspondant.getAppliedStereotypes();
    	return res;
    	
    
    }
    
    //Cached
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
				   {
					  return element;
				   }
			}
    	}
     	
    	
    	
    	if(obj instanceof EAttribute)
    	{
    		for (org.eclipse.uml2.uml.Element element : liste) {
    		  if(((org.eclipse.uml2.uml.Property)element).getAssociation()==null)
			    if( ((org.eclipse.uml2.uml.Property)element).getName().trim().equals(((EAttribute) obj).getName()))
				  //if( ((org.eclipse.uml2.uml.Property)element).getType().getName().trim().equals(((EAttribute) obj).getEType().getName()))
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
    
    
    public static org.eclipse.uml2.uml.Element getCorrespondantForLiterals(EEnumLiteral obj)
    { 
    	org.eclipse.uml2.uml.Element res=null;
    	

    		EEnum container = (EEnum) obj.eContainer();
    		org.eclipse.uml2.uml.Enumeration realContainer= (Enumeration) getCorrespondant(container, realEnumerations);
    		for (org.eclipse.uml2.uml.Element element : realContainer.getOwnedLiterals())
    			if( ((org.eclipse.uml2.uml.EnumerationLiteral)element).getName().trim().equals(((EEnumLiteral) obj).getName()))
    			   {
					  return element;
				   }
    			
    	
    	return res;
    }
    
    
    
    //Executed only once
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
	
	  //Not used
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
	  
	  
	  //Cannot be cached
	  public static boolean isTheContext(EObject object, EClass c)
	  {
		  String className= c.getName();
		  String objectClassName= object.eClass().getName();
		  if(className.equals(objectClassName))return true;
		  else
		  {EList<EClass> x = getAllSuperTypes(object.eClass());
		  
		  
		  for (EClass eClass : x) {
			if(eClass.getName().equals(className))
				return true;
		}
		  return false;
		  }
	  }

	  public static EList<EClass> getAllSuperTypes(EClass c)
	  {
		  EList<EClass> res = new BasicEList<EClass>();
		     res = c.getESuperTypes();
		     
		     for (int i = 0; i <res.size(); i++) {
				EClass current = res.get(i);
				 EList<EClass> potential = current.getESuperTypes();
				 for (EClass eClass : potential) {
					if(res.contains(eClass)==false)
						res.add(eClass);
				}
		    	 
			}
		  
		  
		  return res;
		  
	  }
	  
	  
	  
	  //Is quick
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
	  
	  
	  //Cannot be cached
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
	
	//Cached
	public LinkedList<EAttribute> getReleventAttributes(EClass eClass, LinkedList<EObject> all2)
	{
		LinkedList<EAttribute> cut=cache_getReleventAttributes.get(eClass.toString());
		if(cut!=null)
		return cut;
		
		LinkedList<EAttribute> res =new LinkedList<EAttribute>();
		for (int i = 0; i <all2.size(); i++) {
			if(all2.get(i) instanceof EAttribute)
			{
				
				if(isAttributeNeeded((EAttribute) all2.get(i),eClass))
					if(res.contains((EAttribute) all2.get(i))==false)
					res.add((EAttribute) all2.get(i));
			}
				
		}
		
		resolveEAttributesTesnion(res);
		cache_getReleventAttributes.put(eClass.toString(), res);
		return res;
	}
	
	//Cached
	public LinkedList<EReference>getRefrences(EClass c)
	{   LinkedList<EReference> res= new LinkedList<EReference>();
		LinkedList<EReference> cut = cache_getRefrences.get(c.toString());
		if(cut!=null)
		{ res.addAll(cut);
		  return res;
		}
		for (int i = 0; i < listeEReferences.size(); i++) {

				EReference ref= listeEReferences.get(i);
				if(isReferenceReleventTOClass(c,ref.eContainer()))
					if(res.contains(ref)==false)
					res.add(ref);

		}
		resolveEReferencesTesnion(res);
		cache_getRefrences.put(c.toString(), res);
		return res;
	}
	

	
	//The method using this is cached
	public static boolean isReferenceReleventTOClass(EClass c,EObject container)
	{
		if(!(container instanceof EClass))
			return false;
		else
		{ EClass  classe= (EClass) container;
			if(c.getName().equals(classe.getName()))
				return true;
			else 
			{
				for (EClass c1 : c.getEAllSuperTypes()) {
					 if(c1.getName().equals(classe.getName()))	
						 return true;
				}
				
				return false;
			}
		}
	}
	
	//it is better to not cache this
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
	
	//it is better to not cache this
	public static boolean contains( EList<Stereotype> relatedSteryotypes, String name)
	{
		boolean res=false;
		if(relatedSteryotypes==null)
			return false;
		int i =0;
		while (i<relatedSteryotypes.size()&&res==false) {
			if(relatedSteryotypes.get(i).getName().trim().equals(name.trim()))
				res=true;
			else i++;
		}
		return res;
	}
	
	public boolean literalHasTypeSteryotype(EEnum enumuration)
	{
		boolean res=false;
		EList<EEnumLiteral> literals = enumuration.getELiterals();
		int i=0;
		while (i<literals.size()&&res==false) {
			EList<Stereotype> relatedSteryotypes = getAllSteryotypesFromModel(literals.get(i));
			if(contains(relatedSteryotypes, "probabilistic type"))
				return true;
			else
			i++;
			
		}
		
		return res;
	}
	
	//cannot cache this
	public void createRandomAttribute(EAttribute att, EObject contextIntance,EObject parentInstance)
	{
		 String stringType = att.getEType().getName();
         Object value = null;
  
         if(att.getEType() instanceof EEnum)
         { EEnum enumuration = (EEnum) att.getEType();
           int indexLit = -1;
           
           if(literalHasTypeSteryotype(enumuration)==false)
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
		else if (stringType.toLowerCase().contains("date"))
				value = FiledGenerator.generateRandomDate(1920,2014);
		else value = FiledGenerator.randomIntegerRange(0,Integer.MAX_VALUE);
        
         
         updateAttribute(att, contextIntance, parentInstance, value);
		
	}
	
	//cannot Cache this

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
    			return new Date (value);
    	else if(att.getEType() instanceof EEnum)
    		{ 
    		if(!value.contains("#"))
    			value = att.getEType().getName() +"#"+value;
    		String tab [] = value.split("#");
    		String enumName = tab[0];
    		String LiteralName = tab[1];

    		EEnum enumeration = getEnumerationByName(enumName);
    		EEnumLiteral res = enumeration.getEEnumLiteralByLiteral(LiteralName);
    		return res;

  		}
    	
		else return null;
         
	}
	
	
	public static Object castValue(String value , String stringType)
	{

     	if (stringType.equals("EString"))
			return value;
     	else 
     		if (stringType.contains("EChar"))
     			return value.charAt(0);
    	if (stringType.contains("Char"))
 			return value.charAt(0);
		else if (stringType.contains("EBoolean"))
				return Boolean.valueOf(value);
		else if (stringType.contains("Boolean"))
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
		else  if (stringType.contains("Double"))
			return Double.valueOf(value);
		else  if (stringType.contains("Real"))
			return Double.valueOf(value);
		else if (stringType.contains("EInt"))
			return Double.valueOf(value).intValue();
		else if (stringType.contains("int"))
			return Double.valueOf(value).intValue();
    	else if (stringType.contains("integer"))
			return Double.valueOf(value).intValue();
		else return null;
         
	}
	
	public static EEnum getEnumerationByName(String name)
	{
		EEnum res = null;
		EList<EClassifier> all2 = pck.getEClassifiers();
		for (EClassifier eObject : all2) {
			
			if(eObject instanceof EEnum)
			if( ((EEnum)eObject).getName().equals(name))
				return (EEnum)eObject;
		}
		return res;
	}
	
	//cannot Cache this
	public String generateValue(EAttribute att,String minimum, String maximum)
	{ 
		String res="null";
		if(minimum.equals("invalid")||maximum.equals("invalid"))
			System.out.println();
		if(Double.valueOf(minimum).doubleValue()>Double.valueOf(maximum).doubleValue())
			minimum = maximum;
		if(att.getEType().getName().contains("EDouble")||att.getEType().getName().contains("EBigDecimal"))
		return String.valueOf(FiledGenerator.randomDoubleRange(Double.valueOf(minimum), Double.valueOf(maximum), 2));
		if(att.getEType().getName().contains("EInt")||att.getEType().getName().contains("ELong")||att.getEType().getName().contains("EBigInteger")||att.getEType().getName().contains("Integer"))
		return String.valueOf(FiledGenerator.randomIntegerRange(Double.valueOf(minimum).intValue(), Double.valueOf(maximum).intValue()));
		return res;
	}
	
	//cannot Cache this
	public static String treatValues (String value, boolean isOCL, EObject theObject, int precision)
	{

		
		String res="";
		if( (value.trim().startsWith("[")&&value.trim().endsWith("]"))==false)
		{   if(isOCL==false)
			return value;
		else return OCLForJAVA.evaluateString(theObject, value, "");
		}
		
		
		
		else
		{
			if(value.trim().contains(".."))
			{
				value=value.trim().replace("[", "").replace("]", "").replace("..", "-");
				
				String [] temp=value.split("-");
				double start=-1;
				double end=-1;
				if(isOCL==false)
				{
				 start= Double.valueOf(temp[0].trim());
				 end= Double.valueOf(temp[1].trim());
				}
				else
				{
					 start= Double.valueOf(OCLForJAVA.evaluateString(theObject, temp[0].trim(), ""));
					 end= Double.valueOf(OCLForJAVA.evaluateString(theObject, temp[1].trim(), ""));
				}
				
			return String.valueOf(FiledGenerator.randomDoubleRange(start, end, precision));

				 
			}
			
			if(value.trim().contains("-"))
			{
				value=value.trim().replace("[", "").replace("]", "");
				
				String [] temp=value.split("-");
				double start=-1;
				double end=-1;
				if(isOCL==false)
				{
				 start= Double.valueOf(temp[0].trim());
				 end= Double.valueOf(temp[1].trim());
				}
				else
				{
					 start= Double.valueOf(OCLForJAVA.evaluateString(theObject, temp[0].trim(), ""));
					 end= Double.valueOf(OCLForJAVA.evaluateString(theObject, temp[1].trim(), ""));
				}
				
				return String.valueOf(FiledGenerator.randomDoubleRange(start, end, precision));
			}
			
			if(value.trim().contains(","))
			{
				String [] temp=value.trim().replace("[", "").replace("]", "").split(",");
				int index=FiledGenerator.randomIntegerRange(0, temp.length-1);
				if(isOCL==false)
				return temp[index].trim();
				else return OCLForJAVA.evaluateString(theObject, temp[index].trim(), "");
			}
		}

		
		return res;
	}
	
	//cannot Cache this
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
				if(stereotype.getName().equals("probabilistic type"))
				res.add(new Double(	String.valueOf(getValue(values.get(i), stereotype,"frequency"))));
					
			}
		}
	
	return res;	
	}
		
	}
	
	//cannot Cache this
	public static EEnumLiteral getLiteralFromProba(EList<EEnumLiteral> values, EList<?> probabilities)
	{ 

	if(values.size()==0)
		return null;
	else
	{

	if(values.size()!=probabilities.size() )
	{
		System.err.println("Size of values and probabilities is differenet (getLiteralFromProba) (values="+values.size()+",probabilities"+probabilities.size()+")==>"+values);
	    return null;
	}
	else

		{ double totalprob = Sum(probabilities);
		   if(totalprob!=1&&totalprob!=100)
	{ System.out.println("lool");
		System.err.println("Sum probabilities is not equal to 1 (getLiteralFromProba) ==>"+values);
		return null;
	}
		}
	double p =  new JDKRandomGenerator().nextDouble();
	double cumulativeProbability = 0.0;
	for (int i = 0; i < probabilities.size(); i++) {
	    cumulativeProbability=  cumulativeProbability+ Double.valueOf(String.valueOf(probabilities.get(i)).replace(",", "."));
	    if (p <= cumulativeProbability && Double.valueOf(String.valueOf(probabilities.get(i)).replace(",", ".")) != 0) {
	        return values.get(i);
	    }
	}
	
	
	}
	
	return null;
	}
	
	//cannot Cache this
	public EEnumLiteral getEnumerationfromProba(EList<EEnumLiteral> values, EList<String> probabilities,double totalProba)
	{   
		
		if(totalProba==100)
		{
			for (int i = 0; i < probabilities.size(); i++) {
				probabilities.set(i, String.valueOf(Double.valueOf(String.valueOf(probabilities.get(i).replace(",", ".")))/100));
			}
		}
	
		double p =  new JDKRandomGenerator().nextDouble();
		double cumulativeProbability = 0.0;
		for (int i = 0; i < probabilities.size(); i++) {
		    cumulativeProbability=  cumulativeProbability+ Double.valueOf(String.valueOf(probabilities.get(i)).replace(",", "."));
		    if (p <= cumulativeProbability && Double.valueOf(String.valueOf(probabilities.get(i)).replace(",", ".")) != 0) {
		        return values.get(i);
		    }
		}
		
		return null;

	}
	
	//cannot Cache this
	public static String getfromProba(EList<String> values, EList<String> probabilities, boolean isOCL, EObject theObject, int precision,double totalProba)
	{   
		if(totalProba==100)
		{
			for (int i = 0; i < probabilities.size(); i++) {
				probabilities.set(i, String.valueOf(Double.valueOf(String.valueOf(probabilities.get(i).replace(",", ".")))/100));
			}
		}
	
		double p =  new JDKRandomGenerator().nextDouble();
		double cumulativeProbability = 0.0;
		for (int i = 0; i < probabilities.size(); i++) {
		    cumulativeProbability=  cumulativeProbability+ Double.valueOf(String.valueOf(probabilities.get(i)).replace(",", "."));
		    if (p <= cumulativeProbability && Double.valueOf(String.valueOf(probabilities.get(i).replace(",", "."))) != 0) {
		        return treatValues(values.get(i),isOCL,theObject,precision);
		    }
		}
		
		return treatValues(values.get(values.size()-1),isOCL,theObject,precision);

	}
	
	public static String getfromProbaAttribute(EList<String> values, EList<String> probabilities, boolean isOCL, EObject theObject, int precision,double totalProba, EAttribute att)
	{   
		if(totalProba==100)
		{
			for (int i = 0; i < probabilities.size(); i++) {
				probabilities.set(i, String.valueOf(Double.valueOf(String.valueOf(probabilities.get(i).replace(",", ".")))/100));
			}
		}
	
		double p =  new JDKRandomGenerator().nextDouble();
		double cumulativeProbability = 0.0;
		for (int i = 0; i < probabilities.size(); i++) {
		    cumulativeProbability=  cumulativeProbability+ Double.valueOf(String.valueOf(probabilities.get(i)).replace(",", "."));
		    if (p <= cumulativeProbability && Double.valueOf(String.valueOf(probabilities.get(i).replace(",", "."))) != 0) {
		   
		    	Integer[] actual = actualStatusHistogramsForAttributes.get(att);
		    	if(actual == null)
		    	{
		    		actual = new Integer[probabilities.size()];
		    		for (int j = 0; j < actual.length; j++) {
						actual[j]=0;
					}
		    		actualStatusHistogramsForAttributes.put(att, actual);
		    		
		    	}
		    	actual[i] = actual[i]+1;
		    	
		        return treatValues(values.get(i),isOCL,theObject,precision);
		    }
		}
		
		Integer[] actual = actualStatusHistogramsForAttributes.get(att);
    	if(actual == null)
    	{
    		actual = new Integer[probabilities.size()];
    		for (int i = 0; i < actual.length; i++) {
				actual[i]=0;
			}
    		actualStatusHistogramsForAttributes.put(att, actual);
    	}
    	actual[values.size()-1] = actual[values.size()-1]+1;
		return treatValues(values.get(values.size()-1),isOCL,theObject,precision);

	}
	
	//cannot Cache this
	public static Class getClassfromProba(EList<Class> values, EList<String> probabilities, double totalProba)
	{   
		
		if(totalProba==100)
		{
			for (int i = 0; i < probabilities.size(); i++) {
				probabilities.set(i, String.valueOf(Double.valueOf(String.valueOf(probabilities.get(i).replace(",", ".")))/100));
			}
		}
	
		double p =  new JDKRandomGenerator().nextDouble();
		double cumulativeProbability = 0.0;
		for (int i = 0; i < probabilities.size(); i++) {
		    cumulativeProbability=  cumulativeProbability+ Double.valueOf(String.valueOf(probabilities.get(i).replace(",", ".")));
		    if (p <= cumulativeProbability && Double.valueOf(String.valueOf(probabilities.get(i).replace(",", "."))) != 0) {
		        return values.get(i);
		    }
		}
		
		return null;

	}
	
	//cannot Cache this
	public static double Sum(EList<?> probabilities)
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
	
	//cannot Cache this
	public static double Sum(LinkedList<?> probabilities)
	{
		double res=0;
		for (int i = 0; i < probabilities.size(); i++) {
			res=res+ Double.valueOf(String.valueOf(probabilities.get(i)).replace(",", ".")).doubleValue();
		}
		
		if(Math.ceil(res)==1||Math.floor(res)==1||Math.round(res)==1)
			return 1.0;
		if(Math.ceil(res)==100||Math.floor(res)==100||Math.round(res)==100)
			return 100.0;
		return res;
	}
	
	public void updateAttribute(EAttribute att, EObject contextIntance ,EObject parentInstance,Object value)
	{
		 if(att.getUpperBound()==1)
		        contextIntance.eSet(att, value);
		        else {
		        	LinkedList<Object>container=new LinkedList<Object>();
		        	List temp = (List)contextIntance.eGet(att);
		        	if(temp!=null)
		        	container.addAll(temp);
		        	container.add(value);
		        	removeDuplicatObject(container);
		        	System.out.println(att);
		        	contextIntance.eSet(att, container);
		        	int required =1;
		        	
		        	if(container.size()==1)
		        	{
		        	  required = att.getUpperBound();
		        	     if(required==-1)
		        	      required=1;
		        		EList<Stereotype> ref_steryotypes = getAllSteryotypesFromModel(att);
		        		if(contains(ref_steryotypes, "multiplicity"))
		        		{EList<Constraint> cons=getConstraintsFromSteryotyped(att, "multiplicity"); 
		    			     if(cons!=null)
			    			 if(cons.size()>0)
			    				required = Double.valueOf(handelMultFromConstraints(cons,contextIntance,parentInstance!=null?parentInstance:contextIntance,"int")).intValue();
			    		}
		        	}	
		        		if(container.size()<required)
		        		{
		        			
		        			for (int i = 0; i < required-1; i++) {
								
							
		        			boolean done=false;
			          	    done= handelAttributesSteryotyps(att,contextIntance,parentInstance!=null?parentInstance:contextIntance);
			          		if(done==false)
			          			createRandomAttribute(att,contextIntance,parentInstance!=null?parentInstance:contextIntance);
		        			}
		        		}
		        	
		        }
	}
	
	public boolean isPremitive(EAttribute att)
	{ 	
	String stringType = att.getEType().getName();
 	if (stringType.equals("EString"))
		return true;
 	else 
 		if (stringType.contains("EChar"))
 			return true;
	if (stringType.contains("Char"))
			return true;
	else if (stringType.contains("EBoolean"))
			return true;
	else if (stringType.contains("Boolean"))
		return true;
	else if (stringType.contains("EByte"))
		   return true;
	else if (stringType.equals("EBigInteger"))
			return true;
	else if (stringType.equals("EBigDecimal"))
			return true;
	else if (stringType.contains("ELong"))
		return true;
	else  if (stringType.contains("EDouble"))
		return true;
	else  if (stringType.contains("Double"))
		return true;
	else  if (stringType.contains("Real"))
		return true;
	else if (stringType.contains("EInt"))
		return true;
	else if (stringType.contains("int"))
		return true;
	else if (stringType.contains("Integer"))
		return true;
		else  if(stringType.toLowerCase().contains("date"))
			return true;
	else if(att.getEType() instanceof EEnum)
		return true ;

		
	
		return false;
	}
	
	//cannot Cache this
	public static EList<Stereotype> sortSteryotypes(EList<Stereotype> relatedSteryotypes)
	{  EList<Stereotype> res= new BasicEList<Stereotype>();
		LinkedList<String>  importantNames=new LinkedList<String>();
		if (relatedSteryotypes==null)
			relatedSteryotypes = new BasicEList<Stereotype>();
			importantNames.add("use existing");
		    importantNames.add("value dependency");
		    importantNames.add("type dependency");
		for (int i = 0; i < relatedSteryotypes.size(); i++) {
			if(importantNames.contains(relatedSteryotypes.get(i).getName()))
			res.add(0,relatedSteryotypes.get(i));
			else
			res.add(relatedSteryotypes.get(i));
			
		}
		return res;
	}
	

	//cannot Cache this
	@SuppressWarnings("unchecked")
	public boolean handelAttributesSteryotyps(EAttribute att, EObject contextIntance,EObject parentInstance)
	{ 
		boolean res=false;
        EList<Stereotype> relatedSteryotypes = sortSteryotypes(getAllSteryotypesFromModel(att));
        Stereotype stereotype=null;
		for (int ixx = 0; ixx < relatedSteryotypes.size() && res==false; ixx++) {
        	 stereotype=relatedSteryotypes.get(ixx);   	
        	
        	


			if(stereotype.getName().equals("use existing"))
			{
				boolean toBeExecuted=true;
	        	try{
	        		double execution_proba= Double.valueOf((Double)getValue(att, stereotype,"reuseProbability")).doubleValue();
	        		UniformRealDistribution dis= new UniformRealDistribution(0,1);
	        		double chance = dis.sample();
	        		toBeExecuted = execution_proba >=chance;
	        	}
	        	catch(Exception ex){ex.printStackTrace();}
	        	
	        	if(toBeExecuted)
	        	{
	            	res=true;
	            	EObject selected=null;
	        		Class context = (Class)getValue(att, stereotype,"context");
	      
	        		if(isTheContext(parentInstance, getEVersion(context)))
	        		{
		         
	    					OCLForJAVA.init("", parentInstance);
	    	         		Constraint c = 	(Constraint)getValue(att, stereotype,"eligibleCondidatesQuery");
	    	         		String toBeEvaluated=c.getSpecification().stringValue();	
	    	         		
	    					Collection<EObject> selectable = OCLForJAVA.evaluateECollection(parentInstance, toBeEvaluated, "temp", "", "Set");
	    					if(selectable!=null)
	    					if(selectable.size()>0)
	    					{
	    						int index = FiledGenerator.randomIntegerRange(0, selectable.size()-1);
	    						selected=(EObject)selectable.toArray()[index];
	    					}
	    					
	    				
	        		}
	        		
	        		//contextIntance.eSet(att, selected);
	        		updateAttribute(att, contextIntance, parentInstance, selected);
	        		
	        	}	
			}
			
			
        	
        	
        	if(stereotype.getName().equals("value dependency") && res==false)
        	{  
        		Class context = (Class)getValue(att, stereotype,"context");
        		
        		Stereotype conflictingHistogram = isConflicting(att);
        		boolean isConflicting = conflictingHistogram!=null;
        		String value="";
        		Object OldValue = null;
        		if(context!=null)
        		{
        			EList<Constraint> constraints=(EList<Constraint>)getValue(att, stereotype,"OCLtrigger");
        			for (int i = 0; (i < constraints.size())&&(res==false); i++) {
        				Constraint constraint = constraints.get(i);
        				if(isTheContext(parentInstance,getEVersion(context)))
        				{
        				if(evaluateQuery(parentInstance,getEVersion(context),CharMatcher.ASCII.retainFrom(constraint.getSpecification().stringValue())).equals("true"))
        				{   
        					EList<Constraint> container = new BasicEList<Constraint>();
	        				container.add(constraint);
	        				
	        				
        			    value=handelMultFromConstraints(container,contextIntance,parentInstance,att.getEType().toString());
        
        				if(!value.trim().equals(""))
        				{
        				res=true;
        				
        				OldValue=contextIntance.eGet(att);
        				updateAttribute(att, contextIntance, parentInstance, castValue(att,value));
        				}
        				}
        				}
        				else
        				{
        					if(isTheContext(contextIntance,getEVersion(context)))
                				if(evaluateQuery(contextIntance,getEVersion(context),CharMatcher.ASCII.retainFrom(constraint.getSpecification().stringValue())).equals("true"))
                				{
                					EList<Constraint> container = new BasicEList<Constraint>();
        	        				container.add(constraint);
                				value=handelMultFromConstraints(container,contextIntance,parentInstance,att.getEType().toString());
                				if(!value.trim().equals(""))
                				{
                				res=true;
                				OldValue=contextIntance.eGet(att);
                				updateAttribute(att, contextIntance, parentInstance, castValue(att,value));
                				}
                				}
        					
        				}
        				
        				
        				if(res&&isConflicting)
        				{   
        					updateHistogram(conflictingHistogram,castValue(att,value),att,OldValue,contextIntance,true);
        				}
					}
        			
        			
        		
        		}
        	}
        	
         
    
    		
    		
        	
			if(stereotype.getName().equals("fixed value")&& res==false)
			{
			    
				String value = String.valueOf( getValue(att, stereotype,"value"));
				boolean isOCL = (boolean)getValue(att, stereotype,"isOCL");
				res=true;
				if(isOCL==false)
				{//contextIntance.eSet(att, castValue(att,value));
				updateAttribute(att, contextIntance, parentInstance, castValue(att,value));
				
				}
				else 
				{
					
					Class contextTemp = (Class)getValue(att, stereotype,"context");
					EClass context = getEVersion(contextTemp);
					if(isTheContext(parentInstance, context))
					{
					OCLForJAVA.init("", parentInstance);
					String evaluation= OCLForJAVA.evaluateString(parentInstance, value, "evaluation");
				
					//contextIntance.eSet(att, castValue(att,evaluation));
					updateAttribute(att, contextIntance, parentInstance, castValue(att,evaluation));
					}
					else
					{   if(isTheContext(contextIntance, context))
						{
						OCLForJAVA.init("", contextIntance);
						String evaluation= OCLForJAVA.evaluateString(contextIntance, value, "evaluation");
						//contextIntance.eSet(att, castValue(att,evaluation));	
						updateAttribute(att, contextIntance, parentInstance, castValue(att,evaluation));
						}
					else {System.err.println("Wrong context for steryotype fixed - attribute - (handelAttributesStereotypes) att: " +att.getName() +" class "+att.getEContainingClass().getName());
					res = false;
					}
					}
					}		
				
			}
			
			if(stereotype.getName().equals("from uniform range") && res==false)
			{
				boolean isOCL = (boolean)getValue(att, stereotype,"isOCL");
				String maximum = String.valueOf( getValue(att, stereotype,"upperbound"));
				String minimum = String.valueOf( getValue(att, stereotype,"lowerbound"));
				res=true;
				
				if(isOCL==false)
				{
				String value= generateValue (att,minimum,maximum);
				//contextIntance.eSet(att, castValue(att,value));
				updateAttribute(att, contextIntance, parentInstance, castValue(att,value));
				}
				else
				{
					

					Class contextTemp = (Class)getValue(att, stereotype,"context");
					EClass context = getEVersion(contextTemp);
					
					 if(isTheContext(contextIntance, context))
						{
						OCLForJAVA.init("", contextIntance);
						String minEvaluation= OCLForJAVA.evaluateString(contextIntance, minimum, "evaluation");
						String maxEvaluation= OCLForJAVA.evaluateString(contextIntance, maximum, "evaluation");
						
						//contextIntance.eSet(att, castValue(att,generateValue (att,minEvaluation,maxEvaluation)));
						updateAttribute(att, contextIntance, parentInstance, castValue(att,generateValue (att,minEvaluation,maxEvaluation)));
						}
					 else 
					 {
					if(isTheContext(parentInstance, context))
					{
					OCLForJAVA.init("", parentInstance);
					String minEvaluation= OCLForJAVA.evaluateString(parentInstance, minimum, "evaluation");
					String maxEvaluation= OCLForJAVA.evaluateString(parentInstance, maximum, "evaluation");
					//TODO CORRECT
					if(maxEvaluation.equals("invalid"))
					maxEvaluation="4000";
					//contextIntance.eSet(att, castValue(att,generateValue (att,minEvaluation,maxEvaluation)));
					updateAttribute(att, contextIntance, parentInstance, castValue(att,generateValue (att,minEvaluation,maxEvaluation)));
					
					}
					else {System.err.println("Wrong context for steryotype range - attribute - (handelAttributesStereotypes)  att: " +att.getName() +" class "+att.getEContainingClass().getName());
					res=false;}
					}	
					
				}
			}
					
			if(stereotype.getName().equals("from histogram") && res==false)
			{
				try{

				EList<String> values = (EList<String>)  getValue(att, stereotype,"bins");
				EList<String>probabilities = (EList<String>) getValue(att, stereotype,"frequencies");
				if(desiredHistogramsForAttributes.get(att)==null)
				{
					EList<String> tempProba = new BasicEList<String>();
					double total = Sum(probabilities);
		    		for (String s : probabilities) {
		    			if(total==1)
						tempProba.add(new String(s));
		    			else tempProba.add(new String(String.valueOf((Double.valueOf(s).doubleValue()/100))));
					}
		    		
		    		desiredHistogramsForAttributes.put(att, tempProba);
				}
				
				
				double totalProba = Sum(probabilities);
				boolean isOCL = (boolean)getValue(att, stereotype,"isOCL");
				int precision = (int)getValue(att, stereotype,"precision");
				Object OldValue = null;
				if(values.size()!=probabilities.size() )
					System.err.println("Size of values and probabilities is differenet (handelAttributesStereotypes) ==>"+att.getName());
				
				else {
					
					if(totalProba!=1&&totalProba!=100)
					System.err.println("Sum probabilities is not equal to 1 (handelAttributesStereotypes) "+Sum(probabilities)+" ==>"+att.getName()+" "+probabilities);
				
				else
					res=true;
				}
				
				EObject theObject =null;
				if(isOCL)
				{	Class contextTemp = (Class)getValue(att, stereotype,"context");		    
					EClass context = getEVersion(contextTemp);
					if(isTheContext(parentInstance, context))
					{	OCLForJAVA.init("", parentInstance);
						theObject = parentInstance;
					}
					else if(isTheContext(contextIntance, context))
					{OCLForJAVA.init("", contextIntance);
					theObject = contextIntance;
					}
					else  System.err.println("Wrong context for steryotype range - attribute - (handelAttributesStereotypes) att: " +att.getName() +" class "+att.getEContainingClass().getName());
					
		
					
				}
				
				String value= getfromProbaAttribute (values,probabilities,isOCL, theObject, precision,totalProba,att);
				OldValue = contextIntance.eGet(att);
				updateAttribute(att, contextIntance, parentInstance, castValue(att,value));
				if(res&&isConflicting(att)==stereotype)
				{
					updateHistogram(stereotype,castValue(att,value),att,OldValue,contextIntance,false);
				}
				}catch(Exception e)
				{e.printStackTrace();}
	
			}
			
			
			if(stereotype.getName().equals("from barchart") && res==false)
			{
				try{
				
				EList<org.eclipse.uml2.uml.internal.impl.EnumerationLiteralImpl > values = (EList<org.eclipse.uml2.uml.internal.impl.EnumerationLiteralImpl>)  getValue(att, stereotype,"categories");
				
				EEnum enumeration = (EEnum) att.getEType();
				EList<EEnumLiteral> literals = enumeration.getELiterals();
				boolean same = true;
				
				if(values.size()==0)
					{System.out.println("No literal specified for bar chart steryotype (handelAttributesStereotypes) att: " +att.getName() +" class "+att.getEContainingClass().getName());}
				else
				{

				int x=1;
				while (x<values.size()&& same == true) {
					if(enumeration.getName().equals(  ((values.get(x).getClassifier())).getName())==false)
						same=false;
						x++;
					
				}
				}
				
				if(literals.size()!= values.size())
					System.err.println("Some literals probabilities are missing");
				
				if(! same)
					System.out.println("The literals of the steryotype bar chat does not belong to the same enumeration (handelAttributesStereotypes)att: " +att.getName() +" class "+att.getEContainingClass().getName());
				
				for (int i = 0; i < values.size(); i++) {
					int j= 0;
					boolean find = false;
					String target_name= values.get(i).getName();
					
					while (find==false && j < literals.size()) {
						
						if(literals.get(j).getName().equals(target_name))
							find = true;
						else 
							j++;
						
					}
					
					
					if(j!=i)
					{
					literals.add(i+1,literals.get(j));
					literals.remove(i);
					
					literals.add(j+1,literals.get(i));
					literals.remove(j);
					
					}
					
				}
				
				EList<String>probabilities = (EList<String>) getValue(att, stereotype,"frequencies");
				double totalProba = Sum(probabilities);
				if(values.size()!=probabilities.size() )
					System.err.println("Size of values and probabilities is differenet (handelAttributesStereotypes) ==>"+att.getName());
				
				else {
					
					if(totalProba!=1&&totalProba!=100)
					System.err.println("Sum probabilities is not equal to 1 (handelAttributesStereotypes)==>"+att.getName());
				else
					res=true;
				}
				
				EEnumLiteral value= getEnumerationfromProba (literals,probabilities,totalProba);
				updateAttribute(att, contextIntance, parentInstance, value);
				//contextIntance.eSet(att, value);
				}catch(Exception e)
				{e.printStackTrace();}
	
			}
			
			
			

			if(stereotype.getName().equals("from distribution") && res==false)
			{
	            	res=true;
	            	int precision = (int) getValue(att, stereotype,"precision");
	            	BigDecimal bd = new BigDecimal(FiledGenerator.distribution(att,stereotype,contextIntance, parentInstance)).setScale(precision, RoundingMode.HALF_EVEN);
					//contextIntance.eSet(att, castValue(att,String.valueOf(bd.doubleValue())));
					updateAttribute(att, contextIntance, parentInstance, castValue(att,String.valueOf(bd.doubleValue())));
					
			}
			
			if(stereotype.getName().equals("identifier") && res==false)
			{   
				res=true;
				String att_name=att.getName();
				String class_name=att.getEContainingClass().getName();
				String id = class_name+"#"+att_name;
				String last = cache_identifiers.get(id);
				if(last==null)
				{
				//contextIntance.eSet(att, castValue(att,"1"));
				updateAttribute(att, contextIntance, parentInstance, castValue(att,"1"));

				cache_identifiers.put(id, "1");
				}
				else 
				{
					int id_int= Double.valueOf(last).intValue();
					id_int=id_int+1;
					cache_identifiers.put(id, String.valueOf(id_int));
					updateAttribute(att, contextIntance, parentInstance, castValue(att,String.valueOf(id_int)));

					//contextIntance.eSet(att, castValue(att,String.valueOf(id_int)));
				}
			}
			
			
			
		}
		return res;
	}
	
	
	
	
	@SuppressWarnings("unchecked")
	private void updateHistogram(Stereotype conflictingHistogram, Object castValue, EAttribute att,Object OldValue, EObject contextInstance, boolean dep) {
		if(!castValue.equals(""))
		{  
			Integer[] actual = actualStatusHistogramsForAttributes.get(att);
			EList<String>probabilities = ((EList<String>) getValue(att, conflictingHistogram,"frequencies"));
	    	if(actual == null)
	    	{
	    		actual = new Integer[probabilities.size()];
	    		for (int i = 0; i < actual.length; i++) {
					actual[i]=0;
				}
	    		actualStatusHistogramsForAttributes.put(att, actual);
	    	}
	    	if(dep)
	    	{
	        int idx=determineBine(att,conflictingHistogram,actual,castValue,contextInstance);
	        if(idx!=-1)
	        actual[idx]= actual[idx]+1;
	
	    	}
	    	Double [] actualFrequencies = getActualFrequencies(actual);
			if(desiredHistogramsForAttributes.get(att)==null)
			{	EList<String>dez = (EList<String>) getValue(att, conflictingHistogram,"frequencies");
			
				EList<String> tempProba = new BasicEList<String>();
				double total = Sum(dez);
	    		for (String s : dez) {
	    			if(total==1)
					tempProba.add(new String(s));
	    			else tempProba.add(new String(String.valueOf((Double.valueOf(s).doubleValue()/100))));
				}
	    		
	    		desiredHistogramsForAttributes.put(att, tempProba);
			}
	    	EList<String> desiredFrequencies = desiredHistogramsForAttributes.get(att);
	    	Double [] balance = new Double [actualFrequencies.length];
	    	for (int i = 0; i < balance.length; i++) {
				balance[i]=Double.valueOf(desiredFrequencies.get(i)).doubleValue()-actualFrequencies[i];
				if(balance[i]<0)
				balance[i]=0.0;
			}
	    	
	    	
	    	 EList<String> newProba = new BasicEList<String>();
	    	 normelize(balance,desiredFrequencies);
	    	 for (int i = 0; i < balance.length; i++) {
				newProba.add(String.valueOf(balance[i]));
			}
	    	 normelize2(newProba,desiredFrequencies);
	   
	    	 getCorrespondant(att,realAttributes).setValue(conflictingHistogram, "frequencies", newProba);
	    
	    	
		}
		
	}


	private static void normelize2(EList<String> newProba, EList<String> desiredFrequencies) {
		int nbZero = 0 ;
		double total = 0.0;
		for (String double1 : newProba) {
			if(Double.valueOf(double1).doubleValue()==0.0)
			nbZero++;
			else total = total + Double.valueOf(double1).doubleValue();
		}
		
		if(nbZero==newProba.size())
		{
			for (int i = 0; i < newProba.size(); i++) {
				newProba.set(i, String.valueOf(Double.valueOf(desiredFrequencies.get(i)).doubleValue()));
			}
		}
		else
		{
		
			if(Sum(desiredFrequencies)==1)
			{
		if(total<1)
		{
		double missing = 1-total;
		double bonus = missing /(newProba.size()- nbZero) ;
		for (int i = 0; i < newProba.size(); i++) {
			if(Double.valueOf(newProba.get(i)).doubleValue()!=0.0)
				newProba.set(i,String.valueOf( Double.valueOf(newProba.get(i)).doubleValue()+bonus));
		}
		}
		else 
		{
			if(total>1)
			{
			double extra = total-1;
			double malus = extra /(newProba.size() - nbZero) ;
			for (int i = 0; i < newProba.size(); i++) {
				if(Double.valueOf(newProba.get(i)).doubleValue()!=0.0)
					newProba.set(i,String.valueOf( Double.valueOf(newProba.get(i)).doubleValue()-malus));
			}
			}
		}
		}
			else 
			{	if(total<100)
			{
				double missing = 100-total;
				double bonus = missing /(newProba.size()- nbZero) ;
				for (int i = 0; i < newProba.size(); i++) {
					if(Double.valueOf(newProba.get(i)).doubleValue()!=0.0)
						newProba.set(i,String.valueOf( Double.valueOf(newProba.get(i)).doubleValue()+bonus));
				}
				}
				else 
				{
					if(total>100)
					{
					double extra = total-100;
					double malus = extra /(newProba.size() - nbZero) ;
					for (int i = 0; i < newProba.size(); i++) {
						if(Double.valueOf(newProba.get(i)).doubleValue()!=0.0)
							newProba.set(i,String.valueOf( Double.valueOf(newProba.get(i)).doubleValue()-malus));
					}
					}
				}}
		}
		
	}


	@SuppressWarnings("unchecked")
	private int determineBine(EAttribute att, Stereotype stereotype, Integer[] actual, Object castValue,EObject theObject) {
		int res = -1;
				
		EList<String> values = (EList<String>)  getValue(att, stereotype,"bins");
		boolean isOCL =  (boolean) getValue(att, stereotype,"isOCL");
		for (int j = 0; j < values.size(); j++) {	
		String value = values.get(j);

	
		if( (value.trim().startsWith("[")&&value.trim().endsWith("]"))==false)
		{   if(isOCL==false)
			{
			if(value.equals(castValue))
				return j;
			}
		else {if(OCLForJAVA.evaluateString(theObject, value, "").equals(castValue)) 
			return j;
		}
		}
		else
		{
			if(value.trim().contains(".."))
			{
				String valueTemp=value.trim().replace("[", "").replace("]", "").replace("..", "-");
				
				String [] temp=valueTemp.split("-");
				double start=-1;
				double end=-1;
				if(isOCL==false)
				{
				 start= Double.valueOf(temp[0].trim());
				 end= Double.valueOf(temp[1].trim());
				}
				else
				{
					 start= Double.valueOf(OCLForJAVA.evaluateString(theObject, temp[0].trim(), ""));
					 end= Double.valueOf(OCLForJAVA.evaluateString(theObject, temp[1].trim(), ""));
				}
				
				if(Double.valueOf(String.valueOf(castValue)).doubleValue()>=start && Double.valueOf(String.valueOf(castValue)).doubleValue()<=end)
			    return j;

				 
			}
			
			if(value.trim().contains("-"))
			{
				String valueTemp=value.trim().replace("[", "").replace("]", "");
				
				String [] temp=valueTemp.split("-");
				double start=-1;
				double end=-1;
				if(isOCL==false)
				{
				 start= Double.valueOf(temp[0].trim());
				 end= Double.valueOf(temp[1].trim());
				}
				else
				{
					 start= Double.valueOf(OCLForJAVA.evaluateString(theObject, temp[0].trim(), ""));
					 end= Double.valueOf(OCLForJAVA.evaluateString(theObject, temp[1].trim(), ""));
				}
				
				if(Double.valueOf(String.valueOf(castValue)).doubleValue()>=start && Double.valueOf(String.valueOf(castValue)).doubleValue()<=end)
				    return j;

			}
			
			if(value.trim().contains(","))
			{
				String [] temp=value.trim().replace("[", "").replace("]", "").split(",");
				if(isOCL==false)
				for (int i = 0; i < temp.length; i++) {
					if(temp[i].equals(castValue))
						return j;
				}
				else for (int i = 0; i < temp.length; i++) {
					if(OCLForJAVA.evaluateString(theObject, temp[i].trim(), "") .equals(castValue))
						return j;
				}
			}
		}
		}
		return res;
	}


	private static void normelize(Double[] balance, EList<String> desiredFrequencies) {
		int nbZero = 0 ;
		double total = 0.0;
		for (Double double1 : balance) {
			if(double1==0.0)
			nbZero++;
			else total = total + double1;
		}
		
		if(nbZero==balance.length)
		{
			for (int i = 0; i < balance.length; i++) {
				balance[i]=Double.valueOf(desiredFrequencies.get(i)).doubleValue();
			}
		}
		else
		{
		
			if(Sum(desiredFrequencies)==1)
			{
		if(total<1)
		{
		double missing = 1-total;
		double bonus = missing /(balance.length - nbZero) ;
		for (int i = 0; i < balance.length; i++) {
			if(balance[i]!=0.0)
			balance[i]=balance[i]+bonus;
		}
		}
		else 
		{
			if(total>1)
			{
			double extra = total-1;
			double malus = extra /(balance.length - nbZero) ;
			for (int i = 0; i < balance.length; i++) {
				if(balance[i]!=0.0)
				balance[i]=balance[i]-malus;
			}
			}
		}
		}
			else 
			{	if(total<100)
			{
				double missing = 100-total;
				double bonus = missing /(balance.length - nbZero) ;
				for (int i = 0; i < balance.length; i++) {
					if(balance[i]!=0.0)
					balance[i]=balance[i]+bonus;
				}
				}
				else 
				{
					if(total>100)
					{
					double extra = total-100;
					double malus = extra /(balance.length - nbZero) ;
					for (int i = 0; i < balance.length; i++) {
						if(balance[i]!=0.0)
						balance[i]=balance[i]-malus;
					}
					}
				}}
		}
	}


	private static Double[] getActualFrequencies(Integer[] actual) {
		int total = 0;
		for (Integer integer : actual) {
			total = total + integer;
		}
		Double [] res = new Double [actual.length];
		for (int i = 0; i < res.length; i++) {
			res [i]=0.0;
		}
		
		if(total==0)
		return res;
		else
		for (int i = 0; i < res.length; i++) {
			res [i]=(double)actual[i]/total;
		}
		
		return res;
	}


	private static Stereotype isConflicting(EAttribute att) {
		 EList<Stereotype> relatedSteryotypes = sortSteryotypes(getAllSteryotypesFromModel(att));
		 if(!contains(relatedSteryotypes, "value dependency")&&!contains(relatedSteryotypes, "type dependency"))
			 return null;
		 for (Stereotype stereotype : relatedSteryotypes) {
			if(stereotype.getName().equals("from histogram"))
				return stereotype;
		}
		return null;
	}


	@SuppressWarnings("unchecked")
	public boolean handelAttributesSteryotypsWithoutDep(EAttribute att, EObject contextIntance,EObject parentInstance)
	{ 
		boolean res=false;
        EList<Stereotype> relatedSteryotypes = sortSteryotypes(getAllSteryotypesFromModel(att));
        Stereotype stereotype=null;
		for (int ixx = 0; ixx < relatedSteryotypes.size() && res==false; ixx++) {
        	 stereotype=relatedSteryotypes.get(ixx);   	
        	
        	


		
			if(stereotype.getName().equals("fixed value")&& res==false)
			{
			    
				String value = String.valueOf( getValue(att, stereotype,"value"));
				boolean isOCL = (boolean)getValue(att, stereotype,"isOCL");
				
				if(isOCL==false)
				{res=true;
				updateAttribute(att, contextIntance, parentInstance, castValue(att,value));
				
				}	
				
			}
			
			if(stereotype.getName().equals("from uniform range") && res==false)
			{
				boolean isOCL = (boolean)getValue(att, stereotype,"isOCL");
				String maximum = String.valueOf( getValue(att, stereotype,"upperbound"));
				String minimum = String.valueOf( getValue(att, stereotype,"lowerbound"));
				
				
				if(isOCL==false)
				{
				String value= generateValue (att,minimum,maximum);
				res=true;
				updateAttribute(att, contextIntance, parentInstance, castValue(att,value));
				}
			}
					
			
			
			if(stereotype.getName().equals("from barchart") && res==false)
			{
				try{
				
				EList<org.eclipse.uml2.uml.internal.impl.EnumerationLiteralImpl > values = (EList<org.eclipse.uml2.uml.internal.impl.EnumerationLiteralImpl>)  getValue(att, stereotype,"categories");
				
				EEnum enumeration = (EEnum) att.getEType();
				EList<EEnumLiteral> literals = enumeration.getELiterals();
				boolean same = true;
				
				if(values.size()==0)
					{System.out.println("No literal specified for bar chart steryotype (handelAttributesStereotypes) att: " +att.getName() +" class "+att.getEContainingClass().getName());}
				else
				{

				int x=1;
				while (x<values.size()&& same == true) {
					if(enumeration.getName().equals(  ((values.get(x).getClassifier())).getName())==false)
						same=false;
						x++;
					
				}
				}
				
				if(literals.size()!= values.size())
					System.err.println("Some literals probabilities are missing");
				
				if(! same)
					System.out.println("The literals of the steryotype bar chat does not belong to the same enumeration (handelAttributesStereotypes)att: " +att.getName() +" class "+att.getEContainingClass().getName());
				
				for (int i = 0; i < values.size(); i++) {
					int j= 0;
					boolean find = false;
					String target_name= values.get(i).getName();
					
					while (find==false && j < literals.size()) {
						
						if(literals.get(j).getName().equals(target_name))
							find = true;
						else 
							j++;
						
					}
					
					
					if(j!=i)
					{
					literals.add(i+1,literals.get(j));
					literals.remove(i);
					
					literals.add(j+1,literals.get(i));
					literals.remove(j);
					
					}
					
				}
				
				EList<String>probabilities = (EList<String>) getValue(att, stereotype,"frequencies");
				double totalProba = Sum(probabilities);
				if(values.size()!=probabilities.size() )
					System.err.println("Size of values and probabilities is differenet (handelAttributesStereotypes) ==>"+att.getName());
				
				else {
					
					if(totalProba!=1&&totalProba!=100)
					System.err.println("Sum probabilities is not equal to 1 (handelAttributesStereotypes)==>"+att.getName());
				else
					res=true;
				}
				
				EEnumLiteral value= getEnumerationfromProba (literals,probabilities,totalProba);
				updateAttribute(att, contextIntance, parentInstance, value);
				//contextIntance.eSet(att, value);
				}catch(Exception e)
				{e.printStackTrace();}
	
			}
			
			
			

			if(stereotype.getName().equals("from distribution") && res==false)
			{
	            	res=true;
	            	int precision = (int) getValue(att, stereotype,"precision");
	            	BigDecimal bd = new BigDecimal(FiledGenerator.distribution(att,stereotype,contextIntance, parentInstance)).setScale(precision, RoundingMode.HALF_EVEN);
					updateAttribute(att, contextIntance, parentInstance, castValue(att,String.valueOf(bd.doubleValue())));
					
			}
			
			if(stereotype.getName().equals("identifier") && res==false)
			{   
				res=true;
				String att_name=att.getName();
				String class_name=att.getEContainingClass().getName();
				String id = class_name+"#"+att_name;
				String last = cache_identifiers.get(id);
				if(last==null)
				{
				updateAttribute(att, contextIntance, parentInstance, castValue(att,"1"));

				cache_identifiers.put(id, "1");
				}
				else 
				{
					int id_int= Double.valueOf(last).intValue();
					id_int=id_int+1;
					cache_identifiers.put(id, String.valueOf(id_int));
					updateAttribute(att, contextIntance, parentInstance, castValue(att,String.valueOf(id_int)));
				}
			}
			
			
			
		}
		return res;
	}
	
	
	public boolean handelAttributesSteryotypsAfter(EAttribute att, EObject contextIntance,EObject parentInstance)
	{ 
		boolean res=false;
        EList<Stereotype> relatedSteryotypes = sortSteryotypes(getAllSteryotypesFromModel(att));
        Stereotype stereotype=null;
		for (int ixx = 0; ixx < relatedSteryotypes.size() && res==false; ixx++) {
        	 stereotype=relatedSteryotypes.get(ixx);   	
        	
        
			
        	
        	
        	if(stereotype.getName().equals("value dependency") && res==false)
        	{  
        			Class context = (Class)getValue(att, stereotype,"context");
            		//TODO
            		Stereotype conflictingHistogram = isConflicting(att);
            		boolean isConflicting = conflictingHistogram!=null;
            		String value="";
            		Object oldValue = null;
            		if(context!=null)
            		{
            			EList<Constraint> constraints=(EList<Constraint>)getValue(att, stereotype,"OCLtrigger");
            			for (int i = 0; (i < constraints.size())&&(res==false); i++) {
            				Constraint constraint = constraints.get(i);
            				if(isTheContext(parentInstance,getEVersion(context)))
            				{
            				if(evaluateQuery(parentInstance,getEVersion(context),CharMatcher.ASCII.retainFrom(constraint.getSpecification().stringValue())).equals("true"))
            				{   
            					EList<Constraint> container = new BasicEList<Constraint>();
    	        				container.add(constraint);
    	        				
    	        				
            			    value=handelMultFromConstraints(container,contextIntance,parentInstance,att.getEType().toString());
            
            				if(!value.trim().equals(""))
            				{
            				res=true;
            				
            				oldValue = contextIntance.eGet(att);
            				updateAttribute(att, contextIntance, parentInstance, castValue(att,value));
            				}
            				}
            				}
            				else
            				{
            					if(isTheContext(contextIntance,getEVersion(context)))
                    				if(evaluateQuery(contextIntance,getEVersion(context),CharMatcher.ASCII.retainFrom(constraint.getSpecification().stringValue())).equals("true"))
                    				{
                    					EList<Constraint> container = new BasicEList<Constraint>();
            	        				container.add(constraint);
                    				value=handelMultFromConstraints(container,contextIntance,parentInstance,att.getEType().toString());
                    				if(!value.trim().equals(""))
                    				{
                    				res=true;
                    				oldValue = contextIntance.eGet(att);
                    				updateAttribute(att, contextIntance, parentInstance, castValue(att,value));
                    				}
                    				}
            					
            				}
            				
            				
            				if(res&&isConflicting)
            				{
            					updateHistogram(conflictingHistogram,castValue(att,value),att,oldValue,contextIntance,true);
            				}
    					}
            			
            			
            		
            		}
        		 				
        			
        		
        		
        	}
        	
        	if(stereotype.getName().equals("from histogram") && res==false)
			{
				try{

				EList<String> values = (EList<String>)  getValue(att, stereotype,"bins");
				EList<String>probabilities = (EList<String>) getValue(att, stereotype,"frequencies");
				Object oldValue = null;
				if(desiredHistogramsForAttributes.get(att)==null)
				{
					EList<String> tempProba = new BasicEList<String>();
					double total = Sum(probabilities);
		    		for (String s : probabilities) {
		    			if(total==1)
						tempProba.add(new String(s));
		    			else tempProba.add(new String(String.valueOf((Double.valueOf(s).doubleValue()/100))));
					}
		    		
		    		desiredHistogramsForAttributes.put(att, tempProba);
				}
				double totalProba = Sum(probabilities);
				boolean isOCL = (boolean)getValue(att, stereotype,"isOCL");
				int precision = (int)getValue(att, stereotype,"precision");
				if(values.size()!=probabilities.size() )
					System.err.println("Size of values and probabilities is differenet (handelAttributesStereotypes) ==>"+att.getName());
				
				else {
					
					if(totalProba!=1&&totalProba!=100)
					System.err.println("Sum probabilities is not equal to 1 (handelAttributesStereotypes) "+Sum(probabilities)+" ==>"+att.getName());
				
				else
					res=true;
				}
				
				EObject theObject =null;			
				String value= getfromProbaAttribute(values,probabilities,isOCL, theObject, precision,totalProba,att);
				oldValue = contextIntance.eGet(att);
				updateAttribute(att, contextIntance, parentInstance, castValue(att,value));
				if(res&&isConflicting(att)==stereotype)
				{
					updateHistogram(stereotype,castValue(att,value),att,oldValue,contextIntance,false);
				}
				

				
				}catch(Exception e)
				{e.printStackTrace();}
	
			}
			
		
		}
		return res;
	}
	
	
	//cannot Cache this

	public static String evaluateQuery (EObject instance, EClass classifier , String queryString)
	{
		String res="false";

		try {
			
			
			
			/*
			OclDependencies.helper.setContext(classifier);
		    OCLExpression<EClassifier> query= OclDependencies.helper.createQuery(queryString);
			res=String.valueOf( OclDependencies.ocl.evaluate(instance, query));		
			String ch =String.valueOf(OclDependencies.ocl.evaluate(instance, OclDependencies.helper.createQuery("self")));	
			
			
			System.err.println("Query==>"+queryString);
			System.err.println("Res==>"+res);
			logger.trace("ch==>"+ch);
			logger.trace("Query==>"+queryString);
			logger.trace("Res==>"+res);
	*/
			
		
		
			OCLForJAVA.init("",instance);
			res = OCLForJAVA.evaluateString(instance, CharMatcher.ASCII.retainFrom(queryString), "res");
			/*
			if(queryString.contains("not self.income_type"))
			{
			
			System.out.println("Self==>"+instance);
			String ch =String.valueOf(OclDependencies.ocl.evaluate(instance, OclDependencies.helper.createQuery("self.oclAsType(Income).income_type")));	
			System.out.println("ch==>"+ch);
			System.out.println("Query==>"+queryString);
			System.out.println("Res==>"+res);
			}
			*/
		} catch (Exception e) {
			e.printStackTrace();
		}
        return res;
		
	}

	//cannot Cache this
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
		//e.printStackTrace();
	} }

	//cannot Cache this
    public static void save1(String name, LinkedList<EObject> inst,int round){
    
    ResourceSetImpl resourceSet = new ResourceSetImpl(); 
    resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl()); 
    final File f = new File("");
    final String dossierPath = f.getAbsolutePath().replace("\\", "//");
    final String apiSamplePath = dossierPath +"//Instances1//"; 
    final String file_name=name+"_"+round+".xmi";
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


	//cannot Cache this
	public static void append(String name, LinkedList<EObject> inst){
    	 final String ADName="FD";
		 final String model="//Papyrus//TaxCard.uml";
		 final String instances_rep="//Instances//TaxpayersOf"+ADName+".xmi";
		 
		 
		    File f=new File("");
		    final String folderPath = f.getAbsolutePath();
	        final String absolutePath = folderPath + model;
    
	         

	        	    Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put(
	            		    "uml", new UMLResourceFactoryImpl());
	

	      ResourceSet rs = new ResourceSetImpl(); 
	      final ExtendedMetaData extendedMetaData = new BasicExtendedMetaData(rs.getPackageRegistry());
	      rs.getLoadOptions().put(XMLResource.OPTION_EXTENDED_META_DATA,extendedMetaData);
	      
	      
	      URI modelFileURI = URI.createFileURI(absolutePath);          
	      Resource resource=null;
	      resource = Main.loadUmlResource(modelFileURI,rs);
	    	  
			Iterator<EObject> i = resource.getAllContents();
	 		while (i.hasNext()) {
	 			Object o = i.next();
	 			 if (o instanceof EPackage) {
	 	              EPackage p = (EPackage)o;
	 	              rs.getPackageRegistry().put(p.getNsURI(), p);       	
	 	     
	 	          }
	 			

	 	 	}
			
	 		//read the model
	 		EmfModelReader reader = new EmfModelReader(resource);  
		    	 ResourceSet load_resourceSet = new ResourceSetImpl();
			 	 load_resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());
			 	 load_resourceSet.getPackageRegistry().put(reader.getPackages().get(0).getNsURI(),reader.getPackages().get(0));
			 	 Resource load_resource = load_resourceSet.getResource(URI.createURI(folderPath+instances_rep),true);
			
			 	 EList<EObject> elements= load_resource.getContents();
	
    ResourceSetImpl resourceSet = new ResourceSetImpl(); 
    resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl()); 
    final String dossierPath = f.getAbsolutePath().replace("\\", "//");
    final String apiSamplePath = dossierPath +"//Instances//"; 
    final String file_name=name+".xmi";
    String path = "file://" + apiSamplePath + file_name;
    URI uri = URI.createURI(path); 
    Resource resource1 = resourceSet.createResource(uri); 
    inst.addAll(elements);

    resource1.getContents().addAll(inst); 
	
    
    try {
		resource1.save(null);
	} catch (IOException e) {
		//e.printStackTrace();
	} }



	  
	
	  private static void breakGeneralization(LinkedList<EObject> dep, EClass source) {
		for (int i = 0; i < dep.size(); i++) {
			EClass theTargetClass = null;
			if(dep.get(i) instanceof EClass)
			 theTargetClass = (EClass)dep.get(i);
			else if(dep.get(i) instanceof EAttribute)
				 theTargetClass = ((EAttribute)dep.get(i)).getEContainingClass();
			else  if(dep.get(i) instanceof EReference)
				 theTargetClass = (EClass)((EReference)dep.get(i)).getEType();

				if(source.getName().equals(theTargetClass.getName()))
				{dep.remove(i);
				i--;}
			else {
				if(existsIn(CreateTaxpayers.getAllChildrenClasses(source),theTargetClass))
				{dep.remove(i);
				i--;}
				else 
				{
					if(existsIn(CreateTaxpayers.getAllChildrenClasses(theTargetClass),source))
					{dep.remove(i);
					i--;}
				}
			}
			
			}
		
		
	}
	  
		 public static boolean existsIn (LinkedList<EClass> liste, EClass c)
		 {
			 boolean find = false;
			 int i =0;
			 while (i<liste.size()&&find==false)
			 {
				if(liste.get(i).getName().equals(c.getName()))
					return true;
			 else i++;
			 }
			 return find;
		 }

    

}
