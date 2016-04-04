package com.ds.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class JFontChooser extends JPanel {

    //�������
    private String current_fontName = "����";//��ǰ����������,Ĭ������.
    private int current_fontStyle = Font.PLAIN;//��ǰ������,Ĭ�ϳ���.
    private int current_fontSize = 9;//��ǰ�����С,Ĭ��9��.
    private Color current_color = Color.BLACK;//��ǰ��ɫ,Ĭ�Ϻ�ɫ.
    private JDialog dialog;//������ʾģ̬�Ĵ���
    private MyFont myfont;//����Color������.
    private JLabel lblFont;//ѡ�������LBL
    private JLabel lblStyle;//ѡ�����͵�LBL
    private JLabel lblSize;//ѡ���ִ�С��LBL
    private JLabel lblColor;//ѡ��Color��label
    private JTextField txtFont;//��ʾѡ�������TEXT
    private JTextField txtStyle;//��ʾѡ�����͵�TEXT
    private JTextField txtSize;//��ʾѡ���ִ�С��TEXT
    private JList lstFont;//ѡ��������б�.
    private JList lstStyle;//ѡ�����͵��б�.
    private JList lstSize;//ѡ�������С���б�.
    private JComboBox cbColor;//ѡ��Color��������.
    private JButton ok, cancel;//"ȷ��","ȡ��"��ť.
    private JScrollPane spFont;
    private JScrollPane spSize;
    private JLabel lblShow;//��ʾЧ����label.
    private JPanel showPan;//��ʾ��.
    private Map<String, Integer> sizeMap;//�ֺ�ӳ���.
    private Map<String, Color> colorMap;//����ɫӳ���.
    
    //�������_����________________
    public JFontChooser(MyFont curFont) {
    	//ʵ��������
    	lblFont = new JLabel("����:");
    	lblStyle = new JLabel("����:");
    	lblSize = new JLabel("��С:");
    	lblColor = new JLabel("��ɫ:");
    	lblShow = new JLabel("Sample Test!", JLabel.CENTER);
    	txtFont = new JTextField("����");
    	txtStyle = new JTextField("����");
    	txtSize = new JTextField("9");
    	//ȡ�õ�ǰ������������.
    	GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    	String[] fontNames = ge.getAvailableFontFamilyNames();
    	lstFont = new JList(fontNames);
    	//����.
    	lstStyle = new JList(new String[]{"����", "б��", "����", "��б��"});
    	//�ֺ�.
    	String[] sizeStr = new String[]{
			"8", "9", "10", "11", "12", "14", "16", "18", "20", "22", "24", "26", "28", "36", "48", "72",
			"����", "С��", "һ��", "Сһ", "����", "С��", "����", "С��", "�ĺ�", "С��", "���", "С��", "����", "С��", "�ߺ�", "�˺�"
    	};
    	int sizeVal[] = {8, 9, 10, 11, 12, 14, 16, 18, 20, 22, 24, 26, 28, 36, 48, 72, 42, 36, 26, 24, 22, 18, 16, 15, 14, 12, 11, 9, 8, 7, 6, 5};
    	sizeMap = new HashMap<String, Integer>();
    	for (int i = 0; i < sizeStr.length; ++i) {
    		sizeMap.put(sizeStr[i], sizeVal[i]);
    	}
    	lstSize = new JList(sizeStr);
    	spFont = new JScrollPane(lstFont);
    	spSize = new JScrollPane(lstSize);
    	
    	String[] colorStr = new String[]{
    			"��ɫ", "��ɫ", "���", "��ɫ", "��ɫ", "ǳ��", "���", "�ۻ�", "�ۺ�", "��ɫ", "��ɫ", "��ɫ"
    	};
    	Color[] colorVal = new Color[]{
    			Color.BLACK, Color.BLUE, Color.DARK_GRAY, Color.GRAY, Color.GREEN, Color.LIGHT_GRAY, Color.MAGENTA, Color.ORANGE, Color.PINK, Color.RED, Color.WHITE, Color.YELLOW
    	};
    	colorMap = new HashMap<String, Color>();
    	for (int i = 0; i < colorStr.length; i++) {
    		colorMap.put(colorStr[i], colorVal[i]);
    	}
    	cbColor = new JComboBox(colorStr);
    	showPan = new JPanel();
    	ok = new JButton("ȷ��");
    	cancel = new JButton("ȡ��");
    	//ʵ��������_����
    	
    	//���ֿؼ�
    	this.setLayout(null);//���ò��ֹ�����.     
    	add(lblFont);
    	lblFont.setBounds(12, 10, 30, 20);
    	txtFont.setEditable(false);
    	add(txtFont);
    	txtFont.setBounds(10, 30, 155, 20);
    	add(spFont);
    	spFont.setBounds(10, 50, 155, 100);
    	
    	add(lblStyle);
    	lblStyle.setBounds(175, 10, 30, 20);
    	txtStyle.setEditable(false);
    	add(txtStyle);
    	txtStyle.setBounds(175, 30, 130, 20);
    	lstStyle.setBorder(javax.swing.BorderFactory.createLineBorder(Color.gray));
    	add(lstStyle);
    	lstStyle.setBounds(175, 50, 130, 100);
    	
    	add(lblSize);
    	lblSize.setBounds(320, 10, 30, 20);
    	txtSize.setEditable(false);
    	add(txtSize);
    	txtSize.setBounds(320, 30, 60, 20);
    	add(spSize);
    	spSize.setBounds(320, 50, 60, 100);
    	
    	
    	add(lblColor);
    	lblColor.setBounds(13, 170, 30, 20);
    	add(cbColor);
    	cbColor.setBounds(10, 195, 130, 22);
    	cbColor.setMaximumRowCount(5);
    	
    	showPan.setBorder(javax.swing.BorderFactory.createTitledBorder("ʾ��"));
    	add(showPan);
    	showPan.setBounds(150, 170, 230, 100);
    	showPan.setLayout(new BorderLayout());
    	lblShow.setBackground(Color.white);
    	showPan.add(lblShow);
    	add(ok);
    	ok.setBounds(10, 240, 60, 20);
    	add(cancel);
    	cancel.setBounds(80, 240, 60, 20);
    	//���ֿؼ�_����
    	
    	//�¼�
    	lstFont.addListSelectionListener(new ListSelectionListener() {
    		
    		public void valueChanged(ListSelectionEvent e) {
    			current_fontName = (String) lstFont.getSelectedValue();
    			txtFont.setText(current_fontName);
    			lblShow.setFont(new Font(current_fontName, current_fontStyle, current_fontSize));
    		}
    	});
    	
    	lstStyle.addListSelectionListener(new ListSelectionListener() {
    		
    		public void valueChanged(ListSelectionEvent e) {
    			String value = (String) ((JList) e.getSource()).getSelectedValue();
    			if (value.equals("����")) {
    				current_fontStyle = Font.PLAIN;
    			}
    			if (value.equals("б��")) {
    				current_fontStyle = Font.ITALIC;
    			}
    			if (value.equals("����")) {
    				current_fontStyle = Font.BOLD;
    			}
    			if (value.equals("��б��")) {
    				current_fontStyle = Font.BOLD | Font.ITALIC;
    			}
    			txtStyle.setText(value);
    			lblShow.setFont(new Font(current_fontName, current_fontStyle, current_fontSize));
    		}
    	});
    	
    	lstSize.addListSelectionListener(new ListSelectionListener() {
    		
    		public void valueChanged(ListSelectionEvent e) {
    			current_fontSize = sizeMap.get(lstSize.getSelectedValue());
    			txtSize.setText(String.valueOf(current_fontSize));
    			lblShow.setFont(new Font(current_fontName, current_fontStyle, current_fontSize));
    		}
    	});
    	
    	cbColor.addActionListener(new ActionListener() {
    		
    		public void actionPerformed(ActionEvent e) {
    			current_color = colorMap.get(cbColor.getSelectedItem());
    			lblShow.setForeground(current_color);
    		}
    	});
    	
    	ok.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e) {
    			myfont = new MyFont();
    			myfont.setFont(new Font(current_fontName, current_fontStyle, current_fontSize));
    			myfont.setColor(current_color);
    			myfont.setColorIndex(cbColor.getSelectedIndex());
    			myfont.setFamilyIndex(lstFont.getSelectedIndex());
    			myfont.setSizeIndex(lstSize.getSelectedIndex());
    			myfont.setStyleIndex(lstStyle.getSelectedIndex());
    			dialog.dispose();
    			dialog = null;
    		}
    	});
    	
    	cancel.addActionListener(new ActionListener() {
    		
    		public void actionPerformed(ActionEvent e) {
    			myfont = null;
    			dialog.dispose();
    			dialog = null;
    		}
    	});
    	//�¼�_����
    	
    	if(curFont != null){
    		if(curFont.getFamilyIndex() != -1) {
    			lstFont.setSelectedIndex(curFont.getFamilyIndex());
    			lstFont.ensureIndexIsVisible(curFont.getFamilyIndex());
    		} else {
    			lstFont.setSelectedValue("����", true);
    		}
    		
    		lstSize.setSelectedIndex(curFont.getSizeIndex());
    		lstSize.ensureIndexIsVisible(curFont.getSizeIndex());
    		
    		lstStyle.setSelectedIndex(curFont.getStyleIndex());
    		lstStyle.ensureIndexIsVisible(curFont.getStyleIndex());
    		cbColor.setSelectedIndex(curFont.getColorIndex());
    	}
    }

    public MyFont showDialog(Frame parent, String title, int dx, int dy) {
        if(title == null)
            title = "Font";
        dialog = new JDialog(parent, title,true);
        dialog.add(this);
        dialog.setResizable(false);
        dialog.setBounds(dx, dy, 400, 310);
        dialog.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                myfont = null;
                dialog.removeAll();
                dialog.dispose();
                dialog = null;
            }
        });

        dialog.setVisible(true);
        return myfont;
    }
}  

class MyFont{
    private Font font;
    private Color color;

    public Font getFont() {
        return font;
    }

    public void setFont(Font font) {
        this.font = font;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
    //������������
    private int familyIndex = -1;
    //�����С����
    private int sizeIndex = -1;
    //������ɫ����
    private int colorIndex = -1;
    //����������
    private int styleIndex = -1;

	public int getFamilyIndex() {
		return familyIndex;
	}

	public int getSizeIndex() {
		return sizeIndex;
	}

	public int getColorIndex() {
		return colorIndex;
	}

	public int getStyleIndex() {
		return styleIndex;
	}

	public void setFamilyIndex(int familyIndex) {
		this.familyIndex = familyIndex;
	}

	public void setSizeIndex(int sizeIndex) {
		this.sizeIndex = sizeIndex;
	}

	public void setColorIndex(int colorIndex) {
		this.colorIndex = colorIndex;
	}

	public void setStyleIndex(int styleIndex) {
		this.styleIndex = styleIndex;
	}

	@Override
	public String toString() {
		return familyIndex + " " + sizeIndex + " " + styleIndex + " " + colorIndex + " \n" +
				font + " " + color; 
	}
    
}
