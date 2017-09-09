/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app;

import java.awt.Point;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class ShortestPathCalculator1 {
    //Test Djikstra's

    private static List<Vertex> nodes = null;
    private static List<Edge> edges = null;

    
    public List<Edge> getEdgesList(){
        return edges;
    }
    
    public LinkedList<Vertex> calculateShortestRoute() {
        nodes = new ArrayList<Vertex>();
        edges = new ArrayList<Edge>();
        Random rand = new Random();
        int e01 = rand.nextInt(7) + 1;
        int e02 = rand.nextInt(7) + 1;
        int e13 = rand.nextInt(7) + 1;
        int e23 = rand.nextInt(7) + 1;
        int e15 = rand.nextInt(7) + 1;
        int e34 = rand.nextInt(7) + 1;
        int e26 = rand.nextInt(7) + 1;
        int e45 = rand.nextInt(7) + 1;
        int e46 = rand.nextInt(7) + 1;
        int e57 = rand.nextInt(7) + 1;
        int e67 = rand.nextInt(7) + 1;

        System.out.println(e01 + "," + e02 + "," + e13 + "," + e23 + "," + e15 + "," + e34 + "," + e26 + "," + e45 + "," + e46 + "," + e57 + "," + e67);

        
        
        for (int i = 0; i <= 7; i++) {
            Vertex location = new Vertex("V" + i, "" + i, null);
            nodes.add(location);
        }

        addEdge("Edge_1", 0, 1, e01);
        addEdge("Edge_2", 0, 2, e02);
        addEdge("Edge_3", 1, 3, e13);
        addEdge("Edge_4", 2, 3, e23);
        addEdge("Edge_5", 1, 5, e15);
        addEdge("Edge_6", 3, 4, e34);
        addEdge("Edge_7", 2, 6, e26);
        addEdge("Edge_8", 4, 5, e45);
        addEdge("Edge_9", 4, 6, e46);
        addEdge("Edge_10", 5, 7, e57);
        addEdge("Edge_11", 6, 7, e67);

        Graph graph = new Graph(nodes, edges);

        DjikstraAlgorithm dijkstra = new DjikstraAlgorithm(graph);
        dijkstra.execute(nodes.get(0));
        LinkedList<Vertex> path = dijkstra.getOptimalPath(nodes.get(7));

        for (Vertex vertex : path) {
            System.out.println(vertex);
        }
        return path;
    }

    private static void addEdge(String edgeId, int sourceVertex, int destinationVertex,
            int edgeWeight) {
        Edge edge = new Edge(edgeId, nodes.get(sourceVertex), nodes.get(destinationVertex), edgeWeight);
        edges.add(edge);
    }

}
