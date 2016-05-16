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
	//�ı�������
	private Font myFont = new Font("����", Font.PLAIN, 30);
	//�����ı���
	private JTextField dataFiled = new JTextField(20);
	//����ʾ��Label
	private JTextField demoFiled = new JTextField(20);
	//���ݸ�ʽLabel
	private JTextField formatFiled = new JTextField(20);
	//ͼ����ѡ��
	private JComboBox typeBox = new JComboBox();
	//ͼ��Ȩֵѡ��
	private JComboBox weightBox = new JComboBox();
	//����Filed
	private JTextField stepFiled = new JTextField(15);
	//��������
	private int[] dk = null;
	//�����ͼ���Ƿ�Ϊ����ͼ
	private boolean isDirected;
	//�����ͼ�����Ƿ���Ȩֵ
	private boolean isWeighted;
	//���յ���������
	private String data;
	//��������������ɴ�С��
	private boolean minOrMax;
	//��������������ǿ������򣬷�ʽ1���Ƿ�ʽ2
	private boolean qFirstOrSecond;
	//��������������ǻ�������lsd��1���� lsd(2), msd;
	private int rOneOfThree;
	//���ʽ���㣬 ��ջ�����Ƕ�����
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
	 
	//���ݸ�ʽ ��ֵ
	public void setDataFormatContent(String format){
		formatFiled.setText(format);
	}
	//�������� ��ֵ
	public void setDataDemoContent(String demo){
		demoFiled.setText(demo);
	}
	
	//���������;������������ͣ�ѡ��ͬ������ʽ������ţ���������...
	public static final int SORT_OTHER_TYPE = 0;
	public static final int SORT_QUICK_TYPE = 1;
	public static final int SORT_RADIX_TYPE = 2;
	public static final int SORT_SHELL_TYPE = 3;//ϣ��������Ҫ�ƶ�����
	
	private int sortType = SORT_OTHER_TYPE;
	public void setSortType(int sortType){
		this.sortType = sortType;
		initSortType();
	}
	
	public static final int DIALOG_WIDTH = 500;
	public static final int DIALOG_HEIGHT = 500;
	
	//ģ�͵�����
	public static final int MODEL_TYPE_OTHER = 0;
	public static final int MODEL_TYPE_SORT = 1;
	public static final int MODEL_TYPE_GRAPHIC = 2;
	public static final int MODEL_TYPE_TREE = 3;//�߶�����������״����
	public static final int MODEL_TYPE_FORMULA = 4;//���ʽ�ļ���
	
	private JPanel selectPane;
	
	private void initSortType(){
		JPanel rightPanel = new JPanel();
		rightPanel.setOpaque(false);
		rightPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 0));
		selectPane.add(rightPanel);
		JLabel weightLable = null;
		if(sortType == SORT_QUICK_TYPE){
			weightLable = new JLabel("��ʽ:");
			weightBox.addItem("��ָ��");
			weightBox.addItem("һָ��");
			rightPanel.add(weightLable);
			rightPanel.add(weightBox);
		} else if(sortType == SORT_RADIX_TYPE){
			weightLable = new JLabel("��ʽ:");
			weightBox.addItem("LSD(1)");
			weightBox.addItem("LSD(2)");
			weightBox.addItem("MSD");
			rightPanel.add(weightLable);
			rightPanel.add(weightBox);
		} else if(sortType == SORT_SHELL_TYPE){
			weightLable = new JLabel("����:");
			
			rightPanel.add(weightLable);
			final String STEP_DEMO = "5 3 1";
			stepFiled.setText(STEP_DEMO);
			stepFiled.setToolTipText("��󲽳�һ��Ϊ1");
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
	 * @param isGraphic ģ���Ƿ�Ϊͼ
	 */
	public MyDialog(Frame owner, String title, boolean modal, final int modelType) {
		super(owner, title, modal);
		ImageIcon imageIcon = new ImageIcon("image/dialog_bg.jpg");
		JLabel imgLabel = new JLabel(imageIcon);//������ͼ���ڱ�ǩ�
		this.getLayeredPane().add(imgLabel, new Integer(Integer.MIN_VALUE));//ע�������ǹؼ�����������ǩ��ӵ�dialog��LayeredPane����
		imgLabel.setBounds(0, 0, imageIcon.getIconWidth(), imageIcon.getIconHeight());//���ñ�����ǩ��λ��
		((JPanel)this.getContentPane()).setOpaque(false);
		GridLayout dialogLayout = null;
		if(modelType != MODEL_TYPE_OTHER)
			dialogLayout = new GridLayout(5, 1, 5, 5);
		else 
			dialogLayout = new GridLayout(4, 1, 5, 5);
		//�Ի���Ķ�λ
		if(owner == null){
			Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
			setBounds((screensize.width-DIALOG_WIDTH)/2, (screensize.height-DIALOG_HEIGHT)/2, DIALOG_WIDTH, DIALOG_HEIGHT);
		} else {	
			setBounds(owner.getX()+(owner.getWidth()-DIALOG_WIDTH)/2, owner.getY()+(owner.getHeight()-DIALOG_HEIGHT)/2, DIALOG_WIDTH, DIALOG_HEIGHT);
		}
		setLayout(dialogLayout);
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(10, 10, 10, 10);
		
		typeBox.setFont(new Font("����", Font.BOLD, 18));
		weightBox.setFont(new Font("����", Font.BOLD, 18));
		
		//������������ı���
		JPanel dataBackPane = new JPanel();
		dataBackPane.setOpaque(false);
		GridBagLayout dataPaneLayout = new GridBagLayout();
		dataBackPane.setLayout(dataPaneLayout);
		JLabel dataLabel = new JLabel("����:");
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
		
		//������ݸ�ʽ
		JPanel formatBackPane = new JPanel();
		formatBackPane.setOpaque(false);
		GridBagLayout formatPaneLayout = new GridBagLayout();
		formatBackPane.setLayout(formatPaneLayout);
		JLabel formatLabel = new JLabel("��ʽ:");
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
		
		//�����������
		JPanel demoBackPane = new JPanel();
		demoBackPane.setOpaque(false);
		GridBagLayout demoPaneLayout = new GridBagLayout();
		demoBackPane.setLayout(demoPaneLayout);
		JLabel demoLabel = new JLabel("����:");
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
		
		//���ͼ��Ȩֵѡ� ��������
		if(modelType == MODEL_TYPE_GRAPHIC){
			selectPane = new JPanel();
			selectPane.setOpaque(false);
			GridLayout selectLayout = new GridLayout(1, 2);
			selectPane.setLayout(selectLayout);
			JPanel leftPanel = new JPanel();
			leftPanel.setOpaque(false);
			leftPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 0));
			selectPane.add(leftPanel);
			JLabel typeLable = new JLabel("����:");
			typeLable.setOpaque(true);
			typeLable.setFont(myFont);
			typeBox.addItem("����ͼ");
			typeBox.addItem("����ͼ");
			leftPanel.add(typeLable);
			leftPanel.add(typeBox);
			
			JPanel rightPanel = new JPanel();
			rightPanel.setOpaque(false);
			rightPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
			selectPane.add(rightPanel);
			JLabel weightLable = new JLabel("Ȩֵ:");
			weightLable.setOpaque(true);
			weightLable.setFont(myFont);
			weightBox.addItem("��Ȩֵ");
			weightBox.addItem("��Ȩֵ");
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
				typeLable = new JLabel("����:");
				typeBox.addItem("��С����");
				typeBox.addItem("�ɴ�С");
			} else if(modelType == MODEL_TYPE_TREE) {
				typeLable = new JLabel("��ֵ:");
				typeBox.addItem("���ֵ");
				typeBox.addItem("��Сֵ");
			} else if(modelType == MODEL_TYPE_FORMULA){
				typeLable = new JLabel("��ʽ:");
				typeBox.addItem("��ջ");
				typeBox.addItem("������");
			}
			typeLable.setOpaque(true);
			typeLable.setFont(myFont);
			leftPanel.add(typeLable);
			leftPanel.add(typeBox);
			add(selectPane);
		}  
		//���ȷ����ť
		JPanel btnPanel = new JPanel();
		btnPanel.setOpaque(false);
		btnPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		JButton btn = CreateIconButton.createBtn("ȷ��", "image/btnIcon/confirm.png");
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
