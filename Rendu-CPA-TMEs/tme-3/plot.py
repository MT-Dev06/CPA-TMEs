import argparse
import matplotlib.pyplot as plt

x = []
y = []

with open("pageRank.txt", 'r') as f:
    for line in f:
        tab = line[:-1].split(" ")
        x += [float(tab[1])]

xmin = min(x); xmax = max(x)

with open("outDegree.txt", 'r') as f:
    for line in f:
        tab = line[:-1].split(" ")
        y += [float(tab[1])]

ymin =1; ymax = max(y)

print(ymin)
print(ymax)

plt.yscale("log")
plt.xscale("log")
plt.xlim(xmin, xmax)
plt.ylim(ymin, ymax)
plt.scatter(x, y, label="x = PageRank (alpha = 0.15), y = out degree")
plt.xlabel("page Rank")
plt.ylabel("out degree")
plt.legend()
plt.show()
