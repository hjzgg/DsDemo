package com.ds.model;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JViewport;

import com.ds.model.CrossListModel.Edge;
import com.ds.shape.DsCircle;
import com.ds.shape.DsLine;
import com.ds.shape.DsSampleRect;
import com.ds.shape.Shape;
import com.ds.size.ShapeSize;

public class GraphicModel{
	private DrawModel model;
	private boolean isDicircleed;//是否为有向图
	private boolean isWeighted;//是否带权图
	ArrayList<Shape> shapeList;
	
	private int threadCount;
	
	private void subThread(){
		synchronized (this) {
			--threadCount;
			if(threadCount == 0){
				//唤醒主线程
				this.notify();
			}
		}
	}
	
	//展示空间的高度和宽度
	private int swidth = 0;
	private int sheight = 0;
	
	private List<GraphicNode> nodeList = new ArrayList<GraphicNode>();
	/**
	 * @param data 例如"1 2,3 4", 表示的是1节点和2节点有一条边，3和4节点有一条边
	 */
	public void createGraphicData(String data){
		String[] edges = data.split(",");
		Map<String, GraphicNode> mp = new TreeMap<String, GraphicNode>();
		//对自环弧进行处理
		Map<String, Integer> selfMp = new TreeMap<String, Integer>();
		//自环弧的权值
		Map<String, List<String>> selfW = new TreeMap<String, List<String>>();
		//
		//可以通过mp.size()获得图中一共有多少个结点
		for(String edge : edges){
			String[] nodes = edge.split(" ");
			if(nodes[0].equals(nodes[1])){
				if(!selfMp.containsKey(nodes[0]))
					selfMp.put(nodes[0], 1);
				else
					selfMp.put(nodes[0], selfMp.get(nodes[0])+1);
				if(isWeighted){
					if(!selfW.containsKey(nodes[0]))
						selfW.put(nodes[0], new ArrayList<String>());
					selfW.get(nodes[0]).add(nodes[2]);
				}
				continue;
			}
			if(!mp.containsKey(nodes[0])){
				GraphicNode newNode = new GraphicNode();
				nodeList.add(newNode);
				newNode.content = nodes[0];
				mp.put(nodes[0], newNode);
			}
			if(!mp.containsKey(nodes[1])){
				GraphicNode newNode = new GraphicNode();
				nodeList.add(newNode);
				newNode.content = nodes[1];
				mp.put(nodes[1], newNode);
			}
			
			GraphicEdge newEdge = new GraphicEdge();
			newEdge.fromNode = mp.get(nodes[0]);
			newEdge.toNode = mp.get(nodes[1]);
			if(isWeighted) newEdge.weight = nodes[2];
			mp.get(nodes[0]).neighbourEdges.add(newEdge);
		}
		
		int nodeCnt = mp.size();//n边型的节点的个数
		double angle = 2*Math.PI/nodeCnt;//每个扇区的角度
		double r = ShapeSize.GraphicModel.NODES_DIST/2 / Math.sin(angle/2);//半径
		ArrayList<Point> ps = new ArrayList<Point>();
        int ox = (int)(ShapeSize.GraphicModel.LEFT_MARGIN + r);
        int oy = (int)(ShapeSize.GraphicModel.TOP_MARGIN + r);
        double startAngle = (Math.PI-angle)/2;
        for(int i=0; i<nodeCnt; i++) {
            int x = (int)(ox+r*Math.cos(startAngle+i*angle));
            int y = (int)(oy+r*Math.sin(startAngle+i*angle));
            ps.add(new Point(x,y));
        }
        
        int i = 0;
        for(String nodeKey : mp.keySet()){//根据n边型建立图的节点
        	GraphicNode curNode = mp.get(nodeKey);
        	Point pt = ps.get(i++);
        	DsCircle shape = new DsCircle(pt.x-ShapeSize.GraphicModel.CIRCLE_WIDTH/2, pt.y-ShapeSize.GraphicModel.CIRCLE_HEIGHT/2, ShapeSize.GraphicModel.CIRCLE_WIDTH, ShapeSize.GraphicModel.CIRCLE_HEIGHT, curNode.content);
        	curNode.shape = shape;
        	if(shape.lx+shape.lw*2 > swidth)
        		swidth = shape.lx + shape.lw*2;
        	if(shape.ly+shape.lh*2 > sheight)
        		sheight = shape.ly+shape.lh*2;
        }
        
        //统计两个节点之间有多少条边
        Map<DsLine, Integer> lineMap = new TreeMap<DsLine, Integer>();
        Map<DsLine, Integer> offDistMap = new TreeMap<DsLine, Integer>(); 
        Map<DsLine, Point> lineOrgPt = new TreeMap<DsLine, Point>();
        for(String nodeKey : mp.keySet()){//连边
        	GraphicNode curNode = mp.get(nodeKey);
        	shapeList.add(curNode.shape);
        	int x11 = curNode.shape.lx+ShapeSize.GraphicModel.CIRCLE_WIDTH/2;
        	int y11 = curNode.shape.ly+ShapeSize.GraphicModel.CIRCLE_HEIGHT/2;
        	for(GraphicEdge edge : curNode.neighbourEdges){
        		GraphicNode nextNode = edge.toNode;
        		int x1 = x11;
        		int y1 = y11;
        		int x2 = nextNode.shape.lx+ShapeSize.GraphicModel.CIRCLE_WIDTH/2;
	        	int y2 = nextNode.shape.ly+ShapeSize.GraphicModel.CIRCLE_HEIGHT/2;
	        	//特殊处理， 不让线段画进  树结点的里面， (x1, y1), (x2, y2), (x1, y2)三点组成三角形，然后有
				// (x1, y1)和 (x2, y2)线段的长度为L， 则有 x1+L*sin@ = x2, y1+L*cos@ = y2;
				double L = Math.sqrt((double)((x2-x1)*(x2-x1) + (y2-y1)*(y2-y1)));
				double sinx = (Math.abs(x2-x1))/L/2;//除以2，因为ShapeSize.TreeModel.CIRCLE_WIDTH是直径，我们要半径
				double cosx = (Math.abs(y2-y1))/L/2;
				int flag = x1 < x2 ? 1 : -1;
				x1 = (int)(x1+ShapeSize.TreeModel.CIRCLE_WIDTH*sinx*flag);
				x2 = (int)(x2-ShapeSize.TreeModel.CIRCLE_WIDTH*sinx*flag);
				flag = y1 > y2 ? -1 : 1;
				y1 = (int)(y1+ShapeSize.TreeModel.CIRCLE_WIDTH*cosx*flag);
				y2 = (int)(y2-ShapeSize.TreeModel.CIRCLE_WIDTH*cosx*flag);
				
				DsLine shapeLine = new DsLine(x1, y1, x2, y2, isDicircleed);
				if(isWeighted) shapeLine.weight = edge.weight;
				shapeList.add(shapeLine);
				edge.lineList.add(shapeLine);
				
				if(!lineOrgPt.containsKey(shapeLine)){
					Point pt = new Point(shapeLine.x1, shapeLine.y1);
					lineOrgPt.put((DsLine) shapeLine.clone(), pt);
				}
				//每条线段的起始端点
				shapeLine.ptOrg = new Point(lineOrgPt.get(shapeLine).x, lineOrgPt.get(shapeLine).y);
				
				if(lineMap.containsKey(shapeLine)){
					lineMap.put(shapeLine, lineMap.get(shapeLine)+1);
					shapeLine.weightInLinePos = lineMap.get(shapeLine);
				} else {
					lineMap.put((DsLine) shapeLine.clone(), 1);
					shapeLine.weightInLinePos = lineMap.get(shapeLine);
				}
        	}
        }
        
        //offDistMap, 节点之间每条边相对于节点圆心之间的偏移距离
        for(Shape shape : shapeList){
        	if(shape instanceof DsLine){
        		//线段被分成多段，在不同的段上填上相应的weight值
        		((DsLine)shape).LineSegments += lineMap.get(shape);
        		
        		if(offDistMap.containsKey(shape)){
        			designLinePos(offDistMap, (DsLine) shape, lineOrgPt);
        		} else {//两节点之间的第一条边
        			int lineCnt = lineMap.get(shape);//得到两个节点之间一共有多少条边
        			offDistMap.put((DsLine)((DsLine)shape).clone(), -lineCnt/2*ShapeSize.GraphicModel.LINES_DIST);
        			designLinePos(offDistMap, (DsLine) shape, lineOrgPt);
        		}
        	}
        }
        
        if(!isDicircleed){//无向图，GraphicEdge的数量变成2倍，但是DsLine的数量不变
        	for(GraphicNode node : nodeList)
        		for(GraphicEdge edge : node.neighbourEdges){
        			GraphicEdge newEdge = new GraphicEdge();
        			newEdge.fromNode = edge.toNode;
        			newEdge.toNode = edge.fromNode;
        			newEdge.lineList.addAll(edge.lineList);
        			edge.toNode.neighbourEdges.add(newEdge);
        		}
        }
        
        //绘制自环弧
        for(String node : selfMp.keySet()){
        	int cntEdge = selfMp.get(node);//计算这个个节点一共有多少个自弧
        	int offDistY = 20;
        	for(int cc = 0; cc < cntEdge; ++cc){
        		DsCircle shape = mp.get(node).shape;
        		DsLine leftL = new DsLine(shape.lx, shape.ly+shape.lh/2, shape.lx+shape.lw/2, shape.ly-offDistY, false);
        		leftL.color = Color.CYAN;
        		shapeList.add(0, leftL);
        		GraphicEdge newEdge = new GraphicEdge();
        		newEdge.lineList.add(leftL);
        		newEdge.fromNode = mp.get(node);
        		newEdge.toNode = mp.get(node);
        		//权值添加到leftL的右上端
        		if(isWeighted) {
	        		leftL.weight = newEdge.weight = selfW.get(node).get(cc);
	        		leftL.setWeightAtLineEnd(new Point(leftL.x1, leftL.y1));
        		}
        		DsLine leftR = new DsLine(shape.lx+shape.lw/2, shape.ly-offDistY, shape.lx+shape.lw, shape.ly+shape.lh/2, isDicircleed);
        		leftR.color = Color.CYAN;
        		shapeList.add(0, leftR);
        		newEdge.lineList.add(leftR);
        		offDistY += 15;
        	}
        }
        Collections.sort(nodeList);
        model.getObserverPanel().setPreferredSize(new Dimension(swidth, sheight));
	}
	//结算两个节点之间每条边所在的平移之后位置
	private void designLinePos(Map<DsLine, Integer> offDistMap, DsLine line, Map<DsLine, Point> lineOrgPt){
		int off_dist = offDistMap.get(line);
		offDistMap.put(line, off_dist+ShapeSize.GraphicModel.LINES_DIST);
		double k = (1.0*line.y2-line.y1)/(line.x2-line.x1);//直线平移方向的斜率
		double angle = Math.abs(Math.atan(k));//并计算出角度
		double sinx = (2*Math.tan(angle/2)/(1+Math.tan(angle/2)*Math.tan(angle/2)));
		double cosx = ((1-Math.tan(angle/2)*Math.tan(angle/2)))/((1+Math.tan(angle/2)*Math.tan(angle/2)));
		Point pt = line.ptOrg;
		int x1 = line.x1, y1 = line.y1;
		//注意，在屏幕中斜线形如 / 表示的是斜率小于0， 斜线形如 \ 表示的是斜率大于0
		if(k > 0.0){//斜率大于0的时候
			line.x1 -= off_dist*sinx;
			line.x2 -= off_dist*sinx;
			line.y1 += off_dist*cosx;
			line.y2 += off_dist*cosx;
		} else {
			line.x1 += off_dist*sinx;
			line.x2 += off_dist*sinx;
			line.y1 += off_dist*cosx;
			line.y2 += off_dist*cosx;
		}
		
		if(pt.x==x1 && pt.y==y1){
			pt.x = line.x1;
			pt.y = line.y1;
		} else {
			pt.x = line.x2;
			pt.y = line.y2;
		}
		
	}
	
