package com.test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;

public class CompileAndRunJavaFile {
	public static void main(String[] args) {
		StringBuilder code = new StringBuilder();
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File("C:/Users/���/Desktop/Main.java")));
			String content;
			while((content = br.readLine()) != null){
				code.append(content).append("\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		CompileAndRunJavaFile cr = new CompileAndRunJavaFile();
		cr.compileAndRunJavaFile(code.toString());
		if(cr.isCompileAndRunOK()) {
			System.out.println("����ʱ��: " + cr.getUseTime() + "ms");
			System.out.println("�ڴ�ʹ��: " + cr.getUseMemory() + "kb9");
			System.out.println("���н��: \n" + cr.getOutMsg());
		} else if(cr.isCompilerError()) {
			System.out.println("�������: " + cr.getCE());
		} else if(cr.isRunningError()) {
			System.out.println("���д���: " + cr.getError());
		}
	}
	//�������
	private StringBuilder ce = new StringBuilder();
	public String getCE(){
		return ce.toString();
	}
	
	//�ڴ�ʹ��
	private double useMemory = 0.0;
	public double getUseMemory(){
		return useMemory;
	}
	
	//����ʱ��
	private long useTime = 0;
	public long getUseTime(){
		return useTime;
	}
	//�����Ϣ
	private StringBuilder outMsg = new StringBuilder();
	public String getOutMsg(){
		return outMsg.toString();
	}
	//�쳣��Ϣ
	private String error = null;
	public String getError(){
		return error;
	}
	//�Ƿ��������벢����
	private boolean isCompileAndRunOK = false; 
	
	public boolean isCompileAndRunOK(){
		return isCompileAndRunOK;
	}
	
	//���������ʱ��, ��λ��ms
	private int limitTime = 2000;
	//������ռ�ڴ�, ��λ ��KB
	private double limitMemory = 256000.0;
	
	public void setLimitTime(int limitTime){
		this.limitTime = limitTime;
	}
	
	public void setLimitMemory(double limitMemory){
		this.limitMemory = limitMemory;
	}
	
	//�Ƿ�Ϊ�������
	private boolean isCompilerError = false;
	public boolean isCompilerError(){
		return isCompilerError;
	}
	
	//�Ƿ�Ϊ���д���
	private boolean isRunningError = false;
	public boolean isRunningError(){
		return isRunningError;
	}
	
	private static final String className = "Main";
	private static final String methodName = "main";
	private String getClassOutput(){
		//����class�ļ��Ĵ��λ��
		if(System.getProperty("java.class.path").contains("bin")) return "bin/";
		else return "./";
	}
	
	private void compileAndRunJavaFile(String code){
		PrintStream ps = null;
		FileInputStream fis = null;
		BufferedReader br = null;
		//�����׼�����
		InputStream stdIn = System.in;
		//�����׼������
		PrintStream stdOut = System.out;
		
		//ΪԴ���뵼��Ĭ�ϵİ�
		code = "import java.util.*;\n" + code;
		try {
			JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
			// define the diagnostic object, which will be used to save the
	        // diagnostic information
	        DiagnosticCollector<JavaFileObject> oDiagnosticCollector = new DiagnosticCollector<JavaFileObject>();
			StandardJavaFileManager fileManager = compiler.getStandardFileManager(oDiagnosticCollector, null, null);
			// set class output location
			fileManager.setLocation(StandardLocation.CLASS_OUTPUT, Arrays.asList(new File[] { new File(getClassOutput()) }));
			
			StringSourceJavaObject sourceObject = new CompileAndRunJavaFile.StringSourceJavaObject(className, code);
			Iterable<? extends JavaFileObject> fileObjects = Arrays.asList(sourceObject);
			CompilationTask task = compiler.getTask(null, fileManager, oDiagnosticCollector, null, null, fileObjects);
			boolean result = task.call();
			
			if (result) {
				Runtime runtime = Runtime.getRuntime();
		    	Class<?> clazz = Class.forName(className);
		    	Method method = clazz.getMethod(methodName, new Class<?>[]{String[].class});
		    	
		    	//������������ ��Ҫ��������ļ����ļ���
		    	fis = new FileInputStream(new File("./data/data1.txt"));
				System.setIn(fis);
				//�������������Ҫ��ÿ���̨�����
				ByteArrayOutputStream bao = new ByteArrayOutputStream();
				ps = new PrintStream(bao);
				System.setOut(ps);
				
		    	long startFreeMemory = runtime.freeMemory();//Java ������еĿ����ڴ���
				//ִ��ʱ��Ҳ���޷�֪������Ϊdosִ��java��������޷�֪��������ִ�е������ˣ��������̣������˽�
				long startCurrentTime = System.currentTimeMillis();//��ȡϵͳ��ǰʱ��
		    	method.invoke(null, new Object[]{null});
		    	long endCurrentTime = System.currentTimeMillis();
				long endFreeMemory = runtime.freeMemory();
				//�ڴ��ʹ����������Ǻܾ�ȷ
				useMemory = (startFreeMemory-endFreeMemory)/1024.0;
				if(useMemory > limitMemory) throw new Exception("Out Limit Memory!");
				useTime = endCurrentTime-startCurrentTime;
				if(useTime > limitTime) throw new Exception("Time Limited!");
				
		    	//��ÿ���̨�����
		    	br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(bao.toByteArray())));
		    	String outc = null;
		    	while((outc = br.readLine()) != null)
		    		outMsg.append(outc).append("\n");
		    	//�������벢����
		    	isCompileAndRunOK = true;
			} else {
				isCompilerError = true;
				//��ӡ����Ĵ�����Ϣ
				Pattern p = Pattern.compile("Main.java\\D*(\\d+):", Pattern.DOTALL);
				for (Diagnostic<? extends JavaFileObject> oDiagnostic : oDiagnosticCollector.getDiagnostics()){
					/*��Ϣʾ����
					  Compiler Error: Main.java:8: �Ҳ�������
					     ���ţ� �� Scanner
				                λ�ã� �� Main
					*/
					//���кż�1
					Matcher m = p.matcher("Compiler Error: " + oDiagnostic.getMessage(null));
					if(m.find()) {
						ce.append(m.replaceAll("Main.java " + String.valueOf(Integer.valueOf(m.group(1))-1)) + ":").append("\n");
					} else {
						ce.append("Compiler Error: " + oDiagnostic.getMessage(null)).append("\n");
					}
				}
			}
			
		} catch (Exception e) {
			isRunningError = true;
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw, true));
			Pattern p = Pattern.compile("Main.java\\D*(\\d+)", Pattern.DOTALL);
			Matcher m = p.matcher(sw.toString());
			if(m.find()){
				error = m.replaceAll("Main.java " + String.valueOf(Integer.valueOf(m.group(1))-1) + ":");
			} else {
				error = sw.toString();
			}
		} finally {
			//�ر���
			try {
				if(fis != null)
					fis.close();
				if(ps != null)
					ps.close();	
				if(br != null)
					br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			//�ָ����������
	    	System.setIn(stdIn);
	    	System.setOut(stdOut);
		}
	}
	
	private class StringSourceJavaObject extends SimpleJavaFileObject {
		private String content = null;
		public StringSourceJavaObject(String name, String content) {
			super(URI.create(name.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
			this.content = content;
		}
		public CharSequence getCharContent(boolean ignoreEncodingErrors) {
			return content;
		}
	}
}
