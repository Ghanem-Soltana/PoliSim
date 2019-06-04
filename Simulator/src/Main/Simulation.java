package Main;
import java.util.Collection;

import org.eclipse.emf.ecore.EObject;

import util.OCLForJAVA;
import util.Container;
@SuppressWarnings("unused")
public class Simulation {

	// Beginning of FD simulation code
	public static void FD (EObject input, String ADName)
	{
	OCLForJAVA.init(ADName,input);
	String tempOCL="";
	tempOCL= "self.from_agent.taxation_year";
	int taxation_year = OCLForJAVA.evaluateInt(input,tempOCL,"taxation_year"); 
	/* TRACEABILITY: Specifies when the request was postmarked. - */ 									
	tempOCL= "self.incomes->select(inc:Income|inc.year=taxation_year and inc.tax_card->notEmpty() and inc.tax_card.tax_card_type=Tax_Card_Type::Principal)->union(self.incomes->select(inc:Income|inc.year=taxation_year and inc.tax_card->notEmpty() and inc.tax_card.tax_card_type=Tax_Card_Type::Additional)->sortedBy(income_amount*-1))->asOrderedSet()";
	Collection<EObject> incomes = OCLForJAVA.evaluateECollection(input,tempOCL,"incomes","Income","OrderedSet"); 	 
	/** Description: The incomes of a given taxpayer for the studied tax year - The first income is the principal one. - The rest of incomes are sorted from the highest to the lowest - Note that taxpayers who are taxed jointly have only one principal income. - **/	
	for (EObject income1: incomes){
	OCLForJAVA.newIteration(new Container("income1",income1,"incomes",incomes));
	tempOCL= "income1.income_type.subjectToWithholdingTax";
	boolean is_income_subject_to_withholding = OCLForJAVA.evaluateBoolean(input,tempOCL,"is_income_subject_to_withholding");
	/** Description: Returns yes if the income is under withholding taxation. - **/							
	if ( (is_income_subject_to_withholding) == true){
	   tempOCL= "income1.income_type.oclIsKindOf(Employment_Income)";
	   boolean is_income_eligible = OCLForJAVA.evaluateBoolean(input,tempOCL,"is_income_eligible");
	   /** Description: Checks the type of the treated income.  - This input returns yes if the type is Employment; -  **/							
	   if ( (is_income_eligible) == false){
	   double calculated_FD = 0; 	
	        double flat_maximum_FD = 2574;
	        /** TRACEABILITY: Source: Art. 105bis of the Luxembourg's Income Tax Law, 2013  - maximum_distance_in_units = 30  - minimum_distance_in_units = 4 - flat_maximum_FD= 2574  - flat_rate_per_unit = 99 - **/ 								
	        tempOCL= "incomes->excluding(income1)->select(tax_card->notEmpty()).tax_card.deduction_FD_yearly->sum()";
	        double granted_FD_for_other_incomes = OCLForJAVA.evaluateDouble(input,tempOCL,"granted_FD_for_other_incomes");
	        /** Description: Returns the sum of granted FD deduction granted to -  all incomes of a given taxpayer.  -  **/							
	        if ((calculated_FD >= (flat_maximum_FD - granted_FD_for_other_incomes)) == true){
	        double expected_FD = flat_maximum_FD - (granted_FD_for_other_incomes); 	
	        OCLForJAVA.updateEFeature(input,"income1.tax_card.deduction_FD_yearly",expected_FD);
	        }else{
	          if ((calculated_FD >= (flat_maximum_FD - granted_FD_for_other_incomes)) == false){
	          double expected_FD = calculated_FD; 	
	          OCLForJAVA.updateEFeature(input,"income1.tax_card.deduction_FD_yearly",expected_FD);
	          }else
	          {System.err.println("Unhandeled situation"); return;
	          }
	        }
	   }else{
	     if ((is_income_eligible) == true) {
	     tempOCL= "0";
	     double distance_reference = OCLForJAVA.evaluateDouble(input,tempOCL,"distance_reference");
	     tempOCL= "income1.details->sortedBy(month)->asOrderedSet()";
	     Collection<EObject> details = OCLForJAVA.evaluateECollection(input,tempOCL,"details","Income_Detail","OrderedSet");  	
	     /** Description: The collection of distances between a - taxpayer  s work and home addresses for - each month (from 1 to 12). A distance is - specified by three attributes: the value of the - distance, the month for which the value - of the distance was specified, and the full time - equivalent value (between 0 and 1). - Source: Ministerial - Regulation of February 6, 2012 -  **/	
	     double acc2 = 0;
	     for (EObject detail: details){
	     OCLForJAVA.newIteration(new Container("detail",detail,"details",details));
	     tempOCL= "detail.is_worked";
	     boolean has_the_taxpayer_worked_for_the_treated_month = OCLForJAVA.evaluateBoolean(input,tempOCL,"has_the_taxpayer_worked_for_the_treated_month");
	     /** Description: Returns yes if the taxpayer has worked -  during the month related to the treated -  distance. For instance, if we are treating -  the second distance, then we check if -  the taxpayer was employed in February.  -  **/							
	     if ( (has_the_taxpayer_worked_for_the_treated_month) == true){
	        tempOCL= "detail.distance";
	        double distance_value = OCLForJAVA.evaluateDouble(input,tempOCL,"distance_value");
	        /** Description: Home-to-work distance. -  **/							
	        if ( (distance_value>distance_reference) == true){
	        distance_reference=distance_value;
	        }else{
	          if ((distance_value>distance_reference) == false) {
	               double minimum_distance_in_units = 4;
	               /** TRACEABILITY: Source: Art. 105bis of the Luxembourg's Income Tax Law, 2013  - maximum_distance_in_units = 30  - minimum_distance_in_units = 4 - flat_maximum_FD= 2574  - flat_rate_per_unit = 99 - **/ 								
	               if ( (distance_reference> minimum_distance_in_units) == false){
	               acc2=acc2+0;
	               }else{
	                  if ((distance_reference> minimum_distance_in_units) == true) {
	                         double maximum_distance_in_units = 30;
	                         /** TRACEABILITY: Source: Art. 105bis of the Luxembourg's Income Tax Law, 2013  - maximum_distance_in_units = 30  - minimum_distance_in_units = 4 - flat_maximum_FD= 2574  - flat_rate_per_unit = 99 - **/ 								
	                         if ( (distance_reference > maximum_distance_in_units) == true){
	                         double flat_rate_per_unit = 99;
	                         /** TRACEABILITY: Source: Art. 105bis of the Luxembourg's Income Tax Law, 2013  - maximum_distance_in_units = 30  - minimum_distance_in_units = 4 - flat_maximum_FD= 2574  - flat_rate_per_unit = 99 - **/ 								
	                         tempOCL= "income1.prorata_period()";
	                         double full_time_equivalent = OCLForJAVA.evaluateDouble(input,tempOCL,"full_time_equivalent");
	                         /** Description: The periord for which the taxpayer - is actually working in for a particular -  month (between 0 and 1). -  **/							
	                         acc2=acc2+ flat_rate_per_unit * maximum_distance_in_units * full_time_equivalent;
	                         }else{
	                            if ((distance_reference > maximum_distance_in_units) == false) {
	                            double flat_rate_per_unit = 99;
	                            /** TRACEABILITY: Source: Art. 105bis of the Luxembourg's Income Tax Law, 2013  - maximum_distance_in_units = 30  - minimum_distance_in_units = 4 - flat_maximum_FD= 2574  - flat_rate_per_unit = 99 - **/ 								
	                            tempOCL= "income1.prorata_period()";
	                            double full_time_equivalent = OCLForJAVA.evaluateDouble(input,tempOCL,"full_time_equivalent");
	                            /** Description: The periord for which the taxpayer - is actually working in for a particular -  month (between 0 and 1). -  **/							
	                            acc2=acc2+flat_rate_per_unit * (distance_reference-minimum_distance_in_units) * full_time_equivalent;
	                            }else
	                            {acc2=acc2+0;
	                            }
	                         }
	                  }else
	                  {acc2=acc2+0;
	                  }
	               }
	          }else
	          {acc2=acc2+0;
	          }
	        }
	     }else{
	        if ((has_the_taxpayer_worked_for_the_treated_month) == false) {
	        acc2=acc2+0;
	        }else
	        {acc2=acc2+0;
	        }
	     }
	     OCLForJAVA.iterationExit();
	     }
	     double calculated_FD = acc2; 	
	          double flat_maximum_FD = 2574;
	          /** TRACEABILITY: Source: Art. 105bis of the Luxembourg's Income Tax Law, 2013  - maximum_distance_in_units = 30  - minimum_distance_in_units = 4 - flat_maximum_FD= 2574  - flat_rate_per_unit = 99 - **/ 								
	          tempOCL= "incomes->excluding(income1)->select(tax_card->notEmpty()).tax_card.deduction_FD_yearly->sum()";
	          double granted_FD_for_other_incomes = OCLForJAVA.evaluateDouble(input,tempOCL,"granted_FD_for_other_incomes");
	          /** Description: Returns the sum of granted FD deduction granted to -  all incomes of a given taxpayer.  -  **/							
	          if ((calculated_FD >= (flat_maximum_FD - granted_FD_for_other_incomes)) == true){
	          double expected_FD = flat_maximum_FD - (granted_FD_for_other_incomes); 	
	          OCLForJAVA.updateEFeature(input,"income1.tax_card.deduction_FD_yearly",expected_FD);
	          }else{
	             if ((calculated_FD >= (flat_maximum_FD - granted_FD_for_other_incomes)) == false){
	             double expected_FD = calculated_FD; 	
	             OCLForJAVA.updateEFeature(input,"income1.tax_card.deduction_FD_yearly",expected_FD);
	             }else
	             {System.err.println("Unhandeled situation"); return;
	             }
	          }
	     }else
	     {System.err.println("Unhandeled situation"); return;
	     }
	   }
	}else{
	   if ((is_income_subject_to_withholding) == false) {
	   double calculated_FD = 0; 	
	     double flat_maximum_FD = 2574;
	     /** TRACEABILITY: Source: Art. 105bis of the Luxembourg's Income Tax Law, 2013  - maximum_distance_in_units = 30  - minimum_distance_in_units = 4 - flat_maximum_FD= 2574  - flat_rate_per_unit = 99 - **/ 								
	     tempOCL= "incomes->excluding(income1)->select(tax_card->notEmpty()).tax_card.deduction_FD_yearly->sum()";
	     double granted_FD_for_other_incomes = OCLForJAVA.evaluateDouble(input,tempOCL,"granted_FD_for_other_incomes");
	     /** Description: Returns the sum of granted FD deduction granted to -  all incomes of a given taxpayer.  -  **/							
	     if ((calculated_FD >= (flat_maximum_FD - granted_FD_for_other_incomes)) == true){
	     double expected_FD = flat_maximum_FD - (granted_FD_for_other_incomes); 	
	     OCLForJAVA.updateEFeature(input,"income1.tax_card.deduction_FD_yearly",expected_FD);
	     }else{
	        if ((calculated_FD >= (flat_maximum_FD - granted_FD_for_other_incomes)) == false){
	        double expected_FD = calculated_FD; 	
	        OCLForJAVA.updateEFeature(input,"income1.tax_card.deduction_FD_yearly",expected_FD);
	        }else
	        {System.err.println("Unhandeled situation"); return;
	        }
	     }
	   }else
	   {System.err.println("Unhandeled situation"); return;
	   }
	}
	OCLForJAVA.iterationExit();
	}
	} 
	// End of FD simulation code

