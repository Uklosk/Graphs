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

	private int color;
	
	/*
	 * CONSTRUCTORES
	 * 
	 * CONSTRUCTOR NORMAL
	 */
	public Node(){
		id = "";
		enlacesExistentes = 0;
		enlaces = new ArrayList<Link>();
		color = Color.BLACK;
	}
	
	public Node(int x, int y, String id) {
		super(new OvalShape());
		color = Color.BLACK;
		getPaint().setColor(color);
		getPaint().setAntiAlias(true);
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
		color = Color.BLACK;
		getPaint().setColor(color);
		getPaint().setAntiAlias(true);
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
	
	public int getRadius() {
		return radius;
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
		getPaint().setColor(color);
	}	
	
	/*
	 * ADICIONALES
	 */

	public void agregarEnlace(String idf, int weight) {
		enlaces.add(new Link(idf,weight));

	}

	public void deleteEnlace(String idf){
		for(int i=0;i<enlaces.size();i++){
			if (enlaces.get(i).getIdf()==idf) {
				enlaces.remove(i);
				i=0;
			}
		}		
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
