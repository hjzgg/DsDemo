package com.test;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Zdbx {
    public static void main(String[] args) {
        final JFrame f = new JFrame();
        f.setSize(500, 500);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        final MyPanel panel = new MyPanel();
        f.add(panel);
        final JTextField text = new JTextField("3");
        f.add(text, BorderLayout.NORTH);
        text.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                try {
                int n = Integer.parseInt(text.getText());
                if(n<3) return;
                panel.draw(n);
                } catch(NumberFormatException ex) {}
            }
        });
        f.setVisible(true);
    }
}

class MyPanel extends JPanel {
    int n=3;

    public void draw(int n) {
        this.n = n;
        repaint();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        List<Point> ps = getPoints(n);
        for(int i=0; i<n-1; i++) {
            g.drawLine(ps.get(i).x, ps.get(i).y,
                    ps.get(i+1).x, ps.get(i+1).y);
        }
        g.drawLine(ps.get(n-1).x, ps.get(n-1).y,
                ps.get(0).x, ps.get(0).y);
    }

    List<Point> getPoints(int n) {
        ArrayList<Point> ps = new ArrayList<Point>();
        int ox = getWidth()/2;
        int oy = getHeight()/2;
        double r = (ox>oy ? oy : ox)*0.9;
        double angle = 2*Math.PI/n;
        double startAngle = (Math.PI-angle)/2;
        for(int i=0; i<n; i++) {
            int x = (int)(ox+r*Math.cos(startAngle+i*angle));
            int y = (int)(oy+r*Math.sin(startAngle+i*angle));
            ps.add(new Point(x,y));
        }
        return ps;
    }
}

