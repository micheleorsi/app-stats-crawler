/**
 * 
 */
package it.micheleorsi.handlers;

import java.io.IOException;

/**
 * @author micheleorsi
 *
 */
public interface FileHandler {
	
	public byte[] readFile(String bucket, String path) throws IOException;
	
	public void storeFile(String bucket, String path, byte[] payload) throws IOException;
	
	public void deleteFile(String bucket, String path) throws IOException;

}
