package temp;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.BasicExtendedMetaData;
import org.eclipse.emf.ecore.util.ExtendedMetaData;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.ocl.ecore.Constraint;
import org.eclipse.ocl.ecore.OCL;
import org.eclipse.ocl.helper.OCLHelper;
import org.eclipse.ocl.utilities.ExpressionInOCL;
import org.eclipse.uml2.uml.internal.resource.UMLResourceFactoryImpl;

import util.EmfModelReader;
import util.EmfOclParser;
import util.OCLForJAVA;
import util.OclAST;

@SuppressWarnings("restriction")
public class Filter {


	
	public static void main(String[] args) {
		
		 final String model="//Papyrus//TaxCard.uml";
		 final String oclFile="//model//constraints.ocl";
		 
		 
		    File f=new File("");
		    String folderPath = f.getAbsolutePath();
	        String absolutePath = folderPath + model;
	        String oclFilePath = folderPath + oclFile; 
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
	     resource = Main.loadEmfResource(modelFileURI,rs);
	    else
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

			reader.getPackages().get(0);
			 System.out.println("Loading Model..");
		   for(EClass c: reader.getClasses())
	     {
			   	   
	  	   System.out.println("Class = " +c.getName());  
	     }
		   
		//OCL parsing 
			EmfOclParser oclParser = new EmfOclParser();
			File oclF=new File(oclFilePath.replace(".ocl", "_temp.ocl"));
			try{
			Main.cloneAndCleanOCl (oclFilePath,oclF);  		
			}catch (Exception e)
			{e.printStackTrace();}
			
			System.out.println("\n Preparing simulation environment");
			
			
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
	          
	          try {	
	          oclExpression.accept(oclVisitor);
	          } catch (Exception e) {
				System.err.print(e.getCause());
				}

	        }
	      }
	      
	      
		
		
		
		EList<EObject> elements=new BasicEList<EObject>();
	elements = LoadElements("TaxpayersOfFD.xmi");	
	
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
	 
 for (int j = 0; j < taxpayers.size(); j++) {
	

		 taxpayer = taxpayers.get(j);
		 OCLForJAVA.init("",taxpayer);
		 tempOCL= "self.getTaxClass(2014)";
		 EObject tax_class_old = OCLForJAVA.evaluateEObject(taxpayer, tempOCL, "tax_class",""); 
		 tempOCL= "self.getTaxClassModified(2014)";
		 EObject tax_class_new = OCLForJAVA.evaluateEObject(taxpayer, tempOCL, "tax_class",""); 
		 System.out.println(tax_class_old);
		 
		 System.out.println(tax_class_new);
		 System.out.println(!(tax_class_new.toString().equals(tax_class_old.toString())&&tax_class_old.toString().equals("Two")));
		 if(!(tax_class_new.toString().equals(tax_class_old.toString())&&tax_class_old.toString().equals("Two")))
		 {
			 taxpayers.remove(j);
			 j--;
		 }
	}
 
 elements.clear();
elements.addAll(taxpayers); 
	save(elements);
 	  	 
 	
}
	
	public static EList<EObject>  LoadElements(String file)
	{
		
		
	     final String model="//Papyrus//TaxCard.uml";
		 final String instances_rep="//Instances//"+ file;
		 
		 
		    File f=new File("");
		    String folderPath = f.getAbsolutePath();
	        String absolutePath = folderPath + model;
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
	     resource = Main.loadEmfResource(modelFileURI,rs);
	    else
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

			reader.getPackages().get(0);
			 System.out.println("Loading Model..");
		   for(EClass c: reader.getClasses())
	     {
			   	   
	  	   System.out.println("Class = " +c.getName());  
	     }
		   
	      
	      
	      //Loading instances
	     System.out.println("Loading Instances..");
	 	 ResourceSet load_resourceSet = new ResourceSetImpl();
	 	 load_resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());
	 	 load_resourceSet.getPackageRegistry().put(reader.getPackages().get(0).getNsURI(),reader.getPackages().get(0));
	 	 Resource load_resource = load_resourceSet.getResource(URI.createURI(folderPath+instances_rep),true);
	 	 EList<EObject> elements= load_resource.getContents();

	
		return elements;
	}
	
	
	
	

	 public static void save( EList<EObject> elements){

		 
		    
		    ResourceSetImpl resourceSet = new ResourceSetImpl(); 
		    resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl()); 
		    final File f = new File("");
		    final String dossierPath = f.getAbsolutePath().replace("\\", "//");
		    final String apiSamplePath = dossierPath +"//Instances//"; 
		    final String file_name="taxpayersOfFD.xmi";
		    String path = "file://" + apiSamplePath + file_name;
		    URI uri = URI.createURI(path); 
		    Resource resource = resourceSet.createResource(uri); 
		    resource.getContents().addAll(elements); 

		    
		    try {
				resource.save(null);
			} catch (IOException e) {
				e.printStackTrace();
			} }
	
}


