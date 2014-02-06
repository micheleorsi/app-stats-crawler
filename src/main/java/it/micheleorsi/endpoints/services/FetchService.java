/**
 * 
 */
package it.micheleorsi.endpoints.services;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import static com.google.appengine.api.taskqueue.TaskOptions.Builder.*;
import it.micheleorsi.model.AppInfo;

import javax.net.ssl.HttpsURLConnection;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskHandle;
import com.google.appengine.api.taskqueue.TaskOptions.Method;
import com.google.appengine.tools.cloudstorage.GcsOutputChannel;
import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;
import com.google.appengine.tools.cloudstorage.RetryParams;

/**
 * @author micheleorsi
 *
 */
@Path("/stats")
public class FetchService {
	private static final String GCS_URI = "http://commondatastorage.googleapis.com";

	/** Global configuration of Google Cloud Storage OAuth 2.0 scope. */
	private static final String STORAGE_SCOPE = "https://www.googleapis.com/auth/devstorage.read_write";

	/** Global instance of the HTTP transport. */
	private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
	

	private final GcsService gcsService =
		    GcsServiceFactory.createGcsService(RetryParams.getDefaultInstance());
	GcsOutputChannel outputChannel = null;
	  
	Logger log = Logger.getLogger(FetchService.class.getName());
	String appleUsername;
	String applePassword;
	String googleUsername;
	String googlePassword;
	String mopappUsername;
	String mopappPassword;
	
	String[] mopappApps;
	
	List<String> cookies;
	HttpsURLConnection conn;
	 
	final String USER_AGENT = "Mozilla/5.0";
	
	public FetchService() throws IOException {
		Properties properties = new Properties();
		properties.load(getClass().getClassLoader().getResourceAsStream("account.properties"));
		appleUsername = properties.get("apple.username").toString();
		applePassword = properties.get("apple.password").toString();
		googleUsername = properties.get("google.username").toString();
		googlePassword = properties.get("google.password").toString();
	}
	
	@GET
	@Path("/apple")
	public String storeAppleStatTask() throws UnsupportedEncodingException {
        Calendar cal = Calendar.getInstance();
        // download the previous day stats because it is not available today summary
        cal.add(Calendar.DAY_OF_MONTH, -1);
		String year = Integer.valueOf(cal.get(Calendar.YEAR)).toString();
        String month = String.format("%02d", (cal.get(Calendar.MONTH)+1)); // the calendar.month is the index (starting from 0)
        String day = String.format("%02d", (cal.get(Calendar.DAY_OF_MONTH)));
        log.info("TS: "+year+month+day);        
        // queue fetch file
        this.addDownloadAppleFile(year+month+day);
        return "added to the queue";
	}
	
	private void addDownloadAppleFile(String reportDate) throws UnsupportedEncodingException {
		Queue queue = QueueFactory.getQueue("fetch");
        TaskHandle handler = queue.add(withUrl("/api/workers/fetchApple")
        		.param("username",appleUsername)
        		.param("password",applePassword)
        		.param("vndNumber","85662503")
        		.param("typeOfReport","Sales")
        		.param("dateType","Daily")
        		.param("reportType","Summary")
        		.param("reportDate",reportDate)
        		.method(Method.POST));
        log.info("ETA "+ new Date(handler.getEtaMillis()));
        log.info("Name "+handler.getName());
        log.info("Queue name "+handler.getQueueName());
        log.info("Tag "+handler.getTag());
        log.info("RetryCount "+handler.getRetryCount());
	}
	
