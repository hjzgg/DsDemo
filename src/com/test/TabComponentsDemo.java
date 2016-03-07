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
       //设置frame标题名
       super(title);
       //设置关闭方式
       setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       //创建菜单栏
       initMenu();
       //将tabpane添加到frame中
       add(pane);
    }
   
    //初始化tabpanel相关配置，并且用于resize的调用
    public  void runTest() {
       //设置有关闭按键的标签
       componentTabItem.setSelected(true);
       //设置标签
       for(int i = 0 ; i < numTab ; i++)
       {
           //标签名
           String name = "tab "+i;
           //创建标签
           pane.add(name,createContent(name));
           //初始化标签上的文字和Button
           pane.setTabComponentAt(i, new ButtonTabComponent(pane));
       }
       //设置frame的大小
       setSize(new Dimension(500,500));
       //将frame放到屏幕的正中央
       setLocationRelativeTo(null);
       //显示frame
       setVisible(true);
    }
   
    //创建标签内容部分
    private Component createContent(String name) {
       //创建一个panel，并设置布局为一个分块
       JPanel panel = new JPanel(new GridLayout(1,1));
       //创建一个label
       JLabel label = new JLabel(name);
       //设置label的文本格式
       label.setHorizontalAlignment(JLabel.CENTER);
       //将label放入panel中
       panel.add(label);
       pane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
       return panel;
    }
 
    //创建菜单栏
    private void initMenu() {
       //创建一个菜单条
       JMenuBar mb = new JMenuBar();
       //创建重叠tab选项
       scrollTabItem = new JCheckBoxMenuItem("重叠tab");
       //设置快捷键
       scrollTabItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,InputEvent.CTRL_MASK));
       //设置监听事件
       scrollTabItem.addActionListener(new ActionListener() {
          
           @Override
           public void actionPerformed(ActionEvent arg0) {
              if(pane.getTabLayoutPolicy() == JTabbedPane.SCROLL_TAB_LAYOUT)
                  pane.setTabLayoutPolicy(JTabbedPane.WRAP_TAB_LAYOUT);
              else
                  pane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
           }
       });
      
       //设置可关闭的标签的菜单
       componentTabItem = new JCheckBoxMenuItem("设置可关闭的tab");
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
      
       //设置重置标签
       JMenuItem reSetItem = new JMenuItem("重置");
       reSetItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R,InputEvent.CTRL_MASK));
       reSetItem.addActionListener(new ActionListener() {
          
           @Override
           public void actionPerformed(ActionEvent e) {
              runTest();
           }
       });
      
       //创建菜单
       JMenu menu = new JMenu("选项");
       //添加菜单项
       menu.add(componentTabItem);
       menu.add(scrollTabItem);
       menu.add(reSetItem);
      
       //添加菜单
       mb.add(menu);
       //添加菜单条（注意一个frame只能有一个菜单条，所以用set）
       setJMenuBar(mb);
    }
 
    public static void main(String[] args) {
          new TabComponentsDemo("可关闭的tab测试").runTest();
    }
 
}