/**
 * Copyright 2014 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 package org.kuali.student.kplus2.databus.mapimpl;

import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.student.common.mock.MockService;
import org.kuali.student.common.UUIDHelper;
import org.kuali.student.r2.common.dto.AttributeInfo;
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
import org.kuali.student.r2.core.atp.dto.AtpAtpRelationInfo;
import org.kuali.student.r2.core.atp.dto.AtpInfo;
import org.kuali.student.r2.core.atp.dto.MilestoneInfo;
import org.kuali.student.r2.core.atp.service.${service_class};
import org.kuali.student.r2.core.class1.type.dto.TypeInfo;
import org.kuali.student.r2.core.search.dto.SearchRequestInfo;
import org.kuali.student.r2.core.search.dto.SearchResultInfo;
import org.kuali.student.r2.core.search.service.SearchManager;
import org.kuali.student.r2.core.search.service.SearchService;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.kuali.student.enrollment.academicrecord.dto.StudentProgramRecordInfo;
import org.kuali.student.kplus2.databus.common.CriteriaMatcherInMemory;
import org.kuali.student.kplus2.databus.common.DateUtil;
import org.kuali.student.kplus2.databus.common.MockHelper;

/**
 * This is a mock memory based implementation for ATP service
 *
 * @author Kuali Student Team (Kamal)
 */
public class ${service_class}MapImpl implements ${service_class}, MockService {

    private Map<String, AtpInfo> atps = new HashMap<String, AtpInfo>();
    private Map<String, MilestoneInfo> milestones = new HashMap<String, MilestoneInfo>();
    private Map<String, AtpAtpRelationInfo> atpAtpRelations = new HashMap<String, AtpAtpRelationInfo>();
    private Map<String, Set<String>> milestonesForAtp = new HashMap<String, Set<String>>();
    private SearchManager searchManager;
    private SearchService searchDispatcher;

    @Override
    public void clear() {

        this.atpAtpRelations.clear();
        this.atps.clear();
        this.milestones.clear();
        this.milestonesForAtp.clear();
    }


    //
    // TODO as approriate for methods
    //
    @Override
    public AtpInfo getAtp(String atpId, ContextInfo context) throws DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException {
        AtpInfo atp = atps.get(atpId);
        if (null == atp) {
            throw new DoesNotExistException("No atp found for: " + atpId);

        }
        return new AtpInfo(atp);
    }

    /**
     * Check for missing parameter and throw localized exception if missing
     *
     * @param param
     * @param paramName
     * @throws MissingParameterException
     */
    private void checkForMissingParameter(Object param, String paramName)
            throws MissingParameterException {
        if (param == null) {
            throw new MissingParameterException(paramName + " can not be null");
        }
    }

    @Override
    public TypeInfo getSearchType(String searchTypeKey, ContextInfo contextInfo)
            throws DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException {
        checkForMissingParameter(searchTypeKey, "searchTypeKey");
        return searchManager.getSearchType(searchTypeKey, contextInfo);
    }

    @Override
    public List<TypeInfo> getSearchTypes(ContextInfo contextInfo)
            throws OperationFailedException,
            InvalidParameterException,
            MissingParameterException {
        return searchManager.getSearchTypes(contextInfo);
    }

    public SearchManager getSearchManager() {
        return searchManager;
    }

    public void setSearchManager(SearchManager searchManager) {
        this.searchManager = searchManager;
    }

    @Override
    public SearchResultInfo search(SearchRequestInfo searchRequest, ContextInfo contextInfo) throws MissingParameterException,
            OperationFailedException,
            PermissionDeniedException,
            InvalidParameterException {
        return this.searchDispatcher.search(searchRequest, contextInfo);
    }
}