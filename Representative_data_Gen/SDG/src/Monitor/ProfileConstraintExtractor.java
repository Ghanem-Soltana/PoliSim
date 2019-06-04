package Monitor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.io.IOUtils;
import org.apache.commons.math3.distribution.AbstractRealDistribution;
import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EParameter;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.uml2.uml.Association;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Constraint;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.Enumeration;
import org.eclipse.uml2.uml.Generalization;
import org.eclipse.uml2.uml.Interface;
import org.eclipse.uml2.uml.InterfaceRealization;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.Stereotype;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.traverse.TopologicalOrderIterator;

import snt.oclsolver.eocl.EclipseOCLWrapper;
import snt.oclsolver.tuples2environment.UMLEnvironment;
import snt.oclsolver.writer.TuplesUtil;
import snt.oclsolver.writer.WriteToXmi;
import util.EmfModelReader;
import util.EmfOclParser;
import util.FiledGenerator;
import util.Invariant;
import util.OclAST;
import Main.GenerateSeeds;
import adapters.umlImpl.EDatatypeUtil;

import com.google.common.base.CharMatcher;

public class ProfileConstraintExtractor {
	
	public static  LinkedList<EObject> all;
	public static EList<org.eclipse.uml2.uml.Element> realAll;
	public static  LinkedList<org.eclipse.uml2.uml.Element> realClasses;
	public static  LinkedList<org.eclipse.uml2.uml.Element> realEnumerations;
	public static  LinkedList<org.eclipse.uml2.uml.Element> realAttributes;
	public static  LinkedList<org.eclipse.uml2.uml.Element> realAssociations;
	public static EList<Generalization> listeGeneralization;
	public static EList<InterfaceRealization> listeInterfaceRealization;
	public static String profileName="DomainModelProfile::";
    public static HashMap<String, org.eclipse.uml2.uml.Element>	correspendance;
    public static HashMap<String,EObject>	eVersion;
    public static HashMap<String, LinkedList<EObject>> refrencesNeeds;
    public static HashMap<String, Integer> perturbationMax;
    public static TopologicalOrderIterator<String, DefaultEdge> orderIterator;
	private static Logger logger = Logger.getLogger("Taxpayers");
	protected int maxAllowedRecurison = 100;
	public UMLEnvironment umlEnv;
	public static Map<EClass, Set<EObject>> extents;
	public static LinkedList<EReference> safeEReferences;
	public static LinkedList<EReference> oppositeEReferences;
	public static LinkedList<EReference> unsafeEReferences;
	public static LinkedList<EReference> listeEReferences;
	public static EPackage pck;
	public static LinkedList<HistogramClass> hitogramsForClasses;
	public static LinkedList<Histogram> hitogramsForAttributes;
	public static LinkedList<Histogram> hitogramsForAssociations;
	
	public ProfileConstraintExtractor(ResourceSet resourceSet, Package pck, UMLEnvironment umlEnv)
	{	
	
	this.umlEnv = umlEnv;
	realAll = pck.allOwnedElements();
	realClasses = new LinkedList<org.eclipse.uml2.uml.Element>();
	realEnumerations= new LinkedList<org.eclipse.uml2.uml.Element>();
	realAttributes= new LinkedList<org.eclipse.uml2.uml.Element>();
	realAssociations= new LinkedList<org.eclipse.uml2.uml.Element>();
	perturbationMax=new HashMap<String, Integer>();
	filter(realAll);
	correspendance=new HashMap<String, org.eclipse.uml2.uml.Element>();
	eVersion=new HashMap<String, EObject>();
	hitogramsForClasses=new LinkedList<HistogramClass>();
	hitogramsForAttributes = new LinkedList<Histogram>();
	hitogramsForAssociations = new LinkedList<Histogram>();
   FiledGenerator.cache_distributions = new HashMap<String, AbstractRealDistribution>();
	extents = new HashMap<EClass, Set <EObject>>();
	safeEReferences = new LinkedList<EReference>();
		oppositeEReferences = new LinkedList<EReference>();
		unsafeEReferences  = new LinkedList<EReference>();
		listeEReferences = new LinkedList<EReference>();


	   }
	
	
	
	public ProfileConstraintExtractor(ResourceSet resourceSet, int count,EPackage pck,Model m)
	{        //caches
    	
		 if(count==1)
			{
		ProfileConstraintExtractor.pck = pck;
	
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
    	hitogramsForClasses=new LinkedList<HistogramClass>();
    	hitogramsForAttributes = new LinkedList<Histogram>();
    	hitogramsForAssociations = new LinkedList<Histogram>();
    	listeGeneralization =  OclAST.reader.extractGeneralizations();
    	listeInterfaceRealization = OclAST.reader.extractInterfaceRealization();

       logger.trace("*********** Gen at "+ new Date().toString()+" ***********");
       FiledGenerator.cache_distributions = new HashMap<String, AbstractRealDistribution>();
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
   		if(GenerateSeeds.needSlicing==false)
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

   	   
		}
		 removeDuplicat(listeEReferences);
	
	}
	
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
	  
	  
	  public LinkedList<Classifier> getClassSubtypes(List<Classifier> classList, Classifier c) {
		    LinkedList<Classifier> subTypesList = new LinkedList<Classifier>();  
		    if (classList != null) 
		      for (Classifier cl : classList) 
		        for (Classifier superType : cl.getGenerals()) 
		          if (c.equals(superType))
		            subTypesList.add(cl);
		    return subTypesList.size() > 0 ? subTypesList : new LinkedList<Classifier>();
		  }
	  
	  
	  public  LinkedList<Classifier> getAllChildrenClassesFromUML(Classifier c)
	  {
		 
		  LinkedList<Classifier> res= getClassSubtypes(umlEnv.getUMLClasses(), c);
	
			 for (int i = 0; i < res.size(); i++) {
				 LinkedList<Classifier> propagation = getAllChildrenClassesFromUML(res.get(i));
				 res.addAll(propagation);
			}
			 
			 return res;
	   }
	  
	  
	  

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

