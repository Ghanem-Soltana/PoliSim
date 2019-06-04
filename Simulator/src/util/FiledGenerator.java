package util;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Random;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.apache.commons.math3.distribution.AbstractRealDistribution;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.uml2.uml.EnumerationLiteral;
import org.eclipse.uml2.uml.Stereotype;



public class FiledGenerator {
	
	public static String xmlFile="model/distribution.xml";
	public static HashMap<String, AbstractRealDistribution> cache_distributions;

	
	public static boolean randomBoolean() {
	    Random random = new Random();
	    return random.nextBoolean();
	}
	
	public static int randomInteger()
	{
	  Random random = new Random();
	  return random.nextInt(Integer.MAX_VALUE);

	}

	
	public static int randomIntegerRange(int start, int end)
	{
	if (start > end) {
	System.err.println("randomIntegerRange-->Start cannot exceed End.");
	}
	else if (start==end) return start;
	
    Random random = new Random();
    long range = (long)end - start + 1;
    long fraction = (long)(range * random.nextDouble());
    int randomNumber =  (int)(fraction + start);    
    return randomNumber;

	}
	
	
	public static double randomDoubleRange(double start, double end,int precision)
	{
		
	if (start > end) {
	System.err.println("randomDoubleRange-->Start cannot exceed End ("+start+","+end+").");
	}else if (start==end) return new Double(start).doubleValue();
	
	Random r = new Random();
	double res= start + (end - start) * r.nextDouble();
	BigDecimal bd = new BigDecimal(res).setScale(precision, RoundingMode.HALF_EVEN);
     return  bd.doubleValue();
	}
	
	public static double randomGaussian(double mean, double variance, int precision)
	{
    Random fRandom = new Random();
    double res=mean + fRandom.nextGaussian() * variance;
    BigDecimal bd = new BigDecimal(res).setScale(precision, RoundingMode.HALF_EVEN);
    return  bd.doubleValue();

	}
    

