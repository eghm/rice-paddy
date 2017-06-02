/**
 * Copyright 2014 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.student.service.remote.impl;

import java.util.Date;
import java.util.List;
import org.junit.*;
import static org.junit.Assert.assertNotNull;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.student.r2.common.dto.ContextInfo;
import org.kuali.student.r2.core.atp.dto.AtpInfo;

@Ignore ("because /it depends on a remote server")
public class ${service_class}RemoteImplTest {

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testGetAtps() throws Exception {
        System.out.println (System.getProperty("user.dir"));
        System.out.println (System.getProperty("home.dir"));
//        System.setProperty("javax.net.ssl.trustStore","D:/svn/kplus2/kplus2-ks/kplus2-remote-services-impl/src/test/resources/certificate-chain/kdb.truststore");
//        System.setProperty("javax.net.ssl.trustStorePassword","kdbpoc");
        ${service_class}RemoteImpl service = new ${service_class}RemoteImpl();
//        service.setHostUrl(RemoteServiceConstants.KUALI_STUDENT_ENV2);
//        service.setHostUrl(RemoteServiceConstants.KUALI_CM_DEMO);
//        service.setHostUrl(RemoteServiceConstants.LOCAL_HOST);
//        service.setHostUrl(RemoteServiceConstants.LOCAL_HOST_KDBA);
//        service.setHostUrl(RemoteServiceConstants.MIDDLESEX_KDBA);
//        service.setHostUrl(RemoteServiceConstants.BRISTOL_KDBA);
//        service.setHostUrl(RemoteServiceConstants.TBR_KDBA);
        service.setHostUrl("https://bacon.qcc.edu:8443/kdba-qcc");
        
        ContextInfo contextInfo = new ContextInfo();
        contextInfo.setPrincipalId("ANONYMOUS_GUEST");
        
        QueryByCriteria criteria = QueryByCriteria.Builder.create().build();
        List<AtpInfo> infos = service.searchForAtps(criteria, contextInfo);
        assertNotNull(infos);
        System.out.println ("list.size=" + infos.size());
    }

}
