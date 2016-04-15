package com.ds.model;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Queue;
import java.util.Random;
import java.util.Stack;
import java.util.concurrent.TimeUnit;

import com.ds.panel.DrawPanel;
import com.ds.shape.DsLine;
import com.ds.shape.DsPeople;
import com.ds.shape.DsTrain;
import com.ds.shape.Shape;
import com.ds.size.ShapeSize;

public class StackModel {
	private DrawModel model;
	private DrawPanel panel;
	ArrayList<Shape> shapeList;
	private StackDoor door = null;
	private void delay(int time){
		try {
			TimeUnit.MILLISECONDS.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	//屏幕的宽度和高度的
	private int swidth = 0, sheight = 0;
	 
	private int tn = 5;//火车的初始数量
	
	private class TrainRun implements Runnable{
		private DsTrain train;
		private int tx, ty;
		@Override
		public void run() {
			final int distX = 6;
			final int distY = 6; 
			if(train.getX() > tx){
				while(train.getX() > tx){
					if(train.getX() - distX < tx){
						train.setXY(tx, train.getY());
						break;
					}
					train.setXY(train.getX()-distX, train.getY());
					delay(50);
				}
			} else if(train.getX() < tx){
				while(train.getX() < tx){
					if(train.getX() + distX > tx){
						train.setXY(tx, train.getY());
						break;
					}
					delay(50);
					train.setXY(train.getX()+distX, train.getY());
				}
			}
			
			if(train.getY() > ty){
				while(train.getY() > ty){
					if(train.getY()-distY < ty){
						train.setXY(train.getX(), ty);
						break;
					}
					delay(50);
					train.setXY(train.getX(), train.getY()-distY);
				}
			} else if(train.getY() < ty){
				while(train.getY() < ty){
					if(train.getY()+distY > ty){
						train.setXY(train.getX(), ty);
						break;
					}
					delay(50);
					train.setXY(train.getX(), train.getY()+distY);
				}
			}
		}
		
		public TrainRun(DsTrain train, int tx, int ty) {
			super();
			this.train = train;
			this.tx = tx;
			this.ty = ty;
		}
	}
	private final int trainDist = 20;
	private void pushStack(Stack<DsTrain> stack){
		int bx = ShapeSize.StackModel.STACK_LEFT_MARGIN+ShapeSize.StackModel.NODE_WIDTH*3, by = ShapeSize.StackModel.STACK_TOP_MARGIN;
		DsTrain train = new DsTrain(String.valueOf(++tn));
		train.setBounds(bx, by, ShapeSize.StackModel.NODE_WIDTH, ShapeSize.StackModel.NODE_HEIGHT);
		panel.add(train);
		List<Thread> threadList = new ArrayList<Thread>();
		int threadIndex = 0;
		{
			Thread thread = new Thread(new TrainRun(train, ShapeSize.StackModel.STACK_LEFT_MARGIN, train.getY()),
					"childThread_pushStack" + ++threadIndex);
			thread.start();
			threadList.add(thread);
		}
		for(Iterator<DsTrain> it = stack.iterator(); it.hasNext(); ){
			DsTrain curTrain = it.next();
			Thread thread = new Thread(new TrainRun(curTrain, curTrain.getX(), curTrain.getY()+curTrain.getHeight()+trainDist),
					"childThread_pushStack" + ++threadIndex);
			thread.start();
			threadList.add(thread);
			
			if(curTrain.getY()+curTrain.getHeight()*3+trainDist > sheight)
				sheight = curTrain.getY()+curTrain.getHeight()*3+trainDist;
		}
		
		try {
			for(Thread thread : threadList)
				thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if(swidth > panel.getWidth())
			panel.setPreferredSize(new Dimension(swidth, panel.getHeight()));
		if(sheight > panel.getHeight())
			panel.setPreferredSize(new Dimension(panel.getWidth(), sheight));
		stack.push(train);
	}
	
	private void popStack(Stack<DsTrain> stack){
		if(!stack.isEmpty()){
			List<Thread> threadList = new ArrayList<Thread>();
			int threadIndex = 0;
			boolean isFirst = true;
			for(ListIterator<DsTrain> it = stack.listIterator(stack.size()); it.hasPrevious(); ){
				if(isFirst){
					isFirst = false;
					DsTrain curTrain = it.previous();
					Thread thread = new Thread(new TrainRun(curTrain, curTrain.getX()-curTrain.getWidth()*2, curTrain.getY()), "childThread_popStack" + ++threadIndex);
					thread.start();
					threadList.add(thread);
				} else {
					DsTrain curTrain = it.previous();
					Thread thread = new Thread(new TrainRun(curTrain, curTrain.getX(), curTrain.getY()-curTrain.getHeight()-trainDist), "childThread_popStack" + ++threadIndex);
					thread.start();
					threadList.add(thread);
				}
			}
			try {
				for(Thread thread : threadList)
					thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			panel.remove(stack.pop());
			model.setViewChanged();
		}
	}
	
	public void showStack(){
		Stack<DsTrain> stack = new Stack<DsTrain>();
		door = new StackDoor();
		Random randow = new Random();
		String[] trainNum = new String[tn];
		for(int i=1; i<=tn; ++i)
			trainNum[i-1] = String.valueOf(i);
		int bx = panel.getWidth(), by = ShapeSize.StackModel.STACK_TOP_MARGIN;
		int ex = ShapeSize.StackModel.STACK_LEFT_MARGIN, ey = by + (trainNum.length-1)*(ShapeSize.StackModel.NODE_HEIGHT+trainDist);
		door.initDoor(ey + ShapeSize.StackModel.NODE_HEIGHT);
		List<Thread> threadList = new ArrayList<Thread>();
		swidth = panel.getWidth();
		sheight = panel.getHeight();
		//打开右边的门
		door.openRight();
		if(sheight < ey+ShapeSize.StackModel.NODE_HEIGHT*2) sheight = ey+ShapeSize.StackModel.NODE_HEIGHT*2;
		for(int i=0; i<trainNum.length; ++i){
			DsTrain train = new DsTrain(trainNum[i]);
			bx = panel.getWidth() + i*(ShapeSize.StackModel.NODE_WIDTH+trainDist);
			train.setBounds(bx, by, ShapeSize.StackModel.NODE_WIDTH, ShapeSize.StackModel.NODE_HEIGHT);
			panel.add(train);
			stack.push(train);
			Thread thread = new Thread(new TrainRun(train, ex, ey), "childThread_TrainRun" + i);
			thread.start();
			threadList.add(thread);
			
			ey -= (ShapeSize.StackModel.NODE_HEIGHT+trainDist);
		}
		
		if(swidth > panel.getWidth())
			panel.setPreferredSize(new Dimension(swidth, panel.getHeight()));
		if(sheight > panel.getHeight())
			panel.setPreferredSize(new Dimension(panel.getWidth(), sheight));
		
		try{
			for(Thread thread : threadList)
				thread.join();
		} catch(Exception e){
			e.printStackTrace();
		}
		
		door.closeRight();
		
		Random random = new Random();
		for(int i=0; i<10; ++i){
			if(random.nextBoolean()){
				door.openLeft();
				popStack(stack);
				if(stack.size() > 0)
					door.adjustDoor(stack.firstElement().getY() + stack.firstElement().getHeight());
				door.closeLeft();
			} else { 
				door.openRight();
				if(stack.size() > 0)
					door.adjustDoor(stack.firstElement().getY() + stack.firstElement().getHeight()*2 + trainDist);
				pushStack(stack);
				door.closeRight();
			}
		}
	}
	
	public StackModel(DrawModel model) {
		super();
		this.model = model;
		this.shapeList = model.getShapeList();
		this.panel = model.getObserverPanel();
		this.panel.removeAll();
		this.panel.updateUI();
		this.swidth = panel.getWidth();
		this.swidth = panel.getHeight();
	}
	
	class StackDoor{
		private DsLine up;
		private DsLine down;
		private DsLine left;
		private DsLine right;
		private final int doorMargin = 20;
		
		public void initDoor(int bottom){
			up = new DsLine(ShapeSize.StackModel.STACK_LEFT_MARGIN-doorMargin, ShapeSize.StackModel.STACK_TOP_MARGIN-doorMargin, ShapeSize.StackModel.STACK_LEFT_MARGIN+ShapeSize.StackModel.NODE_WIDTH+doorMargin, ShapeSize.StackModel.STACK_TOP_MARGIN-doorMargin, false);
			down = new DsLine(ShapeSize.StackModel.STACK_LEFT_MARGIN-doorMargin, bottom+doorMargin, ShapeSize.StackModel.STACK_LEFT_MARGIN+ShapeSize.StackModel.NODE_WIDTH+doorMargin, bottom+doorMargin, false);
			left = new DsLine(up.x1, up.y1, down.x1, down.y1, false);
			right = new DsLine(up.x2, up.y1, down.x2, down.y2, false);
			synchronized (Shape.class) {
				shapeList.add(up);
				shapeList.add(down);
				shapeList.add(left);
				shapeList.add(right);
			}
			model.setViewChanged();
		}
		
		public void adjustDoor(int bottom){
			left.y2 = right.y2 = down.y1 = down.y2 = bottom + doorMargin;
			model.setViewChanged();
		}
		
		public void closeLeft(){
			Thread thread = new Thread(new Runnable() {
				@Override
				public void run() {
					final int distY = 5;
					for(int i=0; i<20; ++i){
						left.y1 -= distY;
						model.setViewChanged();
						delay(70);
					}
				}
			}, "childThread_CloseLeftDoor");
			
			thread.start();
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		public void openLeft(){
			Thread thread = new Thread(new Runnable() {
				@Override
				public void run() {
					final int distY = 5;
					for(int i=0; i<20; ++i){
						left.y1 += distY;
						model.setViewChanged();
						delay(70);
					}
				}
			}, "childThread_openLeftDoor");
			
			thread.start();
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		public void closeRight(){
			Thread thread = new Thread(new Runnable() {
				@Override
				public void run() {
					final int distY = 5;
					for(int i=0; i<20; ++i){
						right.y1 -= distY;
						model.setViewChanged();
						delay(70);
					}
				}
			}, "childThread_closeRightDoor");
			
			thread.start();
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		public void openRight(){
			Thread thread = new Thread(new Runnable() {
				@Override
				public void run() {
					final int distY = 5;
					for(int i=0; i<20; ++i){
						right.y1 += distY;
						model.setViewChanged();
						delay(70);
					}
				}
			}, "childThread_openRightDoor");
			
			thread.start();
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}

