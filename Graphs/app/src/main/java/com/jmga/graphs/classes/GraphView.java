package com.jmga.graphs.classes;

import android.graphics.Color;

/**
 * Created by JMGA on 01/02/2015.
 */
public class GraphView {
    boolean visible;
    int color;
    int id;
    String name;
    int icon;
    /**
     *
     *
     *
     *
     */

    public GraphView(int id) {
        visible = true;
        this.id = id;
        name = "Grafo "+id;
        color = Color.BLACK;

    }

    public GraphView(int id, int icon) {
        this.id = id;
        if(id>0){visible = true;
            name = "Grafo "+id;}
        else{
            name = "Añadir Grafo"; visible = false;}

        this.icon = icon;

    }

}
