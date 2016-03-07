package com.ds.model;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;

import com.ds.shape.DsRect;
import com.ds.shape.Shape;
import com.ds.size.ShapeSize;

public class QuickSortModel {
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
	
	private boolean minOrMax;
	private void swapContent(SortNode x, SortNode y){
		String tmp = x.content;
		x.content = y.content;
		y.content = tmp;
		
		tmp = x.shape.content;
		x.shape.content = y.shape.content;
		y.shape.content = tmp;
	}
	
	private void moveTwoRectOpposite(DsRect rect1, DsRect rect2, DsRect rect3){
		int rect1XTo = rect2.lx;
		int rect2XTo = rect1.lx;
		int offDistX = 10;
		while(rect1.lx < rect1XTo){
			rect1.lx += offDistX;
			rect2.lx -= offDistX;
			if(rect3 != null)
				rect3.lx -= offDistX;
			if(rect1.lx > rect1XTo){
				rect1.lx = rect1XTo;
				rect2.lx = rect2XTo;
				if(rect3 != null)
					rect3.lx = rect2XTo;
			}
			model.setViewChanged();
			delay(100);
		}
	}
	
	private DsRect midValueOfThree(ArrayList<SortNode> nodeList, int ld, int rd){
		if(firstOrSecond) {
			moveRect(rectLd, nodeList.get(ld).shape.lx, nodeList.get(ld).shape.lx < rectLd.lx ? DIR_LEFT : DIR_RIGHT);
			moveRect(rectRd, nodeList.get(rd).shape.lx, nodeList.get(rd).shape.lx < rectRd.lx ? DIR_LEFT : DIR_RIGHT);
			tipForSearched(new DsRect[]{rectLd, rectRd});
			rectLd.color = Color.YELLOW;
			rectRd.color = Color.YELLOW;
		}
		StringComparator comp = new StringComparator();
		int mid = (ld+rd)>>1;
		
		compTipRect.content = "三者取中";
		tipForSearched(new DsRect[]{nodeList.get(ld).shape, nodeList.get(mid).shape, nodeList.get(rd).shape, compTipRect});
		compTipRect.content = "CMP:";
		compTipRect.color = Color.CYAN;
		
		DsRect rectPivot = null;
		if(comp.compare(nodeList.get(mid).content, nodeList.get(ld).content) < 0 && comp.compare(nodeList.get(mid).content, nodeList.get(rd).content) > 0 ||
				comp.compare(nodeList.get(mid).content, nodeList.get(ld).content) > 0 && comp.compare(nodeList.get(mid).content, nodeList.get(rd).content) < 0){
			rectPivot = new DsRect(nodeList.get(mid).shape.lx, nodeList.get(mid).shape.ly-ShapeSize.QuickSortModel.NODE_HEIGHT*2, ShapeSize.QuickSortModel.NODE_WIDTH, ShapeSize.QuickSortModel.NODE_HEIGHT, "pivot");
			rectPivot.fontSize = 20;
			shapeList.add(rectPivot);
			
			DsRect rect1 = (DsRect) nodeList.get(ld).shape.clone();
			rect1.color = Color.GREEN;
			DsRect rect2 = (DsRect) nodeList.get(mid).shape.clone();
			rect2.color = Color.GREEN;
			synchronized (Shape.class) {
				shapeList.add(rect1);
				shapeList.add(rect2);
			}
			moveTwoRectOpposite(rect1, rect2, rectPivot);
			synchronized (Shape.class) {
				shapeList.remove(rect1);
				shapeList.remove(rect2);
			}
			swapContent(nodeList.get(mid), nodeList.get(ld));
		} else if(comp.compare(nodeList.get(rd).content, nodeList.get(ld).content) < 0 && comp.compare(nodeList.get(rd).content, nodeList.get(mid).content) > 0 ||
				comp.compare(nodeList.get(rd).content, nodeList.get(ld).content) > 0 && comp.compare(nodeList.get(rd).content, nodeList.get(mid).content) < 0){
			rectPivot = new DsRect(nodeList.get(rd).shape.lx, nodeList.get(rd).shape.ly-ShapeSize.QuickSortModel.NODE_HEIGHT*2, ShapeSize.QuickSortModel.NODE_WIDTH, ShapeSize.QuickSortModel.NODE_HEIGHT, "pivot");
			rectPivot.fontSize = 20;
			shapeList.add(rectPivot);
			
			DsRect rect1 = (DsRect) nodeList.get(ld).shape.clone();
			rect1.color = Color.GREEN;
			DsRect rect2 = (DsRect) nodeList.get(rd).shape.clone();
			rect2.color = Color.GREEN;
			synchronized (Shape.class) {
				shapeList.add(rect1);
				shapeList.add(rect2);
			}
			moveTwoRectOpposite(rect1, rect2, rectPivot);
			synchronized (Shape.class) {
				shapeList.remove(rect1);
				shapeList.remove(rect2);
			}
			swapContent(nodeList.get(rd), nodeList.get(ld));
		} else {
			rectPivot = new DsRect(nodeList.get(ld).shape.lx, nodeList.get(ld).shape.ly-ShapeSize.QuickSortModel.NODE_HEIGHT*2, ShapeSize.QuickSortModel.NODE_WIDTH, ShapeSize.QuickSortModel.NODE_HEIGHT, "pivot");
			rectPivot.fontSize = 20;
			shapeList.add(rectPivot);
		}
		model.setViewChanged();
		DsRect rectPivotVal = (DsRect) nodeList.get(ld).shape.clone();
		
		if(firstOrSecond)
			nodeList.get(ld).shape.content = "";
		
		rectPivotVal.color = Color.GREEN;
		shapeList.add(rectPivotVal);
		moveRect(rectPivotVal, rectPivot.ly, DIR_UP);
		shapeList.remove(rectPivotVal);
		rectPivot.content = rectPivotVal.content;
		rectPivot.fontSize = -1;
		return rectPivot;
	}
	
