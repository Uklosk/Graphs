package com.jmga.graphs;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.jmga.graphs.classes.GView;
import com.jmga.graphs.classes.Node;

public class MainActivity extends Activity implements ActionBar.TabListener {

	GView view;
	Node nFocused;
	private Menu menu;

	private static final int GRAPH_MODE_NODES = 0;
	private static final int GRAPH_MODE_ARROWS = 1;
	private static final int GRAPH_MODE_WEIGHTS = 2;
	private int gMode = GRAPH_MODE_NODES;

	private static final int TOGGLE_ADD = 0;
	private static final int TOGGLE_REMOVE = 1;
	private static final int TOGGLE_EDIT = 2;

	boolean toggle_remove = false;
	boolean toggle_add = false;
	boolean toggle_edit = false;

	private DisplayMetrics metrics;

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
					if (toggle_edit)
						addArrowM(v, event, x, y, node);
					else
						modifyGraph(v, event, x, y, node);
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
		// getMenuInflater().inflate(R.menu.main, menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_1, menu);
		this.menu = menu;
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.action_load:
			view.Kruskal();
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

	public void kruskalM(View v, MotionEvent event, int x, int y, Node node) {

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
				view.changeWeight(nFocused, node, 5);
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
		case TOGGLE_EDIT:
			if (toggle_edit) {
				item.setIcon(R.drawable.ic_action_discard);
				toggle_edit = false;
			} else {
				item.setIcon(R.drawable.ic_action_discard_toggle);
				toggle_edit = true;
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
		case TOGGLE_EDIT:
			item.setIcon(R.drawable.ic_action_discard);
			toggle_edit = false;

			break;
		}

	}

}
