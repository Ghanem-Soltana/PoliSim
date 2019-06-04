package temp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
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
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.BasicExtendedMetaData;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.ExtendedMetaData;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMLResourceFactoryImpl;
import org.eclipse.ocl.ecore.Constraint;
import org.eclipse.ocl.ecore.OCL;
import org.eclipse.ocl.helper.OCLHelper;
import org.eclipse.ocl.utilities.ExpressionInOCL;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.internal.resource.UMLResourceFactoryImpl;
import org.eclipse.uml2.uml.resource.UMLResource;
import org.eclipse.uml2.uml.resources.util.UMLResourcesUtil;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.traverse.TopologicalOrderIterator;

import util.CreateTaxpayers;
import util.EmfModelReader;
import util.EmfOclParser;
import util.Graphs;
import util.OclAST;
import util.OclDependencies;
import adapters.umlImpl.EPackageUMLAdapter;
import adapters.umlImpl.EResourceUMLAdapter;



@SuppressWarnings("restriction")
public class Main {
	
	public static DirectedGraph<String, DefaultEdge> g;
	public static void main(String[] args) {
		System.out.println("THis code is out of date ");
		System.exit(0);
		//Load ecore/uml model 
	    final String model="//Papyrus//TaxCard.uml";
	    final String oclFile="//model//constraints.ocl";
	    
	    
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


      ResourceSet rs = new ResourceSetImpl(); 
      final ExtendedMetaData extendedMetaData = new BasicExtendedMetaData(rs.getPackageRegistry());
      rs.getLoadOptions().put(XMLResource.OPTION_EXTENDED_META_DATA,extendedMetaData);
      
      
      URI modelFileURI = URI.createFileURI(absolutePath);          
      Resource resource=null;
      if (ext.equalsIgnoreCase("ecore"))
       resource = loadEmfResource(modelFileURI,rs);
      else
    	  resource = loadUmlResource(modelFileURI,rs);
    	  
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
   int size=0;
 		reader.getPackages().get(0);
 		 System.out.println("Loading Model..");
 	   for(EClass c: reader.getClasses())
       {
 		   	   
    	   System.out.println("Class = " +c.getName());  
    	   size=size+ c.getEAllAttributes().size();

    	   /*
    	   for (EReference ref : c.getEReferences()) {
    		   System.out.println("Ref = "+ref.getName() + " - "+ ref.getEType().getName());
    	   }
    		   for (EAttribute att : c.getEAttributes()) {
        		  System.out.println("Att = "+att.getName()+ " - "+ att.getEType().getName());
		}
    		   for (EOperation op : c.getEOperations()) {
         		  System.out.println("Op = "+op.getName()+ " - "+ op.getEType().getName());
			}*/
    	   
       }
       

 	  System.err.println("Size = " +size); 

 		//OCL parsing 
 		EmfOclParser oclParser = new EmfOclParser();
 		File oclF=new File(oclFilePath.replace(".ocl", "_temp.ocl"));
 		try{
 		cloneAndCleanOCl (oclFilePath,oclF);  		
 		}catch (Exception e)
 		{e.printStackTrace();}
		
 		System.out.println("\n Sclicing");
 		
 		
 		List<Constraint> cList = oclParser.parseOclDocument(resource, oclF);
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
        for (Constraint c : cList) {
          if (!c.getStereotype().equalsIgnoreCase("precondition") && !c.getStereotype().equalsIgnoreCase("postcondition")) {
            ExpressionInOCL<?, ?> oclExpression = (ExpressionInOCL<?, ?>) c.getSpecification();
        	EClass contextCls = (EClass) c.getConstrainedElements().get(0);
            helper.setContext(contextCls);        
            OclAST.addEClass(OclAST.listeEClass, contextCls); 
            System.out.println(c);
            oclExpression.accept(oclVisitor);
            
           
          }
        }
        

        
        
        //Gen instances
        System.err.println("\n Generation started");
        long startTime = System.nanoTime();
        
     
        int nbTaxpayers= 10;
    	String startinfPointName="Tax_Case";
        boolean toBeAppend = false;
        LinkedList<EObject> instances=new LinkedList<EObject>();
       
		
		EPackage pck =((EPackageUMLAdapter)OclAST.reader.getPackages().get(0)).getPck();
		OclAST.UMLTOECORE(pck);
		  CreateTaxpayers gen = new CreateTaxpayers(rs,1,pck,null);
			CreateTaxpayers.all=OclAST.orderedListe;
			System.err.println("\n Dependencies Graph generation");
	     
		
			EClass startingPointClass= OclAST.getClassByName(OclAST.allClasses, startinfPointName);

	        
			
			
			System.out.println("\n Including OCL triggers to dependencies");
	        for (EAttribute att: OclAST.listeEAttribute)
	        	{gen.getNeedsForAtt(att);}
	        for (EReference ref: OclAST.listeEReference)
        	{gen.getNeedsForRef(ref);}
	        
			Main.g = Graphs.DomainToDependenciesGraph(pck,startingPointClass,gen);
			Graphs.test(Main.g);
			CreateTaxpayers.orderIterator = new TopologicalOrderIterator<String, DefaultEdge>(Main.g);
	        
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
	        
	        
			
	        
	        
	        System.out.println("NB classes reffered to = "+OclAST.listeEClass.size());
	        System.out.println("NB enummuration classs used = "+OclAST.listeEnum.size());      
	        System.out.println("NB attributes reffered to = " + OclAST.listeEAttribute.size());
	        System.out.println("NB EReferences reffered to = " + OclAST.listeEReference.size());
	        System.out.println("NB used operations = " + OclAST.listeEOperation.size());           
	    
	        System.out.println("*********Classes");
	        for (EClass a: OclAST.listeEClass)
	        	{System.out.println(a.getName());}
	        System.out.println("*********Enumurations");
	        for (EEnum b: OclAST.listeEnum)
	        	{System.out.println(b.getName());}
	        System.out.println("*********Attributes");
	        for (EAttribute c: OclAST.listeEAttribute)
	        	{

	        	System.out.println(c.getName()+" -- "+c.getEContainingClass().getName());}
	        System.out.println("*********References");
	        for (EReference d: OclAST.listeEReference)
	            {System.out.println(d.getName()+" ["+d.getEContainingClass().getName()+" - "+d.getEReferenceType().getName()+"] ");}


	        
	    	HashMap<EReference, LinkedList<EObject>> missing=new HashMap<EReference, LinkedList<EObject>>();
			for (int j = 0; j < nbTaxpayers; j++) 
			{System.out.println();


			if(j%10==0)
			{
		        Runtime rt = Runtime.getRuntime();
		        long totalMem = rt.totalMemory();
		        long maxMem = rt.maxMemory();
		        long freeMem = rt.freeMemory();
		        double megs = 1048576.0;
		     
		        System.out.println("Used Memory:"+ (totalMem - freeMem)+ " (" + (totalMem - freeMem)  / megs + " MiB)");
		        System.out.println ("Total Memory: " + totalMem + " (" + (totalMem/megs) + " MiB)");
		        System.out.println ("Max Memory:   " + maxMem + " (" + (maxMem/megs) + " MiB)");
		        System.out.println ("Free Memory:  " + freeMem + " (" + (freeMem/megs) + " MiB)\n");
		        
			}
			
			
			if(j%10==0)
			System.gc();
			
			long start = System.nanoTime();
			
		
			instances.add(gen.generate(pck, "FD",startingPointClass,null,null,instances,startingPointClass,missing,new LinkedList<EObject>()));
			long end = System.nanoTime();
		  	long d = (end - start);
		  	double duration_second=(double)d / 1000000000.0;
			System.out.println("Tax Case "+j+" created in "+duration_second+ " seconds");

			
	
			}
		
		if(toBeAppend==false)
		CreateTaxpayers.save("TaxpayersOf"+"FD",instances);
		else
			CreateTaxpayers.append("TaxpayersOf"+"FD", instances);
        //save domain model results   
        String name = cList.get(0).getName();
        EcoreFactory theCoreFactory = EcoreFactory.eINSTANCE;
        EPackage packageTaxCard = theCoreFactory.createEPackage();
        packageTaxCard.setName("TaxCard");
        packageTaxCard.setNsPrefix(name);
        packageTaxCard.setNsURI("http:///"+name+".ecore");
        
        // put the classes in the new package
      
      if(ext.endsWith("ecore"))
      {
        for (EClass a: OclAST.listeEClass)
    	{clean(a,oclParser,resource); 
        packageTaxCard.getEClassifiers().add(a);
        }
        
        for (EEnum b: OclAST.listeEnum)
    	{packageTaxCard.getEClassifiers().add(b);}
        
        ResourceSet metaResourceSet = new ResourceSetImpl();
        metaResourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore", new  XMLResourceFactoryImpl());
        Resource metaResource =  metaResourceSet.createResource(URI.createURI("file:/" + folderPath.replace("//", "/") +"/model/"+name+".ecore"));
        metaResource.getContents().add(packageTaxCard);

        try { 
             metaResource.save(null);
            } catch (IOException e) {
              e.printStackTrace();
           }
        
     
      }
      
      
     /* 
      int total = 0;
       for (EClass a: OclAST.listeEClass)
       	{total=total+(CreateTaxpayers.getCorrespondant(a, CreateTaxpayers.realClasses)).getAppliedStereotypes().size();}
       System.out.println("*********Enumurations");
       for (EEnum b: OclAST.listeEnum)
       	{total=total+b.getELiterals().size();}
       System.out.println("*********Attributes");
       for (EAttribute c: OclAST.listeEAttribute)
       	{
    	   total=total+(CreateTaxpayers.getCorrespondant(c, CreateTaxpayers.realAttributes)).getAppliedStereotypes().size();}
       System.out.println("*********References");
       for (EReference d: OclAST.listeEReference)
           { total=total+(CreateTaxpayers.getCorrespondant(d, CreateTaxpayers.realAssociations)).getAppliedStereotypes().size();}

       System.err.println(total);
       for (EObject d: OclAST.orderedListe)
       {System.out.println(d);}
       */
       
  	long endTime = System.nanoTime();
  	long duration = (endTime - startTime);
  	double duration_second=(double)duration / 1000000000.0;
	System.err.println("execution time = "+Math.round(duration_second)+ " seconds");
}

//add attribute derivation

