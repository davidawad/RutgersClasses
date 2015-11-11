function fibnum = fib(n)

if n == 0
    fibnum = 0;

elseif n == 1
    fibnum = 1;

else
    fibnum = fib(n-1) + fib(n-2);


end