	// Beginning of FD_Modified simulation code
	public static void FD_Modified (EObject input, String ADName)
	{
	OCLForJAVA.init(ADName,input);
	String tempOCL="";
	tempOCL= "self.from_agent.taxation_year";
	int taxation_year = OCLForJAVA.evaluateInt(input,tempOCL,"taxation_year"); 
	/** TRACEABILITY: Specifies when the request was postmarked. - **/ 									
	tempOCL= "self.incomes->select(inc:Income|inc.year=taxation_year and inc.tax_card->notEmpty() and inc.tax_card.tax_card_type=Tax_Card_Type::Principal)->union(self.incomes->select(inc:Income|inc.year=taxation_year and inc.tax_card->notEmpty() and inc.tax_card.tax_card_type=Tax_Card_Type::Additional)->sortedBy(income_amount*-1))->asOrderedSet()";
	Collection<EObject> incomes = OCLForJAVA.evaluateECollection(input,tempOCL,"incomes","Income","OrderedSet"); 	 
	/** Description: The incomes of a given taxpayer for the studied tax year - The first income is the principal one. - The rest of incomes are sorted from the highest to the lowest - Note that taxpayers who are taxed jointly have only one principal income. - **/	
	for (EObject income1: incomes){
	OCLForJAVA.newIteration(new Container("income1",income1,"incomes",incomes));
	tempOCL= "income1.income_type.subjectToWithholdingTax";
	boolean is_income_subject_to_withholding = OCLForJAVA.evaluateBoolean(input,tempOCL,"is_income_subject_to_withholding");
	/** Description: Returns yes if the income is under withholding taxation. - **/							
	if ( (is_income_subject_to_withholding) == true){
	   tempOCL= "income1.income_type.oclIsKindOf(Employment_Income)";
	   boolean is_income_eligible = OCLForJAVA.evaluateBoolean(input,tempOCL,"is_income_eligible");
	   /** Description: Checks the type of the treated income.  - This input returns yes if the type is Employment; -  **/							
	   if ( (is_income_eligible) == false){
	   double calculated_FD = 0; 	
	   double expected_FD = calculated_FD; 	
	   OCLForJAVA.updateEFeature(input,"income1.tax_card.deduction_FD_yearly",expected_FD);
	   }else{
	     if ((is_income_eligible) == true) {
	     tempOCL= "0";
	     double distance_reference = OCLForJAVA.evaluateDouble(input,tempOCL,"distance_reference");
	     tempOCL= "income1.details->sortedBy(month)->asOrderedSet()";
	     Collection<EObject> details = OCLForJAVA.evaluateECollection(input,tempOCL,"details","Income_Detail","OrderedSet");  	
	     /** Description: The collection of distances between a - taxpayer  s work and home addresses for - each month (from 1 to 12). A distance is - specified by three attributes: the value of the - distance, the month for which the value - of the distance was specified, and the full time - equivalent value (between 0 and 1). - Source: Ministerial - Regulation of February 6, 2012 -  **/	
	     double acc2 = 0;
	     for (EObject detail: details){
	     OCLForJAVA.newIteration(new Container("detail",detail,"details",details));
	     tempOCL= "detail.is_worked";
	     boolean has_the_taxpayer_worked_for_the_treated_month = OCLForJAVA.evaluateBoolean(input,tempOCL,"has_the_taxpayer_worked_for_the_treated_month");
	     /** Description: Returns yes if the taxpayer has worked -  during the month related to the treated -  distance. For instance, if we are treating -  the second distance, then we check if -  the taxpayer was employed in February.  -  **/							
	     if ( (has_the_taxpayer_worked_for_the_treated_month) == true){
	        tempOCL= "detail.distance";
	        double distance_value = OCLForJAVA.evaluateDouble(input,tempOCL,"distance_value");
	        /** Description: Home-to-work distance. -  **/							
	        if ( (distance_value>distance_reference) == true){
	        distance_reference=distance_value;
	        }else{
	          if ((distance_value>distance_reference) == false) {
	               double minimum_distance_in_units = 10;
	               /** TRACEABILITY: Source: Art. 105bis of the Luxembourg's Income Tax Law, 2013  - minimum_distance_in_units = 10 - flat_rate_per_unit = 50 - **/ 								
	               if ( (distance_reference> minimum_distance_in_units) == false){
	               acc2=acc2+0;
	               }else{
	                  if ((distance_reference> minimum_distance_in_units) == true) {
	                  double flat_rate_per_unit = 50;
	                  /** TRACEABILITY: Source: Art. 105bis of the Luxembourg's Income Tax Law, 2013  - minimum_distance_in_units = 10 - flat_rate_per_unit = 50 - **/ 								
	                  tempOCL= "income1.prorata_period()";
	                  double full_time_equivalent = OCLForJAVA.evaluateDouble(input,tempOCL,"full_time_equivalent");
	                  /** Description: The periord for which the taxpayer - is actually working in for a particular -  month (between 0 and 1). -  **/							
	                  acc2=acc2+flat_rate_per_unit * (distance_reference-minimum_distance_in_units) * full_time_equivalent;
	                  }else
	                  {acc2=acc2+0;
	                  }
	               }
	          }else
	          {acc2=acc2+0;
	          }
	        }
	     }else{
	        if ((has_the_taxpayer_worked_for_the_treated_month) == false) {
	        acc2=acc2+0;
	        }else
	        {acc2=acc2+0;
	        }
	     }
	     OCLForJAVA.iterationExit();
	     }
	     double calculated_FD = acc2; 	
	     double expected_FD = calculated_FD; 	
	     OCLForJAVA.updateEFeature(input,"income1.tax_card.deduction_FD_yearly",expected_FD);
	     }else
	     {System.err.println("Unhandeled situation"); return;
	     }
	   }
	}else{
	   if ((is_income_subject_to_withholding) == false) {
	   double calculated_FD = 0; 	
	   double expected_FD = calculated_FD; 	
	   OCLForJAVA.updateEFeature(input,"income1.tax_card.deduction_FD_yearly",expected_FD);
	   }else
	   {System.err.println("Unhandeled situation"); return;
	   }
	}
	OCLForJAVA.iterationExit();
	}
	} 
	// End of FD_Modified simulation code

	
	// Beginning of CIS simulation code
	public static void CIS (EObject input, String ADName)
	{
	OCLForJAVA.init(ADName,input);
	String tempOCL="";
		tempOCL= "self.from_agent.taxation_year";
	int taxation_year = OCLForJAVA.evaluateInt(input,tempOCL,"taxation_year"); 
	
	
	/** TRACEABILITY: Specifies when the request was postmarked. - **/ 									
	if ( (taxation_year>=2009) == true){
	tempOCL= "self.incomes->select(inc:Income|inc.year=taxation_year and inc.tax_card->notEmpty() and inc.tax_card.tax_card_type=Tax_Card_Type::Principal)->union(self.incomes->select(inc:Income|inc.year=taxation_year and inc.tax_card->notEmpty() and inc.tax_card.tax_card_type=Tax_Card_Type::Additional)->sortedBy(income_amount*-1))->asOrderedSet()";
	Collection<EObject> incomes = OCLForJAVA.evaluateECollection(input,tempOCL,"incomes","Income","OrderedSet"); 	 
	/** Description: The incomes of a given taxpayer for the studied tax year. The first income is the principal one. - The rest of incomes are sorted from the highest to the lowest income. - Note that taxpayers who are taxed jointly have only one principal income. - **/	
	for (EObject income: incomes){
	OCLForJAVA.newIteration(new Container("income",income,"incomes",incomes));
	tempOCL= "income.income_type.subjectToWithholdingTax";
	boolean is_income_subject_to_withholding = OCLForJAVA.evaluateBoolean(input,tempOCL,"is_income_subject_to_withholding");
	/** Description: Returns yes if the income is under withholding taxation: false otherwise - **/							
	if ( (is_income_subject_to_withholding) == false){
	double expected_CIS = 0; 	
	OCLForJAVA.updateEFeature(input,"income.tax_card.credit_CIS_yearly",expected_CIS);
	}else{
	   if ((is_income_subject_to_withholding) == true) {
	     tempOCL= "income.income_type.oclIsKindOf(Employment_Income) ";
	     boolean is_income_eligible = OCLForJAVA.evaluateBoolean(input,tempOCL,"is_income_eligible");
	     /** Description: Returns yes if the income is of type Employment; returns false otherwise. - To be eligible an income has to meet the condition cited in: http://www.impotsdirects.public.lu/az/r/reven_net_salar/index.html -  **/							
	     if ( (is_income_eligible) == false){
	     double expected_CIS = 0; 	
	     OCLForJAVA.updateEFeature(input,"income.tax_card.credit_CIS_yearly",expected_CIS);
	     }else{
	        if ((is_income_eligible) == true) {
	             tempOCL= "incomes->excluding(income)->select(i:Income | i.tax_card.credit_CIS_monthly <> 0 or i.tax_card.credit_CIS_yearly <> 0)->notEmpty()";
	             boolean CIS_already_attributed = OCLForJAVA.evaluateBoolean(input,tempOCL,"CIS_already_attributed");
	             /** Description: Makes sure that CIS was not attributed to the taxpayer on any other income.  - It returns yes if the taxpayer has already benefited from CIS. -  **/							
	             if ( (CIS_already_attributed) == true){
	             double expected_CIS = 0; 	
	             OCLForJAVA.updateEFeature(input,"income.tax_card.credit_CIS_yearly",expected_CIS);
	             }else{
	               if ((CIS_already_attributed) == false) {
	                       double CIS_minimum_income_day = 3.12;
	                       /** TRACEABILITY: Source: Reglement Grand Ducal du 19 Decembre 2008 - CIS_minimum_income_year = 936 - CIS_minimum_income_month= 78 - CIS_minimum_income_day = 3,12 - **/ 								
	                       tempOCL= "income.income_per_day()";
	                       double income_per_day = OCLForJAVA.evaluateDouble(input,tempOCL,"income_per_day");
	                       /** Description: The value of the taxpayer's income per day -  **/							
	                       if ( (income_per_day > CIS_minimum_income_day) == false){
	                                 double CIS_minimum_income_month = 78;
	                                 /** TRACEABILITY: Source: Reglement Grand Ducal du 19 Decembre 2008 - CIS_minimum_income_year = 936 - CIS_minimum_income_month= 78 - CIS_minimum_income_day = 3,12 - **/ 								
	                                 tempOCL= "income.income_per_month()";
	                                 double income_per_month = OCLForJAVA.evaluateDouble(input,tempOCL,"income_per_month");
	                                 /** Description: The value of the taxpayer's income per month -  **/							
	                                 if ( (income_per_month > CIS_minimum_income_month) == false){
	                                             double CIS_minimum_income_year = 936;
	                                             /** TRACEABILITY: Source: Reglement Grand Ducal du 19 Decembre 2008 - CIS_minimum_income_year = 936 - CIS_minimum_income_month= 78 - CIS_minimum_income_day = 3,12 - **/ 								
	                                             tempOCL= "income.income_per_year()";
	                                             double income_per_year = OCLForJAVA.evaluateDouble(input,tempOCL,"income_per_year");
	                                             /** Description: The value of the taxpayer's income per year -  **/							
	                                             if ( (income_per_year > CIS_minimum_income_year) == false){
	                                             double expected_CIS = 0; 	
	                                             OCLForJAVA.updateEFeature(input,"income.tax_card.credit_CIS_yearly",expected_CIS);
	                                             }else{
	                                                if ((income_per_year > CIS_minimum_income_year) == true) {
	                                                               tempOCL= "income.prorata_worked_months()";
	                                                               int nb_months = OCLForJAVA.evaluateInt(input,tempOCL,"nb_months");
	                                                               /** Description: Period during which the taxpayer has been employed over - the course of the tax year expressed in terms of months. -  **/							
	                                                               tempOCL= "nb_months=12";
	                                                               boolean does_income_cover_full_year = OCLForJAVA.evaluateBoolean(input,tempOCL,"does_income_cover_full_year");
	                                                               /** Description: Returns yes if the income cover all periods of one year.  - In other words, checks if the taxpayers have been working for the whole year. -  **/							
	                                                               if ( (does_income_cover_full_year) == true){
	                                                               double flat_rate_CIS_yearly = 300;
	                                                               /** TRACEABILITY: Source: Reglement Grand Ducal du 19 Decembre 2008 - flat_rate_CIS_yearly = 300 - flat_rate_CIS_monthly = 25 - flat_rate_CIS_daily = 1 - **/ 								
	                                                               double expected_CIS = flat_rate_CIS_yearly; 	
	                                                               OCLForJAVA.updateEFeature(input,"income.tax_card.credit_CIS_yearly",expected_CIS);
	                                                               }else{
	                                                                 if ((does_income_cover_full_year) == false) {
	                                                                 double flat_rate_CIS_daily = 1;
	                                                                 /** TRACEABILITY: Source: Reglement Grand Ducal du 19 Decembre 2008 - flat_rate_CIS_yearly = 300 - flat_rate_CIS_monthly = 25 - flat_rate_CIS_daily = 1 - **/ 								
	                                                                 double flat_rate_CIS_monthly = 25;
	                                                                 /** TRACEABILITY: Source: Reglement Grand Ducal du 19 Decembre 2008 - flat_rate_CIS_yearly = 300 - flat_rate_CIS_monthly = 25 - flat_rate_CIS_daily = 1 - **/ 								
	                                                                 tempOCL= "income.prorata_worked_days()";
	                                                                 int nb_days = OCLForJAVA.evaluateInt(input,tempOCL,"nb_days");
	                                                                 /** Description: Complement to nb_months. Example the taxpayer worked for 0 year,  - 7 months, and 5 days -  **/							
	                                                                 double expected_CIS = flat_rate_CIS_monthly * nb_months + flat_rate_CIS_daily * nb_days; 	
	                                                                 OCLForJAVA.updateEFeature(input,"income.tax_card.credit_CIS_yearly",expected_CIS);
	                                                                 }else
	                                                                 {System.err.println("Unhandeled situation"); return;
	                                                                 }
	                                                               }
	                                                }else
	                                                {System.err.println("Unhandeled situation"); return;
	                                                }
	                                             }
	                                 }else{
	                                   if ((income_per_month > CIS_minimum_income_month) == true) {
	                                                tempOCL= "income.prorata_worked_months()";
	                                                int nb_months = OCLForJAVA.evaluateInt(input,tempOCL,"nb_months");
	                                                /** Description: Period during which the taxpayer has been employed over - the course of the tax year expressed in terms of months. -  **/							
	                                                tempOCL= "nb_months=12";
	                                                boolean does_income_cover_full_year = OCLForJAVA.evaluateBoolean(input,tempOCL,"does_income_cover_full_year");
	                                                /** Description: Returns yes if the income cover all periods of one year.  - In other words, checks if the taxpayers have been working for the whole year. -  **/							
	                                                if ( (does_income_cover_full_year) == true){
	                                                double flat_rate_CIS_yearly = 300;
	                                                /** TRACEABILITY: Source: Reglement Grand Ducal du 19 Decembre 2008 - flat_rate_CIS_yearly = 300 - flat_rate_CIS_monthly = 25 - flat_rate_CIS_daily = 1 - **/ 								
	                                                double expected_CIS = flat_rate_CIS_yearly; 	
	                                                OCLForJAVA.updateEFeature(input,"income.tax_card.credit_CIS_yearly",expected_CIS);
	                                                }else{
	                                                  if ((does_income_cover_full_year) == false) {
	                                                  double flat_rate_CIS_daily = 1;
	                                                  /** TRACEABILITY: Source: Reglement Grand Ducal du 19 Decembre 2008 - flat_rate_CIS_yearly = 300 - flat_rate_CIS_monthly = 25 - flat_rate_CIS_daily = 1 - **/ 								
	                                                  double flat_rate_CIS_monthly = 25;
	                                                  /** TRACEABILITY: Source: Reglement Grand Ducal du 19 Decembre 2008 - flat_rate_CIS_yearly = 300 - flat_rate_CIS_monthly = 25 - flat_rate_CIS_daily = 1 - **/ 								
	                                                  tempOCL= "income.prorata_worked_days()";
	                                                  int nb_days = OCLForJAVA.evaluateInt(input,tempOCL,"nb_days");
	                                                  /** Description: Complement to nb_months. Example the taxpayer worked for 0 year,  - 7 months, and 5 days -  **/							
	                                                  double expected_CIS = flat_rate_CIS_monthly * nb_months + flat_rate_CIS_daily * nb_days; 	
	                                                  OCLForJAVA.updateEFeature(input,"income.tax_card.credit_CIS_yearly",expected_CIS);
	                                                  }else
	                                                  {System.err.println("Unhandeled situation"); return;
	                                                  }
	                                                }
	                                   }else
	                                   {System.err.println("Unhandeled situation"); return;
	                                   }
	                                 }
	                       }else{
	                         if ((income_per_day > CIS_minimum_income_day) == true) {
	                                   tempOCL= "income.prorata_worked_months()";
	                                   int nb_months = OCLForJAVA.evaluateInt(input,tempOCL,"nb_months");
	                                   /** Description: Period during which the taxpayer has been employed over - the course of the tax year expressed in terms of months. -  **/							
	                                   tempOCL= "nb_months=12";
	                                   boolean does_income_cover_full_year = OCLForJAVA.evaluateBoolean(input,tempOCL,"does_income_cover_full_year");
	                                   /** Description: Returns yes if the income cover all periods of one year.  - In other words, checks if the taxpayers have been working for the whole year. -  **/							
	                                   if ( (does_income_cover_full_year) == true){
	                                   double flat_rate_CIS_yearly = 300;
	                                   /** TRACEABILITY: Source: Reglement Grand Ducal du 19 Decembre 2008 - flat_rate_CIS_yearly = 300 - flat_rate_CIS_monthly = 25 - flat_rate_CIS_daily = 1 - **/ 								
	                                   double expected_CIS = flat_rate_CIS_yearly; 	
	                                   OCLForJAVA.updateEFeature(input,"income.tax_card.credit_CIS_yearly",expected_CIS);
	                                   }else{
	                                      if ((does_income_cover_full_year) == false) {
	                                      double flat_rate_CIS_daily = 1;
	                                      /** TRACEABILITY: Source: Reglement Grand Ducal du 19 Decembre 2008 - flat_rate_CIS_yearly = 300 - flat_rate_CIS_monthly = 25 - flat_rate_CIS_daily = 1 - **/ 								
	                                      double flat_rate_CIS_monthly = 25;
	                                      /** TRACEABILITY: Source: Reglement Grand Ducal du 19 Decembre 2008 - flat_rate_CIS_yearly = 300 - flat_rate_CIS_monthly = 25 - flat_rate_CIS_daily = 1 - **/ 								
	                                      tempOCL= "income.prorata_worked_days()";
	                                      int nb_days = OCLForJAVA.evaluateInt(input,tempOCL,"nb_days");
	                                      /** Description: Complement to nb_months. Example the taxpayer worked for 0 year,  - 7 months, and 5 days -  **/							
	                                      double expected_CIS = flat_rate_CIS_monthly * nb_months + flat_rate_CIS_daily * nb_days; 	
	                                      OCLForJAVA.updateEFeature(input,"income.tax_card.credit_CIS_yearly",expected_CIS);
	                                      }else
	                                      {System.err.println("Unhandeled situation"); return;
	                                      }
	                                   }
	                         }else
	                         {System.err.println("Unhandeled situation"); return;
	                         }
	                       }
	               }else
	               {System.err.println("Unhandeled situation"); return;}
	             }
	        }else
	        {System.err.println("Unhandeled situation"); return;}
	     }
	   }else
	   {System.err.println("Unhandeled situation"); return;}
	}
	OCLForJAVA.iterationExit();
	}
	}else{
	   if ((taxation_year>=2009) == false) {
	   return;
	   }else
	   {System.err.println("Unhandeled situation"); return;
	   }
	}
	} 
	// End of CIS simulation code
	// Beginning of CE_Invalidity simulation code
	public static void CE_Invalidity (EObject input, String ADName)
	{
	OCLForJAVA.init(ADName,input);
	String tempOCL="";
	 tempOCL= "self.oclIsTypeOf(Resident_Tax_Payer)"; 
	boolean tax_payer_is_resident = OCLForJAVA.evaluateBoolean(input,tempOCL,"tax_payer_is_resident"); 
	/** Description: Returns yes if the taxpayer is resident; returns no otherwise - **/	
	if ( (tax_payer_is_resident) == true){
	tempOCL= "self.from_agent.taxation_year";
	int taxation_year = OCLForJAVA.evaluateInt(input,tempOCL,"taxation_year"); 
	/** TRACEABILITY: Specifies when the request was postmarked. - **/ 									
	 tempOCL= "self.incomes->select(inc:Income|inc.year=taxation_year and inc.tax_card->notEmpty() and inc.tax_card.tax_card_type=Tax_Card_Type::Principal)->union(self.incomes->select(inc:Income|inc.year=taxation_year and inc.tax_card->notEmpty() and inc.tax_card.tax_card_type=Tax_Card_Type::Additional)->sortedBy(income_amount*-1))->asOrderedSet()"; 
	Collection<EObject> incomes = OCLForJAVA.evaluateECollection(input,tempOCL,"incomes","Income","OrderedSet"); 	 
	/** Description: The incomes of a given taxpayer for the - studied tax year. The first income is the - principal one. The rest of incomes are sorted - from the highest to the lowest income. Note - that taxpayers who are taxed jointly have - only one principal income - **/	
	for (EObject income: incomes){
	OCLForJAVA.newIteration(new Container("income",income,"incomes",incomes));
	tempOCL= "self.disability_type <> Disability_Types::OTHER";
	boolean eligible_type_of_disability = OCLForJAVA.evaluateBoolean(input,tempOCL,"eligible_type_of_disability");
	/** Description: Checks that the taxpayer disability is supported by - the law. For instance, war mutulation = type A or work - accident= type B are eligible. - **/							
	if ( (eligible_type_of_disability) == true){
	   tempOCL= "self.disability_type = Disability_Types::E";
	   boolean is_disability_of_type_Sight = OCLForJAVA.evaluateBoolean(input,tempOCL,"is_disability_of_type_Sight");
	   /** Description: Returns yes if the disability of the taxpayer is of type E (sight); -  **/							
	   if ( (is_disability_of_type_Sight) == false){
	   tempOCL= "self.deduction_according_disability_rate()"; 
	   double deduction_according_disability_rate = OCLForJAVA.evaluateDouble(input,tempOCL,"deduction_according_disability_rate"); 
	   /** Description: Contains the value of the deduction proportionally to the - taxpayers disability percentage. For instance, - if disability between 45% and 55% then it contains 375. - Source: reglement grand-ducal. -  **/	
	   tempOCL= "income.prorata_period()";
	   double prorata_period = OCLForJAVA.evaluateDouble(input,tempOCL,"prorata_period");
	   /** Description: Period during which the taxpayer has been employed  - over the course of the tax year (between 0 and 1)  -  **/							
	   double expected_CE_invalidity = prorata_period * deduction_according_disability_rate
	   ; 	
	   OCLForJAVA.updateEFeature(input,"income.tax_card.deduction_CE_invalidity_yearly",expected_CE_invalidity);
	   	   
	   }else{
	     if ((is_disability_of_type_Sight) == true) {
	     double CE_invalidity_flat_rate_type_E = 1455;
	     /** TRACEABILITY: Contains the anual deduction for CE invalidity when the -  taxpayer has sight disability. Source: reglement grand-ducal. s - CE_invalidity_flat_rate_typeE =  1.455   - **/ 								
	     tempOCL= "income.prorata_period()";
	     double prorata_period = OCLForJAVA.evaluateDouble(input,tempOCL,"prorata_period");
	     /** Description: Period during which the taxpayer has been employed  - over the course of the tax year (between 0 and 1)  -  **/							
	     double expected_CE_invalidity = prorata_period * CE_invalidity_flat_rate_type_E; 	
	     OCLForJAVA.updateEFeature(input,"income.tax_card.deduction_CE_invalidity_yearly",expected_CE_invalidity);
	     
	     }else
	     {System.err.println("Unhandeled situation"); return;
	     }
	   }
	}else{
	   if ((eligible_type_of_disability) == false) {
	   double expected_CE_invalidity = 0; 	
	   OCLForJAVA.updateEFeature(input,"income.tax_card.deduction_CE_invalidity_yearly",expected_CE_invalidity);
	   
	   }else
	   {System.err.println("Unhandeled situation"); return;
	   }
	}
	OCLForJAVA.iterationExit();
	}
	}else{
	   if ((tax_payer_is_resident) == false) {
	      tempOCL= "self.oclAsType(Non_Resident_Tax_Payer).is_assimilated_to_resident"; 
	     boolean is_taxpayer_assimilated_to_a_resident_taxpayer = OCLForJAVA.evaluateBoolean(input,tempOCL,"is_taxpayer_assimilated_to_a_resident_taxpayer"); 
	     /** Description: Returns yes if the taxpayer is assimilated to a  - resident taxpayer. For instance when a non  - resident taxpayer is taxed by "voie_assiette". -  **/	
	     if ( (is_taxpayer_assimilated_to_a_resident_taxpayer) == true){
	     tempOCL= "self.from_agent.taxation_year";
	     int taxation_year = OCLForJAVA.evaluateInt(input,tempOCL,"taxation_year"); 
	     /** TRACEABILITY: Specifies when the request was postmarked. - **/ 									
	      tempOCL= "self.incomes->select(inc:Income|inc.year=taxation_year and inc.tax_card->notEmpty() and inc.tax_card.tax_card_type=Tax_Card_Type::Principal)->union(self.incomes->select(inc:Income|inc.year=taxation_year and inc.tax_card->notEmpty() and inc.tax_card.tax_card_type=Tax_Card_Type::Additional)->sortedBy(income_amount*-1))->asOrderedSet()"; 
	     Collection<EObject> incomes = OCLForJAVA.evaluateECollection(input,tempOCL,"incomes","Income","OrderedSet"); 	 
	     /** Description: The incomes of a given taxpayer for the  - studied tax year. The first income is the - principal one. The rest of incomes are sorted  - from the highest to the lowest income. Note  - that taxpayers who are taxed jointly have  - only one principal income  -  **/	
	     for (EObject income: incomes){
	     OCLForJAVA.newIteration(new Container("income",income,"incomes",incomes));
	     tempOCL= "self.disability_type <> Disability_Types::OTHER";
	     boolean eligible_type_of_disability = OCLForJAVA.evaluateBoolean(input,tempOCL,"eligible_type_of_disability");
	     /** Description: Checks that the taxpayer disability is supported by - the law. For instance, war mutulation = type A or work - accident= type B are eligible. -  **/							
	     if ( (eligible_type_of_disability) == true){
	        tempOCL= "self.disability_type = Disability_Types::E";
	        boolean is_disability_of_type_Sight = OCLForJAVA.evaluateBoolean(input,tempOCL,"is_disability_of_type_Sight");
	        /** Description: Returns yes if the disability of the taxpayer is of type E (sight); -  **/							
	        if ( (is_disability_of_type_Sight) == false){
	        tempOCL= "self.deduction_according_disability_rate()"; 
	        double deduction_according_disability_rate = OCLForJAVA.evaluateDouble(input,tempOCL,"deduction_according_disability_rate"); 
	        /** Description: Contains the value of the deduction proportionally to the - taxpayers disability percentage. For instance, - if disability between 45% and 55% then it contains 375. - Source: reglement grand-ducal. -  **/	
	        tempOCL= "income.prorata_period()";
	        double prorata_period = OCLForJAVA.evaluateDouble(input,tempOCL,"prorata_period");
	        /** Description: Period during which the taxpayer has been employed  - over the course of the tax year (between 0 and 1)  -  **/							
	        double expected_CE_invalidity = prorata_period * deduction_according_disability_rate
	        ; 	
	        OCLForJAVA.updateEFeature(input,"income.tax_card.deduction_CE_invalidity_yearly",expected_CE_invalidity);
	        
	        }else{
	          if ((is_disability_of_type_Sight) == true) {
	          double CE_invalidity_flat_rate_type_E = 1455;
	          /** TRACEABILITY: Contains the anual deduction for CE invalidity when the -  taxpayer has sight disability. Source: reglement grand-ducal. s - CE_invalidity_flat_rate_typeE =  1.455   - **/ 								
	          tempOCL= "income.prorata_period()";
	          double prorata_period = OCLForJAVA.evaluateDouble(input,tempOCL,"prorata_period");
	          /** Description: Period during which the taxpayer has been employed  - over the course of the tax year (between 0 and 1)  -  **/							
	          double expected_CE_invalidity = prorata_period * CE_invalidity_flat_rate_type_E; 	
	          OCLForJAVA.updateEFeature(input,"income.tax_card.deduction_CE_invalidity_yearly",expected_CE_invalidity);
	          
	          }else
	          {System.err.println("Unhandeled situation"); return;
	          }
	        }
	     }else{
	        if ((eligible_type_of_disability) == false) {
	        double expected_CE_invalidity = 0; 	
	        OCLForJAVA.updateEFeature(input,"income.tax_card.deduction_CE_invalidity_yearly",expected_CE_invalidity);
	        
	        }else
	        {System.err.println("Unhandeled situation"); return;
	        }
	     }
	     OCLForJAVA.iterationExit();
	     }
	     }else{
	        if ((is_taxpayer_assimilated_to_a_resident_taxpayer) == false) {
	        return;
	        }else
	        {System.err.println("Unhandeled situation"); return;
	        }
	     }
	   }else
	   {System.err.println("Unhandeled situation"); return;
	   }
	}
	} 
	// End of CE_Invalidity simulation code

