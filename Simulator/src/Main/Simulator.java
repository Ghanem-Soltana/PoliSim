package Main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFPicture;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.CellRangeAddress;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.util.IOUtils;
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
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.ExtendedMetaData;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.ocl.ParserException;
import org.eclipse.ocl.ecore.Constraint;
import org.eclipse.ocl.ecore.OCL;
import org.eclipse.ocl.expressions.OCLExpression;
import org.eclipse.ocl.helper.OCLHelper;
import org.eclipse.ocl.utilities.ExpressionInOCL;
import org.eclipse.uml2.uml.internal.resource.UMLResourceFactoryImpl;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import temp.Main;
import util.EmfModelReader;
import util.EmfOclParser;
import util.OCLForJAVA;
import util.OclAST;
import util.UpdateContainer;


@SuppressWarnings({ "restriction", "deprecation" })
public class Simulator {
	
	public static HashMap<String, String> save_taxes ;
 
	

	public static void main(String[] args) {
		//TODO
	     boolean change_classes=false; 
	     
	     
		 save_taxes=new HashMap<String, String>();
		 long startTime = System.nanoTime();
		 final String model="//Papyrus//TaxCard.uml";
		 final String oclFile="//model//constraints.ocl";
		 final String instances_rep="//Instances//TaxpayersOfFD.xmi";
		 final String new_models_rep = "//Instances//new_models.txt";
		 final String modified_models_rep = "//Instances//modified_models.txt";
		 
		    File f=new File("");
		    final String folderPath = f.getAbsolutePath();
	        final String absolutePath = folderPath + model;
	        final String oclFilePath = folderPath + oclFile; 
	        final String new_models_Path = folderPath + new_models_rep; 
	        final String modifed_models_Path = folderPath + modified_models_rep; 
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
	        
	        
	        
	        //Loading instances
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
	 	 
	 	 LinkedList<String> AD_names = getNamesOFModels(new_models_Path);
	 	 LinkedList<String> AD_modified = getNamesOFModels(modifed_models_Path);

	 	
	// New Models
	 	 for (int j = 0; j < taxpayers.size(); j++) {
	 		 EObject tax_payer=taxpayers.get(j);

	 		 System.out.println("Simulation of taxpayer (initial) nb: "+(j+1));
	 		 if(j==0)
	 		 {OCLForJAVA.newInstance();}
	 		 
	 		 for (int k = 0; k < AD_names.size(); k++) {
	 			 Class<?> classe=null;
	 			 boolean error=false;
	 	        try {
	 				classe = Class.forName("Main.Simulation");
	 			} catch (ClassNotFoundException e) {
	 				e.printStackTrace();
	 				error=true;
	 			}
	 	        
	 	       if(!error)
	              {
	 	    	  Method method=null;
	 	    		try {
	 	    			Class<?>[]types = new Class<?>[2];
	 	    			types[0]=Class.forName("org.eclipse.emf.ecore.EObject");
	 	    			types[1]=Class.forName("java.lang.String");
						method = classe.getMethod(AD_names.get(k),types);
						Object [] param=new Object[2];
						param[0]=tax_payer;
						param[1]=AD_names.get(k);
						method.invoke(null,param); 
					} catch (Exception e) {
						e.printStackTrace();
					} 
	 	    	
	              }
			}

	    }
	 	 
	 	long endTime = System.nanoTime();
		long duration = (endTime - startTime);
		double duration_second=(double)duration / 1000000000.0;
		System.out.println("execution time = "+Math.round(duration_second)+ " seconds");
		System.exit(0);
	 
	 
	 	 //read Old

 		 LinkedList<EObject> liste = new LinkedList<EObject>();
		 	 for (int j = 0; j < taxpayers.size(); j++) {
		 		 liste.add(taxpayers.get(j));
		 	 }
        
		 if(AD_names.size()>0)
			 if( !(AD_names.size()==1&&AD_names.contains("AEP")))
		saveExcel("current_policies",liste,false, new LinkedList<String>());
	 	save("",elements);
	 	 
	 	 
	 	
	 	if(AD_modified.size()>0)
	 	{
		 	 
			 LinkedList<String> modified_attributes = getNamesOfModifedAttributes(modifed_models_Path);
			 for (int k = 0; k < AD_modified.size(); k++) {
	 			 Class<?> classe=null;
	 			 boolean error=false;
		 	 
	 	ResourceSet re_load_resourceSet = new ResourceSetImpl();
	   	re_load_resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());
	    ResourceSet re_rs = new ResourceSetImpl(); 
	    final ExtendedMetaData re_rs_extendedMetaData = new BasicExtendedMetaData(re_rs.getPackageRegistry());
	    re_rs.getLoadOptions().put(XMLResource.OPTION_EXTENDED_META_DATA,re_rs_extendedMetaData);
	   	EmfModelReader re_reader = new EmfModelReader( Main.loadUmlResource(modelFileURI,re_rs));     
 		re_reader.getPackages().get(0);
 		re_reader.getClasses();
 
	   	re_load_resourceSet.getPackageRegistry().put(re_reader.getPackages().get(0).getNsURI(),re_reader.getPackages().get(0));
	    final String dossierPath = f.getAbsolutePath().replace("\\", "//");
	    final String simulated_instances = dossierPath +"//SimulationResult//"; 
	    final String file_name="taxpayers.xmi";
	    String path =  simulated_instances + file_name;
	    Resource re_load_resource = re_load_resourceSet.getResource(URI.createURI(path),true);
	 	elements= re_load_resource.getContents();
	 	
	 	
	 	
	 	

 	    ocl = org.eclipse.ocl.ecore.OCL.newInstance();
        helper = ocl.createOCLHelper();
 	    OclAST.allEnumerations = (LinkedList<EEnum>) re_reader.getEnumerations();
        OclAST.allClasses = (LinkedList<EClass>) re_reader.getClasses();
 	    OclAST.reader=re_reader;
        oclVisitor = OclAST.getInstance();     
        OclAST.listeEAllOperation = (LinkedList<EOperation>) re_reader.getAllOperations();
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
	 	
	 	


		 OCLForJAVA.init("FD",elements.get(0));
	 	 tempOCL= "Tax_Payer.allInstances()->any(true)";
		 taxpayer = OCLForJAVA.evaluateEObject(elements.get(0), tempOCL, "taxpayer","Tax_Payer"); 
	   	 OCLForJAVA.init("FD",taxpayer);
	 	 taxpayersCollection = OCLForJAVA.evaluateECollection(taxpayer," let all:Set(Tax_Payer)= Tax_Payer.allInstances()in all->select(oclIsKindOf(Tax_Payer))->sortedBy(t:Tax_Payer| t.incomes->sortedBy(income_amount)->last().income_amount)","taxpayers","Tax_Payer","OrderedSet"); 

		taxpayers=new LinkedList<EObject>();
	    it = taxpayersCollection.iterator();
	 	 while(it.hasNext())
	 	 {
	 		taxpayers.add(it.next());
	 	 }
	 	 System.out.println(elements.size());
	 	EList<EObject> clones = new BasicEList<EObject>();
	 	liste= new LinkedList<EObject>();
	 	 for (int j = 0; j < taxpayers.size(); j++) {
	 		 liste.add(taxpayers.get(j));
	 		 clones.add(EcoreUtil.copy(taxpayers.get(j)));
	 	 }
	   	
	
	

	 	 
	 	 
	 	 for (int j = 0; j < liste.size(); j++) {
	 		 EObject tax_payer=liste.get(j);

	 		 System.out.println("Simulation of taxpayer nb (Modified_version of "+AD_modified.get(k)+"): "+(j+1));
	 		 //Reduce time for generating dynamic OCL
	 		 if(j==0)
	 		 {OCLForJAVA.newInstance();}

	 	        try {
	 				classe = Class.forName("test.Simulation");
	 			} catch (ClassNotFoundException e) {
	 				e.printStackTrace();
	 				error=true;
	 			}
	 	        
	 	       if(!error)
	              {
	 	    	  Method method=null;
	 	    		try {
	 	    			Class<?>[]types = new Class<?>[2];
	 	    			types[0]=Class.forName("org.eclipse.emf.ecore.EObject");
	 	    			types[1]=Class.forName("java.lang.String");
						method = classe.getMethod(AD_modified.get(k),types);
						Object [] param=new Object[2];
						param[0]=tax_payer;
						param[1]=AD_modified.get(k);
						method.invoke(null,param); 
					} catch (Exception e) {
						e.printStackTrace();
					} 
	 	    	
	              }
	          
			}
  		
	    
	 

	 	
	 	 saveExcel(AD_modified.get(k),liste,true,getMyattributes(modifed_models_Path,AD_modified.get(k)));
	     save(AD_modified.get(k),elements);
	     if(k==AD_modified.size()-1)
	 	 {
	    	 if(AD_modified.size()>1)
	    	 {
	    		 
	    		 for (int j = 0; j < liste.size(); j++) {
	    			 
	    			 EObject tax_payer=taxpayers.get(j);

	    			 System.out.println("Simulation of taxpayer nb (Modified_version for all modification): "+(j+1));
	    	 		 if(j==0)
	    	 		 {OCLForJAVA.newInstance();}
	    	 		 
	    	 		 for (int kk = 0; kk < AD_modified.size(); kk++) {
	    	 	        try {
	    	 				classe = Class.forName("test.Simulation");
	    	 			} catch (ClassNotFoundException e) {
	    	 				e.printStackTrace();
	    	 				error=true;
	    	 			}
	    	 	        
	    	 	       if(!error)
	    	              {
	    	 	    	  Method method=null;
	    	 	    		try {
	    	 	    			Class<?>[]types = new Class<?>[2];
	    	 	    			types[0]=Class.forName("org.eclipse.emf.ecore.EObject");
	    	 	    			types[1]=Class.forName("java.lang.String");
	    						method = classe.getMethod(AD_modified.get(kk),types);
	    						Object [] param=new Object[2];
	    						param[0]=tax_payer;
	    						param[1]=AD_modified.get(kk);
	    						method.invoke(null,param); 
	    					} catch (Exception e) {
	    						e.printStackTrace();
	    					} 
	    	 	    	
	    	              }
	    			}

	    	 		 
	    	
	    	 }
	    		 saveExcel("all_modifications",liste,true,modified_attributes);
	    	     save(AD_modified.get(k),clones);
	 		
	 	 }
	 	 }
	 	}
	 	
	 	
	 	
	 	}
	 	
	 	
	 	
