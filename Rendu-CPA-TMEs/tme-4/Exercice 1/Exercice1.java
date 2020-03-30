import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

public class Exercice1 {

	public static final int INITIAL_SIZE = 1000;
	int nombreTrous = 0;
	public Sommet[] adjVertices;

	public TreeMap<Integer, IntOpenHashSet> nodesWithDegree;
	public HashMap<Integer, IntOpenHashSet> cores;
	public HashMap<Integer, Integer> degreeOfEachNode;
	public int numberOfNode;
	public int nombreArrete ;
	double averageDegreeDensity;
	double edgDensity;
	double sizeDensestCore;

	public Exercice1() {

		adjVertices = new Sommet[INITIAL_SIZE];
		numberOfNode = 0;
		nombreArrete = 0 ;

	}

	// load graph into the adjacency list
	public void loadGraph2(String path) throws FileNotFoundException, IOException {

		try (BufferedReader br = new BufferedReader(

				new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8), 1024 * 1024)) {

			String line;

			while ((line = br.readLine()) != null) {

				if (line == null) // end of file
					break;


				int a = 0;
				int left = -1;
				int right = -1;

				for (int pos = 0; pos < line.length(); pos++) {
					char c = line.charAt(pos);
					if (c == ' ' || c == '\t') {
						if (left == -1)
							left = a;
						else
							right = a;

						a = 0;
						continue;
					}
					if (c < '0' || c > '9') {
						System.out.println("Erreur format ligne ");
						System.exit(1);
					}
					a = 10 * a + c - '0';
				}
				right = a;
		
				// s'assurer qu'on a toujours de la place dans le tableau
				if (adjVertices.length <= left || adjVertices.length <= right) {
					ensureCapacity(Math.max(left, right) + 1);
				}

				if (adjVertices[left] == null) {
					adjVertices[left] = new Sommet(left);
				}

				if (adjVertices[right] == null) {
					adjVertices[right] = new Sommet(right);
				}

				if (adjVertices[left].listeAdjacence.contains(adjVertices[right])) {
					continue;
				} else {
					adjVertices[left].listeAdjacence.add(adjVertices[right]);
					adjVertices[right].listeAdjacence.add(adjVertices[left]);
					nombreArrete++;
				}
			}
		}

		for (int i = 0; i < adjVertices.length; i++) {
			if (adjVertices[i] == null)
				nombreTrous++;
		}
		numberOfNode = adjVertices.length - nombreTrous;
		System.out.println("-----------------------------------------------------------------");
		System.out.println("Loading graph  done !");
		System.out.println("-----------------------------------------------------------------");

		System.out.println("nombreTrous  " + nombreTrous);
		System.out.println("numberOfNode  " + numberOfNode);
		
	}

	private void ensureCapacity(int size) {

		Sommet[] tempSommets = adjVertices;

		adjVertices = new Sommet[size];

		for (int i = 0; i < tempSommets.length; i++) {
			adjVertices[i] = tempSommets[i];
		}

	}

	// TreeMap that stores for each degree the list of its nodes
	private void degrees() {
		nodesWithDegree = new TreeMap<Integer, IntOpenHashSet>();

		// fill the HashMap nodesWithDegree
		for (Sommet entry : adjVertices) {

			if (entry == null)
				continue;

			int size = entry.listeAdjacence.size();

			if (nodesWithDegree.containsKey(size)) {
				nodesWithDegree.get(size).add(entry.numSommet);
			} else {
				IntOpenHashSet temp = new IntOpenHashSet();
				temp.add(entry.numSommet);
				nodesWithDegree.put(size, temp);
			}
		}
		System.out.println("end of degree method");
	}

	private void coreDecomposition() {
		cores = new HashMap<Integer, IntOpenHashSet>();
		List<Integer> tempList = new ArrayList<Integer>();
		degrees();
		int i = numberOfNode;
		int c = 0;
		long startTime = System.nanoTime();

		while (nombreTrous != adjVertices.length) {

			if (nodesWithDegree.get(nodesWithDegree.firstKey()).isEmpty()) {
				nodesWithDegree.remove(nodesWithDegree.firstKey());
			}
			
			int mindegree = nodesWithDegree.firstKey();
			// let's get the list of nodes with degree == mindegree
			IntOpenHashSet listOfNodes = nodesWithDegree.get(mindegree);

			while (!listOfNodes.isEmpty()) {
				if (c < mindegree) {
					c = mindegree;
				}

				int node = listOfNodes.toIntArray()[0];
				// let's delete the edges
				for (Sommet neighbour : adjVertices[node].listeAdjacence) {

					nodesWithDegree.get(adjVertices[neighbour.numSommet].listeAdjacence.size())
							.remove(neighbour.numSommet);

					if (nodesWithDegree.containsKey(adjVertices[neighbour.numSommet].listeAdjacence.size() - 1)) {
						nodesWithDegree.get(adjVertices[neighbour.numSommet].listeAdjacence.size() - 1)
								.add(neighbour.numSommet);
					} else {
						IntOpenHashSet t = new IntOpenHashSet();
						t.add(neighbour.numSommet);
						nodesWithDegree.put(adjVertices[neighbour.numSommet].listeAdjacence.size() - 1, t);
					}

					adjVertices[neighbour.numSommet].listeAdjacence.remove(adjVertices[node]);
					if (adjVertices[neighbour.numSommet].listeAdjacence.size() == mindegree) {
						tempList.add(neighbour.numSommet);
					}

					// degreeOfEachNode.computeIfPresent(neighbour, (k, v) -> v - 1);
				}
				listOfNodes.addAll(tempList);
				adjVertices[node] = null;
				nombreTrous++;
				listOfNodes.remove(node);
				tempList.clear();
				i = i - 1;
				if (cores.containsKey(c)) {
					cores.get(c).add(node);
					//cores.compute(c, (k, v) -> v + 1);
				} else {
					IntOpenHashSet temp = new IntOpenHashSet();
					temp.add(node);
					
					cores.put(c, temp);
				}
			}


		}

		System.out.println("coreDecomposition done ! ");
		long endTime = System.nanoTime();
		long totalTime = endTime - startTime;
		double seconds = (double) totalTime / 1_000_000_000.0;
		System.out.println("running time = " + seconds);
	}

	private void answer() {

		System.out.println("The average degree density : "+(double)nombreArrete/numberOfNode);
		long temp = Math.multiplyExact((long)numberOfNode, (long)numberOfNode-1)/2;
		System.out.println("The edge density : "+(double)nombreArrete/temp);
	}

	public void writeIntoFile(String path1) {
		try {
			FileOutputStream fout = new FileOutputStream(path1);

			String temp = "";
		
			for (Map.Entry<Integer, IntOpenHashSet> entry : cores.entrySet()) {
				IntOpenHashSet nodes = entry.getValue();  
				for(int s : nodes) {
					temp = s+" "+entry.getKey()+"\n";
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
	
	public static void main(String[] args) {

		Exercice1 m = new Exercice1();

		try {
			m.loadGraph2(args[0]);
			//m.loadGraph2("files/test.txt");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("load graph completed ! ");
		
		m.answer();
		m.coreDecomposition();
		System.out.println(m.cores.size());
		int s = 0;
		for (Map.Entry<Integer, IntOpenHashSet> entry : m.cores.entrySet()) {
			//s = s + entry.getValue().size();
			System.out.println("core  : " + entry.getKey() + "   number of nodes = " + entry.getValue().size());

		}
		m.writeIntoFile("corness.txt");
		System.out.println("total nombreArrete  : " + m.nombreArrete);
		
	}	

}
