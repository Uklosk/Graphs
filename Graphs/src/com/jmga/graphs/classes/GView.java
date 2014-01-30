package com.jmga.graphs.classes;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.jmga.graphs.R;
import com.jmga.graphs.tools.Bipartite;
import com.jmga.graphs.tools.Dijkstra;
import com.jmga.graphs.tools.FlowTable;
import com.jmga.graphs.tools.Kruskal;
import com.jmga.graphs.tools.XMLParser;
import com.jmga.graphs.tools.auxiliary.SizeView;

public class GView extends View {
	private Graph g;
	private Graph gKruskal;
	private Paint paint, auxP;
	private Paint fontPaint;
	private Path path;

	public boolean save_graph = false;
	public boolean info_table = false;
	public boolean cleangraph = false;
	public boolean table_dist = false;

	public boolean isKruskal = false;
	public boolean isBipartite = false;
	private boolean checked_kruskal = false;
	private boolean checked_bipartite = false;
	private Hashtable<String, Integer> subSets;

	private int viewportHeight, viewportWidth;

	final private SizeView size = new SizeView();
	
	public void setMenuStateChecked(boolean ck, boolean cb) {
		checked_kruskal = ck;
		checked_bipartite = cb;
	}
	
	public int getViewportHeight() {
		return viewportHeight;
	}

	public int getViewportWidth() {
		return viewportWidth;
	}

	public void setViewportHeight(int Y) {
		viewportHeight = Y;
	}

	public void setViewportWidth(int X) {
		viewportWidth = X;
	}

	private float density;

	public float getDensity() {
		return density;
	}

	Arrow aux;

	public GView(Context context) {
		super(context);
		init();
	}

	public GView(Context context, float density_) {
		super(context);

		init();
	}

	public GView(Context context, AttributeSet attrs) {
		super(context, attrs);

		init();
	}

	public GView(Context context, AttributeSet attrs, int params) {
		super(context, attrs, params);

		init();
	}

	private void init() {

		density = getResources().getDisplayMetrics().density;

		g = new Graph();
		gKruskal = new Graph();
		subSets = new Hashtable<String, Integer>();

		paint = new Paint();
		paint.setStrokeWidth(4f);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeJoin(Paint.Join.ROUND);
		paint.setAntiAlias(true);

		auxP = new Paint();
		auxP.setStrokeWidth(10f);
		auxP.setStyle(Paint.Style.STROKE);
		auxP.setStrokeJoin(Paint.Join.ROUND);
		auxP.setColor(Color.BLACK);
		auxP.setAntiAlias(true);

		fontPaint = new Paint();
		fontPaint.setTextAlign(Align.CENTER);
		fontPaint.setTextSize(20);

	}

	public boolean graphToXML(String storage, String file_name) {
		boolean task = false;
		g.update();
		XMLParser p = new XMLParser(storage, this);
		try {
			p.saveGraph(g, "/" + file_name + ".graph");
			task = true;
		} catch (Exception e) {
			task = false;
			e.printStackTrace();
		}
		return task;
	}

	public boolean isXMLGraph(String complete_path) {
		return XMLParser.isGraph(complete_path);
	}

	public boolean xmlToGraph(String storage, String xml) {
		boolean task = true;
		if (viewportHeight == 0 || viewportWidth == 0) {
			viewportHeight = (int) (50 * density + 0.5f);
			viewportWidth = (int) (50 * density + 0.5f);
			paint.setStrokeWidth(0);
			fontPaint.setTextSize(0);

		}
		XMLParser xmlp = new XMLParser(storage, xml, this);
		try {
			g = xmlp.parseGraph(g);
		} catch (Exception e) {
			e.printStackTrace();
			task = false;
		}
		return task;
	}

	@SuppressLint("DrawAllocation")
	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawColor(Color.WHITE);

		for (int i = 0; i < g.getArrows().size(); i++) {
			Arrow a = g.getArrows().get(i);
			paint.setColor(g.getArrows().get(i).color);
			canvas.drawLine(a.start[0], a.start[1], a.stop[0], a.stop[1], paint);
			if (a.getWeight() > 0) {
				path = new Path();
				path.moveTo(a.start[0], a.start[1]);
				path.lineTo(a.stop[0], a.stop[1]);
				canvas.drawTextOnPath(a.getWeightS(), path, 0, 30, fontPaint);

				path = new Path();
				path.moveTo(a.stop[0], a.stop[1]);
				path.lineTo(a.start[0], a.start[1]);
				canvas.drawTextOnPath(a.getWeightS(), path, 0, 30, fontPaint);

			}

		}

