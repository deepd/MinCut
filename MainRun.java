import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Created by deep on 4/23/16.
 */
public class MainRun {
    public static void main(String[] args) throws CloneNotSupportedException, FileNotFoundException {
        for(int i = 0; i < 5; i++) {
            System.out.println("*********** "+i+"th iteration ***********");
            Graph graph = new Graph(50, true);
            //        graph.printGraph();

            /*int[][] mat = new int[5][5];
            Scanner input = new Scanner (new File("src/array.txt"));

    // read in the data
            input = new Scanner(new File("src/array.txt"));
            for(int i = 0; i < 5; ++i)
            {
                for(int j = 0; j < 5; ++j)
                {
                    if(input.hasNextInt())
                    {
                        mat[i][j] = input.nextInt();
                    }
                }
            }

            Graph graph = new Graph(5, mat);*/


            System.out.println("edges: "+graph.numberOfEdges());
            KargerStein ks = new KargerStein();
            Graph ks_graph = new Graph(graph);
            long startTime = System.nanoTime();
            System.out.println("KargerStein output: "+ks.kargerSteinSolver(ks_graph));
            long endTime = System.nanoTime();
            long duration = (endTime - startTime);
            System.out.println("\t\tKargerStein :- Time: " + duration + "ns");

            StoerWagner sw = new StoerWagner();
            Graph sw_graph = new Graph(graph);
            startTime = System.nanoTime();
            System.out.println("StoerWagner output: "+sw.minCut(sw_graph));
            endTime = System.nanoTime();
            duration = (endTime - startTime);
            System.out.println("\t\tStoerWagner :- Time: " + duration + "ns");
        }
    }
}
