/**
 * 
 */
package it.micheleorsi.endpoints.services;

import static com.google.appengine.api.taskqueue.TaskOptions.Builder.withUrl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

import org.apache.commons.io.IOUtils;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskHandle;
import com.google.appengine.api.taskqueue.TaskOptions.Method;
import com.google.appengine.tools.cloudstorage.GcsFileOptions;
import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.google.appengine.tools.cloudstorage.GcsInputChannel;
import com.google.appengine.tools.cloudstorage.GcsOutputChannel;
import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;
import com.google.appengine.tools.cloudstorage.RetryParams;

/**
 * @author micheleorsi
 *
 */
@Path("/workers")
public class Workers {
	private static final String AUTOINGESTION_URL = "https://reportingitc.apple.com/autoingestion.tft?";
	Logger log = Logger.getLogger(Workers.class.getName());
		
	@POST
	@Path("/fetchApple")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public void fetchFile(
			@FormParam("username") String username,
			@FormParam("password") String password,
			@FormParam("vndNumber") String vndNumber,
			@FormParam("typeOfReport") String typeOfReport,
			@FormParam("dateType") String dateType,
			@FormParam("reportType") String reportType,
			@FormParam("reportDate") String reportDate) throws IOException {
		log.info("fetch apple file");
		String data = "USERNAME="      + username;
        data = data + "&PASSWORD="     + password;
        data = data + "&VNDNUMBER="    + vndNumber;
        data = data + "&TYPEOFREPORT=" + typeOfReport;
        data = data + "&DATETYPE="     + dateType;
        data = data + "&REPORTTYPE="   + reportType;
        data = data + "&REPORTDATE="   + reportDate;
        
        HttpURLConnection connection = null;
        URL url = new URL(AUTOINGESTION_URL);
        connection = (HttpURLConnection)url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", MediaType.APPLICATION_FORM_URLENCODED);
        connection.setDoOutput(true);
                      
        OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
        out.write(data);
        out.flush();
        out.close();

        if (connection.getHeaderField("ERRORMSG") != null) {
        	String errorMessage = connection.getHeaderField("ERRORMSG");
        	log.warning(errorMessage);
        	throw new RuntimeException(errorMessage);
        } else if (connection.getHeaderField("filename") != null) {
        	byte[] bytes = IOUtils.toByteArray(connection.getInputStream());
        	
        	// queue store file
        	Queue queue = QueueFactory.getQueue("store-queue");
            TaskHandle handler = queue.add(withUrl("/api/files/apple/"+connection.getHeaderField("filename"))
            		.payload(bytes)
            		.method(Method.POST));
            log.info("ETA "+handler.getEtaMillis());
            log.info("Name "+handler.getName());
            log.info("Queue name "+handler.getQueueName());
            log.info("Tag "+handler.getTag());
            log.info("RetryCount "+handler.getRetryCount());
        }
        if (connection != null) {
        	connection.disconnect();
        }
	}
}
