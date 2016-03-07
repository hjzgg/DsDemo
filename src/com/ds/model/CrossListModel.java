package com.ds.model;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

import com.ds.shape.DsCrossList;
import com.ds.shape.DsLine;
import com.ds.shape.Shape;
import com.ds.size.ShapeSize;

public class CrossListModel{
	//节点编号从1开始
	private ArrayList<VexNode> vexNodes = null;
	private Set<Edge> edges = new TreeSet<Edge>();
	private boolean isDirected;
	private Color[] colors = {Color.RED, Color.BLACK, Color.BLUE, Color.GREEN, Color.ORANGE, Color.YELLOW, Color.DARK_GRAY, Color.CYAN, Color.MAGENTA};
	private int colorIndex = 0;
	private DrawModel model;
	/**
	 * @param data	节点数 边数;1 2;2 3;....	以";"区分
	 */
	public void createCrossListData(String data){
		ArrayList<Shape> shapeList = model.getShapeList();
		String[] datas = data.split(";");
		if(datas.length == 0) return;
		String[] nums = datas[0].split(" ");
		int nodeCnt = Integer.valueOf(nums[0]);
		int edgeCnt = Integer.valueOf(nums[1]);
		vexNodes = new ArrayList<VexNode>();
		for(int i=0; i<=nodeCnt; ++i){
			VexNode vexNode = new VexNode(i);
			if(i != 0){
				int lx = ShapeSize.CrossListModel.LEFT_MARGIN;
				int ly = ShapeSize.CrossListModel.TOP_MARGIN + (i-1)*(ShapeSize.CrossListModel.NODE_HEIGHT+ShapeSize.CrossListModel.NODES_VER_DIST);
				DsCrossList shape = new DsCrossList(lx, ly, ShapeSize.CrossListModel.NODE_VECTOR_WIDTH, ShapeSize.CrossListModel.NODE_HEIGHT);
				shape.s1 = "N"+i;
				shapeList.add(shape);
				vexNode.shape = shape;
			}
			vexNodes.add(vexNode);
		}
		if(datas.length-1 != edgeCnt)
			throw new IllegalArgumentException("边数不符!");
		for(int i=1; i<datas.length; ++i){
			String[] edge = datas[i].split(" ");
			int u = Integer.valueOf(edge[0]);
			int v = Integer.valueOf(edge[1]);
			edges.add(new Edge(u, v));
			if(!isDirected)
				edges.add(new Edge(v, u));
		}
		for(Edge edge : edges)
			buildCrossList(edge);
	}
	
