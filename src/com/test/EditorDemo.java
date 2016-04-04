package com.test;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import sun.font.FontDesignMetrics;

public class EditorDemo extends JFrame {
	 public static final String MAX_LINE_NUM = "9999";
	 private JTextPane textPane = new JTextPane(); //文本窗格，编辑窗口
	 private JLabel timeStatusBar = new JLabel(); //时间状态栏
	 private JLabel caretStatusBar = new JLabel(); //光标位置状态栏
	 private JFileChooser filechooser = new JFileChooser(); //文件选择器
	 private JPanel linePane = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
	 private int lineNum = 0;
	 private  MyFont myFont = null;
	 
	 private void initTextPaneDocument(){
		 textPane.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent e) {}
			@Override
			public void insertUpdate(final DocumentEvent e) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						DecorateKeyWords.decorateKeyWords(textPane, myFont);
					}
				}).start();
			}
			@Override
			public void removeUpdate(DocumentEvent e) {
				new Thread(new Runnable() {  
					@Override
					public void run() {
						String text;
						try {
							text = textPane.getDocument().getText(0, textPane.getDocument().getLength()).replaceAll("\\r", "");
							Pattern pattern = Pattern.compile("\\n");
							Matcher matcher = pattern.matcher(text);
							int lineRow = 1;
							while(matcher.find()){
								//计算行数
								++lineRow;
							}
							while(lineRow < linePane.getComponentCount()) {
								--lineNum;
								linePane.remove(linePane.getComponentCount()-1);
							}
							linePane.updateUI();
							
						} catch (BadLocationException ex) {
							ex.printStackTrace();
						} finally {
							DecorateKeyWords.decorateKeyWords(textPane, myFont);
						}
					}
				}).start();
			}
		});
	 }
	 
	 public EditorDemo() { //构造函数
		 super("简单的文本编辑器");  //调用父类构造函数
		 
		 //初始字体
		 myFont = new MyFont();
		 myFont.setColor(Color.black);
		 myFont.setFont(new Font("宋体", Font.PLAIN, 24));
		 myFont.setSizeIndex(19);
		 myFont.setStyleIndex(0);
		 myFont.setColorIndex(0);
		 
		 Action[] actions =  //Action数组,各种操作命令
	     {
		    new NewAction(),
		    new OpenAction(),
		    new SaveAction(),
		    new CutAction(),
		    new CopyAction(),
		    new PasteAction(),
		    new NewFontStyle(),
		    new AboutAction(),
		    new ExitAction()
		  };
		 textPane.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				super.keyPressed(e);
				if(e.getKeyCode() == KeyEvent.VK_ENTER) {
					//添加新的行号
					addLineNum();
				}
			}
		 });
		 
		 textPane.addCaretListener(new CaretListener() {
			@Override
			public void caretUpdate(CaretEvent e) {
				 try {
					String text = textPane.getDocument().getText(0, e.getDot()).replaceAll("\\r", "");
					Pattern pattern = Pattern.compile("\\n");
					Matcher matcher = pattern.matcher(text);
					int lineRow = 1;
					int lastLineBeginPos = -1;//记录文本中最后一行的开始的位置 
					while(matcher.find()){
						//计算行数
						++lineRow;
						lastLineBeginPos = matcher.start();//得到下一行光标所在的位置（根据上一行的换行符）
					}
					int lineCol = e.getDot() - lastLineBeginPos;
					//显示行号和列号
					caretStatusBar.setText("光标 " + lineRow + " : " + lineCol);
				 } catch (BadLocationException ey) {
				 	ey.printStackTrace();
				 }
			}
		 });
		 initTextPaneDocument();

		 setJMenuBar(createJMenuBar(actions));  //设置菜单栏
		 add(createJToolBar(actions), BorderLayout.NORTH); //增加工具栏
		 JPanel textBackPanel = new JPanel(new BorderLayout());
		 textBackPanel.add(linePane, BorderLayout.WEST);//增加行号面板
		 textBackPanel.add(textPane, BorderLayout.CENTER);//增加文本面板
		 add(new JScrollPane(textBackPanel), BorderLayout.CENTER); //文本窗格嵌入到JscrollPane
		 JPanel statusPane = new JPanel(new FlowLayout(FlowLayout.LEFT, 50, 0));
		 statusPane.add(caretStatusBar);
		 statusPane.add(timeStatusBar);
		 //初始化光标位置
		 caretStatusBar.setText("光标 1 : 1");
		 //初始化系统时间显示
		 new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				Date now = new Date(); 
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//可以方便地修改日期格式
				timeStatusBar.setText(dateFormat.format(now)); 
			}
		}, 0, 1000);
		 add(statusPane, BorderLayout.SOUTH); //增加状态栏
		 
		 FontMetrics fm = FontDesignMetrics.getMetrics(myFont.getFont());
		 //设置光标的大小
		 textPane.setFont(myFont.getFont());
		 //设置行数面板的宽度
		 linePane.setPreferredSize(new Dimension(fm.stringWidth(MAX_LINE_NUM), 0));
		 addLineNum();
		
		 setBounds(200, 100, 800, 500); //设置窗口尺寸
		 setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  //关闭窗口时退出程序
		 setVisible(true);  //设置窗口可视
	 }
	 
	 private void addLineNum(){
		 //为textPane添加行号
		 String numText = String.valueOf(++lineNum);
		 int tmpNum = MAX_LINE_NUM.length() - (int)(Math.log10(lineNum*1.0)+1);
		 String spaces = "";
		 while(tmpNum > 0){
			 spaces += " ";
			 --tmpNum;
		 }
		 JLabel lineLabel = new JLabel(numText.replaceAll("(\\d+)", spaces+"$1"), JLabel.RIGHT);
		 lineLabel.setForeground(Color.GRAY);
		 lineLabel.setFont(myFont.getFont());
		 linePane.add(lineLabel);
		 linePane.updateUI();
	 }

	 private JMenuBar createJMenuBar(Action[] actions) {  //创建菜单栏
		 JMenuBar menubar = new JMenuBar(); //实例化菜单栏
		 JMenu menuFile = new JMenu("文件"); //实例化菜单
		 JMenu menuEdit = new JMenu("编辑");
		 JMenu menuAbout = new JMenu("帮助");
		 menuFile.add(new JMenuItem(actions[0])); //增加新菜单项
		 menuFile.add(new JMenuItem(actions[1]));
		 menuFile.add(new JMenuItem(actions[2]));
		 menuFile.add(new JMenuItem(actions[7]));
		 menuEdit.add(new JMenuItem(actions[3]));
		 menuEdit.add(new JMenuItem(actions[4]));
		 menuEdit.add(new JMenuItem(actions[5]));
		 menuAbout.add(new JMenuItem(actions[6]));
		 menubar.add(menuFile); //增加菜单
		 menubar.add(menuEdit);
		 menubar.add(menuAbout);
		 return menubar; //返回菜单栏
	 }

	 private JToolBar createJToolBar(Action[] actions) { //创建工具条
		 JToolBar toolBar = new JToolBar(); //实例化工具条
		 for (int i = 0; i < actions.length; i++) {
			 JButton bt = new JButton(actions[i]); //实例化新的按钮
			 bt.setRequestFocusEnabled(false); //设置不需要焦点
			 toolBar.add(bt); //增加按钮到工具栏
		 }
		 return toolBar;  //返回工具栏
	 }
	 
	 class NewFontStyle extends AbstractAction{
		public NewFontStyle() {
			 super("字体");
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			JFontChooser one = new JFontChooser(myFont);
	        MyFont tmpFont = one.showDialog(null, "字体选择器", textPane.getLocationOnScreen().x, textPane.getLocationOnScreen().y); 
	        if(tmpFont == null) return;
	        myFont = tmpFont;
	        //重新设置 textPane的字体，改变光标的大小
	        textPane.setFont(myFont.getFont());
	        FontMetrics fm = FontDesignMetrics.getMetrics(myFont.getFont());
	        //重新设置数字行数面板的宽度
	        linePane.setPreferredSize(new Dimension(fm.stringWidth(MAX_LINE_NUM), 0));
	        //重新设置行号的字体
	        for(int i=0; i < linePane.getComponentCount(); ++i)
	        	linePane.getComponent(i).setFont(myFont.getFont());
	        
	        StyledDocument doc = textPane.getStyledDocument();  
		  	SimpleAttributeSet wordAttr = new SimpleAttributeSet(); 
		  	DecorateKeyWords.decorateStyleConstants(wordAttr, myFont.getFont());
		  	doc.setCharacterAttributes(0, doc.getLength(), wordAttr, false);
		}
	 }

	 class NewAction extends AbstractAction { //新建文件命令
		 public NewAction() {
			 super("新建");
		 }
		 public void actionPerformed(ActionEvent e) {
			 textPane.setDocument(new DefaultStyledDocument()); //清空文档
			 while(linePane.getComponentCount() > 1)
				 linePane.remove(linePane.getComponent(linePane.getComponentCount()-1));
			 linePane.updateUI();
			 lineNum = 1;
			 initTextPaneDocument();
		 }
	 }

	 class OpenAction extends AbstractAction { //打开文件命令
		  public OpenAction() {
			  super("打开");
		  }
		  public void actionPerformed(ActionEvent e) {
			  int i = filechooser.showOpenDialog(EditorDemo.this); //显示打开文件对话框
			  if (i == JFileChooser.APPROVE_OPTION) { //点击对话框中打开选项
				  File f = filechooser.getSelectedFile(); //得到选择的文件
				  try {
					  InputStream is = new FileInputStream(f); //得到文件输入流
					  textPane.read(is, "d"); //读入文件到文本窗格
					  is.close();
					  is = new FileInputStream(f);
					  LineNumberReader lnr = new LineNumberReader(new InputStreamReader(is));
					  lnr.skip(Long.MAX_VALUE);
					  int newLineNum = lnr.getLineNumber()+1;
					  lnr.close();
					  if(lineNum < newLineNum){
						  while(lineNum < newLineNum)
							  addLineNum();
					  } else {
						  while(lineNum > newLineNum && lineNum > 1){
							  linePane.remove(linePane.getComponentCount()-1);
							  --lineNum;
						  }
						  linePane.updateUI();
					  }
					  
				  } catch (Exception ex) {
					  ex.printStackTrace();  //输出出错信息
				  }
			  }
			  DecorateKeyWords.decorateKeyWords(textPane, myFont);
			  initTextPaneDocument();
		  }
	 }

	 class SaveAction extends AbstractAction {  //保存命令
		 public SaveAction() {
			 super("保存");
		 }
		 public void actionPerformed(ActionEvent e) {
			 int i = filechooser.showSaveDialog(EditorDemo.this); //显示保存文件对话框
			 if (i == JFileChooser.APPROVE_OPTION) {  //点击对话框中保存按钮
				 File f = filechooser.getSelectedFile(); //得到选择的文件
				 try {
					 FileOutputStream out = new FileOutputStream(f);  //得到文件输出流
					 out.write(textPane.getText().getBytes()); //写出文件    
				 } catch (Exception ex) {
					 ex.printStackTrace(); //输出出错信息
				 }
			 }
		 }
	 }

	 class ExitAction extends AbstractAction { //退出命令
		  public ExitAction() {
			  super("退出");
		  }
		  public void actionPerformed(ActionEvent e) {
			  System.exit(0);  //退出程序
		  }
	 }

	 class CutAction extends AbstractAction {  //剪切命令
	 	 public CutAction() {
	 		 super("剪切");
	 	 }
	 	 public void actionPerformed(ActionEvent e) {
	 		 textPane.cut();  //调用文本窗格的剪切命令
	 	 }
	 }

	 class CopyAction extends AbstractAction {  //拷贝命令
		 public CopyAction() {
			 super("拷贝");
		 }
		 public void actionPerformed(ActionEvent e) {
			 textPane.copy();  //调用文本窗格的拷贝命令
		 }
	 }

	 class PasteAction extends AbstractAction {  //粘贴命令
		 public PasteAction() {
			 super("粘贴");
		 }
		 public void actionPerformed(ActionEvent e) {
			 textPane.paste();  //调用文本窗格的粘贴命令
		 }
	 }

	 class AboutAction extends AbstractAction { //关于选项命令
		 public AboutAction() {
			 super("关于");
		 }
		 public void actionPerformed(ActionEvent e) {
			 JOptionPane.showMessageDialog(EditorDemo.this, "简单的文本编辑器演示"); //显示软件信息
		 }
	 }

	 public static void main(String[] args) {
		 new EditorDemo();
	 }
}

