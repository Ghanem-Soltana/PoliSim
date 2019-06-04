 package uni.lu.snt.svv.ctie.activitydiagramsToOcl.main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.acceleo.common.preference.AcceleoPreferences;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.uml2.uml.Activity;
import org.eclipse.uml2.uml.ActivityParameterNode;
import org.eclipse.uml2.uml.Comment;
import org.eclipse.uml2.uml.Constraint;
import org.eclipse.uml2.uml.ExpansionNode;
import org.eclipse.uml2.uml.ExpansionRegion;
import org.eclipse.uml2.uml.InputPin;



public class Test {
	
	


	public static String  bureau="/Users/youradmin/Desktop/".replace("youradmin", System.getProperty("user.name")).replaceAll("/", "//")+"transfornation.log";
	
	public static List<InputPin> getAllInputPinsNamesFromString(String ch, ExpansionRegion anExpansionRegion)
	{writeToAppend("ICICICI", bureau);
		List<InputPin> res = new ArrayList<InputPin>();
		
		List<String> names= getAllNamesFromString(ch);
		
		writeToAppend(names.toString(), bureau);
		
		for (String name : names) {
		    boolean added=false;
			TreeIterator<EObject>temp=anExpansionRegion.eAllContents();

	        
				while (temp.hasNext()&&added==false)
				{  EObject x = temp.next();
			       if(x instanceof InputPin)	
			       {
			    	  InputPin inputPin = (InputPin)x;
			    	  if(inputPin.getName().trim().equals(name.trim()))
			    	  {
			    		  res.add(inputPin);
			    	     added=true;
			    	  }
			       }
			
				}
		}
			
			
			
		
		return res;
	}
	
	
	public static List<String> getAllNamesFromString(String ch)
	{	List<String> res =new ArrayList<String>();
	
	while (ch.contains("name:")) {
		
		String name=ch.substring(ch.indexOf("name:")+6, ch.indexOf(',')).trim();
		res.add(name);
		ch=ch.replace("name: "+name+",", "");
		
		if(ch.contains("name:"))
		{
			ch=ch.substring(ch.indexOf("name:")-1);
		}
		
	}

	
		return res;
	}
	
	
	
	public static List<ActivityParameterNode> emptyListActivityParrameterNode(Activity x)
	{
		List<ActivityParameterNode> res =new ArrayList<ActivityParameterNode>();
		return res;
	}
	
