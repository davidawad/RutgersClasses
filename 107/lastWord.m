function lastWord = lastWord( str )
%LASTWORD(str)

C = strsplit(str);

lastWord = C(length(C));

end