	public static Resource loadEmfResource(URI modelFileURI,ResourceSet rSet) {
		return  rSet.getResource(modelFileURI, true);
	}
	
	public static Resource loadUmlResource (URI modelUri,ResourceSet rSet) {
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
		EcoreUtil.resolveAll(r);	
		UMLResourcesUtil.init(rSet);
		return new EResourceUMLAdapter(r);
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
	
	public static void cloneEcore(String oclFilePath, File oclFile) throws IOException
	{
		InputStream input = new FileInputStream(oclFilePath);
 		OutputStream output = new FileOutputStream(oclFilePath.replace(".ecore", "_temp.ecore"));	
 		IOUtils.copy(input, output);
		RandomAccessFile tools = new RandomAccessFile(oclFile, "rw"); 
		long length = tools.length();
		byte[] nexts = new byte[(int) length];
		tools.readFully(nexts); 
		tools.seek(0); 
		tools.write(nexts);
		tools.setLength(nexts.length); 
		tools.close(); 
		
	}

	public static void clean(EClass a,EmfOclParser oclParser,Resource resource)
	{
	 for (int i = 0; i < a.getEStructuralFeatures().size(); i++) {	
		 if(a.getEStructuralFeatures().get(i) instanceof EAttribute)
		 {
		 if(!OclAST.listeEAttribute.contains((EAttribute)(a.getEStructuralFeatures().get(i))))
		 {
			a.getEStructuralFeatures().remove(i);
			 i--;
		 }}
		 else{
			   if(!OclAST.listeEReference.contains((EReference)(a.getEStructuralFeatures().get(i))))
		 		{
			 	a.getEStructuralFeatures().remove(i);
			 	i--;
		 		}
		 		else{
		 			EReference ref= (EReference)(a.getEStructuralFeatures().get(i));
		 			if (!OclAST.listeEReference.contains(ref.getEOpposite()))
		 					ref.setEOpposite(null);
		 		}
		 
		 }
	
	//	oclParser.cleanConstraintsFromClass(resource, a);
	}
	 
	 for (int i = 0; i < a.getEOperations().size(); i++) {
		 if(!OclAST.listeEOperation.contains((EOperation)(a.getEOperations().get(i))))
		 {
			 a.getEOperations().remove(i);
			 i--;
		 }}
	
	}
    
	
	public void saveEPackages(String absolutePath)
	{		String ext = FilenameUtils.getExtension(absolutePath);   
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


		  ResourceSet rs = new ResourceSetImpl();
		  final ExtendedMetaData extendedMetaData = new BasicExtendedMetaData(rs.getPackageRegistry());
		  rs.getLoadOptions().put(XMLResource.OPTION_EXTENDED_META_DATA,extendedMetaData);


		  URI modelFileURI = URI.createFileURI(absolutePath);          
		  Resource resource=null;
		  if (ext.equalsIgnoreCase("ecore"))
		  resource = loadEmfResource(modelFileURI,rs);
	 	  else
	        resource = loadEmfResource(modelFileURI,rs);
	  
		  Iterator<EObject> i = resource.getAllContents();
		  while (i.hasNext()) {
			  Object o = i.next();
			  if (o instanceof EPackage) {
				  EPackage p = (EPackage)o;
            	rs.getPackageRegistry().put(p.getNsURI(), p);       	
   
			  }
			  }
	}
   



}
