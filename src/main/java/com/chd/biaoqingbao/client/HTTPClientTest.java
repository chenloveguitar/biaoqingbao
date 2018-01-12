package com.chd.biaoqingbao.client;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.chd.biaoqingbao.constans.Constans;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

public class HTTPClientTest {

	
	private static WebClient client = new WebClient(BrowserVersion.CHROME);
	
	static {
		client.getOptions().setCssEnabled(false);
		client.getOptions().setJavaScriptEnabled(false);
	}
	
	public static void main(String[] args) throws Exception {
		File baseFile = new File(Constans.BASE_LOCAL_PATH);
		if(!baseFile.exists()) {
			baseFile.mkdirs();
		}
		//����һ��ģ��chrome�������web�ͻ���
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
				System.out.println(link);
				System.out.println(word);
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
				// ����img
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
						System.out.println("���������ļ��Ѵ��ڣ�");
					}
					System.out.println(link);
					System.out.println(word);
				}
			}catch(FailingHttpStatusCodeException e) {
				break;
			}
		}
	}
	/**
	 * ��ȡ��ȡͨ��js��̬���ɵ�html��ҳ
	 * @param args
	 */
	public static void testGetHtml(String[] args) {
		String page = new String();  
	    try {  
	        WebClient webClient = new WebClient(BrowserVersion.CHROME);  
	        webClient.getOptions().setCssEnabled(false);  
	        webClient.getOptions().setJavaScriptEnabled(false);  
	        //ȥ����ҳ  
	        HtmlPage htmlPage = webClient.getPage("http://pic.sogou.com/");  
	        //�õ���  
	        HtmlForm form = htmlPage.getFormByName("searchForm");  
	        //�õ��ύ��ť  
	        HtmlSubmitInput button = form.getInputByValue("�ѹ�����");  
	        //�õ������  
	        HtmlTextInput textField = form.getInputByName("query");  
	        //��������  
	        textField.setValueAttribute(args[0]);  
	        //��һ�°�ť  
	        HtmlPage nextPage = button.click();  
	        page = nextPage.asXml();
	        webClient.close();  
	    } catch (Exception e) {  
	        e.printStackTrace();  
	    }
	    //��ȡ������ҳ����
	    System.out.println(page);
	}
}
