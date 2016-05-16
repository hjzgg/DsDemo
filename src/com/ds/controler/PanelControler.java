package com.ds.controler;

import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.LayoutManager;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import net.sf.json.JSONObject;

import com.ds.button.FunctionButton;
import com.ds.main.MainFrame;
import com.ds.panel.FunctionPanel;

public class PanelControler {
	//该面板的上一个面板
	public static Map<FunctionMsgPanel, FunctionMsgPanel> prePanel = new TreeMap<FunctionMsgPanel, FunctionMsgPanel>();
	//点击按钮后的下一个面板, 以按钮上的Text作为关键字
	public static Map<String, JPanel> nextPanel = new TreeMap<String, JPanel>();
	//绘图控制器
	public static DrawControler drawControler = null;
	//画面分割器
	public static JSplitPane splitPaneVer = null;
	public static JSplitPane splitPaneHor = null;
	//主界面的JFrame, MyDialog会用到
	public static MainFrame mainFrame = null;
	//算法模拟面板
	public static class SimulatePanel{
		public static class FirstPanel{
			//组件水平距离
			public static final int hgap = 10;
			//组件垂直距离
			public static final int vgap = 10;
			//列数
			public static final int cols = 3;
			//面板上要显示的按钮的信息
			public static String[] btnsMsg = {"顺序表", "链表", "栈", "队列", "串", "广义表", "二叉树", "图", "排序", "并查集", "十字链表", "高级树"};
			//该面板
			public static FunctionMsgPanel firstPanel = new FunctionMsgPanel(new GridLayout(0, cols, hgap, vgap));
			
			public static void initFirstPanel(){
				firstPanel.setBtnsMsg(btnsMsg);
				firstPanel.initPanel();
			}
		}
		
		public static class SecondPanel{
			//组件水平距离
			public static final int hgap = 150;
			//组件垂直距离
			public static final int vgap = 20;
			//列数
			public static final int cols = 2;
			//FirstPanel中的每一个btn都对应一个SecondPanel面板
			//SecondPanel.btnsMsg 和 FirstPanel.btnsMsg长度一致，而且互相对应!
			public static String[][] btnsMsg = {
				//顺序表功能
				{"数组显示", "数组创建", "数组删除", "数组插入"},
				//链表功能
				{"链表显示", "链表创建", "链表删除", "链表插入", "链表合并(生成新链表)", "链表合并(就地合并)"},
				//栈功能
				{"栈演示", "表达式求值", "迷宫DFS"},
				//队列功能
				{"队列演示", "迷宫BFS"},
				//串功能
				{"串匹配(BF)", "串匹配(KMP_NEXT)", "串匹配(KMP_NEXTVAL)"},
				//广义表功能
				{"广义表显示"},
				//二叉树功能
				{"二叉树显示", "先序创建", "中序创建", "后序创建", "先序遍历", "中序遍历", "后序遍历", "二叉树转化为森林", "哈夫曼树编码"},
				//图功能
				{"图的显示", "广度遍历图", "深度遍历图", "迪杰斯特拉算法", "Prim算法", "Kruskal算法", "Floyd算法"},
				//排序功能
				{"选择排序", "简单插入排序", "二分插入排序", "冒泡排序", "快速排序", "归并排序", "推排序", "希尔排序", "基数排序"},
				//并查集功能
				{"并查集演示"},
				//十字链表
				{"十字链表显示"},
				//高级树功能
				{"线段树显示", "树状数组显示", "森林显示", "森林转换二叉树"}
			};
			public static FunctionMsgPanel[] childFunctionPanel = null;
			public static void initSecondPanel() {
				childFunctionPanel = new FunctionMsgPanel[btnsMsg.length];
				for(int i=0; i<childFunctionPanel.length; ++i){
					childFunctionPanel[i] = new FunctionMsgPanel(new GridLayout(0, cols, hgap, vgap));
					childFunctionPanel[i].setBtnsMsg(btnsMsg[i]);
					childFunctionPanel[i].initPanel();
					nextPanel.put(FirstPanel.btnsMsg[i], childFunctionPanel[i]);
					prePanel.put(childFunctionPanel[i], FirstPanel.firstPanel);
				}
			}
		}
		
