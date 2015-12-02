# Project 4: Text reconstruction

## Introduction
This assignment puts together a bunch of ideas that we've learned in the course, and explores some programming techniques that we haven't had a chance to try out so far. The main point of the assignment is as an an exercise in modeling problem solving using state-space search. The assignment also shows a common technique for combining problem solving with learning through factored probabilistic models, and illustrates the utility of training AI models using large data sets.

The assignment involves two tasks in text processing: word segmentation and vowel insertion.

Word segmentation is necessary for processing many written languages; for example, Chinese is written without spaces between the characters; meanwhile, German has long compound words that need to be broken down into their parts to be recognized. Segmentation is also an important ingredient in speech recognition, since speech sounds often flow together continuously even across word boundaries.
Vowel insertion is necessary for languages such as Arabic or Hebrew, where vowels are normally not written. The same kind of reconstruction can be used for many other text processing applications; for example, similar techniques can be used to expand abbreviations and correct errors in entering text input on smartphones and other portable devices.
The way you complete these tasks is to implement a Java data structure that defines an abstract search problem. This data structure can then be used by the generic search solver that we provide. We also give you the infrastructure that you need to deal with language data, including a bunch of string processing methods, a dictionary, and several cost models that you can use to find the most natural outputs.

This assignment is based on a similar (python based) assignment designed by Roy Frostig and given in Stanford's CS 221 in 2013.

## Defining Search Spaces in Java

The assignment includes the Java interface SearchState, defined in SearchState.java. The file contains useful comments that you can follow as you implement your own implementations of this interface. As always, the idea of using an interface is that there is generic code to work with implementations of SearchState, in particular functionality for actually carrying out searches. However, there is no generic or fundamental case of a SearchState, so there's no advantage to organizing kinds of SearchState into a class hierarchy and inheriting functionality across different instances.

Intuitively, a SearchState realizes the key features of search problems, as covered in class and sources like Russell and Norvig:

