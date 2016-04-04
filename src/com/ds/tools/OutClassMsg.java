package com.ds.tools;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class OutClassMsg{
    
    /*获取类的声明*/
    private String getClassStatement(Class<?> c){
        StringBuffer buf = new StringBuffer();
        if(c.getName().equals("java.lang.Object")){
            buf.append("public class Object {");
            return buf.toString();
        } else {
            //得到该类的父类名
            String superName = c.getSuperclass().getName();
            buf.append("public class ").append(c.getName()).append(" extends ").append(superName).append(" {");
            return buf.toString();
        }
    }
    
    /*获取类的属性*/
    private String getFields(Class<?> c){
        StringBuffer buf = new StringBuffer();
        Field[] fields = c.getDeclaredFields();
        for(Field field : fields){
            //获取属性的访问修饰符
            //Modifier的一些信息 http://www.it165.net/pro/html/201309/7203.html
            buf.append("    ").append(Modifier.toString(field.getModifiers())).append(" ");
            Class<?> type = field.getType();
            buf.append(type.getName()).append(" ");
            buf.append(field.getName()).append(";\n");
        }
        return buf.toString();
    }
    
    /*获取类的所有构造方法*/
    private String getConstructors(Class<?> c){
        StringBuffer buf = new StringBuffer();
        //获取类的构造方法
        Constructor<?>[] cons = c.getDeclaredConstructors();
        for(Constructor<?> con : cons){
            //获取构造方法的访问修饰符
            buf.append("    ").append(Modifier.toString(con.getModifiers())).append(" ");
            //获取构造方法的名称
            buf.append(con.getName()).append("(");
            //获取构造方法的参数
            Class<?>[] paramType =  con.getParameterTypes();
            for(int i=0; i<paramType.length; ++i){
                if(i != 0){
                    buf.append(paramType[i].getName());
                } else {
                    buf.append(", ").append(paramType[i].getName());
                }
            }
            buf.append(")");
            
            //获取方法声明的异常
            Class<?>[] excepTypes = con.getExceptionTypes();
            for(int i=0; i<excepTypes.length; ++i){
                if(i==0){
                    buf.append(" throws ").append(excepTypes[i].getName());
                } else {
                    buf.append(", ").append(excepTypes[i].getName());
                }
            }
            buf.append(";\n");
        }
        return buf.toString();
    }
    
    private String getMethods(Class<?> c){
        StringBuffer buf = new StringBuffer();
        Method[] methods = c.getDeclaredMethods();
        for(Method method : methods){
            //获取方法的访问修饰符
            buf.append("    ").append(Modifier.toString(method.getModifiers())).append(" ");
            //获取方法的返回类型
            Class<?> returnType = method.getReturnType();
            buf.append(returnType.getName()).append(" ");
            buf.append(method.getName()).append(" (");//获取方法的名称
            
            //获取方法的参数类型
            Class<?>[] paramTypes = method.getParameterTypes();
            for(int i=0; i<paramTypes.length; ++i){
                if(i==0){
                    buf.append(paramTypes[i].getName());
                } else {
                    buf.append(", ").append(paramTypes[i].getName());
                }
            }
            buf.append(")");
            
            //获取方法声明的异常
            Class<?>[] excepTypes = method.getExceptionTypes();
            for(int i=0; i<excepTypes.length; ++i){
                if(i==0){
                    buf.append(" throws ").append(excepTypes[i].getName());
                } else {
                    buf.append(", ").append(excepTypes[i].getName());
                }
            }
            buf.append(";\n");
        }
        return buf.toString();
    }
    
    public void getClassMessage(Class<?> c){
        StringBuffer buf = new StringBuffer();
        try {
            buf.append("/*类的声明*/\n");
            buf.append(getClassStatement(c));
            buf.append("\n");
            buf.append("    /*字段*/\n");
            buf.append(getFields(c));
            buf.append("    /*构造器*/\n");
            buf.append(getConstructors(c));
            buf.append("    /*方法*/\n");
            buf.append(getMethods(c));
            buf.append("}\n");
            System.out.println(buf.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