	@SuppressWarnings("unchecked")
	public static double distribution(EObject obj, Stereotype stereotype, EObject contextIntance ,EObject parentInstance)
	{  
	
		
		
		
		
		double res=0;
		EnumerationLiteral lit =  (EnumerationLiteral) CreateTaxpayers.getValue(obj, stereotype,"type");
        String name= lit.getName();
        Class<?> classe=null;
        boolean error=false;
        try {
			classe = Class.forName("org.apache.commons.math3.distribution."+name);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			error=true;
		}
           
              if(!error)
              {
            	  
            		AbstractRealDistribution cut = cache_distributions.get(obj.toString()+"#"+stereotype.getName());
            		
            		if(cut!=null)
            		{
            			
            		Method method=null;
            		try {
						method = classe.getMethod("sample");
						res= (Double)method.invoke(cut, new Object[0]); 
					} catch (Exception e) {
						e.printStackTrace();
					} 
            		
            		return res;
            		
            		}
            	  
      			EList<String> names = (EList<String>)  CreateTaxpayers.getValue(obj, stereotype,"parameterNames");
				EList<String>values = (EList<String>) CreateTaxpayers.getValue(obj, stereotype,"parameterValues");
    			boolean isOCL = (boolean) CreateTaxpayers.getValue(obj, stereotype,"isOCL");
     
    				if(isOCL==true)
        			{
    					boolean is_context_OK=false;
    					org.eclipse.uml2.uml.Class tempContext = (org.eclipse.uml2.uml.Class)CreateTaxpayers.getValue(obj, stereotype,"context");
    					EClass context = CreateTaxpayers.getEVersion(tempContext);
    				    EObject the_one=null;
    				    if(CreateTaxpayers.isTheContext(contextIntance, context))
    				    { 
    				    OCLForJAVA.init("", contextIntance);
    				    is_context_OK=true;
    				    the_one = contextIntance;
    				    }
    				    else 	    
    					    if(CreateTaxpayers.isTheContext(parentInstance, context))
    					    {
    					    OCLForJAVA.init("", parentInstance);
    					    is_context_OK=true;
    					    the_one = parentInstance;
    					    }
    				    
    				    if(is_context_OK==true)
    				    for (int i = 0; i < values.size(); i++) {
    				    	String v= values.get(i);
    				    	String evaluation= OCLForJAVA.evaluateString(the_one, v, "evaluation");
    				    	values.set(i, evaluation);
						}
    					else {
							System.err.println("Something went wrong with the context of "+obj);
						}
            			
        			}
    				
				
    			
				
				
				
				
				
				Constructor<?>[] constructors_all = classe.getConstructors();
				
				if(testParameters(names, values, obj , name))
				{
					int nbArgs=names.size();
				
					ArrayList<Constructor<?>>  constructors_potential= getRelventConstructor(constructors_all, name);

					if(constructors_potential.size()==0)
					{System.err.println("No constructor matching "+obj);}
					else 
					{
						
							Constructor<?> theConstructor=constructors_potential.get(0);
							AbstractRealDistribution temp=null;
							try {
								  
								if(nbArgs==0)
								temp = (AbstractRealDistribution) theConstructor.newInstance();
								else
								{
									
									  Object intArgs[] = new Object[nbArgs];
										
									  for (int i = 0; i < intArgs.length; i++) {
										
											Element eElement=getDistribution(name);
											NodeList nList=getParameters(eElement);
											boolean find = false;
											int j=0;					
											while(j<nList.getLength()&&find==false)
											{
												String pos_param=((Element)nList.item(j)).getAttribute("pos");
												if(pos_param.trim().equals(String.valueOf(i)))
													find=true;
												else
												j++;
											}
											String type_param=((Element)nList.item(j)).getAttribute("type");
											String name_param=((Element)nList.item(j)).getAttribute("name");
											
											if(type_param.equals("double"))
												intArgs[i]=new  Double(values.get(getIndexByName(names, name_param)));
											else intArgs[i]=new  Integer(values.get(getIndexByName(names, name_param)));
											
										  
									}
								
									  temp = (AbstractRealDistribution) theConstructor.newInstance(intArgs);
									
								}
								
							
							
								Method method=null;
								method = classe.getMethod("sample");
								res= (Double)method.invoke(temp, new Object[0]);
								
								cache_distributions.put(obj.toString()+"#"+stereotype.getName(), temp);
								
								} catch (Exception e) {
									e.printStackTrace();
						}
							
						}
					
			
				}
              }
			
       
     
      
		return res;
	}
	
	public static int getIndexByName(EList<String> names, String name)
	{
		int res=-1;
		
		int i=0;
		while (i<names.size()&&res==-1)
		{   
			if(names.get(i).trim().equals(name.trim()))
				res=i;
			i++;
		}
		
		return res;
		
	}
	
	public static ArrayList<Constructor<?>> getRelventConstructor(Constructor<?> [] constructors, String name)
	{
		ArrayList<Constructor<?>>  res= new ArrayList<Constructor<?>>();
		Element eElement=getDistribution(name);
		NodeList nList=getParameters(eElement);
		int nbArgs=nList.getLength();
		for (int i = 0; i < constructors.length; i++) {
			if(constructors[i].getParameterTypes().length==nbArgs)
			{
                boolean typeVerification = true;
                int j=0;
                Parameter[] types = constructors[i].getParameters();
                while (j<types.length&& typeVerification) {
					if(!types[j].getType().toGenericString().trim().endsWith("double")&&!types[j].getType().toGenericString().trim().endsWith("int"))
						typeVerification=false;
					j++;
					
				}
                
                
                if(typeVerification)
				res.add(constructors[i]);
			}
		}
		
		
		if(res.size()>1)
		{	
		
			
			boolean stop=false;
			
			while(res.size()>1&& stop==false)
			{   stop=true;
				Constructor<?> constructeur = res.get(0); 
				Parameter[] types = constructeur.getParameters();
				
				
				boolean allpresent=true;
				int i=0;
				while (i<types.length&&allpresent) {
		
					Parameter type = types[i];
					String typeName= type.getName().replace("arg", "");
					String typeLibel= type.getType().toGenericString().trim();
					
					
						boolean find = false;
						int j=0;
						
						while(j<nList.getLength()&&find==false)
						{
							String pos_param=((Element)nList.item(j)).getAttribute("pos");
							String type_param=((Element)nList.item(j)).getAttribute("type");
							if(pos_param.trim().equals(typeName))
								if(type_param.trim().equals(typeLibel.trim()))
								find=true;
							j++;
						}
						if(find==false)
						allpresent=false;
						
						i++;
					}
				
				if(allpresent==false)
				 res.remove(0);
					
					
				}
						
		}	
	
		
		return res;
	}
	
