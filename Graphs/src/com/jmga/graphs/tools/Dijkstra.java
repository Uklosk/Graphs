package com.jmga.graphs.tools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.jmga.graphs.classes.Arrow;
import com.jmga.graphs.classes.GView;
import com.jmga.graphs.classes.Graph;

public class Dijkstra {

	private ArrayList<Arrow> arrows;
	private ArrayList<Integer> nodes;
	private Set<Integer> settledNodes;
	private Set<Integer> unSettledNodes;
	private Map<Integer, Integer> predecessors;
	private Map<Integer, Integer> distance;
	private Hashtable<Integer, Hashtable<Integer, Integer>> disT;

	public Dijkstra(Graph graph) {
		// create a copy of the array so that we can operate on this array
		this.arrows = graph.getArrows();
		this.nodes = graph.getNombres();
	}

	public void execute(int source) {
		settledNodes = new HashSet<Integer>();
		unSettledNodes = new HashSet<Integer>();
		distance = new HashMap<Integer, Integer>();
		predecessors = new HashMap<Integer, Integer>();
		distance.put(source, 0);
		unSettledNodes.add(source);
		while (unSettledNodes.size() > 0) {
			int node = getMinimum(unSettledNodes);
			settledNodes.add(node);
			unSettledNodes.remove(node);
			findMinimalDistances(node);
		}
	}

	private void findMinimalDistances(int node) {
		List<Integer> adjacentNodes = getNeighbors(node);
		for (Integer target : adjacentNodes) {
			if (getShortestDistance(target) > getShortestDistance(node)
					+ getDistance(node, target)) {
				distance.put(target,
						getShortestDistance(node) + getDistance(node, target));
				predecessors.put(target, node);
				unSettledNodes.add(target);
			}
		}

	}

	private int getDistance(int node, int target) {
		for (Arrow arrow : arrows) {
			if ((arrow.getIdi())==(node) && (arrow.getIdf())==(target)) {
				return arrow.getWeight();
			} else if ((arrow.getIdf())==(node)
					&& (arrow.getIdi())==(target)) {
				return arrow.getWeight();
			}
		}
		throw new RuntimeException("Should not happen");
	}

	private List<Integer> getNeighbors(int node) {
		List<Integer> neighbors = new ArrayList<Integer>();
		for (Arrow arrow : arrows) {
			if (arrow.getIdi()==(node) && !isSettled((arrow.getIdf()))) {
				neighbors.add((arrow.getIdf()));
			} else if ((arrow.getIdf())==(node)
					&& !isSettled((arrow.getIdi()))) {
				neighbors.add((arrow.getIdi()));
			}
		}
		return neighbors;
	}

	private int getMinimum(Set<Integer> nodes) {
		Integer minimum = null;
		for (Integer node : nodes) {
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

	private boolean isSettled(int node) {
		return settledNodes.contains(node);
	}

	private int getShortestDistance(int destination) {
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
	public LinkedList<Integer> getPath(int target) {
		LinkedList<Integer> path = new LinkedList<Integer>();
		int step = target;
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

	public void dijkstra(Graph g) {
		disT = new Hashtable<Integer, Hashtable<Integer, Integer>>();
		for (int name : nodes) {
			Hashtable<Integer, Integer> table = new Hashtable<Integer, Integer>();
			execute((name));
			for (int name2 : nodes) {
				if(distance.get(name2)==null){
					table.put(name2, -1);

				}else{
					table.put(name2, distance.get(name2));

				}
			}
			disT.put(name, table);
		}

	}
	
	public FlowTable getTableDistance(Context context){
		FlowTable dTable = new FlowTable(context,nodes);
		for(int name : nodes){
			dTable.addContent(name, disT.get(name));
		}
		return dTable;
		
	}

}