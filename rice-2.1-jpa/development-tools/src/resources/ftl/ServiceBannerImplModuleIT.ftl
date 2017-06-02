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

import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.kuali.student.r2.common.dto.ContextInfo;
import org.kuali.student.r2.core.atp.dto.AtpInfo;
import ${service.pacakge};
import ${service_constants_package};

public class ${service_class}BannerImpl${module}IT {

    public ${service_class}BannerImpl${module}IT() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    private ${service_class} instance;
    private ContextInfo contextInfo;

    @Before
    public void setUp() {
        ${service_class}BannerImpl${module} bannerImpl;
        try {
            bannerImpl = new ${service_class}BannerImpl${module}();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        instance = bannerImpl;

        // setup context
        contextInfo = new ContextInfo();
        contextInfo.setPrincipalId("ITTESTER");
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testGetAtp() throws Exception {
        System.out.println("getAtp");
        String atpId = "201510";
        AtpInfo result = instance.getAtp(atpId, contextInfo);
        assertEquals("201510", result.getId());
        System.out.println(result.getName());
        assertEquals("Fall 2014", result.getDescr().getPlain());
    }

    @Test
    public void testGetAtpIdsByType() throws Exception {
        System.out.println("getAtpIdsByType");
        String atpTypeKey = ${service_class}Constants.ATP_FALL_TYPE_KEY;
        List<String> expResult = new ArrayList<String>();
        expResult.add("201510");
        List<String> result = instance.getAtpIdsByType(atpTypeKey, contextInfo);
        assertFalse(result.contains("198810"));
        assertEquals(expResult, result);
    }

}
