
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;

public class PageRank {

	// -----------------------------------------

	public double[] C;
	public int[] I;
	public int[] L;
	public long nombreArrete = 0;
	public int maxID = 0;
	public int nombreSommet;
	public IntOpenHashSet sommets;

	// init

	public void loadGraph(String path) throws FileNotFoundException, IOException {
		System.out.println("------ Start loading the Graph");
		sommets = new IntOpenHashSet();
		int nbLine = 0;
		Map<Integer, List> listAdj = new LinkedHashMap<Integer, List>();
		try (BufferedReader br = new BufferedReader(

				new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8), 1024 * 1024)) {

			String line;

			while ((line = br.readLine()) != null) {
				nbLine++;
				if (line == null) // end of file
					break;

				if (line.equals(""))
					continue;

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

				listAdj.putIfAbsent(left, new ArrayList());
				listAdj.get(left).add(right);

				if (maxID < left)
					maxID = left;
				if (maxID < right)
					maxID = right;
				sommets.add(left);
				sommets.add(right);

				nombreArrete++;

			}
		}
		nombreSommet = sommets.size();
		System.out.println("------ End load graph");
		System.out.println("------ Start loading the CLI");
		loadCLI2(listAdj);

	}

	public void loadCLI2(Map<Integer, List> map) {
		C = new double[(int) nombreArrete];
		I = new int[(int) nombreArrete];
		L = new int[(int) maxID + 2];
		Iterator<Map.Entry<Integer, List>> it = map.entrySet().iterator();
		int key = 0;
		L[0] = 0;
		int k = 0;
		// Remplire C et I
		while (it.hasNext()) {
			Map.Entry<Integer, List> pair = it.next();
			key = pair.getKey();
			int temp = pair.getValue().size();
			// add to C AND I
			int p = 0;
			for (int i = k; i < temp + k; i++) {
				C[i] = 1.0 / temp;
				I[i] = (int) pair.getValue().get(p);
				p++;
			}
			k += temp;

		}

		// Remplire L

		for (int i = 0; i < maxID + 1; i++) {
			// Le sommet i est un trou
			if (!sommets.contains(i)) {
				// L.add(i+1, L.get(i));
				L[i + 1] = L[i];
			} else {
				// les sommet puis
				if (!map.containsKey(i)) {
					L[i + 1] = L[i];

				} else {
					L[i + 1] = L[i] + map.get(i).size();
				}
			}
		}

		System.out.println("------ End load CLI");
	}

	private double[] produitMatVecteur(double[] v) {

		double[] p = new double[maxID + 1];
		for (int i = 0; i < maxID + 1; i++) {
			// p.add((double) 0);
			p[i] = 0.0;
		}

		for (int i = 0; i < L.length - 1; i++) {
			int index = L[i];
			int nextIndex = L[i + 1];
			for (int t = index; t < nextIndex; t++) {

				p[I[t]] = p[I[t]] + C[t] * v[i];

				// p[I[t]]+= new BigDecimal(C[t]).multiply(new BigDecimal(v[i])).doubleValue();
			}
		}
		return p;
	}

	public double[] powerIteration(double alpha, int t) {
		// P = (1 − d) × T × P + d × I
		// I: vector with entries = 1/n
		// Usually 0.1 ≤ d ≤ 0.2.

		double[] v = new double[maxID + 1];
		for (int i = 0; i < maxID + 1; i++) {
			if (sommets.contains(i)) {
				// v.add(1.0/(nombreSommet));
				v[i] = 1.0 / nombreSommet;
			} else {
				// v.add(0.0);
				v[i] = 0.0;
			}
		}

		double[] P = v;
		for (int k = 0; k < t; k++) {
			P = produitMatVecteur(P);
			for (int j = 0; j < P.length; j++) {
				if (!sommets.contains(j))
					continue;
				P[j] = ((1 - alpha) * P[j]) + (alpha / (double) nombreSommet);
			}

			P = normalize2(P);

			System.out.println(" k =  " + k);

			System.out.println("-------------------------");
		}

		return P;
	}

	public void displaySommeP(double[] p) {
		double x = 0.0;
		for (double z : p) {
			x += z;
		}
		System.out.println(x);
	}

	public double[] normalize2(double[] p) {
		double som = 0.0;
		for (double x : p) {
			som += x;
		}
		for (int i = 0; i < p.length; i++) {
			if (!sommets.contains(i))
				continue;
			p[i] = p[i] + ((1 - som) / nombreSommet);
		}
		return p;
	}

	public void displayVecteur(double[] p) {
		for (double x : p) {
			System.out.println(x);
		}
	}

	public void displayC() {
		for (double x : C) {
			System.out.print(x);
			System.out.print("\t");
		}
	}

	public void displayI() {
		for (int x : I) {
			System.out.print(x);
			System.out.print("\t");
		}
	}

	public void displayL() {
		for (int x : L) {
			System.out.print(x);
			System.out.print("\t");
		}
	}

	public void writeIntoFile(String path1, double[] res) {
		try {
			FileOutputStream fout = new FileOutputStream(path1);

			String temp = "";

			for (int i = 0; i < res.length; i++) {
				if (!sommets.contains(i))
					continue;
				temp = i + " " + res[i] + "\n";
				byte b[] = temp.getBytes();
				fout.write(b);
			}

			fout.close();
			System.out.println("success writing on the file");
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public void writeIntoFile(String path1, int[] res) {
		try {
			FileOutputStream fout = new FileOutputStream(path1);

			String temp = "";

			for (int i = 0; i < res.length; i++) {
				if (!sommets.contains(i))
					continue;
				temp = i + " " + res[i] + "\n";
				byte b[] = temp.getBytes();
				fout.write(b);
			}

			fout.close();
			System.out.println("success writing on the file");
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public int[] inDegree() {
		int[] res = new int[maxID + 1];
		for (int i = 0; i < I.length; i++) {
			res[I[i]]++;
		}
		return res;
	}

	public int[] outDegree() {
		int[] res = new int[maxID + 1];
		for (int i = 0; i < L.length - 1; i++) {
			if (!sommets.contains(i))
				continue;
			int index = L[i];
			int nextIndex = L[i + 1];
			res[i] = nextIndex - index;
		}
		return res;
	}

	public static void main(String[] args) {

		PageRank pr = new PageRank();
		try {
			//pr.loadGraph("files/alr21--dirLinks--enwiki-20071018.txt");
			pr.loadGraph(args[0]);
			//pr.loadGraph("files/test4.txt");
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("------ Start the page rank");
		//double[] res = pr.powerIteration(0.1, 20);
		double[] res = pr.powerIteration(Double.parseDouble(args[1]), Integer.parseInt(args[2]));

		System.out.println("------ End page rank");
		System.out.println("------ Write the result in a file");
		pr.writeIntoFile("pageRank.txt", res);
		System.out.println("------ End");
		System.out.println("------ Compute the inDgree and write the result in a file");
		pr.writeIntoFile("inDegree.txt", pr.inDegree());
		System.out.println("------ End");
		System.out.println("------ Compute the outDegree and write the result in a file");
		pr.writeIntoFile("outDegree.txt",pr.outDegree());
		System.out.println("------ End");
		System.out.println("------ Getting the 5 pages with the highest PageRank and the 5 pages with the lowest PageRank");
		
		double max = 0;
		int indice = 0;

		double min = 1;
		int indiceMin = 0;

		Map<Integer, Double> maxPageRank = new LinkedHashMap<Integer, Double>();
		Map<Integer, Double> minPageRank = new LinkedHashMap<Integer, Double>();

		for (int j = 0; j < 5; j++) {

			for (int i = 0; i < res.length; i++) {
				if (pr.sommets.contains(i)) {
					if (res[i] == -1.0)
						continue;

					if (max < res[i]) {
						max = res[i];
						indice = i;
					}
					if (min > res[i]) {
						min = res[i];
						indiceMin = i;
					}
				}

			}

			maxPageRank.put(indice, max);
			res[indice] = -1.0;
			minPageRank.put(indiceMin, min);
			res[indiceMin] = -1.0;
			max = 0;
			min = 1;
	}

		System.out.println("------ MaxPageRank");
		System.out.println(maxPageRank);

		System.out.println("------ MinPageRank");
		System.out.println(minPageRank);

		
	
	}
}
