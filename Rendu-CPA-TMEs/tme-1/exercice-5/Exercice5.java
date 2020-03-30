
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

public class Exercice5 {

	private static int lastButOneNodeVisited = 0;
	private static int lastNodeVisited = 0;

	/// Attributes
	// private HashMap<Integer, IntOpenHashSet> adjVertices;
	private int lowerBoundGraph;
	private int numberOfNode;
	private double fractionOfNodes;

	// -----------------------------------------

	public static final int INITIAL_SIZE = 10000;

	public Sommet[] adjVertices;
	int nombreTrous = 0;

	// init
	public Exercice5() {
		adjVertices = new Sommet[INITIAL_SIZE];
		lowerBoundGraph = 0;
		numberOfNode = 0;
		fractionOfNodes = 0;
	}

	/**
	 * 
	 * @param size Cette méthoode permet d'augmenter la capacité de tableau
	 *             Contrairement à un simple ArrayList qui double la taille à chaque
	 *             fois ici on s'assure que la taille de tableau == numéro de sommet
	 *             le plus haut, en d'autre term la taille de tableau == nombre de
	 *             sommets
	 * 
	 */
	private void ensureCapacity(int size) {

		Sommet[] tempSommets = adjVertices;

		adjVertices = new Sommet[size];

		for (int i = 0; i < tempSommets.length; i++) {
			adjVertices[i] = tempSommets[i];
		}

	}

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
		

	}

	// getters and setters
	public int getLowerBountGraph() {
		return lowerBoundGraph;
	}

	public int getNumberOfNode() {
		return numberOfNode;
	}

	public double getFractioOfNode() {
		return fractionOfNodes;
	}

	// load graph into the adjacency list

	
	// Breadth First Traversal Algorithm
		public IntOpenHashSet breadthFirstTraversal(int root) {
			//System.out.println("root =  "+root);
			IntOpenHashSet visited = new IntOpenHashSet();
			Queue<Integer> queue = new LinkedList<Integer>();
			queue.add(root);
			visited.add(root);
			while (!queue.isEmpty()) {
				int vertex = queue.poll();
				for (Sommet v : adjVertices[vertex].listeAdjacence) {
					if (!visited.contains(v.numSommet)) {
						lastButOneNodeVisited = v.numSommet;
						visited.add(v.numSommet);
						queue.add(v.numSommet);
					}
				}
			}
			return visited;
		}

	// compute the diameter of the graph
	public int diameterGraph(int root) {

		IntOpenHashSet visited = new IntOpenHashSet();
		Queue<Integer> queue = new LinkedList<Integer>();

		queue.add(lastButOneNodeVisited);
		visited.add(lastButOneNodeVisited);

		HashMap<Integer, Integer> pred = new HashMap<Integer, Integer>();

		while (!queue.isEmpty()) {
			int vertex = queue.poll();
			for (Sommet v : adjVertices[vertex].listeAdjacence) {
				if (!visited.contains(v.numSommet)) {
					pred.put(v.numSommet, vertex);
					lastNodeVisited = v.numSommet;
					visited.add(v.numSommet);
					queue.add(v.numSommet);
				}
			}
		}

		// calcul short path

		int lastButOne = lastButOneNodeVisited;
		int last = lastNodeVisited;

		boolean trouve = false;
		int cpt = 0;
		while (!trouve) {
			int p = pred.get(last);
			cpt++;
			if (p == lastButOne) {
				trouve = true;
			}
			last = p;

		}

		return cpt;

	}

	// for better performance and to reduce redundancy I regroup all questions asked
	// in exercise 8 into one single method
	public ArrayList<IntOpenHashSet> exercise5() {

		ArrayList<IntOpenHashSet> list = new ArrayList<>();
		IntOpenHashSet component;

		boolean finish = false;
		// this variable is used to calculate the fraction of nodes in the largest
		// connected component.
		int numberNodesOfLargestComponent = 0;

		while (nombreTrous != adjVertices.length) {
			// root is the first element from the Adjacency list
			int z = 0;
			int root = z;
			while (true) {
				if (adjVertices[z] != null) {
					root = adjVertices[z].numSommet;
					break;
				}
				z++;
			}
			// get the component related to the root
			component = breadthFirstTraversal(root);

			if (component.size() > numberNodesOfLargestComponent) {
				numberNodesOfLargestComponent = component.size();
			}
			// add it to the list
			list.add(component);

			int firstNodeOfComponent = (int) component.toArray()[0];
			// get the diameter of the component
			int diam = diameterGraph(firstNodeOfComponent);
			// find the lower bound
			if (diam > lowerBoundGraph) {
				lowerBoundGraph = diam;
			}

			// remove all component from the Adjacency list

			for (int t = 0; t < adjVertices.length; t++) {
				if (component.contains(t)) {
					adjVertices[t] = null;
					nombreTrous++;
				}
			}
			// repeat the operation with the loop
		}

		fractionOfNodes = (double) numberNodesOfLargestComponent / numberOfNode;

		return list;

	}

	public static void main(String[] args) throws IOException {

		Exercice5 myGraph = new Exercice5();

		try {
			myGraph.loadGraph2(args[0]);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		System.out.println("The number of nodes =  : " + myGraph.getNumberOfNode());

		System.out.println("Display the information related to exercise 5");
		ArrayList<IntOpenHashSet> Components = myGraph.exercise5();
		int i = 1;
		System.out.println("The number of Components = : " + Components.size());
		for (IntOpenHashSet c : Components) {
			System.out.println("Component number " + i + " - Number of nodes = " + c.size());
			i++;
		}
		System.out.println(
				"------ The fraction of nodes in the largest connected component = : " + myGraph.getFractioOfNode());
		System.out.println("------ lower bound to the diameter of a graph = : " + myGraph.getLowerBountGraph());


	}
}
