package com.ds.model;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;

import com.ds.shape.DsRect;
import com.ds.shape.Shape;
import com.ds.size.ShapeSize;

public class SampleInsertSortModel {
	private DrawModel model;
	private ArrayList<Shape> shapeList;
	
	private class StringComparator implements Comparator<String>{
		@Override
		public int compare(String s1, String s2) {
			return s1.length() == s2.length() ? s1.compareTo(s2) : s1.length()-s2.length();
		}
	}
	
	public void insertSort(String data, boolean minOrMax){
		String[] datas = data.split(",");
		ArrayList<SortNode> nodeList = new ArrayList<SortNode>();
		for(int i=0; i<datas.length; ++i){
			SortNode node = new SortNode(datas[i]);
			DsRect shape = new DsRect(ShapeSize.InsertSortModel.LEFT_MARGIN+i*ShapeSize.InsertSortModel.NODE_WIDTH, ShapeSize.InsertSortModel.TOP_MARGIN, ShapeSize.InsertSortModel.NODE_WIDTH, ShapeSize.InsertSortModel.NODE_HEIGHT, datas[i]);
			node.shape = shape;
			shapeList.add(shape);
			nodeList.add(node);
		}
		StringComparator comp = new StringComparator();
		DsRect rectI = new DsRect(ShapeSize.InsertSortModel.LEFT_MARGIN-ShapeSize.InsertSortModel.NODE_WIDTH, ShapeSize.InsertSortModel.TOP_MARGIN-ShapeSize.InsertSortModel.NODE_HEIGHT, ShapeSize.InsertSortModel.NODE_WIDTH, ShapeSize.InsertSortModel.NODE_HEIGHT, "i¡ý");
		DsRect rectJ = new DsRect(ShapeSize.InsertSortModel.LEFT_MARGIN-ShapeSize.InsertSortModel.NODE_WIDTH, ShapeSize.InsertSortModel.TOP_MARGIN+ShapeSize.InsertSortModel.NODE_HEIGHT, ShapeSize.InsertSortModel.NODE_WIDTH, ShapeSize.InsertSortModel.NODE_HEIGHT, "j¡ü");
		rectI.color = Color.YELLOW;
		rectJ.color = Color.YELLOW;
		rectI.fontSize = 30;
		rectJ.fontSize = 30;
		shapeList.add(rectI);
		shapeList.add(rectJ);
		
		int left = nodeList.get(0).shape.lx + (nodeList.get(nodeList.size()-1).shape.lx+nodeList.get(nodeList.size()-1).shape.lw-nodeList.get(0).shape.lx - ShapeSize.InsertSortModel.NODE_WIDTH*3)/2;
		int top = ShapeSize.InsertSortModel.TOP_MARGIN + ShapeSize.InsertSortModel.NODE_HEIGHT*3;
		if(left < nodeList.get(0).shape.lx) left = nodeList.get(0).shape.lx;
		DsRect compTipRect = new DsRect(left, top, ShapeSize.InsertSortModel.NODE_WIDTH*3, ShapeSize.InsertSortModel.NODE_HEIGHT, "CMP:");
		compTipRect.color = Color.CYAN;
		shapeList.add(compTipRect);
		for(int i=1; i<nodeList.size(); ++i){
			moveRect(rectI, nodeList.get(i).shape.lx, DIR_RIGHT);
			rectJ.lx = nodeList.get(0).shape.lx;
			for(int j=0; j < i; ++j){
				moveRect(rectJ, nodeList.get(j).shape.lx, DIR_RIGHT);
				if(comp.compare(nodeList.get(i).content, nodeList.get(j).content) > 0)
					compTipRect.content = "CMP: >";
				else if(comp.compare(nodeList.get(i).content, nodeList.get(j).content) < 0)
					compTipRect.content = "CMP: <";
				else 
					compTipRect.content = "CMP: =";
				tipForSearched(new DsRect[]{nodeList.get(i).shape, nodeList.get(j).shape});
				if(minOrMax && comp.compare(nodeList.get(i).content, nodeList.get(j).content) < 0 || 
						!minOrMax && comp.compare(nodeList.get(i).content, nodeList.get(j).content) > 0){
					DsRect insertRect = (DsRect) nodeList.get(i).shape.clone();
					insertRect.color = Color.GREEN;
					shapeList.add(insertRect);
					nodeList.get(i).shape.content = "";
					moveRect(insertRect, nodeList.get(i).shape.ly-ShapeSize.InsertSortModel.NODE_HEIGHT*2, DIR_UP);
					for(int k=i-1; k >= j; --k){
						DsRect movingRect = (DsRect) nodeList.get(k).shape.clone();
						movingRect.color = Color.GREEN;
						nodeList.get(k).shape.content = "";
						synchronized (Shape.class) {shapeList.add(movingRect);}
						moveRect(movingRect, nodeList.get(k+1).shape.lx, DIR_RIGHT);
						synchronized (Shape.class) {shapeList.remove(movingRect);}
						nodeList.get(k+1).content = nodeList.get(k).content;
						nodeList.get(k+1).shape.content = movingRect.content;
					}
					moveRect(insertRect, nodeList.get(j).shape.lx, DIR_LEFT);
					moveRect(insertRect, nodeList.get(j).shape.ly, DIR_DOWN);
					shapeList.remove(insertRect);
					nodeList.get(j).content = nodeList.get(j).shape.content = insertRect.content;
					model.setViewChanged();
					break;
				}
			}
		}
		shapeList.remove(rectI);
		shapeList.remove(rectJ);
		model.setViewChanged();
	}
	
	public static final int DIR_LEFT = 1;
	public static final int DIR_RIGHT = 2;
	public static final int DIR_UP = 3;
	public static final int DIR_DOWN = 4;
	private void moveRect(DsRect rect, int pos, int dir){
		final int offDistX = 10, offDistY = 5;
		switch(dir){
			case DIR_LEFT:
				while(rect.lx > pos){
					rect.lx -= offDistX;
					if(rect.lx < pos)
						rect.lx = pos;
					model.setViewChanged();
					delay(100);
				}
				break;
			case DIR_RIGHT:
				while(rect.lx < pos){
					rect.lx += offDistX;
					if(rect.lx > pos)
						rect.lx = pos;
					model.setViewChanged();
					delay(100);
				}
				break;
			case DIR_UP:
				while(rect.ly > pos){
					rect.ly -= offDistX;
					if(rect.ly < pos)
						rect.ly = pos;
					model.setViewChanged();
					delay(100);
				}
				break;
			case DIR_DOWN:
				while(rect.ly < pos){
					rect.ly += offDistX;
					if(rect.ly > pos)
						rect.ly = pos;
					model.setViewChanged();
					delay(100);
				}
				break;
			default:
				break;
		}
	}
	
	private void tipForSearched(DsRect[] shapes){
		boolean flag = true;
		Color color = null;
		for(int k=1; k<=4; ++k){
			color = flag ? Color.GREEN : Color.RED;
			for(DsRect shape : shapes)
				shape.color = color;
			flag = !flag;
			model.setViewChanged();
			delay(300);
		}
	}
	
	private void delay(int time){
		try {
			TimeUnit.MILLISECONDS.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public SampleInsertSortModel(DrawModel model) {
		super();
		this.model = model;
		shapeList = model.getShapeList();
	}
	
}
