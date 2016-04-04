package com.ds.tools;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class OutClassMsg{
    
    /*��ȡ�������*/
    private String getClassStatement(Class<?> c){
        StringBuffer buf = new StringBuffer();
        if(c.getName().equals("java.lang.Object")){
            buf.append("public class Object {");
            return buf.toString();
        } else {
            //�õ�����ĸ�����
            String superName = c.getSuperclass().getName();
            buf.append("public class ").append(c.getName()).append(" extends ").append(superName).append(" {");
            return buf.toString();
        }
    }
    
    /*��ȡ�������*/
    private String getFields(Class<?> c){
        StringBuffer buf = new StringBuffer();
        Field[] fields = c.getDeclaredFields();
        for(Field field : fields){
            //��ȡ���Եķ������η�
            //Modifier��һЩ��Ϣ http://www.it165.net/pro/html/201309/7203.html
            buf.append("    ").append(Modifier.toString(field.getModifiers())).append(" ");
            Class<?> type = field.getType();
            buf.append(type.getName()).append(" ");
            buf.append(field.getName()).append(";\n");
        }
        return buf.toString();
    }
    
    /*��ȡ������й��췽��*/
    private String getConstructors(Class<?> c){
        StringBuffer buf = new StringBuffer();
        //��ȡ��Ĺ��췽��
        Constructor<?>[] cons = c.getDeclaredConstructors();
        for(Constructor<?> con : cons){
            //��ȡ���췽���ķ������η�
            buf.append("    ").append(Modifier.toString(con.getModifiers())).append(" ");
            //��ȡ���췽��������
            buf.append(con.getName()).append("(");
            //��ȡ���췽���Ĳ���
            Class<?>[] paramType =  con.getParameterTypes();
            for(int i=0; i<paramType.length; ++i){
                if(i != 0){
                    buf.append(paramType[i].getName());
                } else {
                    buf.append(", ").append(paramType[i].getName());
                }
            }
            buf.append(")");
            
            //��ȡ�����������쳣
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
            //��ȡ�����ķ������η�
            buf.append("    ").append(Modifier.toString(method.getModifiers())).append(" ");
            //��ȡ�����ķ�������
            Class<?> returnType = method.getReturnType();
            buf.append(returnType.getName()).append(" ");
            buf.append(method.getName()).append(" (");//��ȡ����������
            
            //��ȡ�����Ĳ�������
            Class<?>[] paramTypes = method.getParameterTypes();
            for(int i=0; i<paramTypes.length; ++i){
                if(i==0){
                    buf.append(paramTypes[i].getName());
                } else {
                    buf.append(", ").append(paramTypes[i].getName());
                }
            }
            buf.append(")");
            
            //��ȡ�����������쳣
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
            buf.append("/*�������*/\n");
            buf.append(getClassStatement(c));
            buf.append("\n");
            buf.append("    /*�ֶ�*/\n");
            buf.append(getFields(c));
            buf.append("    /*������*/\n");
            buf.append(getConstructors(c));
            buf.append("    /*����*/\n");
            buf.append(getMethods(c));
            buf.append("}\n");
            System.out.println(buf.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
