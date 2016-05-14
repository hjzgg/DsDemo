package com.ds.underlineLabel;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;

import javax.swing.JLabel;

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
}  
