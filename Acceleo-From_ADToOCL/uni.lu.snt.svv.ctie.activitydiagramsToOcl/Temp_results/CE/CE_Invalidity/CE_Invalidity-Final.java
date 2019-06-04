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
