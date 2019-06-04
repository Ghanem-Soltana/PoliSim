package Main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.BasicExtendedMetaData;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.ExtendedMetaData;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.ocl.ecore.Constraint;
import org.eclipse.ocl.ecore.OCL;
import org.eclipse.ocl.helper.OCLHelper;
import org.eclipse.ocl.utilities.ExpressionInOCL;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.internal.resource.UMLResourceFactoryImpl;
import org.eclipse.uml2.uml.resource.UMLResource;
import org.eclipse.uml2.uml.resources.util.UMLResourcesUtil;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.traverse.TopologicalOrderIterator;

import util.Config;
import util.CreateTaxpayers;
import util.EmfModelReader;
import util.EmfOclParser;
import util.Graphs;
import util.Invariant;
import util.OCLForJAVA;
import util.OclAST;
import util.OclDependencies;
import Monitor.DistCalculator;
import Monitor.Histogram;
import Monitor.HistogramClass;
import Monitor.ProfileConstraintExtractor;
import adapters.umlImpl.EPackageUMLAdapter;
import adapters.umlImpl.EResourceUMLAdapter;


@SuppressWarnings("restriction")
public class GenerateSeeds {
	public static boolean needSlicing=false;
	public String modelFolder;
	public static LinkedList<EClass> allClasses;
	public static EPackage pck;
	public static Model OriginalModel;
	public static DirectedGraph<String, DefaultEdge> g;
	public static boolean calculateDistances = false;
	public  static String model;
    final static String instances_rep="//Instances//TaxpayersOfFD.xmi";
    public  static LinkedList<EObject> roots ;
    
    public static void main(String args[])throws InterruptedException {

		int nb_runs = 1;

		try{
			nb_runs=Integer.valueOf(Config.getProperty("nbRuns", "dataConfig"));
			}catch(Exception e){e.printStackTrace();}
		
		try{
			needSlicing=Boolean.valueOf(Config.getProperty("needSlicing", "dataConfig"));
		}catch(Exception e){e.printStackTrace();}
		
		try{
			calculateDistances=Boolean.valueOf(Config.getProperty("calculateDistances", "dataConfig"));
		}catch(Exception e){e.printStackTrace();}
		

	  Thread t1 = new Thread(new PrepareEverything());
  	  t1.start();
  	  t1.join(60*500000);
    	
		long start = System.nanoTime(); 
	
		for (int i = 0; i < nb_runs; i++) {
				  System.out.println("Round number "+(i+1));
				  OCLForJAVA.newInstance();
		    	  Thread t = new Thread(new Caller());
		    	
		    	  t.start();
		    	  t.join(60*500000);
		    	  
		    	  if(i==nb_runs-1)
		    	  {
		    	  	long endTime = System.nanoTime();
		        	long duration = (endTime - start);
		        	double duration_second=(double)duration / 1000000000.0;	
		           System.err.println("execution time = "+Math.round(duration_second)+ " seconds");

		    	  }
	 
		}	  
		
		
		if(calculateDistances)
		{
			DisplayDistances();
	
		}
		
		
		
		
		
		
		
		System.out.println("Done!");
		
		
    }
    