	public static List<ExpansionNode> emptyListExpansionNode(Activity x)
	{
		List<ExpansionNode> res =new ArrayList<ExpansionNode>();
		return res;
	}
	
	
	public static List<InputPin> emptyListInputPin(Activity x)
	{
		List<InputPin>  res =new ArrayList<InputPin>();
		return res;
	}
	
	
	public static String getConstraintFromRecord(InputPin aNode,Activity anActivity, ExpansionRegion anExpansionRegion)
	{   
		String res ="";
		String ch="";
		Comment aComment = null;
		TreeIterator<EObject>temp=anExpansionRegion.eAllContents();
		try{
        

		if (aNode.getAppliedStereotypes().toString().contains("fromrecord")||aNode.getAppliedStereotypes().toString().contains("temp"))
		{
		  
			while (temp.hasNext()&&aComment==null)
			{  EObject x = temp.next();
		       if(x instanceof Comment)	   
		       {  boolean found=false;  
	    	   for (int i = 0; i < ((Comment)x).getAnnotatedElements().size() && found == false; i++) {
	    		   if(((Comment)x).getAnnotatedElements().get(i) instanceof InputPin)
			        	 if(((InputPin)(((Comment)x).getAnnotatedElements().get(i))).getName().equals(aNode.getName()) )
			        	 {
			        		 aComment= (Comment)x;
			        		 found=true;
			        	 }
	    	   
	 
	       } 
		       } 
		       
			}
			
			if(aComment==null)
			{
				temp=anActivity.eAllContents();
				while (temp.hasNext()&&aComment==null)
				{  EObject x = temp.next();
			       if(x instanceof Comment)	
			    { boolean found=false;  
		    	   for (int i = 0; i < ((Comment)x).getAnnotatedElements().size() && found == false; i++) {
		    		   if(((Comment)x).getAnnotatedElements().get(i) instanceof InputPin)
				        	 if(((InputPin)(((Comment)x).getAnnotatedElements().get(i))).getName().equals(aNode.getName()) )
				        	 {
				        		 aComment= (Comment)x;
				        		 found=true;
				        	 }
		    	   
		 
		       } 
			     }
				}
				
			}
			
			
	
		    ch =  (aComment.getValue(aComment.getAppliedStereotype("CTIE_Profile_Final::query"), "constraint")).toString();
		 
			ch=ch.substring(ch.indexOf(':')+1, ch.indexOf(',')).trim();
			temp=anExpansionRegion.eAllContents();


			Constraint aConstraint = null;
			EList<Constraint> content = (EList<Constraint>)aComment.getValue(aComment.getAppliedStereotype("CTIE_Profile_Final::query"), "constraint");
			if(content.size()==1)
			 aConstraint= content.get(0);
		
			while (temp.hasNext()&&aConstraint==null)
			{  EObject x = temp.next();
		       if(x instanceof Constraint)
		         if(((Constraint)x).getName().trim().equals(ch))   
		            aConstraint= (Constraint)x;
		 
		       
			}
			
			if(aConstraint==null)
			{temp=anActivity.eAllContents();
				while (temp.hasNext()&&aConstraint==null)
				{  EObject x = temp.next();
					if(x instanceof Constraint)
						if(((Constraint)x).getName().trim().equals(ch))   
							aConstraint= (Constraint)x;
		 
		       
				}
			}
			
			res=aConstraint.getSpecification().stringValue();
		}
		}catch (Exception e) {
			res ="";
		}
		return res.replace(aNode.getName(), "");
	}
	
	public static String getConstraintFromRecord(ActivityParameterNode aNode,Activity anActivity)
	{   
		String res ="";
		TreeIterator<EObject>temp=anActivity.eAllContents();

		try{
        

		if (aNode.getAppliedStereotypes().toString().contains("fromrecord")||aNode.getAppliedStereotypes().toString().contains("temp"))
		{
		  Comment aComment = null;
			while (temp.hasNext()&&aComment==null)
			{  EObject x = temp.next();
		       if(x instanceof Comment)	    
		       { 
		    	 boolean found=false;  
		    	   for (int i = 0; i < ((Comment)x).getAnnotatedElements().size() && found == false; i++) {
		    		   if(((Comment)x).getAnnotatedElements().get(i) instanceof ActivityParameterNode)
				        	 if(((ActivityParameterNode)(((Comment)x).getAnnotatedElements().get(i))).getName().equals(aNode.getName()) )
				        	 {
				        		 aComment= (Comment)x;
				        		 found=true;
				        	 }
				}
		    
		        	        
		        } 
		        
		         
		       
			}
			
			String ch =  (aComment.getValue(aComment.getAppliedStereotype("CTIE_Profile_Final::query"), "constraint")).toString();
			
			ch=ch.substring(ch.indexOf(':')+1, ch.indexOf(',')).trim();
			//writeToAppend("Constraint  "+aNode.getName()+" is ==>"+ch, bureau);
			temp=anActivity.eAllContents();
			
	
			Constraint aConstraint = null;
			EList<Constraint> content = (EList<Constraint>)aComment.getValue(aComment.getAppliedStereotype("CTIE_Profile_Final::query"), "constraint");
			if(content.size()==1)
			 aConstraint= content.get(0);
			
			
			while (temp.hasNext()&&aConstraint==null)
			{  EObject x = temp.next();
			writeToAppend("- available elements "+x, bureau);
		       if(x instanceof Constraint)
		         if(((Constraint)x).getName().trim().equals(ch))   
		            aConstraint= (Constraint)x;
		 
		       
			}
			
			
			
			res=aConstraint.getSpecification().stringValue();
		}
		}catch (Exception e) {
			writeToAppend("Exception getConstraintFromRecord Activity "+aNode.getName(), bureau);
			res ="";
		}
		return res.replace(aNode.getName(), "");
	}
	
