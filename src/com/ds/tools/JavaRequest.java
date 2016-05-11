package com.ds.tools;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;

import javax.swing.JOptionPane;

import net.sf.json.JSONObject;

public class JavaRequest {
	 private static final String BASE_URL = "http://localhost:8080/dsdemo/";
	 public static String userToken = null;
	 public static String username = null;
	 public static String problemName = null;
	 public static String sendPost(String sufUrl, JSONObject params) {
		DataOutputStream out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(BASE_URL+sufUrl);
            // 打开和URL之间的连接
            HttpURLConnection connection = (HttpURLConnection) realUrl.openConnection();
            connection.setDoOutput(true);  
            connection.setDoInput(true);  
            connection.setUseCaches(false);  
            connection.setInstanceFollowRedirects(true);  
            connection.setRequestMethod("POST"); // 设置请求方式  
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.connect();
            out = new DataOutputStream (connection.getOutputStream());
            // 发送请求参数
            out.write(jsonToUrlParams(params).getBytes("UTF-8"));
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch(ConnectException e){
        	e.printStackTrace();
        	JSONObject jsono = new JSONObject();
        	jsono.put("success", false);
        	jsono.put("message", "无法连接服务器!");
        	result = jsono.toString();
        } catch (Exception e) {
            e.printStackTrace();
            JSONObject jsono = new JSONObject();
            jsono.put("success", false);
            jsono.put("message", "inner error!");
            result = jsono.toString();
        }
        //使用finally块来关闭输出流、输入流
        finally{
            try{
                if(out!=null){
                    out.close();
                }
                if(in!=null){
                    in.close();
                }
            }
            catch(IOException ex){
                ex.printStackTrace();
            }
        }
        System.out.println(result);
        return result;
    }    
	 
	private static String jsonToUrlParams(JSONObject params){
		StringBuilder sb = new StringBuilder();
		sb.append("userToken=").append(userToken);
		for(Iterator<?> it = params.keys(); it.hasNext(); ){
			String key = (String) it.next();
			sb.append("&").append(key).append("=").append(params.get(key));
		}
		return sb.toString();
	}
}
