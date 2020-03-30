import it.unimi.dsi.fastutil.objects.ObjectArrayList;

public class Sommet {
	public int numSommet;
	public ObjectArrayList<Sommet> listeAdjacence;

	public Sommet(int numSommet) {
		this.numSommet = numSommet;
		listeAdjacence = new ObjectArrayList<Sommet>();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + numSommet;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Sommet other = (Sommet) obj;
		if (numSommet != other.numSommet)
			return false;
		return true;
	}
}