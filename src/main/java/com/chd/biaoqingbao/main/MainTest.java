package com.chd.biaoqingbao.main;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.chd.biaoqingbao.constans.CharsetName;
import com.chd.biaoqingbao.constans.Constans;

public class MainTest {

	private static Logger logger = LogManager.getLogger(MainTest.class);
	
	public static void main(String[] args) throws Exception {
		URL url = new URL(Constans.PATH);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		int code = connection.getResponseCode();
		String message = connection.getResponseMessage();
		StringBuffer html = new StringBuffer();
		if (isSuccess(code) && checkSuccess(message)) {
			InputStream inputStream = connection.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, CharsetName.UTF_8));
			String readLine = null;
			while ((readLine = br.readLine()) != null) {
				html.append(readLine + "\n");
			}
		} else {
			InputStream inputStream = connection.getErrorStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, CharsetName.UTF_8));
			String readLine = null;
			while ((readLine = br.readLine()) != null) {
				html.append(readLine);
			}
		}
//		logger.info("-------------------网页内容------------------");
//		logger.info(html.toString());
//		String aTag = "<a href=\"htpp://www.baidu.com\">表情</a>";
		String urlExp = "((ht|f)tps?):\\/\\/[\\w\\-]+(\\.[\\w\\-]+)+([\\w\\-\\.,@?^=%&:\\/~\\+#]*[\\w\\-\\@?^=%&\\/~\\+#])";
		String pathExp = "[/?\\w?]+";
		String exp = "<a href=\"("+urlExp+"|"+pathExp+")\"\\s?[target=\"_blank\"\\stitle=\"[\\u4e00-\\u9fa5]\"]?>([\\w]?[\\u4e00-\\u9fa5]+)[</a>]?";
		
		Pattern pattern = Pattern.compile(exp);
		Matcher matcher = pattern.matcher(html.toString());
//		System.out.println(html);
		while(matcher.find()) {
			String link = matcher.group(1);
			String word = matcher.group(6);
			
			if(!link.contains("http://")) {
				link = Constans.PATH + link;
			}
			
			if(!link.equals(Constans.PATH)) {
				System.out.println(link);
				System.out.println(word);
			}
//	        System.out.println("start(): "+matcher.start());
//	        System.out.println("end(): "+matcher.end());
	    }
		
		// File imageFile = new File("C:\\Users\\jumili\\Desktop\\huamianmei.jpg");
		// ITesseract instance = new Tesseract(); // JNA Interface Mapping
		// // ITesseract instance = new Tesseract1(); // JNA Direct Mapping
		// try {
		// String result = instance.doOCR(imageFile);
		// System.out.println(result);
		// } catch (TesseractException e) {
		// System.err.println(e.getMessage());
		// }

		// String cmd = "";
		//
		// if (args == null || args.length == 0) {
		// System.out.println("请输入命令行参数");
		// } else {
		//
		// for (int i = 0; i < args.length; i++) {
		// cmd += args[i] + " ";
		// }
		// }
		//
		// try {
		// Process process = Runtime.getRuntime().exec(cmd);
		//
		// InputStreamReader ir = new InputStreamReader(process.getInputStream());
		// LineNumberReader input = new LineNumberReader(ir);
		//
		// String line;
		// while ((line = input.readLine()) != null) {
		// System.out.println(line);
		// }
		// } catch (java.io.IOException e) {
		// System.err.println("IOException " + e.getMessage());
		// }

	}

	public static boolean isSuccess(int code) {
		return code == Constans.SUCCESS;
	}

	public static boolean checkSuccess(String message) {
		return message != null && message.equals(Constans.MESSAGE);
	}
}
