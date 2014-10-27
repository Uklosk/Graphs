package com.jmga.graphs.tools;

import java.util.ArrayList;
import java.util.Hashtable;

import com.jmga.graphs.R;
import com.jmga.graphs.classes.GView;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class FlowTable extends TableLayout {
	private TableRow mCurrentRow;
	private ArrayList<Integer> names;

	public FlowTable(Context context, AttributeSet attrs,
			ArrayList<Integer> names) {
		super(context, attrs);
		this.names = names;
		init();
	}

	public FlowTable(Context context, ArrayList<Integer> names) {
		super(context);
		this.names = names;
		init();
	}

	private void init() {
		mCurrentRow = new TableRow(getContext());
		mCurrentRow.addView(createAndFillTextView("\\")); // title for first row
		for (int name : names) {
			mCurrentRow.addView(createAndFillTextView(GView.getLabel(name)));
			mCurrentRow.setBackgroundResource(R.drawable.tablerow_border);
		}
		setStretchAllColumns(true);
		setGravity(HORIZONTAL);
		setPadding(20, 20, 20, 20);
		finishRowAndStartNew();
	}

	public void addContent(int node, Hashtable<Integer, Integer> data) {
		mCurrentRow.addView(createAndFillTextView(GView.getLabel(node)));

		for (int name : names) {
			
			mCurrentRow.addView(createAndFillTextView(String.valueOf(data.get(name))));
			mCurrentRow.setBackgroundResource(R.drawable.tablerow_border);
			mCurrentRow.setPadding(0, 2, 0, 0);
		}
		finishRowAndStartNew();

	}

	private void finishRowAndStartNew() {
		addView(mCurrentRow);
		mCurrentRow = new TableRow(getContext());
	}

	private TextView createAndFillTextView(String text) {
		TextView tv = new TextView(getContext());
		tv.setText(text);
		tv.setTextColor(Color.BLACK);
		return tv;
	}
}