package com.ds.model;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;

import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JViewport;

import com.ds.shape.DsRect;
import com.ds.shape.DsTipArrow;
import com.ds.shape.Shape;
import com.ds.size.ShapeSize;

public class MergeSortModel {
	private DrawModel model;
	private ArrayList<Shape> shapeList;
	
	private class StringComparator implements Comparator<String>{
		@Override
		public int compare(String s1, String s2) {
			return s1.length() == s2.length() ? s1.compareTo(s2) : s1.length()-s2.length();
		}
	}
	
	//向右移动箭头
	private void moveArrow(DsTipArrow arrow, int pos){
		final int offDistX = 10;
		while(arrow.x < pos){
			arrow.x += offDistX;
			if(arrow.x > pos)
				arrow.x = pos;
			model.setViewChanged();
			delay(100);
		}
	}
	
	private void adjustView(JScrollPane scrollpaen, int x, int y){
		JScrollBar hsb = scrollpaen.getHorizontalScrollBar();
		JScrollBar vsb = scrollpaen.getVerticalScrollBar();
		JViewport view = scrollpaen.getViewport();
		//返回一个矩形，其原位置在 getViewPosition，大小为 getExtentSize。这是视图在视图坐标中的可见部分。 
		Rectangle rect = view.getViewRect();
		int movex = x - (rect.x+rect.width/2);
		int movey = y - (rect.y+rect.height/2);
		hsb.setValue(hsb.getValue() + movex);
		vsb.setValue(vsb.getValue() + movey);
	}
	
	private void mSort(ArrayList<SortNode> nodeList){
		int ld = 0, rd = nodeList.size()-1;
		if(ld >= rd) return;
		int mid = (ld+rd)>>1;
		
		ArrayList<SortNode> ldNodeList = new ArrayList<SortNode>();
		ArrayList<SortNode> rdNodeList = new ArrayList<SortNode>();
		DsRect[] rects = new DsRect[rd-ld+1];
		for(int i=0; i<rects.length; ++i){
			SortNode newNode = (SortNode) nodeList.get(ld+i).clone();
			if(ld+i <= mid){
				newNode.shape.color = Color.RED;
				ldNodeList.add(newNode);
			}
			else {
				newNode.shape.color = Color.GREEN;
				rdNodeList.add(newNode);
			}
			shapeList.add(newNode.shape);
			rects[i] = newNode.shape;
			shapeList.add(rects[i]);
		}
		moveAllRectsToUpOrDown(rects, nodeList.get(ld).shape.ly+ShapeSize.MergeSortModel.LINE_DIST, true);
		DsTipArrow tipArrow = new DsTipArrow(nodeList.get(mid).shape.lx+ShapeSize.MergeSortModel.NODE_WIDTH/2+8, nodeList.get(mid).shape.ly+ShapeSize.MergeSortModel.LINE_DIST-5, "☟");
		shapeList.add(tipArrow);
		
		mSort(ldNodeList);
		mSort(rdNodeList);
		
		 
		compTipRect.content = "合并区间";
		compTipRect.color = Color.CYAN;
		compTipRect.lx = ShapeSize.MergeSortModel.LEFT_MARGIN - compTipRect.lw - 20;
		compTipRect.ly = ldNodeList.get(ld).shape.ly;
		tipForSearched(new DsRect[]{compTipRect});
		compTipRect.content = "CMP:";
		
		adjustView((JScrollPane)(model.getObserverPanel().getParent().getParent()), nodeList.get(ld).shape.lx, nodeList.get(ld).shape.ly);
		
		DsRect[] copyRects = new DsRect[rd-ld+1];
		for(int i=0; i<copyRects.length; ++i){
			copyRects[i] = (DsRect) rects[i].clone();
			copyRects[i].color = Color.WHITE;
			copyRects[i].content = "";
			shapeList.add(copyRects[i]);
		}
		moveAllRectsToUpOrDown(copyRects, rects[0].ly+ShapeSize.MergeSortModel.NODE_HEIGHT, true);
		
		StringComparator comp = new StringComparator();
		DsTipArrow iArrow = new DsTipArrow(nodeList.get(ld).shape.lx + ShapeSize.MergeSortModel.NODE_WIDTH/10, nodeList.get(ld).shape.ly+ShapeSize.MergeSortModel.LINE_DIST-5, "↓");
		DsTipArrow jArrow = new DsTipArrow(nodeList.get(mid+1).shape.lx + ShapeSize.MergeSortModel.NODE_WIDTH/10, nodeList.get(rd).shape.ly+ShapeSize.MergeSortModel.LINE_DIST-5, "↓");
		shapeList.add(iArrow);
		shapeList.add(jArrow);
		for(int i=0, j=0, k=0; i<ldNodeList.size() || j<rdNodeList.size(); ++k){
			if(i < ldNodeList.size())
				moveArrow(iArrow, ldNodeList.get(i).shape.lx + ShapeSize.MergeSortModel.NODE_WIDTH/10);
			if(j < rdNodeList.size())
				moveArrow(jArrow, rdNodeList.get(j).shape.lx + ShapeSize.MergeSortModel.NODE_WIDTH/10);
			DsRect movingRect = null;
			if(i<ldNodeList.size() && j<rdNodeList.size()){
				
				if(comp.compare(ldNodeList.get(i).content, rdNodeList.get(j).content) < 0)
					compTipRect.content = "CMP: <";
				else if(comp.compare(ldNodeList.get(i).content, rdNodeList.get(j).content) > 0)
					compTipRect.content = "CMP: >";
				else
					compTipRect.content = "CMP: =";
					
				if(minOrMax && comp.compare(ldNodeList.get(i).content, rdNodeList.get(j).content) < 0 ||
						!minOrMax && comp.compare(ldNodeList.get(i).content, rdNodeList.get(j).content) > 0){
				    movingRect = (DsRect) ldNodeList.get(i).shape.clone();
					++i;
				} else {
					movingRect = (DsRect) rdNodeList.get(j).shape.clone();
					++j;
				}
			} else if(i < ldNodeList.size()){
				compTipRect.content = "CMP:";
				movingRect = (DsRect) ldNodeList.get(i).shape.clone();
				++i;
			} else {
				compTipRect.content = "CMP:";
				movingRect = (DsRect) rdNodeList.get(j).shape.clone();
				++j;
			}
			movingRect.color = Color.BLUE;
			shapeList.add(movingRect);
			if(movingRect.lx > copyRects[k].lx)
				moveRect(movingRect, copyRects[k].lx, DIR_LEFT);
			else
				moveRect(movingRect, copyRects[k].lx, DIR_RIGHT);
			moveRect(movingRect, copyRects[k].ly, DIR_DOWN);
			copyRects[k].content = movingRect.content;
			copyRects[k].color = Color.LIGHT_GRAY;
			shapeList.remove(movingRect); 
		}
		
		shapeList.remove(iArrow);
		shapeList.remove(jArrow);
		
		//圆形飞出
		circleFly(tipArrow, rects);
		//用排好序的数组覆盖原始的数组
		moveAllRectsToUpOrDown(copyRects, nodeList.get(ld).shape.ly, false);
		for(int i=0; i<copyRects.length; ++i){
			nodeList.get(i).content = nodeList.get(i).shape.content = copyRects[i].content;
			shapeList.remove(copyRects[i]);
		}
	}
	
