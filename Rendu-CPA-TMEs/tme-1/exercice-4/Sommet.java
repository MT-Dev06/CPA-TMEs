import it.unimi.dsi.fastutil.objects.ObjectArrayList;

class Sommet implements Comparable<Sommet>{
    public int               numSommet;
    public ObjectArrayList<Sommet> listeAdjacence;

    public Sommet( int numSommet ) {
        this.numSommet = numSommet;
        listeAdjacence = new ObjectArrayList<Sommet>();
    }

	@Override
	public int compareTo(Sommet o) {
		return o.listeAdjacence.size()-this.listeAdjacence.size();
	}
}