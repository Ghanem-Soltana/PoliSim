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
     tempOCL= "income.income_type.oclIsKindOf(Employment_Income)";
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
