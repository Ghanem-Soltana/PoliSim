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
