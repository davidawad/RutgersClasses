## Satisfiability search programming project
### Out Oct 21, Due Nov 6.
This project asks you to implement the key logic of a propositional satisfiability solver and to evaluate a number of different methods for controlling the search.

## Satisfiability
First, let's start with a brief summary of what a propositional satisfiability solver is supposed to do. The input to the solver is a formula in Boolean logic, in what's called conjunctive normal form or CNF. A CNF formula consists of three nested layers.

The innermost layer consists of propositional literals, in other words, either p or not p for some atomic proposition variable p. (Normally in sat problems, the variables are given by number, and we use the symbol '-' for negation.)

The middle layer consists of clauses, in other words, disjunctions of literals. For example, p or -q or -r is a typical clause. (In the standard DIMACS text format for CNF formulas, clauses are just written as a sequence of positive and negative integers; the integer 0 ends a clause.)

The outermost layer is the conjunction of a set of clauses. This is the whole formula in CNF.

You can evaluate a propositional formula for truth or falsehood if you have an assignment that tells you whether each propositional variable is true or false. (You just use the ordinary logical rules defined, for example, by the truth tables.)

The problem of satisfiability (SAT for short) is this: given a CNF formula F, decide whether or not there exists an assignment under which F comes out true. Normally, if there is such an assignment, we want to find one: the assignment represents some interesting possibility for our application which we want to then use to make better decisions.

## Background
Propositional satisfiability is a problem of both theoretical and practical interest.

Theoretically, SAT plays a key role in the theory of NP completeness. NP completeness makes sense as a concept because you can encode a nondeterministic polynomial-time computation as a polynomial-sized propositional formula, so asking whether the formula is satisfiable amounts to the same thing as asking whether the NP computation can possibly succeed. SAT solving also gives interesting insights into when and why NP-complete problems turn out to be intractable. There seems to be a "phase transition" in collections of problem instances, as they go from having a large number of solutions to having no solutions at all, where they become very difficult for any algorithm to do well on.

Practically, the best SAT solvers are fast enough that they can perform interesting work in a variety of industrial design synthesis and verification tasks, from circuit design to factory logistics. Industrial strength SAT solvers are useful tools for leveraging abstract formal representations to solve concrete problems. They also offer a lot of general lessons about how to explore search spaces efficiently and with minimal redundancies.

This homework includes a mix of programming problems, and example instances of SAT solving, and analysis questions that should help you to appreciate all of these issues.

## What you have to do
The skeleton code for this assignment lives in three files:

`Disjunction.java` defines utilities for working with clauses
`Conjunction.java` defines utilities for searching over CNF formulas
`SearchStats.java` defines utilities for measuring the resources used in a search task
You have to modify the Conjunction.java class in three ways:

give a complete implementation of the `search()` method, which right now is just a stub
create a variety of SearchControl instances, which codify alternative policies for exploring the search space; right now just a single vanillaSearchControl instance is defined
use the `main()` method to solve and report characteristics of various search problems; this will be the basis for a brief writeup that you turn in with your code describing the complexity of some different search problems.
You should be able to use the other files as is, and in fact the amount of code you actually need to work through to write the search method should be just a fraction of what's provided (though this assignment statement will give a complete overview… and it's all documented in the code anyway).

Implementing Search

The first thing to do is to implement the search method for the conjunction class. You start from the stub below:

```java
SearchResult search(SearchControl control, SearchStats stats, int depth) {
    /* TODO: Your code here, replacing the line below */
    return new SearchResult((Disjunction) null);
}
```

The core of the SAT solver is depth first search

