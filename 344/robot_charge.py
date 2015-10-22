'''
Output all possible path traversals on a specific chessboard.
Augments the paths array and takes into consideration
the previous move and the last moves
'''
def possible_paths(paths, curr_path, charge, graph, x_pos, y_pos):
    # have we run over the bounds of the graph?
    if not (0 <= x_pos and x_pos <= len(graph)-1 and y_pos <= len(graph)-1 and 0 <= y_pos):
        return

    # print('traversing on x:{0}, y:{1}, charge: {2}, curr_path: {3}'.format(x_pos, y_pos, charge, curr_path))

    # have we visited before?
    if (x_pos, y_pos) in curr_path:
        return

    # are we at the end of the graph?
    if x_pos == y_pos == len(graph)-1:
        # print("done! x:{0}, y:{1}".format(x_pos, y_pos))
        # path is complete, append this traversal to the list
        curr_path.append((x_pos, y_pos))
        paths.append(curr_path)
        return

    # out of energy
    if charge <= 0:
        return
    # charging station
    if graph[x_pos][y_pos] == 1:
        charge = 10

    # now recursively travel in all 4 directions
    curr_path.append((x_pos, y_pos))
    possible_paths(paths, curr_path[:], charge-1, graph, x_pos+1, y_pos)
    possible_paths(paths, curr_path[:], charge-1, graph, x_pos, y_pos+1)
    # possible_paths(paths, curr_path[:], charge, graph, x_pos, y_pos-1)
    # possible_paths(paths, curr_path[:], charge, graph, x_pos-1, y_pos)


# 11 x 11 board
board = [
         [0, 0, 1, 0, 1, 1, 0, 0, 0, 1, 0],
         [1, 0, 1, 0, 0, 1, 1, 0, 0, 0, 0],
         [0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0],
         [0, 0, 0, 0, 0, 0, 1, 1, 0, 1, 0],
         [0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0],
         [0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0],
         [0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0],
         [0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0],
         [0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0],
         [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
         [0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0]
         ]

paths = []

# find all possible paths only going to the right
#  or downward in O(n^2) time, linear in the size of the board
# start with initial energy of 10
possible_paths(paths, [], 10, board, 0, 0)

# create grid of everywhere the boulders can be moved to in python
for path in paths:
    print(path)

'''
Runtime analysis:


So I started by solving a harder problem, which is to find all possible paths
in a matrix, and if that array of possible paths isn't empty, we know there is a possible path.

For a proof of implementation, you can run this file to see that it works in a reasonable amount of time.


To solve the actual problem, We start by doing a modified recursive BFS traversal on the graph,
looking for the vertices that have ones to use as charge stations.
And as we find complete paths, we append them to an array of paths.
We then iterate through those paths and
mark them as traversable out of an nxn zero-matrix

The runtime becomes just like BFS, which is O(N^2)
'''
