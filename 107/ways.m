function ways_num = ways(m, c)
if c == m
    ways_num = 1;
    
elseif c==1
    ways_num = m;
    
else
    ways_num = ways(m-1, c-1) + ways(m-1, c);


end

