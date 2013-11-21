/**
 * 
 */
package it.micheleorsi.auth;

import static org.junit.Assert.*;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import javax.net.ssl.HttpsURLConnection;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author micheleorsi
 *
 */
public class AuthenticatorTest {
	Logger log = Logger.getLogger("AuthenticatorTest");
	Properties properties;
	String appleUsername;
	String applePassword;
	String googleUsername;
	String googlePassword;
	

	public AuthenticatorTest() {
		
	}

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		properties = new Properties();
		properties.load(getClass().getClassLoader().getResourceAsStream("account.properties"));
		appleUsername = properties.get("apple.username").toString();
		applePassword = properties.get("apple.password").toString();
		googleUsername = properties.get("google.username").toString();
		googlePassword = properties.get("google.password").toString();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link it.micheleorsi.auth.Authenticator#login()}.
	 */
	@Test
	public void testLogin() {
		
	}

	/**
	 * Test method for {@link it.micheleorsi.auth.Authenticator#logout()}.
	 */
	@Test
	public void testLogout() {
		
	}

	/**
	 * Test method for {@link it.micheleorsi.auth.Authenticator#getId()}.
	 */
	@Test
	public void testGetId() {
		
	}
	
	@Test
	public void mainApple() throws Exception {
		String data = "USERNAME="      + URLEncoder.encode(appleUsername, "UTF-8");
        data = data + "&PASSWORD="     + URLEncoder.encode(applePassword, "UTF-8");
        data = data + "&VNDNUMBER="    + URLEncoder.encode("85662503", "UTF-8");
        data = data + "&TYPEOFREPORT=" + URLEncoder.encode("Sales", "UTF-8");
        data = data + "&DATETYPE="     + URLEncoder.encode("Daily", "UTF-8");
        data = data + "&REPORTTYPE="   + URLEncoder.encode("Summary", "UTF-8");
        data = data + "&REPORTDATE="   + URLEncoder.encode("20131104", "UTF-8");
        
        HttpURLConnection connection = null;
        try {
            URL url = new URL("https://reportingitc.apple.com/autoingestion.tft?");

            connection = (HttpURLConnection)url.openConnection();
                  connection.setRequestMethod("POST");
                  connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                  connection.setDoOutput(true);
                      
                  OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
                  out.write(data);
                  out.flush();
                  out.close();

                  if (connection.getHeaderField("ERRORMSG") != null) {
                System.out.println(connection.getHeaderField("ERRORMSG"));
                  } else if (connection.getHeaderField("filename") != null) {
                getFile(connection);
            }
            } catch (Exception ex) {
                  ex.printStackTrace();
            System.out.println("The report you requested is not available at this time.  Please try again in a few minutes.");
        } finally {
                  if (connection != null) {
                connection.disconnect();
                connection = null;
                  }
        }

	}
	
	/**
     * Download a file from a HTTP connection and write it to file indicated by
     * HTTP header 'filename'
     *
     * @param connection HTTP connection to read downloading file from
     *
     * (Note: The argument was HttpsURLConnection in decompiled code)
     */
    private static void getFile(HttpURLConnection connection) throws IOException {
        String filename = connection.getHeaderField("filename");
        System.out.println(filename);

        BufferedInputStream in = new BufferedInputStream(connection.getInputStream());
        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(filename));

        int i = 0;
        byte[] data = new byte[1024];
            while ((i = in.read(data)) != -1) {
            out.write(data, 0, i);
        }

        in.close();
        out.close();
        System.out.println("File Downloaded Successfully ");
    }
	
	@Test
	public void mainGoogle() throws Exception {		 
		String url = "https://accounts.google.com/ServiceLoginAuth";
		String gmail = "https://mail.google.com/mail/";
		String googlePlay = "https://play.google.com/apps/publish/statistics/download?package=com.map2app.U9466013A4001&sd=20131018&ed=20131118&dim=overall,os_version,device,country,language,app_version,carrier&met=current_device_installs,daily_device_installs,daily_device_uninstalls,daily_device_upgrades,current_user_installs,total_user_installs,daily_user_installs,daily_user_uninstalls,daily_avg_rating,total_avg_rating&dev_acc=10592599239558026036";
	 
		// make sure cookies is turn on
		CookieHandler.setDefault(new CookieManager());
	 
		// 1. Send a "GET" request, so that you can extract the form's data.
		String page = this.GetPageContent(url);
		String postParams = this.getFormParams(page, googleUsername, googlePassword);
	 
		// 2. Construct above post's content and then send a POST request for
		// authentication
		this.sendPost(url, postParams);
	 
		// 3. success then go to gmail.
		String result = this.GetPageContent(gmail);
		
		// 4. success then go to google play.
		result = this.GetPageContent(googlePlay);
	}
	
	private List<String> cookies;
	private HttpsURLConnection conn;
	 
	private final String USER_AGENT = "Mozilla/5.0";
	
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
		System.out.println("\nSending 'POST' request to URL : " + url);
		System.out.println("Post parameters : " + postParams);
		System.out.println("Response Code : " + responseCode);
	 
		BufferedReader in = 
	             new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
	 
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		// System.out.println(response.toString());
	 
	  }
	 
	  private String GetPageContent(String url) throws Exception {
	 
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
		System.out.println("\nSending 'GET' request to URL : " + url);
		System.out.println("Response Code : " + responseCode);
		System.out.println("content-disposition : " + conn.getRequestProperty("content-disposition"));
		System.out.println("content-type : " + conn.getRequestProperty("content-type"));
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
		setCookies(conn.getHeaderFields().get("Set-Cookie"));
	 
		return response.toString();
	 
	  }
	 
	  public String getFormParams(String html, String username, String password)
			throws UnsupportedEncodingException {
	 
		System.out.println("Extracting form's data...");
	 
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
	 
	  public List<String> getCookies() {
		return cookies;
	  }
	 
	  public void setCookies(List<String> cookies) {
		this.cookies = cookies;
	  }


}
