package com.ds.model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import com.ds.shape.DsCircle;
import com.ds.shape.DsLine;
import com.ds.shape.Shape;
import com.ds.size.ShapeSize;

public class GraphicModel{
	private DrawModel model;
	private boolean isDirected;//是否为有向图
	private boolean isWeighted;//是否带权图
	/**
	 * @param data 例如"1 2,3 4", 表示的是1节点和2节点有一条边，3和4节点有一条边
	 */
	public void createGraphicData(String data){
		ArrayList<Shape> shapeList = model.getShapeList();
		String[] edges = data.split(",");
		Map<String, GraphicNode> mp = new TreeMap<String, GraphicNode>();
		//可以通过mp.size()获得图中一共有多少个结点
		for(String edge : edges){
			String[] nodes = edge.split(" ");
			if(!mp.containsKey(nodes[0])){
				GraphicNode newNode = new GraphicNode();
				newNode.content = nodes[0];
				mp.put(nodes[0], newNode);
			}
			if(!mp.containsKey(nodes[1])){
				GraphicNode newNode = new GraphicNode();
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
				
				DsLine shapeLine = new DsLine(x1, y1, x2, y2, isDirected);
				if(isWeighted) shapeLine.weight = edge.weight;
				shapeList.add(shapeLine);
				
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
	
	public GraphicModel(DrawModel model, Boolean isDirected, Boolean isWeighted) {
		super();
		this.model = model;
		this.isDirected = isDirected;
		this.isWeighted = isWeighted;
	}
}
