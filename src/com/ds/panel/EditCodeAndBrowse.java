package com.ds.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JTextPane;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.ds.controler.TreeMenuControler;
import com.ds.panel.EditCodePane.NewAction;
import com.ds.tools.CompileAndRunJavaFile;
import com.ds.tools.MyTimer;

import sun.font.FontDesignMetrics;

//代码编辑 以及 题目浏览 的面板
public class EditCodeAndBrowse extends JPanel{
	private BrowsePane browsePane = new BrowsePane();
	private EditCodePane editCodePane = new EditCodePane();
	private JTextPane resultPane = new JTextPane();
	private JTextPane rightDataInputPane = new JTextPane();
	private JTextPane rightDataOuputPane = new JTextPane();
	private String selectTest = null;
	public  EditCodeAndBrowse(){
		setLayout(new GridLayout(1, 1));
		JSplitPane hSplitePane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		hSplitePane.setContinuousLayout(true);
		hSplitePane.setOneTouchExpandable(true);
		hSplitePane.setDividerSize(10);
		hSplitePane.setResizeWeight(0.5);
		
		JSplitPane vSplitePane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		vSplitePane.setContinuousLayout(true);
		vSplitePane.setOneTouchExpandable(true);
		vSplitePane.setDividerSize(10);
		JTabbedPane tabPane = new JTabbedPane(JTabbedPane.TOP);
		resultPane.setEditable(false);
		rightDataInputPane.setEditable(false);
		rightDataOuputPane.setEditable(false);
		tabPane.addTab("运行结果", null, new JScrollPane(resultPane), "Run Result");
		tabPane.addTab("输入数据", null, new JScrollPane(rightDataInputPane), "Input Data");
		tabPane.addTab("输出数据", null, new JScrollPane(rightDataOuputPane), "Out Data");
		vSplitePane.setTopComponent(browsePane);
		vSplitePane.setBottomComponent(tabPane);
		vSplitePane.setResizeWeight(0.85);
		
		hSplitePane.setLeftComponent(vSplitePane);
		hSplitePane.setRightComponent(editCodePane);
		//提交按钮 的监听事件
		editCodePane.getSubmitBtn().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(selectTest == null) return;
				//得到源程序代码
				String code = editCodePane.getTextPane().getText();
				//实例化编译器
				CompileAndRunJavaFile cr = new CompileAndRunJavaFile();
				cr.compileAndRunJavaFile(code, selectTest);
				if(cr.isCompileAndRunOK()) {
					resultPane.setText("运行时间: " + cr.getUseTime() + "ms\n" +
							"内存使用: " + cr.getUseMemory() + "kb\n" +
							"运行结果: " + cr.getOutMsg());
				} else if(cr.isCompilerError()) {
					resultPane.setText("编译错误: " + cr.getCE());
				} else if(cr.isRunningError()) {
					resultPane.setText("运行错误: " + cr.getError());
				}  
			}
		});
		
		//左侧为 图形菜单， 右侧是hSplitePane
		JSplitPane hxSplitePane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		hxSplitePane.setContinuousLayout(true);
		hxSplitePane.setOneTouchExpandable(true);
		hxSplitePane.setDividerSize(10);
		hxSplitePane.setResizeWeight(0.02);
		//树形菜单
		JTree menuTree = TreeMenuControler.getTreeMenu();
		hxSplitePane.setLeftComponent(new JScrollPane(menuTree));
		hxSplitePane.setRightComponent(hSplitePane);
		//添加选择监听器
		menuTree.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent event) {
				JTree treeRoot = (JTree)event.getSource(); 
				DefaultMutableTreeNode selectionNode = (DefaultMutableTreeNode)treeRoot.getLastSelectedPathComponent();
				//如果是叶子节点
				if(selectionNode.getChildCount() == 0){
					try {
						ReaderXML readerXml = new ReaderXML();
						ReaderText rederText = new ReaderText();
						String str = readerXml.read("./xmlTemplate/htmlTemplate.xml");
						//确定当前选择的题目
						selectTest = new TreePath(selectionNode.getPath()).toString();
						Object[] args = rederText.read("./tests/" + selectTest + ".txt");
						String content = MessageFormat.format(str, args);
						browsePane.getTextPane().setText(content);
						
						//获取正确的输出结果和输入数据
						BufferedReader br = new BufferedReader(new FileReader(new File("./outs/" + selectTest + ".txt")));
				    	String msg = null;
				    	StringBuilder sb = new StringBuilder();
				    	while((msg = br.readLine()) != null)
				    		sb.append(msg).append("\n");
				    	br.close();
				    	rightDataOuputPane.setText(sb.toString());
				    	sb.delete(0, sb.length());
				    	
				    	br = new BufferedReader(new FileReader(new File("./inputs/" + selectTest + ".txt")));
				    	msg = null;
				    	while((msg = br.readLine()) != null)
				    		sb.append(msg).append("\n");
				    	br.close();
				    	rightDataInputPane.setText(sb.toString());
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
		});
		
		add(hxSplitePane);
	}
}

class BrowsePane extends JPanel{
	private JTextPane textPane = new JTextPane(); 
	
	public JTextPane getTextPane() {
		return textPane;
	}

	public BrowsePane(){
		setLayout(new GridLayout(1, 1));
		textPane.setBackground(Color.WHITE);
		textPane.setEditable(false);
		textPane.setEditorKit(new HTMLEditorKit());
		textPane.setContentType("text/html");
		add(new JScrollPane(textPane));
	}
	
}

