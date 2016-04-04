package com.ds.model;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.ds.shape.DsCircle;
import com.ds.shape.DsHuffmanCircle;
import com.ds.shape.DsLine;
import com.ds.shape.DsNumberRect;
import com.ds.shape.DsRect;
import com.ds.shape.Shape;
import com.ds.size.ShapeSize;

public class HuffmanTreeModel {
	private DrawModel model;
	private ArrayList<Shape> shapeList;
	
	public void huffmanTree(String data){
		String[] datas = data.split("");
		Map<String, Integer> sCnt = new TreeMap<String, Integer>();
		for(String s : datas){
			if(s.length() == 0) continue;
			if(!sCnt.containsKey(s))
				sCnt.put(s, 1);
			else
				sCnt.put(s, sCnt.get(s)+1);
		}
		
		{
			int i = 0;
			
			DsRect chMsg = new DsRect(ShapeSize.HuffmanTreeModel.LEFT_MARGIN, ShapeSize.HuffmanTreeModel.ARRAY_TOP_MARGIN+ShapeSize.HuffmanTreeModel.RECT_HEIGHT, ShapeSize.HuffmanTreeModel.RECT_WIDTH, ShapeSize.HuffmanTreeModel.RECT_WIDTH, "CH:");
			//字符的权值
			DsRect wMsg = new DsRect(ShapeSize.HuffmanTreeModel.LEFT_MARGIN, ShapeSize.HuffmanTreeModel.ARRAY_TOP_MARGIN+ShapeSize.HuffmanTreeModel.RECT_HEIGHT*2, ShapeSize.HuffmanTreeModel.RECT_WIDTH, ShapeSize.HuffmanTreeModel.RECT_WIDTH, "W:");
			wMsg.fontSize = chMsg.fontSize = 25;
			shapeList.add(chMsg);
			shapeList.add(wMsg);
			for(String key : sCnt.keySet()){
				++i;
				//节点编号
				DsNumberRect tipR = new DsNumberRect(ShapeSize.HuffmanTreeModel.LEFT_MARGIN+i*ShapeSize.HuffmanTreeModel.RECT_WIDTH, ShapeSize.HuffmanTreeModel.ARRAY_TOP_MARGIN, ShapeSize.HuffmanTreeModel.RECT_WIDTH, ShapeSize.HuffmanTreeModel.RECT_WIDTH, "["+i+"]");
				tipR.fontSize = 25;
				//字符
				DsRect chR = new DsRect(ShapeSize.HuffmanTreeModel.LEFT_MARGIN+i*ShapeSize.HuffmanTreeModel.RECT_WIDTH, ShapeSize.HuffmanTreeModel.ARRAY_TOP_MARGIN+ShapeSize.HuffmanTreeModel.RECT_HEIGHT, ShapeSize.HuffmanTreeModel.RECT_WIDTH, ShapeSize.HuffmanTreeModel.RECT_WIDTH, key);
				//字符的权值
				DsRect wR = new DsRect(ShapeSize.HuffmanTreeModel.LEFT_MARGIN+i*ShapeSize.HuffmanTreeModel.RECT_WIDTH, ShapeSize.HuffmanTreeModel.ARRAY_TOP_MARGIN+ShapeSize.HuffmanTreeModel.RECT_HEIGHT*2, ShapeSize.HuffmanTreeModel.RECT_WIDTH, ShapeSize.HuffmanTreeModel.RECT_WIDTH, String.valueOf(sCnt.get(key)));
				shapeList.add(wR);
				shapeList.add(tipR);
				shapeList.add(chR);
			}
		}
		
		ArrayTreeList atl = new ArrayTreeList();
		{
			HuffmanNode node = new HuffmanNode(-1, -1, -1, -1, null);
			atl.addNode(node);
		}
		ArrayList<HuffmanTreeNode> roots = new ArrayList<HuffmanTreeNode>();
		PriorityQueue<HuffmanNode> queue = new PriorityQueue<HuffmanNode>();
		for(String key : sCnt.keySet()){
			HuffmanTreeNode fNode = new HuffmanTreeNode(String.valueOf(sCnt.get(key)), "HT["+atl.list.size()+"]");
			synchronized (Shape.class) {
				forestNodeCnt = 0;
				for(HuffmanTreeNode hNode : roots)
					shapeList.remove(hNode.shape);
			}
			roots.add(fNode);
			HuffmanNode node = new HuffmanNode(-1, -1, -1, sCnt.get(key), fNode);
			atl.addNode(node);
			queue.add(node);
		}
		int childs = sCnt.size();
		
		int arrayHeight = ShapeSize.HuffmanTreeModel.LEFT_MARGIN+(childs+1)*ShapeSize.HuffmanTreeModel.RECT_WIDTH;
		graphicHuffmanTreeModel(roots, 1, true);
		panelResize(Math.max(arrayHeight, forestRight), -1);
		//建立huffman树
		while(atl.list.size() < childs*2){
			HuffmanNode nodeLc = queue.poll();
			HuffmanNode nodeRc = queue.poll();
			HuffmanTreeNode fNode = new HuffmanTreeNode(String.valueOf(nodeLc.weight+nodeRc.weight), "HT["+atl.list.size()+"]");
			circleShake(new DsCircle[]{nodeLc.fNode.shape, nodeRc.fNode.shape});
			//重新绘制树
			forestNodeCnt = 0;
			forestRight = 0;
			removeForest(roots);
			
			roots.remove(nodeLc.fNode);
			roots.remove(nodeRc.fNode);
			fNode.childList.add(nodeLc.fNode);
			fNode.childList.add(nodeRc.fNode);
			roots.add(fNode);
			graphicHuffmanTreeModel(roots, 1, true);
			//调整panel的大小
			panelResize(Math.max(arrayHeight, forestRight), -1);
			
			HuffmanNode newNode = new HuffmanNode(-1, atl.list.indexOf(nodeLc), atl.list.indexOf(nodeRc), nodeLc.weight+nodeRc.weight, fNode);
			nodeLc.parent = nodeRc.parent = atl.list.size();
			atl.addShowingNode(newNode);
			queue.add(newNode);
			tipForSearched(new DsNumberRect[]{(DsNumberRect) nodeLc.shapes[0], (DsNumberRect) nodeRc.shapes[0]});
			nodeLc.updateParentValue();
			nodeRc.updateParentValue();
		}
		
		//编码
		for(int i=1; i<=childs; ++i){
			DsNumberRect codeRect = new DsNumberRect(atl.list.get(i).fNode.shape.lx-ShapeSize.HuffmanTreeModel.NODES_HOR_DIST/2, atl.list.get(i).fNode.shape.ly+ShapeSize.HuffmanTreeModel.RECT_HEIGHT+10,
					 ShapeSize.HuffmanTreeModel.RECT_WIDTH+ShapeSize.HuffmanTreeModel.NODES_HOR_DIST, ShapeSize.HuffmanTreeModel.RECT_HEIGHT/2, "");
			codeRect.fontSize = 18;
			shapeList.add(codeRect);
			for(HuffmanNode node = atl.list.get(i); node.parent != -1; ){
				HuffmanNode pNode = atl.list.get(node.parent);
				circleShake(new DsCircle[]{pNode.fNode.shape, node.fNode.shape});
				DsLine line = null;
				if(atl.list.get(pNode.lchild) == node){//左子树
					line = pNode.fNode.lineList.get(0);
					line.weight = "0";
					line.setDefaultLine(new Point(line.x1, line.y1));
				} else if(atl.list.get(pNode.rchild) == node){//右子树
					line = pNode.fNode.lineList.get(1);
					line.weight = "1";
					line.setDefaultLine(new Point(line.x1, line.y1));
				}
				codeRect.content = line.weight + codeRect.content;
				model.setViewChanged();
				node = pNode;
			}
		}
		model.setViewChanged();
	}
	
