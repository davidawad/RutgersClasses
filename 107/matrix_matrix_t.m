function[telapsed] = matrix_matrix_t(n)
a = rand(n);
b = rand(n);

tstart = tic; 

for j=1:n
    for i=1:n
        c(i,j) = 0;
        for k=1:n
            c(i,j) = c(i,j) + a(i,k) * b(k,j);
        end
    end
end
telapsed = toc(tstart); 
end