		//展示面板
		public static class ThirdPanel{
			//通过SecondPanel上的按钮的信息，找到该按钮对应的算法
			public static Map<String, String> btnMsgMapClassMsg = new TreeMap<String, String>();
			public static FunctionMsgPanel thirdPanel = new FunctionMsgPanel(new GridBagLayout());
			//通过java的反射机制获得DrawControler的内部 确切的 Controler
			public static String[][] classMsg = {
				//顺序表功能
				{"com.ds.controler.DrawControler$ArrayControler;showArrayView", "com.ds.controler.DrawControler$ArrayControler;showArrayCreate", "com.ds.controler.DrawControler$ArrayControler;showArrayDelete", "com.ds.controler.DrawControler$ArrayControler;showArrayInsert"},
				//链表功能
				{"com.ds.controler.DrawControler$ListControler;showListView", "com.ds.controler.DrawControler$ListControler;showListCreate", "com.ds.controler.DrawControler$ListControler;showListDelete", "com.ds.controler.DrawControler$ListControler;showListInsert", "com.ds.controler.DrawControler$ListControler;showListMergeForThree", "com.ds.controler.DrawControler$ListControler;showListMergeForOne"},
				//栈功能
				{"com.ds.controler.DrawControler$StackControler;showStack", "com.ds.controler.DrawControler$FormulaControler;formula", "com.ds.controler.DrawControler$MazeControler;mazeShowByDfs"},
				//队列功能
				{"com.ds.controler.DrawControler$QueueControler;showQueue", "com.ds.controler.DrawControler$MazeControler;mazeShowByBfs"},
				//串功能
				{"com.ds.controler.DrawControler$StringControler;BF", "com.ds.controler.DrawControler$StringControler;KMP_NEXT", "com.ds.controler.DrawControler$StringControler;KMP_NEXTVAL"},
				//广义表功能
				{"com.ds.controler.DrawControler$GListControler;showGListView"},
				//二叉树功能
				{"com.ds.controler.DrawControler$BTreeControler;showBTreeView", "com.ds.controler.DrawControler$BTreeControler;showPreCreateBTree", "com.ds.controler.DrawControler$BTreeControler;showInorCreateBTree", "com.ds.controler.DrawControler$BTreeControler;showPostCreateBTree", "com.ds.controler.DrawControler$BTreeControler;showPreData", "com.ds.controler.DrawControler$BTreeControler;showInorData", "com.ds.controler.DrawControler$BTreeControler;showPostData", "com.ds.controler.DrawControler$BTreeControler;showBTreeToForest", "com.ds.controler.DrawControler$HuffmanTreeControler;huffmanTree"},
				//图功能
				{"com.ds.controler.DrawControler$GraphicControler;showGraphicView", "com.ds.controler.DrawControler$GraphicControler;BFSGraphic", "com.ds.controler.DrawControler$GraphicControler;DFSGraphic", "com.ds.controler.DrawControler$GraphicControler;dijkstra", "com.ds.controler.DrawControler$GraphicControler;prim", "com.ds.controler.DrawControler$GraphicControler;kruskal", "com.ds.controler.DrawControler$GraphicControler;floyd"},
				//排序功能
				{"com.ds.controler.DrawControler$SelectiveSortControler;selectiveSort", "com.ds.controler.DrawControler$SampleInsertSortControler;insertSort", "com.ds.controler.DrawControler$BinaryInsertSortControler;binaryInsertSort", "com.ds.controler.DrawControler$BubbleSortControler;bubbleSort", "com.ds.controler.DrawControler$QuickSortControler;quickSort", "com.ds.controler.DrawControler$MergeSortControler;mergeSort", "com.ds.controler.DrawControler$HeapSortControler;heapSort", "com.ds.controler.DrawControler$ShellInsertSortControler;insertSort", "com.ds.controler.DrawControler$RadixSortControler;radixSort"},
				//并查集功能
				{"com.ds.controler.DrawControler$UnionFindSetControler;unionFindSet"},
				//十字链表
				{"com.ds.controler.DrawControler$CrossListControler;showCrossListView"},
				//高级树功能
				{"com.ds.controler.DrawControler$SegmentTreeControler;showSegmentTreeView", "com.ds.controler.DrawControler$BinaryIndexedTreeControler;showBinaryIndexedTreeView", "com.ds.controler.DrawControler$ForestControler;showFroestCreataData", "com.ds.controler.DrawControler$ForestControler;showForestToBTree"}
			};
			