	private void circleShake(DsCircle[] circles){
		final int moveDist = 8;
		int[][] dir = {{-1, -1}, {-1, 1}, {1, 1}, {1, -1}};
		for(int i=0; i<dir.length; ++i){
			for(DsCircle circle : circles){
				circle.lx += dir[i][0]*moveDist;
				circle.ly += dir[i][1]*moveDist;
			}
			model.setViewChanged();
			delay(100);
			for(DsCircle circle : circles){
				circle.lx -= dir[i][0]*moveDist;
				circle.ly -= dir[i][1]*moveDist;
			}
			model.setViewChanged();
			delay(100);
		}
	}
	
	private void tipForSearched(DsNumberRect[] shapes){
		boolean flag = true;
		Color color = null;
		for(int k=1; k<=4; ++k){
			color = flag ? Color.RED : Color.BLACK;
			for(DsNumberRect shape : shapes)
				shape.preColor = color;
			flag = !flag;
			model.setViewChanged();
			delay(300);
		}
	}
	
	private void removeForest(ArrayList<HuffmanTreeNode> roots){
		for(HuffmanTreeNode root : roots){
			synchronized(Shape.class){
				shapeList.remove(root.shape);
				shapeList.removeAll(root.lineList);
				root.lineList.clear();
			}
			removeForest(root.childList);
		}
	}
	
