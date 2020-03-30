import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;

public class Exercice1 {

	// attributes
	public HashMap<Integer, IntOpenHashSet> adjVertices;
	public HashMap<Integer, IntOpenHashSet> cluster1;
	public HashMap<Integer, IntOpenHashSet> cluster2;
	public HashMap<Integer, IntOpenHashSet> cluster3;
	public HashMap<Integer, IntOpenHashSet> cluster4;

	// Initialization
	// cluster 1 : 0 .. 99
	// cluster 2 : 100 .. 199
	// cluster 3 : 200 .. 299
	// cluster 4 : 300 .. 399

	public Exercice1() {
		
		this.adjVertices = new HashMap<Integer, IntOpenHashSet>(200);
	 
		this.cluster1 = new HashMap<Integer, IntOpenHashSet>();
		this.cluster2 = new HashMap<Integer, IntOpenHashSet>();
		this.cluster3 = new HashMap<Integer, IntOpenHashSet>();
		this.cluster4 = new HashMap<Integer, IntOpenHashSet>();

		for (int i = 0; i < 400; i++) {
			IntOpenHashSet temp = new IntOpenHashSet();
			adjVertices.put(i, temp);

			if (i >= 0 && i <= 99) {
				cluster1.put(i, temp);
			} else if (i >= 100 && i <= 199) {
				cluster2.put(i, temp);

			} else if (i >= 200 && i <= 299) {
				cluster3.put(i, temp);

			} else {
				cluster4.put(i, temp);
			}
			
		}

		
	}

	public void connectSameCluster(double p, int start, int end, HashMap<Integer, IntOpenHashSet> cluster) {

		for (int i = start; i < end - 1; i++) {

			for (int j = i + 1; j < end; j++) {
				double random = ThreadLocalRandom.current().nextDouble(0, Double.MAX_VALUE)/Double.MAX_VALUE;

				if (random < p) {
					cluster.get(i).add(j);
					cluster.get(j).add(i);
				}
			}
		}

	}

	public void connectDifferentCluster(double q, int startI, int endI, HashMap<Integer, IntOpenHashSet> clusterI,
			int StartJ, int endJ, HashMap<Integer, IntOpenHashSet> clusterJ) {

		// connect clusterI to clusterJ
		for (int i = startI; i <= endI; i++) {
			for (int j = StartJ; j <= endJ; j++) {
				double random = ThreadLocalRandom.current().nextDouble(0, Double.MAX_VALUE)/Double.MAX_VALUE;

				if (random <= q) {
					clusterI.get(i).add(j);
					clusterJ.get(j).add(i);
				}
			}
		}
	}

	public void writeIntoFile(String path) {
		try {
			FileOutputStream fout = new FileOutputStream(path);
			Set<Entry<Integer, IntOpenHashSet>> set = adjVertices.entrySet();
			Iterator<Entry<Integer, IntOpenHashSet>> iterator = set.iterator();

			set = adjVertices.entrySet();
			iterator = set.iterator();
			String temp = "";

			while (iterator.hasNext()) {

				Map.Entry mentry = (Map.Entry) iterator.next();

				IntOpenHashSet values = (IntOpenHashSet) mentry.getValue();

				for (Integer val : values) {

					temp = String.valueOf(mentry.getKey()) + " " + String.valueOf(val) + "\n";
					byte b[] = temp.getBytes();
					fout.write(b);
				}

			}

			fout.close();
			System.out.println("success writing on the file");
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public void exercise1(double p, double q) {

		// Connect each pair of nodes in the same cluster with a probability < p
		// Cluster 1
		connectSameCluster(p, 0, 99, cluster1);
		System.out.println("cluster 1 part 1 : " + cluster1.get(0).size());
		// Cluster 2
		connectSameCluster(p, 100, 199, cluster2);
		// Cluster 3
		connectSameCluster(p, 200, 299, cluster3);
		// Cluster 4
		connectSameCluster(p, 300, 399, cluster4);
		// Connect Each pair of nodes in different clusters with a probability q <= p
		// --------------------------------------------------------------
		// Cluster 1 to Cluster 2
		connectDifferentCluster(q, 0, 99, cluster1, 100, 199, cluster2);
		// Cluster 1 to Cluster 3
		connectDifferentCluster(q, 0, 99, cluster1, 200, 299, cluster3);
		// Cluster 1 to Cluster 4
		connectDifferentCluster(q, 0, 99, cluster1, 300, 399, cluster4);
		System.out.println("cluster 1 part 2 : " + cluster1.get(0).size());

		// --------------------------------------------------------------
		// Cluster 2 to cluster 3
		connectDifferentCluster(q, 100, 199, cluster2, 200, 299, cluster3);
		// Cluster 2 to cluster 4
		connectDifferentCluster(q, 100, 199, cluster2, 300, 399, cluster4);
		// --------------------------------------------------------------
		// Cluster 3 to Cluster 4
		connectDifferentCluster(q, 200, 299, cluster3, 300, 399, cluster4);

	}	
	
	public static void main(String[] args) {

		Exercice1 m = new Exercice1();
		m.exercise1(0.8, 0.1);
		m.writeIntoFile("myGraph.txt");
				
	}

}
