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
