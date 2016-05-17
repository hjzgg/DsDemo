package com.ds.model;

import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import com.ds.shape.DsRect;
import com.ds.shape.Shape;
import com.ds.size.ShapeSize;

public class StringModel {
	private DrawModel model;
	private ArrayList<Shape> shapeList = null;
	
	private void moveStringNodes(ArrayList<ArrayList<StringNode>> ss, int pos, boolean dir){
		if(ss == null || ss.size() == 0) return;
		StringNode beginNode = ss.get(0).get(0);
		int offDist = 20;
		//向右移动
		if(dir){
			while(beginNode.shape.lx < pos){
				for(ArrayList<StringNode> list : ss)
					for(StringNode node : list){
						node.shape.lx += offDist;
						if(node == beginNode && node.shape.lx > pos){
							node.shape.lx -= offDist;
							offDist = pos - node.shape.lx;
							node.shape.lx = pos;
						}
					}
				model.setViewChanged();
				delay(100);
			}
		} else {//向下移动
			while(beginNode.shape.ly < pos){
				for(ArrayList<StringNode> list : ss)
					for(StringNode node : list){
						node.shape.ly += offDist;
						if(node == beginNode && node.shape.ly > pos){
							node.shape.ly -= offDist;
							offDist = pos - node.shape.ly;
							node.shape.ly = pos;
						}
					}
				model.setViewChanged();
				delay(100);
			}
		}
	}
	
	private ArrayList<StringNode> sl = null;
	private ArrayList<StringNode> tl = null;
	
	private void initStringList(String s, String t){
		sl = new ArrayList<StringNode>();
		tl = new ArrayList<StringNode>();
		int leftMargin = -(ShapeSize.StringModel.LEFT_MARGIN + ShapeSize.StringModel.FIRST_NODE_WIDTH + Math.max(s.length(), t.length())*ShapeSize.StringModel.NODE_WIDTH);
		int topMargin = ShapeSize.StringModel.TOP_MARGIN;
		StringNode firstS = new StringNode("S串:");
		firstS.shape = new DsRect(leftMargin, topMargin, ShapeSize.StringModel.FIRST_NODE_WIDTH, ShapeSize.StringModel.NODE_HEIGHT, "S串:");
		shapeList.add(firstS.shape);
		sl.add(firstS);
		StringNode firstT = new StringNode("T串:");
		firstT.shape = new DsRect(leftMargin, topMargin+ShapeSize.StringModel.NODE_HEIGHT, ShapeSize.StringModel.FIRST_NODE_WIDTH, ShapeSize.StringModel.NODE_HEIGHT, "T串:");
		shapeList.add(firstT.shape);
		tl.add(firstT);
		
		leftMargin += ShapeSize.StringModel.FIRST_NODE_WIDTH;
		int length = Math.max(s.length(), t.length());
		for(int i=0; i<length; ++i){
			if(i < s.length()){
				StringNode sn = new StringNode(String.valueOf(s.charAt(i)));
				DsRect shape = new DsRect(leftMargin, topMargin, ShapeSize.StringModel.NODE_WIDTH, ShapeSize.StringModel.NODE_HEIGHT, String.valueOf(s.charAt(i)));
				sn.shape = shape;
				shapeList.add(shape);
				sl.add(sn);
			}
			if(i < t.length()){
				StringNode tn = new StringNode(String.valueOf(t.charAt(i)));
				DsRect shape = new DsRect(leftMargin, topMargin+ShapeSize.StringModel.NODE_HEIGHT, ShapeSize.StringModel.NODE_WIDTH, ShapeSize.StringModel.NODE_HEIGHT, String.valueOf(t.charAt(i)));
				tn.shape = shape;
				shapeList.add(shape);
				tl.add(tn);
			}
			leftMargin += ShapeSize.StringModel.NODE_WIDTH;
		}
	}
	
	//字符串匹配算法之BF算法
	public void BF(String s, String t){
		initStringList(s, t);
		ArrayList<ArrayList<StringNode>> ss = new ArrayList<ArrayList<StringNode>>();
		ss.add(sl);
		ss.add(tl);
		//向右移动，使得串出现在可见view中
		moveStringNodes(ss, ShapeSize.StringModel.LEFT_MARGIN, true);
		BFDisplay();
	}
	
	private void tipForSearched(DsRect[] shapes){
		boolean flag = true;
		Color color = null;
		for(int k=1; k<=4; ++k){
			color = flag ? Color.GREEN : Color.RED;
			for(DsRect shape : shapes)
				shape.color = color;
			flag = !flag;
			model.setViewChanged();
			delay(300);
		}
	}
	
