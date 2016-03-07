package com.ds.model;

import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import com.ds.shape.DsCircle;
import com.ds.shape.DsLine;
import com.ds.shape.DsRect;
import com.ds.shape.DsSampleCircle;
import com.ds.shape.DsSampleRect;
import com.ds.shape.Shape;
import com.ds.size.ShapeSize;

//表达式的两种模拟（表达式树 或者 栈）
public class FormulaModel {
	private DrawModel model;
	private ArrayList<Shape> shapeList;
	
	private Map<DsRect, TreeNode> arrayToTree = new TreeMap<DsRect, TreeNode>();
	private TreeNode buildT(ArrayList<DsRect> formulaRects, int ld, int rd){
		TreeNode root = null;
		if(ld > rd) return root;
		if(ld == rd) {
			root = new TreeNode(formulaRects.get(ld).content);//数字节点
			arrayToTree.put(formulaRects.get(ld), root);
			return root;
		}
		
		int pc = 0;//没有匹配的左括弧的数量
		int c1 = -1, c2 = -1;//分别是-，+ 和 *，/的位置
		for(int i=ld; i <= rd; ++i){
			if("(".equals(formulaRects.get(i).content))
				++pc;
			else if(")".equals(formulaRects.get(i).content))
				--pc;
			else if(pc == 0){
				if("-".equals(formulaRects.get(i).content) || "+".equals(formulaRects.get(i).content))
					c1 = i;
				else if("*".equals(formulaRects.get(i).content) || "/".equals(formulaRects.get(i).content))
					c2 = i;
			}
		}
		if(c1 == -1 && c2 == -1) return buildT(formulaRects, ld+1, rd-1);
		if(c1 == -1) c1 = c2;
		root = new TreeNode(formulaRects.get(c1).content);
		arrayToTree.put(formulaRects.get(c1), root);
		root.lchild = buildT(formulaRects, ld, c1-1);
		root.rchild = buildT(formulaRects, c1+1, rd);
		return root;
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
	
	private void tipForSearched(DsSampleCircle[] shapes){
		boolean flag = false;
		Color color = null;
		for(int k=1; k<=4; ++k){
			color = flag ? Color.GREEN : Color.RED;
			for(DsSampleCircle shape : shapes)
				shape.color = color;
			flag = !flag;
			model.setViewChanged();
			delay(300);
		}
	}
	
	public static final int DIR_LEFT = 1;
	public static final int DIR_RIGHT = 2;
	public static final int DIR_UP = 3;
	public static final int DIR_DOWN = 4;
	
	private void moveCircle(DsCircle rect, int pos, int dir){
		final int offDistX = 20, offDistY = 20;
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
	private void buildTDisplay(ArrayList<DsRect> formulaRects, int ld, int rd){
		if(ld > rd) return;
		if(ld == rd){
			tipForSearched(new DsRect[]{formulaRects.get(ld)});
			arrayToTreeDisplay(formulaRects.get(ld));
			return ;
		}
		
		int pc = 0;//没有匹配的左括弧的数量
		int c1 = -1, c2 = -1;//分别是-，+ 和 *，/的位置
		for(int i=ld; i <= rd; ++i){
			if("(".equals(formulaRects.get(i).content))
				++pc;
			else if(")".equals(formulaRects.get(i).content))
				--pc;
			else if(pc == 0){
				if("-".equals(formulaRects.get(i).content) || "+".equals(formulaRects.get(i).content))
					c1 = i;
				else if("*".equals(formulaRects.get(i).content) || "/".equals(formulaRects.get(i).content))
					c2 = i;
			}
		}
		if(c1 == -1 && c2 == -1) {
			buildTDisplay(formulaRects, ld+1, rd-1);
			return ;
		}
		if(c1 == -1) c1 = c2;
		DsRect rectL = (DsRect) formulaRects.get(ld).clone();
		DsRect rectR = (DsRect) formulaRects.get(rd).clone();
		rectL.content = rectR.content = "↑";
		rectL.ly += rectL.lh;
		rectR.ly += rectR.lh;
		synchronized(Shape.class){
			shapeList.add(rectL);
			shapeList.add(rectR);
		}
		tipForSearched(new DsRect[]{formulaRects.get(c1), rectL, rectR});
		arrayToTreeDisplay(formulaRects.get(c1));
		synchronized(Shape.class){
			shapeList.remove(rectL);
			shapeList.remove(rectR);
		}
		buildTDisplay(formulaRects, ld, c1-1);
		buildTDisplay(formulaRects, c1+1, rd);
	}
	
	private void arrayToTreeDisplay(DsRect movingRect){
		DsCircle circle = new DsCircle(movingRect.lx, movingRect.ly, movingRect.lw, movingRect.lh, movingRect.content);
		circle.color = Color.GREEN;
		synchronized (Shape.class) {shapeList.add(circle);}
		circle.fontSize = movingRect.fontSize;
		moveCircle(circle, movingRect.ly+movingRect.lh*2, DIR_DOWN);
		circle.fontSize = -1;
		circle.lw = ShapeSize.FormulaModel.CIRCLE_WIDTH;
		circle.lh = ShapeSize.FormulaModel.CIRCLE_HEIGHT;
		if(circle.lx > arrayToTree.get(movingRect).shape.lx)
			moveCircle(circle, arrayToTree.get(movingRect).shape.lx, DIR_LEFT);
		else 
			moveCircle(circle, arrayToTree.get(movingRect).shape.lx, DIR_RIGHT);
		moveCircle(circle, arrayToTree.get(movingRect).shape.ly, DIR_DOWN);
		synchronized (Shape.class) {
			shapeList.remove(circle);
			shapeList.add(arrayToTree.get(movingRect).shape);
		}
		for(DsLine line2 : arrayToTree.get(movingRect).lineList){
			DsLine line1 = new DsLine(line2.x1, line2.y1, line2.x1, line2.y1, false);
			synchronized (Shape.class) { shapeList.add(line1);}
			lineMove(line1, line2);
			synchronized (Shape.class) { 
				shapeList.add(line2);
				shapeList.remove(line1);
			}
		}
	}
	
	Map<Integer, DsSampleCircle> treeScircle = null;
	private String process(TreeNode root){
		String ret = null;
		if(root == null) return "0";
		if(root.lchild == null && root.rchild == null){
			DsSampleCircle circle = new DsSampleCircle(root.shape.lx, root.shape.ly, root.shape.lw, root.shape.lh, root.shape.content);
			circle.color = Color.GREEN;
			synchronized(Shape.class) {shapeList.add(circle);}
			treeScircle.put(root.hashCode(), circle);
			tipForSearched(new DsSampleCircle[]{circle});
			return root.content;
		}
		String retL = process(root.lchild);
		String retR = process(root.rchild);
		switch(root.content.charAt(0)){
			case '+':
				ret = String.valueOf(Integer.valueOf(retL) + Integer.valueOf(retR));
				break;
			case '-':
				ret = String.valueOf(Integer.valueOf(retL) - Integer.valueOf(retR));
				break;
			case '*':
				ret = String.valueOf(Integer.valueOf(retL) * Integer.valueOf(retR));
				break;
			case '/':
				ret = String.valueOf(Integer.valueOf(retL) / Integer.valueOf(retR));
				break;
			default:
				ret = "0";
		}
		DsSampleCircle circle = new DsSampleCircle(root.shape.lx, root.shape.ly, root.shape.lw, root.shape.lh, root.shape.content);
		circle.color = Color.GREEN;
		synchronized(Shape.class) {shapeList.add(circle);}
		treeScircle.put(root.hashCode(), circle);
		tipForSearched(new DsSampleCircle[]{circle, treeScircle.get(root.lchild.hashCode()), treeScircle.get(root.rchild.hashCode())});
		circleMove(treeScircle.get(root.lchild.hashCode()), treeScircle.get(root.rchild.hashCode()), circle);
		circle.content = ret;
		synchronized(Shape.class) {
			shapeList.remove(treeScircle.get(root.lchild.hashCode()));
			shapeList.remove(treeScircle.get(root.rchild.hashCode()));
		}
		return ret;
	}
	
	private void circleMove(DsSampleCircle shape0, DsSampleCircle shape1, DsSampleCircle shape2){
		final int offDistY = 8;
		double k1 = (1.0*shape2.ly-shape1.ly)/(shape2.lx-shape1.lx);
		double b1 = shape2.ly-k1*shape2.lx;
		double k0 = (1.0*shape2.ly-shape0.ly)/(shape2.lx-shape0.lx);
		double b0 = shape2.ly-k0*shape2.lx;
		while(shape1.ly > shape2.ly){
			shape1.ly -= offDistY;
			shape0.ly -= offDistY;
			shape1.lx = (int) ((shape1.ly-b1)/k1);
			shape0.lx = (int) ((shape0.ly-b0)/k0);
			if(shape1.ly < shape2.ly){
				shape1.lx = shape2.lx;
				shape1.ly = shape2.ly;
				shape0.lx = shape2.lx;
				shape0.ly = shape2.ly;
			}
			model.setViewChanged();
			delay(100);
		}
	}
	
	public void formulaTree(String data){
		//格式化字符串
		data = data.replaceAll("(\\D)", " $1 ");
		data = data.replaceAll("\\s+", " ");
		data = data.trim();
		String[] datas = data.split(" ");
		
		ArrayList<DsRect> formulaRects = new ArrayList<DsRect>();
		for(int i = 0; i < datas.length; ++i){
			DsRect rect = new DsRect(ShapeSize.FormulaModel.LEFT_MARGIN+i*ShapeSize.FormulaModel.RECT_WIDTH, ShapeSize.FormulaModel.ARRAY_TOP_MARGIN, 
					ShapeSize.FormulaModel.RECT_WIDTH, ShapeSize.FormulaModel.RECT_HEIGHT, datas[i]);
			rect.fontSize = 15;
			shapeList.add(rect);
			formulaRects.add(rect);
		}
		TreeNode root = buildT(formulaRects, 0, formulaRects.size()-1);
		createFormulaTreeModel(root, 1, false);
		
		int tw = ShapeSize.FormulaModel.LEFT_MARGIN + treeNodeCnt*(ShapeSize.FormulaModel.NODES_HOR_DIST + ShapeSize.FormulaModel.CIRCLE_WIDTH);
		int th = treeBottom + ShapeSize.FormulaModel.CIRCLE_HEIGHT;
		model.getObserverPanel().setPreferredSize(new Dimension(tw, th));
		buildTDisplay(formulaRects, 0, formulaRects.size()-1);
		treeScircle = new TreeMap<Integer, DsSampleCircle>();
		process(root);
		
		DsRect eqlRect = (DsRect) formulaRects.get(formulaRects.size()-1).clone();
		eqlRect.lx += eqlRect.lw;
		eqlRect.content = "=";
		DsRect retRect = (DsRect) eqlRect.clone();
		retRect.lx += retRect.lw;
		DsSampleCircle retCircle = treeScircle.get(root.hashCode());
		retRect.lw += retRect.lw;
		retRect.content = treeScircle.get(root.hashCode()).content;
		moveCircle(retCircle, retCircle.ly-retCircle.lh, DIR_UP);
		if(retCircle.lx > retRect.lx)
			moveCircle(retCircle, retRect.lx, DIR_LEFT);
		else
			moveCircle(retCircle, retRect.lx, DIR_RIGHT);
		retCircle.lw = retRect.lw;
		retCircle.lh = retRect.lh;
		retCircle.fontSize = retRect.fontSize;
		moveCircle(retCircle, retRect.ly, DIR_UP);
		synchronized (Shape.class) {
			shapeList.add(eqlRect);
			shapeList.remove(retCircle);
			shapeList.add(retRect);
		}
		model.setViewChanged();
	}
	
	private int treeNodeCnt = 0, treeBottom = -1;
	
	private void createFormulaTreeModel(TreeNode T, int level, boolean isAdd){
		ArrayList<Shape> shapeList = model.getShapeList();
		if(T == null) return;
		createFormulaTreeModel(T.lchild, level+1, isAdd);
		++treeNodeCnt;
		int circleLeft = ShapeSize.FormulaModel.LEFT_MARGIN; 
		circleLeft += (treeNodeCnt-1)*(ShapeSize.FormulaModel.NODES_HOR_DIST + ShapeSize.FormulaModel.CIRCLE_WIDTH);
		int circleTop = ShapeSize.FormulaModel.TREE_TOP_MARGIN;
		circleTop += (level-1)*(ShapeSize.FormulaModel.CIRCLE_HEIGHT + ShapeSize.FormulaModel.LEVEL_DIST);
		createFormulaTreeModel(T.rchild, level+1, isAdd);
		
		DsCircle shapeCircle = new DsCircle(circleLeft, circleTop, ShapeSize.FormulaModel.CIRCLE_WIDTH, ShapeSize.FormulaModel.CIRCLE_HEIGHT, T.content);
		if(isAdd) shapeList.add(shapeCircle);
		T.shape = shapeCircle;
		
		if(treeBottom < T.shape.ly+T.shape.lh)
			treeBottom = T.shape.ly+T.shape.lh;
		
		if(T.lchild != null)
			addTreeLine(T, T.lchild, isAdd);
			
		if(T.rchild != null) 
			addTreeLine(T, T.rchild, isAdd);
	}
	
	private void delay(int time){
		try {
			TimeUnit.MILLISECONDS.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void lineMove(DsLine line1, DsLine line2){
		double k = (1.0*line2.y2-line2.y1)/(line2.x2-line2.x1);
		double b = line2.y1-k*line2.x1;
		int offDist = 10;
		if(line1.x2 < line2.x2){
			while(line1.x2 < line2.x2){
				line1.x2 += offDist;
				if(line1.x2 > line2.x2){
					line1.x2 = line2.x2;
					line1.y2 = line2.y2;
				}
				line1.y2 = (int) (k*line1.x2 + b);
				model.setViewChanged();
				delay(100);
			}
		} else {
			while(line1.x2 > line2.x2){
				line1.x2 -= offDist;
				if(line1.x2 < line2.x2){
					line1.x2 = line2.x2;
					line1.y2 = line2.y2;
				}
				line1.y2 = (int) (k*line1.x2 + b);
				model.setViewChanged();
				delay(100);
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
		double sinx = (x2-x1)/L/2;//除以2，因为ShapeSize.FormulaModel.CIRCLE_WIDTH是直径，我们要半径
		double cosx = (y2-y1)/L/2;
		x1 = (int)(x1+ShapeSize.FormulaModel.CIRCLE_WIDTH*sinx);
		y1 = (int)(y1+ShapeSize.FormulaModel.CIRCLE_WIDTH*cosx);
		x2 = (int)(x2-ShapeSize.FormulaModel.CIRCLE_WIDTH*sinx);
		y2 = (int)(y2-ShapeSize.FormulaModel.CIRCLE_WIDTH*cosx);
		DsLine line = new DsLine(x1, y1, x2, y2, false);
		if(isAdd) shapeList.add(line);
		T.lineList.add(line);
	}
	
	public void formulaStack(String data){
		data += "=";
		//格式化字符串
		data = data.replaceAll("(\\D)", " $1 ");
		data = data.replaceAll("\\s+", " ");
		data = data.trim();
		String[] datas = data.split(" ");
		
		ArrayList<FormulaNode> formulaList = new ArrayList<FormulaNode>();
		for(int i = 0; i < datas.length; ++i){
			DsRect rect = new DsRect(ShapeSize.FormulaModel.LEFT_MARGIN+i*ShapeSize.FormulaModel.RECT_WIDTH, ShapeSize.FormulaModel.ARRAY_TOP_MARGIN, 
					ShapeSize.FormulaModel.RECT_WIDTH, ShapeSize.FormulaModel.RECT_HEIGHT, datas[i]);
			rect.fontSize = 15;
			shapeList.add(rect);
			formulaList.add(new FormulaNode(datas[i], rect));
		}
		
		StackList opts = new StackList(model);
		StackList opds = new StackList(model);
		int offDist = (formulaList.size()*ShapeSize.FormulaModel.RECT_WIDTH-(ShapeSize.FormulaModel.STACK_WIDTH*2+ShapeSize.FormulaModel.STACK_DIST))/2;
		if(offDist < 0) offDist = 0;
		opts.ll = new DsLine(ShapeSize.FormulaModel.LEFT_MARGIN+offDist, ShapeSize.FormulaModel.STACK_TOP_MARGIN, ShapeSize.FormulaModel.LEFT_MARGIN+offDist, ShapeSize.FormulaModel.STACK_TOP_MARGIN+ShapeSize.FormulaModel.RECT_HEIGHT, false);
		opts.lr = new DsLine(opts.ll.x1+ShapeSize.FormulaModel.STACK_WIDTH, opts.ll.y1, opts.ll.x2+ShapeSize.FormulaModel.STACK_WIDTH, opts.ll.y2, false);
		opts.lb = new DsLine(opts.ll.x1, opts.ll.y2, opts.lr.x2, opts.ll.y2, false);
		opts.msg = new DsRect(opts.lb.x1, opts.lb.y1+20, ShapeSize.FormulaModel.STACK_WIDTH, ShapeSize.FormulaModel.RECT_HEIGHT, "操作符");
		opts.msg.fontSize = 20;
		opts.ll.color = opts.lr.color = opts.lb.color = Color.GREEN;
		
		opds.ll = new DsLine(opts.lr.x1+ShapeSize.FormulaModel.STACK_DIST, opts.lr.y1, opts.lr.x2+ShapeSize.FormulaModel.STACK_DIST, opts.lr.y2, false);
		opds.lr = new DsLine(opds.ll.x1+ShapeSize.FormulaModel.STACK_WIDTH, opds.ll.y1, opds.ll.x2+ShapeSize.FormulaModel.STACK_WIDTH, opds.ll.y2, false);
		opds.lb = new DsLine(opds.ll.x1, opds.ll.y2, opds.lr.x2, opds.ll.y2, false);
		opds.msg = new DsRect(opds.lb.x1, opds.lb.y1+20, ShapeSize.FormulaModel.STACK_WIDTH, ShapeSize.FormulaModel.RECT_HEIGHT, "操作数");
		opds.msg.fontSize = 20;
		opds.ll.color = opds.lr.color = opds.lb.color = Color.GREEN;
		
		DsRect msgOpdL = new DsRect(opts.lb.x2+ShapeSize.FormulaModel.RECT_WIDTH, ShapeSize.FormulaModel.STACK_TOP_MARGIN+ShapeSize.FormulaModel.RECT_HEIGHT*5, ShapeSize.FormulaModel.RECT_WIDTH, ShapeSize.FormulaModel.RECT_HEIGHT, "");
		msgOpdL.color = Color.CYAN;
		DsRect msgOpdR = new DsRect(opds.lb.x1-ShapeSize.FormulaModel.RECT_WIDTH*2, ShapeSize.FormulaModel.STACK_TOP_MARGIN+ShapeSize.FormulaModel.RECT_HEIGHT*5, ShapeSize.FormulaModel.RECT_WIDTH, ShapeSize.FormulaModel.RECT_HEIGHT, "");
		msgOpdR.color = Color.CYAN;
		DsCircle msgOpt = new DsCircle(msgOpdL.lx+ShapeSize.FormulaModel.RECT_WIDTH, msgOpdL.ly-(ShapeSize.FormulaModel.CIRCLE_HEIGHT-ShapeSize.FormulaModel.RECT_HEIGHT)/2, ShapeSize.FormulaModel.CIRCLE_WIDTH, ShapeSize.FormulaModel.CIRCLE_HEIGHT, "");
		msgOpt.color = Color.CYAN;
		
		synchronized(Shape.class){
			shapeList.add(msgOpdL);
			shapeList.add(msgOpdR);
			shapeList.add(msgOpt);
			shapeList.add(opts.ll);
			shapeList.add(opts.lr);
			shapeList.add(opts.lb);
			shapeList.add(opts.msg);
			shapeList.add(opds.ll);
			shapeList.add(opds.lr);
			shapeList.add(opds.lb);
			shapeList.add(opds.msg);
		}
		//运算符栈 
		Stack<FormulaNode> opt = new Stack<FormulaNode>();
		//操作数栈
		Stack<FormulaNode> opd = new Stack<FormulaNode>();
		
		opt.push(new FormulaNode("#", null));
		int i = 0;
		while(!opt.peek().content.equals("=")){
			FormulaNode node = formulaList.get(i);
	    	//数字字符的处理 
	        if(Character.isDigit(node.content.charAt(0))){
	        	opds.addAdjust();
	        	opd.push(node);
	        	DsRect movingRect = (DsRect) node.shape.clone();
	        	movingRect.color = Color.YELLOW;
	        	tipForSearched(new DsRect[]{node.shape});
	        	synchronized(Shape.class){shapeList.add(movingRect);}
	        	moveRect(movingRect, movingRect.ly+movingRect.lh, DIR_DOWN);
	        	int posX = opds.lb.x1 + (ShapeSize.FormulaModel.STACK_WIDTH-ShapeSize.FormulaModel.RECT_WIDTH)/2;
	        	if(movingRect.lx > posX)
	        		moveRect(movingRect, posX, DIR_LEFT);
	        	else 
	        		moveRect(movingRect, posX, DIR_RIGHT);
	        	moveRect(movingRect, ShapeSize.FormulaModel.STACK_TOP_MARGIN, DIR_DOWN);
	        	opds.rects.add(movingRect);
	        	++i;
	        ////非数字字符的处理 
	        } else if(")".equals(node.content) && "(".equals(opt.peek().content)){
			     opt.pop();//弹出左括号 
			     tipForSearched(new DsRect[]{node.shape});
			     DsRect movingRect = opts.rects.remove(opts.rects.size()-1);
			     moveRect(movingRect, ShapeSize.FormulaModel.STACK_TOP_MARGIN-ShapeSize.FormulaModel.RECT_HEIGHT, DIR_UP);
			     disappearRects(new DsRect[]{movingRect});
			     synchronized(Shape.class){shapeList.remove(movingRect);}
			     ++i;//右括号不做处理 
	        } else if(cmp(node.content.charAt(0)) > cmp(opt.peek().content.charAt(0)) || "(".equals(opt.peek().content)) {
			    opts.addAdjust();
			    opt.push(node);
	        	DsRect movingRect = (DsRect) node.shape.clone();
	        	movingRect.color = Color.YELLOW;
	        	tipForSearched(new DsRect[]{node.shape});
	        	synchronized(Shape.class){shapeList.add(movingRect);}
	        	moveRect(movingRect, movingRect.ly+movingRect.lh, DIR_DOWN);
	        	int posX = opts.lb.x1 + (ShapeSize.FormulaModel.STACK_WIDTH-ShapeSize.FormulaModel.RECT_WIDTH)/2;
	        	if(movingRect.lx > posX)
	        		moveRect(movingRect, posX, DIR_LEFT);
	        	else 
	        		moveRect(movingRect, posX, DIR_RIGHT);
	        	moveRect(movingRect, ShapeSize.FormulaModel.STACK_TOP_MARGIN, DIR_DOWN);
	        	opts.rects.add(movingRect);
			    ++i;
			} else {
			   FormulaNode b = opd.pop();
			   DsRect moveB = opds.rects.remove(opds.rects.size()-1);
			   FormulaNode a = opd.pop();
			   DsRect moveA = opds.rects.remove(opds.rects.size()-1);
			   FormulaNode o = opt.pop();
			   DsRect moveO = opts.rects.remove(opts.rects.size()-1);
			   MyRun ra =  new MyRun(moveA, msgOpdL);
			   MyRun rb =  new MyRun(moveB, msgOpdR);
			   MyRun ro =  new MyRun(moveO, msgOpt);
			   new Thread(ra).start();
			   new Thread(rb).start();
			   new Thread(ro).start();
			   opts.deleteAdjust();
			   opds.deleteAdjust();
			   while(!ra.isOver || !rb.isOver || !ro.isOver);
			   FormulaNode c = kernelCal(a, o, b);
			   DsSampleRect msa = new DsSampleRect(msgOpdL.lx, msgOpdL.ly, msgOpdL.lw, msgOpdL.lh, msgOpdL.content);
			   msa.fontSize = msgOpdL.fontSize;
			   msa.color = msgOpdL.color;
			   DsSampleRect msb = new DsSampleRect(msgOpdR.lx, msgOpdR.ly, msgOpdR.lw, msgOpdR.lh, msgOpdR.content);
			   msb.fontSize = msgOpdR.fontSize;
			   msb.color = msgOpdR.color;
			   synchronized (Shape.class) { 
				   shapeList.add(shapeList.indexOf(msgOpt), msa);
				   shapeList.add(shapeList.indexOf(msgOpt), msb);
			   }
			   msgOpdL.content = msgOpdR.content = "";
			   
			   final int offDistX = 5;
			   while(msa.lx < msgOpt.lx){
				   msa.lx += offDistX;
				   msb.lx -= offDistX;
				   if(msa.lx > msgOpt.lx){
					   msa.lx = msgOpt.lx;
					   msb.lx = msgOpt.lx;
				   }
				   model.setViewChanged();
				   delay(100);
			   }
			   DsRect msc = new DsRect(msa.lx, msa.ly, msa.lw, msa.lh, c.content);
			   msc.color = Color.YELLOW;
			   msc.fontSize = msa.fontSize;
			   synchronized (Shape.class) {
				   shapeList.remove(msa);
				   shapeList.remove(msb);
				   shapeList.add(shapeList.indexOf(msgOpt), msc);
			   }
			   
			   opds.addAdjust();
			   
			   moveRect(msc, ShapeSize.FormulaModel.STACK_TOP_MARGIN-ShapeSize.FormulaModel.RECT_HEIGHT, DIR_UP);
			   moveRect(msc, opds.ll.x1+(ShapeSize.FormulaModel.STACK_WIDTH-ShapeSize.FormulaModel.RECT_WIDTH)/2, DIR_RIGHT);
			   moveRect(msc, ShapeSize.FormulaModel.STACK_TOP_MARGIN, DIR_DOWN);
			   opds.rects.add(msc);
			   opd.push(c);
			}
	    }
		if(!opd.isEmpty() && opds.rects.size() > 0){
			opd.pop();
			DsRect movingRect = opds.rects.remove(opds.rects.size()-1);
			moveRect(movingRect, movingRect.ly-movingRect.lh, DIR_UP);
			int posX = formulaList.get(formulaList.size()-1).shape.lx + ShapeSize.FormulaModel.RECT_WIDTH;
			if(movingRect.lx > posX)
				moveRect(movingRect, posX, DIR_LEFT);
			else
				moveRect(movingRect, posX, DIR_RIGHT);
			moveRect(movingRect, ShapeSize.FormulaModel.ARRAY_TOP_MARGIN, DIR_UP);
		}
		model.setViewChanged();
	}
	
	private class MyRun implements Runnable{
		private DsRect from;
		private Object to;
		private boolean isOver = false;
		
		@Override
		public void run() {
			from.color = Color.GREEN;
			moveRect(from, ShapeSize.FormulaModel.STACK_TOP_MARGIN-from.lh, DIR_UP);
			if(to instanceof DsCircle){
				DsCircle fromc = new DsCircle(from.lx, from.ly, from.lw, from.lh, from.content);
				fromc.color = Color.GREEN;
				synchronized (Shape.class) {
					shapeList.remove(from);
					shapeList.add(fromc);
				}
				moveCircle(fromc, ((DsCircle)to).lx, DIR_RIGHT);
				fromc.lw = ((DsCircle)to).lw;
				fromc.lh = ((DsCircle)to).lh;
				moveCircle(fromc, ((DsCircle)to).ly, DIR_DOWN);
				synchronized (Shape.class) {
					shapeList.remove(fromc);
				}
				((DsCircle)to).content = fromc.content;
				((DsCircle)to).fontSize = fromc.fontSize;
			} else {
				moveRect(from, ((DsRect)to).lx, DIR_LEFT);
				moveRect(from, ((DsRect)to).ly, DIR_DOWN);
				synchronized (Shape.class) {
					shapeList.remove(from);
				}
				((DsRect)to).content = from.content;
				((DsRect)to).fontSize = from.fontSize;
			}
			isOver = true;
		}

		public MyRun(DsRect from, Object to) {
			super();
			this.from = from;
			this.to = to;
		}
		
	}
	
	private void disappearRects(DsRect[] rects){
		final int offDist = 5;
		if(rects == null || rects.length == 0) return;
		while(rects[0].lw > 0 && rects[0].lh > 0){
			for(DsRect rect : rects){
				rect.lx += offDist;
				rect.ly += offDist;
				rect.lh -= offDist;
				rect.lw -= offDist;
				rect.fontSize -= 8;
				if(rect.fontSize < 0) rect.fontSize = 5;
			}
			if(rects[0].lw < 0 || rects[0].lh < 0) break;
			model.setViewChanged();
			delay(100);
		}
	}
	
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
	
	private FormulaNode kernelCal(FormulaNode a, FormulaNode o, FormulaNode b){
		FormulaNode c = null;
		String ret = null;
		switch(o.content.charAt(0)){
			case '+':
				ret = String.valueOf(Integer.valueOf(a.content) + Integer.valueOf(b.content));
				break;
			case '-':
				ret = String.valueOf(Integer.valueOf(a.content) - Integer.valueOf(b.content));
				break;
			case '*':
				ret = String.valueOf(Integer.valueOf(a.content) * Integer.valueOf(b.content));
				break;
			case '/':
				ret = String.valueOf(Integer.valueOf(a.content) / Integer.valueOf(b.content));
				break;
			default:
				ret = "0";
		}
		c = new FormulaNode(ret, null);
		return c;
	}
	
	private int cmp(char ch)
	{
	   switch(ch)
	   {
	       case ')'://右括号的优先级比所有符号的优先级都小 
	       	  return -2;
	       case '#':
	       	  return -1;
	       case '=':
	       	  return 0;
	       case '-':
	       case '+':
	       	  return 1;
	       case '*':
	       case '/':
	       	  return 2;
	       case '(':
	       	  return 3;//左括号的的优先级比所有符号的优先级都大 
	   }
	   return -3;
	}
	
	public FormulaModel(DrawModel model) {
		super();
		this.model = model;
		shapeList = model.getShapeList();
	}
	
}
