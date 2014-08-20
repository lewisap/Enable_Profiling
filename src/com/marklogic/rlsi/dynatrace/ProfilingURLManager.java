package com.marklogic.rlsi.dynatrace;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

public class ProfilingURLManager {
	private String protocol = "http";
	private String host = "localhost";
	private Integer port = 8080;
	private String path = "/example/watching";
	
	private Logger logger = Logger.getLogger("ProfilingURLManager");
	
	private List<String> extractResponse(InputStream is) {
		List<String> result = null;
		try {						
			ObjectMapper mapper = new ObjectMapper();
			JsonNode root = mapper.readTree(is);
			
			result = new LinkedList<String>();
			
			for(int i = 0; i < root.size(); i++) {
				result.add(root.get(i).getTextValue());
			}			
		} catch(ClientProtocolException cpe) {
			logger.log(Level.SEVERE, cpe.getMessage(), cpe);
		} catch(IOException ioe) {
			logger.log(Level.SEVERE, ioe.getMessage(), ioe);
		} finally {
			if(is != null) { try { is.close(); } catch(Exception e) { } }
		}
		return result;
	}

	public ProfilingURLManager() {
		
	}
	
	public ProfilingURLManager(String host, Integer port) {
		this.host = host;
		this.port = port;
	}
	
	public ProfilingURLManager(String protocol, String host, Integer port, String path) {
		this.protocol = protocol;
		this.host = host;
		this.port = port;
		this.path = path;
	}
	
	public List<String> addUrl(String uri) {
		List<String> result = null;
		
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpPost post = new HttpPost(String.format("%s://%s:%d%s/add?watchUri=%s", protocol, 
				host, port, path, uri));
		
		CloseableHttpResponse response = null;
		InputStream is = null;
		
		try {
		response = httpClient.execute(post);
		is = response.getEntity().getContent();
			
		result = extractResponse(is);
		} catch(IOException ioe) {
			logger.log(Level.SEVERE, ioe.getMessage(), ioe);
		}
		return result;
	}
	
	public List<String> removeUrl(String uri) {
		List<String> result = null;
		
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpPost post = new HttpPost(String.format("%s://%s:%d%s/delete?watchUri=%s", protocol, 
				host, port, path, uri));
		
		CloseableHttpResponse response = null;
		InputStream is = null;
		
		try {
		response = httpClient.execute(post);
		is = response.getEntity().getContent();
			
		result = extractResponse(is);
		} catch(IOException ioe) {
			logger.log(Level.SEVERE, ioe.getMessage(), ioe);
		}
		return result;
	}
	
	public List<String> registeredUrls() {
		List<String> result = null;
		
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpGet get = new HttpGet(String.format("%s://%s:%d%s", protocol, host, port, path));
		
		CloseableHttpResponse response = null;
		InputStream is = null;
		try {
			response = httpClient.execute(get);
			is = response.getEntity().getContent();
						
			result = extractResponse(is);
			
		} catch(ClientProtocolException cpe) {
			logger.log(Level.SEVERE, cpe.getMessage(), cpe);
		} catch(IOException ioe) {
			logger.log(Level.SEVERE, ioe.getMessage(), ioe);
		} finally {
			if(response != null) { 
				try { response.close(); } catch (Exception e) { } }
			if(is != null) { try { is.close(); } catch(Exception e) { } }
		}
		
		return result;
	}
}
