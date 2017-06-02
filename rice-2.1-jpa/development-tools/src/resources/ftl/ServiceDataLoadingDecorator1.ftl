/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kuali.student.kplus2.databus.federators;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.kuali.student.kplus2.databus.decorators.${service_class}Decorator;
import org.kuali.student.kplus2.databus.mapimpl.${service_class}MapImpl;
import org.kuali.student.r2.common.dto.ContextInfo;
import org.kuali.student.r2.common.exceptions.DataValidationErrorException;
import org.kuali.student.r2.common.exceptions.InvalidParameterException;
import org.kuali.student.r2.common.exceptions.MissingParameterException;
import org.kuali.student.r2.common.exceptions.OperationFailedException;
import org.kuali.student.r2.common.exceptions.PermissionDeniedException;
import org.kuali.student.r2.common.exceptions.ReadOnlyException;
import org.kuali.student.r2.core.atp.dto.AtpInfo;
import ${service_package};
import org.kuali.student.r2.core.constants.${service_class}Constants;

public class ${service_class}DataLoadingDecorator1 extends ${service_class}Decorator implements ${service_class} {

    public ${service_class}DataLoadingDecorator1() {
        init ();
    }
    
    protected void init () {
        ${service_class} nextDecorator  = new ${service_class}MapImpl ();
        this.setNextDecorator(nextDecorator);
        ContextInfo contextInfo = new ContextInfo ();
        contextInfo.setPrincipalId("DATALOADINGDECORATOR");
        this.loadData (contextInfo);
    }

    protected void loadData (ContextInfo contextInfo) {
        this.createTerm ("2015FA", "2014-09-01", "2014-12-31", ${service_class}Constants.ATP_FALL_TYPE_KEY, contextInfo);
        this.createTerm ("2015WI", "2015-01-01", "2015-03-31", ${service_class}Constants.ATP_WINTER_TYPE_KEY, contextInfo);
        this.createTerm ("2015SP", "2015-04-01", "2015-06-30", ${service_class}Constants.ATP_SPRING_TYPE_KEY, contextInfo);
        this.createTerm ("2015SU", "2015-07-01", "2015-08-31", ${service_class}Constants.ATP_SUMMER_TYPE_KEY, contextInfo);
    }
    
    private AtpInfo createTerm (String id, String start, String end, String typeKey, ContextInfo contextInfo) {
        AtpInfo info = new AtpInfo ();
        info.setId(id);
        info.setCode(id);
        info.setTypeKey (typeKey);
        // TODO: find out the real state key for this
        info.setStateKey("Official");
        info.setStartDate(toDate (start));
        info.setEndDate(toDate (end));
        AtpInfo created;
        try {
            created = this.createAtp(info.getTypeKey(), info, contextInfo);
        } catch (DataValidationErrorException ex) {
            throw new IllegalArgumentException (ex);
        } catch (InvalidParameterException ex) {
            throw new IllegalArgumentException (ex);
        } catch (MissingParameterException ex) {
            throw new IllegalArgumentException (ex);
        } catch (OperationFailedException ex) {
            throw new IllegalArgumentException (ex);
        } catch (PermissionDeniedException ex) {
            throw new IllegalArgumentException (ex);
        } catch (ReadOnlyException ex) {
            throw new IllegalArgumentException (ex);
        }
        return created;
    }
    
    private Date toDate (String yyyymmdd) {
        DateFormat df = new SimpleDateFormat ("yyyy-MM-dd");
        Date date;
        try {
            date = df.parse(yyyymmdd);
        } catch (ParseException ex) {
            throw new IllegalArgumentException (yyyymmdd, ex);
        }
        return date;
    }    
}

