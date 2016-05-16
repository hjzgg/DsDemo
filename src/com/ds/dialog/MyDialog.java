package com.ds.dialog;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.ds.button.CreateIconButton;

public class MyDialog extends JDialog {
	//文本的字体
	private Font myFont = new Font("宋体", Font.PLAIN, 30);
	//数据文本域
	private JTextField dataFiled = new JTextField(20);
	//数据示例Label
	private JTextField demoFiled = new JTextField(20);
	//数据格式Label
	private JTextField formatFiled = new JTextField(20);
	//图类型选择
	private JComboBox typeBox = new JComboBox();
	//图的权值选择
	private JComboBox weightBox = new JComboBox();
	//步长Filed
	private JTextField stepFiled = new JTextField(15);
	//步长数组
	private int[] dk = null;
	//如果是图，是否为有向图
	private boolean isDirected;
	//如果是图，边是否有权值
	private boolean isWeighted;
	//最终的输入数据
	private String data;
	//如果是排序，排序由大到小？
	private boolean minOrMax;
	//如果是排序，排序是快速排序，方式1还是方式2
	private boolean qFirstOrSecond;
	//如果是排序，排序是基数排序，lsd（1）， lsd(2), msd;
	private int rOneOfThree;
	//表达式计算， 堆栈或者是二叉树
	private int formulaShowWay = 1;
	
	public int getFormulaShowWay(){
		return formulaShowWay;
	}
	
	public int[] getSteps(){
		return dk;
	}
	
	public boolean getIsWeighted(){
		return isWeighted;
	}
	
	public boolean getIsDirected(){
		return isDirected;
	}
	
	public String getData(){
		return data;
	}
	public boolean isMinOrMax() {
		return minOrMax;
	}
	public boolean isqFirstOrSecond() {
		return qFirstOrSecond;
	}
	public int getrOneOfThree() {
		return rOneOfThree;
	}
	 
	//数据格式 赋值
	public void setDataFormatContent(String format){
		formatFiled.setText(format);
	}
	//数据样例 赋值
	public void setDataDemoContent(String demo){
		demoFiled.setText(demo);
	}
	
	//排序的类型;根据排序的类型，选择不同的排序方式，如快排，基数排序...
	public static final int SORT_OTHER_TYPE = 0;
	public static final int SORT_QUICK_TYPE = 1;
	public static final int SORT_RADIX_TYPE = 2;
	public static final int SORT_SHELL_TYPE = 3;//希尔排序，需要制定步长
	
	private int sortType = SORT_OTHER_TYPE;
	public void setSortType(int sortType){
		this.sortType = sortType;
		initSortType();
	}
	
	public static final int DIALOG_WIDTH = 500;
	public static final int DIALOG_HEIGHT = 500;
	
	//模型的类型
	public static final int MODEL_TYPE_OTHER = 0;
	public static final int MODEL_TYPE_SORT = 1;
	public static final int MODEL_TYPE_GRAPHIC = 2;
	public static final int MODEL_TYPE_TREE = 3;//线段树或者是树状数组
	public static final int MODEL_TYPE_FORMULA = 4;//表达式的计算
	
	private JPanel selectPane;
	
