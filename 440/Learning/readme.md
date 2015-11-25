# Collaborative Filtering Assignment
## Due Wed Nov 23, 4:00pm

The point of this assignment is to implement and analyze the performance of nearest neighbor methods for collaborative filtering. Read the assignment carefully, because the most important part of the assignment is the analysis. There are lots of possible choices about how to create the best recommendation. They are all easy to implement. The hard part is to justify one choice over another. How do you know how good a particular solution is? This assignment teaches you how to answer that question.

### Programming
So there is actually only one function to write for this assignment. It is the method

```java
public Rating predict(String rater, String item, Method method, int numItemNeighbors, int numRaterNeighbors) {
	// TODO Your code here
	double score = this.defaultScore();
    return new Rating(rater, item, score);
}
```

from `RatingDictionary.java`. The initial version is a stub that gives every possible data point the average rating of all items by all raters in the collection. In other words, it's the simplest, most basic guess.

There are six different enumeration constants defined for the Method variable, and you have to implement a separate prediction for each of them. Here's the definition for the Method enumeration, from the top of `RatingDictionary.java`:

```java
// Strategies for predicting rating from rater and item
static enum Method {
    ITEM_BASELINE, RATER_BASELINE, MIXED_BASELINE,
    ITEM_SIMILARITY, RATER_SIMILARITY, CUSTOM
}
```
You can dispatch three of these easily, once you have gotten a hang of how the code is organized. These are the BASELINES. The remaining two are the SIMILARITY measures, and they basically amount to a nearest neighbor algorithm, just as described in the textbook. There are no constraints on the CUSTOM strategy. You can leave it as the simplest guess or do something more interesting for extra credit.





### Writing the baseline

Writing the Baselines
The idea of a baseline is important in machine learning (or in any other engineering discipline where you might not be certain that a change that you have made would lead to an improvement). A baseline is a simple algorithm whose performance you understand analytically and whose results you can compare to a more sophisticated method whose behavior is more complicated. A baseline is a way to gauge whether you have made an improvement and, sometimes more importantly, whether you have done the right experiments and made the right measurements so that you could recognize any possible improvement if it realy was present.

So, each of the baselines is a simple rule that says how to predict a new rating based on ratings that you have in your collection.

The `ITEM_BASELINE` says: guess that a new rating for an item will be about equal to the average rating that that item has received in the past.

The `RATER_BASELINE` says: guess that a new rating for an item will be about equal to the average rating that the user you're looking at has given in the past.

The `MIXED_BASELINE` says: make a good guess about what rating you'd expect the item to receive and what rating you'd expect the rater to give, and split the difference between them.

You can implement each of these in just a few lines of code. You just need to understand how the `RatingDictionary` object stores ratings by item and by rater in hash tables called `itemData` and `raterData`; this allows you to look up ratings information of either type by name (e.g., `itemData.get(movieName)` or `raterData.get(raterName)`). These functions return objects of type `RatingTable` which have a variety of methods defined, including a method `getAverage()` which returns the average rating in the table.

