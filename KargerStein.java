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
//        System.out.println("in mincut, t: "+t);
        while(G.validVertices() > t) {
            int[] vertices = G.selectRandomEdge();
            G.contractEdge(vertices[0], vertices[1]);
        }
        return G;
    }

    //fastMinCut
    public int fastMinCut(Graph G) throws CloneNotSupportedException {
//        System.out.println("Running fastmincut for vertices: "+G.validVertices());
        if(G.validVertices() <= 6) {
            Graph ret_graph = minCut(new Graph(G), 2);
            int ans =  ret_graph.theKargerSteinCut();
//            System.out.println("found solution : "+ans);
            return ans;
        }
        else {
            int t = (int)Math.ceil(G.validVertices() / Math.sqrt(2)) + 1;
            Graph G1 = minCut(new Graph(G), t);
//            System.out.println("size: "+G1.validVertices());
            Graph G2 = minCut(new Graph(G), t);
//            System.out.println("size: "+G2.validVertices());

            int minimum = Math.min(fastMinCut(G1), fastMinCut(G2));
//            System.out.println("the minimum found is : "+minimum);
            return minimum;
        }
    }

    public int kargerSteinSolver(Graph G) throws CloneNotSupportedException {
        if(G.disjoint()) {
            System.out.println("disjoint in kargerstein");
            return 0;
        }
        int min = Integer.MAX_VALUE;
        for(int i = 0; i < Math.pow((Math.log(G.size()) / Math.log(2)), 2); i++) {
            int val = fastMinCut(new Graph(G));
//            System.out.println("The value found in "+i+"th iteration is : "+val);
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
