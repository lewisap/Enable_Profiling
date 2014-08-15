package com.marklogic.rlsi.dynatrace;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class ProfilingURLManagerTest {
	
	private ProfilingURLManager manager;

	@Before
	public void setUp() throws Exception {
		manager = new ProfilingURLManager("192.168.1.9", 8080);
	}

	@Test
	public void testAddUrl() {
		List<String> registered = manager.registeredUrls();
		if (registered.contains("/foo/bar")) {
			manager.removeUrl("/foo/bar");
		}
		
		registered = manager.registeredUrls();
		assertTrue(!registered.contains("/foo/bar"));
		
		registered = manager.addUrl("/foo/bar");
		assertTrue(registered.contains("/foo/bar"));
	}

	@Test
	public void testRemoveUrl() {
		manager.addUrl("/foo/bar");
		
		assertTrue(manager.registeredUrls().contains("/foo/bar"));
		assertTrue(!manager.removeUrl("/foo/bar").contains("/foo/bar"));
		assertTrue(!manager.registeredUrls().contains("/foo/bar"));
	}

	@Test
	public void testRegisteredUrls() {
		List<String> registered = manager.registeredUrls();
		assertNotNull(registered);
	}

}
