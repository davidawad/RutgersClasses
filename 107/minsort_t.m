function [telapsed] = minsort_t( v )
    n=length(v);    
    vs = [] ; 
    
    tstart = tic; 
    for i=1:n
        [mn, j] = min(v);   
        vs = [vs mn]; 
        v(j) = Inf;  
    end
    
    telapsed = toc(tstart)
end