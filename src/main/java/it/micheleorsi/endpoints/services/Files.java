/**
 * 
 */
package it.micheleorsi.endpoints.services;

import static com.google.appengine.api.taskqueue.TaskOptions.Builder.withUrl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.util.Date;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

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
@Path("/files")
public class Files {
	Logger log = Logger.getLogger(Files.class.getName());
	
	@POST
	@Path("/{statType}/{accountId}/{path}")
	public void storeFile(@PathParam("statType") String statType,
			@PathParam("accountId") String accountId,
			@PathParam("path") String path,
			byte[] payload) throws IOException {
		log.info("store "+statType+" type");
		log.info("from "+accountId+" id");
		log.info("into "+path+" path");
		GcsService gcsService =
			    GcsServiceFactory.createGcsService(RetryParams.getDefaultInstance());
		GcsOutputChannel outputChannel =
        	    gcsService.createOrReplace(
        	    		new GcsFilename("appstats", statType+"/"+accountId+"/"+path), 
        	    		GcsFileOptions.getDefaultInstance());
		outputChannel.write(ByteBuffer.wrap(payload));
		outputChannel.close();
		
		if(path.endsWith(".gz")) {
			log.info("going to unzip file");
			// queue unzip file
	    	Queue queue = QueueFactory.getQueue("unzip");
	        TaskHandle handler = queue.add(withUrl("/api/files/"+statType+"/"+accountId+"/"+path)
	        		.method(Method.PUT));
	        log.info("ETA "+ new Date(handler.getEtaMillis()));
	        log.info("Name "+handler.getName());
	        log.info("Queue name "+handler.getQueueName());
	        log.info("Tag "+handler.getTag());
	        log.info("RetryCount "+handler.getRetryCount());
		}		
	}
	
	@PUT
	@Path("/{statType}/{accountId}/{path}")
	public void unzipFile(@PathParam("statType") String statType,
			@PathParam("accountId") String accountId,
			@PathParam("path") String path) throws IOException {
		log.info("unzip "+statType+" type");
		log.info("from "+accountId+" id");
		log.info("into "+path+" path");
		
		GcsService gcsService =
			    GcsServiceFactory.createGcsService(RetryParams.getDefaultInstance());
		GcsInputChannel readChannel = gcsService.openReadChannel(
				new GcsFilename("appstats", statType+"/"+accountId+"/"+path), 0);

		InputStream gzippedResponse = Channels.newInputStream(readChannel);
	    InputStream ungzippedResponse = new GZIPInputStream(gzippedResponse);
	    
	    GcsOutputChannel outputChannel =
        	    gcsService.createOrReplace(new GcsFilename("appstats", statType+"/"+accountId+"/"+path.replace(".gz", "")), 
        	    		new GcsFileOptions.Builder().mimeType("text/tab-separated-values").build());
	    
	    IOUtils.copy(ungzippedResponse, Channels.newOutputStream(outputChannel));
		
	    readChannel.close();
	    outputChannel.close();
	    
	    // queue delete file
    	Queue queue = QueueFactory.getQueue("delete");
        TaskHandle handler = queue.add(withUrl("/api/files/"+statType+"/"+accountId+"/"+path)
        		.method(Method.DELETE));
        log.info("ETA "+ new Date(handler.getEtaMillis()));
        log.info("Name "+handler.getName());
        log.info("Queue name "+handler.getQueueName());
        log.info("Tag "+handler.getTag());
        log.info("RetryCount "+handler.getRetryCount());
	}
	
	@DELETE
	@Path("/{statType}/{accountId}/{path}")
	public void deleteFile(@PathParam("statType") String statType,
			@PathParam("accountId") String accountId,
			@PathParam("path") String path) throws IOException {
		log.info("remove "+statType+" type");
		log.info("from "+accountId+" id");
		log.info("into "+path+" path");
		
		GcsService gcsService =
			    GcsServiceFactory.createGcsService(RetryParams.getDefaultInstance());
		gcsService.delete(new GcsFilename("appstats", statType+"/"+accountId+"/"+path));
	}
}
