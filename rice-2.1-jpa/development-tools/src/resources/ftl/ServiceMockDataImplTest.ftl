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
package org.kuali.student.kplus2.databus.adapters;

import org.kuali.student.kplus2.databus.mockdata.${service_class}MockBannerDataImpl;
import org.kuali.student.kplus2.databus.mapimpl.${service_class}MapImpl;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kuali.student.r2.common.dto.ContextInfo;
import org.kuali.student.r2.core.atp.dto.AtpInfo;
import org.kuali.student.r2.core.constants.${service_class}Constants;

/**
 *
 * @author eghm
 */
public class ${service_class}MockDataImplTest {
    
    public ${service_class}MockDataImplTest() {
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

    //
    // TODO
    //
    /**
     * Test 
     */
    @Test
    public void testGetData() throws Exception {
        System.out.println("getData");
        ${service_class}MockBannerDataImpl instance = new ${service_class}MockBannerDataImpl();
        instance.setNextDecorator(new ${service_class}MapImpl ());
        instance.init();
        
        ContextInfo contextInfo = new ContextInfo ();
        contextInfo.setPrincipalId ("UNITTESTER");
        List<String> ids = instance.getAtpIdsByType(${service_class}Constants.ATP_FALL_TYPE_KEY, contextInfo);
        assertEquals(1, ids.size());
        assertEquals ("2015FA", ids.get (0));
        
        AtpInfo atpInfo = instance.getAtp("2015FA", contextInfo);
        assertEquals ("2015FA", atpInfo.getId());
        assertEquals ("2015FA", atpInfo.getCode());
    }
    
}
