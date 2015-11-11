function v = rprintNStars(n)

%v = [];

if n == 1
    v = '*';
else    
    v = ['*' rprintNStars(n-1)]; 

end

