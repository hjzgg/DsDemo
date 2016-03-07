package com.ds.model;

import java.awt.Color;
import java.awt.dnd.DragSourceListener;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import javax.swing.JPanel;
import javax.swing.JTextPane;

import com.ds.panel.DrawPanel;
import com.ds.shape.DsBIndexedTreeRect;
import com.ds.shape.DsCircle;
import com.ds.shape.DsCrossList;
import com.ds.shape.DsGListRect;
import com.ds.shape.DsLine;
import com.ds.shape.DsListRect;
import com.ds.shape.DsNumberRect;
import com.ds.shape.DsRect;
import com.ds.shape.DsSegTreeRect;
import com.ds.shape.Shape;
import com.ds.size.ShapeSize;

public class DrawModel extends Observable{
	private ArrayList<Shape> shapeList = new ArrayList<Shape>();
	private DrawPanel observerPanel;
	
	public DrawPanel getObserverPanel(){
		return observerPanel;
	}
	
	public void setViewChanged(){
		setChanged();
		notifyObservers();
	}
	
	public Object getModel(String modelName, Object[] args){
		shapeList.clear();
		Object obj = null;
		try {
			Class<?> cls = Class.forName("com.ds.model." + modelName);
			Constructor<?> con = null;
			Class<?>[] classes = new Class<?>[args.length];
			for(int i=0; i<args.length; ++i)
				classes[i] = args[i].getClass();
			con = cls.getConstructor(classes);
			obj = con.newInstance(args);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return obj;
	}

	public ArrayList<Shape> getShapeList() {
		return shapeList;
	}

	@Override
	public synchronized void addObserver(Observer obj) {
		if(obj instanceof DrawPanel)
			this.observerPanel = (DrawPanel)obj;
		else 
			this.observerPanel = null;
		super.addObserver(obj);
	}
}




class BinaryIndexedTreeNode{
	public Integer data;
	public DsBIndexedTreeRect shape;
	public BinaryIndexedTreeNode(Integer data, DsBIndexedTreeRect shape) {
		super();
		this.data = data;
		this.shape = shape;
	}
}

///十字链表的结构体
class ArcBoxNode{
	public int tailVex, headVex;//该弧的尾和头顶点的位置 
    public ArcBoxNode hlink, tlink;//分别为弧头相同和弧尾相同的弧的链域 
    public DsCrossList shape = null;
    public ArcBoxNode(){
        hlink = null;
        tlink = null;
    }
}

class VexNode{
	public int tag;
    public ArcBoxNode firstin, firstout;
    public DsCrossList shape = null;
    public VexNode(int tag){
    	this.tag = tag;
        firstin = null;
        firstout = null;
    } 
}

///////

class GLNode{
	//tag 为true表示是一个独立的节点，
    public boolean tag;
    public String content;
    public GLNodePtr ptr;
    public DsGListRect shape;
    public static final boolean NODE = true;
    public static final boolean LIST = false;
    
    public GLNode(){
    	ptr = new GLNodePtr();
    }
    
    class GLNodePtr{
    	public GLNode hp, tp;
    	public GLNodePtr(){
    		hp = tp = null;
    	}
    }
};

class SegmentTreeNode{
	public SegmentTreeNode lchild;
	public SegmentTreeNode rchild;
	public int ld;
	public int rd;
	public DsSegTreeRect shape;
	public String content;
	public SegmentTreeNode(String content, int ld, int rd){
		lchild = rchild = null;
		shape = null;
		this.ld = ld;
		this.rd = rd;
		this.content = content;
	}
}

class TreeNode implements Comparable<TreeNode>{
	public TreeNode lchild;
	public TreeNode rchild;
	public DsCircle shape;
	public ArrayList<DsLine> lineList;
	public String content;
	public TreeNode(){
		lchild = rchild = null;
		shape = null;
		content = null;
		lineList = new ArrayList<DsLine>();
	}
	
	public TreeNode(String content) {
		lchild = rchild = null;
		shape = null;
		this.content = content;
		lineList = new ArrayList<DsLine>();
	}
	
	@Override
	public int compareTo(TreeNode tmp) {
		return this.content.compareTo(tmp.content);
	}
}

class GraphicEdge{
	public String weight;
	public GraphicNode fromNode;
	public GraphicNode toNode;
	public GraphicEdge(){
		weight = null;
		fromNode = null;
		toNode = null;
	}
}

class GraphicNode{
	public ArrayList<GraphicEdge> neighbourEdges;
	public String content;
	public DsCircle shape;
	public GraphicNode(){
		neighbourEdges = new ArrayList<GraphicEdge>();
		content = null;
		shape = null;
	}
}

class ArrayNode{
	public String content;
	public DsRect shape;
	public ArrayList<DsLine> lineList;//和该节点相关的连线
	public ArrayNode(String content){
		this.content = content;
		shape = null;
		lineList = new ArrayList<DsLine>();
	}
}

class ListNode{
	public String content;
	public DsListRect shape;
	public ArrayList<DsLine> lineList;//和该节点相关的连线
	public ListNode(String content){
		this.content = content;
		shape = null;
		lineList = new ArrayList<DsLine>();
	}
}

class ForestNode{
	public String content;
	public DsCircle shape = null;
	public ArrayList<ForestNode> childList;
	public ArrayList<DsLine> lineList;//和该节点相关的连线
	public ForestNode(String content){
		this.content = content;
		childList = new ArrayList<ForestNode>();
		lineList = new ArrayList<DsLine>();
	}
}

class HuffmanTreeNode{
	public String content;
	public String nodeTip;
	public DsCircle shape = null;
	public ArrayList<HuffmanTreeNode> childList;
	public ArrayList<DsLine> lineList;//和该节点相关的连线
	public HuffmanTreeNode(String content, String nodeTip){
		this.content = content;
		this.nodeTip = nodeTip;
		childList = new ArrayList<HuffmanTreeNode>();
		lineList = new ArrayList<DsLine>();
	}
}

class StringNode{
	public String content;
	public DsRect shape = null;
	public StringNode(String content) {
		super();
		this.content = content;
	}
}

class SortNode implements Cloneable, Comparable<SortNode>{
	public String content;
	public DsRect shape = null;
	public SortNode(String content) {
		super();
		this.content = content;
	}
	@Override
	public Object clone() {
		SortNode obj = new SortNode(this.content);
		obj.shape = (DsRect) this.shape.clone();
		return obj;
	}
	