    public static void DisplayDistances()
    {
    	System.out.println("\n*********Input distributions from the profile");
		System.out.println("****Histograms from class selection");
    	for (HistogramClass hist : ProfileConstraintExtractor.hitogramsForClasses) {
			System.out.println(hist);
		}
    	
    	System.out.println("****Histograms from attributes");
     	for (Histogram hist : ProfileConstraintExtractor.hitogramsForAttributes) {
			System.out.println(hist);
		}
     	
     	System.out.println("****Histograms from associations");

     	for (Histogram hist : ProfileConstraintExtractor.hitogramsForAssociations) {
			System.out.println(hist);
		}
    	System.out.println("*********\n");

     	
     	//TODO Here dist functions
        LinkedList<EObject> roots = readInstanes();
     	DistCalculator plotter = new DistCalculator(roots, ProfileConstraintExtractor.hitogramsForClasses, ProfileConstraintExtractor.hitogramsForAttributes, ProfileConstraintExtractor.hitogramsForAssociations);
		System.out.println("\n*********Calculated distributions from the generated population");
		System.out.println("****Histograms from class selection");
		LinkedList<HistogramClass> temp = plotter.saveClasses();
		for (HistogramClass hist :temp) {
			System.out.println(hist);
		}
		
		System.out.println("****Histograms from attributes");
		LinkedList<Histogram> temp1 = plotter.saveAttributes();
		for (Histogram hist :temp1) {
			System.out.println(hist);
		}
  
		LinkedList<Histogram> temp2 = plotter.saveAssociations();
		System.out.println("****Histograms from associations");
		for (Histogram hist :temp2) {
			System.out.println(hist);
		}
		
		System.out.println("*********\n");
		System.out.println("\n*********Euclidian distances");
		plotter.calculteDistancesClasses(temp);
		System.out.println("****Histograms from class selection");
		for (HistogramClass hist :temp) {
			System.out.println(hist);
			System.out.println(hist.probabilities);
			System.out.println("vs");
			System.out.println(plotter.getCorrependantClass(hist).probabilities);
			System.out.println("Distance ====> "+hist.distance);
		}
		
		plotter.calculteDistancesAttributes(temp1);
		System.out.println("****Histograms from attributes");
		for (Histogram hist :temp1) {
			System.out.println(hist);
			System.out.println(hist.probabilities);
			System.out.println("vs");
			System.out.println(plotter.getCorrependantAttribute(hist).probabilities);
			System.out.println("Distance ====> "+hist.distance);
		}
		
		plotter.calculteDistancesAssociations(temp2);
		System.out.println("****Histograms from associations");
		for (Histogram hist :temp2) {
			System.out.println(hist);
			System.out.println(hist.probabilities);
			System.out.println("vs");
			System.out.println(plotter.getCorrependantAssociation(hist).probabilities);
			System.out.println("Distance ====> "+hist.distance);
		}
		System.out.println("*********\n");
    }


	public static EStructuralFeature getEFeatureByName(EClass c, String name)
	{
		EList<EStructuralFeature> all = c.getEAllStructuralFeatures();
		for (EStructuralFeature eStructuralFeature : all) {
		if(eStructuralFeature.getName().trim().equals(name.trim()))
			return eStructuralFeature;
		}
	return null;
	}



	private static class Caller implements Runnable {
    	public static int count =0;
    	public static ResourceSet rs;
    
