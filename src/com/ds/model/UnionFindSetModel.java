package com.ds.model;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import com.ds.shape.DsCircle;
import com.ds.shape.DsLine;
import com.ds.shape.DsRect;
import com.ds.shape.Shape;
import com.ds.size.ShapeSize;

public class UnionFindSetModel {
	private DrawModel model;
	private ArrayList<Shape> shapeList;
	
	private Map<String, String> f = new TreeMap<String, String>();
	private Map<String, ForestNode> setNodes = new TreeMap<String, ForestNode>();
	private ArrayList<ForestNode> roots = null;
	private String find(String x){
		if(!x.equals(f.get(x))){
			String pf = f.get(x);
			String sf = find(f.get(x));
			setNodes.get(pf).childList.remove(setNodes.get(x));
			setNodes.get(sf).childList.add(setNodes.get(x));
			f.put(x, sf);
		}
		return f.get(x);
	}
	
	private void repaintTree(){
		leftMargin = -1;
		forestNodeCnt = 0;
		forestRight = -1;
		maxLevel = -1;
		shapeList.clear();
		graphicUnionFindSetModel(roots, 1, true);
		model.setViewChanged();
	}
	
	private void delay(int time){
		try {
			TimeUnit.MILLISECONDS.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void moveChildrenForest(ForestNode fxNode, int offDistY){
		fxNode.shape.ly += offDistY;
		for(DsLine line : fxNode.lineList){
			line.y1 += offDistY;
			line.y2 += offDistY;
		}
		for(ForestNode childNode : fxNode.childList)
			moveChildrenForest(childNode, offDistY);
	}
	
	private void union(String x, String y){
		String fx = find(x);
		String fy = find(y);
		if(!fx.equals(fy)){
			//fx对应的子树下滑一段距离
			ForestNode fxNode = setNodes.get(fx);
			final int offDistY = 5;
			for(int i=1; i<=10; ++i){
				moveChildrenForest(fxNode, offDistY);
				model.setViewChanged();
				delay(100);
			}
			DsLine line2 = addForestLine(setNodes.get(fy), setNodes.get(fx), false);
			DsLine line1 = new DsLine(line2.x1, line2.y1, line2.x1, line2.y1, false);
			line1.color = Color.RED;
			synchronized (Shape.class) { shapeList.add(0, line1); }
			lineMove(line1, line2);
			boolean flag = true;
			for(int i=1; i<=4; ++i){
				if(!flag) line1.color = Color.RED;
				else line1.color = Color.GREEN;
				flag = !flag;
				model.setViewChanged();
				delay(500);
			}
			f.put(fx, fy);
			setNodes.get(fy).childList.add(setNodes.get(fx));
			roots.remove(setNodes.get(fx));
			repaintTree();
		}
	}
	
	private void lineMove(DsLine line1, DsLine line2){
		double k = (1.0*line2.y2-line2.y1)/(line2.x2-line2.x1);
		double b = line2.y1-k*line2.x1;
		int offDist = 10;
		if(line1.x2 < line2.x2){
			while(line1.x2 < line2.x2){
				line1.x2 += offDist;
				if(line1.x2 > line2.x2){
					line1.x2 = line2.x2;
					line1.y2 = line2.y2;
				}
				line1.y2 = (int) (k*line1.x2 + b);
				model.setViewChanged();
				delay(100);
			}
		} else {
			while(line1.x2 > line2.x2){
				line1.x2 -= offDist;
				if(line1.x2 < line2.x2){
					line1.x2 = line2.x2;
					line1.y2 = line2.y2;
				}
				line1.y2 = (int) (k*line1.x2 + b);
				model.setViewChanged();
				delay(100);
			}
		}
	}
	
	/**
	 * @param data 节点列表； 节点关系
	 */
	public void unionFindSet(String data){
		String[] datas = data.split(";");
		roots = new ArrayList<ForestNode>();
		String[] nodes = datas[0].split(" ");
		for(String node : nodes){
			f.put(node, node);
			roots.add(new ForestNode(node));
			setNodes.put(node, roots.get(roots.size()-1));
		}
		graphicUnionFindSetModel(roots, 1, true);
		
		String[] relates = datas[1].split(",");
		for(String relate : relates){
			String[] xy = relate.split(" ");
			union(xy[0], xy[1]);
			delay(1000);
		}
		model.setViewChanged();
	}
	
	private int leftMargin = -1, forestNodeCnt = 0, forestRight = -1, maxLevel = -1;
	
	private int graphicUnionFindSetModel(ArrayList<ForestNode> roots, int level, boolean isAdd){
		if(maxLevel < level) maxLevel = level;
		int allNodes = 0;
		for(ForestNode root : roots){
			int curRootChilds;
			if(root.childList.size() == 0){//叶子节点
				++forestNodeCnt;
				int circleLeft = leftMargin < 0 ? ShapeSize.UnionFindSetModel.LEFT_MARGIN : leftMargin; 
				circleLeft += (forestNodeCnt-1)*(ShapeSize.UnionFindSetModel.NODES_HOR_DIST + ShapeSize.UnionFindSetModel.CIRCLE_WIDTH);
				int circleTop = ShapeSize.UnionFindSetModel.TOP_MARGIN;
				circleTop += (level-1)*(ShapeSize.UnionFindSetModel.CIRCLE_HEIGHT + ShapeSize.UnionFindSetModel.LEVEL_DIST);
				
				DsCircle shapeCircle = new DsCircle(circleLeft, circleTop, ShapeSize.UnionFindSetModel.CIRCLE_WIDTH, ShapeSize.UnionFindSetModel.CIRCLE_HEIGHT, root.content);
				if(isAdd) shapeList.add(shapeCircle);
				root.shape = shapeCircle;
				
				if(forestRight < circleLeft+ShapeSize.UnionFindSetModel.CIRCLE_WIDTH)
					forestRight = circleLeft+ShapeSize.UnionFindSetModel.CIRCLE_WIDTH;
				curRootChilds = 1;
			} else {//非叶子节点
				curRootChilds = 1+graphicUnionFindSetModel(root.childList, level+1, isAdd);
				int circleLeft = (root.childList.get(0).shape.lx + root.childList.get(root.childList.size()-1).shape.lx)/2;
				int circleTop = ShapeSize.UnionFindSetModel.TOP_MARGIN + (level-1)*(ShapeSize.UnionFindSetModel.CIRCLE_HEIGHT + ShapeSize.UnionFindSetModel.LEVEL_DIST);
				DsCircle shapeCircle = new DsCircle(circleLeft, circleTop, ShapeSize.UnionFindSetModel.CIRCLE_WIDTH, ShapeSize.UnionFindSetModel.CIRCLE_HEIGHT, root.content);
				if(isAdd) shapeList.add(shapeCircle);
				root.shape = shapeCircle;
				
				//添加连线
				for(ForestNode child : root.childList) 
					addForestLine(root, child, isAdd);
			}
			allNodes += curRootChilds;
			if(level == 1){//绘制这棵树一共有多少个节点
				DsRect rect = new DsRect(root.shape.lx, root.shape.ly-ShapeSize.UnionFindSetModel.LEVEL_DIST-root.shape.lh, root.shape.lw, root.shape.lh, String.valueOf(curRootChilds));
				DsLine line = new DsLine(root.shape.lx+root.shape.lw/2, root.shape.ly, root.shape.lx+root.shape.lw/2, root.shape.ly-ShapeSize.UnionFindSetModel.LEVEL_DIST, false);
				shapeList.add(line);
				shapeList.add(rect);
			}
		}
		return allNodes;
	}
	
	private DsLine addForestLine(ForestNode T, ForestNode Tchild, boolean isAdd){
		int x1 = T.shape.lx + T.shape.lw/2;
		int y1 = T.shape.ly + T.shape.lh/2;
		int x2 = Tchild.shape.lx + Tchild.shape.lw/2;
		int y2 = Tchild.shape.ly + Tchild.shape.lh/2;
		
		if(x1 == x2){
			DsLine line = new DsLine(x1, y1+ShapeSize.UnionFindSetModel.CIRCLE_HEIGHT/2, x2, y2-ShapeSize.UnionFindSetModel.CIRCLE_HEIGHT/2, false);
			if(isAdd) shapeList.add(line);
			T.lineList.add(line);
			return line;
		}
		
		//特殊处理， 不让线段画进  树结点的里面， (x1, y1), (x2, y2), (x1, y2)三点组成三角形，然后有
		// (x1, y1)和 (x2, y2)线段的长度为L， 则有 x1+L*sin@ = x2, y1+L*cos@ = y2;
		double L = Math.sqrt((double)((x2-x1)*(x2-x1) + (y2-y1)*(y2-y1)));
		double sinx = (x2-x1)/L/2;//除以2，因为ShapeSize.UnionFindSetModel.CIRCLE_WIDTH是直径，我们要半径
		double cosx = (y2-y1)/L/2;
		x1 = (int)(x1+ShapeSize.UnionFindSetModel.CIRCLE_WIDTH*sinx);
		y1 = (int)(y1+ShapeSize.UnionFindSetModel.CIRCLE_WIDTH*cosx);
		x2 = (int)(x2-ShapeSize.UnionFindSetModel.CIRCLE_WIDTH*sinx);
		y2 = (int)(y2-ShapeSize.UnionFindSetModel.CIRCLE_WIDTH*cosx);
		DsLine line = new DsLine(x1, y1, x2, y2, false);
		if(isAdd) shapeList.add(line);
		T.lineList.add(line);
		return line;
	}
	
	public UnionFindSetModel(DrawModel model) {
		super();
		this.model = model;
		shapeList = model.getShapeList();
	}
}