	private void panelResize(int width, int height){
		JPanel panel = model.getObserverPanel();
		if(panel.getPreferredSize().height < height)
			panel.setPreferredSize(new Dimension(panel.getPreferredSize().width, height));
		if(panel.getPreferredSize().width < width)
			panel.setPreferredSize(new Dimension(width, panel.getPreferredSize().height));
		JScrollPane jsp = (JScrollPane) panel.getParent().getParent();
		jsp.revalidate();
	}
	
	private class ArrayTreeList{
		private int ld;
		public DsLine up, left, right, down;
		public ArrayList<HuffmanNode> list = new ArrayList<HuffmanNode>();
		public ArrayTreeList() {
			super();
			ld = ShapeSize.HuffmanTreeModel.ARRAY_TREE_LD;
			up = new DsLine(ShapeSize.HuffmanTreeModel.LEFT_MARGIN, ShapeSize.HuffmanTreeModel.TREE_TOP_MARGIN, ShapeSize.HuffmanTreeModel.LEFT_MARGIN+ShapeSize.HuffmanTreeModel.ARRAY_TREE_WIDTH, ShapeSize.HuffmanTreeModel.TREE_TOP_MARGIN, false);
			up.color = Color.CYAN;
			left = new DsLine(up.x1, up.y1, up.x1, up.y1+ShapeSize.HuffmanTreeModel.RECT_HEIGHT+ld*2, false);
			left.color = Color.CYAN;
			right = new DsLine(up.x2, up.y2, up.x2, up.y2+ShapeSize.HuffmanTreeModel.RECT_HEIGHT+ld*2, false);
			right.color = Color.CYAN;
			down = new DsLine(left.x2, left.y2, right.x2, right.y2, false);
			down.color = Color.CYAN;
			shapeList.add(up);
			shapeList.add(left);
			shapeList.add(right);
			shapeList.add(down);
		}
		
