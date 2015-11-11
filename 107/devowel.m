function devowel = devowel(word)
% takes a word and removes vowels

% Erik why the fuck does this work? We're going up to the length -1...

for i = 1:length(word)-1
    char = word(i);
    % fat or statement 
    if char == 'a' || char =='e' || char =='i' || char == 'o' || char =='u'
        word(i) = '';
    end

devowel = word;

% fuck this language
end

