package com.labs.digitizer.logic;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

public class Digitizing {
	private static final String TAG = "Digitizing";
	private static final char[] ids = {'A','B','C','D','E','F','G','H','H','I',
									   'J','K','L','M','N','O','P','Q','R','S',
									   'T','U','V','W','X','Y','Z'};
	private String img_path;
	private double[][] allvec;
	private int totalvec;
	private double[][] allcir;
	private int totalcir;
	
	
	public Digitizing(String path) {
		super();
		img_path = path;
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
	
	public boolean loadData(){
		boolean task = true;
		if(this.loadDataCir())
			this.loadDataVec();
		else task = false;
		
		return task;
	}
	
	private boolean loadDataCir(){
		boolean task = true;

		Mat m_img = new Mat(), m_gray = new Mat();
		File file = new File(img_path);
		m_img = Highgui.imread(file.getAbsolutePath());

	    if(m_img.empty() == true)
	    	task = false;
	   
	    if(task == true){
		    Imgproc.cvtColor(m_img, m_gray, Imgproc.COLOR_BGR2GRAY);
		    Imgproc.GaussianBlur( m_gray, m_gray, new Size(9, 9), 0, 0);
		    Mat circles = new Mat();
		    Imgproc.HoughCircles( m_gray, circles, Imgproc.CV_HOUGH_GRADIENT, 1, 5, 70, 10, 1, 15);
		    totalcir = circles.cols();
		    Log.e(TAG, "Tester: |V|:"+totalcir);
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
		File file = new File(img_path);
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
		    
		    double[][] allvectors = new double[t][5];
		    for (int i = 0; i < t; i++){
		    	double[] vec = lines.get(0, i);
		    	allvectors[i] = vec;
		    }

		    Log.e(TAG, "Tester: |A|:"+t);
				
		    // Puede darse el caso de que una linea sea interpretada como 2 o más, será 
		    // necesario un arreglo para que sea interpretada como una sóla linea, para
		    // ello el punto final de una linea será prolongado hasta cortar con un vertice
		    for(int i = 0; i < t; i++){
		    	if(allvectors[i] != null)
				    for(int j = 0; j < t; j++){
				    	if(allvectors[j] != null && i != j){
					    	boolean[] ok = new boolean[2];
				    		for(int z = 0; z < 2; z++)
						    	if(allvectors[i][z]>allvectors[j][z+2])
						    		if(allvectors[i][z]-allvectors[j][z+2]<=2) ok[z] = true;
						    		else ok[z] = false;
						    	else
						    		if(allvectors[j][z+2]-allvectors[i][z]<=2) ok[z] = true;
						    		else ok[z] = false;
				    		if(ok[0]==true && ok[1]==true){
				    			// Prolongación de la linea fragmentada (sólo a partir del primer fragmento)
				    			if(allvectors[i][5] < 0){
				    				double distance = Math.sqrt(Math.pow(allvectors[i][0]-allvectors[i][2],2)+
				    										    Math.pow(allvectors[i][1]-allvectors[i][3],2));
				    				/* Para obtener la inclinación de la recta y poder prolongarla hasta un vertice,
				    				 * necesito obtener el grado del angulo de la inclinacion del fragmento inicial:
				    				 *  x * COS(a) + y * SIN(a) = distance 
				    				 *  
				    				 *  http://es.answers.yahoo.com/question/index?qid=20080903233313AAaWjPM
				    				 */
				    			}
						    	allvectors[j][5] = -1; // Si forma parte de una linea fragmentada se marca con [][5] = -1
						    						   // Esta linea será eliminada posteriormente y servirá para eliminar
						    						   // otras posibles fragmentaciones de la recta buscada a continuación
						    }
				    	}
				    }
		    }
		    
		    // Las lineas marcadas como fragmentos secundarios de la linea prolongada se eliminan en el siguiente fragmeto
		    for(int i = 0; i < t; i++){
		    	if(allvectors[i] != null && allvectors[i][5] < 0){
		    		allvectors[i] = null;
		    		tv--;
			    	Log.e(TAG, "Eliminado 1, quedan: "+tv);
		    	}
		    }		    
		    
		    // Por cada recta, HoughLinesP capta 2 lineas, una por cada borde,
		    // por tanto, de cada par de lineas detectadas solo una será util.
		    // El siguiente código se encarga de deshechar las lineas sobrantes
		    for(int i = 0; i < t; i++){
		    	if(allvectors[i] != null)
				    for(int j = 0; j < t; j++){
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
						    	Log.e(TAG, "Eliminado 2, quedan: "+tv);
						    }
					    }	
				    }
		    }
		    
		    int index = 0;
		    allvec = new double[tv][4];
		    for(int i = 0; i < t; i++){
		    	if(allvectors[i] != null){
		    		allvec[index] = allvectors[i];
		    		index++;
		    	}
		    }
		    
		    totalvec = tv;
		    
		    
		    Log.e(TAG, "Tester: |A|:"+totalvec);
	    }
		    
	    return task;
	}
	
	public void generateXML(){
		
		for(int i = 0; i < totalcir; i++){
			double[] cir = new double[4];
			cir = allcir[i]; 
			double x = cir[0],
				   y = cir[1],
				   r = cir[2]; // Radius
			Log.i(TAG, "Tester: (Vertices:"+totalcir+") : x:"+x+", y:"+y+"   radius:"+r);
		}
		
		for(int i = 0; i < totalvec; i++){
			double[] vec = new double[4];
			vec = allvec[i]; 
			double x1 = vec[0],
				   y1 = vec[1],
				   x2 = vec[2],
				   y2 = vec[3];
			Log.i(TAG, "Tester: (Aristas:"+totalvec+") : x1:"+x1+", y1:"+y1+"  -  x2:"+x2+", y2:"+y2);
			for(int j = 0; j < totalcir; j++){
				double[] cir = new double[4];
				cir = allcir[j]; 
				double x = cir[0],
					   y = cir[1],
					   r = cir[2]; // Radius
				Log.i(TAG, "Tester: (Vertices:"+totalcir+") : x:"+x+", y:"+y+"   radius:"+r);
				if(Math.pow((x1-x),2) + Math.pow((y1-y),2) == Math.pow(r,2)){
					// inside the area of the vertex
					Log.i(TAG,"XML: Punto inicial, dentro del area de la arista");
				}else if(Math.pow((x2-x),2) + Math.pow((y2-y),2) == Math.pow(r,2)){
					// inside the area of the vertex
					Log.i(TAG,"XML: Punto final, dentro del area de la arista");
				}
			}
		}
		
	}
	
	public void cargarXML(){
		
	}
    
}
