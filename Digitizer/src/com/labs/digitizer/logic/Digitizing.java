package com.labs.digitizer.logic;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import org.xmlpull.v1.XmlSerializer;

import android.content.Context;
import android.util.Log;
import android.util.Xml;

public class Digitizing {
	private static final String TAG = "Digitizing";
	private static final int EXTRA_RADIUS = 1;
	private Context con;
	private static final char[] ids = {'A','B','C','D','E','F','G','H','I',
										'J','K','L','M','N','O','P','Q','R','S',
										'T','U','V','W','X','Y','Z',
										'a','b','c','d','e','f','g','h','i','j','k',
										'l','m','n','o','p','q','r','s','t','u',
										'v','w','x','y','z'};
	private String storage_path;
	private String current_image;
	private double[][] allvec;
	private int totalvec;
	private double[][] allcir;
	private int totalcir;
	
	
	public Digitizing(Context con_, String path) {
		super();
		con = con_;
		storage_path = path + "/";
	}
	
	
	public void setCurrentImage(String ci){
		current_image = ci;
	}
	
	public double[][] getAllVec(){
		return allvec;
	} 
	
	public int getTotalVec(){
		return totalvec;
	}
	
	public double[][] getAllCir(){
		return allcir;
	} 
	
	public int getTotalCir(){
		return totalcir;
	}	
	
	
	public boolean inVertex(double x, double y, double r, double x1, double y1){
		return Math.pow((x1-x),2) + Math.pow((y1-y),2) <= Math.pow(r+EXTRA_RADIUS,2);
	}
	
	public boolean inVertex(double x1, double y1){
		boolean ok = false;
		for(int j = 0; j < totalcir && ok != true; j++){
			double[] cir = new double[4];
			cir = allcir[j]; 
			double x = cir[0],
				   y = cir[1],
				   r = cir[2]; // Radius
			if(inVertex(x, y, r, x1, y1)){
				// inside the area of the vertex
				ok = true;
			}
		}
		return ok;
	}
	
	public boolean loadData(){
		boolean task = true;
		if(this.loadDataCir())
			this.loadDataVec();
		else task = false;

		Log.e(TAG, "Image location: " + storage_path + current_image);
		
		return task;
	}
	
	private boolean loadDataCir(){
		boolean task = true;

		Mat m_img = new Mat(), m_gray = new Mat();
		File file = new File(storage_path + current_image);
		m_img = Highgui.imread(file.getAbsolutePath());

	    if(m_img.empty() == true)
	    	task = false;
	   
	    if(task == true){
		    Imgproc.cvtColor(m_img, m_gray, Imgproc.COLOR_BGR2GRAY);
		    Imgproc.GaussianBlur( m_gray, m_gray, new Size(9, 9), 0, 0);
		    Mat circles = new Mat();
		    Imgproc.HoughCircles( m_gray, circles, Imgproc.CV_HOUGH_GRADIENT, 1, 80, 70, 10, 3, 15);
		    totalcir = circles.cols();
		    allcir = new double[totalcir][3];
		    for(int i = 0; i < totalcir; i++ ){
		    	double[] cir = circles.get(0, i);
		    	allcir[i] = cir;
		    }
	    }
		
		return task;
	}
	