			public static void initThirdPanel(){
				for(int i=0; i < SecondPanel.btnsMsg.length; ++i)
					for(int j=0; j < SecondPanel.btnsMsg[i].length; ++j) {
						nextPanel.put(SecondPanel.btnsMsg[i][j], thirdPanel);
						btnMsgMapClassMsg.put(SecondPanel.btnsMsg[i][j], classMsg[i][j]);
						//创建算法源码文件
						try {
							File file = new File("./algorithmCode/" + classMsg[i][j] + ".txt");
							file.createNewFile();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				thirdPanel.initPanel();
			}
		}
	}
	
	
	//初始化
	public static void initControler(DrawControler drawControler){
		PanelControler.drawControler = drawControler;
		//算法模拟面板中的第二类面板初始化
		SimulatePanel.FirstPanel.initFirstPanel();
		SimulatePanel.SecondPanel.initSecondPanel();
		SimulatePanel.ThirdPanel.initThirdPanel();
	}
	
	public static class FunctionMsgPanel extends JPanel implements Comparable<FunctionMsgPanel>{
		public FunctionButton[] btns = null;
		private boolean initBtns = false;
		public boolean isInitBtns(){
			return initBtns;
		}
		public void initBtns(){
			initBtns = true;
		}
		private String[] btnsMsg;
		public void setBtnsMsg(String[] btnsMsg){
			this.btnsMsg = btnsMsg;
		}
		public FunctionMsgPanel(LayoutManager layout) {
			super();
			this.setAlignmentY(Component.CENTER_ALIGNMENT);
			this.setLayout(layout);
		}
		
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			ImageIcon imageIcon = new ImageIcon("image/super_bg/center.jpg");
			Image image = imageIcon.getImage();
			if(image != null)
				g.drawImage(image, 0, 0, this.getWidth(), this.getHeight(), 0, 0, image.getWidth(null), image.getHeight(null), null);
		}
		
		public void initPanel(){
			if(this.getLayout() instanceof GridLayout){//功能面板
				btns = new FunctionButton[btnsMsg.length];
				for(int i=0; i<btns.length; ++i){
					btns[i] = new FunctionButton(btnsMsg[i]);
					btns[i].setIcon(new ImageIcon(FunctionPanel.class.getResource("/com/sun/java/swing/plaf/windows/icons/TreeOpen.gif")));
					btns[i].setFont(new Font("微软雅黑 Light", Font.BOLD, 18));
					this.add(btns[i]);
				}
			} else {//显示面板
				splitPaneVer = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
				splitPaneVer.setContinuousLayout(true);
				splitPaneVer.setOneTouchExpandable(true);
				splitPaneVer.setResizeWeight(0.7);
				splitPaneVer.setTopComponent(drawControler.getMainCodePanel());
				splitPaneVer.setBottomComponent(drawControler.getMainMsgPanel());
				
				splitPaneHor = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
				splitPaneHor.setContinuousLayout(true);
				splitPaneHor.setOneTouchExpandable(true);
				splitPaneHor.setResizeWeight(0.8);
				splitPaneHor.setLeftComponent(drawControler.getDrawScrollPane());
				splitPaneHor.setRightComponent(splitPaneVer);
				this.add(splitPaneHor);
				
				GridBagLayout layout = (GridBagLayout) this.getLayout();
				GridBagConstraints gbc = new GridBagConstraints();
				gbc.fill = GridBagConstraints.BOTH;
				gbc.gridwidth = 1;//该方法是设置组件水平所占用的格子数，如果为0，就说明该组件是该行的最后一个
				gbc.gridheight = 1;
				gbc.weightx = 1;//该方法设置组件水平的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
				gbc.weighty = 1;//该方法设置组件垂直的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
				layout.setConstraints(splitPaneHor, gbc);
			}
		}
		
		@Override
		public int compareTo(FunctionMsgPanel o) {
			return this.hashCode() - o.hashCode();
		}
	}
	
	public static void initMsgPanelText(JSONObject params){
		StringBuilder sb = new StringBuilder();
		for(Object key : params.keySet()){
			sb.append(key).append(params.get(key)).append("\n");
		}
		drawControler.getMsgPanel().setText(sb.toString());
	}
}