	private int choosePivot1(ArrayList<SortNode> nodeList, int ld, int rd){
		StringComparator comp = new StringComparator();
		//得到枢轴对应的矩形
		DsRect rectPivot = midValueOfThree(nodeList, ld, rd);
		String pivot = nodeList.get(ld).content;
		while(ld < rd){
			while(ld < rd && (minOrMax && comp.compare(nodeList.get(rd).content, pivot) >= 0 || !minOrMax && comp.compare(nodeList.get(rd).content, pivot) <= 0)){
				if(comp.compare(nodeList.get(rd).content, pivot) > 0)
					compTipRect.content = "CMP: >";
				else
					compTipRect.content = "CMP: =";
				moveRect(rectRd, nodeList.get(rd).shape.lx, DIR_LEFT);
				tipForSearched(new DsRect[]{nodeList.get(rd).shape, rectPivot});
				--rd;
			}
			if(ld == rd) break;
			moveRect(rectRd, nodeList.get(rd).shape.lx, DIR_LEFT);
			compTipRect.content = "CMP: <";
			tipForSearched(new DsRect[]{nodeList.get(rd).shape, rectPivot});
			
			DsRect movingRect = (DsRect) nodeList.get(rd).shape.clone();
			nodeList.get(rd).shape.content  = "";
			movingRect.color = Color.GREEN;
			shapeList.add(movingRect);
			
			moveRect(movingRect, nodeList.get(ld).shape.ly-ShapeSize.QuickSortModel.NODE_HEIGHT, DIR_UP);
			moveRect(movingRect, nodeList.get(ld).shape.lx, DIR_LEFT);
			moveRect(movingRect, nodeList.get(ld).shape.ly, DIR_DOWN);
			shapeList.remove(movingRect);
			
			nodeList.get(ld).content = nodeList.get(rd).content;
			nodeList.get(ld).shape.content = movingRect.content;
			
			while(ld < rd && (minOrMax && comp.compare(nodeList.get(ld).content, pivot) <= 0 || !minOrMax && comp.compare(nodeList.get(ld).content, pivot) >= 0)){
				if(comp.compare(nodeList.get(ld).content, pivot) < 0)
					compTipRect.content = "CMP: <";
				else
					compTipRect.content = "CMP: =";
				moveRect(rectLd, nodeList.get(ld).shape.lx, DIR_RIGHT);
				tipForSearched(new DsRect[]{nodeList.get(ld).shape, rectPivot});
				++ld;
			}
			if(ld == rd) break;
			moveRect(rectLd, nodeList.get(ld).shape.lx, DIR_RIGHT);
			compTipRect.content = "CMP: >";
			tipForSearched(new DsRect[]{nodeList.get(ld).shape, rectPivot});
			
			movingRect = (DsRect) nodeList.get(ld).shape.clone();
			nodeList.get(ld).shape.content = "";
			movingRect.color = Color.GREEN;
			shapeList.add(movingRect);
			
			moveRect(movingRect, nodeList.get(rd).shape.ly-ShapeSize.QuickSortModel.NODE_HEIGHT, DIR_UP);
			moveRect(movingRect, nodeList.get(rd).shape.lx, DIR_RIGHT);
			moveRect(movingRect, nodeList.get(rd).shape.ly, DIR_DOWN);
			shapeList.remove(movingRect);
			
			nodeList.get(rd).content = nodeList.get(ld).content;
			nodeList.get(rd).shape.content = movingRect.content;
		}
		moveRect(rectPivot, nodeList.get(ld).shape.lx, DIR_RIGHT);
		moveRect(rectPivot, nodeList.get(ld).shape.ly, DIR_DOWN);
		nodeList.get(ld).content = pivot;
		nodeList.get(ld).shape.content = pivot;
		shapeList.remove(rectPivot);
		return ld;
	}
	
