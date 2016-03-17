package com.ds.shape;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.GeneralPath;

import com.ds.size.ShapeSize;
import com.sun.xml.internal.messaging.saaj.packaging.mime.util.LineInputStream;

//�޼�ͷ�����м�ͷ����
public class DsLine implements Shape, Comparable<DsLine>, Cloneable{
	public int x1, y1, x2, y2;
	public String weight;
	public int LineSegments = 1;//�߶ι��ֳɼ�����
	public int weightInLinePos = 0;//weightֵ���߶��ϵ�λ��
	public boolean isArrow;
	public Point ptOrg = null;
	public Color color = null;
	public DsLine(int x1, int y1, int x2, int y2, boolean isArrow) {
		super();
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		this.isArrow = isArrow;
		weight = null;
	}
	
	public void setWeightAtLineEnd(Point ptOrg){
		weightInLinePos = 1;
		LineSegments = 1;
		this.ptOrg = ptOrg;
	}
	
	public void setDefaultLine(Point ptOrg){
		weightInLinePos = 1;
		LineSegments = 2;
		this.ptOrg = ptOrg;
	}
	
	@Override
	public void drawShape(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setPaint(Color.black);
		if(color != null) g2.setPaint(color);
		g2.setStroke(new BasicStroke(2));
		if(isArrow)
			drawAL(x1, y1, x2, y2, g2);
		else
			g2.drawLine(x1, y1, x2, y2);
		//���ߵ�Ȩֵ
		if(weight != null){
			g2.setFont(new Font("�����п�", Font.BOLD, 18));
			FontMetrics fontMetrics = g2.getFontMetrics();
			int wordH = fontMetrics.getLeading()-fontMetrics.getAscent();
			int wordW = fontMetrics.stringWidth(weight);
			g2.setPaint(Color.BLUE);
			double k = (1.0*y2-y1)/(x2-x1);//ֱ��ƽ�Ʒ����б��
			double angle = Math.atan(k);//��������Ƕ�
			double sinx = (2*Math.tan(angle/2)/(1+Math.tan(angle/2)*Math.tan(angle/2)));
			double cosx = ((1-Math.tan(angle/2)*Math.tan(angle/2)))/((1+Math.tan(angle/2)*Math.tan(angle/2)));
			double L = Math.sqrt((double)((y2-y1)*(y2-y1) + (x2-x1)*(x2-x1)))*weightInLinePos/LineSegments;
			
			int x = (int)(ptOrg.x+L*cosx);
			if((x-x1)*(x-x2) > 0)
				x = (int)(ptOrg.x-L*cosx);
			
			int y = (int)(ptOrg.y+L*sinx); 
			if((y-y1)*(y-y2) > 0)
				y = (int)(ptOrg.y-L*sinx);
			g2.drawString(weight, x-wordW/2, y-wordH/2);
		}
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

	@Override
	public String toString() {
		 return x1 + "," + y1 + " " + x2 + "," + y2;
	}

	@Override
	public int compareTo(DsLine line2) {
		if((x1==line2.x1 && x2==line2.x2 && y1==line2.y1 && y2==line2.y2) || 
				(x1==line2.x2 && x2==line2.x1 && y1==line2.y2 && y2==line2.y1))
			return 0;
		else
			return ((double)y2-y1)/(x2-x1) > ((double)line2.y2-line2.y1)/(line2.x2-line2.x1) ? 1 : -1;
	}

	@Override
	public Object clone() {
		//��Ϊû���漰�������������֮������ݣ��������һ��ǳ��¡�ͺ���
		Object o = null;
		try {
			o = super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return o;
	}
	
}

