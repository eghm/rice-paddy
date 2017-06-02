/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information regarding copyright ownership. The ASF licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.kuali.student.kplus2.databus.rest;

import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import javax.xml.ws.WebServiceContext;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.student.kplus2.databus.common.BaseAuthenticationLogic;
import org.kuali.student.r2.common.dto.ContextInfo;
import org.kuali.student.r2.common.exceptions.DoesNotExistException;
import org.kuali.student.r2.common.exceptions.InvalidParameterException;
import org.kuali.student.r2.common.exceptions.MissingParameterException;
import org.kuali.student.r2.common.exceptions.OperationFailedException;
import org.kuali.student.r2.common.exceptions.PermissionDeniedException;
import org.kuali.student.r2.core.atp.dto.AtpInfo;
import ${service_package};

public class ${service_class}RestImpl extends BaseAuthenticationLogic implements ${service_class}Rest {

    public ${service_class}RestImpl() {
//        System.setProperty("javax.net.ssl.trustStore",
//                "D:/svn/kplus2/kplus2-ks/kplus2-remote-services-impl/src/test/resources/certificate-chain/kdb.truststore");
//        System.setProperty("javax.net.ssl.trustStorePassword", "kdbpoc");
    }

    private ${service_class} atpService;

    public ${service_class} get${service_class}() {
        return atpService;
    }

    public void set${service_class}(${service_class} atpService) {
        this.atpService = atpService;
    }

    protected ContextInfo getContext(String contextKey) {
        ContextInfo contextInfo = new ContextInfo();
        contextInfo.setAuthenticatedPrincipalId("ANONYMOUS_GUEST");
        contextInfo.setPrincipalId("ANONYMOUS_GUEST");
        contextInfo.setCurrentDate(new Date());
        return contextInfo;
    }

    protected ContextInfo getFixContext(String contextKey) throws PermissionDeniedException {
        ContextInfo contextInfo = this.getContext(contextKey);
//        contextInfo = this.fixPrincipals(contextInfo);
        return contextInfo;
    }

    //
    // TODO this for approriate methods
    //
    @Override
    public List<AtpInfo> getAtps(String contextKey)
            throws InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException {
        QueryByCriteria criteria = QueryByCriteria.Builder.create().build();
        List<AtpInfo> infos = this.atpService.searchForAtps(criteria, getFixContext(contextKey));
        return infos;
    }

    @Override
    public AtpInfo getAtp(String atpId, String contextKey)
            throws DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException {
        AtpInfo info = this.atpService.getAtp(atpId, getFixContext(contextKey));
        return info;
    }
}
