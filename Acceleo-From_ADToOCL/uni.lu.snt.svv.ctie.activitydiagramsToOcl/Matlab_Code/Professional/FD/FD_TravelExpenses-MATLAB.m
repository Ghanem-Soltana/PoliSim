function FD_TravelExpenses
for i_1=1:length(incomes)
income=incomes(i_1);
is_income_subject_to_withholding=income.is_income_subject_to_withholding ;% We will see later
if (is_income_subject_to_withholding) == true 
 is_income_eligible=income.is_income_eligible ;% We will see later
 if (is_income_eligible) == false 
 calculated_FD = 0; %CentralBuffer
 flat_maximum_FD = 2574 ; % From law 
 if (calculated_FD >= (flat_maximum_FD - granted_FD_for_other_incomes)) == true 
 expected_FD = flat_maximum_FD - (granted_FD_for_other_incomes); %CentralBuffer
 income.FD_yearly=expected_FD; % Update
 else
 if (calculated_FD >= (flat_maximum_FD - granted_FD_for_other_incomes)) == false 
 expected_FD = calculated_FD; %CentralBuffer
 income.FD_yearly=expected_FD; % Update
 else
 disp('Unhandled situation');
 end
 end
 else
 if (is_income_eligible) == true 
 acc_2=0; %INIT
 distances= income.distances;% We will see later
 for i_2=1:length(distances)
 distance=distances(i_2);
 has_the_taxpayer_worked_for_the_treated_month=distance.has_the_taxpayer_worked_for_the_treated_month ;% We will see later
 if (has_the_taxpayer_worked_for_the_treated_month) == true 
 distance_reference=distance.distance_reference ;% We will see later
 distance_value=distance.distance_value ;% We will see later
 if (distance_value>distance_reference) == true 
 distance_reference=distance_value; % Update
 minimum_distance_in_units = 4 ; % From law 
 if (distance_reference> minimum_distance_in_units) == false 
 acc_2=acc_2 + 0;
 else
 if (distance_reference> minimum_distance_in_units) == true 
  maximum_distance_in_units = 30 ; % From law 
  if (distance_reference > maximum_distance_in_units) == true 
  flat_rate_per_unit = 99 ; % From law 
  full_time_equivalent=distance.full_time_equivalent ;% We will see later
  acc_2=acc_2 + flat_rate_per_unit * maximum_distance_in_units * full_time_equivalent;
  else
  if (distance_reference > maximum_distance_in_units) == false 
  flat_rate_per_unit = 99 ; % From law 
  full_time_equivalent=distance.full_time_equivalent ;% We will see later
  acc_2=acc_2 + flat_rate_per_unit * (distance_reference-minimum_distance_in_units) * full_time_equivalent;
  else
  disp('Unhandled situation');
  end
  end
 else
 disp('Unhandled situation');
 end
 end
 else
 if (distance_value>distance_reference) == false 
 minimum_distance_in_units = 4 ; % From law 
 if (distance_reference> minimum_distance_in_units) == false 
 acc_2=acc_2 + 0;
 else
  if (distance_reference> minimum_distance_in_units) == true 
  maximum_distance_in_units = 30 ; % From law 
  if (distance_reference > maximum_distance_in_units) == true 
  flat_rate_per_unit = 99 ; % From law 
  full_time_equivalent=distance.full_time_equivalent ;% We will see later
  acc_2=acc_2 + flat_rate_per_unit * maximum_distance_in_units * full_time_equivalent;
  else
  if (distance_reference > maximum_distance_in_units) == false 
  flat_rate_per_unit = 99 ; % From law 
  full_time_equivalent=distance.full_time_equivalent ;% We will see later
  acc_2=acc_2 + flat_rate_per_unit * (distance_reference-minimum_distance_in_units) * full_time_equivalent;
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
 if (has_the_taxpayer_worked_for_the_treated_month) == false 
 acc_2=acc_2 + 0;
 else
 disp('Unhandled situation');
 end
 end
 end
 calculated_FD=acc_2;
 else
 disp('Unhandled situation');
 end
 end
else
 if (is_income_subject_to_withholding) == false 
 calculated_FD = 0; %CentralBuffer
 flat_maximum_FD = 2574 ; % From law 
 if (calculated_FD >= (flat_maximum_FD - granted_FD_for_other_incomes)) == true 
 expected_FD = flat_maximum_FD - (granted_FD_for_other_incomes); %CentralBuffer
 income.FD_yearly=expected_FD; % Update
 else
 if (calculated_FD >= (flat_maximum_FD - granted_FD_for_other_incomes)) == false 
 expected_FD = calculated_FD; %CentralBuffer
 income.FD_yearly=expected_FD; % Update
 else
 disp('Unhandled situation');
 end
 end
 else
 disp('Unhandled situation');
 end
end
end
flat_maximum_FD = 2574 ; % From law 
if (calculated_FD >= (flat_maximum_FD - granted_FD_for_other_incomes)) == true 
expected_FD = flat_maximum_FD - (granted_FD_for_other_incomes); %CentralBuffer
income.FD_yearly=expected_FD; % Update
else
 if (calculated_FD >= (flat_maximum_FD - granted_FD_for_other_incomes)) == false 
 expected_FD = calculated_FD; %CentralBuffer
 income.FD_yearly=expected_FD; % Update
 else
 disp('Unhandled situation');
 end
end
end
