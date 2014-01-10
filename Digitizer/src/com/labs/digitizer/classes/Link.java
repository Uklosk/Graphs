package com.labs.digitizer.classes;

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
	
}
