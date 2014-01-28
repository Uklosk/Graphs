package com.jmga.graphs.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import android.util.Log;
import android.util.Xml;

import com.jmga.graphs.classes.Arrow;
import com.jmga.graphs.classes.Graph;
import com.jmga.graphs.classes.Link;
import com.jmga.graphs.classes.Node;

public class XMLParser {
	private static final String TAG = "XMLParser";

	private String storage_path;
	private String current_xml;
	
	private static final String tag_control = "apk";
	private static final String name_apk = "Graphs.apk";
	
	private int[] displacement; /* [0]:x [1]:y */
	private float density;
	
	private int[] cardinals;
	private Hashtable<String, ArrayList<String>> data;
	
	public XMLParser(){
		storage_path = "";
		current_xml = "";
		
		displacement = new int[2];
		displacement[0] = displacement[1] = 0;
		
		cardinals = new int[2];
		data = new Hashtable<String, ArrayList<String>>();
	}
	
	public XMLParser(String storage){
		storage_path = storage + "/";
		current_xml = "";

		displacement = new int[2];
		displacement[0] = displacement[1] = 0;

		cardinals = new int[2];
		data = new Hashtable<String, ArrayList<String>>();
	}
	
	public XMLParser(String storage, String xml){
		storage_path = storage + "/";
		current_xml = xml;

		displacement = new int[2];
		displacement[0] = displacement[1] = 0;

		cardinals = new int[2];
		data = new Hashtable<String, ArrayList<String>>();
	}
	
	
	public void setStorage(String storage){
		storage_path = storage;
	}
	
	public void setXml(String xml){
		current_xml = xml;
	}
	
	public void setDisplacement(int x, int y, float d){
		displacement[0] = x;
		displacement[1] = y;
		density = d;
	}
	
	public boolean isGraph(String file_path){
		Log.d(TAG,"File path: " + file_path);
		
		boolean task = false;
		FileInputStream fis = null;
		XmlPullParser xml = Xml.newPullParser();

		try {
			fis = new FileInputStream(file_path);
			xml.setInput(fis, "UTF-8");
			
			int event = xml.next();
			while(event != XmlPullParser.END_DOCUMENT){
				if(event == XmlPullParser.START_TAG){
					if(xml.getName().equals( tag_control ))
						if(xml.getAttributeName(0).equals("name"))
							if(xml.getAttributeValue(0).equals( name_apk )){
								fis.close();
								return true;
							}
				}
				event = xml.next();
			}
			fis.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return task;
	}
	
	public void saveGraph(Graph g, String name) throws Exception{
		Log.d(TAG,"File name: " + name);
		Hashtable<String, Node> v = new Hashtable<String, Node>();
		ArrayList<Arrow> a = new ArrayList<Arrow>();
		
		v = g.getVertex();
		a = g.getArrows();
		
		File file = new File(storage_path, name);
		FileOutputStream fout = null;
		fout = new FileOutputStream(file, false);
		XmlSerializer serializer = Xml.newSerializer();
		
		serializer.setOutput(fout, "UTF-8");
	    serializer.startDocument(null, true);
	    serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);

		// Control tag
		serializer.startTag(null, tag_control);
		serializer.attribute(null, "name", name_apk);
		serializer.endTag(null, tag_control);
	    
		serializer.startTag(null, "graph");
		serializer.attribute(null, "v", Integer.toString(v.size()));
		serializer.attribute(null, "a", Integer.toString(a.size()));
		
		String[] keys = g.getNombres().toArray(new String[0]); 
        Arrays.sort(keys);  		
        for(String key : keys){
        	Node v_ = new Node();
        	v_ = (Node)v.get(key);
			serializer.startTag(null, "vertex");
			serializer.attribute(null, "id", key);
			serializer.attribute(null, "x", Integer.toString(v_.getCenterX()));
			serializer.attribute(null, "y", Integer.toString(v_.getCenterY()));
			serializer.attribute(null, "r", Integer.toString(v_.getRadius()));
			String adjacent = "";
			ArrayList<Link> links = v_.getEnlaces();
			Iterator<Link> ln = links.iterator();
			while(ln.hasNext()){
				Link l = (Link)ln.next();
				adjacent += l.getIdf() + ",";
			}
			if(adjacent.length() > 0)
				adjacent = adjacent.substring(0, adjacent.lastIndexOf(","));serializer.attribute(null, "adjacent", adjacent);
			serializer.endTag(null, "vertex");
			adjacent = "";
        }

		serializer.endTag(null, "graph");
		
		serializer.endDocument();
		serializer.flush();
			fout.close();
		
	}
	
