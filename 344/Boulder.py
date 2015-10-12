'''
Output all possible path traversals on a specific chessboard.
Augments the paths array and takes into consideration
the previous move and the last moves
'''
def possible_paths(paths, curr_path, graph, x_pos, y_pos):
    # print('traversing on x:{0}, y:{1}, curr_path: {2}'.format(x_pos, y_pos, curr_path))

    if x_pos > len(graph)-1 or y_pos > len(graph)-1:
        return

    if graph[x_pos][y_pos] == 1:
        return
    # are we at the bottom of the graph?
    if x_pos == y_pos == (len(graph)-1):
        # print("done! x:{0}, y:{1}".format(x_pos, y_pos))
        # path is complete, append this traversal to the list
        print(curr_path)
        paths.append(curr_path)
        return

    if len(curr_path) >= 2:
        if curr_path[-2] == curr_path[-1]:
            # have we made the previous movement twice consecutively,
            # we need to move in a different direction
            if curr_path[-1] == 'r':
                # move down instead
                possible_paths(paths, curr_path + 'd', graph, x_pos, y_pos+1)
            else:
                # move right
                possible_paths(paths, curr_path + 'r', graph, x_pos+1, y_pos)
        else:
            possible_paths(paths, curr_path + 'r', graph, x_pos+1, y_pos)
            possible_paths(paths, curr_path + 'd', graph, x_pos, y_pos+1)

    else:
        possible_paths(paths, curr_path + 'r', graph, x_pos+1, y_pos)
        possible_paths(paths, curr_path + 'd', graph, x_pos, y_pos+1)


def render_paths(board, paths):
    # create an empty array and mark all positions of the original bombs
    # then iterate through the paths and mark them as possible traversals
    modified = [[0] * len(board) for _ in range(len(board))]

    for path in paths:
        x = 0
        y = 0
        for char in path:
            # mark all paths
            modified[x][y] = 1
            if char == 'r':
                x += 1
            else:
                y += 1
    return modified

# [[1, 1, 0, 0, 0, 0], [0, 1, 0, 0, 0, 0], [0, 1, 1, 1, 0, 0], [0, 0, 1, 1, 1, 1], [0, 0, 1, 1, 0, 1], [0, 0, 0, 1, 1, 0]]

board = [
         [0, 0, 1, 0, 1, 1],
         [1, 0, 1, 0, 0, 1],
         [0, 0, 0, 0, 0, 0],
         [0, 0, 0, 0, 0, 0],
         [0, 0, 0, 0, 1, 0],
         [0, 0, 0, 0, 0, 0]
         ]

paths = []

# find all possible paths only going to the right
#  or downward in O(n^2) time, linear in the size of the board
possible_paths(paths, "", board, 0, 0)

# create grid of everywhere the boulders can be moved to in python
print(board)

# modify the original board
modified = render_paths(board, paths)

# print the modified board with the paths marked
print(modified)


'''
Runtime analysis:

We start by doing a modified recursive BFS traversal on the graph,
looking for the vertices that have zeroes.
And as we find complete paths, we append them to an array of paths.
We then iterate through those paths and
mark them as traversable out of an nxn zero-matrix

Any BFS on an adjacency matrix is O(V^2), [or n^2 here]
in addition we have one iteration through all of the possible paths,
which in the worst case is still O(n^2)
'''