	 	if(change_classes)
	 	{
	 		saveExcelForClasses("tax_categories_modifications",liste);

	 	}
	 	
	 	
	 	
	 	
	 	//long endTime = System.nanoTime();
		//long duration = (endTime - startTime);
		//double duration_second=(double)duration / 1000000000.0;
		//System.out.println("execution time = "+Math.round(duration_second)+ " seconds");
	        
	}
	

	public static void saveExcel(String ADName, LinkedList<EObject>taxpayers, boolean modified, LinkedList<String> modified_attributes)
	{
		
		
	    FileOutputStream fileOut;
	    double sum_inital=0;
	    double sum_new =0;
	    final File f = new File("");
	    final String dossierPath = f.getAbsolutePath().replace("\\", "//");
	    final String apiSamplePath = dossierPath +"//SimulationResult//"; 
	    final String file_name=ADName+".xls";
	    String path =  apiSamplePath + file_name;
	    int nbColumn=20;
	    
	    HSSFWorkbook wb = new HSSFWorkbook();

	    HSSFSheet sheet = wb.createSheet("Simulation results");
	 

	    HSSFRow row = sheet.createRow(0);	  
	    HSSFCellStyle my_style = wb.createCellStyle();
        HSSFFont my_font=wb.createFont();
        my_font.setFontHeightInPoints((short) 16);
        my_font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        my_style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        my_style.setAlignment(CellStyle.ALIGN_CENTER);
        my_style.setFont(my_font);
	    row.setRowStyle(my_style);
	    HSSFCell cell = row.createCell(0);
	    cell.setCellValue("Taxpayers");
	    cell.setCellStyle(my_style);
	    ArrayList<String> fieldsNames =OCLForJAVA.fieldsNames;
	  
	    row.createCell(1).setCellValue("Updated Features");
	    row.getCell(1).setCellStyle(my_style);
	    row.createCell(2).setCellValue("Deductions/Credits");
	    row.getCell(2).setCellStyle(my_style);
	    row.createCell(3).setCellValue("Old Value");
	    row.getCell(3).setCellStyle(my_style);
	    row.createCell(4).setCellValue("New Value");
	    row.getCell(4).setCellStyle(my_style);
	    row.createCell(5).setCellValue("Tax Class");
	    row.getCell(5).setCellStyle(my_style);
	    row.createCell(6).setCellValue("Income Type");
	    row.getCell(6).setCellStyle(my_style);
	    row.createCell(7).setCellValue("Gross Income (per year)");
	    row.getCell(7).setCellStyle(my_style);
	    row.createCell(8).setCellValue("Taxable Income (per year)");
	    row.getCell(8).setCellStyle(my_style);
	    row.createCell(9).setCellValue("Taxes (per year)");
	    row.getCell(9).setCellStyle(my_style);
	    
	    	
	    if(modified)
	    {
	        row.createCell(10).setCellValue("Initial Taxes (per year)");
		    row.getCell(10).setCellStyle(my_style);
		    
		    row.createCell(11).setCellValue("Loss/Gain");
		    row.getCell(11).setCellStyle(my_style);
	    }
	 
	    int ligne=0;
	  
	    
		 for (int j = 0; j < taxpayers.size(); j++) {
			 boolean alone=false;
  			 ligne=ligne+1;
			 int SavLigne=ligne;

			    
			    row=sheet.createRow(SavLigne);
			    row.setRowStyle(my_style);
				cell = row.createCell(0);
				cell.setCellValue(taxpayers.get(j).eClass().getName()+" "+(j+1));
				
                OCLForJAVA.init("",taxpayers.get(j));
			    String  tempOCL= "self.incomes ";
			    Collection<EObject> incomes = OCLForJAVA.evaluateECollection(taxpayers.get(j),tempOCL,"incomes11","Income","Set"); 
			    Iterator<EObject> it = incomes.iterator();
			    boolean first=true;
			    while(it.hasNext()){
			    EObject income=it.next();	
			    
			    if(first==false)
				{	
				ligne=ligne+1; 
				row=sheet.createRow(ligne);
				 row.setRowStyle(my_style);
				}
				else {row=sheet.getRow(ligne);
				 row.setRowStyle(my_style);
				}
			    first=false;
				int SavFieldNameLigne=ligne;	
				cell = row.createCell(1);cell.setCellValue(income.eClass().getName());
			    
				
				
				if(modified)
				{
				for (int i = 0; i < fieldsNames.size(); i++) {
					if(modified_attributes.contains(fieldsNames.get(i))==false)
						{fieldsNames.remove(i);
						i--;
						}
				}
			
				}
				
	
				for (int k = 0; k < fieldsNames.size(); k++) {
				String fieldName=fieldsNames.get(k);
				
				 if(k!=0)
					{	
					ligne=ligne+1; 
					row=sheet.createRow(ligne);
					 row.setRowStyle(my_style);
					}
					else {row=sheet.getRow(ligne);}
				 
				cell = row.createCell(2);cell.setCellValue(fieldName.substring(fieldName.lastIndexOf(".")+1));
					
					boolean done=false; 
					
					for (int k2 = 0; k2 < OCLForJAVA.modifiedFields.size()&&done==false; k2++) {
						UpdateContainer modif=OCLForJAVA.modifiedFields.get(k2);
						
						 if(modif.getInput()==taxpayers.get(j))	
						 {
							
							 if(modif.getOcl().equals(fieldName))
							 {
								 
							  
									if(modif.obj==income || (modif.obj==taxpayers.get(j)))
									{
									if(done)
									{
										ligne=ligne+1; 
										row=sheet.createRow(ligne);
										 row.setRowStyle(my_style);
									}
									else {row=sheet.getRow(ligne);
									}
								
							            
									   double old=new BigDecimal(modif.getOld() ).setScale(2, RoundingMode.HALF_EVEN).doubleValue();
									   double newValue=new BigDecimal(modif.getValue() ).setScale(2, RoundingMode.HALF_EVEN).doubleValue();
										if(newValue<0)
										newValue=0;
									   cell = row.createCell(3);cell.setCellValue(old);
									   cell = row.createCell(4);cell.setCellValue(newValue);
										
								 System.out.println("fieldName=======>"+fieldName);
								 System.out.println("+ old ="+old+" new Value "+newValue);	
								 done=true;
									}
								
							 }
						 }	
							
					}
					
					if(done==false)
					{row=sheet.getRow(ligne);
					
					 OCLForJAVA.init("",income);
					 cell = row.createCell(2);cell.setCellValue(fieldName.substring(fieldName.lastIndexOf(".")+1));
					 String ch=fieldName.substring(0,fieldName.lastIndexOf("."));
					 ch="self"+ch.substring(fieldName.indexOf("."));
					 EObject temp= OCLForJAVA.evaluateEObject(income, ch,"temp2","");
					 if(temp==null)
					 { cell = row.createCell(3);cell.setCellValue(String.valueOf("-"));
					 cell = row.createCell(4);cell.setCellValue("-");
					 }
					 else{
					 String temp1="self"+fieldName.substring(fieldName.indexOf("."));
					 double old=-1;
				
				      old = OCLForJAVA.evaluateDouble(income,temp1,"temp3"); 


					 
				     cell = row.createCell(3);cell.setCellValue(old);
				     cell = row.createCell(4);cell.setCellValue(old);
					 }
					}
	
					 double gross = 0;
					 double taxable=0;
					 double taxes =0;
					//get taxation_year
					 OCLForJAVA.init("",income);
					 tempOCL= "self.taxPayer.from_agent.taxation_year";
					 OCLForJAVA.evaluateInt(income, tempOCL, "tax_p"); 

               		//add Tax Category
					 OCLForJAVA.init("",income);
					 tempOCL= "self.taxPayer.getTaxClass(self.taxPayer.from_agent.taxation_year)";
					 EObject tax_class = OCLForJAVA.evaluateEObject(income, tempOCL, "tax_class",""); 
					 cell = row.createCell(5);cell.setCellValue(tax_class.toString());
					//add  income type
					 tempOCL= "let income_type:String= if(self.income_type.oclIsTypeOf(Employment_Income)) then 'Employment' else if (self.income_type.oclIsTypeOf(Pensions_and_Annuities_Income)) then 'Pension' else 'Other' endif endif in income_type ";
					 String type = OCLForJAVA.evaluateString(income, tempOCL, "type"); 
					 cell = row.createCell(6);cell.setCellValue(type);
	
							
						
							 
							 OCLForJAVA.init("",income);
							 tempOCL=  "self.taxPayer.getIsTaxedJointly(self.taxPayer.from_agent.taxation_year)";
							 boolean is_taxedJointly = OCLForJAVA.evaluateBoolean(income, tempOCL, "is_taxedJointly"); 
							 alone = ! is_taxedJointly;
							 EObject spouse=null;
	
					 
					if(alone)
					{ 
						
						OCLForJAVA.init("",income);
						 //add gross
						 tempOCL=  "let amount:Real= self.income_per_year() in self.taxPayer.from_law.precision(amount,2)";		 
						 gross = OCLForJAVA.evaluateDouble(income, tempOCL, "gross"); 
						 cell = row.createCell(7);cell.setCellValue(gross);
							
							//add taxable
						 tempOCL=  "let amount:Real= self.taxPayer.from_law.getTaxableIncomePerYear(self) in amount";
						 taxable = OCLForJAVA.evaluateDouble(income, tempOCL, "taxable"); 
						 cell = row.createCell(8);cell.setCellValue(taxable);
					
						
						
				
					 tempOCL= "let income_type1:String= if(self.income_type.oclIsTypeOf(Employment_Income)) then 'Employment' else if (self.income_type.oclIsTypeOf(Pensions_and_Annuities_Income)) then 'Pension' else 'Other' endif endif in "
					 			+ "let amount:Real= self.taxPayer.from_law.getTaxableIncomePerYear(self) in "
						 		+ " self.taxPayer.from_law.calculate_taxes_per_year(amount, Tax_Class_Category::"+tax_class.toString()+", income_type1)";

					 taxes = OCLForJAVA.evaluateDouble(income, tempOCL, "taxes"); 
					 cell = row.createCell(9);cell.setCellValue(taxes);
					}
					else
					{
				
						 	 //add gross
						    	
						          double global_gross =0;
							      double global_taxable =0;
								 tempOCL= "let amount:Bag(Real)=  self.incomes->iterate(inc: Income;  acc:Bag(Real)=Bag{}  | acc->including(inc.income_per_year()) )  in "
								 		+ "let amount1:Bag(Real)=  self.getSpouse(from_agent.taxation_year).oclAsType(Tax_Payer).incomes->iterate(inc: Income;  acc:Bag(Real)=Bag{}  | acc->including(inc.income_per_year()) )  in "
								 		+ "self.from_law.precision(((amount->sum())+(amount1->sum())),2)";		 
								 global_gross = OCLForJAVA.evaluateDouble(taxpayers.get(j), tempOCL, "gross"); 
								 cell = row.createCell(7);cell.setCellValue(global_gross);
									
								 
								 //add taxable
								 tempOCL=  "let amount:Bag(Real)= self.incomes->iterate(inc: Income;  acc:Bag(Real)=Bag{}  | acc->including(self.from_law.getTaxableIncomePerYear(inc))) in "
								 		+ "let amount1:Bag(Real)= self.getSpouse(from_agent.taxation_year).oclAsType(Tax_Payer).incomes->iterate(inc: Income;  acc:Bag(Real)=Bag{}  | acc->including(self.from_law.getTaxableIncomePerYear(inc))) in "
								 		+ " amount->sum()+amount1->sum() - (self.AEP_deduction + self.getSpouse(from_agent.taxation_year).oclAsType(Tax_Payer).AEP_deduction)";
								 global_taxable = OCLForJAVA.evaluateDouble(taxpayers.get(j), tempOCL, "global_taxable"); 
								 cell = row.createCell(8);cell.setCellValue(global_taxable);
								 boolean enLigne = false;
								 double AEP=0;
								 tempOCL="self.AEP_deduction + self.getSpouse(from_agent.taxation_year).oclAsType(Tax_Payer).AEP_deduction";
								 AEP = OCLForJAVA.evaluateDouble(taxpayers.get(j), tempOCL, "AEP"); 
		
								 //calcul taxes
								
								 double taxableEmployment=0;
								    tempOCL=  "let amount:Bag(Real)= self.incomes->select(income_type.oclIsTypeOf(Employment_Income))->iterate(inc: Income;  acc:Bag(Real)=Bag{}  | acc->including(self.from_law.getTaxableIncomePerYear(inc))) in "
								 		+ "let amount1:Bag(Real)= self.getSpouse(from_agent.taxation_year).oclAsType(Tax_Payer).incomes->select(income_type.oclIsTypeOf(Employment_Income))->iterate(inc: Income;  acc:Bag(Real)=Bag{}  | acc->including(self.from_law.getTaxableIncomePerYear(inc))) in "
								 		+ " amount->sum()+amount1->sum()";
								    taxableEmployment = OCLForJAVA.evaluateDouble(taxpayers.get(j), tempOCL, "taxable_empyment"); 
								    if(taxableEmployment>0)
								    {
								    	enLigne=true;
								    	taxableEmployment = taxableEmployment - AEP;
								    }
								 tempOCL= " self.from_law.calculate_taxes_per_year("+taxableEmployment+", Tax_Class_Category::Two, 'Employment')";
						    	 double taxesEmployment = OCLForJAVA.evaluateDouble(taxpayers.get(j), tempOCL, "taxes"); 
								 
						    	 
						    	 
						    	 double taxablePensiom=0;
								    tempOCL=  "let amount:Bag(Real)= self.incomes->select(income_type.oclIsTypeOf(Pensions_and_Annuities_Income))->iterate(inc: Income;  acc:Bag(Real)=Bag{}  | acc->including(self.from_law.getTaxableIncomePerYear(inc))) in "
								 		+ "let amount1:Bag(Real)= self.getSpouse(from_agent.taxation_year).oclAsType(Tax_Payer).incomes->select(income_type.oclIsTypeOf(Pensions_and_Annuities_Income))->iterate(inc: Income;  acc:Bag(Real)=Bag{}  | acc->including(self.from_law.getTaxableIncomePerYear(inc))) in "
								 		+ " amount->sum()+amount1->sum()";
								    taxablePensiom = OCLForJAVA.evaluateDouble(taxpayers.get(j), tempOCL, "taxable_empyment"); 
								    if(enLigne==false && taxablePensiom>0)
								    {
								    	enLigne=true;
								    	taxablePensiom = taxablePensiom - AEP;
								    }

								    tempOCL= " self.from_law.calculate_taxes_per_year("+taxablePensiom+", Tax_Class_Category::Two, 'Pension')";
						    	   double taxesPension = OCLForJAVA.evaluateDouble(taxpayers.get(j), tempOCL, "taxes"); 
						    	   
						    	   double taxableOther=0;
								    tempOCL=  "let amount:Bag(Real)= self.incomes->select(not income_type.oclIsTypeOf(Pensions_and_Annuities_Income) and not income_type.oclIsTypeOf(Employment_Income) )->iterate(inc: Income;  acc:Bag(Real)=Bag{}  | acc->including(self.from_law.getTaxableIncomePerYear(inc))) in "
								 		+ "let amount1:Bag(Real)= self.getSpouse(from_agent.taxation_year).oclAsType(Tax_Payer).incomes->select(not income_type.oclIsTypeOf(Pensions_and_Annuities_Income) and not income_type.oclIsTypeOf(Employment_Income) )->iterate(inc: Income;  acc:Bag(Real)=Bag{}  | acc->including(self.from_law.getTaxableIncomePerYear(inc))) in "
								 		+ " amount->sum()+amount1->sum()";
								    taxableOther = OCLForJAVA.evaluateDouble(taxpayers.get(j), tempOCL, "taxable_empyment"); 
								    if(enLigne==false && taxableOther>0)
								    {
								    	enLigne=true;
								    	taxableOther = taxableOther - AEP;
								    }
								    	
						        	tempOCL= " self.from_law.calculate_taxes_per_year("+taxableOther+", Tax_Class_Category::Two, 'Pension')";
						    	   double taxesOther = OCLForJAVA.evaluateDouble(taxpayers.get(j), tempOCL, "taxes"); 
							 
							 
						    	   
							 taxes=taxesEmployment+taxesPension+taxesOther;
							 cell = row.createCell(9);
							 
							 
							 OCLForJAVA.init("",taxpayers.get(j));
							    tempOCL= "self.SSNo";
							    String taxpayer_id = OCLForJAVA.evaluateString(taxpayers.get(j), tempOCL, "taxpayer_id"); 
							    OCLForJAVA.init("",income);
							
							    tempOCL= "self.num";
							    String income_id = OCLForJAVA.evaluateString(income, tempOCL, "income_num"); 
							    
							    boolean spouseBeforeMe= getIsSpouseBeforeMe(spouse,taxpayers,j);
								if(spouseBeforeMe==false)
								{cell.setCellValue(taxes);
								  if(modified==false)
								  save_taxes.put(taxpayer_id+"#"+income_id,String.valueOf(taxes));
								}
							 else
							 {
								 String ch=taxes+" Spouse ==> TP"+getPostion(taxpayers,spouse);
								 cell.setCellValue(ch);
							 
						    double temp = row.getCell(7).getNumericCellValue();
						    row.getCell(7).setCellValue("--"+temp+"--");
						    temp = row.getCell(8).getNumericCellValue();
						    row.getCell(8).setCellValue("--"+temp+"--");
						    if(modified==false)
						    save_taxes.put(taxpayer_id+"#"+income_id,taxes + "(Spouse)");
							 }
						     
					}
	
					
					
					 
					 OCLForJAVA.init("",taxpayers.get(j));
					    tempOCL= "self.SSNo";
					    String taxpayer_id = OCLForJAVA.evaluateString(taxpayers.get(j), tempOCL, "taxpayer_id"); 
					    OCLForJAVA.init("",income);
					
					    tempOCL= "self.num";
					    String income_id = OCLForJAVA.evaluateString(income, tempOCL, "income_num"); 
					 
					 if(modified==false)
					 {
						 if(alone)
						   save_taxes.put(taxpayer_id+"#"+income_id,String.valueOf(taxes));
					 }
					 else
					 {  try{
						 double old_taxes = Double.valueOf(save_taxes.get(taxpayer_id+"#"+income_id)).doubleValue();
						 cell = row.createCell(10);cell.setCellValue(old_taxes);
						 cell = row.createCell(11);cell.setCellValue(taxes-old_taxes);
					 }catch(Exception e){				
						 cell = row.createCell(10);cell.setCellValue(save_taxes.get(taxpayer_id+"#"+income_id));
						 cell = row.createCell(11);cell.setCellValue(0);
					 }
					 }
				
					 
                  if(SavFieldNameLigne!=ligne)
                  {
					sheet.addMergedRegion(new CellRangeAddress(
							SavFieldNameLigne, //first row (0-based)
							ligne, //last row  (0-based)
					        1, //first column (0-based)
					        1  //last column  (0-based)
					));
					

					sheet.addMergedRegion(new CellRangeAddress(
							SavFieldNameLigne, //first row (0-based)
							ligne, //last row  (0-based)
					        6, //first column (0-based)
					        6  //last column  (0-based)
					));
					
					
					
					sheet.addMergedRegion(new CellRangeAddress(
							SavFieldNameLigne, //first row (0-based)
							ligne, //last row  (0-based)
					        7, //first column (0-based)
					        7  //last column  (0-based)
					));
					
					
					sheet.addMergedRegion(new CellRangeAddress(
							SavFieldNameLigne, //first row (0-based)
							ligne, //last row  (0-based)
					        8, //first column (0-based)
					        8  //last column  (0-based)
					));
					
					
					sheet.addMergedRegion(new CellRangeAddress(
							SavFieldNameLigne, //first row (0-based)
							ligne, //last row  (0-based)
					        9, //first column (0-based)
					        9  //last column  (0-based)
					));
					
					
					if(modified)
					{
						
					sheet.addMergedRegion(new CellRangeAddress(
							SavFieldNameLigne, //first row (0-based)
							ligne, //last row  (0-based)
					        10, //first column (0-based)
					        10  //last column  (0-based)
					));
					
					sheet.addMergedRegion(new CellRangeAddress(
							SavFieldNameLigne, //first row (0-based)
							ligne, //last row  (0-based)
					        11, //first column (0-based)
					        11  //last column  (0-based)
					));

					}

                  }
				}	
 

				}
			    
			    
		if(SavLigne!=ligne)
		{
				 if(alone==false)
				 {      
						sheet.addMergedRegion(new CellRangeAddress(
								SavLigne, //first row (0-based)
								ligne, //last row  (0-based)
						        9, //first column (0-based)
						        9  //last column  (0-based)
						));
						
						
						sheet.addMergedRegion(new CellRangeAddress(
								SavLigne, //first row (0-based)
								ligne, //last row  (0-based)
						        8, //first column (0-based)
						        8  //last column  (0-based)
						));
						
						
						sheet.addMergedRegion(new CellRangeAddress(
								SavLigne, //first row (0-based)
								ligne, //last row  (0-based)
						        7, //first column (0-based)
						        7  //last column  (0-based)
						));
						
						if(modified)
						{
							
						sheet.addMergedRegion(new CellRangeAddress(
								SavLigne, //first row (0-based)
								ligne, //last row  (0-based)
						        10, //first column (0-based)
						        10  //last column  (0-based)
						));
						

						sheet.addMergedRegion(new CellRangeAddress(
								SavLigne, //first row (0-based)
								ligne, //last row  (0-based)
						        11, //first column (0-based)
						        11  //last column  (0-based)
						));
						}
				 
				 }
			
		
				sheet.addMergedRegion(new CellRangeAddress(
						SavLigne, //first row (0-based)
						ligne, //last row  (0-based)
				        5, //first column (0-based)
				        5  //last column  (0-based)
				));
				

				sheet.addMergedRegion(new CellRangeAddress(
						SavLigne, //first row (0-based)
						ligne, //last row  (0-based)
				        0, //first column (0-based)
				        0  //last column  (0-based)
				));
				
		}

			}

		 
		 ArrayList<Double> newDeductions = new ArrayList<Double>();
		 ArrayList<Double> oldDeductions = new ArrayList<Double>();
		 ArrayList<Double> newTaxes = new ArrayList<Double>();
		 ArrayList<Double> oldTaxes = new ArrayList<Double>();
		    for (int i = 0; i < nbColumn; i++) {
		    	sheet.autoSizeColumn(i, true);   
		    	
		    	
		    	
		    	
		    	if(modified)
		    	{
		    		if(i==4)
			    	{
			    		for (int j = 1; j <ligne; j++) {
			    			try{
			    			if(!sheet.getRow(j).getCell(i).toString().equals("-"))
			    				newDeductions.add(Double.valueOf(sheet.getRow(j).getCell(i).toString()).doubleValue());	
			    			}catch(Exception e){}
			    	}
			    	}
		    		
		    		if(i==3)
			    	{
			    		for (int j = 1; j <ligne; j++) {
			    			try{
			    			if(!sheet.getRow(j).getCell(i).toString().equals("-"))
			    				oldDeductions.add(Double.valueOf(sheet.getRow(j).getCell(i).toString()).doubleValue());
			    			}catch(Exception e){}
						}
			    	}
		    		
		    		
		    	if(i==9)
		    	{
		    		for (int j = 1; j <ligne; j++) {
		    			try{
		    			sum_new = sum_new + Double.valueOf(sheet.getRow(j).getCell(i).toString()).doubleValue();
		    			newTaxes.add(Double.valueOf(sheet.getRow(j).getCell(i).toString()).doubleValue());
		    			}catch(Exception e){}
					}
		    	}
		    	
		      	if(i==10)
		    	{
		    		for (int j = 1; j <ligne; j++) {
		    			try{
		    			sum_inital = sum_inital + Double.valueOf(sheet.getRow(j).getCell(i).toString()).doubleValue();
		    			oldTaxes.add(Double.valueOf(sheet.getRow(j).getCell(i).toString()).doubleValue());
		    			}catch(Exception e){}
					}
		    	}
		    	
			}
		    	}

		    if(modified)
		    {
		  
			try {
			plotTaxes(sum_inital/100000, sum_new/100000);
			InputStream my_banner_image;
		    my_banner_image = new FileInputStream("temp//taxes.png");	
            byte[] bytes = IOUtils.toByteArray(my_banner_image);
            int my_picture_id = wb.addPicture(bytes, Workbook.PICTURE_TYPE_PNG);
            my_banner_image.close();                
            HSSFPatriarch drawing = sheet.createDrawingPatriarch();
            HSSFClientAnchor my_anchor = new HSSFClientAnchor();
            my_anchor.setCol1(0);
            my_anchor.setRow1(ligne+25);           
            HSSFPicture  my_picture = drawing.createPicture(my_anchor, my_picture_id);
            my_picture.resize();            
			} catch (Exception e) {
				
				e.printStackTrace();
			}
			

			try {
				plotDeductions(newDeductions,oldDeductions);
				InputStream my_banner_image;
			    my_banner_image = new FileInputStream("temp//deductions.png");	
	            byte[] bytes = IOUtils.toByteArray(my_banner_image);
	            int my_picture_id = wb.addPicture(bytes, Workbook.PICTURE_TYPE_PNG);
	            my_banner_image.close();                
	            HSSFPatriarch drawing = sheet.createDrawingPatriarch();
	            HSSFClientAnchor my_anchor = new HSSFClientAnchor();
	            my_anchor.setCol1(7);
	            my_anchor.setRow1(ligne+5);           
	            HSSFPicture  my_picture = drawing.createPicture(my_anchor, my_picture_id);
	            my_picture.resize();            
				} catch (Exception e) {
					
					e.printStackTrace();
				}
			
			
			
			try {
				plotTaxRanges(newTaxes,oldTaxes);
				InputStream my_banner_image;
			    my_banner_image = new FileInputStream("temp//tax_ranges.png");	
	            byte[] bytes = IOUtils.toByteArray(my_banner_image);
	            int my_picture_id = wb.addPicture(bytes, Workbook.PICTURE_TYPE_PNG);
	            my_banner_image.close();                
	            HSSFPatriarch drawing = sheet.createDrawingPatriarch();
	            HSSFClientAnchor my_anchor = new HSSFClientAnchor();
	            my_anchor.setCol1(5);
	            my_anchor.setRow1(ligne+35);           
	            HSSFPicture  my_picture = drawing.createPicture(my_anchor, my_picture_id);
	            my_picture.resize();            
				} catch (Exception e) {
					
					e.printStackTrace();
				}
			
			
			
			
			
			
			
		    }
		    

		    for (int i = 0; i < sheet.getNumMergedRegions(); i++) {
				org.apache.poi.ss.util.CellRangeAddress region = sheet.getMergedRegion(i);
				int debut = region.getFirstRow();
				int fin = region.getLastRow();
				int numCol = region.getFirstColumn();
				if(debut!=fin)
				for (int j = debut; j <fin; j++) {
					try{
					row=sheet.getRow(j+1);
					cell=row.getCell(numCol);
					cell.setCellValue("");
					}catch(Exception e){}
					}
			}
           row=sheet.createRow(ligne+1);
           row.createCell(6).setCellValue("Total");
           row.getCell(6).setCellStyle(my_style);
           cell = row.createCell(7);
           cell.setCellStyle(my_style);
		    String strFormula= "CEILING(SUBTOTAL(109,H2:H"+(ligne+1)+"),3)";
		    cell.setCellType(HSSFCell.CELL_TYPE_FORMULA);
		    cell.setCellFormula(strFormula);
           
		    cell = row.createCell(8);
	           cell.setCellStyle(my_style);
			    strFormula= "CEILING(SUBTOTAL(109,I2:I"+(ligne+1)+"),3)";
			    cell.setCellType(HSSFCell.CELL_TYPE_FORMULA);
			    cell.setCellFormula(strFormula);
			    
			    cell = row.createCell(9);
		           cell.setCellStyle(my_style);
				    strFormula= "SUBTOTAL(109,J2:J"+(ligne+1)+")";
				    cell.setCellType(HSSFCell.CELL_TYPE_FORMULA);
				    cell.setCellFormula(strFormula);
            
				 
					    
				    if(modified)
				    {
				    	
				    	   cell = row.createCell(10);
				           cell.setCellStyle(my_style);
						    strFormula= "SUBTOTAL(109,k2:k"+(ligne+1)+")";
						    cell.setCellType(HSSFCell.CELL_TYPE_FORMULA);
						    cell.setCellFormula(strFormula);
		            
						    
				    	 cell = row.createCell(11);
				           cell.setCellStyle(my_style);
						    strFormula= "SUBTOTAL(109,L2:L"+(ligne+1)+")";
						    cell.setCellType(HSSFCell.CELL_TYPE_FORMULA);
						    cell.setCellFormula(strFormula);
		            
				    }
				    
		    try {
			      fileOut = new FileOutputStream(path);
			      wb.write(fileOut);
			      fileOut.close();
			    } catch (Exception e) {
			      e.printStackTrace();
			    }
	}
	
	
	
	
	public static void saveExcelForClasses(String ADName, LinkedList<EObject>taxpayers)
	{
		

	    FileOutputStream fileOut;
 	    double sum_inital=0;
	    double sum_new =0;
	    final File f = new File("");
	    final String dossierPath = f.getAbsolutePath().replace("\\", "//");
	    final String apiSamplePath = dossierPath +"//SimulationResult//"; 
	    final String file_name=ADName+".xls";
	    String path =  apiSamplePath + file_name;
	    int nbColumn=20;
	    
	    HSSFWorkbook wb = new HSSFWorkbook();

	    HSSFSheet sheet = wb.createSheet("Simulation results");
	 

	    HSSFRow row = sheet.createRow(0);	  
	    HSSFCellStyle my_style = wb.createCellStyle();
        HSSFFont my_font=wb.createFont();
        my_font.setFontHeightInPoints((short) 16);
        my_font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        my_style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        my_style.setAlignment(CellStyle.ALIGN_CENTER);
        my_style.setFont(my_font);
	    row.setRowStyle(my_style);
	    HSSFCell cell = row.createCell(0);
	    cell.setCellValue("Taxpayers");
	    cell.setCellStyle(my_style);
	    row.createCell(1).setCellValue("Old Abatt. Extra-Pro.");
	    row.getCell(1).setCellStyle(my_style);
	    row.createCell(2).setCellValue("New Abatt. Extra-Pro.");
	    row.getCell(2).setCellStyle(my_style);
	    row.createCell(3).setCellValue("Old Tax Class");
	    row.getCell(3).setCellStyle(my_style);
	    row.createCell(4).setCellValue("New Tax Class");
	    row.getCell(4).setCellStyle(my_style);
	    row.createCell(5).setCellValue("Income");
	    row.getCell(5).setCellStyle(my_style);
	    row.createCell(6).setCellValue("Income Type");
	    row.getCell(6).setCellStyle(my_style);
	    row.createCell(7).setCellValue("Gross Income (per year)");
	    row.getCell(7).setCellStyle(my_style);
	    row.createCell(8).setCellValue("Taxable Income (per year)");
	    row.getCell(8).setCellStyle(my_style);
	    row.createCell(9).setCellValue("New Taxes (per year)");
	    row.getCell(9).setCellStyle(my_style);
	    row.createCell(10).setCellValue("Old Taxes (per year)");
		row.getCell(10).setCellStyle(my_style);
		row.createCell(11).setCellValue("Loss/Gain");
		row.getCell(11).setCellStyle(my_style);
		row.createCell(12).setCellValue("SSNo");
		row.getCell(12).setCellStyle(my_style);

	 
	    int ligne=0;
	  
	    
		 for (int j = 0; j < taxpayers.size(); j++) {

			 System.out.println("Taxpayer "+j);
			 boolean alone=false;
  			 ligne=ligne+1;
			 int SavLigne=ligne;

			    
			    row=sheet.createRow(SavLigne);
			    row.setRowStyle(my_style);
				cell = row.createCell(0);
				cell.setCellValue(taxpayers.get(j).eClass().getName()+" "+(j+1));
				
                OCLForJAVA.init("",taxpayers.get(j));
			    String  tempOCL= "self.incomes ";
			    Collection<EObject> incomes = OCLForJAVA.evaluateECollection(taxpayers.get(j),tempOCL,"incomes11","Income","Set"); 
			    Iterator<EObject> it = incomes.iterator();
			    boolean first=true;
			    while(it.hasNext()){
			    EObject income=it.next();	
			    
			    if(first==false)
				{	
				ligne=ligne+1; 
				row=sheet.createRow(ligne);
				 row.setRowStyle(my_style);
				}
				else {row=sheet.getRow(ligne);
				 row.setRowStyle(my_style);
				}
			    first=false;
				cell = row.createCell(5);cell.setCellValue(income.eClass().getName());

				cell = row.createCell(2);cell.setCellValue(0);
				cell = row.createCell(1);
				OCLForJAVA.init("",taxpayers.get(j));
			    tempOCL= "self.AEP_deduction";
				double theOld= OCLForJAVA.evaluateDouble(taxpayers.get(j), tempOCL, "type"); 
				cell.setCellValue(theOld);
				
				cell = row.createCell(12);
				OCLForJAVA.init("",taxpayers.get(j));
			    tempOCL= "self.SSNo";
				String SSNo= OCLForJAVA.evaluateString(taxpayers.get(j), tempOCL, "SSNo"); 
				cell.setCellValue(SSNo);
				
				for (int k = 0; k < 1; k++) {
				
				 if(k!=0)
					{	
					ligne=ligne+1; 
					row=sheet.createRow(ligne);
					 row.setRowStyle(my_style);
					}
					else {row=sheet.getRow(ligne);}
				 
		
						
					 double gross = 0;
					 double taxable=0;
					 double new_taxes =0;
					 double old_taxes =0;
					//get taxation_year
					 OCLForJAVA.init("",income);
					 tempOCL= "self.taxPayer.from_agent.taxation_year";
					 OCLForJAVA.evaluateInt(income, tempOCL, "tax_p"); 

               		//add OLD Tax Category
					 OCLForJAVA.init("",income);
					 tempOCL= "self.taxPayer.getTaxClass(self.taxPayer.from_agent.taxation_year)";
					 EObject old_tax_class = OCLForJAVA.evaluateEObject(income, tempOCL, "tax_class",""); 
					 cell = row.createCell(3);cell.setCellValue(old_tax_class.toString());
					 
		          	//add Modified Tax Category
					OCLForJAVA.init("",income);
					tempOCL= "self.taxPayer.getTaxClassModified(self.taxPayer.from_agent.taxation_year)";
					EObject new_tax_class = OCLForJAVA.evaluateEObject(income, tempOCL, "tax_class",""); 
					cell = row.createCell(4);cell.setCellValue(new_tax_class.toString());

					//add  income type
					OCLForJAVA.init("",income);
					 tempOCL= "let income_type:String= if(self.income_type.oclIsTypeOf(Employment_Income)) then 'Employment' else if (self.income_type.oclIsTypeOf(Pensions_and_Annuities_Income)) then 'Pension' else 'Other' endif endif in income_type ";
					 String type = OCLForJAVA.evaluateString(income, tempOCL, "type"); 
					 cell = row.createCell(6);cell.setCellValue(type);
						
						OCLForJAVA.init("",income);
						 //add gross
						 tempOCL=  "let amount:Real= self.income_per_year() in self.taxPayer.from_law.precision(amount,2)";		 
						 gross = OCLForJAVA.evaluateDouble(income, tempOCL, "gross"); 
						 cell = row.createCell(7);cell.setCellValue(gross);
							
							//add taxable
						 tempOCL=  "let amount:Real= self.taxPayer.from_law.getTaxableIncomePerYear(self) in amount";
						 taxable = OCLForJAVA.evaluateDouble(income, tempOCL, "taxable"); 
						 cell = row.createCell(8);cell.setCellValue(taxable);
						 OCLForJAVA.init("",taxpayers.get(j));
					 
					 OCLForJAVA.init("",income);
					
					 tempOCL= "let income_type1:String= if(self.income_type.oclIsTypeOf(Employment_Income)) then 'Employment' else if (self.income_type.oclIsTypeOf(Pensions_and_Annuities_Income)) then 'Pension' else 'Other' endif endif in "
						 			+ "let amount:Real= self.taxPayer.from_law.getTaxableIncomePerYear(self) in "
							 		+ " self.taxPayer.from_law.calculate_taxes_per_year(amount, Tax_Class_Category::"+new_tax_class.toString()+", income_type1)";

				
					 new_taxes = OCLForJAVA.evaluateDouble(income, tempOCL, "taxes"); 
					 cell = row.createCell(9);cell.setCellValue(new_taxes);
					 OCLForJAVA.init("",income);
					 tempOCL=  "self.taxPayer.getIsTaxedJointly(self.taxPayer.from_agent.taxation_year)";
					 boolean is_taxedJointly = OCLForJAVA.evaluateBoolean(income, tempOCL, "is_taxedJointly"); 
					 alone = ! is_taxedJointly;
					 EObject spouse=null;
			 
			if(alone)
			{ 
				
				OCLForJAVA.init("",income);

				 tempOCL=  "let amount:Real= self.income_per_year() in self.taxPayer.from_law.precision(amount,2)";		 
				 gross = OCLForJAVA.evaluateDouble(income, tempOCL, "gross"); 

				 tempOCL=  "let amount:Real= self.taxPayer.from_law.getTaxableIncomePerYear(self) in amount";
				 taxable = OCLForJAVA.evaluateDouble(income, tempOCL, "taxable"); 

			 tempOCL= "let income_type1:String= if(self.income_type.oclIsTypeOf(Employment_Income)) then 'Employment' else if (self.income_type.oclIsTypeOf(Pensions_and_Annuities_Income)) then 'Pension' else 'Other' endif endif in "
			 			+ "let amount:Real= self.taxPayer.from_law.getTaxableIncomePerYear(self) in "
				 		+ " self.taxPayer.from_law.calculate_taxes_per_year(amount, Tax_Class_Category::"+old_tax_class.toString()+", income_type1)";

			 old_taxes = OCLForJAVA.evaluateDouble(income, tempOCL, "taxes"); 
			 cell = row.createCell(10);cell.setCellValue(old_taxes);
			}
			else
			{
		
							boolean enLigne = false;
							double AEP=0;
							OCLForJAVA.init("",taxpayers.get(j));
							tempOCL="if(self.getSpouse(from_agent.taxation_year).oclIsKindOf(Tax_Payer)) then self.AEP_deduction + self.getSpouse(from_agent.taxation_year).oclAsType(Tax_Payer).AEP_deduction else self.AEP_deduction endif";
							AEP = OCLForJAVA.evaluateDouble(taxpayers.get(j), tempOCL, "AEP"); 
							
							
							 
							boolean mixed_couple=false;
							
						 //calcul taxes
						 double taxableEmployment=0;
						    tempOCL=  "let amount:Bag(Real)= self.incomes->select(income_type.oclIsTypeOf(Employment_Income))->iterate(inc: Income;  acc:Bag(Real)=Bag{}  | acc->including(self.from_law.getTaxableIncomePerYear(inc))) in "
						 		+ "let amount1:Bag(Real)= if(self.getSpouse(from_agent.taxation_year).oclIsKindOf(Tax_Payer)) then self.getSpouse(from_agent.taxation_year).oclAsType(Tax_Payer).incomes->select(income_type.oclIsTypeOf(Employment_Income))->iterate(inc: Income;  acc:Bag(Real)=Bag{}  | acc->including(self.from_law.getTaxableIncomePerYear(inc))) else Bag{} endif in "
						 		+ " amount->sum()+amount1->sum()";
						    taxableEmployment = OCLForJAVA.evaluateDouble(taxpayers.get(j), tempOCL, "taxable_empyment"); 
						    if(taxableEmployment>0)
						    {
						    	enLigne=true;
						    	taxableEmployment = taxableEmployment - AEP;
						    }
						   
				    	 
				    	 double taxablePensiom=0;
						    tempOCL=  "let amount:Bag(Real)= self.incomes->select(income_type.oclIsTypeOf(Pensions_and_Annuities_Income))->iterate(inc: Income;  acc:Bag(Real)=Bag{}  | acc->including(self.from_law.getTaxableIncomePerYear(inc))) in "
						 		+ "let amount1:Bag(Real)=  if(self.getSpouse(from_agent.taxation_year).oclIsKindOf(Tax_Payer)) then  self.getSpouse(from_agent.taxation_year).oclAsType(Tax_Payer).incomes->select(income_type.oclIsTypeOf(Pensions_and_Annuities_Income))->iterate(inc: Income;  acc:Bag(Real)=Bag{}  | acc->including(self.from_law.getTaxableIncomePerYear(inc))) else Bag{} endif in "
						 		+ " amount->sum()+amount1->sum()";
						    taxablePensiom = OCLForJAVA.evaluateDouble(taxpayers.get(j), tempOCL, "taxable_empyment"); 
						    if(enLigne==false && taxablePensiom>0)
						    {
						    	enLigne=true;
						    	taxablePensiom = taxablePensiom - AEP;
						    }
						 
				  		 double taxableOther=0;
						    tempOCL=  "let amount:Bag(Real)= self.incomes->select(not income_type.oclIsTypeOf(Pensions_and_Annuities_Income) and not income_type.oclIsTypeOf(Employment_Income) )->iterate(inc: Income;  acc:Bag(Real)=Bag{}  | acc->including(self.from_law.getTaxableIncomePerYear(inc))) in "
						 		+ "let amount1:Bag(Real)=  if(self.getSpouse(from_agent.taxation_year).oclIsKindOf(Tax_Payer)) then self.getSpouse(from_agent.taxation_year).oclAsType(Tax_Payer).incomes->select(not income_type.oclIsTypeOf(Pensions_and_Annuities_Income) and not income_type.oclIsTypeOf(Employment_Income) )->iterate(inc: Income;  acc:Bag(Real)=Bag{}  | acc->including(self.from_law.getTaxableIncomePerYear(inc))) else Bag{} endif in "
						 		+ " amount->sum()+amount1->sum()";
						    taxableOther = OCLForJAVA.evaluateDouble(taxpayers.get(j), tempOCL, "taxable_empyment"); 
						    if(enLigne==false && taxableOther>0)
						    {
						    	enLigne=true;
						    	taxableOther = taxableOther - AEP;
						    }
						  
						    if(mixed_couple==false)
							{
				    	     tempOCL= " self.from_law.calculate_taxes_per_year("+taxableEmployment+", Tax_Class_Category::Two, 'Employment')";
				    	     double taxesEmployment = OCLForJAVA.evaluateDouble(taxpayers.get(j), tempOCL, "taxes"); 
				    	     tempOCL= " self.from_law.calculate_taxes_per_year("+taxablePensiom+", Tax_Class_Category::Two, 'Pension')";
					    	 double taxesPension = OCLForJAVA.evaluateDouble(taxpayers.get(j), tempOCL, "taxes"); 
					    	 tempOCL= " self.from_law.calculate_taxes_per_year("+taxableOther+", Tax_Class_Category::Two, 'Pension')";
					    	 double taxesOther = OCLForJAVA.evaluateDouble(taxpayers.get(j), tempOCL, "taxes"); 
						 
					    	 old_taxes=taxesEmployment+taxesPension+taxesOther;
					    	 cell = row.createCell(10);
							}
						    
			
						    else
						    {
						    	tempOCL="self.oclIsTypeOf(Non_Resident_Tax_Payer)";
						    	boolean is_The_resident = OCLForJAVA.evaluateBoolean(taxpayers.get(j), tempOCL, "mixed_couple"); 
						    	if(is_The_resident==false)
						    	{
						    		new_taxes=0;
						    		old_taxes=0;
						    	}
						    	else
						    	{
						    		 double taxableEmploymentME=0;
									    tempOCL=  "let amount:Bag(Real)= self.incomes->select(income_type.oclIsTypeOf(Employment_Income))->iterate(inc: Income;  acc:Bag(Real)=Bag{}  | acc->including(self.from_law.getTaxableIncomePerYear(inc))) in "
									 		+ " amount->sum()";
									    taxableEmploymentME = OCLForJAVA.evaluateDouble(taxpayers.get(j), tempOCL, "taxable_empyment"); 
								
							    	 
							    	 double taxablePensiomME=0;
									    tempOCL=  "let amount:Bag(Real)= self.incomes->select(income_type.oclIsTypeOf(Pensions_and_Annuities_Income))->iterate(inc: Income;  acc:Bag(Real)=Bag{}  | acc->including(self.from_law.getTaxableIncomePerYear(inc))) in "
									 		+ " amount->sum()";
									    taxablePensiom = OCLForJAVA.evaluateDouble(taxpayers.get(j), tempOCL, "taxable_empyment"); 
									
									 
							  		 double taxableOtherME=0;
									    tempOCL=  "let amount:Bag(Real)= self.incomes->select(not income_type.oclIsTypeOf(Pensions_and_Annuities_Income) and not income_type.oclIsTypeOf(Employment_Income) )->iterate(inc: Income;  acc:Bag(Real)=Bag{}  | acc->including(self.from_law.getTaxableIncomePerYear(inc))) in "
									 		+ " amount->sum()";
									    taxableOtherME = OCLForJAVA.evaluateDouble(taxpayers.get(j), tempOCL, "taxable_empyment"); 

						    		 tempOCL= " self.from_law.calculate_taxes_per_year_mixed("+taxableEmployment+","+taxableEmploymentME+", Tax_Class_Category::Two, 'Employment')";
						    	     double taxesEmployment = OCLForJAVA.evaluateDouble(taxpayers.get(j), tempOCL, "taxes"); 
						    	     tempOCL= " self.from_law.calculate_taxes_per_year_mixed("+taxablePensiom+","+taxablePensiomME+", Tax_Class_Category::Two, 'Pension')";
							    	 double taxesPension = OCLForJAVA.evaluateDouble(taxpayers.get(j), tempOCL, "taxes"); 
							    	 tempOCL= " self.from_law.calculate_taxes_per_year_mixed("+taxableOther+","+taxableOtherME+", Tax_Class_Category::Two, 'Pension')";
							    	 double taxesOther = OCLForJAVA.evaluateDouble(taxpayers.get(j), tempOCL, "taxes"); 
								 
							    	 old_taxes=taxesEmployment+taxesPension+taxesOther;
							    	 cell = row.createCell(10);
								
						    	}
			}
					   
						 OCLForJAVA.init("",taxpayers.get(j));
					     tempOCL= "self.getSpouse(from_agent.taxation_year).oclIsKindOf(Tax_Payer)";
						 boolean is_spouse_taxpayer = OCLForJAVA.evaluateBoolean(taxpayers.get(j), tempOCL, "is_spouse_taxpayer"); 	
						 tempOCL= "self.getSpouse(from_agent.taxation_year)";
						 spouse = OCLForJAVA.evaluateEObject(taxpayers.get(j), tempOCL, "spouse", "Physical_Person"); 
					  
						
						if(is_spouse_taxpayer==true)
						{   
							boolean spouseBeforeMe= getIsSpouseBeforeMe(spouse,taxpayers,j);
							if(spouseBeforeMe==false)
							cell.setCellValue(old_taxes);
							else 
							{
								int pos = getPostion(taxpayers,spouse);
								cell.setCellValue(old_taxes+" Spouse ==> TP"+pos);
							}
						}
					 else
					 {  
					  cell.setCellValue(old_taxes+" Not a taxpayer spouse");
					 }
			
				     
			}
	
			  cell = row.createCell(11);			  
			  if(row.getCell(10).getCellType()==0)
			  cell.setCellValue(new_taxes-old_taxes);
			  else
			  {
				  if(row.getCell(10).toString().contains("==>"))
				  cell.setCellValue(new_taxes);
				  else
				  {
					  String oldCH= row.getCell(10).toString().replace("Not a taxpayer spouse", "").trim();
					  double temp_old = Double.valueOf(oldCH).doubleValue();
					  cell.setCellValue(new_taxes-temp_old);
				  }
			  }	
				}	
 

				}
			    
			    if(SavLigne!=ligne){
				
				 if(alone==false)
				 {
						sheet.addMergedRegion(new CellRangeAddress(
								SavLigne, //first row (0-based)
								ligne, //last row  (0-based)
						        10, //first column (0-based)
						        10 //last column  (0-based)
						));
					 
				 }
			  
		
				sheet.addMergedRegion(new CellRangeAddress(
						SavLigne, //first row (0-based)
						ligne, //last row  (0-based)
				        0, //first column (0-based)
				        0  //last column  (0-based)
				));
				
				
				  
				sheet.addMergedRegion(new CellRangeAddress(
						SavLigne, //first row (0-based)
						ligne, //last row  (0-based)
				        3, //first column (0-based)
				        3  //last column  (0-based)
				));
				
				
				  
				sheet.addMergedRegion(new CellRangeAddress(
						SavLigne, //first row (0-based)
						ligne, //last row  (0-based)
				        4, //first column (0-based)
				        4  //last column  (0-based)
				));
				

			}
	}

		 

		 ArrayList<Double> newTaxes = new ArrayList<Double>();
		 ArrayList<Double> oldTaxes = new ArrayList<Double>();
		    for (int i = 0; i < nbColumn; i++) {
		    	sheet.autoSizeColumn(i, true);   
		    	
		    

		    		
		    	if(i==9)
		    	{  LinkedList<String> sav_Spouses = new LinkedList<String>();
		    		for (int j = ligne; j >=1 ; j--) {
		    			try{
		    			double toAdd=0;
		    			//System.out.println(sav_Spouses.contains(String.valueOf(j-1)));
		    			if(!sav_Spouses.contains(String.valueOf(j)))
		    			{
		    			if(sheet.getRow(j).getCell(10).toString().contains("Spouse")==false)
		    			{
		    			toAdd = Double.valueOf(sheet.getRow(j).getCell(i).toString()).doubleValue();
		    			sum_new = sum_new + toAdd;
		    			newTaxes.add(toAdd);
		    			sav_Spouses.add(String.valueOf(j));
		    			}
		    			else
		    			{
		    				toAdd = Double.valueOf(sheet.getRow(j).getCell(i).toString()).doubleValue();
		    				int num_spouse = Double.valueOf(sheet.getRow(j).getCell(10).toString().split("Spouse ==> TP")[1]).intValue();
		    				double part_spouse = Double.valueOf(sheet.getRow(num_spouse).getCell(i).toString()).doubleValue();
		    		
		    				sum_new = toAdd +part_spouse+sum_new;
		    				newTaxes.add(toAdd +part_spouse);
		    				sav_Spouses.add(String.valueOf(num_spouse));
		    				
		    			}
		    			}
		    			}catch(Exception e){
		    			
		    			}
					}
		    	}
		    	
		      	if(i==10)
		    	{
		    		for (int j = 1; j <=ligne; j++) {
		    			try{
		    				
		    			sum_inital = sum_inital + Double.valueOf(sheet.getRow(j).getCell(i).toString()).doubleValue();
		    			oldTaxes.add(Double.valueOf(sheet.getRow(j).getCell(i).toString()).doubleValue());
		    			}catch(Exception e){
		    	
		    			}
					}
		    	}
		    	
			
		    	}

			try {
			plotTaxes(sum_inital/100000, sum_new/100000);
			InputStream my_banner_image;
		    my_banner_image = new FileInputStream("temp//taxes.png");	
            byte[] bytes = IOUtils.toByteArray(my_banner_image);
            int my_picture_id = wb.addPicture(bytes, Workbook.PICTURE_TYPE_PNG);
            my_banner_image.close();                
            HSSFPatriarch drawing = sheet.createDrawingPatriarch();
            HSSFClientAnchor my_anchor = new HSSFClientAnchor();
            my_anchor.setCol1(0);
            my_anchor.setRow1(ligne+5);           
            HSSFPicture  my_picture = drawing.createPicture(my_anchor, my_picture_id);
            my_picture.resize();            
			} catch (Exception e) {
				
				e.printStackTrace();
			}
			

			
			try {
				plotTaxRanges(newTaxes,oldTaxes);
				InputStream my_banner_image;
			    my_banner_image = new FileInputStream("temp//tax_ranges.png");	
	            byte[] bytes = IOUtils.toByteArray(my_banner_image);
	            int my_picture_id = wb.addPicture(bytes, Workbook.PICTURE_TYPE_PNG);
	            my_banner_image.close();                
	            HSSFPatriarch drawing = sheet.createDrawingPatriarch();
	            HSSFClientAnchor my_anchor = new HSSFClientAnchor();
	            my_anchor.setCol1(4);
	            my_anchor.setRow1(ligne+5);           
	            HSSFPicture  my_picture = drawing.createPicture(my_anchor, my_picture_id);
	            my_picture.resize();            
				} catch (Exception e) {
					
					e.printStackTrace();
				}
			
			
			
			
			
			
			
		    
		    for (int i = 0; i < sheet.getNumMergedRegions(); i++) {
						org.apache.poi.ss.util.CellRangeAddress region = sheet.getMergedRegion(i);
						int debut = region.getFirstRow();
						int fin = region.getLastRow();
						int numCol = region.getFirstColumn();
						if(debut!=fin)
						for (int j = debut; j <fin; j++) {
							try{
							row=sheet.getRow(j+1);
							cell=row.getCell(numCol);
							cell.setCellValue("");
							}catch(Exception e){}
							}
					}
           row=sheet.createRow(ligne+1);
           row.createCell(6).setCellValue("Total");
           row.getCell(6).setCellStyle(my_style);
           
           cell = row.createCell(7);
           cell.setCellStyle(my_style);
		    String strFormula= "CEILING(SUBTOTAL(109,H2:H"+(ligne+1)+"),3)";
		    cell.setCellType(HSSFCell.CELL_TYPE_FORMULA);
		    cell.setCellFormula(strFormula);
           
		    cell = row.createCell(8);
	           cell.setCellStyle(my_style);
			     strFormula= "CEILING(SUBTOTAL(109,I2:I"+(ligne+1)+"),3)";
			    cell.setCellType(HSSFCell.CELL_TYPE_FORMULA);
			    cell.setCellFormula(strFormula);
	           
           
           cell = row.createCell(9);
           cell.setCellStyle(my_style);
		     strFormula= "SUBTOTAL(109,J2:J"+(ligne+1)+")";
		    cell.setCellType(HSSFCell.CELL_TYPE_FORMULA);
		    cell.setCellFormula(strFormula);
           
		    cell = row.createCell(10);
	           cell.setCellStyle(my_style);
			    strFormula= "SUBTOTAL(109,K2:K"+(ligne+1)+")";
			    cell.setCellType(HSSFCell.CELL_TYPE_FORMULA);
			    cell.setCellFormula(strFormula);
			    
			    cell = row.createCell(11);
		           cell.setCellStyle(my_style);
				    strFormula= "SUBTOTAL(109,L2:L"+(ligne+1)+")";
				    cell.setCellType(HSSFCell.CELL_TYPE_FORMULA);
				    cell.setCellFormula(strFormula);
            
				 
					    
				    
		    try {
			      fileOut = new FileOutputStream(path);
			      wb.write(fileOut);
			      fileOut.close();
			    } catch (Exception e) {
			      e.printStackTrace();
			    }
	}
	
	
	 public static void plotTaxes(double vInitial, double VFinal){
         try {
                
                 DefaultCategoryDataset my_bar_chart_dataset = new DefaultCategoryDataset();
                 my_bar_chart_dataset.addValue(vInitial, "Initial", " ");
                 my_bar_chart_dataset.addValue(VFinal, "Modified", " ");
                 JFreeChart BarChartObject=ChartFactory.createBarChart("Impact of the simulated modification on the Government's revenue","Collected taxes","Amount (100k euros)",my_bar_chart_dataset,PlotOrientation.VERTICAL,true,true,false);                                                   
                 int width=640; 
                 int height=480;             
                 File BarChart=new File("temp//taxes.png");              
                 ChartUtilities.saveChartAsPNG(BarChart,BarChartObject,width,height); 
         }
         catch (Exception e)
         {
             System.out.println(e);
         }
     }
	 public static void plotTaxRanges(ArrayList<Double> newTaxes,ArrayList<Double> oldTaxes){
         try {
        	 
        	 Double max1 = max(newTaxes);
        	 Double max2 = max(oldTaxes);
        	 Double max = Math.max(max1, max2);
        	 int nbBins = (Double.valueOf(30000).intValue()/1000) +1;
        	 DefaultCategoryDataset my_bar_chart_dataset = new DefaultCategoryDataset();
        	 int size=newTaxes.size();
        	 for (int i = -1; i < nbBins; i++) {
        		 double lower=0; double upper =0;
            	 if(i!=-1)
    			 {
            	  lower = (3000*i)+1;
    			  upper =3000 * (i+1);
    			 }
            	 
            	 int v1 = counter(lower,upper,newTaxes);	 
    			 int v2 = counter(lower,upper,oldTaxes);
    			 
    			System.out.println(lower+"-->"+upper+" "+v2+" "+v1);
            	 
            	 if(upper<=30000)
    			 {
    				 if((v1!=0 || v2!=0) || i==-1)
    				 { 
    						my_bar_chart_dataset.addValue((v2*100)/size, "Initial", i!=-1?String.valueOf(lower)+"-":"0");
    		                my_bar_chart_dataset.addValue((v1*100)/size, "Modified", i!=-1?String.valueOf(lower)+"-":"0");
    				 }
    			 
    			 }
    			 else
    			 { 		v1 = counter(lower,max,oldTaxes);	 
			         	v2 = counter(lower,max,newTaxes);
    			    	my_bar_chart_dataset.addValue((v1*100)/size, "Initial", ">30000");
    	                my_bar_chart_dataset.addValue((v2*100)/size, "Modified", ">30000");
    			 }
            	 
	
		
			 }
               

                 JFreeChart BarChartObject=ChartFactory.createBarChart("Impact of the simulated modification on tax ranges","Tax ranges (1000 euros)","Percentage of households",my_bar_chart_dataset,PlotOrientation.VERTICAL,true,true,false);                                                   
                 int width=1500; 
                 int height=480;             
                 File BarChart=new File("temp//tax_ranges.png");              
                 ChartUtilities.saveChartAsPNG(BarChart,BarChartObject,width,height); 
         }
         catch (Exception e)
         {
             System.out.println(e);
         }
     }
	 
	 public static void plotDeductions(ArrayList<Double> newDeductions,ArrayList<Double> oldDeductions){
         try {
        	 
        	 Double max1 = max(newDeductions);
        	 Double max2 = max(oldDeductions);
        	 Double max = Math.max(max1, max2);
        	 int size=newDeductions.size();
        	 int nbBins = (Double.valueOf(String.valueOf(3005)).intValue()/500) +1;
        	 DefaultCategoryDataset my_bar_chart_dataset = new DefaultCategoryDataset();
        	 for (int i = -1; i < nbBins; i++) {
        	 double lower=0; double upper =0;
        	 if(i!=-1)
			 {
        	  lower = (500*i)+1;
			  upper = 500 * (i+1);
			 }
		
			 
			 
			 int v1 = counter(lower,upper,oldDeductions);	 
			 int v2 = counter(lower,upper,newDeductions);
			 
			 if(upper<=3000)
			 {
				 if((v1!=0 || v2!=0) || i==-1)
				 { 
                my_bar_chart_dataset.addValue((v1*100)/size, "Initial", i!=-1?String.valueOf(lower/1000)+"-"+String.valueOf(upper/1000)+"":"0");
                my_bar_chart_dataset.addValue((v2*100)/size, "Modified", i!=-1?String.valueOf(lower/1000)+"-"+String.valueOf(upper/1000)+"":"0");
				 }
			 
			 }
			 else
			 { 		v1 = counter(lower,max,oldDeductions);	 
			    	v2 = counter(lower,max,newDeductions);
			    	my_bar_chart_dataset.addValue((v1*100)/size, "Initial", ">3000");
	                my_bar_chart_dataset.addValue((v2*100)/size, "Modified", ">3000");
			 }
			 }
               

                 JFreeChart BarChartObject=ChartFactory.createBarChart("Impact of the simulated modification on deduction ranges","Deduction ranges (1000 euros)","Percentage of households",my_bar_chart_dataset,PlotOrientation.VERTICAL,true,true,false);                                                   
                 int width=1000; 
                 int height=480;             
                 File BarChart=new File("temp//deductions.png");              
                 ChartUtilities.saveChartAsPNG(BarChart,BarChartObject,width,height); 
         }
         catch (Exception e)
         {
             System.out.println(e);
         }
     }
	 
	 public static  int counter(Double lower, Double upper, ArrayList<Double> liste)
	 {
		 int res = 0 ;
		 for (int i = 0; i < liste.size(); i++) {
			if(liste.get(i)>=lower && liste.get(i)<=upper)
				res=res+1;
		}
		  return res;
	 }
	 public static Double max(ArrayList<Double> deductions)
	 {
		 Double res = 500.0;
		 for (int i = 0; i < deductions.size(); i++) {
			if(deductions.get(i)>res)
				res= deductions.get(i);
		}
		 return res;
	 }
	 
	 public static LinkedList<String> getNamesOFModels(String fin)
	 {
		 LinkedList<String> res = new LinkedList<String>();
		 BufferedReader br =null;
		 try{
		 br = new BufferedReader(new FileReader(fin));
		 
			String line = null;
			while ((line = br.readLine()) != null) {
				line=line.trim();
				if(line.equals("")==false)
				{
					
					if(line.contains("//")==false)
					
					{
					if(line.contains("#")==false)
					res.add(line);
				else
					res.add(line.split("#")[0]);
					}
					}
			}
		 }catch(Exception e)
		 {e.printStackTrace();}
		 finally{
			if(br!=null)
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		 }
		 return res;
	 }
	
	 public static LinkedList<String> getNamesOfModifedAttributes(String fin)
	 {
		 LinkedList<String> res = new LinkedList<String>();
		 BufferedReader br =null;
		 try{
		 br = new BufferedReader(new FileReader(fin));
		 
			String line = null;
			while ((line = br.readLine()) != null) {
				line=line.trim();
				if(line.equals("")==false)
				{  if(line.contains("#"))
					res.add(line.split("#")[1]);
				}
			}
		 }catch(Exception e)
		 {e.printStackTrace();}
		 finally{
			if(br!=null)
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		 }
		 return res;
	 }
	 
	 public static LinkedList<String> getMyattributes(String fin, String Ad_name)
	 {
		 LinkedList<String> res = new LinkedList<String>();
		 BufferedReader br =null;
		 try{
		 br = new BufferedReader(new FileReader(fin));
		 
			String line = null;
			while ((line = br.readLine()) != null) {
				line=line.trim();
				if(line.equals("")==false)
				{  String[] temp=null;
				if(line.contains("#"))
					temp=line.split("#");
				 if(temp[0].trim().equals(Ad_name.trim()))
					res.add(temp[1]);
				}
			}
		 }catch(Exception e)
		 {e.printStackTrace();}
		 finally{
			if(br!=null)
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		 }
		 return res;
	 }
	 public static boolean getIsSpouseBeforeMe(EObject spouse,LinkedList<EObject> taxpayers,int size)
	 {boolean res=false;
	 int i=0;
	 while(i<size&&res==false&&i<taxpayers.size())
	 {
		 if(taxpayers.get(i)==spouse)
			 res=true;
		 else i++;
	 }
	 return res;}
	 
	 public static int getPostion(LinkedList<EObject> taxpayers, EObject tp)
	 {
	
		 for (int i = 0; i < taxpayers.size(); i++) {
			 if(taxpayers.get(i)==tp)
				 return i+1;
			
		}
		 return -1;
	 }
	 
	 public static void save(String name, EList<EObject> elements){

		 
		    
		    ResourceSetImpl resourceSet = new ResourceSetImpl(); 
		    resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl()); 
		    final File f = new File("");
		    final String dossierPath = f.getAbsolutePath().replace("\\", "//");
		    final String apiSamplePath = dossierPath +"//SimulationResult//"; 
		    final String file_name="taxpayers"+name+".xmi";
		    String path = "file://" + apiSamplePath + file_name;
		    URI uri = URI.createURI(path); 
		    Resource resource = resourceSet.createResource(uri); 
		    resource.getContents().addAll(elements); 

		    
		    try {
				resource.save(null);
			} catch (IOException e) {
				e.printStackTrace();
			} }
	     
	   


	
	public static void readLineByLine(File oclFile,EObject taxpayer) throws IOException
	{
		BufferedReader br = new BufferedReader(new FileReader(oclFile));
		String line;
		String all="";
		while ((line = br.readLine()) != null) {
		   if(line.contains("let ")&&line.contains(" in"))
		   {
			String name = getNameFromLet(line);
		    all=all+" \n"+line;
		    String toBeEvaluated=all+" "+name;
		    System.err.println(toBeEvaluated );
		    OCLExpression<EClassifier> query;
			try {
				OclAST.helper.setContext(taxpayer.eClass());
				query = OclAST.helper.createQuery(toBeEvaluated);

				OclAST.ocl.evaluate(taxpayer, query);
			
				taxpayer.eAllContents();
			    System.out.println(OclAST.ocl.evaluate(taxpayer, query));
			   // System.err.println( taxpayers.get(j).eGet(taxpayer.eClass().getEAllAttributes().get(1)));
			   
			} catch (ParserException e) {
				e.printStackTrace();
			}
			
		   }
		}
		br.close();
		
	}
	
	public static String getNameFromLet(String letExpression)
	{   letExpression=letExpression.replace("let ", "");
		return letExpression.substring(0,letExpression.indexOf(":")).trim();}
	
	public static String getTypeFromLet(String letExpression)
	{letExpression=letExpression.replace("let ", "");
	 return letExpression.substring(letExpression.indexOf(":")+1,letExpression.indexOf("=")-1).replace("TaxCard::", "").trim();}
	
}
