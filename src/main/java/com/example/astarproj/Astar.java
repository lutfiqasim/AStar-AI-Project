package com.example.astarproj;
import javafx.util.Pair;
import java.util.Map;
import java.util.PriorityQueue;

public class Astar {
    private PriorityQueue<Adjacent> openList;
    private City start;
    private City target;
    private Map<Pair<String, String>, Double> hueristicMap;
    private long startTime=0;
    private long endTime=0;
    public Astar(Map<Pair<String, String>, Double> hueristicMap) {
        this.hueristicMap = hueristicMap;
        openList = new PriorityQueue<Adjacent>();
    }

    public TableEntry[] findPath(City start, City target, TableEntry[] table) {
        startTime = System.nanoTime();
        this.target = target;
        this.start = start;
        table[start.cityEntry].distance = 0;
        openList.add(new Adjacent(start, 0));
        while (!openList.isEmpty()) {
            Adjacent current = openList.poll();
            int cityEntry = current.getCity().cityEntry;
            if (table[cityEntry].known)
                continue;
            table[cityEntry].known = true;

            if (isFinalDestination(current.getCity())) {
                endTime = System.nanoTime();
                return table;
            } else {
                addAdjacents(current.getCity(), table);
            }
        }
        endTime = System.nanoTime();
        return table;
    }

    private void addAdjacents(City current, TableEntry[] table){
        double edgeDis = -1;
        double newDis = -1;
        for (Adjacent adj : current.adjacent) {
            //current edge hasn't been processed
            if (!table[adj.getCity().cityEntry].known) {
                double h=  hurrestic(adj.getCity(), target);
                edgeDis = adj.getDistance() +h;
                newDis = table[current.cityEntry].distance + edgeDis;
                if (newDis < table[adj.getCity().cityEntry].distance) {
                    table[adj.getCity().cityEntry].distance = newDis -h;
                    table[adj.getCity().cityEntry].path = current;
                }
            openList.add(new Adjacent(adj.getCity(), (float) (table[adj.getCity().cityEntry].distance)));
            }
        }
    }

    private double hurrestic(City cur, City target) {
        Pair<String, String> p = new Pair<>(cur.getName(), target.getName());
        return hueristicMap.getOrDefault(p, calculateDistance(cur.lattitude,cur.longtidue,target.lattitude,target.longtidue));//if not found calculate air distance
    }

    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double radius = 6371.0; // Radius of the Earth in kilometers

        double lat1Rad = Math.toRadians(lat1);
        double lon1Rad = Math.toRadians(lon1);
        double lat2Rad = Math.toRadians(lat2);
        double lon2Rad = Math.toRadians(lon2);

        double dlon = lon2Rad - lon1Rad;
        double dlat = lat2Rad - lat1Rad;

        double a = Math.sin(dlat / 2) * Math.sin(dlat / 2)
                + Math.cos(lat1Rad) * Math.cos(lat2Rad)
                * Math.sin(dlon / 2) * Math.sin(dlon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = radius * c;
//        System.out.println("Distance calculating"+distance);
        return distance;
    }

    private boolean isFinalDestination(City currentNode) {
        return currentNode.equals(target);
    }
    public long timeTaken(){
        if(startTime==0 || endTime ==0){
            return -1;
        }
        return endTime - startTime ;
    }

}
