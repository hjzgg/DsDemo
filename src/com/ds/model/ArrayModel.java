package com.ds.model;

import java.awt.Color;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import com.ds.shape.DsLine;
import com.ds.shape.DsRect;
import com.ds.shape.Shape;
import com.ds.size.ShapeSize;

/**
 * @author hjzgg
 * 生成数组模型
 */
public class ArrayModel{
	private ArrayList<ArrayNode> nodeList = null;
	private DrawModel model;
	//创建过程的显示
	public void showArrayCreate(String data){
		ArrayList<Shape> shapeList = model.getShapeList();
		String[] contents = data.split(",");
		int left = 0, top =  ShapeSize.ArrayModel.TOP_MARGIN-ShapeSize.ArrayModel.ROW_HEIGHT;
		DsRect rect = null;
		for(int i=0; i<contents.length; ++i){
			synchronized (Shape.class) {
				if(i % ShapeSize.ArrayModel.ROW_NUM == 0){//新的一行的第一个节点
					int preTop = top;//上一行
					top += ShapeSize.ArrayModel.ROW_HEIGHT;//下一行
					if(i != 0){//添加行与行之间的折线
						int preRowMidTop = (preTop*2+ShapeSize.ArrayModel.RECT_HEIGHT)/2;
						int rowAndRowMidTop = (preTop+ShapeSize.ArrayModel.RECT_HEIGHT+top)/2;
						DsLine lineCol1 = new DsLine(left, preRowMidTop, left, rowAndRowMidTop, false);
						shapeList.add(lineCol1);
						DsLine lineRow1 = new DsLine(left, rowAndRowMidTop, ShapeSize.ArrayModel.LEFT_MARGIN, rowAndRowMidTop, false);
						shapeList.add(lineRow1);
						int nextRowMidTop = (top*2+ShapeSize.ArrayModel.RECT_HEIGHT)/2;
						DsLine lineCol2 = new DsLine(ShapeSize.ArrayModel.LEFT_MARGIN, rowAndRowMidTop, ShapeSize.ArrayModel.LEFT_MARGIN, nextRowMidTop, false);
						shapeList.add(lineCol2);
						DsLine lineRow2 = new DsLine(ShapeSize.ArrayModel.LEFT_MARGIN, nextRowMidTop, ShapeSize.ArrayModel.LEFT_MARGIN+ShapeSize.ArrayModel.LINE_DIST_X, nextRowMidTop, false);
						shapeList.add(lineRow2);
					}
					left = ShapeSize.ArrayModel.LEFT_MARGIN+ShapeSize.ArrayModel.LINE_DIST_X;
				} 
				rect = new DsRect(left, top, ShapeSize.ArrayModel.RECT_WIDTH, ShapeSize.ArrayModel.RECT_HEIGHT, contents[i]);
				shapeList.add(rect);
				
				if(i != contents.length-1){//每一个rect的后面都加上一条横线
					left += ShapeSize.ArrayModel.RECT_WIDTH;
					DsLine line = new DsLine(left, (ShapeSize.ArrayModel.RECT_HEIGHT+top*2)/2, left+=ShapeSize.ArrayModel.LINE_DIST_X, (ShapeSize.ArrayModel.RECT_HEIGHT+top*2)/2, false);
					shapeList.add(line);
				}
			}
			
			boolean flag = true;
			for(int k=1; k<=4; ++k){
				model.setViewChanged();
				rect.color = flag ? Color.GREEN : null;
				flag = !flag;
				delay(500);
			}
		}
	}
	
