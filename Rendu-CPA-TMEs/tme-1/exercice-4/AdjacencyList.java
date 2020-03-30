
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

public class AdjacencyList {

	// -----------------------------------------

	public static final int INITIAL_SIZE = 100;
	private static final long MEGABYTE = 1024L * 1024L;
	
    public static long bytesToMegabytes(long bytes) {
        return bytes / MEGABYTE;
    }
	public Sommet[] adjVertices;

	// init
	public AdjacencyList() {
		adjVertices = new Sommet[INITIAL_SIZE];
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
				// calculer le nombre de boucles
				if (left == right) {
					continue;
				}
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

	}

	// load graph into the adjacency list
	
	public static void main(String[] args) throws IOException {

		AdjacencyList myGraph = new AdjacencyList();

		// now let's load the graph
		// you need to specify the path of the file as an argument to the loadGraph
		try {
			myGraph.loadGraph2(args[0]);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		Runtime runtime = Runtime.getRuntime();
		runtime.gc();
		long memory = runtime.totalMemory() - runtime.freeMemory();
		System.out.println("Used memory is bytes: " + memory);
		System.out.println("Used memory is megabytes: " + bytesToMegabytes(memory));
	}
}