	private void BFDisplay(){
		if(sl.size() < tl.size()) return;
		int len = sl.size()-tl.size()+1;
		boolean isFind = false;
		DsRect rect = new DsRect(0, tl.get(0).shape.ly+ShapeSize.StringModel.NODE_HEIGHT, ShapeSize.StringModel.NODE_WIDTH, ShapeSize.StringModel.NODE_HEIGHT, null);//等于或者不等于的信息
		for(int i=1; i <= len; ++i){
			int j;
			for(j=1; j<tl.size(); ++j){
				rect.color = Color.BLUE;
				rect.lx = tl.get(j).shape.lx;
				if(tl.get(j).content.compareTo(sl.get(i+j-1).content) != 0)
					rect.content = "≠";
				else {
					rect.content = "＝";
					if(j == tl.size()-1){
						rect.content = "＝☑";
						rect.lw = ShapeSize.StringModel.NODE_HEIGHT*2;
					}
				}
				shapeList.add(rect);
				tipForSearched(new DsRect[]{tl.get(j).shape, sl.get(i+j-1).shape});
				if(!rect.content.equals("＝☑"))
					shapeList.remove(rect);
				if(tl.get(j).content.compareTo(sl.get(i+j-1).content) != 0)
					break;
			}
			if(j == tl.size()){
				isFind = true;
				break;
			} else {
				if(i+1 > len) break;
				ArrayList<ArrayList<StringNode>> ss = new ArrayList<ArrayList<StringNode>>();
				ss.add(tl);
				moveStringNodes(ss, ShapeSize.StringModel.NODE_WIDTH+tl.get(0).shape.lx, true);
			}
		}
		model.setViewChanged();
		if(isFind) {}
	}
	
	public static final int DIR_LEFT = 1;
	public static final int DIR_RIGHT = 2;
	public static final int DIR_UP = 3;
	public static final int DIR_DOWN = 4;
	
