package com.ds.model;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import com.ds.shape.DsLine;
import com.ds.shape.DsRect;
import com.ds.shape.DsSampleRect;
import com.ds.shape.Shape;
import com.ds.size.ShapeSize;

public class RadixSortModel {
	private DrawModel model;
	private boolean lsdOrMsd;
	private ArrayList<Shape> shapeList;
	
	private void sampleRectAppear(DsSampleRect[] rects){
		if(rects == null || rects.length == 0) return;
		int offDistY = 2;
		String[] contents = new String[rects.length];
		for(int i=0; i < rects.length; ++i){
			contents[i] = rects[i].content;
			rects[i].content = "";
			rects[i].lh = 0;
		}
		
		while(rects[0].lh < ShapeSize.RadixSortModel.NODE_HEIGHT){
			if(rects[0].lh + offDistY > ShapeSize.RadixSortModel.NODE_HEIGHT){
				for(DsSampleRect rect : rects)
					rect.lh = ShapeSize.RadixSortModel.NODE_HEIGHT;
			} else { 
				for(DsSampleRect rect : rects)
					rect.lh += offDistY;
			}
			model.setViewChanged();
			delay(100);
		}
		for(int i=0; i < rects.length; ++i){
			rects[i].lh = ShapeSize.RadixSortModel.NODE_HEIGHT;
			rects[i].content = contents[i];
		}
	}
	
