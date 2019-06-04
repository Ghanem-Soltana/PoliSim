package temp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
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
import org.eclipse.ocl.ParserException;
import org.eclipse.ocl.ecore.Constraint;
import org.eclipse.ocl.ecore.OCL;
import org.eclipse.ocl.expressions.OCLExpression;
import org.eclipse.ocl.helper.OCLHelper;
import org.eclipse.ocl.utilities.ExpressionInOCL;
import org.eclipse.uml2.uml.internal.resource.UMLResourceFactoryImpl;

import util.EmfModelReader;
import util.EmfOclParser;
import util.OCLForJAVA;
import util.OclAST;


@SuppressWarnings({ "restriction" })
public class Charts {
	
	public static HashMap<String, String> save_taxes ;
 
	

	public static void main(String[] args) {

		 long startTime = System.nanoTime();
		 final String model="//Papyrus//TaxCard.uml";
		 final String oclFile="//model//constraints.ocl";
		 final String instances_rep="//Instances//TaxpayersOfFD.xmi";
	 
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
	 	 
	 	parseExcel("tax_categories_modifications",taxpayers);
	 	
	 	
	 	long endTime = System.nanoTime();
		long duration = (endTime - startTime);
		double duration_second=(double)duration / 1000000000.0;
		System.out.println("execution time = "+Math.round(duration_second)+ " seconds");
	        
	}
	

