package com.ds.model;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import javax.management.timer.TimerMBean;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JViewport;

import com.ds.shape.DsCircle;
import com.ds.shape.DsLine;
import com.ds.shape.DsRect;
import com.ds.shape.DsTipArrow;
import com.ds.shape.Shape;
import com.ds.size.ShapeSize;
import com.sun.jmx.snmp.tasks.Task;

/**
 * @author 峻峥
 *
 */
/**
 * @author 峻峥
 *
 */
public class HeapSortModel {
	private DrawModel model;
	private ArrayList<Shape> shapeList;
	
	private class StringComparator implements Comparator<String>{
		@Override
		public int compare(String s1, String s2) {
			return s1.length() == s2.length() ? s1.compareTo(s2) : s1.length()-s2.length();
		}
	}
	
	private Map<SortNode, TreeNode> sMapT = new TreeMap<SortNode, TreeNode>();
	private TreeNode buildBT(ArrayList<SortNode> nodeList){
		TreeNode root = null;
		Queue<TreeNode> q = new LinkedList<TreeNode>();
		if(nodeList != null && nodeList.size() > 0){
			root = new TreeNode();
			root.content = nodeList.get(0).content;
			sMapT.put(nodeList.get(0), root);
			q.add(root);
		}
		int i = 1;
		while(!q.isEmpty() && i < nodeList.size()){
			TreeNode curNode = q.poll();
			curNode.lchild = new TreeNode(nodeList.get(i).content);
			sMapT.put(nodeList.get(i++), curNode.lchild);
			q.add(curNode.lchild);
			if(i < nodeList.size()){
				curNode.rchild = new TreeNode(nodeList.get(i).content);
				sMapT.put(nodeList.get(i++), curNode.rchild);
				q.add(curNode.rchild);
			}
		}
		return root;
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
	
	//指示调整哪棵子树
	private DsTipArrow arrow = null;
	public void heapSort(String data, boolean minOrMax){
		String[] datas = data.split(",");
		ArrayList<SortNode> nodeList = new ArrayList<SortNode>();
		for(int i=0; i<datas.length; ++i){
			SortNode node = new SortNode(datas[i]);
			DsRect shape = new DsRect(ShapeSize.MergeSortModel.LEFT_MARGIN+i*ShapeSize.MergeSortModel.NODE_WIDTH, ShapeSize.MergeSortModel.TOP_MARGIN, ShapeSize.MergeSortModel.NODE_WIDTH, ShapeSize.MergeSortModel.NODE_HEIGHT, datas[i]);
			node.shape = shape;
			synchronized(Shape.class){shapeList.add(shape);}
			nodeList.add(node);
		}
		
		TreeNode root = buildBT(nodeList);
		createBTreeModel(root, 1, true);
		int width = ShapeSize.HeapSortModel.LEFT_MARGIN + treeNodeCnt*(ShapeSize.HeapSortModel.NODES_HOR_DIST + ShapeSize.HeapSortModel.CIRCLE_WIDTH);
		int height = treeBottom + ShapeSize.HeapSortModel.NODE_HEIGHT;
		model.getObserverPanel().setPreferredSize(new Dimension(width, height));
		delay(100);
		
		//指示
		arrow = new DsTipArrow(-100, -100, "☞");
		synchronized(Shape.class){shapeList.add(arrow);}
		Timer timer = new Timer();
		timer.schedule(new MyTask(arrow), 0, 500);
		
		for(int i=nodeList.size()/2-1; i >= 0; --i)
			heapAdjust(nodeList, i, minOrMax);	
		
		timer.cancel();
		model.setViewChanged();
	}
	
	private void delay(int time){
		try {
			TimeUnit.MILLISECONDS.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void twoRectsMove(DsRect rect1, DsRect rect2){
		int r1x = rect1.lx;
		int r2x = rect2.lx;
		
		final int offDistX = 10;
		while(rect1.lx < r2x){
			rect1.lx += offDistX;
			rect2.lx -= offDistX;
			if(rect1.lx > r2x){
				rect1.lx = r2x;
				rect2.lx = r1x;
			}
			model.setViewChanged();
			delay(100);
		}
	}
	
	/**
	 * @param shape1	父节点
	 * @param shape2	子节点
	 */
	private void twoCirclesMove(DsCircle shape1, DsCircle shape2){
		Point tp1 = new Point(shape1.lx, shape1.ly);
		Point tp2 = new Point(shape2.lx, shape2.ly);
		double k = (1.0*shape2.ly-shape1.ly)/(shape2.lx-shape1.lx);
		double b = shape2.ly-k*shape2.lx;
		final int offDistY = 5;
		while(shape1.ly < tp2.y){
			shape1.ly += offDistY;
			shape1.lx = (int) ((shape1.ly-b)/k);
			
			shape2.ly -= offDistY;
			shape2.lx = (int) ((shape2.ly-b)/k);
			
			if(shape1.ly > tp2.y){
				shape1.lx = tp2.x;
				shape1.ly = tp2.y;
				
				shape2.lx = tp1.x;
				shape2.ly = tp1.y;
			}
			model.setViewChanged();
			delay(100);
		}
		
		
	}
	
	private class MyTask extends TimerTask{
		private DsTipArrow arrow;
		private String orgContent;
		@Override
		public void run() {
			if("".equals(arrow.content))
				arrow.content = orgContent;
			else 
				arrow.content = "";
			model.setViewChanged();
		}

		public MyTask(DsTipArrow arrow) {
			super();
			this.arrow = arrow;
			orgContent = arrow.content;
		}
	}
	
	private void heapAdjust(ArrayList<SortNode> nodeList, int k, boolean minOrMax){
		StringComparator comp = new StringComparator();
		arrow.x = sMapT.get(nodeList.get(k)).shape.lx - ShapeSize.HeapSortModel.CIRCLE_WIDTH;
		arrow.y = sMapT.get(nodeList.get(k)).shape.ly + ShapeSize.HeapSortModel.CIRCLE_HEIGHT/2;
		while(k*2+1 < nodeList.size()){
			int kc = k*2+1;//左孩子
			if(kc < nodeList.size()){
				if(kc+1 < nodeList.size() && (minOrMax && comp.compare(nodeList.get(kc).content, nodeList.get(kc+1).content) > 0 ||
						!minOrMax && comp.compare(nodeList.get(kc).content, nodeList.get(kc+1).content) < 0))
					kc = kc+1;//右孩子
			}
			
			if(minOrMax && comp.compare(nodeList.get(k).content, nodeList.get(kc).content) > 0 || !minOrMax && comp.compare(nodeList.get(k).content, nodeList.get(kc).content) < 0){
				//图形演示
				JScrollPane sp = (JScrollPane) model.getObserverPanel().getParent().getParent();
				DsCircle fc = (DsCircle) sMapT.get(nodeList.get(k)).shape.clone();
				DsCircle cc = (DsCircle) sMapT.get(nodeList.get(kc)).shape.clone();
				adjustView(sp, fc.lx, fc.ly);
				fc.color = Color.GREEN;
				cc.color = Color.GREEN;
				synchronized(Shape.class){shapeList.add(fc);}
				synchronized(Shape.class){shapeList.add(cc);}
				twoCirclesMove(fc, cc);
				synchronized(Shape.class){shapeList.remove(fc);}
				synchronized(Shape.class){shapeList.remove(cc);}
				//父节点和孩子节点 交换内容
				sMapT.get(nodeList.get(k)).content = sMapT.get(nodeList.get(k)).shape.content = cc.content;
				sMapT.get(nodeList.get(kc)).content = sMapT.get(nodeList.get(kc)).shape.content = fc.content;
				
				DsRect fr = (DsRect) nodeList.get(k).shape.clone();
				DsRect cr = (DsRect) nodeList.get(kc).shape.clone();
				adjustView(sp, fr.lx, fr.ly);
				fr.color = Color.GREEN;
				cr.color = Color.GREEN;
				synchronized(Shape.class){shapeList.add(fr);}
				synchronized(Shape.class){shapeList.add(cr);}
				twoRectsMove(fr, cr);
				synchronized(Shape.class){shapeList.remove(fr);}
				synchronized(Shape.class){shapeList.remove(cr);}
				nodeList.get(k).content = nodeList.get(k).shape.content = cr.content;
				nodeList.get(kc).content = nodeList.get(kc).shape.content = fr.content;
				
				k = kc;
			} else {
				break;
			}
		}
	}
	
	private void addTreeLine(TreeNode T, TreeNode Tchild, boolean isAdd){
		int x1 = T.shape.lx + T.shape.lw/2;
		int y1 = T.shape.ly + T.shape.lh/2;
		int x2 = Tchild.shape.lx + Tchild.shape.lw/2;
		int y2 = Tchild.shape.ly + Tchild.shape.lh/2;
		//特殊处理， 不让线段画进  树结点的里面， (x1, y1), (x2, y2), (x1, y2)三点组成三角形，然后有
		// (x1, y1)和 (x2, y2)线段的长度为L， 则有 x1+L*sin@ = x2, y1+L*cos@ = y2;
		double L = Math.sqrt((double)((x2-x1)*(x2-x1) + (y2-y1)*(y2-y1)));
		double sinx = (x2-x1)/L/2;//除以2，因为ShapeSize.HeapSortModel.CIRCLE_WIDTH是直径，我们要半径
		double cosx = (y2-y1)/L/2;
		x1 = (int)(x1+ShapeSize.HeapSortModel.CIRCLE_WIDTH*sinx);
		y1 = (int)(y1+ShapeSize.HeapSortModel.CIRCLE_WIDTH*cosx);
		x2 = (int)(x2-ShapeSize.HeapSortModel.CIRCLE_WIDTH*sinx);
		y2 = (int)(y2-ShapeSize.HeapSortModel.CIRCLE_WIDTH*cosx);
		DsLine line = new DsLine(x1, y1, x2, y2, false);
		if(isAdd) synchronized(Shape.class){shapeList.add(line);}
		T.lineList.add(line);
	}
	
	
	private int treeNodeCnt = 0;
	private int treeBottom = -1;
	/**
	 * @param T	二叉树
	 * @param treeNodeCnt	二叉树从左到右的节点数的累积
	 * @param level	二叉树的层次
	 */
	private void createBTreeModel(TreeNode T, int level, boolean isAdd){
		if(T == null) return;
		createBTreeModel(T.lchild, level+1, isAdd);
		++treeNodeCnt;
		int circleLeft = ShapeSize.HeapSortModel.LEFT_MARGIN; 
		circleLeft += (treeNodeCnt-1)*(ShapeSize.HeapSortModel.NODES_HOR_DIST + ShapeSize.HeapSortModel.CIRCLE_WIDTH);
		int circleTop = ShapeSize.HeapSortModel.TOP_MARGIN + ShapeSize.HeapSortModel.NODE_HEIGHT*2;
		circleTop += (level-1)*(ShapeSize.HeapSortModel.CIRCLE_HEIGHT + ShapeSize.HeapSortModel.LEVEL_DIST);
		createBTreeModel(T.rchild, level+1, isAdd);
		
		DsCircle shapeCircle = new DsCircle(circleLeft, circleTop, ShapeSize.HeapSortModel.CIRCLE_WIDTH, ShapeSize.HeapSortModel.CIRCLE_HEIGHT, T.content);
		if(isAdd) synchronized(Shape.class){shapeList.add(shapeCircle);}
		T.shape = shapeCircle;
		
		if(treeBottom < T.shape.ly+T.shape.lh)
			treeBottom = T.shape.ly+T.shape.lh;
		
		if(T.lchild != null)
			addTreeLine(T, T.lchild, isAdd);
			
		if(T.rchild != null) 
			addTreeLine(T, T.rchild, isAdd);
	}
	
	public HeapSortModel(DrawModel model) {
		super();
		this.model = model;
		shapeList = model.getShapeList();
	}
	
}
