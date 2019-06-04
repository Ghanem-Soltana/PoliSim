package Monitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.uml2.uml.Class;

import Main.SDG;
import snt.oclsolver.LogicalExpresions.LogicalExpressionUtil;
import util.Invariant;
import util.OCLForJAVA;

public class DistributionChecker {

	
	LinkedList<HistogramClass> histClasses;
	LinkedList<Histogram> histAttributes;
	LinkedList<Histogram> histAssoc;
	EObject root;
	InvariantEvaluation invEvaluator;
	LinkedList<Histogram> myAssoc;
	LinkedList<HistogramClass> myclass;
	LinkedList<Histogram> myAtt;

	
	public LinkedList<Histogram> getMyAssoc() {
		return myAssoc;
	}
	public void setMyAssoc(LinkedList<Histogram> myAssoc) {
		this.myAssoc = myAssoc;
	}
	public LinkedList<HistogramClass> getMyclass() {
		return myclass;
	}
	public void setMyclass(LinkedList<HistogramClass> myclass) {
		this.myclass = myclass;
	}
	public LinkedList<Histogram> getMyAtt() {
		return myAtt;
	}
	public void setMyAtt(LinkedList<Histogram> myAtt) {
		this.myAtt = myAtt;
	}
	public LinkedList<HistogramClass> getHistClasses() {
		return histClasses;
	}
	public void setHistClasses(LinkedList<HistogramClass> histClasses) {
		this.histClasses = histClasses;
	}
	public LinkedList<Histogram> getHistAttributes() {
		return histAttributes;
	}
	public void setHistAttributes(LinkedList<Histogram> histAttributes) {
		this.histAttributes = histAttributes;
	}
	public LinkedList<Histogram> getHistAssoc() {
		return histAssoc;
	}
	public void setHistAssoc(LinkedList<Histogram> histAssoc) {
		this.histAssoc = histAssoc;
	}
	public EObject getRoot() {
		return root;
	}
	public void setRoot(EObject root) {
		this.root = root;
	}
	public InvariantEvaluation getInvEvaluator() {
		return invEvaluator;
	}
	public void setInvEvaluator(InvariantEvaluation invEvaluator) {
		this.invEvaluator = invEvaluator;
	}

	public DistributionChecker(LinkedList<HistogramClass> histClasses, LinkedList<Histogram> histAttributes,
			LinkedList<Histogram> histAssoc, LinkedList<HistogramClass> myclass, LinkedList<Histogram> myAtt, LinkedList<Histogram> myAssoc, EObject root) {
		super();
		this.histClasses = histClasses;
		this.histAttributes = histAttributes;
		this.histAssoc = histAssoc;
		this.root = root;
		this.invEvaluator = new InvariantEvaluation(root);
		this.myAtt = myAtt;
		this.myclass = myclass;
		this.myAssoc = myAssoc;
		
	}
	
	public HashMap<HistogramClass, HashMap<EClass, Integer>>  extractDiffClasses(LinkedList<HistogramClass> oldConstHis)
	{
	HashMap<HistogramClass, HashMap<EClass, Integer>> changesPerHistogram = new HashMap<HistogramClass, HashMap<EClass,Integer>>();
	for (int i = 0; i < oldConstHis.size(); i++) {
		HashMap<EClass, Integer> changes = new HashMap<EClass, Integer>();
		HistogramClass oldHistI = oldConstHis.get(i);
		HistogramClass newHistI = (HistogramClass)getCorrespendant(oldHistI, myclass);
	
			for (int j = 0; j < newHistI.values.size(); j++) {
				EClass value = newHistI.values.get(j);
				int newFreq = newHistI.frequencies.get(j);
				if(value!=null)
				{
				if(oldHistI!=null)
				{
				
				int oldFreq = 0;
				try{
			    oldFreq = oldHistI.frequencies.get(oldHistI.getIndexValue(value));
				}catch(Exception e){e.printStackTrace();}

				changes.put(value, oldFreq - newFreq);
				}
				else 
				{
				changes.put(value, newFreq);
				}
				}

	}
			changesPerHistogram.put(newHistI, changes);
	}

	return changesPerHistogram;
	}
	
	
	public HashMap<Histogram, HashMap<String, Integer>>  extractDiffAttributes(LinkedList<Histogram> oldConstHis)
	{
	HashMap<Histogram, HashMap<String, Integer>> changesPerHistogram = new HashMap<Histogram, HashMap<String,Integer>>();
	for (int i = 0; i < oldConstHis.size(); i++) {
		HashMap<String, Integer> changes = new HashMap<String, Integer>();
		Histogram oldHistI = oldConstHis.get(i);
		Histogram newHistI = (Histogram)getCorrespendant(oldHistI, myAtt);

	
			for (int j = 0; j < newHistI.values.size(); j++) {
				String value = newHistI.values.get(j);
				int newFreq = newHistI.frequencies.get(j);
				if(value!=null)
				{
				if(oldHistI!=null)
				{
				
				int oldFreq = 0;
				try{
			    oldFreq = oldHistI.frequencies.get(oldHistI.getIndexValue(value));
				}catch(Exception e){e.printStackTrace();}

				changes.put(value, oldFreq - newFreq);
				}
				else 
				{
				changes.put(value, newFreq);
				}
				}

	}
			changesPerHistogram.put(newHistI, changes);
	}

	return changesPerHistogram;
	}
	
	
	
	public HashMap<Histogram, HashMap<String, Integer>>  extractDiffAssociations(LinkedList<Histogram> oldConstHis)
	{
	HashMap<Histogram, HashMap<String, Integer>> changesPerHistogram = new HashMap<Histogram, HashMap<String,Integer>>();
	for (int i = 0; i < oldConstHis.size(); i++) {
		HashMap<String, Integer> changes = new HashMap<String, Integer>();

		Histogram oldHistI = oldConstHis.get(i);
		Histogram newHistI = (Histogram)getCorrespendant(oldHistI, myAssoc);

	
			for (int j = 0; j < newHistI.values.size(); j++) {
				String value = newHistI.values.get(j);
				int newFreq = newHistI.frequencies.get(j);
				if(value!=null)
				{
				if(oldHistI!=null)
				{
				
				int oldFreq = 0;
				try{
			    oldFreq = oldHistI.frequencies.get(oldHistI.getIndexValue(value));
				}catch(Exception e){e.printStackTrace();}

				changes.put(value, oldFreq - newFreq);
				}
				else 
				{
				changes.put(value, newFreq);
				}
				}

	}
			changesPerHistogram.put(newHistI, changes);
	}

	return changesPerHistogram;
	}
	
	
	
	
	public List<Invariant> generateAdditionalConstraintsFromClasses(EObject initalEObject, LinkedList<EObject> roots) {
		List<Invariant> res = new BasicEList<Invariant>();
		LinkedList<HistogramClass> targetedPopHist = ProfileConstraintExtractor.hitogramsForClasses;
		LinkedList<HistogramClass> looper = myclass;
		LinkedList<HistogramClass> realHist = histClasses;
		ArrayList<HistogramClass> treated = new ArrayList<HistogramClass>();
		
		for (HistogramClass myhistogram : looper) {
			if(treated.contains(myhistogram))
				continue;
			
			
			ArrayList<HistogramClass> conflicting = getConflictingHistogramsForClasses(looper, treated, myhistogram);
			treated.addAll(conflicting);
			
			if(conflicting.size()>1)
				System.out.println();
			
			 Invariant temp = null;
			 temp= generateOCLForClassPerHistImproved(conflicting,targetedPopHist,realHist);
			 if(temp!=null)
			 res.add(temp);
		}
		
		
		return res;
	} 
	
	

	public List<Invariant> generateAdditionalConstraintsFromAttributesOrAssociation(EObject initalEObject, LinkedList<EObject> roots, int type) {
		//1 ==>attribute, 2==> assocation 
		List<Invariant> res = new BasicEList<Invariant>();
		LinkedList<Histogram> targetedPopHist = null;
		if(type==1)
		targetedPopHist = ProfileConstraintExtractor.hitogramsForAttributes;
			
		if(type==2)
		targetedPopHist = ProfileConstraintExtractor.hitogramsForAssociations;
		
		
		LinkedList<Histogram> looper = null;
		if(type==1)
		looper = myAtt;
		if(type==2)
		looper = myAssoc;
		
		LinkedList<Histogram> realHist = null;
		
		if(type==1)
		realHist = histAttributes;
		if(type==2)
		realHist = histAssoc;
		
		ArrayList<Histogram> treated = new ArrayList<Histogram>();
		
		for (Histogram myhistogram : looper) {
		if(treated.contains(myhistogram))
			continue;
			
			ArrayList<Histogram> conflicting = getConflictingHistograms(looper, treated, myhistogram);
			treated.addAll(conflicting);

			 Invariant temp = null;
			 temp= generateOCLMixtPerHist(conflicting,targetedPopHist,realHist);
			 if(temp!=null)
			 res.add(temp);
		}
		
		
		return res;
	} 
	
	
	
	private ArrayList<HistogramClass> getConflictingHistogramsForClasses(LinkedList<HistogramClass> looper, ArrayList<HistogramClass> treated, HistogramClass current) {
		ArrayList<HistogramClass> res = new ArrayList<HistogramClass>();
		res.add(current);
		for (HistogramClass histogram : looper) {
			if(!treated.contains(histogram))
			if(current.root.equals(histogram.root))
			if(!res.contains(histogram))
			res.add(histogram);
			
		}
		return res;
	}
	
	private ArrayList<Histogram> getConflictingHistograms(LinkedList<Histogram> looper, ArrayList<Histogram> treated, Histogram current) {
		ArrayList<Histogram> res = new ArrayList<Histogram>();
		res.add(current);
		for (Histogram histogram : looper) {
			if(!treated.contains(histogram))
			if(current.elem.equals(histogram.elem))
			if(!res.contains(histogram))
			res.add(histogram);
			
		}
		return res;
	}
	