	private void lsdRadixSort2(ArrayList<SortNode> nodeList){
		int left = ShapeSize.RadixSortModel.LEFT_MARGIN+(nodeList.get(nodeList.size()-1).shape.lx+ShapeSize.RadixSortModel.NODE_WIDTH-nodeList.get(0).shape.lx - ShapeSize.RadixSortModel.NODE_WIDTH*4)/2;
		if(left < 0) left = ShapeSize.RadixSortModel.LEFT_MARGIN;
		int top = ShapeSize.RadixSortModel.TOP_MARGIN/3;
		DsRect msgRect = new DsRect(left, top, ShapeSize.RadixSortModel.NODE_WIDTH*4, ShapeSize.RadixSortModel.NODE_HEIGHT, "");
		msgRect.color = Color.YELLOW;
		msgRect.fontSize = 15;
		shapeList.add(msgRect);
		int maxLen = -1;//记录最长数据的长度
		for(int i=0; i<nodeList.size(); ++i)
			if(nodeList.get(i).content.length() > maxLen)
				maxLen = nodeList.get(i).content.length();
		
		ArrayList<DsRect> bucket = new ArrayList<DsRect>();
		for(int j=0; j<nodeList.size(); ++j){
			DsRect rect = new DsRect(nodeList.get(j).shape.lx-ShapeSize.RadixSortModel.NODE_WIDTH, nodeList.get(j).shape.ly+ShapeSize.RadixSortModel.NODE_HEIGHT*4, ShapeSize.RadixSortModel.NODE_WIDTH, ShapeSize.RadixSortModel.NODE_HEIGHT, "");
			bucket.add(rect);
		}
		//最低位开始，根据这个位置上的数字进行节点的分发
		for(int i=1; i <= maxLen; ++i){
			msgRect.content = "根据倒数第"+ i +"位KW排序";
			//cnt, 统计每个桶 最右边界的 索引位置
			Map<Character, Integer> cnt = new TreeMap<Character, Integer>();
			for(int j=0; j < nodeList.size(); ++j){
				String data = nodeList.get(j).content;
				Character ch;
				if(data.length()-i >= 0)
					ch = data.charAt(data.length()-i);
				else 
					ch = 0;
				if(!cnt.containsKey(ch))
					cnt.put(ch, 1);
				else
					cnt.put(ch, cnt.get(ch)+1);
			}
			
			//初始化桶视图
			for(DsRect rect : bucket){
				synchronized (Shape.class) {shapeList.add(rect);}
				moveRect(rect, rect.lx+ShapeSize.RadixSortModel.NODE_WIDTH, DIR_RIGHT);
			}
			
			
			ArrayList<DsSampleRect> bucketTip = new ArrayList<DsSampleRect>();
			Character preCh = null;
			boolean upOrDown = true;
			for(Character ch : cnt.keySet()){
				DsSampleRect rect = null;
				if(preCh == null){
					rect = new DsSampleRect(ShapeSize.RadixSortModel.LEFT_MARGIN, ShapeSize.RadixSortModel.TOP_MARGIN+ShapeSize.RadixSortModel.NODE_HEIGHT*3, cnt.get(ch)*ShapeSize.RadixSortModel.NODE_WIDTH, ShapeSize.RadixSortModel.NODE_HEIGHT, "kw:" + (ch == 0 ? "□" : String.valueOf(ch)));
					upOrDown = !upOrDown;
				}
				else {
					cnt.put(ch, cnt.get(ch)+cnt.get(preCh));
					rect = new DsSampleRect(ShapeSize.RadixSortModel.LEFT_MARGIN+cnt.get(preCh)*ShapeSize.RadixSortModel.NODE_WIDTH, 
							upOrDown ? ShapeSize.RadixSortModel.TOP_MARGIN+ShapeSize.RadixSortModel.NODE_HEIGHT*3 : ShapeSize.RadixSortModel.TOP_MARGIN+ShapeSize.RadixSortModel.NODE_HEIGHT*5, 
									(cnt.get(ch)-cnt.get(preCh))*ShapeSize.RadixSortModel.NODE_WIDTH, ShapeSize.RadixSortModel.NODE_HEIGHT, "kw:" + (ch == 0 ? "" : String.valueOf(ch)));
					upOrDown = !upOrDown;
				}
				rect.color = Color.GREEN;
				rect.fontSize = 20;
				shapeList.add(rect);
				bucketTip.add(rect);
				preCh = ch;
			}
			
			sampleRectAppear(bucketTip.toArray(new DsSampleRect[]{}));
			
			for(int j=nodeList.size()-1; j >= 0; --j){
				String data = nodeList.get(j).content;
				DsRect movingRect = (DsRect) nodeList.get(j).shape.clone();
				nodeList.get(j).content = nodeList.get(j).shape.content = "";
				movingRect.color = Color.CYAN;
				synchronized (Shape.class) {shapeList.add(movingRect);}
				Character ch;
				if(data.length()-i >= 0)
					ch = data.charAt(data.length()-i);
				else 
					ch = 0;
				moveRect(movingRect, movingRect.ly+ShapeSize.RadixSortModel.NODE_HEIGHT*2, DIR_DOWN);
				int num = cnt.get(ch);//桶的块数
				int pos = bucket.get(num-1).lx;
				if(movingRect.lx > pos)
					moveRect(movingRect, pos, DIR_LEFT);
				else
					moveRect(movingRect, pos, DIR_RIGHT);
				moveRect(movingRect, bucket.get(num-1).ly, DIR_DOWN);
				synchronized (Shape.class) {shapeList.remove(movingRect);}
				bucket.get(num-1).fontSize = movingRect.fontSize;
				bucket.get(num-1).content = movingRect.content;
				cnt.put(ch, num-1);
			}
			
			
			if(bucketTip.size() > 0)
				circleFly((DsSampleRect)bucketTip.get(0).clone(), bucketTip.toArray(new DsSampleRect[]{}));
			
			MyRunMoveRectsUp run = new MyRunMoveRectsUp(ShapeSize.RadixSortModel.TOP_MARGIN, bucket.size());
			new Thread(run).start();
			for(int j=bucket.size()-1; j >= 0; --j){
				DsRect rect = bucket.get(j);
				DsRect movingRect = (DsRect)rect.clone();
				movingRect.color = Color.GREEN;
				synchronized (Shape.class) {shapeList.add(movingRect);}
				synchronized (RadixSortModel.class) {run.list.add(movingRect);}
				rect.lx -= ShapeSize.RadixSortModel.NODE_WIDTH;
				if(j != 0) {
					model.setViewChanged();
					delay(200);
				}
				synchronized (Shape.class) {shapeList.remove(rect);}
			}
			
			while(run.isRunStop());//阻塞
			for(int j=0; j < nodeList.size(); ++j) {
				nodeList.get(j).content = nodeList.get(j).shape.content = bucket.get(j).content;
				bucket.get(j).content = "";
			}
			shapeList.removeAll(run.list);
		}
	}
	
