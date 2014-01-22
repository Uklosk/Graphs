package com.jmga.graphs.tools;

import com.jmga.graphs.classes.Graph;

public class XMLParser {

	private String storage_path;
	private String current_image;
	
	public XMLParser(){
		storage_path = "";
		current_image = "";
	}
	
	public XMLParser(String storage){
		storage_path = storage;
		current_image = "";
	}
	
	public XMLParser(String storage, String image){
		storage_path = storage;
		current_image = image;
	}
	
	public boolean parseGraph(Graph gr){
		boolean task = true;
		
		// Cargamos el grafo pasado por referencia
		
		return task;
	}
	
}
