package com.jmga.graphs.tools;

import java.util.ArrayList;

import com.jmga.graphs.classes.Arrow;
import com.jmga.graphs.classes.Graph;
import com.jmga.graphs.classes.Link;
import com.jmga.graphs.classes.Node;

// ALGORITMO DE KRUSKAL

public class Kruskal {

	@SuppressWarnings("unchecked")
	public static Graph aplicarKruskal(Graph grafo) {
		/*
		 * Creamos el grafo que va a ser nuestro árbol
		 * y le añadimos los vértices al grafo 
		 */
		Graph arbol = new Graph();
		ArrayList<Integer> Nodes = grafo.getNombres();

		for (int j = 0; j < Nodes.size(); j++) {

			arbol.addNode(Nodes.get(j));

		}

		/*
		 * Creamos un conjunto auxiliar con todas las aristas del grafo
		 * ordenadas de menor a mayor peso
		 * 
		 * Añadimos la primera al árbol y la borramos del conjunto auxiliar
		 */
		ArrayList<Arrow> L = (ArrayList<Arrow>) grafo.getArrows().clone();

		for (int i = 0; i < L.size(); i++) {
			System.out.println(L.get(i).getWeightS());
		}
		Arrow pro = L.get(0);
		arbol.addLink(pro.getIdi(), pro.getIdf(), pro.getWeight());
		L.remove(pro);

		/*
		 * Mientras queden aristas en este conjunto seleccionamos la de
		 * menor peso e iteramos comprobando si forma ciclo
		 * 
		 * Si no forma ciclo la añadimos y luego la borramos
		 * 
		 * Si forma ciclo la borramos directamente
		 */
		while (L.size()>0) {
			pro = L.get(0);
			
			if (HayCiclo(arbol, pro, arbol.getNode(pro.getIdf()), 
					pro.getIdf()) == false) {
				arbol.addLink(pro.getIdi(), pro.getIdf(), 
						pro.getWeight());
			}

			L.remove(pro);
		}
		System.out.println(arbol.getArrows().size());
		return arbol;
	}

	public static boolean HayCiclo(Graph g, Arrow aVerificar, Node Idf, int N) {
		ArrayList<Link> aux = Idf.getEnlaces();

		if (aux.size() == 0)
			return false;

		if (Idf.existeEnlace(aVerificar.getIdi()) != -1)
			return true;

		for (int i = 0; i < aux.size(); i++) {
			Link Node = aux.get(i);

			if (!(Node.getIdf()==N))
				if (HayCiclo(g, aVerificar, g.getNode(Node.getIdf()),
						Idf.getId()))
					return true;
		}

		return false;
	}
}
