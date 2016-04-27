package com.ds.model;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.ds.panel.DrawPanel;
import com.ds.shape.DsImage;
import com.ds.shape.DsLine;
import com.ds.shape.DsPeople;
import com.ds.shape.Shape;
import com.ds.size.ShapeSize;

public class MazeModel {
	private DrawModel model;
	private DrawPanel panel;
	private ArrayList<Shape> shapeList;
	private MazeShape mazeShape;
	/**
	 * @param data 存有迷宫的宽度和长度，已经 小人的初始位置， 结束位置， 6个数字
	 */
	public void mazeShowByDfs(String data){
		String[] params = data.split(" ");
		if(params.length != 6) return;
		int mr = Integer.parseInt(params[0]);
		int mc = Integer.parseInt(params[1]);
		int px = Integer.parseInt(params[2]);
		int py = Integer.parseInt(params[3]);
		int ex = Integer.parseInt(params[4]);
		int ey = Integer.parseInt(params[5]);
	    mazeShape = new MazeShape(mr, mc, px, py, ex, ey);
		model.setViewChanged();
		showDfs(mr, mc, px, py, ex, ey);
	}
	
	private int swidth, sheight;
	
	private static final int[][] dir = {{0,1}, {1,0}, {0,-1}, {-1,0}};
	private static final int[] pDir = {MazeRect.RIGHT, MazeRect.BOTTOM, MazeRect.LEFT, MazeRect.TOP};
	private static final int[] reversePDir = {MazeRect.LEFT, MazeRect.TOP, MazeRect.RIGHT, MazeRect.BOTTOM};
	/**
	 * @param mr	迷宫的行数
	 * @param mc	迷宫的列数
	 * @param px	人的初始位置x
	 * @param py	人的初始位置y
	 * @param ex	出口位置x
	 * @param ey	出口位置y
	 * @param ppx	上一步的位置x
	 * @param ppy	上一步的位置y
	 */
	private boolean showDfs(int mr, int mc, int px, int py, int ex, int ey){
		if(px==ex && py==ey){
			return true;
		}
		mazeShape.vis[px][py] = true;
		for(int i=0; i<dir.length; ++i){
			int npx = px + dir[i][0];
			int npy = py + dir[i][1];
			if(npx<0 || npy<0 || npx>=mr || npy>=mc || mazeShape.vis[npx][npy] || !mazeShape.maze[npx][npy]) continue;
			DsLine tmpLine = null;
			tmpLine = mazeShape.openDoor(px, py, pDir[i]);
			
			mazeShape.peopleMove(mazeShape.people, pDir[i], npx, npy);
			//画脚印
			DsImage foots = new DsImage("image/foots.png");
			foots.setBounds(mazeShape.mazeRect[px][py].left, mazeShape.mazeRect[px][py].top, ShapeSize.MazeModel.NODE_WIDTH, ShapeSize.MazeModel.NODE_HEIGHT);
			panel.add(foots);
			mazeShape.closeDoor(tmpLine);
			
			//找到出口，结束
			if(npx==ex && npy==ey) { return true; }
			if(showDfs(mr, mc, npx, npy, ex, ey)) return true;
			tmpLine = mazeShape.openDoor(npx, npy, reversePDir[i]);
			panel.remove(foots);
			panel.updateUI();
			mazeShape.peopleMove(mazeShape.people, reversePDir[i], px, py);
			mazeShape.closeDoor(tmpLine);
		}
		return false;
	}
	
