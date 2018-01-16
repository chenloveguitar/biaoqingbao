package com.chd.biaoqingbao.main;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {

	public static void main(String[] args) {
//		String html = "<img src=\"/wp-content/uploads/2017/04/1419.png\" alt=\"你们用了我的作品不给钱\" width=\"150\" height=\"150\" border=\"0\">";
//		String exp = "<img src=\"([\\/?\\w?]+\\.png|gif|jpeg|jpg)\" alt=\"([\\u4e00-\\u9fa5]*)\" width=\"150\" height=\"150\" border=\"0\">";
		
		
		String html = "<img src=\"/wp-content/uploads/2017/04/1419.png\" alt=\"你们用了我的作品不给钱\" width=\"150\" height=\"150\" border=\"0\">";
		String exp = "<img src=\"([\\/?\\w?\\-?]+\\.(png|jpg|jpeg|gif))\" alt=\"([\\u4e00-\\u9fa5]*)\" width=\"150\" height=\"150\" border=\"0\">";
		Pattern pattern = Pattern.compile(exp);
		Matcher matcher = pattern.matcher(html.toString());
		while (matcher.find()) {
			String link = matcher.group(1);
//			String word = matcher.group(2);
			System.out.println(link);
//			System.out.println(word);
		}
	}
}
