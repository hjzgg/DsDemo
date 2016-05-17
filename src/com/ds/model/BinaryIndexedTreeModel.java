package com.ds.model;

import java.awt.Dimension;
import java.util.ArrayList;

import com.ds.shape.DsBIndexedTreeRect;
import com.ds.shape.DsLine;
import com.ds.shape.Shape;
import com.ds.size.ShapeSize;

public class BinaryIndexedTreeModel{
	private ArrayList<BinaryIndexedTreeNode> nums = null;
	private ArrayList<BinaryIndexedTreeNode> tree = null;
	private DrawModel model;
	private int lowbit(int x){
		return x&(-x);
	}
	
	private void buildBinaryIndexedTree(int maxLevel){
		ArrayList<Shape> shapeList = model.getShapeList();
		int leftMargin = ShapeSize.BinaryIndexedTreeModel.LEFT_MARGIN;
		for(int i=1; i<nums.size(); ++i){
			int sum = 0;
			
			if(lowbit(i) == 1){
				sum = nums.get(i).data;
			} else {// > 1的时候
				int k = i;
				sum += nums.get(k).data;
				--k;
				sum += nums.get(k).data;
				int cnt = (int)(Math.log(lowbit(i)*1.0)/Math.log(2.0))-1;
				int cc = 1;
				while(cnt-- > 0){
					k -= cc;
					cc <<= 1;
					sum += nums.get(k).data;
				}
			}
			
			int level = (int)(Math.log(lowbit(i))/Math.log(2.0)) + 1;
			int topMargin = ShapeSize.BinaryIndexedTreeModel.TOP_MARGIN + (ShapeSize.BinaryIndexedTreeModel.NODE_HEIGHT+ShapeSize.BinaryIndexedTreeModel.LEVEL_DIST)*(maxLevel-level);
			DsBIndexedTreeRect shape = new DsBIndexedTreeRect(leftMargin, topMargin, ShapeSize.BinaryIndexedTreeModel.NODE_WIDTH, ShapeSize.BinaryIndexedTreeModel.NODE_HEIGHT, "C"+i, String.valueOf(sum));
			shapeList.add(shape);
			tree.add(new BinaryIndexedTreeNode(sum, shape));
			leftMargin += ShapeSize.BinaryIndexedTreeModel.NODE_WIDTH;
			
			if(lowbit(i) == 1){
				shapeList.add(new DsLine(tree.get(i).shape.lx+tree.get(i).shape.lw/2, tree.get(i).shape.ly+tree.get(i).shape.lh, nums.get(i).shape.lx+nums.get(i).shape.lw/2, nums.get(i).shape.ly, false));
			} else {// > 1的时候
				int k = i;
				shapeList.add(new DsLine(tree.get(i).shape.lx+tree.get(i).shape.lw/2, tree.get(i).shape.ly+tree.get(i).shape.lh, nums.get(k).shape.lx+nums.get(k).shape.lw/2, nums.get(k).shape.ly, false));
				--k;
				shapeList.add(new DsLine(tree.get(i).shape.lx+tree.get(i).shape.lw/2, tree.get(i).shape.ly+tree.get(i).shape.lh, tree.get(k).shape.lx+tree.get(k).shape.lw/2, tree.get(k).shape.ly, false));
				int cnt = (int)(Math.log(lowbit(i)*1.0)/Math.log(2.0))-1;
				int cc = 1;
				while(cnt-- > 0){
					k -= cc;
					cc <<= 1;
					shapeList.add(new DsLine(tree.get(i).shape.lx+tree.get(i).shape.lw/2, tree.get(i).shape.ly+tree.get(i).shape.lh, tree.get(k).shape.lx+tree.get(k).shape.lw/2, tree.get(k).shape.ly, false));
				}
			}
		}
	}
	
	public void createBinaryIndexedTreeData(String data){
		ArrayList<Shape> shapeList = model.getShapeList();
		String[] datas = data.split(" ");
		if(datas == null || datas.length == 0) return;
		int level = (int)(Math.log(datas.length*1.0)/Math.log(2.0))+1;
		nums = new ArrayList<BinaryIndexedTreeNode>();
		nums.add(new BinaryIndexedTreeNode(null, null));
		tree = new ArrayList<BinaryIndexedTreeNode>();
		tree.add(new BinaryIndexedTreeNode(null, null));
		int leftMargin = ShapeSize.BinaryIndexedTreeModel.LEFT_MARGIN;
		int topMargin = ShapeSize.BinaryIndexedTreeModel.TOP_MARGIN + (ShapeSize.BinaryIndexedTreeModel.NODE_HEIGHT+ShapeSize.BinaryIndexedTreeModel.LEVEL_DIST)*level;
		for(int i=0; i<datas.length; ++i){
			DsBIndexedTreeRect shape = new DsBIndexedTreeRect(leftMargin, topMargin, ShapeSize.BinaryIndexedTreeModel.NODE_WIDTH, ShapeSize.BinaryIndexedTreeModel.NODE_HEIGHT, "A"+(i+1), datas[i]);
			shapeList.add(shape);
			leftMargin += ShapeSize.BinaryIndexedTreeModel.NODE_WIDTH;
			nums.add(new BinaryIndexedTreeNode(Integer.valueOf(datas[i]), shape));
		}
		buildBinaryIndexedTree(level);
	}

	public BinaryIndexedTreeModel(DrawModel model) {
		super();
		this.model = model;
		model.getObserverPanel().setPreferredSize(new Dimension(ShapeSize.WindowInitSize.WIDTH, ShapeSize.WindowInitSize.HEIGHT));
	}
	
	 
}
