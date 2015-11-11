function amount = amount(age, weight)
% AMOUNT returns amount (in milligrams) 

% If the child is 2 years old or more and 40 pounds or more, 
% the amount should be 0.25 milligrams per pound of weight.  
% E.g. for 50 pounds the amount should be 50 x 0.25, i.e. 12.5 milligrams.
if age < 2 && weight < 40
    amount = 0;

elseif age >= 2 || weight >= 40
    amount = 0.25 * weight;
    

end

