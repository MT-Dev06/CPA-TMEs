import it.unimi.dsi.fastutil.objects.ObjectArrayList;

class Sommet {
    public int               numSommet;
    public ObjectArrayList<Sommet> listeAdjacence;

    public Sommet( int numSommet ) {
        this.numSommet = numSommet;
        listeAdjacence = new ObjectArrayList<Sommet>();
    }

}
