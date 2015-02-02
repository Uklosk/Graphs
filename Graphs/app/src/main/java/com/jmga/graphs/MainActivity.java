package com.jmga.graphs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Hashtable;
import java.util.zip.Inflater;

import android.R.color;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.view.ActionProvider;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.jmga.graphs.classes.GView;
import com.jmga.graphs.classes.Node;
import com.jmga.graphs.tools.XMLParser;
import com.jmga.graphs.tools.auxiliary.SizeView;

public class MainActivity extends ActionBarActivity implements ActionBar.TabListener, NavigationDrawerFragment.NavigationDrawerCallbacks {

	private final static String storage = Environment
			.getExternalStorageDirectory().toString() + "/Graphs";

	SharedPreferences prefs;
    private static boolean running = false;
    private static boolean first_time = true;

	private static GView g_view;
	private static Node nFocused;
    private static int gFocused;
	private Menu menu;

	private final SizeView size = new SizeView();

	private static final int GRAPH_MODE_NODES = 0;
	private static final int GRAPH_MODE_ARROWS = 1;
	private static final int GRAPH_MODE_WEIGHTS = 2;
    private static final int MAX_DESVIATION = 10;
    private static long lastPressTime = 0;
    private static int lastX, lastY;

	private int gMode = GRAPH_MODE_NODES;

	private static final int TOGGLE_ADD = 0;
	private static final int TOGGLE_REMOVE = 1;
    /*
	boolean isKruskal = false;
	boolean isBipartite = false;
	boolean isIso = false;
	*/
	boolean toggle_remove = false;

	boolean toggle_add = false;

    private NavigationDrawerFragment mNavigationDrawerFragment;


    public int weight = 0;
	private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";

	private static final int REQUEST_PATH = 0, ISOMORPHISM = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
		copyAssets();
		//view = new GView(this);
        gFocused = 0;

		prefs = getSharedPreferences("Preferences_Graph", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		if (!running) {
			editor.putString("mode", "graph");
			editor.putString("currentfile", "");
			editor.putString("load", "");
		}
		editor.commit();
		running=true;

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

		/*final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayShowHomeEnabled(false);

		actionBar.addTab(actionBar.newTab().setText("vértices")
				.setTabListener(this));
		actionBar.addTab(actionBar.newTab().setText("aristas")
				.setTabListener(this));
		actionBar.addTab(actionBar.newTab().setText("pesos")
				.setTabListener(this));
        */


	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		MenuInflater inflater = getMenuInflater();

		inflater.inflate(R.menu.menu_1, menu);
		this.menu = menu;

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {

		if (gMode == GRAPH_MODE_WEIGHTS) {
			menu.findItem(R.id.action_add).setVisible(false);
			menu.findItem(R.id.action_remove).setVisible(false);
			menu.findItem(R.id.action_edit).setVisible(true);

			View v = (View) menu.findItem(R.id.action_edit).getActionView();
			EditText text = (EditText) v.findViewById(R.id.weightText);

			text.setOnEditorActionListener(new OnEditorActionListener() {

				@Override
				public boolean onEditorAction(TextView v, int actionId,
						KeyEvent event) {
					if (v.getText() != null) {
						weight = Integer.parseInt(v.getText().toString());
					}
					System.out.println(weight);
					return false;
				}
			});
		} else {

			menu.findItem(R.id.action_add).setVisible(true);
			menu.findItem(R.id.action_remove).setVisible(true);
			menu.findItem(R.id.action_edit).setVisible(false);

		}

		updatingMenu();

		this.menu = menu;
		return super.onPrepareOptionsMenu(menu);

	}

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        if(first_time){
            fragmentManager.beginTransaction()
                    .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                    .commit(); first_time = false;}
        else if(gFocused==position) g_view.toggle_visible(position);
        else {
            if(!g_view.isVisible(position))g_view.toggle_visible(position);
            gFocused = position;
        }



    }

