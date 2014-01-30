package com.jmga.graphs.tools.auxiliary;

public class SizeView {

	private int width;
	private int height;
	private int percent;
	private int old_width;
	private int old_height;
	private int old_percent;
	private int new_width;
	private int new_height;
	private int new_percent;
	
	public SizeView() {
		super();
		percent = old_percent = 100;
		height = old_height = 1;
		width = old_width = 1;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getPercent() {
		return percent;
	}

	public void setPercent(int percent) {
		this.percent = percent;
	}
	
	public int getOld_width() {
		return old_width;
	}

	public void setOld_width(int old_width) {
		this.old_width = old_width;
	}

	public int getOld_height() {
		return old_height;
	}

	public void setOld_height(int old_height) {
		this.old_height = old_height;
	}

	public int getNew_width() {
		return new_width;
	}

	public void setNew_width(int new_width) {
		this.new_width = new_width;
	}

	public int getNew_height() {
		return new_height;
	}

	public void setNew_height(int new_height) {
		this.new_height = new_height;
	}

	public int getOld_percent() {
		return old_percent;
	}

	public void setOld_percent(int old_percent) {
		this.old_percent = old_percent;
	}

	public int getNew_percent() {
		return new_percent;
	}

	public void setNew_percent(int new_percent) {
		this.new_percent = new_percent;
	}
	
}
