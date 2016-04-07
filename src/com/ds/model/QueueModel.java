package com.ds.model;

import java.util.ArrayList;

import com.ds.shape.Shape;

public class QueueModel {
	private DrawModel model;
	ArrayList<Shape> shapeList;
	
	public void showQueue(String data){
		
	}
	
	public QueueModel(DrawModel model) {
		super();
		this.model = model;
		this.shapeList = model.getShapeList();
	}
	
	
}
