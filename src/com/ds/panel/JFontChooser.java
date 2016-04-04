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

    //定义变量
    private String current_fontName = "宋体";//当前的字体名称,默认宋体.
    private int current_fontStyle = Font.PLAIN;//当前的字样,默认常规.
    private int current_fontSize = 9;//当前字体大小,默认9号.
    private Color current_color = Color.BLACK;//当前字色,默认黑色.
    private JDialog dialog;//用于显示模态的窗体
    private MyFont myfont;//带有Color的字体.
    private JLabel lblFont;//选择字体的LBL
    private JLabel lblStyle;//选择字型的LBL
    private JLabel lblSize;//选择字大小的LBL
    private JLabel lblColor;//选择Color的label
    private JTextField txtFont;//显示选择字体的TEXT
    private JTextField txtStyle;//显示选择字型的TEXT
    private JTextField txtSize;//显示选择字大小的TEXT
    private JList lstFont;//选择字体的列表.
    private JList lstStyle;//选择字型的列表.
    private JList lstSize;//选择字体大小的列表.
    private JComboBox cbColor;//选择Color的下拉框.
    private JButton ok, cancel;//"确定","取消"按钮.
    private JScrollPane spFont;
    private JScrollPane spSize;
    private JLabel lblShow;//显示效果的label.
    private JPanel showPan;//显示框.
    private Map<String, Integer> sizeMap;//字号映射表.
    private Map<String, Color> colorMap;//字着色映射表.
    
    //定义变量_结束________________
    public JFontChooser(MyFont curFont) {
    	//实例化变量
    	lblFont = new JLabel("字体:");
    	lblStyle = new JLabel("字型:");
    	lblSize = new JLabel("大小:");
    	lblColor = new JLabel("颜色:");
    	lblShow = new JLabel("Sample Test!", JLabel.CENTER);
    	txtFont = new JTextField("宋体");
    	txtStyle = new JTextField("常规");
    	txtSize = new JTextField("9");
    	//取得当前环境可用字体.
    	GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    	String[] fontNames = ge.getAvailableFontFamilyNames();
    	lstFont = new JList(fontNames);
    	//字形.
    	lstStyle = new JList(new String[]{"常规", "斜休", "粗休", "粗斜休"});
    	//字号.
    	String[] sizeStr = new String[]{
			"8", "9", "10", "11", "12", "14", "16", "18", "20", "22", "24", "26", "28", "36", "48", "72",
			"初号", "小初", "一号", "小一", "二号", "小二", "三号", "小三", "四号", "小四", "五号", "小五", "六号", "小六", "七号", "八号"
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
    			"黑色", "蓝色", "深灰", "灰色", "绿色", "浅灰", "洋红", "桔黄", "粉红", "红色", "白色", "黄色"
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
    	ok = new JButton("确定");
    	cancel = new JButton("取消");
    	//实例化变量_结束
    	
    	//布局控件
    	this.setLayout(null);//不用布局管理器.     
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
    	
    	showPan.setBorder(javax.swing.BorderFactory.createTitledBorder("示例"));
    	add(showPan);
    	showPan.setBounds(150, 170, 230, 100);
    	showPan.setLayout(new BorderLayout());
    	lblShow.setBackground(Color.white);
    	showPan.add(lblShow);
    	add(ok);
    	ok.setBounds(10, 240, 60, 20);
    	add(cancel);
    	cancel.setBounds(80, 240, 60, 20);
    	//布局控件_结束
    	
    	//事件
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
    			if (value.equals("常规")) {
    				current_fontStyle = Font.PLAIN;
    			}
    			if (value.equals("斜休")) {
    				current_fontStyle = Font.ITALIC;
    			}
    			if (value.equals("粗休")) {
    				current_fontStyle = Font.BOLD;
    			}
    			if (value.equals("粗斜休")) {
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
    	//事件_结束
    	
    	if(curFont != null){
    		if(curFont.getFamilyIndex() != -1) {
    			lstFont.setSelectedIndex(curFont.getFamilyIndex());
    			lstFont.ensureIndexIsVisible(curFont.getFamilyIndex());
    		} else {
    			lstFont.setSelectedValue("宋体", true);
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
    //字体名称索引
    private int familyIndex = -1;
    //字体大小索引
    private int sizeIndex = -1;
    //字体颜色索引
    private int colorIndex = -1;
    //字体风格索引
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
