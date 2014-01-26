package com.jmga.graphs;

import java.util.Hashtable;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
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
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.jmga.graphs.classes.GView;
import com.jmga.graphs.classes.Graph;
import com.jmga.graphs.classes.Node;

public class MainActivity extends Activity implements ActionBar.TabListener {
	
	private final static String storage = Environment.getExternalStorageDirectory().toString() 
			+ "/Graphs";

	GView view;
	Node nFocused;
	private Menu menu;

	private static final int GRAPH_MODE_NODES = 0;
	private static final int GRAPH_MODE_ARROWS = 1;
	private static final int GRAPH_MODE_WEIGHTS = 2;
	private int gMode = GRAPH_MODE_NODES;

	private static final int TOGGLE_ADD = 0;
	private static final int TOGGLE_REMOVE = 1;

	boolean isKruskal = false;
	boolean toggle_remove = false;
	boolean toggle_add = false;

	private DisplayMetrics metrics;

	public int weight = 0;
	private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayShowHomeEnabled(false);

		actionBar.addTab(actionBar.newTab().setText("vértices")
				.setTabListener(this));
		actionBar.addTab(actionBar.newTab().setText("aristas")
				.setTabListener(this));
		actionBar.addTab(actionBar.newTab().setText("pesos")
				.setTabListener(this));

		metrics = getResources().getDisplayMetrics();

		view = new GView(this, metrics.xdpi);

		setContentView(view);

		view.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				int x = Math.round(event.getX());
				int y = Math.round(event.getY());
				Node node = view.checkBounds(x, y);

				switch (gMode) {
				case GRAPH_MODE_NODES:
					if (toggle_add)
						addNodeM(v, event, x, y, node);
					else if (toggle_remove)
						deleteNodeM(v, event, x, y, node);
					else
						modifyGraph(v, event, x, y, node);
					break;
				case GRAPH_MODE_ARROWS:
					if (toggle_add)
						addArrowM(v, event, x, y, node);
					else if (toggle_remove)
						deleteArrowM(v, event, x, y, node);
					else
						modifyGraph(v, event, x, y, node);
					break;
				case GRAPH_MODE_WEIGHTS:
					weightM(v, event, x, y, node);
					break;
				default:
					modifyGraph(v, event, x, y, node);

					break;
				}

				view.update();
				view.invalidate();

				return true;

			}

			private void modifyGraph(View v, MotionEvent event, int x, int y,
					Node node) {
				switch (event.getActionMasked()) {
				case MotionEvent.ACTION_DOWN:
					if (node == null) {
					} else {
						nFocused = node;
					}
					break;
				case MotionEvent.ACTION_MOVE:
					if (nFocused != null) {
						view.setPosition(x, y, nFocused);
					}
					break;

				case MotionEvent.ACTION_UP:
					nFocused = null;
					break;

				default:
					break;

				}
				// TODO Auto-generated method stub

			}
		});
		
		
		
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
		stop(TOGGLE_ADD, (MenuItem) menu.findItem(R.id.action_add));
		stop(TOGGLE_REMOVE, (MenuItem) menu.findItem(R.id.action_remove));
		menu.findItem(R.id.action_kruskal).setEnabled(view.isKruskal);
		menu.findItem(R.id.action_kruskal).setChecked(isKruskal);
		
		this.menu = menu;
		return super.onPrepareOptionsMenu(menu);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.action_load:
			if(!isExternalStorageWritable()){
				Toast toast = Toast.makeText(getBaseContext(), getString(R.string.memory_not_available),Toast.LENGTH_LONG);
				toast.show();
				

				return true;
			}
			// *****************************************************************************
			// Esta sentencia sirve para cargar al grafo con los datos almacenados en el xml
			// El grafo puede estar vacio o inicializado con aristas y vertices
			// La sentencia tiene que estar en un Listener
			// *****************************************************************************
			if(view.xmlToGraph(storage, "graph.xml"))
				Toast.makeText(getBaseContext(), "El grafo se ha cargado, toca la pantalla para visualizarlo.",Toast.LENGTH_LONG).show();
			view.invalidate();
			// *****************************************************************************
			return true;
		case R.id.action_save:
			if(!isExternalStorageWritable()){
				Toast toast = Toast.makeText(getBaseContext(), getString(R.string.memory_not_available),Toast.LENGTH_LONG);
				toast.show();
				return true;
			}
			
			return true;
		
		case R.id.action_kruskal:
			if (isKruskal) {
				isKruskal = false;
				view.restore();

			} else {
				isKruskal = true;
				view.Kruskal();
				view.dijkstra();
			}
			view.invalidate();
			return true;
			
		case R.id.action_clear:
			view.clear();
			return true;
			
		case R.id.action_settings:
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
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// Serialize the current tab position.
		outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, getActionBar()
				.getSelectedNavigationIndex());
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

	public void addNodeM(View v, MotionEvent event, int x, int y, Node node) {
		switch (event.getActionMasked()) {
		case MotionEvent.ACTION_DOWN:
			if (node == null) {
				Node v1 = new Node(x, y);
				view.addNode(v1);
			} else {
				nFocused = node;
			}
			break;
		case MotionEvent.ACTION_MOVE:
			if (nFocused != null) {
				view.setPosition(x, y, nFocused);
			}
			break;

		case MotionEvent.ACTION_UP:
			nFocused = null;
			break;

		default:
			break;

		}
	}

	public void deleteNodeM(View v, MotionEvent event, int x, int y, Node node) {
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
				view.deleteNode(nFocused);
			}
			break;

		default:
			break;

		}
	}

	public void addArrowM(View v, MotionEvent event, int x, int y, Node node) {
		switch (event.getActionMasked()) {
		case MotionEvent.ACTION_DOWN:
			if (node != null) {
				nFocused = node;
				view.addAux(nFocused, x, y);
			}
			break;
		case MotionEvent.ACTION_MOVE:
			if (nFocused != null) {
				view.updateAux(x, y);
			}
			break;

		case MotionEvent.ACTION_UP:
			if (node == nFocused) {

			} else if (node != null && node != nFocused) {
				view.addArrow(nFocused, node);
			}
			view.deleteAux();

			nFocused = null;
			break;

		default:
			break;

		}

	}

	public void deleteArrowM(View v, MotionEvent event, int x, int y, Node node) {

		switch (event.getActionMasked()) {
		case MotionEvent.ACTION_DOWN:
			if (node != null) {
				nFocused = node;
				view.addAux(nFocused, x, y);
			}
			break;
		case MotionEvent.ACTION_MOVE:
			if (nFocused != null) {
				view.updateAux(x, y);
			}
			break;

		case MotionEvent.ACTION_UP:
			if (node == nFocused) {

			} else if (node != null && node != nFocused) {
				view.deleteArrow(nFocused, node);
			}
			view.deleteAux();

			nFocused = null;
			break;

		default:
			break;
		}
	}

	public void weightM(View v, MotionEvent event, int x, int y, Node node) {

		switch (event.getActionMasked()) {
		case MotionEvent.ACTION_DOWN:
			if (node != null) {
				nFocused = node;
				view.addAux(nFocused, x, y);
			}
			break;
		case MotionEvent.ACTION_MOVE:
			if (nFocused != null) {
				view.updateAux(x, y);
			}
			break;

		case MotionEvent.ACTION_UP:
			if (node == nFocused) {

			} else if (node != null && node != nFocused) {
				view.changeWeight(nFocused, node, weight);
			}
			view.deleteAux();

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

	
}
