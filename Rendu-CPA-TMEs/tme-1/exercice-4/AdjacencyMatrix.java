import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class AdjacencyMatrix {
	
	private static final long MEGABYTE = 1024L * 1024L;
    public static long bytesToMegabytes(long bytes) {
        return bytes / MEGABYTE;
    }
    
	private final int nbVertices;

	private int[][] adjacencyMatrix;
	
	public int[][] getAdjacencyMatrix() {
		return adjacencyMatrix;
	}

	public AdjacencyMatrix(int nbNodes) {

		nbVertices = nbNodes;
		adjacencyMatrix = new int[nbVertices][nbVertices];
	}

	public void makeEdge(int to, int from) {
		try {

			adjacencyMatrix[to][from] = 1;

		} catch (ArrayIndexOutOfBoundsException index) {
			System.out.println("The vertices does not exists");
		}
	}

	public int getEdge(int to, int from) {

		try {
			return adjacencyMatrix[to][from];
		}

		catch (ArrayIndexOutOfBoundsException index) {
			System.out.println("The vertices does not exists");
		}
		return -1;
	}


	public void loadGraph(String path) throws FileNotFoundException, IOException {

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
				makeEdge(left, right);
		
			}
		}	
	}

	public static void main(String args[]) {
		AdjacencyMatrix m = new AdjacencyMatrix(Integer.parseInt(args[1])) ; 
		try {
			m.loadGraph(args[0]);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Runtime runtime = Runtime.getRuntime();
        runtime.gc();
        long memory = runtime.totalMemory() - runtime.freeMemory();
        System.out.println("Used memory is bytes: " + memory);
        System.out.println("Used memory is megabytes: "
                + bytesToMegabytes(memory));
				
	}

}
