
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

public class Exercice6 {

	private int numberOfNode;

	public static final int INITIAL_SIZE = 10000;

	public Sommet[] adjVertices;
	int nombreTrous = 0;

	// init
	public Exercice6() {
		adjVertices = new Sommet[INITIAL_SIZE];
		numberOfNode = 0;

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

	// compute the number of triangle in the graph
	public int nbTriangle() {
		long startTime = System.nanoTime();

		int result = 0;
		// fill the node degree map
		int h = 0;
		for (int j = 0; j < adjVertices.length; j++) {
			if (adjVertices[j] == null)
				adjVertices[j] = new Sommet(-1);
		}
		System.out.println("taille du tableau  " + adjVertices.length);

		// sort the map in non-increasing order
		Arrays.sort(adjVertices);

		System.out.println("end sorting");

		for (Sommet entry : adjVertices) {

			if (entry.numSommet == -1)
				continue;

			int u = entry.numSommet;
			ObjectArrayList<Sommet> temp = entry.listeAdjacence;
			// U is a set contenant les nodes de u tq forall x in uSet we have entry2 > x
			Set<Sommet> U = new HashSet<Sommet>();
			for (Sommet w : temp) {
				if (u > w.numSommet)
					U.add(w);
			}

			Iterator<Sommet> i = U.iterator();

			while (i.hasNext()) {

				Sommet v = i.next();

				ObjectArrayList<Sommet> temp2 = v.listeAdjacence;

				ObjectArrayList<Sommet> V = new ObjectArrayList<Sommet>();

				for (Sommet w : temp2) {
					if (v.numSommet < w.numSommet)
						V.add(w);
				}

				List<Sommet> triangles = new ArrayList<Sommet>(U);
				triangles.retainAll(V);

				result = result + triangles.size();
				triangles.clear();
			}
		}

		long endTime = System.nanoTime();
		long totalTime = endTime - startTime;
		double seconds = (double) totalTime / 1_000_000_000.0;

		System.out.println("Running time = " + seconds);

		return result;

	}



	public static void main(String[] args) throws IOException {

		Exercice6 myGraph = new Exercice6();

		try {
			myGraph.loadGraph2(args[0]);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	System.out.println("------ number or triangle = : " + myGraph.nbTriangle());


	}
}
