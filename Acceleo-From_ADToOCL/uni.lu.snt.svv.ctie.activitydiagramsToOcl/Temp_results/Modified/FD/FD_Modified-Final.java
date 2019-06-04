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
