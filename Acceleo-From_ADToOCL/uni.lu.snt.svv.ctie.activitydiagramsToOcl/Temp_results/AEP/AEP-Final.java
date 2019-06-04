// Beginning of AEP simulation code
public static void AEP (EObject input, String ADName)
{
OCLForJAVA.init(ADName,input);
String tempOCL="";
tempOCL= "self.from_agent.taxation_year";
int taxation_year = OCLForJAVA.evaluateInt(input,tempOCL,"taxation_year"); 
/** TRACEABILITY: Specifies when the request was postmarked. - **/ 									
 tempOCL= "self.getSpouse(taxation_year)"; 
EObject spouse = OCLForJAVA.evaluateEObject(input,tempOCL,"spouse","Physical_Person"); 	 
/** Description: Returns the spouse of the taxpayer - **/	
 tempOCL= "if(spouse.oclIsUndefined()) then false else if(spouse.oclIsKindOf(Tax_Payer)) then self.AEP_deduction + spouse.oclAsType(Tax_Payer).AEP_deduction >0 else false endif endif"; 
boolean AEP_already_granted_to_household = OCLForJAVA.evaluateBoolean(input,tempOCL,"AEP_already_granted_to_household"); 
/** Description: Returns yes if the taxpayer has the highest revenue - within the household, in which he is taxed jointly otherwise no - **/	
if ( (AEP_already_granted_to_household) == false){
   tempOCL= "self.getIsTaxedJointly(taxation_year) and self.getIsMaried(taxation_year)"; 
   boolean is_taxed_jointly = OCLForJAVA.evaluateBoolean(input,tempOCL,"is_taxed_jointly"); 
   /** Description: Returns yes if the taxpayer is taxed-jointly;no otherwises -  **/	
   if ( (is_taxed_jointly) == false){
   double expected_AEP = 0; 	
   tempOCL= "let temp:Tax_Payer = if(spouse.oclIsUndefined()) then self else if(spouse.oclIsKindOf(Tax_Payer)) then if(self.incomes->any(true).income_amount*12> spouse.oclAsType(Tax_Payer).incomes->any(true).income_amount*12) then self else spouse.oclAsType(Tax_Payer) endif else self endif endif in if(temp.oclIsUndefined()) then self else temp endif"; 
   EObject theOne = OCLForJAVA.evaluateEObject(input,tempOCL,"theOne","Tax_Payer"); 	 
   /** Description: -  **/	
   OCLForJAVA.updateEFeature(input,"theOne.AEP_deduction",expected_AEP);
   }else{
     if ((is_taxed_jointly) == true) {
           tempOCL= "self.incomes->exists(income_type.oclIsTypeOf(Trade_and_Business_Income) or income_type.oclIsTypeOf(Agriculture_and_Forestry_Income) or income_type.oclIsTypeOf(Employment_Income) or income_type.oclIsTypeOf(Capital_and_Investments_Income))"; 
          boolean has_professional_income = OCLForJAVA.evaluateBoolean(input,tempOCL,"has_professional_income"); 
          /** Description: Returns yes if the taxpayer has a professional income; otherwise no -  **/	
           tempOCL= "if(spouse.oclIsKindOf(Tax_Payer)=false)then false else spouse.oclAsType(Tax_Payer).incomes->exists(income_type.oclIsTypeOf(Trade_and_Business_Income) or income_type.oclIsTypeOf(Agriculture_and_Forestry_Income) or income_type.oclIsTypeOf(Employment_Income) or income_type.oclIsTypeOf(Capital_and_Investments_Income)) endif"; 
          boolean spouse_has_professional_income = OCLForJAVA.evaluateBoolean(input,tempOCL,"spouse_has_professional_income"); 
          /** Description: Returns yes if the spouse  has a professional income; otherwise no -  **/	
           tempOCL= "if(spouse.oclIsKindOf(Tax_Payer)=false)then false else spouse.oclAsType(Tax_Payer).incomes.details->any(is_contributing_CNS.oclIsUndefined()=false).is_contributing_CNS=true endif"; 
          boolean is_spouse_affiliated_to_social_security = OCLForJAVA.evaluateBoolean(input,tempOCL,"is_spouse_affiliated_to_social_security"); 
          /** Description: Returns yes if the taxpayer is affiliated to public or private social security; otherwise no -  **/	
           tempOCL= "self.incomes.details->any(is_contributing_CNS.oclIsUndefined()=false).is_contributing_CNS=true"; 
          boolean is_taxpayer_affiliated_to_social_security = OCLForJAVA.evaluateBoolean(input,tempOCL,"is_taxpayer_affiliated_to_social_security"); 
          /** Description: Returns yes if the taxpayer is affiliated to public or private social security; otherwise no -  **/	
          if ( (has_professional_income && spouse_has_professional_income && is_taxpayer_affiliated_to_social_security && is_spouse_affiliated_to_social_security) == false){
                  tempOCL= "self.incomes->exists(income_type.oclIsTypeOf(Trade_and_Business_Income) or income_type.oclIsTypeOf(Capital_and_Investments_Income))"; 
                  boolean has_commercial_income = OCLForJAVA.evaluateBoolean(input,tempOCL,"has_commercial_income"); 
                  /** Description: Returns yes if the taxpayer has a commercial income; otherwise no -  **/	
                  tempOCL= "true"; 
                  boolean has_independent_income = OCLForJAVA.evaluateBoolean(input,tempOCL,"has_independent_income"); 
                  /** Description: -  **/	
                  tempOCL= "if(spouse.oclIsKindOf(Tax_Payer)=false)then false else spouse.is_assisting_spouse endif"; 
                  boolean spouse_is_assisting_taxpayer = OCLForJAVA.evaluateBoolean(input,tempOCL,"spouse_is_assisting_taxpayer"); 
                  /** Description: Returns yes if the spouse is assisting the taxpayer in its commercial income; otherwise no -  **/	
                  if ( (has_commercial_income || has_independent_income && spouse_is_assisting_taxpayer) == false){
                            tempOCL= "true"; 
                            boolean AEP_has_been_requested = OCLForJAVA.evaluateBoolean(input,tempOCL,"AEP_has_been_requested"); 
                            /** Description: -  **/	
                            if ( (AEP_has_been_requested) == false){
                            double expected_AEP = 0; 	
                            tempOCL= "let temp:Tax_Payer = if(spouse.oclIsUndefined()) then self else if(spouse.oclIsKindOf(Tax_Payer)) then if(self.incomes->any(true).income_amount*12> spouse.oclAsType(Tax_Payer).incomes->any(true).income_amount*12) then self else spouse.oclAsType(Tax_Payer) endif else self endif endif in if(temp.oclIsUndefined()) then self else temp endif"; 
                            EObject theOne = OCLForJAVA.evaluateEObject(input,tempOCL,"theOne","Tax_Payer"); 	 
                            /** Description: -  **/	
                            OCLForJAVA.updateEFeature(input,"theOne.AEP_deduction",expected_AEP);
                            }else{
                              if ((AEP_has_been_requested) == true) {
                                           tempOCL= "if(spouse.oclIsKindOf(Tax_Payer)=false)then 0 else spouse.oclAsType(Tax_Payer).incomes.getDurationInyears()->max() endif"; 
                                           int spouse_pension_period = OCLForJAVA.evaluateInt(input,tempOCL,"spouse_pension_period"); 
                                           /** Description: Returns -1 if the spouse does not have any pension;otherwise returns the number of years for which the spouse has been receiving  at least one pension  -  **/	
                                           if ( ((has_professional_income && (spouse_pension_period <= 3 && spouse_pension_period >0))) == false){
                                           double expected_AEP = 0; 	
                                           tempOCL= "let temp:Tax_Payer = if(spouse.oclIsUndefined()) then self else if(spouse.oclIsKindOf(Tax_Payer)) then if(self.incomes->any(true).income_amount*12> spouse.oclAsType(Tax_Payer).incomes->any(true).income_amount*12) then self else spouse.oclAsType(Tax_Payer) endif else self endif endif in if(temp.oclIsUndefined()) then self else temp endif"; 
                                           EObject theOne = OCLForJAVA.evaluateEObject(input,tempOCL,"theOne","Tax_Payer"); 	 
                                           /** Description: -  **/	
                                           OCLForJAVA.updateEFeature(input,"theOne.AEP_deduction",expected_AEP);
                                           }else{
                                             if (((has_professional_income && (spouse_pension_period <= 3 && spouse_pension_period >0))) == true) {
                                                             tempOCL= "if(spouse.oclIsKindOf(Tax_Payer)) then Set{self.incomes->any(true).income_amount*12, spouse.oclAsType(Tax_Payer).incomes->any(true).income_amount*12}->min() else self.incomes->any(true).income_amount*12 endif"; 
                                                            double couple_lowest_annual_income_eligible_for_AEP = OCLForJAVA.evaluateDouble(input,tempOCL,"couple_lowest_annual_income_eligible_for_AEP"); 
                                                            /** Description: -  **/	
                                                            if ( (couple_lowest_annual_income_eligible_for_AEP >= 4500) == true){
                                                            double AEP_flat_rate = 4500;
                                                            /** TRACEABILITY: Source: Art. 129b of the Luxembourg's Income Tax Law, 2013   - AEP_flat_rate =4500 euros per year - 375 euros par month - **/ 								
                                                            double expected_AEP = AEP_flat_rate; 	
                                                             tempOCL= "let temp:Tax_Payer = if(spouse.oclIsUndefined()) then self else if(spouse.oclIsKindOf(Tax_Payer)) then if(self.incomes->any(true).income_amount*12> spouse.oclAsType(Tax_Payer).incomes->any(true).income_amount*12) then self else spouse.oclAsType(Tax_Payer) endif else self endif endif in if(temp.oclIsUndefined()) then self else temp endif"; 
                                                            EObject theOne = OCLForJAVA.evaluateEObject(input,tempOCL,"theOne","Tax_Payer"); 	 
                                                            /** Description: -  **/	
                                                            OCLForJAVA.updateEFeature(input,"theOne.AEP_deduction",expected_AEP);
                                                            }else{
                                                               if ((couple_lowest_annual_income_eligible_for_AEP >= 4500) == false) {
                                                               double expected_AEP = couple_lowest_annual_income_eligible_for_AEP; 	
                                                               tempOCL= "let temp:Tax_Payer = if(spouse.oclIsUndefined()) then self else if(spouse.oclIsKindOf(Tax_Payer)) then if(self.incomes->any(true).income_amount*12> spouse.oclAsType(Tax_Payer).incomes->any(true).income_amount*12) then self else spouse.oclAsType(Tax_Payer) endif else self endif endif in if(temp.oclIsUndefined()) then self else temp endif"; 
                                                               EObject theOne = OCLForJAVA.evaluateEObject(input,tempOCL,"theOne","Tax_Payer"); 	 
                                                               /** Description: -  **/	
                                                               OCLForJAVA.updateEFeature(input,"theOne.AEP_deduction",expected_AEP);
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
                    if ((has_commercial_income || has_independent_income && spouse_is_assisting_taxpayer) == true) {
                               tempOCL= "if(spouse.oclIsKindOf(Tax_Payer)) then Set{self.incomes->any(true).income_amount*12, spouse.oclAsType(Tax_Payer).incomes->any(true).income_amount*12}->min() else self.incomes->any(true).income_amount*12 endif"; 
                              double couple_lowest_annual_income_eligible_for_AEP = OCLForJAVA.evaluateDouble(input,tempOCL,"couple_lowest_annual_income_eligible_for_AEP"); 
                              /** Description: -  **/	
                              if ( (couple_lowest_annual_income_eligible_for_AEP >= 4500) == true){
                              double AEP_flat_rate = 4500;
                              /** TRACEABILITY: Source: Art. 129b of the Luxembourg's Income Tax Law, 2013   - AEP_flat_rate =4500 euros per year - 375 euros par month - **/ 								
                              double expected_AEP = AEP_flat_rate; 	
                               tempOCL= "let temp:Tax_Payer = if(spouse.oclIsUndefined()) then self else if(spouse.oclIsKindOf(Tax_Payer)) then if(self.incomes->any(true).income_amount*12> spouse.oclAsType(Tax_Payer).incomes->any(true).income_amount*12) then self else spouse.oclAsType(Tax_Payer) endif else self endif endif in if(temp.oclIsUndefined()) then self else temp endif"; 
                              EObject theOne = OCLForJAVA.evaluateEObject(input,tempOCL,"theOne","Tax_Payer"); 	 
                              /** Description: -  **/	
                              OCLForJAVA.updateEFeature(input,"theOne.AEP_deduction",expected_AEP);
                              }else{
                                 if ((couple_lowest_annual_income_eligible_for_AEP >= 4500) == false) {
                                 double expected_AEP = couple_lowest_annual_income_eligible_for_AEP; 	
                                 tempOCL= "let temp:Tax_Payer = if(spouse.oclIsUndefined()) then self else if(spouse.oclIsKindOf(Tax_Payer)) then if(self.incomes->any(true).income_amount*12> spouse.oclAsType(Tax_Payer).incomes->any(true).income_amount*12) then self else spouse.oclAsType(Tax_Payer) endif else self endif endif in if(temp.oclIsUndefined()) then self else temp endif"; 
                                 EObject theOne = OCLForJAVA.evaluateEObject(input,tempOCL,"theOne","Tax_Payer"); 	 
                                 /** Description: -  **/	
                                 OCLForJAVA.updateEFeature(input,"theOne.AEP_deduction",expected_AEP);
                                 }else
                                 {System.err.println("Unhandeled situation"); return;
                                 }
                              }
                    }else
                    {System.err.println("Unhandeled situation"); return;
                    }
                  }
          }else{
             if ((has_professional_income && spouse_has_professional_income && is_taxpayer_affiliated_to_social_security && is_spouse_affiliated_to_social_security) == true) {
                     tempOCL= "if(spouse.oclIsKindOf(Tax_Payer)) then Set{self.incomes->any(true).income_amount*12, spouse.oclAsType(Tax_Payer).incomes->any(true).income_amount*12}->min() else self.incomes->any(true).income_amount*12 endif"; 
                    double couple_lowest_annual_income_eligible_for_AEP = OCLForJAVA.evaluateDouble(input,tempOCL,"couple_lowest_annual_income_eligible_for_AEP"); 
                    /** Description: -  **/	
                    if ( (couple_lowest_annual_income_eligible_for_AEP >= 4500) == true){
                    double AEP_flat_rate = 4500;
                    /** TRACEABILITY: Source: Art. 129b of the Luxembourg's Income Tax Law, 2013   - AEP_flat_rate =4500 euros per year - 375 euros par month - **/ 								
                    double expected_AEP = AEP_flat_rate; 	
                     tempOCL= "let temp:Tax_Payer = if(spouse.oclIsUndefined()) then self else if(spouse.oclIsKindOf(Tax_Payer)) then if(self.incomes->any(true).income_amount*12> spouse.oclAsType(Tax_Payer).incomes->any(true).income_amount*12) then self else spouse.oclAsType(Tax_Payer) endif else self endif endif in if(temp.oclIsUndefined()) then self else temp endif"; 
                    EObject theOne = OCLForJAVA.evaluateEObject(input,tempOCL,"theOne","Tax_Payer"); 	 
                    /** Description: -  **/	
                    OCLForJAVA.updateEFeature(input,"theOne.AEP_deduction",expected_AEP);
                    }else{
                       if ((couple_lowest_annual_income_eligible_for_AEP >= 4500) == false) {
                       double expected_AEP = couple_lowest_annual_income_eligible_for_AEP; 	
                       tempOCL= "let temp:Tax_Payer = if(spouse.oclIsUndefined()) then self else if(spouse.oclIsKindOf(Tax_Payer)) then if(self.incomes->any(true).income_amount*12> spouse.oclAsType(Tax_Payer).incomes->any(true).income_amount*12) then self else spouse.oclAsType(Tax_Payer) endif else self endif endif in if(temp.oclIsUndefined()) then self else temp endif"; 
                       EObject theOne = OCLForJAVA.evaluateEObject(input,tempOCL,"theOne","Tax_Payer"); 	 
                       /** Description: -  **/	
                       OCLForJAVA.updateEFeature(input,"theOne.AEP_deduction",expected_AEP);
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
   if ((AEP_already_granted_to_household) == true) {
   return;
   }else
   {System.err.println("Unhandeled situation"); return;
   }
}
} 
// End of AEP simulation code
