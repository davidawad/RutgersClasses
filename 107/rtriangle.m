function rtriangle(n)
% prints the triangle
if n==1
    disp(printNStars(1))
else
    disp(printNStars(n))
    rtriangle(n-1)
    disp(printNStars(n))
end

end
