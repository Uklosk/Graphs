package com.jmga.graphs.classes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

import android.graphics.Color;
import android.util.Log;

public class Graph implements Cloneable {
	private final String[] ids = { "A", "B", "C", "D", "E", "F", "G", "H", "I",
			"J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
			"W", "X", "Y", "Z", "a", "b", "c", "d", "e", "f", "g", "h", "i",
			"j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v",
			"w", "x", "y", "z" };

	public String getId(int i) {
		return ids[i];
	}

	private Hashtable<String, Integer[]> distancesT;
	private ArrayList<String> nombres;
	private Hashtable<String, Node> vertex;
	private ArrayList<Arrow> arrows;
	private Arrow aux;
	private int nodes;

	/*
	 * CONSTRUCTORES
	 * 
	 * CONSTRUCTOR NORMAL
	 */

	public Graph() {
		vertex = new Hashtable<String, Node>();
		arrows = new ArrayList<Arrow>();
		nombres = new ArrayList<String>();
		distancesT = new Hashtable<String, Integer[]>();
		nodes = 0;
	}

	/*
	 * CONSTRUCTOR ADICIONAL
	 */
	public Graph(Hashtable<String, Node> vertex, ArrayList<Arrow> arrows) {
		this.vertex = vertex;
		this.arrows = arrows;
		distancesT = new Hashtable<String, Integer[]>();

		nodes = vertex.size();
	}

	/*
	 * GETTERS AND SETTERS
	 * 
	 * NODES
	 */
	public Hashtable<String, Node> getVertex() {
		return vertex;
	}

	public Node getNode(String nombre) {
		return (Node) vertex.get(nombre);
	}

	public void setVertex(Hashtable<String, Node> vertex) {
		this.vertex = vertex;
	}

	public ArrayList<String> getNombres() {
		return nombres;
	}

	public void setNombres(ArrayList<String> nombres) {
		this.nombres = nombres;
	}

	public void addNode(Node node) {
		String nombre = ids[nodes];
		nombres.add(nombre);
		vertex.put(nombre, node);
		nodes++;
	}

	public void addNode(int x, int y,int viewportWidth, int viewportHeight, float density) {
		String nombre = ids[nodes];
		nombres.add(nombre);
		Node node = new Node(x, y, nombre,viewportWidth, viewportHeight, density);
		vertex.put(nombre, node);
		nodes++;
	}
	
	public void addNodeF(String id, float posX, float posY, int viewportWidth, int viewportHeight, float density){
		String nombre = id;
		nombres.add(nombre);
		Node node = new Node(posX, posY, nombre,viewportWidth, viewportHeight, density);
		vertex.put(nombre, node);
		nodes++;
	}

	public Node copiarNode(String name) {
		Node n = vertex.get(name);
		return n;

	}

	public void colorRestorationNodes() {
		Enumeration<Node> nodes = vertex.elements();
		while (nodes.hasMoreElements())
			((Node) (nodes.nextElement())).setColor(Color.BLACK);
	}

	public void setColorOfNode(String id, int color) {
		((Node) vertex.get(id)).setColor(color);
	}

	public void addNode(String string) {
		// TODO Auto-generated method stub
		Node node = new Node(0, 0, string,1,1,1);
		vertex.put(string, node);
		nodes++;
	}

	public void deleteNode(String name) {
		ArrayList<String[]> to_delete = new ArrayList<String[]>();
		Node node = new Node();
		node = vertex.get(name);
		Iterator<Link> links = node.getEnlaces().iterator();
		while(links.hasNext()){
			Link link = links.next();
			String idf = link.getIdf();
			Node nodef = vertex.get(idf);
			String[] link_to_delete = new String[2];
			link_to_delete[0] = name;
			link_to_delete[1] = idf;
			to_delete.add(link_to_delete);
			Iterator<Link> links_ = nodef.getEnlaces().iterator();
			nodef.initNode(idf);
			while(links_.hasNext()){
				Link link_ = links_.next();
				if(!link_.getIdf().equals(name))
					nodef.agregarEnlace(link_.getIdf(), (int)Math.floor(link_.getweight()));
			}
		}
		Iterator<String[]> remove = to_delete.iterator();
		while(remove.hasNext()){
			String[] r = new String[2];
			r = remove.next();
			deleteLink(r[0], r[1]);
			deleteLink(r[1], r[0]);
		}
		nombres.remove(name);
		vertex.get(name).initNode(name);
		Node n = new Node(0, 0, "nulo",1,1,1);
		vertex.put(name, n);
	}

	/*
	 * ARROWS
	 */
	public void setArrows(ArrayList<Arrow> arrows) {
		this.arrows = arrows;
	}

	public ArrayList<Arrow> getArrows() {
		return arrows;
	}

	public void addLink(String idi, String idf, int weight) {
		Arrow a = new Arrow(idi, idf, weight);
		for (int i = 0; i < arrows.size(); i++) {
			Arrow aa = arrows.get(i);
			if (aa.getIdi().equals(idi)) {
				if (aa.getIdf().equals(idf)) {
					a = null;
				}
			}
			if (aa.getIdf().equals(idi)) {
				if (aa.getIdi().equals(idf)) {
					a = null;
				}
			}
		}

		if (a != null) {
			int i = buscarIndice(a.getWeight());
			if (i == -1)
				arrows.add(a);
			else
				arrows.add(i, a);

			vertex.get(idi).agregarEnlace(idf, weight);
			vertex.get(idf).agregarEnlace(idi, weight);
		}
	}