	private Invariant generateOCLMixtPerHist(ArrayList<Histogram> conflictingHistograms, LinkedList<Histogram> targetedPopHist, LinkedList<Histogram> realHist) {
		Invariant res = null;
		if(conflictingHistograms.isEmpty())
			return res;
		
		ArrayList<String> toOR = new ArrayList<String>();
		
		ArrayList<String> selectOrss = new ArrayList<String>();

		boolean isConditional = false;
		if(conflictingHistograms.size()>1)
		isConditional = true;
		else  isConditional = conflictingHistograms.get(0).context!=null&&!conflictingHistograms.get(0).condition.trim().equals("")&&!conflictingHistograms.get(0).condition.trim().equals("true");

		HashMap<Histogram, String> mapConditions = new  HashMap<Histogram, String>();
		HashMap<Histogram, EClass> mapContexts = new  HashMap<Histogram, EClass>();
		if(isConditional)
		for (Histogram myhistogram : conflictingHistograms) {
			mapContexts.put(myhistogram, myhistogram.context);
			String temPC = getMyCondition(conflictingHistograms,myhistogram);
			mapConditions.put(myhistogram, temPC);
		}
		
		for (Histogram myhistogram : conflictingHistograms) {
		Histogram targetedpopHisCorr = (Histogram)getCorrespendant(myhistogram, targetedPopHist);
		Histogram realPopHist = (Histogram)getCorrespendant(myhistogram, realHist);
		String ocla = "";
		ArrayList<String> oclas = new ArrayList<String>();
		ArrayList<String> oclOver = new ArrayList<String>();
		ArrayList<String> oclUnder = new ArrayList<String>();
		EStructuralFeature att = (EStructuralFeature) conflictingHistograms.get(0).elem;
		EClass context = att.getEContainingClass();
		EClass contextConstraint = mapContexts.get(myhistogram);
		boolean isMany = att.isMany();

		
		if(myhistogram!=null && targetedpopHisCorr !=null && realPopHist!=null)
		if(myhistogram.values.size()==targetedpopHisCorr.values.size() && myhistogram.values.size() == realPopHist.values.size())
		{
			
		
			
		for (int i = 0; i < myhistogram.values.size(); i++) {
		String value = myhistogram.values.get(i);
		
		if(value.equals(targetedpopHisCorr.values.get(i)) && value.equals(realPopHist.values.get(i)))
		{     
			
			
			
			double targetedProba = new Double(targetedpopHisCorr.probabilities.get(i)).doubleValue();
			if(targetedProba==1)
			break;
			double currentProba = new Double(realPopHist.probabilities.get(i)).doubleValue();
			if(currentProba==targetedProba)
			continue;
			boolean isOverpopulated = true;
			if(currentProba==targetedProba|| Math.abs(currentProba-targetedProba)<=SDG.diffTolerance)
				continue;
			if(currentProba<targetedProba)
			{
			if((targetedProba-currentProba)>SDG.diffTolerance)
			isOverpopulated = false;
			}
			else 
			{
				if((currentProba-targetedProba)>SDG.diffTolerance)
				continue;
			}
			int myCount = myhistogram.frequencies.get(i);
			
	
			
			if(isOverpopulated)
			{
				if(myCount==0)
				{
					String p1="";
					if(att instanceof EAttribute)
					{
					if(!isMany)
					p1 = getExpressionFromValue("self."+att.getName(),value,false).trim();
					else p1 = "self."+att.getName()+"->forAll(t:"+att.getEType().getName()+"|"+getExpressionFromValueMANY(value,false).trim()+")";
					}
					
					if(att instanceof EReference)
					{
					if(!isMany)
					p1 = "self.oclIsUndefined() = true";
					else p1 = getExpressionFromValue("self."+att.getName()+"->size()",value,false).trim();
					}
						
					if(!isConditional)
					{
					 oclOver.add(p1);	
					}
					else 
						{oclOver.add(treatSelf(p1, contextConstraint, context).trim());	}
				}
				else {
				
					LinkedList<EObject> contirbutors = new LinkedList<EObject>();
					if(att instanceof EAttribute)
					contirbutors = getObjectInsideMeForBinATT(value,myhistogram);
					if(att instanceof EReference)
					contirbutors = getObjectInsideMeForBinAssoc(value,myhistogram);
					
					ArrayList<String> ands  = new ArrayList<String>();
					for (EObject eObject : contirbutors) {
						EAttribute idAttribute = getIdAttribute(eObject.eClass());
						if(idAttribute!=null)
						{
						String id_name = idAttribute.getName();
						String id = String.valueOf(eObject.eGet(idAttribute));
						String type = idAttribute.getEType().getName();
						if(id!=null)
						{
						String self = context.getName()+".allInstances()->select("+id_name+" = "+updateCasseString(id,type)+")->forAll(";
						selectOrss.add(id_name+" = "+updateCasseString(id,type));
						String p1 ="";
						
						if(att instanceof EAttribute)
						p1 = getExpressionFromValue(att.getName(),value,false).trim();
						
						
						if(att instanceof EReference)
						{
							
						if(!isMany)
						 p1 = "oclIsUndefined() = true";
						else p1 = getExpressionFromValue(att.getName()+"->size()",value,false).trim();
						}
						
						p1= self+p1+")";
						
						if(!isConditional)
						ands.add(p1);	
						else ands.add(treatSelf(p1, contextConstraint, context).trim());	
							
								
						}
						}
					}			
					
					
					oclOver.add(concatANDS(ands));
				}
			}
			

			if(!isOverpopulated)
			{
				
				if(myCount==0)
				{
					String p1="";
					if(att instanceof EAttribute)
					{
					if(!isMany)
					p1 = getExpressionFromValue("self."+att.getName(),value,true).trim();
					else p1 = "self."+att.getName()+"->forAll(t:"+att.getEType().getName()+"|"+getExpressionFromValueMANY(value,true).trim()+")";
					}
					
					if(att instanceof EReference)
					{
					if(!isMany)
					p1 = "not self.oclIsUndefined()";
					else p1 = getExpressionFromValue("self."+att.getName()+"->size()",value,true).trim();
					}
						
					if(!isConditional)
					{
					 oclUnder.add(p1);	
					}
					else 
						{oclUnder.add(treatSelf(p1, contextConstraint, context).trim());	}
				}
				else
				{
					LinkedList<EObject> contirbutors = new LinkedList<EObject>();
					if(att instanceof EAttribute)
					contirbutors = getObjectInsideMeForBinATT(value,myhistogram);
					if(att instanceof EReference)
					contirbutors = getObjectInsideMeForBinAssoc(value,myhistogram);
					
					ArrayList<String> ors  = new ArrayList<String>();
					for (EObject eObject : contirbutors) {
						EAttribute idAttribute = getIdAttribute(eObject.eClass());
						if(idAttribute!=null)
						{
						String id_name = idAttribute.getName();
						String id = String.valueOf(eObject.eGet(idAttribute));
						String type = idAttribute.getEType().getName();
						if(id!=null)
						{
						String self = context.getName()+".allInstances()->select("+id_name+" = "+updateCasseString(id,type)+")->forAll(";
						selectOrss.add(id_name+" = "+updateCasseString(id,type));
						String p1 ="";
						if(att instanceof EAttribute)
						p1 = getExpressionFromValue(att.getName(),value,true).trim();
						
						
						
						if(att instanceof EReference)
						{
							
						if(!isMany)
						 p1 = "not "+ att.getName()+"oclIsUndefined()";
						else p1 = getExpressionFromValue(att.getName()+"->size()",value,true).trim();
						}
						
						p1=self+p1+")";
						
						if(!isConditional)
						ors.add(p1);	
						else ors.add(treatSelf(p1, contextConstraint, context).trim());	
						
						}
						}
					}
					
					
				
						oclUnder.add(concatORS(ors));
					
				}
					
			}
	
		}
		
		}
		
		String up = "";
		String down = "";
		
		 up = concatANDS(oclOver);
		 down = concatORS(oclUnder);

		
		if(!up.equals(""))
			oclas.add(up);
		if(!down.equals(""))
			oclas.add(down);
		
		removeDuplicatOCL(oclas);
		ocla=concatANDS(oclas);
		
		if(!ocla.trim().equals(""))
		{	
			Set<String> hs = new HashSet<>();
			hs.addAll(selectOrss);
			selectOrss.clear();
			selectOrss.addAll(hs);
			if(!isConditional)
			toOR.add("("+ocla+")");
		else
			{
			
			String condition =mapConditions.get(myhistogram);
			condition = LogicalExpressionUtil.normelizeNot(condition);
			condition =LogicalExpressionUtil.introduceNotIntoBlock(condition);
			condition = condition.replace("( ", "(");
			toOR.add("((not("+condition+")) or (("+condition+") and (" + ocla+")))");
			
			}
			}
		}
		
		}
		
		
		if(!toOR.isEmpty())
		{	
		String finalOCL = concatORS(toOR);
		res = new Invariant(((EStructuralFeature) conflictingHistograms.get(0).elem).getEContainingClass(), finalOCL);
		
		}
		return res;
	}
	
	
	
	private String getMyConditionForClasses(ArrayList<HistogramClass> conflictingHistograms, HistogramClass myhistogram) {
		String res = "";
		boolean isConditional=myhistogram.context!=null&&!myhistogram.condition.trim().equals("")&&!myhistogram.condition.trim().equals("true");
		ArrayList<String> toAnd = new ArrayList<String>();
		if(isConditional)
			toAnd.add(myhistogram.condition.trim());
		
		for (HistogramClass iHist : conflictingHistograms) {
			if(!iHist.equals(myhistogram))
			{
				isConditional=iHist.context!=null&&!iHist.condition.trim().equals("")&&!iHist.condition.trim().equals("true");
				if(isConditional)
				{
					String condition =iHist.condition.trim();
					condition = LogicalExpressionUtil.normelizeNot(condition);
					condition =LogicalExpressionUtil.introduceNotIntoBlock(condition);
					condition = condition.replace("( ", "(");
				toAnd.add("not ("+condition+")");
				}
			}
			
		}
		
		res = concatANDS(toAnd);
		return res;
	}
	
