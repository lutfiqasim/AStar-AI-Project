package com.example.astarproj;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class BFS {
    private Queue<Adjacent> queue;
    private HashSet<City> visited;

    public BFS() {
        queue = new LinkedList<>();
        visited = new HashSet<>();
    }

    public void findPath(City start, City target) {
        queue.add(new Adjacent(start, 0));
        visited.add(start);
        while (!queue.isEmpty()){
            Adjacent adj = queue.poll();
            if(adj.getCity().equals(target))
                break;
            for (Adjacent a: adj.getCity().adjacent){
                if( visited.contains(a.getCity())){
                    visited.add(a.getCity());
                    queue.add(a);
                }
            }
        }
    }
}
