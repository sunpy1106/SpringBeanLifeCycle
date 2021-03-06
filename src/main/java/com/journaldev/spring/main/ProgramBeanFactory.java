package com.journaldev.spring.main;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.BeanDefinitionDocumentReader;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.beans.factory.xml.DefaultBeanDefinitionDocumentReader;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.EncodedResource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
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
		int countBefore = reader.getRegistry().getBeanDefinitionCount();
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
					//reader.registerBeanDefinitions(doc, resource);
					DefaultBeanDefinitionDocumentReader documentReader = (DefaultBeanDefinitionDocumentReader)reader.createBeanDefinitionDocumentReader();
					documentReader.setEnvironment(reader.getEnvironment());
					//core code
					//documentReader.registerBeanDefinitions(doc, reader.createReaderContext(resource));
					documentReader.setReaderContext(reader.createReaderContext(resource));
					Element root = doc.getDocumentElement();
					//documentReader.doRegisterBeanDefinitions(root);
					BeanDefinitionParserDelegate parent = documentReader.delegate;
					BeanDefinitionParserDelegate curDelegate  = documentReader.createDelegate(documentReader.readerContext, root, parent);
					//documentReader.parseBeanDefinitions(root, documentReader.delegate);				
					if (curDelegate.isDefaultNamespace(root)) {			
						NodeList nl = root.getChildNodes();
						for (int i = 0; i < nl.getLength(); i++) {
							Node node = nl.item(i);
							if (node instanceof Element) {
								Element ele = (Element) node;
								if (curDelegate.isDefaultNamespace(ele)) {
									//documentReader.parseDefaultElement(ele, curDelegate);
									if (curDelegate.nodeNameEquals(ele, BeanDefinitionParserDelegate.BEAN_ELEMENT)) {
										//documentReader.processBeanDefinition(ele, curDelegate);
										BeanDefinitionHolder bdHolder = curDelegate.parseBeanDefinitionElement(ele,null);
										if (bdHolder != null) {
											bdHolder = curDelegate.decorateBeanDefinitionIfRequired(ele, bdHolder);
											try {
												// Register the final decorated instance.
												BeanDefinitionReaderUtils.registerBeanDefinition(bdHolder, documentReader.getReaderContext().getRegistry());
											}
											catch (BeanDefinitionStoreException ex) {
												documentReader.getReaderContext().error("Failed to register bean definition with name '" +
														bdHolder.getBeanName() + "'", ele, ex);
											}

										}
									}
								}
							}
						}
					}
					int res = reader.getRegistry().getBeanDefinitionCount() - countBefore ;
					System.out.println("Find " + res + " beans in total");
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


		System.out.println("get bean");
		MyAwareService service = factory.getBean("myAwareService",MyAwareService.class);
		service.showDescription();
		
	}
}
