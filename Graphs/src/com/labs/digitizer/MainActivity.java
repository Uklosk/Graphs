package com.labs.digitizer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import com.jmga.graphs.R;
import com.labs.digitizer.camera.DigitizerView;
import com.labs.digitizer.logic.Digitizing;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends Activity implements CvCameraViewListener2{
	private static final String  	TAG = "Mensage::";
    private MenuItem[] 			 	mModeListItems;
    private SubMenu				 	mMode;
    private List<Size> 			 	mResolutionList;
    private MenuItem[] 			 	mResolutionMenuItems;
    private SubMenu				 	mResolutionMenu;
    private MenuItem             	mItemPHOTO;
    private MenuItem             	mItemXML;
    private MenuItem             	mItemSettings;
    private DigitizerView		 	mOpenCvCameraView;
    private Mat                  	mRgba;
    private String					storage_directory;
	private Digitizing 			 	digitizer;
	
	private BaseLoaderCallback  mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };
    
    
    public MainActivity() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_digitizer);

        mOpenCvCameraView = (DigitizerView) findViewById(R.id.digitizer_surface_view);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
        
        storage_directory = Environment.getExternalStorageDirectory().toString();
        digitizer = new Digitizing(getApplicationContext(), storage_directory);
        
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i(TAG, "called onCreateOptionsMenu");
        mMode = menu.addSubMenu("Mode");
        mModeListItems = new MenuItem[2];
        for(int i = 0; i < 2; i++){
        	mModeListItems[i] = mMode.add(1, i, Menu.NONE, (i%2==0)?"Camera":"Graphs");
        }
        
        mItemPHOTO = menu.add("Photo");
        mItemXML = menu.add("XML");
        mItemSettings = menu.add("Settings");
        
        mResolutionMenu = menu.addSubMenu("Resolution");
        mResolutionList = mOpenCvCameraView.getResolutionList();
        mResolutionMenuItems = new MenuItem[mResolutionList.size()];
        ListIterator<Size> resolutionItr = mResolutionList.listIterator();
        int idx = 0;
        while(resolutionItr.hasNext()) {
            Size element = resolutionItr.next();
            mResolutionMenuItems[idx] = mResolutionMenu.add(2, idx, Menu.NONE,
                    Integer.valueOf(element.width).toString() + "x" + Integer.valueOf(element.height).toString());
            idx++;
        }
        
        return true;
    }


    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    
    @Override
    public void onResume()
    {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_8, this, mLoaderCallback);
    }


	@Override
	public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat(height, width, CvType.CV_8UC4);
	}


	@Override
	public void onCameraViewStopped() {
        mRgba.release();
	}


	@Override
	public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        // input frame has RBGA format
        mRgba = inputFrame.rgba();

        return mRgba;
	}

	
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i(TAG, "called onOptionsItemSelected; selected item: " + item + " - " + item.getItemId());
        
        if (item.getGroupId() == 1){
        	int id = item.getItemId();
	        if (id == 0) {
	        	mOpenCvCameraView.enableView();
	            mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
	            Toast.makeText(this, "The camera is active", Toast.LENGTH_SHORT).show();
	            /*******************************************************************
	             * *****************************************************************
	             * *****************************************************************
	             * 
	             * Esta opción se implementa en graphs.apk, que servira para abrir
	             * este activity.
	             * 
	             * *****************************************************************
	             * *****************************************************************
	             * *****************************************************************
	             */
	        }else if (id == 1) {
	            mOpenCvCameraView.setVisibility(SurfaceView.GONE);
	        	mOpenCvCameraView.disableView();
	            Toast.makeText(this, "Now you can work", Toast.LENGTH_SHORT).show();
	            
	            /*******************************************************************
	             * *****************************************************************
	             * *****************************************************************
	             * 
	             * Abrir el activity de graphs.apk
	             * 
	             * *****************************************************************
	             * *****************************************************************
	             * *****************************************************************
	             */
	        }
        }
        
        if (item.getGroupId() == 2){
            
            /*******************************************************************
             * *****************************************************************
             * *****************************************************************
             * 
             * Cambiar reslucion de pantalla.
             * Yo lo dejaria...
             * 
             * *****************************************************************
             * *****************************************************************
             * *****************************************************************
             */
        	
            int id = item.getItemId();
            Size resolution = mResolutionList.get(id);
            mOpenCvCameraView.setResolution(resolution);
            resolution = mOpenCvCameraView.getResolution();
            String caption = Integer.valueOf(resolution.width).toString() + "x" + Integer.valueOf(resolution.height).toString();
            Toast.makeText(this, caption, Toast.LENGTH_SHORT).show();
        }

        if (item == mItemPHOTO) {
            
            /*******************************************************************
             * *****************************************************************
             * *****************************************************************
             * 
             * Tomar foto:
             * Hay que cambiar la ruta actual, por la ruta interna de la apk.
             * 
             * Hacer:
             *  -> Que cuando se tome la foto, digitalice la imagen a xml.
             *  -> Además desde la opcion XML se puede selecciona una imagen
             *     a digitalizar. Se quedan guardadas a modo historico.
             *     
             *     SI LA CARPETA ES ACCESIBLE PARA EL USUARIO, ESTE PODRIA:
             *     - Pegar fotos que le pasen amigos
             *     - Pegar pantallazos hechos con el ordenador
             *     - ...
             *     (Emulated/0/Graphs/photos)     
             * 
             * 
             * *****************************************************************
             * *****************************************************************
             * *****************************************************************
             */
        	
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
            String currentDateandTime = sdf.format(new Date());
            String fileName = storage_directory + "/graph_" + currentDateandTime + ".png";
            mOpenCvCameraView.takePicture(fileName);
            Toast.makeText(this, fileName + " saved", Toast.LENGTH_SHORT).show();
        } else if (item == mItemXML) {
            
            /*******************************************************************
             * *****************************************************************
             * *****************************************************************
             * 
             * Voy a cambiarla:
             * 
             * Esta opcion solo ocultara la camara, dejando visible la interfaz
             * que mostrara la lista de las imagenes tomadas (Historial):
             * 1. Se marca una imagen de la lista
             * 2. Se pulsa el boton digitalizar, desde donde se ejecutara todo
             *    el codigo implementado de momento en esta opcion del menu.
             * 
             * *****************************************************************
             * *****************************************************************
             * *****************************************************************
             */
        	
        	digitizer.setCurrentImage("graph.png");
        	if(digitizer.loadData() == true){
        		Toast.makeText(this, "Grafo digitalizado con éxito", Toast.LENGTH_SHORT).show();
        		digitizer.generateXML();
        	}            
            
            Bitmap b = Bitmap.createBitmap(800, 500, Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(b);
            Paint p = new Paint();
            p.setStrokeWidth(1);
            
            int t = digitizer.getTotalVec();
            double[][] po = digitizer.getAllVec();
            for(int i = 0; i < t; i++){
            	if(i%2==0)
                    p.setColor(Color.BLUE);
            	else
                    p.setColor(Color.MAGENTA);
            	c.drawLine((float)po[i][0], (float)po[i][1], (float)po[i][2], (float)po[i][3], p);
        	}
            
            int d = digitizer.getTotalCir();
            double[][] ci = digitizer.getAllCir();
            for(int i = 0; i < d; i++){
            	if(i%2==0)
                    p.setColor(Color.BLUE);
            	else
                    p.setColor(Color.MAGENTA);
            	c.drawCircle((float)ci[i][0], (float)ci[i][1], (float)ci[i][2], p);
        	}
            
            OutputStream outStream = null;
            File file = new File(storage_directory, "digitalizedgraph.png");
            try {
             outStream = new FileOutputStream(file);
             b.compress(Bitmap.CompressFormat.PNG, 100, outStream);
             outStream.flush();
             outStream.close();
            }
            catch(Exception e){
            	e.printStackTrace();
            }
            
            
        } else if (item == mItemSettings) {
        	
        }

        return true;
    }
    
    
    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }
    
}