	// Beginning of CE_Invalidity_Modified simulation code
	public static void CE_Invalidity_Modified (EObject input, String ADName)
	{
	OCLForJAVA.init(ADName,input);
	String tempOCL="";
	 tempOCL= "self.oclIsTypeOf(Resident_Tax_Payer)"; 
	boolean tax_payer_is_resident = OCLForJAVA.evaluateBoolean(input,tempOCL,"tax_payer_is_resident"); 
	/** Description: Returns yes if the taxpayer is resident; returns no otherwise - **/	
	if ( (tax_payer_is_resident) == true){
	tempOCL= "self.from_agent.taxation_year";
	int taxation_year = OCLForJAVA.evaluateInt(input,tempOCL,"taxation_year"); 
	/** TRACEABILITY: Specifies when the request was postmarked. - **/ 									
	 tempOCL= "self.incomes->select(inc:Income|inc.year=taxation_year and inc.tax_card->notEmpty() and inc.tax_card.tax_card_type=Tax_Card_Type::Principal)->union(self.incomes->select(inc:Income|inc.year=taxation_year and inc.tax_card->notEmpty() and inc.tax_card.tax_card_type=Tax_Card_Type::Additional)->sortedBy(income_amount*-1))->asOrderedSet()"; 
	Collection<EObject> incomes = OCLForJAVA.evaluateECollection(input,tempOCL,"incomes","Income","OrderedSet"); 	 
	/** Description: The incomes of a given taxpayer for the - studied tax year. The first income is the - principal one. The rest of incomes are sorted - from the highest to the lowest income. Note - that taxpayers who are taxed jointly have - only one principal income - **/	
	for (EObject income: incomes){
	OCLForJAVA.newIteration(new Container("income",income,"incomes",incomes));
	tempOCL= "self.disability_type <> Disability_Types::OTHER";
	boolean eligible_type_of_disability = OCLForJAVA.evaluateBoolean(input,tempOCL,"eligible_type_of_disability");
	/** Description: Checks that the taxpayer disability is supported by - the law. For instance, war mutulation = type A or work - accident= type B are eligible. - **/							
	if ( (eligible_type_of_disability) == true){
	   tempOCL= "self.disability_type = Disability_Types::E";
	   boolean is_disability_of_type_Sight = OCLForJAVA.evaluateBoolean(input,tempOCL,"is_disability_of_type_Sight");
	   /** Description: Returns yes if the disability of the taxpayer is of type E (sight); -  **/							
	   if ( (is_disability_of_type_Sight) == false){
	   tempOCL= "self.deduction_according_disability_rate()"; 
	   double deduction_according_disability_rate = OCLForJAVA.evaluateDouble(input,tempOCL,"deduction_according_disability_rate"); 
	   /** Description: Contains the value of the deduction proportionally to the - taxpayers disability percentage. For instance, - if disability between 45% and 55% then it contains 375. - Source: reglement grand-ducal. -  **/	
	   tempOCL= "income.prorata_period()";
	   double prorata_period = OCLForJAVA.evaluateDouble(input,tempOCL,"prorata_period");
	   /** Description: Period during which the taxpayer has been employed  - over the course of the tax year (between 0 and 1)  -  **/							
	   double expected_CE_invalidity = prorata_period * deduction_according_disability_rate
	   ; 	
	   OCLForJAVA.updateEFeature(input,"income.tax_card.deduction_CE_invalidity_yearly",expected_CE_invalidity);
	   	   
	   }else{
	     if ((is_disability_of_type_Sight) == true) {
	     double CE_invalidity_flat_rate_type_E = 500;
	     /** TRACEABILITY: Contains the anual deduction for CE invalidity when the -  taxpayer has sight disability. Source: reglement grand-ducal. s - CE_invalidity_flat_rate_typeE =  1.455   - **/ 								
	     tempOCL= "income.prorata_period()";
	     double prorata_period = OCLForJAVA.evaluateDouble(input,tempOCL,"prorata_period");
	     /** Description: Period during which the taxpayer has been employed  - over the course of the tax year (between 0 and 1)  -  **/							
	     double expected_CE_invalidity = prorata_period * CE_invalidity_flat_rate_type_E; 	
	     OCLForJAVA.updateEFeature(input,"income.tax_card.deduction_CE_invalidity_yearly",expected_CE_invalidity);
	     
	     }else
	     {System.err.println("Unhandeled situation"); return;
	     }
	   }
	}else{
	   if ((eligible_type_of_disability) == false) {
	   double expected_CE_invalidity = 0; 	
	   OCLForJAVA.updateEFeature(input,"income.tax_card.deduction_CE_invalidity_yearly",expected_CE_invalidity);
	   
	   }else
	   {System.err.println("Unhandeled situation"); return;
	   }
	}
	OCLForJAVA.iterationExit();
	}
	}else{
	   if ((tax_payer_is_resident) == false) {
	      tempOCL= "self.oclAsType(Non_Resident_Tax_Payer).is_assimilated_to_resident"; 
	     boolean is_taxpayer_assimilated_to_a_resident_taxpayer = OCLForJAVA.evaluateBoolean(input,tempOCL,"is_taxpayer_assimilated_to_a_resident_taxpayer"); 
	     /** Description: Returns yes if the taxpayer is assimilated to a  - resident taxpayer. For instance when a non  - resident taxpayer is taxed by "voie_assiette". -  **/	
	     if ( (is_taxpayer_assimilated_to_a_resident_taxpayer) == true){
	     tempOCL= "self.from_agent.taxation_year";
	     int taxation_year = OCLForJAVA.evaluateInt(input,tempOCL,"taxation_year"); 
	     /** TRACEABILITY: Specifies when the request was postmarked. - **/ 									
	      tempOCL= "self.incomes->select(inc:Income|inc.year=taxation_year and inc.tax_card->notEmpty() and inc.tax_card.tax_card_type=Tax_Card_Type::Principal)->union(self.incomes->select(inc:Income|inc.year=taxation_year and inc.tax_card->notEmpty() and inc.tax_card.tax_card_type=Tax_Card_Type::Additional)->sortedBy(income_amount*-1))->asOrderedSet()"; 
	     Collection<EObject> incomes = OCLForJAVA.evaluateECollection(input,tempOCL,"incomes","Income","OrderedSet"); 	 
	     /** Description: The incomes of a given taxpayer for the  - studied tax year. The first income is the - principal one. The rest of incomes are sorted  - from the highest to the lowest income. Note  - that taxpayers who are taxed jointly have  - only one principal income  -  **/	
	     for (EObject income: incomes){
	     OCLForJAVA.newIteration(new Container("income",income,"incomes",incomes));
	     tempOCL= "self.disability_type <> Disability_Types::OTHER";
	     boolean eligible_type_of_disability = OCLForJAVA.evaluateBoolean(input,tempOCL,"eligible_type_of_disability");
	     /** Description: Checks that the taxpayer disability is supported by - the law. For instance, war mutulation = type A or work - accident= type B are eligible. -  **/							
	     if ( (eligible_type_of_disability) == true){
	        tempOCL= "self.disability_type = Disability_Types::E";
	        boolean is_disability_of_type_Sight = OCLForJAVA.evaluateBoolean(input,tempOCL,"is_disability_of_type_Sight");
	        /** Description: Returns yes if the disability of the taxpayer is of type E (sight); -  **/							
	        if ( (is_disability_of_type_Sight) == false){
	        tempOCL= "self.deduction_according_disability_rate()"; 
	        double deduction_according_disability_rate = OCLForJAVA.evaluateDouble(input,tempOCL,"deduction_according_disability_rate"); 
	        /** Description: Contains the value of the deduction proportionally to the - taxpayers disability percentage. For instance, - if disability between 45% and 55% then it contains 375. - Source: reglement grand-ducal. -  **/	
	        tempOCL= "income.prorata_period()";
	        double prorata_period = OCLForJAVA.evaluateDouble(input,tempOCL,"prorata_period");
	        /** Description: Period during which the taxpayer has been employed  - over the course of the tax year (between 0 and 1)  -  **/							
	        double expected_CE_invalidity = prorata_period * deduction_according_disability_rate
	        ; 	
	        OCLForJAVA.updateEFeature(input,"income.tax_card.deduction_CE_invalidity_yearly",expected_CE_invalidity);
	        
	        }else{
	          if ((is_disability_of_type_Sight) == true) {
	          double CE_invalidity_flat_rate_type_E = 500;
	          /** TRACEABILITY: Contains the anual deduction for CE invalidity when the -  taxpayer has sight disability. Source: reglement grand-ducal. s - CE_invalidity_flat_rate_typeE =  1.455   - **/ 								
	          tempOCL= "income.prorata_period()";
	          double prorata_period = OCLForJAVA.evaluateDouble(input,tempOCL,"prorata_period");
	          /** Description: Period during which the taxpayer has been employed  - over the course of the tax year (between 0 and 1)  -  **/							
	          double expected_CE_invalidity = prorata_period * CE_invalidity_flat_rate_type_E; 	
	          OCLForJAVA.updateEFeature(input,"income.tax_card.deduction_CE_invalidity_yearly",expected_CE_invalidity);
	          
	          }else
	          {System.err.println("Unhandeled situation"); return;
	          }
	        }
	     }else{
	        if ((eligible_type_of_disability) == false) {
	        double expected_CE_invalidity = 0; 	
	        OCLForJAVA.updateEFeature(input,"income.tax_card.deduction_CE_invalidity_yearly",expected_CE_invalidity);
	        
	        }else
	        {System.err.println("Unhandeled situation"); return;
	        }
	     }
	     OCLForJAVA.iterationExit();
	     }
	     }else{
	        if ((is_taxpayer_assimilated_to_a_resident_taxpayer) == false) {
	        return;
	        }else
	        {System.err.println("Unhandeled situation"); return;
	        }
	     }
	   }else
	   {System.err.println("Unhandeled situation"); return;
	   }
	}
	} 
	// End of CE_Invalidity_Modified simulation code
	// Beginning of CIP simulation code
	public static void CIP (EObject input, String ADName)
	{
	OCLForJAVA.init(ADName,input);
	String tempOCL="";
	tempOCL= "self.from_agent.taxation_year";
	int taxation_year = OCLForJAVA.evaluateInt(input,tempOCL,"taxation_year"); 
	/** TRACEABILITY: Specifies when the request was postmarked. - **/ 									
	if ( (taxation_year>=2009) == false){
	return;
	}else{
	   if ((taxation_year>=2009) == true) {
	   tempOCL= "self.incomes->select(inc:Income|inc.year=taxation_year and inc.tax_card->notEmpty() and inc.tax_card.tax_card_type=Tax_Card_Type::Principal)->union(self.incomes->select(inc:Income|inc.year=taxation_year and inc.tax_card->notEmpty() and inc.tax_card.tax_card_type=Tax_Card_Type::Additional)->sortedBy(income_amount*-1))->asOrderedSet()";
	   Collection<EObject> incomes = OCLForJAVA.evaluateECollection(input,tempOCL,"incomes","Income","OrderedSet"); 	 
	   /** Description: The incomes of a given taxpayer for the studied tax year. The first income is the principal one. - The rest of incomes are sorted from the highest to the lowest income. -  Note that taxpayers who are taxed jointly have only one principal income. -  **/	
	   for (EObject income: incomes){
	   OCLForJAVA.newIteration(new Container("income",income,"incomes",incomes));
	   tempOCL= "income.income_type.subjectToWithholdingTax";
	   boolean is_income_subject_to_withholding = OCLForJAVA.evaluateBoolean(input,tempOCL,"is_income_subject_to_withholding");
	   /** Description: Returns yes if the income is under withholding taxation: false otherwise -  **/							
	   if ( (is_income_subject_to_withholding) == true){
	     tempOCL= "income.income_type.oclIsTypeOf(Pensions_and_Annuities_Income)";
	     boolean is_income_pension = OCLForJAVA.evaluateBoolean(input,tempOCL,"is_income_pension");
	     /** Description: Returns yes if the income is of type Pension and Annuities; returns false otherwise -  **/							
	     if ( (is_income_pension) == false){
	          tempOCL= "income.income_type.oclIsTypeOf(Rentals_and_Leases_Income)";
	          boolean is_income_ofType_rental = OCLForJAVA.evaluateBoolean(input,tempOCL,"is_income_ofType_rental");
	          /** Description: Returns yes if the income is of type Rentals and Leases; returns false otherwise -  **/							
	          if ( (is_income_ofType_rental) == false){
	          double expected_CIP = 0; 	
	          OCLForJAVA.updateEFeature(input,"income.tax_card.credit_CIP_yearly",expected_CIP);
	          }else{
	             if ((is_income_ofType_rental) == true) {
	                    tempOCL= "income.oclAsType(Rentals_and_Leases_Income).is_periodic";
	                    boolean is_income_periodic = OCLForJAVA.evaluateBoolean(input,tempOCL,"is_income_periodic ");
	                    /** Description: Returns yes if the income is received by the taxpayer on regular basis; returns false otherwise -  **/							
	                    if ( (is_income_periodic ) == false){
	                    double expected_CIP = 0; 	
	                    OCLForJAVA.updateEFeature(input,"income.tax_card.credit_CIP_yearly",expected_CIP);
	                    }else{
	                       if ((is_income_periodic ) == true) {
	                                 tempOCL= "incomes->excluding(income)->select(i:Income | i.tax_card.credit_CIP_monthly <> 0 or i.tax_card.credit_CIP_yearly <> 0)->notEmpty()";
	                                 boolean credit_already_attributed = OCLForJAVA.evaluateBoolean(input,tempOCL,"credit_already_attributed");
	                                 /** Description: Makes sure that CIP was not attributed to the taxpayer on any other income. It returns yes if the taxpayer -  has already benefited from CIP. -  **/							
	                                 if ( (credit_already_attributed) == false){
	                                             double CIP_minimum_income_month = 25;
	                                             /** TRACEABILITY: Source: Reglement Grand Ducal du 19 Decembre 2008  - CIP_minimum_income_year= 300  - CIP_minimum_income_month= 25  - flat_rate_CIP_yearly = 300  - **/ 								
	                                             tempOCL= "income.income_per_month()";
	                                             double income_per_month = OCLForJAVA.evaluateDouble(input,tempOCL,"income_per_month");
	                                             /** Description: The value of the income per month -  **/							
	                                             if ( (income_per_month >= CIP_minimum_income_month) == true){
	                                             double flat_rate_CIP_yearly = 300;
	                                             /** TRACEABILITY: Source: Reglement Grand Ducal du 19 Decembre 2008  - CIP_minimum_income_year= 300  - CIP_minimum_income_month= 25  - flat_rate_CIP_yearly = 300  - **/ 								
	                                             tempOCL= "income.prorata_period()";
	                                             double prorata_period = OCLForJAVA.evaluateDouble(input,tempOCL,"prorata_period");
	                                             /** Description: Period during which the taxpayer has been employed over the course of the tax year (between 0 and 1) -  **/							
	                                             double expected_CIP = flat_rate_CIP_yearly * prorata_period; 	
	                                             OCLForJAVA.updateEFeature(input,"income.tax_card.credit_CIP_yearly",expected_CIP);
	                                             }else{
	                                                if ((income_per_month >= CIP_minimum_income_month) == false) {
	                                                               double CIP_minimum_income_year = 300;
	                                                               /** TRACEABILITY: Source: Reglement Grand Ducal du 19 Decembre 2008  - CIP_minimum_income_year= 300  - CIP_minimum_income_month= 25  - flat_rate_CIP_yearly = 300  - **/ 								
	                                                               tempOCL= "income.income_per_year()";
	                                                               double income_per_year = OCLForJAVA.evaluateDouble(input,tempOCL,"income_per_year ");
	                                                               /** Description: The value of the income per year -  **/							
	                                                               if ( (income_per_year >= CIP_minimum_income_year) == true){
	                                                               double flat_rate_CIP_yearly = 300;
	                                                               /** TRACEABILITY: Source: Reglement Grand Ducal du 19 Decembre 2008  - CIP_minimum_income_year= 300  - CIP_minimum_income_month= 25  - flat_rate_CIP_yearly = 300  - **/ 								
	                                                               tempOCL= "income.prorata_period()";
	                                                               double prorata_period = OCLForJAVA.evaluateDouble(input,tempOCL,"prorata_period");
	                                                               /** Description: Period during which the taxpayer has been employed over the course of the tax year (between 0 and 1) -  **/							
	                                                               double expected_CIP = flat_rate_CIP_yearly * prorata_period; 	
	                                                               OCLForJAVA.updateEFeature(input,"income.tax_card.credit_CIP_yearly",expected_CIP);
	                                                               }else{
	                                                                 if ((income_per_year >= CIP_minimum_income_year) == false) {
	                                                                 double expected_CIP = 0; 	
	                                                                 OCLForJAVA.updateEFeature(input,"income.tax_card.credit_CIP_yearly",expected_CIP);
	                                                                 }else
	                                                                 {System.err.println("Unhandeled situation"); return;
	                                                                 }
	                                                               }
	                                                }else
	                                                {System.err.println("Unhandeled situation"); return;
	                                                }
	                                             }
	                                 }else{
	                                   if ((credit_already_attributed) == true) {
	                                   double expected_CIP = 0; 	
	                                   OCLForJAVA.updateEFeature(input,"income.tax_card.credit_CIP_yearly",expected_CIP);
	                                   }else
	                                   {System.err.println("Unhandeled situation"); return;
	                                   }
	                                 }
	                       }else
	                       {System.err.println("Unhandeled situation"); return;
	                       }
	                    }
	             }else
	             {System.err.println("Unhandeled situation"); return;
	             }
	          }
	     }else{
	        if ((is_income_pension) == true) {
	             tempOCL= "incomes->excluding(income)->select(i:Income | i.tax_card.credit_CIP_monthly <> 0 or i.tax_card.credit_CIP_yearly <> 0)->notEmpty()";
	             boolean credit_already_attributed = OCLForJAVA.evaluateBoolean(input,tempOCL,"credit_already_attributed");
	             /** Description: Makes sure that CIP was not attributed to the taxpayer on any other income. It returns yes if the taxpayer -  has already benefited from CIP. -  **/							
	             if ( (credit_already_attributed) == false){
	                    double CIP_minimum_income_month = 25;
	                    /** TRACEABILITY: Source: Reglement Grand Ducal du 19 Decembre 2008  - CIP_minimum_income_year= 300  - CIP_minimum_income_month= 25  - flat_rate_CIP_yearly = 300  - **/ 								
	                    tempOCL= "income.income_per_month()";
	                    double income_per_month = OCLForJAVA.evaluateDouble(input,tempOCL,"income_per_month");
	                    /** Description: The value of the income per month -  **/							
	                    if ( (income_per_month >= CIP_minimum_income_month) == true){
	                    double flat_rate_CIP_yearly = 300;
	                    /** TRACEABILITY: Source: Reglement Grand Ducal du 19 Decembre 2008  - CIP_minimum_income_year= 300  - CIP_minimum_income_month= 25  - flat_rate_CIP_yearly = 300  - **/ 								
	                    tempOCL= "income.prorata_period()";
	                    double prorata_period = OCLForJAVA.evaluateDouble(input,tempOCL,"prorata_period");
	                    /** Description: Period during which the taxpayer has been employed over the course of the tax year (between 0 and 1) -  **/							
	                    double expected_CIP = flat_rate_CIP_yearly * prorata_period; 	
	                    OCLForJAVA.updateEFeature(input,"income.tax_card.credit_CIP_yearly",expected_CIP);
	                    }else{
	                       if ((income_per_month >= CIP_minimum_income_month) == false) {
	                                 double CIP_minimum_income_year = 300;
	                                 /** TRACEABILITY: Source: Reglement Grand Ducal du 19 Decembre 2008  - CIP_minimum_income_year= 300  - CIP_minimum_income_month= 25  - flat_rate_CIP_yearly = 300  - **/ 								
	                                 tempOCL= "income.income_per_year()";
	                                 double income_per_year = OCLForJAVA.evaluateDouble(input,tempOCL,"income_per_year ");
	                                 /** Description: The value of the income per year -  **/							
	                                 if ( (income_per_year >= CIP_minimum_income_year) == true){
	                                 double flat_rate_CIP_yearly = 300;
	                                 /** TRACEABILITY: Source: Reglement Grand Ducal du 19 Decembre 2008  - CIP_minimum_income_year= 300  - CIP_minimum_income_month= 25  - flat_rate_CIP_yearly = 300  - **/ 								
	                                 tempOCL= "income.prorata_period()";
	                                 double prorata_period = OCLForJAVA.evaluateDouble(input,tempOCL,"prorata_period");
	                                 /** Description: Period during which the taxpayer has been employed over the course of the tax year (between 0 and 1) -  **/							
	                                 double expected_CIP = flat_rate_CIP_yearly * prorata_period; 	
	                                 OCLForJAVA.updateEFeature(input,"income.tax_card.credit_CIP_yearly",expected_CIP);
	                                 }else{
	                                   if ((income_per_year >= CIP_minimum_income_year) == false) {
	                                   double expected_CIP = 0; 	
	                                   OCLForJAVA.updateEFeature(input,"income.tax_card.credit_CIP_yearly",expected_CIP);
	                                   }else
	                                   {System.err.println("Unhandeled situation"); return;
	                                   }
	                                 }
	                       }else
	                       {System.err.println("Unhandeled situation"); return;
	                       }
	                    }
	             }else{
	               if ((credit_already_attributed) == true) {
	               double expected_CIP = 0; 	
	               OCLForJAVA.updateEFeature(input,"income.tax_card.credit_CIP_yearly",expected_CIP);
	               }else
	               {System.err.println("Unhandeled situation"); return;
	               }
	             }
	        }else
	        {System.err.println("Unhandeled situation"); return;
	        }
	     }
	   }else{
	     if ((is_income_subject_to_withholding) == false) {
	     double expected_CIP = 0; 	
	     OCLForJAVA.updateEFeature(input,"income.tax_card.credit_CIP_yearly",expected_CIP);
	     }else
	     {System.err.println("Unhandeled situation"); return;
	     }
	   }
	   OCLForJAVA.iterationExit();
	   }
	   }else
	   {System.err.println("Unhandeled situation"); return;
	   }
	}
	} 
	// End of CIP simulation code