	public static void sortByNameActivity(List <ActivityParameterNode> all)
	{
		ActivityParameterNode liste []= new   ActivityParameterNode [all.size()];
		for(int i = 0 ;i <all.size();i++)
		liste [i]=all.get(i);
		
		ActivityParameterNode temp1 = null;
		
		for (int i =0; i<liste.length-1;i++)
			for (int j=i+1;j<liste.length;j++)
				if(liste[i].getName().trim().length()<liste[j].getName().trim().length())
				{
					temp1=liste[i];
					liste[i]=liste[j];
					liste[j]=temp1;
					
				}
		
		for(int i = 0 ;i <all.size();i++)
		all.set(i,liste[i]);
	
	}
	
	public static void sortByNameInputPin(List <InputPin> all)
	{
		InputPin liste []= new   InputPin [all.size()];
		for(int i = 0 ;i <all.size();i++)
		liste [i]=all.get(i);
		
		InputPin temp1 = null;
		
		for (int i =0; i<liste.length-1;i++)
			for (int j=i+1;j<liste.length;j++)
				if(liste[i].getName().trim().length()<liste[j].getName().trim().length())
				{
					temp1=liste[i];
					liste[i]=liste[j];
					liste[j]=temp1;
					
				}
		
		for(int i = 0 ;i <all.size();i++)
		all.set(i,liste[i]);
	
	}
	
	public static List<ActivityParameterNode> activityParameterNodeToBedeclared(Activity anActivity,String ch,List <ActivityParameterNode> all,List <ActivityParameterNode> declared) throws IOException
	{   
		
		sortByNameActivity(all);
		List<ActivityParameterNode> res =new ArrayList<ActivityParameterNode>();
		writeToAppend("Inital CH= "+ch, bureau);	

	try{	
		for(int i = 0 ;i<all.size();i++)
		{
			if( needIt(ch, all.get(i)))
			{
				if(notExistIn(all.get(i), declared))
				{
				res.add(0,all.get(i));
				ch=ch.replace(all.get(i).getName(), "");
				}
				ch=ch.replace(all.get(i).getName(), "");
			}
				
		}	

		writeToAppend("Modif CH= "+ch, bureau);	
		writeToAppend("Initial Res "+res, bureau);	
		
		boolean stop=false;
		while(stop==false)
		{
		stop=true;
		List<ActivityParameterNode> toBeAdded =new ArrayList<ActivityParameterNode>();
		
		for(int i = 0 ;i<res.size();i++)
		{   String tempCh=getConstraintFromRecord(res.get(i), anActivity);
	
		    for(int x = 0  ;x<res.size();x++)
			tempCh= tempCh.replace(res.get(x).getName().trim(), "");
		    
			if(!tempCh.equals(""))
			{   
				List<ActivityParameterNode> temp =activityParameterNodeToBedeclared(anActivity,tempCh,all,declared);
				for(int j=0;j<temp.size();j++)
				if(notExistIn(temp.get(j), declared))
					if(notExistIn(temp.get(j), res))
					toBeAdded.add(0,temp.get(j));
				
			}
			
		}
		

		writeToAppend("To Be Added "+toBeAdded, bureau);	
		
		for(int i = 0 ;i<toBeAdded.size();i++)
		{	res.add(0,toBeAdded.get(i));
            if(toBeAdded.get(i).getAppliedStereotypes().toString().contains("fromrecord")||toBeAdded.get(i).getAppliedStereotypes().toString().contains("temp"))
            	stop=false;
		}
		
		}
		
		//writeToAppend("REs= "+res.toString(), bureau);
	}catch(Exception e){
		
		writeToAppend("activityParameterNodeToBedeclared "+e.getMessage(), bureau);
	}
		removeDuplicat(res);
		return res;
	}
	
	
	
