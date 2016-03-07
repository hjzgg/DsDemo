package com.test;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;

public class TabComponentsDemo extends JFrame{
	 
    private JTabbedPane pane = new JTabbedPane();
    private JMenuItem scrollTabItem ;
    private JMenuItem componentTabItem;
    private final int numTab = 5;
      
    public TabComponentsDemo(String title) {
       //����frame������
       super(title);
       //���ùرշ�ʽ
       setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       //�����˵���
       initMenu();
       //��tabpane��ӵ�frame��
       add(pane);
    }
   
    //��ʼ��tabpanel������ã���������resize�ĵ���
    public  void runTest() {
       //�����йرհ����ı�ǩ
       componentTabItem.setSelected(true);
       //���ñ�ǩ
       for(int i = 0 ; i < numTab ; i++)
       {
           //��ǩ��
           String name = "tab "+i;
           //������ǩ
           pane.add(name,createContent(name));
           //��ʼ����ǩ�ϵ����ֺ�Button
           pane.setTabComponentAt(i, new ButtonTabComponent(pane));
       }
       //����frame�Ĵ�С
       setSize(new Dimension(500,500));
       //��frame�ŵ���Ļ��������
       setLocationRelativeTo(null);
       //��ʾframe
       setVisible(true);
    }
   
    //������ǩ���ݲ���
    private Component createContent(String name) {
       //����һ��panel�������ò���Ϊһ���ֿ�
       JPanel panel = new JPanel(new GridLayout(1,1));
       //����һ��label
       JLabel label = new JLabel(name);
       //����label���ı���ʽ
       label.setHorizontalAlignment(JLabel.CENTER);
       //��label����panel��
       panel.add(label);
       pane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
       return panel;
    }
 
    //�����˵���
    private void initMenu() {
       //����һ���˵���
       JMenuBar mb = new JMenuBar();
       //�����ص�tabѡ��
       scrollTabItem = new JCheckBoxMenuItem("�ص�tab");
       //���ÿ�ݼ�
       scrollTabItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,InputEvent.CTRL_MASK));
       //���ü����¼�
       scrollTabItem.addActionListener(new ActionListener() {
          
           @Override
           public void actionPerformed(ActionEvent arg0) {
              if(pane.getTabLayoutPolicy() == JTabbedPane.SCROLL_TAB_LAYOUT)
                  pane.setTabLayoutPolicy(JTabbedPane.WRAP_TAB_LAYOUT);
              else
                  pane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
           }
       });
      
       //���ÿɹرյı�ǩ�Ĳ˵�
       componentTabItem = new JCheckBoxMenuItem("���ÿɹرյ�tab");
       componentTabItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,InputEvent.CTRL_MASK));
       componentTabItem.addActionListener(new ActionListener() {
          
           @Override
           public void actionPerformed(ActionEvent e) {
              for(int i = 0 ; i < numTab ; i++)
              {
                  if(componentTabItem.isSelected())
                	  pane.setTabComponentAt(i, new ButtonTabComponent(pane));
                  else
                     pane.setTabComponentAt(i, null);
              }
           }
       });
      
       //�������ñ�ǩ
       JMenuItem reSetItem = new JMenuItem("����");
       reSetItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R,InputEvent.CTRL_MASK));
       reSetItem.addActionListener(new ActionListener() {
          
           @Override
           public void actionPerformed(ActionEvent e) {
              runTest();
           }
       });
      
       //�����˵�
       JMenu menu = new JMenu("ѡ��");
       //��Ӳ˵���
       menu.add(componentTabItem);
       menu.add(scrollTabItem);
       menu.add(reSetItem);
      
       //��Ӳ˵�
       mb.add(menu);
       //��Ӳ˵�����ע��һ��frameֻ����һ���˵�����������set��
       setJMenuBar(mb);
    }
 
    public static void main(String[] args) {
          new TabComponentsDemo("�ɹرյ�tab����").runTest();
    }
 
}