	// Beginning of DS_for_Permanent_Expenses_Alimony simulation code
	public static void DS_for_Permanent_Expenses_Alimony (EObject input, String ADName)
	{
	OCLForJAVA.init(ADName,input);
	String tempOCL="";
	 tempOCL= "self.oclIsTypeOf(Resident_Tax_Payer)";
	boolean is_resident_taxpayer = OCLForJAVA.evaluateBoolean(input,tempOCL,"is_resident_taxpayer"); 
	/** Description: Returns yes if the taxpayer is resident; returns no otherwise - **/	
	if ( (is_resident_taxpayer) == true){
	tempOCL= "self.from_agent.taxation_year";
	int taxation_year = OCLForJAVA.evaluateInt(input,tempOCL,"taxation_year"); 
	/** TRACEABILITY: Specifies when the request was postmarked. - **/ 									
	 tempOCL= "Legal_Union_Record.allInstances()->select(individual_A=self or individual_B=self and oclIsTypeOf(Marriage_Record))->select(start_year<=taxation_year and separation_cause=Separation_Causes::DIVORCE)";
	Collection<EObject> legel_unions = OCLForJAVA.evaluateECollection(input,tempOCL,"legel_unions","Legal_Union_Record","OrderedSet"); 	 
	/** Description: Returns the taxpayer's legal union records. - **/	
	for (EObject union: legel_unions){
	OCLForJAVA.newIteration(new Container("union",union,"legel_unions",legel_unions));
	tempOCL= "self.incomes->select(inc:Income|inc.year=taxation_year and inc.tax_card->notEmpty() and inc.tax_card.tax_card_type=Tax_Card_Type::Principal)->union(self.incomes->select(inc:Income|inc.year=taxation_year and inc.tax_card->notEmpty() and inc.tax_card.tax_card_type=Tax_Card_Type::Additional)->sortedBy(income_amount*-1))->asOrderedSet()";
	Collection<EObject> incomes = OCLForJAVA.evaluateECollection(input,tempOCL,"incomes","Income","OrderedSet"); 	
	/** Description: The taxapayer's incomes for a given taxation year. - **/	
	for (EObject income: incomes){
	OCLForJAVA.newIteration(new Container("income",income,"incomes",incomes));
	tempOCL= "union.end_year";
	int year_of_divorce = OCLForJAVA.evaluateInt(input,tempOCL,"year_of_divorce");
	/** Description: Returns the year when the divorce was agreed. - **/							
	if ( (year_of_divorce >= 1998) == true){
	   tempOCL= "union.mutual_agreement";
	   boolean is_divorce_by_mutual_agreement = OCLForJAVA.evaluateBoolean(input,tempOCL,"is_divorce_by_mutual_agreement");
	   /** Description: Returns yes if the divorce was by mutual agreement. -  **/							
	   if ( (is_divorce_by_mutual_agreement) == false){
	   double calculated_deduction = 0; 	
	   tempOCL= "income.tax_card.deduction_DS_Alimony_yearly";
	   double old = OCLForJAVA.evaluateDouble(input,tempOCL,"old");
	   /** Description: Add the calculated deduction using the model to the previous value of the deduction. -  **/							
	   OCLForJAVA.updateEFeature(input,"income.tax_card.deduction_DS_Alimony_yearly",old + calculated_deduction);
	   }else{
	     if ((is_divorce_by_mutual_agreement) == true) {
	          tempOCL= "if(union.individual_A=self) then union.individual_B else union.individual_A endif";
	          EObject spouse = OCLForJAVA.evaluateEObject(input,tempOCL,"spouse","Physical_Person"); 	
	          /** Description: Get the potential beneficiary of the expenses. -  **/							
	          tempOCL= "income.expenses->select(e:Expense| e.year_expense_was_incurred_in= taxation_year and e.expense_purpose= Expense_Purpose::ALIMONY and e.beneficiary=spouse).declared_amount->sum()";
	          double sum_expenses_to_spouse = OCLForJAVA.evaluateDouble(input,tempOCL,"sum_expenses_to_spouse");
	          /** Description: Calculate the alimony. -  **/							
	          tempOCL= "self.from_law.MAXIMUM_FLAT_RATE_FOR_ALIMONY (taxation_year, year_of_divorce)";
	          double maximum_flat_rate_for_alimony = OCLForJAVA.evaluateDouble(input,tempOCL,"maximum_flat_rate_for_alimony");
	          /** TRACEABILITY: Source: Art. 109bis Income Tax Law (2013) - Contains the maximal legal deduction for alimony according to the year parameter. - **/ 
	          if ( (sum_expenses_to_spouse < maximum_flat_rate_for_alimony) == true){
	          double calculated_deduction = sum_expenses_to_spouse; 	
	          tempOCL= "income.tax_card.deduction_DS_Alimony_yearly";
	          double old = OCLForJAVA.evaluateDouble(input,tempOCL,"old");
	          /** Description: Add the calculated deduction using the model to the previous value of the deduction. -  **/							
	          OCLForJAVA.updateEFeature(input,"income.tax_card.deduction_DS_Alimony_yearly",old + calculated_deduction);
	          }else{
	             if ((sum_expenses_to_spouse < maximum_flat_rate_for_alimony) == false) {
	             double calculated_deduction = maximum_flat_rate_for_alimony; 	
	             tempOCL= "income.tax_card.deduction_DS_Alimony_yearly";
	             double old = OCLForJAVA.evaluateDouble(input,tempOCL,"old");
	             /** Description: Add the calculated deduction using the model to the previous value of the deduction. -  **/							
	             OCLForJAVA.updateEFeature(input,"income.tax_card.deduction_DS_Alimony_yearly",old + calculated_deduction);
	             }else
	             {System.err.println("Unhandeled situation"); return;
	             }
	          }
	     }else
	     {System.err.println("Unhandeled situation"); return;
	     }
	   }
	}else{
	   if ((year_of_divorce >= 1998) == false) {
	     tempOCL= "if(union.individual_A=self) then union.individual_B else union.individual_A endif";
	     EObject spouse = OCLForJAVA.evaluateEObject(input,tempOCL,"spouse","Physical_Person"); 	
	     /** Description: Get the potential beneficiary of the expenses. -  **/							
	     tempOCL= "income.expenses->select(e:Expense| e.year_expense_was_incurred_in= taxation_year and e.expense_purpose= Expense_Purpose::ALIMONY and e.beneficiary=spouse).declared_amount->sum()";
	     double sum_expenses_to_spouse = OCLForJAVA.evaluateDouble(input,tempOCL,"sum_expenses_to_spouse");
	     /** Description: Calculate the alimony. -  **/							
	     tempOCL= "self.from_law.MAXIMUM_FLAT_RATE_FOR_ALIMONY (taxation_year, year_of_divorce)";
	     double maximum_flat_rate_for_alimony = OCLForJAVA.evaluateDouble(input,tempOCL,"maximum_flat_rate_for_alimony");
	     /** TRACEABILITY: Source: Art. 109bis Income Tax Law (2013) - Contains the maximal legal deduction for alimony according to the year parameter. - **/ 
	     if ( (sum_expenses_to_spouse < maximum_flat_rate_for_alimony) == true){
	     double calculated_deduction = sum_expenses_to_spouse; 	
	     tempOCL= "income.tax_card.deduction_DS_Alimony_yearly";
	     double old = OCLForJAVA.evaluateDouble(input,tempOCL,"old");
	     /** Description: Add the calculated deduction using the model to the previous value of the deduction. -  **/							
	     OCLForJAVA.updateEFeature(input,"income.tax_card.deduction_DS_Alimony_yearly",old + calculated_deduction);
	     }else{
	        if ((sum_expenses_to_spouse < maximum_flat_rate_for_alimony) == false) {
	        double calculated_deduction = maximum_flat_rate_for_alimony; 	
	        tempOCL= "income.tax_card.deduction_DS_Alimony_yearly";
	        double old = OCLForJAVA.evaluateDouble(input,tempOCL,"old");
	        /** Description: Add the calculated deduction using the model to the previous value of the deduction. -  **/							
	        OCLForJAVA.updateEFeature(input,"income.tax_card.deduction_DS_Alimony_yearly",old + calculated_deduction);
	        }else
	        {System.err.println("Unhandeled situation"); return;
	        }
	     }
	   }else
	   {System.err.println("Unhandeled situation"); return;
	   }
	}
	OCLForJAVA.iterationExit();
	}
	OCLForJAVA.iterationExit();
	}
	}else{
	   if ((is_resident_taxpayer) == false) {
	      tempOCL= "self.oclAsType(Non_Resident_Tax_Payer).is_assimilated_to_resident";
	     boolean is_taxpayer_assimilated_to_a_resident_taxpayer = OCLForJAVA.evaluateBoolean(input,tempOCL,"is_taxpayer_assimilated_to_a_resident_taxpayer"); 
	     /** Description: Returns yes if the taxpayer is assimilated to a  - resident taxpayer. For instance when a non  - resident taxpayer is taxed by "voie_assiette". -  **/	
	     if ( (is_taxpayer_assimilated_to_a_resident_taxpayer) == false){
	     return;
	     }else{
	        if ((is_taxpayer_assimilated_to_a_resident_taxpayer) == true) {
	        tempOCL= "self.from_agent.taxation_year";
	        int taxation_year = OCLForJAVA.evaluateInt(input,tempOCL,"taxation_year"); 
	        /** TRACEABILITY: Specifies when the request was postmarked. - **/ 									
	        tempOCL= "Legal_Union_Record.allInstances()->select(individual_A=self or individual_B=self and oclIsTypeOf(Marriage_Record))->select(start_year<=taxation_year and separation_cause=Separation_Causes::DIVORCE)";
	        Collection<EObject> legel_unions = OCLForJAVA.evaluateECollection(input,tempOCL,"legel_unions","Legal_Union_Record","OrderedSet"); 	 
	        /** Description: Returns the taxpayer's legal union records. -  **/	
	        for (EObject union: legel_unions){
	        OCLForJAVA.newIteration(new Container("union",union,"legel_unions",legel_unions));
	        tempOCL= "self.incomes->select(inc:Income|inc.year=taxation_year and inc.tax_card->notEmpty() and inc.tax_card.tax_card_type=Tax_Card_Type::Principal)->union(self.incomes->select(inc:Income|inc.year=taxation_year and inc.tax_card->notEmpty() and inc.tax_card.tax_card_type=Tax_Card_Type::Additional)->sortedBy(income_amount*-1))->asOrderedSet()";
	        Collection<EObject> incomes = OCLForJAVA.evaluateECollection(input,tempOCL,"incomes","Income","OrderedSet");  	
	        /** Description: The taxapayer's incomes for a given taxation year. -  **/	
	        for (EObject income: incomes){
	        OCLForJAVA.newIteration(new Container("income",income,"incomes",incomes));
	        tempOCL= "union.end_year";
	        int year_of_divorce = OCLForJAVA.evaluateInt(input,tempOCL,"year_of_divorce");
	        /** Description: Returns the year when the divorce was agreed. -  **/							
	        if ( (year_of_divorce >= 1998) == true){
	          tempOCL= "union.mutual_agreement";
	          boolean is_divorce_by_mutual_agreement = OCLForJAVA.evaluateBoolean(input,tempOCL,"is_divorce_by_mutual_agreement");
	          /** Description: Returns yes if the divorce was by mutual agreement. -  **/							
	          if ( (is_divorce_by_mutual_agreement) == false){
	          double calculated_deduction = 0; 	
	          tempOCL= "income.tax_card.deduction_DS_Alimony_yearly";
	          double old = OCLForJAVA.evaluateDouble(input,tempOCL,"old");
	          /** Description: Add the calculated deduction using the model to the previous value of the deduction. -  **/							
	          OCLForJAVA.updateEFeature(input,"income.tax_card.deduction_DS_Alimony_yearly",old + calculated_deduction);
	          }else{
	             if ((is_divorce_by_mutual_agreement) == true) {
	                  tempOCL= "if(union.individual_A=self) then union.individual_B else union.individual_A endif";
	                  EObject spouse = OCLForJAVA.evaluateEObject(input,tempOCL,"spouse","Physical_Person"); 	
	                  /** Description: Get the potential beneficiary of the expenses. -  **/							
	                  tempOCL= "income.expenses->select(e:Expense| e.year_expense_was_incurred_in= taxation_year and e.expense_purpose= Expense_Purpose::ALIMONY and e.beneficiary=spouse).declared_amount->sum()";
	                  double sum_expenses_to_spouse = OCLForJAVA.evaluateDouble(input,tempOCL,"sum_expenses_to_spouse");
	                  /** Description: Calculate the alimony. -  **/							
	                  tempOCL= "self.from_law.MAXIMUM_FLAT_RATE_FOR_ALIMONY (taxation_year, year_of_divorce)";
	                  double maximum_flat_rate_for_alimony = OCLForJAVA.evaluateDouble(input,tempOCL,"maximum_flat_rate_for_alimony");
	                  /** TRACEABILITY: Source: Art. 109bis Income Tax Law (2013) - Contains the maximal legal deduction for alimony according to the year parameter. - **/ 
	                  if ( (sum_expenses_to_spouse < maximum_flat_rate_for_alimony) == true){
	                  double calculated_deduction = sum_expenses_to_spouse; 	
	                  tempOCL= "income.tax_card.deduction_DS_Alimony_yearly";
	                  double old = OCLForJAVA.evaluateDouble(input,tempOCL,"old");
	                  /** Description: Add the calculated deduction using the model to the previous value of the deduction. -  **/							
	                  OCLForJAVA.updateEFeature(input,"income.tax_card.deduction_DS_Alimony_yearly",old + calculated_deduction);
	                  }else{
	                    if ((sum_expenses_to_spouse < maximum_flat_rate_for_alimony) == false) {
	                    double calculated_deduction = maximum_flat_rate_for_alimony; 	
	                    tempOCL= "income.tax_card.deduction_DS_Alimony_yearly";
	                    double old = OCLForJAVA.evaluateDouble(input,tempOCL,"old");
	                    /** Description: Add the calculated deduction using the model to the previous value of the deduction. -  **/							
	                    OCLForJAVA.updateEFeature(input,"income.tax_card.deduction_DS_Alimony_yearly",old + calculated_deduction);
	                    }else
	                    {System.err.println("Unhandeled situation"); return;
	                    }
	                  }
	             }else
	             {System.err.println("Unhandeled situation"); return;
	             }
	          }
	        }else{
	          if ((year_of_divorce >= 1998) == false) {
	             tempOCL= "if(union.individual_A=self) then union.individual_B else union.individual_A endif";
	             EObject spouse = OCLForJAVA.evaluateEObject(input,tempOCL,"spouse","Physical_Person"); 	
	             /** Description: Get the potential beneficiary of the expenses. -  **/							
	             tempOCL= "income.expenses->select(e:Expense| e.year_expense_was_incurred_in= taxation_year and e.expense_purpose= Expense_Purpose::ALIMONY and e.beneficiary=spouse).declared_amount->sum()";
	             double sum_expenses_to_spouse = OCLForJAVA.evaluateDouble(input,tempOCL,"sum_expenses_to_spouse");
	             /** Description: Calculate the alimony. -  **/							
	             tempOCL= "self.from_law.MAXIMUM_FLAT_RATE_FOR_ALIMONY (taxation_year, year_of_divorce)";
	             double maximum_flat_rate_for_alimony = OCLForJAVA.evaluateDouble(input,tempOCL,"maximum_flat_rate_for_alimony");
	             /** TRACEABILITY: Source: Art. 109bis Income Tax Law (2013) - Contains the maximal legal deduction for alimony according to the year parameter. - **/ 
	             if ( (sum_expenses_to_spouse < maximum_flat_rate_for_alimony) == true){
	             double calculated_deduction = sum_expenses_to_spouse; 	
	             tempOCL= "income.tax_card.deduction_DS_Alimony_yearly";
	             double old = OCLForJAVA.evaluateDouble(input,tempOCL,"old");
	             /** Description: Add the calculated deduction using the model to the previous value of the deduction. -  **/							
	             OCLForJAVA.updateEFeature(input,"income.tax_card.deduction_DS_Alimony_yearly",old + calculated_deduction);
	             }else{
	               if ((sum_expenses_to_spouse < maximum_flat_rate_for_alimony) == false) {
	               double calculated_deduction = maximum_flat_rate_for_alimony; 	
	               tempOCL= "income.tax_card.deduction_DS_Alimony_yearly";
	               double old = OCLForJAVA.evaluateDouble(input,tempOCL,"old");
	               /** Description: Add the calculated deduction using the model to the previous value of the deduction. -  **/							
	               OCLForJAVA.updateEFeature(input,"income.tax_card.deduction_DS_Alimony_yearly",old + calculated_deduction);
	               }else
	               {System.err.println("Unhandeled situation"); return;
	               }
	             }
	          }else
	          {System.err.println("Unhandeled situation"); return;
	          }
	        }
	        OCLForJAVA.iterationExit();
	        }
	        OCLForJAVA.iterationExit();
	        }
	        }else
	        {System.err.println("Unhandeled situation"); return;
	        }
	     }
	   }else
	   {System.err.println("Unhandeled situation"); return;
	   }
	}
	} 
	// End of DS_for_Permanent_Expenses_Alimony simulation code