	public static boolean testParameters(EList<String> names, EList<String> values, EObject obj, String name)
	{  
		if(names.size()!=values.size())
			{System.err.println("Size of values and names are not matching for "+ name +" "+obj);
			return false;}
		else
		{
			Element eElement=getDistribution(name);
			NodeList nList=getParameters(eElement);
			
			if(nList.getLength()!=names.size())
			System.err.println("Some parameters are missing"+ name +" "+obj);	
			
			boolean allpresent=true;
			int i=0;
		
			
			while (i<names.size()&&allpresent) {
				boolean find = false;
				int j=0;
				
				while(j<nList.getLength()&&find==false)
				{
					String name_param=((Element)nList.item(j)).getAttribute("name");
					if(name_param.trim().equals(names.get(i).trim()))
						find=true;
					j++;
				}
				if(find==false)
				allpresent=false;
				
				i++;
				
			}
			
			if(! allpresent)
			System.err.println("The parameters names are not defined in the apache library"+ name +" "+obj);	
			
			
		}
		return true;
	}
	
	
	
	public static NodeList getParameters(Element eElement)
	{	
		
		NodeList nList=null;
		 try {
			 
				File fXmlFile = new File(xmlFile);
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				Document doc = dBuilder.parse(fXmlFile);
				doc.getDocumentElement().normalize();
				nList = eElement.getElementsByTagName("param");
				
		 }catch(Exception e){ e.printStackTrace();}
		 return nList;		 
	}
	
	public static Element getDistribution(String name)
	{
		 try {
			 
				File fXmlFile = new File(xmlFile);
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				Document doc = dBuilder.parse(fXmlFile);
				doc.getDocumentElement().normalize();
				NodeList nList = doc.getElementsByTagName("distribution");
			 
			 
				for (int temp = 0; temp < nList.getLength(); temp++) {
					 
					Node nNode = nList.item(temp);
					if (nNode.getNodeType() == Node.ELEMENT_NODE) {
						Element eElement = (Element) nNode;
						if(eElement.getAttribute("name").trim().equals(name.trim()))
						return eElement;

					}
				}
				
			    } catch (Exception e) {
				e.printStackTrace();
			    }
		 return null;

	}
	
	
	public static Date generateRandomDate(int y1, int y2)
	{
	 GregorianCalendar gc = new GregorianCalendar();

     int year = randBetween(y1, y2);

     gc.set(GregorianCalendar.YEAR, year);

     int dayOfYear = randBetween(1, gc.getActualMaximum(GregorianCalendar.DAY_OF_YEAR));

     gc.set(GregorianCalendar.DAY_OF_YEAR, dayOfYear);
     
     return new Date(gc.get(GregorianCalendar.YEAR), (gc.get(GregorianCalendar.MONTH) + 1) , gc.get(GregorianCalendar.DAY_OF_MONTH));



 }

 public static int randBetween(int start, int end) {
     return start + (int)Math.round(Math.random() * (end - start));
 }


}