        public void run() {
        	count++;
        	
        	 String startinfPointName="Tax_Payer";
     		try{
     			startinfPointName=Config.getProperty("rootClass", "dataConfig");
  			}catch(Exception e){e.printStackTrace();}
  			
     		
        	if(count==1)
        	{
        	//final String modelFolder = "Models";
        	 String modelFolder = "Papyrus";
 			try{
 				modelFolder=Config.getProperty("modelFolder", "dataConfig");
 			}catch(Exception e){e.printStackTrace();}
 			
        	 
        	 
        	//final String modelFile= "paper.uml";
        	//final String modelFile= "testModel1.uml";
        	 String modelFile= "TaxCard.uml";
        	//final String startinfPointName="Person";
        	 
        		try{
        			modelFile=Config.getProperty("modelFile", "dataConfig");
     			}catch(Exception e){e.printStackTrace();}
     			
            	 
        	
        	//final String startinfPointName="TaxPayer";
        	//final String startinfPointName="Root";
        	//final String startinfPointName="TaxPayer";
        	
            	 
        	 
    	     model="//"+modelFolder+"//"+modelFile;
    	     String oclFile="//model//constraints.ocl";
    			try{
    				oclFile=Config.getProperty("OCLFileForSlicing", "dataConfig");
    			}catch(Exception e){e.printStackTrace();}
    		
    			try{
    				calculateDistances=Boolean.valueOf(Config.getProperty("calculateDistances", "dataConfig"));
    			}catch(Exception e){e.printStackTrace();}
    			

        	
        
    	
    	    
    	    File f=new File("");
    	    final String folderPath = f.getAbsolutePath();
            final String absolutePath = folderPath + model; 
            final String oclFilePath = folderPath + oclFile; 
             String ext = FilenameUtils.getExtension(absolutePath);      
             
       
             if (ext.equalsIgnoreCase("ecore"))
            		 {
            	         Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put(
               		    "ecore", new EcoreResourceFactoryImpl());
            		 }
             else if (ext.equalsIgnoreCase("uml")) 	{
            	    Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put(
                		    "uml", new UMLResourceFactoryImpl());
            	 
             } else{System.out.println("Unsupported extension");
             System.exit(0);
             }


          rs = new ResourceSetImpl(); 
          final ExtendedMetaData extendedMetaData = new BasicExtendedMetaData(rs.getPackageRegistry());
          rs.getLoadOptions().put(XMLResource.OPTION_EXTENDED_META_DATA,extendedMetaData);
          
          
          URI modelFileURI = URI.createFileURI(absolutePath);          
          Resource resource=null;
          if (ext.equalsIgnoreCase("ecore"))
           resource = loadEmfResource(modelFileURI,rs);
          else
        	  resource = loadUmlResource(modelFileURI,rs,"//Ecores//"+modelFile);
        	
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

     		reader.getPackages().get(0);
     		
    		
       		 System.out.println("Loading Model..");
     	   for(EClass c: reader.getClasses())
           {
     		   	   
        	   System.out.println("Class = " +c.getName());     
           }
           

     

     		//OCL parsing 

    		
     		OCL ocl = org.eclipse.ocl.ecore.OCL.newInstance();
     	    OCLHelper<EClassifier, EOperation, EStructuralFeature, Constraint> helper = ocl.createOCLHelper();
     	    OclAST.allEnumerations = (LinkedList<EEnum>) reader.getEnumerations();
            OclAST.allClasses = (LinkedList<EClass>) reader.getClasses();
            System.out.println("NB all classes = "+ OclAST.allClasses.size());
     	    OclAST.reader=reader;
            OclAST oclVisitor = OclAST.getInstance();     
            OclAST.listeEAllOperation = (LinkedList<EOperation>) reader.getAllOperations();
            OclAST.helper=helper;
            OclAST.ocl=ocl;
            if(needSlicing)
     		{
     
    		System.out.println("\n Sclicing");
     		EmfOclParser oclParser = new EmfOclParser();
     		File oclF=new File(oclFilePath.replace(".ocl", "_temp.ocl"));
     		try{
     		cloneAndCleanOCl (oclFilePath,oclF);  		
     		}catch (Exception e)
     		{e.printStackTrace();}
    		List<Constraint> cList = oclParser.parseOclDocument(resource, oclF);
            for (Constraint c : cList) {
              if (!c.getStereotype().equalsIgnoreCase("precondition") && !c.getStereotype().equalsIgnoreCase("postcondition")) {
                ExpressionInOCL<?, ?> oclExpression = (ExpressionInOCL<?, ?>) c.getSpecification();
            	EClass contextCls = (EClass) c.getConstrainedElements().get(0);
                helper.setContext(contextCls);        
                OclAST.addEClass(OclAST.listeEClass, contextCls); 
                oclExpression.accept(oclVisitor);
                
             
          
              }
            }
            
            System.out.println("NB classes reffered to = "+OclAST.listeEClass.size());
            System.out.println("NB enummuration classs used = "+OclAST.listeEnum.size());      
            System.out.println("NB attributes reffered to = " + OclAST.listeEAttribute.size());
            System.out.println("NB EReferences reffered to = " + OclAST.listeEReference.size());
            System.out.println("NB used operations = " + OclAST.listeEOperation.size());           
        
            System.out.println("*********Classes");
       
            
            for (EClass a: OclAST.listeEClass)
            	{System.out.println(a);
            	EList<EAttribute> myAtts = a.getEAllAttributes();
            	for (EAttribute eAttribute : myAtts) {
					if(eAttribute.isID())
					{OclAST.listeEAttribute.add(eAttribute);
					OclAST.orderedListe.add(eAttribute);
					}
				}
            	}
            System.out.println("*********Enumurations");
            for (EEnum b: OclAST.listeEnum)
            	{System.out.println(b);}
            System.out.println("*********Attributes");
            for (EAttribute c: OclAST.listeEAttribute)
            	{System.out.println(c);}
            System.out.println("*********References");
            for (EReference d: OclAST.listeEReference)
                {System.out.println(d);}
            System.out.println("*********Operations");
            for (EOperation d: OclAST.listeEOperation)
                {System.out.println(d);}
            
            System.out.println("*********The ordered list");
            for (EObject d: OclAST.orderedListe)
            {System.out.println(d);}
            
     		}
     		else {
     			OclAST.listeEClass.addAll(OclAST.allClasses);
     			OclAST.listeEnum.addAll(OclAST.allEnumerations);
     			for (EClass c : OclAST.allClasses) {
     				 OclAST.listeEAttribute.addAll(c.getEAllAttributes());
     				 OclAST.listeEReference.addAll(c.getEAllReferences());
				}
     			
     			removeDuplicatAttribute( OclAST.listeEAttribute);
     			OclAST.listeEOperation.addAll(OclAST.listeEAllOperation );
     			
     			
     			OclAST.orderedListe.addAll(OclAST.allClasses);
     			OclAST.orderedListe.addAll(OclAST.allEnumerations);
     			OclAST.orderedListe.addAll(OclAST.listeEAttribute);
     			OclAST.orderedListe.addAll(OclAST.listeEAllOperation);
     			OclAST.orderedListe.addAll(OclAST.listeEReference);
     			makeRootFirst(startinfPointName);
     		}
            

        	
        	} 
        	
            //Gen instances
        	
        	 
	
	        
            System.err.println("\n Generation started");
            int nbTaxpayers=250;
            
        	try{
        		nbTaxpayers=Integer.valueOf(Config.getProperty("nbObjectsPerRun", "dataConfig"));
    			}catch(Exception e){e.printStackTrace();}
    			
    		
            boolean toBeAppend = count!=1;
            LinkedList<EObject> instances=new LinkedList<EObject>();
           
    		
    	
    		CreateTaxpayers gen = new CreateTaxpayers(rs,count,pck,OriginalModel);
    			
    	     
    		
    			EClass startingPointClass= OclAST.getClassByName(OclAST.allClasses, startinfPointName);
            	if(count==1)
            	{
            	System.err.println("\n Dependencies Graph generation");
    		
    			
    			
    			System.out.println("\n Including OCL triggers to dependencies");
    	        for (EAttribute att: OclAST.listeEAttribute)
    	        	{gen.getNeedsForAtt(att);}
    	       for (EReference ref: OclAST.listeEReference)
          	  {System.out.println(ref);
    	    	   gen.getNeedsForRef(ref);}
    	        
    	        System.out.println( OclDependencies.attributesNeeds);
    	        g = Graphs.DomainToDependenciesGraph(pck,startingPointClass,gen);
    	        System.out.println( OclDependencies.attributesNeeds);
    	        for (DefaultEdge eObject : g.edgeSet()) {
    	        	System.out.println(eObject);
				}
   				Graphs.test(g);
   				CreateTaxpayers.orderIterator = new TopologicalOrderIterator<String, DefaultEdge>(g);
   	         
    	        Iterator<Entry<String, LinkedList<EObject>>> it = OclDependencies.refrencesNeeds.entrySet().iterator();
    	        while (it.hasNext()) {
    	        	Entry<String, LinkedList<EObject>> pairs = it.next();
    	        	LinkedList<EObject> ref_dep = pairs.getValue();
    	        	for (EObject temp : ref_dep) {
                		if(temp instanceof EClass)
                			OclAST.addEClass(OclAST.listeEClass, (EClass)temp);
                			if(temp instanceof EReference)
                				OclAST.addEReference(OclAST.listeEReference, (EReference)temp);
                				if(temp instanceof EAttribute)
                					OclAST.addEAttribute(OclAST.listeEAttribute, (EAttribute)temp);
                					if(temp instanceof EOperation)
                						OclAST.addEOperation(OclAST.listeEOperation, (EOperation)temp);
                						if(temp instanceof EEnum)
                							OclAST.addEEnum(OclAST.listeEnum, (EEnum)temp);
    	        	}
    	     
    	        
    	        }
    	        
    	        
    	        
    	         it = OclDependencies.attributesNeeds.entrySet().iterator();
    	        while (it.hasNext()) {
    	        	Entry<String, LinkedList<EObject>> pairs = it.next();
    	        	LinkedList<EObject> ref_dep = pairs.getValue();
    	        	for (EObject temp : ref_dep) {
                		if(temp instanceof EClass)
                			OclAST.addEClass(OclAST.listeEClass, (EClass)temp);
                			if(temp instanceof EReference)
                				OclAST.addEReference(OclAST.listeEReference, (EReference)temp);
                				if(temp instanceof EAttribute)
                					OclAST.addEAttribute(OclAST.listeEAttribute, (EAttribute)temp);
                					if(temp instanceof EOperation)
                						OclAST.addEOperation(OclAST.listeEOperation, (EOperation)temp);
                						if(temp instanceof EEnum)
                							OclAST.addEEnum(OclAST.listeEnum, (EEnum)temp);
    	        	}
    
    	        
    	        }
    	        
    	        
            	
      
                LinkedList<EClass> treated = new LinkedList<EClass>();
            	gen.clusterReferences(startingPointClass,treated, null);
            	gen.cleanReferences();
            	
            	System.out.println("Safe");
            	for (EReference ref : CreateTaxpayers.safeEReferences) {
					System.out.print(ref.getName()+"-");
				}
            	System.out.println("\nUnSafe");
            	for (EReference ref : CreateTaxpayers.unsafeEReferences) {
					System.out.print(ref.getName()+"/"+ref.getEType().getName()+"-");
					
				}
            	System.out.println("\nOpposite");
            	for (EReference ref : CreateTaxpayers.oppositeEReferences) {
					System.out.print(ref.getName()+"-");
				}
            	
            //	System.out.println();
            	
            	for (EClass eClass : treated) {
            		System.out.print(eClass.getName()+"-");
				}
            //	System.out.println();
            	for (EClass eClass : OclAST.listeEClass) {
            		System.out.print(eClass.getName()+"-");
				}

             	
            	}
            	
            	
           
            	
            	HashMap<EReference, LinkedList<EObject>> missing = new HashMap<EReference, LinkedList<EObject>>();
    			for (int j = 0; j < nbTaxpayers; j++) 
    			{System.out.println("Tax Case "+j);
    			
				instances.add(gen.generate(pck, "FD",startingPointClass,null,null,instances,startingPointClass,missing, new LinkedList<EObject>(),true));
    			CreateTaxpayers.count_Recursion = new HashMap<EReference, Integer>();
    			}
    			roots = instances;
    		
    	if(toBeAppend==false)
    		CreateTaxpayers.save("TaxpayersOf"+"FD",instances);
    		else
    			CreateTaxpayers.append("TaxpayersOf"+"FD", instances);
	
    }
        