	@Override
	public int compareTo(SortNode tmp) {
		return this.hashCode() - tmp.hashCode();
	}
	
}

class BucketNode{
	public Stack<DsRect> bucket;//桶, 桶里装的是节点对应的图形
	public int bucketHeight;//桶的剩余高度
	public int bucketLeft;//桶的左边界
	public BucketNode(Stack<DsRect> bucket, int bucketHeight, int bucketLeft) {
		super();
		this.bucket = bucket;
		this.bucketHeight = bucketHeight;
		this.bucketLeft = bucketLeft;
	}
}

class FormulaNode{
	public String content;
	public DsRect shape;
	public FormulaNode(String content, DsRect shape) {
		super();
		this.content = content;
		this.shape = shape;
	}
}

class StackList{
	private int bottom;
	public DsLine lr;
	public DsLine lb;
	public DsLine ll;
	public DsRect msg;
	public ArrayList<DsRect> rects;
	
	private DrawModel model;
	public StackList(DrawModel model) {
		super();
		this.model = model;
		bottom = ShapeSize.FormulaModel.STACK_TOP_MARGIN+ShapeSize.FormulaModel.RECT_HEIGHT;
		rects = new ArrayList<DsRect>();
	}
	
	class MyTask extends TimerTask{
		private DsRect rect;
		private int pos;
		
		private void delay(int time){
			try {
				TimeUnit.MILLISECONDS.sleep(time);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		@Override
		public void run() {
			final int offDistY = 5;
			if(rect.ly < pos){
				while(rect.ly < pos){
					rect.ly += offDistY;
					if(rect.ly > pos) 
						rect.ly = pos;
					model.setViewChanged();
					delay(100);
				}
			} else {
				while(rect.ly > pos){
					rect.ly -= offDistY;
					if(rect.ly < pos) 
						rect.ly = pos;
					model.setViewChanged();
					delay(100);
				}
			}
		}
		public MyTask(DsRect rect, int pos) {
			super();
			this.rect = rect;
			this.pos = pos;
		}
	}
	
	public void addAdjust(){
		if((rects.size()+1)*ShapeSize.FormulaModel.RECT_HEIGHT+ShapeSize.FormulaModel.STACK_TOP_MARGIN > bottom){
			bottom += ShapeSize.FormulaModel.RECT_HEIGHT;
			msg.ly += ShapeSize.FormulaModel.RECT_HEIGHT;
			addAdjustStack();
		}
	}
	
	public void deleteAdjust(){
		if(rects.size()*ShapeSize.FormulaModel.RECT_HEIGHT+ShapeSize.FormulaModel.STACK_TOP_MARGIN < bottom){
			int offDist;
			if(rects.size() > 0)
				offDist = bottom - ShapeSize.FormulaModel.STACK_TOP_MARGIN - rects.size()*ShapeSize.FormulaModel.RECT_HEIGHT;
			else 
				offDist = bottom - ShapeSize.FormulaModel.STACK_TOP_MARGIN - ShapeSize.FormulaModel.RECT_HEIGHT;
			bottom -= offDist;
			deleteAdjustStack(offDist);
		}
	}
	
	public void deleteAdjustStack(int offDist){
		for(int i=0; i<rects.size(); ++i){
			Timer timer = new Timer();
			MyTask task = new MyTask(rects.get(i), rects.get(i).ly - offDist);
			timer.schedule(task, i*100);
		}
		ll.y2 = lr.y2 = bottom;
		lb.y1 = lb.y2 = bottom;
		msg.ly -= offDist;
		model.setViewChanged();
	}
	
	private void addAdjustStack(){
		ll.y2 = lr.y2 = bottom;
		lb.y1 = lb.y2 = bottom;
		model.setViewChanged();
		for(int i=0; i<rects.size(); ++i){
			Timer timer = new Timer();
			MyTask task = new MyTask(rects.get(i), rects.get(i).ly + ShapeSize.FormulaModel.RECT_HEIGHT);
			timer.schedule(task, i*100);
		}
	}
}

class HuffmanNode implements Comparable<HuffmanNode>{
	public int parent;
	public int lchild;
	public int rchild;
	public int weight;
	public HuffmanTreeNode fNode;
	public DsRect[] shapes = new DsRect[5];
	public HuffmanNode(int parent, int lchild, int rchild, int weight,
			HuffmanTreeNode fNode) {
		super();
		this.parent = parent;
		this.lchild = lchild;
		this.rchild = rchild;
		this.weight = weight;
		this.fNode = fNode;
	}
	
	public void updateParentValue(){
		shapes[1].content = String.valueOf(this.parent);
	}
	
	@Override
	public int compareTo(HuffmanNode o) {
		return this.weight - o.weight;
	}
}
