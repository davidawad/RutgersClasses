from collections import defaultdict


class Graph(object):
    """ Graph data structure, undirected by default. """

    def __init__(self, connections, directed=False):
        self._graph = defaultdict(set)
        self._directed = directed
        self.add_connections(connections)

    def add_connections(self, connections):
        """ Add connections (list of tuple pairs) to graph """

        for node1, node2 in connections:
            self.add(node1, node2)

    def add(self, node1, node2):
        """ Add connection between node1 and node2 """

        self._graph[node1].add(node2)
        if not self._directed:
            self._graph[node2].add(node1)

    def remove(self, node):
        """ Remove all references to node """

        for n, cxns in self._graph.iteritems():
            try:
                cxns.remove(node)
            except KeyError:
                pass
        try:
            del self._graph[node]
        except KeyError:
            pass

    def is_connected(self, node1, node2):
        """ Is node1 directly connected to node2 """

        return node1 in self._graph and node2 in self._graph[node1]

    def find_path(self, node1, node2, path=[]):
        """ Find any path between node1 and node2 (may not be shortest) """

        path = path + [node1]
        if node1 == node2:
            return path
        if node1 not in self._graph:
            return None
        for node in self._graph[node1]:
            if node not in path:
                new_path = self.find_path(node, node2, path)
                if new_path:
                    return new_path
        return None

    def __str__(self):
        return '{}({})'.format(self.__class__.__name__, dict(self._graph))


connections = [(1, 2), (1, 6), (2, 1), (2, 5), (3, 5), (4, 1), (4, 7), (5, 2),
               (5, 6), (7, 3), (8, 7), (8, 6)]

g = Graph(connections, directed=True)

population = [1, 0, -1, -1, 0, 0, 0, 1]

spy_index = []

enemy_index = []


for count in range(len(population)):
    if population[count] > 0:
        spy_index.append(count)
    elif population[count] < 0:
        enemy_index.append(count)

for spy in spy_index:
    for enemy in enemy_index:
        path = g.find_path(spy, enemy)
        if path:
            print('yes :' + str(path))
        else:
            continue


'''
The problem here is searching for a path from
one person to another in this graph.

If there is a connection then we return true.
We can again use BFS and get a runtime of O(V + E)

With the list of spies we can then search the graph for every single spy
to see if there is a connection to any enemies.

If they are all spies and we search for all possible connections,
this yeilds a worst case of O(n(V+E))
'''
