package com.jmga.graphs.tools.fileexplorer;

import java.util.List;

import com.jmga.graphs.R;
import com.jmga.graphs.classes.GView;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class FileArrayAdapter extends ArrayAdapter<Item> {

	private Context c;
	private int id;
	private List<Item> items;

	public FileArrayAdapter(Context context, int textViewResourceId,
			List<Item> objects) {
		super(context, textViewResourceId, objects);
		c = context;
		id = textViewResourceId;
		items = objects;
	}

	public Item getItem(int i) {
		return items.get(i);
	}
	public List<Item> getItems(){
		return items;
	}

	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) c
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(id, null);
		}

		/* create a new view of my layout and inflate it in the row */
		// convertView = ( RelativeLayout ) inflater.inflate( resource, null );

		final Item o = items.get(position);
		if (o != null) {

			Log.d("isgraph", "dentro de FileArrayAdapter");

			TextView t1 = (TextView) v.findViewById(R.id.TextView01);
			TextView t2 = (TextView) v.findViewById(R.id.TextView02);
			TextView t3 = (TextView) v.findViewById(R.id.TextViewDate);
			/* Take the ImageView from layout and set the city's image */
			ImageView imageCity = (ImageView) v.findViewById(R.id.fd_Icon1);
			GView view = (GView) v.findViewById(R.id.fd_Icon2);
			view.setViewportHeight(50);
			view.setViewportHeight(50);
			if (o.getDir()) {
				
				String uri = "drawable/ic_action_storage";
				int imageResource = c.getResources().getIdentifier(uri, null,
						c.getPackageName());
				Drawable image = c.getResources().getDrawable(imageResource);
				imageCity.setImageDrawable(image);
				view.setVisibility(View.GONE);

			} else {

				view.xmlToGraph(o.getPath(), "");
				imageCity.setVisibility(View.GONE);
				view.invalidate();

			}

			if (t1 != null)
				t1.setText(o.getName());
			if (t2 != null)
				t2.setText(o.getData());
			if (t3 != null)
				t3.setText(o.getDate());
		}
		return v;
	}
}