	public void showArrayInsert(String content, int pos){
		if(pos<0 || pos>=nodeList.size())
			throw new IllegalArgumentException("数组插入位置不合法!");
		ArrayList<Shape> shapeList = model.getShapeList();
		//找到位置
		DsRect posRect = nodeList.get(pos-1).shape;
		DsRect beginRect = nodeList.get(0).shape;
		DsRect rect = new DsRect(beginRect.lx-beginRect.lw, beginRect.ly-beginRect.lh, beginRect.lw, beginRect.lh, content);
		shapeList.add(rect);
		while(rect.ly+rect.lh <= posRect.ly){
			model.setViewChanged();
			delay(100);
			if(rect.ly+rect.lh == posRect.ly) break;
			rect.ly += 5;
		}
		
		while(rect.lx <= posRect.lx){
			model.setViewChanged();
			delay(100);
			if(rect.lx == posRect.lx) break;
			rect.lx += 5;
		}
		
		DsRect tipRect = new DsRect(posRect.lx, posRect.ly+posRect.lh, posRect.lw, posRect.lh, "^");
		shapeList.add(tipRect);
		boolean flag = true;
		for(int k=1; k<=4; ++k){
			model.setViewChanged();
			tipRect.color = flag ? Color.GREEN : null;
			flag = !flag;
			delay(500);
		}
		shapeList.remove(tipRect);
		model.setViewChanged();
		
		//数组移动
		for(int i=nodeList.size()-2; i >= pos-1; --i){
			DsRect curRect = nodeList.get(i).shape;
			DsRect moveRect = (DsRect) curRect.clone();
			moveRect.color = Color.GREEN;
			shapeList.add(moveRect);
			DsRect nextRect = nodeList.get(i+1).shape;
			while(moveRect.lx <= nextRect.lx){
				model.setViewChanged();
				delay(100);
				if(moveRect.lx == nextRect.lx) break;
				moveRect.lx += 5;
			}
			
			ArrayList<DsLine> lineList = nodeList.get(i).lineList;
			if(lineList.size() == 1){
				DsLine line = lineList.get(0);
				while(moveRect.lx <= line.x2){
					model.setViewChanged();
					delay(100);
					if(moveRect.lx == line.x2) break;
					moveRect.lx += 5;
				}
			} else {
				DsLine line = lineList.get(0);
				while(moveRect.lx <= line.x2-moveRect.lw/2){
					model.setViewChanged();
					delay(100);
					moveRect.lx += 5;
					if(moveRect.lx > line.x2-moveRect.lw/2) break;
				}
				line = lineList.get(1);
				while(moveRect.ly <= line.y2-moveRect.lh/2){
					model.setViewChanged();
					delay(50);
					moveRect.ly += 5;
					if(moveRect.ly > line.y2-moveRect.lh/2) break;
				}
				
				line = lineList.get(2);
				while(moveRect.lx >= line.x2-moveRect.lw/2){
					model.setViewChanged();
					delay(50);
					moveRect.lx -= 5;
					if(moveRect.lx < line.x2-moveRect.lw/2) break;
				}
				
				while(moveRect.ly <= nextRect.ly){
					model.setViewChanged();
					delay(50);
					moveRect.ly += 5;
					if(moveRect.ly > nextRect.ly) break;
				}
				moveRect.ly = nextRect.ly;
				
				while(moveRect.lx <= nextRect.lx){
					model.setViewChanged();
					delay(50);
					moveRect.lx += 5;
					if(moveRect.lx > nextRect.lx) break;
				}
				moveRect.lx = nextRect.lx;
				model.setViewChanged();
			}
			nodeList.get(i+1).content = nodeList.get(i).content;
			nextRect.content = moveRect.content;
			curRect.content = "?";
			shapeList.remove(moveRect);
			model.setViewChanged();
		}
		rect.color = Color.GREEN;
		while(rect.ly <= posRect.ly){
			model.setViewChanged();
			delay(100);
			if(rect.ly == posRect.ly) break;
			rect.ly += 5;
		}
		shapeList.remove(rect);
		posRect.content = rect.content;
		nodeList.get(pos-1).content = posRect.content;
		model.setViewChanged();
	}
	
	public void showArrayDelete(int pos){
		if(pos<1 || pos>nodeList.size())
			throw new IllegalArgumentException("数组删除位置不合法!");
		ArrayList<Shape> shapeList = model.getShapeList();
		//找到位置
		DsRect posRect = nodeList.get(pos-1).shape;
		DsRect beginRect = nodeList.get(0).shape;
		DsRect tipRect = new DsRect(beginRect.lx-beginRect.lw, beginRect.ly-beginRect.lh, beginRect.lw, beginRect.lh, "↓");
		shapeList.add(tipRect);
		while(tipRect.ly+tipRect.lh <= posRect.ly){
			model.setViewChanged();
			delay(100);
			if(tipRect.ly+tipRect.lh == posRect.ly) break;
			tipRect.ly += 5;
		}
		
		while(tipRect.lx <= posRect.lx){
			model.setViewChanged();
			delay(100);
			if(tipRect.lx == posRect.lx) break;
			tipRect.lx += 5;
		}
		
		DsRect deleteRect = new DsRect(posRect.lx, posRect.ly+posRect.lh, posRect.lw, posRect.lh, "×");
		shapeList.add(deleteRect);
		boolean flag = true;
		for(int k=1; k<=4; ++k){
			model.setViewChanged();
			tipRect.color = flag ? Color.GREEN : null;
			deleteRect.color = flag ? Color.GREEN : null;
			flag = !flag;
			delay(500);
		}
		shapeList.remove(tipRect);
		shapeList.remove(deleteRect);
		model.setViewChanged();
		
		for(int i=pos; i<nodeList.size(); ++i){
			DsRect curRect = nodeList.get(i).shape;
			DsRect moveRect = (DsRect) curRect.clone();
			moveRect.color = Color.GREEN;
			shapeList.add(moveRect);
			DsRect nextRect = nodeList.get(i-1).shape;
			
			ArrayList<DsLine> lineList = nodeList.get(i-1).lineList;
			if(lineList.size() > 1){
				DsLine line = lineList.get(4);
				while(moveRect.lx >= line.x1-moveRect.lw/2){
					model.setViewChanged();
					delay(50);
					moveRect.lx -= 5;
					if(moveRect.lx < line.x1-moveRect.lw/2) break;
				}
				line = lineList.get(3);
				while(moveRect.ly >= line.y1-moveRect.lh/2){
					model.setViewChanged();
					delay(50);
					moveRect.ly -= 5;
					if(moveRect.ly < line.y1-moveRect.lh/2) break;
				}
				
				line = lineList.get(2);
				while(moveRect.lx <= line.x1-moveRect.lw/2){
					model.setViewChanged();
					delay(50);
					moveRect.lx += 5;
					if(moveRect.lx > line.x1-moveRect.lw/2) break;
				}
				
				while(moveRect.ly >= nextRect.ly){
					model.setViewChanged();
					delay(50);
					moveRect.ly -= 5;
					if(moveRect.ly < nextRect.ly) break;
				}
				moveRect.ly = nextRect.ly;
				model.setViewChanged();
			}
			
			while(moveRect.lx >= nextRect.lx){
				model.setViewChanged();
				delay(100);
				if(moveRect.lx == nextRect.lx) break;
				moveRect.lx -= 5;
			}
			
			nodeList.get(i-1).content = nodeList.get(i).content;
			nextRect.content = moveRect.content;
			curRect.content = "";
			shapeList.remove(moveRect);
			model.setViewChanged();
		}
		DsRect rect = nodeList.get(nodeList.size()-1).shape;
		flag = true;
		for(int k=1; k<=4; ++k){
			model.setViewChanged();
			rect.color = flag ? Color.GREEN : null;
			flag = !flag;
			delay(200);
		}
		shapeList.remove(rect);
		if(nodeList.size() > 1)
			shapeList.removeAll(nodeList.get(nodeList.size()-2).lineList);
		model.setViewChanged();
		nodeList.remove(nodeList.size()-1);
	}
	
