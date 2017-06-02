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

import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

import org.kuali.student.kplus2.databus.common.MockPrincipal;
import org.kuali.student.kplus2.databus.common.MockWebServiceContext;
import org.kuali.student.kplus2.databus.mapimpl.RolePermissionServiceMapImpl;
import org.kuali.student.kplus2.databus.mapimpl.${service_class}MapImpl;
import org.kuali.student.kplus2.databus.mockdata.${service_class}MockDataImpl;
import org.kuali.student.kplus2.databus.mockdata.${service_class}DataImplHawaii;
import org.kuali.student.r2.common.dto.ContextInfo;
import org.kuali.student.r2.common.exceptions.*;
import org.kuali.student.r2.core.atp.dto.AtpInfo;
import org.kuali.student.r2.core.kim.RolePermissionServiceConstants;

/**
 *
 * @author nwright
 */
public class ${authorization_decorator_class}${module}Test {
    
    public ${authorization_decorator_class}${module}Test() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

<#list methods as method>
    ${method}
</#list>
}
