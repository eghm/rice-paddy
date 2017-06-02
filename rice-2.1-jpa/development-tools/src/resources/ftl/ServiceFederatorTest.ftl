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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kuali.student.r2.common.dto.ContextInfo;
import org.kuali.student.r2.core.atp.dto.AtpInfo;
import org.kuali.student.r2.core.atp.service.${service_class};
import org.kuali.student.r2.core.constants.${service_class}Constants;

/**
 * Tests the Federator
 * @author eghm
 */
public class ${service_class}FederatorTest {
    
    public ${service_class}FederatorTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    private static final String DELEGATE1 = "delegate1";    
    private static final String DELEGATE2 = "delegate2";
    
    private ${service_class}Federator instance;
    private ${service_class} delegate1;
    private ${service_class} delegate2;

    @Before
    public void setUp() {        
        instance = new ${service_class}Federator();
        delegate1 = new ${service_class}DataLoadingDecorator1 ();
        instance.getDelegates().put(DELEGATE1, delegate1);
        delegate2 = new ${service_class}DataLoadingDecorator2 ();
        instance.getDelegates().put(DELEGATE2, delegate2);        
    }
    
    @After
    public void tearDown() {
    }

}
