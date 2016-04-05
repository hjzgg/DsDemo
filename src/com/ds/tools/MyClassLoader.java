package com.ds.tools;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MyClassLoader extends ClassLoader {
    //�����������
    private String loaderName;
    //�������·��
    private String path = "";
    private final String fileType = ".class";
    public MyClassLoader(String loaderName){
        //��ϵͳ���������Ϊ�� ��������ĸ�������
        super();
        this.loaderName = loaderName;
    }

    public MyClassLoader(ClassLoader parent, String loaderName){
        //��ʾָ������������ĸ�������
        super(parent);
        this.loaderName = loaderName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return this.loaderName;
    }

    /**
     * ��ȡ.class�ļ����ֽ�����
     * @param name
     * @return
     */
    private byte[] loaderClassData(String name){
        InputStream is = null;
        byte[] data = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        name = name.replace(".", "/");
        try {
            is = new FileInputStream(new File(path + name + fileType));
            int c = 0;
            while(-1 != (c = is.read())){
                baos.write(c);
            }
            data = baos.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            try {
            	if(is != null)
            		is.close();
        		if(baos != null)
        			baos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return data;
    }

    /**
     * ��ȡClass����
     */
    @Override
    public Class<?> findClass(String name) throws ClassNotFoundException{
        byte[] data = loaderClassData(name);
        return this.defineClass(name, data, 0, data.length);
    }

    public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, SecurityException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException {
    	for(int i=0; i<5; i++){
	        MyClassLoader loader1 = new MyClassLoader("MyClassLoader");
	        String path = new File(MyClassLoader.getSystemClassLoader().getResource("").getPath()).getParent();
	        loader1.setPath(path+"/myClass/");
	        Class<?> clazz = loader1.loadClass("Main");
    	}
    }
}

