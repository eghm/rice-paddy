/**
 * Copyright 2014 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.student.service.remote.impl;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import org.apache.cxf.endpoint.Client;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.frontend.ClientProxy;
import static org.apache.cxf.jaxrs.client.WebClient.client;
import org.apache.cxf.transport.http.HTTPConduit;

import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.core.api.criteria.QueryByCriteria;

import org.kuali.student.kplus2.databus.decorators.${service_class}Decorator;
import org.kuali.student.r2.common.dto.ContextInfo;
import org.kuali.student.r2.common.dto.ValidationResultInfo;
import org.kuali.student.r2.common.exceptions.DoesNotExistException;
import org.kuali.student.r2.common.exceptions.InvalidParameterException;
import org.kuali.student.r2.common.exceptions.MissingParameterException;
import org.kuali.student.r2.common.exceptions.OperationFailedException;
import org.kuali.student.r2.common.exceptions.PermissionDeniedException;

// TODO dtos

import org.kuali.student.r2.core.atp.service.${service_class};
import org.kuali.student.r2.core.constants.${service_class}Constants;

public class ${service_class}RemoteImpl extends ${service_class}Decorator {

    private String hostUrl = null;
    private String servicesUrlFragment = "/services/";
    private String serviceNameLocalPart = ${service_class}Constants.SERVICE_NAME_LOCAL_PART;
    private String namespace = ${service_class}Constants.NAMESPACE;
    private URL wsdlUrl = null;

    public String getHostUrl() {
        // check if explicitly configured, manually for testing or via spring
        if (this.hostUrl != null) {
            return hostUrl;
        }
        // check for a specific url for this service (not all services may be hosted from the same place)
        Properties config = ConfigContext.getCurrentContextConfig().getProperties();
        this.hostUrl = config.getProperty("remote.service.host.url.${interface.class.toLowerCase}");
        if (this.hostUrl != null) {
            return this.hostUrl;
        }
        // check for the default for all remote services
        this.hostUrl = config.getProperty("remote.service.host.url");
        if (this.hostUrl != null) {
            return this.hostUrl;
        }
        throw new IllegalArgumentException("No host url configured");
    }

    public void setHostUrl(String hostUrl) {
        this.hostUrl = hostUrl;
    }

    public URL getWsdlUrl() {
        if (this.wsdlUrl == null) {
            String urlString = this.getHostUrl() + this.getServicesUrlFragment() + this.getServiceNameLocalPart() + "?wsdl";
            try {
                this.wsdlUrl = new URL(urlString);
            } catch (MalformedURLException ex) {
                throw new IllegalArgumentException(urlString, ex);
            }
        }
        return wsdlUrl;
    }

    public void setWsdlUrl(URL wsdlUrl) {
        this.wsdlUrl = wsdlUrl;
    }

    public String getServiceNameLocalPart() {
        return serviceNameLocalPart;
    }

    public void setServiceNameLocalPart(String serviceNameLocalPart) {
        this.serviceNameLocalPart = serviceNameLocalPart;
    }

    public String getServicesUrlFragment() {
        return servicesUrlFragment;
    }

    public void setServicesUrlFragment(String servicesUrlFragment) {
        this.servicesUrlFragment = servicesUrlFragment;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public void init() {
        javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(
                new javax.net.ssl.HostnameVerifier() {

                    public boolean verify(String hostname,
                            javax.net.ssl.SSLSession sslSession) {
                        return true;
                    }
                });
        DummyX509TrustManager dummyTrustManager = new DummyX509TrustManager();
        final SSLContext sslContext;
        try {
            sslContext = SSLContext.getInstance("TLS");
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException(ex);
        }
        try {
            KeyManager[] kms = null;
            TrustManager[] tms = new javax.net.ssl.TrustManager[]{dummyTrustManager};
            SecureRandom sr = new java.security.SecureRandom();
            sslContext.init(kms, tms, sr);
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
        } catch (KeyManagementException ex) {
            throw new RuntimeException(ex);
        }
        QName qname = new QName(this.getNamespace(), this.getServiceNameLocalPart());
        URL url = this.getWsdlUrl();
        Service factory = Service.create(url, qname);
        ${service_class} port = factory.getPort(${service_class}.class);
        if (port == null) {
            throw new NullPointerException(url.toExternalForm());
        }
        Client proxy = ClientProxy.getClient(port);
        HTTPConduit conduit = (HTTPConduit) proxy.getConduit();
        TLSClientParameters tcp = new TLSClientParameters();
        tcp.setTrustManagers(new TrustManager[]{dummyTrustManager});
        tcp.setDisableCNCheck(true);
        conduit.setTlsClientParameters(tcp);
        this.setNextDecorator(port);
    }

    private boolean hasBeenIntialized = false;

    @Override
    public ${service_class} getNextDecorator() {
        if (!this.hasBeenIntialized) {
            synchronized (this) {
                // check the flag again within the synchronized block
                if (!this.hasBeenIntialized) {
                    this.init();
                    this.hasBeenIntialized = true;
                }
            }
        }
        return super.getNextDecorator();
    }

    // TODO
    //
    // Have to override and check each method that returns a list for null
    // This is because SOAP/JAXB renders and empty list as null 
    // but our contract standard says we ALWAYS return an empty list
    //

}