	public void mazeShowByBfs(String datas){
		String[] params = datas.split(" ");
		if(params.length != 6) return;
		int mr = Integer.parseInt(params[0]);
		int mc = Integer.parseInt(params[1]);
		int px = Integer.parseInt(params[2]);
		int py = Integer.parseInt(params[3]);
		int ex = Integer.parseInt(params[4]);
		int ey = Integer.parseInt(params[5]);
	    mazeShape = new MazeShape(mr, mc, px, py, ex, ey);
		model.setViewChanged();
		
		if(px==ex && py==ey) return;
		
		Point begin = new Point(px, py);
		Queue<Point> queue = new LinkedList<Point>();
		Set<Point> vis = new HashSet<Point>();
		queue.add(begin);
		vis.add(begin);
		mazeShape.mapToPeople.put(begin, mazeShape.people);
		
		class BfsOperation implements Runnable{
			Point cur, next;
			int dir;
			@Override
			public void run() {
				//产生新的people，设置方向
				DsPeople prePeople = mazeShape.mapToPeople.get(cur);
				DsPeople people = new DsPeople(dir, null);
				people.setBounds(prePeople.getX(), prePeople.getY(), prePeople.getWidth(), prePeople.getHeight());
				panel.add(people);
				panel.setComponentZOrder(people, 0);
				
				DsLine tmpLine = mazeShape.openDoor(cur.x, cur.y, dir);
				mazeShape.peopleMove(people, dir, next.x, next.y);
				mazeShape.closeDoor(tmpLine);
				mazeShape.mapToPeople.put(next, people);
			}
			public BfsOperation(Point cur, Point next, int dir) {
				super();
				this.cur = cur;
				this.next = next;
				this.dir = dir;
			}
		}
		while(!queue.isEmpty()){
			Point cur = queue.poll();
			List<Thread> threadList = new ArrayList<Thread>();
			boolean find = false;
			for(int i=0; i<4; ++i){
				int x = cur.x + dir[i][0];
				int y = cur.y + dir[i][1];
				if(x<0 || y<0 || x>=mr || y>=mc) continue;
				if(x==ex && y==ey) find = true;
				Point next = new Point(x, y);
				if(vis.contains(next) || !mazeShape.maze[x][y]) continue;
				vis.add(next);
				queue.add(next);
				Thread thread = new Thread(new BfsOperation(cur, next, pDir[i]), "childThread_PeopleMove" + i);
				threadList.add(thread);
				thread.start();
			}
			if(threadList.size() > 0){
				DsPeople people = mazeShape.mapToPeople.get(new Point(cur.x, cur.y));
				panel.remove(people);
				DsImage foots = new DsImage("image/foots.png");
				foots.setBounds(people.getX(), people.getY(), people.getWidth(), people.getHeight());
				panel.add(foots);
			}
			
			try {
				for(Thread thread : threadList)
					thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if(find) break;
		}
	}
	
	class MazeShape{
		//用于 dfs 遍历
		private DsPeople people = null;
		//用于 bfs 遍历
		private Map<Point, DsPeople> mapToPeople = new HashMap<Point, DsPeople>();
		private MazeRect[][] mazeRect = null;
		private boolean[][] maze = null;
		private boolean[][] vis = null;
		//初始化迷宫
		private void initMaze(int mr, int mc, int px, int py, int ex, int ey){
			swidth = panel.getWidth();
			sheight = panel.getHeight();
			mazeRect = new MazeRect[mr][];
			int lx, ly = ShapeSize.MazeModel.MAZE_TOP_MARGIN;
			int lw = ShapeSize.MazeModel.NODE_WIDTH, lh = ShapeSize.MazeModel.NODE_HEIGHT;
			for(int i=0; i<mr; ++i){
				lx = ShapeSize.MazeModel.MAZE_LEFT_MARGIN;
				mazeRect[i] = new MazeRect[mc];
				for(int j=0; j<mc; ++j){
					mazeRect[i][j] = new MazeRect(lx, ly, lw, lh);
					lx += ShapeSize.MazeModel.NODE_WIDTH;
					if(swidth < lx+ShapeSize.MazeModel.NODE_WIDTH)
						swidth = lx+ShapeSize.MazeModel.NODE_WIDTH;
					//画边
					DsLine topLine = new DsLine(mazeRect[i][j].left, mazeRect[i][j].top, mazeRect[i][j].left+mazeRect[i][j].width, mazeRect[i][j].top, false);
					DsLine leftLine = new DsLine(mazeRect[i][j].left, mazeRect[i][j].top, mazeRect[i][j].left, mazeRect[i][j].top+mazeRect[i][j].height, false);
					mazeRect[i][j].lines[MazeRect.LEFT] = leftLine;
					mazeRect[i][j].lines[MazeRect.TOP] = topLine;
					//(i,j)上面和左边的来两个矩形的底边 和 右边
					if(j > 0) mazeRect[i][j-1].lines[MazeRect.RIGHT] = leftLine;
					if(i > 0) mazeRect[i-1][j].lines[MazeRect.BOTTOM] = topLine;
					
					shapeList.add(leftLine);
					shapeList.add(topLine);
				}
				ly += ShapeSize.MazeModel.NODE_HEIGHT;
				if(sheight < ly+ShapeSize.MazeModel.NODE_HEIGHT)
					sheight = ly+ShapeSize.MazeModel.NODE_HEIGHT;
			}
			
			//调整屏幕的尺寸
			if(swidth > panel.getWidth())
				panel.setPreferredSize(new Dimension(swidth, panel.getHeight()));
			if(sheight > panel.getHeight())
				panel.setPreferredSize(new Dimension(panel.getWidth(), sheight));
			
			//填充整个迷宫的最右边的边
			for(int i=0; i<mr; ++i){
				DsLine rightLine = new DsLine(mazeRect[i][mc-1].left+mazeRect[i][mc-1].width, mazeRect[i][mc-1].top, mazeRect[i][mc-1].left+mazeRect[i][mc-1].width, mazeRect[i][mc-1].top+mazeRect[i][mc-1].height, false);
				mazeRect[i][mc-1].lines[MazeRect.RIGHT] = rightLine;
				shapeList.add(rightLine);
			}
			//填充整个迷宫的最底部的边
			for(int j=0; j<mc; ++j){
				DsLine bottomLine = new DsLine(mazeRect[mr-1][j].left, mazeRect[mr-1][j].top+mazeRect[mr-1][j].height, mazeRect[mr-1][j].left+mazeRect[mr-1][j].width, mazeRect[mr-1][j].top+mazeRect[mr-1][j].height, false);
				mazeRect[mr-1][j].lines[MazeRect.BOTTOM] = bottomLine;
				shapeList.add(bottomLine);
			}
			vis = new boolean[mr][];
			maze = new boolean[mr][];
			for(int i=0; i<mr; ++i){
				maze[i] = new boolean[mc];
				vis[i] = new boolean[mc];
				for(int j=0; j<mc; ++j){
					maze[i][j] = true;
					vis[i][j] = false;
				}
			}
			
			Random random = new Random();
			//随机 迷宫中的墙壁
			for(int i=0; i < mr; ++i){
				for(int j=0; j< mc; ++j){
					if((i==px && j==py || i==ex && j==ey)) continue;
					int num = Math.abs(random.nextInt()) % mc;
					if(num <= mc/4){//1/4的概率，表示这个方格是墙壁
						maze[i][j] = false;
						//画墙壁
						DsImage wall = new DsImage("image/wall.png");
						wall.setBounds(mazeRect[i][j].left, mazeRect[i][j].top, ShapeSize.MazeModel.NODE_WIDTH, ShapeSize.MazeModel.NODE_HEIGHT);
						panel.add(wall);
					}
				}
			}
			
			//迷宫的门
			DsImage door = new DsImage("image/door.png");
			door.setBounds(mazeRect[ex][ey].left, mazeRect[ex][ey].top, ShapeSize.MazeModel.NODE_WIDTH, ShapeSize.MazeModel.NODE_HEIGHT);
			panel.add(door);
			
			int tpx = ShapeSize.MazeModel.MAZE_LEFT_MARGIN + py*ShapeSize.MazeModel.NODE_WIDTH;
			int tpy = ShapeSize.MazeModel.MAZE_TOP_MARGIN + px*ShapeSize.MazeModel.NODE_HEIGHT;
			people = new DsPeople(DsPeople.DIR_DOWN, null);
			people.setBounds(tpx, tpy, ShapeSize.MazeModel.NODE_WIDTH, ShapeSize.MazeModel.NODE_HEIGHT);
			panel.add(people);
			panel.setComponentZOrder(people, 0);
		}
		
		private DsLine topLine, bottomLine, leftLine, rightLine;
		private static final int DOOR_MOVE_DIST = 5;
		/**
		 * @param x	人所在的方格X，打开对应方向的门
		 * @param y
		 * @param dir
		 */
		public DsLine openDoor(int x, int y, int dir){
			DsLine tmpLine = mazeRect[x][y].lines[dir];
			synchronized (Shape.class) {
				shapeList.remove(tmpLine);
			}
			
			if(tmpLine.x1 == tmpLine.x2){//垂直线
				topLine = new DsLine(tmpLine.x1, tmpLine.y1, tmpLine.x2, tmpLine.y1+ShapeSize.MazeModel.NODE_HEIGHT/2, false);
				bottomLine = new DsLine(tmpLine.x2, tmpLine.y1+ShapeSize.MazeModel.NODE_HEIGHT/2, tmpLine.x2, tmpLine.y2, false);
				synchronized (Shape.class) {
					shapeList.add(topLine);
					shapeList.add(bottomLine);
				}
				
				
				while(topLine.y2 > topLine.y1 && bottomLine.y1 < bottomLine.y2){
					topLine.y2 -= DOOR_MOVE_DIST;
					bottomLine.y1 += DOOR_MOVE_DIST;
					if(topLine.y2 < topLine.y1){
						topLine.y2 = topLine.y1;
					}
					if(bottomLine.y1 > bottomLine.y2){
						bottomLine.y1 = bottomLine.y2;
					}
					model.setViewChanged();
					delay(100);
				}
				
				synchronized (Shape.class) {
					shapeList.remove(topLine);
					shapeList.remove(bottomLine);
				}
			} else {//水平线
				leftLine = new DsLine(tmpLine.x1, tmpLine.y1, tmpLine.x1+ShapeSize.MazeModel.NODE_WIDTH/2, tmpLine.y2, false);
				rightLine = new DsLine(tmpLine.x1+ShapeSize.MazeModel.NODE_WIDTH/2, tmpLine.y2, tmpLine.x2, tmpLine.y2, false);
				synchronized (Shape.class) {
					shapeList.add(leftLine);
					shapeList.add(rightLine);
				}
				
				while(leftLine.x2 > leftLine.x1 && rightLine.x1 < rightLine.x2){
					leftLine.x2 -= DOOR_MOVE_DIST;
					rightLine.x1 += DOOR_MOVE_DIST;
					if(leftLine.x2 < leftLine.x1){
						leftLine.x2 = leftLine.x1;
					}
					if(rightLine.x1 > rightLine.x2){
						rightLine.x1 = rightLine.x2;
					}
					model.setViewChanged();
					delay(100);
				}
				
				synchronized (Shape.class) {
					shapeList.remove(leftLine);
					shapeList.remove(rightLine);
				}
			}
			
			return tmpLine;
		}
		
		public void closeDoor(DsLine tmpLine){
			if(tmpLine.x1 == tmpLine.x2){
				synchronized (Shape.class) {
					shapeList.add(topLine);
					shapeList.add(bottomLine);
				}
				
				while(topLine.y2 < bottomLine.y1){
					topLine.y2 += DOOR_MOVE_DIST;
					bottomLine.y1 -= DOOR_MOVE_DIST;
					if(topLine.y2 > bottomLine.y1){
						continue;
					}
					model.setViewChanged();
					delay(100);
				}
				
				synchronized (Shape.class) {
					shapeList.remove(topLine);
					shapeList.remove(bottomLine);
				}
			} else {
				synchronized (Shape.class) {
					shapeList.add(leftLine);
					shapeList.add(rightLine);
				}
				
				while(leftLine.x2 < rightLine.x1){
					leftLine.x2 += DOOR_MOVE_DIST;
					rightLine.x1 -= DOOR_MOVE_DIST;
					if(leftLine.x2 > rightLine.x1){
						continue;
					}
					model.setViewChanged();
					delay(100);
				}
				
				synchronized (Shape.class) {
					shapeList.remove(leftLine);
					shapeList.remove(rightLine);
				}
			}
			
			synchronized (Shape.class) {
				shapeList.add(tmpLine);
			}
		}
		
		public void peopleMove(DsPeople curPeople, int dir, int x, int y){
			curPeople.setDir(dir);
			final int PEOPLE_MOVE_DIST = 5;
			int tx = ShapeSize.MazeModel.MAZE_LEFT_MARGIN + y*ShapeSize.MazeModel.NODE_WIDTH;
			int ty = ShapeSize.MazeModel.MAZE_TOP_MARGIN + x*ShapeSize.MazeModel.NODE_HEIGHT;
			switch(dir){
				case MazeRect.LEFT:
					while(curPeople.getX() > tx){
						curPeople.setXY(curPeople.getX()-PEOPLE_MOVE_DIST, curPeople.getY());
						if(curPeople.getX() < tx)
							curPeople.setXY(tx, curPeople.getY());
						delay(100);
					}
					break;
				case MazeRect.RIGHT:
					while(curPeople.getX() < tx){
						curPeople.setXY(curPeople.getX()+PEOPLE_MOVE_DIST, curPeople.getY());
						if(curPeople.getX() > tx)
							curPeople.setXY(tx, curPeople.getY());
						delay(100);
					} 
					break;
				case MazeRect.TOP:
					while(curPeople.getY() > ty){
						curPeople.setXY(curPeople.getX(), curPeople.getY()-PEOPLE_MOVE_DIST);
						if(curPeople.getY() < ty)
							curPeople.setXY(curPeople.getX(), ty);
						delay(100);
					}
					break;
				case MazeRect.BOTTOM:
					while(curPeople.getY() < ty){
						curPeople.setXY(curPeople.getX(), curPeople.getY()+PEOPLE_MOVE_DIST);
						if(curPeople.getY() > ty)
							curPeople.setXY(curPeople.getX(), ty);
						delay(100);
					} 
					break;
			}
		}
		
		public MazeShape(int mr, int mc, int px, int py, int ex, int ey) {
			shapeList.clear();
			panel.removeAll();
			initMaze(mr, mc, px, py, ex, ey);
		}
		
	}
	
	private void delay(int time){
		try {
			TimeUnit.MILLISECONDS.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	class MazeRect{
		public int left, top, width, height;
		//和这个矩形关联的4条边
		public static final int LEFT = 0;
		public static final int RIGHT = 1;
		public static final int TOP = 2;
		public static final int BOTTOM = 3;
		public DsLine[] lines = new DsLine[4];
		public MazeRect(int left, int top, int width, int height) {
			super();
			this.left = left;
			this.top = top;
			this.width = width;
			this.height = height;
		}

	}
	
	public MazeModel(DrawModel model) {
		super();
		this.model = model;
		this.shapeList = model.getShapeList();
		this.panel = model.getObserverPanel();
	}
}

class Point{
	public int x, y;

	public Point(int x, int y) {
		super();
		this.x = x;
		this.y = y;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Point other = (Point) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}
}