class ReaderText{
	public Object[] read(String path){
		Object[] objs = getInsertContents(path).toArray(new Object[]{});
		return objs;
	}
	
	private List<String> getInsertContents(String path){
		ArrayList<String> list = new ArrayList<String>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(path)));
			String content = null;
			StringBuilder sb = new StringBuilder();
			while((content = br.readLine()) != null){
				if("".equals(content)) { //空白行
					list.add(sb.toString());
					sb.delete(0, sb.length());
				} else {
					if(sb.length() > 0)
						sb.append("\n");
					sb.append(content);
				}
			}
			if(sb.length() > 0)
				list.add(sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
}

class ReaderXML {
    public String read(String path){
        String str=null;
        str=reader(path);   
        return str;
    }    
    private String reader(String path){        
        SAXReader reader=new SAXReader();
        String str=null;        
        try {            
            Document d=reader.read(new File(path));
            Element e=d.getRootElement();
            Element htmle=e.element("html");
            str=htmle.asXML();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return str;
    }
}

class EditCodePane extends JPanel{
	 public static final String MAX_LINE_NUM = "9999";
	 private JTextPane textPane = new JTextPane(); //文本窗格，编辑窗口
	 private JLabel timeStatusBar = new JLabel(); //时间状态栏
	 private JLabel caretStatusBar = new JLabel(); //光标位置状态栏
	 private JFileChooser filechooser = new JFileChooser(); //文件选择器
	 private JPanel linePane = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
	 private JButton submitBtn = new JButton("提交代码");
	 private int lineNum = 0;
	 private  MyFont myFont = null;
	 
	 public JButton getSubmitBtn(){
		 return submitBtn;
	 }
	 
	 public JTextPane getTextPane(){
		 return textPane;
	 }
	 
	 private void initTextPaneDocument(){
		 textPane.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent e) {}
			@Override
			public void insertUpdate(final DocumentEvent e) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						adjustLinePane();
						DecorateKeyWords.decorateKeyWords(textPane, myFont);
					}
				}).start();
			}
			@Override
			public void removeUpdate(DocumentEvent e) {
				new Thread(new Runnable() {  
					@Override
					public void run() {
						adjustLinePane();
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
	 
	 public EditCodePane() { //构造函数
		 setLayout(new BorderLayout());
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
		    new AboutAction()
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

		 add(createJToolBar(actions), BorderLayout.NORTH); //增加工具栏
		 JPanel textBackPanel = new JPanel(new BorderLayout());
		 textBackPanel.add(linePane, BorderLayout.WEST);//增加行号面板
		 textBackPanel.add(textPane, BorderLayout.CENTER);//增加文本面板
		 add(new JScrollPane(textBackPanel), BorderLayout.CENTER); //文本窗格嵌入到JscrollPane
		 JPanel statusPane = new JPanel(new FlowLayout(FlowLayout.LEFT, 50, 10));
		 //添加提交按钮
		 statusPane.add(submitBtn);
		 //添加行列信息
		 statusPane.add(caretStatusBar);
		 //添加时间信息
		 statusPane.add(timeStatusBar);
		 
		 //初始化光标位置
		 caretStatusBar.setText("光标 1 : 1");
		 //初始化系统时间显示
		 MyTimer.getTimer().schedule(new TimerTask() {
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


	 private JToolBar createJToolBar(Action[] actions) { //创建工具条
		 JToolBar toolBar = new JToolBar(); //实例化工具条
		 for (int i = 0; i < actions.length; i++) {
			 JButton bt = new JButton(actions[i]); //实例化新的按钮
			 bt.setRequestFocusEnabled(false); //设置不需要焦点
			 toolBar.add(bt); //增加按钮到工具栏
		 }
		 return toolBar;  //返回工具栏
	 }
	 
	 private synchronized void adjustLinePane(){
		 try {
			LineNumberReader lnr = new LineNumberReader(new StringReader(textPane.getText()));
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
		} catch (IOException e) {
			e.printStackTrace();
		}
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
			  int i = filechooser.showOpenDialog(EditCodePane.this); //显示打开文件对话框
			  if (i == JFileChooser.APPROVE_OPTION) { //点击对话框中打开选项
				  File f = filechooser.getSelectedFile(); //得到选择的文件
				  try {
					  InputStream is = new FileInputStream(f); //得到文件输入流
					  textPane.read(is, "d"); //读入文件到文本窗格
					  is.close();
					  adjustLinePane();
					  
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
			 int i = filechooser.showSaveDialog(EditCodePane.this); //显示保存文件对话框
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

	 class CutAction extends AbstractAction {  //剪切命令
	 	 public CutAction() {
	 		 super("剪切");
	 	 }
	 	 public void actionPerformed(ActionEvent e) {
	 		 textPane.cut();  //调用文本窗格的剪切命令
	 		 adjustLinePane();
	 	 }
	 }

	 class CopyAction extends AbstractAction {  //拷贝命令
		 public CopyAction() {
			 super("拷贝");
		 }
		 public void actionPerformed(ActionEvent e) {
			 textPane.copy();  //调用文本窗格的拷贝命令
			 adjustLinePane();
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
			 JOptionPane.showMessageDialog(EditCodePane.this, "简单的文本编辑器演示"); //显示软件信息
		 }
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