	public void deleteLink(String idi, String idf) {
		Node ni = vertex.get(idi);
		Iterator<Link> lni = ni.getEnlaces().iterator();
		ni.initNode(idi);
		Node nf = vertex.get(idf);
		Iterator<Link> lnf = nf.getEnlaces().iterator();
		nf.initNode(idf);
		while(lni.hasNext()){
			Link li = lni.next();
			if(!li.getIdf().equals(idf))
				ni.agregarEnlace(li.getIdf(), (int)Math.floor(li.getweight()));
		}
		while(lnf.hasNext()){
			Link lf = lnf.next();
			if(!lf.getIdf().equals(idi))
				nf.agregarEnlace(lf.getIdf(), (int)Math.floor(lf.getweight()));
		}
		for (int i = 0; i < arrows.size(); i++) {
			Arrow a = arrows.get(i);
			if (a.getIdi().equals(idi) && (a.getIdf().equals(idf))){
					vertex.get(idf).deleteEnlace(idi);
					vertex.get(idi).deleteEnlace(idf);
					arrows.remove(i);
			}
			if (a.getIdf().equals(idi) && a.getIdi().equals(idf)) {
					vertex.get(idf).deleteEnlace(idi);
					vertex.get(idi).deleteEnlace(idf);
					arrows.remove(i);
			}
		}
	}

	public int compararPeso(Arrow a1, Arrow a2) {
		if (a1.getWeight() < a2.getWeight())
			return 1;
		if (a1.getWeight() > a2.getWeight())
			return 2;
		return 3;
	}

	public void changeWeight(String idi, String idf, int weight) {
		for (int i = 0; i < arrows.size(); i++) {
			Arrow a = arrows.get(i);
			if (mismaArista(a, buscarArista(idi, idf))) {
				a.setWeight(weight);
				ordenarArrows();
			}
		}
		Node n = new Node();
		n = vertex.get(idi);
		Iterator<Link> links = n.getEnlaces().iterator();
		while(links.hasNext()){
			Link link = links.next();
			if(link.getIdf().equals(idf))
				link.modificar(weight);
			Node n_ = new Node();
			n_ = vertex.get(link.getIdf());
			Iterator<Link> links_ = n_.getEnlaces().iterator();
			while(links_.hasNext()){
				Link link_ = links_.next();
				if(link_.getIdf().equals(idi))
					link_.modificar(weight);
			}
		}
	}

	public boolean buscarArista(Arrow a) {
		for (int i = 0; i < arrows.size(); i++) {
			Arrow otro = arrows.get(i);
			if (a.getIdi().equals(otro.getIdi())
					&& a.getIdf().equals(otro.getIdf())
					&& a.getWeight() == otro.getWeight()) {
				arrows.remove(otro);
				return true;
			}
		}
		return false;
	}

	public void ordenarArrows() {
		for (int i = 0; i < arrows.size(); i++) {
			for (int j = 0; j < arrows.size(); j++) {
				switch (compararPeso(arrows.get(i), arrows.get(j))) {
				case 1:
					if (i > j) {
						arrows.add(j, arrows.get(i));
						arrows.remove(i + 1);
					}
					break;
				case 2:

					break;
				case 3:

					break;
				default:
					break;

				}
			}
		}
	}

	public Arrow buscarArista(String idi, String idf) {
		Arrow a = new Arrow(idi, idf, 0);
		return a;
	}

	public int buscarIndice(float peso) {
		for (int i = 0; i < arrows.size(); i++) {
			if (peso < arrows.get(i).getWeight())
				return i;
		}
		return -1;
	}

	public void update() {
		for (int i = 0; i < arrows.size(); i++) {
			String idi = arrows.get(i).getIdi();
			String idf = arrows.get(i).getIdf();
			Node iN = vertex.get(idi);
			Node iF = vertex.get(idf);
			arrows.get(i).start = new float[] { iN.getCenterX(),
					iN.getCenterY() };
			arrows.get(i).stop = new float[] { iF.getCenterX(), iF.getCenterY() };

		}
	}

	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	public boolean mismaArista(Arrow a1, Arrow a2) {
		if (a1.getIdi().equals(a2.getIdi()) && a1.getIdf().equals(a2.getIdf())) {
			return true;
		} else if (a1.getIdf().equals(a2.getIdi())
				&& a1.getIdi().equals(a2.getIdf())) {
			return true;
		}
		return false;
	}

	public void updateDistances() {

	}
	
	public int getTotalWeight(){
		int total = 0;
		
		Iterator<Arrow> arrows_ = arrows.iterator();
		while(arrows_.hasNext()){
			total += arrows_.next().getWeight();
		}
		
		return total;
	}
	
	// return (n = 0) -> No regular
	// return (n > 0) -> regular
	public int isRegular(){
		int degree = 0;
		
		String[] seq = new String[nombres.size()];
		seq = getSequenceDegrees();
		if(seq.length==0)
			return 0;
		else if(seq.length==1)
			return Integer.parseInt(seq[0]);
		else{
			boolean regular = true;
			for(int i = 1; i < seq.length; i++)
				if(!seq[0].equals(seq[i]))
					regular = false;
			if(regular)
				degree = Integer.parseInt(seq[0]);
		}
		
		return degree;
	}
	
	public String[] getSequenceDegrees(){
		String[] seq = new String[nombres.size()];
		int c = 0;
		
		Iterator<String> nodes = nombres.iterator();
		while(nodes.hasNext())
			seq[c++] = Integer.toString(getNode(nodes.next()).getEnlaces().size());
		
		Arrays.sort(seq,Collections.reverseOrder());
		
		return seq;
	}

	public Arrow getAux() {
		return aux;
	}

	public void setAux(Arrow aux) {
		this.aux = aux;
	}
}
