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
import org.kuali.student.kplus2.databus.mockdata.${service_class}MockBannerDataImpl;
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.student.r2.common.dto.ContextInfo;
import org.kuali.student.r2.core.atp.dto.AtpInfo;
import ${service_package};
import ${service_constants_package};

/**
 *
 * @author eghm
 */
//@Ignore ("unless you have set up a test user in mysql")
public class ${service_class}BannerImplTest {

    //
    // WARNING: in order to run this unit test you need to create a test user in mysql
    //
    // Login in as root.
    // 
    //mysql> create user dxtera identified by 'dxtera';
    //Query OK, 0 rows affected (0.00 sec)
    //
    //mysql> create database dxtera;
    //Query OK, 1 row affected (0.02 sec)
    //
    //mysql> grant all privileges on dxtera.* to 'dxtera';
    //Query OK, 0 rows affected (0.00 sec)
    public ${service_class}BannerImplTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    private static final String DDL_FILE_NAME = "/banner_mysql_test_schema.sql";

    private ${service_class} instance;
    private ContextInfo contextInfo;

    @Before
    public void setUp() {
        ResetDatabaseSchema rds = new ResetDatabaseSchema();
        ${service_class}BannerImpl bannerImpl;
        try {
            bannerImpl = new ${service_class}BannerImpl();
            rds.setConnection(bannerImpl.getJDBCConnection());
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        rds.setDdlFileName(DDL_FILE_NAME);
        rds.reset();

        ${service_class}MockBannerDataImpl dataImpl = new ${service_class}MockBannerDataImpl();
        dataImpl.setNextDecorator(bannerImpl);
        instance = dataImpl;
        dataImpl.init();
        // setup context
        contextInfo = new ContextInfo();
        contextInfo.setPrincipalId("UNITTESTER");
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getAtp method, of class ${service_class}BannerImpl.
     */
    @Test
    public void testGetAtp() throws Exception {
        System.out.println("getAtp");
        String atpId = "2015FA";
        AtpInfo result = instance.getAtp(atpId, contextInfo);
        assertEquals("2015FA", result.getId());
        assertEquals("Fall 2014-15", result.getName());
        assertEquals("Fall 2014-15", result.getDescr().getPlain());
   
        System.out.println("getAtpIdsByType");
        String atpTypeKey = ${service_class}Constants.ATP_FALL_TYPE_KEY;
        List<String> expResult = new ArrayList<>();
        expResult.add("2015FA");
        List<String> atpIds = instance.getAtpIdsByType(atpTypeKey, contextInfo);
        assertEquals(expResult, atpIds);
   
        QueryByCriteria criteria = QueryByCriteria.Builder.create().build();
        List<AtpInfo> infos = instance.searchForAtps(criteria, contextInfo);
        System.out.println (infos);
        assertNotNull(infos);
    }
}