```python
DFS(state) :
   if state is a goal
       return solution based on state;
   for each successor of state
       next = DFS(successor)
       if next is a solution, return next
   return failure;
```
SAT search specializes DFS in four ways:
We can use logical inference to simplify the state before starting the search.
We know to backtrack if the current state is inconsistent.
The variable we branch on next is up to us (we don't have to search over this), and there are only two possibilities for its value: `true` or `false`.
We can keep track of the decision variables that led to the last inconsistency (think of this as the reason that search failed). After one branch fails, if the reason doesn't involve the current variable, then we can tell that the other branch will also fail (it will encounter the same inconsistency). So we can return immediately: this is called backjumping instead of simply backtracking.
Here's an overall summary of the control flow in the SAT solver:

```python
search(state) :
   use logical inference to simplify state
   if we know state is satisfiable:
      return solution based on state
   if we know state is inconsistent:
      return failure (with an appropriate reason)
   pick a variable v and a boolean value b
   do search(state with v=b)
   if you get a solution, return it
   if you get a failure (because of the assumption v=b)
      do search(state with v=not b)
      if you get a solution return it
      otherwise return failure
      		(with an appropriate reason)
```

Your job is to flesh this out into java code by supplying specific control flow constructs and specific calls to the relevant methods on the Conjunction, Disjunction and SearchStats classes.

States here are realized by the Conjunction class, of course, as that's the class for which we're defining the search method.

Helper classes you should look at now
There are a few helper classes that make it easier to organize the information in the search.

### Assignments
The first is the Assignment class, which lets you return a variable v together with the truth value b that you want to assign to it as a single object. Use

`new Assignment(v, b)`
to create an assignment, and access its fields directly as `a.v` and `a.b`.

### SearchResult
The second is the SearchResult class, which lets you package together the information you might want from a search under various different circumstances: if the search succeeds, the search fails, or the search times out (more about the actual mechanics for having search time out below).

- If search succeeds, you'll want to create a SearchResult based on the assignments field of the current state.

- If search fails, and you're doing backjumping search, you'll want to create a SearchResult with a summary of how to avoid the specific choices that led to a dead end here (for example, when you find that the current state is inconsistent, you can figure out what decisions cause the inconsistency with the `conflictInducedClause()` method).

- If search times out, there's nothing to say, so just create a SearchResult with SearchStatus.Timeout.

### SearchStats
The third is the SearchStats class, which gives an abstract interface for recording specific events associated with the search. For example:

`expand(depth)` note that you've expanded a node at a certain depth
`deadEnd(depth)` note that you've reached an inconsistent node at a certain depth
`succeed(depth)` note that you've found a solution at a different depth

`fail()` that the search didn't work out

`tick()` put this on anything that you want to count
`sample(interval)` an indicator that's true after the last out of every interval calls to
`tick()`, useful for printing out occasional diagnostics during some aspect of the search.

`summary()` get a string describing what happened during the search
You should instrument your search implementation so that it calls expand, deadEnd, succeed and fail at appropriate times.

In the skeleton code, the search operation is parameterized by an object called SearchControl. This structure gives

- `public void simplify(Conjunction c)` - a function to call to simplify a state by inference

- `public Assignment pick(Conjunction c)` - a function to call to pick the next variable and the value to try first

- `public boolean backjump()` - a function to call to say whether to try backjumping

You should use these methods from the SearchControl to handle the key steps in carrying out the search.

The example vanillaSearchControl shows how to create a particular SearchControl object with particular parameters. (You might not have seen examples of nested classes or the syntax for creating objects of anonymous classes that implement interfaces, but they come up a lot with some of the more abstract object-oriented patterns in Java.) When you use the vanillaSearchControl, your search should not simplify the state at all, it should always pick the alphabetically first variable next and always try the value of true first, and it shouldn't do backjumping. You will have to define a superfancySearchControl that makes the search:

simplifies the state using the two inference methods `propagateUnitClauses()` and `assumePureLiterals()` that are already defined as instance methods of the Conjunction class.
pick the next move using the mostFrequentVariable() method (again already defined as an instance method of the Conjunction class).
does the backjumping optimization
Timing Out
You should be prepared that your search algorithms will run longer than you feel like waiting for them, especially when you test the simpler algorithms that don't exploit the structure of the search space as well. As you're developing the program, running test suites, and putting together your analysis, you will probably want to set a time limit on the searches that you do. There is a bunch of infrastructure in Conjunction.java to make this easy for you: there's code to run a search process in a separate thread (in the SearchThread class), and illustrative code that starts the thread, waits for a time limit, and then tells the thread to stop if it's still running, and gathers the results from the search (packaged together in the static method doSearchWithTimeLimit).

However, you need to make your search method responsive if you want any of this to work. Here's the deal. In Java, you can define separate threads of execution in your program that run concurrently. A thread can send messages to other threads, but a thread can't do anything drastic like killing another thread. So the best we can do is send an 'interrupt' message to the search thread. It basically means "Hey, if you get this, it would be nice if you stopped." These messages are only delivered to threads when they listen; that happens automatically at a bunch of operating system calls (for example I/O operations). Unfortunately, your search algorithm is not going to be doing any of those calls; it's just going to be plowing away exploring the search space.

Bottom line: somewhere your search method needs to call the static method `Thread.interrupted()` to see if it should give up, and return a timeout search result if `Thread.interrupted()` is true. If you do that, your code will play nicely with time limits.

## Backjumping
This is the most sophisticated search optimization in the assignment, and it goes beyond what we covered in the recitation, so read this carefully!

It's easiest to explain how backjumping should work with an example. Suppose we set variable `a=true` at a certain step of search, and then we set variable `b=true` at the next step of search, and finally `c=true` at the next step. However, it might turn out that the inconsistency we find doesn't depend on the value of b. For example, the original problem could have the clauses `(-a or -c or d)` and `(-c or -d)`. When we set `a=true` and `c=true` and simplify, we get `d` and `-d`, and when we do unit propagation we see that these are inconsistent. In fact, under the hood, the Conjunction data structure keeps track of this, so when we call `conflictInducedClause()` in this situation, it says `-a` or `-c`.

Now we move on to the other case c=false. It might turn out that the value of b doesn't matter here either. When we call `conflictInducedClause()` here, it will say something like `-a` or `c` (depending on what variables other than `b` and `c` are involved).

At this point, we need to take stock of why the overall search with c failed, and report something useful to the higher ups. If the overall formula is satisfiable, we know that c or -c is true: in one case, the first clause says -a has to be true; in the other case, the second clause says -a has to be true. So in fact, the reason the search failed is that -a has to be true. You can calculate this automatically by the static method `Disjunction.resolve()`, which takes two clauses as arguments (like two clauses giving reasons for failure) and reports a single clause that the two jointly together. That's what you should do with backjumping to report the reason for failure after trying two alternatives, neither of which worked.

Now we backtrack up to b. Normally, we'd consider the other value of b here: b=false. But when we look at the reason that search failed last time, the reason is -a. It doesn't matter whether b is true or b is false, that is still going to be a problem. In other words, if the last conflict induced clause already has the truth value of false on the current assignment, you shouldn't try extending that assignment the other way, you should just fail. This is called backjumping because instead of backtracking through all the variables you've assigned, you back up immediately all the way to the first variable that you see could actually make a difference. Use `getConflictInducedClause()` to find the reason for a reported failure in a SearchResult object, and call the Disjunction class's `truthValue()` instance method with the current assignments to get a Boolean giving its truth value (or null if the assignments leave the truth value of the formula open).

###### (Note that actually now we know that a=false no matter what b is. That's the basis for a general optimization that we're not going to be implementing, called clause learning, which uses knowledge of previous failures throughout its exploration of the rest of the search tree, not just in backtracking.)

## Evaluation

The skeleton code comes with a number of external link: sample inputs from external link: John Burkardt. In addition, there are many resources for getting sample SAT problems to play with. You can get external link: random problems and you can also browse external link: collections of problems that have been used as benchmarks. Some of these problems are too large to solve easily, but many of them run in reasonable times once you have some good search algorithms in place. I encourage you to look at some of these problems and try them if you are curious.

There is no one way to measure the difficulty of SAT problems, and different search procedures are appropriate for different kinds of problems.

Unsatisfiable problems are typically hard because you have to systematically explore all of the possibilities. You typically need the most efficient representations in these cases, because you really will wind up considering everything.
Satisfiable instances can be hard because you have to find a good combination of variable values and that can take some luck. You don't always have to be efficient here; sometimes it's better to be random, especially if there are likely to be many solutions.
Thus, you can't say once and for all what algorithm is best; you typically need to run an experiment to assess the performance of different algorithms on different kinds of problems.

You should do a small evaluation of your method, and report your results and conclusions. Proceed as follows.

 - Pick one set of SAT problems to investigate. The problems should have similar characteristics so you have a good basis to generalize. For example, you could pick random satisfiable problems, random unsatisfiable problems (external link: from online benchmarks), satisfiable problems or unsatisfiable problems derived from mathematics (some external link: here), or problems derived from encoding practical problems (again, external link: examples online). In most cases it's easy to get many examples; 20 items is a good rule of thumb for when an experiment starts to be reliable.

- Pick some different configurations of the solver to work on. You should pick at least three different configurations that it makes sense to compare. For example, you can compare different ways of picking the splitting variable, or doing different amounts of inference to simplify problems, or different kinds of backtracking.

- Run the different searches on your problems and get the results. Describe the differences that you find. Things to consider: are the results consistent? or do they vary from problem to problem? how large is the search space that different methods employ? how much time to the different methods take? Depending on the parameters you choose, you might find that differences are small or large; you might find that they are consistent or vary from problem to problem. You might find that cutting down the size of the search space is crucial to performance; you might find it better to search more nodes faster than to use expensive inference to limit the search to fewer nodes.


Write up a short document summarizing your experiment. Be clear, concise and precise. Say: what SAT problems you consider, what configurations of the solver you use, what results you obtain, and what lessons you draw for solving similar problems in the future. Credit for this part comes from proceeding carefully, setting up a meaningful experiment, describing what you did cogently and making a good argument for your conclusion. You can use any format to describe your results (e.g., plain text, word processor, typesetting program) but please convert your file to PDF before submitting it.

## Hand in

Your code for `Conjunction.java` giving your implementation of search, and the brief writeup experiment.pdf that you prepare to answer your choice of the evaluation questions. Use the dropbox tool on sakai.
