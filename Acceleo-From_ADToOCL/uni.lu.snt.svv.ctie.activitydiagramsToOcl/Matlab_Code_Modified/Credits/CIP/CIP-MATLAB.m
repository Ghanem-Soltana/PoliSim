function CIP
if (taxation_year>=2009) == false 
disp('Program exit');
else
   if (taxation_year>=2009) == true 
   for i_1=1:length(incomes)
   income=incomes(i_1);
   is_income_subject_to_withholding=income.is_income_subject_to_withholding ;% We will see later
   if (is_income_subject_to_withholding) == true 
     is_income_pension=income.is_income_pension ;% We will see later
     if (is_income_pension) == false 
          is_income_ofType_rental=income.is_income_ofType_rental ;% We will see later
          if (is_income_ofType_rental) == false 
          expected_CIP = 0; %CentralBuffer
          income.CIP_yearly=expected_CIP; % Update
          else
             if (is_income_ofType_rental) == true 
                    is_income_periodic =income.is_income_periodic ;% We will see later
                    if (is_income_periodic ) == false 
                    expected_CIP = 0; %CentralBuffer
                    income.CIP_yearly=expected_CIP; % Update
                    else
                       if (is_income_periodic ) == true 
                                 if (credit_already_attributed_to_another_pension_or_rental_income) == false 
                                             if (income_per_month >= CIP_minimum_income_month) == true 
                                             expected_CIP = flat_rate_CIP_yearly * prorata_period; %CentralBuffer
                                             income.CIP_yearly=expected_CIP; % Update
                                             else 
                                                if (income_per_month >= CIP_minimum_income_month) == false 
                                                               if (income_per_year >= CIP_minimum_income_year) == true 
                                                               expected_CIP = flat_rate_CIP_yearly * prorata_period; %CentralBuffer
                                                               income.CIP_yearly=expected_CIP; % Update
                                                               else
                                                                 if (income_per_year >= CIP_minimum_income_year) == false 
                                                                 expected_CIP = 0; %CentralBuffer
                                                                 income.CIP_yearly=expected_CIP; % Update
                                                                 else
                                                                 disp('Unhandled situation');
                                                                 end
                                                               end
                                             else
                                                if (income_per_month >= CIP_minimum_income_month) == false 
                                                expected_CIP = 0; %CentralBuffer
                                                income.CIP_yearly=expected_CIP; % Update
                                                else
                                                disp('Unhandled situation');
                                                end
                                                end
                                             end
                                 else
                                   if (credit_already_attributed_to_another_pension_or_rental_income) == true 
                                   expected_CIP = 0; %CentralBuffer
                                   income.CIP_yearly=expected_CIP; % Update
                                   else
                                   disp('Unhandled situation');
                                   end
                                 end
                       else
                       disp('Unhandled situation');
                       end
                    end
             else
             disp('Unhandled situation');
             end
          end
     else
        if (is_income_pension) == true 
             if (credit_already_attributed_to_another_pension_or_rental_income) == false 
                    if (income_per_month >= CIP_minimum_income_month) == true 
                    expected_CIP = flat_rate_CIP_yearly * prorata_period; %CentralBuffer
                    income.CIP_yearly=expected_CIP; % Update
                    else 
                       if (income_per_month >= CIP_minimum_income_month) == false 
                                 if (income_per_year >= CIP_minimum_income_year) == true 
                                 expected_CIP = flat_rate_CIP_yearly * prorata_period; %CentralBuffer
                                 income.CIP_yearly=expected_CIP; % Update
                                 else
                                   if (income_per_year >= CIP_minimum_income_year) == false 
                                   expected_CIP = 0; %CentralBuffer
                                   income.CIP_yearly=expected_CIP; % Update
                                   else
                                   disp('Unhandled situation');
                                   end
                                 end
                    else
                       if (income_per_month >= CIP_minimum_income_month) == false 
                       expected_CIP = 0; %CentralBuffer
                       income.CIP_yearly=expected_CIP; % Update
                       else
                       disp('Unhandled situation');
                       end
                       end
                    end
             else
               if (credit_already_attributed_to_another_pension_or_rental_income) == true 
               expected_CIP = 0; %CentralBuffer
               income.CIP_yearly=expected_CIP; % Update
               else
               disp('Unhandled situation');
               end
             end
        else
        disp('Unhandled situation');
        end
     end
   end
   end
   else
   disp('Unhandled situation');
   end
end
end
