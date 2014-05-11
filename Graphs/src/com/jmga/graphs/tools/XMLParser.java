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
import com.jmga.graphs.classes.GView;
import com.jmga.graphs.classes.Graph;
import com.jmga.graphs.classes.Link;
import com.jmga.graphs.classes.Node;

public class XMLParser {
	private static final String TAG = "XMLParser";

	private String storage_path;
	private String current_xml;

	// Controla la lectura ordenada de los grafos A y B para un ejercicio de
	// isomorfismo
	private int control_isomorphism;

	private float[] displacement; /* [0]:x [1]:y */
	private float density;
	private GView view;
	private int[] cardinals;
	private Hashtable<String, ArrayList<String>> data;

	public XMLParser(GView view) {
		storage_path = "";
		current_xml = "";

		control_isomorphism = 0;

		this.view = view;
		density = view.getDensity();

		displacement = new float[2];
		displacement[0] = displacement[1] = 1;

		cardinals = new int[2];
		data = new Hashtable<String, ArrayList<String>>();
	}

	public XMLParser(String storage, GView view) {
		storage_path = storage + "/";
		checkTheDirectory();
		current_xml = "";

		control_isomorphism = 0;

		this.view = view;
		density = view.getDensity();

		displacement = new float[2];
		displacement[0] = displacement[1] = 1;

		cardinals = new int[2];
		data = new Hashtable<String, ArrayList<String>>();
	}

	public XMLParser(String storage, String xml, GView view) {
		storage_path = storage + "/";
		checkTheDirectory();
		current_xml = xml;

		control_isomorphism = 0;

		this.view = view;
		density = view.getDensity();
		displacement = new float[2];
		displacement[0] = displacement[1] = 1;

		cardinals = new int[2];
		data = new Hashtable<String, ArrayList<String>>();
	}

	private void checkTheDirectory() {
		File folder = new File(storage_path);
		folder.mkdirs();
	}

	public void setStorage(String storage) {
		storage_path = storage;
	}

	public void setXml(String xml) {
		current_xml = xml;
	}

