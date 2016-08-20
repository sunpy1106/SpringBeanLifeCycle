package com.journaldev.spring.main;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.EncodedResource;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.journaldev.spring.service.MyAwareService;

public class ProgramBeanFactory {
	public static void main(String[] args) throws Exception{
		System.out.println("create resource");
		ClassPathResource resource = new ClassPathResource("spring-aware.xml");
		System.out.println("create bean factory");
		DefaultListableBeanFactory factory = new DefaultListableBeanFactory();
		System.out.println("create xml file reader ");
		XmlBeanDefinitionReader reader  = new XmlBeanDefinitionReader(factory);
		System.out.println("use the xmlBeanDefinitionReader to load the resource");
		EncodedResource encodedResource = new EncodedResource(resource);
		try {
			InputStream inputStream = encodedResource.getResource().getInputStream();
			try {
				InputSource inputSource = new InputSource(inputStream);
				if (encodedResource.getEncoding() != null) {
					inputSource.setEncoding(encodedResource.getEncoding());
				}
				//reader.doLoadBeanDefinitions(inputSource, encodedResource.getResource());
				try {
					Document doc = reader.doLoadDocument(inputSource, resource);
					reader.registerBeanDefinitions(doc, resource);
				}
				catch (BeanDefinitionStoreException ex) {
					throw ex;
				}
				
			}
			finally {
				inputStream.close();
			}
		}
		catch (IOException ex) {
			throw new BeanDefinitionStoreException(
					"IOException parsing XML document from " + encodedResource.getResource(), ex);
		}
		//reader.loadBeanDefinitions(resource);
		

		System.out.println("get bean");
		MyAwareService service = factory.getBean("myAwareService",MyAwareService.class);
		service.showDescription();
		
	}
}
