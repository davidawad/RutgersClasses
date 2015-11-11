function acronym = acronym(str)
% ACRONYM Returns the acronym of a string

C = strsplit(char(str));

acronym = '';

for i = 1:length(C)
    
    curr = char(C(i)); 
    
    acronym = strcat(acronym, curr(1)); 

% loop has now concatendated all first characters of the array

end

