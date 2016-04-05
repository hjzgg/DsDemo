package com.ds.tools;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MyClassLoader extends ClassLoader {
    //类加载器名称
    private String loaderName;
    //加载类的路径
    private String path = "";
    private final String fileType = ".class";
    public MyClassLoader(String loaderName){
        //让系统类加载器成为该 类加载器的父加载器
        super();
        this.loaderName = loaderName;
    }

    public MyClassLoader(ClassLoader parent, String loaderName){
        //显示指定该类加载器的父加载器
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
     * 获取.class文件的字节数组
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
     * 获取Class对象
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

