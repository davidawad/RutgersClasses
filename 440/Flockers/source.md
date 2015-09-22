## Source

The skeleton code consists of a set of program files that have complete behavior, and a stub file Flocker.java that you have to modify. This source code can be downloaded from external link: worksite:/Projects/Flocking/Code/.

In all there are 16 files. There are 6 general support files:

    Agent.java
    Percept.java
    Intention.java
    World.java
    FlockingReader.java
    Simulation.java

There are 7 files describing sample agents:

    LightSource.java
    Obstacle.java
    Runner.java
    Follower.java
    SmartFollower.java
    ReactivePredator.java
    ModelPredator.java

And there is the stub file:

    Flocker.java

To complete the assignment programming, you just have to flesh out the deliberate methods in the stub file so that these agents respond to their percepts as they are supposed to. To do that nicely, you might have to familiarize yourself with certain aspects of the support files and the sample files. This page gives you a roadmap to these files so you can pinpoint the code you need to focus on. This material supplements the discussion that we have had of the code in lectures and in recitation.
The overall framework

Three support files, Simulation.java, World.java and FlockingReader.java, set up the high-level flow of control to create an environment, create agents, and simulate them acting together. These are the files that you are least likely to need to understand or modify, even for optional extensions you create.

Simulation.java contains the main method for the interaction. It handles reading in data, stepping the simulation, and managing the startup and shutdown of the user interface. This is a short file (100+ lines) and the only possibly interesting thing about Simulation.java, for people who like Java tricks, is the code to interface with the window manager and operating system so that XML log files remain valid whenever the program exits.

World.java manages the simulation of many agents acting together. It handles stuff like moving agents on the screen, constructing percepts, sequencing agents' deliberation, computing the effects of agents' actions, detecting collisions between agents, handling interactions between agents, and logging events. World.java is a fairly long file (about 800 lines) but it's largely devoted to geometric computations and file I/O which you won't need to change (or even understand). The most interesting stuff happens at the end, where the world actually drives each agent. Key methods are makeAgentThink() which constructs each agent's percepts and calls deliberation, tryToMove() which computes what the agent's commands to its effectors will actually do in the environment, and stepWorld() which drives the cycle of perception, deliberation and action for all the agents.

FlockingReader.java manages the creation and update of agents as specified in files. It's a medium-sized file (about 400 lines) but again it should be set up so that you don't have to worry about it. See HW2 Specs for more details on what you need to know about the file format for world descriptions. 

## Support for agents

Now we turn to the other three support files: Agent.java, Percept.java and Intention.java. Recall the basic architecture of an agent in AI:

Any implementation of this architecture will need to define objects to encode percepts and intentions. That's the job of Percept.java and Intention.java respectively. You won't need to modify either of these (unless you do some pretty sophisticated extensions), but you probably should understand them. Fortunately they are among the shortest files in the skeleton code base.

Percept.java first off describes the percept structure. A percept says what kind of object was perceived (that's given by a value from the enum class called Percept.ObjectCategory). It also says what color the object has. That's a graphics Color object, but you can inspect the color by looking at its red, blue and green components. There is an example of this at the end of Percept.java. Finally, a percept describes the geometric relationship between the object seen and the viewer agent. The following diagram shows the four parameters of this relationship as defined in a percept object: the distance, the angle, the orientation and the speed.

Note that angles are represented internally in percepts in radians, so you can directly use them with the trigonometric functions defined in the Math class. Because y values on the screen increase from top to bottom, positive angles represent clockwise turns and negative angles represent counterclockwise turns. For example, angle in the above figure would take on a positive value and orientation would take on a negative value. Most agents can only go forwards. Agents that can go backwards will be perceived as having the heading in which they are "facing" and a negative speed.

Percept.java concludes with accessor functions and with Measure objects that provide an interface for abstract methods that search through lists of percepts for particular characteristics. You will see an example of this in Follower.java.

Intention.java describes what agents in this simulation can do. The simulation is described entirely by geometry, so agents can plan to turn or change speed. This short file describes data structures for recording these intentions.

Agent.java, finally, describes the overall agent architecture. It begins by describing the parameters of any agent. These include the information specified by an instance form of the nested class FixedAgentAttributes which specifies the behavior and appearance of the agent generally, and an instance status of a nested class DynamicAgentAttributes which specifies the particular configuration of the agent at any moment of the simulation. The instance members of these nested classes are good to know - although the default values provided in the skeleton code suffice for the assignment. (The nested class definitions include a fair amount of code for XML I/O.) Agents also have a world myWorld (to organize interactions with the environment), an integer id (for reference in XML files), and a boolean isAlive that helps with the bookkeeping when agents die and get removed from the simulation. The agent also keeps track of lastStatus the dynamic agent parameters at the previous round of simulation; this is useful in visualizing and debugging agent decision-making.

Agent.java continues with getter and setter methods, along with some utility code that handles agent action in a general way, enforcing constraints on agent capabilities and managing the interaction with the world.

The last bit of Agent.java describes the methods that most specific agent implementations will override. There is XML I/O - because individual agents have specific types and may have extra properties, these are usually overriden by specialized methods that are provided in the skeleton code. More interesting are the methods:

``java

    void draw(Graphics) //says what the agent looks like to you

    Percept.ObjectCategory looksLike() //says what the agent looks like to other agents

    InteractiveBehavior behaviorOnApproach(Percept.ObjectCategory) //says what the agent does when it gets close to somebody else as a function of what they look like

    void deliberate(List<Percept>) //create a new todo list for the agent giving the agent's intentions in response to the agents that it sees, as given by the available percepts.


```

## Sample Agents

LightSource.java a simple agent that does nothing, drawn as a circle and perceived by other agents as a LIGHT. Other agents can pass right through the light source.

Obstacle.java a simple agent that does nothing, drawn as a square and perceived by other agents as an OBSTACLE. Other agents cannot pass through an obstacle. If something hits an obstacle with higher strength than its own, it dies!

Runner.java a simple agent that proceeds forward along a fixed path. Useful to have in the mix to test the behavior of predator and flocking agents. Add code to adapt the speed based on the available light to reproduce the description of Braitenberg's vehicle 1.

Follower.java the light following agent based on Braitenberg's vehicle 2. This provides a superclass for the Predator and Grazer agents you will create in parts 1 and 2 of the project. Some of the functionality here is designed to be adapted for those agents so there are some things you should take note of. Follower implements a general way of deciding what to chase. A method boolean isTarget(Percept) says whether a percept describes an agent this follower would chase. A method double targetCost(Percept) says how much effort is involved in chasing a particular target. Then there are general methods to find the lowest cost target Percept bestTarget(List<Percept>) and to chase it void steerTo(Percept). See what these default to and how they are put together in the void deliberate(List<Percept>) method.

SmartFollower.java the light following agent we constructed that will back up when it is too close to its target. This illustrates the way you can subclass Follower agents. SmartFollowers just differ in their deliberation in how they approach the target - not what the target is.

ReactivePredator.java an agent that steers towards the location of the closest boid.

ModelPredator.java an agent that anticipates the behavior of boids and steers towards the closest anticipated intersection point. 

# Stub Files

The stub files all provide for agents that you can compile and run. However, by default, these agents have trivial behavior.

The assignment specification indicates the methods that need to be changed or added to these files. Code that you might want to change is also marked by the string TBC in comments, which you can search for. 