	//直接显示
	public void createArrayData(String data, boolean isInsert){
		ArrayList<Shape> shapeList = model.getShapeList();
		nodeList = new ArrayList<ArrayNode>();
		if(isInsert) data += ",?";
		String[] contents = data.split(",");
		int left = 0, top =  ShapeSize.ArrayModel.TOP_MARGIN-ShapeSize.ArrayModel.ROW_HEIGHT;
		for(int i=0; i<contents.length; ++i){
			ArrayNode newNode = new ArrayNode(contents[i]);
			if(i % ShapeSize.ArrayModel.ROW_NUM == 0){//新的一行的第一个节点
				int preTop = top;//上一行
				top += ShapeSize.ArrayModel.ROW_HEIGHT;//下一行
				if(i != 0){//添加行与行之间的折线
					int preRowMidTop = (preTop*2+ShapeSize.ArrayModel.RECT_HEIGHT)/2;
					int rowAndRowMidTop = (preTop+ShapeSize.ArrayModel.RECT_HEIGHT+top)/2;
					DsLine lineCol1 = new DsLine(left, preRowMidTop, left, rowAndRowMidTop, false);
					shapeList.add(lineCol1);
					nodeList.get(nodeList.size()-1).lineList.add(lineCol1);
					DsLine lineRow1 = new DsLine(left, rowAndRowMidTop, ShapeSize.ArrayModel.LEFT_MARGIN, rowAndRowMidTop, false);
					shapeList.add(lineRow1);
					nodeList.get(nodeList.size()-1).lineList.add(lineRow1);
					int nextRowMidTop = (top*2+ShapeSize.ArrayModel.RECT_HEIGHT)/2;
					DsLine lineCol2 = new DsLine(ShapeSize.ArrayModel.LEFT_MARGIN, rowAndRowMidTop, ShapeSize.ArrayModel.LEFT_MARGIN, nextRowMidTop, false);
					shapeList.add(lineCol2);
					nodeList.get(nodeList.size()-1).lineList.add(lineCol2);
					DsLine lineRow2 = new DsLine(ShapeSize.ArrayModel.LEFT_MARGIN, nextRowMidTop, ShapeSize.ArrayModel.LEFT_MARGIN+ShapeSize.ArrayModel.LINE_DIST_X, nextRowMidTop, false);
					shapeList.add(lineRow2);
					nodeList.get(nodeList.size()-1).lineList.add(lineRow2);
				}
				left = ShapeSize.ArrayModel.LEFT_MARGIN+ShapeSize.ArrayModel.LINE_DIST_X;
			} 
			DsRect rect = new DsRect(left, top, ShapeSize.ArrayModel.RECT_WIDTH, ShapeSize.ArrayModel.RECT_HEIGHT, contents[i]);
			shapeList.add(rect);
			newNode.shape = rect;
			
			if(i != contents.length-1){//每一个rect的后面都加上一条横线
				left += ShapeSize.ArrayModel.RECT_WIDTH;
				DsLine line = new DsLine(left, (ShapeSize.ArrayModel.RECT_HEIGHT+top*2)/2, left+=ShapeSize.ArrayModel.LINE_DIST_X, (ShapeSize.ArrayModel.RECT_HEIGHT+top*2)/2, false);
				shapeList.add(line);
				newNode.lineList.add(line);
			}
			nodeList.add(newNode);
		}
	}
	
	private void delay(int time){
		try {
			TimeUnit.MILLISECONDS.sleep(time);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public ArrayModel(DrawModel model) {
		super();
		this.model = model;
	}
}
