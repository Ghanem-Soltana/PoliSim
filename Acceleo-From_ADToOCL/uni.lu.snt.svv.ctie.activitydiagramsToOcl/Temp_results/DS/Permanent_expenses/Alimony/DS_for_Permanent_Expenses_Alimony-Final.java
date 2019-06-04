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