	// Beginning of DS_for_Permanent_Expenses_Debt simulation code
	public static void DS_for_Permanent_Expenses_Debt (EObject input, String ADName)
	{
	OCLForJAVA.init(ADName,input);
	String tempOCL="";
	tempOCL= "self.from_agent.taxation_year";
	int taxation_year = OCLForJAVA.evaluateInt(input,tempOCL,"taxation_year"); 
	/** TRACEABILITY: Agent type: officer - Question: Which year needs to be checked? - **/ 									
	 tempOCL= "self.incomes->select(inc:Income|inc.year=taxation_year and inc.tax_card->notEmpty() and inc.tax_card.tax_card_type=Tax_Card_Type::Principal)->union(self.incomes->select(inc:Income|inc.year=taxation_year and inc.tax_card->notEmpty() and inc.tax_card.tax_card_type=Tax_Card_Type::Additional)->sortedBy(income_amount*-1))->asOrderedSet()";
	Collection<EObject> incomes = OCLForJAVA.evaluateECollection(input,tempOCL,"incomes","Income","OrderedSet"); 	 
	/** Description: The incomes of a given taxpayer for the studied tax year. - **/	
	for (EObject income: incomes){
	OCLForJAVA.newIteration(new Container("income",income,"incomes",incomes));
	tempOCL= "income.expenses->select(e:Expense| e.year_expense_was_incurred_in= taxation_year and e.expense_purpose= Expense_Purpose::RECURRENT_DEPT)";
	Collection<EObject> declared_debts = OCLForJAVA.evaluateECollection(input,tempOCL,"declared_debts","Expense","OrderedSet"); 	
	/** Description: collection of declared debts. - **/	
	double acc2 = 0;
	for (EObject declared_debt: declared_debts){
	OCLForJAVA.newIteration(new Container("declared_debt",declared_debt,"declared_debts",declared_debts));
	tempOCL= "declared_debt.from_agent.is_eligible_debt";
	boolean is_eligible_debt = OCLForJAVA.evaluateBoolean(input,tempOCL,"is_eligible_debt");
	/** TRACEABILITY: The agent should provide his judgment about the declared debt. An eligible debt should adhere to the following: - * Les arr rages de rentes et de charges permanentes dues en vertu d'une obligation particuli re, notamment d'un contrat r gulier en bonne et due forme, d'une disposition l gale ou d'une d cision de justice, sont d ductibles au titre de d penses sp ciales dans la mesure o ces arr rages ne sont pas en rapport conomique avec des revenus exempt s et ne sont pas consid rer comme d penses d'exploitation ou frais d'obtention. <br>* Toutefois, les arr rages servis des personnes qui, si elles taient dans le besoin, seraient en droit d'apr s les dispositions du code civil, de r clamer des aliments au contribuable, ne constituent des d penses sp ciales qu'au cas o ils sont stipul s l'occasion d'une transmission de biens et qu'ils ne sont pas excessifs par rapport la valeur des biens transmis. <br> - **/						
	if ( (is_eligible_debt) == false){
	acc2=acc2+0;
	}else{
	   if ((is_eligible_debt) == true) {
	   tempOCL= "declared_debt.declared_amount";
	   double amount = OCLForJAVA.evaluateDouble(input,tempOCL,"amount");
	   /** Description: Amount of the recurrent debt.  -  **/							
	   acc2=acc2+amount;
	   }else
	   {acc2=acc2+0;
	   }
	}
	OCLForJAVA.iterationExit();
	}
	double sum_of_eligible_debts = acc2; 	
	double calcuated_deduction = sum_of_eligible_debts * 0.5; 	
	OCLForJAVA.updateEFeature(input,"income.tax_card.deduction_DS_Debt_yearly",calcuated_deduction);
	OCLForJAVA.iterationExit();
	}
	} 
	// End of DS_for_Permanent_Expenses_Debt simulation code