	private void moveRect(DsRect rect, int pos, int dir){
		final int offDistX = 10, offDistY = 5;
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
					rect.ly -= offDistX;
					if(rect.ly < pos)
						rect.ly = pos;
					model.setViewChanged();
					delay(100);
				}
				break;
			case DIR_DOWN:
				while(rect.ly < pos){
					rect.ly += offDistX;
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
	
	public void KMP_NEXTOrNEXTVAL(String s, String t, boolean nextOrNextval){
		initStringList(s, t);
		ArrayList<StringNode> nextl= new ArrayList<StringNode>();
		StringNode firstNextNode = new StringNode(tl.get(0).content);
		firstNextNode.shape = (DsRect) tl.get(0).shape.clone();
		if(nextOrNextval) firstNextNode.shape.content = "Next:";
		else firstNextNode.shape.content = "NextVal:";
		firstNextNode.shape.ly += ShapeSize.StringModel.NODE_HEIGHT;
		shapeList.add(firstNextNode.shape);
		nextl.add(firstNextNode);
		for(int i=1; i<tl.size(); ++i){
			StringNode node = new StringNode("∧");
			node.shape = (DsRect) tl.get(i).shape.clone();
			node.shape.ly += ShapeSize.StringModel.NODE_HEIGHT;
			node.shape.content = "∧";
			shapeList.add(node.shape);
			nextl.add(node);
		}
		
		ArrayList<ArrayList<StringNode>> ss = new ArrayList<ArrayList<StringNode>>();
		ss.add(sl);
		ss.add(tl);
		ss.add(nextl);
		//向右移动，使得串出现在可见view中
		moveStringNodes(ss, ShapeSize.StringModel.LEFT_MARGIN, true);
		
		ss.remove(sl);
		moveStringNodes(ss, ShapeSize.StringModel.LEFT_MARGIN+ShapeSize.StringModel.NODE_WIDTH*3, true);
		moveStringNodes(ss, ShapeSize.StringModel.TOP_MARGIN+ShapeSize.StringModel.NODE_HEIGHT*3, false);
		
		//开始计算NEXT的值
		nextl.get(1).content = "0";
		nextl.get(1).shape.content = "0";
		tipForSearched(new DsRect[]{tl.get(1).shape, nextl.get(1).shape});
		for(int fi=1, fj = 0; fi<tl.size()-1; ){
			//寻找失配节点
			if(fj==0 || tl.get(fi).content.equals(tl.get(fj).content)){
		 	  	//制作效果，失配值的寻找
				for(int k=1; k <= fj; ++k)
					tl.get(k).shape.color = Color.YELLOW;
				for(int k=fi-fj+1; k <= fi; ++k)
					tl.get(k).shape.color = Color.YELLOW;
				
				tl.get(fi+1).shape.color = Color.MAGENTA;
				tl.get(fj+1).shape.color = Color.MAGENTA;
				model.setViewChanged();
				
				DsRect fTipShape = (DsRect) tl.get(0).shape.clone();
				fTipShape.content = "失配值→";
				fTipShape.color = Color.CYAN;
				fTipShape.ly -= fTipShape.lh;
				shapeList.add(fTipShape);
				DsRect fShape = (DsRect) tl.get(fj+1).shape.clone();
				fShape.content = String.valueOf(fj+1);
				fShape.color = Color.WHITE;
				shapeList.add(fShape);
				moveRect(fShape, fShape.ly-fShape.lh, DIR_UP);
				moveRect(fShape, tl.get(fi+1).shape.lx, DIR_RIGHT);
				moveRect(fShape, nextl.get(fi+1).shape.ly, DIR_DOWN);
				shapeList.remove(fShape);
				shapeList.remove(fTipShape);
				for(int k=1; k <= fj; ++k)
					tl.get(k).shape.color = Color.RED;
				for(int k=fi-fj+1; k <= fi; ++k)
					tl.get(k).shape.color = Color.RED;
				tl.get(fi+1).shape.color = Color.RED;
				tl.get(fj+1).shape.color = Color.RED;
				model.setViewChanged();
					 
				++fi;
				++fj;
				//计算nextval值
				nextl.get(fi).content = nextl.get(fi).shape.content = String.valueOf(fj);
				if(!nextOrNextval && tl.get(fi).content.equals(tl.get(fj).content))
					nextl.get(fi).content = nextl.get(fi).shape.content = nextl.get(fj).content;
				tipForSearched(new DsRect[]{tl.get(fi).shape, nextl.get(fi).shape});
		 	} else {
		 		fj = Integer.valueOf(nextl.get(fj).content);
			}
		}
		ss.clear();
		ss.add(sl);
		moveStringNodes(ss, ShapeSize.StringModel.LEFT_MARGIN+ShapeSize.StringModel.NODE_WIDTH*3, true);
		moveStringNodes(ss, ShapeSize.StringModel.TOP_MARGIN+ShapeSize.StringModel.NODE_HEIGHT*2, false);
		//kmp匹配展示
		KMP_NEXTOrNEXTVALDisplay(nextl);
	}
	
	private void KMP_NEXTOrNEXTVALDisplay(ArrayList<StringNode> nextl){
		ArrayList<ArrayList<StringNode>> ss = new ArrayList<ArrayList<StringNode>>();
		ss.add(tl);
		ss.add(nextl);
		
		DsRect rect = new DsRect(-100, sl.get(0).shape.ly-ShapeSize.StringModel.NODE_HEIGHT, ShapeSize.StringModel.NODE_WIDTH, ShapeSize.StringModel.NODE_HEIGHT, null);//是否相等指示器
		rect.color = Color.BLUE;
		shapeList.add(rect);
		//开始匹配
		for(int i=1, j=1; i<sl.size(); ){
			if(j == 0){
				++i;
				++j;
				int pos = sl.get(i).shape.lx - ShapeSize.StringModel.FIRST_NODE_WIDTH;
				moveStringNodes(ss, pos, true);
			} else {
				rect.lx = sl.get(i).shape.lx;
				if(sl.get(i).content.equals(tl.get(j).content)){
					rect.content = "＝";
					if(j+1 == tl.size()){
						rect.lw = ShapeSize.StringModel.NODE_WIDTH*2;
						rect.content = "＝☑";
					}
					tipForSearched(new DsRect[]{sl.get(i).shape, tl.get(j).shape});
					++i;
					++j;
					if(j == tl.size()){//匹配成功
						break;
					}
				} else {
					rect.content = "≠";
					tipForSearched(new DsRect[]{sl.get(i).shape, tl.get(j).shape, nextl.get(j).shape});
					j = Integer.valueOf(nextl.get(j).content);
					int pos = sl.get(i).shape.lx - (j-1)*ShapeSize.StringModel.NODE_WIDTH - ShapeSize.StringModel.FIRST_NODE_WIDTH;
					moveStringNodes(ss, pos, true);
				}
			}
		}
	}
	
	public void KMP_NEXTVAL(String s, String t){
		initStringList(s, t);
	}

	private void delay(int time){
		try {
			TimeUnit.MILLISECONDS.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		};
	}
		
	public StringModel(DrawModel model) {
		super();
		this.model = model;
		model.getObserverPanel().setPreferredSize(new Dimension(ShapeSize.WindowInitSize.WIDTH, ShapeSize.WindowInitSize.HEIGHT));
		shapeList = model.getShapeList();
	}
}
