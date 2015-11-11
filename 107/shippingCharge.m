function shippingCharge = shippingCharge(cdsBought, promocode)

% SHIPPINGCHARGE
% calculates the shipping charges
% and are writing a program to calculate the shipping charges for an order.
% Suppose the charges depend on the number of CD's in the order.  
% The charge for the first 5 CD is $2 each, and the charge for the 6th 
% through the 10th cd is $1 each, and there is no shipping charge for the 11th on.  
% So, for example, the charge for 7 CDs is $10 for the first 5 plus $1 each for the 6th and 7th for a total of $12.  The shipping charge for 10 or more CD's is $15.  

% Write a function shippingCharge(cdsBought, promocode) where cdsBought is the number of CD's in this order, and promocode is a special code number.  If the promocode is 107, shipping is free so the function shippingCharge should return 0.  Otherwise it should return the charge calculated as described above.


if cdsBought < 5
    shippingCharge = 2 * cdsBought;  
    
else % cdsBought > 5
    if cdsBought <= 10
        shippingCharge = 10 + (cdsBought - 5);
    
    else % cds bought greater than 10, no additional shipping information
        shippingCharge = 15;
        
    end
    
end

if promocode == 107
    shippingCharge = 0; 
   
    
end

