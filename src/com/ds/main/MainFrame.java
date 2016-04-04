package com.ds.main;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.UIManager;

import com.ds.panel.EditCodeAndBrowse;
import com.ds.panel.FunctionPanel;
import com.ds.tools.MyTimer;


public class MainFrame extends JFrame{
	private JTabbedPane pane = new JTabbedPane();
    private final String[] tabText = {"模拟系统", "练习系统", "用户交流"};
    private final JPanel[] contentPanels = new JPanel[tabText.length];
    private final JPanel[] tabComponents = new JPanel[tabText.length];
    private final JMenuItem[] functionItems = new JCheckBoxMenuItem[tabText.length];
    private final JMenuItem componentTabItem = new JCheckBoxMenuItem("设置为可关闭的tab");
    public MainFrame(String title) {
       //设置frame标题名
       super(title);
       //设置关闭方式
       addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				//终止所有计时器中的任务
				MyTimer.getTimer().cancel();
				MainFrame.this.dispose();
				System.exit(0);
			}
	   });
       //创建菜单栏
       initMenu();
       //设置可重叠的Tab
       pane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
       //将tabpane添加到frame中
       add(pane);
       //设置界面外观的样式
       try {
    	   UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
       } catch (Exception e) {
		   e.printStackTrace();
       }
       //初始化界面
       createMainFrame();
    }
   
    //初始化tabpanel相关配置，并且用于resize的调用
    private void createMainFrame() {
       //创建标签并初始化标签上的文字和Button
       for(int i=0; i < tabText.length; ++i){
    	   contentPanels[i] = createContent(i);
    	   pane.add(tabText[i], contentPanels[i]);
    	   tabComponents[i] = new ButtonTabComponent(pane, functionItems[i]);
    	   pane.setTabComponentAt(i, tabComponents[i]);
       }
    }
   
    private Class<?>[] mainPanels = {FunctionPanel.class, EditCodeAndBrowse.class};
    //创建标签内容部分
    private JPanel createContent(int index) {
		if(index >= mainPanels.length) return null;
		JPanel obj = null;
		try {
			obj = (JPanel) mainPanels[index].newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return obj;
    }
    
    private class MyActionListener implements ActionListener{
	   	private JMenuItem item;
	   	private int tabIndex;
		@Override
		public void actionPerformed(ActionEvent e) {
			if(item.isSelected()){
				pane.add(tabText[tabIndex], contentPanels[tabIndex]);
				pane.setTabComponentAt(pane.getTabCount()-1, tabComponents[tabIndex]);
				//判断是否和 可关闭菜单项 对应
				if( ((ButtonTabComponent)tabComponents[tabIndex]).getIsTabColsed() != componentTabItem.isSelected())
					((ButtonTabComponent)tabComponents[tabIndex]).setIsTabColsed();
			} else {
				int i = pane.indexOfTabComponent(tabComponents[tabIndex]);
				if(i != -1)
					pane.remove(i);
			}
		}

		public MyActionListener(JMenuItem item, int tabIndex) {
			super();
			this.item = item;
			this.tabIndex = tabIndex;
		}
   }
    
    //创建菜单栏
    private void initMenu() {
       //创建一个菜单条
       JMenuBar mb = new JMenuBar();
       
       //设置有关闭按键的标签
       componentTabItem.setSelected(true);
       //设置可关闭的标签的菜单
       componentTabItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,InputEvent.CTRL_MASK));
       componentTabItem.addActionListener(new ActionListener() {
          
           @Override
           public void actionPerformed(ActionEvent e) {
              for(int i = 0 ; i<tabText.length && i<pane.getTabCount(); i++){
            	  if(pane.getTabComponentAt(i) instanceof ButtonTabComponent)
                  	((ButtonTabComponent)pane.getTabComponentAt(i)).setIsTabColsed();
              }
           }
       });
       
       for(int i=0; i<functionItems.length; ++i){
    	   functionItems[i] = new JCheckBoxMenuItem(tabText[i]);
    	   functionItems[i].setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C+i+1,InputEvent.CTRL_MASK));
    	   functionItems[i].setSelected(true);
    	   functionItems[i].addActionListener(new MyActionListener(functionItems[i], i));
       }
      
       //创建菜单
       JMenu selectMenu = new JMenu("选项");
       selectMenu.add(componentTabItem);
       mb.add(selectMenu);
       
       JMenu functionMenu = new JMenu("功能");
       for(int i=0; i<functionItems.length; ++i){
    	   functionMenu.add(functionItems[i]);
       }
       mb.add(functionMenu);
       
       setJMenuBar(mb);
    }
}