	public static boolean isGraph(String file_path) {
		Log.d(TAG, "File path: " + file_path);

		boolean task = false;
		FileInputStream fis = null;
		XmlPullParser xml = Xml.newPullParser();

		try {
			fis = new FileInputStream(file_path);
			xml.setInput(fis, "UTF-8");

			int event = xml.next();
			while (event != XmlPullParser.END_DOCUMENT) {
				if (event == XmlPullParser.START_TAG) {
					if (xml.getName().equals("isomorphism")) {
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

	public static boolean isIsomorphism(String file_path) {
		Log.d(TAG, "File path: " + file_path);

		boolean task = false;
		FileInputStream fis = null;
		XmlPullParser xml = Xml.newPullParser();

		try {
			fis = new FileInputStream(file_path);
			xml.setInput(fis, "UTF-8");

			int event = xml.next();
			while (event != XmlPullParser.END_DOCUMENT) {
				if (event == XmlPullParser.START_TAG) {
					if (xml.getName().equals("isomorphism")) {
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

	public void saveGraph(Graph g, String name) throws Exception {
		Log.d(TAG, "File name: " + name);
		Hashtable<Integer, Node> v = new Hashtable<Integer, Node>();
		ArrayList<Arrow> a = new ArrayList<Arrow>();

		v = g.getVertex();
		a = g.getArrows();

		File file = new File(storage_path, name);
		FileOutputStream fout = null;
		fout = new FileOutputStream(file, false);
		XmlSerializer serializer = Xml.newSerializer();

		serializer.setOutput(fout, "UTF-8");
		serializer.startDocument(null, true);
		serializer.setFeature(
				"http://xmlpull.org/v1/doc/features.html#indent-output", true);

		serializer.startTag(null, "graph");
		serializer.attribute(null, "v", Integer.toString(v.size()));
		serializer.attribute(null, "a", Integer.toString(a.size()));

		String[] keys = g.getNombresString().toArray(new String[0]);
		Arrays.sort(keys);
		for (String key : keys) {
			Node v_ = new Node();
			v_ = (Node) v.get(Integer.parseInt(key));
			serializer.startTag(null, "vertex");
			serializer.attribute(null, "id", key);
			serializer.attribute(null, "x", Float.toString(v_.getPosX()));
			serializer.attribute(null, "y", Float.toString(v_.getPosY()));
			serializer.attribute(null, "r", Integer.toString(v_.getRadius()));
			String adjacent = "", weights = "";
			ArrayList<Link> links = v_.getEnlaces();
			Iterator<Link> ln = links.iterator();
			while (ln.hasNext()) {
				Link l = (Link) ln.next();
				adjacent += l.getIdf() + ",";
				weights += (int) l.getweight() + ",";
			}
			Log.i(TAG, "Vertices: " + adjacent);
			Log.i(TAG, "Pesos   : " + weights);
			if (adjacent.length() > 0)
				adjacent = adjacent.substring(0, adjacent.lastIndexOf(","));
			serializer.attribute(null, "adjacent", adjacent);
			if (weights.length() > 0)
				weights = weights.substring(0, weights.lastIndexOf(","));
			serializer.attribute(null, "weights", weights);
			serializer.endTag(null, "vertex");
			adjacent = "";
		}

		serializer.endTag(null, "graph");

		serializer.endDocument();
		serializer.flush();
		fout.close();

	}

	public Graph parseGraph(Graph gr) throws Exception {
		FileInputStream fis = null;
		XmlPullParser xml = Xml.newPullParser();

		fis = new FileInputStream(storage_path + current_xml);
		xml.setInput(fis, "UTF-8");

		boolean graph_load = false;
		int event = xml.next();
		while (event != XmlPullParser.END_DOCUMENT) {
			ArrayList<String> xmldata = new ArrayList<String>();
			if (event == XmlPullParser.START_TAG) {
				for (int i = 0; i < xml.getAttributeCount(); i++) {
					if (xml.getName().equals("isomorphism")) {
						control_isomorphism = (control_isomorphism == 0) ? 1
								: 2;
					} else if (xml.getName().equals("graph")
							|| xml.getName().equals(
									"graph" + control_isomorphism)) {
						if (xml.getAttributeName(i).equals("v")) {
							cardinals[0] = Integer.parseInt(xml
									.getAttributeValue(i));
						} else if (xml.getAttributeName(i).equals("a")) {
							cardinals[1] = Integer.parseInt(xml
									.getAttributeValue(i));
						}
						graph_load = true;
					} else if (xml.getName().equals("vertex")
							&& graph_load == true) {
						if (xml.getAttributeName(i).equals("id")) {
							data.put(xml.getAttributeValue(i), xmldata);
						} else {
							if (xml.getAttributeValue(i) != null) {
								xmldata.add(xml.getAttributeValue(i));
							} else
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
		// [0]:x [1]:y
		float[] max = new float[2];
		float[] min = new float[2];
		max[0] = max[1] = min[0] = min[1] = 0;
		for (String id : keys) {
			ArrayList<String> xmlitem = (ArrayList<String>) data.get(id);
			if (!load) {
				max[0] = min[0] = Float.parseFloat(xmlitem.get(0));
				max[1] = min[1] = Float.parseFloat(xmlitem.get(1));
				load = true;
			} else {
				for (int i = 0; i < 2; i++) {
					if (Float.parseFloat(xmlitem.get(i)) > max[i])
						max[i] = Float.parseFloat(xmlitem.get(i));
					if (Float.parseFloat(xmlitem.get(i)) < min[i])
						min[i] = Float.parseFloat(xmlitem.get(i));
				}
			}
		}
		// Calculando el tamaño que ocupa el grafo
		// Y actualizando el desplazamiento de los vertices respecto del tamaño
		// del canvas
		// [0]:width [1]:height
		float[] tam = new float[2];
		for (int i = 0; i < 2; i++) {
			tam[i] = max[i] + min[i];
			displacement[i] = (1 - tam[i]) / 2;
		}

		// Generando el objeto grafo
		for (String id : keys) {
			ArrayList<String> xmlitem = (ArrayList<String>) data.get(id);

			gr.addNodeF(Integer.parseInt(id), Float.parseFloat(xmlitem.get(0))
					+ displacement[0], Float.parseFloat(xmlitem.get(1))
					+ displacement[1], view.getViewportWidth(),
					view.getViewportHeight(), density);
		}
		for (String id : keys) {
			ArrayList<String> xmlitem = (ArrayList<String>) data.get(id);
			aux_read.add(id);
			// El xml se genera teniendo en cuenta el orden de los vertices
			// respecto a sus coordenadas,
			// entonces si el ID de un vertice adyacente, ya fue registrado
			// anteriormente como vertice
			// principal (hash<"id principal", ArrayList>), la arista que pueda
			// generarse con esos IDs
			// ya existira y por tanto no se añadira
			String adj = xmlitem.get(3);
			String wei = xmlitem.get(4);
			if (adj.length() > 0)
				if (adj.indexOf(",") != -1) {
					String[] adjacent = adj.split(",");
					String[] weight = wei.split(",");
					for (int i = 0; i < adjacent.length; i++)
						if (aux_read.contains(adjacent[i]) == false) {
							gr.addLink(Integer.parseInt(id),
									Integer.parseInt(adjacent[i]),
									Integer.parseInt(weight[i]));
						}
				} else if (aux_read.contains(adj) == false) {
					gr.addLink(Integer.parseInt(id), Integer.parseInt(adj),
							Integer.parseInt(wei));
				}
		}

		gr.update();
		return gr;
	}

	public Graph parseIsoGraph(Graph gr, int caso) throws Exception {
		FileInputStream fis = null;
		XmlPullParser xml = Xml.newPullParser();

		fis = new FileInputStream(storage_path + current_xml);
		xml.setInput(fis, "UTF-8");

		boolean graph_load = false;
		int event = xml.next();
		while (event != XmlPullParser.END_DOCUMENT) {
			ArrayList<String> xmldata = new ArrayList<String>();
			if (event == XmlPullParser.START_TAG) {
				for (int i = 0; i < xml.getAttributeCount(); i++) {
					if (xml.getName().equals("isomorphism")) {
						control_isomorphism = (control_isomorphism == 0) ? 1
								: 2;
					} else if (xml.getName().equals("graph")
							|| xml.getName().equals(
									"graph" + control_isomorphism)) {
						if (xml.getAttributeName(i).equals("v")) {
							cardinals[0] = Integer.parseInt(xml
									.getAttributeValue(i));
						} else if (xml.getAttributeName(i).equals("a")) {
							cardinals[1] = Integer.parseInt(xml
									.getAttributeValue(i));
						}
						graph_load = true;
					} else if (xml.getName().equals("vertex")
							&& graph_load == true) {
						if (xml.getAttributeName(i).equals("id")) {
							data.put(xml.getAttributeValue(i), xmldata);
						} else {
							if (xml.getAttributeValue(i) != null) {
								xmldata.add(xml.getAttributeValue(i));
							} else
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
		// [0]:x [1]:y
		float[] max = new float[2];
		float[] min = new float[2];
		max[0] = max[1] = min[0] = min[1] = 0;
		for (String id : keys) {
			ArrayList<String> xmlitem = (ArrayList<String>) data.get(id);
			if (!load) {
				max[0] = min[0] = Float.parseFloat(xmlitem.get(0));
				max[1] = min[1] = Float.parseFloat(xmlitem.get(1));
				load = true;
			} else {
				for (int i = 0; i < 2; i++) {
					if (Float.parseFloat(xmlitem.get(i)) > max[i])
						max[i] = Float.parseFloat(xmlitem.get(i));
					if (Float.parseFloat(xmlitem.get(i)) < min[i])
						min[i] = Float.parseFloat(xmlitem.get(i));
				}
			}
		}
		// Calculando el tamaño que ocupa el grafo
		// Y actualizando el desplazamiento de los vertices respecto del tamaño
		// del canvas
		// [0]:width [1]:height
		float[] tam = new float[2];
		for (int i = 0; i < 2; i++) {
			tam[i] = max[i] + min[i];
			displacement[i] = (1 - tam[i]) / 2;
		}

		// Generando el objeto grafo
		for (String id : keys) {
			ArrayList<String> xmlitem = (ArrayList<String>) data.get(id);
			switch (caso) {
			case 1:
				if(view.getViewportWidth()>view.getViewportHeight())
				gr.addNodeF(Integer.parseInt(id),
						Float.parseFloat(xmlitem.get(0)) + displacement[0],
						Float.parseFloat(xmlitem.get(1)) + displacement[1],
						view.getViewportWidth()/2, view.getViewportHeight(),
						density);
				else
				gr.addNodeF(Integer.parseInt(id),
						Float.parseFloat(xmlitem.get(0)) + displacement[0],
						Float.parseFloat(xmlitem.get(1)) + displacement[1],
						view.getViewportWidth(), view.getViewportHeight()/2,
						density);
				break;
			case 2:
				if(view.getViewportWidth()>view.getViewportHeight())
				gr.addNodeF(Integer.parseInt(id),
						Float.parseFloat(xmlitem.get(0)) + displacement[0]+ 1f,
						Float.parseFloat(xmlitem.get(1)) + displacement[1],
						view.getViewportWidth()/2, view.getViewportHeight(),
						density);
				else
				gr.addNodeF(Integer.parseInt(id),
						Float.parseFloat(xmlitem.get(0)) + displacement[0],
						Float.parseFloat(xmlitem.get(1)) + displacement[1]+1f,
						view.getViewportWidth(), view.getViewportHeight()/2,
						density);
					
			}
		}
		for (String id : keys) {
			ArrayList<String> xmlitem = (ArrayList<String>) data.get(id);
			aux_read.add(id);
			// El xml se genera teniendo en cuenta el orden de los vertices
			// respecto a sus coordenadas,
			// entonces si el ID de un vertice adyacente, ya fue registrado
			// anteriormente como vertice
			// principal (hash<"id principal", ArrayList>), la arista que pueda
			// generarse con esos IDs
			// ya existira y por tanto no se añadira
			String adj = xmlitem.get(3);
			String wei = xmlitem.get(4);
			if (adj.length() > 0)
				if (adj.indexOf(",") != -1) {
					String[] adjacent = adj.split(",");
					String[] weight = wei.split(",");
					for (int i = 0; i < adjacent.length; i++)
						if (aux_read.contains(adjacent[i]) == false) {
							gr.addLink(Integer.parseInt(id),
									Integer.parseInt(adjacent[i]),
									Integer.parseInt(weight[i]));
						}
				} else if (aux_read.contains(adj) == false) {
					gr.addLink(Integer.parseInt(id), Integer.parseInt(adj),
							Integer.parseInt(wei));
				}
		}

		gr.update();
		return gr;
	}

	public Hashtable<String, Graph> parseIsomorphism(
			Hashtable<String, Graph> isomorphism) throws Exception {
		Graph ga = new Graph();
		Graph gb = new Graph();

		ga = parseGraph(ga);
		// A : izquierda
		control_isomorphism++; // Para guardar el segundo grafo del archivo
		gb = parseGraph(gb);
		// B : dercha

		isomorphism.put("A", ga);
		isomorphism.put("B", gb);

		return isomorphism;
	}

}
