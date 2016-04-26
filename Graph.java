import com.sun.tools.javac.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Stack;

/**
 * Created by deep on 4/19/16.
 */
public class Graph implements Cloneable{
    private int[][] matrix;
    private int size;
    private int edges;
    private boolean[] valid_vertex;
    private int valid_vertices;

    //for karger-stein
    private int[] degree;

    //for stoer-wagner
    private Stack<Integer> orderOfVertices;
    private ArrayList<Integer> remainingVertices;


    public Graph(Graph g) {
        size = g.size();
        matrix = new int[size][size];
        for(int i = 0; i < size; i++) {
            if(g.valid_vertex[i]) {
                for (int j = 0; j < size; j++) {
                    matrix[i][j] = g.matrix[i][j];
                }
            }
        }

        valid_vertex = new boolean[size];
        for(int i = 0; i < size; i++)
            valid_vertex[i] = g.valid_vertex[i];
        valid_vertices = g.validVertices();
        edges = g.edges;
        degree = new int[size];
        for(int i = 0; i < size; i++) {
            degree[i] = g.degree[i];
        }

        orderOfVertices = new Stack<Integer>();
        remainingVertices = new ArrayList<Integer>();
        for(int i = 0; i < size; i++)
            remainingVertices.add(i);
    }

    public Graph(int m, int[][] mat) {
        matrix = mat;
        size = m;

        valid_vertex = new boolean[size];
        for(int i = 0; i < size; i++)
            valid_vertex[i] = true;
        valid_vertices = size;
        degree = new int[size];
        for(int i = 0; i < size; i++) {
            degree[i] = degreeOfVertex(i);
        }

        orderOfVertices = new Stack<Integer>();
        remainingVertices = new ArrayList<Integer>();
        for(int i = 0; i < size; i++)
            remainingVertices.add(i);
    }

    public Graph(int m, boolean isSparse) {
        matrix = new int[m][m];
        size = m;

        makeGraph(isSparse);

        valid_vertex = new boolean[size];
        for(int i = 0; i < size; i++)
            valid_vertex[i] = true;
        valid_vertices = size;
        degree = new int[size];
        for(int i = 0; i < size; i++) {
            degree[i] = degreeOfVertex(i);
        }

        orderOfVertices = new Stack<Integer>();
        remainingVertices = new ArrayList<Integer>();
        for(int i = 0; i < size; i++)
            remainingVertices.add(i);
    }

    //Build random graph
    public void makeGraph(boolean isSparse) {
        double ratio;
        if(isSparse) {
            ratio = 0.1;
        } else {
            ratio = 0.6;
        }
        for(int i = 0; i < size; i++) {
            int connections = connectivity(i);
            while(connections <= (int)(ratio * size)) {
                int j = (int) (Math.random() * size());
                if(j == i)
                    continue;
                if(matrix[i][j] == 0) {
                    connections++;
                    int ed = (int) (Math.random() * 10);
                    matrix[i][j] = ed;
                    matrix[j][i] = ed;
                    edges += ed;
                }
            }
        }
    }

    private int connectivity(int i) {
        int sum = 0;
        for(int j = 0; j < size; j++) {
            if(matrix[i][j] != 0)
                sum++;
        }
        return sum;
    }

    //Random edge selection
    public int[] selectRandomEdge() {
        int[] pair = new int[2];
        int ed_no = (int) (Math.random() * (2*edges));
        int i = 0;

        while(i < size-1 && ed_no - degree[i] > 0) {
            if(!valid_vertex[i]) {
                i++;
                continue;
            }
            ed_no = ed_no - degree[i];
            i++;
        }

        int v1 = i;
        int j = 0;

        do {
            if(j < size - 1 && (!valid_vertex[j] || j == v1)) {
                j++;
                continue;
            }
            else if(j < size - 1 && valid_vertex[j]) {
                ed_no = ed_no - matrix[v1][j];
                j++;
            }
        }while(j != size - 1 && ed_no > 0);

        int v2 = j - 1;

        pair[0] = v1;
        pair[1] = v2;
//        System.out.println("adding "+v2+" to "+v1);
        return pair;
    }


    //Edge Contraction
    public void contractEdge(int v1, int v2) {
        if(!valid_vertex[v2] || v1 == v2)
            return;
        edges = edges - matrix[v1][v2];
//        System.out.println("edges: "+edges);
        degree[v1] = degree[v1] + degree[v2] - 2 * matrix[v1][v2];
        degree[v2] = 0;
        for(int j = 0; j < matrix[v2].length; j++) {
            matrix[v1][j] = matrix[v1][j] + matrix[v2][j];
        }
        for(int i = 0; i < matrix.length; i++) {
            matrix[i][v1] = matrix[i][v1] + matrix[i][v2];
        }
        matrix[v1][v2] = 0;
        matrix[v2][v1] = 0;
        matrix[v1][v1] = 0;
//        System.out.println("making "+v2+" invalid");
        valid_vertex[v2] = false;
        for(int i = 0; i < size; i++)
            matrix[v2][i] = 0;
        for(int i = 0; i < size; i++)
            matrix[i][v2] = 0;
        valid_vertices--;
//        System.out.println("Now the valid_vertices = "+valid_vertices);
//        printGraph();
    }

