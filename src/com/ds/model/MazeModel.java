package com.ds.model;

import java.util.ArrayList;
import java.util.Random;
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
	 * @param data �����Թ��Ŀ�Ⱥͳ��ȣ��Ѿ� С�˵ĳ�ʼλ�ã� ����λ�ã� 6������
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
	
	private static final int[][] dir = {{0,1}, {1,0}, {0,-1}, {-1,0}};
	private static final int[] pDir = {MazeRect.RIGHT, MazeRect.BOTTOM, MazeRect.LEFT, MazeRect.TOP};
	private static final int[] reversePDir = {MazeRect.LEFT, MazeRect.TOP, MazeRect.RIGHT, MazeRect.BOTTOM};
	/**
	 * @param mr	�Թ�������
	 * @param mc	�Թ�������
	 * @param px	�˵ĳ�ʼλ��x
	 * @param py	�˵ĳ�ʼλ��y
	 * @param ex	����λ��x
	 * @param ey	����λ��y
	 * @param ppx	��һ����λ��x
	 * @param ppy	��һ����λ��y
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
			mazeShape.openDoor(px, py, pDir[i]);
			mazeShape.peopleMove(pDir[i], npx, npy);
			//����ӡ
			DsImage foots = new DsImage("image/foots.png");
			foots.setBounds(mazeShape.mazeRect[px][py].left, mazeShape.mazeRect[px][py].top, ShapeSize.MazeModel.NODE_WIDTH, ShapeSize.MazeModel.NODE_HEIGHT);
			panel.add(foots);
			mazeShape.closeDoor();
			//�ҵ����ڣ�����
			if(npx==ex && npy==ey) { return true; }
			if(showDfs(mr, mc, npx, npy, ex, ey)) return true;
			mazeShape.openDoor(npx, npy, reversePDir[i]);
			panel.remove(foots);
			panel.updateUI();
			mazeShape.peopleMove(reversePDir[i], px, py);
			mazeShape.closeDoor();
		}
		return false;
	}
	
	public void mazeShowByBfs(){
		
	}
	
	class MazeShape{
		private DsPeople people = null;
		private MazeRect[][] mazeRect = null;
		private boolean[][] maze = null;
		private boolean[][] vis = null;
		//��ʼ���Թ�
		private void initMaze(int mr, int mc, int px, int py, int ex, int ey){
			mazeRect = new MazeRect[mr][];
			int lx, ly = ShapeSize.MazeModel.MAZE_TOP_MARGIN;
			int lw = ShapeSize.MazeModel.NODE_WIDTH, lh = ShapeSize.MazeModel.NODE_HEIGHT;
			for(int i=0; i<mr; ++i){
				lx = ShapeSize.MazeModel.MAZE_LEFT_MARGIN;
				mazeRect[i] = new MazeRect[mc];
				for(int j=0; j<mc; ++j){
					mazeRect[i][j] = new MazeRect(lx, ly, lw, lh);
					lx += ShapeSize.MazeModel.NODE_WIDTH;
					//����
					DsLine topLine = new DsLine(mazeRect[i][j].left, mazeRect[i][j].top, mazeRect[i][j].left+mazeRect[i][j].width, mazeRect[i][j].top, false);
					DsLine leftLine = new DsLine(mazeRect[i][j].left, mazeRect[i][j].top, mazeRect[i][j].left, mazeRect[i][j].top+mazeRect[i][j].height, false);
					mazeRect[i][j].lines[MazeRect.LEFT] = leftLine;
					mazeRect[i][j].lines[MazeRect.TOP] = topLine;
					//(i,j)�������ߵ����������εĵױ� �� �ұ�
					if(j > 0) mazeRect[i][j-1].lines[MazeRect.RIGHT] = leftLine;
					if(i > 0) mazeRect[i-1][j].lines[MazeRect.BOTTOM] = topLine;
					
					shapeList.add(leftLine);
					shapeList.add(topLine);
				}
				ly += ShapeSize.MazeModel.NODE_HEIGHT;
			}
			
			//��������Թ������ұߵı�
			for(int i=0; i<mr; ++i){
				DsLine rightLine = new DsLine(mazeRect[i][mc-1].left+mazeRect[i][mc-1].width, mazeRect[i][mc-1].top, mazeRect[i][mc-1].left+mazeRect[i][mc-1].width, mazeRect[i][mc-1].top+mazeRect[i][mc-1].height, false);
				mazeRect[i][mc-1].lines[MazeRect.RIGHT] = rightLine;
				shapeList.add(rightLine);
			}
			//��������Թ�����ײ��ı�
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
			//��� �Թ��е�ǽ��
			for(int i=0; i < mr; ++i){
				for(int j=0; j< mc; ++j){
					if((i==px && j==py || i==ex && j==ey)) continue;
					int num = Math.abs(random.nextInt()) % mc;
					if(num <= mc/4){//1/4�ĸ��ʣ���ʾ���������ǽ��
						maze[i][j] = false;
						//��ǽ��
						DsImage wall = new DsImage("image/wall.png");
						wall.setBounds(mazeRect[i][j].left, mazeRect[i][j].top, ShapeSize.MazeModel.NODE_WIDTH, ShapeSize.MazeModel.NODE_HEIGHT);
						panel.add(wall);
					}
				}
			}
			
			//�Թ�����
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
		
		private DsLine tmpLine;
		private DsLine topLine, bottomLine, leftLine, rightLine;
		private static final int DOOR_MOVE_DIST = 5;
		/**
		 * @param x	�����ڵķ���X���򿪶�Ӧ�������
		 * @param y
		 * @param dir
		 */
		public void openDoor(int x, int y, int dir){
		    tmpLine = mazeRect[x][y].lines[dir];
			synchronized (Shape.class) {
				shapeList.remove(tmpLine);
			}
			
			if(tmpLine.x1 == tmpLine.x2){//��ֱ��
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
			} else {//ˮƽ��
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
		}
		
		public void closeDoor(){
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
		
		public void peopleMove(int dir, int x, int y){
			people.setDir(dir);
			final int PEOPLE_MOVE_DIST = 5;
			int tx = ShapeSize.MazeModel.MAZE_LEFT_MARGIN + y*ShapeSize.MazeModel.NODE_WIDTH;
			int ty = ShapeSize.MazeModel.MAZE_TOP_MARGIN + x*ShapeSize.MazeModel.NODE_HEIGHT;
			switch(dir){
				case MazeRect.LEFT:
					while(people.getX() > tx){
						people.setXY(people.getX()-PEOPLE_MOVE_DIST, people.getY());
						if(people.getX() < tx)
							people.setXY(tx, people.getY());
						delay(100);
					}
					break;
				case MazeRect.RIGHT:
					while(people.getX() < tx){
						people.setXY(people.getX()+PEOPLE_MOVE_DIST, people.getY());
						if(people.getX() > tx)
							people.setXY(tx, people.getY());
						delay(100);
					} 
					break;
				case MazeRect.TOP:
					while(people.getY() > ty){
						people.setXY(people.getX(), people.getY()-PEOPLE_MOVE_DIST);
						if(people.getY() < ty)
							people.setXY(people.getX(), ty);
						delay(100);
					}
					break;
				case MazeRect.BOTTOM:
					while(people.getY() < ty){
						people.setXY(people.getX(), people.getY()+PEOPLE_MOVE_DIST);
						if(people.getY() > ty)
							people.setXY(people.getX(), ty);
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
		//��������ι�����4����
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


