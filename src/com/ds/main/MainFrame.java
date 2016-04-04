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
    private final String[] tabText = {"ģ��ϵͳ", "��ϰϵͳ", "�û�����"};
    private final JPanel[] contentPanels = new JPanel[tabText.length];
    private final JPanel[] tabComponents = new JPanel[tabText.length];
    private final JMenuItem[] functionItems = new JCheckBoxMenuItem[tabText.length];
    private final JMenuItem componentTabItem = new JCheckBoxMenuItem("����Ϊ�ɹرյ�tab");
    public MainFrame(String title) {
       //����frame������
       super(title);
       //���ùرշ�ʽ
       addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				//��ֹ���м�ʱ���е�����
				MyTimer.getTimer().cancel();
				MainFrame.this.dispose();
				System.exit(0);
			}
	   });
       //�����˵���
       initMenu();
       //���ÿ��ص���Tab
       pane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
       //��tabpane��ӵ�frame��
       add(pane);
       //���ý�����۵���ʽ
       try {
    	   UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
       } catch (Exception e) {
		   e.printStackTrace();
       }
       //��ʼ������
       createMainFrame();
    }
   
    //��ʼ��tabpanel������ã���������resize�ĵ���
    private void createMainFrame() {
       //������ǩ����ʼ����ǩ�ϵ����ֺ�Button
       for(int i=0; i < tabText.length; ++i){
    	   contentPanels[i] = createContent(i);
    	   pane.add(tabText[i], contentPanels[i]);
    	   tabComponents[i] = new ButtonTabComponent(pane, functionItems[i]);
    	   pane.setTabComponentAt(i, tabComponents[i]);
       }
    }
   
    private Class<?>[] mainPanels = {FunctionPanel.class, EditCodeAndBrowse.class};
    //������ǩ���ݲ���
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
				//�ж��Ƿ�� �ɹرղ˵��� ��Ӧ
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
    
    //�����˵���
    private void initMenu() {
       //����һ���˵���
       JMenuBar mb = new JMenuBar();
       
       //�����йرհ����ı�ǩ
       componentTabItem.setSelected(true);
       //���ÿɹرյı�ǩ�Ĳ˵�
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
      
       //�����˵�
       JMenu selectMenu = new JMenu("ѡ��");
       selectMenu.add(componentTabItem);
       mb.add(selectMenu);
       
       JMenu functionMenu = new JMenu("����");
       for(int i=0; i<functionItems.length; ++i){
    	   functionMenu.add(functionItems[i]);
       }
       mb.add(functionMenu);
       
       setJMenuBar(mb);
    }
}