	public void updatingMenu() {
		/*menu.findItem(R.id.action_save).setEnabled(view.save_graph);
		menu.findItem(R.id.action_tableinfo).setEnabled(view.info_table);
		menu.findItem(R.id.action_distance_table).setEnabled(view.table_dist);
		menu.findItem(R.id.action_clear).setEnabled(view.cleangraph);
        */
		stop(TOGGLE_ADD, (MenuItem) menu.findItem(R.id.action_add));
		stop(TOGGLE_REMOVE, (MenuItem) menu.findItem(R.id.action_remove));

        /*menu.findItem(R.id.action_kruskal).setEnabled(view.isKruskal);
		menu.findItem(R.id.action_kruskal).setChecked(isKruskal);
		menu.findItem(R.id.action_bipartit).setEnabled(view.isBipartite);
		menu.findItem(R.id.action_bipartit).setChecked(
				(view.isBipartite) ? isBipartite : false);*/
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.action_load:
			if (!isExternalStorageWritable()) {
				Toast toast = Toast.makeText(getBaseContext(),
						getString(R.string.memory_not_available),
						Toast.LENGTH_LONG);
				toast.show();

				return true;
			}
			getfile();

			return true;
            /**
             *

		case R.id.action_save:
			if (!isExternalStorageWritable()) {
				Toast toast = Toast.makeText(getBaseContext(),
						getString(R.string.memory_not_available),
						Toast.LENGTH_LONG);
				toast.show();

				return true;
			}
			LayoutInflater factory = LayoutInflater.from(this);
			final View dialogView = factory.inflate(R.layout.save_dialog, null);
			AlertDialog.Builder builder_ = new AlertDialog.Builder(this);
			builder_.setTitle(R.string.file_titlesave);
			builder_.setView(dialogView);
			builder_.setPositiveButton(R.string.file_save,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							EditText txtTexto = (EditText) dialogView
									.findViewById(R.id.text_filename);
							String file_name = txtTexto.getText().toString();
							if (view.graphToXML(storage, file_name)) {
								Toast.makeText(
										getApplicationContext(),
										"Guardado correctamente como " + file_name
												+ ".graph", Toast.LENGTH_LONG)
										.show();
								dialog.dismiss();
								view.update();
							} else
								Toast.makeText(
										getApplicationContext(),
										"No ha podido guardarse, prueba de nuevo.",
										Toast.LENGTH_LONG).show();
						}
					});
			builder_.setNegativeButton(R.string.file_cancel,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}
					});
			AlertDialog dialog_ = builder_.create();
			dialog_.show();

			return true;

		case R.id.action_clear:
			view.graphToPNG();

			return true;

		case R.id.action_bipartit:
			if (isBipartite) {
				isBipartite = false;
				view.initializingNodesColor();
				view.invalidate();
			} else {
				isBipartite = true;
				view.bipartite(true);
				view.invalidate();
			}

			return true;
		case R.id.action_iso:

			getisofile();
			return true;
		case R.id.action_kruskal:
			if (isKruskal) {
				isKruskal = false;
				view.restore();

			} else {
				isKruskal = true;
			}
			view.setMenuStateChecked(isKruskal, isBipartite);
			view.update();
			view.invalidate();

			return true;

		case R.id.action_distance_table:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Tabla de distancias");
			builder.setView(view.dijkstra(getApplicationContext()));
			AlertDialog dialog = builder.create();
			dialog.show();

			return true;

		case R.id.action_tableinfo:
			LayoutInflater factory_ = LayoutInflater.from(this);
			final View dialogView_ = factory_.inflate(
					R.layout.information_graph, null);
			AlertDialog.Builder builder__ = new AlertDialog.Builder(this);
			builder__.setTitle(R.string.action_tableinfo);

			Hashtable<String, String> info = view.getTableInfo();
			TextView v = (TextView) dialogView_.findViewById(R.id.val_vertex);
			v.setText(info.get("|V|"));
			TextView a = (TextView) dialogView_.findViewById(R.id.val_edges);
			a.setText(info.get("|A|"));
			TextView b = (TextView) dialogView_
					.findViewById(R.id.val_bipartite);
			b.setText(info.get("Bipartite"));
			TextView c = (TextView) dialogView_
					.findViewById(R.id.val_component);
			c.setText(info.get("Components"));
			TextView s = (TextView) dialogView_.findViewById(R.id.val_sum);
			s.setText(info.get("Sum"));
			TextView r = (TextView) dialogView_.findViewById(R.id.val_regular);
			r.setText(info.get("Regular"));
			TextView d = (TextView) dialogView_.findViewById(R.id.val_sequence);
			d.setText(info.get("Sequence"));

			builder__.setView(dialogView_);
			AlertDialog dialog__ = builder__.create();
			dialog__.show();

			return true;

		case R.id.action_clear:
			clear();

			return true;
             */
		case R.id.action_settings:
			LayoutInflater f = LayoutInflater.from(this);
			final View dv = f.inflate(R.layout.menu_settings, null);
			AlertDialog.Builder bu = new AlertDialog.Builder(this);

			bu.setTitle(R.string.action_settings);
			/*
			 * Scale graph
			 */
			SeekBar seekBar = (SeekBar) dv.findViewById(R.id.seekBar_zoom);
			seekBar.setProgress((size.getNew_percent() == 0) ? size
					.getOld_percent() : size.getNew_percent());
			final TextView seekBarValue = (TextView) dv
					.findViewById(R.id.settings_zoom);
			seekBarValue
					.setText(Integer.toString((size.getNew_percent() == 0) ? size
							.getOld_percent() : size.getNew_percent()));
			seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
				@Override
				public void onProgressChanged(SeekBar seekBar, int progress,
						boolean fromUser) {
					seekBarValue.setText(String.valueOf(progress));

				}

				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {
					//size.setOld_height(view.getViewportHeight());
					//size.setOld_width(view.getViewportWidth());
					size.setOld_percent(Integer.parseInt(seekBarValue.getText()
							.toString()));
				}

				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {
					int nw = size.getOld_width()
							* Integer.parseInt(seekBarValue.getText()
									.toString()) / size.getOld_percent();
					int nh = size.getOld_height()
							* Integer.parseInt(seekBarValue.getText()
									.toString()) / size.getOld_percent();
					size.setNew_width(nw);
					size.setNew_height(nh);
					size.setNew_percent(Integer.parseInt(seekBarValue.getText()
							.toString()));
				}
			});
			/*
			 * Scale vertex

			SeekBar seekBar_vertex = (SeekBar) dv
					.findViewById(R.id.seekBar_zoomvertex);
			seekBar_vertex
					.setProgress((size.getNew_percent_vertex() == 0) ? size
							.getOld_percent_vertex() : size
							.getNew_percent_vertex());
			final TextView seekBarValue_vertex = (TextView) dv
					.findViewById(R.id.setting_zoomvertex);
			seekBarValue_vertex.setText(Integer.toString((size
					.getNew_percent_vertex() == 0) ? size
					.getOld_percent_vertex() : size.getNew_percent_vertex()));
			seekBar_vertex
					.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
						@Override
						public void onProgressChanged(SeekBar seekBar,
								int progress, boolean fromUser) {
							seekBarValue_vertex.setText(String
									.valueOf(progress));
							size.setNew_percent_vertex(progress);
							view.changeRadius(size);
						}

						@Override
						public void onStartTrackingTouch(SeekBar seekBar) {
							//
						}

						@Override
						public void onStopTrackingTouch(SeekBar seekBar) {
							//
						}
					});
			bu.setPositiveButton(R.string.file_save,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							view.resizeGraph(size);
							view.update();
							size.setOld_percent(100);
							size.setNew_percent(100);
						}
					});
			bu.setNegativeButton(R.string.file_cancel,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}
					});
			bu.setView(dv);

			AlertDialog di = bu.create();
			di.getWindow().clearFlags(
					WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			di.show();
            */
			return true;

		case R.id.action_remove:
			toggle(TOGGLE_REMOVE, item);
			stop(TOGGLE_ADD, (MenuItem) menu.findItem(R.id.action_add));

			return true;

		case R.id.action_add:
			toggle(TOGGLE_ADD, item);
			stop(TOGGLE_REMOVE, (MenuItem) menu.findItem(R.id.action_remove));

			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

    /*
	public void clear() {
		isKruskal = isBipartite = isIso = false;
		updatingMenu();
		view.initializingNodesColor();
		view.clear();
	}*/

	public class CustomEditProvider extends ActionProvider {
		public int weight = 0;
		private EditText text;
		Context mContext;

		public CustomEditProvider(Context context) {
			super(context);
			mContext = context;
			// TODO Auto-generated constructor stub
		}

		@Override
		public View onCreateActionView() {
			// Inflate the action view to be shown on the action bar.
			LayoutInflater layoutInflater = LayoutInflater.from(mContext);
			View view = layoutInflater.inflate(R.layout.edit_weight_layout,
					null);
			text = (EditText) view.findViewById(R.id.weightText);
			text.setOnEditorActionListener(new OnEditorActionListener() {
				@Override
				public boolean onEditorAction(TextView v, int actionId,
						KeyEvent event) {
					if (actionId == EditorInfo.IME_ACTION_DONE) {
						if (text.getText().toString() != null) {
							weight = Integer
									.parseInt(text.getText().toString());
						}

					}
					return false;
				}
			});
			return view;

		}
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onRestoreInstanceState(savedInstanceState);
		if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
			getActionBar().setSelectedNavigationItem(
					savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
		}		
		//view.graphToRestore = 2;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
        first_time = true;
		// http://www.sgoliver.net/blog/?p=1731
		SharedPreferences.Editor editor = prefs.edit();
		String mode = prefs.getString("mode", "");
		if (mode.equals("isomorphism")) {

			editor.putString("currentfile", prefs.getString("currentfile", ""));
			editor.commit();
		} else {
			//view.graphToXML(getFilesDir().toString(), "/temp_save----");
		}
		editor.commit();
		// Serialize the current tab position.
		//outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, getActionBar()
		//		.getSelectedNavigationIndex());
		//view.graphToXML(getFilesDir().toString(), "/temp_save----");

	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, show the tab contents in the
		// container view.
		switch (tab.getPosition()) {
		case 0:
			gMode = GRAPH_MODE_NODES;
			break;
		case 1:
			gMode = GRAPH_MODE_ARROWS;
			break;
		case 2:
			gMode = GRAPH_MODE_WEIGHTS;
			break;
		}
		invalidateOptionsMenu();
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	public void addNodeM(View v, MotionEvent event, int x, int y, Node node, int grafo) {
		switch (event.getActionMasked()) {
		case MotionEvent.ACTION_DOWN:
			if (node == null) {
                /**
                 *
                 *  MODIFICAR
                 */
				//view.addNode(x, y,0);
			} else {
				nFocused = node;
			}
			break;
		case MotionEvent.ACTION_MOVE:
			if (nFocused != null) {
				//view.setPosition(x, y, nFocused);
			}
			break;

		case MotionEvent.ACTION_UP:
			nFocused = null;
			break;

		default:
			break;

		}
	}

	public void deleteNodeM(View v, MotionEvent event, int x, int y, Node node, int grafo) {
		switch (event.getActionMasked()) {
		case MotionEvent.ACTION_DOWN:
			if (node != null) {
				nFocused = node;

			}
			break;
		case MotionEvent.ACTION_MOVE:

			break;

		case MotionEvent.ACTION_UP:
			if (nFocused == node) {
				//view.deleteNode(nFocused, grafo);
			}
			break;

		default:
			break;

		}
	}

	public void addArrowM(View v, MotionEvent event, int x, int y, Node node, int grafo) {
		switch (event.getActionMasked()) {
		case MotionEvent.ACTION_DOWN:
			if (node != null) {
				nFocused = node;
                gFocused = grafo;
				//view.addAux(nFocused, x, y);
			}
			break;
		case MotionEvent.ACTION_MOVE:
			if (nFocused != null) {
				//view.updateAux(x, y);
			}
			break;

		case MotionEvent.ACTION_UP:
			if (node == nFocused) {

			} else if (node != null && node != nFocused) {
				//view.addArrow(nFocused, node, grafo);
			}
			//view.deleteAux();

			nFocused = null;
			break;

		default:
			break;

		}

	}

	public void deleteArrowM(View v, MotionEvent event, int x, int y, Node node, int grafo) {

		switch (event.getActionMasked()) {
		case MotionEvent.ACTION_DOWN:
			if (node != null) {
				nFocused = node;
				//view.addAux(nFocused, x, y);
			}
			break;
		case MotionEvent.ACTION_MOVE:
			if (nFocused != null) {
				//view.updateAux(x, y);
			}
			break;

		case MotionEvent.ACTION_UP:
			if (node == nFocused) {

			} else if (node != null && node != nFocused) {
				//view.deleteArrow(nFocused, node, grafo);
			}
			//view.deleteAux();

			nFocused = null;
			break;

		default:
			break;
		}
	}

	public void weightM(View v, MotionEvent event, int x, int y, Node node, int grafo) {

		switch (event.getActionMasked()) {
		case MotionEvent.ACTION_DOWN:
			if (node != null) {
				nFocused = node;
				//view.addAux(nFocused, x, y);
			}
			break;
		case MotionEvent.ACTION_MOVE:
			if (nFocused != null) {
				//view.updateAux(x, y);
			}
			break;

		case MotionEvent.ACTION_UP:
			if (node == nFocused) {

			} else if (node != null && node != nFocused) {
				//view.changeWeight(nFocused, node, weight, grafo);
			}
			//view.deleteAux();

			nFocused = null;
			break;

		default:
			break;
		}
	}

	public void dijkstraM(View v, MotionEvent event, int x, int y, Node node) {

	}

	/* Checks if external storage is available for read and write */
	public boolean isExternalStorageWritable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			return true;
		}
		return false;
	}

	public void toggle(int toggle, MenuItem item) {
		switch (toggle) {
		case TOGGLE_ADD:
			if (toggle_add) {
				item.setIcon(R.drawable.ic_action_new);
				toggle_add = false;
			} else {
				item.setIcon(R.drawable.ic_action_new_toggle);
				toggle_add = true;
			}
			break;
		case TOGGLE_REMOVE:
			if (toggle_remove) {
				item.setIcon(R.drawable.ic_action_discard);
				toggle_remove = false;
			} else {
				item.setIcon(R.drawable.ic_action_discard_toggle);
				toggle_remove = true;
			}
			break;
		}
	}

	public void stop(int toggle, MenuItem item) {
		switch (toggle) {
		case TOGGLE_ADD:
			item.setIcon(R.drawable.ic_action_new);
			toggle_add = false;

			break;
		case TOGGLE_REMOVE:
			item.setIcon(R.drawable.ic_action_discard);
			toggle_remove = false;

			break;
		}

	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// See which child activity is calling us back.
		if (requestCode == REQUEST_PATH) {
			if (resultCode == RESULT_OK) {
				//clear();
				
				SharedPreferences.Editor editor = prefs.edit();
				
				editor.putString("currentfile", data.getStringExtra("GetPath"));
				editor.putString("mode", "graph");
				editor.commit();
				
				//isIso=false;
				//view.xmlToGraph(data.getStringExtra("GetPath"), "");
				//view.update();
				//view.invalidate();

			}
		}
		if (requestCode == ISOMORPHISM) {
			if (resultCode == RESULT_OK) {
				//clear();
				
				SharedPreferences.Editor editor = prefs.edit();
				
				editor.putString("currentfile", data.getStringExtra("GetPath"));
				editor.putString("mode", "isomorphism");
				editor.commit();
				
				//isIso = true;
				//view.xmlToIsomorphism(data.getStringExtra("GetPath"), "");
				//view.update();
				//view.invalidate();

			}
		}
	}

	public void getfile() {
		Intent intent1 = new Intent(this, FileChooser.class);
		startActivityForResult(intent1, REQUEST_PATH);
		SharedPreferences.Editor editor;
		editor = prefs.edit();
		editor.putString("load", "graph");
		editor.commit();

	}

	public void getisofile() {
		Intent intent1 = new Intent(this, FileChooser.class);
		startActivityForResult(intent1, ISOMORPHISM);
		SharedPreferences.Editor editor;
		editor = prefs.edit();
		editor.putString("load", "isomorphism");
		editor.commit();
	}

	private void copyAssets() {
		AssetManager assetManager = getAssets();
		String[] files = null;
		try {
			files = assetManager.list("");
		} catch (IOException e) {
		}
		for (String filename : files) {
			InputStream in = null;
			FileOutputStream out = null;
			try {
				in = assetManager.open(filename);
				File file = new File(storage);

				file.mkdirs();
				File outFile = new File(storage, filename);

				out = new FileOutputStream(outFile);
				copyFile(in, out);
				in.close();
				in = null;
				out.flush();
				out.close();
				out = null;
			} catch (IOException e) {
			}
		}
	}

    private void menuNodo (Node nodo, int grafo){
        LayoutInflater f = LayoutInflater.from(this);
        final View dv = f.inflate(R.layout.menu_settings, null);
        AlertDialog.Builder bu = new AlertDialog.Builder(this);

        bu.setTitle(R.string.action_settings);
			/*
			 * Scale graph
			 */
        SeekBar seekBar = (SeekBar) dv.findViewById(R.id.seekBar_zoom);
        seekBar.setProgress((size.getNew_percent() == 0) ? size
                .getOld_percent() : size.getNew_percent());
        final TextView seekBarValue = (TextView) dv
                .findViewById(R.id.settings_zoom);
        seekBarValue
                .setText(Integer.toString((size.getNew_percent() == 0) ? size
                        .getOld_percent() : size.getNew_percent()));
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                seekBarValue.setText(String.valueOf(progress));

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
               // size.setOld_height(view.getViewportHeight());
                //size.setOld_width(view.getViewportWidth());
                size.setOld_percent(Integer.parseInt(seekBarValue.getText()
                        .toString()));
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int nw = size.getOld_width()
                        * Integer.parseInt(seekBarValue.getText()
                        .toString()) / size.getOld_percent();
                int nh = size.getOld_height()
                        * Integer.parseInt(seekBarValue.getText()
                        .toString()) / size.getOld_percent();
                size.setNew_width(nw);
                size.setNew_height(nh);
                size.setNew_percent(Integer.parseInt(seekBarValue.getText()
                        .toString()));
            }
        });
			/*
			 * Scale vertex

			SeekBar seekBar_vertex = (SeekBar) dv
					.findViewById(R.id.seekBar_zoomvertex);
			seekBar_vertex
					.setProgress((size.getNew_percent_vertex() == 0) ? size
							.getOld_percent_vertex() : size
							.getNew_percent_vertex());
			final TextView seekBarValue_vertex = (TextView) dv
					.findViewById(R.id.setting_zoomvertex);
			seekBarValue_vertex.setText(Integer.toString((size
					.getNew_percent_vertex() == 0) ? size
					.getOld_percent_vertex() : size.getNew_percent_vertex()));
			seekBar_vertex
					.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
						@Override
						public void onProgressChanged(SeekBar seekBar,
								int progress, boolean fromUser) {
							seekBarValue_vertex.setText(String
									.valueOf(progress));
							size.setNew_percent_vertex(progress);
							view.changeRadius(size);
						}

						@Override
						public void onStartTrackingTouch(SeekBar seekBar) {
							//
						}

						@Override
						public void onStopTrackingTouch(SeekBar seekBar) {
							//
						}
					});
			bu.setPositiveButton(R.string.file_save,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							view.resizeGraph(size);
							view.update();
							size.setOld_percent(100);
							size.setNew_percent(100);
						}
					});
			bu.setNegativeButton(R.string.file_cancel,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}
					});
			bu.setView(dv);

			AlertDialog di = bu.create();
			di.getWindow().clearFlags(
					WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			di.show();
            */
    }

	private void copyFile(InputStream in, FileOutputStream out)
			throws IOException {
		byte[] buffer = new byte[1024];
		int read;
		while ((read = in.read(buffer)) != -1) {
			out.write(buffer, 0, read);
		}
	}

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private int lastXmoved = 0;
        private int lastYmoved = 0;
        private boolean isLong = false;

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            //View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            g_view =  new GView(getActivity());
            g_view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                                /* ... */
                    isLong = true;
                    if(g_view.isVisible(gFocused)) {
                        Node node = g_view.checkBounds(lastX, lastY);
                        int grafo = g_view.checkBoundsG(lastX, lastY);

                        Node nodo2 = g_view.checkBounds(lastXmoved, lastYmoved);
                        int grafo2 = g_view.checkBoundsG(lastXmoved, lastYmoved);
                        System.out.println(lastX + " " + lastXmoved);
                        if (grafo2 == grafo && nodo2 != null && node != null && node != nodo2)
                            g_view.addArrow(node, nodo2, grafo);


                /*    if(node!=null&&grafo>=0)
                    view.deleteNode(node,grafo);
                */
                        //menuNodo(node, grafo);
                    }
                    return true;
                }
            });

            g_view.setOnTouchListener(new OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    // TODO Auto-generated method stub
                    int x = Math.round(event.getX());
                    int y = Math.round(event.getY());
                    Node node = g_view.checkBounds(x, y);
                    int grafo = g_view.checkBoundsG(x,y);
                    switch (event.getActionMasked())
                    {
                        case MotionEvent.ACTION_DOWN:


                            if(g_view.isVisible(gFocused))

                                if(node==null)
                                    g_view.addNode(x, y, gFocused);


                            else
                            {   nFocused = node;
                                if(event.getEventTime()-lastPressTime<500)
                                    g_view.deleteNode(node,grafo);
                                lastX = x;
                                lastY = y;
                            }

                            break;
                        case MotionEvent.ACTION_POINTER_DOWN:
                            System.out.println(event.getActionIndex()==1);
                            System.out.println(g_view.checkBoundsG((int)event.getX(1),(int)event.getY(1))+" "+g_view.checkBoundsG((int)event.getX(0),(int)event.getY(0)));
                            System.out.println(g_view.checkBounds((int)event.getX(1),(int)event.getY(1))+" "+g_view.checkBounds((int)event.getX(0),(int)event.getY(0)));
                            if(event.getActionIndex()==1&&g_view.checkBoundsG((int)event.getX(1),(int)event.getY(1))==g_view.checkBoundsG((int)event.getX(0),(int)event.getY(0))&&g_view.checkBounds((int)event.getX(1),(int)event.getY(1))!=null&&g_view.checkBounds((int)event.getX(0),(int)event.getY(0))!=null&&g_view.checkBounds((int)event.getX(1),(int)event.getY(1))!=g_view.checkBounds((int)event.getX(0),(int)event.getY(0)))
                                g_view.addArrow(g_view.checkBounds((int)event.getX(1),(int)event.getY(1)),g_view.checkBounds((int)event.getX(0),(int)event.getY(0)),grafo);
                            System.out.println("shit");
                            break;
                        case MotionEvent.ACTION_MOVE:


                            if (node == null)

                            {//view.addNode(x,y,grafo);
                                lastXmoved = x;
                                lastYmoved = y;
                            } else {
                                if(nFocused!=null)
                                g_view.setPosition(x,y,nFocused);
                                //if(event.getEventTime()-event.getDownTime()>2000){
                                //    view.deleteNode(node,grafo);
                                //}


                            }


                            break;
                        case MotionEvent.ACTION_UP:;
                            if(node==null);
                                //view.addNode(x,y,grafo);
                            else
                            {
                                nFocused= null;

                            }
                            break;

                    }


                    //if (!isIso)
					/*switch (gMode) {
					case GRAPH_MODE_NODES:
						if (toggle_add)
							addNodeM(v, event, x, y, node, grafo);
						else if (toggle_remove)
							deleteNodeM(v, event, x, y, node, grafo);
						else
							modifyGraph(v, event, x, y, node);
						break;
					case GRAPH_MODE_ARROWS:
						if (toggle_add)
							addArrowM(v, event, x, y, node, grafo);
						else if (toggle_remove)
							deleteArrowM(v, event, x, y, node, grafo);
						else
							modifyGraph(v, event, x, y, node);
						break;
					case GRAPH_MODE_WEIGHTS:
						weightM(v, event, x, y, node, grafo);
						break;
					default:
						modifyGraph(v, event, x, y, node);

						break;
					}*/
				/*else
					modifyGraph(v, event, x, y, node);*/

                    //view.setMenuStateChecked(isKruskal, isBipartite);
                    lastPressTime = event.getDownTime();
                    g_view.update();
                    g_view.invalidate();

                    return true;

                }



                private void modifyGraph(View v, MotionEvent event, int x, int y,
                                         Node node, int grafo) {
                    switch (event.getActionMasked()) {
                        case MotionEvent.ACTION_DOWN:
                            if (node == null) {
                                nFocused = null;
                            } else {
                                nFocused = node;
                            }
                            break;
                        case MotionEvent.ACTION_MOVE:
                            if (nFocused != null) {
                                g_view.setPosition(x, y, nFocused);
                            }
                            break;

                        case MotionEvent.ACTION_UP:
                            nFocused = null;
                            break;

                        default:
                            break;

                    }

                }
            });
            return g_view;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }


    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                //mTitle = getString(R.string.title_section1);
                break;
            case 2:
                //mTitle = getString(R.string.title_section2);
                break;
            case 3:
                // mTitle = getString(R.string.title_section3);
                break;
        }
    }


}