class WordNode {  
	 private int location;  
	 private String word;  
	 public WordNode(int location, String str) {  
		 super();  
		 this.location = location;  
		 this.word = str;  
	 }  
	 public int getLocation() {  
		 return location;  
	 }  
	 public String getWord() {  
		 return word;  
	 }  
}  

class DecorateKeyWords{
	//java中的关键字
	private static final String KEYWORDS[]={"abstract","assert","boolen","break","byte","case","catch","char","class","const",  
         "continue","default","do","double","else","enum","extends","final","finally","float","for",  
         "if","implements","import","instanceof","int","interface","long","native","new","package",  
         "private","protected","public","return","short","static","strictfp","super","switch","synchrpnized",  
         "this","throw","throws","transient","try","void","volatile","while"
    };  
     // 准备关键字     
	private static HashSet<String> keywords = new HashSet<String>(); 
	
	public static void decorateStyleConstants(SimpleAttributeSet attr, Font font){
		StyleConstants.setFontFamily(attr, font.getFamily());
	    StyleConstants.setFontSize(attr, font.getSize());
	    switch(font.getStyle()) {
	    	case Font.BOLD :
	    		StyleConstants.setBold(attr, true);
	    		break;
	    	case Font.ITALIC :
	    		StyleConstants.setItalic(attr, true);
	    		break;
	    	case Font.PLAIN :
	    		StyleConstants.setItalic(attr, false);
	    		StyleConstants.setBold(attr, false);
	    		break;
	    	case Font.BOLD | Font.ITALIC :
	    		StyleConstants.setItalic(attr, true);
    			StyleConstants.setBold(attr, true);
	    		break;
	    	default :
	    		break;
	    }
	}
	
