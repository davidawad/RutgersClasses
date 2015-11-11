function [vs] = minsort( v )
    n=length(v);    
    vs = [] ; 
    for i=1:n
        [mn, j] = min(v);   
        vs = [vs mn]; 
        v(j) = Inf;  
    end
end