import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

public class DegreeOfNodes {

	public static final int INITIAL_SIZE = 1000;
	public int nombreTrous = 0;
	public int numberOfNode;
	public int nombreArrete;
	public Sommet[] adjVertices;

	public DegreeOfNodes() {
		adjVertices = new Sommet[INITIAL_SIZE];
		numberOfNode = 0;
		nombreArrete = 0;
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

	public void writeDegreeDistribution(String path) throws FileNotFoundException, IOException {

		String temp = "";
		FileOutputStream fout = new FileOutputStream(path);

		for (Sommet sommet : adjVertices) {
			if (sommet == null)
				continue;
			temp = sommet.numSommet + " " + sommet.listeAdjacence.size() + "\n";
			byte b[] = temp.getBytes();
			fout.write(b);

		}

		fout.close();
		System.out.println("success writing on the file");
	}

	public static void main(String[] args) throws FileNotFoundException, IOException {

		DegreeOfNodes d = new DegreeOfNodes() ; 
		
		d.loadGraph2(args[0]);
		d.writeDegreeDistribution("degree.txt");
		
	}
}