	private DsRect compTipRect = null;
	private boolean minOrMax;
	public void mergeSort(String data, boolean minOrMax){
		this.minOrMax = minOrMax;
		String[] datas = data.split(",");
		ArrayList<SortNode> nodeList = new ArrayList<SortNode>();
		for(int i=0; i<datas.length; ++i){
			SortNode node = new SortNode(datas[i]);
			DsRect shape = new DsRect(ShapeSize.MergeSortModel.LEFT_MARGIN+i*ShapeSize.MergeSortModel.NODE_WIDTH, ShapeSize.MergeSortModel.TOP_MARGIN, ShapeSize.MergeSortModel.NODE_WIDTH, ShapeSize.MergeSortModel.NODE_HEIGHT, datas[i]);
			node.shape = shape;
			shapeList.add(shape);
			nodeList.add(node);
		}
		
		compTipRect = new DsRect(-100, -100, ShapeSize.MergeSortModel.NODE_WIDTH*3, ShapeSize.MergeSortModel.NODE_HEIGHT, "CMP:");
		compTipRect.color = Color.CYAN;
		shapeList.add(compTipRect);
		
		int level = (int) (Math.log(nodeList.size()*1.0)/Math.log(2.0));
		model.getObserverPanel().setPreferredSize(new Dimension(ShapeSize.MergeSortModel.LEFT_MARGIN+(nodeList.size()+1)*ShapeSize.MergeSortModel.NODE_WIDTH, ShapeSize.MergeSortModel.TOP_MARGIN+(level+1)*(ShapeSize.MergeSortModel.NODE_HEIGHT+ShapeSize.MergeSortModel.LINE_DIST)));
		mSort(nodeList);
		model.setViewChanged();
	}
	
	public static final int DIR_LEFT = 1;
	public static final int DIR_RIGHT = 2;
	public static final int DIR_UP = 3;
	public static final int DIR_DOWN = 4;
	
	private void moveAllRectsToUpOrDown(DsRect[] rects, int pos, boolean isDown){
		int offDist = 10;
		if(rects.length == 0) return;
		if(isDown){
			while(rects[0].ly < pos){
				for(int i=0; i<rects.length; ++i)
					rects[i].ly += offDist;
				if(rects[0].ly > pos){
					for(int i=0; i<rects.length; ++i)
						rects[i].ly = pos;
				}
				model.setViewChanged();
				delay(100);
			}
		} else {
			while(rects[0].ly > pos){
				for(int i=0; i<rects.length; ++i)
					rects[i].ly -= offDist;
				if(rects[0].ly < pos){
					for(int i=0; i<rects.length; ++i)
						rects[i].ly = pos;
				}
				model.setViewChanged();
				delay(100);
			}
		}
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
	
	private void circleFly(DsTipArrow curRect, DsRect[] curRects){
		int angle = 0;
		int orgX = curRect.x;
		int orgY = curRect.y;
		final int R = 120;
		while(angle <= 360){
			double x = Math.PI*angle/180;
			double sinx = Math.sin(x);
			double cosx = Math.cos(x);
			
			curRect.y = (int) (orgY+R*(1-cosx));
			curRect.x = (int) (orgX+R*sinx);
			
			for(DsRect rect : curRects){
				rect.lx += curRect.x-orgX;
				rect.ly += curRect.y-orgY;
			}
			angle += 20;
			model.setViewChanged();
			delay(100);
			for(DsRect rect : curRects){
				rect.lx -= curRect.x-orgX;
				rect.ly -= curRect.y-orgY;
			}
		}
		final int dist = 50;
		while(curRect.y > -200){
			curRect.x += dist;
			curRect.y -= dist;
			for(DsRect rect : curRects){
				rect.lx += dist;
				rect.ly -= dist;
			}
			model.setViewChanged();
			delay(100);
		}
		shapeList.remove(curRect);
		for(DsRect rect : curRects)
			shapeList.remove(rect);
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
	
	public MergeSortModel(DrawModel model) {
		super();
		this.model = model;
		shapeList = model.getShapeList();
	}
}
