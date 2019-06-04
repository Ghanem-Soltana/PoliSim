package temp;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
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
public class Evaluator {

	private static Logger logger ;
	
	public static void main(String[] args) {
		
		

	 	 double [] tab11={64.5,1.4, 34.1};
	 	 double [] tab22={69,1.4, 29.6};
	 	 
	 	  double [] all=concat(tab11, tab22); 
		     normaliser(all);
		     for (int j = 0; j < all.length; j++) {
				if(j<tab11.length)
				tab11[j] = all[j];
				else tab22[j-tab11.length] = all[j];
			}
		    
		 	 System.out.println("Distance ==>"+distance(tab11,tab22));
		 	//System.exit(0);
	 		        
	 final String model="//Papyrus//TaxCard.uml";
	 final String instances_rep="//Instances//TaxpayersOfFD.xmi";
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
      
      
      
      //Loading instances
     System.out.println("Loading Instances..");
	 ResourceSet load_resourceSet = new ResourceSetImpl();
	 load_resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());
	 load_resourceSet.getPackageRegistry().put(reader.getPackages().get(0).getNsURI(),reader.getPackages().get(0));
	 Resource load_resource = load_resourceSet.getResource(URI.createURI(folderPath+instances_rep),true);
	 EList<EObject> elements= load_resource.getContents();
 	 OCLForJAVA.init("FD",elements.get(0));
 	Collection<EObject> taxpayersCollection =new LinkedList<EObject>();
 	 for (int j = 0; j < elements.size(); j++) {
		EObject Eobj = elements.get(j);
		OCLForJAVA.init("FD",Eobj);
		
		 Collection<EObject> temp = OCLForJAVA.evaluateECollection(Eobj,"if(self.oclIsKindOf(Tax_Payer)) then Set{self} else  let hoeshold:Legal_Union_Record= self.oclAsType(Household).parents in Set{ hoeshold.individual_A, hoeshold.individual_B}->select(oclIsKindOf(Tax_Payer))  endif","temp","Tax_Payer","Set"); 
        taxpayersCollection.addAll(temp);


	}
 	 
 	String [] binAge = {"15-19","20-24","25-29","30-34","35-39","40-44","45-49","50-54","55-59","60-64","65-69","70-74","75-79","80-100"};
 	double [] probaAge= {0.069,0.079,0.087,0.105,0.109,0.099,0.086,0.077,0.062,0.057,0.052,0.049,0.033,0.036};
 	
 	int [] countAge = new int[binAge.length];
 	double [] probaAgePop = new double[binAge.length];


 	for (int j = 0; j < countAge.length; j++) {
 		countAge[j]=0;
 	}




 	//  Employment_Income  Pensions_and_Annuities_Income
 	String [] binType= {"Employment_Income","Pensions_and_Annuities_Income","Other"};
 	double [] probaTypeIncome= {0.6,0.2,0.2};
 	int [] countTypeIncome = new int[binType.length];
 	double [] probaTypeIncomePop = new double[binType.length];
 	for (int j = 0; j < countTypeIncome.length; j++) {
 		  countTypeIncome[j]=0;
 	}

 	       String [] binRanges = {"0-10000","10000-20000","20000-30000","30000-40000","40000-50000","50000-60000","60000-70000","70000-80000","80000-90000"
	    		, "90000-100000","100000-110000","110000-120000","120000-130000","130000-140000","140000-150000","150000-160000","160000-170000","170000-180000"
	    		,"180000-190000","190000-200000","200000-230000","230000-330000","330000-500000","500000-700000","700000-1000000","1000000-2000000"};
	    double [] countRanges = new double[binRanges.length];
	 
