/**
 * Created by deep on 4/19/16.
 */
public class StoerWagner {
    public StoerWagner() {
    }

    public void makeOrderOfVertex(Graph G) {
        G.makeNewOrder();
        G.addVertexToOrder(0);
        while(G.sizeOfOrder() != G.validVertices()) {
            int v = G.findNextIntensiveVertex();
            G.addVertexToOrder(v);
        }
    }

    public int minCut(Graph G) {
        if(G.disjoint()) {
            System.out.println("disjoint in stoerwagner");
            return 0;
        }
        makeOrderOfVertex(G);
        int min = G.phaseCutAtStart();
        int s,t;
        while(G.validVertices() != 1) {
            int[] vertices = G.returnVerticesSAndT();
            int phase_cut = 0;
            s = vertices[0];
            t = vertices[1];
            phase_cut = G.contract(s,t);
            if(phase_cut != 0 && min > phase_cut)
                min = phase_cut;
            makeOrderOfVertex(G);
        }
        return min;
    }
}