    public static void filter (EList<org.eclipse.uml2.uml.Element> liste)
    {

    	
    		for (org.eclipse.uml2.uml.Element element : liste) {
    			if(element instanceof Interface)
    			realClasses.add(element);
    				
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
    
	public static void removeDuplicatClasses( LinkedList<EClass> list)
	{
		if(list!=null)
		{
		LinkedHashSet<EClass> s = new LinkedHashSet<EClass>(list);
		list=new LinkedList<EClass>();
		list.addAll(s);
		}
	}
	
	public static LinkedList<Classifier> removeDuplicatUMLClasses( LinkedList<Classifier> list)
	{
		if(list!=null)
		{
		LinkedHashSet<Classifier> s = new LinkedHashSet<Classifier>(list);
		list=new LinkedList<Classifier>();
		list.addAll(s);
		}
		
		for (int i = 0; i < list.size()-1; i++) {
			
			for (int j = i+1; j <  list.size(); j++) {
				if(list.get(i).getName().equals(list.get(j).getName()))
				{
					list.remove(j);
					j--;
				}
				
			}
			
		}
		return list;
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
	
	public static void removeDuplicatOperations( LinkedList<EOperation> list)
	{
		
		if(list!=null)
		{
		LinkedHashSet<EOperation> s = new LinkedHashSet<EOperation>(list);
		list=new LinkedList<EOperation>();
		list.addAll(s);
		}
	}

	//TODO Main
	public void extract(EPackage ePackage, EClass context, EReference callingRef,LinkedList<EObject> visited,EClass root, LinkedList<Invariant> res ) {
      
		
		if(!visited.contains(context))
       {
		
    	   
    	   visited.add(context);
    	   
    	   
    	   LinkedList<EAttribute> attributes=null;
    	   attributes=getReleventAttributes(context, all); 
    	   
    	   //attributes
    	   for(EAttribute att: attributes)
    	   {
           
    		   if(!visited.contains(att))
    		   {
    		   visited.add(att);
    		   
    		   
    		   if(att.isDerived()==false)
	          	{
    			   res.addAll(extractConstraintsFromSteryotypes((ENamedElement)att,context));
    			   
    			   if(att.isMany())
    			   {
    				   int lower = att.getLowerBound();
    				   int upper = att.getUpperBound();
    				   
    				   if(lower!=0||upper!=-1)
    				   {
    						EClass contextTemp = (EClass) att.getEContainingClass();
    						String ocl ="";
    						if(lower == upper)
    						ocl = "self."+att.getName()+"->size() = "+ upper;
    						if(lower!=0&&upper!=-1)
    						 ocl = "self."+att.getName()+"->size() >= "+ lower +" and "+ "self."+att.getName()+"->size() <= "+ upper;
    						else if(lower!=0)
       						 ocl = "self."+att.getName()+"->size() >= "+ lower;
    						else ocl = "self."+att.getName()+"->size() <= "+ upper;


    		              	Invariant inv = new Invariant((Class)getCorrespondant(contextTemp, realClasses),ocl);
    						storeRes(res,inv,false,"");
    				   }
    			   }
	          	
	          	}
    	   }
    	   }
    	   
    	   
    	   
    	   EList<EClass> supers = context.getEAllSuperTypes();
    	   LinkedList<EClass> sup2 = getAllChildrenClasses(context);
    	   
    	   sup2.addAll(supers);
    	   sup2.add(context);
    	   
    	   
    	   
    	   LinkedList<EReference> conexions=getRefrences(context); 
    	   
    	  for (EReference eReference : conexions) {
    		res.addAll(extractConstraintsFromSteryotypes((ENamedElement)eReference,context));
    		
				   int lower = eReference.getLowerBound();
				   int upper = eReference.getUpperBound();
				   
				   if(lower!=0||upper!=-1)
				   {
						EClass contextTemp = (EClass) eReference.getEContainingClass();
						String ocl ="";
						if(eReference.isMany())
						{
						if(lower == upper)
						ocl = "self."+eReference.getName()+"->size() = "+ upper;
						else if(lower!=0&&upper!=-1)
						 ocl = "self."+eReference.getName()+"->size() >= "+ lower +" and "+ "self."+eReference.getName()+"->size() <= "+ upper;
						else if(lower!=0)
						 ocl = "self."+eReference.getName()+"->size() >= "+ lower;
						else ocl = "self."+eReference.getName()+"->size() <= "+ upper;
						}
						else 
						{
							if(lower==1)
							ocl = "self."+eReference.getName()+".oclIsUndefined() = false ";
						}

						if(!ocl.equals(""))
						{
		              	Invariant inv = new Invariant((Class)getCorrespondant(contextTemp, realClasses),ocl);
						storeRes(res,inv,false,"");
						}
				   }
			   
    	
    	  }
    	   
    	   
    	   	   
    	  
    	   //Explore

    	   for (EClass eClass : sup2) {
    	    	extract(ePackage,eClass, callingRef, visited,root, res);
    	    	
    	    	
    	    	
    	    	
    	    	
    	   	 //already sorted
  	    	  LinkedList<EReference> references=getRefrences(eClass);    
    	    	
    	    	for (EReference eReference : references) {
    	    		extract(ePackage, (EClass)eReference.getEType(), eReference, visited,root, res);

 				   int lower = eReference.getLowerBound();
 				   int upper = eReference.getUpperBound();
 				   
 				   if(lower!=0||upper!=-1)
 				   {
 						EClass contextTemp = (EClass) eReference.getEContainingClass();
 						String ocl ="";
 						if(eReference.isMany())
 						{
 						if(lower == upper)
 						ocl = "self."+eReference.getName()+"->size() = "+ upper;
 						else if(lower!=0&&upper!=-1)
 						 ocl = "self."+eReference.getName()+"->size() >= "+ lower +" and "+ "self."+eReference.getName()+"->size() <= "+ upper;
 						else if(lower!=0)
 						 ocl = "self."+eReference.getName()+"->size() >= "+ lower;
 						else ocl = "self."+eReference.getName()+"->size() <= "+ upper;
 						}
 						else 
 						{
 							if(lower==1)
 							ocl = "self."+eReference.getName()+".oclIsUndefined() = false ";
 						}

 						if(!ocl.equals(""))
 						{
 		              	Invariant inv = new Invariant((Class)getCorrespondant(contextTemp, realClasses),ocl);
 						storeRes(res,inv,false,"");
 						}
 				   }
  			   
				}
    	    	
    	    	
    			}
    	   
    	   
    	   //References mult + types dep
    	   
       
       }
		else {
			
			if(callingRef!=null)
			{
				res.addAll(extractConstraintsFromSteryotypes((ENamedElement)callingRef,context));
			}
		}
	    	

	
	}
	
	  
	  public EList<Constraint> getConstraintsFromSteryotype(EReference ref, String name)
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
	
	public LinkedList<EReference>getRefrences(EClass c)
	{   LinkedList<EReference> res= new LinkedList<EReference>();

		for (int i = 0; i < listeEReferences.size(); i++) {

				EReference ref= listeEReferences.get(i);
				if(isReferenceReleventTOClass(c,ref.eContainer()))
					if(res.contains(ref)==false)
					res.add(ref);

		}

		return res;
	}
	
	
	public LinkedList<Property>getRefrencesProperties(Classifier c)
	{   LinkedList<Property> res= new LinkedList<Property>();
	
		EList<Property> atts = c.getAttributes();

		for (int i = 0; i < atts.size(); i++) {

				Property ref= atts.get(i);
				if(ref.getAssociation()==null)
					continue;
					if(res.contains(ref)==false)
					res.add(ref);

		}		return res;
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
	  public static org.eclipse.uml2.uml.Element getCorrespondant(EObject obj, LinkedList<org.eclipse.uml2.uml.Element> liste)
	    { 
	    	org.eclipse.uml2.uml.Element res=correspendance.get(obj.toString());;
	    	
	    	if(res==null)
	    	{
	    	if(obj instanceof EClass)
	    	{
	    		for (org.eclipse.uml2.uml.Element element : liste) {
	    			System.out.println(element);
	    			if(element instanceof org.eclipse.uml2.uml.Class)
					if( ((org.eclipse.uml2.uml.Class)element).getName().trim().equals(((EClass) obj).getName()))
					   {correspendance.put(obj.toString(), element);
						  return element;
					   }
	    			
	    			if(element instanceof Interface)
						if( ((Interface)element).getName().trim().equals(((EClass) obj).getName()))
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
	    		  if(((org.eclipse.uml2.uml.Property)element).isAttribute())
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
		
		//Cannot be cached
		@SuppressWarnings({ "unchecked", "restriction" })
		public LinkedList<String> handelMultFromConstraints(EList<Constraint> allCons, String type, String p1, Class contextOfTheAttribute, Class contextOftheConstraint, ENamedElement att)
		{   
			LinkedList<String> res=new LinkedList<String>();
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
	               			for (int i = 0; (i < constraints.size()); i++) {
	               				Constraint constraint = constraints.get(i);
	               				EList<Constraint> container = new BasicEList<Constraint>();
	        	        		container.add(constraint);			
	               			    LinkedList<String> what=handelMultFromConstraints(container,"",treatSelf(p1,context,contextOfTheAttribute),contextOfTheAttribute,context,att);
	               			   
	               			    
	               			    String ch = "";
	               			for (int j = 0; j < what.size(); j++) {
	               				if(j==0)
	               				ch = p1+what.get(j);
	               				else ch = ch +" or "+ p1+what.get(j);
	            
	        				}
	               			    
	                          	res.add(ch);
	        					}
	               		}
	               		
	               	
	               	}
	               	
	                
	        	
	        		
	               	
	    			if(stereotype.getName().equals("fixed value"))
	    			{
	    			    
	    				String value = String.valueOf( getValue(cons, stereotype,"value"));
	    				value=updateCasseString(value,type);
	    				String ocl = p1 + " = "+treatSelf(value, contextOftheConstraint, contextOfTheAttribute);
	    				res.add(ocl);
	    				
	    			}
	    			
	    			
	    			if(stereotype.getName().equals("from uniform range"))
	    			{
	    				String maximum = String.valueOf( getValue(cons, stereotype,"upperbound"));
	    				String minimum = String.valueOf( getValue(cons, stereotype,"lowerbound"));
	    				String ocl = p1 + " >= "+treatSelf(minimum, contextOftheConstraint, contextOfTheAttribute)+" and " + p1+ " <= "+treatSelf(maximum, contextOftheConstraint, contextOfTheAttribute);
	    				res.add(ocl);
	    			
	    			}
	    					
	    			if(stereotype.getName().equals("from histogram"))
	    			{
	    				
	    				EList<String> values = (EList<String>)  getValue(cons, stereotype,"bins");
	    				EList<String>probabilities = (EList<String>) getValue(cons, stereotype,"frequencies");
	    				String ocl ="";
	    				for (int i = 0; i < values.size(); i++) {
	    					if(i==0)
	    					ocl = "("+ getExpression(p1,values.get(i),probabilities.get(i),contextOftheConstraint,contextOfTheAttribute, type) +")";
	    					else ocl = ocl + " or ("+ getExpression(p1,values.get(i),probabilities.get(i),contextOftheConstraint,contextOfTheAttribute,type) +")";

	    					
	    				}
	    				
	    				res.add(ocl);
	    			}
	    			
	    			
	    			
	    			if(stereotype.getName().equals("type dependency spec"))
	    			{
	    				EList<Class> values = (EList<Class>)  getValue(cons, stereotype,"possibleTypes");
	    				EList<String>probabilities = (EList<String>) getValue(cons, stereotype,"frequencies");
	    				String ocl ="";		
	    				for (int i = 0; i < values.size(); i++) {
	    					if(i==0)
	    					ocl = "("+ getExpressionSpecial(p1,values.get(i).getName(),probabilities.get(i),contextOftheConstraint,contextOfTheAttribute, type,att) +")";
	    					else ocl = ocl + " or ("+ getExpressionSpecial(p1,values.get(i).getName(),probabilities.get(i),contextOftheConstraint,contextOfTheAttribute,type,att) +")";

	    					
	    				}
	    				res.add(ocl);
	    				
	    			}
	    			
	    			if(stereotype.getName().equals("from barchart"))
	    			{
	    				try{
	    				
						EList<org.eclipse.uml2.uml.internal.impl.EnumerationLiteralImpl > values = (EList<org.eclipse.uml2.uml.internal.impl.EnumerationLiteralImpl>)  getValue(cons, stereotype,"categories");
	    				
	    				EEnum enumeration =null;
	    	
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
	    		
	    				String ocl ="";
	    				for (int i = 0; i < values.size(); i++) {
	    					if(i==0)
	    						ocl = "("+ getExpressionLiteral(p1,values.get(i),probabilities.get(i)) +")";
	    					else ocl = ocl + " or ("+ getExpressionLiteral(p1,values.get(i),probabilities.get(i)) +")";
	    					
	    				}
	    				
	    				res.add(ocl);
	    				}catch(Exception e)
	    				{e.printStackTrace();}
	    	
	    			}
	    			
	    	
	    			
			}
			
		}
			return res;
		}
	
	
	private static String treatSelf(String p1, Class contextOfTheConstraint, Class contextOfTheAttribute) {
			if(contextOfTheConstraint==null||contextOfTheAttribute==null)
			return p1;
			else if(contextOfTheConstraint==contextOfTheAttribute)
			return p1;
			else if(contextOfTheConstraint.getSuperClasses().contains(contextOfTheAttribute))
			return p1;
			else
			{
				

			  
			  boolean find = false;
			  EClass source = getEVersion(contextOfTheAttribute);
			  EClass target = getEVersion(contextOfTheConstraint);
			  EList<EReference> myrefs = source.getEAllReferences();
			  String nav = "";
			  for (int i = 0; i < myrefs.size()&&find == false; i++) {
				  if(myrefs.get(i).getUpperBound()==1)
				  if(myrefs.get(i).getEType().getName().equals(target.getName()))
				  {
					  find = true;
					  nav = myrefs.get(i).getName();
				  }
				
			}
				
			  if(!find)
				return p1;
			  else return p1.replace("self.", "self."+nav+".");
			}
				
				
		
		}
	
	public static EClass getEVersion(Classifier c)
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
	
	
	//TODO irass

	@SuppressWarnings({ "unchecked", "restriction" })
	private LinkedList<Invariant> extractConstraintsFromSteryotypes(ENamedElement att,EClass contextInput) {
		LinkedList<Invariant> res = new LinkedList<Invariant>();
		 EList<Stereotype> relatedSteryotypes = sortSteryotypes(getAllSteryotypesFromModel(att));
	        Stereotype stereotype=null;
	        boolean needExtraCollectionTreatment = false;
	        boolean isMixed=false;
	        EClass souceOfTheElement = null; 
	        if(att instanceof EAttribute)
	        {souceOfTheElement = ((EAttribute) att).getEContainingClass();}
	        
	        if(att instanceof EReference)
	        {souceOfTheElement = ((EReference) att).getEContainingClass();}
	      
	        
	        
	        
	        if(souceOfTheElement!=null)
	        {
	        if(isTheContext(contextInput, souceOfTheElement))	
	        {
	        
	        
	        Class contextOfTheAttribute=(Class)getCorrespondant(souceOfTheElement,realClasses);
	        String p1="";
	        if(att instanceof EAttribute)
	         p1 = "self."+att.getName();
	        if(att instanceof EReference)
	        {
		    if(!((EReference)att).isMany())
		    {needExtraCollectionTreatment=true;}
	        p1 = "self."+att.getName()+"->size()";
	        }
	        
	        
    		String type = "string";
    		if(att instanceof EAttribute)
   	        type = ((EAttribute) att).getEType().getName();
   	        
   	        if(att instanceof EReference)
   	        type = ((EReference) att).getEType().getName();
	        

	        
	    for (int ixx = 0; ixx < relatedSteryotypes.size(); ixx++) {
       	 stereotype=relatedSteryotypes.get(ixx);   	

       	if(stereotype.getName().equals("value dependency"))
       	{  isMixed = true;
       		Class context = (Class)getValue(att, stereotype,"context");
       		
       		if(context!=null)
       		{
       			EList<Constraint> constraints=(EList<Constraint>)getValue(att, stereotype,"OCLtrigger");
       			for (int i = 0; (i < constraints.size()); i++) {
       				Constraint constraint = constraints.get(i);
       				EList<Constraint> container = new BasicEList<Constraint>();
	        		container.add(constraint);			
	        		
	       	     
	       	        
       			    LinkedList<String> what=handelMultFromConstraints(container,type,p1,contextOfTheAttribute,context,att);
       			   
       			    
       			    String ch = "";
       			for (int j = 0; j < what.size(); j++) {
       				if(j==0)
       				ch = what.get(j);
       				else ch = ch + " or " +  what.get(j);
       				
				}
       			    
              		String ocl = "if("+treatSelf(CharMatcher.ASCII.retainFrom(constraint.getSpecification().stringValue()),context,contextOfTheAttribute).replaceAll("(?m)^[ \t]*\r?\n", "")+") then "+ch.replaceAll("(?m)^[ \t]*\r?\n", "")+" else $$$$$ endif";
              		Invariant inv = new Invariant(contextOfTheAttribute,ocl);
    				storeRes(res,inv,needExtraCollectionTreatment,p1);
					}
       		}
       		
       	
       	}
       	
        
    	if(stereotype.getName().equals("type dependency"))
		{
    		EClass context = getEVersion((Class)getValue(att, stereotype,"context"));
    		if(context==souceOfTheElement)
    		{
    		EList<Constraint> constraints = (EList<Constraint>)getValue(att, stereotype,"OCLtrigger");
   			for (int i = 0; (i < constraints.size()); i++) {
   				Constraint constraint = constraints.get(i);
   				EList<Constraint> container = new BasicEList<Constraint>();
        		container.add(constraint);				        
   			    LinkedList<String> what=handelMultFromConstraints(container,type,"self."+att.getName(),contextOfTheAttribute,(Class)getCorrespondant(context, realClasses),att);
	    
   			    String ch = "";
   			for (int j = 0; j < what.size(); j++) {
   				if(j==0)
   				ch = what.get(j);
   				else ch = ch + " or " +  what.get(j);
   				
			}
   			
   			String condString = treatSelf(CharMatcher.ASCII.retainFrom(constraint.getSpecification().stringValue()),(Class)getCorrespondant(context, realClasses),contextOfTheAttribute).replaceAll("(?m)^[ \t]*\r?\n", "");
   			String ocl = "";
   			if(!condString.trim().equals(""))
   			ocl = "if("+condString+") then "+ch.replaceAll("(?m)^[ \t]*\r?\n", "")+" else $$$$$ endif";
   			else ocl=ch.replaceAll("(?m)^[ \t]*\r?\n", "");
      		Invariant inv = new Invariant(contextOfTheAttribute,ocl);
			storeRes(res,inv,needExtraCollectionTreatment,p1);
	
   			}
					
				
		}
		
		}
   		
   		
       	
			if(stereotype.getName().equals("fixed value"))
			{
			    
				String value = String.valueOf( getValue(att, stereotype,"value"));
				value=updateCasseString(value,type);
				Class contextTemp = (Class)getValue(att, stereotype,"context");
				String ocl = p1 + " = "+ treatSelf(value, contextTemp, contextOfTheAttribute);
              	Invariant inv = new Invariant(contextOfTheAttribute,ocl);
				storeRes(res,inv,needExtraCollectionTreatment,p1);
			}
			
			
			if(stereotype.getName().equals("from uniform range"))
			{	
				Class contextTemp = (Class)getValue(att, stereotype,"context");
				String maximum = String.valueOf( getValue(att, stereotype,"upperbound"));
				String minimum = String.valueOf( getValue(att, stereotype,"lowerbound"));
				String ocl = p1 + " >= "+treatSelf(minimum, contextTemp, contextOfTheAttribute)+" and " + p1+ " <= "+treatSelf(maximum, contextTemp, contextOfTheAttribute);
				Invariant inv = new Invariant(contextOfTheAttribute,ocl);
				storeRes(res,inv,needExtraCollectionTreatment,p1);
			
			}
					
			if(stereotype.getName().equals("from histogram"))
			{
				
				EList<String> values = (EList<String>)  getValue(att, stereotype,"bins");
				Class contextTemp = (Class)getValue(att, stereotype,"context");
				EList<String>probabilities = (EList<String>) getValue(att, stereotype,"frequencies");
				String ocl ="";
				for (int i = 0; i < values.size(); i++) {
					if(i==0)
					ocl = "("+ getExpression(p1,values.get(i),probabilities.get(i),contextTemp,contextOfTheAttribute,type) +")";
					else ocl = ocl + " or ("+ getExpression(p1,values.get(i),probabilities.get(i),contextTemp,contextOfTheAttribute,type) +")";
					
					
				}
		    	
				Invariant inv = new Invariant(contextOfTheAttribute,ocl);
				storeRes(res,inv,needExtraCollectionTreatment,p1);
			}
			
			
			if(stereotype.getName().equals("multiplicity"))
			{   
				
				Class target =  (Class) getValue(att, stereotype,"targetMember");
				if(!target.getName().equals(contextOfTheAttribute.getName()))
				{
				EList<Constraint> constraints =  (EList<Constraint>) getValue(att, stereotype,"constraint");
				if(constraints!=null)
				{
	       			for (int i = 0; (i < constraints.size()); i++) {
	       				Constraint constraint = constraints.get(i);
	       				EList<Constraint> container = new BasicEList<Constraint>();
		        		container.add(constraint);			
		        		LinkedList<String> what=handelMultFromConstraints(container,type,p1,contextOfTheAttribute,(Class)getCorrespondant(souceOfTheElement,realClasses),att);
       			   
       			    
       			    String ch = "";
       			for (int j = 0; j < what.size(); j++) {
       				if(j==0)
       				ch = what.get(j);
       				else ch = ch + " or " +  what.get(j);
       				
				}
       			    
              		String ocl = ch.replaceAll("(?m)^[ \t]*\r?\n", "");
              		Invariant inv = new Invariant(contextOfTheAttribute,ocl);
    				storeRes(res,inv,needExtraCollectionTreatment,p1);
				
				}
				}
				}
			}
			
			if(stereotype.getName().equals("from barchart"))
			{
				try{
				
				EList<org.eclipse.uml2.uml.internal.impl.EnumerationLiteralImpl > values = (EList<org.eclipse.uml2.uml.internal.impl.EnumerationLiteralImpl>)  getValue(att, stereotype,"categories");
				
				EEnum enumeration =null;
	
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
				EList<String>probabilities = (EList<String>) getValue(att, stereotype,"frequencies");
		
				String ocl ="";
				for (int i = 0; i < values.size(); i++) {
					if(i==0)
						ocl = "("+ getExpressionLiteral(p1,values.get(i),probabilities.get(i)) +")";
					else ocl = ocl + " or ("+ getExpressionLiteral(p1,values.get(i),probabilities.get(i)) +")";
					
				}
				
				Invariant inv = new Invariant(contextOfTheAttribute,ocl);
				storeRes(res,inv,needExtraCollectionTreatment,p1);
				}catch(Exception e)
				{e.printStackTrace();}
	
			}
			
	
			
	    }
	    }
		}
	        
	    
		return processRes(res,isMixed,att);
	    
	}
	
	
	public void storeRes (LinkedList<Invariant> res,Invariant inv, boolean needExtraCollectionTreatment, String p1)
	{
		
		if(needExtraCollectionTreatment==true)
		{
			String ch = inv.getexpression();
			ch=ch.replace(p1+ " = 0", p1.replace("->size()", ".oclIsUndefined() = true"));
			ch=ch.replace(p1+ " = 1", p1.replace("->size()", ".oclIsUndefined() = false"));
			inv.setexpression(ch);
		}
		res.add(inv);
	}

  private LinkedList<Invariant> processRes(LinkedList<Invariant> res, boolean isMixed, ENamedElement att)
  {
	  
	  if(res.size()>0)
	  {
		  Classifier tempClass = res.get(0).context;
		  if(tempClass.getName().contains("Income_Detail")&&att.getName().equals("amount"))
			  isMixed = false;
	  }  

	  if(isMixed==false)
	  { for (Invariant invariant : res) {
		invariant.setexpression(invariant.getexpression().replace("$$$$$", "true"));
	}
		  return res;
	    
	  }
	  else if(res.size()==0)
		  return res;
	  else
	  {
		
			  LinkedList<Invariant>  temp = new LinkedList<Invariant>();
			  for (int i = 0; i < res.size(); i++) {
				if(!res.get(i).getexpression().contains("$$$$$"))
				{
					
					temp.add(res.get(i));
					res.remove(i);
					i--;
				}
			}
			  
			  
			  
			 
			  String ch = "";
			  for (int i = 0; i < temp.size(); i++) {
					if(i==0)
						ch = "("+ temp.get(i).getexpression() +")";
					else ch = ch + " and ("+ temp.get(i).getexpression() +")";
					
				}
			  
			  if(ch.trim().equals(""))
				  ch="true";
			  
			  String exp="";
			  for (int i = 0; i <res.size()-1; i++) {

					  exp = res.get(i).getexpression().replace("$$$$$", res.get(i+1).getexpression());
					  res.get(i+1).setexpression(exp);
				  
			  }
			  
			  

			  Invariant combine = null;
			  if(res.size()>0)
			  {
			   combine = res.getLast();
			  exp = combine.getexpression().replace("$$$$$", ch);
			  combine.setexpression(exp);
			  res.clear();
			  res.add(combine);
			  }
			  else res.addAll(temp);
			  
			  return res;
	  }
			  }
			  
			  
		  
		  
	  
  
	
	
	
	private static String updateCasseString(String value, String type) {
		
     	if (type.equals("EString"))
			return "'"+value+"'";
     	else 
     		if (type.equals("String"))
    			return "'"+value+"'";
     		else
     		if (type.contains("EChar"))
     			return "'"+value.charAt(0)+"'";
    	if (type.contains("Char"))
 			return "'"+value.charAt(0)+"'";
    	else return value;
		
	}

	public LinkedList<EAttribute> getReleventAttributes(EClass eClass, LinkedList<EObject> all2)
	{
		LinkedList<EAttribute> res =new LinkedList<EAttribute>();
		for (int i = 0; i <all2.size(); i++) {
			if(all2.get(i) instanceof EAttribute)
			{
				
				if(isAttributeNeeded((EAttribute) all2.get(i),eClass))
					if(res.contains((EAttribute) all2.get(i))==false)
					res.add((EAttribute) all2.get(i));
			}
				
		}
		

		return res;
	}
	
	  public static boolean isTheContext(EClass under, EClass pere)
	  {
		  String className= pere.getName();
		  String objectClassName= under.getName();
		  if(className.equals(objectClassName))return true;
		  else
		  {EList<EClass> x = getAllSuperTypes(under);
		  
		  
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
	
	//cannot Cache this
		@SuppressWarnings("restriction")
		public static String getExpressionLiteral (String p1, org.eclipse.uml2.uml.internal.impl.EnumerationLiteralImpl value, String proba)
		{
		return proba.equals("0")? p1+" <> "+value.getEnumeration().getName()+"::"+value.getName():p1+" = "+value;
		}
	
	//cannot Cache this
	public static String getExpression (String p1, String value, String proba, Class contextOfTheConstraint, Class contextOfTheAttribute,String type)
	{
		value=updateCasseString(value,type);
		
		String res="";
		if( (value.trim().startsWith("[")&&value.trim().endsWith("]"))==false)
		{  return proba.equals("0")? p1+" <> "+treatSelf(value, contextOfTheConstraint, contextOfTheAttribute) :p1+" = "+treatSelf(value, contextOfTheConstraint, contextOfTheAttribute);}
		
		
		
		else
		{
			if(value.trim().contains(".."))
			{
				value=value.trim().replace("[", "").replace("]", "").replace("..", "-");
				
				String [] temp=value.split("-");
				double start=-1;
				double end=-1;
		
				 start= Double.valueOf(temp[0].trim());
				 end= Double.valueOf(temp[1].trim());
				
			
				
			return proba.equals("0")? "not (" +p1 + " >= "+treatSelf(String.valueOf(start), contextOfTheConstraint, contextOfTheAttribute)+" and " + p1+ " <= "+treatSelf(String.valueOf(end), contextOfTheConstraint, contextOfTheAttribute)+")" : p1 + " >= "+treatSelf(String.valueOf(start), contextOfTheConstraint, contextOfTheAttribute)+" and " + p1+ " <= "+treatSelf(String.valueOf(end), contextOfTheConstraint, contextOfTheAttribute);
				 
			}
			
			if(value.trim().contains("-"))
			{
				value=value.trim().replace("[", "").replace("]", "");
				
				String [] temp=value.split("-");
				double start=-1;
				double end=-1;
				 start= Double.valueOf(temp[0].trim());
				 end= Double.valueOf(temp[1].trim());
				return proba.equals("0")? "not (" + p1 + " >= "+treatSelf(String.valueOf(start), contextOfTheConstraint, contextOfTheAttribute)+" and " + p1+ " <= "+treatSelf(String.valueOf(end), contextOfTheConstraint, contextOfTheAttribute)+")":p1 + " >= "+treatSelf(String.valueOf(start), contextOfTheConstraint, contextOfTheAttribute)+" and " + p1+ " <= "+treatSelf(String.valueOf(end), contextOfTheConstraint, contextOfTheAttribute);
			}
			
			if(value.trim().contains(","))
			{	res = "";
				String [] temp=value.trim().replace("[", "").replace("]", "").split(",");
				
				if(!proba.equals("0"))
				{
				for (int i = 0; i < temp.length; i++) {
					
					if(i==0)
					res = p1+" = "+treatSelf(temp[i], contextOfTheConstraint, contextOfTheAttribute);
					else res = res + " or " + p1+" = "+treatSelf(temp[i], contextOfTheConstraint, contextOfTheAttribute);
				}
				}
				else for (int i = 0; i < temp.length; i++) {
					
					if(i==0)
					res = p1+" <> "+treatSelf(temp[i], contextOfTheConstraint, contextOfTheAttribute);
					else res = res + " and " + p1+" <> "+treatSelf(temp[i], contextOfTheConstraint, contextOfTheAttribute);
				}

				
			}
		}

		
		return res;
	}
	
	
	//cannot Cache this
	public static String getExpressionSpecial (String p1, String value, String proba, Class contextOfTheConstraint, Class contextOfTheAttribute,String type, ENamedElement att)
	{
		
		
		boolean isMany = false;
		if(att instanceof EReference)
		isMany =  ((EReference)att).isMany();
		if(att instanceof EAttribute)
		isMany =  ((EAttribute)att).isMany();
		
		if(isMany==false)
		return proba.equals("0")? "not "+p1+".oclIsKindOf("+value+")" :p1+".oclIsKindOf("+value+")";
		else return proba.equals("0")? "not "+p1+"->forAll(oclIsKindOf("+value+"))":p1+"->forAll(oclIsKindOf("+value+"))";
	
	
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
	
	public static void removeDuplicatInvariants(LinkedList<Invariant> list)
	{
		
		for (int i = 0; i < list.size()-1; i++) {
			for (int j = i+1; j < list.size(); j++) {
				
				if(list.get(j).equals(list.get(i)))
				{
					list.remove(j);
					j--;
				}
			}
		}
	}
	
	public void writeOperations(LinkedList<EOperation> listeEAllOperation, String path) {
		
		BufferedWriter writer = null;
	    final File f = new File("");
        final String dossierPath = f.getAbsolutePath().replace("\\", "//");
        String ch = dossierPath + "//" + path ; 
        ch =ch.replace("\\", "//"); 
	
	try
	{
		
	    writer = new BufferedWriter(new FileWriter(ch));
	    writer.write("import 'TaxCard.ecore'  \n");
	    writer.write("package TaxCard \n");
	    writer.newLine();
	
	    removeDuplicatOperations(listeEAllOperation);
	    LinkedList<EClass> contexts=getDiffrentContextsFromOperations(listeEAllOperation);
	    for (EClass class1 : contexts) {
	    	String header="";
	    	boolean treated = false;
		  LinkedList<EOperation> myOperations = getReleventEOperations(class1,listeEAllOperation);
		  
		  for (EOperation eOperation : myOperations) {
			  
			 
			  String body = getBody(eOperation);
			  if(!body.trim().equals(""))
			  {
				  	if(treated==false)
				  	{
				     header = "\n context "+ class1.getName();
					 writer.write(header);
					 writer.newLine();
					 treated=true;
				  	}
			  String params = getInputParams(eOperation);
			  String result = getReturn(eOperation);
			  writer.newLine();
			  header = "def: "+ eOperation.getName()+ "("+params+"):"+result+" = \n";
			  writer.write(header);
			  
			  writer.write(body.replace(")then", ") then"));
			  writer.newLine();
			  }
		}
		
		}	   
	
	   
	    writer.newLine();
		   writer.write("endpackage");
		    writer.newLine();
	
		   

	}
	catch ( IOException e)
	{
	}
	finally
	{
	    try
	    {
	        if ( writer != null)
	        writer.close( );
	     }
	    catch ( IOException e)
	    {
	    }
	}
	
	
}

	private String getReturn(EOperation eOperation) {
		String res="null";
		EClassifier temp = eOperation.getEType();
		res = EDatatypeUtil.convertFromTypeToString(temp);
		return res;
	}

	private String getInputParams(EOperation eOperation) {
		String res = "";
		
		EList<EParameter> listeParams = eOperation.getEParameters();
		for (int i = 0; i < listeParams.size(); i++) {
			EParameter param = listeParams.get(i);
			String type = EDatatypeUtil.convertFromTypeToString(param.getEType());
			if(i==0)
			res = param.getName()+":"+type ;
			else res = res + ", " + param.getName()+":"+type;
			
		}
		return res;
	}

	private String getBody(EOperation op) {
		String res="";
		EList<EAnnotation> annotationList = op.getEAnnotations();
		if (annotationList != null)
			for (EAnnotation ea : annotationList)
				if (ea.getSource().endsWith("Pivot"))
				{
				
					String val = ea.getDetails().get("body");	   
				    res = val.trim();							    
					
				}
		
		return res;
	}

	private LinkedList<EOperation> getReleventEOperations(EClass class1, LinkedList<EOperation> listeEAllOperation) {
		LinkedList<EOperation> res = new LinkedList<EOperation>();
		
		for (EOperation eOperation : listeEAllOperation) {
			EClass temp = eOperation.getEContainingClass();
			if(temp==class1)
			if(!res.contains(eOperation))
			res.add(eOperation);
			
		}
		return res;
	}

	public void writeMain (LinkedList<Invariant> invariants, String path)
	{
		BufferedWriter writer = null;
		BufferedWriter writerJava = null;
		BufferedWriter writerJavaAllFromScratch = null;
		BufferedWriter writerJavaAllFromExisting = null;
		    final File f = new File("");
	        final String dossierPath = f.getAbsolutePath().replace("\\", "//");
	        String ch = dossierPath + "//" + path ; 
	        ch =ch.replace("\\", "//"); 
		
		try
		{
			
		    writer = new BufferedWriter(new FileWriter(ch));
		    writerJava = new BufferedWriter(new FileWriter(ch.replace(".ocl", ".java")));
		    writerJavaAllFromScratch = new BufferedWriter(new FileWriter(ch.replace(".ocl", ".java1")));
		    writerJavaAllFromExisting = new BufferedWriter(new FileWriter(ch.replace(".ocl", ".java2")));
		    writer.write("import 'TaxCard.ecore'  \n");
		    writer.write("package TaxCard \n");
		    writer.newLine();
		    writeJavaHeader(writerJava,"All");
		    writeJavaHeader1(writerJavaAllFromScratch,"All");
		    writeJavaHeader2(writerJavaAllFromExisting,"All");
		    
		    removeDuplicatInvariants(invariants);
		    
		    LinkedList<Classifier> contexts=getDiffrentContexts(invariants);
		    int pos=1;
		    String cumul = "String query =";
		   for (Classifier class1 : contexts) {
			   LinkedList<String> invs = getReleventInvariants(invariants,class1);
			   for (String ocla : invs) {
				   if(!ocla.contains("->indexOf"))
				   {
				 String header = "context "+ class1.getName()+ " inv inv"+pos+": \n";
				 writer.write(header);
				 writer.write(ocla+"\n");
				 writer.newLine();
				 
				 String temp = header + " " + ocla;
				 String cleanQuery = temp.replaceAll("\\r|\\n", " ").replace(")then", ") then");

				 String JavaPro= "\n @Test \n"
				 		+ "public void testQuery"+(pos<10?"0"+pos:pos)+"() { \n"
				 		+ "ArrayList<ClassifierTuple> result = null; \n"
				 		+ "String query =\""+cleanQuery+"\"; \n"
				 		+ "result = test(1,1000, query, SearchAlgorithmEnum.AVM, umlFile,path); \n"
				 		+ "String str = this.verifyResult(query,result); \n"
				 		+ "assertEquals(\"true\", str); \n"
				 		+ "} \n";
				writerJava.write(JavaPro);
			 
				if(pos==1)
				cumul = cumul + "\""+cleanQuery+"\"";
				else cumul = cumul + "\n" + "+\" and "+cleanQuery+"\"";
				pos++; 
				   }
			}	   
		}
		   
		   cumul=cumul+";";
		   cumul = cumul.replaceAll(" +", " ");
		   cumul = cumul.replaceAll("\\s+(?=[^()]*\\))", " ");
		 
	
		   writerJavaAllFromExisting.write(cumul);
		   writerJavaAllFromScratch.write(cumul);
		   writeBody1(writerJavaAllFromExisting);
		   writeBody2(writerJavaAllFromScratch);
		   
		    writer.newLine();
			   writer.write("endpackage");
			    writer.newLine();
			    
			    writerJava.newLine();
			    writerJava.write("}");
			    writerJava.newLine();
			   

		}
		catch ( IOException e)
		{
		}
		finally
		{
		    try
		    {
		        if ( writer != null)
		        writer.close( );
		        if(writerJava!=null)
		        writerJava.close();
		        if(writerJavaAllFromScratch!=null)
		        	writerJavaAllFromScratch.close();
		        if(writerJavaAllFromExisting!=null)
		        	writerJavaAllFromExisting.close();
		        File f1 = new File(ch.replace(".ocl", ".java")) ;
		        File f2 = new File(ch.replace("constraints_from_profile.ocl", "Test_All_Gen.java")) ;
		        if(f1.exists())
		        	f1.renameTo(f2);
		        
		         f1 = new File(ch.replace(".ocl", ".java1")) ;
		         f2 = new File(ch.replace("constraints_from_profile.ocl", "Test_All_Gen_Combined.java")) ;
		        if(f1.exists())
		        	f1.renameTo(f2);
		        
		        f1 = new File(ch.replace(".ocl", ".java2")) ;
		        f2 = new File(ch.replace("constraints_from_profile.ocl", "Test_All_Gen_Combined_From_Existing.java")) ;
		        if(f1.exists())
		        	f1.renameTo(f2);        
		        
		    }
		    catch ( IOException e)
		    {
		    }
		}
		
		
	}
	
	public static void cloneAndCleanOCl(String oclFilePath, File oclFile) throws IOException
	{
		InputStream input = new FileInputStream(oclFilePath);
 		OutputStream output = new FileOutputStream(oclFilePath.replace(".ocl", "_temp.ocl"));	
 		IOUtils.copy(input, output);
		RandomAccessFile tools = new RandomAccessFile(oclFile, "rw"); 
		tools.readLine(); 
		long length = tools.length() - tools.getFilePointer(); 
		byte[] nexts = new byte[(int) length];
		tools.readFully(nexts); 
		tools.seek(0); 
		tools.write(nexts); 
		tools.setLength(nexts.length); 
		tools.close(); 
		
	}
	
	
    public static List<Invariant> extractInvariantsFromFileAsItIs(String oclFile1) {
    	    File fSource=new File("");
		    final String folderPath = fSource.getAbsolutePath();
		    File f=new File(folderPath+oclFile1);
		    List<Invariant> res = new LinkedList<Invariant>();
		     String line = null;
			 String bigQuery ="";
			 String lastQuery ="";

		    try {
		BufferedReader reader = new BufferedReader( new InputStreamReader(new FileInputStream(f.getAbsolutePath()), "UTF-8"));
		
	     while ((line = reader.readLine()) != null)
	        { line = line.trim();
	    	 if(line.startsWith("import ") || line.startsWith("package ") || line.startsWith("--") || line.equals("")|| line.equals("endpackage"))
	    		 continue;
	    	 
	    	 if(line.startsWith("context "))
	    	 {
	    		 if(!lastQuery.trim().equals(""))
	    		 {
	    		 lastQuery=lastQuery.replaceAll("\\r|\\n", " ");
	    		 lastQuery = lastQuery.replaceAll("\\s{2,}", " ").trim();
	    		 if(bigQuery.trim().equals(""))
	    		 {bigQuery = lastQuery;}
	    		 else bigQuery = bigQuery+" and "+lastQuery;
	    		 }
	    		 lastQuery = line;
	    	 }
	    	 else 
	    	 {
	    		 lastQuery=lastQuery+" "+line;
	    	 }
	        }
	     
		 if(!lastQuery.trim().equals(""))
		 {
		 lastQuery=lastQuery.replaceAll("\\r|\\n", " ");
		 lastQuery = lastQuery.replaceAll("\\s{2,}", " ").trim();
		 if(bigQuery.trim().equals(""))
		 {bigQuery = lastQuery;}
		 else bigQuery = bigQuery+" and "+lastQuery;
		 }
	     
	     
	  reader.close();
	} catch (Exception e) {
		e.printStackTrace();
	}
	
	
	if(bigQuery.toLowerCase().contains("context")){
		int occurance = bigQuery.split("context").length-1;
		if(occurance == 1){
			if (bigQuery.indexOf("inv") > -1) { 
				StringTokenizer st = new StringTokenizer(bigQuery, " ");
				String token = st.nextToken();
				if (token.equals("context")) {
					String contextClassName = st.nextToken();
					
					int beginIndex=bigQuery.indexOf(":");
					String expression=bigQuery.substring(beginIndex+1, bigQuery.length());	
					
					Invariant inv = new Invariant(OclAST.getClassByName(OclAST.allClasses,contextClassName), expression);
					res.add(inv);
				}
			}

		}
		
		else {  ArrayList<String> contextParts = EclipseOCLWrapper.getContextQueryParts(bigQuery);
				for(int i=1;i<contextParts.size();i++){
					String part = contextParts.get(i);
					if (part.indexOf("inv") > -1) { 
						StringTokenizer st = new StringTokenizer(part, " ");
						String token = st.nextToken();
						if (token.equals("context")) {
							String contextClassName = st.nextToken();
							
							int beginIndex=part.indexOf(":");
							String expression=part.substring(beginIndex+1, part.length());	
							
							Invariant inv = new Invariant(OclAST.getClassByName(OclAST.allClasses,contextClassName), expression);
							res.add(inv);
						}
					}
		}
		
	}
	}
	return res;
	}
    
    
    public static List<Invariant> extractInvariantsFromFileAsItIs(String oclFile1,UMLEnvironment env) {
	    File fSource=new File("");
	    final String folderPath = fSource.getAbsolutePath();
	    File f=new File(folderPath+oclFile1);
	    List<Invariant> res = new LinkedList<Invariant>();
	     String line = null;
		 String bigQuery ="";
		 String lastQuery ="";

	    try {
	BufferedReader reader = new BufferedReader( new InputStreamReader(new FileInputStream(f.getAbsolutePath()), "UTF-8"));
	
     while ((line = reader.readLine()) != null)
        { line = line.trim();
    	 if(line.startsWith("import ") || line.startsWith("package ") || line.startsWith("--") || line.equals("")|| line.equals("endpackage"))
    		 continue;
    	 
    	 if(line.startsWith("context "))
    	 {
    		 if(!lastQuery.trim().equals(""))
    		 {
    		 lastQuery=lastQuery.replaceAll("\\r|\\n", " ");
    		 lastQuery = lastQuery.replaceAll("\\s{2,}", " ").trim();
    		 if(bigQuery.trim().equals(""))
    		 {bigQuery = lastQuery;}
    		 else bigQuery = bigQuery+" and "+lastQuery;
    		 }
    		 lastQuery = line;
    	 }
    	 else 
    	 {
    		 lastQuery=lastQuery+" "+line;
    	 }
        }
     
	 if(!lastQuery.trim().equals(""))
	 {
	 lastQuery=lastQuery.replaceAll("\\r|\\n", " ");
	 lastQuery = lastQuery.replaceAll("\\s{2,}", " ").trim();
	 if(bigQuery.trim().equals(""))
	 {bigQuery = lastQuery;}
	 else bigQuery = bigQuery+" and "+lastQuery;
	 }
     
     
  reader.close();
} catch (Exception e) {
	e.printStackTrace();
}


if(bigQuery.toLowerCase().contains("context")){
	int occurance = bigQuery.split("context").length-1;
	if(occurance == 1){
		if (bigQuery.indexOf("inv") > -1) { 
			StringTokenizer st = new StringTokenizer(bigQuery, " ");
			String token = st.nextToken();
			if (token.equals("context")) {
				String contextClassName = st.nextToken();
				
				int beginIndex=bigQuery.indexOf(":");
				String expression=bigQuery.substring(beginIndex+1, bigQuery.length());	
				
				Invariant inv = new Invariant(TuplesUtil.getClassByName(contextClassName, env.getUMLClasses()), expression);
				res.add(inv);
			}
		}

	}
	
	else {  ArrayList<String> contextParts = EclipseOCLWrapper.getContextQueryParts(bigQuery);
			for(int i=1;i<contextParts.size();i++){
				String part = contextParts.get(i);
				if (part.indexOf("inv") > -1) { 
					StringTokenizer st = new StringTokenizer(part, " ");
					String token = st.nextToken();
					if (token.equals("context")) {
						String contextClassName = st.nextToken();
						
						int beginIndex=part.indexOf(":");
						String expression=part.substring(beginIndex+1, part.length());	
						
						Invariant inv = new Invariant(TuplesUtil.getClassByName(contextClassName, env.getUMLClasses()), expression);
						res.add(inv);
					}
				}
	}
	
}
}
return res;
}
	
    public static List<org.eclipse.ocl.ecore.Constraint> extractInvariantsFromFile(String oclFile1, Resource resource) {
    	
		File f=new File("");
		final String folderPath = f.getAbsolutePath();
		final String oclFilePath = folderPath + oclFile1; 
		EmfOclParser oclParser = new EmfOclParser();
 		File oclF=new File(oclFilePath.replace(".ocl", "_temp.ocl"));
 		try{
 		cloneAndCleanOCl (oclFilePath,oclF);  		
 		}catch (Exception e){e.printStackTrace();}
 		
		List<org.eclipse.ocl.ecore.Constraint> cList = oclParser.parseOclDocument(resource, oclF);
 		return cList;
	}

	public static String cleanExpression(String string) {
		String res = string;
		res = res.replace(".=", " =");
		res = res.replace(".-", " -");
		res = res.replace("->=", " = ");
		res = res.replace(".+", " +");
		res = res.replace("./", " /");
		res = res.replace(".*", " *");
		res = res.replace(".implies", " implies ");
		res = res.replace(".>", " >");
		res = res.replace(".<", " <");
		res = res.replace(".and", " and ");
		res = res.replace(".or", " or ");
		res = res.replace(".xor", " xor ");
		res = res.replace(".not", " = false ");
		res = res.replace(" not", " = false ");
		res = res.replace("()", "");
		res = res.replace(".(", " ( ");
		res = res.replace(".)", " ) ");
		res=res.replace("TaxCard::", "");
		res=res.replace("1 -()", "-1");
		res=res.replace("(1 -)", "-1");
		res=res.replace("(1)", "1");
		res=res.replace("(0)", "0");
		res=res.replace("(2)", "2");
		res=res.replace("(5)", "5");
		res=res.replace("(12)", "12");
		res=res.replace("(10)", "10");
		res=res.replace("(25)", "25");
		res=res.replace("(28)", "28");
		res=res.replace("(18)", "18");
		res=res.replace("(3.0)", "3");
		res=res.replace("(4.0)", "4");
		res=res.replace("(50)", "50");
		res=res.replace("=(2014)", "=2014");
		res=res.replace("=(1900)", "=1900");
		res=res.replace("('Not important')", "'Not important'");
		res=res.replace("(Disability_Types::NONE)", " Disability_Types::NONE");
		res=res.replace("(true)", "true");
		res=res.replace("(false)", "false");
		res=res.replace("->size ", "->size()");
		res=res.replace(".oclIsUndefined", ".oclIsUndefined()");
		res=res.replace("(self.disability_percentage <=1)", "self.disability_percentage <=1");
		res=res.replace("->asOrderedSet", "->asOrderedSet()");
		res=res.replace(".oclIsInvalid", ".oclIsInvalid()");
		res=res.replace("->collect(temp1 : Income_Detail | temp1.month)", ".month");
		res=res.replace("(if", "( if");
		res=res.replace(".allInstances->", ".allInstances()->");
		//res = freeFromParenthesis(res);
		return res;
	}
	

	
	private static String freeFromParenthesis(String ch) {
		String res = "";
		if(!ch.contains(")"))
		return ch;
		
		int toSkip=-1;
		boolean copy = true;
		for (int i = 0; i < ch.length(); i++) {
			
			if(copy)
			if(ch.charAt(i)=='(')
			if(ch.charAt(i+1)=='-'||Character.isDigit(ch.charAt(i+1)))
			{
				toSkip = i;
				copy = false;
			}
			
			if(!copy)
			if(ch.charAt(i)==')')
			{
				toSkip = i;
				copy = true;
			}
			if(toSkip!=i)
				res = res + ch.charAt(i);
				
		}
		
		return res;
	}

	public void writeUser (String path)
	{
		LinkedList<Invariant> invariants = new LinkedList<Invariant>();
		List<org.eclipse.ocl.ecore.Constraint> constraintsFromUser = extractInvariantsFromFile("//model//user_constraints.ocl",OclAST.reader.getResource());
		for (org.eclipse.ocl.ecore.Constraint constraint : constraintsFromUser) {
			Invariant temp = new Invariant((Class)getCorrespondant(constraint.getConstrainedElements().get(0),realClasses), cleanExpression(constraint.getSpecification().getBodyExpression().toString()));
			invariants.add(temp);
		}
		
		
		
		BufferedWriter writerJava = null;
		BufferedWriter writerJavaAllFromScratch = null;
		BufferedWriter writerJavaAllFromExisting = null;
		final File f = new File("");
	    final String dossierPath = f.getAbsolutePath().replace("\\", "//");
	    String ch = dossierPath + "//" + path ; 
	    ch =ch.replace("\\", "//"); 
		
		try
		{
			

		    writerJava = new BufferedWriter(new FileWriter(ch.replace(".ocl", ".java")));
		    writerJavaAllFromScratch = new BufferedWriter(new FileWriter(ch.replace(".ocl", ".java1")));
		    writerJavaAllFromExisting = new BufferedWriter(new FileWriter(ch.replace(".ocl", ".java2")));
		
		    writeJavaHeader(writerJava, "User");
		    writeJavaHeader1(writerJavaAllFromScratch, "User");
		    writeJavaHeader2(writerJavaAllFromExisting, "User");
		    
		    removeDuplicatInvariants(invariants);
		    
		    LinkedList<Classifier> contexts=getDiffrentContexts(invariants);
		    int pos=1;
		    String cumul = "String query =";
		   for (Classifier class1 : contexts) {
			   LinkedList<String> invs = getReleventInvariants(invariants,class1);
			   for (String ocla : invs) {
				 String header = "context "+ class1.getName()+ " inv inv"+pos+": \n";
				 
				 String temp = header + " " + ocla;
				 String cleanQuery = temp.replaceAll("\\r|\\n", " ").replace(")then", ") then");

				 String JavaPro= "\n @Test \n"
				 		+ "public void testQuery"+(pos<10?"0"+pos:pos)+"() { \n"
				 		+ "ArrayList<ClassifierTuple> result = null; \n"
				 		+ "String query =\""+cleanQuery+"\"; \n"
				 		+ "result = test(1,1000, query, SearchAlgorithmEnum.AVM, umlFile,path); \n"
				 		+ "String str = this.verifyResult(query,result); \n"
				 		+ "assertEquals(\"true\", str); \n"
				 		+ "} \n";
				writerJava.write(JavaPro);
			 
				if(pos==1)
				cumul = cumul + "\""+cleanQuery+"\"";
				else cumul = cumul + "\n" + "+\" and "+cleanQuery+"\"";
				pos++; 
			}	   
		}
		   
		   cumul=cumul+";";
		   cumul = cumul.replaceAll(" +", " ");
		   cumul = cumul.replaceAll("\\s+(?=[^()]*\\))", " ");
		   
		   writerJavaAllFromExisting.write(cumul);
		   writerJavaAllFromScratch.write(cumul);
		   writeBody1(writerJavaAllFromExisting);
		   writeBody2(writerJavaAllFromScratch);
			    
			    writerJava.newLine();
			    writerJava.write("}");
			    writerJava.newLine();
			   

		}
		catch ( IOException e)
		{
		}
		finally
		{
		    try
		    {
		        if(writerJava!=null)
		        writerJava.close();
		        if(writerJavaAllFromScratch!=null)
		        	writerJavaAllFromScratch.close();
		        if(writerJavaAllFromExisting!=null)
		        	writerJavaAllFromExisting.close();
		        File f1 = new File(ch.replace(".ocl", ".java")) ;
		        File f2 = new File(ch.replace("constraints_from_profile.ocl", "Test_User_Gen.java")) ;
		        if(f1.exists())
		        	f1.renameTo(f2);
		        
		         f1 = new File(ch.replace(".ocl", ".java1")) ;
		         f2 = new File(ch.replace("constraints_from_profile.ocl", "Test_User_Gen_Combined.java")) ;
		        if(f1.exists())
		        	f1.renameTo(f2);
		        
		        f1 = new File(ch.replace(".ocl", ".java2")) ;
		        f2 = new File(ch.replace("constraints_from_profile.ocl", "Test_User_Gen_Combined_From_Existing.java")) ;
		        if(f1.exists())
		        	f1.renameTo(f2);        
		        
		    }
		    catch ( IOException e)
		    {
		    }
		}
		
		
	}
	
	
	private void writeBody1(BufferedWriter writerJava) {


			try{
			writerJava.newLine();
			writerJava.newLine();
			writerJava.write("@Before \n");
			writerJava.write("public void testPopulateCTs(){\n");
			writerJava.write("	classifierTupleList = test(instanceFilePath,1,1000, query, SearchAlgorithmEnum.AVM, umlFile,path);}\n");
			writerJava.newLine();
			writerJava.newLine();
			writerJava.write("@Test\n ");
			writerJava.write("public void testQuery01() throws Exception{\n");
			writerJava.write("umlEnv = new UMLEnvironment();\n");
			writerJava.write("umlEnv.setUpEnvironment(umlFile);\n");
			writerJava.write("WriteToXmi writeToXmi= new WriteToXmi(umlEnv);\n");
			writerJava.write("ArrayList<ClassifierTuple> previousResult = writeToXmi.getNextResult(classifierTupleList);\n");
			writerJava.write("ArrayList<ClassifierTuple> result = test(1,1000, query, SearchAlgorithmEnum.AVM, umlFile,path,previousResult);\n");
			writerJava.write("String str = this.verifyResult(query,result);\n");
			writerJava.write("assertEquals(\"true\", str);\n");
			writerJava.write("writeToXmi.saveFromInstance();\n");
			writerJava.write("}\n}\n");
			
			writerJava.newLine();
			}catch(Exception w){}
		
				    
	}
	
	
	private void writeBody2(BufferedWriter writerJava) {


		try{
		writerJava.newLine();
		writerJava.newLine();
		writerJava.write("@Test \n");
		writerJava.write("public void testQuery01() throws Exception{\n");
		writerJava.write("ArrayList<ClassifierTuple> result = null; \n");
		writerJava.write("result = test(1,1000, query, SearchAlgorithmEnum.AVM, umlFile,path); \n");
		writerJava.write("String str = this.verifyResult(query,result);\n");
		writerJava.write("assertEquals(\"true\", str);\n");
		writerJava.write("}\n}\n");
		
		writerJava.newLine();
		}catch(Exception w){}
		 		    
}

	private void writeJavaHeader(BufferedWriter writerJava, String name) {
		try{
		writerJava.write("package test.snt.oclsolver.taxpayer.Gen; \n");
		writerJava.write("import static org.junit.Assert.assertEquals;\n");
		writerJava.write("import java.util.ArrayList;\n");
		writerJava.write("import org.junit.Test;\n");
		writerJava.write("import snt.oclsolver.search.SearchAlgorithmEnum;\n");
		writerJava.write("import snt.oclsolver.tuples.ClassifierTuple;\n");
		writerJava.write("import test.snt.oclsolver.AbstractTestCase;\n");
		writerJava.write("import snt.oclsolver.tuples2environment.UMLEnvironment;\n");
		
		writerJava.newLine();
		writerJava.write("public class Test_"+name+"_Gen extends AbstractTestCase { \n");
		writerJava.write("	String umlFile = \"examples/TaxPayer/TaxCard.uml\";\n");
		writerJava.write("	String path = \"ObjectDiagramModel\";\n");
		writerJava.newLine();
		}catch(Exception w){}
		
	}
	


	private void writeJavaHeader1(BufferedWriter writerJava, String name) {
		try{
		writerJava.write("package test.snt.oclsolver.taxpayer.Gen; \n");
		writerJava.write("import static org.junit.Assert.assertEquals;\n");
		writerJava.write("import java.util.ArrayList;\n");
		writerJava.write("import org.junit.Test;\n");
		writerJava.write("import snt.oclsolver.search.SearchAlgorithmEnum;\n");
		writerJava.write("import snt.oclsolver.tuples.ClassifierTuple;\n");
		writerJava.write("import test.snt.oclsolver.AbstractTestCase;\n");
		writerJava.write("import snt.oclsolver.tuples2environment.UMLEnvironment;\n");
		writerJava.newLine();
		writerJava.write("public class Test_"+name+"_Gen_Combined extends AbstractTestCase { \n");
		writerJava.write("	String umlFile = \"examples/TaxPayer/TaxCard.uml\";\n");
		writerJava.write("		String path = \"ObjectDiagramModel\";\n");
		writerJava.newLine();
		}catch(Exception w){}
		
	}

	




	
	private void writeJavaHeader2(BufferedWriter writerJava, String name) {
		try{
		writerJava.write("package test.snt.oclsolver.taxpayer.Gen; \n");
		writerJava.write("import static org.junit.Assert.assertEquals;\n");
		writerJava.write("import java.io.BufferedWriter; \n");
		writerJava.write("import java.io.File; \n");
		writerJava.write("import java.io.FileWriter; \n");
		writerJava.write("import java.util.ArrayList; \n");
		writerJava.write("import java.util.Collection; \n");
		writerJava.write("import org.junit.Before; \n");
		writerJava.write("import org.junit.Test;\n");
		writerJava.write("import snt.oclsolver.search.SearchAlgorithmEnum;\n");
		writerJava.write("import snt.oclsolver.writer.WriteToXmi;\n");
		writerJava.write("import snt.oclsolver.tuples.ClassifierTuple;\n");
		writerJava.write("import test.snt.oclsolver.AbstractTestCase;\n");
		writerJava.write("import snt.oclsolver.tuples2environment.UMLEnvironment;\n");
		writerJava.newLine();
		writerJava.write("public class Test_"+name+"_Gen_Combined_From_Existing extends AbstractTestCase { \n");
		writerJava.write("	String umlFile = \"examples/TaxPayer/TaxCard.uml\";\n");
		writerJava.write("	String path = \"ObjectDiagramModel\";\n");
		writerJava.write("String instanceFilePath=\"TaxpayersOfFD.xmi\";\n");
		writerJava.write("String instancesFromTuple=\"InstanceDiagramFromTuples.xmi\";\n");
		writerJava.write("Collection<Collection<ClassifierTuple>> classifierTupleList = null;\n");
		writerJava.write("UMLEnvironment umlEnv= null;\n");
		writerJava.newLine();
		
	
		}catch(Exception w){}
		
	}





	
	public static LinkedList<String> getReleventInvariants(List<Invariant> invariants, Classifier class1) {
		 LinkedList<String> res = new LinkedList<String>();
		for (Invariant inv : invariants) {
			if(inv.getContext().equals(class1))
			res.add(inv.getexpression());
		}
		 return res;
	}
	
	
	
	public static LinkedList<String> getReleventConstraints(List<org.eclipse.ocl.ecore.Constraint> allConstraints, Classifier class1) {
		 LinkedList<String> res = new LinkedList<String>();
		for (org.eclipse.ocl.ecore.Constraint cons : allConstraints) {
			if( ((EClass)cons.getConstrainedElements().get(0)).getName().equals(class1.getName()))
			{String initalCons=cons.getSpecification().getBodyExpression().toString();
			String cleanedCons=cleanExpression(initalCons);
			//TODO better parsing of the constraints
			if(cleanedCons.equals("self.disability_type = Disability_Types::NONE and (self.disability_percentage =0) or (self.disability_type <> Disability_Types::NONE and (self.disability_percentage >0) and self.disability_percentage <=1)"))
			cleanedCons = " (self.disability_type = Disability_Types::NONE and self.disability_percentage =0) or (self.disability_type <> Disability_Types::NONE and self.disability_percentage >0 and self.disability_percentage <=1)";
			
			//if(cleanedCons.trim().startsWith("let origin :"))
			//cleanedCons= "let origin : Physical_Person = self.reciver in let children1:Set(Dependent)=if(origin.oclIsKindOf(Tax_Payer)) then origin.oclAsType(Tax_Payer).dependents->select(allowances->size()>0) else Set{} endif in let union:Legal_Union_Record=origin.getLegalUnionRecord(2014) in let house: Household = union.household in let children:Set(Dependent)=if (house.oclIsInvalid()) then children1 else children1->union(house.children->select(allowances->size()>0)->select(allowances->any(true).reciver=origin)) endif in let eligible_children:Set(Dependent) = children->select(getAge(2014)>2) in let is_disabled:Boolean = self.person.disability_type<>Disability_Types::NONE and self.person.disability_percentage>0.5 in let dep_age:Integer = self.person.getAge(2014) in (dep_age<=2 and self.amount = 580) or (((dep_age>2)) and (eligible_children->size()=1) and self.amount = self.getAmount(185.60, dep_age, is_disabled)) or (((dep_age>2)) and ((eligible_children->size()<>1)) and (eligible_children->size()=2) and self.amount = self.getAmount(220.36, dep_age, is_disabled)) or (((dep_age>2)) and ((eligible_children->size()<>1)) and ((eligible_children->size()<>2)) and (eligible_children->size()>=3) and (self.amount =self.getAmount(267.59, dep_age, is_disabled)))";

			
			res.add(cleanedCons);
			}
		}
		 return res;
	}


	public static LinkedList<Classifier> getDiffrentContexts(List<Invariant> invariants) {
		LinkedList<Classifier> res = new LinkedList<Classifier>();
		for (Invariant inv : invariants) {
			if(!res.contains(inv.getContext()))
			res.add(inv.getContext());
		}
		
		res=removeDuplicatUMLClasses(res);
		return res;
		
	}
	
	
	public static LinkedList<Classifier> getDiffrentContextsFromConstraint(List<org.eclipse.ocl.ecore.Constraint> allConstraints) {
		LinkedList<Classifier> res = new LinkedList<Classifier>();
		for (org.eclipse.ocl.ecore.Constraint cons : allConstraints) {
			if(!res.contains((Classifier)getCorrespondant(cons.getConstrainedElements().get(0), realClasses)))
			res.add((Classifier)getCorrespondant(cons.getConstrainedElements().get(0), realClasses));
		}
		
		res=removeDuplicatUMLClasses(res);
		return res;
		
	}
	
	private LinkedList<EClass> getDiffrentContextsFromOperations(LinkedList<EOperation> operations) {
		LinkedList<EClass> res = new LinkedList<EClass>();
		for (EOperation op : operations) {
			EClass temp = op.getEContainingClass();
			if(temp!=null)
			if(!res.contains(temp))
			res.add(temp);
		}
		
		return res;
		
	}

	//cannot Cache this
	public static double Sum(LinkedList<Double> probabilities)
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
	
	public static String replaceBySelf(String toBeEvaluated,String name)
	{
		 String res="";

	 
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
	
		return res;
		}
	
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

	public void clean(LinkedList<Invariant> cons) {
		for (int i = 0; i < cons.size()-1; i++) {
			Invariant consI = cons.get(i);
			
			for (int j = i+1; j < cons.size(); j++) {
				Invariant consJ = cons.get(j);
			
				
				if(consI.getexpression().trim().equals(consJ.getexpression().trim())||consJ.getexpression().trim().replace("(", "").replace(")", "").replaceAll("\\s+","").equals(consI.getexpression().trim().replace("(", "").replace(")", "").replaceAll("\\s+",""))||consI.getexpression().trim().replace("(", "").replace(")", "").replaceAll("\\s+","").equals(consJ.getexpression().trim().replace("(", "").replace(")", "").replaceAll("\\s+","")))
				{
					boolean sameContext = consI.getContext().getName().equals(consJ.getContext().getName());
							
					if(sameContext)
					{
					cons.remove(j);
					j--;
					}
					else{
						boolean IfatherOfJ = superOf(consI.getContext(),consJ.getContext());
						boolean JFatherOfI = superOf(consJ.getContext(),consI.getContext());
						
						if(IfatherOfJ)
						{
							cons.remove(j);
							j--;
						}
						else {
							if(JFatherOfI)
							{
								Collections.swap(cons,  i,  j);
								cons.remove(j);
								j--;
							}
						}
					}
					
				}
			}
		}
		
	}

	private boolean superOf(Classifier pere, Classifier fils) {
		LinkedList<EClass> sons = getAllChildrenClasses(getEVersion(pere));
		for (EClass eClass : sons) {
			if(fils.getName().equals(eClass.getName()))
			return true;
		}
		return false;
	}



	public static boolean deleteDir(File dir) {
	    if (dir.isDirectory()) {
	        String[] children = dir.list();
	        for (int i = 0; i < children.length; i++) {
	            boolean success = deleteDir(new File(dir, children[i]));
	            if (!success) {
	                return false;
	            }
	        }
	    }

	    return dir.delete(); // The directory is empty now and can be deleted.
	}

	
	//TODO snapshot
	public void SnapShotHistograms(EPackage ePackage, EClass context, EReference callingRef,LinkedList<EObject> visited,EClass root) {

		
		
		if(callingRef!=null)
		{
			extractHistogramsFromReference(callingRef);
		}
		
		
		
		
		if(!visited.contains(context))
       {
   
    	   visited.add(context);

    	  
	    		 HistogramClass temp = new HistogramClass();
	    		 temp.setCondition("true");
	    		 temp.setRoot(context);
	    		 temp.setCallingRef(callingRef);
	    		 updateHistogramForClasses(temp,context,callingRef,context);
	    	
    	   LinkedList<EAttribute> attributes=null;
    	   attributes=getReleventAttributes(context, all); 
    	   
    	   //attributes
    	   for(EAttribute att: attributes)
    	   {
           
    		   if(!visited.contains(att))
    		   {
    		   visited.add(att);
    		   //Add hitograms from multiplicities 
    		   
    		   if(att.isDerived()==false)
	          	{
    			extractHistogramsFromAttribute(att);
	          	
	          	}
    	   }
    	   }
    	   
    	   
    	   //Explore
    	  EList<EClass> supers = context.getEAllSuperTypes();
    	  LinkedList<EClass> sup2 = getAllChildrenClasses(context);
    	  sup2.addAll(supers);
    	  sup2.add(context);
    	  LinkedList<EReference> conexions=getRefrences(context); 
    	  for (EReference eReference : conexions) {
    		  SnapShotHistograms(ePackage, (EClass)eReference.getEType(), eReference,visited,root);
    	  }

    	   for (EClass eClass : sup2) {
    		   SnapShotHistograms(ePackage, eClass, callingRef,visited,root);
  	    	   LinkedList<EReference> references=getRefrences(eClass);    
    	    	
    	    	for (EReference eReference : references) {
    	    		 SnapShotHistograms(ePackage, (EClass)eReference.getEType(), eReference,visited,root);
				}
    	    	
    	    	
    			}

       }
	
	}
	
	  @SuppressWarnings("unchecked")
	private void extractHistogramsFromAttribute(EAttribute att) {
if(att.getName().equals("is_widower"))
	return;

			if(!(att.getEType() instanceof EEnum))
			{
	        EList<Stereotype> relatedSteryotypes = sortSteryotypes(getAllSteryotypesFromModel(att));
	        Stereotype stereotype=null;
			for (int ixx = 0; ixx < relatedSteryotypes.size(); ixx++) {
	        	 stereotype=relatedSteryotypes.get(ixx);   	
	     		if(stereotype.getName().equals("from histogram"))
    			{
    				
    				EList<String> values = (EList<String>)  getValue(att, stereotype,"bins");
    				EList<String>probabilities = (EList<String>) getValue(att, stereotype,"frequencies");
    				boolean isOCL = (boolean)getValue(att, stereotype,"isOCL");
    				
    				if(isOCL == false)
    				{

    		    		 Histogram temp = new Histogram();
    		    		 temp.setCondition("true");
    		    		 temp.setElem(att);
    		    		 temp.setProbabilities(clone(probabilities));
    		    		 temp.setValues(values);
    		    		 if(values.size()>0)
    		    		 if(!alreadyStoredInAttributesHistograms(temp))
    		   	 		  hitogramsForAttributes.add(temp);
    				}
    			}
	     		
	    		if(stereotype.getName().equals("value dependency"))
    			{
	    			
	    			Class context = (Class)getValue(att, stereotype,"context");
	    			if(context!=null)
            		{
            			EList<Constraint> constraints=(EList<Constraint>)getValue(att, stereotype,"OCLtrigger");
            			for (int i = 0; (i < constraints.size()); i++) {
            				    Constraint constraint = constraints.get(i);

            				    EList<Stereotype> constraintSteryotypes = sortSteryotypes(getAllSteryotypesFromModel(constraint));
            			        Stereotype constraintStereotype=null;
            					for (int j = 0; j < constraintSteryotypes.size(); j++) {
            						constraintStereotype=constraintSteryotypes.get(j);   
            			        	 
            						if(constraintStereotype.getName().equals("from histogram"))
            						{
            				 Histogram temp = new Histogram();
            				 String ocl= constraint.getSpecification().stringValue().replaceAll("\\r|\\n", " ").trim();
            				 if(!ocl.trim().equals(""))
            					 temp.setCondition(ocl);
        			    		 else temp.setCondition("true");
           		    		 temp.setElem(att);
           		    		 temp.setContext(getEVersion(context));
           		  		EList<String> values = (EList<String>)  getValue(constraint, constraintStereotype,"bins");
        				EList<String>probabilities = (EList<String>) getValue(constraint, constraintStereotype,"frequencies");
        		
           		    		 temp.setProbabilities(clone(probabilities));
           		    		 temp.setValues(values);
           		    		 if(values.size()>0)
           		    		 if(!alreadyStoredInAttributesHistograms(temp))
           		   	 		  hitogramsForAttributes.add(temp);
            						}
            					}
    					}
            			
            			
            		
            		}
    			}
    			
			
			}
	        	
			}
			else 
			{
				 
					 

	    		EEnum enumeration = (EEnum)att.getEType();
	    		EList<EEnumLiteral> literals = enumeration.getELiterals();
	    		EList<String> probas = new  BasicEList<String>();
	    		EList<String> valuess = new  BasicEList<String>();
	    		boolean atLeastOne = false;
	    			if(literals.size()!=0)
	    			{
	    				for (int i = 0; i <literals.size(); i++) {
	    					
	    					EList<Stereotype> ster = getAllSteryotypesFromModel(literals.get(i));
	    				
	    					if(ster!=null)
	    					for (Stereotype stereotype : ster) {
	    						if(stereotype.getName().equals("probabilistic type"))
	    						{
	    						atLeastOne=true;
	    						valuess.add(enumeration.getName()+"::"+literals.get(i).getName());
	    						probas.add(String.valueOf(getValue(literals.get(i), stereotype,"frequency")));
	    						}
	    							
	    					}
	    				}
	    			}
	    			
	    			if(atLeastOne)
	    			{
	    				 Histogram temp = new Histogram();
    			    	 temp.setCondition("true");
       		    		 temp.setElem(att);
       		    		 temp.setContext(att.getEContainingClass());
       		    		 temp.setProbabilities(probas);
       		    		 temp.setValues(valuess);
       		    		 if(valuess.size()>0)
           		    		 if(!alreadyStoredInAttributesHistograms(temp))
           		   	 		  hitogramsForAttributes.add(temp);
            						
	    			}
			}
		
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
					if(stereotype.getName().equals("probabilistic type"))
					res.add(new Double(	String.valueOf(getValue(values.get(i), stereotype,"frequency"))));
						
				}
			}
		
		return res;	
		}
			
		}
		
	  @SuppressWarnings("unchecked")
	private void extractHistogramsFromReference(EReference ref) {
		  if(ref.getName().equals("incomes")||ref.getName().equals("taxPayer"))
				return;
	        EList<Stereotype> relatedSteryotypes = sortSteryotypes(getAllSteryotypesFromModel(ref));
	        Stereotype stereotype=null;
			for (int ixx = 0; ixx < relatedSteryotypes.size(); ixx++) {
	        	 stereotype=relatedSteryotypes.get(ixx);   	
	
     		
	    		if(stereotype.getName().equals("multiplicity"))
    			{
	    		
            			EList<Constraint> constraints=(EList<Constraint>) getValue(ref, stereotype,"constraint");
            			Class target =  (Class) getValue(ref, stereotype,"targetMember");
            			for (int i = 0; (i < constraints.size()); i++) {
            				    Constraint constraint = constraints.get(i);

            				    EList<Stereotype> constraintSteryotypes = sortSteryotypes(getAllSteryotypesFromModel(constraint));
            			        Stereotype constraintStereotype=null;
            					for (int j = 0; j < constraintSteryotypes.size(); j++) {
            						constraintStereotype=constraintSteryotypes.get(j);   
            			        	 
            						if(constraintStereotype.getName().equals("from histogram"))
            						{
            				 Histogram temp = new Histogram();
            				 String ocl= constraint.getSpecification().stringValue().replaceAll("\\r|\\n", " ").trim();
            				 if(!ocl.trim().equals(""))
            					 temp.setCondition(ocl);
        			    		 else temp.setCondition("true");
            				 if(ref.getEType().getName().equals(target.getName()))
           		    		 temp.setElem(ref);
            				 else temp.setElem(ref.getEOpposite());
           		  		EList<String> values = (EList<String>)  getValue(constraint, constraintStereotype,"bins");
        				EList<String>probabilities = (EList<String>) getValue(constraint, constraintStereotype,"frequencies");
        		
           		    		 temp.setProbabilities(clone(probabilities));
           		    		 temp.setValues(values);
           		
           		    		 if(values.size()>0)
           		    		 if(!alreadyStoredInRefrencesHistograms(temp))
           		   	 		  hitogramsForAssociations.add(temp);
            						}
            						
            						
            						if(constraintStereotype.getName().equals("value dependency"))
            						{
            							
            							
            						Class context = (Class)getValue(constraint, constraintStereotype,"context");
            							
            						if(context!=null)	
            						{
            							
            							
            							EList<Constraint> constraintsInner=(EList<Constraint>)getValue(constraint, stereotype,"OCLtrigger");
            	            			for (int ix = 0; (ix < constraintsInner.size()); ix++) {
            	            				    Constraint constraintInner = constraintsInner.get(ix);

            	            				    EList<Stereotype> constraintSteryotypesInner = sortSteryotypes(getAllSteryotypesFromModel(constraintInner));
            	            			        Stereotype constraintStereotypeInner=null;
            	            					for (int jx = 0; jx < constraintSteryotypesInner.size(); jx++) {
            	            						constraintStereotypeInner=constraintSteryotypesInner.get(jx);   
            	            			        	 
            	            						if(constraintStereotypeInner.getName().equals("from histogram"))
            	            						{
            	            				 Histogram temp = new Histogram();
            	            				 String ocl= constraintInner.getSpecification().stringValue().replaceAll("\\r|\\n", " ").trim();
            	            				 if(!ocl.trim().equals(""))
            	            					 temp.setCondition(ocl);
            	        			    		 else temp.setCondition("true");
            	            				 if(ref.getEType().getName().equals(target.getName()))
            	               		    		 temp.setElem(ref);
            	                				 else temp.setElem(ref.getEOpposite());
            	           		  		EList<String> values = (EList<String>)  getValue(constraintInner, constraintStereotypeInner,"bins");
            	        				EList<String>probabilities = (EList<String>) getValue(constraintInner, constraintStereotypeInner,"frequencies");
            	        		
            	           		    		 temp.setProbabilities(clone(probabilities));
            	           		    		 temp.setValues(values);
            	           		    		 temp.setContext(getEVersion(context));
            	           		    		 if(values.size()>0)
            	           		    		 if(!alreadyStoredInRefrencesHistograms(temp))
            	           		    			hitogramsForAssociations.add(temp);
            	            						}
            						}
            						}
            						
            					}
    					}

    			}
    			
			
			}
	        	
    			}
			}
		
	}

	  public static LinkedList<EClass> getDirectChildrenClasses(EClass c)
	  {
		  
		  
		  LinkedList<EClass> res= new LinkedList<EClass>();
		  if(c.isInterface()==false)
		  {
		  for (Generalization gen : listeGeneralization) {
			 if(containsByName(gen.getTargets(),c))
			{
				EList<Element> sources = gen.getSources();
				for (Element element : sources) {
					if(element instanceof Class)
					res.add((EClass)getEVersion((Class)element));
				}
			}
		}
		  }
		  else 
		  {
			  for (InterfaceRealization real : listeInterfaceRealization) {
					if(containsByName(real.getTargets(),c))
					{
						EList<Element> sources = real.getSources();
						for (Element element : sources) {
							if(element instanceof Class)
							  res.add((EClass)getEVersion((Class)element));
						}
					}
				}
			  
		  }
		  
	
		
			 return res;
	
	   }
	  
	  private static boolean containsByName(EList<Element> elements, EClass c)
	  {
		  boolean res = false;
		  int i = 0;
		  while (i<elements.size()&&res==false)
		  {   Element other = elements.get(i);
			  if(other instanceof Class)
			  { Class newOther= (Class) other;
			  if(newOther.getName().trim().equals(c.getName().trim()))
				  res=true;
			  }  
			  
			  if(other instanceof Interface)
			  { Interface newOther= (Interface) other;
			  if(newOther.getName().trim().equals(c.getName().trim()))
				  res=true;
			  }  
				  i++;
		  }
		  return res;
	  }
	  
	  
		private void updateHistogramForClassesDown(HistogramClass temp, EClass context, EReference callingRef) {

		      EClass c=context;
		      LinkedList<EClass> children = getDirectChildrenClasses(c);
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
			  
			 
				 if(c.isAbstract()==false && c.isInterface()==false)
				 {
				  double childrenProba=Sum(probabilities);
				  if(childrenProba<1)
				  {
				  values.add(c);
				  probabilities.add(1.0-childrenProba);
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
			  
			  if(values.size()>1)
			  {
			  temp.setProbabilities(clone1(probabilities));
			  temp.setValues(values);
	 		  if(!alreadyStoredInClassHistograms(temp))
	 		   hitogramsForClasses.add(temp);
	 		  
	 		 for (EClass child : children) {

	 	 		  HistogramClass temp1 = new HistogramClass();
	 	  		 temp1.setCondition("true");
	 	  		 temp1.setRoot(child);
	 	  		 temp1.setCallingRef(callingRef);
	 	  		 updateHistogramForClassesDown(temp1,child,callingRef);
			}
		  } 		
		}

	@SuppressWarnings("unchecked")
	private void updateHistogramForClasses(HistogramClass temp, EClass c1, EReference callingRef, EClass root) {
		
	      EClass c = EmfModelReader.getBaseC(c1);
	      if(c==null)
	      c=c1;
	      LinkedList<EClass> children = getDirectChildrenClasses(c);
	      
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
		  
		 
			 if(c.isAbstract()==false && c.isInterface()==false)
			 {
			  double childrenProba=Sum(probabilities);
			  if(childrenProba<1)
			  {
			  values.add(c);
			  probabilities.add(1.0-childrenProba);
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
		  
		  if(values.size()>1)
		  {
		  temp.setProbabilities(clone1(probabilities));
		  temp.setValues(values);
 		  if(!alreadyStoredInClassHistograms(temp))
 		   hitogramsForClasses.add(temp);
 		  
 		 for (EClass child : children) {

 	 		 HistogramClass temp1 = new HistogramClass();
 	  		 temp1.setCondition("true");
 	  		 temp1.setCallingRef(callingRef);
 	  		 temp1.setRoot(child);
 	  		 updateHistogramForClassesDown(temp1,child,callingRef);
		}
	  } 		
		  
		  
		  
		  if(callingRef!=null)
		  {
		  EList<Stereotype> sters = getAllSteryotypesFromModel(callingRef);
		  
		for (int i = 0; i <sters.size(); i++) {
			Stereotype stereotype=sters.get(i);
			
			
			if(stereotype.getName().equals("type dependency"))
			{
			
			EClass context = getEVersion( (Class)getValue(callingRef, stereotype,"context"));	
			EList<Constraint> constraints = (EList<Constraint>)getValue(callingRef, stereotype,"OCLtrigger");
			for (int j = 0; j < constraints.size(); j++) {

    		String ocl= constraints.get(j).getSpecification().stringValue().replaceAll("\\r|\\n", " ").trim();
    		
			EList<Stereotype> consSterys = getAllSteryotypesFromModel(constraints.get(j));
			EList<Class> values1 = (EList<Class>)  getValue(constraints.get(j), getSteryotypeByName(consSterys, "type dependency spec"),"possibleTypes");
			EList<String>probabilities1 = (EList<String>) getValue(constraints.get(j), getSteryotypeByName(consSterys, "type dependency spec"),"frequencies");
			double totalprob = Sum(convertProba(probabilities1));
					if(values1.size()!=probabilities1.size() )
					{;}
					else 
					{ 
					   if(totalprob!=1&&totalprob!=100)
						{;}
						
					else
					{

			    		 HistogramClass temp2 = new HistogramClass();
			    		 if(!ocl.trim().equals(""))
			    		 temp2.setCondition(ocl);
			    		 else temp2.setCondition("true");
			    		 temp2.setContext(context);
			    		 temp2.setRoot(root);
			    		 temp2.setCallingRef(callingRef);
			    	
			   		  if(values1.size()>0)
					  {
		
					  temp2.setProbabilities(convertProba(probabilities1));
					  temp2.setValues(convertClasses(values1));
			 		  if(!alreadyStoredInClassHistograms(temp2))
			 		   hitogramsForClasses.add(temp2);
		
				  } 		
					  
						
					}
					}
			
    		
				
		}
		 	
		}}
        	
		  
		  }
	}
	
	public boolean alreadyStoredInAttributesHistograms(Histogram hist)
	{
		boolean res = false;
		for (Histogram h : hitogramsForAttributes) {
			if(h.equals(hist))
			return true;
		}
		return res;
	}

	public boolean alreadyStoredInRefrencesHistograms(Histogram hist)
	{
		boolean res = false;
		for (Histogram h : hitogramsForAssociations) {
			if(h.equals(hist))
			return true;
		}
		return res;
	}
	
public boolean alreadyStoredInClassHistograms(HistogramClass hist)
{
	boolean res = false;
	for (HistogramClass h : hitogramsForClasses) {
		if(h.equals(hist))
		return true;
	}
	return res;
}

 public LinkedList<Double> convertProba(EList<String>probabilities)
 {
	 LinkedList<Double> res = new LinkedList<Double>();
	 for (String ch : probabilities) {
		 try {
			res.add(new Double(ch));
		} catch (Exception e) {
		e.printStackTrace();
		}
		
	}
	 return res;
 }
 
 public LinkedList<Double> clone1(LinkedList<Double> probabilities)
 {
	 LinkedList<Double> res = new LinkedList<Double>();
	 for (Double ch : probabilities) {
		 try {
			res.add(ch);
		} catch (Exception e) {
		e.printStackTrace();
		}
		
	}
	 return res;
 }
 
 
 public EList<String> clone(EList<String>probabilities)
 {
	 EList<String> res = new BasicEList<String>();
	 for (String ch : probabilities) {
		 try {
			res.add(new String (ch));
		} catch (Exception e) {
		e.printStackTrace();
		}
		
	}
	 return res;
 }
 
 public LinkedList<EClass> convertClasses(EList<Class> classes)
 {
	 LinkedList<EClass> res = new LinkedList<EClass>();
	 for (Class c : classes) {
		 try {
			res.add(getEVersion(c));
		} catch (Exception e) {
		e.printStackTrace();
		}
		
	}
	 return res;
 }

@SuppressWarnings("unused")
public void normelizeDist() {
for ( HistogramClass hist1 : hitogramsForClasses) {
	
	if(hist1.values.size()==1)
	{
		EClass c = hist1.values.get(0);
		LinkedList<EClass> temp = getBrothers(c);
		temp.remove(c);
		for (EClass eClass : temp) {
			hist1.values.add(eClass);
			hist1.probabilities.add(new Double(0));
		}
		
	}
	
	for (Double hist : hist1.probabilities) {
		hist1.frequencies.add(0);
		
	}
}

for (Histogram hist1 : hitogramsForAttributes) {
	for (String hist : hist1.probabilities) {
		hist1.frequencies.add(0);
		
	}
}

for (Histogram hist2 : hitogramsForAssociations) {
	for (String hist : hist2.probabilities) {
		hist2.frequencies.add(0);
		
	}
}

	
}

private  LinkedList<EClass> getBrothers(EClass c) {
	 LinkedList<EClass> res= new LinkedList<EClass>();
	  if(c.isInterface()==false)
	  {
	  for (Generalization gen : listeGeneralization) {
		 if(containsByName(gen.getSources(),c))
		{
			EList<Element> targets = gen.getTargets();
			for (Element element : targets) {
				if(element instanceof Class)
				res.addAll(getDirectChildrenClasses(getEVersion((Class)element)));
				
			}
		}
	}
	  }
	  else 
	  {
		  for (InterfaceRealization real : listeInterfaceRealization) {
				 if(containsByName(real.getSources(),c))
					{
						EList<Element> targets = real.getTargets();
						for (Element element : targets) {
							if(element instanceof Class)
							res.addAll(getDirectChildrenClasses(getEVersion((Class)element)));
						}
					}
			}
		  
	  }
	  return res;
	
}

//TODO Main
public void extractFromUml(Package ePackage, Classifier context, Property callingRef,LinkedList<Object> visited,Classifier root, LinkedList<Invariant> res, UMLEnvironment umlEnv) {
  
	
	if(!visited.contains(context)
			||(callingRef!=null)&&!visited.contains(callingRef.getType())
			)
   {
	   visited.add(context);
	   
	   
	   EList<Property> attributes = context.getAttributes(); 
	   EList<Property> conexions = new BasicEList<Property>();
	   
	   
	   //attributes
	   for(Property att: attributes)
	   {

		   if(TuplesUtil.getCorrAssocFromProp(att, umlEnv.getUMLAssociations())==null)
		   { Classifier c = TuplesUtil.getClassByName(att.getType().getName(), umlEnv.getUMLClasses());
			   if(c==null||c instanceof Enumeration)
			   {
		   if(!visited.contains(att))
		   {
		   visited.add(att);

		   if(att.isDerived()==false)
          	{  
			   if(att.isMultivalued())
			   {
				   int lower = att.getLower();
				   int upper = att.getUpper();   
				   if(lower!=0||upper!=-1)
				   {
						String ocl ="";
						if(lower == upper)
						ocl = "self."+att.getName()+"->size() = "+ upper;
						if(lower!=0&&upper!=-1)
						 ocl = "self."+att.getName()+"->size() >= "+ lower +" and "+ "self."+att.getName()+"->size() <= "+ upper;
						else if(lower!=0)
   						 ocl = "self."+att.getName()+"->size() >= "+ lower;
						else ocl = "self."+att.getName()+"->size() <= "+ upper;

		              	Invariant inv = new Invariant(context,ocl);
						storeRes(res,inv,false,"");
				   }
			   }
          	
          	}
	   }
			   }   else conexions.add(att); 
	   }
		   else conexions.add(att);   
	   }
	   
	   
	   
	   EList<Classifier> supers = context.getGenerals();
	   LinkedList<Classifier> sup2 = getAllChildrenClassesFromUML(context);
	   
	   sup2.addAll(supers);
	   sup2.add(context);
	   
	   if(context instanceof Interface)
		   sup2.addAll(TuplesUtil.getAllChildrenClasses(context, umlEnv.getUMLClasses(), new LinkedList<Classifier>()));
	   
	  for (Property eReference : conexions) {
		
			   int lower = eReference.getLower();
			   int upper = eReference.getUpper();
			   
			   if(lower!=0||upper!=-1)
			   {
					String ocl ="";
					if(eReference.isMultivalued())
					{
					if(lower == upper)
					ocl = "self."+eReference.getName()+"->size() = "+ upper;
					else if(lower!=0&&upper!=-1)
					 ocl = "self."+eReference.getName()+"->size() >= "+ lower +" and "+ "self."+eReference.getName()+"->size() <= "+ upper;
					else if(lower!=0)
					 ocl = "self."+eReference.getName()+"->size() >= "+ lower;
					else ocl = "self."+eReference.getName()+"->size() <= "+ upper;
					}
					else 
					{
						if(lower==1)
						ocl = "not self."+eReference.getName()+".oclIsUndefined()";
					}

					if(!ocl.equals(""))
					{
	              	Invariant inv = new Invariant(context,ocl);
					storeRes(res,inv,false,"");
					}
			   }
		   
	
	  }
	   
	     	   
	  
	   //Explore

	   for (Classifier eClass : sup2) {
	    	extractFromUml(ePackage, eClass, callingRef, visited, root, res,umlEnv);
	    	
	   	 //already sorted
	    	  LinkedList<Property> references=getRefrencesProperties(eClass);    
	    	
	    	for (Property eReference : references) {
	    		extractFromUml(ePackage, (Classifier) eReference.getType(), eReference, visited,root, res,umlEnv);

				   int lower = eReference.getLower();
				   int upper = eReference.getUpper();
				   
				   if(lower!=0||upper!=-1)
				   {
		
						String ocl ="";
						if(eReference.isMultivalued())
						{
						if(lower == upper)
						ocl = "self."+eReference.getName()+"->size() = "+ upper;
						else if(lower!=0&&upper!=-1)
						 ocl = "self."+eReference.getName()+"->size() >= "+ lower +" and "+ "self."+eReference.getName()+"->size() <= "+ upper;
						else if(lower!=0)
						 ocl = "self."+eReference.getName()+"->size() >= "+ lower;
						else ocl = "self."+eReference.getName()+"->size() <= "+ upper;
						}
						else 
						{
							if(lower==1)
							ocl = "not self."+eReference.getName()+".oclIsUndefined()";
						}

						if(!ocl.equals(""))
						{
		              	Invariant inv = new Invariant(eClass,ocl);
						storeRes(res,inv,false,"");
						}
				   }
			   
			}
	    	
	    	
			}
	   
	   
   
   }



}

public void writeInvariants(LinkedList<Invariant> invariants, String path, String pathOfPackage, Package pck) {
	
	BufferedWriter writer = null;
    final File f = new File("");
    final String dossierPath = f.getAbsolutePath().replace("\\", "//");
    String ch = dossierPath + "//" + path ; 
    ch =ch.replace("\\", "//"); 

try
{
	
    writer = new BufferedWriter(new FileWriter(ch));
    writer.write("import 'file:"+pathOfPackage+"'  \n");
    writer.write("package "+pck.getName()+" \n");
    writer.newLine();

    if(!invariants.isEmpty())
    {
    	int i = 0;
    	LinkedList<Classifier> contexts = getDiffrentContexts(invariants);
    for (Classifier class1 : contexts) {
    	String header="";
	   LinkedList<String> likeMeInv = getReleventInvariants(invariants, class1);
	  
	  for (String body : likeMeInv) {
		  if(!body.trim().equals(""))
		  {
			  	 i++;
			     header = "\n context "+ class1.getName()+" inv invGenMult"+i+":";
				 writer.write(header);
				 writer.newLine();
				 writer.write(body);
				 writer.newLine();
		  }
	}
	}	   
    }
   
    writer.newLine();
	   writer.write("endpackage");
	    writer.newLine();

}
catch ( IOException e)
{e.printStackTrace();}
finally
{
    try
    {
        if (writer != null)
        writer.close( );
     }
    catch ( IOException e)
    { e.printStackTrace();
    }
}


}
	
}