	public Graph parseGraph(Graph gr) throws Exception{
		FileInputStream fis = null;
		XmlPullParser xml = Xml.newPullParser();

		fis = new FileInputStream(storage_path + current_xml);
		xml.setInput(fis, "UTF-8");
		
		int event = xml.next();
		while(event != XmlPullParser.END_DOCUMENT) {
			ArrayList<String> xmldata = new ArrayList<String>();
			if(event == XmlPullParser.START_TAG){
				for(int i = 0; i < xml.getAttributeCount(); i++){ 
					if(xml.getName().equals("graph")){
						if(xml.getAttributeName(i).equals("v")){
							cardinals[0] = Integer.parseInt(xml.getAttributeValue(i));
						}else if(xml.getAttributeName(i).equals("a")){
							cardinals[1] = Integer.parseInt(xml.getAttributeValue(i));}
					}else if(xml.getName().equals("vertex")){
						if(xml.getAttributeName(i).equals("id")){
							data.put(xml.getAttributeValue(i), xmldata);
						}else{
							if(xml.getAttributeValue(i) != null){
								xmldata.add((xml.getAttributeValue(i).indexOf(".") > 0)
											?xml.getAttributeValue(i).substring(0, xml.getAttributeValue(i).indexOf("."))
											:xml.getAttributeValue(i));
							}
							else
								xmldata.add("");
						}
					}
				}
			}
			event = xml.next();
		}
		fis.close();
		
		ArrayList<String> aux_read = new ArrayList<String>(cardinals[0]);
		String[] keys = (String[]) data.keySet().toArray(new String[0]);  
        Arrays.sort(keys);  
        
        // Actualizando la posicion
        boolean load = false;
        // [0]:x  [1]:y
        int[] max = new int[2];
        int[] min = new int[2];
        max[0] = max[1] = min[0] = min[1] = 0; 
        for(String id : keys) {
			ArrayList<String> xmlitem = (ArrayList<String>)data.get(id);
			if(!load){
		        max[0] = min[0] = Integer.parseInt(xmlitem.get(0));
		        max[1] = min[1] = Integer.parseInt(xmlitem.get(1)); 
		        load = true;
			}else{
				for(int i=0; i<2; i++){
					if(Integer.parseInt(xmlitem.get(i)) > max[i])
						max[i] = Integer.parseInt(xmlitem.get(i));
					if(Integer.parseInt(xmlitem.get(i)) < min[i])
						min[i] = Integer.parseInt(xmlitem.get(i));
				}
			}
        }
        // Calculando el tamaño que ocupa el grafo
        // Y actualizando el desplazamiento de los vertices respecto del tamaño del canvas
        // [0]:width  [1]:height
        int[] tam = new int[2];
        for(int i=0; i<2; i++){
        	//tam[i] = max[i] - min[i];
        	tam[i] = max[i] + min[i];
        	displacement[i] = (displacement[i] - tam[i])/2;
        }
        
        // Generando el objeto grafo
        for(String id : keys) {  
			ArrayList<String> xmlitem = (ArrayList<String>)data.get(id);
			gr.addNode(Integer.parseInt(xmlitem.get(0))+displacement[0],
						Integer.parseInt(xmlitem.get(1))+displacement[1]);
		}
        for(String id : keys) {  
			ArrayList<String> xmlitem = (ArrayList<String>)data.get(id);
			aux_read.add(id);
			// El xml se genera teniendo en cuenta el orden de los vertices respecto a sus coordenadas,
			// entonces si el ID de un vertice adyacente, ya fue registrado anteriormente como vertice
			// principal (hash<"id principal", ArrayList>), la arista que pueda generarse con esos IDs
			// ya existira y por tanto no se añadira
			String adj = xmlitem.get(3);
			if(adj.length()>0)
				if(adj.indexOf(",") != -1){
					String[] adjacent = adj.split(",");
					for(int i=0; i<adjacent.length; i++)
						if(aux_read.contains(adjacent[i]) == false)
							gr.addLink(id, adjacent[i], 1);
				}else
					if(aux_read.contains(adj) == false)
						gr.addLink(id, adj, 1);
        }
		
		Log.d(TAG,"Grafo generado a partir del xml");
		gr.update();
		return gr;
	}
	
}
