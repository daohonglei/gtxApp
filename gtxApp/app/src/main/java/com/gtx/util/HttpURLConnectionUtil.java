package com.gtx.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Set;

public class HttpURLConnectionUtil {
	public String request(String urlPath, Map<String,String> map)throws  RuntimeException{
		System.out.println("HttpURLConnectionUtil>>map-------------------------------------"+map);
		PrintWriter writer=null;
		String response="";
		BufferedReader responseReader=null;
		try {
			String param="";
			Set<String> set=map.keySet();
			for (String key : set) {
				String str= URLEncoder.encode(map.get(key),"UTF-8");
				param+=key+"="+str+"&";
			}

			System.out.println("HttpURLConnectionUtil>>param-------------------------------------"+param);
			
			URL url=new URL(urlPath);
			HttpURLConnection httpConn=(HttpURLConnection)url.openConnection();
			 
			//设置参数
			//httpConn.setConnectTimeout(6000); //超时时长
			httpConn.setDoOutput(true);     //需要输出
			httpConn.setDoInput(true);      //需要输入
			httpConn.setUseCaches(false);   //不允许缓存
			httpConn.setRequestMethod("POST");      //设置POST方式连接
			 
			//设置请求属性
			httpConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			httpConn.setRequestProperty("Connection", "Keep-Alive");// 维持长连接
			httpConn.setRequestProperty("Charset", "UTF-8");
			 
			//连接,也可以不用明文connect，使用下面的httpConn.getOutputStream()会自动connect
			httpConn.connect();
			     
			//建立输入流，向指向的URL传入参数
			writer=new PrintWriter(httpConn.getOutputStream());
			writer.println(param);
			writer.flush();
			
			 //获得响应状态
	        int resultCode=httpConn.getResponseCode();
	        if(HttpURLConnection.HTTP_OK==resultCode){
	            String readLine;
				responseReader=new BufferedReader(new InputStreamReader(httpConn.getInputStream(),"UTF-8"));
	          	while((readLine=responseReader.readLine())!=null){
					response+=readLine+"\n";
	            }
	        }
		} finally{
			try {
				writer.close();
				if(responseReader!=null)responseReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}finally{
				return response.toString();
			}
		}
	}
	/*public static void main(String[] args) {
		Map<String,String> map=new HashMap<>();
		map.put("userId","962");
		map.put("curpage","1");
		String url="http://www.gzgtx.com:8992/Hitachi_TicketAppServlet/Servlet_ServiceList";
		String string=new HttpURLConnectionUtil().request(url,map);
		System.out.println(string);
	}*/
}