		public void addShowingNode(HuffmanNode node){
			int listH = left.y2 - left.y1;
			if((list.size()+1)*ShapeSize.HuffmanTreeModel.RECT_HEIGHT > listH){
				listH += ShapeSize.HuffmanTreeModel.RECT_HEIGHT;
				down.y1 = down.y2 = left.y2 = right.y2 = left.y2+ShapeSize.HuffmanTreeModel.RECT_HEIGHT;
				panelResize(-1, left.y2+ShapeSize.HuffmanTreeModel.RECT_HEIGHT);
				model.setViewChanged();
			}
			synchronized(Shape.class){
				for(int i=0; i<node.shapes.length; ++i){
					if(i == 0 && node.fNode != null)
						node.shapes[i] = new DsNumberRect(right.x1+10, right.y1+(listH-ShapeSize.HuffmanTreeModel.RECT_HEIGHT)/2, ShapeSize.HuffmanTreeModel.RECT_WIDTH, ShapeSize.HuffmanTreeModel.RECT_HEIGHT, null);
					else {
						node.shapes[i] = new DsRect(right.x1+10, right.y1+(listH-ShapeSize.HuffmanTreeModel.RECT_HEIGHT)/2, ShapeSize.HuffmanTreeModel.RECT_WIDTH, ShapeSize.HuffmanTreeModel.RECT_HEIGHT, null);
						node.shapes[i].color = Color.ORANGE;
					}
					shapeList.add(node.shapes[i]);
				}
			}
			if(node.fNode == null) {
				node.shapes[0].content = "N";
				node.shapes[1].content = "P";
				node.shapes[2].content = "LC";
				node.shapes[3].content = "RC";
				node.shapes[4].content = "W";
			} else {
				node.shapes[0].content = "["+list.size()+"]";
				node.shapes[1].content = String.valueOf(node.parent);
				node.shapes[2].content = String.valueOf(node.lchild);
				node.shapes[3].content = String.valueOf(node.rchild);
				node.shapes[4].content = String.valueOf(node.weight);
			}
			MyTask[] tasks = new MyTask[node.shapes.length];
			for(int i=0; i<node.shapes.length; ++i){
				tasks[i] = new MyTask(node.shapes[i], left.x1+ShapeSize.HuffmanTreeModel.ARRAY_TREE_LD+i*ShapeSize.HuffmanTreeModel.RECT_WIDTH, up.y1+ShapeSize.HuffmanTreeModel.ARRAY_TREE_LD+list.size()*ShapeSize.HuffmanTreeModel.RECT_HEIGHT);
				Timer timer = new Timer();
				timer.schedule(tasks[i], i*500);
			}
			list.add(node);
			openDoor();
			
			for(int i=0; i<tasks.length; ++i)
				while(!tasks[i].isRunOver());
			
			closeDoor();
		}
		
		class MyTask extends TimerTask{
			private DsRect rect;
			private int tx, ty;
			private boolean isOver = false;
			public boolean isRunOver(){
				return isOver;
			}
			@Override
			public void run() {
				moveRect(rect, right.y2, DIR_DOWN);
				moveRect(rect, tx, DIR_LEFT);
				moveRect(rect, ty, DIR_UP);
				isOver = true;
			}
			public MyTask(DsRect rect, int tx, int ty) {
				super();
				this.rect = rect;
				this.tx = tx;
				this.ty = ty;
			}
		}
		
		public void addNode(HuffmanNode node){
			int listH = left.y2 - left.y1;
			if((list.size()+1)*ShapeSize.HuffmanTreeModel.RECT_HEIGHT > listH){
				listH += ShapeSize.HuffmanTreeModel.RECT_HEIGHT;
				down.y1 = down.y2 = left.y2 = right.y2 = left.y2+ShapeSize.HuffmanTreeModel.RECT_HEIGHT;
			}
			for(int i=0; i<node.shapes.length; ++i){
				if(i == 0 && node.fNode != null)
					node.shapes[i] = new DsNumberRect(left.x1+ShapeSize.HuffmanTreeModel.ARRAY_TREE_LD+i*ShapeSize.HuffmanTreeModel.RECT_WIDTH, up.y1+ShapeSize.HuffmanTreeModel.ARRAY_TREE_LD+list.size()*ShapeSize.HuffmanTreeModel.RECT_HEIGHT, ShapeSize.HuffmanTreeModel.RECT_WIDTH, ShapeSize.HuffmanTreeModel.RECT_HEIGHT, null);
				else {
					node.shapes[i] = new DsRect(left.x1+ShapeSize.HuffmanTreeModel.ARRAY_TREE_LD+i*ShapeSize.HuffmanTreeModel.RECT_WIDTH, up.y1+ShapeSize.HuffmanTreeModel.ARRAY_TREE_LD+list.size()*ShapeSize.HuffmanTreeModel.RECT_HEIGHT, ShapeSize.HuffmanTreeModel.RECT_WIDTH, ShapeSize.HuffmanTreeModel.RECT_HEIGHT, null);
					if(list.size() != 0)
						node.shapes[i].color = Color.PINK;
				}
				shapeList.add(node.shapes[i]);
			}
			if(node.fNode == null) {
				node.shapes[0].content = "N";
				node.shapes[1].content = "P";
				node.shapes[2].content = "LC";
				node.shapes[3].content = "RC";
				node.shapes[4].content = "W";
			} else {
				node.shapes[0].content = "["+list.size()+"]";
				node.shapes[1].content = String.valueOf(node.parent);
				node.shapes[2].content = String.valueOf(node.lchild);
				node.shapes[3].content = String.valueOf(node.rchild);
				node.shapes[4].content = String.valueOf(node.weight);
			}
			 
			list.add(node);
		}
		
		
		private DsLine dl, dr;
	
