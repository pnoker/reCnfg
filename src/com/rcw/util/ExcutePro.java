package com.rcw.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

public class ExcutePro {

	public static Map<String, String> getProperties(String path) throws Exception {
		Resource resource = new ClassPathResource(path);
		Properties props = PropertiesLoaderUtils.loadProperties(resource);
		Map<String, String> param = new HashMap<String, String>((Map) props);
		return param;
	}
}
