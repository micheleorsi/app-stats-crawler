/**
 * 
 */
package it.micheleorsi.handlers;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.util.Arrays;
import java.util.zip.GZIPInputStream;

import org.apache.commons.io.IOUtils;

import com.google.appengine.tools.cloudstorage.GcsFileMetadata;
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
public class FileHandlerImpl implements FileHandler {
	
	public byte[] readFile(String bucket, String path) throws IOException {
		GcsService gcsService =
			    GcsServiceFactory.createGcsService();
		GcsFilename fileName = new GcsFilename(bucket, path);
		GcsFileMetadata meta = gcsService.getMetadata(fileName);
		int fileSize = 0;
		if(meta!=null) {
			fileSize = (int) meta.getLength();
		}
		if(fileSize>0) {
			ByteBuffer result = ByteBuffer.allocate(fileSize);
			try (GcsInputChannel readChannel = gcsService.openReadChannel(fileName, 0)) {
			  readChannel.read(result);
			}
			if(result.hasArray()) {
				return result.array();
			} else {
				return null;
			}
		}
		else {
			return null;
		}
		
	}
	
	public void storeFile(String bucket, String path, byte[] payload) throws IOException {
		GcsService gcsService =
			    GcsServiceFactory.createGcsService();
		GcsOutputChannel outputChannel =
        	    gcsService.createOrReplace(
        	    		new GcsFilename(bucket, path), 
        	    		GcsFileOptions.getDefaultInstance());
		outputChannel.write(ByteBuffer.wrap(payload));
		outputChannel.close();
	}
	
	public void deleteFile(String bucket, String path) throws IOException {
		GcsService gcsService =
			    GcsServiceFactory.createGcsService(RetryParams.getDefaultInstance());
		gcsService.delete(new GcsFilename(bucket, path));
	}

}
