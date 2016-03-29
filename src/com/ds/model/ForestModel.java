package com.ds.model;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JViewport;


import com.ds.panel.DrawPanel;
import com.ds.shape.DsCircle;
import com.ds.shape.DsLine;
import com.ds.shape.DsTipArrow;
import com.ds.shape.Shape;
import com.ds.size.ShapeSize;

public class ForestModel {
	private DrawModel model;
	private ArrayList<Shape> shapeList;
	//森林的树根
	private ArrayList<ForestNode> buildForest(String[] contents){
		 ArrayList<ForestNode> roots = new ArrayList<ForestNode>();
		 Map<String, ForestNode> mp = new TreeMap<String, ForestNode>();
		 Map<String, Integer> inDeg = new TreeMap<String, Integer>();
		 for(int i=0; i<contents.length; ++i){
			 String[] uv = contents[i].split(" ");//节点u和节点v有边
			 if(!mp.containsKey(uv[0]))
				 mp.put(uv[0], new ForestNode(uv[0]));
			 if(!mp.containsKey(uv[1]))
				 mp.put(uv[1], new ForestNode(uv[1]));
			 mp.get(uv[0]).childList.add(mp.get(uv[1]));
			 
			 //节点入度的计算
			 if(!inDeg.containsKey(uv[0]))
				 inDeg.put(uv[0], 0);
			 
			 if(!inDeg.containsKey(uv[1]))
				 inDeg.put(uv[1], 1);
			 else 
				 inDeg.put(uv[1], inDeg.get(uv[1])+1);
		 }
		 
		 //森林中可能有多棵子森林
		 for(String node : inDeg.keySet())
			 if(inDeg.get(node) == 0)
				 roots.add(mp.get(node));
		 return roots;
	}
	
	private void addForestLine(ForestNode T, ForestNode Tchild, boolean isAdd){
		int x1 = T.shape.lx + T.shape.lw/2;
		int y1 = T.shape.ly + T.shape.lh/2;
		int x2 = Tchild.shape.lx + Tchild.shape.lw/2;
		int y2 = Tchild.shape.ly + Tchild.shape.lh/2;
		
		if(x1 == x2){
			DsLine line = new DsLine(x1, y1+ShapeSize.ForestModel.CIRCLE_HEIGHT/2, x2, y2-ShapeSize.ForestModel.CIRCLE_HEIGHT/2, false);
			if(isAdd) shapeList.add(line);
			T.lineList.add(line);
			return ;
		}
		
		//特殊处理， 不让线段画进  树结点的里面， (x1, y1), (x2, y2), (x1, y2)三点组成三角形，然后有
		// (x1, y1)和 (x2, y2)线段的长度为L， 则有 x1+L*sin@ = x2, y1+L*cos@ = y2;
		double L = Math.sqrt((double)((x2-x1)*(x2-x1) + (y2-y1)*(y2-y1)));
		double sinx = (x2-x1)/L/2;//除以2，因为ShapeSize.ForestModel.CIRCLE_WIDTH是直径，我们要半径
		double cosx = (y2-y1)/L/2;
		x1 = (int)(x1+ShapeSize.ForestModel.CIRCLE_WIDTH*sinx);
		y1 = (int)(y1+ShapeSize.ForestModel.CIRCLE_WIDTH*cosx);
		x2 = (int)(x2-ShapeSize.ForestModel.CIRCLE_WIDTH*sinx);
		y2 = (int)(y2-ShapeSize.ForestModel.CIRCLE_WIDTH*cosx);
		DsLine line = new DsLine(x1, y1, x2, y2, false);
		if(isAdd) shapeList.add(line);
		T.lineList.add(line);
	}
	
	private int leftMargin = -1, forestNodeCnt = 0, bTreeNodeCnt = 0;
	
