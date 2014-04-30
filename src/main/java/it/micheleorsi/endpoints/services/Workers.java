/**
 * 
 */
package it.micheleorsi.endpoints.services;

import static com.google.appengine.api.taskqueue.TaskOptions.Builder.withUrl;
import it.micheleorsi.utils.Configurator;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import org.apache.commons.io.IOUtils;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskHandle;
import com.google.appengine.api.taskqueue.TaskOptions.Method;
/**
 * @author micheleorsi
 *
 */
@Path("/workers")
public class Workers {
	private static final String AUTOINGESTION_APPLE_URL = "https://reportingitc.apple.com/autoingestion.tft?";
	Logger log = Logger.getLogger(Workers.class.getName());
	
	private static final String WARNING_MESSAGE = "There are no reports available to download for this selection";
		
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
        
        log.info("ready to send data: "+data);
        
        HttpURLConnection connection = null;
        URL url = new URL(AUTOINGESTION_APPLE_URL);
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
        	if(errorMessage.equals(WARNING_MESSAGE)) {
        		log.warning(errorMessage);
        		throw new RuntimeException(errorMessage);
        	} else {
        		log.info(WARNING_MESSAGE);
        	}
        } else if (connection.getHeaderField("filename") != null) {
        	byte[] bytes = IOUtils.toByteArray(connection.getInputStream());
        	
        	// queue store file
        	Queue queue = QueueFactory.getQueue("store");
            TaskHandle handler = queue.add(withUrl(Configurator.ROOT_PATH+"/files/apple/"+vndNumber+"/"+connection.getHeaderField("filename"))
            		.payload(bytes)
            		.method(Method.POST));
            log.info("ETA "+ new Date(handler.getEtaMillis()));
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