	public static List<InputPin> inputPinToBedeclared(Activity anActivity,ExpansionRegion anExpansionRegion,String ch,List <InputPin> all,List <InputPin> declared) throws IOException
	{   
		writeToAppend("Inside inputPinToBedeclared CH= "+ch, bureau);
		sortByNameInputPin(all);
		List<InputPin> res =new ArrayList<InputPin>();
	try{	
		for(int i = 0 ;i<all.size();i++)
		{
			
		
		if( needIt(ch, all.get(i)))	
		{
			if(notExistIn(all.get(i), declared))
			{
			res.add(0,all.get(i));
			writeToAppend("accepted "+all.get(i).getName(), bureau);
			ch=ch.replace(all.get(i).getName(), "");
			}
			
			ch=ch.replace(all.get(i).getName(), "");
		}		
		}	
		
		boolean stop=false;
		
		while(stop==false)
		{
			
		stop=true;
		
		List<InputPin> toBeAdded =new ArrayList<InputPin>();
		
		for(int i = 0  ;i<res.size();i++)
		{   String tempCh=getConstraintFromRecord(res.get(i),anActivity,anExpansionRegion);
		
		for(int x = 0  ;x<res.size();x++)
		tempCh= tempCh.replace(res.get(x).getName().trim(), "");
		
	    	if(!tempCh.equals(""))
			{  
				List<InputPin> temp =inputPinToBedeclared(anActivity,anExpansionRegion,tempCh,all,declared);
				for(int j=0;j<temp.size();j++)
				if(notExistIn(temp.get(j), declared))
					if(notExistIn(temp.get(j), res))
					{
						toBeAdded.add(0,temp.get(j));
						writeToAppend("accepted niveau2 "+temp.get(j), bureau);
					}
				
			}
	    	
			
		}

		
		for(int i = 0 ;i<toBeAdded.size();i++)
		{
			res.add(0,toBeAdded.get(i));
			 if(toBeAdded.get(i).getAppliedStereotypes().toString().contains("fromrecord")||toBeAdded.get(i).getAppliedStereotypes().toString().contains("temp"))
	           stop=false;
		}
		}
		
	}catch(Exception e){
		writeToAppend("inputPinToBedeclared "+e.getMessage(), bureau);
	}
	
		removeDuplicat(res);
		return res;
	}

	public static boolean notExistIn(ExpansionNode ExpansionNode, List <ExpansionNode> declared)
	{boolean res = true;
	int i = 0;
	while(res==true&&i<declared.size())
	{
		if(declared.get(i).getName().equals(ExpansionNode.getName()))
			res=false;
		else i++;
	}
	return res;
	}
	
	public static boolean notExistIn(ActivityParameterNode anActivityParameterNode, List <ActivityParameterNode> declared)
	{boolean res = true;
	int i = 0;
	while(res==true&&i<declared.size())
	{
		if(declared.get(i).getName().equals(anActivityParameterNode.getName()))
			res=false;
		else i++;
	}
	return res;
	}
	
	public static boolean notExistIn(InputPin anInputPin, List <InputPin> declared)
	{boolean res = true;
	int i = 0;
	while(res==true&&i<declared.size())
	{
		if(declared.get(i).getName().equals(anInputPin.getName()))
			res=false;
		else i++;
	}
	return res;
	}
	public static boolean needIt(String ch,ExpansionNode anExpansionNode) 
	{return contains(ch,anExpansionNode.getName().trim());}
	
	public static boolean needIt(String ch,ActivityParameterNode anActivityParameterNode) 
	{return contains(ch,anActivityParameterNode.getName().trim());}
	