	private String getMyCondition(ArrayList<Histogram> conflictingHistograms, Histogram myhistogram) {
		String res = "";
		boolean isConditional=myhistogram.context!=null&&!myhistogram.condition.trim().equals("")&&!myhistogram.condition.trim().equals("true");
		ArrayList<String> toAnd = new ArrayList<String>();
		if(isConditional)
			toAnd.add(myhistogram.condition.trim());
		
		for (Histogram iHist : conflictingHistograms) {
			if(!iHist.equals(myhistogram))
			{
				isConditional=iHist.context!=null&&!iHist.condition.trim().equals("")&&!iHist.condition.trim().equals("true");
				if(isConditional)
				{
					String condition =iHist.condition.trim();
					condition = LogicalExpressionUtil.normelizeNot(condition);
					condition =LogicalExpressionUtil.introduceNotIntoBlock(condition);
					condition = condition.replace("( ", "(");
				toAnd.add("not ("+condition+")");
				}
			}
			
		}
		
		res = concatANDS(toAnd);
		return res;
	}
	public List<Invariant> generateAdditionalConstraintsFromAttributes(EObject initalEObject, LinkedList<EObject> roots) {
		List<Invariant> res = new BasicEList<Invariant>();
		LinkedList<Histogram> targetedPopHistAttributes = ProfileConstraintExtractor.hitogramsForAttributes;
		
		
		for (Histogram myhistogramAttribute : myAtt) {
			Histogram popHisCorrAttribute = (Histogram)getCorrespendant(myhistogramAttribute, targetedPopHistAttributes);
			Histogram realPopHist = (Histogram)getCorrespendant(myhistogramAttribute, histAttributes);
			 Invariant temp = null;
			if(realPopHist!=null)
			 temp= generateOCLForAttributePerHist(myhistogramAttribute, popHisCorrAttribute,realPopHist);
			 if(temp!=null)
			 res.add(temp);
		}
		
		
		return res;
	} 
	
	
	
	public static void removeDuplicatOCL(ArrayList<String> list)
	{
		if(list!=null)
		{
		LinkedHashSet<String> s = new LinkedHashSet<String>(list);
		list.clear();
		list.addAll(s);
		}
	}
	
	
	//We do not have conditions here
	private Invariant generateOCLForAttributePerHist(Histogram myhistogramAttribute,Histogram targetedPopoHistAttribute, Histogram reaPopHist) {
		Invariant res = null;
		ArrayList<String> oclas = new ArrayList<String>();
		ArrayList<String> oclOver = new ArrayList<String>();
		ArrayList<String> oclUnder = new ArrayList<String>();
		ArrayList<String> oclasWithConditionUnder = new ArrayList<String>();
		ArrayList<String> oclasWithConditionOver = new ArrayList<String>();
		EAttribute att = (EAttribute) myhistogramAttribute.elem;
		EClass context = att.getEContainingClass();
		boolean isConditional = myhistogramAttribute.context!=null&&!myhistogramAttribute.condition.trim().equals("")&&!myhistogramAttribute.condition.trim().equals("true");
		EClass contextConstraint = null;
		if(isConditional)
		contextConstraint = myhistogramAttribute.getContext();
		boolean isMany = att.isMany();
		
		if(myhistogramAttribute!=null && targetedPopoHistAttribute !=null && reaPopHist!=null)
		if(myhistogramAttribute.values.size()==targetedPopoHistAttribute.values.size() && myhistogramAttribute.values.size() == reaPopHist.values.size())
		{
			
		
			
		for (int i = 0; i < myhistogramAttribute.values.size(); i++) {
		String value = myhistogramAttribute.values.get(i);
		
		if(value.equals(targetedPopoHistAttribute.values.get(i)) && value.equals(reaPopHist.values.get(i)))
		{     
			
			
			
			double targetedProba = new Double(targetedPopoHistAttribute.probabilities.get(i)).doubleValue();
			if(targetedProba==1)
			break;
			double currentProba = new Double(reaPopHist.probabilities.get(i)).doubleValue();
			if(currentProba==targetedProba)
			continue;
			boolean isOverpopulated = true;
			if(currentProba==targetedProba|| Math.abs(currentProba-targetedProba)<=SDG.diffTolerance)
				continue;
			if(currentProba<targetedProba)
			if( (targetedProba-currentProba)>SDG.diffTolerance)
			isOverpopulated = false;
			int myCount = myhistogramAttribute.frequencies.get(i);
			
	
			
			if(isOverpopulated && myCount!=0)
			{
	
				if(myCount==1)
				{
				String p1 = "";
					
				if(!isMany)
				p1 = getExpressionFromValue("self."+att.getName(),value,false).trim();
				else p1 = "self."+att.getName()+"->forAll(t:"+att.getEType().getName()+"|"+getExpressionFromValueMANY(value,false).trim()+")";
				
				
				if(!isConditional)
				{
				 oclOver.add(p1);	
				}
				else 
					{
					String newOCL = "("+myhistogramAttribute.getCondition().trim()+") and "+"("+treatSelf(p1, contextConstraint, context).trim()+")";
					oclasWithConditionOver.add(newOCL);	
					}
				
				}
				else 
				{
					LinkedList<EObject> contirbutors = getObjectInsideMeForBinATT(value,myhistogramAttribute);
					
					ArrayList<String> ors  = new ArrayList<String>();
					for (EObject eObject : contirbutors) {
						EAttribute idAttribute = getIdAttribute(eObject.eClass());
						if(idAttribute!=null)
						{
						String id_name = idAttribute.getName();
						String id = String.valueOf(eObject.eGet(idAttribute));
						String type = idAttribute.getEType().getName();
						if(id!=null)
						{
						String self = context.getName()+".allInstances()->any("+id_name+" = "+updateCasseString(id,type)+").";
						
						String p1 ="";
						if(!isMany)
						 p1 = getExpressionFromValue(self+att.getName(),value,false).trim();
						else p1 = self+att.getName()+"->forAll(t:"+att.getEType().getName()+"|"+getExpressionFromValueMANY(value,false).trim()+")";
						
						if(!isConditional)
						{
						ors.add(p1);	
						}
						else 
							{
							String newOCL = "("+myhistogramAttribute.getCondition().trim()+") and "+"("+treatSelf(p1, contextConstraint, context).trim()+")";
							ors.add(newOCL);	
							}
								
						}
						}
					}
					
					
					if(isConditional)
					{
					oclasWithConditionOver.add(concatORS(ors));
					}
					else {
						oclOver.add(concatORS(ors));
					}
					
						
				}
				
			}
			

			if(!isOverpopulated)
			{
				
				if(myCount==0||myCount==1)
				{
					String p1="";
					if(!isMany)
						p1 = getExpressionFromValue("self."+att.getName(),value,true).trim();
						else p1 = "self."+att.getName()+"->forAll(t:"+att.getEType().getName()+"|"+getExpressionFromValueMANY(value,true).trim()+")";
						
					if(!isConditional)
					{
					 oclUnder.add(p1);	
					}
					else 
						{
						String newOCL = "("+myhistogramAttribute.getCondition().trim()+") and "+"("+treatSelf(p1, contextConstraint, context).trim()+")";
						oclasWithConditionUnder.add(newOCL);	
						}
				}
				else
				{
					LinkedList<EObject> contirbutors = getObjectInsideMeForBinATT(value,myhistogramAttribute);
					
					ArrayList<String> ors  = new ArrayList<String>();
					for (EObject eObject : contirbutors) {
						EAttribute idAttribute = getIdAttribute(eObject.eClass());
						if(idAttribute!=null)
						{
						String id_name = idAttribute.getName();
						String id = String.valueOf(eObject.eGet(idAttribute));
						String type = idAttribute.getEType().getName();
						if(id!=null)
						{
						String self = context.getName()+".allInstances()->any("+id_name+" = "+updateCasseString(id,type)+").";
						
						String p1 ="";
						if(!isMany)
						 p1 = getExpressionFromValue(self+att.getName(),value,true).trim();
						else p1 = self+att.getName()+"->forAll(t:"+att.getEType().getName()+"|"+getExpressionFromValueMANY(value,true).trim()+")";
						
						if(!isConditional)
						{
						ors.add(p1);	
						}

						else 
							{
							String newOCL = "("+myhistogramAttribute.getCondition().trim()+") and "+"("+treatSelf(p1, contextConstraint, context).trim()+")";
							ors.add(newOCL);	
							}
						
						
						}
						}
					}
					
					
					if(isConditional)
					{
					oclasWithConditionUnder.add(concatORS(ors));
					}
					else {
						oclUnder.add(concatORS(ors));
					}
				}
					
			}
	
		}
		
		}
		
		String up = "";
		String down = "";
		
		if(!isConditional)
		{
		 up = concatANDS(oclOver);
		 down = concatORS(oclUnder);
		}
		else 
		{ up = concatANDS(oclasWithConditionOver);
		 down = concatORS(oclasWithConditionUnder);
		}
		
		if(!up.equals(""))
			oclas.add(up);
		if(!down.equals(""))
			oclas.add(down);
		
		removeDuplicatOCL(oclas);
		String ocla=concatANDS(oclas);
		
		if(!ocla.trim().equals(""))
		res = new Invariant(!isConditional?context:contextConstraint, ocla);
		try{res.toString();
		}catch(Exception e){return null;}
		}
		//System.out.println();
		return res;
	}
	
	
	private String concatORS(ArrayList<String>oclas) {
		String ocla = "";
		if(oclas.size()==1)
		return oclas.get(0);
		for (int i = 0; i < oclas.size(); i++) {
			if(i==0)
			ocla = "("+oclas.get(0)+")";
			else ocla = ocla + " or "+ "("+oclas.get(i)+")";
		}
		return ocla;
	}
	
