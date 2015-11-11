    function triangle(n)
% prints the triangle
if n==1
    disp(printNStars(1))
else
    disp(printNStars(n))
    triangle(n-1)
    % you can comment out line 8 to create the first output of just a
end

end



