package com.ds.model;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;
import com.ds.shape.DsRect;
import com.ds.shape.Shape;
import com.ds.size.ShapeSize;

public class BubbleSortModel {
	private DrawModel model;
	private ArrayList<Shape> shapeList;
	
	public static final int DIR_LEFT = 1;
	public static final int DIR_RIGHT = 2;
	public static final int DIR_UP = 3;
	public static final int DIR_DOWN = 4;
	
	private class StringComparator implements Comparator<String>{
		@Override
		public int compare(String s1, String s2) {
			return s1.length() == s2.length() ? s1.compareTo(s2) : s1.length()-s2.length();
		}
	}
	
	private void moveTwoRectOpposite(DsRect rect1, DsRect rect2){
		int rect1XTo = rect2.lx;
		int rect2XTo = rect1.lx;
		int offDistX = 10;
		while(rect1.lx < rect1XTo){
			rect1.lx += offDistX;
			rect2.lx -= offDistX;
			if(rect1.lx > rect1XTo){
				rect1.lx = rect1XTo;
				rect2.lx = rect2XTo;
			}
			model.setViewChanged();
			delay(100);
		}
	}
	
	public void bubbleSort(String data, boolean minOrMax){
		String[] datas = data.split(",");
		ArrayList<SortNode> nodeList = new ArrayList<SortNode>();
		for(int i=0; i<datas.length; ++i){
			SortNode node = new SortNode(datas[i]);
			DsRect shape = new DsRect(ShapeSize.BubbleSortModel.LEFT_MARGIN+i*ShapeSize.BubbleSortModel.NODE_WIDTH, ShapeSize.BubbleSortModel.TOP_MARGIN, ShapeSize.BubbleSortModel.NODE_WIDTH, ShapeSize.BubbleSortModel.NODE_HEIGHT, datas[i]);
			node.shape = shape;
			shapeList.add(shape);
			nodeList.add(node);
		}
		
		StringComparator comp = new StringComparator();
		DsRect rectI = new DsRect(nodeList.get(nodeList.size()-1).shape.lx+ShapeSize.InsertSortModel.NODE_WIDTH, ShapeSize.BubbleSortModel.TOP_MARGIN-ShapeSize.BubbleSortModel.NODE_HEIGHT, ShapeSize.BubbleSortModel.NODE_WIDTH, ShapeSize.BubbleSortModel.NODE_HEIGHT, "¡ý");
		DsRect rectJ = new DsRect(nodeList.get(nodeList.size()-1).shape.lx+ShapeSize.InsertSortModel.NODE_WIDTH, ShapeSize.BubbleSortModel.TOP_MARGIN+ShapeSize.BubbleSortModel.NODE_HEIGHT, ShapeSize.BubbleSortModel.NODE_WIDTH, ShapeSize.BubbleSortModel.NODE_HEIGHT, "¡ü");
		rectI.color = Color.YELLOW;
		rectJ.color = Color.YELLOW;
		shapeList.add(rectI);
		shapeList.add(rectJ);
		
		int left = nodeList.get(0).shape.lx + (nodeList.get(nodeList.size()-1).shape.lx+nodeList.get(nodeList.size()-1).shape.lw-nodeList.get(0).shape.lx - ShapeSize.BubbleSortModel.NODE_WIDTH*3)/2;
		int top = ShapeSize.BubbleSortModel.TOP_MARGIN + ShapeSize.BubbleSortModel.NODE_HEIGHT*3;
		if(left < nodeList.get(0).shape.lx) left = nodeList.get(0).shape.lx;
		DsRect compTipRect = new DsRect(left, top, ShapeSize.BubbleSortModel.NODE_WIDTH*3, ShapeSize.BubbleSortModel.NODE_HEIGHT, "CMP:");
		compTipRect.color = Color.CYAN;
		shapeList.add(compTipRect);
		for(int i=nodeList.size()-1; i > 0; --i){
			 for(int j=0; j < i; ++j){//Ã°ÅÝ½»»»Êý×Ö
				 if(comp.compare(nodeList.get(j).content, nodeList.get(j+1).content) > 0)
					 compTipRect.content = "CMP: >";
				 else if(comp.compare(nodeList.get(j).content, nodeList.get(j+1).content) < 0)
					 compTipRect.content = "CMP: <";
				 else
					 compTipRect.content = "CMP: =";
				 tipForSearched(new DsRect[]{nodeList.get(j).shape, nodeList.get(j+1).shape});
				 if(minOrMax && comp.compare(nodeList.get(j).content, nodeList.get(j+1).content) > 0 ||
							!minOrMax && comp.compare(nodeList.get(j).content, nodeList.get(j+1).content) < 0){
					DsRect rect1 = (DsRect) nodeList.get(j).shape.clone();
					DsRect rect2 = (DsRect) nodeList.get(j+1).shape.clone();
					rect1.color = Color.GREEN;
					rect2.color = Color.GREEN;
					shapeList.add(rect1);
					shapeList.add(rect2);
					moveTwoRectOpposite(rect1, rect2);
					shapeList.remove(rect1);
					shapeList.remove(rect2);
					
					String tmp = nodeList.get(j).content;
					nodeList.get(j).content = nodeList.get(j+1).content;
					nodeList.get(j+1).content = tmp;
					tmp = nodeList.get(j).shape.content;
					nodeList.get(j).shape.content = nodeList.get(j+1).shape.content;
					nodeList.get(j+1).shape.content = tmp;
					
					model.setViewChanged();
				 }
			 }
			 moveRect(rectI, nodeList.get(i).shape.lx, DIR_LEFT);
			 if(i == nodeList.size()-1)
				 moveRect(rectJ, nodeList.get(nodeList.size()-1).shape.lx, DIR_LEFT);
		}
		moveRect(rectI, nodeList.get(0).shape.lx, DIR_LEFT);
		model.setViewChanged();
	}
	
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
	
	public BubbleSortModel(DrawModel model) {
		super();
		this.model = model;
		shapeList = model.getShapeList();
	}
	
}
