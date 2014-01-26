package com.jmga.graphs.classes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class Dijkstra {

  private ArrayList<Arrow> arrows;
  private ArrayList<String> vertex;
  private Set<String> settledNodes;
  private Set<String> unSettledNodes;
  private Map<String, String> predecessors;
  private Map<String, Integer> distance;

  public Dijkstra(Graph graph) {
    // create a copy of the array so that we can operate on this array
    this.arrows = graph.getArrows();
    this.vertex = graph.getNombres();
  }

  public void execute(String source) {
    settledNodes = new HashSet<String>();
    unSettledNodes = new HashSet<String>();
    distance = new HashMap<String, Integer>();
    predecessors = new HashMap<String, String>();
    distance.put(source, 0);
    unSettledNodes.add(source);
    while (unSettledNodes.size() > 0) {
      String node = getMinimum(unSettledNodes);
      settledNodes.add(node);
      unSettledNodes.remove(node);
      findMinimalDistances(node);
    }
    System.out.println("Distancia de B al resto  " + distance.values());
  }

  private void findMinimalDistances(String node) {
    List<String> adjacentNodes = getNeighbors(node);
    for (String target : adjacentNodes) {
      if (getShortestDistance(target) > getShortestDistance(node)
          + getDistance(node, target)) {
        distance.put(target, getShortestDistance(node)
            + getDistance(node, target));
        predecessors.put(target, node);
        unSettledNodes.add(target);
      }
    }

  }

  private int getDistance(String node, String target) {
    for (Arrow arrow : arrows) {
      if (arrow.getIdi().equals(node)
          && arrow.getIdf().equals(target)) {
        return arrow.getWeight();
      } else if (arrow.getIdf().equals(node) && arrow.getIdi().equals(target)){
    	  return arrow.getWeight();
      }
    }
    throw new RuntimeException("Should not happen");
  }

  private List<String> getNeighbors(String node) {
    List<String> neighbors = new ArrayList<String>();
    for (Arrow arrow : arrows) {
      if (arrow.getIdi().equals(node)
          && !isSettled(arrow.getIdf())) {
        neighbors.add(arrow.getIdf());
      } else if(arrow.getIdf().equals(node) && !isSettled(arrow.getIdi())){
    	  neighbors.add(arrow.getIdi());
      }
    }
    return neighbors;
  }

  private String getMinimum(Set<String> nodes) {
    String minimum = null;
    for (String node : nodes) {
      if (minimum == null) {
        minimum = node;
      } else {
        if (getShortestDistance(node) < getShortestDistance(minimum)) {
          minimum = node;
        }
      }
    }
    return minimum;
  }

  private boolean isSettled(String node) {
    return settledNodes.contains(node);
  }

  private int getShortestDistance(String destination) {
    Integer d = distance.get(destination);
    if (d == null) {
      return Integer.MAX_VALUE;
    } else {
      return d;
    }
  }

  /*
   * This method returns the path from the source to the selected target and
   * NULL if no path exists
   */
  public LinkedList<String> getPath(String target) {
    LinkedList<String> path = new LinkedList<String>();
    String step = target;
    // check if a path exists
    if (predecessors.get(step) == null) {
      return null;
    }
    path.add(step);
    while (predecessors.get(step) != null) {
      step = predecessors.get(step);
      path.add(step);
    }
    // Put it into the correct order
    Collections.reverse(path);
    return path;
  }

} 