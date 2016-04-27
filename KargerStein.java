import java.util.LinkedList;

/**
 * Created by deep on 4/19/16.
 */
public class KargerStein {
    //private Graph G;


    public KargerStein() {
    }

    //minCut
    private Graph minCut(Graph G, int t) {
        while(G.validVertices() > t) {
            int[] vertices = G.selectRandomEdge();
            G.contractEdge(vertices[0], vertices[1]);
        }
        return G;
    }

    //fastMinCut
    public int fastMinCut(Graph G) throws CloneNotSupportedException {
        if(G.validVertices() <= 6) {
            Graph ret_graph = minCut(new Graph(G), 2);
            int ans =  ret_graph.theKargerSteinCut();
            return ans;
        }
        else {
            int t = (int)Math.ceil(G.validVertices() / Math.sqrt(2)) + 1;
            Graph G1 = minCut(new Graph(G), t);
            Graph G2 = minCut(G, t);

            int minimum = Math.min(fastMinCut(G1), fastMinCut(G2));
            return minimum;
        }
    }

    public int fastMinCutAltered(Graph G) throws CloneNotSupportedException {
        if(G.validVertices() <= 6) {
            Graph ret_graph = minCut(new Graph(G), 2);
            int ans =  ret_graph.theKargerSteinCut();
            return ans;
        }
        else {
            int t = (int)Math.ceil(G.validVertices() / Math.sqrt(2)) + 1;
            Graph G1 = minCut(new Graph(G), t);
            Graph G2 = minCut(G, t);

            G1.resize();
            G2.resize();

            int minimum = Math.min(fastMinCut(G1), fastMinCut(G2));
            return minimum;
        }
    }

    public int kargerSteinSolver(Graph G) throws CloneNotSupportedException {
        if(G.disjoint()) {
            System.out.println("disjoint in kargerstein");
            return 0;
        }
        int min = Integer.MAX_VALUE;
        for(int i = 0; i < (int)Math.pow((Math.log(G.size()) / Math.log(2)), 2); i++) {
            int val = fastMinCutAltered(new Graph(G));
            if(min > val)
                min = val;
        }
        return min;
    }

    public static void main(String[] args) throws CloneNotSupportedException {
        Graph graph = new Graph(4, true);
        System.out.println(graph.numberOfEdges());
        KargerStein ks = new KargerStein();
        System.out.println(ks.fastMinCut(graph));
    }
}