	private class MyRunMoveRectsUp implements Runnable{
		private int posY;
		private int sz;
		public ArrayList<DsRect> list = new ArrayList<DsRect>();
		private boolean flag = true;
		
		public boolean isRunStop(){
			return flag;
		}
		
		@Override
		public void run() {
			final int offDistY = 10;
			while(flag){
				synchronized (RadixSortModel.class) {
					boolean change = false;
					for(DsRect rect : list){
						if(rect.ly - offDistY >= posY){ 
							rect.ly -= offDistY;
							change = true;
						} else {
							rect.ly = posY;
						}
					}
					if(!change && list.size()==sz) flag = false;
				}
				model.setViewChanged();
				delay(200);
			}
		}
		public MyRunMoveRectsUp(int posY, int sz) {
			super();
			this.posY = posY;
			this.sz = sz;
		}
	}
	
	private void circleFly(DsSampleRect curRect, DsSampleRect[] curRects){
		int angle = 0;
		int orgX = curRect.lx;
		int orgY = curRect.ly;
		final int R = 120;
		while(angle <= 360){
			double x = Math.PI*angle/180;
			double sinx = Math.sin(x);
			double cosx = Math.cos(x);
			
			curRect.ly = (int) (orgY+R*(1-cosx));
			curRect.lx = (int) (orgX+R*sinx);
			
			for(DsSampleRect rect : curRects){
				rect.lx += curRect.lx-orgX;
				rect.ly += curRect.ly-orgY;
			}
			angle += 20;
			model.setViewChanged();
			delay(100);
			for(DsSampleRect rect : curRects){
				rect.lx -= curRect.lx-orgX;
				rect.ly -= curRect.ly-orgY;
			}
		}
		final int dist = 50;
		while(curRect.ly > -200){
			curRect.lx += dist;
			curRect.ly -= dist;
			for(DsSampleRect rect : curRects){
				rect.lx += dist;
				rect.ly -= dist;
			}
			model.setViewChanged();
			delay(100);
		}
		for(DsSampleRect rect : curRects)
			shapeList.remove(rect);
	}
	
