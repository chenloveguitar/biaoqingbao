package com.chd.biaoqingbao.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.chd.biaoqingbao.constans.CharsetName;
import com.chd.biaoqingbao.constans.Constans;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

/**
 * 该版本无法执行js,如果网站是通过js动态生成的网页,就没办法获取到想要的内容了.
 * @author jumili
 *
 */
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
		// logger.info("-------------------网页内容------------------");
		// logger.info(html.toString());
		String urlExp = "((ht|f)tps?):\\/\\/[\\w\\-]+(\\.[\\w\\-]+)+([\\w\\-\\.,@?^=%&:\\/~\\+#]*[\\w\\-\\@?^=%&\\/~\\+#])";
		String pathExp = "[\\/?\\w?]+";
		String exp = "<a href=\"(" + urlExp + "|" + pathExp + ")\"\\s?[target=\"_blank\"\\stitle=\"[\\u4e00-\\u9fa5]\"]*>([\\w]*[\\u4e00-\\u9fa5]+)[</a>]?";
		Pattern pattern = Pattern.compile(exp);
		Matcher matcher = pattern.matcher(html.toString());
		while (matcher.find()) {
			String link = matcher.group(1);
			String word = matcher.group(6);

			if (!link.contains("http://")) {
				link = Constans.PATH + link;
			}

			if (!link.equals(Constans.PATH) && link.contains(Constans.PATH)) {
				System.out.println(link);
				System.out.println(word);
				parseImages(link);
			}
		}
	}

	public static void parseImages(String baseUrl) throws Exception {

		for (int i = 1; i <= 100; i++) {
			URL url = new URL(baseUrl + "/page/" + i);
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

			if (!html.toString().contains("http://biaoqingbao.xin/wp-content/themes/bizhiyun/images/error.gif")) {
				// 解析img
				String exp = "<img src=\"([\\/?\\w?\\-?]+\\.(png|jpg|jpeg|gif))\" alt=\"([\\u4e00-\\u9fa5]*)\" width=\"150\" height=\"150\" border=\"0\">";
				Pattern pattern = Pattern.compile(exp);
				Matcher matcher = pattern.matcher(html.toString());
				while (matcher.find()) {
					String link = matcher.group(1);
					String word = matcher.group(2);

					if (!link.contains("http://")) {
						link = Constans.PATH + link;
					}

					if (!link.equals(Constans.PATH) && link.contains(Constans.PATH)) {
						System.out.println(link);
						System.out.println(word);
					}
				}
			}
		}
	}

	public static boolean isSuccess(int code) {
		return code == Constans.SUCCESS;
	}

	public static boolean checkSuccess(String message) {
		return message != null && message.equals(Constans.MESSAGE);
	}

	public void ocrText(String[] args) {
		File imageFile = new File("C:\\Users\\jumili\\Desktop\\huamianmei.jpg");
		ITesseract instance = new Tesseract(); // JNA Interface Mapping
		// ITesseract instance = new Tesseract1(); // JNA Direct Mapping
		try {
			String result = instance.doOCR(imageFile);
			System.out.println(result);
		} catch (TesseractException e) {
			System.err.println(e.getMessage());
		}

		String cmd = "";

		if (args == null || args.length == 0) {
			System.out.println("请输入命令行参数");
		} else {

			for (int i = 0; i < args.length; i++) {
				cmd += args[i] + " ";
			}
		}

		try {
			Process process = Runtime.getRuntime().exec(cmd);

			InputStreamReader ir = new InputStreamReader(process.getInputStream());
			LineNumberReader input = new LineNumberReader(ir);

			String line;
			while ((line = input.readLine()) != null) {
				System.out.println(line);
			}
		} catch (java.io.IOException e) {
			System.err.println("IOException " + e.getMessage());
		}
	}
}