 	 double [] tab1 = {20.89
 			,12.51
 			,17.14
 			,14.89
 			,10.42
 			,6.80
 			,4.61
 			,3.13
 			,2.14
 			,1.14
 			,0.89
 			,0.64
 			,0.22
 			,0.32
 			,0.23
 			,0.18
 			,0.24
 			,0.44
 			,0.38
 			,0.30
 			,1.02
 			,0.75
 			,0.37
 			,0.17
 			,0.10
 			,0.08};
 	 double [] tab2 = new double [tab1.length];
 	 
 	 
 	 LinkedList<EObject> taxpayers = new LinkedList<EObject>();
     Iterator<EObject> it = taxpayersCollection.iterator();
	 while(it.hasNext())
	 {taxpayers.add(it.next());}
	 System.out.println(taxpayers.size());
	for (int j = 0; j < taxpayers.size(); j++) {
		double toAddGross=0;
    	//System.out.println("Tax case Important "+j);
	  EObject taxpayer=taxpayers.get(j);
	  OCLForJAVA.init("FD",taxpayer);
	  String tempOCL=  "let amount:Real= self.incomes->any(true).income_amount in self.from_law.precision(amount,2)";		 
	  toAddGross = OCLForJAVA.evaluateDouble(taxpayer, tempOCL, "gross"); 
	  toAddGross=toAddGross*12;
	  boolean affectedRange=false;
		 for (int k = 0; k < binRanges.length&&affectedRange==false; k++) {
			String ch = binRanges[k];
			String[] border = ch.split("-");
			
			if(toAddGross>= Double.valueOf(border[0]).intValue() && toAddGross<=Double.valueOf(border[1]).intValue() )
				{countRanges[k]=countRanges[k]+1;
				affectedRange=true;}
		}
		 if(affectedRange==false)
		  {
			 countRanges[countRanges.length-1]=countRanges[countRanges.length-1]+1;  
		  }
		 
		 String query  = "self.getAge(2014) ";
		 String res_Age = OCLForJAVA.evaluateString(taxpayer, query, "res_Age"); 
	
		 boolean affectedAge=false;
		 for (int k = 0; k < binAge.length&&affectedAge==false; k++) {
			String ch = binAge[k];
			String[] border = ch.split("-");
			
			if(Integer.valueOf(res_Age).intValue()>= Integer.valueOf(border[0]).intValue() && Integer.valueOf(res_Age).intValue()<=Integer.valueOf(border[1]).intValue() )
				{countAge[k]=countAge[k]+1;
				affectedAge=true;}
		}
		 if(affectedAge==false)
		  {
			  countAge[countAge.length-1]=countAge[countAge.length-1]+1;  
		  }

	  

		 query  = "self.incomes->select(income_type.oclIsTypeOf(Employment_Income))->size()";
		 String nb = OCLForJAVA.evaluateString(taxpayer, query, "nb"); 
		 countTypeIncome[0]=countTypeIncome[0]+Integer.valueOf(nb).intValue();

		 query  = "self.incomes->select(income_type.oclIsTypeOf(Pensions_and_Annuities_Income))->size()";
		 nb = OCLForJAVA.evaluateString(taxpayer, query, "nb"); 
		 countTypeIncome[1]=countTypeIncome[1]+Integer.valueOf(nb).intValue();

		 query  = "self.incomes->select(not income_type.oclIsTypeOf(Employment_Income) and not income_type.oclIsTypeOf(Pensions_and_Annuities_Income))->size()";
		 nb = OCLForJAVA.evaluateString(taxpayer, query, "nb"); 
		 countTypeIncome[2]=countTypeIncome[2]+Integer.valueOf(nb).intValue();
	  
	}
	
		double total = 0;
		for (int k = 0; k < countRanges.length; k++) {
			total=total+countRanges[k];
			
		}
		
		for (int k = 0; k < tab2.length; k++) {
			tab2[k] = round(countRanges[k]/total)*100;
		}
		
		 System.out.println("Original");
	 	 for (int j = 0; j < tab1.length; j++) {
			System.out.print(tab1[j]+"-");
		}
		 System.out.println("Generated");
	 	 for (int j = 0; j < tab2.length; j++) {
			System.out.print(tab2[j]+"-");
		}
	 	 