	private int choosePivot2(ArrayList<SortNode> nodeList, int ld, int rd){
		StringComparator comp = new StringComparator();
		//得到枢轴对应的矩形
		DsRect rectPivot = midValueOfThree(nodeList, ld, rd);
		String pivot = nodeList.get(ld).content;
		rectLd.lx = nodeList.get(ld).shape.lx;
		int i = ld;
		for(int j=ld+1; j <= rd; ++j){
			if(comp.compare(nodeList.get(j).content, pivot) > 0)
				compTipRect.content = "CMP: >";
			else if(comp.compare(nodeList.get(j).content, pivot) < 0)
				compTipRect.content = "CMP: <";
			else 
				compTipRect.content = "CMP: =";
			moveRect(rectLd, nodeList.get(j).shape.lx, DIR_RIGHT);
			tipForSearched(new DsRect[]{nodeList.get(j).shape, rectPivot});
			if(minOrMax && comp.compare(nodeList.get(j).content, pivot) <= 0 || !minOrMax && comp.compare(nodeList.get(j).content, pivot) >= 0){
				DsRect rect1 = (DsRect) nodeList.get(++i).shape.clone();
				DsRect rect2 = (DsRect) nodeList.get(j).shape.clone();
				rect1.color = Color.GREEN;
				rect2.color = Color.GREEN;
				synchronized (Shape.class) {shapeList.add(rect1); shapeList.add(rect2);}
				moveTwoRectOpposite(rect1, rect2, null);
				synchronized (Shape.class) {shapeList.remove(rect1); shapeList.remove(rect2);}
				swapContent(nodeList.get(i), nodeList.get(j));
			}
		}
		compTipRect.content = "枢轴归位";
		model.setViewChanged();
		delay(500);
		DsRect rect1 = (DsRect) nodeList.get(ld).shape.clone();
		DsRect rect2 = (DsRect) nodeList.get(i).shape.clone();
		rect1.color = Color.GREEN;
		rect2.color = Color.GREEN;
		synchronized (Shape.class) {shapeList.add(rect1); shapeList.add(rect2);}
		moveTwoRectOpposite(rect1, rect2, null);
		synchronized (Shape.class) {shapeList.remove(rect1); shapeList.remove(rect2);}
		swapContent(nodeList.get(i), nodeList.get(ld));
		
		shapeList.remove(rectPivot);
		return i;
	}
	
