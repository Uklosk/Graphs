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
	

	/*
	 * private String moduleLabel(){
	 * 
	 * }
	 */

	private Hashtable<String, Integer[]> distancesT;
	private ArrayList<Integer> nombres;
	private Hashtable<Integer, Node> vertex;
	private ArrayList<Arrow> arrows;
	private Arrow aux;
	private int nodes;
    int color = Color.BLACK;
    public boolean visible = true;

	/*
	 * CONSTRUCTORES
	 * 
	 * CONSTRUCTOR NORMAL
	 */

	public Graph() {
		vertex = new Hashtable<Integer, Node>();
		arrows = new ArrayList<Arrow>();
		nombres = new ArrayList<Integer>();
		distancesT = new Hashtable<String, Integer[]>();
		nodes = 0;
	}

	/*
	 * CONSTRUCTOR ADICIONAL
	 */
	public Graph(Hashtable<Integer, Node> vertex, ArrayList<Arrow> arrows) {
		this.vertex = vertex;
		this.arrows = arrows;
		distancesT = new Hashtable<String, Integer[]>();
		nombres = Collections.list(vertex.keys());
		nodes = vertex.size();
	}

	/*
	 * GETTERS AND SETTERS
	 * 
	 * NODES
	 */
	public Hashtable<Integer, Node> getVertex() {
		return vertex;
	}

	public Node getNode(int nombre) {
		return (Node) vertex.get(nombre);
	}

	public void setVertex(Hashtable<Integer, Node> vertex) {
		this.vertex = vertex;
	}

	public ArrayList<Integer> getNombres() {
		return nombres;
	}
	
	public ArrayList<String> getNombresString(){
		ArrayList<String> nombres = new ArrayList<String>();
		for(int nombre : this.nombres){
			nombres.add(nombre+"");
		}
		return nombres;
		
		
	}

	public void setNombres(ArrayList<Integer> nombres) {
		this.nombres = nombres;
	}

	public void addNode(Node node) {
		nombres.add(nodes);
		vertex.put(nodes, node);
		nodes++;
	}

	public void addNode(int id) {
		Node node = new Node(0, 0, id, 1, 1, 1);
		vertex.put(id, node);
		nodes++;
	}

	public void addNode(int x, int y, int viewportWidth, int viewportHeight,
			float density) {
		nombres.add(nodes);
		Node node = new Node(x, y, nodes, viewportWidth, viewportHeight,
				density);
		vertex.put(nodes, node);
		nodes++;
	}

	public void addNodeF(int id, float posX, float posY, int viewportWidth,
			int viewportHeight, float density) {
		nombres.add(id);
		Node node = new Node(posX, posY, id, viewportWidth, viewportHeight,
				density);
		vertex.put(id, node);
		if(id>=nodes)nodes=id+1;
	}

	public Node copiarNode(int name) {
		Node n = vertex.get(name);
		return n;

	}

	public void colorRestorationNodes() {
		Enumeration<Node> nodes = vertex.elements();
		while (nodes.hasMoreElements())
			((Node) (nodes.nextElement())).setColor(Color.BLACK);
	}

	public void setColorOfNode(int id, int color) {
		((Node) vertex.get(id)).setColor(color);
	}

	public void deleteNode(int name) {
		ArrayList<int[]> to_delete = new ArrayList<int[]>();
		Node node = new Node();
		node = vertex.get(name);
		Iterator<Link> links = node.getEnlaces().iterator();
		while (links.hasNext()) {
			Link link = links.next();
			int idf = link.getIdf();
			Node nodef = vertex.get(idf);
			int[] link_to_delete = new int[2];
			link_to_delete[0] = name;
			link_to_delete[1] = idf;
			to_delete.add(link_to_delete);
			Iterator<Link> links_ = nodef.getEnlaces().iterator();
			nodef.initNode(idf);
			while (links_.hasNext()) {
				Link link_ = links_.next();
				if (!(link_.getIdf() == name))
					nodef.agregarEnlace(link_.getIdf(),
							(int) Math.floor(link_.getweight()));
			}
		}
		Iterator<int[]> remove = to_delete.iterator();
		while (remove.hasNext()) {
			int[] r = new int[2];
			r = remove.next();
			deleteLink(r[0], r[1]);
			deleteLink(r[1], r[0]);
		}
		nombres.remove((Object)name);
		vertex.get(name).initNode(name);
		Node n = new Node(0, 0, -1, 1, 1, 1);
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

	public void addLink(int idi, int idf, int weight) {
		Arrow a = new Arrow(idi, idf, weight);
		for (int i = 0; i < arrows.size(); i++) {
			Arrow aa = arrows.get(i);
			if (aa.getIdi()==idi) {
				if (aa.getIdf()==idf) {
					a = null;
				}
			}
			if (aa.getIdf()==idi) {
				if (aa.getIdi()==idf) {
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

	public void deleteLink(int idi, int idf) {
		Node ni = vertex.get(idi);
		Iterator<Link> lni = ni.getEnlaces().iterator();
		ni.initNode(idi);
		Node nf = vertex.get(idf);
		Iterator<Link> lnf = nf.getEnlaces().iterator();
		nf.initNode(idf);
		while (lni.hasNext()) {
			Link li = lni.next();
			if (!(li.getIdf() == idf))
				ni.agregarEnlace(li.getIdf(), (int) Math.floor(li.getweight()));
		}
		while (lnf.hasNext()) {
			Link lf = lnf.next();
			if (!(lf.getIdf() == idi))
				nf.agregarEnlace(lf.getIdf(), (int) Math.floor(lf.getweight()));
		}
		for (int i = 0; i < arrows.size(); i++) {
			Arrow a = arrows.get(i);
			if (a.getIdi() == idi && (a.getIdf() == idf)) {
				vertex.get(idf).deleteEnlace(idi);
				vertex.get(idi).deleteEnlace(idf);
				arrows.remove(i);
			}
			if (a.getIdf() == idi && a.getIdi() == idf) {
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

	public void changeWeight(int idi, int idf, int weight) {
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
		while (links.hasNext()) {
			Link link = links.next();
			if (link.getIdf() == idf)
				link.modificar(weight);
		}
		Node n_ = new Node();
		n_ = vertex.get(idf);
		Iterator<Link> links_ = n_.getEnlaces().iterator();
		while (links_.hasNext()) {
			Link link_ = links_.next();
			if (link_.getIdf() == idi)
				link_.modificar(weight);
		}
	}

	public boolean buscarArista(Arrow a) {
		for (int i = 0; i < arrows.size(); i++) {
			Arrow otro = arrows.get(i);
			if (a.getIdi() == otro.getIdi() && a.getIdf() == otro.getIdf()
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

	public Arrow buscarArista(int idi, int idf) {
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
			int idi = arrows.get(i).getIdi();
			int idf = arrows.get(i).getIdf();
			Node iN = vertex.get(idi);
			Node iF = vertex.get(idf);
			arrows.get(i).start = new float[] { iN.getCenterX(),
					iN.getCenterY() };
			arrows.get(i).stop = new float[] { iF.getCenterX(), iF.getCenterY() };

		}
        for (int ns : getNombres()) {
            Node n = getVertex().get(ns);
            n.getPaint().setColor(color);
        }
        for(int i = 0;i < arrows.size();i++){
            arrows.get(i).color=color;
        }
	}
    public void setColor(int color){
        this.color=color;

    }
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	public boolean mismaArista(Arrow a1, Arrow a2) {
		if (a1.getIdi()==(a2.getIdi()) && a1.getIdf()==(a2.getIdf())) {
			return true;
		} else if (a1.getIdf()==(a2.getIdi())
				&& a1.getIdi()==(a2.getIdf())) {
			return true;
		}
		return false;
	}

	public void updateDistances() {

	}

	public int getTotalWeight() {
		int total = 0;

		Iterator<Arrow> arrows_ = arrows.iterator();
		while (arrows_.hasNext()) {
			total += arrows_.next().getWeight();
		}

		return total;
	}

	// return (n = 0) -> No regular
	// return (n > 0) -> regular
	public int isRegular() {
		int degree = 0;

		String[] seq = new String[nombres.size()];
		seq = getSequenceDegrees();
		if (seq.length == 0)
			return 0;
		else if (seq.length == 1)
			return Integer.parseInt(seq[0]);
		else {
			boolean regular = true;
			for (int i = 1; i < seq.length; i++)
				if (!seq[0].equals(seq[i]))
					regular = false;
			if (regular)
				degree = Integer.parseInt(seq[0]);
		}

		return degree;
	}

	public String[] getSequenceDegrees() {
		String[] seq = new String[nombres.size()];
		int c = 0;

		Iterator<Integer> nodes = nombres.iterator();
		while (nodes.hasNext())
			seq[c++] = Integer.toString(getNode(nodes.next()).getEnlaces()
					.size());

		Arrays.sort(seq, Collections.reverseOrder());

		return seq;
	}

	public Arrow getAux() {
		return aux;
	}

	public void setAux(Arrow aux) {
		this.aux = aux;
	}

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
