/**
 * 
 */
package it.micheleorsi.handlers;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.tools.development.testing.LocalBlobstoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalFileServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalTaskQueueTestConfig;

/**
 * @author micheleorsi
 *
 */
public class FileHandlerImplTest {
	private final LocalServiceTestHelper helper = new LocalServiceTestHelper(
		      new LocalTaskQueueTestConfig(), new LocalFileServiceTestConfig(),
		      new LocalBlobstoreServiceTestConfig(), new LocalDatastoreServiceTestConfig());
	
	private byte[] payload = null;
	private FileHandler handler = null;
	
	public FileHandlerImplTest() throws IOException {
		InputStream is = null;
		is = this.getClass().getClassLoader()
					.getResourceAsStream("testimage.png");
		payload = new byte[is.available()];
		is.read(payload);
		assertNotNull(payload);
		
		handler = new FileHandlerImpl();
	}
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		helper.setUp();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		helper.tearDown();
	}

	/**
	 * Test method for {@link it.micheleorsi.handlers.FileHandlerImpl#readFile(java.lang.String, java.lang.String)}.
	 * @throws IOException 
	 */
	@Test
	public void testReadFile() throws IOException {
		handler.storeFile("test-bucket", "testing-temp", payload);
		
		byte[] returnPayload = handler.readFile("test-bucket", "testing-temp");
		
		assertNotNull(returnPayload);
		assertEquals(payload.length, returnPayload.length);
	}

	/**
	 * Test method for {@link it.micheleorsi.handlers.FileHandlerImpl#storeFile(java.lang.String, java.lang.String, byte[])}.
	 * @throws IOException 
	 */
	@Test
	public void testStoreFile() throws IOException {
		handler.storeFile("test-bucket", "testing-temp", payload);
		
		byte[] returnPayload = handler.readFile("test-bucket", "testing-temp");
		
		assertNotNull(returnPayload);
		assertEquals(payload.length, returnPayload.length);
	}

	/**
	 * Test method for {@link it.micheleorsi.handlers.FileHandlerImpl#deleteFile(java.lang.String, java.lang.String)}.
	 * @throws IOException 
	 */
	@Test
	public void testDeleteFile() throws IOException {
		handler.storeFile("test-bucket", "testing-temp", payload);
		
		handler.deleteFile("test-bucket", "testing-temp");
		
		byte[] returnPayload = handler.readFile("test-bucket", "testing-temp");
		
		assertNull(returnPayload);
	}

}
