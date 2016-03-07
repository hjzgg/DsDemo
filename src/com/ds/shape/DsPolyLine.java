package com.ds.shape;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;

//һϵ�е����ߣ� �����м�ͷ
public class DsPolyLine implements Shape{
	public int[] lx=null, ly=null;
	private boolean isArrow;
	public DsPolyLine(boolean isArrow){
		this.isArrow = isArrow;
	}
	@Override
	public void drawShape(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setPaint(Color.black);
		g2.setStroke(new BasicStroke(2));
		if(lx!=null && ly!=null){
			if(lx.length != ly.length)
				throw new IllegalArgumentException("x�����y����ĳ��Ȳ�һ��!");
			for(int i=1; i<lx.length-1; ++i)
				g2.drawLine(lx[i-1], ly[i-1], lx[i], ly[i]);
			
			if(isArrow)
				drawAL(lx[lx.length-2], ly[ly.length-2], lx[lx.length-1], ly[ly.length-1], g2);
			else
				g2.drawLine(lx[lx.length-2], ly[ly.length-2], lx[lx.length-1], ly[ly.length-1]);
		}
		g2.setStroke(new BasicStroke(1));
	}
	
	private void drawAL(int sx, int sy, int ex, int ey, Graphics2D g2){

		double H = 10; // ��ͷ�߶�
		double L = 6; // �ױߵ�һ��
		int x3 = 0;
		int y3 = 0;
		int x4 = 0;
		int y4 = 0;
		double awrad = Math.atan(L / H); // ��ͷ�Ƕ�
		double arraow_len = Math.sqrt(L * L + H * H); // ��ͷ�ĳ���
		double[] arrXY_1 = rotateVec(ex - sx, ey - sy, awrad, true, arraow_len);
		double[] arrXY_2 = rotateVec(ex - sx, ey - sy, -awrad, true, arraow_len);
		double x_3 = ex - arrXY_1[0]; // (x3,y3)�ǵ�һ�˵�
		double y_3 = ey - arrXY_1[1];
		double x_4 = ex - arrXY_2[0]; // (x4,y4)�ǵڶ��˵�
		double y_4 = ey - arrXY_2[1];

		Double X3 = new Double(x_3);
		x3 = X3.intValue();
		Double Y3 = new Double(y_3);
		y3 = Y3.intValue();
		Double X4 = new Double(x_4);
		x4 = X4.intValue();
		Double Y4 = new Double(y_4);
		y4 = Y4.intValue();
		// ����
		g2.drawLine(sx, sy, ex, ey);
		//
		GeneralPath triangle = new GeneralPath();
		triangle.moveTo(ex, ey);
		triangle.lineTo(x3, y3);
		triangle.lineTo(x4, y4);
		triangle.closePath();
		//ʵ�ļ�ͷ
		g2.fill(triangle);
		//��ʵ�ļ�ͷ
		//g2.draw(triangle);
	}

	// ����
	private double[] rotateVec(int px, int py, double ang,
			boolean isChLen, double newLen) {

		double mathstr[] = new double[2];
		// ʸ����ת��������������ֱ���x������y��������ת�ǡ��Ƿ�ı䳤�ȡ��³���
		double vx = px * Math.cos(ang) - py * Math.sin(ang);
		double vy = px * Math.sin(ang) + py * Math.cos(ang);
		if (isChLen) {
			double d = Math.sqrt(vx * vx + vy * vy);
			vx = vx / d * newLen;
			vy = vy / d * newLen;
			mathstr[0] = vx;
			mathstr[1] = vy;
		}
		return mathstr;
	}
}
