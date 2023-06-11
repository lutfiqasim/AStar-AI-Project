package com.example.astarproj;

import javafx.scene.paint.Color;

import java.util.*;

public class BFS {
    private Queue<Adjacent> queue;
    private HashSet<City> visited;
    private TableEntry[] table;
    private long startTime = 0;
    private long endTime = 0;
    public BFS() {
        queue = new LinkedList<>();
        visited = new HashSet<>();
    }

    public ArrayList<City> findPath(City start, City target, int numberOfVertices) {
        queue.clear();
        visited.clear();
        startTime = System.currentTimeMillis();
        ArrayList<City> c=startSearch(start, target, numberOfVertices);
        endTime = System.currentTimeMillis();
        return c;

    }

    private ArrayList<City> startSearch(City start, City target, int v) {
        City [] prev= new City[v];
        queue.add(new Adjacent(start,0));
        visited.add(start);
        while (!queue.isEmpty()){
            Adjacent adj = queue.poll();
            for(Adjacent neigbour:adj.getCity().adjacent){
                if(!visited.contains(neigbour.getCity())){
                    queue.add(neigbour);
                    visited.add(neigbour.getCity());
                    prev[neigbour.getCity().cityEntry] = adj.getCity();
                }
            }
        }
        return showPath(start,target,prev);
    }

    public ArrayList<City> showPath(City start, City end, City[]prev) {//Note to bebo start her is end
        ArrayList<City> arr = new ArrayList<>();
        for(City c = end;c!=null;c=prev[c.cityEntry]){
            arr.add(c);
        }
        Collections.reverse(arr);
        return arr;
    }

    public long getTime() {
        if (startTime == 0 || endTime == 0)
            return -1;
        return endTime - startTime;
    }

}
