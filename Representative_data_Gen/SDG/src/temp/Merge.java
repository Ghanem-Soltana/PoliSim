package temp;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.BasicExtendedMetaData;
import org.eclipse.emf.ecore.util.ExtendedMetaData;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.uml2.uml.internal.resource.UMLResourceFactoryImpl;

import util.EmfModelReader;

@SuppressWarnings("restriction")
public class Merge {


	
	public static void main(String[] args) {
		EList<EObject> elements=new BasicEList<EObject>();
	 for (int i = 1; i < 5; i++) {
	EList<EObject> temp = LoadElements("TaxpayersOfFD"+i+".xmi");	
	elements.addAll(temp);
	}
 	
 	 
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


