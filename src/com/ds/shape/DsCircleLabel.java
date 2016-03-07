package com.ds.shape;

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JLabel;

//»­Ô²
public class DsCircleLabel extends JLabel{
	public int lx, ly, lw, lh;
	private void drawShape(Graphics g) {
		g.setColor(Color.BLACK);
		g.fillOval(lx, ly, lw, lh);
		g.setColor(Color.RED);
		g.fillOval(lx+2, ly+2, lw-6, lh-6);
	}
	
	@Override
	protected void printComponent(Graphics g) {
		super.printComponent(g);
		drawShape(g);
	}

	public DsCircleLabel(int lx, int ly, int lw, int lh, String content) {
		super(content);
		this.lx = lx;
		this.ly = ly;
		this.lw = lw;
		this.lh = lh;
	}
	
}
