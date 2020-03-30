import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

class Cluster {

    List<Integer> sommets     = new ArrayList<Integer>();
    int           sommeDegree = 0;
    int           index;

    public String getSommetsAsLine() {
        String result = "";
        for ( Integer integer : sommets ) {
            result += integer;
            result += " ";
        }
        return result;
    }

    @Override
    public String toString() {
        return sommets + "";
    }

}

class ClusterPaire {

    public Cluster c1;
    public Cluster c2;
    public double  incrementModularite;

    public ClusterPaire( Cluster c1, Cluster c2, double incrementModularite ) {
        this.c1 = c1;
        this.c2 = c2;
        this.incrementModularite = incrementModularite;
    }

    @Override
    public String toString() {
        return "ClusterPaire [c1=" + c1 + ", c2=" + c2 + ", incrementModularite=" + incrementModularite + "]";
    }

}

class ClusterPaireComparator implements Comparator<ClusterPaire> {

    @Override
    public int compare( ClusterPaire o1, ClusterPaire o2 ) {

        if ( o1.incrementModularite < o2.incrementModularite ) {
            return 1;
        }

        if ( o1.incrementModularite > o2.incrementModularite ) {
            return -1;
        }

        return 0;
    }

}

class Graphe {

    public static final int INITIAL_SIZE = 1;

    public Sommet[]         sommets;
    public int              nombreArrete;
    public int              nombreBoucles;
    public int              nombreDoublons;
    public int              nombreSommets;
    public int              nombreTrous;
    public Sommet           sommetDegreeMax;

    public Graphe() {
        sommets = new Sommet[INITIAL_SIZE];
        nombreArrete = 0;
        nombreBoucles = 0;
        nombreTrous = 0;
    }

    public void loadGraph( String path ) throws FileNotFoundException, IOException {

        try ( BufferedReader br = new BufferedReader(

                new InputStreamReader( new FileInputStream( path ), StandardCharsets.UTF_8 ), 1024 * 1024 ) ) {

            String line;

            while ( ( line = br.readLine() ) != null ) {

                if ( line == null ) // end of file
                    break;

                if ( line.charAt( 0 ) == '#' ) {
                    // commentaire
                    continue;
                }

                int a = 0;
                int left = -1;
                int right = -1;

                for ( int pos = 0; pos < line.length(); pos++ ) {
                    char c = line.charAt( pos );
                    if ( c == ' ' || c == '\t' ) {
                        if ( left == -1 )
                            left = a;
                        else
                            right = a;

                        a = 0;
                        continue;
                    }
                    if ( c < '0' || c > '9' ) {
                        System.out.println( "Erreur format ligne " );
                        System.exit( 1 );
                    }
                    a = 10 * a + c - '0';
                }
                right = a;
                // calculer le nombre de boucles
                if ( left == right ) {
                    nombreBoucles++;
                    continue;
                }
                // s'assurer qu'on a toujours de la place dans le tableau
                if ( sommets.length <= left || sommets.length <= right ) {
                    ensureCapacity( Math.max( left, right ) + 1 );
                }

                if ( sommets[left] == null ) {
                    sommets[left] = new Sommet( left );
                }

                if ( sommets[right] == null ) {
                    sommets[right] = new Sommet( right );
                }

                if ( sommets[left].listeAdjacence.contains( sommets[right] ) ) {
                    nombreDoublons++;
                    continue;
                } else {
                    sommets[left].listeAdjacence.add( sommets[right] );
                    sommets[right].listeAdjacence.add( sommets[left] );
                    nombreArrete++;
                }
            }
        }
        System.out.println( "-----------------------------------------------------------------" );
        System.out.println( "Loading graph  done !" );
        System.out.println( "-----------------------------------------------------------------" );
    }

    /**
     * 
     * @param size
     *            Cette méthoode permet d'augmenter la capacité de tableau
     *            Contrairement à un simple ArrayList qui double la taille à
     *            chaque fois ici on s'assure que la taille de tableau == numéro
     *            de sommet le plus haut, en d'autre term la taille de tableau
     *            == nombre de sommets
     * 
     */
    private void ensureCapacity( int size ) {

        Sommet[] tempSommets = sommets;

        sommets = new Sommet[size];

        for ( int i = 0; i < tempSommets.length; i++ ) {
            sommets[i] = tempSommets[i];
        }

    }

}

class Partition {

