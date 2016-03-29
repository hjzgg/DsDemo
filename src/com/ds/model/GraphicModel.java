package com.ds.model;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
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
import com.ds.shape.DsSampleCircle;
import com.ds.shape.DsSampleRect;
import com.ds.shape.Shape;
import com.ds.size.ShapeSize;

public class GraphicModel{
	private DrawModel model;
	private boolean isDirected;//�Ƿ�Ϊ����ͼ
	private boolean isWeighted;//�Ƿ��Ȩͼ
	ArrayList<Shape> shapeList;
	
	private int threadCount;
	
	private void subThread(){
		synchronized (this) {
			--threadCount;
			if(threadCount == 0){
				//�������߳�
				this.notify();
			}
		}
	}
	
	//չʾ�ռ�ĸ߶ȺͿ��
	private int swidth = 0;
	private int sheight = 0;
	
	private List<GraphicNode> nodeList = new ArrayList<GraphicNode>();
	/**
	 * @param data ����"1 2,3 4", ��ʾ����1�ڵ��2�ڵ���һ���ߣ�3��4�ڵ���һ����
	 */
	public void createGraphicData(String data){
		String[] edges = data.split(",");
		Map<String, GraphicNode> mp = new TreeMap<String, GraphicNode>();
		//���Ի������д���
		Map<String, Integer> selfMp = new TreeMap<String, Integer>();
		//�Ի�����Ȩֵ
		Map<String, List<String>> selfW = new TreeMap<String, List<String>>();
		//
		//����ͨ��mp.size()���ͼ��һ���ж��ٸ����
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
		
		int nodeCnt = mp.size();//n���͵Ľڵ�ĸ���
		double angle = 2*Math.PI/nodeCnt;//ÿ�������ĽǶ�
		double r = ShapeSize.GraphicModel.NODES_DIST/2 / Math.sin(angle/2);//�뾶
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
        for(String nodeKey : mp.keySet()){//����n���ͽ���ͼ�Ľڵ�
        	GraphicNode curNode = mp.get(nodeKey);
        	Point pt = ps.get(i++);
        	DsCircle shape = new DsCircle(pt.x-ShapeSize.GraphicModel.CIRCLE_WIDTH/2, pt.y-ShapeSize.GraphicModel.CIRCLE_HEIGHT/2, ShapeSize.GraphicModel.CIRCLE_WIDTH, ShapeSize.GraphicModel.CIRCLE_HEIGHT, curNode.content);
        	curNode.shape = shape;
        	if(shape.lx+shape.lw*2 > swidth)
        		swidth = shape.lx + shape.lw*2;
        	if(shape.ly+shape.lh*2 > sheight)
        		sheight = shape.ly+shape.lh*2;
        }
        
        //ͳ�������ڵ�֮���ж�������
        Map<DsLine, Integer> lineMap = new TreeMap<DsLine, Integer>();
        Map<DsLine, Integer> offDistMap = new TreeMap<DsLine, Integer>(); 
        Map<DsLine, Point> lineOrgPt = new TreeMap<DsLine, Point>();
        for(String nodeKey : mp.keySet()){//����
        	GraphicNode curNode = mp.get(nodeKey);
        	shapeList.add(curNode.shape);
        	for(GraphicEdge edge : curNode.neighbourEdges){
        		GraphicNode nextNode = edge.toNode;
				DsLine shapeLine = createLine(curNode.shape, nextNode.shape);
				if(isWeighted) shapeLine.weight = edge.weight;
				shapeList.add(shapeLine);
				edge.lineList.add(shapeLine);
				
				if(!lineOrgPt.containsKey(shapeLine)){
					Point pt = new Point(shapeLine.x1, shapeLine.y1);
					lineOrgPt.put((DsLine) shapeLine.clone(), pt);
				}
				//ÿ���߶ε���ʼ�˵�
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
        
        //offDistMap, �ڵ�֮��ÿ��������ڽڵ�Բ��֮���ƫ�ƾ���
        for(Shape shape : shapeList){
        	if(shape instanceof DsLine){
        		//�߶α��ֳɶ�Σ��ڲ�ͬ�Ķ���������Ӧ��weightֵ
        		((DsLine)shape).LineSegments += lineMap.get(shape);
        		
        		if(offDistMap.containsKey(shape)){
        			designLinePos(offDistMap, (DsLine) shape, lineOrgPt);
        		} else {//���ڵ�֮��ĵ�һ����
        			int lineCnt = lineMap.get(shape);//�õ������ڵ�֮��һ���ж�������
        			offDistMap.put((DsLine)((DsLine)shape).clone(), -lineCnt/2*ShapeSize.GraphicModel.LINES_DIST);
        			designLinePos(offDistMap, (DsLine) shape, lineOrgPt);
        		}
        	}
        }
        
        if(!isDirected){//����ͼ��GraphicEdge���������2��������DsLine����������
        	for(GraphicNode node : nodeList)
        		for(GraphicEdge edge : node.neighbourEdges){
        			if(edge.isNewAdd) continue;
        			GraphicEdge newEdge = new GraphicEdge();
        			newEdge.fromNode = edge.toNode;
        			newEdge.toNode = edge.fromNode;
        			newEdge.weight = edge.weight;
        			newEdge.isNewAdd = true;
        			newEdge.lineList.addAll(edge.lineList);
        			edge.toNode.neighbourEdges.add(newEdge);
        		}
        }
        
        //�����Ի���
        for(String node : selfMp.keySet()){
        	int cntEdge = selfMp.get(node);//����������ڵ�һ���ж��ٸ��Ի�
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
        		//Ȩֵ��ӵ�leftL�����϶�
        		if(isWeighted) {
	        		leftL.weight = newEdge.weight = selfW.get(node).get(cc);
	        		leftL.setWeightAtLineEnd(new Point(leftL.x1, leftL.y1));
        		}
        		DsLine leftR = new DsLine(shape.lx+shape.lw/2, shape.ly-offDistY, shape.lx+shape.lw, shape.ly+shape.lh/2, isDirected);
        		leftR.color = Color.CYAN;
        		shapeList.add(0, leftR);
        		newEdge.lineList.add(leftR);
        		offDistY += 15;
        	}
        }
        Collections.sort(nodeList);
        model.getObserverPanel().setPreferredSize(new Dimension(swidth, sheight));
	}
	
	private DsLine createLine(DsCircle shapeOne, DsCircle shapeTwo){
		int x1 = shapeOne.lx+ShapeSize.GraphicModel.CIRCLE_WIDTH/2;
		int y1 = shapeOne.ly+ShapeSize.GraphicModel.CIRCLE_HEIGHT/2;
		int x2 = shapeTwo.lx+ShapeSize.GraphicModel.CIRCLE_WIDTH/2;
    	int y2 = shapeTwo.ly+ShapeSize.GraphicModel.CIRCLE_HEIGHT/2;
    	//���⴦�� �����߶λ���  ���������棬 (x1, y1), (x2, y2), (x1, y2)������������Σ�Ȼ����
		// (x1, y1)�� (x2, y2)�߶εĳ���ΪL�� ���� x1+L*sin@ = x2, y1+L*cos@ = y2;
		double L = Math.sqrt((double)((x2-x1)*(x2-x1) + (y2-y1)*(y2-y1)));
		double sinx = (Math.abs(x2-x1))/L/2;//����2����ΪShapeSize.TreeModel.CIRCLE_WIDTH��ֱ��������Ҫ�뾶
		double cosx = (Math.abs(y2-y1))/L/2;
		int flag = x1 < x2 ? 1 : -1;
		x1 = (int)(x1+ShapeSize.TreeModel.CIRCLE_WIDTH*sinx*flag);
		x2 = (int)(x2-ShapeSize.TreeModel.CIRCLE_WIDTH*sinx*flag);
		flag = y1 > y2 ? -1 : 1;
		y1 = (int)(y1+ShapeSize.TreeModel.CIRCLE_WIDTH*cosx*flag);
		y2 = (int)(y2-ShapeSize.TreeModel.CIRCLE_WIDTH*cosx*flag);
		
		DsLine shapeLine = new DsLine(x1, y1, x2, y2, isDirected);
		return shapeLine;
	}
	
	//���������ڵ�֮��ÿ�������ڵ�ƽ��֮��λ��
	private void designLinePos(Map<DsLine, Integer> offDistMap, DsLine line, Map<DsLine, Point> lineOrgPt){
		int off_dist = offDistMap.get(line);
		offDistMap.put(line, off_dist+ShapeSize.GraphicModel.LINES_DIST);
		double k = (1.0*line.y2-line.y1)/(line.x2-line.x1);//ֱ��ƽ�Ʒ����б��
		double angle = Math.abs(Math.atan(k));//��������Ƕ�
		double sinx = (2*Math.tan(angle/2)/(1+Math.tan(angle/2)*Math.tan(angle/2)));
		double cosx = ((1-Math.tan(angle/2)*Math.tan(angle/2)))/((1+Math.tan(angle/2)*Math.tan(angle/2)));
		Point pt = line.ptOrg;
		int x1 = line.x1, y1 = line.y1;
		//ע�⣬����Ļ��б������ / ��ʾ����б��С��0�� б������ \ ��ʾ����б�ʴ���0
		if(k > 0.0){//б�ʴ���0��ʱ��
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
		//���߳�ֹͣ���ȴ��������߳̽���֮����ִ�С�
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
	
	private int sequenceLeft = ShapeSize.GraphicModel.LEFT_MARGIN;//�������е��������
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
		//����һ�����Σ���ԭλ���� getViewPosition����СΪ getExtentSize��������ͼ����ͼ�����еĿɼ����֡� 
		Rectangle circle = view.getViewRect();
		int movex = x - (circle.x+circle.width/2);
		int movey = y - (circle.y+circle.height/2);
		hsb.setValue(hsb.getValue() + movex);
		vsb.setValue(vsb.getValue() + movey);
		scrollpaen.revalidate();
	}
	
	private void delay(int time){
		try {
			TimeUnit.MILLISECONDS.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	//�Ͻ�˹�����㷨
	public void dijkstra(String data){
		createGraphicData(data);
		if(nodeList.size() == 0) return;
		if(this.isWeighted == false) return;
		//������Ϣ��ʾ
		DsSampleRect tipOneRect = new DsSampleRect(swidth, ShapeSize.GraphicModel.TOP_MARGIN, ShapeSize.GraphicModel.SMALL_RECT_WIDTH*10, ShapeSize.GraphicModel.SMALL_RECT_HEIGHT, "���� : �������");
		DsSampleRect tipTwoRect = new DsSampleRect(swidth, ShapeSize.GraphicModel.TOP_MARGIN*2, ShapeSize.GraphicModel.SMALL_RECT_WIDTH*10, ShapeSize.GraphicModel.SMALL_RECT_HEIGHT, "���� : ������²��ҵ���Դ����ڵ�");
		tipOneRect.color = tipTwoRect.color = model.getObserverPanel().getBackground();
		tipOneRect.fontSize = tipTwoRect.fontSize = 20;
		shapeList.add(tipOneRect);
		shapeList.add(tipTwoRect);
		//���������ڵ㣬����ָʾ������µ���ʾ
		DsCircle circleV = new DsCircle(swidth, tipTwoRect.ly+tipTwoRect.lh*2, ShapeSize.GraphicModel.CIRCLE_WIDTH, ShapeSize.GraphicModel.CIRCLE_HEIGHT, "");
		DsCircle circleU = new DsCircle(swidth, circleV.ly+circleV.lh*4, ShapeSize.GraphicModel.CIRCLE_WIDTH, ShapeSize.GraphicModel.CIRCLE_HEIGHT, "");
		DsCircle circleR = new DsCircle(circleU.lx+circleU.lw*3, circleU.ly+circleU.lh*2, ShapeSize.GraphicModel.CIRCLE_WIDTH, ShapeSize.GraphicModel.CIRCLE_HEIGHT, nodeList.get(0).content);
		DsSampleRect compareRect = new DsSampleRect(circleR.lx, circleR.ly-circleR.lh*3, ShapeSize.GraphicModel.CIRCLE_WIDTH*4, ShapeSize.GraphicModel.CIRCLE_HEIGHT, "");
		compareRect.color = Color.CYAN;
		shapeList.add(compareRect);
		DsLine lineUV = createLine(circleU, circleV);
		lineUV.weight = "��";
		lineUV.setDefaultLine(new Point(lineUV.x1+5, lineUV.y1));
		shapeList.add(lineUV);
		DsLine lineRU = createLine(circleR, circleU);
		lineRU.weight = "��";
		lineRU.setDefaultLine(new Point(lineRU.x1+5, lineRU.y1));
		shapeList.add(lineRU);
		DsLine lineRV = createLine(circleR, circleV);
		lineRV.weight = "��";
		lineRV.setDefaultLine(new Point(lineRV.x1+5, lineRV.y1));
		shapeList.add(lineRV);
		
		circleR.color = Color.WHITE;
		shapeList.add(circleR);
		circleU.color = Color.GREEN;
		shapeList.add(circleU);
		shapeList.add(circleV);
		swidth += tipTwoRect.lw + ShapeSize.GraphicModel.CIRCLE_WIDTH; 
		//��¼ÿ���ڵ������
		Map<GraphicNode, Integer> nodeIndex = new TreeMap<GraphicNode, Integer>();
		//��ά���飬ֱ�۵���ʾԴ�㵽���������̾���
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
		//Դ�㵽ÿһ���ڵ����̵ľ���
		Map<GraphicNode, Integer> dist = new TreeMap<GraphicNode, Integer>();
		//��ʶĳ���ڵ��Ƿ���ʹ�
		Set<GraphicNode> vis = new TreeSet<GraphicNode>();
		//���½ڵ��ʱ�򣬼�¼�Ǳ���һ���߸��µ�
		Map<GraphicNode, GraphicEdge> nodeToUpdateEdge = new TreeMap<GraphicNode, GraphicEdge>();
		for(GraphicNode node : nodeList)
			dist.put(node, Integer.MAX_VALUE);
		GraphicNode root = nodeList.get(0);
		dist.put(root, 0);
		vis.add(root);
		for(int i=1; i < nodeList.size(); ++i){//���� ��n-1����
			Thread thread = new Thread(new RootRunning(root), "childThread" + i);
			thread.start();
			//��ʼ��û�и���ǰ Դ�ڵ� �������ڵ����̵ľ���
			arrDist.add(new ArrayList<DsSampleRect>());
			leftDist = ShapeSize.GraphicModel.LEFT_MARGIN;
			topDist += ShapeSize.GraphicModel.SMALL_RECT_HEIGHT;
			sheight += ShapeSize.GraphicModel.SMALL_RECT_HEIGHT;
			model.getObserverPanel().setPreferredSize(new Dimension(swidth, sheight));
			for(int j=0; j<nodeList.size(); ++j){
				int nodesDist = dist.get(nodeList.get(j));
				DsSampleRect rect = new DsSampleRect(leftDist, topDist, ShapeSize.GraphicModel.SMALL_RECT_WIDTH, ShapeSize.GraphicModel.SMALL_RECT_HEIGHT, nodesDist == Integer.MAX_VALUE ? "��" : String.valueOf(nodesDist));
				rect.color = Color.WHITE;
				rect.fontSize = 20;
				synchronized (Shape.class) {shapeList.add(rect);}
				arrDist.get(i).add(rect);
				if(j == 0){
					leftDist += ShapeSize.GraphicModel.SMALL_RECT_WIDTH*2;
				} else {
					leftDist += ShapeSize.GraphicModel.SMALL_RECT_WIDTH;
				}
			}
			//���м�������չ��
			DsSampleRect topRect = new DsSampleRect(ShapeSize.GraphicModel.LEFT_MARGIN, topDist, leftDist-ShapeSize.GraphicModel.LEFT_MARGIN, ShapeSize.GraphicModel.SMALL_RECT_HEIGHT/2, null);
			DsSampleRect downRect = new DsSampleRect(ShapeSize.GraphicModel.LEFT_MARGIN, topDist+ShapeSize.GraphicModel.SMALL_RECT_HEIGHT/2, leftDist-ShapeSize.GraphicModel.LEFT_MARGIN, ShapeSize.GraphicModel.SMALL_RECT_HEIGHT-ShapeSize.GraphicModel.SMALL_RECT_HEIGHT/2, null);
			topRect.color = downRect.color = model.getObserverPanel().getBackground();
			synchronized (Shape.class) {
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
			synchronized (Shape.class) {
				shapeList.remove(topRect);
				shapeList.remove(downRect);
			}
			int minDist = Integer.MAX_VALUE;
			//�ҵ��µĸ��ڵ�
			GraphicNode newRoot = null;
			//�½ڵ���ɽڵ�֮��Ψһ��һ����
			GraphicEdge selectEdge = null;
			for(GraphicEdge edge : root.neighbourEdges){
				adjustView(model.getObserverPanel(), root.shape.lx, root.shape.ly);
				GraphicNode to = edge.toNode;
				int weight = Integer.parseInt(edge.lineList.get(0).weight);
				
				if(!vis.contains(to)) {
					lineAppearAndDisAppear(edge.lineList.toArray(new DsLine[]{}));
					//����ģ��
					try {
						Thread tv = null, tu = null;
						DsSampleCircle cv = null, cu = null;
						if(!circleV.content.equals(to.content)){
							 cv = new DsSampleCircle(to.shape.lx, to.shape.ly, to.shape.lw, to.shape.lh, to.content);
							 cv.setTransparent(true);
							 synchronized (Shape.class) { shapeList.add(cv); }
							 tv = new Thread(new NumberMoving(cv, circleV), "childThread_circleV");
							 tv.start();
						}
						
						if(!circleU.content.equals(root.content)){
							cu = new DsSampleCircle(root.shape.lx, root.shape.ly, root.shape.lw, root.shape.lh, root.content);
							cu.setTransparent(true);
							synchronized (Shape.class) { shapeList.add(cu); }
							tu = new Thread(new NumberMoving(cu, circleU), "childThread_circleU");
							tu.start();
						}
						if(tv != null) tv.join();
						if(tu != null) tu.join();
						if(cv != null) synchronized (Shape.class) { shapeList.remove(cv); }
						if(cu != null) synchronized (Shape.class) { shapeList.remove(cu); }
						
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
					lineUV.weight = String.valueOf(weight);
					lineRU.weight = String.valueOf(dist.get(root));
					lineRV.weight = dist.get(to) == Integer.MAX_VALUE ? "��" : String.valueOf(dist.get(to));
					lineAppearAndDisAppear(new DsLine[]{lineUV, lineRU, lineRV});
					
					compareRect.content = lineUV.weight + "+" + lineRU.weight + (dist.get(to) > weight + dist.get(root) ? " < " : " >= ") + lineRV.weight;
					tipForUpdate(new DsSampleRect[]{compareRect});
				}
				
				if(!vis.contains(to) && dist.get(to) > weight + dist.get(root)){
					for(DsLine line : edge.lineList)
						line.color = Color.YELLOW;
					nodeToUpdateEdge.put(to, edge);
					dist.put(to, dist.get(root) + weight);
					//��̬����
					DsSampleCircle numOne = new DsSampleCircle(lineUV.getContentPoint().x, lineUV.getContentPoint().y, ShapeSize.GraphicModel.CIRCLE_WIDTH/2, ShapeSize.GraphicModel.CIRCLE_HEIGHT/2, lineUV.weight);
					numOne.fontSize = 25;
					numOne.setTransparent(true);
					DsSampleCircle numTwo = new DsSampleCircle(lineRU.getContentPoint().x, lineRU.getContentPoint().y, ShapeSize.GraphicModel.CIRCLE_WIDTH/2, ShapeSize.GraphicModel.CIRCLE_HEIGHT/2, lineRU.weight);
					numTwo.fontSize = 25;
					numTwo.setTransparent(true);
					synchronized (Shape.class) {
						shapeList.add(numOne);
						shapeList.add(numTwo);
					}
					try {
						Thread twu = new Thread(new NumberMoving(numOne, lineRV.getContentPoint().x, lineRV.getContentPoint().y));
						Thread twv = new Thread(new NumberMoving(numTwo, lineRV.getContentPoint().x, lineRV.getContentPoint().y));
						twu.start(); twv.start();
						twu.join();
						twv.join();
						lineRV.weight = String.valueOf(dist.get(to));
						lineAppearAndDisAppear(new DsLine[]{lineRV});
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					adjustView(model.getObserverPanel(), arrDist.get(0).get(0).lx, arrDist.get(0).get(0).ly);
					tipForUpdate(new DsSampleRect[]{arrDist.get(0).get(0), arrDist.get(0).get(nodeIndex.get(to)), arrDist.get(i).get(nodeIndex.get(to))});
					arrDist.get(i).get(nodeIndex.get(to)).content = String.valueOf(dist.get(to));
					model.setViewChanged();
					delay(1000);
				}
			}
			for(GraphicNode oneNode : nodeList){
				if(!vis.contains(oneNode) && minDist > dist.get(oneNode)){
					newRoot = oneNode;
					minDist = dist.get(oneNode);
					selectEdge = nodeToUpdateEdge.get(oneNode);
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
	
	private void lineAppearAndDisAppear(DsLine[] lines){
		boolean flag = true;
		for(int i=1; i<=4; ++i){
			if(flag){
				synchronized (Shape.class) {
					shapeList.removeAll(Arrays.asList(lines));
				}
			} else {
				synchronized (Shape.class) {
					shapeList.addAll(Arrays.asList(lines));
				}
			}
			flag = !flag;
			delay(300);
		}
	}
	
	class NumberMoving implements Runnable{
		private DsSampleCircle circle;
		private DsCircle target = null;
		private int tx, ty;
		public NumberMoving(DsSampleCircle circle, DsCircle target){
			this.circle = circle;
			this.target = target;
		}
		public NumberMoving(DsSampleCircle circle, int tx, int ty){
			this.circle = circle;
			this.tx = tx;
			this.ty = ty;
		}
		@Override
		public void run() {
			if(target != null) {
				if(circle.lx < target.lx)
					moveNumbers(circle, target.lx, DIR_RIGHT);
				else 
					moveNumbers(circle, target.lx, DIR_LEFT);
				
				if(circle.ly < target.ly)
					moveNumbers(circle, target.ly, DIR_DOWN);
				else 
					moveNumbers(circle, target.ly, DIR_UP);
				target.content = circle.content;
			} else {
				if(circle.lx < tx)
					moveCircle(circle, tx, DIR_RIGHT);
				else 
					moveCircle(circle, tx, DIR_LEFT);
				
				if(circle.ly < ty)
					moveCircle(circle, ty, DIR_DOWN);
				else 
					moveCircle(circle, ty, DIR_UP);
				synchronized (Shape.class) {
					shapeList.remove(circle);
				}
			}
		}
	}
	
	private void moveNumbers(DsSampleCircle circle, int pos, int dir){
		final int offDistX = 20, offDistY = 10;
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
	
	//�õ�������������
	private String firstPrim(){
		StringBuilder content = new StringBuilder();
		//Դ�㵽ÿһ���ڵ����̵ľ���
		Map<GraphicNode, Integer> dist = new TreeMap<GraphicNode, Integer>();
		//��ǽڵ��Ƿ񱻷��ʹ�
		Set<GraphicNode> vis = new TreeSet<GraphicNode>();
		for(GraphicNode node : nodeList)
			dist.put(node, Integer.MAX_VALUE);
		GraphicNode root = nodeList.get(0);
		dist.put(root, 0);
		vis.add(root);
		for(int i=1; i < nodeList.size(); ++i){//���� ��n-1����
			int minDist = Integer.MAX_VALUE;
			//�ҵ��µĸ��ڵ�
			GraphicNode newRoot = null;
			//�½ڵ���ɽڵ�֮��Ψһ��һ����
			GraphicEdge selectEdge = null;
			for(GraphicEdge edge : root.neighbourEdges){
				GraphicNode to = edge.toNode;
				int weight = Integer.parseInt(edge.lineList.get(0).weight);
				if(!vis.contains(to) && dist.get(to) > weight){
					for(DsLine line : edge.lineList)
						line.color = Color.YELLOW;
					dist.put(to, weight);
				}
			}
			
			for(GraphicNode oneNode : nodeList){
				if(!vis.contains(oneNode) && minDist > dist.get(oneNode)){
					newRoot = oneNode;
					minDist = dist.get(oneNode);
				}
			}
			
			if(newRoot != null){
				content.append(root.content + " " + newRoot.content).append(";");
				root = newRoot;
				vis.add(root);
			} 
		}
		if(content.charAt(content.length()-1) == ';')
			content.deleteCharAt(content.length()-1);
		return content.toString();
	}
	
	//prim�㷨
	public void prim(String data){
		createGraphicData(data);
		if(nodeList.size() == 0) return;
		if(this.isWeighted == false) return;
		
		//������Ϣ��ʾ
		DsSampleRect tipOneRect = new DsSampleRect(swidth, ShapeSize.GraphicModel.TOP_MARGIN, ShapeSize.GraphicModel.SMALL_RECT_WIDTH*10, ShapeSize.GraphicModel.SMALL_RECT_HEIGHT, "���� : �������");
		DsSampleRect tipTwoRect = new DsSampleRect(swidth, ShapeSize.GraphicModel.TOP_MARGIN*2, ShapeSize.GraphicModel.SMALL_RECT_WIDTH*10, ShapeSize.GraphicModel.SMALL_RECT_HEIGHT, "���� : ������²��ҵ���Դ����ڵ�");
		tipOneRect.color = tipTwoRect.color = model.getObserverPanel().getBackground();
		tipOneRect.fontSize = tipTwoRect.fontSize = 20;
		shapeList.add(tipOneRect);
		shapeList.add(tipTwoRect);
		//���������ڵ㣬����ָʾ������µ���ʾ
		DsCircle circleV = new DsCircle(swidth, ShapeSize.GraphicModel.TOP_MARGIN*2+ShapeSize.GraphicModel.SMALL_RECT_HEIGHT*2, ShapeSize.GraphicModel.CIRCLE_WIDTH, ShapeSize.GraphicModel.CIRCLE_HEIGHT, "");
		DsCircle circleU = new DsCircle(swidth, circleV.ly+circleV.lh*4, ShapeSize.GraphicModel.CIRCLE_WIDTH, ShapeSize.GraphicModel.CIRCLE_HEIGHT, "");
		DsCircle circleR = new DsCircle(circleU.lx+circleU.lw*3, circleU.ly+circleU.lh*2, ShapeSize.GraphicModel.CIRCLE_WIDTH, ShapeSize.GraphicModel.CIRCLE_HEIGHT, nodeList.get(0).content);
		DsSampleRect compareRect = new DsSampleRect(circleR.lx, circleR.ly-circleR.lh*3, ShapeSize.GraphicModel.CIRCLE_WIDTH*4, ShapeSize.GraphicModel.CIRCLE_HEIGHT, "");
		compareRect.color = Color.CYAN;
		shapeList.add(compareRect);
		DsLine lineUV = createLine(circleU, circleV);
		lineUV.weight = "��";
		lineUV.setDefaultLine(new Point(lineUV.x1+5, lineUV.y1));
		shapeList.add(lineUV);
		DsLine lineRU = createLine(circleR, circleU);
		shapeList.add(lineRU);
		DsLine lineRV = createLine(circleR, circleV);
		lineRV.weight = "��";
		lineRV.setDefaultLine(new Point(lineRV.x1+5, lineRV.y1));
		shapeList.add(lineRV);
		
		//�� circleR �� circleUȦ��������ʾ��һ��������
		//DsLine rectTopLine = new DsLine(circleU.lx, y1, x2, y2, false);
		
		circleR.color = Color.GREEN;
		shapeList.add(circleR);
		circleU.color = Color.GREEN;
		shapeList.add(circleU);
		shapeList.add(circleV);
		swidth += circleR.lx + circleR.lw*2; 
		//��¼ÿ���ڵ������
		Map<GraphicNode, Integer> nodeIndex = new TreeMap<GraphicNode, Integer>();
		//��ά���飬ֱ�۵���ʾԴ�㵽���������̾���
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
		
		//���Ƚ�����С��������ģ��
		String content = firstPrim();
		System.out.println(content);
		ForestModel forest = new ForestModel(model);
		forest.setPrimTreeLeft(leftDist+ShapeSize.GraphicModel.SMALL_RECT_WIDTH*2);
		forest.setPrimTreeTop(circleR.lx+circleR.lh*2);
		PrimTreeNodeNeed primTreeNodeNeed = forest.getForestModel(content);
		
		//Դ�㵽ÿһ���ڵ����̵ľ���
		Map<GraphicNode, Integer> dist = new TreeMap<GraphicNode, Integer>();
		//��ʶĳ���ڵ��Ƿ���ʹ�
		Set<GraphicNode> vis = new TreeSet<GraphicNode>();
		//���½ڵ��ʱ�򣬼�¼�Ǳ���һ���߸��µ�
		Map<GraphicNode, GraphicEdge> nodeToUpdateEdge = new TreeMap<GraphicNode, GraphicEdge>();
		for(GraphicNode node : nodeList)
			dist.put(node, Integer.MAX_VALUE);
		GraphicNode root = nodeList.get(0);
		dist.put(root, 0);
		vis.add(root);
		shapeList.add(primTreeNodeNeed.nodeToShape.get(root.content));
		for(int i=1; i < nodeList.size(); ++i){//���� ��n-1����
			Thread thread = new Thread(new RootRunning(root), "childThread" + i);
			thread.start();
			//��ʼ��û�и���ǰ Դ�ڵ� �������ڵ����̵ľ���
			arrDist.add(new ArrayList<DsSampleRect>());
			leftDist = ShapeSize.GraphicModel.LEFT_MARGIN;
			topDist += ShapeSize.GraphicModel.SMALL_RECT_HEIGHT;
			sheight += ShapeSize.GraphicModel.SMALL_RECT_HEIGHT;
			model.getObserverPanel().setPreferredSize(new Dimension(swidth, sheight));
			for(int j=0; j<nodeList.size(); ++j){
				int nodesDist = dist.get(nodeList.get(j));
				DsSampleRect rect = new DsSampleRect(leftDist, topDist, ShapeSize.GraphicModel.SMALL_RECT_WIDTH, ShapeSize.GraphicModel.SMALL_RECT_HEIGHT, nodesDist == Integer.MAX_VALUE ? "��" : String.valueOf(nodesDist));
				rect.color = Color.WHITE;
				rect.fontSize = 20;
				synchronized (Shape.class) {shapeList.add(rect);}
				arrDist.get(i).add(rect);
				if(j == 0){
					leftDist += ShapeSize.GraphicModel.SMALL_RECT_WIDTH*2;
				} else {
					leftDist += ShapeSize.GraphicModel.SMALL_RECT_WIDTH;
				}
			}
			//���м�������չ��
			DsSampleRect topRect = new DsSampleRect(ShapeSize.GraphicModel.LEFT_MARGIN, topDist, leftDist-ShapeSize.GraphicModel.LEFT_MARGIN, ShapeSize.GraphicModel.SMALL_RECT_HEIGHT/2, null);
			DsSampleRect downRect = new DsSampleRect(ShapeSize.GraphicModel.LEFT_MARGIN, topDist+ShapeSize.GraphicModel.SMALL_RECT_HEIGHT/2, leftDist-ShapeSize.GraphicModel.LEFT_MARGIN, ShapeSize.GraphicModel.SMALL_RECT_HEIGHT-ShapeSize.GraphicModel.SMALL_RECT_HEIGHT/2, null);
			topRect.color = downRect.color = model.getObserverPanel().getBackground();
			synchronized (Shape.class) {
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
			synchronized (Shape.class) {
				shapeList.remove(topRect);
				shapeList.remove(downRect);
			}
			int minDist = Integer.MAX_VALUE;
			//�ҵ��µĸ��ڵ�
			GraphicNode newRoot = null;
			//�½ڵ���ɽڵ�֮��Ψһ��һ����
			GraphicEdge selectEdge = null;
			for(GraphicEdge edge : root.neighbourEdges){
				adjustView(model.getObserverPanel(), root.shape.lx, root.shape.ly);
				GraphicNode to = edge.toNode;
				int weight = Integer.parseInt(edge.lineList.get(0).weight);
				
				if(!vis.contains(to)) {
					lineAppearAndDisAppear(edge.lineList.toArray(new DsLine[]{}));
					//����ģ��
					try {
						Thread tv = null, tu = null;
						DsSampleCircle cv = null, cu = null;
						if(!circleV.content.equals(to.content)){
							 cv = new DsSampleCircle(to.shape.lx, to.shape.ly, to.shape.lw, to.shape.lh, to.content);
							 cv.setTransparent(true);
							 synchronized (Shape.class) { shapeList.add(cv); }
							 tv = new Thread(new NumberMoving(cv, circleV), "childThread_circleV");
							 tv.start();
						}
						
						if(!circleU.content.equals(root.content)){
							cu = new DsSampleCircle(root.shape.lx, root.shape.ly, root.shape.lw, root.shape.lh, root.content);
							cu.setTransparent(true);
							synchronized (Shape.class) { shapeList.add(cu); }
							tu = new Thread(new NumberMoving(cu, circleU), "childThread_circleU");
							tu.start();
						}
						if(tv != null) tv.join();
						if(tu != null) tu.join();
						if(cv != null) synchronized (Shape.class) { shapeList.remove(cv); }
						if(cu != null) synchronized (Shape.class) { shapeList.remove(cu); }
						
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
					lineUV.weight = String.valueOf(weight);
					lineRV.weight = dist.get(to) == Integer.MAX_VALUE ? "��" : String.valueOf(dist.get(to));
					lineAppearAndDisAppear(new DsLine[]{lineUV, lineRV});
					
					compareRect.content = lineUV.weight + (dist.get(to) > weight + dist.get(root) ? " < " : " >= ") + lineRV.weight;
					tipForUpdate(new DsSampleRect[]{compareRect});
				}
				
				if(!vis.contains(to) && dist.get(to) > weight){
					for(DsLine line : edge.lineList)
						line.color = Color.YELLOW;
					dist.put(to, weight);
					//��ǽڵ������µ�ʱ������һ�����й�
					nodeToUpdateEdge.put(to, edge);
					//��̬����
					DsSampleCircle numOne = new DsSampleCircle(lineUV.getContentPoint().x, lineUV.getContentPoint().y, ShapeSize.GraphicModel.CIRCLE_WIDTH/2, ShapeSize.GraphicModel.CIRCLE_HEIGHT/2, lineUV.weight);
					numOne.fontSize = 25;
					numOne.setTransparent(true);
					synchronized (Shape.class) { shapeList.add(numOne); }
					try {
						Thread twu = new Thread(new NumberMoving(numOne, lineRV.getContentPoint().x, lineRV.getContentPoint().y));
						twu.start();
						twu.join();
						lineRV.weight = String.valueOf(dist.get(to));
						lineAppearAndDisAppear(new DsLine[]{lineRV});
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					adjustView(model.getObserverPanel(), arrDist.get(0).get(0).lx, arrDist.get(0).get(0).ly);
					tipForUpdate(new DsSampleRect[]{arrDist.get(0).get(0), arrDist.get(0).get(nodeIndex.get(to)), arrDist.get(i).get(nodeIndex.get(to))});
					arrDist.get(i).get(nodeIndex.get(to)).content = String.valueOf(dist.get(to));
					model.setViewChanged();
					delay(1000);
				}
			}
			
			for(GraphicNode oneNode : nodeList){
				if(!vis.contains(oneNode) && minDist > dist.get(oneNode)){
					newRoot = oneNode;
					minDist = dist.get(oneNode);
					selectEdge = nodeToUpdateEdge.get(oneNode);
				}
			}
			
			if(newRoot != null){
				GraphicNode oldRoot = root;
				root.shape.color = Color.GREEN;
				root = newRoot;
				vis.add(root);
				for(DsLine line : selectEdge.lineList)
					line.color = Color.CYAN;
				synchronized (Shape.class) {
					shapeList.add(primTreeNodeNeed.nodeToShape.get(newRoot.content));
					shapeList.add(primTreeNodeNeed.nodesLine.get(new TwoNodes(root.content, newRoot.content)));
				}
			} 
			
			thread.stop();
		}
		//������û��ѡ�еıߣ���̬��ʧ
		ArrayList<Thread> threadList = new ArrayList<Thread>();
		Set<DsLine> lineSet = new TreeSet<DsLine>();
		int lineIndex = 0;
		for(GraphicNode oneNode : nodeList){
			for(GraphicEdge edge : oneNode.neighbourEdges){
				for(DsLine line : edge.lineList){
					if(!lineSet.contains(line) && line.color != Color.CYAN) {//��������ѡ�еıߣ���ʧ
						lineSet.add(line);
						Thread lineThead = new Thread(new DynamicLineDisAppear(line), "childThreadLineDisappear" + ++lineIndex);
						threadList.add(lineThead);
						lineThead.start();
					}
				}
			}
		}
		//����ÿ��Thread�������Ȼ��һ�����join�����Ż�������
		try {
			for(Thread lineThread : threadList)
				lineThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		model.setViewChanged();
	}
	
	private void adjustLinePtOrg(Boolean ptOrg1Or2, DsLine line){
		if(ptOrg1Or2 != null) {
			if(ptOrg1Or2) {
				line.ptOrg.x = line.x1;
				line.ptOrg.y = line.y1;
			}else {
				line.ptOrg.x = line.x2;
				line.ptOrg.y = line.y2;
			}
		} 
	}
	
	private void lineDisappear(DsLine line){
		double k = (1.0*line.y2-line.y1)/(line.x2-line.x1);
		double b = line.y1-k*line.x1;
		int offDist = 5;
		Boolean ptOrg1Or2 = null;
		if(line.ptOrg != null){
			if(line.ptOrg.equals(new Point(line.x1, line.y1)))
				ptOrg1Or2 = true;
			else 
				ptOrg1Or2 = false;
		}
		if(Math.abs(k) < 1.0) {// xֵ�ݼ�
			if(line.x1 < line.x2){
				while(line.x1 < line.x2){
					line.x1 += offDist;
					line.x2 -= offDist;
					if(line.x1 > line.x2)
						break;
					line.y1 = (int) (k*line.x1 + b);
					line.y2 = (int) (k*line.x2 + b);
					adjustLinePtOrg(ptOrg1Or2, line);
					model.setViewChanged();
					delay(100);
				}
			} else {
				while(line.x1 > line.x2){
					line.x1 -= offDist;
					line.x2 += offDist;
					if(line.x1 < line.x2)
						break;
					line.y1 = (int) (k*line.x1 + b);
					line.y2 = (int) (k*line.x2 + b);
					adjustLinePtOrg(ptOrg1Or2, line);
					model.setViewChanged();
					delay(100);
				}
			} 
		} else {
			if(line.y1 < line.y2){
				while(line.y1 < line.y2){
					line.y1 += offDist;
					line.y2 -= offDist;
					if(line.y1 > line.y2)
						break;
					line.x1 = (int)((line.y1-b)/k);
					line.x2 = (int)((line.y2-b)/k);
					adjustLinePtOrg(ptOrg1Or2, line);
					model.setViewChanged();
					delay(100);
				}
			} else {
				while(line.y1 > line.y2){
					line.y1 -= offDist;
					line.y2 += offDist;
					if(line.y1 > line.y2)
						break;
					line.x1 = (int)((line.y1-b)/k);
					line.x2 = (int)((line.y2-b)/k);
					adjustLinePtOrg(ptOrg1Or2, line);
					model.setViewChanged();
					delay(100);
				}
			} 
		}
	}
	
	class DynamicLineDisAppear implements Runnable{
		private DsLine line;
		
		@Override
		public void run() {
			lineDisappear(line);
			synchronized (Shape.class) {
				shapeList.remove(line);
			}
		}
		
		public DynamicLineDisAppear(DsLine line) {
			super();
			this.line = line;
		}
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
	
	//���������㷨
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
	
	public GraphicModel(DrawModel model, Boolean isDirected, Boolean isWeighted) {
		super();
		this.model = model;
		this.isDirected = isDirected;
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

class TwoNodes implements Comparable<TwoNodes>{
	private String nodeOne;
	private String nodeTwo;
	public TwoNodes(String nodeOne, String nodeTwo) {
		super();
		this.nodeOne = nodeOne;
		this.nodeTwo = nodeTwo;
	}
	@Override
	public int compareTo(TwoNodes o) {
		return (nodeOne.hashCode()+nodeTwo.hashCode()) - (o.nodeOne.hashCode()+o.nodeTwo.hashCode());
	}
}

class PrimTreeNodeNeed {
	//ÿ���ڵ��Ӧ������ͼ��
	Map<String, Shape> nodeToShape = new TreeMap<String, Shape>();
	//����ÿ�����ڵ�֮��ı�
	Map<TwoNodes, DsLine> nodesLine = new TreeMap<TwoNodes, DsLine>();
}