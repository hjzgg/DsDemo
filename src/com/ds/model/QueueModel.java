package com.ds.model;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import com.ds.panel.DrawPanel;
import com.ds.shape.DsPeople;
import com.ds.shape.DsSampleRect;
import com.ds.shape.Shape;
import com.ds.size.ShapeSize;

public class QueueModel {
	private DrawModel model;
	private DrawPanel panel;
	ArrayList<Shape> shapeList;
	private static final int  DIR_RIGHT = 0;
	private static final int DIR_LEFT = 1;
	private static final int DIR_MIDDLE = 2;
	
	private void delay(int time){
		try {
			TimeUnit.MILLISECONDS.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	//屏幕的宽度和高度的
	private int swidth, sheight;
	
	private void popQueue(Queue<DsPeople> queue){
		ArrayList<Thread> threadList = new ArrayList<Thread>();
		if(queue != null && !queue.isEmpty()){
			DsPeople people = null;
			int threadIndex = 0;
			people = queue.peek();
			{
				Thread thread = new Thread(new PeopleRun(people, people.getX()-ShapeSize.QueueModel.NODE_WIDTH*2, -ShapeSize.QueueModel.NODE_HEIGHT, DsPeople.DIR_UP), "childThread_PopQueue" + ++threadIndex);
				thread.start();
				threadList.add(thread); 
			}
			
			DsPeople prePeople = null;
			for(Iterator<DsPeople> it = queue.iterator(); it.hasNext(); ){
				DsPeople curPeople = it.next();
				if(prePeople != null){
					Thread thread = new Thread(new PeopleRun(curPeople, prePeople.getX(), prePeople.getY(), prePeople.getDir()), "childThread_PopQueue" + ++threadIndex);
					thread.start();
					threadList.add(thread); 
				}
				prePeople = curPeople;
			}
			queue.poll();
			try {
				for(Thread thread : threadList)
					thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private class PeopleRun implements Runnable{
		private DsPeople people;
		private int tx, ty;
		private int tDir;
		private boolean isRandom = true;
		public void setNoRandom(){
			isRandom = false;
		}
		
		private void move(int dir){
			final int distX = Math.abs(tx - people.getX())/20+1;
			final int distY = Math.abs(ty - people.getY())/20+1;
			switch(dir){
				case DsPeople.DIR_UP:
					people.setDir(DsPeople.DIR_UP);
					while(people.getY() > ty){
						if(people.getY()-distY < ty){
							people.setXY(people.getX(), ty);
							break;
						}
						people.setXY(people.getX(), people.getY()-distY);
						delay(100);
					}
					break;
				case DsPeople.DIR_DOWN:
					people.setDir(DsPeople.DIR_DOWN);
					while(people.getY() < ty){
						if(people.getY()+distY > ty){
							people.setXY(people.getX(), ty);
							break;
						}
						people.setXY(people.getX(), people.getY()+distY);
						delay(100);
					}
					break;
				case DsPeople.DIR_LEFT:
					people.setDir(DsPeople.DIR_LEFT);
					while(people.getX() > tx){
						if(people.getX()-distX < tx){
							people.setXY(tx, people.getY());
							break;
						}
						people.setXY(people.getX()-distX, people.getY());
						delay(100);
					}
					break;
				case DsPeople.DIR_RIGHT:
					people.setDir(DsPeople.DIR_RIGHT);
					while(people.getX() < tx){
						if(people.getX()+distX > tx){
							people.setXY(tx, people.getY());
							break;
						}
						people.setXY(people.getX()+distX, people.getY());
						delay(100);
					}
					break;
				default:
					break;
			}
			people.setDir(tDir);
		}
		
		@Override
		public void run() {
			if(isRandom) {
				Random random = new Random();
				if(random.nextBoolean()){
					if(people.getX() < tx)
						move(DsPeople.DIR_RIGHT);
					else if(people.getX() > tx)
						move(DsPeople.DIR_LEFT);
					
					if(people.getY() < ty)
						move(DsPeople.DIR_DOWN);
					else if(people.getY() > ty)
						move(DsPeople.DIR_UP);
				} else {
					if(people.getY() < ty)
						move(DsPeople.DIR_DOWN);
					else if(people.getY() > ty)
						move(DsPeople.DIR_UP);
					
					if(people.getX() < tx)
						move(DsPeople.DIR_RIGHT);
					else if(people.getX() > tx)
						move(DsPeople.DIR_LEFT);
				}
			} else {
				if(people.getY() < ty)
					move(DsPeople.DIR_DOWN);
				else if(people.getY() > ty)
					move(DsPeople.DIR_UP);
				
				if(people.getX() < tx)
					move(DsPeople.DIR_RIGHT);
				else if(people.getX() > tx)
					move(DsPeople.DIR_LEFT);
			}
		}
		/**
		 * @param people	移动的人
		 * @param tx	目的x坐标
		 * @param ty	目的y坐标
		 */
		public PeopleRun(DsPeople people, int tx, int ty, int tDir) {
			super();
			this.people = people;
			this.tx = tx;
			this.ty = ty;
			this.tDir = tDir;
		}
		
	}
	
	private void pushQueue(Queue<DsPeople> queue){
		int size = queue.size();
		int row = size/(ShapeSize.QueueModel.NUMBER_OF_LINE+1);
		int col = size%(ShapeSize.QueueModel.NUMBER_OF_LINE+1);
		
		int ty = row*2*ShapeSize.QueueModel.NODE_HEIGHT+ShapeSize.QueueModel.QUEUE_TOP_MARGIN;
		//下一一行
		if(col == ShapeSize.QueueModel.NUMBER_OF_LINE) ty+=ShapeSize.QueueModel.NODE_HEIGHT;
		int tx = ShapeSize.QueueModel.QUEUE_LEFT_MARGIN + (row%2 != 0 ?  (col != ShapeSize.QueueModel.NUMBER_OF_LINE ? col*ShapeSize.QueueModel.NODE_WIDTH : ShapeSize.QueueModel.QUEUE_LEFT_MARGIN) :
			 				col*ShapeSize.QueueModel.NODE_WIDTH);
		
		DsPeople people = new DsPeople(DsPeople.DIR_LEFT, String.valueOf(++pn));
		Random randow = new Random();
		int lx = panel.getWidth() - Math.abs(randow.nextInt()) % (Math.abs(panel.getWidth()-swidth-ShapeSize.QueueModel.NODE_WIDTH)) - ShapeSize.QueueModel.NODE_WIDTH;
		int ly = Math.abs(randow.nextInt()) % (panel.getHeight()-ShapeSize.QueueModel.NODE_HEIGHT);
		int tDir = col == ShapeSize.QueueModel.NUMBER_OF_LINE ? DsPeople.DIR_UP : (row%2 != 0 ? DsPeople.DIR_RIGHT : DsPeople.DIR_LEFT);
		people.setBounds(lx, ly, ShapeSize.QueueModel.NODE_WIDTH, ShapeSize.QueueModel.NODE_HEIGHT);
		people.setDir(DsPeople.DIR_LEFT);
		PeopleRun pr = new PeopleRun(people, tx, ty, tDir);
		pr.setNoRandom();
		panel.add(people);
		
		Thread thread = new Thread(pr, "childThread_pushQueue");
		thread.start();
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		queue.add(people);
	}
	private int pn = 20;
	public void showQueue(){
		Random randow = new Random();
		String[] peopleNum = new String[pn];
		for(int i=1; i<=pn; ++i)
			peopleNum[i-1] = String.valueOf(i);
			
		int left = ShapeSize.QueueModel.QUEUE_LEFT_MARGIN - ShapeSize.QueueModel.NODE_WIDTH;
		int top = ShapeSize.QueueModel.QUEUE_TOP_MARGIN;
		
		
		int dir = DIR_RIGHT, preDir = 0;
		int numberOfLine = 0;
		ArrayList<Thread> threadList = new ArrayList<Thread>();
		Queue<DsPeople> queue = new LinkedList<DsPeople>();
		for(int i=0; i<peopleNum.length; ++i){
			DsPeople people = new DsPeople(DsPeople.DIR_UP, peopleNum[i]);
			queue.add(people);
			
			PeopleRun pr = null;
			int tx = 0, ty = 0;
			if(dir == DIR_RIGHT) {
				left += ShapeSize.QueueModel.NODE_WIDTH;
				tx = left;
				ty = top;
				pr = new PeopleRun(people, tx, ty, dir);
			} else if(dir == DIR_LEFT) {
				left -= ShapeSize.QueueModel.NODE_WIDTH;
				tx = left;
				ty = top;
				pr = new PeopleRun(people, tx, ty, dir);
			} else if(dir == DIR_MIDDLE) {
				//新的一行，这一行只有一个人
				top += ShapeSize.QueueModel.NODE_HEIGHT;
				tx = left;
				ty = top;
				pr = new PeopleRun(people, tx, ty, dir);
				
				//新的一行
				top += ShapeSize.QueueModel.NODE_HEIGHT;
				if(preDir == DIR_LEFT){
					dir = DIR_RIGHT;
					left -= ShapeSize.QueueModel.NODE_WIDTH;
				} else if(preDir == DIR_RIGHT) {
					dir = DIR_LEFT;
					left += ShapeSize.QueueModel.NODE_WIDTH;
				}
				numberOfLine = -1;
			}
			if(swidth < tx+ShapeSize.QueueModel.NODE_WIDTH*2)
				swidth = tx+ShapeSize.QueueModel.NODE_WIDTH*2;
			if(sheight < ty+ShapeSize.QueueModel.NODE_HEIGHT*2)
				sheight = ty+ShapeSize.QueueModel.NODE_HEIGHT*2;
			
			int lx = Math.abs(randow.nextInt()) % (panel.getWidth()-ShapeSize.QueueModel.NODE_WIDTH);
			int ly = Math.abs(randow.nextInt()) % (panel.getHeight()-ShapeSize.QueueModel.NODE_HEIGHT);

			people.setBounds(lx, ly, ShapeSize.QueueModel.NODE_WIDTH, ShapeSize.QueueModel.NODE_HEIGHT);
			people.setDir(DsPeople.DIR_UP);
			panel.add(people);
			Thread thread = new Thread(pr, "childThread_peopleMove" + i);
			thread.start();
			threadList.add(thread);
			++numberOfLine;
			if(numberOfLine == ShapeSize.QueueModel.NUMBER_OF_LINE){
				preDir = dir;
				dir = DIR_MIDDLE;
			}
		}

		if(swidth > panel.getWidth())
			panel.setPreferredSize(new Dimension(swidth, panel.getHeight()));
		if(sheight > panel.getHeight())
			panel.setPreferredSize(new Dimension(panel.getWidth(), sheight));
		
		try {
			for(Thread thread : threadList)
				thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		//出队列，或者入队列的操作
		for(int i=0; i<10; ++i){
			if(randow.nextBoolean())
				pushQueue(queue);
			else 
				popQueue(queue);
		}
	}
	
	public QueueModel(DrawModel model) {
		super();
		this.model = model;
		this.shapeList = model.getShapeList();
		this.panel = model.getObserverPanel();
		this.panel.removeAll();
		this.panel.updateUI();
		this.swidth = panel.getWidth();
		this.swidth = panel.getHeight();
	}
	
}