    public List<Cluster>               partition;
    public Graphe                      graphe;
    public long                        nbArreteAuCarreMultiplierPar4;
    public double                      meilleurIncrementModularite = Integer.MIN_VALUE;
    public double                      modularite                  = 0.0;
    public double                      meilleurModularite          = Integer.MIN_VALUE;
    public ClusterPaireComparator      comparator                  = new ClusterPaireComparator();
    public PriorityQueue<ClusterPaire> queue;
    public List<Cluster>               deletedClusters             = new ArrayList<Cluster>();
    public String                      filePath                    = "files/result.clu";
    public boolean                     fileIsWritten               = false;
    public int 						   numberClustersBestModularity = 0;
    // matIJ = matrice de nombre de liens entres clusters ( intialement =
    // matrice d'adjacence)
    public int[][]                     matIJ;

    public Partition( Graphe graphe ) {
        this.graphe = graphe;
        partition = new ArrayList<Cluster>();
        matIJ = new int[graphe.sommets.length][graphe.sommets.length];
        nbArreteAuCarreMultiplierPar4 = (long) ( 4 * Math.pow( graphe.nombreArrete, 2 ) );
    }

    public int m( Cluster i, Cluster j ) {
        int result = 0;
        // Même clusler => return nombre d’arêtes internes au cluster Vi
        if ( i == j ) {

            // pour chaque sommet de cluster i
            for ( int node : i.sommets ) {
                // pour chaque sommet de la liste d'adjacence d'un sommet de
                // cluster i
                for ( Sommet s : graphe.sommets[node].listeAdjacence ) {
                    // si ce test == true => le cluster j à une arrete vers
                    // le
                    // sommet s
                    if ( j.sommets.contains( s.numSommet ) ) {
                        result++;
                    }
                }
            }
            // on divise sur deux
            result = result / 2;
        }
        // le nombre d’arêtes ayant une extrémité dans i et l’autre dans j
        else {
            // pour chaque sommet de cluster i
            for ( int node : i.sommets ) {
                // pour chaque sommet de la liste d'adjacence d'un sommet de
                // cluster i
                for ( Sommet s : graphe.sommets[node].listeAdjacence ) {
                    // si ce test == true => le cluster j à une arrete vers
                    // le
                    // sommet s
                    if ( j.sommets.contains( s.numSommet ) ) {
                        result++;
                    }
                }
            }
        }

        return result;
    }

    public double e( Cluster i, Cluster j ) {

        return (double) m( i, j ) / (double) graphe.nombreArrete;
    }

    public double a( Cluster i, Cluster j ) {
        double result = 0.0;
        if ( i != j ) {
            for ( int nodeI : i.sommets ) {
                for ( int nodeJ : j.sommets ) {
                    result = result + ( ( (double) graphe.sommets[nodeI].listeAdjacence.size() )
                            / ( (double) 2 * graphe.nombreArrete ) )
                            * ( ( (double) graphe.sommets[nodeJ].listeAdjacence.size() )
                                    / ( (double) 2 * graphe.nombreArrete ) );
                }
            }
        } else {
            int som = 0;
            for ( int nodeI : i.sommets ) {
                som = som + graphe.sommets[nodeI].listeAdjacence.size();
            }
            result = (double) ( som * som ) / (double) ( 4 * graphe.nombreArrete * graphe.nombreArrete );
        }

        return result;
    }

    public double q() {
        double result = 0.0;
        for ( Cluster cluster : partition ) {
            result = result + ( e( cluster, cluster ) - a( cluster, cluster ) );
        }
        return result;
    }

    // ---------------------------------------------------------------------------------------------------------

    // return l'incerment de modularité si on fusionne le cluster c1 &
    // cluster
    // c2
    public double incrementModularite( Cluster c1, Cluster c2 ) {
        int somDegreeC1 = c1.sommeDegree;
        int somDegreeC2 = c2.sommeDegree;
        int indexC1 = c1.index;
        int indexC2 = c2.index;
        double x = (double) matIJ[indexC1][indexC2] / (double) graphe.nombreArrete;
        double y = (double) Math.pow( ( somDegreeC1 + somDegreeC2 ), 2 )
                / (double) nbArreteAuCarreMultiplierPar4;
        double z1 = (double) Math.pow( somDegreeC1, 2 ) / (double) nbArreteAuCarreMultiplierPar4;
        double z2 = (double) Math.pow( somDegreeC2, 2 ) / (double) nbArreteAuCarreMultiplierPar4;
        return x - y + z1 + z2;

    }