	private boolean loadDataVec(){
		boolean task = true;

		Mat m_img = new Mat();
		File file = new File(storage_path + current_image);
		m_img = Highgui.imread(file.getAbsolutePath());
		
	    if(m_img.empty() == true)
	    	task = false;
	   
	    if(task == true){
		    Mat dst  = new Mat(),
		        cdst = new Mat();
		    Imgproc.Canny(m_img, dst, 50, 200);
		    Imgproc.cvtColor(dst, cdst, Imgproc.COLOR_GRAY2RGBA);
		    
		    Mat lines = new Mat();
		    int threshold = 70;
		    int minLineSize = 50;
		    int lineGap = 20;
		
		    Imgproc.HoughLinesP(dst, lines, 1, Math.PI / 180, threshold,
		            minLineSize, lineGap);
		    
		    int t = lines.cols();
		    int tv = t;
		    
		    double[][] allvectors = new double[t][4];
		    for (int i = 0; i < t; i++){
		    	double[] vec = lines.get(0, i);
		    	allvectors[i] = vec;
		    }
				
		    // Una recta puede interpretarse como una recta fragmentada, de modo que hay que
		    // seleccionar unico fragmento con el que trabajar y eliminar los sobrantes, para
		    // ello se realiza una busqueda que elimina las rectas cuyo punto inicial no esta
		    // contenido en un vertice, y se prolongan las rectas cuyo punto final no esta 
		    // contenido en un vertice, la recta se prolonga hasta cortar con un vertice
		    for(int i = 0; i < t; i++){
		    	boolean[] remain = new boolean[2];
		    	remain[0] = inVertex(allvectors[i][0], allvectors[i][1]);
		    	remain[1] = inVertex(allvectors[i][2], allvectors[i][3]);
		    	if(remain[0]==true && remain[1]==true){}
		    	else if(remain[0]==true && remain[1]==false){
					// m = tang (degree angle) -> v2/v1
					double m = (allvectors[i][3]-allvectors[i][1])/(allvectors[i][2]-allvectors[i][0]);
					// Ecuacion punto-pendiente: y - y1 = m * (x - x1)
					// X se aumentará en el siguiente bucle y despejando en la anterior ecuacion
					// se obtiene la Y relativa para obtener un nuevo punto de prolongación, se
					// comprobará si dicho punto está en el area de un vertice, en tal caso la
					// prolongación finliza, obteniendo el nuevo punto final de la recta
					double x = allvectors[i][2], y = 0;
					int index = 1;
					boolean end = false;
					for( ; index<m_img.width() && end != true; index++){
						y = m * (x+index - allvectors[i][2]) + allvectors[i][3];
						end = inVertex(x+index, y);
					}
					// Se guarda el punto obtenido como el punto final de la recta prolongada
					allvectors[i][2] = x + index - 1;
					allvectors[i][3] = y;
		    	}else{
		    		allvectors[i] = null;
		    		tv--;
		    	}
		    }	    
		    
		    // Por cada recta, HoughLinesP capta 2 lineas, una por cada borde,
		    // por tanto, de cada par de lineas detectadas solo una será util.
		    // El siguiente código se encarga de deshechar las lineas sobrantes
		    for(int i = 0; i < t; i++)
		    	if(allvectors[i] != null)
				    for(int j = 0; j < t; j++)
				    	if(allvectors[j] != null && i != j){
					    	boolean[] ok = new boolean[4];
					    	for(int z = 0; z < 4; z++)
						    	if(allvectors[i][z]>allvectors[j][z])
						    		if(allvectors[i][z]-allvectors[j][z]<=lineGap*2) ok[z] = true;
						    		else ok[z] = false;
						    	else
						    		if(allvectors[j][z]-allvectors[i][z]<=lineGap*2) ok[z] = true;
						    		else ok[z] = false;
					    	if(ok[0]==true && ok[1]==true
					    			&& ok[2]==true && ok[3]==true){
						    	allvectors[j] = null;
						    	tv--;
						    }
					    }	
		    
		    int index = 0;
		    allvec = new double[tv][4];
		    for(int i = 0; i < t; i++)
		    	if(allvectors[i] != null){
		    		allvec[index] = allvectors[i];
		    		index++;
		    	}
		    
		    totalvec = tv;
		    
		    Log.e(TAG, "Graph:\n|V|:"+totalcir+"\n|A|:"+totalvec);
	    }
		    
	    return task;
	}
	
	public void generateXML(){
		File file = new File(storage_path, "graph.xml");
		FileOutputStream fout = null;
		try {
			fout = new FileOutputStream(file, false);
	    } catch (FileNotFoundException e) {
	    	e.printStackTrace();
	    }
		XmlSerializer serializer = Xml.newSerializer();
		try {
		    serializer.setOutput(fout, "UTF-8");
		    serializer.startDocument(null, true);
		    serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
		    
			serializer.startTag(null, "graph");
			serializer.attribute(null, "v", Integer.toString(totalcir));
			serializer.attribute(null, "a", Integer.toString(totalvec));
	
			for(int i = 0; i < totalcir; i++){
				serializer.startTag(null, "vertex");
				serializer.attribute(null, "id", ids[i]+"");
				serializer.attribute(null, "x", Double.toString(allcir[i][0]));
				serializer.attribute(null, "y", Double.toString(allcir[i][1]));
				serializer.attribute(null, "r", Double.toString(allcir[i][2]));
				String adjacent = "";
				for(int j = 0; j < totalvec; j++)
					if(inVertex(allcir[i][0],allcir[i][1],allcir[i][2],allvec[j][0], allvec[j][1])
							||inVertex(allcir[i][0],allcir[i][1],allcir[i][2],allvec[j][2], allvec[j][3]))
						for(int z = 0; z < totalcir; z++)
							if(i!=z && (inVertex(allcir[z][0],allcir[z][1],allcir[z][2],allvec[j][0], allvec[j][1])
									||inVertex(allcir[z][0],allcir[z][1],allcir[z][2],allvec[j][2], allvec[j][3])))
								adjacent += ids[z]+",";
				if(adjacent.indexOf(",")!=-1)
					adjacent = adjacent.substring(0, adjacent.lastIndexOf(","));
				serializer.attribute(null, "adjacent", adjacent);
				serializer.endTag(null, "vertex");
				adjacent = "";
			}
	
			serializer.endTag(null, "graph");
	
			serializer.endDocument();
			serializer.flush();
			fout.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    
}
