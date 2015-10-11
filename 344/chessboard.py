
def possible_paths(paths, curr_path, graph, x_pos, y_pos):
    # are we at the bottom of the graph?
    if (graph[x_pos] == graph[-1]) and (graph[-1][-1])
        # path is complete, append this traversal to the list
        return curr_path
    # have we made the previous movement twice consecutively?
    if curr_path[-1] == curr_path[-2]:
        return "yolo"


board = [
 [0, 0, 1, 0, 1, 1],
 [1, 0, 1, 0, 0, 1],
 [0, 0, 0, 0, 0, 0],
 [0, 0, 0, 0, 0, 0],
 [0, 0, 0, 0, 1, 0],
 [0, 0, 0, 0, 0, 0]
]

# find all possible paths only going to the right
#  or downward in O(n^2) time
paths = possible_paths(board)

print(paths)