	private void initSortType(){
		JPanel rightPanel = new JPanel();
		rightPanel.setOpaque(false);
		rightPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 0));
		selectPane.add(rightPanel);
		JLabel weightLable = null;
		if(sortType == SORT_QUICK_TYPE){
			weightLable = new JLabel("方式:");
			weightBox.addItem("两指针");
			weightBox.addItem("一指针");
			rightPanel.add(weightLable);
			rightPanel.add(weightBox);
		} else if(sortType == SORT_RADIX_TYPE){
			weightLable = new JLabel("方式:");
			weightBox.addItem("LSD(1)");
			weightBox.addItem("LSD(2)");
			weightBox.addItem("MSD");
			rightPanel.add(weightLable);
			rightPanel.add(weightBox);
		} else if(sortType == SORT_SHELL_TYPE){
			weightLable = new JLabel("步长:");
			
			rightPanel.add(weightLable);
			final String STEP_DEMO = "5 3 1";
			stepFiled.setText(STEP_DEMO);
			stepFiled.setToolTipText("最后步长一定为1");
			stepFiled.addFocusListener(new FocusListener() {
				@Override
				public void focusLost(FocusEvent e) {
					if("".equals(stepFiled.getText()))
						stepFiled.setText(STEP_DEMO);
				}
				@Override
				public void focusGained(FocusEvent e) {
					if(STEP_DEMO.equals(stepFiled.getText()))
						stepFiled.setText("");
				}
			});
			rightPanel.add(stepFiled);
		}
		weightLable.setOpaque(true);
		weightLable.setFont(myFont);
	}
	/**
	 * @param owner
	 * @param title
	 * @param modal
	 * @param isGraphic 模型是否为图
	 */
	public MyDialog(Frame owner, String title, boolean modal, final int modelType) {
		super(owner, title, modal);
		ImageIcon imageIcon = new ImageIcon("image/dialog_bg.jpg");
		JLabel imgLabel = new JLabel(imageIcon);//将背景图放在标签里。
		this.getLayeredPane().add(imgLabel, new Integer(Integer.MIN_VALUE));//注意这里是关键，将背景标签添加到dialog的LayeredPane面板里。
		imgLabel.setBounds(0, 0, imageIcon.getIconWidth(), imageIcon.getIconHeight());//设置背景标签的位置
		((JPanel)this.getContentPane()).setOpaque(false);
		GridLayout dialogLayout = null;
		if(modelType != MODEL_TYPE_OTHER)
			dialogLayout = new GridLayout(5, 1, 5, 5);
		else 
			dialogLayout = new GridLayout(4, 1, 5, 5);
		//对话框的定位
		if(owner == null){
			Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
			setBounds((screensize.width-DIALOG_WIDTH)/2, (screensize.height-DIALOG_HEIGHT)/2, DIALOG_WIDTH, DIALOG_HEIGHT);
		} else {	
			setBounds(owner.getX()+(owner.getWidth()-DIALOG_WIDTH)/2, owner.getY()+(owner.getHeight()-DIALOG_HEIGHT)/2, DIALOG_WIDTH, DIALOG_HEIGHT);
		}
		setLayout(dialogLayout);
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(10, 10, 10, 10);
		
		typeBox.setFont(new Font("宋体", Font.BOLD, 18));
		weightBox.setFont(new Font("宋体", Font.BOLD, 18));
		
		//添加输入数据文本框
		JPanel dataBackPane = new JPanel();
		dataBackPane.setOpaque(false);
		GridBagLayout dataPaneLayout = new GridBagLayout();
		dataBackPane.setLayout(dataPaneLayout);
		JLabel dataLabel = new JLabel("数据:");
		dataLabel.setOpaque(true);
		dataLabel.setFont(myFont);
		dataFiled.setFont(myFont);
		dataBackPane.add(dataLabel);
		dataBackPane.add(dataFiled);
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.anchor = GridBagConstraints.EAST;
		dataPaneLayout.setConstraints(dataLabel, gbc);
		gbc.anchor = GridBagConstraints.WEST;
		gbc.weightx = 4;
		dataPaneLayout.setConstraints(dataFiled, gbc);
		add(dataBackPane);
		
		//添加数据格式
		JPanel formatBackPane = new JPanel();
		formatBackPane.setOpaque(false);
		GridBagLayout formatPaneLayout = new GridBagLayout();
		formatBackPane.setLayout(formatPaneLayout);
		JLabel formatLabel = new JLabel("格式:");
		formatLabel.setOpaque(true);
		formatLabel.setFont(myFont);
		this.formatFiled.setFont(myFont);
		formatBackPane.add(formatLabel);
		this.formatFiled.setEditable(false);
		formatBackPane.add(this.formatFiled);
		gbc.weightx = 1;
		gbc.anchor = GridBagConstraints.EAST;
		formatPaneLayout.setConstraints(formatLabel, gbc);
		gbc.weightx = 4;
		gbc.anchor = GridBagConstraints.WEST;
		formatPaneLayout.setConstraints(this.formatFiled, gbc);
		add(formatBackPane);
		
		//添加数据样例
		JPanel demoBackPane = new JPanel();
		demoBackPane.setOpaque(false);
		GridBagLayout demoPaneLayout = new GridBagLayout();
		demoBackPane.setLayout(demoPaneLayout);
		JLabel demoLabel = new JLabel("样例:");
		demoLabel.setOpaque(true);
		demoLabel.setFont(myFont);
		demoFiled.setFont(myFont);
		demoBackPane.add(demoLabel);
		demoFiled.setEditable(false);
		demoBackPane.add(demoFiled);
		gbc.weightx = 1;
		gbc.anchor = GridBagConstraints.EAST;
		demoPaneLayout.setConstraints(demoLabel, gbc);
		gbc.weightx = 4;
		gbc.anchor = GridBagConstraints.WEST;
		demoPaneLayout.setConstraints(demoFiled, gbc);
		add(demoBackPane);
		
		//添加图的权值选项， 还有类型
		if(modelType == MODEL_TYPE_GRAPHIC){
			selectPane = new JPanel();
			selectPane.setOpaque(false);
			GridLayout selectLayout = new GridLayout(1, 2);
			selectPane.setLayout(selectLayout);
			JPanel leftPanel = new JPanel();
			leftPanel.setOpaque(false);
			leftPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 0));
			selectPane.add(leftPanel);
			JLabel typeLable = new JLabel("类型:");
			typeLable.setOpaque(true);
			typeLable.setFont(myFont);
			typeBox.addItem("无向图");
			typeBox.addItem("有向图");
			leftPanel.add(typeLable);
			leftPanel.add(typeBox);
			
			JPanel rightPanel = new JPanel();
			rightPanel.setOpaque(false);
			rightPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
			selectPane.add(rightPanel);
			JLabel weightLable = new JLabel("权值:");
			weightLable.setOpaque(true);
			weightLable.setFont(myFont);
			weightBox.addItem("无权值");
			weightBox.addItem("有权值");
			rightPanel.add(weightLable);
			rightPanel.add(weightBox);
			
			add(selectPane);
		} else if(modelType != MODEL_TYPE_OTHER){
			selectPane = new JPanel();
			selectPane.setOpaque(false);
			GridLayout selectLayout = new GridLayout(1, 2);
			selectPane.setLayout(selectLayout);
			JPanel leftPanel = new JPanel();
			leftPanel.setOpaque(false);
			leftPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 0));
			selectPane.add(leftPanel);
			JLabel typeLable = null;
			if(modelType == MODEL_TYPE_SORT){
				typeLable = new JLabel("排序:");
				typeBox.addItem("由小到大");
				typeBox.addItem("由大到小");
			} else if(modelType == MODEL_TYPE_TREE) {
				typeLable = new JLabel("最值:");
				typeBox.addItem("最大值");
				typeBox.addItem("最小值");
			} else if(modelType == MODEL_TYPE_FORMULA){
				typeLable = new JLabel("方式:");
				typeBox.addItem("堆栈");
				typeBox.addItem("二叉树");
			}
			typeLable.setOpaque(true);
			typeLable.setFont(myFont);
			leftPanel.add(typeLable);
			leftPanel.add(typeBox);
			add(selectPane);
		}  
		//添加确定按钮
		JPanel btnPanel = new JPanel();
		btnPanel.setOpaque(false);
		btnPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		JButton btn = CreateIconButton.createBtn("确定", "image/btnIcon/confirm.png");
		btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(modelType == MODEL_TYPE_GRAPHIC){
					int selectIndex = typeBox.getSelectedIndex();
					if(selectIndex == 0)
						isDirected = false;
					else
						isDirected = true;
					
					selectIndex = weightBox.getSelectedIndex();
					if(selectIndex == 0)
						isWeighted = false;
					else
						isWeighted = true;
				} else if(modelType == MODEL_TYPE_SORT || modelType == MODEL_TYPE_TREE){
					int selectIndex = typeBox.getSelectedIndex();
					if(selectIndex == 0)
						minOrMax = true;
					else 
						minOrMax = false;
						
					if(sortType == SORT_QUICK_TYPE) {
						selectIndex = weightBox.getSelectedIndex();
						if(selectIndex == 0)
							qFirstOrSecond = true;
						else
							qFirstOrSecond = false;
					} else if(sortType == SORT_RADIX_TYPE){
						selectIndex = weightBox.getSelectedIndex();
						rOneOfThree = selectIndex;
					} else if(sortType == SORT_SHELL_TYPE) {
						String[] steps = stepFiled.getText().split(" ");
						dk = new int[steps.length];
						for(int i=0; i < dk.length; ++i){
							try{
								dk[i] = Integer.parseInt(steps[i]);
							} catch(Exception ex){
								dk[i] = 1;
							}
						}
					}
				} else if(modelType == MODEL_TYPE_FORMULA){
					formulaShowWay = typeBox.getSelectedIndex()+1;
				}
				
				data = dataFiled.getText();
				MyDialog.this.dispose();
			}
		});
		btnPanel.add(btn);
		add(btnPanel);
	}
	
	public static void main(String[] args){
		MyDialog dialog = new MyDialog(null, "test", true, MODEL_TYPE_SORT);
		dialog.setSortType(MyDialog.SORT_RADIX_TYPE);
		dialog.setDataDemoContent("hjzgg_demo");
		dialog.setDataFormatContent("hjzgg_format");
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.setVisible(true);
	}
}
