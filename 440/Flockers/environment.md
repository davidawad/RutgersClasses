Writing Environment Specifications

The skeleton code provides infrastructure for reading and writing specifications for simulations. The specifications are encoded as XML documents. You have already seen this in the maze assignment, but the specifications for the flocking assignment are much more complicated, so we've provided more extensive documentation for them here.

If you are not familiar with XML, check out external link: the Wikipedia entry. Basically, an XML document defines a tree of data. Each node in the tree corresponds to a tag, with a type and a list of attribute-value pairs. Nodes can be atomic, in which case there is a single tag marking the node description as complete. Or nodes can have children, in which case the node opens with a beginning tag, then continues with a nested specification of the node's children, then ends with a closing tag. XML is a convenient way to read in and out data because it's a standard, flexible format and java provides easy and efficient tools for reading it.

The XML reading code is located in the file FlockingReader.java. The code uses the SAX interface (see external link: this quickstart guide), which processes each XML document incrementally and provides data from the XML file to an application using callbacks. In FlockingReader.java, the startElement callback does all the interesting work of creating and updating agents in the simulation.
The world

In this project, the root node of the XML file is a world tag. It describes the complete simulation. Individual agent specifications are nested in groups (see below) and included as children of the world element. The world currently provides the following attributes:

    width and height specify the rectangular extent of the simulation window, in pixels.
    time specifies the delay between calculating successive steps of simulation. this is useful to slow down a simulation to see more clearly what is happening or to speed up a simulation to "get to the good part" faster.
    logfile specifies a new file to record an exact XML specification of everything that happens in this run of the program. That includes user interface actions that you do to move agents around!
    runnable is a boolean value. If this is false, the simulation will play through the data described in the XML file and then quit. Otherwise, after playing through the data, the simulation will continue driving the world indefinitely.
    debug is a boolean value. If this is true, the world will visualize debugging information about the state of the simulation. Right now this just displays the number of steps of simulation that have taken place, but you might want to add to this behavior if you are having particular difficulties.

Subgroups in the world

If you just want to create a simulation and run it, you can nest the specification of the individual agents that you want to create directly within the world element.

However, there are two useful structuring elements that let you organize the specification of a world further.

    The defaults tag allows you to specify the common attributes of all subsequent agents in a single place. Within the defaults element, elements describing kinds of agents are not treated as instructions to create or update an existing agent. Instead they update the default parameter values by which subsequent agents will be created. This can be useful if you want to do something general, like turning on or off particular features for all the agents in a simulation.
    The state tag allows you to group together the status of agents at a particular moment in time. The skeleton code uses state elements to organize simulation traces in XML. Each step of the simulation corresponds to its own state element in a log file. The attribute step of a state indicates the number of the time step as displayed on the screen in debug mode. You can use this to find the exact description of a moment where something unexpected has happened.

In addition, there are two further elements that do not correspond to agents but instead describe how the world should evolve.

    The wait tag says that the simulation should pause. The length of the pause is the value of the time attribute, and should be an integer value in milliseconds.
    The kill tag says that a specific agent should be removed from the rest of the simulation. The agent to remove is specified by the id attribute and should be the integer id value of an agent previously created in the world.

Individual agents

The skeleton code includes code you can look at and play with for seven simple kinds of agents, most of which we have gone through during course lectures or recitations. These are:

    light a stationary object that serves as a point of interest for other agents. You can think of it as a light source, sound source, or food source. This agent is defined in the file LightSource.java
    obstacle a stationary object that other agents cannot pass through but will instead collide with. This agent is defined in the file Obstacle.java
    runner a basic agent, like Braitenberg's vehicle one, that proceeds indefinitely on an initial course. This agent is defined in the file Runner.java
    follower the local light follower, which always aims to go forward towards the closest light source, but because it has a limited turning radius may find itself "orbiting" the light rather than getting right to it. This agent is defined in the file Follower.java
    shadower the smarter light follower, which recognizes when the light source is placed too close and too far off to the side to approach directly, and in such cases backs up turning to enable a direct approach. This agent is defined in the file SmartFollower.java
    reactive-predator a chasing agent that steers towards the closest boid. This agent is defined in the file ReactivePredator.java
    model-predator a chasing agent that predicts the motions of boids, and steers towards the closest intercept point where it would reach a boid, if the boid continued on its current path. This agent is defined in the file ModelPredator.java

In addition, the skeleton code includes a stub that you can extend for an additional kind of agent. The initial version of the XML reading code provides interfaces so your project will automatically read and write instances of these agents. Of course, nothing will happen yet! The agent will always just continue to move forward, because these new agents have no rules for creating intelligent behavior! The tag for these agents is:

    flocker For the flocking agent that you write.

All agents come with a range of attributes and values that you can set from XML. These are:

    size how big the agent is drawn, in pixels
    forwardMax the fastest the agent can go forward, in pixels per step
    backwardMax the fastest the agent can go backward, in pixels per step
    accelMax the biggest increase possible in the agent's speed (ie., when the agent is actually speeding up), in pixels per step per step
    decelMax the biggest decrease possible in the agent's speed (ie., when the agent is slowing down), in pixels per step per step
    maxTurn the largest angle in degrees that the agent can turn per step
    strength the number of "hit points" that the agent has in conflicts with other agents
    viewAngle the angle in degrees from straight ahead that the agent can see to either side (thus, e.g., 90 means the agent can just see forward while 180 means the agent can see everything in the world)
    r the red component of the color with which the agent is to be drawn (an integer between 0 and 255, inclusive)
    g the green component of the color with which the agent is to be drawn (an integer between 0 and 255, inclusive)
    b the blue component of the color with which the agent is to be drawn (an integer between 0 and 255 inclusive)
    x the horizontal coordinate of the agent on the screen
    y the vertical coordinate of the agent on the screen
    heading the direction that the agent is facing, in degrees, 0 is horizontal to the right, 90 is straight down, -90 is straight up, etc.
    speed the rate at which the agent is advancing in the direction heading - can be positive or (in the case of certain agents that sometimes go backwards) negative
    debug whether to visualize extra information in the rendering to help explain what the agent is doing

The input and output of these parameters are managed by nested classes FixedAgentAttributes and DynamicAgentAttributes in the file Agent.java.

Finally, certain individual agents have some additional parameters. The flocker agents have a whole bunch of parameters that control the details of the flocking algorithm. These parameters are:

    clear whether this agent avoids collisions with obstacles and predators
    evade whether this agent avoids collisions with neighboring boids
    align whether this agent aligns with the orientation of neighboring boids
    center whether this agent moves to the average position of neighboring boids
    follow whether this agent is attracted by light sources
    clearance the distance at which this agent starts to avoid collisions with obstacles
    cone the angle to left or right that counts as an obstacle
    separation the distance at which this agent starts to avoid collisions with neighbors
    detection the distance at which this agent starts to align or center with neighboring boids
    ow relative weight of force to avoid obstacles
    sw relative weight of force to avoid collisions
    aw relative weight of force to align with neighbors
    cw relative weight of force to move towards neighbors' average position
    lw relative weight of force to follow lights