	private void graphicForestModel(ArrayList<ForestNode> roots, int level, boolean isAdd){
		if(maxLevel < level) maxLevel = level;
		for(ForestNode root : roots){
			if(root.childList.size() == 0){//叶子节点
				++forestNodeCnt;
				int circleLeft = leftMargin < 0 ? ShapeSize.ForestModel.LEFT_MARGIN : leftMargin; 
				circleLeft += (forestNodeCnt-1)*(ShapeSize.ForestModel.NODES_HOR_DIST + ShapeSize.ForestModel.CIRCLE_WIDTH);
				int circleTop = ShapeSize.ForestModel.TOP_MARGIN;
				circleTop += (level-1)*(ShapeSize.ForestModel.CIRCLE_HEIGHT + ShapeSize.ForestModel.LEVEL_DIST);
				
				DsCircle shapeCircle = new DsCircle(circleLeft, circleTop, ShapeSize.ForestModel.CIRCLE_WIDTH, ShapeSize.ForestModel.CIRCLE_HEIGHT, root.content);
				if(isAdd) shapeList.add(shapeCircle);
				root.shape = shapeCircle;
				
				if(forestRight < circleLeft+ShapeSize.ForestModel.CIRCLE_WIDTH)
					forestRight = circleLeft+ShapeSize.ForestModel.CIRCLE_WIDTH;
			} else {//非叶子节点
				graphicForestModel(root.childList, level+1, isAdd);
				int circleLeft = (root.childList.get(0).shape.lx + root.childList.get(root.childList.size()-1).shape.lx)/2;
				int circleTop = ShapeSize.ForestModel.TOP_MARGIN + (level-1)*(ShapeSize.ForestModel.CIRCLE_HEIGHT + ShapeSize.ForestModel.LEVEL_DIST);
				DsCircle shapeCircle = new DsCircle(circleLeft, circleTop, ShapeSize.ForestModel.CIRCLE_WIDTH, ShapeSize.ForestModel.CIRCLE_HEIGHT, root.content);
				if(isAdd) shapeList.add(shapeCircle);
				root.shape = shapeCircle;
				
				//添加连线
				for(ForestNode child : root.childList) 
					addForestLine(root, child, isAdd);
			}
		}
	}
	

	private void graphicBTreeModel(TreeNode T, int level, boolean isAdd){
		ArrayList<Shape> shapeList = model.getShapeList();
		if(T == null) return;
		if(maxLevel < level) maxLevel = level;
		graphicBTreeModel(T.lchild, level+1, isAdd);
		++bTreeNodeCnt;
		int circleLeft = ShapeSize.ForestModel.CIRCLE_WIDTH;
		circleLeft += (bTreeNodeCnt-1)*(ShapeSize.ForestModel.CIRCLE_WIDTH) + forestRight;
		int circleTop = ShapeSize.ForestModel.TOP_MARGIN;
		circleTop += (level-1)*(ShapeSize.ForestModel.CIRCLE_HEIGHT + ShapeSize.ForestModel.LEVEL_DIST);
		graphicBTreeModel(T.rchild, level+1, isAdd);
		
		DsCircle shapeCircle = new DsCircle(circleLeft, circleTop, ShapeSize.ForestModel.CIRCLE_WIDTH, ShapeSize.ForestModel.CIRCLE_HEIGHT, T.content);
		if(isAdd) shapeList.add(shapeCircle);
		T.shape = shapeCircle;
		
		if(T.lchild != null)
			addTreeLine(T, T.lchild, isAdd);
			
		if(T.rchild != null) 
			addTreeLine(T, T.rchild, isAdd);
	}
	
	private void addTreeLine(TreeNode T, TreeNode Tchild, boolean isAdd){
		int x1 = T.shape.lx + T.shape.lw/2;
		int y1 = T.shape.ly + T.shape.lh/2;
		int x2 = Tchild.shape.lx + Tchild.shape.lw/2;
		int y2 = Tchild.shape.ly + Tchild.shape.lh/2;
		//特殊处理， 不让线段画进  树结点的里面， (x1, y1), (x2, y2), (x1, y2)三点组成三角形，然后有
		// (x1, y1)和 (x2, y2)线段的长度为L， 则有 x1+L*sin@ = x2, y1+L*cos@ = y2;
		double L = Math.sqrt((double)((x2-x1)*(x2-x1) + (y2-y1)*(y2-y1)));
		double sinx = (x2-x1)/L/2;//除以2，因为ShapeSize.ForestModel.CIRCLE_WIDTH是直径，我们要半径
		double cosx = (y2-y1)/L/2;
		x1 = (int)(x1+ShapeSize.ForestModel.CIRCLE_WIDTH*sinx);
		y1 = (int)(y1+ShapeSize.ForestModel.CIRCLE_WIDTH*cosx);
		x2 = (int)(x2-ShapeSize.ForestModel.CIRCLE_WIDTH*sinx);
		y2 = (int)(y2-ShapeSize.ForestModel.CIRCLE_WIDTH*cosx);
		DsLine line = new DsLine(x1, y1, x2, y2, false);
		if(isAdd) shapeList.add(line);
		T.lineList.add(line);
	}
	