	private void lsdRadixSort1(ArrayList<SortNode> nodeList){
		int left = ShapeSize.RadixSortModel.LEFT_MARGIN+(nodeList.get(nodeList.size()-1).shape.lx+ShapeSize.RadixSortModel.NODE_WIDTH-nodeList.get(0).shape.lx - ShapeSize.RadixSortModel.NODE_WIDTH*4)/2;
		if(left < 0) left = ShapeSize.RadixSortModel.LEFT_MARGIN;
		int top = ShapeSize.RadixSortModel.TOP_MARGIN/3;
		DsRect msgRect = new DsRect(left, top, ShapeSize.RadixSortModel.NODE_WIDTH*4, ShapeSize.RadixSortModel.NODE_HEIGHT, "");
		msgRect.color = Color.YELLOW;
		msgRect.fontSize = 15;
		shapeList.add(msgRect);
		int maxLen = -1;//记录最长数据的长度
		for(int i=0; i<nodeList.size(); ++i)
			if(nodeList.get(i).content.length() > maxLen)
				maxLen = nodeList.get(i).content.length();
		
		//最低位开始，根据这个位置上的数字进行节点的分发
		for(int i=1; i <= maxLen; ++i){
			msgRect.content = "根据倒数第"+ i +"位KW排序";
			//cnt, 统计每个桶的高度
			Map<Character, Integer> cnt = new TreeMap<Character, Integer>();
			for(int j=0; j < nodeList.size(); ++j){
				String data = nodeList.get(j).content;
				Character ch;
				if(data.length()-i >= 0)
					ch = data.charAt(data.length()-i);
				else 
					ch = 0;
				if(!cnt.containsKey(ch))
					cnt.put(ch, 1);
				else
					cnt.put(ch, cnt.get(ch)+1);
			}
			
			int maxH = -1;//统计桶的最大的高度
			for(Character ch : cnt.keySet()){
				if(maxH < cnt.get(ch))
					maxH = cnt.get(ch);
			}
			//桶信息
			Map<Character, BucketNode> bucketMsg = new TreeMap<Character, BucketNode>();
			
			//建立图形 桶
			int bHeight = (maxH+1)*ShapeSize.RadixSortModel.NODE_HEIGHT;//桶高
			int bWidth = ShapeSize.RadixSortModel.BUCKET_WIDTH;//桶宽
			int btLeft = ShapeSize.RadixSortModel.LEFT_MARGIN;
			int bTop = ShapeSize.RadixSortModel.TOP_MARGIN+ShapeSize.RadixSortModel.NODE_HEIGHT*4;//桶的顶部
			int btTop = bTop + bHeight + ShapeSize.RadixSortModel.TIP_AND_BUCKET_DIST;//桶标记顶部
			int bBottom = btTop-ShapeSize.RadixSortModel.TIP_AND_BUCKET_DIST;//桶的底部
			
			ArrayList<Shape> bucketShpapes = new ArrayList<Shape>();
			for(Character ch : cnt.keySet()){
				 bucketMsg.put(ch, new BucketNode(new Stack<DsRect>(), bBottom-ShapeSize.RadixSortModel.NODE_HEIGHT, btLeft));
				 
				 String bTip = ch == 0 ? "□" : String.valueOf(ch);
				 DsRect bucketTip = new DsRect(btLeft, btTop, ShapeSize.RadixSortModel.NODE_WIDTH, ShapeSize.RadixSortModel.NODE_WIDTH, bTip);
				 shapeList.add(bucketTip);
				 bucketShpapes.add(bucketTip);
				 int bLeft = btLeft - (ShapeSize.RadixSortModel.BUCKET_WIDTH-ShapeSize.RadixSortModel.NODE_WIDTH)/2;
				 int bRight = btLeft + ShapeSize.RadixSortModel.NODE_WIDTH + (ShapeSize.RadixSortModel.BUCKET_WIDTH-ShapeSize.RadixSortModel.NODE_WIDTH)/2;
				 DsLine lineLeft = new DsLine(bLeft, bBottom, bLeft, bTop, false);
				 lineLeft.color = Color.CYAN;
				 shapeList.add(lineLeft);
				 bucketShpapes.add(lineLeft);
				 DsLine lineMid = new DsLine(bLeft, bBottom, bRight, bBottom, false);
				 lineMid.color = Color.CYAN;
				 shapeList.add(lineMid);
				 bucketShpapes.add(lineMid);
				 DsLine lineRight = new DsLine(bRight, bBottom, bRight, bTop, false);
				 lineRight.color = Color.CYAN;
				 shapeList.add(lineRight);
				 bucketShpapes.add(lineRight);
				 
				 btLeft += ShapeSize.RadixSortModel.BUCKET_DIST;
			}
			
			//将数字分发到桶中
			for(int j=nodeList.size()-1; j >= 0; --j){
				String data = nodeList.get(j).content;
				Character ch;
				if(data.length()-i >= 0)
					ch = data.charAt(data.length()-i);
				else 
					ch = 0;
				
				DsRect movingRect = (DsRect) nodeList.get(j).shape.clone();
				DsRect kwRect = new DsRect(movingRect.lx, movingRect.ly-movingRect.lh, movingRect.lw, movingRect.lh, "KW");
				kwRect.fontSize = 20;
				DsRect kwMsgRect = new DsRect(movingRect.lx, movingRect.ly+movingRect.lh, movingRect.lw, movingRect.lh, ch == 0 ? "□" : String.valueOf(ch)); 
				kwMsgRect.fontSize = 20;
				synchronized (Shape.class) {
					shapeList.add(kwRect);
					shapeList.add(kwMsgRect);
				}
				tipForSearched(new DsRect[]{kwMsgRect, kwRect});
				synchronized (Shape.class) {
					shapeList.remove(kwRect);
					shapeList.remove(kwMsgRect);
				}
				
				movingRect.color = Color.GREEN;
				synchronized(Shape.class){shapeList.add(movingRect);}
				nodeList.get(j).shape.content = "";
				moveRect(movingRect, movingRect.ly+ShapeSize.RadixSortModel.NODE_HEIGHT*2, DIR_DOWN);
				if(movingRect.lx > bucketMsg.get(ch).bucketLeft)
					moveRect(movingRect, bucketMsg.get(ch).bucketLeft, DIR_LEFT);
				else 
					moveRect(movingRect, bucketMsg.get(ch).bucketLeft, DIR_RIGHT);
				moveRect(movingRect, bucketMsg.get(ch).bucketHeight, DIR_DOWN);
				
				bucketMsg.get(ch).bucketHeight -= ShapeSize.RadixSortModel.NODE_HEIGHT;
				bucketMsg.get(ch).bucket.add(movingRect);
			}
			
			//将数字从桶中取出来，并放入对应的位置
			int pos = 0;//nodeList的索引
			for(Character ch : cnt.keySet()){
				Stack<DsRect> bucket = bucketMsg.get(ch).bucket;
				while(!bucket.empty()){
					DsRect movingRect = bucket.pop();
					moveRect(movingRect, nodeList.get(pos).shape.ly+ShapeSize.RadixSortModel.NODE_HEIGHT*2, DIR_UP);
					if(movingRect.lx > nodeList.get(pos).shape.lx)
						moveRect(movingRect, nodeList.get(pos).shape.lx, DIR_LEFT);
					else
						moveRect(movingRect, nodeList.get(pos).shape.lx, DIR_RIGHT);
					moveRect(movingRect, nodeList.get(pos).shape.ly, DIR_UP);
					nodeList.get(pos).content = nodeList.get(pos).shape.content = movingRect.content;
					++pos;
					synchronized(Shape.class){shapeList.remove(movingRect);}
				}
			}
			shapeList.removeAll(bucketShpapes);
		}
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
	
	public static final int DIR_LEFT = 1;
	public static final int DIR_RIGHT = 2;
	public static final int DIR_UP = 3;
	public static final int DIR_DOWN = 4;
	
	private void delay(int time){
		try {
			TimeUnit.MILLISECONDS.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
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
	
	/**
	 * @param nodeList
	 * @param ld	桶的左区间
	 * @param rd	桶的右区间
	 * @param ki	第几个关键字
	 * @param msgRect 消息提示矩形
	 */
	private void msdRadixSort(ArrayList<SortNode> nodeList, int ld, int rd, int ki, DsRect msgRect){
		if(nodeList == null || ld >= rd || ld < nodeList.size() && ki > nodeList.get(ld).content.length()) return;
		ArrayList<DsRect> bucket = new ArrayList<DsRect>();
		for(int j=ld; j <= rd; ++j){
			DsRect rect = new DsRect(nodeList.get(j).shape.lx-ShapeSize.RadixSortModel.NODE_WIDTH, nodeList.get(j).shape.ly+ShapeSize.RadixSortModel.NODE_HEIGHT*4, ShapeSize.RadixSortModel.NODE_WIDTH, ShapeSize.RadixSortModel.NODE_HEIGHT, "");
			bucket.add(rect);
		}
		
		msgRect.content = "根据第"+ ki +"位KW排序";
		//cnt, 统计每个桶 最右边界的 索引位置
		Map<Character, Integer> cnt = new TreeMap<Character, Integer>();
		for(int j=ld; j <= rd; ++j){
			String data = nodeList.get(j).content;
			Character ch;
			ch = data.charAt(ki-1);
			if(ch == '□') ch = 0;
			if(!cnt.containsKey(ch))
				cnt.put(ch, 1);
			else
				cnt.put(ch, cnt.get(ch)+1);
		}
		
		//初始化桶视图
		for(DsRect rect : bucket){
			synchronized (Shape.class) {shapeList.add(rect);}
			moveRect(rect, rect.lx+ShapeSize.RadixSortModel.NODE_WIDTH, DIR_RIGHT);
		}
		
		
		ArrayList<DsSampleRect> bucketTip = new ArrayList<DsSampleRect>();
		Character preCh = null;
		boolean upOrDown = true;
		for(Character ch : cnt.keySet()){
			DsSampleRect rect = null;
			if(preCh == null){
				rect = new DsSampleRect(nodeList.get(ld).shape.lx, ShapeSize.RadixSortModel.TOP_MARGIN+ShapeSize.RadixSortModel.NODE_HEIGHT*3, cnt.get(ch)*ShapeSize.RadixSortModel.NODE_WIDTH, ShapeSize.RadixSortModel.NODE_HEIGHT, "kw:" + (ch == 0 ? "□" : String.valueOf(ch)));
				upOrDown = !upOrDown;
			}
			else {
				cnt.put(ch, cnt.get(ch)+cnt.get(preCh));
				rect = new DsSampleRect(nodeList.get(ld).shape.lx+cnt.get(preCh)*ShapeSize.RadixSortModel.NODE_WIDTH, 
						upOrDown ? ShapeSize.RadixSortModel.TOP_MARGIN+ShapeSize.RadixSortModel.NODE_HEIGHT*3 : ShapeSize.RadixSortModel.TOP_MARGIN+ShapeSize.RadixSortModel.NODE_HEIGHT*5, 
								(cnt.get(ch)-cnt.get(preCh))*ShapeSize.RadixSortModel.NODE_WIDTH, ShapeSize.RadixSortModel.NODE_HEIGHT, "kw:" + (ch == 0 ? "□" : String.valueOf(ch)));
				upOrDown = !upOrDown;
			}
			rect.color = Color.GREEN;
			rect.fontSize = 20;
			shapeList.add(rect);
			bucketTip.add(rect);
			preCh = ch;
		}
		Map<Character, Integer> copyCnt = new TreeMap<Character, Integer>(cnt);
		sampleRectAppear(bucketTip.toArray(new DsSampleRect[]{}));
		
		for(int j=rd; j >= ld; --j){
			String data = nodeList.get(j).content;
			DsRect movingRect = (DsRect) nodeList.get(j).shape.clone();
			nodeList.get(j).content = nodeList.get(j).shape.content = "";
			movingRect.color = Color.CYAN;
			synchronized (Shape.class) {shapeList.add(movingRect);}
			Character ch;
			ch = data.charAt(ki-1);
			if(ch == '□') ch = 0;
			moveRect(movingRect, movingRect.ly+ShapeSize.RadixSortModel.NODE_HEIGHT*2, DIR_DOWN);
			int num = cnt.get(ch);//桶的块数
			int pos = bucket.get(num-1).lx;
			if(movingRect.lx > pos)
				moveRect(movingRect, pos, DIR_LEFT);
			else
				moveRect(movingRect, pos, DIR_RIGHT);
			moveRect(movingRect, bucket.get(num-1).ly, DIR_DOWN);
			synchronized (Shape.class) {shapeList.remove(movingRect);}
			bucket.get(num-1).fontSize = movingRect.fontSize;
			bucket.get(num-1).content = movingRect.content;
			cnt.put(ch, num-1);
		}
		
		
		if(bucketTip.size() > 0)
			circleFly((DsSampleRect)bucketTip.get(0).clone(), bucketTip.toArray(new DsSampleRect[]{}));
		
		MyRunMoveRectsUp run = new MyRunMoveRectsUp(ShapeSize.RadixSortModel.TOP_MARGIN, bucket.size());
		new Thread(run).start();
		for(int j=bucket.size()-1; j >= 0; --j){
			DsRect rect = bucket.get(j);
			DsRect movingRect = (DsRect)rect.clone();
			movingRect.color = Color.GREEN;
			synchronized (Shape.class) {shapeList.add(movingRect);}
			synchronized (RadixSortModel.class) {run.list.add(movingRect);}
			rect.lx -= ShapeSize.RadixSortModel.NODE_WIDTH;
			if(j != 0) {
				model.setViewChanged();
				delay(200);
			}
			synchronized (Shape.class) {shapeList.remove(rect);}
		}
		
		while(run.isRunStop());//阻塞
		for(int j=ld, k=0; j <= rd; ++j, ++k) {
			nodeList.get(j).content = nodeList.get(j).shape.content = bucket.get(k).content;
			bucket.get(k).content = "";
		}
		synchronized (Shape.class) {shapeList.removeAll(run.list);}
		
		preCh = null;
		for(Character ch : copyCnt.keySet()){
			if(preCh == null)
				msdRadixSort(nodeList, ld, ld+copyCnt.get(ch)-1, ki+1, msgRect);
			else
				msdRadixSort(nodeList, ld+copyCnt.get(preCh), ld+copyCnt.get(ch)-1, ki+1, msgRect);
			preCh = ch;
		}
	}
	
	public void radixSort(String data, boolean oneOrTwo){
		String[] datas = data.split(",");
		ArrayList<SortNode> nodeList = new ArrayList<SortNode>();
		
		for(int i=0; i<datas.length; ++i){
			SortNode node = new SortNode(datas[i]);
			DsRect shape = new DsRect(ShapeSize.RadixSortModel.LEFT_MARGIN+i*ShapeSize.RadixSortModel.NODE_WIDTH, ShapeSize.RadixSortModel.TOP_MARGIN, ShapeSize.RadixSortModel.NODE_WIDTH, ShapeSize.RadixSortModel.NODE_HEIGHT, datas[i]);
			shape.fontSize = 20;
			node.shape = shape;
			shapeList.add(shape);
			nodeList.add(node);
		}
		
		//最低位优先法实现
		if(lsdOrMsd){
			if(oneOrTwo)
				lsdRadixSort1(nodeList);
			else
				lsdRadixSort2(nodeList);
		} else {
			int left = ShapeSize.RadixSortModel.LEFT_MARGIN+(nodeList.get(nodeList.size()-1).shape.lx+ShapeSize.RadixSortModel.NODE_WIDTH-nodeList.get(0).shape.lx - ShapeSize.RadixSortModel.NODE_WIDTH*4)/2;
			if(left < 0) left = ShapeSize.RadixSortModel.LEFT_MARGIN;
			int top = ShapeSize.RadixSortModel.TOP_MARGIN/3;
			DsRect msgRect = new DsRect(left, top, ShapeSize.RadixSortModel.NODE_WIDTH*4, ShapeSize.RadixSortModel.NODE_HEIGHT, "");
			msgRect.color = Color.YELLOW;
			msgRect.fontSize = 15;
			shapeList.add(msgRect);
			
			msgRect.content = "串等长变换:";
			tipForSearched(new DsRect[]{msgRect});
			tipForSearched(new DsRect[]{msgRect});
			msgRect.color = Color.YELLOW;
			
			int maxLen = -1, minLen = 0x3f3f3f3f;
			for(int i=0; i < nodeList.size(); ++i){
				if(nodeList.get(i).content.length() > maxLen)
					maxLen = nodeList.get(i).content.length();
				if(nodeList.get(i).content.length() < minLen)
					minLen = nodeList.get(i).content.length();
			}
			StringBuilder sb = new StringBuilder();
			for(int i=minLen; i<=maxLen; ++i)
				sb.append("□");
			for(int i=0; i < nodeList.size(); ++i)
				if(nodeList.get(i).content.length() < maxLen){
					String rep = nodeList.get(i).content.replaceAll("(.+)", sb + "$1");
					nodeList.get(i).content = nodeList.get(i).shape.content = rep.replaceAll("□*(.{" + maxLen + "})", "$1");
				}
			msdRadixSort(nodeList, 0, nodeList.size()-1, 1, msgRect);
			for(int i=0; i < nodeList.size(); ++i){
				nodeList.get(i).content = nodeList.get(i).shape.content = nodeList.get(i).content.replaceAll("□*(.+)", "$1");
			}
			msgRect.content = "串还原:";
			tipForSearched(new DsRect[]{msgRect});
			tipForSearched(new DsRect[]{msgRect});
			msgRect.color = Color.YELLOW;
		}
		model.setViewChanged();
	}
	
	public RadixSortModel(DrawModel model, Boolean lsdOrMsd) {
		super();
		this.model = model;
		this.lsdOrMsd = lsdOrMsd;
		shapeList = model.getShapeList();
	}
}