	private void qsort(ArrayList<SortNode> nodeList, int ld, int rd){
		if(ld >= rd) return;
		int mid;
		
		if(firstOrSecond)
			mid = choosePivot1(nodeList, ld, rd);
		else
			mid = choosePivot2(nodeList, ld, rd);
		
		qsort(nodeList, ld, mid-1);
		qsort(nodeList, mid+1, rd);
	}
	
	private DsRect rectLd = null;
	private DsRect rectRd = null;
	private DsRect compTipRect = null;
	public void quickSort(String data, boolean minOrMax){
		this.minOrMax = minOrMax;
		String[] datas = data.split(",");
		ArrayList<SortNode> nodeList = new ArrayList<SortNode>();
		for(int i=0; i<datas.length; ++i){
			SortNode node = new SortNode(datas[i]);
			DsRect shape = new DsRect(ShapeSize.QuickSortModel.LEFT_MARGIN+i*ShapeSize.QuickSortModel.NODE_WIDTH, ShapeSize.QuickSortModel.TOP_MARGIN, ShapeSize.QuickSortModel.NODE_WIDTH, ShapeSize.QuickSortModel.NODE_HEIGHT, datas[i]);
			node.shape = shape;
			shapeList.add(shape);
			nodeList.add(node);
		}
		
		if(firstOrSecond) {
			rectLd = new DsRect(ShapeSize.QuickSortModel.LEFT_MARGIN-ShapeSize.QuickSortModel.NODE_WIDTH, ShapeSize.QuickSortModel.TOP_MARGIN+ShapeSize.QuickSortModel.NODE_HEIGHT, ShapeSize.QuickSortModel.NODE_WIDTH, ShapeSize.QuickSortModel.NODE_HEIGHT, "↑");
			rectRd = new DsRect(nodeList.get(nodeList.size()-1).shape.lx+ShapeSize.QuickSortModel.NODE_WIDTH, ShapeSize.QuickSortModel.TOP_MARGIN+ShapeSize.QuickSortModel.NODE_HEIGHT, ShapeSize.QuickSortModel.NODE_WIDTH, ShapeSize.QuickSortModel.NODE_HEIGHT, "↑");
			rectRd.color = Color.YELLOW;
			shapeList.add(rectRd);
		} else {
			rectLd = new DsRect(ShapeSize.QuickSortModel.LEFT_MARGIN, ShapeSize.QuickSortModel.TOP_MARGIN+ShapeSize.QuickSortModel.NODE_HEIGHT, ShapeSize.QuickSortModel.NODE_WIDTH, ShapeSize.QuickSortModel.NODE_HEIGHT, "↑");
		}
		rectLd.color = Color.YELLOW;
		shapeList.add(rectLd);
		
		int left = nodeList.get(0).shape.lx + (nodeList.get(nodeList.size()-1).shape.lx+nodeList.get(nodeList.size()-1).shape.lw-nodeList.get(0).shape.lx - ShapeSize.QuickSortModel.NODE_WIDTH*3)/2;
		int top = ShapeSize.QuickSortModel.TOP_MARGIN + ShapeSize.QuickSortModel.NODE_HEIGHT*3;
		if(left < nodeList.get(0).shape.lx) left = nodeList.get(0).shape.lx;
		compTipRect = new DsRect(left, top, ShapeSize.QuickSortModel.NODE_WIDTH*3, ShapeSize.QuickSortModel.NODE_HEIGHT, "CMP:");
		compTipRect.color = Color.CYAN;
		shapeList.add(compTipRect);
		
		qsort(nodeList, 0, nodeList.size()-1);
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
					rect.ly -= offDistY;
					if(rect.ly < pos)
						rect.ly = pos;
					model.setViewChanged();
					delay(100);
				}
				break;
			case DIR_DOWN:
				while(rect.ly < pos){
					rect.ly += offDistY;
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
	
	private boolean firstOrSecond;
	public QuickSortModel(DrawModel model, Boolean firstOrSecond) {
		super();
		this.model = model;
		this.firstOrSecond = firstOrSecond;
		shapeList = model.getShapeList();
	}
	
}
