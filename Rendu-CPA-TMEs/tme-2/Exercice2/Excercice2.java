import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

public class Excercice2 {

	public static final int INITIAL_SIZE = 1000;
	int nombreTrous = 0;
	public Sommet[] adjVertices;
	public HashMap<Integer, Integer> label;
	public HashMap<Integer, Integer> communities;
	Random r = new Random();
	public ArrayList<Integer> randomOrder;
	
	public int numberOfNode;
	
	public Excercice2() {

		adjVertices = new Sommet[INITIAL_SIZE];
		numberOfNode = 0;
		label = new HashMap<Integer, Integer>();
		communities = new HashMap<Integer, Integer>();
		randomOrder = new ArrayList<Integer>();
	}

	// load graph into the adjacency list
	public void loadGraph2(String path) throws FileNotFoundException, IOException {

		try (BufferedReader br = new BufferedReader(

				new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8), 1024 * 1024)) {

			String line;

			while ((line = br.readLine()) != null) {

				if (line == null) // end of file
					break;

				if (line.charAt(0) == '#') {
					// commentaire
					continue;
				}

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

		for (int i = 0; i < adjVertices.length; i++) {
			if (adjVertices[i] == null)
				nombreTrous++;
		}
		numberOfNode = adjVertices.length - nombreTrous;
		System.out.println("-----------------------------------------------------------------");
		System.out.println("Loading graph  done !");
		System.out.println("-----------------------------------------------------------------");

		System.out.println("nombreTrous  " + nombreTrous);
	}

	private void ensureCapacity(int size) {

		Sommet[] tempSommets = adjVertices;

		adjVertices = new Sommet[size];

		for (int i = 0; i < tempSommets.length; i++) {
			adjVertices[i] = tempSommets[i];
		}

	}

	private static void shuffleList(List<Integer> a) {
		int n = a.size();
		Random random = new Random();
		random.nextInt();
		for (int i = 0; i < n; i++) {
			int change = i + random.nextInt(n - i);
			swap(a, i, change);
		}
	}

	private static void swap(List<Integer> a, int i, int change) {
		int helper = a.get(i);
		a.set(i, a.get(change));
		a.set(change, helper);
	}

	private void initializeRandomOrderAndLabel() {

		for (Sommet entry : adjVertices) {

			if (entry == null) {
				randomOrder.add(-10);
				continue;
			}

			label.put(entry.numSommet, entry.numSommet);
			randomOrder.add(entry.numSommet);
		}

		shuffleList(randomOrder);

	}

	private int getRandomNumberInRange(int min, int max) {

		return r.ints(min, (max + 1)).findFirst().getAsInt();

	}

	public void labelPropagation() {

		initializeRandomOrderAndLabel();

		for (int b = 0; b < 200; b++) {
			for (Integer randNode : randomOrder) {

				if (randNode == -10)
					continue;

				HashMap<Integer, Integer> occur = new HashMap<Integer, Integer>(50);

				ObjectArrayList<Sommet> neighbours = adjVertices[randNode].listeAdjacence;

				int maxOccur = -1;
				Sommet theneighbor = null;

				for (Sommet n : neighbours) {

					int l = n.label;

					if (occur.containsKey(l)) {
						int temp = occur.compute(l, (k, v) -> v + 1);

						if (temp > maxOccur) {
							maxOccur = temp;
							theneighbor = n;
						}
					} else {
						occur.put(l, 1);
					}

				}

				if (theneighbor == null) {
					theneighbor = neighbours.get(getRandomNumberInRange(0, neighbours.size() - 1));
				}

				adjVertices[randNode].label = theneighbor.label;
				label.put(randNode, label.get(theneighbor));

			}

			System.out.println("itération : " + b);

		}

		for (Sommet s : adjVertices) {
			if (s == null)
				continue;
			if (communities.containsKey(s.label)) {
				communities.compute(s.label, (k, v) -> v + 1);
			} else {
				communities.put(s.label, 1);
			}
		}

	}

	public void displayCommunities() {
		for (Map.Entry<Integer, Integer> entry : communities.entrySet()) {
			System.out.println("communitie : " + entry.getKey() + " number of nodes =  " + entry.getValue());
		}
	}

	public void writeIntoFile2(String path1) {
		try {
			FileOutputStream fout = new FileOutputStream(path1);

			String temp = "";

			for (Sommet sommet : adjVertices) {
				if(sommet == null )  continue ; 
				temp = sommet.numSommet + " " + sommet.label + "\n";
				byte b[] = temp.getBytes();
				fout.write(b);
			}
			
			fout.close();
			System.out.println("success writing on the file");
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	public void writeIntoFile(String path1) {
		try {
			FileOutputStream fout = new FileOutputStream(path1);

			String temp = "";

			for (Map.Entry<Integer, Integer> com : communities.entrySet()) {
				temp = String.valueOf(com.getKey()) + " " + String.valueOf(com.getValue()) + "\n";
				byte b[] = temp.getBytes();
				fout.write(b);

			}

			fout.close();
			System.out.println("success writing on the file");
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public static void main(String[] args) {

		Excercice2 m = new Excercice2();
		try {
			m.loadGraph2(args[0]);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Load graph complet");

		m.labelPropagation();
		
		System.out.println("End of labelPropagation complet");
		
		m.writeIntoFile2("communities.txt");
		
		System.out.println("Writing the result in a file complet");

		System.out.println("nb com = " + m.communities.size());
	}

}
