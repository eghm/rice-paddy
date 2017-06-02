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
package org.kuali.student.kplus2.databus.adapters;

import org.kuali.student.r2.common.dto.ContextInfo;
import org.kuali.student.r2.common.exceptions.DataValidationErrorException;
import org.kuali.student.r2.common.exceptions.InvalidParameterException;
import org.kuali.student.r2.common.exceptions.MissingParameterException;
import org.kuali.student.r2.common.exceptions.OperationFailedException;
import org.kuali.student.r2.common.exceptions.PermissionDeniedException;
import org.kuali.student.r2.common.exceptions.ReadOnlyException;
import org.kuali.student.r2.core.atp.dto.AtpInfo;
import ${service_constants_package};

public class ${service_class}BannerImpl${module} extends ${service_class}BannerImpl  {

    private boolean createAtp = false;

    public ${service_class}BannerImpl${module}() {
    }

    @Override
    protected String getAdminOrgId() {
        return "University of ${module}";
    }

    @Override
    public AtpInfo createAtp(String atpTypeKey, AtpInfo atpInfo, ContextInfo contextInfo) throws DataValidationErrorException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException,
            ReadOnlyException {
        if (!createAtp) {
            throw new OperationFailedException ("unsupported operation");
        }
        return super.createAtp(atpTypeKey, atpInfo, contextInfo);
    }

    @Override
    protected String convertAtpTypeKey2codePattern(String atpTypeKey) throws OperationFailedException {
        // STVTERM_START_DATE > 01 JAN 2006
        // Prior to 2006:
        //     Spring   = 20
        //     Summer 1 = 30
        //     Summer 2 = 40
        if (atpTypeKey.equals(${service_class}Constants.ATP_FALL_TYPE_KEY)) {
            return "%10";
        }
        if (atpTypeKey.equals(${service_class}Constants.ATP_SPRING_TYPE_KEY)) {
            return "%30";
        }
        if (atpTypeKey.equals(${service_class}Constants.ATP_SUMMER_TYPE_KEY)) {
            return "%40";
        }
        throw new OperationFailedException("Unsupported atpTypeKey " + atpTypeKey);
    }

    @Override
    protected String code2Type(String code) {
        if (code.endsWith("10")) {
            return ${service_class}Constants.ATP_FALL_TYPE_KEY;
        }
        if (code.endsWith("30")) {
            return ${service_class}Constants.ATP_SPRING_TYPE_KEY;
        }
        if (code.endsWith("40")) {
            return ${service_class}Constants.ATP_SUMMER_TYPE_KEY;
        }
        return ${service_class}Constants.ATP_ADHOC_TYPE_KEY;
    }

    @Override
    protected String getAtpIdsByTypeSQL() {
        String query = "SELECT *"
                + "\n" + "FROM STVTERM"
                + "\n" + "WHERE STVTERM_START_DATE > '01 JAN 2006'"
                + "\n" + "AND STVTERM_CODE LIKE ?";
        return query;
    }

    public void setCreateAtp(boolean createAtp) {
        this.createAtp = createAtp;
    }
}
