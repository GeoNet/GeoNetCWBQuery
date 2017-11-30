/*
 * Copyright 2013, Institute of Geological & Nuclear Sciences Ltd or
 * third-party contributors as indicated by the @author tags.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */



package nz.org.geonet.simplequakeml;

//~--- non-JDK imports --------------------------------------------------------

import nz.org.geonet.simplequakeml.domain.Event;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.InputStream;
import java.io.StringWriter;

//~--- JDK imports ------------------------------------------------------------

/**
 * Implements SimpleQuakeML for QuakeML RT 1.2
 *
 * @author: Geoff Clitheroe
 * Date: 5/3/13
 * Time: 11:24 AM
 */
public class QuakeML_RT_1_2 implements SimpleQuakeML {
    public Event read(InputStream inputStream) throws Exception {
        InputStream xslt = this.getClass().getClassLoader().getResourceAsStream("xsl/quakemlRT1.2tosimpleXML.xsl");
        StreamSource streamSource = new StreamSource(xslt);
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer(streamSource);
        StringWriter writer = new StringWriter();

        transformer.transform(new StreamSource(inputStream), new StreamResult(writer));

        Serializer serializer = new Persister();

        return serializer.read(Event.class, writer.toString());
    }

    public Event read(String url) throws Exception {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(url);
        HttpResponse response = httpClient.execute(httpGet);
        Event event = null;
        HttpEntity entity = response.getEntity();

        if (entity != null) {
            try {
                InputStream inputStream = entity.getContent();

                event = this.read(inputStream);
            } finally {
                httpGet.releaseConnection();
            }
        } else {
            httpGet.releaseConnection();
        }

        return event;
    }
}