	// Beginning of AEP simulation code
		public static void AEP (EObject input, String ADName)
		{
		OCLForJAVA.init(ADName,input);
		String tempOCL="";
		tempOCL= "self.from_agent.taxation_year";
		int taxation_year = OCLForJAVA.evaluateInt(input,tempOCL,"taxation_year"); 
		/** TRACEABILITY: Specifies when the request was postmarked. - **/ 									
		 tempOCL= "self.getIsTaxedJointly(taxation_year) and self.getIsMaried(taxation_year)"; 
		boolean is_taxed_jointly = OCLForJAVA.evaluateBoolean(input,tempOCL,"is_taxed_jointly"); 

		/** Description: Returns yes if the taxpayer is taxed-jointly;no otherwises - **/	
		if ( (is_taxed_jointly) == false){
		double expected_AEP = 0; 	
		OCLForJAVA.updateEFeature(input,"self.AEP_deduction",expected_AEP);
		}else{
		   if ((is_taxed_jointly) == true) {
		      tempOCL= "self.getSpouse(taxation_year)"; 
		     EObject spouse = OCLForJAVA.evaluateEObject(input,tempOCL,"spouse","Physical_Person"); 	 
		     
		     /** Description: Returns the spouse of the taxpayer -  **/	
		      tempOCL= "if(spouse.oclIsKindOf(Tax_Payer)=false) then true else self.incomes.income_per_year()->max() > spouse.oclAsType(Tax_Payer).incomes.income_per_year()->max() endif"; 
		     boolean has_highest_revenue_in_household = OCLForJAVA.evaluateBoolean(input,tempOCL,"has_highest_revenue_in_household"); 
		     /** Description: Returns yes if the taxpayer has the highest revenue -  within the household, in which he is taxed jointly otherwise no -  **/	
		     if ( (has_highest_revenue_in_household) == true){
		     double expected_AEP = 0; 	
		     OCLForJAVA.updateEFeature(input,"self.AEP_deduction",expected_AEP);
		     }else{
		        if ((has_highest_revenue_in_household) == false) {
		             tempOCL= "self.incomes->exists(income_type.oclIsTypeOf(Trade_and_Business_Income) or income_type.oclIsTypeOf(Agriculture_and_Forestry_Income) or income_type.oclIsTypeOf(Employment_Income) or income_type.oclIsTypeOf(Capital_and_Investments_Income))"; 
		             boolean has_professional_income = OCLForJAVA.evaluateBoolean(input,tempOCL,"has_professional_income"); 
		             /** Description: Returns yes if the taxpayer has a professional income; otherwise no -  **/	
		             tempOCL= "if(spouse.oclIsKindOf(Tax_Payer)=false) then false else spouse.oclAsType(Tax_Payer).incomes->exists(income_type.oclIsTypeOf(Trade_and_Business_Income) or income_type.oclIsTypeOf(Agriculture_and_Forestry_Income) or income_type.oclIsTypeOf(Employment_Income) or income_type.oclIsTypeOf(Capital_and_Investments_Income)) endif"; 
		             boolean spouse_has_professional_income = OCLForJAVA.evaluateBoolean(input,tempOCL,"spouse_has_professional_income"); 

		             /** Description: Returns yes if the spouse  has a professional income; otherwise no -  **/	
		             tempOCL= "if(spouse.oclIsKindOf(Tax_Payer)=false) then false else spouse.oclAsType(Tax_Payer).incomes.details->any(is_contributing_CNS.oclIsUndefined()=false).is_contributing_CNS=true endif"; 
		             boolean is_spouse_affiliated_to_social_security = OCLForJAVA.evaluateBoolean(input,tempOCL,"is_spouse_affiliated_to_social_security"); 

		             /** Description: Returns yes if the taxpayer is affiliated to public or private social security; otherwise no -  **/	
		             tempOCL= "self.incomes.details->any(is_contributing_CNS.oclIsUndefined()=false).is_contributing_CNS=true"; 
		             boolean is_taxpayer_affiliated_to_social_security = OCLForJAVA.evaluateBoolean(input,tempOCL,"is_taxpayer_affiliated_to_social_security"); 
		             /** Description: Returns yes if the taxpayer is affiliated to public or private social security; otherwise no -  **/	
		             if ( (has_professional_income && spouse_has_professional_income && is_taxpayer_affiliated_to_social_security && is_spouse_affiliated_to_social_security) == true){
		             double AEP_flat_rate = 4500;
		             /** TRACEABILITY: Source: Art. 129b of the Luxembourg's Income Tax Law, 2013   - AEP_flat_rate =4500 euros per year - 375 euros par month - **/ 								
		             double expected_AEP = AEP_flat_rate; 	

		             OCLForJAVA.updateEFeature(input,"self.AEP_deduction",expected_AEP);
		             }else{
		               if ((has_professional_income && spouse_has_professional_income && is_taxpayer_affiliated_to_social_security && is_spouse_affiliated_to_social_security) == false) {
		                       tempOCL= "self.incomes->exists(income_type.oclIsTypeOf(Trade_and_Business_Income) or income_type.oclIsTypeOf(Capital_and_Investments_Income))"; 
		                       boolean has_commercial_income = OCLForJAVA.evaluateBoolean(input,tempOCL,"has_commercial_income"); 
		                       /** Description: Returns yes if the taxpayer has a commercial income; otherwise no -  **/	
		                       tempOCL= "if(spouse.oclIsKindOf(Tax_Payer)=false)then false else self.is_assisting_spouse endif"; 
		                       boolean taxpayer_is_assisting_spouse = OCLForJAVA.evaluateBoolean(input,tempOCL,"taxpayer_is_assisting_spouse"); 
		                       /** Description: Returns yes if the taxpayer is assisting his spouse in its commercial income; otherwise no -  **/	
		                       tempOCL= "if(spouse.oclIsKindOf(Tax_Payer)=false)then false else spouse.is_assisting_spouse endif"; 
		                       boolean spouse_is_assisting_taxpayer = OCLForJAVA.evaluateBoolean(input,tempOCL,"spouse_is_assisting_taxpayer"); 
		                       /** Description: Returns yes if the spouse is assisting the taxpayer in its commercial income; otherwise no -  **/	
		                       tempOCL= "if(spouse.oclIsKindOf(Tax_Payer)=false)then false else spouse.oclAsType(Tax_Payer).incomes->exists(income_type.oclIsTypeOf(Trade_and_Business_Income) or income_type.oclIsTypeOf(Capital_and_Investments_Income)) endif"; 
		                       boolean spouse_has_commercial_income = OCLForJAVA.evaluateBoolean(input,tempOCL,"spouse_has_commercial_income"); 
		                       /** Description: Returns yes if the spouse has a commercial income; otherwise no -  **/	
		                       if ( ((has_commercial_income && spouse_is_assisting_taxpayer) || (spouse_has_commercial_income && taxpayer_is_assisting_spouse)) == true){
		                       double AEP_flat_rate = 4500;
		                       /** TRACEABILITY: Source: Art. 129b of the Luxembourg's Income Tax Law, 2013   - AEP_flat_rate =4500 euros per year - 375 euros par month - **/ 								
		                       double expected_AEP = AEP_flat_rate; 	
		                       OCLForJAVA.updateEFeature(input,"self.AEP_deduction",expected_AEP);
		                       }else{
		                         if (((has_commercial_income && spouse_is_assisting_taxpayer) || (spouse_has_commercial_income && taxpayer_is_assisting_spouse)) == false) {
		                                    tempOCL= "if(spouse.oclIsKindOf(Tax_Payer)=false)then 0 else spouse.oclAsType(Tax_Payer).incomes.getDurationInyears()->max() endif"; 
		                                   int spouse_pension_period = OCLForJAVA.evaluateInt(input,tempOCL,"spouse_pension_period"); 
		                                   /** Description: Returns -1 if the spouse does not have any pension;otherwise returns the number of years for which the spouse has been receiving  at least one pension  -  **/	
		                                    tempOCL= "self.incomes.getDurationInyears()->max()"; 
		                                   int taxpayer_pension_period = OCLForJAVA.evaluateInt(input,tempOCL,"taxpayer_pension_period"); 
		                                   /** Description: Returns -1 if the taxpayer does not have any pension; otherwise returns the number of years for which the taxpayer has been receiving  at least one pension  -  **/	
		                                   if ( ((has_professional_income && (spouse_pension_period <= 3 && spouse_pension_period >0)) || (spouse_has_professional_income && (taxpayer_pension_period <= 3 && taxpayer_pension_period > 0))) == true){
		                                   double AEP_flat_rate = 4500;
		                                   /** TRACEABILITY: Source: Art. 129b of the Luxembourg's Income Tax Law, 2013   - AEP_flat_rate =4500 euros per year - 375 euros par month - **/ 								
		                                   double expected_AEP = AEP_flat_rate; 	
		                                   OCLForJAVA.updateEFeature(input,"self.AEP_deduction",expected_AEP);
		                                   }else{
		                                      if (((has_professional_income && (spouse_pension_period <= 3 && spouse_pension_period >0)) || (spouse_has_professional_income && (taxpayer_pension_period <= 3 && taxpayer_pension_period > 0))) == false) {
		                                      double expected_AEP = 0; 	
		                                      OCLForJAVA.updateEFeature(input,"self.AEP_deduction",expected_AEP);
		                                      }else
		                                      {System.err.println("Unhandeled situation"); return;
		                                      }
		                                   }
		                         }else
		                         {System.err.println("Unhandeled situation"); return;
		                         }
		                       }
		               }else
		               {System.err.println("Unhandeled situation"); return;
		               }
		             }
		        }else
		        {System.err.println("Unhandeled situation"); return;
		        }
		     }
		   }else
		   {System.err.println("Unhandeled situation"); return;
		   }
		}
		} 
		// End of AEP simulation code

		
}