	private String concatANDS(ArrayList<String>oclas) {
		String ocla = "";
		if(oclas.size()==1)
			return oclas.get(0);
		for (int i = 0; i < oclas.size(); i++) {
			if(i==0)
			ocla = "("+oclas.get(0)+")";
			else ocla = ocla + " and "+ "("+oclas.get(i)+")";
		}
		return ocla;
	}
	
	
	private static String treatSelf(String p1,   EClass target,  EClass source) {
		if(target==null||source==null)
		return p1;
		else if(target==source)
		return p1;
		else if(target.getEAllSuperTypes().contains(source))
		return p1;
		else
		{
			

		  
		  boolean find = false;
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
	
	
	private LinkedList<EObject> getObjectInsideMeForBinClass(EClass classe1, HistogramClass histogramClass) {
	LinkedList<EObject> res = new LinkedList<EObject>();
		
		if(root!=null)
		{
		if(histogramClass.callingRef==null&&histogramClass.root.getName().equals("Tax_Case"))
		{

			EClass c = classe1;
			String toBeEvaluated = "self.oclIsKindOf("+c.getName()+")";
			OCLForJAVA.init("",root);
			try {
			boolean belong = OCLForJAVA.evaluateBoolean(root, toBeEvaluated, "temp");
			if(belong)
			{
				res.add(root);
			}
			} catch (Exception e) {e.printStackTrace();}
			
		
		}
		else 
		{
			OCLForJAVA.init("",root);
	
				EClass c = classe1;
				if(histogramClass.callingRef!=null)
				 c=histogramClass.callingRef.getEContainingClass();
				if(!histogramClass.condition.trim().equalsIgnoreCase("true"))
				c=histogramClass.callingRef.getEContainingClass();
			
				
				LinkedList<EObject> newRoots = new LinkedList<EObject>();
				EList<EObject> insideMe = new BasicEList<EObject>();
				extractEveryThingFromRoot(root, insideMe);
				for (EObject obj : insideMe) {	
					EClass myClass = ((EClass)obj.eClass());
					boolean pass = false;
					if(myClass.getName().equals(c.getName()))
					pass = true;
					else {
						 EList<EClass> fouch2 = myClass.getESuperTypes();
						 for (EClass eClass : fouch2) {
							if(eClass.getName().equals(c.getName()))
							pass=true;
						}
						 if(pass==false)
						 {
							 EList<EClass> fouch1 = c.getESuperTypes();
							 for (EClass eClass : fouch1) {
								if(eClass.getName().equals(c.getName()))
								pass=true;
							}
						 }
					}
					
				   
					if(pass)
					if(!newRoots.contains(obj))
					newRoots.add(obj);
				}
				
				c = classe1;
				
					if(!histogramClass.condition.trim().equalsIgnoreCase("true"))
						for (int j = 0; j < newRoots.size(); j++) {
							EObject eObject = newRoots.get(j);
							OCLForJAVA.init("",eObject);
							boolean test = OCLForJAVA.evaluateBoolean(eObject, histogramClass.condition.replace("self.", ""), "test");
							if(test==false)
							{
								newRoots.remove(j);
								j--;
							}
						}
					
					
					for (EObject root1 : newRoots) {
						String toBeEvaluated="0";
					
				    	 if(histogramClass.callingRef!=null)
				    	toBeEvaluated = "self."+histogramClass.callingRef.getName()+".oclAsSet()->iterate(inc;  acc:Bag("+histogramClass.callingRef.getEType().getName()+")=Bag{}  |"
				    			+ " if(inc.oclIsKindOf("+c.getName()+")) then acc->including(inc) else acc endif)->size()";
				    	 else toBeEvaluated = "self.oclIsKindOf("+c.getName()+")";

						OCLForJAVA.init("",root1);
						
						 if(histogramClass.callingRef==null)
						 {
						try {
						boolean belong = OCLForJAVA.evaluateBoolean(root1, toBeEvaluated, "temp");
						if(belong)
						{
							res.add(root1);
						}
						} catch (Exception e) {e.printStackTrace();} 
						}
						 else {
								try {
							 int count = OCLForJAVA.evaluateIntOut(root1, toBeEvaluated, "count");
							 if(count!=0)
							 {
								 res.add(root1);
							 }
								} catch (Exception e) {e.printStackTrace();} 
						 }
						}
	
		}
		
	}
		
	
		return res;
	}

	
	
	
	private LinkedList<EObject> getObjectInsideMeForBinATT(String value1, Histogram myhistogramAttribute) {
		LinkedList<EObject> res = new LinkedList<EObject>();
		
		if(root!=null)
		{
			
		OCLForJAVA.init("",root);
		EAttribute att = ((EAttribute)myhistogramAttribute.getElem());
		EClass c = (EClass) att.getEContainingClass();
		Collection<EObject> newRoots = new LinkedList<EObject>();
		EList<EObject> insideMe = new BasicEList<EObject>();
		extractEveryThingFromRoot(root, insideMe);
		for (EObject obj : insideMe) {	
			EClass myClass = ((EClass)obj.eClass());
			boolean pass = false;
			if(myClass.getName().equals(c.getName()))
			pass = true;
			else {
				 EList<EClass> fouch2 = myClass.getESuperTypes();
				 for (EClass eClass : fouch2) {
					if(eClass.getName().equals(c.getName()))
					pass=true;
				}
				 if(pass==false)
				 {
					 EList<EClass> fouch1 = c.getESuperTypes();
					 for (EClass eClass : fouch1) {
						if(eClass.getName().equals(c.getName()))
						pass=true;
					}
				 }
			}
			
		   
			if(pass)
			if(!newRoots.contains(obj))
			newRoots.add(obj);
		}
				String bin =value1;
				
				for (EObject root1 : newRoots) {
				OCLForJAVA.init("",root1);
				String toBeEvaluated="0";
					  if(!bin.contains(".."))
					  toBeEvaluated = "self."+att.getName()+" = "+bin;
					  else 
					  {
						  try{
						  	String value=bin.trim().replace("[", "").replace("]", "").replace("..", "-");
							String [] temp=value.split("-");
							double start=-1;
							double end=-1;
							start= Double.valueOf(temp[0].trim());
							end= Double.valueOf(temp[1].trim());
						    toBeEvaluated = "self."+att.getName()+" >= "+start+" and self."+att.getName()+" <= "+end;
						  }
						  catch(Exception w){w.printStackTrace();}
						
					  }
						boolean belong = OCLForJAVA.evaluateBoolean(root1, toBeEvaluated, "belong");
						if(belong)
						{
							res.add(root1);
						}
				
				}
			
		
		}
		
		
		return res;
	}
	

	private LinkedList<EObject> getObjectInsideMeForBinAssoc(String valueTemp, Histogram myhistogramAssociaiton) {
		LinkedList<EObject> res = new LinkedList<EObject>();
		
		if(root!=null)
		{
			
		OCLForJAVA.init("",root);
		
		EReference ref = (EReference) myhistogramAssociaiton.elem;
		EClass c = (EClass) ref.getEContainingClass();
		Collection<EObject> newRoots = new LinkedList<EObject>();
		EList<EObject> insideMe = new BasicEList<EObject>();
		extractEveryThingFromRoot(root, insideMe);
		for (EObject obj : insideMe) {	
			EClass myClass = ((EClass)obj.eClass());
			boolean pass = false;
			if(myClass.getName().equals(c.getName()))
			pass = true;
			else {
				 EList<EClass> fouch2 = myClass.getESuperTypes();
				 for (EClass eClass : fouch2) {
					if(eClass.getName().equals(c.getName()))
					pass=true;
				}
				 if(pass==false)
				 {
					 EList<EClass> fouch1 = c.getESuperTypes();
					 for (EClass eClass : fouch1) {
						if(eClass.getName().equals(c.getName()))
						pass=true;
					}
				 }
			}
			
		   
			if(pass)
			newRoots.add(obj);
		}
		
				String bin = valueTemp;
				
				for (EObject root1 : newRoots) {
				OCLForJAVA.init("",root1);
				String toBeEvaluated="0";
					  if(!bin.contains(".."))
					  toBeEvaluated = "self."+ref.getName()+"->size() = "+bin;
					  else 
					  {
						  try{
						  	String value=bin.trim().replace("[", "").replace("]", "").replace("..", "-");
							String [] temp=value.split("-");
							double start=-1;
							double end=-1;
							start= Double.valueOf(temp[0].trim());
							end= Double.valueOf(temp[1].trim());
						    toBeEvaluated = "self."+ref.getName()+"->size() >= "+start+" and self."+ref.getName()+"->size() <= "+end;
						  }
						  catch(Exception w){w.printStackTrace();}
						
					  }
						boolean belong = OCLForJAVA.evaluateBoolean(root1, toBeEvaluated, "belong");
						if(belong)
						{
								res.add(root1);
						}
				
				}
		
		}
		
		return res;
	}
	public static EAttribute getIdAttribute(EClass classe)
	{
		EStructuralFeature temp=null;
		int c = 0;
		EList<EStructuralFeature> features = classe.getEAllStructuralFeatures();
		while (temp==null && c<features.size()) {
			if(features.get(c) instanceof EAttribute)
				if(((EAttribute)features.get(c)).isID())
					temp = features.get(c);
			c++;
		}
		
		if(temp==null)
			return null;
		return (EAttribute)temp;
	}
	
	
	public static String getExpressionFromValueMANY(String value, boolean in)
	{
		
		String res="";
		if( (value.trim().startsWith("[")&&value.trim().endsWith("]"))==false)
		{  res=  in? "t = "+value :"t <> "+value;}
		
		
		
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
				
			
				
			res=  in?"( t >= "+String.valueOf(start)+" and t <= "+String.valueOf(end)+")" : " (t >= "+String.valueOf(start)+" and t <= "+String.valueOf(end)+") = false";
				 
			}
			
			if(value.trim().contains("-"))
			{
				value=value.trim().replace("[", "").replace("]", "");
				
				String [] temp=value.split("-");
				double start=-1;
				double end=-1;
				start= Double.valueOf(temp[0].trim());
				end= Double.valueOf(temp[1].trim());
				res= in==false? " (t >= "+String.valueOf(start)+" and t <= "+String.valueOf(end)+") = false":"t >= "+String.valueOf(start)+" and t <= "+String.valueOf(end);
			}
			
			if(value.trim().contains(","))
			{	res = "";
				String [] temp=value.trim().replace("[", "").replace("]", "").split(",");
				
				if(in)
				{
				for (int i = 0; i < temp.length; i++) {
					
					if(i==0)
					res = "t = "+temp[i];
					else res = res + " or t = "+temp[i];
				}
				}
				else for (int i = 0; i < temp.length; i++) {
					
					if(i==0)
					res = "t <> "+temp[i];
					else res = res + " and t <> "+temp[i];
				}

				
			}
		}

		
		//System.out.println("***********");
		//System.out.println(res);
		res = LogicalExpressionUtil.normelizeNot(res);
		res =LogicalExpressionUtil.introduceNotIntoBlock(res);
		res = res.replace("( ", "(");
		//System.out.println(res);
		//System.out.println("***********");
		return res;
	}