The `RatingDictionary` itself already has a method called `geometricMeanBaseline(rater, item)` that provides a good mixed baseline. I have provided this for you because it is an open-ended problem (that I actually got badly wrong a few times along the way). The trick is that you not only need to figure out how to balance the weights for rater and item together in a sensible way, but then you have to figure out how to set the weights (so balanced) so that they actually match your training data. (You can maybe convince yourself that unless you're very careful, when you predict new data based on combining old averages, the averages from the predictions might not match those averages you started from. For those of you who are interested, you can trace through the assignment and see how the weights and estimation works - the programs for creating new test data, which I will describe later, also show how I eventually managed to debug the method I chose.)

The baselines are actually the most important ingredient in building and understanding a recommendation system -- something that we have really only properly come to understand and appreciate in the last few years (since the last round of the Netflix prize competition). In this assignment, you have to work carefully to beat the mixed baseline.

### Writing the Similarity Methods

Writing the Similarity Methods
The other two methods are similarity methods. Each similarity method says to make an overall prediction for a new rating by weighting together simple predictions from similar ratings. The two methods just differ in which kind of similar ratings you work from.

By the time the predict method is called, we have already populated fields in the RatingDictionary that describe the most similar raters for each rater, and the most similar items for each item. These are hash tables raterNeighbors and the itemNeighbors, so you can retrieve the record for a particular rater by running something like raterNeighbors.get(raterName). The object returned is a SimilarityTable.

A SimilarityTable stores a list of similar items. (The abstraction layer here helps hide the difficulty of managing memory, which is a consideration when you might potentially have thousands of items each ranked against the other.) The items are stored in sorted order in a list field called similarities. The elements of this list are - not surprisingly - Similarity objects. Each similarity object has a key, which says what object this is, it has a value that says how similar the specified entity is to the target of the SimilarityTable, and it also comes with a method

public double predict(double from)
That says how to translate from a rating score on the specified entity to a rating score on the target.

So to operate over the first k nearest neighbors to an entity, you find the similarity table for that entity and go through the first k elements in the similarities list from the table (or all of the elements, in case there are not enough).

The similarity methods agree: to make an aggregate prediction, take a weighted average of the predictions from the top k ratings, weighted by their similarity value. We have one method that uses similar items, and another method that uses similar raters.

Be careful to make intelligent defaults if there are not enough similar items in the collection for the calculation to be completed.



### Debugging
In order to do the analysis, you need to know how your prediction method actually gets called. The file SampleRun.java illustrates all the steps involved in running prediction off of real data (or test data). I assume that you have downloaded the MovieLens data, specifically the files "u.item" and "u.data" somewhere on your system. We will also use other ".item" and ".data" files that we create. Therefore the prefix (e.g., "u") is a command line argument to the analyzer.

The file begins with three steps to set up the data:

RatingDictionary rd = RatingDictionary.fromMovieLensItems(filePrefix + ".item");
RatingTable data = rd.tabulateMovieLensData(filePrefix + ".data");
rd.addTrainingData(data, sampleFold, numCrossFolds);
The first step loads the movie titles (this is just for convenience in interaction), the second loads in the ratings (you can change this to load in fake ratings from test data), and the third sets 90% of the data to be used as training and every tenth item to be used as test data (this happens by setting numCrossFolds to 10; you can use any number N, and a fraction of N-1/N of the data will be used for training and the rest for test). The particular items that will be used as test data start with the first (that happens by setting sampleFold to 0, sample fold can be any number from 0 to N-1).

It then figures out whether to adjust for baseline rates (yes or no, controlled by the variable predictAgainstBaseline, initially false). Then it computes similarities for items and raters (according to the specified method, initially PEARSON, up to a number of maxItemNeighbors or maxRaterNeighbors, intially 100). It then computes predictions from the test data, using a specified prediction method and a set number of neighbors for similarity methods. It ends by printing out the results, in the form of "root mean square error" RMSE, the normal error measure for the field. You can think of this as a variant of Euclidean distance that normalizes for the number of dimensions in the space.

Eventually you will want to run analyses on the MovieLens data, but to start it is probably too large and complex, while you explore the first steps of your implementation. You have two programs that can construct smaller random data sets, and one of them, RecoTestData, will get you some good data to start with - here is a way to create a small data set of 20 raters of 2 types, 100 movies and 1000 ratings:

`RecoTestData 20 100 2 0 1000` Sample.data
The resulting file is stored as Sample.data.

There is a pattern you may remember from the search assignment. I am once again asking you to run code to create the input to the program you have to write! This data is designed so that the baselines will not work all that well but either kind of similarity measure will get good performance quickly. If you see something else, it's probably a bug. Another set of data that might help is a set of data that's specifically designed to test the baselines:

`MixedBaselineTestData 20 100 1 1000 Sample2.data`
Again, the resulting data is stored as Sample2.data.

As long as there is enough noise, this data set doesn't give a lot of success to the similarity methods.


### Choosing Parameters
Once you have everything in place, you can start to play with the different parameters of the rating method with different versions of the test data sets. You can even load in the MovieLens data and start to see how different methods work. But you have to be careful. You can easily ruin both your data and your method by throwing countless alternative versions of your method at all of your data.

So here's what you should do to choose parameters. Start with a special set of test data, call it fold 0 of a crossvalidation run (as it so happens). That's called your development data. You use this data mercilessly to winnow out horrible parameter settings and come up with a handful of good ideas that you think are really plausible candidates for being very very good (if not the best). The formal process of assessing results will use just these few versions of your system.

You probably have not written a program before that you could ruin by running it, so let me explain.

Take what you know about machine learning and apply it to yourself
Here is your situation as a programmer. Somebody comes to you and asks: "Predict users' ratings for me, as best as you can, and let me know how good you expect it to be". You have a bunch of programs you can try; there are lots of different algorithms you can use with lots of different parameters. Most of those parameter settings are not going to work at all, but quite a few of them are going to be fairly good. But you don't just want a good one, you want the best one. And the only way you can know for sure is to try them out.

Suppose you try all the methods on all the data. You can pick the best one, but then you won't know why it worked. In fact, you can convince yourself that if there was any chance at all involved in the outcome of any of the experiments, then you have almost certainly picked a particular method that just happened by accident to work better than the alternatives. Think of it this way. You tried lots of options. One of them had to be the lucky one. Whichever it was, you would have picked it. If you've taken an experimental design course in statistics, psychology, or something similar, you will recognize this as "experimenter bias". Famous scientists have gotten caught off guard by experimenter bias and convinced themselves that they had hard data for ESP, cold fusion, you name it!

### Assessing Results
Now you make the best choice you can among the methods that you have found. You do that by using NEW data, that you would never otherwise have tested on, and you average the behavior of the methods on a bunch of runs with the new data (call this folds 1 to N-1). Use the performance on this round as your estimate for how well the different methods actually work, and make your choice about what to use based on this step. Oh, and keep your baselines around to make sure that the experiment worked!

The logic here is that you have separated the two parts of the problem: optimizing your algorithm and deciding how good it is. That means you don't have to worry as much about the key problem we started from: choosing what to do explicitly based on what happens by accident. So what typically happens now is that your best guess is actually fielded, and you will get honor or acclaim (or stock options) based on how well it actually works. We don't have access to secret test data, so we will have to make it ourselves.


### What to hand in
###### Four things:

- Your code for `RatingDictionary.java` with your predict method.

- A revised version of `SampleRun.java` with the parameters that you think work the best.

- A text report file `Analysis.txt` describing the process you used to arrive at three candidate methods to try. Present cross validation results for folds 1-9 of the movielens data for your three methods plus baselines. Conclude with your recommendation about what to use.
Finally - the movies are totally out of date, but we need this for a final contest - find 30 movies that you've seen from the items included in the movielens list, give yourself a random 4 digit id, and type up a little file in movielens format with your ratings. The file format can be a little fussy so make sure your data can be read in by the rating dictionary.

We'll compile all your ratings into a new test data set, and report the outcomes of everybody's recommended methods on the new test data set.