		private void closeDoor(){
			DsLine dlo = (DsLine) dl.clone();
			DsLine dro = (DsLine) dr.clone();
			int angle = 0;
			int r = (down.x2 - down.x1)/2;
			final int anglex = 5;
			while(angle < 90){
				double tmpAngle = 0.0;
				angle += anglex;
				tmpAngle = Math.PI/180 * angle;
				dl.x2 = (int) (dlo.x2 + r*Math.sin(tmpAngle));
				dl.y2 = (int) (dlo.y2 + r*(1-Math.cos(tmpAngle)));
				
				dr.x1 = (int) (dro.x1 - r*Math.sin(tmpAngle));
				dr.y1 = (int) (dro.y1 + r*(1-Math.cos(tmpAngle)));
				model.setViewChanged();
				delay(50);
			}
			synchronized (Shape.class) {
				shapeList.add(down);
				shapeList.remove(dl);
				shapeList.remove(dr);
			}
		}
			
		private void openDoor(){
			dl = new DsLine(down.x1, down.y1, down.x1+(down.x2-down.x1)/2, down.y1, false);
			dr = new DsLine(dl.x2, dl.y1, down.x2, dl.y1, false);
			dl.color = Color.CYAN;
			dr.color = Color.CYAN;
			DsLine dlo = (DsLine) dl.clone();
			DsLine dro = (DsLine) dr.clone();
			synchronized (Shape.class) {
				shapeList.add(dl);
				shapeList.add(dr);
				shapeList.remove(down);
			}
			
			int angle = 0;
			int r = (down.x2 - down.x1)/2;
			final int anglex = 5;
			while(angle < 90){
				double tmpAngle = 0.0;
				angle += anglex;
				tmpAngle = Math.PI/180 * angle;
				dl.x2 = (int) (dlo.x2 - r*(1-Math.cos(tmpAngle)));
				dl.y2 = (int) (dlo.y2 - r*Math.sin(tmpAngle));
				
				dr.x1 = (int) (dro.x1 + r*(1-Math.cos(tmpAngle)));
				dr.y1 = (int) (dro.y1 - r*Math.sin(tmpAngle));
				model.setViewChanged();
				delay(100);
			}
		}
	}
	
