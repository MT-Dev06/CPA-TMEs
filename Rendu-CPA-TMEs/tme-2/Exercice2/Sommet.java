import it.unimi.dsi.fastutil.objects.ObjectArrayList;

public class Sommet {
	public int numSommet;
	public ObjectArrayList<Sommet> listeAdjacence;
	public int label;

	public Sommet(int numSommet) {
		this.numSommet = numSommet;
		listeAdjacence = new ObjectArrayList<Sommet>();
		label = numSommet;
	}
}
