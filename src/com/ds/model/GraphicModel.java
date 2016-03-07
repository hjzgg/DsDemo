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
	private boolean isDirected;//�Ƿ�Ϊ����ͼ
	private boolean isWeighted;//�Ƿ��Ȩͼ
	/**
	 * @param data ����"1 2,3 4", ��ʾ����1�ڵ��2�ڵ���һ���ߣ�3��4�ڵ���һ����
	 */
	public void createGraphicData(String data){
		ArrayList<Shape> shapeList = model.getShapeList();
		String[] edges = data.split(",");
		Map<String, GraphicNode> mp = new TreeMap<String, GraphicNode>();
		//����ͨ��mp.size()���ͼ��һ���ж��ٸ����
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
        }
        
        //ͳ�������ڵ�֮���ж�������
        Map<DsLine, Integer> lineMap = new TreeMap<DsLine, Integer>();
        Map<DsLine, Integer> offDistMap = new TreeMap<DsLine, Integer>(); 
        Map<DsLine, Point> lineOrgPt = new TreeMap<DsLine, Point>();
        for(String nodeKey : mp.keySet()){//����
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
				if(isWeighted) shapeLine.weight = edge.weight;
				shapeList.add(shapeLine);
				
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
	
	public GraphicModel(DrawModel model, Boolean isDirected, Boolean isWeighted) {
		super();
		this.model = model;
		this.isDirected = isDirected;
		this.isWeighted = isWeighted;
	}
}
