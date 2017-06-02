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

import org.kuali.student.kplus2.databus.common.ResetDatabaseSchema;
import org.kuali.student.kplus2.databus.mockdata.${service_class}MockDataImpl;
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.kuali.student.kplus2.databus.mockdata.${service_class}MockDataImpl${module};
import org.kuali.student.r2.common.dto.ContextInfo;
import org.kuali.student.r2.core.atp.dto.AtpInfo;
import ${service_class};

/**
 *
 * @author eghm
 */
@Ignore ("unless you have oracle running and the test user setup")
public class ${service_class}BannerImpl${module}Test {

    //
    // WARNING: in order to run this unit test you need to create a test user in oracle
    //
    // run drop_recreate_user_BANNER_TEST.cmd (in test/resources)
    // which runs drop_recreate_user_BANNER_TEST.sql
    // 

    private static final String DDL_FILE_NAME = System.getProperty("ddl.file.name", "/banner_oracle_test_schema.sql");

    private ${service_class} instance;
    private ${service_class}BannerImpl${module} bannerImpl;
    private ContextInfo contextInfo;


    public ${service_class}BannerImpl${module}Test() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        ResetDatabaseSchema rds = new ResetDatabaseSchema();
        try {
            bannerImpl = new ${service_class}BannerImpl${module}();
            bannerImpl.setCreateAtp(true);
            rds.setConnection(bannerImpl.getJDBCConnection());
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        rds.setDdlFileName(DDL_FILE_NAME);
        rds.reset();

        ${service_class}MockDataImpl dataImpl = new ${service_class}MockDataImpl${module}();
        dataImpl.setNextDecorator(bannerImpl);
        dataImpl.init();
        instance = dataImpl;

        // setup context
        contextInfo = new ContextInfo();
        contextInfo.setPrincipalId("UNITTESTER");
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testGetAtp() throws Exception {
        System.out.println("getAtp");
        for (String[] term: ${service_class}MockDataImpl${module}.terms) {
            AtpInfo result = instance.getAtp(term[1] + term[2], contextInfo);
            assertEquals(term[1] + term[2], result.getId());
            assertEquals(term[3], result.getName());
            assertEquals(term[3], result.getDescr().getPlain());
        }
    }

    @Test
    public void testGetAtpIdsByType() throws Exception {
        System.out.println("getAtpIdsByType");
        for (String[] term: ${service_class}MockDataImpl${module}.terms) {
            List<String> expResult = new ArrayList<String>();
            expResult.add(term[1] + term[2]);
            List<String> result = instance.getAtpIdsByType(bannerImpl.code2Type(term[2]), contextInfo);
            assertEquals(expResult, result);
        }
    }

}
