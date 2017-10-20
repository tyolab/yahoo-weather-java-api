/**
 * 
 */
package com.github.fedy2.weather.binding;

import com.github.fedy2.weather.data.Rss;

import org.arbonaut.xml.bind.JAXBContext;
import org.arbonaut.xml.bind.JAXBException;
import org.arbonaut.xml.bind.Unmarshaller;

import java.io.StringReader;

/**
 * @author "Federico De Faveri defaveri@gmail.com"
 *
 */
public class RSSParser implements Parser<Rss> {

	private Unmarshaller unmarshaller;

	public RSSParser() throws JAXBException
	{
		JAXBContext context = JAXBContext.newInstance(Rss.class);
		unmarshaller = context.createUnmarshaller();
	}

	public Rss parse(String xml) throws JAXBException
	{
		return (Rss)unmarshaller.unmarshal(new StringReader(xml));
	}
	
}
