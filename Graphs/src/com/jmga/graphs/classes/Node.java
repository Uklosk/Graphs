package com.jmga.graphs.classes;

import java.util.ArrayList;

import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;

public class Node extends ShapeDrawable {

	private ArrayList<Link> enlaces;


	private int enlacesExistentes;
	public int radius = 50;
	private String id;

	/*
	 * CONSTRUCTORES
	 * 
	 * CONSTRUCTOR NORMAL
	 */
	public Node(int x, int y, String id) {
		super(new OvalShape());
		getPaint().setColor(Color.BLACK);
		setPos(x, y);
		enlacesExistentes = -1;
		enlaces = new ArrayList<Link>();
		this.id = id;

	}

	/*
	 * CONSTRUCTOR AUXILIAR
	 */
	public Node(int x, int y) {
		super(new OvalShape());
		getPaint().setColor(Color.BLACK);
		setPos(x, y);
		enlacesExistentes = -1;
		enlaces = new ArrayList<Link>();
		id = "aux";

	}

	/*
	 * ESTABLECER POSICION
	 */
	public void setPos(int x, int y) {
		setBounds(x - radius, y - radius, x + radius, y + radius);
	}

	/*
	 * GETTERS AND SETTERS
	 */
	public int getCenterX() {
		return getBounds().centerX();
	}

	public int getCenterY() {
		return getBounds().centerY();
	}

	public String getId() {
		return id;
	}

	public void String(String id) {
		this.id = id;
	}

	public ArrayList<Link> getEnlaces() {
		return enlaces;
	}

	public void setEnlaces(ArrayList<Link> enlaces) {
		this.enlaces = enlaces;
	}
	
	
	public int getEnlacesExistentes() {
		return enlacesExistentes;
	}

	public void setEnlacesExistentes(int enlacesExistentes) {
		this.enlacesExistentes = enlacesExistentes;
	}

	/*
	 * ADICIONALES
	 */

	
	
	public void agregarEnlace(String idf, int weight) {
		enlaces.add(new Link(idf,weight));

	}

	public void deleteEnlace(String idf){
		enlaces.remove(idf);
		
	}


	public int existeEnlace(String idi)
 	{
 		for (int i = 0; i < enlaces.size(); i++)
 		{
 			Link miEnlace;
 			miEnlace = enlaces.get(i);
 			if (miEnlace.getIdf().equals(idi))
 				return i;
 		}
 		return -1;
 	}
	
	public String NodoPosicion(int posi) {
		Link miEnlace;
		miEnlace = enlaces.get(posi);
		return miEnlace.getIdf();
	}


}
