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


connections = [('X', 'Z'), ('Y', 'X'), ('Y', 'W'), ('W', 'Z'), ('Z', 'U')]

nodes = ['U', 'W', 'X', 'Y', 'Z']

g = Graph(connections, directed=True)

for i in nodes:
    for j in nodes:
        if g.find_path(i, j):
            if len(g.find_path(i, j)) == len(nodes)-1:
                # variable exists that implies all others.
                print('PATH EXISTS, variable is :' + i)


'''
We can just perform a bfs through all nodes to all others,
one of them could have a path that goes through all others.

Such as Y.

So we have an n^2 traversal through all nodes and an O(V+E)
traversal through the graph.

worst case is n^2 + 2n
'''