	private TreeNode forestToBTree(ArrayList<ForestNode> roots){
		TreeNode bTRoot = null, bTRchild = null;
		for(int i=0; i<roots.size(); ++i){
			TreeNode newNode = new TreeNode();
			newNode.content = roots.get(i).content;
			if(i == 0){
				bTRoot = newNode;
				bTRchild = bTRoot;
			} else {
				bTRchild.rchild = newNode;
				bTRchild = bTRchild.rchild;
			}
			bTRchild.lchild = forestToBTree(roots.get(i).childList);
		}
		return bTRoot;
	}
	
	private void delay(int time){
		try {
			TimeUnit.MILLISECONDS.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void tipForSearched(DsCircle circle){
		boolean flag = true;
		for(int k=1; k<=4; ++k){
			if(flag)
				circle.color = Color.GREEN;
			else
				circle.color = Color.RED;
			flag = !flag;
			model.setViewChanged();
			delay(500);
		}
	}
	
	private void lineMove(DsLine line1, DsLine line2){
		double k = (1.0*line2.y2-line2.y1)/(line2.x2-line2.x1);
		double b = line2.y1-k*line2.x1;
		int offDist = 20;
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
	
	private TreeNode forestToBTreeDisplay(ArrayList<ForestNode> roots, TreeNode bTRoot, DsTipArrow arrow){
		TreeNode bTRchild = null;
		for(int i=0; i<roots.size(); ++i){
			if(i == 0){
				bTRchild = bTRoot;
			} else {
				bTRchild = bTRchild.rchild;
			}
			arrow.x = roots.get(i).shape.lx;
			arrow.y = roots.get(i).shape.ly;
			
			adjustView((JScrollPane)model.getObserverPanel().getParent().getParent(), roots.get(i).shape.lx, roots.get(i).shape.ly);
			
			tipForSearched(roots.get(i).shape);
			synchronized(Shape.class){shapeList.add(bTRchild.shape);}
			adjustView((JScrollPane)model.getObserverPanel().getParent().getParent(), bTRchild.shape.lx, bTRchild.shape.ly);
			tipForSearched(bTRchild.shape);
			for(DsLine line2 : bTRchild.lineList){
				DsLine line1 = new DsLine(line2.x1, line2.y1, line2.x1, line2.y1, false);
				synchronized(Shape.class){shapeList.add(line1);}
				lineMove(line1, line2);
				synchronized(Shape.class){
					shapeList.remove(line1);
					shapeList.add(line2);
				}
			}
			bTRchild.lchild = forestToBTreeDisplay(roots.get(i).childList, bTRchild.lchild, arrow);
		}
		return bTRoot;
	}
	
	private int forestRight = -1;//记录森林的右边距
	private int maxLevel = 0;//记录森林和二叉树两者的最高层次
	public void showForestToBTree(String data){
		DrawPanel panel = model.getObserverPanel();
		String[] contents = data.split(";");
		ArrayList<ForestNode> roots = buildForest(contents);
		graphicForestModel(roots, 1, true);
		TreeNode bTRoot = forestToBTree(roots);//得到二叉树的根节点 
		graphicBTreeModel(bTRoot, 1, false);//得到二叉树的模型
		
		int panelWidth = (bTreeNodeCnt+1)*(ShapeSize.ForestModel.CIRCLE_WIDTH) + forestRight + ShapeSize.ForestModel.LEFT_MARGIN;
		int panelHeight = maxLevel * (ShapeSize.ForestModel.CIRCLE_HEIGHT + ShapeSize.ForestModel.LEVEL_DIST);
		panel.setPreferredSize(new Dimension(panelWidth, panelHeight));
		DsTipArrow arrow = new DsTipArrow(-100, -100, "↘");
		shapeList.add(arrow);
		forestToBTreeDisplay(roots, bTRoot, arrow);
		shapeList.remove(arrow);
		model.setViewChanged();
	}
	
	public void showFroestCreataData(String data){
		String[] contents = data.split(";");
		ArrayList<ForestNode> roots = buildForest(contents);
		delay(100);
		graphicForestModel(roots, 1, true);
	}

	public ForestModel(DrawModel model) {
		super();
		this.model = model;
		shapeList = model.getShapeList();
	}
	
	private int primTreeLeft;
	private int primTreeTop;
	
	public void setPrimTreeLeft(int primTreeLeft){
		this.primTreeLeft = primTreeLeft;
	}
	
	public void setPrimTreeTop(int primTreeTop){
		this.primTreeTop = primTreeTop;
	}
	
	private void graphicForestModel(ArrayList<ForestNode> roots, int level, PrimTreeNodeNeed primTreeNodeNeed){
		if(maxLevel < level) maxLevel = level;
		for(ForestNode root : roots){
			if(root.childList.size() == 0){//叶子节点
				++forestNodeCnt;
				int circleLeft = primTreeLeft; 
				circleLeft += (forestNodeCnt-1)*(ShapeSize.ForestModel.NODES_HOR_DIST + ShapeSize.ForestModel.CIRCLE_WIDTH);
				int circleTop = primTreeTop;
				circleTop += (level-1)*(ShapeSize.ForestModel.CIRCLE_HEIGHT + ShapeSize.ForestModel.LEVEL_DIST);
				
				DsCircle shapeCircle = new DsCircle(circleLeft, circleTop, ShapeSize.ForestModel.CIRCLE_WIDTH, ShapeSize.ForestModel.CIRCLE_HEIGHT, root.content);
				root.shape = shapeCircle;
				
			} else {//非叶子节点
				graphicForestModel(root.childList, level+1, primTreeNodeNeed);
				int circleLeft = (root.childList.get(0).shape.lx + root.childList.get(root.childList.size()-1).shape.lx)/2;
				int circleTop = primTreeTop + (level-1)*(ShapeSize.ForestModel.CIRCLE_HEIGHT + ShapeSize.ForestModel.LEVEL_DIST);
				DsCircle shapeCircle = new DsCircle(circleLeft, circleTop, ShapeSize.ForestModel.CIRCLE_WIDTH, ShapeSize.ForestModel.CIRCLE_HEIGHT, root.content);
				root.shape = shapeCircle;
				
				//添加连线
				for(ForestNode child : root.childList) 
					addForestLine(root, child, primTreeNodeNeed);
			}
			primTreeNodeNeed.nodeToShape.put(root.content, root.shape);
		}
	}
	
	private void addForestLine(ForestNode T, ForestNode Tchild, PrimTreeNodeNeed primTreeNodeNeed){
		int x1 = T.shape.lx + T.shape.lw/2;
		int y1 = T.shape.ly + T.shape.lh/2;
		int x2 = Tchild.shape.lx + Tchild.shape.lw/2;
		int y2 = Tchild.shape.ly + Tchild.shape.lh/2;
		
		if(x1 == x2){
			DsLine line = new DsLine(x1, y1+ShapeSize.ForestModel.CIRCLE_HEIGHT/2, x2, y2-ShapeSize.ForestModel.CIRCLE_HEIGHT/2, false);
			primTreeNodeNeed.nodesLine.put(new TwoNodes(T.content, Tchild.content), line);
			return ;
		}
		
		//特殊处理， 不让线段画进  树结点的里面， (x1, y1), (x2, y2), (x1, y2)三点组成三角形，然后有
		// (x1, y1)和 (x2, y2)线段的长度为L， 则有 x1+L*sin@ = x2, y1+L*cos@ = y2;
		double L = Math.sqrt((double)((x2-x1)*(x2-x1) + (y2-y1)*(y2-y1)));
		double sinx = (x2-x1)/L/2;//除以2，因为ShapeSize.ForestModel.CIRCLE_WIDTH是直径，我们要半径
		double cosx = (y2-y1)/L/2;
		x1 = (int)(x1+ShapeSize.ForestModel.CIRCLE_WIDTH*sinx);
		y1 = (int)(y1+ShapeSize.ForestModel.CIRCLE_WIDTH*cosx);
		x2 = (int)(x2-ShapeSize.ForestModel.CIRCLE_WIDTH*sinx);
		y2 = (int)(y2-ShapeSize.ForestModel.CIRCLE_WIDTH*cosx);
		DsLine line = new DsLine(x1, y1, x2, y2, false);
		primTreeNodeNeed.nodesLine.put(new TwoNodes(T.content, Tchild.content), line);
	}
	
	public PrimTreeNodeNeed getForestModel(String data){
		String[] contents = data.split(";");
		PrimTreeNodeNeed primTreeNodeNeed = new PrimTreeNodeNeed();
		ArrayList<ForestNode> roots = buildForest(contents);
		graphicForestModel(roots, 1, primTreeNodeNeed);
		return primTreeNodeNeed;
	}
}
