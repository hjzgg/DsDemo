package com.ds.dialog;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.sun.media.sound.ModelTransform;

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
	
	private JPanel selectPane;
	
	private void initSortType(){
		if(sortType == SORT_QUICK_TYPE){
			JPanel rightPanel = new JPanel();
			rightPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
			selectPane.add(rightPanel);
			JLabel weightLable = new JLabel("��ʽ:");
			weightLable.setFont(myFont);
			weightBox.addItem("��ָ��");
			weightBox.addItem("һָ��");
			rightPanel.add(weightLable);
			rightPanel.add(weightBox);
		} else if(sortType == SORT_RADIX_TYPE){
			JPanel rightPanel = new JPanel();
			rightPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
			selectPane.add(rightPanel);
			JLabel weightLable = new JLabel("��ʽ:");
			weightLable.setFont(myFont);
			weightBox.addItem("LSD(1)");
			weightBox.addItem("LSD(2)");
			weightBox.addItem("MSD");
			rightPanel.add(weightLable);
			rightPanel.add(weightBox);
		}
	}
	/**
	 * @param owner
	 * @param title
	 * @param modal
	 * @param isGraphic ģ���Ƿ�Ϊͼ
	 */
	public MyDialog(Frame owner, String title, boolean modal, final int modelType) {
		super(owner, title, modal);
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
		
		//������������ı���
		JPanel dataBackPane = new JPanel();
		GridBagLayout dataPaneLayout = new GridBagLayout();
		dataBackPane.setLayout(dataPaneLayout);
		JLabel dataLabel = new JLabel("����:");
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
		GridBagLayout formatPaneLayout = new GridBagLayout();
		formatBackPane.setLayout(formatPaneLayout);
		JLabel formatLabel = new JLabel("��ʽ:");
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
		GridBagLayout demoPaneLayout = new GridBagLayout();
		demoBackPane.setLayout(demoPaneLayout);
		JLabel demoLabel = new JLabel("����:");
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
			GridLayout selectLayout = new GridLayout(1, 2);
			selectPane.setLayout(selectLayout);
			JPanel leftPanel = new JPanel();
			leftPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 0));
			selectPane.add(leftPanel);
			JLabel typeLable = new JLabel("����:");
			typeLable.setFont(myFont);
			typeBox.addItem("����ͼ");
			typeBox.addItem("����ͼ");
			leftPanel.add(typeLable);
			leftPanel.add(typeBox);
			
			JPanel rightPanel = new JPanel();
			rightPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
			selectPane.add(rightPanel);
			JLabel weightLable = new JLabel("Ȩֵ:");
			weightLable.setFont(myFont);
			weightBox.addItem("��Ȩֵ");
			weightBox.addItem("��Ȩֵ");
			rightPanel.add(weightLable);
			rightPanel.add(weightBox);
			
			add(selectPane);
		} else if(modelType == MODEL_TYPE_SORT){
			selectPane = new JPanel();
			GridLayout selectLayout = new GridLayout(1, 2);
			selectPane.setLayout(selectLayout);
			JPanel leftPanel = new JPanel();
			leftPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 0));
			selectPane.add(leftPanel);
			JLabel typeLable = new JLabel("����:");
			typeLable.setFont(myFont);
			typeBox.addItem("��С����");
			typeBox.addItem("�ɴ�С");
			leftPanel.add(typeLable);
			leftPanel.add(typeBox);
			
			add(selectPane);
		}
		
		//���ȷ����ť
		JPanel btnPanel = new JPanel();
		btnPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		JButton btn = new JButton("ȷ��");
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
				} else if(modelType == MODEL_TYPE_SORT){
					if(sortType == SORT_QUICK_TYPE){
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
						}
					}
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
