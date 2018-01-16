package com.chd.biaoqingbao.client;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.chd.biaoqingbao.constans.Constans;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

public class HTTPClientTest {

	
	private static final WebClient client = new WebClient(BrowserVersion.CHROME);
	private static final Logger logger = LogManager.getLogger(HTTPClientTest.class);
	static {
		
		//检测运行环境
		String osName = System.getProperty("os.name");
		if(osName != null) {
			osName = osName.toLowerCase();
			if(osName.contains("linux")) {//Linux系统
				Constans.BASE_LOCAL_PATH = "/usr/local/biaoqingbao/";
			}
		}
		System.out.println("当前系统为:"+osName);
		client.getOptions().setCssEnabled(false);
		client.getOptions().setJavaScriptEnabled(false);
	}
	
	public static void main(String[] args) throws Exception {
		getHtmlPage();
	}
	
	public static void getHtmlPage() throws Exception {
		File baseFile = new File(Constans.BASE_LOCAL_PATH);
		if(!baseFile.exists()) {
			baseFile.mkdirs();
		}
		//创建一个模拟chrome浏览器的web客户端
		HtmlPage htmlPage = client.getPage(Constans.PATH);
		String html = htmlPage.asXml();
		String urlExp = "((ht|f)tps?):\\/\\/[\\w\\-]+(\\.[\\w\\-]+)+([\\w\\-,@?^=%&:\\/~\\+#]*[\\w\\-\\@?^=%&\\/~\\+#])";
		String pathExp = "[\\/?\\w?]+";
		String exp = "<a href=\"(" + urlExp + "|" + pathExp + ")\"\\s?[target=\"_blank\"\\stitle=\"[\\u4e00-\\u9fa5]\"]*>\\s*([\\w]*[\\u4e00-\\u9fa5]+)\\s*</a>";
		Pattern pattern = Pattern.compile(exp);
		Matcher matcher = pattern.matcher(html);
		while (matcher.find()) {
			String link = matcher.group(1);
			String word = matcher.group(6);

			if (!link.contains("http://")) {
				link = Constans.PATH + link;
			}

			if (!link.equals(Constans.PATH) && link.contains(Constans.PATH)) {
				logger.info(link);
				logger.info(word);
				File file = new File(baseFile, word);
				if(!file.exists()) {
					file.mkdir();
				}
				parseImages(link,file);
			}
		}
	}
	
	public static void parseImages(String baseUrl,File file) throws Exception {
		for (int i = 1; i <= 100; i++) {
			try {
				HtmlPage htmlPage = client.getPage(baseUrl + "/page/" + i);
				String html = htmlPage.asXml();
				// 解析img
				String exp = "<img src=\"([\\/?\\w?\\-?]+\\.(png|jpg|jpeg|gif))\" alt=\"([\\u4e00-\\u9fa5]*)\" width=\"150\" height=\"150\" border=\"0\"/>";
				Pattern pattern = Pattern.compile(exp);
				Matcher matcher = pattern.matcher(html);
				while (matcher.find()) {
					String link = matcher.group(1);
					String word = matcher.group(3);

					if (!link.contains("http://")) {
						link = Constans.PATH + link;
					}
					URL url = new URL(link);
					String ext = matcher.group(2);
					File f = new File(file, word+"."+ext);
					if(!f.exists()) {
						f.createNewFile();
						HttpURLConnection connection = (HttpURLConnection) url.openConnection();
						InputStream inputStream = connection.getInputStream();
						FileOutputStream outputStream = new FileOutputStream(f);
						int length = 0;
						byte[] b = new byte[1024];
						while((length = inputStream.read(b)) != -1) {
							outputStream.write(b, 0, length);
						}
						outputStream.close();
						inputStream.close();
					}else {
						logger.info("已跳过，文件已存在！");
					}
					logger.info(link);
					logger.info(word);
				}
			}catch(FailingHttpStatusCodeException e) {
				break;
			}
		}
	}
	/**
	 * 获取获取通过js动态生成的html网页
	 * @param args
	 */
	public static void testGetHtml(String[] args) {
		String page = new String();  
	    try {  
	        WebClient webClient = new WebClient(BrowserVersion.CHROME);  
	        webClient.getOptions().setCssEnabled(false);  
	        webClient.getOptions().setJavaScriptEnabled(false);  
	        //去拿网页  
	        HtmlPage htmlPage = webClient.getPage("http://pic.sogou.com/");  
	        //得到表单  
	        HtmlForm form = htmlPage.getFormByName("searchForm");  
	        //得到提交按钮  
	        HtmlSubmitInput button = form.getInputByValue("搜狗搜索");  
	        //得到输入框  
	        HtmlTextInput textField = form.getInputByName("query");  
	        //输入内容  
	        textField.setValueAttribute(args[0]);  
	        //点一下按钮  
	        HtmlPage nextPage = button.click();  
	        page = nextPage.asXml();
	        webClient.close();  
	    } catch (Exception e) {  
	        e.printStackTrace();  
	    }
	    //获取到的网页内容
	    System.out.println(page);
	}
}