    // prends le cluster a & b les fusionne en un seul cluster c
    // postCondition :
    // 1 : c.sommeDgeree = a.sommeDgeree + b.sommeDgeree
    // 2 : c.index = a.index
    // 3 : matIJ(c.index , c.index ) = matIJ(a.index , a.index ) +
    // matIJ(a.index
    // , b.index ) + matIJ(b.index , b.index )
    // : AND forAll d with d != a,b,c => matIJ(c.index , d.index ) =
    // matIJ(a.index , d.index )+matIJ(b.index , d.index )
    // 4 : supprimer le cluster a & cluser b
    public Cluster fusionneCluster( Cluster a, Cluster b ) {
        Cluster c = new Cluster();
        c.sommeDegree = a.sommeDegree + b.sommeDegree;
        c.index = a.index;
        matIJ[c.index][c.index] = matIJ[a.index][a.index] + matIJ[b.index][b.index] + matIJ[a.index][b.index];
        for ( Cluster d : partition ) {
            if ( d == a || d == b || d == c )
                continue;
            matIJ[c.index][d.index] = matIJ[a.index][d.index] + matIJ[b.index][d.index];
            matIJ[d.index][c.index] = matIJ[c.index][d.index];
        }
        c.sommets.addAll( a.sommets );
        c.sommets.addAll( b.sommets );
        partition.add( c );
        deletedClusters.add( a );
        deletedClusters.add( b );
        partition.remove( a );
        partition.remove( b );
        return c;
    }

    // load matIJ
    public void loadMatIJ() {
        for ( Sommet s : graphe.sommets ) {
            if ( s == null )
                continue;
            for ( Sommet x : s.listeAdjacence ) {
                matIJ[s.numSommet][x.numSommet] = 1;
            }
        }
    }

    // un cluster = un sommet
    public void initPartition() {
        for ( Sommet s : graphe.sommets ) {
            if ( s == null )
                continue;
            Cluster c = new Cluster();
            c.index = s.numSommet;
            c.sommeDegree = s.listeAdjacence.size();
            c.sommets.add( s.numSommet );
            partition.add( c );
        }
    }

    public void initModularite() {
        double temp = 0.0;
        for ( Cluster c : partition ) {
            temp += Math.pow( (double) c.sommeDegree, 2 );
        }
        temp = -1 * ( temp / nbArreteAuCarreMultiplierPar4 );
        modularite = temp;
    }

    // initialiser le tas = combainison possible = k!/((k-2)!*2!)
    public void initPriorityQueue() {
        queue = new PriorityQueue<ClusterPaire>( partition.size(), comparator );
        int j = 1;
        int t = 1;
        for ( int i = 0; i < partition.size(); i++ ) {
            if ( partition.get( i ) == null )
                continue;
            while ( j < partition.size() ) {
                if ( i == j )
                    continue;
                Cluster c1 = partition.get( i );
                Cluster c2 = partition.get( j );
                ClusterPaire clusterPaire = new ClusterPaire( c1, c2, incrementModularite( c1, c2 ) );
                // System.out.println( clusterPaire );
                queue.add( clusterPaire );
                j++;
            }
            t++;
            j = t;
        }
    }

    public void addClusterToQueue( Cluster c ) {
        for ( Cluster temp : partition ) {
            if ( temp == c )
                continue;
            ClusterPaire cp = new ClusterPaire( c, temp, incrementModularite( c, temp ) );
            queue.add( cp );
        }
    }

    // return les deux cluster à fusionner parmis les k*k possiblités
    // méthode
    // naive
    public Cluster[] getClusterToMerge() {
        Cluster[] result = new Cluster[2];
        double bestIncrementModularite = Integer.MIN_VALUE;
        for ( Cluster c1 : partition ) {
            for ( Cluster c2 : partition ) {
                if ( c1 == c2 )
                    continue;
                double temp = incrementModularite( c1, c2 );
                if ( bestIncrementModularite < temp ) {
                    bestIncrementModularite = temp;
                    result[0] = c1;
                    result[1] = c2;
                }
            }
        }
        modularite += bestIncrementModularite;

        if ( meilleurModularite < modularite )
            meilleurModularite = modularite;

        if ( meilleurIncrementModularite < bestIncrementModularite )
            meilleurIncrementModularite = bestIncrementModularite;
        return result;
    }

    // return les deux cluster à fusionner en utilisant la queue
    public Cluster[] getClusterToMergeUsingQueue() {
        Cluster[] result = new Cluster[2];
        boolean trouve = false;
        while ( !trouve ) {
            ClusterPaire cp = queue.poll();
            if ( !( deletedClusters.contains( cp.c1 ) || deletedClusters.contains( cp.c2 ) ) ) {
                trouve = true;
                result[0] = cp.c1;
                result[1] = cp.c2;
                modularite += cp.incrementModularite;
                if ( meilleurModularite < modularite )
                    meilleurModularite = modularite;

                if ( !fileIsWritten ) {
                    if ( meilleurModularite > modularite ) {
                        ecrirePartition();
                        fileIsWritten = true;
                    }
                }

            }
        }

        return result;
    }

