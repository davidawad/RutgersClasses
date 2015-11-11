function avgNeg = avgNeg(numbers)
% avgNeg takes in a listof numbers and takes the average of the negatve
% numbers

% sum is the sum of available negative numbers
sum = 0;

% n is the number of negative numbers
n = 0;

    for i = 1:length(numbers)

        if numbers(i) < 0
           n = n+1;
           sum = sum + numbers(i);
        end


    end
    
    
avgNeg = (sum/n);
    
end
