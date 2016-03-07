package com.ds.model;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import javax.swing.JLabel;

import sun.java2d.pipe.ShapeDrawPipe;

import com.ds.controler.DrawControler;
import com.ds.panel.DrawPanel;
import com.ds.shape.DsLine;
import com.ds.shape.DsListRect;
import com.ds.shape.DsRect;
import com.ds.shape.DsTipArrow;
import com.ds.shape.Shape;
import com.ds.size.ShapeSize;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;

/**
 * @author hjzgg
 * 生成链表的模型
 */
public class ListModel{
	private DrawModel model;
	private LinkedList<ListNode> nodeList = null;
	
	public static final int DIR_LEFT = 1;
	public static final int DIR_RIGHT = 2;
	public static final int DIR_UP = 3;
	public static final int DIR_DOWN = 4;
	
	public void createListData(String data){
		nodeList = new LinkedList<ListNode>();
		ArrayList<Shape> shapeList = model.getShapeList();
		String[] contents = data.split(",");
		int left = 0, top = ShapeSize.ListModel.TOP_MARGIN-ShapeSize.ListModel.ROW_HEIGHT;
		for(int i=0; i<contents.length; ++i){
			ListNode newNode = new ListNode(contents[i]);
			if(i % ShapeSize.ListModel.ROW_NUM == 0){//新的一行的第一个节点
				int preTop = top;//上一行
				top += ShapeSize.ListModel.ROW_HEIGHT;//下一行
				if(i != 0){//添加行与行之间的折线
					int preRowMidTop = (preTop*2+ShapeSize.ListModel.RECT_HEIGHT)/2;
					int rowAndRowMidTop = (preTop+ShapeSize.ListModel.RECT_HEIGHT+top)/2;
					DsLine lineCol1 = new DsLine(left, preRowMidTop, left, rowAndRowMidTop, false);
					shapeList.add(lineCol1);
					nodeList.get(nodeList.size()-1).lineList.add(lineCol1);
					DsLine lineRow1 = new DsLine(left, rowAndRowMidTop, ShapeSize.ListModel.LEFT_MARGIN, rowAndRowMidTop, false);
					shapeList.add(lineRow1);
					nodeList.get(nodeList.size()-1).lineList.add(lineRow1);
					int nextRowMidTop = (top*2+ShapeSize.ListModel.RECT_HEIGHT)/2;
					DsLine lineCol2 = new DsLine(ShapeSize.ListModel.LEFT_MARGIN, rowAndRowMidTop, ShapeSize.ListModel.LEFT_MARGIN, nextRowMidTop, false);
					shapeList.add(lineCol2);
					nodeList.get(nodeList.size()-1).lineList.add(lineCol2);
					DsLine lineRow2 = new DsLine(ShapeSize.ListModel.LEFT_MARGIN, nextRowMidTop, ShapeSize.ListModel.LEFT_MARGIN+ShapeSize.ListModel.LINE_DIST_X, nextRowMidTop, true);
					shapeList.add(lineRow2);
					nodeList.get(nodeList.size()-1).lineList.add(lineRow2);
				}
				left = ShapeSize.ListModel.LEFT_MARGIN+ShapeSize.ListModel.LINE_DIST_X;
			} 
			DsListRect rect = new DsListRect(left, top, ShapeSize.ListModel.RECT_WIDTH, ShapeSize.ListModel.RECT_HEIGHT, contents[i]);
			shapeList.add(rect);
			newNode.shape = rect;
			
			if(i != contents.length-1){//每一个rect的后面都加上一条横线
				left += ShapeSize.ListModel.RECT_WIDTH;
				boolean isArrow = true;
				if((i+1)%ShapeSize.ListModel.ROW_NUM == 0)
					isArrow = false;
				DsLine line = new DsLine(left-ShapeSize.ListModel.SMALL_RECT_WIDTH/2, (ShapeSize.ListModel.RECT_HEIGHT+top*2)/2, left+=ShapeSize.ListModel.LINE_DIST_X, (ShapeSize.ListModel.RECT_HEIGHT+top*2)/2, isArrow);
				shapeList.add(line);
				newNode.lineList.add(line);
			}
			nodeList.add(newNode);
		}
	}
	