	public static void decorateKeyWords(JTextPane tp, MyFont myFont) {  
	  //初始化关键字
	  for(int i = 0; i<KEYWORDS.length; i++)  
		 keywords.add(KEYWORDS[i]);
	  // 对所有关键词进行修饰颜色  
	  String text = tp.getText().replaceAll("\\r", "");  
	  StyledDocument doc = tp.getStyledDocument();  
	  SimpleAttributeSet keyWordAttr = new SimpleAttributeSet();  
	  StyleConstants.setForeground(keyWordAttr, Color.cyan);
	  decorateStyleConstants(keyWordAttr, myFont.getFont());
	  SimpleAttributeSet otherWordAttr = new SimpleAttributeSet();  
	  StyleConstants.setForeground(otherWordAttr, myFont.getColor());
	  decorateStyleConstants(otherWordAttr, myFont.getFont());
	  ListIterator<WordNode> iterator1 = split(text, "\\s|\\{|\\}|\\(|\\)|\\<|\\>|\\.|\\n");  
	  while (iterator1.hasNext()) {  
		  WordNode wn = iterator1.next(); 
		  if (keywords.contains(wn.getWord())) { 
			  doc.setCharacterAttributes(wn.getLocation(), wn.getWord().length(), keyWordAttr, true);  
		  } else {
			  doc.setCharacterAttributes(wn.getLocation(), wn.getWord().length(), otherWordAttr, true);
		  }
	  }  
	  // 对注释行进行修饰不同的颜色  
	  SimpleAttributeSet annotationAttr = new SimpleAttributeSet();  
	  StyleConstants.setForeground(annotationAttr, Color.green); 
	  decorateStyleConstants(annotationAttr, myFont.getFont());
	  ListIterator<WordNode> iterator2 = split(text, "\\n");  
	  boolean exegesis = false; // 是否加了/*的注释  
	  while (iterator2.hasNext()) {  
		  WordNode wn = iterator2.next();  
		  if (wn.getWord().startsWith("//")) {  
			  doc.setCharacterAttributes(wn.getLocation(), wn.getWord()  
					  .length(), annotationAttr, true);  
		  } else if (wn.getWord().startsWith("/*")  && wn.getWord().endsWith("*/")) {  
			  doc.setCharacterAttributes(wn.getLocation(), wn.getWord()  
					  .length(), annotationAttr, true);  
		  } else if (wn.getWord().startsWith("/*")  && !wn.getWord().endsWith("*/")) {  
			  exegesis = true;  
			  doc.setCharacterAttributes(wn.getLocation(), wn.getWord()  
					  .length(), annotationAttr, true);  
		  } else if (!wn.getWord().startsWith("/*") && wn.getWord().endsWith("*/") && true == exegesis) {  
			  doc.setCharacterAttributes(wn.getLocation(), wn.getWord()  
					  .length(), annotationAttr, true);  
			  exegesis = false;  
		  } else if (true == exegesis) {  
			  doc.setCharacterAttributes(wn.getLocation(), wn.getWord()  
					  .length(), annotationAttr, true);  
		  }  
	  	}  
	 }  
	 
	 /**  
	   * 按照指定的多个字符进行字符串分割，如‘ ’或‘,’等  
	  * @param str   
	  *            被分割的字符串  
	  * @param regexs  
	  *            要分割所需的符号  
	  * @return 包含WordNodeList对象的iterator  
	  */  
	 private static ListIterator<WordNode> split(String str,String regex) {  
		 Pattern p = Pattern.compile(regex);  
		 Matcher m = p.matcher(str); 
		 List<WordNode> nodeList = new ArrayList<WordNode>();  
		 int strStart = 0; // 分割单词的首位置  
		 String s; // 分割的单词  
		 WordNode wn; // StringNode节点  
		 while (m.find()) {  
			 s = str.substring(strStart, m.start());  
			 if (!s.equals(new String())) {  
				 wn = new WordNode(strStart, s);  
				 nodeList.add(wn);  
			 }  
			 strStart = m.start() + 1;  
		 }  
		 s = str.substring(strStart, str.length());  
		 wn = new WordNode(strStart, s);  
		 nodeList.add(wn);  
		 return nodeList.listIterator();  
	 }  
}
