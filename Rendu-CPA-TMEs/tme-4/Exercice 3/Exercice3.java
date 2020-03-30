import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

class MutableInt {
	int value = 1; // note that we start at 1 since we're counting

	public void increment() {
		++value;
	}

	public int get() {
		return value;
	}
}

public class Exercice3 {
	public static final int INITIAL_SIZE = 1000;
	int nombreTrous = 0;

	public Sommet[] adjVertices;

	double[] r;

	HashMap<Double, MutableInt> res;

	double densestPrefix = 0;
	public int numberOfNode;
	public int nombreArrete;

	public void initR() {
		for (int i = 0; i < r.length; i++) {
			r[i] = 0.0;
		}
	}

	public Exercice3() {

		adjVertices = new Sommet[INITIAL_SIZE];
		numberOfNode = 0;
		nombreArrete = 0;
		res = new HashMap<Double, MutableInt>(1000);
	}

	// load graph into the adjacency list
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
				// calculer le nombre de boucles

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
//
//				if (adjVertices[left].listeAdjacence.contains(adjVertices[right])) {
//					continue;
//				} else {
//					adjVertices[left].listeAdjacence.add(adjVertices[right]);
//					adjVertices[right].listeAdjacence.add(adjVertices[left]);
//					nombreArrete++;
//				}
				//if (adjVertices[right].listeAdjacence.contains(adjVertices[left])) continue ; 
				nombreArrete++;
				adjVertices[left].listeAdjacence.add(adjVertices[right]);

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
		System.out.println("numberOfNode   " + numberOfNode);
	}

	private void ensureCapacity(int size) {

		Sommet[] tempSommets = adjVertices;

		adjVertices = new Sommet[size];

		for (int i = 0; i < tempSommets.length; i++) {
			adjVertices[i] = tempSommets[i];
		}

	}

	public void mkScore(int t) {
		r = new double[adjVertices.length];
		initR();
		for (int i = 0; i < t; i++) {
			for (Sommet s : adjVertices) {
				if (s == null)
					continue;
				for (Sommet v : s.listeAdjacence) {
					if (r[s.numSommet] <= r[v.numSommet]) {
						r[s.numSommet]++;
					} else {
						r[v.numSommet]++;

					}
				}

			}
		}

		for (int i = 0; i < r.length; i++) {

			r[i] = r[i] / (double) (t);
			
			MutableInt count = res.get(r[i]);
			if (count == null) {
				res.put(r[i], new MutableInt());
			}
			else {
			    count.increment();
			}
		}

		double max = 0.0;
		for (Map.Entry<Double, MutableInt> ele : res.entrySet()) {
			if (max < ele.getKey())
				max = ele.getKey();
		}

		densestPrefix = max;
	}

	public static void main(String[] args) {

		Exercice3 d = new Exercice3();

		try {
			// newAmazon
			// newLJ
			// newOrkut
			// newEmail
			//d.loadGraph2("files/test2.txt");
			d.loadGraph2(args[0]);
			
		} catch (IOException e) {
			e.printStackTrace();
		}

		d.mkScore(Integer.parseInt(args[1]));
		
		for (Map.Entry<Double, MutableInt> edge : d.res.entrySet()) {
			System.out.println("node : " + edge.getKey() + "   value : " + edge.getValue().value);
		}

		System.out.println("densest prefix = " + d.densestPrefix + "  size = " + d.res.get(d.densestPrefix).value);
		
	}

}
