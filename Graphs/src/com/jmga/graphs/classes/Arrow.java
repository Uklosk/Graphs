package com.jmga.graphs.classes;

import android.graphics.Color;

public class Arrow {
	private String idi, idf;

	public float[] start = new float[2];
	public float[] stop = new float[2];
	private int weight = 0;
	public int color = Color.BLACK;

	/*
	 * CONSTRUCTORES
	 * 
	 * CONSTRUCTOR NORMAL
	 */
	public Arrow(String idi, String idf, int weight) {
		this.idi = idi;
		this.idf = idf;
		this.weight = weight;
		start = null;
		stop = null;
	}

	/*
	 * CONSTRUCTOR AUXILIAR
	 */
	public Arrow(int x1, int y1, int x, int y) {
		start[0] = x1;
		start[1] = y1;
		stop[0] = x;
		stop[1] = y;
	}

	/*
	 * GETTERS AND SETTERS
	 */
	public void setAristaClave(){
		color = Color.BLUE;
	}
	
	public int getWeight() {
		return weight;
	}
	public String getWeightS(){
		return ""+weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public String getIdi() {
		return idi;
	}

	public void setIdi(String idi) {
		this.idi = idi;
	}

	public String getIdf() {
		return idf;
	}

	public void setIdf(String idf) {
		this.idf = idf;
	}
	
}
