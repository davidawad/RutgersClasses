function[ telapsed ] = insort( v )
n=length(v);

tstart = tic;

for i=2:n 
    temp=v(i);
    for j = i-1:-1:1
        if temp<v(j)   
            v(j+1)=v(j); 
            v(j)=temp;
        end
    end
end

telapsed = toc(tstart) 

end