		private void makeRootFirst(String startinfPointName) {
			for (int i = 0; i < OclAST.orderedListe.size(); i++) {
				
				if(OclAST.orderedListe.get(i) instanceof EClass)
					if(((EClass)OclAST.orderedListe.get(i)).getName().toLowerCase().trim().equals(startinfPointName.toLowerCase().trim()))
					if(i==0)
					return;
					else 
					{
						EClass temp = (EClass)OclAST.orderedListe.get(i);
						OclAST.orderedListe.remove(i);
						OclAST.orderedListe.add(0,temp);
						return;
					}
			}
			
		}
    }


    
    public static LinkedList<EObject> readInstanes()
    {
    	//prepare stand alone

		 final String instances_rep="//Instances//TaxpayersOfFD.xmi";
		 
		    File f=new File("");
		    final String folderPath = f.getAbsolutePath();
	      
	     
		

		//Loading instances
		System.out.println("Loading Instances..");
		ResourceSet load_resourceSet = new ResourceSetImpl();
		load_resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());
		load_resourceSet.getPackageRegistry().put(OclAST.reader.getPackages().get(0).getNsURI(),OclAST.reader.getPackages().get(0));
		Resource load_resource = load_resourceSet.getResource(URI.createURI(folderPath+instances_rep),true);
		EList<EObject> elements= load_resource.getContents();
		OCLForJAVA.init("FD",elements.get(0));
		String tempOCL= "Tax_Payer.allInstances()->any(true)";
		EObject taxpayer = OCLForJAVA.evaluateEObject(elements.get(0), tempOCL, "taxpayer","Tax_Payer"); 
		OCLForJAVA.init("FD",taxpayer);
		String ocla = "let p1:Set(Tax_Case)= Household.allInstances() in "
				+ "let p2:Set(Tax_Case)=Tax_Payer.allInstances()->select(getSpouse(from_agent.taxation_year).oclIsInvalid())->asSet() in " 
				+ "let all:Set(Tax_Case) = p1->union(p2) in all";
		Collection<EObject> taxpayersCollection = OCLForJAVA.evaluateECollection(taxpayer,ocla,"all","Tax_Case","OrderedSet"); 
		LinkedList<EObject> roots = new LinkedList<EObject>();
		Iterator<EObject> it = taxpayersCollection.iterator();
		while(it.hasNext())
		{roots.add(it.next());}
		return roots;
    }
    
    public static void removeDuplicatREderences(LinkedList<EReference> list)
 	{
 		 
 		LinkedHashSet<EReference> s = new LinkedHashSet<EReference>(list);
 		list.clear();
 		list.addAll(s);
 	}
    public static void removeDuplicatAttribute(LinkedList<EAttribute> list)
	{
		 
		LinkedHashSet<EAttribute> s = new LinkedHashSet<EAttribute>(list);
		list.clear();
		list.addAll(s);
	}
    
    public static void generateEcoreModel(String name, EPackage pck){
        
        ResourceSetImpl resourceSet = new ResourceSetImpl(); 
        resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl()); 
        Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl());
        final ExtendedMetaData extendedMetaData = new BasicExtendedMetaData(resourceSet.getPackageRegistry());
        resourceSet.getLoadOptions().put(XMLResource.OPTION_EXTENDED_META_DATA,extendedMetaData);

        final File f = new File("");
        final String dossierPath = f.getAbsolutePath().replace("\\", "//");
        final String apiSamplePath = dossierPath +"//Models//"; 
        final String file_name=name;
        String path = "file://" + apiSamplePath + file_name;
        URI uri = URI.createURI(path); 
        Resource resource = resourceSet.createResource(uri); 
        resource.getContents().add(pck); 

        
        try {
    		resource.save(null);
    		cleanFileForDerived(file_name);
    		
    		
    	} catch (IOException e) {
    		cleanFileForDerived(file_name);
    	} 
        
    
    }

	private static void cleanFileForDerived(String file_name) {
	
		 
		 File inputFile = new File("Models"+file_name);
	       File tempFile = new File("Models//Ecores//temp.ecore");
try{
	   BufferedReader reader = new BufferedReader(new FileReader(inputFile));
	   BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));


	   String lineToRemove1 = "transient=\"true\"";
	   String lineToRemove2 = "volatile=\"true\"";
	   String lineToRemove3 = "readonly=\"true\"";
	   String currentLine;

	 while((currentLine = reader.readLine()) != null)
	 {
	   
	   
	   writer.write(currentLine.replace(lineToRemove1, " transient=\"false\" ").replace(lineToRemove2, " volatile=\"false\" ").replace(lineToRemove3, " readonly=\"false\" "));
	   writer.newLine();
	 }
		    
		    reader.close();
		    writer.close();
		    inputFile.delete();
		    tempFile.renameTo(inputFile);


}catch(Exception e){}
	}

	public static Resource loadUmlResource (URI modelUri,ResourceSet rSet,String modelFile) {
		final URL umlJarFileLocation = org.eclipse.uml2.uml.resources.ResourcesPlugin.class.getResource("ResourcesPlugin.class");
		String umlJarPath = umlJarFileLocation.toString();
		umlJarPath = umlJarPath.substring(0, umlJarPath.indexOf('!'));
		final Map<URI, URI> uriMap = URIConverter.URI_MAP;
		uriMap.put(URI.createURI(UMLResource.LIBRARIES_PATHMAP), URI.createURI(umlJarPath + "!/libraries/"));
		uriMap.put(URI.createURI(UMLResource.METAMODELS_PATHMAP), URI.createURI(umlJarPath + "!/metamodels/"));
		
		

		UMLResourcesUtil.init(rSet);
		rSet.getPackageRegistry().put(UMLPackage.eNS_URI, UMLPackage.eINSTANCE);
		rSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(UMLResource.FILE_EXTENSION, UMLResource.Factory.INSTANCE);
		UMLPackage.eINSTANCE.eClass();

		UMLResource r = (UMLResource)rSet.getResource(modelUri, true);
		GenerateSeeds.OriginalModel=(Model)r.getContents().get(0);
		EcoreUtil.resolveAll(r);	
		UMLResourcesUtil.init(rSet);
		EResourceUMLAdapter r1= new EResourceUMLAdapter(r);
	    pck =((EPackageUMLAdapter)r1.getContents().get(0)).getPck();
		generateEcoreModel(modelFile.replace(".uml", ".ecore"),pck);
		r.getContents().clear();
		r.getContents().add(pck);
		EcoreUtil.resolveAll(r);	
		UMLResourcesUtil.init(rSet);
		return r;

	}
	
	public static Resource loadEmfResource(URI modelFileURI,ResourceSet rSet) {
		return  rSet.getResource(modelFileURI, true);
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
	
	public static LinkedList<EObject> loadInstances(EmfModelReader reader)
	{
	     //Loading instances
		 
	     File f=new File("");
	     String folderPath = f.getAbsolutePath();
	     final String instances_rep="//Instances//TaxpayersOfFD.xmi";
	     System.out.println("Loading Instances..");
	 	 ResourceSet load_resourceSet = new ResourceSetImpl();
	 	 load_resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());
	 	 load_resourceSet.getPackageRegistry().put(reader.getPackages().get(0).getNsURI(),reader.getPackages().get(0));
	 	 Resource load_resource = load_resourceSet.getResource(URI.createURI(folderPath+instances_rep),true);
	 	 EList<EObject> elements= load_resource.getContents();
	   	 OCLForJAVA.init("FD",elements.get(0));
	 	 String tempOCL= "Tax_Payer.allInstances()->any(true)";
		 EObject taxpayer = OCLForJAVA.evaluateEObject(elements.get(0), tempOCL, "taxpayer","Tax_Payer"); 
	   	 OCLForJAVA.init("FD",taxpayer);
	 	 Collection<EObject> taxpayersCollection = OCLForJAVA.evaluateECollection(taxpayer," let all:Set(Tax_Payer)= Tax_Payer.allInstances()in all->select(oclIsKindOf(Tax_Payer))->sortedBy(incomes->sortedBy(income_amount)->last().income_amount)","taxpayers","Tax_Payer","OrderedSet"); 
	 	 LinkedList<EObject> taxpayers = new LinkedList<EObject>();
	     Iterator<EObject> it = taxpayersCollection.iterator();
	 	 while(it.hasNext())
	 	 {
	 		taxpayers.add(it.next());
	 	 }
	 	 return taxpayers;
	}
	
    private static class PrepareEverything implements Runnable {
    	public static int count =0;
    	public static ResourceSet rs;
    
        public void run() {
        	 count++;
        	 String startinfPointName="Tax_Payer";
     		try{
     			startinfPointName=Config.getProperty("rootClass", "dataConfig");
  			}catch(Exception e){e.printStackTrace();}
  			
     		
        	if(count==1)
        	{String modelFolder = "Papyrus";
 			try{
 				modelFolder=Config.getProperty("modelFolder", "dataConfig");
 			}catch(Exception e){e.printStackTrace();}
        	 String modelFile= "TaxCard.uml";
        	 
        		try{
        			modelFile=Config.getProperty("modelFile", "dataConfig");
     			}catch(Exception e){e.printStackTrace();}
   	 
    	     model="//"+modelFolder+"//"+modelFile;
    	     String oclFile="//model//constraints.ocl";
    			try{
    				oclFile=Config.getProperty("OCLFileForSlicing", "dataConfig");
    			}catch(Exception e){e.printStackTrace();}
    		
    			try{
    				calculateDistances=Boolean.valueOf(Config.getProperty("calculateDistances", "dataConfig"));
    			}catch(Exception e){e.printStackTrace();}
	    
    	    File f=new File("");
    	    final String folderPath = f.getAbsolutePath();
            final String absolutePath = folderPath + model; 
            final String oclFilePath = folderPath + oclFile; 
             String ext = FilenameUtils.getExtension(absolutePath);      
             
       
             if (ext.equalsIgnoreCase("ecore"))
            		 {
            	         Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put(
               		    "ecore", new EcoreResourceFactoryImpl());
            		 }
             else if (ext.equalsIgnoreCase("uml")) 	{
            	    Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put(
                		    "uml", new UMLResourceFactoryImpl());
            	 
             } else{System.out.println("Unsupported extension");
             System.exit(0);
             }


          rs = new ResourceSetImpl(); 
          final ExtendedMetaData extendedMetaData = new BasicExtendedMetaData(rs.getPackageRegistry());
          rs.getLoadOptions().put(XMLResource.OPTION_EXTENDED_META_DATA,extendedMetaData);
          
          
          URI modelFileURI = URI.createFileURI(absolutePath);          
          Resource resource=null;
          if (ext.equalsIgnoreCase("ecore"))
           resource = loadEmfResource(modelFileURI,rs);
          else
        	  resource = loadUmlResource(modelFileURI,rs,"//Ecores//"+modelFile);
        	
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

     		reader.getPackages().get(0);
     		
    		
       		 System.out.println("Loading Model..");
     	   for(EClass c: reader.getClasses())
           {
     		   	   
        	   System.out.println("Class = " +c.getName());     
           }
           

    		
     		OCL ocl = org.eclipse.ocl.ecore.OCL.newInstance();
     	    OCLHelper<EClassifier, EOperation, EStructuralFeature, Constraint> helper = ocl.createOCLHelper();
     	    OclAST.allEnumerations = (LinkedList<EEnum>) reader.getEnumerations();
            OclAST.allClasses = (LinkedList<EClass>) reader.getClasses();
            System.out.println("NB all classes = "+ OclAST.allClasses.size());
     	    OclAST.reader=reader;
            OclAST oclVisitor = OclAST.getInstance();     
            OclAST.listeEAllOperation = (LinkedList<EOperation>) reader.getAllOperations();
            OclAST.helper=helper;
            OclAST.ocl=ocl;
            if(needSlicing)
     		{
     
    		System.out.println("\n Sclicing");
     		EmfOclParser oclParser = new EmfOclParser();
     		File oclF=new File(oclFilePath.replace(".ocl", "_temp.ocl"));
     		try{
     		cloneAndCleanOCl (oclFilePath,oclF);  		
     		}catch (Exception e)
     		{e.printStackTrace();}
    		List<Constraint> cList = oclParser.parseOclDocument(resource, oclF);
            for (Constraint c : cList) {
              if (!c.getStereotype().equalsIgnoreCase("precondition") && !c.getStereotype().equalsIgnoreCase("postcondition")) {
                ExpressionInOCL<?, ?> oclExpression = (ExpressionInOCL<?, ?>) c.getSpecification();
            	EClass contextCls = (EClass) c.getConstrainedElements().get(0);
                helper.setContext(contextCls);        
                OclAST.addEClass(OclAST.listeEClass, contextCls); 
                oclExpression.accept(oclVisitor);
                
             
          
              }
            }
            
            System.out.println("NB classes reffered to = "+OclAST.listeEClass.size());
            System.out.println("NB enummuration classs used = "+OclAST.listeEnum.size());      
            System.out.println("NB attributes reffered to = " + OclAST.listeEAttribute.size());
            System.out.println("NB EReferences reffered to = " + OclAST.listeEReference.size());
            System.out.println("NB used operations = " + OclAST.listeEOperation.size());           
        
            System.out.println("*********Classes");
       
            
            for (EClass a: OclAST.listeEClass)
            	{System.out.println(a);
            	EList<EAttribute> myAtts = a.getEAllAttributes();
            	for (EAttribute eAttribute : myAtts) {
					if(eAttribute.isID())
					{OclAST.listeEAttribute.add(eAttribute);
					OclAST.orderedListe.add(eAttribute);
					}
				}
            	}
            System.out.println("*********Enumurations");
            for (EEnum b: OclAST.listeEnum)
            	{System.out.println(b);}
            System.out.println("*********Attributes");
            for (EAttribute c: OclAST.listeEAttribute)
            	{System.out.println(c);}
            System.out.println("*********References");
            for (EReference d: OclAST.listeEReference)
                {System.out.println(d);}
            System.out.println("*********Operations");
            for (EOperation d: OclAST.listeEOperation)
                {System.out.println(d);}
            
            System.out.println("*********The ordered list");
            for (EObject d: OclAST.orderedListe)
            {System.out.println(d);}
            
     		}
     		else {
     			OclAST.listeEClass.addAll(OclAST.allClasses);
     			OclAST.listeEnum.addAll(OclAST.allEnumerations);
     			for (EClass c : OclAST.allClasses) {
     				 OclAST.listeEAttribute.addAll(c.getEAllAttributes());
     				 OclAST.listeEReference.addAll(c.getEAllReferences());
				}
     			
     			removeDuplicatAttribute( OclAST.listeEAttribute);
     			OclAST.listeEOperation.addAll(OclAST.listeEAllOperation );
     			
     			
     			OclAST.orderedListe.addAll(OclAST.allClasses);
     			OclAST.orderedListe.addAll(OclAST.allEnumerations);
     			OclAST.orderedListe.addAll(OclAST.listeEAttribute);
     			OclAST.orderedListe.addAll(OclAST.listeEAllOperation);
     			OclAST.orderedListe.addAll(OclAST.listeEReference);
     		}
            
   	} 
        	
           
    			
    	     
    		
    			EClass startingPointClass= OclAST.getClassByName(OclAST.allClasses, startinfPointName);
            	
            	
             	ProfileConstraintExtractor extractor = new ProfileConstraintExtractor(rs,count,pck,OriginalModel) ;
            	LinkedList<EObject> instancesTemp = new LinkedList<EObject>();
            	LinkedList<Invariant> cons = new LinkedList<Invariant>();
            	extractor.extract(pck,startingPointClass,null,instancesTemp ,startingPointClass,cons);
            	extractor.clean(cons);
            	extractor.writeMain(cons,"//model//constraints_from_profile.ocl");
            	extractor.writeUser("//model//constraints_from_profile.ocl");
            	extractor.writeOperations(OclAST.listeEAllOperation,"//model//operations.ocl");
            	instancesTemp = new LinkedList<EObject>();
            	extractor.SnapShotHistograms(pck,startingPointClass,null,instancesTemp ,startingPointClass);
            	extractor.normelizeDist();
	}
            	
            	
           
            	
      
    	
    	
    }
        
	
	
}