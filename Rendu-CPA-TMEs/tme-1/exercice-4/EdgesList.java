import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class EdgesList {

	public ArrayList<int[]> list;
	public static final int INITIAL_SIZE = 10000;
	private static final long MEGABYTE = 1024L * 1024L;

    public static long bytesToMegabytes(long bytes) {
        return bytes / MEGABYTE;
    }
	public EdgesList() {

		list = new ArrayList<int[]>(INITIAL_SIZE);
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
				int[] edge = new int[2];
				edge[0] = left;
				edge[1] = right;
				list.add(edge);				
			}
		}	
	}
	
	public void displayGraph() {
		for (int[] edge : list) {
			System.out.println(edge[0]+" "+edge[1]);
		}
	}

	public static void main(String[] args) {
		EdgesList e = new EdgesList() ; 
		try {
			e.loadGraph(args[0]);
	        Runtime runtime = Runtime.getRuntime();
	        runtime.gc();
	        long memory = runtime.totalMemory() - runtime.freeMemory();
	        System.out.println("Used memory is bytes: " + memory);
	        System.out.println("Used memory is megabytes: "
	                + bytesToMegabytes(memory));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
}