	public static final int DIR_LEFT = 1;
	public static final int DIR_RIGHT = 2;
	public static final int DIR_UP = 3;
	public static final int DIR_DOWN = 4;
	private void moveRect(DsRect rect, int pos, int dir){
		final int offDistX = 20, offDistY = 10;
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
	private void delay(int time){
		try {
			TimeUnit.MILLISECONDS.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void addForestLine(HuffmanTreeNode T, HuffmanTreeNode Tchild, boolean isAdd){
		int x1 = T.shape.lx + T.shape.lw/2;
		int y1 = T.shape.ly + T.shape.lh/2;
		int x2 = Tchild.shape.lx + Tchild.shape.lw/2;
		int y2 = Tchild.shape.ly + Tchild.shape.lh/2;
		
		if(x1 == x2){
			DsLine line = new DsLine(x1, y1+ShapeSize.HuffmanTreeModel.CIRCLE_HEIGHT/2, x2, y2-ShapeSize.HuffmanTreeModel.CIRCLE_HEIGHT/2, false);
			if(isAdd) shapeList.add(line);
			T.lineList.add(line);
			return ;
		}
		
		//特殊处理， 不让线段画进  树结点的里面， (x1, y1), (x2, y2), (x1, y2)三点组成三角形，然后有
		// (x1, y1)和 (x2, y2)线段的长度为L， 则有 x1+L*sin@ = x2, y1+L*cos@ = y2;
		double L = Math.sqrt((double)((x2-x1)*(x2-x1) + (y2-y1)*(y2-y1)));
		double sinx = (x2-x1)/L/2;//除以2，因为ShapeSize.HuffmanTreeModel.CIRCLE_WIDTH是直径，我们要半径
		double cosx = (y2-y1)/L/2;
		x1 = (int)(x1+ShapeSize.HuffmanTreeModel.CIRCLE_WIDTH*sinx);
		y1 = (int)(y1+ShapeSize.HuffmanTreeModel.CIRCLE_WIDTH*cosx);
		x2 = (int)(x2-ShapeSize.HuffmanTreeModel.CIRCLE_WIDTH*sinx);
		y2 = (int)(y2-ShapeSize.HuffmanTreeModel.CIRCLE_WIDTH*cosx);
		DsLine line = new DsLine(x1, y1, x2, y2, false);
		if(isAdd) shapeList.add(line);
		T.lineList.add(line);
	}
	
	private int maxLevel = 0, forestNodeCnt = 0, forestRight = 0;
	
	private void graphicHuffmanTreeModel(ArrayList<HuffmanTreeNode> roots, int level, boolean isAdd){
		if(maxLevel < level) maxLevel = level;
		for(HuffmanTreeNode root : roots){
			if(root.childList.size() == 0){//叶子节点
				++forestNodeCnt;
				int circleLeft = ShapeSize.HuffmanTreeModel.TREE_LEFT_MARGIN;
				circleLeft += (forestNodeCnt-1)*(ShapeSize.HuffmanTreeModel.NODES_HOR_DIST + ShapeSize.HuffmanTreeModel.CIRCLE_WIDTH);
				int circleTop = ShapeSize.HuffmanTreeModel.TREE_TOP_MARGIN;
				circleTop += (level-1)*(ShapeSize.HuffmanTreeModel.CIRCLE_HEIGHT + ShapeSize.HuffmanTreeModel.LEVEL_DIST);
				
				DsHuffmanCircle shapeCircle = new DsHuffmanCircle(circleLeft, circleTop, ShapeSize.HuffmanTreeModel.CIRCLE_WIDTH, ShapeSize.HuffmanTreeModel.CIRCLE_HEIGHT, root.content, root.nodeTip);
				if(isAdd) shapeList.add(shapeCircle);
				shapeCircle.color = Color.GREEN;
				root.shape = shapeCircle;
				if(forestRight < circleLeft+ShapeSize.HuffmanTreeModel.CIRCLE_WIDTH)
					forestRight = circleLeft+ShapeSize.HuffmanTreeModel.CIRCLE_WIDTH;
			} else {//非叶子节点
				graphicHuffmanTreeModel(root.childList, level+1, isAdd);
				int circleLeft = (root.childList.get(0).shape.lx + root.childList.get(root.childList.size()-1).shape.lx)/2;
				int circleTop = ShapeSize.HuffmanTreeModel.TREE_TOP_MARGIN + (level-1)*(ShapeSize.HuffmanTreeModel.CIRCLE_HEIGHT + ShapeSize.HuffmanTreeModel.LEVEL_DIST);
				DsHuffmanCircle shapeCircle = new DsHuffmanCircle(circleLeft, circleTop, ShapeSize.HuffmanTreeModel.CIRCLE_WIDTH, ShapeSize.HuffmanTreeModel.CIRCLE_HEIGHT, root.content, root.nodeTip);
				if(isAdd) shapeList.add(shapeCircle);
				root.shape = shapeCircle;
				
				//添加连线
				for(HuffmanTreeNode child : root.childList) 
					addForestLine(root, child, isAdd);
			}
		}
	}
	
	public HuffmanTreeModel(DrawModel model) {
		super();
		this.model = model;
		shapeList = model.getShapeList();
	}
}
