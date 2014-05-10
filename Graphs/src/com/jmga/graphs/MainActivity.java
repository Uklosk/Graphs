package com.jmga.graphs;

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

public class MainActivity extends Activity implements ActionBar.TabListener {

	private final static String storage = Environment
			.getExternalStorageDirectory().toString() + "/Graphs";

	private GView view;
	private Node nFocused;
	private Menu menu;

	private final SizeView size = new SizeView();

	private static final int GRAPH_MODE_NODES = 0;
	private static final int GRAPH_MODE_ARROWS = 1;
	private static final int GRAPH_MODE_WEIGHTS = 2;
	private int gMode = GRAPH_MODE_NODES;

	private static final int TOGGLE_ADD = 0;
	private static final int TOGGLE_REMOVE = 1;

	boolean isKruskal = false;
	boolean isBipartite = false;
	boolean isIso = false;
	boolean toggle_remove = false;
	boolean toggle_add = false;

	public int weight = 0;
	private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";

	private static final int REQUEST_PATH = 0,ISOMORPHISM = 1;

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

		view = new GView(this);

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

				view.setMenuStateChecked(isKruskal, isBipartite);
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

		updatingMenu();

		this.menu = menu;
		return super.onPrepareOptionsMenu(menu);

	}

	public void updatingMenu() {
		menu.findItem(R.id.action_save).setEnabled(view.save_graph);
		menu.findItem(R.id.action_tableinfo).setEnabled(view.info_table);
		menu.findItem(R.id.action_distance_table).setEnabled(view.table_dist);
		menu.findItem(R.id.action_clear).setEnabled(view.cleangraph);

		stop(TOGGLE_ADD, (MenuItem) menu.findItem(R.id.action_add));
		stop(TOGGLE_REMOVE, (MenuItem) menu.findItem(R.id.action_remove));
		menu.findItem(R.id.action_kruskal).setEnabled(view.isKruskal);
		menu.findItem(R.id.action_kruskal).setChecked(isKruskal);
		menu.findItem(R.id.action_bipartit).setEnabled(view.isBipartite);
		menu.findItem(R.id.action_bipartit).setChecked(
				(view.isBipartite) ? isBipartite : false);
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
										"Guardado con éxito como " + file_name
												+ ".graph", Toast.LENGTH_LONG)
										.show();
								dialog.dismiss();
								view.update();
							} else
								Toast.makeText(
										getApplicationContext(),
										"No ha podido guardarse, inténtelo de nuevo.",
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
			isIso = true;
			
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
					size.setOld_height(view.getViewportHeight());
					size.setOld_width(view.getViewportWidth());
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
			 */
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

	public void clear() {
		isKruskal = isBipartite = isIso = false;
		updatingMenu();
		view.initializingNodesColor();
		view.clear();
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
		view.graphToRestore = 2;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// Serialize the current tab position.
		outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, getActionBar()
				.getSelectedNavigationIndex());
		view.graphToXML(getFilesDir().toString(), "/temp_save----");
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
				view.addNode(x, y);
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

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// See which child activity is calling us back.
		if (requestCode == REQUEST_PATH) {
			if (resultCode == RESULT_OK) {
				clear();

				view.xmlToGraph(data.getStringExtra("GetPath"), "");
				view.update();
				view.invalidate();

			}
		}
		if (requestCode == ISOMORPHISM) {
			if (resultCode == RESULT_OK) {
				clear();

				view.xmlToIsomorphism(data.getStringExtra("GetPath"), "");
				view.update();
				view.invalidate();

			}
		}
	}

	public void getfile() {
		Intent intent1 = new Intent(this, FileChooser.class);
		startActivityForResult(intent1, REQUEST_PATH);
	}
	
	public void getisofile() {
		Intent intent1 = new Intent(this, FileChooser.class);
		startActivityForResult(intent1, ISOMORPHISM);
	}

}
