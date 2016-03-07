package com.ds.panel;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JPanel;

import com.ds.model.DrawModel;
import com.ds.shape.Shape;

public class DrawPanel extends JPanel implements Observer{
	public DrawPanel(){
		setLayout(null);
	}
	
	private DrawModel model = null;
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		//抗锯齿
		((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		if(model != null){
			synchronized (Shape.class) {
				for(Shape shape : model.getShapeList())
					shape.drawShape(g);
			}
		}
	}

	@Override
	public void update(Observable o, Object arg) {
		model = (DrawModel)o;//获得视图的模型
		repaint();//更新视图
	}
}
