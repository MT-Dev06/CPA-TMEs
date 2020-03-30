import argparse
import matplotlib.pyplot as plt


x = [0 for _ in range(int(287426))]
y = [0 for _ in range(int(287426))]

with open("degree.txt", 'r') as f:
    for line in f:
        tab = line[:-1].split(" ")
        x[int(tab[0])] = float(tab[1])

xmin = min(x); xmax = max(x)
print(xmin)
print(xmax)

with open("corness.txt", 'r') as f:
    for line in f:
        tab = line[:-1].split(" ")
        y[int(tab[0])] = float(tab[1])

ymin = min(y); ymax = max(y)

plt.yscale("log")
plt.xscale("log")
plt.xlim(xmin/10, xmax*10)
plt.ylim(ymin/10, ymax*10)
plt.plot([xmin/10,xmax*10], [xmin/10,xmax*10], ls="-", color="k")
plt.scatter(x, y, label="x = Degree, y = Coreness")
plt.xlabel("Degree")
plt.ylabel("Coreness")
plt.legend()
plt.show()