* A set of states (this is the SearchState type)
* A set of actions (it's convenient to use String to name actions)
* An initial state
* A way to test if a state is a goal (isGoal())
* A way to get the applicable actions in each state (getApplicableActions())
* A way to measure the cost of doing an action in a state (getActionCost(String))
* A way to apply an action in a state to get a new state (applyAction(String))

Most of these are straightforward, but there are a couple cases where Java requires the data structures to be realized with some subtlety.

As you may recall, search algorithms require maintaining tables that indicate what states have been visited and what the best cost path is associated with a state. This means that the SearchState data type must allow for a semantic test for equality and must be capable of being indexed efficiently in ways that respect equality. Normally, Java provides built-in methods hashCode() and equals() for all Objects, that simply based on the location in memory where the objects reside. Objects that are created at different times are always treated as different. This won't work for SearchState, so when you implement a SearchState you have to override hashCode() and equals(). There's a simple template for both functions: for hashCode() you get integer hashes for all of the meaningful fields in the data structure and XOR them together; for equals() you make sure you have an object of an appropriate type and check that all corresponding meaningful fields are equals() recursively. Because hashCode() and equals() are methods of Object, they are implicitly part of any interface type in Java. So there's no way to force the compiler to make you override these methods in a particular way. But if you fail to override equals(), search will consider all search states as different, which means that you'll get an exponential blowup in the number of states that you have to consider to solve the problem and you'll find your code running horribly slowly. And beware of cutting and pasting code: you'll want to be careful to change everything that needs to be changed (like all the typecasting), because errors will silently lead to bad results.

The initial state is handled by a Builder pattern. The Builder pattern is a general method for using a helper object to manage the creation of a Java object. The Builder pattern is commonly used for a few useful features which are present to some degree in this case. A Builder can make it easier to specify the parameters of an object, because the Builder can have methods that set these parameters one at a time (a constructor needs all the arguments at once). The Builder can also specify a wide range of intelligent default values for parameters that you don't want to deal with explicitly. And the Builder lets you program with an imperative idiom, even if you want the parameters of the final object you create to be immutable (which is what we want in this case, because SearchState instances are shared, indexed and compared across the search in unpredictable ways). However, in this case, the Builder serves another purpose: allowing you to include the functionality of getting an initial state for a search problem as part of the interface. Interfaces cannot specify constructors in Java, so you need to have some sort of factory or builder operation for all the ways that you want to create new instances of the interface in generic code. You also can't specify static methods in interfaces, which means that your Builder has to be realized as an object. Concretely, SearchState.Builder is an inner class in the interface, where the object provides a method makeInitialState(String) to create a representation of an initial state corresponding to a problem.

As part of the assignment, you get a sample implementation of the SearchState interface, called TestSearchState. This implements a simple search problem for getting a particular quantity of flour by moving fixed amounts of flour back and forth into a working area. You'll see how the state is defined, in terms of a quantity you have and a target quantity you want. You'll see examples of the usual templates for hashCode() and equals() based on this definition. You'll see the structure that you need to create to set up the builder pattern for this new type in a way that meets the interface. And you'll see how actions are defined, tested for applicability, assigned costs, and applied to reach new states.

You can actually play with this sample interface by using the command cc k in the console interface, where k is the quantity of flour you want the system to pour out. (More about this below.) Make sure you think through the relationship between the search state and the solutions found. Solutions are managed by Node data structures (just as in the last search assignment) and the Node keeps track of the path found; the state just has to keep track of the progress towards solving the problem.

Your answers for your own search problems will consist of comparable implementations of the SearchState interface that work specifically on the language problems that we have set up.

## Infrastructure for language data

What's interesting about this assignment is the way it takes empirical data into account. How you reconstruct text depends on the words in the language and the frequent patterns of words that people actually tend to use. We've set up three data structures that help to answer these questions.

ExpansionDictionary looks up words. Given a word without vowels s and an ExpansionDictionary d, d.lookup(s) returns all the words that we've seen in our database that are compatible with s.
UnigramModel ranks words by frequency. Given a word s and a UnigramModel u, u.cost(s) returns a measure of how surprising it would be to see s given the way language is used in our database. Surprisal (as is common) is measured by negative log probability, and so it roughly corresponds to the amount of information associated with an event: the more unlikely it is, the more unexpected it is so the more information we get when we see it. The UnigramModel includes estimates for unseen words: longer words get more and more unlikely.
SmoothedBigramModel ranks words by frequency in the context of the previous word. Given a context word w1, a target word w2, and a BigramModel b, b.cost(w1,w2) returns a measure of how surprising it would be to see w2 immediately following w1, given the way language is used in our database. If you're interested in the first word, use b.cost(LangUtil.SENTENCE_BEGIN, w1), to indicate that you're looking at w1 at the start of a sentence. SmoothedBigramModel doesn't directly use the counts of pairs of words to compute these results, because even with a big database the counts are likely to be sparse: many sensible pairs will not occur. So it uses a weighted mixture of the unigram estimates and the actual bigram counts. (ExplicitBigramModel works the same way but uses the counts directly, if you want to see what that looks like.)
All of these data structures implement the Singleton pattern, which is a common way of enforcing that data types have only one instance in a Java program. In this case, these models can be fairly expensive to build. You get them by reading a corpus, which is potentially quite large (there's about 10MB of language data available for you to experiment with as part of the assignment). So you don't want to create them over and over again, you just want to be able to get them when needed. All these classes therefore provide a static method getInstance() that returns the unique instance of the class to you.

Since all of these models use log probabilities for costs, the cost of a set of events is just the sum of the costs of each event in the set.

## Problem 1: Word Segmentation

Create a class SpaceSearchState that describes the problem of breaking up an unsegmented string into a sequence of words in a way that minimizes the total unigram cost of the words.

The work here is primarily conceptual. How should you think of breaking up an unsegmented string? How does progress proceed? How does that translate into the information that you should keep track of in each state? How does that translate into the actions that are available at each state? How will you actually apply one of these actions? And how will you calculate its cost? If you have a clear picture of what has to happen then you will find that there's not much code to write, maybe 50 or 60 lines, a lot of which involves adapting templates that you can see in the TestSearchState file.

## Problem 2: Vowel Reconstruction

Create a class VowelSearchSpace that describes the problem of reconstructing words from a dictionary so as to match a sequence of words without vowels and minimize the total bigram cost of the words.

Same advice: The work here is primarily conceptual. How should you think of reconstructing a vowel-less sequence of words? How does progress proceed? How does that translate into the information that you should keep track of in each state? How does that translate into the actions that are available at each state? How will you actually apply one of these actions? And how will you calculate its cost? If you have a clear picture of what has to happen then you will find that there's not much code to write, maybe 50 or 60 lines, a lot of which involves adapting templates that you can see in the TestSearchState file.

## Problem 3: Extra Credit: Combined Reconstruction

Create a class CombinedSearchSpace that describes the joint problem of breaking up an unsegmented string of consonants and reconstructing a sequence of words that match the consonants seen and minimize the total bigram cost of the words.

## Testing your work

You're given a console application class SearchConsole.java that you can use to play with your search problems as you build them. To start with, it includes help and quit commands, as well as commands to see what you get from the unigram model (ug sentence), (smoothed) bigram model (bg sentence), and expansion dictionary (ed sentence). It also includes a command cc total which lets you plan to move flour around.

The file includes definitions that you will be able to use once you write your search spaces to test the results that you get. These definitions are commented out to start with, with a comment that begins "Add back in the code below". There's a command sp sentence to add spaces, vw sentence to add vowels, and sv sentence to do both.

By default, the data is read from a file "corpus.txt". The sakai site includes a default corpus.txt which includes the Project Gutenberg texts of Jane Austen's Pride and Prejudice, Charles Dickens's Great Expectations, Adam Smith's The Wealth of Nations, Mark Twain's Letters, Mary Wollstonecraft's Vindication of the Rights of Women and Charles Darwin's Origin of Species. It's about 7MB. The sakai site also has the data Stanford used: Tolstoy's War and Peace and Shakespeare's Romeo and Juliet. It's about 3MB. It takes a noticeable amount of time to load these, so if you have frequent bugs, you might want to use the 1KB file "abe.txt" which stores the Gettysburg Address and loads almost instantly. To change the corpus, update the variable LangUtil.CORPUS. You'll notice by the way that even with all this data there are still lots of words missing and lots of patterns that seem vary familiar to you but that aren't present in the data. Google trains its language models on more than 1TB of language data (5 orders of magnitude more than what we have here). Thus, you probably have only rough intuitions about what's actually in the data that you're working with. For one thing, the corpora include the legal boilerplate that's associated with all the Project Gutenberg releases

Because of this, when it comes time to test your code, you'll want to understand the examples you work with, by visualizing them with the expansion dictionary, the unigram model and the bigram model.

Of course, you probably don't want to test your code directly on a full search problem. It probably makes sense to plan some tests for yourself and make sure that they get the expected results. You can do that by writing a main method in your SpaceSearchState and VowelSearchState classes and testing key features that you're interested in, like do you have the right actions and the right costs, do you update with actions correctly, and can you correctly detect a goal.

## Hand in

Your files SpaceSearchState.java, VowelSearchState.java and (if you choose to do this) CombinedSearchState.java.
