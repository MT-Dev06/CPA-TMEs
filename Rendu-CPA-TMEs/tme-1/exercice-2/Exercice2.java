
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

public class Exercice2 {

	public int[] sizeGraph(String path) throws FileNotFoundException, IOException {

		HashMap<Integer, Integer> map = new HashMap<Integer, Integer>(10000);
		int[] result = new int[2];

		result[0] = 0;
		result[0] = 0;
		
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
				map.putIfAbsent(left, 1);
				map.putIfAbsent(right, 1);
				// increase the number of lines
				result[1] = result[1] + 1;
		}

	}
		result[0] = map.size();
		return result;
	}
	
	public static void main(String[] args) throws IOException {

		Exercice2 myGraph = new Exercice2();
		int[] sizeGraph = null ;
		try {
			sizeGraph = myGraph.sizeGraph(args[0]);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		System.out.println("Nombre de sommet = "+sizeGraph[0]);
		System.out.println("Nombre arretes = "+sizeGraph[1]);

		
	}
}
