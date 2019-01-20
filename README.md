# MazeStartToEnd
Given an nxn matrix with numerous walls, a start and end coordinate. A theoretical robot, starts off at a point inside the maze and must determine where it is inside the maze. Once it has determined it's location it will determine the shortest path from where it is at to the given end coordinate.

# Methodology
# 1)Determine Location
Since we are putting a start coordinate in this program, we could just tell the robot the location. But I wanted to simulate a real scenario where the robot does not know where it at in the maze but can only see what is infront of it.
How we go about this is that we create all the possibilities that the robot could be in based on the size of the matrix and the direction that it is facing.
This would be that there are nxnx4 possibilities.
For example, a 3x3 matrix. The first coordinate: (0,0), the robot could be facing NORTH, EAST , SOUTH, WEST making 4 possibilities.
Do that for each coordinate in the matrix you have 3x3x4=36 possibilites that the robot can be positioned.
As the robot gives input of whether infront of it is a wall and moves around, we slowly eliminate the possibilities until there is only one path to which the robot could possibily have gone.
Once we've narrowed the possibilities down to 1. We record the path and the last position which would denote where the robot is currently on the map.

# 2)Shortest Path
Once we determine it's location, we need to find the shortest path for it to go to the end coordinate. Here we use a backtracking algorithm of possible paths the robot can go. A tree is used to record where it has been so it does not overlap where it has already been.
Once the tree has been created we recursively loop through the tree until we find the shortest branch on the tree which would denote the shortest path.