		if (aux != null) {
			canvas.drawLine(aux.start[0], aux.start[1], aux.stop[0],
					aux.stop[1], auxP);
		}

		for (String ns : g.getNombres()) {
			Node n = g.getVertex().get(ns);
			n.draw(canvas);
			canvas.drawText(n.getId(), n.getCenterX(), n.getCenterY()
					- n.radius - 20, fontPaint);
		}

	}

	public Node checkBounds(int x, int y) {
		for (int i = 0; i < g.getVertex().size(); i++) {
			Node n = g.getVertex().get(g.getId(i));
			if (n != null && !n.getId().equals("nulo")) {
				if (n.getBounds().left < x && n.getBounds().right > x
						&& n.getBounds().top < y && n.getBounds().bottom > y) {
					return n;
				}
			}
		}
		return null;
	}

	public void Kruskal() {
		restore();
		if (g.getArrows().size() >= g.getNombres().size() - 1) {
			for (int i = 0; i < g.getArrows().size(); i++) {
				for (int j = 0; j < gKruskal.getArrows().size(); j++) {
					Arrow a = g.getArrows().get(i);
					Arrow k = gKruskal.getArrows().get(j);
					if (a.getIdi().equals(k.getIdi())
							&& a.getIdf().equals(k.getIdf())) {
						a.color = Color.BLUE;
					} else if (a.getIdi().equals(k.getIdf())
							&& a.getIdf().equals(k.getIdi())) {
						a.color = Color.BLUE;
					}
				}
			}
		}
	}

	public boolean bipartite(boolean print) {
		boolean printBipatite = false;
		Bipartite b = new Bipartite(g);
		try {
			printBipatite = b.execute(true);
		} catch (Exception e) {
			printBipatite = false;
			e.printStackTrace();
		}
		if (printBipatite && print) {
			subSets = b.getSubSet();
			Enumeration<String> keys = subSets.keys();
			while (keys.hasMoreElements()) {
				String key = (String) keys.nextElement();
				g.setColorOfNode(key, (subSets.get(key) == 1) ? Color.YELLOW
						: Color.GREEN);
			}
		} else {
			subSets = new Hashtable<String, Integer>();
			initializingNodesColor();
		}

		return printBipatite;
	}

	public int connectedComponents() {
		int cc = 0;
		Bipartite b = new Bipartite(g);
		try {
			b.execute(false);
			cc = b.getConnectedComponents();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cc;
	}

	public Hashtable<String, String> getTableInfo() {
		Hashtable<String, String> info = new Hashtable<String, String>();

		info.put("|V|", Integer.toString(g.getNombres().size()));
		info.put("|A|", Integer.toString(g.getArrows().size()));
		info.put("Bipartite", (bipartite(false) ? "Si" : "No"));
		info.put("Components", Integer.toString(connectedComponents()));
		info.put("Sum", Integer.toString(g.getTotalWeight()));
		int degree = g.isRegular();
		info.put("Regular", (degree > 0) ? "Si, regular de grado " + degree
				: "No");
		info.put("Sequence", "{" + arrayParseString(g.getSequenceDegrees())
				+ "}");

		return info;
	}

	private String arrayParseString(String[] array) {
		StringBuilder builder = new StringBuilder();
		for (String s : array) {
			builder.append(s + ",");
		}
		return builder.toString().substring(0,
				builder.toString().lastIndexOf(","));
	}

	public void initializingNodesColor() {
		g.colorRestorationNodes();
	}

	public Graph aplicarKruskal(Graph g) {
		return Kruskal.aplicarKruskal(g);
	}

	public void addNode(int x, int y) {
		g.addNode(x, y, viewportWidth, viewportHeight, density);
	}

	public void addNode(Node n) {
		g.addNode(n.getCenterX(), n.getCenterY(), viewportWidth,
				viewportHeight, density);
	}

	public void deleteNode(Node n) {
		if (n != null) {
			g.deleteNode(n.getId());
		}
	}

	public void addArrow(Node n1, Node n2) {
		if (n1 != null && n2 != null) {
			g.addLink(n1.getId(), n2.getId(), 1);
		}
	}

	public void deleteArrow(Node n1, Node n2) {
		g.deleteLink(n1.getId(), n2.getId());
	}

	public void changeWeight(Node n1, Node n2, int weight) {
		g.changeWeight(n1.getId(), n2.getId(), weight);
	}

	public void addAux(Node n, int x, int y) {
		aux = new Arrow(n.getCenterX(), n.getCenterY(), x, y);
	}

	public void updateAux(int x, int y) {
		aux.stop[0] = x;
		aux.stop[1] = y;
	}

	public void deleteAux() {
		aux = null;
	}

	public void setPosition(int x, int y, Node n) {
		n.setPos(x, y, viewportWidth, viewportHeight);
	}

	public void update() {
		g.update();

		if (g.getNombres().size() > 0)
			save_graph = info_table = table_dist = cleangraph = true;
		else
			save_graph = info_table = table_dist = cleangraph = false;

		if (g.getArrows().size() >= g.getNombres().size() - 1
				&& g.getNombres().size() > 0 && g.getArrows().size() > 0) {

			isKruskal = true;
			if (checked_kruskal) {
				gKruskal = aplicarKruskal(g);
				Kruskal();
			}

		} else
			isKruskal = false;

		if (g.getNombres().size() > 0 && g.getArrows().size() > 0) {
			isBipartite = true;
			if (checked_bipartite)
				bipartite(true);
		} else {
			isBipartite = false;
			initializingNodesColor();
		}

		invalidate();
	}

	public void clear() {
		g = new Graph();
		gKruskal = new Graph();
		isKruskal = isBipartite = cleangraph = table_dist = save_graph = info_table = false;
		invalidate();
	}

	public void restore() {
		for (int i = 0; i < g.getArrows().size(); i++) {
			g.getArrows().get(i).color = Color.BLACK;
		}
	}

	public FlowTable dijkstra(Context context) {
		Dijkstra d = new Dijkstra(g);
		d.dijkstra(g);
		return d.getTableDistance(context);
	}

	@Override
	public void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		viewportWidth = w;
		viewportHeight = h;
	}
	
	public void resizeGraph(SizeView s){
		Enumeration<Node> nodes = g.getVertex().elements();
		while(nodes.hasMoreElements()){
			Node node = new Node();
			node = nodes.nextElement();
			float x = node.getPosX(), xo = 0;
			float y = node.getPosY(), yo = 0;
			xo = x * (float)s.getNew_width() / (float)s.getOld_width();
			yo = y * (float)s.getNew_height() / (float)s.getOld_height();
			node.setPosF(xo, yo, viewportWidth, viewportHeight);
		}
		boolean load = false;
        // [0]:x  [1]:y
		float[] displacement = new float[2];
        float[] max = new float[2];
        float[] min = new float[2];
        max[0] = max[1] = min[0] = min[1] = 0; 
        Iterator<String> keys = g.getNombres().iterator();
        while(keys.hasNext()){
        	String key = keys.next();
			if(!load){
		        max[0] = min[0] = g.getVertex().get(key).getPosX();
		        max[1] = min[1] = g.getVertex().get(key).getPosY(); 
		        load = true;
			}else{
				if(g.getVertex().get(key).getPosX() > max[0])
					max[0] = g.getVertex().get(key).getPosX();
				if(g.getVertex().get(key).getPosX() < min[0])
					min[0] = g.getVertex().get(key).getPosX();
				if(g.getVertex().get(key).getPosY() > max[1])
					max[1] = g.getVertex().get(key).getPosY();
				if(g.getVertex().get(key).getPosY() < min[1])
					min[1] = g.getVertex().get(key).getPosY();
			}
        }
        // [0]:width  [1]:height
        float[] tam = new float[2];
        for(int i=0; i<2; i++){
        	tam[i] = max[i] + min[i];
        	displacement[i] = (1 - tam[i])/2;
        }
		nodes = g.getVertex().elements();
		while(nodes.hasMoreElements()){
			Node node = new Node();
			node = nodes.nextElement();
			float x = node.getPosX() + displacement[0];
			float y = node.getPosY() + displacement[1];
			node.setPosF(x, y, viewportWidth, viewportHeight);
		}
	}
}
