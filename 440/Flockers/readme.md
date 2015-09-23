# Flockers


The point of this project is to explore strategic interactions among groups of agents. The programming part is to complete skeleton code in java to create a basic agent. Then you evaluate the agent by creating environment specifications that illustrate specific kinds of qualitative behavior. Finally, you have the option to extend the agent in light of your evaluation to give new qualitative behavior, in open-ended ways, for extra credit.



###Flocking

Your task in this assignment is to implement and explore the flocking algorithm.

The skeleton code Flocker.java resembles the other agents that are provided in the project source code, such as the Follower class. There are two important additions to the skeleton code file Flocker.java, which take the form of inner classes.

The FlockerAttributes class handles input and output of the properties that determine flocker behavior. Each Flocker includes an instance flocking of the FlockerAttributes, which you can access in determining the behavior of the agent. For example, flocker attributes allow you to turn on and off the individual behaviors and forces that each flocker performs, and this is reflected in the skeleton code.
The WeightedForce class provides infrastructure for manipulating and especially visualizing the factors that go into flocker decision making. In particular, the skeleton code creates instance members in the Flocker class for each of the potential forces, and ensures that the force is drawn if the debug attribute of the Flocker is set.

Your job is to implement the different forces, then create scenarios that illustrate typical flocking behavior. Extensions allow you to experiment with the expressive capabilities of the simulation.


Your Flocker should potentially reflect a balance among five different priorities. They are:


####Avoiding obstacles (clearance)
####Avoiding other flockers (separation)
####Aligning with nearby flockers (alignment)
####Moving to the center of nearby flockers (centering)
####Approaching light sources (following)




 The way a Flocker balances these priorities is using the WeightedForce object. In particular, the basic algorithm for Flocker decision making goes like this.

```shell
Start with a new force with weight 1 and heading 0 (straight ahead)
For of the five flocker priorities
   If the agent is sensitive to that priority
      Calculate its force in the current situation
   Otherwise
       The relevant force is zero
Create the intention to move in the direction of the overall force on the flocker, at full speed

```

 This amounts to the pseudocode for the new deliberate method in your flocker. You will see that the skeleton code already contains the rough outline of this algorithm. What you have to do is make sure that each force is calculated correctly, and that you calculate the correct overall force based on these components.

Here is a brief explanation of how to calculate each force.

####Clearance.
Find all the obstacles that the flocker can see (and predators and whatever else you think this flocker will want to avoid, besides lights and other flockers). If the obstacle is closer than the distance flocker.clearance and lies within the angle bounded by flocking.cone on either side of the flocker's direct heading, add in a force to avoid that obstacle. That force should:

Be weighted inversely proportional to the distance to the obstacle
Assume a weight of flocking.obstacleWeight when the obstacle is flocking.clearance away
Go in the direction of flocking.cone or -flocking.cone, whichever turns the flocker most quickly away from the obstacle.

Note: There are lots of different directions you could imagine steering to avoid an obstacle, as Braitenberg already points out. You could imagine steering backwards, or directly to one side or another. You might want to play with the different strategies. The strategy above, more or less, is the one Craig Reynolds recommends in his original paper. It balances better with the other flocking forces, which will tend to keep a group of agents flying more or less forward. A directly opposed clearance force would need to be very strong and would lead to unstable and, perhaps, unlikely behaviors.



#### Separation

 Find all the nearby boids that the flocker can see. If they are closer than the distance flocking.separation, then add in a force to avoid that boid. The force should:

Be weighted inversely proportional to the distance to the other boid.
Assume a weight of flocking.separationWeight when the obstacle is flocking.separation away
Go in the opposite direction of the other boid.

Note: In this case, we do steer away from the other boid, because we imagine this force typically being applied in the middle of crowds, where the boids are ''jockeying for position'' with subtle movements to one side or another.

####Alignment