	public static boolean needIt(String ch,InputPin anInputPin) 
	{return contains(ch, anInputPin.getName().trim());}
	
	
	public static boolean contains(String toBeEvaluated,String name)
	{
		boolean res=false;
		if(toBeEvaluated.contains(name)==false)
			return false;
		

		int index=0;
		while (index<toBeEvaluated.length()&&index!=-1) {
			index=toBeEvaluated.indexOf(name,index);

			if(index!=-1)
			if(isValidName(name,toBeEvaluated,index))
				return true;
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

	
	
	public static String getAbsolutePath(EObject element) throws MalformedURLException, IOException {
	    return FileLocator.resolve(new URL(element.eResource().getURI().toString())).getFile();
	}
	
	public static  void clean (String file, Activity anActivity) throws Exception
	{
         String ch=getAbsolutePath(anActivity);
         ch=ch.replace("/model/", "/src-gen/");
         ch=ch.substring(0,ch.lastIndexOf("/")+1);

 	     String chemin =ch.replace("youradmin", System.getProperty("user.name"));
		try
	  	{ 
	  	   
	  	     chemin = chemin+file;
	  	     chemin = chemin.replace("/", "//");
	      
	         
	    String fichier=chemin;
	    InputStream fis = new FileInputStream(fichier);
	    Reader reader = new InputStreamReader(fis);
	    BufferedReader input =  new BufferedReader(reader);
	    String line = null;
	    StringBuilder str=new StringBuilder();
	   
	    while ((line = input.readLine()) != null)
	    {
	    	if(!line.trim().equals("") && !line.equals("\n"))
	    	{
	    		
	    	if(line.charAt(0)!=' ')
	    	str.append(new String(line.replaceAll("[^\\w\\s\\p{Punct}]", " ").getBytes(),Charset.forName("UTF-8")).replaceAll("  ", " "));
	    	else
	    	str.append(new String(line.replaceAll("[^\\w\\s\\p{Punct}]", " ").getBytes(),Charset.forName("UTF-8")));
	    	str.append("\n");
	    	}
	  	}

	  	writeTo(str.toString(), fichier);
	  	}catch(IOException ex)
	  	{
	  		afficher(chemin);
	  	}
	      
	  }
	
	public static  void cleanMatlab (String file, Activity anActivity) throws Exception
	{
         String ch=getAbsolutePath(anActivity);   
         ch=ch.replace("/New_Models/", "/Matlab_Code/");
         ch=ch.replace("/Modified_Models/", "/Matlab_Code_Modified/");
         ch=ch.replace("/Temp_models/", "/Temp_results/");
         ch=ch.substring(0,ch.lastIndexOf("/")+1);
 	     String chemin =ch.replace("youradmin", System.getProperty("user.name"));
		try
	  	{          
	  	   
	  	     chemin = chemin+file;
	  	     chemin = chemin.replace("/", "//");
	  	     	
	    String fichier=chemin;
	    InputStream fis = new FileInputStream(fichier);
	    Reader reader = new InputStreamReader(fis);
	    BufferedReader input =  new BufferedReader(reader);
	    String line = null;
	    StringBuilder str=new StringBuilder();
	   
	    
	    while ((line = input.readLine()) != null)
	    {
	    	if(!line.trim().equals("") && !line.equals("\n"))
	    	{
	    		
	    	if(line.charAt(0)!=' ')
	    	str.append(new String(line.replaceAll("[^\\w\\s\\p{Punct}]", " ").getBytes(),Charset.forName("UTF-8")).replaceAll("  ", " "));
	    	else
	    	str.append(new String(line.replaceAll("[^\\w\\s\\p{Punct}]", " ").getBytes(),Charset.forName("UTF-8")));
	    	str.append("\n");
	    	}
	  	}
	  	writeTo(str.toString(), fichier);
	  	}catch(IOException ex)
	  	{
	  		 writeToAppend("Error in cleanMatlab"+chemin,bureau);
	  	}
	      
	  }
	
	
	
	public static  void clean1 (String file, Activity anActivity) throws Exception
	{
         String ch=getAbsolutePath(anActivity);
         ch=ch.replace("/model/", "/src-gen/");
         ch=ch.substring(0,ch.lastIndexOf("/")+1);
 	     String chemin =ch.replace("youradmin", System.getProperty("user.name"));
		try
	  	{ 
	  	   
	  	     chemin = chemin+file;
	  	     chemin = chemin.replaceAll("/", "//");
	  	     	
	    String fichier=chemin;
	    InputStream fis = new FileInputStream(fichier);
	    Reader reader = new InputStreamReader(fis);
	    BufferedReader input =  new BufferedReader(reader);
	    String line = null;
	    StringBuilder str=new StringBuilder();
	   int somme=0;
	    while ((line = input.readLine()) != null)
	    {
	    	if(!line.trim().equals("") && !line.equals("\n"))
	    	{
	    		
	    	if(line.charAt(0)!=' ')
	    	str.append(new String(line.replaceAll("[^\\w\\s\\p{Punct}]", " ").getBytes(),Charset.forName("UTF-8")).replaceAll("  ", " "));
	    	else
	    	str.append(new String(line.replaceAll("[^\\w\\s\\p{Punct}]", " ").getBytes(),Charset.forName("UTF-8")));
	    	
	    	try{somme=somme+ Integer.valueOf(line.trim()).intValue();}catch (Exception e) {
				// TODO: handle exception
			}
	    	str.append("\n");
	    	//if(line.trim().endsWith("in"))
	    	//str.append("\n");
	    	}
	  	}
	    str.append("OCL length"+String.valueOf(somme));
	  	writeTo(str.toString(), fichier);
	  	}catch(IOException ex)
	  	{
	  		afficher(chemin);
	  	}
	      
	  }
	
	
	  private static void writeTo(String data, String fichier)throws IOException
	  {
	  	FileWriter writer=new FileWriter(fichier);
	  	writer.write(data.replaceAll("  ", " "));
	  	writer.close();
	  }
	  

	private static void writeToAppend(String data, String fichier)
	  {try{
		 	FileWriter fileWritter = new FileWriter(fichier,true);
	        BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
	        if(data!=null)
	        bufferWritter.write("\n"+data.replaceAll("  ", " "));
	        bufferWritter.close();
	  }catch (Exception e) {
		// TODO: handle exception
	}
	  }
	
	
	public Activity Init(Activity x)
	{
	AcceleoPreferences.switchQueryCache(false);
	return x;
	}
	
	public static void afficher (String x) throws Exception
	{
		Exception e =new Exception("###Here is your message " +x);
		if(true)
	    throw e;
	}
	public static void afficher (int x) throws Exception
	{
		Exception e =new Exception("###Here is your message " +String.valueOf(x));
		if(true)
	    throw e;
	}
	public static void afficher (boolean x) throws Exception
	{
		Exception e =new Exception("###Here is your message " +String.valueOf(x));
		if(true)
	    throw e;
	}
	
	public static void sortByNameExpansionNode(List <ExpansionNode> all)
	{
		ExpansionNode liste []= new   ExpansionNode [all.size()];
		for(int i = 0 ;i <all.size();i++)
		liste [i]=all.get(i);
		
		ExpansionNode temp1 = null;
		
		for (int i =0; i<liste.length-1;i++)
			for (int j=i+1;j<liste.length;j++)
				if(liste[i].getName().length()<liste[j].getName().length())
				{
					temp1=liste[i];
					liste[i]=liste[j];
					liste[j]=temp1;
					
				}
		
		for(int i = 0 ;i <all.size();i++)
		all.set(i,liste[i]);
	
	}
	
	public static String getConstraintFromRecord(ExpansionNode aNode,Activity anActivity, ExpansionRegion anExpansionRegion)
	{  
		String res ="";
		String ch="";
		Comment aComment = null;
		TreeIterator<EObject>temp=anExpansionRegion.eAllContents();
		try{
        

		if (aNode.getAppliedStereotypes().toString().contains("fromrecord")||aNode.getAppliedStereotypes().toString().contains("temp"))
		{
		  
			while (temp.hasNext()&&aComment==null)
			{  EObject x = temp.next();
		       if(x instanceof Comment)	   
		       {
		    	   boolean found=false;  
		    	   for (int i = 0; i < ((Comment)x).getAnnotatedElements().size() && found == false; i++) {
		    		   if(((Comment)x).getAnnotatedElements().get(i) instanceof ExpansionNode)
				        	 if(((ExpansionNode)(((Comment)x).getAnnotatedElements().get(i))).getName().equals(aNode.getName()) )
				        	 {
				        		 aComment= (Comment)x;
				        		 found=true;
				        	 }
		    	   
		 
		       } 
		       
			}
			}
			
			if(aComment==null)
			{
				temp=anActivity.eAllContents();
				while (temp.hasNext()&&aComment==null)
				{  EObject x = temp.next();
			       if(x instanceof Comment)	
			    {  boolean found=false;  
		    	   for (int i = 0; i < ((Comment)x).getAnnotatedElements().size() && found == false; i++) {
		    		   if(((Comment)x).getAnnotatedElements().get(i) instanceof ExpansionNode)
				        	 if(((ExpansionNode)(((Comment)x).getAnnotatedElements().get(i))).getName().equals(aNode.getName()) )
				        	 {
				        		 aComment= (Comment)x;
				        		 found=true;
				        	 }
		    	   		 
		       } 
			     }
				}
				
			}
			
			
		   
		    ch =  (aComment.getValue(aComment.getAppliedStereotype("CTIE_Profile_Final::query"), "constraint")).toString();
		 
			ch=ch.substring(ch.indexOf(':')+1, ch.indexOf(',')).trim();
			temp=anExpansionRegion.eAllContents();
			
			Constraint aConstraint = null;
			EList<Constraint> content = (EList<Constraint>)aComment.getValue(aComment.getAppliedStereotype("CTIE_Profile_Final::query"), "constraint");
			if(content.size()==1)
			 aConstraint= content.get(0);
		
			

			while (temp.hasNext()&&aConstraint==null)
			{  EObject x = temp.next();
		       if(x instanceof Constraint)
		         if(((Constraint)x).getName().equals(ch))   
		            aConstraint= (Constraint)x;
		 
		       
			}
			
			if(aConstraint==null)
			{temp=anActivity.eAllContents();
				while (temp.hasNext()&&aConstraint==null)
				{  EObject x = temp.next();
					if(x instanceof Constraint)
						if(((Constraint)x).getName().equals(ch))   
							aConstraint= (Constraint)x;
		 
		       
				}
			}
			
			res=aConstraint.getSpecification().stringValue();
		}
		}catch (Exception e) {
			res ="";
		}
		return res.replace(aNode.getName(), "");
	}
	
	public static List<ExpansionNode> expansionNodeToBedeclared(Activity anActivity,ExpansionRegion anExpansionRegion,String ch,List <ExpansionNode> all,List <ExpansionNode> declared) throws IOException
	{   
		AcceleoPreferences.switchQueryCache(false);
		sortByNameExpansionNode(all);
		List<ExpansionNode> res =new ArrayList<ExpansionNode>();
	try{	
		for(int i = 0 ;i<all.size();i++)
		{
		if( needIt(ch, all.get(i)))
		{
			if(notExistIn(all.get(i), declared))
			{
			res.add(0,all.get(i));
			ch=ch.replace(all.get(i).getName(), "");
			}
			ch=ch.replace(all.get(i).getName(), "");
		}		
		}	
		
		boolean stop=false;
		while(stop==false)
		{
			
		stop=true;
		List<ExpansionNode> toBeAdded =new ArrayList<ExpansionNode>();
		
		for(int i = 0  ;i<res.size();i++)
		{   String tempCh=getConstraintFromRecord(res.get(i),anActivity,anExpansionRegion);
		    
			for(int x = 0  ;x<res.size();x++)
			tempCh= tempCh.replace(res.get(x).getName().trim(), "");
		
	    	if(!tempCh.equals(""))
			{  
				List<ExpansionNode> temp =expansionNodeToBedeclared(anActivity,anExpansionRegion,tempCh,all,declared);
				for(int j=0;j<temp.size();j++)
				if(notExistIn(temp.get(j), declared))
					if(notExistIn(temp.get(j), res))
						toBeAdded.add(0,temp.get(j));

				
			}
	    	
			
		}

		
		for(int i = 0 ;i<toBeAdded.size();i++)
		{
			res.add(0,toBeAdded.get(i));
			 if(toBeAdded.get(i).getAppliedStereotypes().toString().contains("fromrecord")||toBeAdded.get(i).getAppliedStereotypes().toString().contains("temp"))
	           stop=false;
		}
		}
		
	}catch(Exception e){
		writeToAppend("Error in expansionNodeToBedeclared "+e.getMessage(), bureau);
	}
		removeDuplicat(res);
		return res;
	}
	
	public static List<ActivityParameterNode> extraActivityParameterNodeToBedeclared(Activity anActivity,List <InputPin> pins, ExpansionRegion anExpansionRegion,List <ActivityParameterNode> all,List <ActivityParameterNode> declared) throws IOException
	{   
		
		String ch = "";
		for (int i = 0; i < pins.size(); i++) {
		ch=ch+getConstraintFromRecord(pins.get(i), anActivity, anExpansionRegion);
		}
		
		sortByNameActivity(all);
		List<ActivityParameterNode> res =new ArrayList<ActivityParameterNode>();
		
	try{	
		for(int i = 0 ;i<all.size();i++)
		{
			if( needIt(ch, all.get(i)))
			{
				if(notExistIn(all.get(i), declared))
				{
				res.add(0,all.get(i));
				ch=ch.replace(all.get(i).getName(), "");
				}
				ch=ch.replace(all.get(i).getName(), "");
			}
				
		}	
		
		
		boolean stop=false;
		while(stop==false)
		{
		stop=true;
		List<ActivityParameterNode> toBeAdded =new ArrayList<ActivityParameterNode>();
		
		for(int i = 0 ;i<res.size();i++)
		{   String tempCh=getConstraintFromRecord(res.get(i), anActivity);
			
		    for(int x = 0  ;x<res.size();x++)
			tempCh= tempCh.replace(res.get(x).getName().trim(), "");
			
		    if(!tempCh.equals(""))
			{  
				List<ActivityParameterNode> temp =activityParameterNodeToBedeclared(anActivity,tempCh,all,declared);
				for(int j=0;j<temp.size();j++)
				if(notExistIn(temp.get(j), declared))
					if(notExistIn(temp.get(j), res))
					toBeAdded.add(0,temp.get(j));
				
			}
			
		}
		
		for(int i = 0 ;i<toBeAdded.size();i++)
		{	res.add(0,toBeAdded.get(i));
            if(toBeAdded.get(i).getAppliedStereotypes().toString().contains("fromrecord"))
            	stop=false;
		}
		
		}
		
	}catch(Exception e){
		
		writeToAppend("activityParameterNodeToBedeclared extra"+e.getMessage(), bureau);
	}
		removeDuplicat(res);
		return res;
	}
	
	
	
	public static void removeDuplicat(List<?> res)
	{
	for (int i = 0; i < res.size()-1; i++) {
		for (int j = i+1; j < res.size(); j++) {
			if(res.get(i)==res.get(j))
			{
				res.remove(j);
				j--;
			}
		}
	}
	}
	
	
	
	
	public static void main(String[] args) {

System.out.print(getAllNamesFromString("org.eclipse.uml2.uml.internal.impl.ActivityParameterNodeImpl@69164d1c (name: taxation_year, visibility: <unset>) (isLeaf: false) (ordering: FIFO, isControlType: false)org.eclipse.uml2.uml.internal.impl.ActivityParameterNodeImpl@5644b50e (name: test, visibility: <unset>) (isLeaf: false) (ordering: FIFO, isControlType: false)"));
	}

}