	private void buildCrossList(Edge edge){
		ArrayList<Shape> shapeList = model.getShapeList();
		ArcBoxNode p = new ArcBoxNode();
		p.tailVex = edge.u;
	    p.headVex = edge.v;
	    int lx = ShapeSize.CrossListModel.LEFT_MARGIN+ShapeSize.CrossListModel.NODE_VECTOR_WIDTH+ShapeSize.CrossListModel.NODES_HOR_DIST + (edge.v-1)*(ShapeSize.CrossListModel.NODES_HOR_DIST+ShapeSize.CrossListModel.NODE_ARCBOX_WIDTH);
	    int ly = ShapeSize.CrossListModel.TOP_MARGIN + (edge.u-1)*(ShapeSize.CrossListModel.NODE_HEIGHT+ShapeSize.CrossListModel.NODES_VER_DIST);
	    DsCrossList shape = new DsCrossList(lx, ly, ShapeSize.CrossListModel.NODE_ARCBOX_WIDTH, ShapeSize.CrossListModel.NODE_HEIGHT);
	    shape.s1 = String.valueOf(edge.u);
	    shape.s2 = String.valueOf(edge.v);
	    p.shape = shape;
	    shapeList.add(shape);
	    
	    VexNode vexNodeU = vexNodes.get(edge.u);
	    if(vexNodeU.firstout == null){//在弧尾的地方插入 
	    	vexNodeU.shape.s3 = null;
	    	vexNodeU.firstout = p;
	    	//添加连线
	    	DsLine line = new DsLine((int)(vexNodeU.shape.lx+vexNodeU.shape.lw*(5.0/6)), vexNodeU.shape.ly+vexNodeU.shape.lh/2,
	    								p.shape.lx, p.shape.ly+p.shape.lh/2, true);
	    	shapeList.add(line);
	    } else {
	    	ArcBoxNode tmp = vexNodeU.firstout;
	        while(tmp.tlink != null) tmp = tmp.tlink;//找到和u节点相关的最后一个弧尾 
	        tmp.tlink = p; 
	        tmp.shape.s4 = null;
	        //添加连线
	    	DsLine line = new DsLine((int)(tmp.shape.lx+tmp.shape.lw*(7.0/8)), tmp.shape.ly+tmp.shape.lh/2,
	    								p.shape.lx, p.shape.ly+p.shape.lh/2, true);
	    	shapeList.add(line);
	    }
	    
	    VexNode vexNodeV = vexNodes.get(edge.v);
	    {
	    	DsLine line1 = null, line2 = null, line3 = null;
		    if(vexNodeV.firstin == null){//在弧头的地方插入 
		    	vexNodeV.firstin = p;
		    	vexNodeV.shape.s2 = null;
		    	
		    	//添加连线
		    	line1 = new DsLine(vexNodeV.shape.lx+vexNodeV.shape.lw/2, vexNodeV.shape.ly+vexNodeV.shape.lh/2, 
		    						vexNodeV.shape.lx+vexNodeV.shape.lw/2, vexNodeV.shape.ly+vexNodeV.shape.lh+ShapeSize.CrossListModel.NODES_VER_DIST/2, false);
		    	line2 = new DsLine(vexNodeV.shape.lx+vexNodeV.shape.lw/2, vexNodeV.shape.ly+vexNodeV.shape.lh+ShapeSize.CrossListModel.NODES_VER_DIST/2,
		    						p.shape.lx+p.shape.lw/8, vexNodeV.shape.ly+vexNodeV.shape.lh+ShapeSize.CrossListModel.NODES_VER_DIST/2, false);
		    	line3 = new DsLine(p.shape.lx+p.shape.lw/8, vexNodeV.shape.ly+vexNodeV.shape.lh+ShapeSize.CrossListModel.NODES_VER_DIST/2,
		    						p.shape.lx+p.shape.lw/8, vexNodeV.shape.ly < p.shape.ly ? p.shape.ly : p.shape.ly+p.shape.lh, true);
		    } else {	
		    	ArcBoxNode tmp = vexNodeV.firstin;
		        while(tmp.hlink != null) tmp = tmp.hlink;//找到和u节点相关的最后一个弧头 
		        tmp.hlink = p; 
		        tmp.shape.s3 = null; 
		        
		      //添加连线
		    	line1 = new DsLine((int)(tmp.shape.lx+tmp.shape.lw*(5.0/8)), tmp.shape.ly+tmp.shape.lh/2, 
		    						(int)(tmp.shape.lx+tmp.shape.lw*(5.0/8)), tmp.shape.ly+tmp.shape.lh+ShapeSize.CrossListModel.NODES_VER_DIST/2, false);
		    	line2 = new DsLine((int)(tmp.shape.lx+tmp.shape.lw*(5.0/8)), tmp.shape.ly+tmp.shape.lh+ShapeSize.CrossListModel.NODES_VER_DIST/2,
		    						p.shape.lx+p.shape.lw/8, tmp.shape.ly+tmp.shape.lh+ShapeSize.CrossListModel.NODES_VER_DIST/2, false);
		    	line3 = new DsLine(p.shape.lx+p.shape.lw/8, tmp.shape.ly+tmp.shape.lh+ShapeSize.CrossListModel.NODES_VER_DIST/2,
		    						p.shape.lx+p.shape.lw/8, tmp.shape.ly < p.shape.ly ? p.shape.ly : p.shape.ly+p.shape.lh, true);
		    }
		    Color color = colors[colorIndex<colors.length ? colorIndex++ : (colorIndex=0)];
		    line1.color = color;
		    line2.color = color;
		    line3.color = color;
		    shapeList.add(line1);
		    shapeList.add(line2);
		    shapeList.add(line3);
	    }
	}

	class Edge implements Comparable<Edge>{
		public int u;
		public int v;
		public Edge(int u, int v) {
			super();
			this.u = u;
			this.v = v;
		}
		
		@Override
		public int compareTo(Edge tmp) {
			if(tmp.u == u && tmp.v == v) return 0;
			if(u == tmp.u) return v-tmp.v;
			return u-tmp.u;
		}

		@Override
		public String toString() {
			return u + "," + v;
		}
		
	}

	public CrossListModel(DrawModel model, Boolean isDirected) {
		super();
		this.model = model;
		this.isDirected = isDirected;
	}
	
}
