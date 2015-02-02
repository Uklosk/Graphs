package com.jmga.graphs.classes;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.jmga.graphs.R;

/**
 * Created by JMGA on 01/02/2015.
 */
public class GraphAdapter extends ArrayAdapter<GraphView> {
    Context context;
    int resource;
    GraphView [] objects = null;

    public GraphAdapter(Context context, int resource, GraphView[] objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.objects = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            GraphHolder holder = null;

            if ( row == null)
            {
                LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                row = inflater.inflate(resource, parent, false);

                holder = new GraphHolder();
                holder.name = (TextView)row.findViewById(R.id.name);
                holder.visible = (CheckBox)row.findViewById(R.id.visible);
                holder.add_delete = (ImageButton)row.findViewById(R.id.add_delete);

                row.setTag(holder);
            }
        else
            {
                holder = (GraphHolder)row.getTag();
            }

        GraphView graph_view = objects[position];
        holder.name.setText(graph_view.name);
       // holder.visible.setVisibility(View.VISIBLE);
       // holder.add_delete.setVisibility(View.VISIBLE);
        holder.add_delete.setImageResource(graph_view.icon);
        if(!graph_view.visible){ holder.visible.setVisibility(View.INVISIBLE);}

        return row;
    }

    static class GraphHolder
    {
        TextView name;
        CheckBox visible;
        ImageButton add_delete;
    }
}