	public static String getExpressionFromValue(String p1, String value, boolean in)
	{
		
		String res="";
		if( (value.trim().startsWith("[")&&value.trim().endsWith("]"))==false)
		{  res =  in? p1+" = "+value :p1+" <> "+value;

		}
		
		
		
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
				
			
				
			res=  in?"(" +p1 + " >= "+String.valueOf(start)+" and " + p1+ " <= "+String.valueOf(end)+")" : " (" +p1 + " >= "+String.valueOf(start)+" and " + p1+ " <= "+String.valueOf(end)+") = false";
				 
			}
			
			if(value.trim().contains("-"))
			{
				value=value.trim().replace("[", "").replace("]", "");
				
				String [] temp=value.split("-");
				double start=-1;
				double end=-1;
				start= Double.valueOf(temp[0].trim());
				end= Double.valueOf(temp[1].trim());
				res= in==false? " (" + p1 + " >= "+String.valueOf(start)+" and " + p1+ " <= "+String.valueOf(end)+") = false":p1 + " >= "+String.valueOf(start)+" and " + p1+ " <= "+String.valueOf(end);
			}
			
			if(value.trim().contains(","))
			{	res = "";
				String [] temp=value.trim().replace("[", "").replace("]", "").split(",");
				
				if(in)
				{
				for (int i = 0; i < temp.length; i++) {
					
					if(i==0)
					res = p1+" = "+temp[i];
					else res = res + " or " + p1+" = "+temp[i];
				}
				}
				else for (int i = 0; i < temp.length; i++) {
					
					if(i==0)
					res = p1+" <> "+temp[i];
					else res = res + " and " + p1+" <> "+temp[i];
				}

				
			}
		}
		//System.out.println("***********");
		//System.out.println(res);
		res = LogicalExpressionUtil.normelizeNot(res);
		res =LogicalExpressionUtil.introduceNotIntoBlock(res);
		res = res.replace("( ", "(");
		//System.out.println(res);
		//System.out.println("***********");
		return res;
	}
	
	private Invariant generateOCLForClassPerHist(HistogramClass myhistogramClass, HistogramClass targetedPopHistClasses, HistogramClass reaPopHist) {
		Invariant res = null;
		ArrayList<String> oclas = new ArrayList<String>();
		ArrayList<String> oclOver = new ArrayList<String>();
		ArrayList<String> oclUnder = new ArrayList<String>();
		ArrayList<String> oclasWithConditionUnder = new ArrayList<String>();
		ArrayList<String> oclasWithConditionOver = new ArrayList<String>();
		EClass context = myhistogramClass.root;
		if(context.getName().equalsIgnoreCase("tax_case"))
		return null;

		boolean isConditional = myhistogramClass.context!=null&&!myhistogramClass.condition.trim().equals("")&&!myhistogramClass.condition.trim().equals("true");
		EClass contextConstraint = null;
		String condition = "";
		if(isConditional)
		{
		contextConstraint = myhistogramClass.getContext();
		condition=myhistogramClass.getCondition().trim();
		}

		
		if(myhistogramClass!=null && targetedPopHistClasses !=null && reaPopHist!=null)
		if(myhistogramClass.values.size()==targetedPopHistClasses.values.size() && myhistogramClass.values.size() == reaPopHist.values.size())
		{
			
		
			
		for (int i = 0; i < myhistogramClass.values.size(); i++) {
		EClass value = myhistogramClass.values.get(i);
		
		if(value.equals(targetedPopHistClasses.values.get(i)) && value.equals(reaPopHist.values.get(i)))
		{     
			
			
			
			double targetedProba = new Double(targetedPopHistClasses.probabilities.get(i)).doubleValue();
			if(targetedProba==1)
			break;
			double currentProba = new Double(reaPopHist.probabilities.get(i)).doubleValue();
			if(currentProba==targetedProba)
			continue;
			boolean isOverpopulated = true;
			if(currentProba==targetedProba|| Math.abs(currentProba-targetedProba)<=SDG.diffTolerance)
				continue;
			if(currentProba<targetedProba)
			{
			if((targetedProba-currentProba)>SDG.diffTolerance)
			isOverpopulated = false;
			}
			else 
			{
				if((currentProba-targetedProba)>SDG.diffTolerance)
				continue;
			}
			int myCount = myhistogramClass.frequencies.get(i);
			
	
			
			if(isOverpopulated)
			{
	
				if(myCount==1||myCount==0)
				{
				String p1 = "not self.oclIsKindOf("+value.getName()+")";

				if(!isConditional)
				{
				 oclOver.add(p1);	
				}
				else 
					{
					String newOCL = "("+condition+") and "+"("+treatSelf(p1, contextConstraint, context).trim()+")";
					oclasWithConditionOver.add(newOCL);	
					}
				
				}
				else 
				{
					LinkedList<EObject> contirbutors = getObjectInsideMeForBinClass(value,myhistogramClass);
					
					ArrayList<String> ands  = new ArrayList<String>();
					for (EObject eObject : contirbutors) {
						EAttribute idAttribute = getIdAttribute(eObject.eClass());
						if(idAttribute!=null)
						{
						String id_name = idAttribute.getName();
						String id = String.valueOf(eObject.eGet(idAttribute));
						String type = idAttribute.getEType().getName();
						if(id!=null)
						{
						String self = context.getName()+".allInstances()->any("+id_name+" = "+updateCasseString(id,type)+").";
						String p1 = "not "+self+"oclIsKindOf("+value.getName()+")";

						if(!isConditional)
						{
							ands.add(p1);	
						}
						else 
							{
							String newOCL = "("+condition+") and "+"("+treatSelf(p1, contextConstraint, context).trim()+")";
							ands.add(newOCL);	
							}
								
						}
						}
					}
								
					if(isConditional)
					{
					oclasWithConditionOver.add(concatANDS(ands));
					}
					else {
						oclOver.add(concatANDS(ands));
					}
					
						
				}
				
			}
			

			if(!isOverpopulated)
			{
				
				if(myCount==0||myCount==1)
				{
					String p1 = "self.oclIsKindOf("+value.getName()+")";
					
					if(!isConditional)
					{
					 oclUnder.add(p1);	
					}
					else 
						{
						String newOCL = "("+condition+") and "+"("+treatSelf(p1, contextConstraint, context).trim()+")";
						oclasWithConditionUnder.add(newOCL);	
						}
				}
				else
				{
					LinkedList<EObject> contirbutors = getObjectInsideMeForBinClass(value,myhistogramClass);
					
					ArrayList<String> ors  = new ArrayList<String>();
					for (EObject eObject : contirbutors) {
						EAttribute idAttribute = getIdAttribute(eObject.eClass());
						if(idAttribute!=null)
						{
						String id_name = idAttribute.getName();
						String id = String.valueOf(eObject.eGet(idAttribute));
						String type = idAttribute.getEType().getName();
						if(id!=null)
						{
						String self = context.getName()+".allInstances()->any("+id_name+" = "+updateCasseString(id,type)+").";
						
						String p1 = self+"oclIsKindOf("+value.getName()+")";
						if(!isConditional)
						{
						ors.add(p1);	
						}

						else 
							{
							String newOCL = "("+condition+") and "+"("+treatSelf(p1, contextConstraint, context).trim()+")";
							ors.add(newOCL);	
							}
						
						
						}
						}
					}
					
					
					if(isConditional)
					{
					oclasWithConditionUnder.add(concatORS(ors));
					}
					else {
						oclUnder.add(concatORS(ors));
					}
				}
					
			}
	
		}
		
		}
		
		String up = "";
		String down = "";
		
		if(!isConditional)
		{
		 up = concatANDS(oclOver);
		 down = concatORS(oclUnder);
		}
		else 
		{ up = concatANDS(oclasWithConditionOver);
		 down = concatORS(oclasWithConditionUnder);
		}
		
		if(!up.equals(""))
			oclas.add(up);
		if(!down.equals(""))
			oclas.add(down);
		
		removeDuplicatOCL(oclas);
		String ocla=concatANDS(oclas);
		
		if(!ocla.trim().equals(""))
		res = new Invariant(!isConditional?context:contextConstraint, ocla);
		try{res.toString();
		}catch(Exception e){return null;}
		}
		//System.out.println();
		return res;
	}
	
	
	//TODO
	private Invariant generateOCLForClassPerHistImproved(ArrayList<HistogramClass> conflictingHistograms, LinkedList<HistogramClass> targetedPopHistAll, LinkedList<HistogramClass> realHistAll) {
		Invariant res = null;
		if(conflictingHistograms.isEmpty())
			return res;
		ArrayList<String> selectOrs = new ArrayList<String>();
		ArrayList<String> toOR = new ArrayList<String>();
		EClass overAllContext = null;
		for (HistogramClass temp : conflictingHistograms) {
			if(temp.context!=null)
				overAllContext= temp.context;
		}
		
		
		if(overAllContext==null)
			overAllContext = conflictingHistograms.get(0).root;
		

		boolean isConditional = false;
		if(conflictingHistograms.size()>1)
		isConditional = true;
		else  isConditional = conflictingHistograms.get(0).context!=null&&!conflictingHistograms.get(0).condition.trim().equals("")&&!conflictingHistograms.get(0).condition.trim().equals("true");

		HashMap<HistogramClass, String> mapConditions = new  HashMap<HistogramClass, String>();
		HashMap<HistogramClass, EClass> mapContexts = new  HashMap<HistogramClass, EClass>();
		if(isConditional)
		for (HistogramClass myhistogram : conflictingHistograms) {
			mapContexts.put(myhistogram, myhistogram.context);
			String temPC = getMyConditionForClasses(conflictingHistograms,myhistogram);
			mapConditions.put(myhistogram, temPC);
		}
		
		for (HistogramClass myhistogram : conflictingHistograms) {
		String ocla= "";
		HistogramClass targetedPopHist = (HistogramClass)getCorrespendant(myhistogram, targetedPopHistAll);
		HistogramClass realHist = (HistogramClass)getCorrespendant(myhistogram, realHistAll);
		if(conflictingHistograms.size()==1)
		{ int nbOnes = 0;
		for (int i = 0; i < targetedPopHist.probabilities.size(); i++) {
			if(targetedPopHist.probabilities.get(i)==1.0)
				nbOnes++;
		}
			if(nbOnes==1)
			return null;
		}
		ArrayList<String> oclas = new ArrayList<String>();
		ArrayList<String> oclOver = new ArrayList<String>();
		ArrayList<String> oclUnder = new ArrayList<String>();
		EClass context = myhistogram.root;
		
		if(myhistogram!=null && targetedPopHist !=null && realHist!=null)
		if(myhistogram.values.size()==targetedPopHist.values.size() && myhistogram.values.size() == realHist.values.size())
		{
			
			if(myhistogram.getRoot().getName().contains("Tax_Case"))
				continue;
		
		for (int i = 0; i < myhistogram.values.size(); i++) {
		EClass value = myhistogram.values.get(i);
	
		
		if(value.equals(targetedPopHist.values.get(i)) && value.equals(realHist.values.get(i)))
		{     
			
			
			
			double targetedProba = new Double(targetedPopHist.probabilities.get(i)).doubleValue();
	
			double currentProba = new Double(realHist.probabilities.get(i)).doubleValue();
			boolean isOverpopulated = true;

			
			
			if(currentProba==targetedProba|| Math.abs(currentProba-targetedProba)<=SDG.diffTolerance)
				continue;
			if(currentProba<targetedProba)
			{
			if((targetedProba-currentProba)>=SDG.diffTolerance)
			isOverpopulated = false;
			}
			else 
			{
				if((currentProba-targetedProba)>=SDG.diffTolerance)
				continue;
			}
			int myCount = myhistogram.frequencies.get(i);
			
	
			
			if(isOverpopulated)
			{
	
				if(myCount==1||myCount==0)
				{
				String p1 = "not self.oclIsKindOf("+value.getName()+")";
				oclOver.add(treatSelf(p1, context,overAllContext));	
				
				}
				else 
				{
					LinkedList<EObject> contirbutors = getObjectInsideMeForBinClass(value,myhistogram);
					
					ArrayList<String> ands  = new ArrayList<String>();
					for (EObject eObject : contirbutors) {
						EAttribute idAttribute = getIdAttribute(eObject.eClass());
						if(idAttribute!=null)
						{
						String id_name = idAttribute.getName();
						String id = String.valueOf(eObject.eGet(idAttribute));
						String type = idAttribute.getEType().getName();
						if(id!=null)
						{
						String self = context.getName()+".allInstances()->any("+id_name+" = "+updateCasseString(id,type)+").";
						selectOrs.add(id_name+" = "+updateCasseString(id,type));
						String p1 = self+"not oclIsKindOf("+value.getName()+")";
						
	
							ands.add(treatSelf(p1, context,overAllContext));	
						}
				
						}
						}
					oclOver.add(concatANDS(ands));
					}							
			}
			

			if(!isOverpopulated)
			{
				
				if(myCount==0||myCount==1)
				{
					String p1 = "self.oclIsKindOf("+value.getName()+")";
					 oclUnder.add(treatSelf(p1, context,overAllContext));	
				}
				else
				{
					LinkedList<EObject> contirbutors = getObjectInsideMeForBinClass(value,myhistogram);
					
					ArrayList<String> ors  = new ArrayList<String>();
					for (EObject eObject : contirbutors) {
						EAttribute idAttribute = getIdAttribute(eObject.eClass());
						if(idAttribute!=null)
						{
						String id_name = idAttribute.getName();
						String id = String.valueOf(eObject.eGet(idAttribute));
						String type = idAttribute.getEType().getName();
						if(id!=null)
						{
						String self = context.getName()+".allInstances()->any("+id_name+" = "+updateCasseString(id,type)+").";
						selectOrs.add(id_name+" = "+updateCasseString(id,type));
						String p1 = self+"oclIsKindOf("+value.getName()+")";
						
						ors.add(treatSelf(p1, context,overAllContext));		
						
						}
						}
					}
	
						oclUnder.add(concatORS(ors));
					
				}
					
			}
	
		}
		
		}
		
		String up = "";
		String down = "";
		
		 up = concatANDS(oclOver);
		 down = concatORS(oclUnder);

		
		if(!up.equals(""))
			oclas.add(up);
		if(!down.equals(""))
			oclas.add(down);
		
		removeDuplicatOCL(oclas);
		ocla=concatANDS(oclas);
		
		if(!ocla.trim().equals(""))
		{	
		if(!isConditional)
			toOR.add(ocla);
		else {
			
			String condition =mapConditions.get(myhistogram);
			condition = LogicalExpressionUtil.normelizeNot(condition);
			condition =LogicalExpressionUtil.introduceNotIntoBlock(condition);
			condition = condition.replace("( ", "(");
		toOR.add( "(not("+condition+")) or (("+condition+") and " + "("+ocla+"))");
		}
		}
		else 
		{
			if(conflictingHistograms.size()>1)
			{
				String elseExpression = "";
				ArrayList<String> toAndForElse = new ArrayList<String>();
				for (HistogramClass tempH : conflictingHistograms) {
					if(!tempH.getCondition().trim().equals("true"))
					{
						String condition =tempH.getCondition().trim();
						condition = LogicalExpressionUtil.normelizeNot(condition);
						condition =LogicalExpressionUtil.introduceNotIntoBlock(condition);
						condition = condition.replace("( ", "(");
				
						toAndForElse.add(condition);
					}					
				}
				
				elseExpression = concatANDS(toAndForElse);
			
			if(!elseExpression.trim().equals(""))
			toOR.add("not("+elseExpression+")");
			}
		}
		}
		}
		
	
		
		
		
		if(!toOR.isEmpty())
		{	
			Set<String> hs = new HashSet<>();
			hs.addAll(selectOrs);
			selectOrs.clear();
			selectOrs.addAll(hs);
		String finalOCL = concatORS(toOR);
		res = new Invariant(overAllContext, finalOCL);
		}
		
		return res;
	}
	
	
	

	
	public List<Invariant> processAttributes() {
		List<Invariant> res = new BasicEList<Invariant>();
		LinkedList<Histogram> newContAttributes = verifAttributes();
	
		HashMap<Histogram, HashMap<String, Integer>> diffs = extractDiffAttributes(newContAttributes);
		
			for (Histogram histogram : newContAttributes) {

				 HashMap<String, Integer> tempChanges = diffs.get(histogram);
				 boolean hasChanged = verifHasChanged(tempChanges);
				 List<Invariant> temp = new BasicEList<Invariant>();
				 if(hasChanged)
				 {
					 temp= generateOCLBasedOnChangesAttributes(histogram, tempChanges);
				 }
				 res.addAll(temp);
			}
	
		return res;
	} 


	
	private LinkedList<Histogram> verifAttributes() {

		LinkedList<Histogram> res = new LinkedList<Histogram>();
		
		if(root!=null)
		{
		for (Histogram histogramAss : histAttributes) {
			
		OCLForJAVA.init("",root);
		Histogram container = histogramAss.getEmptyClone();
		EAttribute att = ((EAttribute)container.getElem());
		EClass c = (EClass) att.getEContainingClass();
		Collection<EObject> newRoots = new LinkedList<EObject>();
		EList<EObject> insideMe = new BasicEList<EObject>();
		extractEveryThingFromRoot(root, insideMe);
		for (EObject obj : insideMe) {	
			EClass myClass = ((EClass)obj.eClass());
			boolean pass = false;
			if(myClass.getName().equals(c.getName()))
			pass = true;
			else {
				 EList<EClass> fouch2 = myClass.getESuperTypes();
				 for (EClass eClass : fouch2) {
					if(eClass.getName().equals(c.getName()))
					pass=true;
				}
				 if(pass==false)
				 {
					 EList<EClass> fouch1 = c.getESuperTypes();
					 for (EClass eClass : fouch1) {
						if(eClass.getName().equals(c.getName()))
						pass=true;
					}
				 }
			}
			
		   
			if(pass)
			if(!newRoots.contains(obj))
			newRoots.add(obj);
		}
			for (int i = 0; i < container.values.size(); i++) {
				String bin = container.values.get(i);
				
				for (EObject root : newRoots) {
				OCLForJAVA.init("",root);
				String toBeEvaluated="0";
					  if(!bin.contains(".."))
					  toBeEvaluated = "self."+att.getName()+" = "+bin;
					  else 
					  {
						  try{
						  	String value=bin.trim().replace("[", "").replace("]", "").replace("..", "-");
							String [] temp=value.split("-");
							double start=-1;
							double end=-1;
							start= Double.valueOf(temp[0].trim());
							end= Double.valueOf(temp[1].trim());
						    toBeEvaluated = "self."+att.getName()+" >= "+start+" and self."+att.getName()+" <= "+end;
						  }
						  catch(Exception w){w.printStackTrace();}
						
					  }
						boolean belong = OCLForJAVA.evaluateBoolean(root, toBeEvaluated, "belong");
						if(belong)
						{
							container.probabilities.set(i, String.valueOf(Double.valueOf(container.probabilities.get(i))+1));
							container.frequencies.set(i, container.frequencies.get(i)+1);
						}
				
				}
			
			}
		res.add(container);
		}
		}
		
		for (int i = 0; i < res.size(); i++) {
			if(res.get(i).isEmpty())
			{res.remove(i);
			i--;
				
			}
		}
		return res;
	}
	
	
	public static void extractEveryThingFromRoot(EObject me, EList<EObject> res)
	{
		if(!res.contains(me))
		{
			res.add(me);
			EList<EObject> insideMe = me.eContents();
			for (EObject eObject : insideMe) {
				extractEveryThingFromRoot(eObject,res);
			}
		}
	}
	
	private List<Invariant> generateOCLBasedOnChangesClass(HistogramClass newHistogramClass, HashMap<EClass, Integer> tempChanges) {
		List<Invariant> res = new BasicEList<Invariant>();
		LinkedList<EClass> values = newHistogramClass.getValues();
		HashMap<HistogramClass, EClass> overs = new HashMap<HistogramClass, EClass>();
		HashMap<HistogramClass, EClass> unders = new HashMap<HistogramClass, EClass>();
		
		for (int i = 0; i < values.size(); i++) {
			EClass c = values.get(i);
			if(c!=null)
			{
			int change = tempChanges.get(c);
			if(change!=0)
			{
				if(change>0)
				{
					boolean isover = isValueOverRepresntedClass(newHistogramClass, change, c);
					if(isover)
					try {
						overs.put(newHistogramClass, c);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				if(change<0)
				{
					boolean isUnder = isValueUnderRepresntedClass(newHistogramClass, change, c );
					if(isUnder)
					try {
						unders.put(newHistogramClass, c);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			}
		}
		
		res = generateConstraintsClass(overs,unders);
		return res;
	}
	
	
	
	
	private List<Invariant> generateOCLBasedOnChangesAttributes(Histogram newHistogram, HashMap<String, Integer> tempChanges) {
		List<Invariant> res = new BasicEList<Invariant>();
		EList<String> values = newHistogram.values;
		HashMap<Histogram, String> overs = new HashMap<Histogram, String>();
		HashMap<Histogram, String> unders = new HashMap<Histogram, String>();
		
		for (int i = 0; i < values.size(); i++) {
			String c = values.get(i);
			if(c!=null)
			{
			int change = tempChanges.get(c);
			if(change!=0)
			{
				if(change>0)
				{
					boolean isover = isValueOverRepresntedGlob(newHistogram, change, c);
					if(isover)
					try {
						overs.put(newHistogram, c);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				if(change<0)
				{
					boolean isUnder = isValueUnderRepresntedGlob(newHistogram, change, c );
					if(isUnder)
					try {
						unders.put(newHistogram, c);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			}
		}
		
		res = generateConstraintsAttributes(overs,unders);
		return res;
	}
	
	
	private List<Invariant> generateConstraintsClass(HashMap<HistogramClass, EClass> overs,
			HashMap<HistogramClass, EClass> unders) {
		List<Invariant> res = new BasicEList<Invariant>();
		
		
		// generate for over 
		Iterator<Map.Entry<HistogramClass, EClass>> it = overs.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry<HistogramClass, EClass> pair = (Map.Entry<HistogramClass, EClass>)it.next();

	        HistogramClass tempHist = pair.getKey();
	 
	       
	        Class context=null;
	        String ocla ="not self.";
	        
	       if(tempHist.callingRef!=null)
	        context = (Class)ProfileConstraintExtractor.getCorrespondant(tempHist.callingRef.getEContainingClass(),ProfileConstraintExtractor.realClasses);
	       else context = (Class)ProfileConstraintExtractor.getCorrespondant(tempHist.root,ProfileConstraintExtractor.realClasses);
	   
	       if(tempHist.callingRef!=null)
	       {
	       if(tempHist.callingRef.getUpperBound()==1)
		        ocla=ocla+tempHist.callingRef.getName()+".oclIsKindOf("+pair.getValue().getName()+")";
	       else ocla=ocla+tempHist.callingRef.getName()+"->exists(oclIsKindOf("+pair.getValue().getName()+"))";
	       } else   ocla=ocla+"oclIsKindOf("+pair.getValue().getName()+")";
	       
	       
	       if(!tempHist.condition.equals("true")&&!tempHist.condition.equals(""))
	       {
	    	   context = (Class)ProfileConstraintExtractor.getCorrespondant(tempHist.context,ProfileConstraintExtractor.realClasses);
	    	   ocla = "if( "+tempHist.condition+" ) then "+ocla+" else false endif";
	       }
	       
	       Invariant inv = new Invariant(context,ocla);
	       res.add(inv);
	   
	    }
		
		
	    it = unders.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry<HistogramClass, EClass> pair = (Map.Entry<HistogramClass, EClass>)it.next();

	        HistogramClass tempHist = pair.getKey();
	 
	       
	        Class context=null;
	        String ocla ="self.";
	        
	       if(tempHist.callingRef!=null)
	        context = (Class)ProfileConstraintExtractor.getCorrespondant(tempHist.callingRef.getEContainingClass(),ProfileConstraintExtractor.realClasses);
	       else context = (Class)ProfileConstraintExtractor.getCorrespondant(tempHist.root,ProfileConstraintExtractor.realClasses);
	   
	       if(tempHist.callingRef!=null)
	       {
	       if(tempHist.callingRef.getUpperBound()==1)
		        ocla=ocla+tempHist.callingRef.getName()+".oclIsKindOf("+pair.getValue().getName()+")";
	       else ocla=ocla+tempHist.callingRef.getName()+"->exists(oclIsKindOf("+pair.getValue().getName()+"))";
	       } else   ocla=ocla+"oclIsKindOf("+pair.getValue().getName()+")";
	       
	       
	       
	       Invariant inv = new Invariant(context,ocla);
	       res.add(inv);
	   
	    }
	    
		return res;
	}
	
	
	
	
	
	private List<Invariant> generateConstraintsAttributes(HashMap<Histogram, String> overs,
			HashMap<Histogram, String> unders) {
		List<Invariant> res = new BasicEList<Invariant>();
	
		// generate for over 
		Iterator<Map.Entry<Histogram, String>> it = overs.entrySet().iterator();
	    while (it.hasNext()) {
	    	Map.Entry<Histogram, String> pair = (Map.Entry<Histogram, String>)it.next();

	        Histogram tempHist = pair.getKey();
	 
	       
	        Class context=null;
	        String ocla ="not self.";
	        context = (Class)ProfileConstraintExtractor.getCorrespondant(tempHist.context,ProfileConstraintExtractor.realClasses);

	       if(((EAttribute)tempHist.elem).getUpperBound()==1)
		        ocla=ocla+tempHist.elem.getName()+" = "+pair.getValue();
	       else ocla=ocla+tempHist.elem.getName()+"->exists("+pair.getValue()+")";
	   
	       
	       if(!tempHist.condition.equals("true")&&!tempHist.condition.equals(""))
	       {
	    	   context = (Class)ProfileConstraintExtractor.getCorrespondant(tempHist.context,ProfileConstraintExtractor.realClasses);
	    	   ocla = "if( "+tempHist.condition+" ) then "+ocla+" else false endif";
	       }
	       
	       Invariant inv = new Invariant(context,ocla);
	       res.add(inv);
	   
	    }
		
		
	    it = overs.entrySet().iterator();
	    while (it.hasNext()) {
	    	Map.Entry<Histogram, String> pair = (Map.Entry<Histogram, String>)it.next();

	        Histogram tempHist = pair.getKey();
	 
	       
	        Class context=null;
	        String ocla ="self.";
	        context = (Class)ProfileConstraintExtractor.getCorrespondant(tempHist.context,ProfileConstraintExtractor.realClasses);

	       if(((EAttribute)tempHist.elem).getUpperBound()==1)
		        ocla=ocla+tempHist.elem.getName()+" = "+pair.getValue();
	       else ocla=ocla+tempHist.elem.getName()+"->exists("+pair.getValue()+")";
	   
	       
	       if(!tempHist.condition.equals("true")&&!tempHist.condition.equals(""))
	       {
	    	   context = (Class)ProfileConstraintExtractor.getCorrespondant(tempHist.context,ProfileConstraintExtractor.realClasses);
	    	   ocla = "if( "+tempHist.condition+" ) then "+ocla+" else false endif";
	       }
	       
	       Invariant inv = new Invariant(context,ocla);
	       res.add(inv);
	    }
	    
		return res;
	}
	
	
	
	private boolean isValueOverRepresntedClass(HistogramClass newHistogramClass, int change, EClass c) {
		
		HistogramClass desiredHist = (HistogramClass)getCorrespendant(newHistogramClass, ProfileConstraintExtractor.hitogramsForClasses);
		HistogramClass realHist = (HistogramClass)getCorrespendant(newHistogramClass, histClasses);
		if(desiredHist!=null&&realHist!=null)
		{
		
			double desiredProaba = desiredHist.getProbabilities().get(desiredHist.getIndexValue(c));
			double realProba = realHist.getProbabilities().get(realHist.getIndexValue(c));
			
			if(desiredProaba<realProba)
			return true;
		}
		
		return false;
	}
	
	private boolean isValueUnderRepresntedClass(HistogramClass newHistogramClass, int change, EClass c) {
		
		HistogramClass desiredHist = (HistogramClass)getCorrespendant(newHistogramClass, ProfileConstraintExtractor.hitogramsForClasses);
		HistogramClass realHist = (HistogramClass)getCorrespendant(newHistogramClass, histClasses);
		if(desiredHist!=null&&realHist!=null)
		{
		
			double desiredProaba = desiredHist.getProbabilities().get(desiredHist.getIndexValue(c));
			double realProba = realHist.getProbabilities().get(realHist.getIndexValue(c));
			
			if(desiredProaba>realProba)
			return true;
		}
		
		return false;
	}
	
	private boolean isValueOverRepresntedGlob(Histogram newHistogram, int change, String c) {
		
		Histogram desiredHist = (Histogram)getCorrespendant(newHistogram, ProfileConstraintExtractor.hitogramsForAttributes);
		Histogram realHist = (Histogram)getCorrespendant(newHistogram, histAttributes);
		if(desiredHist!=null&&realHist!=null)
		{
		

			double desiredProaba = 0;
			double realProba = 0;
			
			try{
			 desiredProaba = Double.valueOf(desiredHist.getProbabilities().get(desiredHist.getIndexValue(c))).doubleValue();
			 realProba = Double.valueOf(realHist.getProbabilities().get(realHist.getIndexValue(c))).doubleValue();
			}catch (Exception e){e.printStackTrace();
			 desiredProaba = 0;
			 realProba = 0;
			}
			
			
			if(desiredProaba<realProba)
			return true;
		}
		
		return false;
	}
	
	private boolean isValueUnderRepresntedGlob(Histogram newHistogram, int change, String c) {
			
		Histogram desiredHist = (Histogram)getCorrespendant(newHistogram, ProfileConstraintExtractor.hitogramsForAttributes);
		Histogram realHist = (Histogram)getCorrespendant(newHistogram, histAttributes);
		if(desiredHist!=null&&realHist!=null)
		{
		

			double desiredProaba = 0;
			double realProba = 0;
			
			try{
			 desiredProaba = Double.valueOf(desiredHist.getProbabilities().get(desiredHist.getIndexValue(c))).doubleValue();
			 realProba = Double.valueOf(realHist.getProbabilities().get(realHist.getIndexValue(c))).doubleValue();
			}catch (Exception e){e.printStackTrace();
			 desiredProaba = 0;
			 realProba = 0;
			}
			
			
			if(desiredProaba>realProba)
			return true;
		}
		
		return false;
	}
	
	
	
	
	private boolean verifHasChanged(HashMap<?, Integer> mp) {
		 Iterator<?> it = mp.entrySet().iterator();
		    while (it.hasNext()) {
		        @SuppressWarnings("unchecked")
				Map.Entry<EClass, Integer> pair = (Map.Entry<EClass, Integer>)it.next();
		       if(pair.getValue()!=0)
		    	  return true;
	
		    }
		return false;
	}
	

	private Object getCorrespendant(Object newHistI, LinkedList<?> myclass2) {
	
		for (Object histogramClass : myclass2) {
			if(newHistI instanceof Histogram && histogramClass instanceof Histogram)
			if(((Histogram)newHistI).equals((Histogram)histogramClass))
			return histogramClass;
			
			if(newHistI instanceof HistogramClass && histogramClass instanceof HistogramClass)
				if(((HistogramClass)newHistI).equals((HistogramClass)histogramClass))
				return histogramClass;
		}
		return null;
	}
	private LinkedList<HistogramClass> verifClasses() {
		LinkedList<HistogramClass> res = new LinkedList<HistogramClass>();
		
		EObject me = root ;
		if(me!=null)
		{
		for (HistogramClass histogramClass : histClasses) {
		HistogramClass container = histogramClass.getEmptyClone();
		if(histogramClass.callingRef==null&&histogramClass.root.getName().equals("Tax_Case"))
		{

			for (int i = 0; i < container.values.size(); i++) {
			EClass c = container.values.get(i);
			String toBeEvaluated = "self.oclIsKindOf("+c.getName()+")";
			OCLForJAVA.init("",me);
			try {
			boolean belong = OCLForJAVA.evaluateBoolean(me, toBeEvaluated, "temp");
			if(belong)
			{
				container.probabilities.set(i, container.probabilities.get(i)+1);
				container.frequencies.set(i, container.frequencies.get(i)+1);

			}
			} catch (Exception e) {e.printStackTrace();}
			}
		
		}
		else 
		{
			OCLForJAVA.init("",me);
			for (int i = 0; i < container.values.size(); i++) {
				if(container.values.size()>1)
				{
					
				
				EClass c = container.values.get(i);
				if(container.callingRef!=null)
				 c=container.callingRef.getEContainingClass();
				if(!container.condition.trim().equalsIgnoreCase("true"))
				c=container.callingRef.getEContainingClass();
			
				
				LinkedList<EObject> newRoots = new LinkedList<EObject>();
				EList<EObject> insideMe = new BasicEList<EObject>();
				DistCalculator.extractEveryThingFromRoot(me, insideMe);
				for (EObject obj : insideMe) {	
					EClass myClass = ((EClass)obj.eClass());
					boolean pass = false;
					if(myClass.getName().equals(c.getName()))
					pass = true;
					else {
						 EList<EClass> fouch2 = myClass.getESuperTypes();
						 for (EClass eClass : fouch2) {
							if(eClass.getName().equals(c.getName()))
							pass=true;
						}
						 if(pass==false)
						 {
							 EList<EClass> fouch1 = c.getESuperTypes();
							 for (EClass eClass : fouch1) {
								if(eClass.getName().equals(c.getName()))
								pass=true;
							}
						 }
					}
					
				   
					if(pass)
					if(!newRoots.contains(obj))
					newRoots.add(obj);
				}
				
				c = container.values.get(i);
				
					if(!container.condition.trim().equalsIgnoreCase("true"))
						for (int j = 0; j < newRoots.size(); j++) {
							EObject eObject = newRoots.get(j);
							OCLForJAVA.init("",eObject);
							boolean test = OCLForJAVA.evaluateBoolean(eObject, container.condition.replace("self.", ""), "test");
							if(test==false)
							{
								newRoots.remove(j);
								j--;
							}
						}
					
					for (EObject root : newRoots) {
						String toBeEvaluated="0";
					
				    	 if(container.callingRef!=null)
				    	toBeEvaluated = "self."+container.callingRef.getName()+".oclAsSet()->iterate(inc;  acc:Bag("+container.callingRef.getEType().getName()+")=Bag{}  |"
				    			+ " if(inc.oclIsKindOf("+c.getName()+")) then acc->including(inc) else acc endif)->size()";
				    	 else toBeEvaluated = "self.oclIsKindOf("+c.getName()+")";

						OCLForJAVA.init("",root);
						
						 if(container.callingRef==null)
						 {
						try {
						boolean belong = OCLForJAVA.evaluateBoolean(root, toBeEvaluated, "temp");
						if(belong)
						{
							container.probabilities.set(i, container.probabilities.get(i)+1);
							container.frequencies.set(i, container.frequencies.get(i)+1);
						}
						} catch (Exception e) {e.printStackTrace();} 
						}
						 else {
								try {
							 int count = OCLForJAVA.evaluateIntOut(root, toBeEvaluated, "count");
							 container.probabilities.set(i, container.probabilities.get(i)+count);
							 container.frequencies.set(i, container.frequencies.get(i)+count);
								} catch (Exception e) {e.printStackTrace();} 
						 }
						}
				    
				
				}
			
			}
			
		}
	
res.add(container);
	 
		
		
		
		}
	}
		
		return res;
	}
	public void updatePopDistForChange(EObject initalEObject, LinkedList<EObject> roots) {

     	DistCalculator plotter = new DistCalculator(roots, ProfileConstraintExtractor.hitogramsForClasses, ProfileConstraintExtractor.hitogramsForAttributes, ProfileConstraintExtractor.hitogramsForAssociations);

     	{
     	LinkedList<HistogramClass> oldContClasses = plotter.getMyBinsFromClass(initalEObject);
		HashMap<HistogramClass, HashMap<EClass, Integer>> diffs = extractDiffClasses(oldContClasses);
		for (HistogramClass histogramClass : myclass) {
			HistogramClass popHist = (HistogramClass) getCorrespendant(histogramClass, histClasses);
			if(popHist!=null)
			{
			HashMap<EClass, Integer> changes = diffs.get(histogramClass);
			
			for (int i = 0; i <  popHist.getValues().size(); i++) {
				EClass c  = popHist.getValues().get(i);
				if(c!=null)
				try{
				int move = changes.get(c);
				//System.out.println(move);
				popHist.frequencies.set(i, popHist.frequencies.get(i)+ (move*-1));
				}catch(Exception e ){e.printStackTrace();}
			}
			
			popHist.CalculateProbaFromFrequencies();
			}
			}
		
     	}
		
     	{
		LinkedList<Histogram> oldContAtt = plotter.getMyBinsFromAtt(initalEObject);
		HashMap<Histogram, HashMap<String, Integer>> diffs = extractDiffAttributes(oldContAtt);
		for (Histogram histogramAtt : myAtt) {
			Histogram popHist = (Histogram) getCorrespendant(histogramAtt, histAttributes);
			if(popHist!=null)
			{
			HashMap<String, Integer> changes = diffs.get(histogramAtt);
			
			for (int i = 0; i <  popHist.getValues().size(); i++) {
				String ch  = popHist.getValues().get(i);
				if(ch!=null)
				try{
				int move = changes.get(ch);
				//System.out.println(move);
				popHist.frequencies.set(i, popHist.frequencies.get(i)+(move*-1));
				}catch(Exception e ){e.printStackTrace();}
			}
			
			popHist.CalculateProbaFromFrequencies();
			}
			}
	}
		
     	
       	{
    		LinkedList<Histogram> oldContAss = plotter.getMyBinsFromAssoc(initalEObject);
    		HashMap<Histogram, HashMap<String, Integer>> diffs = extractDiffAssociations(oldContAss);
    		for (Histogram histogramAss : myAssoc) {
    			Histogram popHist = (Histogram) getCorrespendant(histogramAss, histAssoc);
    			if(popHist!=null)
    			{
    			HashMap<String, Integer> changes = diffs.get(histogramAss);
    			
    			for (int i = 0; i <  popHist.getValues().size(); i++) {
    				String ch  = popHist.getValues().get(i);
    				if(ch!=null)
    				try{
    				int move = changes.get(ch);
    				//System.out.println(move);
    				popHist.frequencies.set(i, popHist.frequencies.get(i)+(move*-1));
    				}catch(Exception e ){e.printStackTrace();}
    			}
    			
    			popHist.CalculateProbaFromFrequencies();
    			}
    			}
    	}
       	
		}
	
	
	public void updatePopDistToDrop() {

		for (HistogramClass histogramClass : myclass) {
			HistogramClass popHist = (HistogramClass) getCorrespendant(histogramClass, histClasses);
			if(popHist!=null)
			{
			
			for (int i = 0; i <  popHist.getValues().size(); i++) {
				EClass c  = popHist.getValues().get(i);
				if(c!=null)
				try{
				int move = (-1)*histogramClass.frequencies.get(i);
				popHist.frequencies.set(i, popHist.frequencies.get(i)+move);
				}catch(Exception e ){e.printStackTrace();}
			}
			
			popHist.CalculateProbaFromFrequencies();
			}
			}
		
		
		
		for (Histogram histogramAss : myAssoc) {
			Histogram popHist = (Histogram) getCorrespendant(histogramAss, histAssoc);
			if(popHist!=null)
			{
			
			for (int i = 0; i <  popHist.getValues().size(); i++) {
				String ch  = popHist.getValues().get(i);
				if(ch!=null)
				try{
				int move = (-1)*histogramAss.frequencies.get(i);
				popHist.frequencies.set(i, popHist.frequencies.get(i)+move);
				}catch(Exception e ){e.printStackTrace();}
			}
			
			popHist.CalculateProbaFromFrequencies();
			}
			}
		
		for (Histogram histogramAss : myAtt) {
			Histogram popHist = (Histogram) getCorrespendant(histogramAss, histAttributes);
			if(popHist!=null)
			{
			
			for (int i = 0; i <  popHist.getValues().size(); i++) {
				String ch  = popHist.getValues().get(i);
				if(ch!=null)
				try{
				int move = (-1)*histogramAss.frequencies.get(i);
				popHist.frequencies.set(i, popHist.frequencies.get(i)+move);
				}catch(Exception e ){e.printStackTrace();}
			}
			
			popHist.CalculateProbaFromFrequencies();
			}
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
	
}
