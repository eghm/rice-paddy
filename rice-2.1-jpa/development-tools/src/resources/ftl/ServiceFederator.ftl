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
package org.kuali.student.kplus2.databus.federators;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.student.r2.common.dto.ContextInfo;
import org.kuali.student.r2.common.dto.StatusInfo;
import org.kuali.student.r2.common.dto.ValidationResultInfo;
import org.kuali.student.r2.common.exceptions.AlreadyExistsException;
import org.kuali.student.r2.common.exceptions.DataValidationErrorException;
import org.kuali.student.r2.common.exceptions.DoesNotExistException;
import org.kuali.student.r2.common.exceptions.InvalidParameterException;
import org.kuali.student.r2.common.exceptions.MissingParameterException;
import org.kuali.student.r2.common.exceptions.OperationFailedException;
import org.kuali.student.r2.common.exceptions.PermissionDeniedException;
import org.kuali.student.r2.common.exceptions.ReadOnlyException;
import org.kuali.student.r2.common.exceptions.VersionMismatchException;

// TODO dtos

import ${service_package};
import org.kuali.student.r2.core.class1.type.dto.TypeInfo;
import org.kuali.student.r2.core.search.dto.SearchRequestInfo;
import org.kuali.student.r2.core.search.dto.SearchResultInfo;

/**
 * Federates multiple ${service_class}s into a single stream
 *
 * @author nwright
 */
public class ${service_class}Federator implements ${service_class} {
    
    private Map<String, ${service_class}> delegates = new LinkedHashMap<String, ${service_class}> ();

    public Map<String, ${service_class}> getDelegates() {
        return delegates;
    }

    public void setDelegates(Map<String, ${service_class}> delegates) {
        this.delegates = delegates;
    } 
    
    public String buildId (String key, String id) {
        return key + ":" + id;
    }
    
    public String parseId (String key, String id) {
        return id.substring(key.length() + 1);
    }

    //
    // TODO these for service methods
    //
    @Override
    public AtpInfo getAtp(String atpId, ContextInfo contextInfo) throws DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException {
        
        for(String key : this.delegates.keySet()){
            ${service_class} delegate = this.delegates.get(key);
            if(atpId.startsWith(key)){
                String newAtpId = parseId(key, atpId);
                AtpInfo atpInfo = delegate.getAtp(newAtpId, contextInfo);                
                atpInfo.setId(buildId(key, newAtpId));
                return atpInfo;
            }
        }        
        
        throw new DoesNotExistException();
    }


    //
    // Thsoe come from...
    //
    @Override
    public List<TypeInfo> getSearchTypes(ContextInfo contextInfo) throws InvalidParameterException,
            MissingParameterException,
            OperationFailedException {
        throw new UnsupportedOperationException();
    }

    @Override
    public TypeInfo getSearchType(String searchTypeKey, ContextInfo contextInfo) throws DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException {
        throw new UnsupportedOperationException();
    }

    @Override
    public SearchResultInfo search(SearchRequestInfo searchRequestInfo, ContextInfo contextInfo) throws MissingParameterException,
            InvalidParameterException,
            OperationFailedException,
            PermissionDeniedException {
        throw new UnsupportedOperationException();
    }    
}
