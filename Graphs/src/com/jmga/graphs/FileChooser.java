package com.jmga.graphs;

import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.jmga.graphs.tools.XMLParser;
import com.jmga.graphs.tools.fileexplorer.FileArrayAdapter;
import com.jmga.graphs.tools.fileexplorer.Item;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

public class FileChooser extends ListActivity {

	private File currentDir;
	private FileArrayAdapter adapter;
	SharedPreferences prefs;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		currentDir = new File("/sdcard/Graphs");
		fill(currentDir);
		
	}

	private void fill(File f) {
		File[] dirs = f.listFiles();
		this.setTitle("Current Dir: " + f.getName());
		List<Item> dir = new ArrayList<Item>();
		List<Item> fls = new ArrayList<Item>();
		try {
			for (File ff : dirs) {
				Date lastModDate = new Date(ff.lastModified());
				DateFormat formater = DateFormat.getDateTimeInstance();
				String date_modify = formater.format(lastModDate);
				if (ff.isDirectory()) {

					File[] fbuf = ff.listFiles();
					int buf = 0;
					if (fbuf != null) {
						buf = fbuf.length;
					} else
						buf = 0;
					String num_item = String.valueOf(buf);
					if (buf == 0)
						num_item = num_item + " item";
					else
						num_item = num_item + " items";

					// String formated = lastModDate.toString();
					dir.add(new Item(ff.getName(), num_item, date_modify, ff
							.getAbsolutePath(), true));
				} else {
					Item it = new Item(ff.getName(), ff.length() + " Byte",
							date_modify, ff.getAbsolutePath(), false);
					it.setIsGraph(XMLParser.isGraph(it.getPath()));
					prefs = getSharedPreferences("Preferences_Graph", Context.MODE_PRIVATE);
					switch (it.getIsGraph()) {
					case 1:
						if (prefs.getString("load", "").equals("graph"))
						fls.add(it);
						break;
					case 2:
						if (prefs.getString("load", "").equals("isomorphism"))
						fls.add(it);

					}
				}
			}
		} catch (Exception e) {

		}
		Collections.sort(dir);
		Collections.sort(fls);
		dir.addAll(fls);
		if (!f.getName().equalsIgnoreCase("sdcard"))
			dir.add(0, new Item("..", "Parent Directory", "", f.getParent(),
					true));
		adapter = new FileArrayAdapter(FileChooser.this, R.layout.file_view,
				dir);
		this.setListAdapter(adapter);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		Item o = adapter.getItem(position);
		if (o.getDir()) {
			currentDir = new File(o.getPath());
			fill(currentDir);
		} else {
			onFileClick(o);
		}
	}

	private void onFileClick(Item o) {
		// Toast.makeText(this, "Folder Clicked: "+ currentDir,
		// Toast.LENGTH_SHORT).show();
		Intent intent = new Intent();
		// intent.putExtra("GetPath", currentDir.toString());
		intent.putExtra("GetPath", o.getPath());
		intent.putExtra("GetFileName", o.getName());
		setResult(RESULT_OK, intent);
		finish();
	}
}