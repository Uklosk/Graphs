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
	private static final String  TAG = "Mensage::";
    private MenuItem[] 			 mModeListItems;
    private SubMenu				 mMode;
    private List<Size> 			 mResolutionList;
    private MenuItem[] 			 mResolutionMenuItems;
    private SubMenu				 mResolutionMenu;
    private MenuItem             mItemPHOTO;
    private MenuItem             mItemXML;
    private MenuItem             mItemSettings;
    private DigitizerView		 mOpenCvCameraView;
    private Mat                  mRgba;
	private Digitizing 			 digitizer;
	
	private BaseLoaderCallback  mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                    digitizer = new Digitizing(Environment.getExternalStorageDirectory().getPath() + "/grafo.png");
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

        setContentView(R.layout.activity_main);

        mOpenCvCameraView = (DigitizerView) findViewById(R.id.digitizer_surface_view);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
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
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this, mLoaderCallback);
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
	        }else if (id == 1) {
	            mOpenCvCameraView.setVisibility(SurfaceView.GONE);
	        	mOpenCvCameraView.disableView();
	            Toast.makeText(this, "Now you can work", Toast.LENGTH_SHORT).show();
	        }
        }
        
        if (item.getGroupId() == 2){
            int id = item.getItemId();
            Size resolution = mResolutionList.get(id);
            mOpenCvCameraView.setResolution(resolution);
            resolution = mOpenCvCameraView.getResolution();
            String caption = Integer.valueOf(resolution.width).toString() + "x" + Integer.valueOf(resolution.height).toString();
            Toast.makeText(this, caption, Toast.LENGTH_SHORT).show();
        }

        if (item == mItemPHOTO) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
            String currentDateandTime = sdf.format(new Date());
            String fileName = Environment.getExternalStorageDirectory().getPath() +
                                   "/Grafo_" + currentDateandTime + ".jpg";
            mOpenCvCameraView.takePicture(fileName);
            Toast.makeText(this, fileName + " saved", Toast.LENGTH_SHORT).show();
        } else if (item == mItemXML) {
            digitizer.loadData();
            digitizer.generateXML();
            
            
            Bitmap b = Bitmap.createBitmap(800, 500, Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(b);
            Paint p = new Paint();
            p.setColor(Color.BLUE);
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
            
            String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
            OutputStream outStream = null;
            File file = new File(extStorageDirectory, "digitalizer.png");
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
