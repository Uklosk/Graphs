package com.jmga.graphs.classes;

import android.graphics.Color;

public class Arrow {
	private int idi, idf;

	public float[] start = new float[2];
	public float[] stop = new float[2];
	private int weight = 1;
	public int color = Color.BLACK;

	/*
	 * CONSTRUCTORES
	 * 
	 * CONSTRUCTOR NORMAL
	 */
	public Arrow(int idi, int idf, int weight) {
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

	public int getIdi() {
		return idi;
	}

	public void setIdi(int idi) {
		this.idi = idi;
	}

	public int getIdf() {
		return idf;
	}

	public void setIdf(int idf) {
		this.idf = idf;
	}
	
}
