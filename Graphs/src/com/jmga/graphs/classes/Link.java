package com.jmga.graphs.classes;

import java.util.Iterator;

public class Link {
	private String idf;
	private int weight;

	public Link(String idf, int weight) {
		this.idf = idf;
		this.weight = weight;
	}

	public Link(String idf) {
		this.idf = idf;
		weight = -1;
	}

	public void modificar(int weight) {
		this.weight=weight;
	}

	public String getIdf() {
		return idf;
	}

	public double getweight() {
		return weight;
	}
	
	public void changeIds(Graph g, String id, String new_idf){
		Arrow a = g.buscarArista(id, idf);
		a.setIdi(id);
		a.setIdf(new_idf);
		idf = new_idf;
	}
	
}