	      all=concat(tab1, tab2); 
	     normaliser(all);
	     for (int j = 0; j < all.length; j++) {
			if(j<tab1.length)
			tab1[j] = all[j];
			else tab2[j-tab1.length] = all[j];
		}

	 	 double dis_income=distance(tab1,tab2);
	 	 System.out.println("Income ranges ==>"+dis_income);
	 	
	 	
	  
	    total = 0;
		for (int k = 0; k < countAge.length; k++) {
			total=total+countAge[k];
			
		}
	
		for (int k = 0; k < probaAgePop.length; k++) {
			probaAgePop[k] = round(countAge[k]/total);
		}
		
		 System.out.println("Original");
	 	 for (int j = 0; j < probaAge.length; j++) {
			System.out.print(probaAge[j]+"-");
		}
		 System.out.println("Generated");
	 	 for (int j = 0; j < probaAgePop.length; j++) {
			System.out.print(probaAgePop[j]+"-");
		}
		
	     all=concat(probaAgePop, probaAge); 
	     normaliser(all);
	     for (int j = 0; j < all.length; j++) {
			if(j<probaAgePop.length)
				probaAgePop[j] = all[j];
			else probaAge[j-probaAgePop.length] = all[j];
		}
		
	 	 
	 	 double dis_age=distance(probaAge,probaAgePop);
	 	 System.out.println("\nAge ranges ==>"+dis_age);
	 	 
	 	 
	 	 
	     total = 0;
			for (int k = 0; k < countTypeIncome.length; k++) {
				total=total+countTypeIncome[k];
				
			}
		
			for (int k = 0; k < probaTypeIncomePop.length; k++) {
				probaTypeIncomePop[k] = round(countTypeIncome[k]/total);
			}
			
			
		     all=concat(probaTypeIncome, probaTypeIncomePop); 
		     normaliser(all);
		     for (int j = 0; j < all.length; j++) {
				if(j<probaTypeIncome.length)
					probaTypeIncome[j] = all[j];
				else probaTypeIncomePop[j-probaTypeIncome.length] = all[j];
			}

		 	 double dis_type=distance(probaTypeIncomePop,probaTypeIncome);
		 	 System.out.println("Type ranges ==>"+dis_type);

			 	 
			  System.err.println("Done!");
			  
}
	
	
	public static void normaliser(double[]tab)
	{
		double max= getMax(tab);
		double min = getMin(tab);

		for (int i = 0; i < tab.length; i++) {
			tab[i]= (tab[i]-min)/(max-min);
		}
		
	}
	
	
	public static double getMax(double[]tab)
	{
		double res=tab[0];
		for (int i = 0; i < tab.length; i++) {
			if(tab[i]>res)
			res=tab[i];
		}
		return res;
	}
	
	
	public static double getMin(double[]tab)
	{
		double res=tab[0];
		for (int i = 0; i < tab.length; i++) {
			if(tab[i]<res)
			res=tab[i];
		}
		return res;
	}
	
	
	public static double distance(double[]tab1,double tab2[])
	{
		double res=0;
		for (int i = 0; i < tab2.length; i++) {
			
			res=res+ Math.pow( Math.abs((tab1[i]-tab2[i])),2);
		}
		return Math.sqrt(res);
		
	}
	
	public static double[] concat(double[] a, double[] b) {
		   int aLen = a.length;
		   int bLen = b.length;
		   double[] c= new double[aLen+bLen];
		   System.arraycopy(a, 0, c, 0, aLen);
		   System.arraycopy(b, 0, c, aLen, bLen);
		   return c;
		}
	public static double round(double in)
	{
		BigDecimal res = new BigDecimal(in).setScale(2, RoundingMode.HALF_EVEN);
		return  res.doubleValue();
	}
}



//	 Double toBeTruncated = new Double (Double.valueOf(res).doubleValue()/12);
// Double truncatedDouble=new BigDecimal(toBeTruncated ).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