	public void BFSGraphic(String data){
		createGraphicData(data);
		sheight += ShapeSize.GraphicModel.CIRCLE_HEIGHT;
		int twidth = (nodeList.size()+1)*ShapeSize.GraphicModel.CIRCLE_WIDTH + ShapeSize.GraphicModel.LEFT_MARGIN + 10*(nodeList.size()-1);
		if(twidth > swidth){
			swidth = twidth;
		}
		model.getObserverPanel().setPreferredSize(new Dimension(swidth, sheight));
		Set<GraphicNode> set = new TreeSet<GraphicNode>();
		
		int sequenceIndex = 0;
		Map<DsCircle, Integer> sequenceMap = new TreeMap<DsCircle, Integer>(new Comparator<DsCircle>() {
			@Override
			public int compare(DsCircle o1, DsCircle o2) {
				return o1.content.compareTo(o2.content);
			}
		});
		for(GraphicNode node : nodeList){
			if(!set.contains(node)){
				Queue<GraphicNode> queue = new LinkedList<GraphicNode>();
				queue.add(node);
				set.add(node);
				sequenceMap.put(node.shape, ++sequenceIndex);
				adjustView(model.getObserverPanel(), node.shape.lx, node.shape.ly);
				tipForSearched(new DsCircle[]{node.shape}, null);
				while(!queue.isEmpty()){
					GraphicNode curNode = queue.poll();
					for(GraphicEdge edge : curNode.neighbourEdges){
						GraphicNode nextNode = edge.toNode;
						if(!set.contains(nextNode)){
							sequenceMap.put(nextNode.shape, ++sequenceIndex);
							tipForSearched(new DsCircle[]{nextNode.shape}, edge.lineList);
							adjustView(model.getObserverPanel(), nextNode.shape.lx, nextNode.shape.ly);
							set.add(nextNode);
							queue.add(nextNode);
						}
					}
				}
			}
		}
		
		this.threadCount = sequenceMap.size();
		int childThread = 0;
		for(Map.Entry<DsCircle, Integer> entry : sequenceMap.entrySet()){
			Thread thread = new Thread(new MyRunning(entry), "childThread" + ++childThread);
			thread.setDaemon(true);
			thread.start();
		}
		//主线程停止，等待所有子线程结束之后在执行。
		synchronized (this) {
			try {
				this.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	class MyRunning implements Runnable{
	    Map.Entry<DsCircle, Integer> entry;
		@Override
		public void run() {
			Random random = new Random();
			int rd = random.nextInt();
			DsCircle circle = (DsCircle) entry.getKey().clone();
			circle.color = Color.GREEN;
			synchronized (Shape.class) { shapeList.add(circle); }
			int index = entry.getValue();
			int ww = ShapeSize.GraphicModel.LEFT_MARGIN + (index-1)*(ShapeSize.GraphicModel.CIRCLE_WIDTH+10); 
			if(rd%2 == 0){
				int hh = (circle.ly + sheight-(int)(1.5*circle.lh))/2;
				moveCircle(circle, hh, DIR_DOWN);
				if(circle.lx < ww)
					moveCircle(circle, ww, DIR_RIGHT);
				else 
					moveCircle(circle, ww, DIR_LEFT);
				moveCircle(circle, sheight-(int)(1.5*circle.lh), DIR_DOWN);
			} else {
				if(circle.lx < ww)
					moveCircle(circle, ww, DIR_RIGHT);
				else 
					moveCircle(circle, ww, DIR_LEFT);
				moveCircle(circle, sheight-(int)(1.5*circle.lh), DIR_DOWN);
			}
			subThread();
		}
		
		public MyRunning(Entry<DsCircle, Integer> entry) {
			super();
			this.entry = entry;
		}
	}
	
	public static final int DIR_LEFT = 1;
	public static final int DIR_RIGHT = 2;
	public static final int DIR_UP = 3;
	public static final int DIR_DOWN = 4;
	private void moveCircle(DsCircle circle, int pos, int dir){
		final int offDistX = 10, offDistY = 10;
		switch(dir){
			case DIR_LEFT:
				while(circle.lx > pos){
					circle.lx -= offDistX;
					if(circle.lx < pos)
						circle.lx = pos;
					model.setViewChanged();
					delay(100);
				}
				break;
			case DIR_RIGHT:
				while(circle.lx < pos){
					circle.lx += offDistX;
					if(circle.lx > pos)
						circle.lx = pos;
					model.setViewChanged();
					delay(100);
				}
				break;
			case DIR_UP:
				while(circle.ly > pos){
					circle.ly -= offDistX;
					if(circle.ly < pos)
						circle.ly = pos;
					model.setViewChanged();
					delay(100);
				}
				break;
			case DIR_DOWN:
				while(circle.ly < pos){
					circle.ly += offDistX;
					if(circle.ly > pos)
						circle.ly = pos;
					model.setViewChanged();
					delay(100);
				}
				break;
			default:
				break;
		}
	}
	
	private int sequenceLeft = ShapeSize.GraphicModel.LEFT_MARGIN;//遍历序列的左端坐标
	private void dfsGrahpic(GraphicNode curNode, Set<GraphicNode> set){
		set.add(curNode);
		DsCircle shape = (DsCircle) curNode.shape.clone();
		shape.lx = sequenceLeft;
		sequenceLeft += shape.lw + 10;
		shape.ly = sheight - (int)(1.5*shape.lh);
		
		shapeList.add(shape);
		adjustView(model.getObserverPanel(), shape.lx, shape.ly);
		tipForSearched(new DsCircle[]{shape}, null);
		for(GraphicEdge edge : curNode.neighbourEdges){
			GraphicNode nextNode = edge.toNode;
			if(!set.contains(nextNode)){
				adjustView(model.getObserverPanel(), nextNode.shape.lx, nextNode.shape.ly);
				tipForSearched(new DsCircle[]{nextNode.shape}, edge.lineList);
				dfsGrahpic(nextNode, set);
			}
		}
	}
	public void DFSGraphic(String data){
		createGraphicData(data);
		sheight += ShapeSize.GraphicModel.CIRCLE_HEIGHT;
		int twidth = (nodeList.size()+1)*ShapeSize.GraphicModel.CIRCLE_WIDTH + ShapeSize.GraphicModel.LEFT_MARGIN + 10*(nodeList.size()-1);
		if(twidth > swidth){
			swidth = twidth;
		}
		model.getObserverPanel().setPreferredSize(new Dimension(swidth, sheight));
		Set<GraphicNode> set = new TreeSet<GraphicNode>();
		for(GraphicNode node : nodeList){
			if(!set.contains(node)){
				adjustView(model.getObserverPanel(), node.shape.lx, node.shape.ly);
				tipForSearched(new DsCircle[]{node.shape}, null);
				dfsGrahpic(node, set);
			}
		}
	}
	private void tipForSearched(DsCircle[] shapes, List<DsLine> lines){
		boolean flag = true;
		Color color = null;
		for(int k=1; k<=4; ++k){
			color = flag ? Color.GREEN : Color.RED;
			for(DsCircle shape : shapes)
				shape.color = color;
			if(lines != null){
				synchronized (Shape.class) {
					if(flag){
						shapeList.removeAll(lines);
					} else {
						shapeList.addAll(0, lines);
					}
				}
			}
			flag = !flag;
			model.setViewChanged();
			delay(300);
		}
	}
	
	private void tipForUpdate(DsSampleRect[] shapes){
		boolean flag = true;
		Color color = null;
		if(shapes.length != 3) return;
		for(int k=1; k<=4; ++k){
			color = flag ? Color.GREEN : Color.RED;
			shapes[0].color = shapes[1].color = color;
			color = flag ? Color.GREEN : Color.WHITE;
			shapes[2].color = color;
			flag = !flag;
			model.setViewChanged();
			delay(300);
		}
		
	}
	
	private void adjustView(JPanel pane, int x, int y){
		if(!(pane.getParent().getParent() instanceof JScrollPane)) return;
		JScrollPane scrollpaen = (JScrollPane) pane.getParent().getParent();
		JScrollBar hsb = scrollpaen.getHorizontalScrollBar();
		JScrollBar vsb = scrollpaen.getVerticalScrollBar();
		JViewport view = scrollpaen.getViewport();
		//返回一个矩形，其原位置在 getViewPosition，大小为 getExtentSize。这是视图在视图坐标中的可见部分。 
		Rectangle circle = view.getViewRect();
		int movex = x - (circle.x+circle.width/2);
		int movey = y - (circle.y+circle.height/2);
		hsb.setValue(hsb.getValue() + movex);
		vsb.setValue(vsb.getValue() + movey);
	}
	
	private void delay(int time){
		try {
			TimeUnit.MILLISECONDS.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	//迪杰斯特拉算法
	public void dijkstra(String data){
		createGraphicData(data);
		if(nodeList.size() == 0) return;
		if(this.isWeighted == false) return;
		//记录每个节点的索引
		Map<GraphicNode, Integer> nodeIndex = new TreeMap<GraphicNode, Integer>();
		//二维数组，直观的显示源点到各个点的最短距离
		ArrayList<ArrayList<DsSampleRect> > arrDist = new ArrayList<ArrayList<DsSampleRect>>();
		arrDist.add(new ArrayList<DsSampleRect>());
		int leftDist = ShapeSize.GraphicModel.LEFT_MARGIN;
		int topDist = sheight;
		sheight += ShapeSize.GraphicModel.CIRCLE_HEIGHT*2;
		model.getObserverPanel().setPreferredSize(new Dimension(swidth, sheight));
		for(int i=0; i<nodeList.size(); ++i){
			nodeIndex.put(nodeList.get(i), i);
			DsSampleRect rect = new DsSampleRect(leftDist, topDist, ShapeSize.GraphicModel.SMALL_RECT_WIDTH, ShapeSize.GraphicModel.SMALL_RECT_HEIGHT, nodeList.get(i).content);
			rect.color = Color.RED;
			rect.fontSize = 20;
			shapeList.add(rect);
			arrDist.get(0).add(rect);
			if(i == 0){
				leftDist += ShapeSize.GraphicModel.SMALL_RECT_WIDTH*2;
			} else {
				leftDist += ShapeSize.GraphicModel.SMALL_RECT_WIDTH;
			}
		}
		//源点到每一个节点的最短的距离
		Map<GraphicNode, Integer> dist = new TreeMap<GraphicNode, Integer>();
		//标识某个节点是否访问过
		Set<GraphicNode> vis = new TreeSet<GraphicNode>();
		for(GraphicNode node : nodeList)
			dist.put(node, Integer.MAX_VALUE);
		GraphicNode root = nodeList.get(0);
		dist.put(root, 0);
		vis.add(root);
		for(int i=1; i < nodeList.size(); ++i){//更新 （n-1）次
			Thread thread = new Thread(new RootRunning(root), "childThread" + i);
			thread.start();
			//初始化没有更新前 源节点 到各个节点的最短的距离
			arrDist.add(new ArrayList<DsSampleRect>());
			leftDist = ShapeSize.GraphicModel.LEFT_MARGIN;
			topDist += ShapeSize.GraphicModel.SMALL_RECT_HEIGHT;
			sheight += ShapeSize.GraphicModel.SMALL_RECT_HEIGHT;
			model.getObserverPanel().setPreferredSize(new Dimension(swidth, sheight));
			for(int j=0; j<nodeList.size(); ++j){
				int nodesDist = dist.get(nodeList.get(j));
				DsSampleRect rect = new DsSampleRect(leftDist, topDist, ShapeSize.GraphicModel.SMALL_RECT_WIDTH, ShapeSize.GraphicModel.SMALL_RECT_HEIGHT, nodesDist == Integer.MAX_VALUE ? "∞" : String.valueOf(nodesDist));
				rect.color = Color.WHITE;
				rect.fontSize = 20;
				synchronized (ShapeSize.class) {shapeList.add(rect);}
				arrDist.get(i).add(rect);
				if(j == 0){
					leftDist += ShapeSize.GraphicModel.SMALL_RECT_WIDTH*2;
				} else {
					leftDist += ShapeSize.GraphicModel.SMALL_RECT_WIDTH;
				}
			}
			//从中间向两边展现
			DsSampleRect topRect = new DsSampleRect(ShapeSize.GraphicModel.LEFT_MARGIN, topDist, leftDist-ShapeSize.GraphicModel.LEFT_MARGIN, ShapeSize.GraphicModel.SMALL_RECT_HEIGHT/2, null);
			DsSampleRect downRect = new DsSampleRect(ShapeSize.GraphicModel.LEFT_MARGIN, topDist+ShapeSize.GraphicModel.SMALL_RECT_HEIGHT/2, leftDist-ShapeSize.GraphicModel.LEFT_MARGIN, ShapeSize.GraphicModel.SMALL_RECT_HEIGHT-ShapeSize.GraphicModel.SMALL_RECT_HEIGHT/2, null);
			topRect.color = downRect.color = model.getObserverPanel().getBackground();
			synchronized (ShapeSize.class) {
				shapeList.add(topRect);
				shapeList.add(downRect);
			}
			final int offDistY = 3;
			while(topRect.lh >= 0 || downRect.lh >= 0){
				if(topRect.lh >= 0){
					topRect.lh -= offDistY;
				}
				if(downRect.lh >= 0){
					downRect.ly += offDistY;
					downRect.lh -= offDistY;
				}
				delay(200);
			}
			synchronized (ShapeSize.class) {
				shapeList.remove(topRect);
				shapeList.remove(downRect);
			}
			int minDist = Integer.MAX_VALUE;
			//找到新的根节点
			GraphicNode newRoot = null;
			//新节点与旧节点之间唯一的一条边
			GraphicEdge selectEdge = null;
			for(GraphicEdge edge : root.neighbourEdges){
				GraphicNode to = edge.toNode;
				int weight = Integer.parseInt(edge.lineList.get(0).weight);
				if(!vis.contains(to) && dist.get(to) > weight + dist.get(root)){
					dist.put(to, dist.get(root) + weight);
					for(DsLine line : edge.lineList)
						line.color = Color.YELLOW;
					
					tipForUpdate(new DsSampleRect[]{arrDist.get(0).get(0), arrDist.get(0).get(nodeIndex.get(to)), arrDist.get(i).get(nodeIndex.get(to))});
					arrDist.get(i).get(nodeIndex.get(to)).content = String.valueOf(dist.get(to));
				}
				if(!vis.contains(to) && minDist > dist.get(to)){
					newRoot = to;
					minDist = dist.get(to);
					selectEdge = edge;
				}
			}
			thread.stop();
			
			if(newRoot != null){
				root.shape.color = Color.GREEN;
				root = newRoot;
				vis.add(root);
				for(DsLine line : selectEdge.lineList)
					line.color = Color.CYAN;
			} else {
				break;
			}
		}
		
		model.setViewChanged();
	}
	
	//prim算法
	public void prim(String data){
		createGraphicData(data);
		if(nodeList.size() == 0) return;
		if(this.isWeighted == false) return;
		//源点到每一个节点的最短的距离
		Map<GraphicNode, Integer> dist = new TreeMap<GraphicNode, Integer>();
		//标识某个节点是否访问过
		Set<GraphicNode> vis = new TreeSet<GraphicNode>();
		for(GraphicNode node : nodeList)
			dist.put(node, Integer.MAX_VALUE);
		GraphicNode root = nodeList.get(0);
		dist.put(root, 0);
		vis.add(root);
		for(int i=1; i < nodeList.size(); ++i){//更新 （n-1）次
			Thread thread = new Thread(new RootRunning(root), "childThread" + i);
			thread.start();
			int minDist = Integer.MAX_VALUE;
			//找到新的根节点
			GraphicNode newRoot = null;
			//新节点与旧节点之间唯一的一条边
			GraphicEdge selectEdge = null;
			for(GraphicEdge edge : root.neighbourEdges){
				GraphicNode to = edge.toNode;
				int weight = Integer.parseInt(edge.lineList.get(0).weight);
				if(!vis.contains(to) && dist.get(to) > weight){
					dist.put(to, weight);
					for(DsLine line : edge.lineList)
						line.color = Color.YELLOW;
				}
				if(!vis.contains(to) && minDist > dist.get(to)){
					newRoot = to;
					minDist = dist.get(to);
					selectEdge = edge;
				}
			}
			
			if(newRoot != null){
				root.shape.color = Color.GREEN;
				root = newRoot;
				vis.add(root);
				for(DsLine line : selectEdge.lineList)
					line.color = Color.CYAN;
			}
			thread.stop();
		}
		
		model.setViewChanged();
	}
	
	private GraphicNode getFather(Map<GraphicNode, GraphicNode> f, GraphicNode x){
		return f.get(x) == x ? x : f.put(x, getFather(f, f.get(x)));
	}
	
	private void union(Map<GraphicNode, GraphicNode> f, GraphicNode a, GraphicNode b){
		GraphicNode fa = getFather(f, a);
		GraphicNode fb = getFather(f, b);
		if(fa != fb){
			f.put(fb, fa);
		}
	}
	
	public void kruskal(String data){
		createGraphicData(data);
		if(nodeList.size() == 0) return;
		if(this.isWeighted == false) return;
		Map<GraphicNode, GraphicNode> f = new TreeMap<GraphicNode, GraphicNode>();
		ArrayList<GraphicEdge> edgeList = new ArrayList<GraphicEdge>();
		for(GraphicNode node : nodeList) {
			edgeList.addAll(node.neighbourEdges);
			f.put(node, node);
		}
		Collections.sort(edgeList, new Comparator<GraphicEdge>() {
			@Override
			public int compare(GraphicEdge o1, GraphicEdge o2) {
				return Integer.parseInt(o1.weight) - Integer.parseInt(o2.weight);
			}
		});
		
		for(GraphicEdge edge : edgeList){
			union(f, edge.fromNode, edge.toNode);
		}
	}
	
	//弗洛伊德算法
	public void floyd(String data){
		createGraphicData(data);
		if(nodeList.size() == 0) return;
		if(this.isWeighted == false) return;
		Map<TwoGraphicNode, Integer> g = new TreeMap<TwoGraphicNode, Integer>();
		for(GraphicNode from : nodeList)
			for(GraphicNode to : nodeList){
				g.put(new TwoGraphicNode(from, to), Integer.MAX_VALUE);
			}
		
		for(GraphicNode node : nodeList){
			TwoGraphicNode x = new TwoGraphicNode(node, node);
			if(g.get(x) == Integer.MAX_VALUE)
				g.put(x, 0);
		}
		
		for(GraphicNode k : nodeList)
			for(GraphicNode i : nodeList)
				for(GraphicNode j : nodeList){
					TwoGraphicNode ik = new TwoGraphicNode(i, k);
					TwoGraphicNode kj = new TwoGraphicNode(k, j);
					TwoGraphicNode ij = new TwoGraphicNode(i, j);
					if(g.get(ij) > g.get(ik)+g.get(kj))
						g.put(ij, g.get(ik)+g.get(kj));
				}
	}
	
	class RootRunning implements Runnable{
		private GraphicNode root;
		private boolean run = true;
		public void stop(){
			run = false;
		}
		@Override
		public void run() {
			boolean flag = true;
			while(run){
				if(flag) {
					root.shape.color = Color.GREEN;
				} else { 
					root.shape.color = Color.RED;
				}
				flag = !flag;
				model.setViewChanged();
				delay(300);
			}
			root.shape.color = Color.RED;
		}
		public RootRunning(GraphicNode root) {
			super();
			this.root = root;
		}
	}
	
	public GraphicModel(DrawModel model, Boolean isDicircleed, Boolean isWeighted) {
		super();
		this.model = model;
		this.isDicircleed = isDicircleed;
		this.isWeighted = isWeighted;
		this.shapeList = model.getShapeList();
	}
}

class TwoGraphicNode implements Comparable<TwoGraphicNode>{
	public GraphicNode from;
	public GraphicNode to;
	private String newContent;
	public TwoGraphicNode(GraphicNode from, GraphicNode to) {
		super();
		this.from = from;
		this.to = to;
		newContent = from.content + to.content;
	}
	@Override
	public int compareTo(TwoGraphicNode o) {
		return this.newContent.compareTo(o.newContent);
	}
}