    public void resize() {
        //handle size
        size = valid_vertices;

        //handle matrix
        int[][] temp_matrix = new int[matrix.length][matrix[0].length];
        for(int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++)
                temp_matrix[i][j] = matrix[i][j];
        }
        matrix = new int[valid_vertices][valid_vertices];
        int indexi = 0;
        for(int i = 0; i < temp_matrix.length; i++) {
            if(!valid_vertex[i])
                continue;
            int indexj = 0;
            for(int j = 0; j < temp_matrix[i].length; j++) {
                if(!valid_vertex[j])
                    continue;
                matrix[indexi][indexj] = temp_matrix[i][j];
                indexj++;
            }
            indexi++;
        }

        //handle degree
        indexi = 0;
        int[] temp_degree = Arrays.copyOf(degree, degree.length);
        degree = new int[valid_vertices];
        for(int i = 0; i < temp_degree.length; i++) {
            if(!valid_vertex[i])
                continue;
            degree[indexi++] = temp_degree[i];
        }

        //handle valid_vertex
        valid_vertex = new boolean[valid_vertices];
        for(int i = 0; i < valid_vertex.length; i++) {
            valid_vertex[i] = true;
        }
    }

    //size
    public int size() {
        return size;
    }

    //edges
    public int numberOfEdges() {
        int sum = 0;
        for(int i = 0; i < size; i++) {
            for(int j = i; j < size; j++) {
                sum += matrix[i][j];
            }
        }
        return sum;
    }

    //degree of vertex
    public int degreeOfVertex(int i) {
        int sum = 0;
        for(int j = 0; j < size(); j++) {
            sum += matrix[i][j];
        }
        return sum;
    }

    //number of valid vertices
    public int validVertices() {
        return valid_vertices;
    }

    //the cut for when valid_vertices == 2
    public int theKargerSteinCut() {
//        System.out.println("in the cut");
        int sum = Integer.MAX_VALUE;
        if(valid_vertices == 2) {
            for(int i = 0; i < size; i++) {
                if(valid_vertex[i]) {
                    int temp_sum = 0;
//                    System.out.println("valid vertex: "+i);
                    for(int j = 0; j < size; j++) {
                        temp_sum += matrix[i][j];
                    }
                    if(sum > temp_sum) {
                        sum = temp_sum;
                        return sum;
                    }
                }
            }
        }
        return -1;
    }

    //stoer-wagner methods

    public void addVertexToOrder(int v) {
        orderOfVertices.add(v);
        boolean removed;
        removed = remainingVertices.remove(new Integer(v));
    }

    public int findNextIntensiveVertex() {
        int ans = 0;
        int sum = 0;
        for(int i : remainingVertices) {
            int temp_sum = 0;
            for(int v: orderOfVertices) {
                temp_sum += matrix[v][i];
            }
            if(temp_sum >= sum) {
                sum = temp_sum;
                ans = i;
            }
        }
        return ans;
    }

    public int sizeOfOrder() {
        return orderOfVertices.size();
    }

    public int[] returnVerticesSAndT() {
        int[] ans = new int[2];
        ans[1] = orderOfVertices.pop();
        ans[0] = orderOfVertices.peek();
        return ans;
    }

    //contract v1 and v2 and return phase-cut
    public int contract(int v1, int v2) {
        int phase_cut = 0;
        edges = edges - matrix[v1][v2];
        degree[v1] = degree[v1] + degree[v2] - 2 * matrix[v1][v2];
        degree[v2] = 0;
        for(int j = 0; j < matrix[v2].length; j++) {
            matrix[v1][j] = matrix[v1][j] + matrix[v2][j];
        }
        for(int i = 0; i < matrix.length; i++) {
            matrix[i][v1] = matrix[i][v1] + matrix[i][v2];
        }
        matrix[v1][v2] = 0;
        matrix[v1][v1] = 0;
        for(int i = 0; i < size; i++)
            matrix[v2][i] = 0;
        for(int i = 0; i < size; i++)
            matrix[i][v2] = 0;
        valid_vertex[v2] = false;
        valid_vertices--;
        for(int i = 0; i < size; i++) {
            phase_cut += matrix[i][v1];
        }
        return phase_cut;
    }

    public void printGraph() {
        for(int i = 0; i < matrix.length; i++) {
            for(int j = 0; j < matrix[i].length; j++)
                System.out.print(matrix[i][j] + "\t");
            System.out.print("\n");
        }
    }

    public int phaseCutAtStart() {
        int sum = 0;
        int v = orderOfVertices.peek();
        for(int i = 0; i < size; i++)
            sum += matrix[i][v];
        return sum;
    }

    public boolean disjoint() {
        boolean[] visited = new boolean[size];
        dfs(0, visited);
        for(int i = 0; i < size; i++)
            if(!visited[i]) return true;
        return false;
    }

    private void dfs(int v, boolean[] visited) {
        visited[v] = true;
        for(int i = 0; i < size; i++) {
            if(!visited[i] && matrix[v][i] != 0) {
                dfs(i, visited);
            }
        }
    }

    public void makeNewOrder() {
        orderOfVertices = new Stack<Integer>();
        remainingVertices = new ArrayList<Integer>();
        for(int i = 0; i < size; i++)
            if(valid_vertex[i]) remainingVertices.add(i);
    }

    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
