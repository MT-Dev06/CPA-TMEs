import argparse
import matplotlib.pyplot as plt
import networkx as nx

graph = nx.Graph()
graph.add_edges_from(nx.read_edgelist("communities.txt").edges())

colorMap = ['r','b','g','y','p','c']
colors = ['r' for _ in range(0, 400)]
with open("communities.txt", 'r') as cfile:
    for line in cfile:
        line = line.rstrip().split(' ')
        node = int(line[0]); com = int(line[1])
        colors[node] = com

colorSet = list(set(colors))

colors = [colorMap[colorSet.index(c)] for c in colors]

nx.draw(graph, node_size=20, node_color=colors)
plt.show();
