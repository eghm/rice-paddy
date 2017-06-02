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
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.student.r2.common.dto.ContextInfo;
import org.kuali.student.r2.core.atp.dto.AtpInfo;
import ${service_package};
import ${service_constants_package};

/**
 *
 * @author eghm
 */
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
//@Ignore ("unless you have set up a test user in mysql see above")
public class ${service_class}JenzabarImplTest {

    public ${service_class}JenzabarImplTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    private static final String DDL_FILE_NAME = "/jenzabar_mysql_test_schema.sql";

    private ${service_class} instance;
    private ContextInfo contextInfo;

    @Before
    public void setUp() {
        ResetDatabaseSchema rds = new ResetDatabaseSchema();
        ${service_class}JenzabarImpl jenzabarImpl;
        try {
            jenzabarImpl = new ${service_class}JenzabarImpl();
            rds.setConnection(jenzabarImpl.getJDBCConnection());
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        rds.setDdlFileName(DDL_FILE_NAME);
        rds.reset();

        ${service_class}MockDataImpl dataImpl = new ${service_class}MockDataImpl();
        dataImpl.setNextDecorator(jenzabarImpl);
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
     * Test of getAtp method, of class ${service_class}JenzabarImpl.
     */
    @Test
    public void testGetAtp() throws Exception {
        System.out.println("getAtp");
        String atpId = "UNDG.2014.FA";
        AtpInfo result = instance.getAtp(atpId, contextInfo);
        assertEquals("UNDG.2014.FA", result.getId());
        assertEquals("FA of 2014", result.getName());
        assertEquals("FA of 2014", result.getDescr().getPlain());
    }

    @Test
    public void testGetAtpIdsByType() throws Exception {
        System.out.println("getAtpIdsByType");
        String atpTypeKey = ${service_class}Constants.ATP_FALL_TYPE_KEY;
        List<String> expResult = new ArrayList<String>();
        expResult.add("UNDG.2014.FA");
        List<String> result = instance.getAtpIdsByType(atpTypeKey, contextInfo);
        assertEquals(expResult, result);
    }

    @Test
    public void testSearchForAtps() throws Exception {
        System.out.println("searchForAtps");
        QueryByCriteria criteria = QueryByCriteria.Builder.create().build();
        List<AtpInfo> result = instance.searchForAtps(criteria, contextInfo);
        System.out.println (result.size());
        for (AtpInfo info : result) {
            System.out.println ("atp=" + info.getId() + " " + info.getCode());
        }
        assertNotNull(result);
    }
}
