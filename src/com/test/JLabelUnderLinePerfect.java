package com.test;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.border.LineBorder;

public class JLabelUnderLinePerfect extends JLabel {  
	  
    private Color underLineColor;  
  
    public JLabelUnderLinePerfect() {  
        this("");  
    }  
  
    public JLabelUnderLinePerfect(String text) {  
        super(text);  
    }  
  
    /** 
     * @return the underLineColor 
     */  
    public Color getUnderLineColor() {  
        return underLineColor;  
    }  
  
    /** 
     * @param pUnderLineColor the underLineColor to set 
     */  
    public void setUnderLineColor(Color pUnderLineColor) {  
        underLineColor = pUnderLineColor;  
    }  
  
    public void paint(Graphics g) {  
        super.paint(g);  
  
        Rectangle r = g.getClipBounds();  
        int xoffset = 0, yoffset = 0, pointX = 0, pointY = 0, point2X = 0, point2Y = 0;  
  
        // 根据border的设置 计算出下划线的起止Point  
        if (null != this.getBorder()  
                && null != this.getBorder().getBorderInsets(this)) {  
            Insets inserts = this.getBorder().getBorderInsets(this);  
            // System.out.println(inserts);  
            xoffset = inserts.left;  
            yoffset = inserts.bottom;  
        }  
        pointX = xoffset;  
        pointY = point2Y = r.height - yoffset  
                - getFontMetrics(getFont()).getDescent();  
        point2X = pointX + getFontMetrics(getFont()).stringWidth(getText());  
        if (null != underLineColor) {  
            g.setColor(underLineColor);  
        }  
  
        g.drawLine(pointX, pointY, point2X, point2Y);  
    }  
  
    /** 
     * @param args 
     */  
    public static void main(String[] args) {  
        JFrame f = new JFrame("JLabe with Under Line");  
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
        f.setSize(new Dimension(300, 200));  
        f.setLayout(new FlowLayout());  
  
        JLabelUnderLinePerfect label1 = new JLabelUnderLinePerfect("Label下划线");  
        f.add(label1);  
  
        JLabelUnderLinePerfect label2 = new JLabelUnderLinePerfect("Label下划线");  
        label2.setUnderLineColor(Color.BLUE);  
        f.add(label2);  
  
        JLabelUnderLinePerfect label3 = new JLabelUnderLinePerfect(  
                "Label(Border)下划线");  
        label3.setBorder(BorderFactory.createCompoundBorder(new LineBorder(  
                Color.RED), BorderFactory.createEmptyBorder(8, 18, 28, 38)));  
        label3.setUnderLineColor(Color.BLUE);  
        f.add(label3);  
  
        // f.pack();  
        f.setVisible(true);  
  
    }  
}  