package com.jmga.graphs.classes;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Path;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import com.jmga.graphs.tools.XMLParser;

public class GView extends View {
	private Graph g;
	private Graph gKruskal;
	private Paint paint, auxP;
	private Paint fontPaint;
	private Path path;
	
	public boolean isKruskal = false;
	
	private int height, width;
	private float density;

	Arrow aux;

	public GView(Context context) {
		super(context);
	}

	public GView(Context context, float density_) {
		super(context);
		
		density = density_;
		
		g = new Graph();

		paint = new Paint();
		paint.setStrokeWidth(6f);
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

		// TODO Auto-generated constructor stub

	}
	
	public boolean xmlToGraph(String storage, String xml){
		boolean task = true;
		XMLParser xmlp = new XMLParser(storage, xml);
		xmlp.setDisplacement(width, height, density);
		try {
			g = xmlp.parseGraph(g); // El grafo instaciado previamente, ahora actualiza sus datos
		} catch (Exception e) {
			// El unico error que puede suceder es que no exista el archivo xml
			Log.d("XMLParser", "Error: " + e.getMessage());
			task = false;
		}
		return task;
	}

	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawColor(Color.WHITE);
		
		height = this.getHeight();
		width = this.getWidth();

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

		for (int i = 0; i < g.getVertex().size(); i++) {
			Node n = g.getVertex().get(g.getId(i));
			if (n != null && !n.getId().equals("nulo")) {
				n.draw(canvas);
				canvas.drawText(n.getId(), n.getCenterX(), n.getCenterY()
						- n.radius - 20, fontPaint);
			}
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
		if (g.getArrows().size() >= g.getVertex().size() - 1) {
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

	public Graph aplicarKruskal(Graph g) {
		return Kruskal.aplicarKruskal(g);
	}

	public void addNode(int x, int y) {
		g.addNode(x, y);
	}

	public void addNode(Node n) {
		g.addNode(n.getCenterX(), n.getCenterY());
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
		// TODO Auto-generated method stub
		n.setPos(x, y);
	}

	public void update() {
		g.update();
		if (g.getArrows().size() >= g.getVertex().size() - 1
				&& g.getVertex().size() > 0 && g.getArrows().size() > 0) {
			gKruskal = aplicarKruskal(g);
			isKruskal = true;
		} else
			isKruskal = false;
	}

	public void clear() {
		g = new Graph();
		gKruskal = new Graph();
		invalidate();
	}

	public void restore() {
		for (int i = 0; i < g.getArrows().size(); i++) {
			g.getArrows().get(i).color = Color.BLACK;
		}
	}
	public void dijkstra(){
		Dijkstra d = new Dijkstra(g);
		d.execute("B");
	}
}
