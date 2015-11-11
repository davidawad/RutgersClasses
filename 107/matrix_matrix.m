function[ c,telapsed] = matrix_matrix(a,b)
[n m]=size(a);

for j=1:n
    for i=1:n
        c(i,j) = 0;
        for k=1:n
            c(i,j) = c(i,j) + a(i,k) * b(k,j);
        end
    end
end

end