	private void sendPost(String url, String postParams) throws Exception {
		 
		URL obj = new URL(url);
		conn = (HttpsURLConnection) obj.openConnection();
	 
		// Acts like a browser
		conn.setUseCaches(false);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Host", "accounts.google.com");
		conn.setRequestProperty("User-Agent", USER_AGENT);
		conn.setRequestProperty("Accept",
			"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		for (String cookie : this.cookies) {
			conn.addRequestProperty("Cookie", cookie.split(";", 1)[0]);
		}
		conn.setRequestProperty("Connection", "keep-alive");
		conn.setRequestProperty("Referer", "https://accounts.google.com/ServiceLoginAuth");
		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		conn.setRequestProperty("Content-Length", Integer.toString(postParams.length()));
	 
		conn.setDoOutput(true);
		conn.setDoInput(true);
	 
		// Send post request
		DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
		wr.writeBytes(postParams);
		wr.flush();
		wr.close();
	 
		int responseCode = conn.getResponseCode();
		log.info("\nSending 'POST' request to URL : " + url);
		log.info("Post parameters : " + postParams);
		log.info("Response Code : " + responseCode);
	 
		BufferedReader in = 
	             new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
	 
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		// log.info(response.toString());
	 
	  }
	 
	  private String getPageContent(String url) throws Exception {
	 
		URL obj = new URL(url);
		conn = (HttpsURLConnection) obj.openConnection();
	 
		// default is GET
		conn.setRequestMethod("GET");
	 
		conn.setUseCaches(false);
	 
		// act like a browser
		conn.setRequestProperty("User-Agent", USER_AGENT);
		conn.setRequestProperty("Accept",
			"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		if (cookies != null) {
			for (String cookie : this.cookies) {
				conn.addRequestProperty("Cookie", cookie.split(";", 1)[0]);
			}
		}
		int responseCode = conn.getResponseCode();
		log.info("\nSending 'GET' request to URL : " + url);
		log.info("Response Code : " + responseCode);
		log.info("content-disposition : " + conn.getRequestProperty("content-disposition"));
		log.info("content-type : " + conn.getRequestProperty("content-type"));
		log.info(conn.getHeaderField("content-disposition"));
		log.info(conn.getHeaderField("content-type"));
	 
		BufferedReader in = 
	            new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
	 
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
	 
		// Get the response cookies
		cookies = conn.getHeaderFields().get("Set-Cookie");
	 
		return response.toString();
	 
	  }
	 
	private String getFormParams(String html, String username, String password)
			throws UnsupportedEncodingException {
	 
		log.info("Extracting form's data...");
	 
		Document doc = (Document) Jsoup.parse(html);
	 
		// Google form id
		Element loginform = doc.getElementById("gaia_loginform");
		Elements inputElements = loginform.getElementsByTag("input");
		List<String> paramList = new ArrayList<String>();
		for (Element inputElement : inputElements) {
			String key = inputElement.attr("name");
			String value = inputElement.attr("value");
	 
			if (key.equals("Email"))
				value = username;
			else if (key.equals("Passwd"))
				value = password;
			paramList.add(key + "=" + URLEncoder.encode(value, "UTF-8"));
		}
	 
		// build parameters list
		StringBuilder result = new StringBuilder();
		for (String param : paramList) {
			if (result.length() == 0) {
				result.append(param);
			} else {
				result.append("&" + param);
			}
		}
		return result.toString();
	}
	  
	@POST
	@Path("/google")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public void storeGoogleStat(AppInfo app) throws Exception {
		String url = "https://accounts.google.com/ServiceLoginAuth";
		String gmail = "https://mail.google.com/mail/";
		String googlePlay = "https://play.google.com/apps/publish/statistics/download?package=com.map2app.U9466013A4001&sd=20131018&ed=20131118&dim=overall,os_version,device,country,language,app_version,carrier&met=current_device_installs,daily_device_installs,daily_device_uninstalls,daily_device_upgrades,current_user_installs,total_user_installs,daily_user_installs,daily_user_uninstalls,daily_avg_rating,total_avg_rating&dev_acc=10592599239558026036";
	 
		// make sure cookies is turn on
		CookieHandler.setDefault(new CookieManager());
	 
		// 1. Send a "GET" request, so that you can extract the form's data.
		String page = this.getPageContent(url);
		String postParams = this.getFormParams(page, googleUsername, googlePassword);
	 
		// 2. Construct above post's content and then send a POST request for
		// authentication
		this.sendPost(url, postParams);
	 
		// 3. success then go to gmail.
		String result = this.getPageContent(gmail);
		
		// 4. success then go to google play.
		result = this.getPageContent(googlePlay);
	}

}
