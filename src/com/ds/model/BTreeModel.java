package com.ds.model;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JViewport;

import com.ds.panel.DrawPanel;
import com.ds.shape.DsCircle;
import com.ds.shape.DsLine;
import com.ds.shape.Shape;
import com.ds.size.ShapeSize;

//二叉树模型生成
	public class BTreeModel{
		private DrawModel model;
		private ArrayList<Shape> shapeList;
		private TreeNode root = null;
		private int index = 0;
		private int treeNodeCnt = 0;
		/**
		 * @param contents 传入二叉树的先序遍历序列
		 * @return
		 */
		private TreeNode buildBT(String[] contents){
			if(index >= contents.length || contents[index].equals("?")) return null;
			TreeNode node = new TreeNode();
			node.content = contents[index];
			++index;
			node.lchild = buildBT(contents);
			++index;
			node.rchild = buildBT(contents);
			return node;
		}
		
		private void addTreeLine(TreeNode T, TreeNode Tchild, boolean isAdd){
			int x1 = T.shape.lx + T.shape.lw/2;
			int y1 = T.shape.ly + T.shape.lh/2;
			int x2 = Tchild.shape.lx + Tchild.shape.lw/2;
			int y2 = Tchild.shape.ly + Tchild.shape.lh/2;
			//特殊处理， 不让线段画进  树结点的里面， (x1, y1), (x2, y2), (x1, y2)三点组成三角形，然后有
			// (x1, y1)和 (x2, y2)线段的长度为L， 则有 x1+L*sin@ = x2, y1+L*cos@ = y2;
			double L = Math.sqrt((double)((x2-x1)*(x2-x1) + (y2-y1)*(y2-y1)));
			double sinx = (x2-x1)/L/2;//除以2，因为ShapeSize.TreeModel.CIRCLE_WIDTH是直径，我们要半径
			double cosx = (y2-y1)/L/2;
			x1 = (int)(x1+ShapeSize.TreeModel.CIRCLE_WIDTH*sinx);
			y1 = (int)(y1+ShapeSize.TreeModel.CIRCLE_WIDTH*cosx);
			x2 = (int)(x2-ShapeSize.TreeModel.CIRCLE_WIDTH*sinx);
			y2 = (int)(y2-ShapeSize.TreeModel.CIRCLE_WIDTH*cosx);
			DsLine line = new DsLine(x1, y1, x2, y2, false);
			if(isAdd) shapeList.add(line);
			T.lineList.add(line);
		}
		
		/**
		 * @param T	二叉树
		 * @param treeNodeCnt	二叉树从左到右的节点数的累积
		 * @param level	二叉树的层次
		 */
		private void createBTreeModel(TreeNode T, int level, boolean isAdd){
			ArrayList<Shape> shapeList = model.getShapeList();
			if(T == null) return;
			createBTreeModel(T.lchild, level+1, isAdd);
			++treeNodeCnt;
			int circleLeft = ShapeSize.TreeModel.LEFT_MARGIN; 
			circleLeft += (treeNodeCnt-1)*(ShapeSize.TreeModel.NODES_HOR_DIST + ShapeSize.TreeModel.CIRCLE_WIDTH);
			int circleTop = ShapeSize.TreeModel.TOP_MARGIN;
			circleTop += (level-1)*(ShapeSize.TreeModel.CIRCLE_HEIGHT + ShapeSize.TreeModel.LEVEL_DIST);
			createBTreeModel(T.rchild, level+1, isAdd);
			
			DsCircle shapeCircle = new DsCircle(circleLeft, circleTop, ShapeSize.TreeModel.CIRCLE_WIDTH, ShapeSize.TreeModel.CIRCLE_HEIGHT, T.content);
			if(isAdd) shapeList.add(shapeCircle);
			T.shape = shapeCircle;
			
			if(treeBottom < T.shape.ly+T.shape.lh)
				treeBottom = T.shape.ly+T.shape.lh;
			
			if(T.lchild != null)
				addTreeLine(T, T.lchild, isAdd);
				
			if(T.rchild != null) 
				addTreeLine(T, T.rchild, isAdd);
		}
		
		/**
		 * @param data 节点之间的值以' '分割，'?'表示的是空节点
		 */
		public void createBTreeData(String data){
			String[] contents = data.split(" ");
			root = buildBT(contents);
			createBTreeModel(root, 1, true);
		}
		
		private void delay(int time){
			try {
				TimeUnit.MILLISECONDS.sleep(time);
			} catch (InterruptedException e) {
				e.printStackTrace();
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
		
		private void circleMove(DsCircle shape1, DsCircle shape2){
			final int offDistY = 15, offDistX = 10;
			int stopPosY = root.shape.ly+root.shape.lh*3;
			while(shape1.ly < stopPosY){
				shape1.ly += offDistY;
				if(shape1.ly > stopPosY)
					shape1.ly = stopPosY;
				model.setViewChanged();
				delay(100);
			}
			circleFly(shape1);
			
			if(shape1.lx == shape2.lx){
				if(shape1.ly < shape2.ly){
					while(shape1.ly < shape2.ly){
						shape1.ly += offDistY;
						if(shape1.ly > shape2.ly)
							shape1.ly = shape2.ly;
						model.setViewChanged();
						delay(100);
					}
				} else {
					while(shape1.ly > shape2.ly){
						shape1.ly -= offDistY;
						if(shape1.ly < shape2.ly)
							shape1.ly = shape2.ly;
						model.setViewChanged();
						delay(100);
					}
				}
				return;
			}
			
			if(shape1.ly == shape2.ly){
				if(shape1.lx < shape2.lx){
					while(shape1.lx < shape2.lx){
						shape1.lx += offDistY;
						if(shape1.lx > shape2.lx)
							shape1.lx = shape2.lx;
						model.setViewChanged();
						delay(100);
					}
				} else {
					while(shape1.lx > shape2.lx){
						shape1.lx -= offDistY;
						if(shape1.lx < shape2.lx)
							shape1.lx = shape2.lx;
						model.setViewChanged();
						delay(100);
					}
				}
				return;
			}
			
			double k = (1.0*shape2.ly-shape1.ly)/(shape2.lx-shape1.lx);
			double b = shape2.ly-k*shape2.lx;
			if(shape1.lx < shape2.lx){
				while(shape1.lx < shape2.lx){
					shape1.lx += offDistX;
					shape1.ly = (int) (k*shape1.lx + b);
					if(shape1.lx > shape2.lx){
						shape1.lx = shape2.lx;
						shape1.ly = shape2.ly;
					}
					model.setViewChanged();
					delay(100);
				}
			} else {
				while(shape1.lx > shape2.lx){
					shape1.lx -= offDistX;
					shape1.ly = (int) (k*shape1.lx + b);
					if(shape1.lx < shape2.lx){
						shape1.lx = shape2.lx;
						shape1.ly = shape2.ly;
					}
					model.setViewChanged();
					delay(100);
				}
			}
		}
		
		private void circleFly(DsCircle circle){
			int angle = 0;
			int orgX = circle.lx;
			int orgY = circle.ly;
			final int R = 120;
			while(angle <= 360){
				double x = Math.PI*angle/180;
				double sinx = Math.sin(x);
				double cosx = Math.cos(x);
				circle.ly = (int) (orgY+R*(1-cosx));
				circle.lx = (int) (orgX+R*sinx);
				angle += 20;
				model.setViewChanged();
				delay(100);
			}
		}
		
		private void createNewNode(TreeNode T){
			DsCircle shape = (DsCircle) T.shape.clone();
			shape.lx = root.shape.lx+50;
			shape.ly = -60;
			shapeList.add(shape);
			circleMove(shape, T.shape);
			for(DsLine line2 : T.lineList){
				DsLine line1 = new DsLine(line2.x1, line2.y1, line2.x1, line2.y1, false);
				synchronized (Shape.class) {shapeList.add(line1);}
				lineMove(line1, line2);
				synchronized (Shape.class) {
					shapeList.remove(line1);
					shapeList.add(line2);
				}
			}
		}
		
		private void preCreateBTree(TreeNode T){
			if(T == null) return;
			createNewNode(T); 
			preCreateBTree(T.lchild);
			preCreateBTree(T.rchild);
		}
		
		private void inorCreateBTree(TreeNode T){
			if(T == null) return;
			inorCreateBTree(T.lchild);
			createNewNode(T); 
			inorCreateBTree(T.rchild);
		}
		
		private void postCreateBTree(TreeNode T){
			if(T == null) return;
			postCreateBTree(T.lchild);
			postCreateBTree(T.rchild);
			createNewNode(T); 
		}
		
		public void showPreCreateBTree(String data){
			String[] contents = data.split(" ");
			root = buildBT(contents);
			createBTreeModel(root, 1, false);
			preCreateBTree(root);
		}
		
		public void showInorCreateBTree(String data){
			String[] contents = data.split(" ");
			root = buildBT(contents);
			createBTreeModel(root, 1, false);
			inorCreateBTree(root);
		}
		
		public void showPostCreateBTree(String data){
			String[] contents = data.split(" ");
			root = buildBT(contents);
			createBTreeModel(root, 1, false);
			postCreateBTree(root);
		}
		
		private int treeLeft = ShapeSize.TreeModel.LEFT_MARGIN, treeBottom = 0;
		private int dataLeft, dataTop, dataRight;
		//先序遍历
		public void showPreData(String data){
			String[] contents = data.split(" ");
			root = buildBT(contents);
			createBTreeModel(root, 1, true);
			dataRight = ShapeSize.TreeModel.LEFT_MARGIN + (treeNodeCnt-1)*(ShapeSize.TreeModel.NODES_HOR_DIST + ShapeSize.TreeModel.CIRCLE_WIDTH);
			dataLeft = treeLeft + ShapeSize.TreeModel.CIRCLE_WIDTH;
			dataTop = treeBottom + ShapeSize.TreeModel.CIRCLE_HEIGHT*2;
			preData(root);
		}
		//用于先序,中序，后序遍历
		private void circleMove(DsCircle circle, int toX, int toY){
			final int offDistY = 20, offDistX = 10;
			while(circle.ly < treeBottom){
				circle.ly += offDistY;
				if(circle.ly > treeBottom)
					circle.ly = treeBottom;
				model.setViewChanged();
				delay(100);
			}
			
			if(circle.lx < toX){
				while(circle.lx < toX){
					circle.lx += offDistX;
					if(circle.lx > toX)
						circle.lx = toX;
					model.setViewChanged();
					delay(100);
				}
			} else {
				while(circle.lx > toX){
					circle.lx -= offDistX;
					if(circle.lx < toX)
						circle.lx = toX;
					model.setViewChanged();
					delay(100);
				}
			}
			
			while(circle.ly < dataTop){
				circle.ly += offDistY;
				if(circle.ly > dataTop)
					circle.ly = dataTop;
				model.setViewChanged();
				delay(100);
			}
		}
		
		private void preData(TreeNode T){
			if(T == null) return;
			DsCircle circle = (DsCircle) T.shape.clone();
			circle.color = Color.GREEN;
			synchronized (Shape.class) {shapeList.add(circle);}
			circleMove(circle, dataLeft, dataTop);
			dataLeft += ShapeSize.TreeModel.CIRCLE_WIDTH;
			if(dataLeft > dataRight){
				dataLeft = treeLeft + ShapeSize.TreeModel.CIRCLE_WIDTH;
				dataTop += ShapeSize.TreeModel.CIRCLE_HEIGHT+20;
			}
			preData(T.lchild);
			preData(T.rchild);
		}
		
		public void showInorData(String data){
			String[] contents = data.split(" ");
			root = buildBT(contents);
			createBTreeModel(root, 1, true);
			dataRight = ShapeSize.TreeModel.LEFT_MARGIN + (treeNodeCnt-1)*(ShapeSize.TreeModel.NODES_HOR_DIST + ShapeSize.TreeModel.CIRCLE_WIDTH);
			dataLeft = treeLeft + ShapeSize.TreeModel.CIRCLE_WIDTH;
			dataTop = treeBottom + ShapeSize.TreeModel.CIRCLE_HEIGHT*2;
			inorData(root);
		}
		
		private void inorData(TreeNode T){
			if(T == null) return;
			inorData(T.lchild);
			DsCircle circle = (DsCircle) T.shape.clone();
			circle.color = Color.GREEN;
			synchronized (Shape.class) {shapeList.add(circle);}
			circleMove(circle, dataLeft, dataTop);
			dataLeft += ShapeSize.TreeModel.CIRCLE_WIDTH;
			if(dataLeft > dataRight){
				dataLeft = treeLeft + ShapeSize.TreeModel.CIRCLE_WIDTH;
				dataTop += ShapeSize.TreeModel.CIRCLE_HEIGHT+20;
			}
			inorData(T.rchild);
		}
		
		public void showPostData(String data){
			String[] contents = data.split(" ");
			root = buildBT(contents);
			createBTreeModel(root, 1, true);
			dataRight = ShapeSize.TreeModel.LEFT_MARGIN + (treeNodeCnt-1)*(ShapeSize.TreeModel.NODES_HOR_DIST + ShapeSize.TreeModel.CIRCLE_WIDTH);
			dataLeft = treeLeft + ShapeSize.TreeModel.CIRCLE_WIDTH;
			dataTop = treeBottom + ShapeSize.TreeModel.CIRCLE_HEIGHT*2;
			postData(root);
		}
		
		private void postData(TreeNode T){
			if(T == null) return;
			postData(T.lchild);
			postData(T.rchild);
			DsCircle circle = (DsCircle) T.shape.clone();
			circle.color = Color.GREEN;
			synchronized (Shape.class) {shapeList.add(circle);}
			circleMove(circle, dataLeft, dataTop);
			dataLeft += ShapeSize.TreeModel.CIRCLE_WIDTH;
			if(dataLeft > dataRight){
				dataLeft = treeLeft + ShapeSize.TreeModel.CIRCLE_WIDTH;
				dataTop += ShapeSize.TreeModel.CIRCLE_HEIGHT+20;
			}
		}
		
		private int forestBottom = 0, forestRight = -1, forestNodeCnt = 0, treeRight = -1;
		
		private void graphicForestModel(ArrayList<ForestNode> roots, int level, boolean isAdd){
			for(ForestNode root : roots){
				if(root.childList.size() == 0){//叶子节点
					++forestNodeCnt;
					int circleLeft = treeRight < 0 ? ShapeSize.ForestModel.LEFT_MARGIN : treeRight; 
					circleLeft += (forestNodeCnt-1)*(ShapeSize.ForestModel.NODES_HOR_DIST + ShapeSize.ForestModel.CIRCLE_WIDTH);
					int circleTop = ShapeSize.ForestModel.TOP_MARGIN;
					circleTop += (level-1)*(ShapeSize.ForestModel.CIRCLE_HEIGHT + ShapeSize.ForestModel.LEVEL_DIST);
					
					if(forestBottom < circleTop+ShapeSize.ForestModel.CIRCLE_HEIGHT)
						forestBottom = circleTop+ShapeSize.ForestModel.CIRCLE_HEIGHT;
					
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
		
		Map<TreeNode, ForestNode> treeMapForest = null;
		
		private ArrayList<ForestNode> bTreeToForest(TreeNode bTRoot){
			ArrayList<ForestNode> roots = new ArrayList<ForestNode>();
			if(bTRoot == null) return roots;
			treeMapForest = new TreeMap<TreeNode, ForestNode>();
			Queue<TreeNode> q = new LinkedList<TreeNode>();
			ForestNode root = new ForestNode(bTRoot.content);
			q.add(bTRoot);
			treeMapForest.put(bTRoot, root);
			roots.add(root);
			while(!q.isEmpty()){
				if(!treeMapForest.containsKey(q.peek())){//说明对应的森林的树根节点
					ForestNode rootx = new ForestNode(q.peek().content);
					treeMapForest.put(q.peek(), rootx);
					roots.add(rootx);
				}
				TreeNode curTreeNode = q.poll();
				if(curTreeNode.lchild != null)
					q.add(curTreeNode.lchild);
				if(curTreeNode.rchild != null)
					q.add(curTreeNode.rchild);
				
				ForestNode curForestNode = treeMapForest.get(curTreeNode);
				for(TreeNode child = curTreeNode.lchild; child != null; child = child.rchild){
					ForestNode rootx = new ForestNode(child.content);
					treeMapForest.put(child, rootx);
					curForestNode.childList.add(rootx);
				}
			}
			return roots;
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
		
		private void bTreeToForestDisplay(JScrollPane scrollpaen, TreeNode bTRoot){
			if(bTRoot == null) return;
			adjustView(scrollpaen, bTRoot.shape.lx, bTRoot.shape.ly);
			tipForSearched(bTRoot.shape);
			ForestNode forestNode = treeMapForest.get(bTRoot);
			synchronized(Shape.class){ shapeList.add(forestNode.shape); }
			adjustView(scrollpaen, forestNode.shape.lx, forestNode.shape.ly);
			tipForSearched(forestNode.shape);
			for(DsLine line2 : forestNode.lineList){
				DsLine line1 = new DsLine(line2.x1, line2.y1, line2.x1, line2.y1, false);
				synchronized(Shape.class){ shapeList.add(line1); }
				lineMove(line1, line2);
				synchronized(Shape.class){ 
					shapeList.remove(line1);
					shapeList.add(line2);
				}
			}
			bTreeToForestDisplay(scrollpaen, bTRoot.lchild);
			bTreeToForestDisplay(scrollpaen, bTRoot.rchild);
		}
		
		public void showBTreeToForest(String data){
			DrawPanel panel = model.getObserverPanel();
			String[] contents = data.split(" ");
			root = buildBT(contents);
			createBTreeModel(root, 1, true);
			treeRight = ShapeSize.TreeModel.LEFT_MARGIN + (treeNodeCnt)*(ShapeSize.TreeModel.NODES_HOR_DIST + ShapeSize.TreeModel.CIRCLE_WIDTH) + ShapeSize.TreeModel.CIRCLE_WIDTH;
			ArrayList<ForestNode> roots = bTreeToForest(root);
			graphicForestModel(roots, 1, false);
			panel.setPreferredSize(new Dimension(forestRight+ShapeSize.TreeModel.CIRCLE_WIDTH, Math.max(treeBottom, forestBottom)+ShapeSize.TreeModel.CIRCLE_HEIGHT));
			JScrollPane scrollPane = (JScrollPane) panel.getParent().getParent();
			delay(100);
			bTreeToForestDisplay(scrollPane, root);
		}

		public BTreeModel(DrawModel model) {
			super();
			this.model = model;
			shapeList = model.getShapeList();
		}

}
