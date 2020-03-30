import argparse
import matplotlib.pyplot as plt

x = []
y = []

with open("pr1.txt", 'r') as f:
    for line in f:
        tab = line[:-1].split(" ")
        x += [float(tab[1])]

xmin = min(x); xmax = max(x)

with open("pr2.txt", 'r') as f:
    for line in f:
        tab = line[:-1].split(" ")
        y += [float(tab[1])]

ymin = min(y); ymax = max(y)

plt.yscale("log")
plt.xscale("log")
plt.xlim(xmin, xmax)
plt.ylim(ymin, ymax)
plt.scatter(x, y, label="x = PageRank (alpha = 0.15), y = PageRank (alpha = 0.9)")
plt.xlabel("alpha = 0.15")
plt.ylabel("alpha = 0.9")
plt.legend()
plt.show()
