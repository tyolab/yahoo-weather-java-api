/**
 *
 */
package com.github.fedy2.weather.binding.adapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * @author "Federico De Faveri defaveri@gmail.com"
 */
public class DateAdapter extends XmlAdapter<String, Date> {

	// private Logger logger = LoggerFactory.getLogger(DateAdapter.class);

	private SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM yyyy", Locale.US);

    /**
     * {@inheritDoc}
     */
    @Override
    public String marshal(Date v) throws Exception {
        return dateFormat.format(v);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Date unmarshal(String v) throws Exception {

        try {
            return dateFormat.parse(v);
        } catch (Exception e) {
            // logger.warn("Unknown date format \"{}\"", v);
            return null;
        }
    }

}