Find the nearby boids that the flocker can see that are closer than the distance flocking.detection but further than flocking.separation. Create a force with a weight of flocking.alignmentWeight that points in the average orientation of these boids. (To average, add all the forces together and then reweight by 1/n where n is the number of relevant boids.

Note: You want the force to depend on the average orientation because that way there is the same alignment force on each flocker regardless of the size of the flock around it. That means you only have to tune the balance among the different priorities once for flocks of many different sizes.

####Centering

Consider the same nearby boids as the alignment force. Create a force with a weight of flocking.centeringWeight that points to the average position of these boids


#### Following

Find the closest light, and create a force towards it. The force should have a weight of followWeight.

Note: You could imagine having the force vary with the distance to the light. If the force increases with distance, it steers the flock as a whole towards lights; if it increases with the inverse of distance, it makes sure that individual agents near lights are sure to go out of their way to eat them.


##Testing

inside the examples folders are the sample data and the sample runs

The best way to test the flocker is to implement the forces one at a time, visualizing them as you go. These test cases showcase the effects of individual forces.

external link: worksite:/Projects/Flocking/Examples/fo1.xml The obstacle force should make a boid steer away from obstacles. external link: sample run
external link: worksite:/Projects/Flocking/Examples/fs1.xml The separation force should make a boid steer away from nearby boids. external link: sample run
external link: worksite:/Projects/Flocking/Examples/fa1.xml The alignment force should make a boid steer in the same direction as nearby boids. external link: sample run
external link: worksite:/Projects/Flocking/Examples/fc1.xml The centering force should make a boid nestle in among nearby boids. external link: sample run
external link: worksite:/Projects/Flocking/Examples/ff1.xml The light following force should make a boid go after lights. external link: sample run

Once you have the forces working individually, then you can start to look at how they interact with one another in simple cases. For example, external link: worksite:/Projects/Flocking/Examples/fl1.xml shows three boids going around an obstacle to reach a light. external link: sample run

Finally you can work with fancier files that show more interesting behavior, like external link: this one which shows a predator chasing a big flock. external link: sample run


## Analysis

Create three specific files, showing some of the strengths and weaknesses of flocking.

####split.xml shows a flock splitting and then merging back together as it goes around and obstacle.
####stuck.xml shows a flock unable to get around an obstacle to some food because the flock is not capable of doing long-term planning but instead just follows its immediate drives.
####story.xml a demonstration file showing some interesting, fun and perhaps surprisingly complex behavior from a flock, which we can use as a conversation piece in class.

## Extensions

Make the behavior of your flockers more sophisticated, interesting or realistic. As always the extensions should get turned on only if the XML property with-extensions and therefore the internal variable form.withExtensions are set.

Possible examples include:

Having flockers interact differently with other boids of different colors
Having flockers explicitly react to predators, for example by scattering.

Another option is to create an auxiliary program that makes interesting random worlds for flockers to navigate through.

## Submit

Flocker.java - Your implmentation
split.xml, stuck.xml and story.xml your examples
Any additional code you create for extensions (for example to make random worlds).

## Debugging

Set the colors of agents with different behavior to be different on the screen so you can see which agent is which. Use the XML attributes r, g, and b.

Edit the world's time parameter so that things step slowly enough for you to see moments where decisions are made.

Use debug mode to watch for particular time steps (which will be displayed on the screen) and use the logfile feature to check the exact parameters for your agents at that time (by searching in the logfile for the string step="N if you are interested in time N).

Draw extra information on the screen when your agents are in debug mode to make sure they are behaving the way you expect. Take advantage of the drawing methods in the World class that handle out-of-bounds data correctly:

```java

    fillPolygon(int xpoints, int ypoints, int nPoints, Graphics g)
    fillOval(int x, int y, int width, int height, Graphics g)
    fillRect(int x, int y, int width, int height, Graphics g)
    drawLine(int x1, int y1, int x2, int y2, Graphics g)
```

Remember that drawing occurs immediately after an agent has carried out a step of action and the environment has been updated accordingly. If you want to visualize aspects of the agent's state from the last round of deliberation, use the Agent member lastStatus (which is an object of class DynamicAgentAttributes).





##Links

[Project Requirements](https://sakai.rutgers.edu/portal/tool/8937f6eb-00fc-444d-88f9-1e59bbd126ae?pageName=%2Fsite%2Fab466b6d-ff95-468f-a5f9-20363bea4476%2Fhw2+requirements&action=view&panel=Main&realm=%2Fsite%2Fab466b6d-ff95-468f-a5f9-20363bea4476)


[]()