	public void showListCreate(String data){
		ArrayList<Shape> shapeList = model.getShapeList();
		String[] contents = data.split(",");
		int left = 0, top =  ShapeSize.ListModel.TOP_MARGIN-ShapeSize.ListModel.ROW_HEIGHT;
		DsListRect rect = null;
		for(int i=0; i<contents.length; ++i){
			synchronized (Shape.class) {
				if(i % ShapeSize.ListModel.ROW_NUM == 0){//新的一行的第一个节点
					int preTop = top;//上一行
					top += ShapeSize.ListModel.ROW_HEIGHT;//下一行
					if(i != 0){//添加行与行之间的折线
						int preRowMidTop = (preTop*2+ShapeSize.ListModel.RECT_HEIGHT)/2;
						int rowAndRowMidTop = (preTop+ShapeSize.ListModel.RECT_HEIGHT+top)/2;
						DsLine lineCol1 = new DsLine(left, preRowMidTop, left, rowAndRowMidTop, false);
						shapeList.add(lineCol1);
						DsLine lineRow1 = new DsLine(left, rowAndRowMidTop, ShapeSize.ListModel.LEFT_MARGIN, rowAndRowMidTop, false);
						shapeList.add(lineRow1);
						int nextRowMidTop = (top*2+ShapeSize.ListModel.RECT_HEIGHT)/2;
						DsLine lineCol2 = new DsLine(ShapeSize.ListModel.LEFT_MARGIN, rowAndRowMidTop, ShapeSize.ListModel.LEFT_MARGIN, nextRowMidTop, false);
						shapeList.add(lineCol2);
						DsLine lineRow2 = new DsLine(ShapeSize.ListModel.LEFT_MARGIN, nextRowMidTop, ShapeSize.ListModel.LEFT_MARGIN+ShapeSize.ListModel.LINE_DIST_X, nextRowMidTop, true);
						shapeList.add(lineRow2);
					}
					left = ShapeSize.ListModel.LEFT_MARGIN+ShapeSize.ListModel.LINE_DIST_X;
				} 
				rect = new DsListRect(left, top, ShapeSize.ListModel.RECT_WIDTH, ShapeSize.ListModel.RECT_HEIGHT, contents[i]);
				shapeList.add(rect);
				
				if(i != contents.length-1){//每一个rect的后面都加上一条横线
					left += ShapeSize.ListModel.RECT_WIDTH;
					boolean isArrow = true;
					if((i+1)%ShapeSize.ListModel.ROW_NUM == 0)
						isArrow = false;
					DsLine line = new DsLine(left-ShapeSize.ListModel.SMALL_RECT_WIDTH/2, (ShapeSize.ListModel.RECT_HEIGHT+top*2)/2, left+=ShapeSize.ListModel.LINE_DIST_X, (ShapeSize.ListModel.RECT_HEIGHT+top*2)/2, isArrow);
					shapeList.add(line);
				}
			}
			
			boolean flag = true;
			for(int k=1; k<=4; ++k){
				model.setViewChanged();
				rect.color = flag ? Color.GREEN : null;
				flag = !flag;
				try {
					TimeUnit.MILLISECONDS.sleep(500);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void showListInsert(String content, int pos){
		if(pos<1 || pos>nodeList.size()+1)
			throw new IllegalArgumentException("数组插入位置不合法!");
		ArrayList<Shape> shapeList = model.getShapeList();
		//插入的新块
		DsListRect rect = null;
		ArrayList<DsLine> lines = new ArrayList<DsLine>();
		if(pos == nodeList.size()+1){
			int i = pos-1;
			int top =  ShapeSize.ListModel.TOP_MARGIN-ShapeSize.ListModel.ROW_HEIGHT;
			int left = ShapeSize.ListModel.LEFT_MARGIN+ShapeSize.ListModel.LINE_DIST_X;
			
			if(nodeList.size() != 0){
				top = ShapeSize.ListModel.ROW_HEIGHT*((i-1)/ShapeSize.ListModel.ROW_NUM) + ShapeSize.ListModel.TOP_MARGIN;
				left = nodeList.get(nodeList.size()-1).shape.lx + ShapeSize.ListModel.RECT_WIDTH;
				boolean isArrow = true;
				if(i%ShapeSize.ListModel.ROW_NUM == 0)
					isArrow = false;
				DsLine line = new DsLine(left-ShapeSize.ListModel.SMALL_RECT_WIDTH/2, (ShapeSize.ListModel.RECT_HEIGHT+top*2)/2, left+=ShapeSize.ListModel.LINE_DIST_X, (ShapeSize.ListModel.RECT_HEIGHT+top*2)/2, isArrow);
				lines.add(line);
			}
			
			if(i % ShapeSize.ListModel.ROW_NUM == 0){//新的一行的第一个节点
				int preTop = top;//上一行
				top += ShapeSize.ListModel.ROW_HEIGHT;//下一行
				if(i != 0){//添加行与行之间的折线
					int preRowMidTop = (preTop*2+ShapeSize.ListModel.RECT_HEIGHT)/2;
					int rowAndRowMidTop = (preTop+ShapeSize.ListModel.RECT_HEIGHT+top)/2;
					DsLine lineCol1 = new DsLine(left, preRowMidTop, left, rowAndRowMidTop, false);
					lines.add(lineCol1);
					DsLine lineRow1 = new DsLine(left, rowAndRowMidTop, ShapeSize.ListModel.LEFT_MARGIN, rowAndRowMidTop, false);
					lines.add(lineRow1);
					int nextRowMidTop = (top*2+ShapeSize.ListModel.RECT_HEIGHT)/2;
					DsLine lineCol2 = new DsLine(ShapeSize.ListModel.LEFT_MARGIN, rowAndRowMidTop, ShapeSize.ListModel.LEFT_MARGIN, nextRowMidTop, false);
					lines.add(lineCol2);
					DsLine lineRow2 = new DsLine(ShapeSize.ListModel.LEFT_MARGIN, nextRowMidTop, ShapeSize.ListModel.LEFT_MARGIN+ShapeSize.ListModel.LINE_DIST_X, nextRowMidTop, true);
					lines.add(lineRow2);
				}
				left = ShapeSize.ListModel.LEFT_MARGIN+ShapeSize.ListModel.LINE_DIST_X;
			} 
			rect = new DsListRect(left, top, ShapeSize.ListModel.RECT_WIDTH, ShapeSize.ListModel.RECT_HEIGHT, content);
		} 
		
		//找到位置
		DsListRect posRect = null;
		DsListRect beginRect = null;
		if(nodeList.size() == 0 || pos == nodeList.size()+1){
			if(nodeList.size() == 0){
				beginRect = posRect = rect;
			} else {
				beginRect = nodeList.get(0).shape;
				posRect = rect;
			}
		} else {
			beginRect = nodeList.get(0).shape;
			posRect = nodeList.get(pos-1).shape;
		}
		DsListRect tipRect = new DsListRect(beginRect.lx-beginRect.lw, beginRect.ly-beginRect.lh, beginRect.lw, beginRect.lh, content);
		tipRect.isDrawColLine = false;
		shapeList.add(tipRect);
		searchPosition(tipRect, posRect);
		
		DsListRect deleteRect = new DsListRect(posRect.lx, posRect.ly+posRect.lh, posRect.lw, posRect.lh, "↑");
		deleteRect.isDrawColLine = false;
		shapeList.add(deleteRect);
		tipForHadSearched(tipRect, deleteRect);
		
		shapeList.remove(deleteRect);
		model.setViewChanged();
		
		if(pos == nodeList.size()+1){//直接在末端插入并显示
			while(tipRect.ly < rect.ly){
				tipRect.ly += 5;
				model.setViewChanged();
				delay(100);
			}
			shapeList.remove(tipRect);
			shapeList.add(rect);
			tipForHadSearched(rect, null);
			
			int[] dir = {DIR_RIGHT, DIR_DOWN, DIR_LEFT, DIR_DOWN, DIR_RIGHT};
			final int offDist = 10;
			for(int i=0; i<lines.size(); ++i){
				DsLine line2 = lines.get(i);
				DsLine line1 = new DsLine(line2.x1, line2.y1, line2.x1, line2.y1, true);
				synchronized (Shape.class) { shapeList.add(line1); }
				lineMove(line1, line2, dir[i], offDist, 100);
				synchronized (Shape.class) { 
					shapeList.remove(line1); 
					shapeList.add(line2);
					if(nodeList.size() > 0)
						nodeList.get(nodeList.size()-1).lineList.add(line2);
				}
			}
			
			ListNode newNode = new ListNode(content);
			newNode.shape = rect;
			nodeList.add(pos-1, newNode);
		} else {
			if(pos > 1){// 连线 飞走
				//圆形 旋转
				DsListRect curRect = (DsListRect) nodeList.get(pos-2).shape.clone();
				ArrayList<DsLine> curLines = nodeList.get(pos-2).lineList;
				circleFly(curRect, curLines);
			}
			
			if((pos-1) % ShapeSize.ListModel.ROW_NUM != 0){//在当前行某位置插入
				rect = new DsListRect(posRect.lx-((ShapeSize.ListModel.LINE_DIST_X-posRect.lw)/2 + posRect.lw), 
						posRect.ly+posRect.lh+ShapeSize.ListModel.NEW_RECT_TOPDIST, posRect.lw, posRect.lh, content);
				while(tipRect.lx > rect.lx){
					tipRect.lx -= 5;
					model.setViewChanged();
					delay(100);
				}
				while(tipRect.ly < rect.ly){
					tipRect.ly += 5;
					model.setViewChanged();
					delay(100);
				}
				
				shapeList.remove(tipRect);
				shapeList.add(rect);
				tipForHadSearched(rect, null);
				lines.clear();
				
				nodeList.get(pos-2).lineList.clear();
				DsListRect leftRect = nodeList.get(pos-2).shape, rightRect = nodeList.get(pos-1).shape;
				lines.add(new DsLine(leftRect.lx+leftRect.lw-ShapeSize.ListModel.SMALL_RECT_WIDTH/2, leftRect.ly+leftRect.lh/2, leftRect.lx+leftRect.lw+ShapeSize.ListModel.SMALL_RECT_WIDTH/2, leftRect.ly+leftRect.lh/2, false));
				nodeList.get(pos-2).lineList.add(lines.get(lines.size()-1));
				lines.add(new DsLine(leftRect.lx+leftRect.lw+ShapeSize.ListModel.SMALL_RECT_WIDTH/2, leftRect.ly+leftRect.lh/2, leftRect.lx+leftRect.lw+ShapeSize.ListModel.SMALL_RECT_WIDTH/2, rect.ly+rect.lh/2, false));
				nodeList.get(pos-2).lineList.add(lines.get(lines.size()-1));
				lines.add(new DsLine(leftRect.lx+leftRect.lw+ShapeSize.ListModel.SMALL_RECT_WIDTH/2, rect.ly+rect.lh/2, rect.lx, rect.ly+rect.lh/2, true));
				nodeList.get(pos-2).lineList.add(lines.get(lines.size()-1));
				
				ListNode newNode = new ListNode(content);
				newNode.shape = rect;
				//模型中插入新的链表节点
				nodeList.add(pos-1, newNode);
				lines.add(new DsLine(rect.lx+rect.lw-ShapeSize.ListModel.SMALL_RECT_WIDTH/2, rect.ly+rect.lh/2, rect.lx+rect.lw+ShapeSize.ListModel.SMALL_RECT_WIDTH/2, rect.ly+rect.lh/2, false));
				newNode.lineList.add(lines.get(lines.size()-1));
				lines.add(new DsLine(rect.lx+rect.lw+ShapeSize.ListModel.SMALL_RECT_WIDTH/2, rect.ly+rect.lh/2, rect.lx+rect.lw+ShapeSize.ListModel.SMALL_RECT_WIDTH/2, rightRect.ly+rightRect.lh/2, false));
				newNode.lineList.add(lines.get(lines.size()-1));
				lines.add(new DsLine(rect.lx+rect.lw+ShapeSize.ListModel.SMALL_RECT_WIDTH/2, rightRect.ly+rightRect.lh/2, rightRect.lx, rightRect.ly+rightRect.lh/2, true));
				newNode.lineList.add(lines.get(lines.size()-1));
				
				int[] dir = {DIR_RIGHT, DIR_DOWN, DIR_RIGHT, DIR_RIGHT, DIR_UP, DIR_RIGHT};
				final int offDist = 5;
				for(int i=0; i<lines.size(); ++i){
					 DsLine line2 = lines.get(i);
					 DsLine line1 = new DsLine(line2.x1, line2.y1, line2.x1, line2.y1, true);
					 line1.color = line2.color = Color.GREEN;
					 synchronized (Shape.class) {shapeList.add(line1);}
					 lineMove(line1, line2, dir[i], offDist, 100);
					 synchronized (Shape.class){
						 shapeList.remove(line1);
						 shapeList.add(line2);
					 }
					 
				}
			} else {//在两行之间插入的图形
				 rect = new DsListRect(ShapeSize.ListModel.LEFT_MARGIN+(ShapeSize.ListModel.LINE_DIST_XX-ShapeSize.ListModel.RECT_WIDTH)/2, 
						 posRect.ly-(posRect.lh+(ShapeSize.ListModel.ROW_HEIGHT-2*posRect.lh)/2), ShapeSize.ListModel.RECT_WIDTH, ShapeSize.ListModel.RECT_HEIGHT, content);
				 rect.isLineDrawRight = false;
				 while(tipRect.ly > rect.ly){
					 tipRect.ly -= 5;
					 model.setViewChanged();
					 delay(100);
				 }
				 while(tipRect.lx < rect.lx){
					tipRect.lx += 5;
					model.setViewChanged();
					delay(100);
				 }
				 
				 shapeList.remove(tipRect);
				 shapeList.add(rect);
				 tipForHadSearched(rect, null);
				 
				 ListNode newNode = new ListNode(content);
				 newNode.shape = rect;
				 lines.clear();
				 DsListRect downRect = nodeList.get(pos-1).shape;
				 int[] dir1 = {DIR_RIGHT, DIR_DOWN, DIR_LEFT, DIR_LEFT, DIR_DOWN, DIR_RIGHT};
				 int[] dir2 = {DIR_LEFT, DIR_DOWN, DIR_RIGHT};
				 int[] dir = null;
				 if(pos > 1) {
					 nodeList.get(pos-2).lineList.clear();
					 DsListRect topRect = nodeList.get(pos-2).shape;
					 lines.add(new DsLine(topRect.lx+topRect.lw-ShapeSize.ListModel.SMALL_RECT_WIDTH/2, topRect.ly+topRect.lh/2, ShapeSize.ListModel.RIGHT_MARGIN, topRect.ly+topRect.lh/2, false));
					 nodeList.get(pos-2).lineList.add(lines.get(lines.size()-1));
					 lines.add(new DsLine(ShapeSize.ListModel.RIGHT_MARGIN, topRect.ly+topRect.lh/2, ShapeSize.ListModel.RIGHT_MARGIN, rect.ly+rect.lh/2, false));
					 nodeList.get(pos-2).lineList.add(lines.get(lines.size()-1));
					 lines.add(new DsLine(ShapeSize.ListModel.RIGHT_MARGIN, rect.ly+rect.lh/2, rect.lx+rect.lw, rect.ly+rect.lh/2, false));
					 nodeList.get(pos-2).lineList.add(lines.get(lines.size()-1));
					 
					 lines.add(new DsLine(rect.lx+ShapeSize.ListModel.SMALL_RECT_WIDTH/2, rect.ly+rect.lh/2, ShapeSize.ListModel.LEFT_MARGIN, rect.ly+rect.lh/2, false));
					 newNode.lineList.add(lines.get(lines.size()-1));
					 lines.add(new DsLine(ShapeSize.ListModel.LEFT_MARGIN, rect.ly+rect.lh/2, ShapeSize.ListModel.LEFT_MARGIN, downRect.ly+downRect.lh/2, false));
					 newNode.lineList.add(lines.get(lines.size()-1));
					 lines.add(new DsLine(ShapeSize.ListModel.LEFT_MARGIN, downRect.ly+downRect.lh/2, downRect.lx, downRect.ly+downRect.lh/2, true));
					 newNode.lineList.add(lines.get(lines.size()-1));
					 dir = dir1;
				 } else {
					 lines.add(new DsLine(rect.lx+ShapeSize.ListModel.SMALL_RECT_WIDTH/2, rect.ly+rect.lh/2, ShapeSize.ListModel.LEFT_MARGIN, rect.ly+rect.lh/2, false));
					 lines.add(new DsLine(ShapeSize.ListModel.LEFT_MARGIN, rect.ly+rect.lh/2, ShapeSize.ListModel.LEFT_MARGIN, downRect.ly+downRect.lh/2, false));
					 lines.add(new DsLine(ShapeSize.ListModel.LEFT_MARGIN, downRect.ly+downRect.lh/2, downRect.lx, downRect.ly+downRect.lh/2, true));
					 newNode.lineList = lines;
					 dir = dir2; 
				 }
				 //模型中插入新的链表节点
				 nodeList.add(pos-1, newNode);
				 
				 final int offDist = 10;
				 for(int i=0; i<lines.size(); ++i){
					 DsLine line2 = lines.get(i);
					 DsLine line1 = new DsLine(line2.x1, line2.y1, line2.x1, line2.y1, true);
					 line1.color = line2.color = Color.GREEN;
					 synchronized (Shape.class) {shapeList.add(line1);}
					 lineMove(line1, line2, dir[i], offDist, 100);
					 synchronized (Shape.class){
						 shapeList.remove(line1);
						 shapeList.add(line2);
					 }
				 }
			}
		}
	}
	
	private void searchPosition(DsListRect tipRect, DsListRect posRect){
		while(tipRect.ly+tipRect.lh < posRect.ly){
			tipRect.ly += 5;
			if(tipRect.ly+tipRect.lh > posRect.ly)
				tipRect.ly = posRect.ly-tipRect.lh;
			model.setViewChanged();
			delay(100);
		}
		
		while(tipRect.lx < posRect.lx){
			tipRect.lx += 5;
			if(tipRect.lx > posRect.lx)
				tipRect.lx = posRect.lx;
			model.setViewChanged();
			delay(100);
		}
	}
	
	private void tipForHadSearched(DsListRect tipRect, DsListRect deleteRect){
		boolean flag = true;
		for(int k=1; k<=4; ++k){
			model.setViewChanged();
			if(tipRect != null)
				tipRect.color = flag ? Color.GREEN : null;
			if(deleteRect != null)
				deleteRect.color = flag ? Color.GREEN : null;
			flag = !flag;
			delay(500);
		}
	}
	
	public void showListDelete(int pos){
		if(pos<1 || pos>nodeList.size())
			throw new IllegalArgumentException("数组删除位置不合法!");
		ArrayList<Shape> shapeList = model.getShapeList();
		//找到位置
		DsListRect posRect = nodeList.get(pos-1).shape;
		DsListRect beginRect = nodeList.get(0).shape;
		DsListRect tipRect = new DsListRect(beginRect.lx-beginRect.lw, beginRect.ly-beginRect.lh, beginRect.lw, beginRect.lh, "↓");
		tipRect.isDrawColLine = false;
		shapeList.add(tipRect);
		searchPosition(tipRect, posRect);
		
		DsListRect deleteRect = new DsListRect(posRect.lx, posRect.ly+posRect.lh, posRect.lw, posRect.lh, "×");
		deleteRect.isDrawColLine = false;
		shapeList.add(deleteRect);
		tipForHadSearched(tipRect, deleteRect);
		
		shapeList.remove(tipRect);
		shapeList.remove(deleteRect);
		model.setViewChanged();
		
		DsListRect curRect = nodeList.get(pos-1).shape;
		ArrayList<DsLine> curLines = new ArrayList<DsLine>(), copyLines = new ArrayList<DsLine>();
		if(pos > 1)
			curLines.addAll(nodeList.get(pos-2).lineList);
		if(pos != nodeList.size())
			curLines.addAll(nodeList.get(pos-1).lineList);
		for(DsLine line : curLines) 
			copyLines.add((DsLine) line.clone());
		//圆形 旋转
		circleFly(curRect, curLines);
		
		nodeList.remove(pos-1);
		model.setViewChanged();
		//copyLine保存的是移除的边de最开始的位置
		if(copyLines.size()==1 || copyLines.size()==5) return;
		DsLine lineRow1 = new DsLine(copyLines.get(0).x1, copyLines.get(0).y1, copyLines.get(0).x1, copyLines.get(0).y1, true);
		synchronized (Shape.class) {shapeList.add(lineRow1);}
		
		final int offDist = 10, time = 100;
		lineMove(lineRow1, copyLines.get(1), DIR_RIGHT, offDist, time);
		 
		if(copyLines.size() > 2){
			lineRow1.isArrow = false;
			DsLine lineCol1 = new DsLine(lineRow1.x2, lineRow1.y2, lineRow1.x2, lineRow1.y2, true);
			synchronized (Shape.class) {shapeList.add(lineCol1);}
			lineMove(lineCol1, copyLines.get(2), DIR_DOWN, offDist, time);
			lineCol1.isArrow = false;
			
			DsLine lineRow2 = new DsLine(lineCol1.x2, lineCol1.y2, lineCol1.x2, lineCol1.y2, true);
			synchronized (Shape.class) {shapeList.add(lineRow2);}
			lineMove(lineRow2, copyLines.get(3), DIR_LEFT, offDist, time);
			lineRow2.isArrow = false;
			
			DsLine lineCol2 = new DsLine(lineRow2.x2, lineRow2.y2, lineRow2.x2, lineRow2.y2, true);
			synchronized (Shape.class) {shapeList.add(lineCol2);}
			lineMove(lineCol2, copyLines.get(4), DIR_DOWN, offDist, time);
			lineCol2.isArrow = false;
			
			DsLine lineRow3 = new DsLine(lineCol2.x2, lineCol2.y2, lineCol2.x2, lineCol2.y2, true);
			synchronized (Shape.class) {shapeList.add(lineRow3);}
			lineMove(lineRow3, copyLines.get(5), DIR_RIGHT, offDist, time);
		}
	}
	
	private void delay(int time){
		try {
			TimeUnit.MILLISECONDS.sleep(time);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void lineMove(DsLine line1, DsLine line2, int dir, int offDist, int time){
		switch(dir){
			case DIR_UP:
				while(line1.y2 > line2.y2){
					line1.y2 -= offDist;
					if(line1.y2 < line2.y2)
						line1.y2 = line2.y2;
					model.setViewChanged();
					delay(time);
				}
				break;
			case DIR_DOWN:
				while(line1.y2 < line2.y2){
					line1.y2 += offDist;
					if(line1.y2 > line2.y2)
						line1.y2 = line2.y2;
					model.setViewChanged();
					delay(time);
				}
				break;
			case DIR_LEFT:
				while(line1.x2 > line2.x2){
					line1.x2 -= offDist;
					if(line1.x2 < line2.x2)
						line1.x2 = line2.x2;
					model.setViewChanged();
					delay(time);
				}
				break;
			case DIR_RIGHT:
				while(line1.x2 < line2.x2){
					line1.x2 += offDist;
					if(line1.x2 > line2.x2)
						line1.x2 = line2.x2;
					model.setViewChanged();
					delay(time);
				}
				break;
			default:
				break;
		}
	}
	
	LinkedList<ListNode> list1 = null, list2 = null, list3 = null;
	
	//两个链表合并之后 产生 一个链表
	public void showListMergeForOne(String data){
		String[] datas = data.split(";");
		String[] datas1 = datas[0].split(",");
		String[] datas2 = datas[1].split(",");
		if(datas1.length > 0 && datas2.length > 0 && datas1[0].compareTo(datas2[0]) > 0){
			String tmp = datas[0];
			datas[0] = datas[1];
			datas[1] = tmp;
		}
		list1 = createList(null, datas[0], "①", true);
		list2 = createList(null, datas[1], "②", true);
		model.setViewChanged();
		ArrayList<Shape> shapeList = model.getShapeList();
		
		final int arrowLeftOffDist = -100;
		DsTipArrow arrowI = null, arrowJ = null;
		if(list1.size() > 0){
			arrowI = new DsTipArrow(list1.get(0).shape.lx+arrowLeftOffDist, list1.get(0).shape.ly+35, "L①→");
			shapeList.add(arrowI);
		}
		if(list2.size() > 0){
			arrowJ = new DsTipArrow(list2.get(0).shape.lx+arrowLeftOffDist, list2.get(0).shape.ly+35, "L②→");
			shapeList.add(arrowJ);
		}
		
		//这里是为了调用 showListInsert()方法
		nodeList = list1;
		for(int i=0; i<list2.size(); ++i){
			int pos = 0;
			for(int j=0; j<list1.size(); ++j){
				if(list2.get(i).content.compareTo(list1.get(j).content) < 0){
					pos = j+1;
					break;
				}
			}
			if(pos == 0) pos = list1.size()+1;
			//线飞出去
			if(i != list2.size()-1){
				//圆形 旋转
				DsListRect curRect = (DsListRect) list2.get(i).shape.clone();
				ArrayList<DsLine> curLines = list2.get(i).lineList;
				circleFly(curRect, curLines);
			}
			if(arrowJ != null){
				if(i != list2.size()-1)
					arrowJ.x = list2.get(i+1).shape.lx+arrowLeftOffDist;
				else
					shapeList.remove(arrowJ);
			}
			DsListRect beginRect = list1.get(0).shape;
			DsListRect tipRect = new DsListRect(beginRect.lx-beginRect.lw, beginRect.ly-beginRect.lh, beginRect.lw, beginRect.lh, null);
			moveRectTo(list2.get(i).shape, tipRect, 20, 100);
			tipForHadSearched(list2.get(i).shape, null);
			shapeList.remove(list2.get(i).shape);
			
			//将list2链表中的元素插入list1链表中
			showListInsert(list2.get(i).content, pos);
			//调整list1链表, 先得到调整之后的位置
			LinkedList<ListNode> adjustList = adjustList(list1);
			
			int[] dir = {DIR_RIGHT, DIR_DOWN, DIR_LEFT, DIR_DOWN, DIR_RIGHT};
			for(int k=list1.size()-1; k>=0; --k){
				//和调整之前的一样，则不用再进行模拟操作 了！
				if(list1.get(k).shape.lx == adjustList.get(k).shape.lx && list1.get(k).shape.ly == adjustList.get(k).shape.ly)
					break;
				if(k > 0)
					circleFly((DsListRect) list1.get(k-1).shape.clone(), list1.get(k-1).lineList);
				moveRectTo(list1.get(k).shape, adjustList.get(k).shape, 20, 100);
				for(int kk=0; kk<adjustList.get(k).lineList.size(); ++kk){
					DsLine line2 = adjustList.get(k).lineList.get(kk);
					DsLine line1 = new DsLine(line2.x1, line2.y1, line2.x1, line2.y1, true);
					synchronized (Shape.class) {shapeList.add(line1);}
					lineMove(line1, line2, dir[kk], 20, 100);
					synchronized (Shape.class) {
						shapeList.remove(line1);
						shapeList.add(line2);
					}
				}
			}
			for(int k=0; k<list1.size(); ++k){
				shapeList.remove(list1.get(k).shape);
				shapeList.removeAll(list1.get(k).lineList);
				shapeList.add(adjustList.get(k).shape);
				shapeList.addAll(adjustList.get(k).lineList);
			}
			nodeList = list1 = adjustList;
		}
	}
	
	private LinkedList<ListNode> adjustList(LinkedList<ListNode> list){
		LinkedList<ListNode> nodes = new LinkedList<ListNode>();
		int topMargin = ShapeSize.ListModel.TOP_MARGIN;
		int rowHeight = ShapeSize.ListModel.ROW_HEIGHT;
		int left = 0, top = topMargin-rowHeight;
		for(int i=0; i < list.size(); ++i){
			ListNode newNode = new ListNode(list.get(i).content);
			if(i % ShapeSize.ListModel.ROW_NUM == 0){//新的一行的第一个节点
				int preTop = top;//上一行
				top += rowHeight;//下一行
				if(i != 0){//添加行与行之间的折线
					int preRowMidTop = (preTop*2+ShapeSize.ListModel.RECT_HEIGHT)/2;
					int rowAndRowMidTop = (preTop+ShapeSize.ListModel.RECT_HEIGHT+top)/2;
					DsLine lineCol1 = new DsLine(left, preRowMidTop, left, rowAndRowMidTop, false);
					nodes.get(nodes.size()-1).lineList.add(lineCol1);
					DsLine lineRow1 = new DsLine(left, rowAndRowMidTop, ShapeSize.ListModel.LEFT_MARGIN, rowAndRowMidTop, false);
					nodes.get(nodes.size()-1).lineList.add(lineRow1);
					int nextRowMidTop = (top*2+ShapeSize.ListModel.RECT_HEIGHT)/2;
					DsLine lineCol2 = new DsLine(ShapeSize.ListModel.LEFT_MARGIN, rowAndRowMidTop, ShapeSize.ListModel.LEFT_MARGIN, nextRowMidTop, false);
					nodes.get(nodes.size()-1).lineList.add(lineCol2);
					DsLine lineRow2 = new DsLine(ShapeSize.ListModel.LEFT_MARGIN, nextRowMidTop, ShapeSize.ListModel.LEFT_MARGIN+ShapeSize.ListModel.LINE_DIST_X, nextRowMidTop, true);
					nodes.get(nodes.size()-1).lineList.add(lineRow2);
				}
				left = ShapeSize.ListModel.LEFT_MARGIN+ShapeSize.ListModel.LINE_DIST_X;
			} 
			
			DsListRect rect = new DsListRect(left, top, ShapeSize.ListModel.RECT_WIDTH, ShapeSize.ListModel.RECT_HEIGHT, list.get(i).content);
			newNode.shape = rect;
			
			if(i != list.size()-1){//每一个rect的后面都加上一条横线
				left += ShapeSize.ListModel.RECT_WIDTH;
				boolean isArrow = true;
				if((i+1)%ShapeSize.ListModel.ROW_NUM == 0)
					isArrow = false;
				DsLine line = new DsLine(left-ShapeSize.ListModel.SMALL_RECT_WIDTH/2, (ShapeSize.ListModel.RECT_HEIGHT+top*2)/2, left+=ShapeSize.ListModel.LINE_DIST_X, (ShapeSize.ListModel.RECT_HEIGHT+top*2)/2, isArrow);
				newNode.lineList.add(line);
			}
			nodes.add(newNode);
		}
		return nodes;
	}
	
	private void circleFly(DsListRect curRect, ArrayList<DsLine> curLines){
		int angle = 0;
		int orgX = curRect.lx;
		int orgY = curRect.ly;
		final int R = 120;
		while(angle <= 360){
			double x = Math.PI*angle/180;
			double sinx = Math.sin(x);
			double cosx = Math.cos(x);
			
			curRect.ly = (int) (orgY+R*(1-cosx));
			curRect.lx = (int) (orgX+R*sinx);
			
			for(DsLine line : curLines){
				line.x1 += curRect.lx-orgX;
				line.x2 += curRect.lx-orgX;
				line.y1 += curRect.ly-orgY;
				line.y2 += curRect.ly-orgY;
			}
			angle += 20;
			model.setViewChanged();
			delay(100);
			for(DsLine line : curLines){
				line.x1 -= curRect.lx-orgX;
				line.x2 -= curRect.lx-orgX;
				line.y1 -= curRect.ly-orgY;
				line.y2 -= curRect.ly-orgY;
			}
		}
		final int dist = 50;
		while(curRect.ly > -200){
			curRect.lx += dist;
			curRect.ly -= dist;
			for(DsLine line : curLines){
				line.x1 += dist;
				line.x2 += dist;
				line.y1 -= dist;
				line.y2 -= dist;
			}
			model.setViewChanged();
			delay(100);
		}
		model.getShapeList().removeAll(curLines);
	}
	
	
	//合并之后有三个链表
	/**
	 * @param data1
	 * @param data2	两个有序的链表值
	 */
	public void showListMergeForThree(String data){
		String[] datas = data.split(";");
		DrawPanel panel = model.getObserverPanel();
		list1 = createList(panel, datas[0], "①", true);
		list2 = createList(panel, datas[1], "②", true);
		StringBuilder data3 = new StringBuilder();
		{
			String[] datas1 = datas[0].split(",");
			String[] datas2 = datas[1].split(",");
			int i, j;
			for(i=0, j=0; i<datas1.length && j<datas2.length;){
				if(datas1[i].compareTo(datas2[j]) <= 0){
					data3.append(datas1[i]+",");
					++i;
				} else {
					data3.append(datas2[j]+",");
					++j;
				}
			}
			for(; i<datas1.length; ++i)
				data3.append(datas1[i]+",");
			for(; j<datas2.length; ++j)
				data3.append(datas2[j]+",");
		}
		list3 = createList(panel, data3.length()>1 ? data3.substring(0, data3.length()-1) : "", "③", false);
	}
	
	public void beginListMergeForThree(){
		ArrayList<Shape> shapeList = model.getShapeList();
		int i, j, k = 0;//分别是list1， list2， list3的下标索引
		int offDist = 0, time = 0;
		DsTipArrow arrowI = null, arrowJ = null;
		for(i=0, j=0; i<list1.size() || j<list2.size(); ){
			if(i==0 && j==0){
				arrowI = new DsTipArrow(list1.get(0).shape.lx, list1.get(0).shape.ly, "↘");
				arrowJ = new DsTipArrow(list2.get(0).shape.lx, list2.get(0).shape.ly, "↘");
				shapeList.add(arrowI);
				shapeList.add(arrowJ);
			}
			offDist = 20; time = 100;
			DsListRect rect = null;
			if(i<list1.size() && j<list2.size()){
				arrowI.x = list1.get(i).shape.lx;
				arrowI.y = list1.get(i).shape.ly;
				arrowJ.x = list2.get(j).shape.lx;
				arrowJ.y = list2.get(j).shape.ly;
				tipForHadSearched(list1.get(i).shape, list2.get(j).shape);
				if(list1.get(i).content.compareTo(list2.get(j).content) < 0){
					rect = (DsListRect) list1.get(i).shape.clone();
					++i;
				} else {
					rect = (DsListRect) list2.get(j).shape.clone();
					++j;
				}
			} else {
				if(i < list1.size()){
					rect = (DsListRect) list1.get(i).shape.clone();
					arrowI.x = list1.get(i).shape.lx;
					arrowI.y = list1.get(i).shape.ly;
					tipForHadSearched(list1.get(i).shape, null);
					++i;
				} else {
					rect = (DsListRect) list2.get(j).shape.clone();
					arrowJ.x = list2.get(j).shape.lx;
					arrowJ.y = list2.get(j).shape.ly;
					tipForHadSearched(list2.get(j).shape, null);
					++j;
				}
			}
			rect.color = Color.GREEN;
			rect.isDrawColLine = false;
			shapeList.add(rect);
			moveRectTo(rect, list3.get(k).shape, offDist, time);
			tipForHadSearched(rect, null);
			shapeList.remove(rect);
			shapeList.add(list3.get(k).shape);
			model.setViewChanged();
			if(k > 0){
				offDist = 10; time = 100;
				ArrayList<DsLine> lines = list3.get(k-1).lineList;
				int[] dir = {DIR_RIGHT, DIR_DOWN, DIR_LEFT, DIR_DOWN, DIR_RIGHT};
				for(int x=0; x<lines.size(); ++x){
					DsLine line2 = lines.get(x); 
					DsLine line1 = new DsLine(line2.x1, line2.y1, line2.x1, line2.y1, true);
					synchronized (Shape.class) {shapeList.add(line1);}
					lineMove(line1, line2, dir[x], offDist, time);
					shapeList.remove(line1);
					synchronized (Shape.class) {shapeList.add(line2);}
				}
			}
			++k;
		}
		shapeList.remove(arrowI);
		shapeList.remove(arrowJ);
		model.setViewChanged();
	}
	
	private void moveRectTo(DsListRect from, DsListRect to, int offDist, int time){
		if(from.ly < to.ly){
			while(from.ly < to.ly){
				from.ly += offDist;
				if(from.ly > to.ly)
					from.ly = to.ly;
				model.setViewChanged();
				delay(time);
			}
		} else {
			while(from.ly > to.ly){
				from.ly -= offDist;
				if(from.ly < to.ly)
					from.ly = to.ly;
				model.setViewChanged();
				delay(time);
			}
		}
		if(from.lx < to.lx){
			while(from.lx < to.lx){
				from.lx += offDist;
				if(from.lx > to.lx)
					from.lx = to.lx;
				model.setViewChanged();
				delay(time);
			}
		} else {
			while(from.lx > to.lx){
				from.lx -= offDist;
				if(from.lx < to.lx)
					from.lx = to.lx;
				model.setViewChanged();
				delay(time);
			}
		}
	}

	class MyTask extends TimerTask{
		private JLabel label;
		private boolean flag = true;
		@Override
		public void run() {
			if(flag)
				label.setForeground(Color.BLACK);
			else
				label.setForeground(Color.GREEN);
			flag = !flag;
			label.repaint();
		}

		public MyTask(JLabel label) {
			super();
			this.label = label;
		}
	}
	
	private int listTop = -1;
	
	private LinkedList<ListNode> createList(DrawPanel panel, String data, String listIndex, boolean isAdd){
		if(panel != null) {
			JLabel label = new JLabel("List" + listIndex);
			label.setFont(new Font("华文行楷", Font.BOLD, 30));
			label.setBounds(ShapeSize.ListModel.LEFT_MARGIN, listTop < 0 ? ShapeSize.ListModel.TOP_MARGIN/2 : listTop+ShapeSize.ListModel.ROW_HEIGHT/2, ShapeSize.ListModel.RECT_WIDTH+10, ShapeSize.ListModel.RECT_HEIGHT);
			panel.add(label);
			
			Timer timer = new Timer();
			timer.schedule(new MyTask(label), 1000, 1000);
		}
		LinkedList<ListNode> nodes = new LinkedList<ListNode>();
		ArrayList<Shape> shapeList = model.getShapeList();
		String[] contents = data.split(",");
		//panel != null ? 两个链表合并成一个链表 ： 两个链表合并，最后变成三个链表
		int topMargin = panel != null ? ShapeSize.ListModel.TOP_MARGIN/2 : ShapeSize.ListModel.TOP_MARGIN;
		int rowHeight = panel != null ? ShapeSize.ListModel.ROW_HEIGHT/2 : ShapeSize.ListModel.ROW_HEIGHT;
		
		int left = 0, top = listTop < 0 ? topMargin-rowHeight : listTop;
		for(int i=0; i<contents.length; ++i){
			ListNode newNode = new ListNode(contents[i]);
			if(i % ShapeSize.ListModel.ROW_NUM == 0){//新的一行的第一个节点
				int preTop = top;//上一行
				top += rowHeight;//下一行
				if(i != 0){//添加行与行之间的折线
					int preRowMidTop = (preTop*2+ShapeSize.ListModel.RECT_HEIGHT)/2;
					int rowAndRowMidTop = (preTop+ShapeSize.ListModel.RECT_HEIGHT+top)/2;
					DsLine lineCol1 = new DsLine(left, preRowMidTop, left, rowAndRowMidTop, false);
					if(isAdd) shapeList.add(lineCol1);
					nodes.get(nodes.size()-1).lineList.add(lineCol1);
					DsLine lineRow1 = new DsLine(left, rowAndRowMidTop, ShapeSize.ListModel.LEFT_MARGIN, rowAndRowMidTop, false);
					if(isAdd) shapeList.add(lineRow1);
					nodes.get(nodes.size()-1).lineList.add(lineRow1);
					int nextRowMidTop = (top*2+ShapeSize.ListModel.RECT_HEIGHT)/2;
					DsLine lineCol2 = new DsLine(ShapeSize.ListModel.LEFT_MARGIN, rowAndRowMidTop, ShapeSize.ListModel.LEFT_MARGIN, nextRowMidTop, false);
					if(isAdd) shapeList.add(lineCol2);
					nodes.get(nodes.size()-1).lineList.add(lineCol2);
					DsLine lineRow2 = new DsLine(ShapeSize.ListModel.LEFT_MARGIN, nextRowMidTop, ShapeSize.ListModel.LEFT_MARGIN+ShapeSize.ListModel.LINE_DIST_X, nextRowMidTop, true);
					if(isAdd) shapeList.add(lineRow2);
					nodes.get(nodes.size()-1).lineList.add(lineRow2);
				}
				left = ShapeSize.ListModel.LEFT_MARGIN+ShapeSize.ListModel.LINE_DIST_X;
			} 
			
			DsListRect rect = new DsListRect(left, top, ShapeSize.ListModel.RECT_WIDTH, ShapeSize.ListModel.RECT_HEIGHT, contents[i]);
			if(isAdd) shapeList.add(rect);
			newNode.shape = rect;
			
			if(i != contents.length-1){//每一个rect的后面都加上一条横线
				left += ShapeSize.ListModel.RECT_WIDTH;
				boolean isArrow = true;
				if((i+1)%ShapeSize.ListModel.ROW_NUM == 0)
					isArrow = false;
				DsLine line = new DsLine(left-ShapeSize.ListModel.SMALL_RECT_WIDTH/2, (ShapeSize.ListModel.RECT_HEIGHT+top*2)/2, left+=ShapeSize.ListModel.LINE_DIST_X, (ShapeSize.ListModel.RECT_HEIGHT+top*2)/2, isArrow);
				if(isAdd) shapeList.add(line);
				newNode.lineList.add(line);
			}
			nodes.add(newNode);
		}
		listTop = top;
		return nodes;
	}
	
	
	public ListModel(DrawModel model) {
		super();
		this.model = model;
	}
}
