package com.jmga.graphs.tools;

import java.util.ArrayList;
import java.util.Hashtable;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class FlowTable extends TableLayout {
	private TableRow mCurrentRow;
	private ArrayList<String> names;

	public FlowTable(Context context, AttributeSet attrs,
			ArrayList<String> names) {
		super(context, attrs);
		this.names = names;
		init();
	}

	public FlowTable(Context context, ArrayList<String> names) {
		super(context);
		this.names = names;
		init();
	}

	private void init() {
		mCurrentRow = new TableRow(getContext());
		mCurrentRow.addView(createAndFillTextView("\\")); // title for first row
		for (String name : names) {
			mCurrentRow.addView(createAndFillTextView(name));
		}
		setStretchAllColumns(true);
		setGravity(HORIZONTAL);
		setPadding(20, 20, 20, 20);
		finishRowAndStartNew();
	}

	public void addContent(String node, Hashtable<String, Integer> data) {
		mCurrentRow.addView(createAndFillTextView(node));

		for (String name : names) {

			mCurrentRow.addView(createAndFillTextView(String.valueOf(data
					.get(name))));
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