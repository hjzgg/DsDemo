package com.test;


//test git_bash(ʹ�������е���ʽ����commit)
import java.awt.*;
import javax.swing.*;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.*;

public class ArrowLinePanel extends JPanel implements MouseMotionListener,MouseListener{
	// confirm the line position
	private int x1 = 0;
	private int y1 = 0;
	private int x2 = 0;
	private int y2 = 0;
  
	public ArrowLinePanel() {
		setBackground(Color.white);
		addMouseListener(this);
		addMouseMotionListener(this);
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setPaint(Color.black);
		drawAL(x1, y1, x2, y2, g2);// ����x1, y1, x2, y2����Ҫ�������ҳ�ʼ����������������λ�úͳ�ʼ����ֵ
	}

	public static void drawAL(int sx, int sy, int ex, int ey, Graphics2D g2)
	{

		double H = 10; // ��ͷ�߶�
		double L = 4; // �ױߵ�һ��
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
	public static double[] rotateVec(int px, int py, double ang,
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
	public void mouseDragged(MouseEvent e) {
                 //�ı��ͷ������
		x2 = e.getX();
		y2 = e.getY();
		repaint();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
                //���ü�ͷ����ʼ����
		x1 = e.getX();
		y1 = e.getY();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		
	}

}
