package com.jmga.graphs.tools;

import java.util.ArrayList;

import com.jmga.graphs.classes.Arrow;
import com.jmga.graphs.classes.Graph;
import com.jmga.graphs.classes.Link;
import com.jmga.graphs.classes.Node;

public class Kruskal {

	@SuppressWarnings("unchecked")
	public static Graph aplicarKruskal(Graph grafo) {
		Graph arbol = new Graph();
		ArrayList<String> Nodes = grafo.getNombres();

		for (int j = 0; j < Nodes.size(); j++) {

			arbol.addNode(Nodes.get(j));

		}

		ArrayList<Arrow> L = (ArrayList<Arrow>) grafo.getArrows().clone();

		for (int i = 0; i < L.size(); i++) {
			System.out.println(L.get(i).getWeightS());
		}
		Arrow pro = L.get(0);
		arbol.addLink(pro.getIdi(), pro.getIdf(), pro.getWeight());
		L.remove(pro);

		while (L.size()>0) {
			pro = L.get(0);
			
			if (HayCiclo(arbol, pro, arbol.getNode(pro.getIdf()), pro.getIdf()) == false) {
				arbol.addLink(pro.getIdi(), pro.getIdf(), pro.getWeight());
			}

			L.remove(pro);
		}
		System.out.println(arbol.getArrows().size());
		return arbol;
	}

	public static boolean HayCiclo(Graph g, Arrow aVerificar, Node Idf, String N) {
		ArrayList<Link> aux = Idf.getEnlaces();

		if (aux.size() == 0)
			return false;

		if (Idf.existeEnlace(aVerificar.getIdi()) != -1)
			return true;

		for (int i = 0; i < aux.size(); i++) {
			Link Node = aux.get(i);

			if (Node.getIdf().equals(N) == false)
				if (HayCiclo(g, aVerificar, g.getNode(Node.getIdf()),
						Idf.getId()))
					return true;
		}

		return false;
	}
}