	public static void parseExcel(String fileName, LinkedList<EObject>taxpayers)
	{
		

	    final File f = new File("");
	    final String dossierPath = f.getAbsolutePath().replace("\\", "//");
	    final String apiSamplePath = dossierPath +"//SimulationResult//"; 
	    final String file_name=fileName+".xls";
	    String path =  apiSamplePath + file_name;

	    
	    POIFSFileSystem fs;
		try {
		fs = new POIFSFileSystem(new FileInputStream(path));
	    HSSFWorkbook wb = new HSSFWorkbook(fs);
	    HSSFSheet sheet = wb.getSheetAt(0);
	    HSSFRow row;
	    int rows=sheet.getPhysicalNumberOfRows();
	    LinkedList<String> sav_Spouses = new LinkedList<String>();
    	
	    String [] binRanges = {"0-10000","10000-20000","20000-30000","30000-40000","40000-50000","50000-60000","60000-70000","70000-80000","80000-90000"
	    		, "90000-100000","100000-110000","110000-120000","120000-130000","130000-140000","140000-150000","150000-160000","160000-170000","170000-180000"
	    		,"180000-190000","190000-200000","200000-230000","230000-330000","330000-500000","500000-700000","700000-1000000","1000000-2000000"};
	    int [] countRanges = new int[binRanges.length];
	    double [] oldTaxes = new double[binRanges.length];
	    double [] newTaxes = new double[binRanges.length];
	    int [] tax_rangeOld = new int [13];
	    int [] tax_rangeNew = new int [13];
	    for (int j = 0; j < tax_rangeOld.length; j++) {
	    	tax_rangeOld[j]=0;
	    	tax_rangeNew[j]=0;
	    }
	    LinkedList<Double> diff = new LinkedList<Double>();
	    for (int j = 0; j < countRanges.length; j++) {
	    	countRanges[j]=0;
	    	oldTaxes[j]=0;
	    	newTaxes[j]=0;
	  }
	   for(int i = rows-1; i >= 1; i--) {
		  

	    	double toAddGross=0;
	    	double toAddOldTaxes=0;
	    	double toAddNewTaxes=0;
	  	  System.out.println("Tax case "+i);

	        row = sheet.getRow(i);
	        if(row != null) {
	        	if(row.getCell(2)!=null)
	        	{
	        	 if(!sheet.getRow(i).getCell(7).toString().trim().equals(""))
	        	 {
	    			
	    			if(!sav_Spouses.contains(String.valueOf(i)))
	    			{
	    				
	    			if(sheet.getRow(i).getCell(10).toString().contains("Spouse")==false)
	    			{   EObject taxpayer = getTaxPyerByID(sheet,i,taxpayers);
	    				OCLForJAVA.init("FD",taxpayer);
	    				String tempOCL = "self.getLegalUnionRecord(self.from_agent.taxation_year).oclIsUndefined()=false";
	    				boolean is_married = OCLForJAVA.evaluateBoolean(taxpayer, tempOCL, "tempOCL");
	    				
	    				
	    				
	    				toAddGross = Double.valueOf(sheet.getRow(i).getCell(7).toString()).doubleValue();
	    				toAddOldTaxes = Double.valueOf(sheet.getRow(i).getCell(10).toString().replace("Not a taxpayer spouse", "")).doubleValue();
	    				toAddNewTaxes = Double.valueOf(sheet.getRow(i).getCell(9).toString()).doubleValue();
	    				if(is_married)
	    				{
	    					 tempOCL= "self.getSpouse(from_agent.taxation_year)";
						   	 EObject spouse = OCLForJAVA.evaluateEObject(taxpayer, tempOCL, "spouse", "Physical_Person"); 
						   	 tempOCL= "not spouse.oclIsInvalid()";
						     boolean has_spouse = OCLForJAVA.evaluateBoolean(taxpayer, tempOCL, "has_spouse");
							if(spouse!=null)
							if(has_spouse)
							{
							 tempOCL= "self.getSpouse(from_agent.taxation_year).oclIsKindOf(Tax_Payer)";
							 boolean is_spouse_taxpayer = OCLForJAVA.evaluateBoolean(taxpayer, tempOCL, "is_spouse_taxpayer"); 	
							 if(is_spouse_taxpayer)
							 {   
								 int x = getIdSpouse(spouse,sheet);
								 
								
								 double part_spouseGross = Double.valueOf(sheet.getRow(x).getCell(7).toString()).doubleValue();
				    			 double part_spouseNew = Double.valueOf(sheet.getRow(x).getCell(9).toString()).doubleValue();
				    			 double part_spouseOld = Double.valueOf(sheet.getRow(x).getCell(10).toString()).doubleValue();
				    			toAddGross=toAddGross +part_spouseGross;
				    			toAddNewTaxes = toAddNewTaxes + part_spouseNew;
				    			toAddOldTaxes = toAddOldTaxes + part_spouseOld;
				    			
								 sav_Spouses.add(String.valueOf(x));
								 
								 }	 
							 }
						   
	    				}
	    			}
	    			else
	    			{
	    				toAddGross = Double.valueOf(sheet.getRow(i).getCell(7).toString()).doubleValue();
	    				toAddOldTaxes = Double.valueOf(sheet.getRow(i).getCell(10).toString().split("Spouse ==> TP")[0].trim()).doubleValue();
	    				toAddNewTaxes = Double.valueOf(sheet.getRow(i).getCell(9).toString()).doubleValue();

	    				int num_spouse = Double.valueOf(sheet.getRow(i).getCell(10).toString().split("Spouse ==> TP")[1].trim()).intValue();
	    				double part_spouseGross = Double.valueOf(sheet.getRow(num_spouse).getCell(7).toString()).doubleValue();
	    				double part_spouseNew = Double.valueOf(sheet.getRow(num_spouse).getCell(9).toString()).doubleValue();

	    				toAddGross=toAddGross +part_spouseGross;
	    				toAddNewTaxes = toAddNewTaxes + part_spouseNew;
	    				
	    				
	    				sav_Spouses.add(String.valueOf(num_spouse));
	    				
	    				
	    				
	    			}
	    			
	    			
	    			
	    	 		 boolean affectedRange=false;
		    		 for (int k = 0; k < binRanges.length&&affectedRange==false; k++) {
		    			String ch = binRanges[k];
		    			String[] border = ch.split("-");
		    			
		    			if(toAddGross>= Double.valueOf(border[0]).intValue() && toAddGross<=Double.valueOf(border[1]).intValue() )
		    				{countRanges[k]=countRanges[k]+1;
		    				oldTaxes[k] = oldTaxes[k]+toAddOldTaxes;
		    				newTaxes[k] = newTaxes[k]+toAddNewTaxes;
		    				affectedRange=true;}
		    		}
		    		 if(affectedRange==false)
		    		  {
		    			 countRanges[countRanges.length-1]=countRanges[countRanges.length-1]+1;  
		    			 oldTaxes[countRanges.length-1] = oldTaxes[countRanges.length-1]+toAddOldTaxes;
		    			 newTaxes[countRanges.length-1] = newTaxes[countRanges.length-1]+toAddNewTaxes;
		    		  }
		    		 
		    		 //DIFF
		    		 diff.add(toAddNewTaxes-toAddOldTaxes);
		    		 
		    		 
		    		 	boolean affectedNew = false;
		            	 for (int j = 0; j < tax_rangeNew.length && affectedNew==false; j++) {
		            		if(j==0)
		            		{
		            			if(toAddNewTaxes==0)
		            			{
		            				tax_rangeNew[0]=tax_rangeNew[0]+1;
		            				affectedNew=true;
		            			}
		            		}
		            		else
		            		{
								if(toAddNewTaxes>=((j-1)*3000)+1 && toAddNewTaxes<=(j*3000))
							{
								tax_rangeNew[j]=tax_rangeNew[j]+1;
	            				affectedNew=true;

							}
		            		}
						}
		          
		          		 if(affectedNew==false)
		          		 {
		          			tax_rangeNew[tax_rangeNew.length-1] = tax_rangeNew[tax_rangeNew.length-1] +1;
		          		 }
			    		 	boolean affectedOld = false;
			            	 for (int j = 0; j < tax_rangeOld.length && affectedOld==false; j++) {
			            		if(j==0)
			            		{
			            			if(toAddOldTaxes==0)
			            			{
			            				tax_rangeOld[0]=tax_rangeOld[0]+1;
			            				affectedOld=true;
			            			}
			            		}
			            		else
			            		{
								if(toAddOldTaxes>=((j-1)*3000)+1 && toAddOldTaxes<=(j*3000))
								{
									tax_rangeOld[j]=tax_rangeOld[j]+1;
									affectedOld=true;

								}
			            		}
							}
			            	 
			            	 if(affectedOld==false)
			          		 {
			            		 tax_rangeOld[tax_rangeOld.length-1] = tax_rangeOld[tax_rangeOld.length-1] +1;
			          		 }
			          
		    		  
		    
	    }

	        	}
	        	}
	        	}
	        
	    }
	       
	  for (int i = 0; i < binRanges.length; i++) {
		System.out.println(binRanges[i]+"==>"+countRanges[i]+" OLD==>"+oldTaxes[i]+ " New==>"+newTaxes[i]);
	}
	    int [] wins = new int [11];
	    int [] losses = new int [11];
	    for (int j = 0; j < losses.length; j++) {
	    	losses[j]=0;
	    	wins[j]=0;
	    }
	  
	  int count_zeros = 0;
	  for (Double double1 : diff) {
		  System.out.println(double1);
		  if(double1==0)
		  {count_zeros++;}
		  else{if(double1<0)
		  {//wins
			  
			 boolean affectedWins = false;
         	 for (int j = 0; j < wins.length && affectedWins==false; j++) {
         		
         		 if(double1<=((j)*(3000*-1))-1 && double1>=((j+1)* (3000*-1)))		
					{
							wins[j]=wins[j]+1;
							affectedWins=true;

					}
         		
				}
       
       		 if(affectedWins==false)
       		 {
       			wins[wins.length-1] = wins[wins.length-1] +1;
       		 }
			  
		  }
		  else {
			  
			  //losses
			     boolean affectedloss = false;
	         	 for (int j = 0; j < losses.length && affectedloss==false; j++) {
	         		
	         		if(double1>=((j)*3000)+1 && double1<=((j+1)*3000))
						{
								losses[j]=losses[j]+1;
								affectedloss=true;

						}
	         		
					}
	       
	       		 if(affectedloss==false)
	       		 {
	       			losses[losses.length-1] = losses[losses.length-1] +1;
	       		 }
		  }
		  
		  }
		  
	}
	  
	  
	  System.out.println("neutre ==>"+count_zeros);
	  for (int i : losses) {
		  System.out.println("losses ==>"+i);
	}
	  
	  for (int i : wins) {
		  System.out.println("wins ==>"+i);
	}
	 
	  
	  for (int i = 0; i < tax_rangeNew.length; i++) {
	 if(i==0)
		 System.out.println("Taxe 0 ==> old= "+tax_rangeOld[i]+ "==> new "+ tax_rangeNew[i]);
	 else
		 System.out.println("Taxe "+((i-1)*3000)+1 +"-"+ i*3000+"==> old= "+tax_rangeOld[i]+ "==> new "+ tax_rangeNew[i]);
	}
	 

		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		
	}
	

	 
	 private static EObject getTaxPyerByID(HSSFSheet sheet, int i, LinkedList<EObject> taxpayers) {
		 OCLForJAVA.init("FD");
		String SSNo= sheet.getRow(i).getCell(12).toString().trim();
		for (int j = taxpayers.size()-1; j >= 0; j--) {
			EObject eObject  = taxpayers.get(j);
			OCLForJAVA.init("FD",eObject);
	
		 String tempOCL = "self.SSNo.trim()='"+SSNo+"'";
		 boolean is_TheOne = OCLForJAVA.evaluateBoolean(eObject, tempOCL, "is_TheOne");
		 if(is_TheOne)
		 {taxpayers.remove(j);
		return eObject;
		 }
		}
		return null;
	}


	private static int getIdSpouse(EObject spouse, HSSFSheet sheet) {
		 OCLForJAVA.init("FD",spouse);
		 String tempOCL = "self.SSNo";
		 String SSNo = OCLForJAVA.evaluateString(spouse, tempOCL, "SSNo");
		 int rows=sheet.getPhysicalNumberOfRows();
		  for(int i = rows-1; i >= 1; i--) {
	        HSSFRow row = sheet.getRow(i);
		        if(row != null) {
		        	if(row.getCell(12)!=null)
		        	{
		        	 if(!sheet.getRow(i).getCell(12).toString().trim().equals(""))
		        	 {
		    			String SSNo_InExcel = sheet.getRow(i).getCell(12).toString().trim();
		    			if(SSNo.trim().equals(SSNo_InExcel))
		    				return i;
		        	 }
		        	}
		        }
		  }
		return -1;
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
		 if(taxpayers.get(i).equals(spouse))
			 res=true;
		 else i++;
	 }
	 return res;}
	 
	 public static int getPostion(LinkedList<EObject> taxpayers, EObject tp)
	 {
	
		 for (int i = 0; i < taxpayers.size(); i++) {
			 if(taxpayers.get(i).equals(tp))
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
