package com.chd.biaoqingbao.main;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.chd.biaoqingbao.constans.CharsetName;
import com.chd.biaoqingbao.constans.Constans;

public class MainTest {
	
	public static void main(String[] args) throws Exception {
		URL url = new URL(Constans.PATH);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		int code = connection.getResponseCode();
		String message = connection.getResponseMessage();
		if(isSuccess(code) && checkSuccess(message)) {
			InputStream inputStream = connection.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, CharsetName.UTF_8));
			String readLine = null;
			StringBuffer html = new StringBuffer();
			while((readLine = br.readLine()) != null) {
				html.append(readLine);
			}
		}
	}
	
	public static boolean isSuccess(int code) {
		return code == Constans.SUCCESS;
	}
	
	public static boolean checkSuccess(String message) {
		return message != null && message.equals(Constans.MESSAGE);
	}
}
