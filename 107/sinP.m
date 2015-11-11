function sinP = sinP(x, kmax)
%SinP 
sum = 0.00 ; 

for k = 0:kmax
    % add a_k and x to the power of 2k + 1
    temp = 0.00; 
    
    % variable to represent a_k
    ak = ((-1)^k)*(pi^(2*k+1));
    ak = ak / factorial(2*k+1);
    
    sum = sum + (ak*(x^(2*k+1)));
    
end 

sinP = sum; 

end