    public void ecrirePartition() {
        File file = new File( filePath );
        FileWriter fr = null;
        numberClustersBestModularity = partition.size();
        try {
            fr = new FileWriter( file );
            for ( int i = 0; i < partition.size() - 1; i++ ) {
                String data = partition.get( i ).getSommetsAsLine();
                data += "\n";
                fr.write( data );
            }
            String data = partition.get( partition.size() - 1 ).getSommetsAsLine();
            fr.write( data );
        } catch ( IOException e ) {
            e.printStackTrace();
        } finally {
            // close resources
            try {
                fr.close();
            } catch ( IOException e ) {
                e.printStackTrace();
            }
        }
    }

    public void executeLouvain() {
        initPartition();
        loadMatIJ();
        initModularite();
        initPriorityQueue();
        boolean stop = false;
        while ( partition.size() != 1 ) {
            Cluster[] clusterAFusionner = getClusterToMergeUsingQueue();
            Cluster clusterMerged = fusionneCluster( clusterAFusionner[0], clusterAFusionner[1] );
            addClusterToQueue( clusterMerged );
        }
    }

    public void displayMatIJ() {
        for ( int i = 0; i < matIJ.length; i++ ) {
            for ( int j = 0; j < matIJ[i].length; j++ ) {
                System.out.print( matIJ[i][j] + " " );
            }
            System.out.println();
        }
    }

    public void displayParition() {
        for ( Cluster c : partition ) {
            System.out.println( c );
        }
    }

    public Cluster[] executePaire() {
        Cluster[] result = new Cluster[3];
        for ( Cluster c1 : partition ) {
            for ( Cluster c2 : partition ) {
                if ( c1 != c2 ) {
                    int somDegreeC1 = c1.sommeDegree;
                    int somDegreeC2 = c2.sommeDegree;
                    double x = ( (double) m( c1, c2 ) / (double) graphe.nombreArrete );
                    double y = ( (double) Math.pow( ( somDegreeC1 + somDegreeC2 ), 2 )
                            / (double) nbArreteAuCarreMultiplierPar4 );
                    double z1 = (double) Math.pow( somDegreeC1, 2 ) / (double) nbArreteAuCarreMultiplierPar4;
                    double z2 = (double) Math.pow( somDegreeC2, 2 ) / (double) nbArreteAuCarreMultiplierPar4;
                    double tempIncrementModularite = x - y + z1 + z2;
                    if ( tempIncrementModularite > meilleurIncrementModularite ) {
                        meilleurIncrementModularite = tempIncrementModularite;
                        result[0] = c1;
                        result[1] = c2;
                    }
                }
            }
        }
        return result;
    }

    public int calculSommeDegree( Cluster c ) {
        int som = 0;
        for ( Integer s : c.sommets ) {
            som += graphe.sommets[s].listeAdjacence.size();
        }
        return som;
    }

    // ---------------------------------------------------------------------------------------------------------

    public void loadPartition( String path ) throws FileNotFoundException, IOException {
        try ( BufferedReader br = new BufferedReader(
                new InputStreamReader( new FileInputStream( path ), StandardCharsets.UTF_8 ), 1024 * 1024 ) ) {
            String line;
            while ( ( line = br.readLine() ) != null ) {
                Cluster cluster = new Cluster();
                if ( line == null ) // end of file
                    break;
                int a = 0;
                for ( int pos = 0; pos < line.length(); pos++ ) {
                    char c = line.charAt( pos );
                    if ( c == ' ' || c == '\t' ) {
                        cluster.sommets.add( a );
                        a = 0;
                        continue;
                    }
                    if ( c < '0' || c > '9' ) {
                        System.out.println( "Erreur format ligne " );
                        System.exit( 1 );
                    }
                    a = 10 * a + c - '0';
                }
                int s = calculSommeDegree( cluster );
                cluster.sommeDegree = s;
                partition.add( cluster );
            }
        }
        
    }

}

class Sommet {
    public int               numSommet;
    public ArrayList<Sommet> listeAdjacence;

    public Sommet( int numSommet ) {
        this.numSommet = numSommet;
        listeAdjacence = new ArrayList<Sommet>();
    }
}

public class Exercice3 {

    public static void main( String[] args ) {

      	String nomGraphe = args[0];
      	
        String nomFichier = args[1];
        
        Graphe graphe = new Graphe();
        try {
            graphe.loadGraph( nomGraphe );
        } catch ( FileNotFoundException e ) {
            System.out.println( "Fichier introuvé " );
        } catch ( IOException e ) {
            System.out.println( "Erreur dans le fichier " );
        }

        Partition partition = new Partition( graphe );

            partition.filePath = nomFichier;
            partition.executeLouvain();
            System.out.println( "meilleur  modularité = " + partition.meilleurModularite );
            System.out.println( "nombre de clusters = " + partition.numberClustersBestModularity );
            
            

        }

    }
