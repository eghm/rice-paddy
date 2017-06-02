/*
 * Copyright 2010 The Kuali Foundation Licensed under the
 * Educational Community License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 * http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package org.kuali.student.r2.core.atp.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.kuali.student.r2.common.dto.IdEntityInfo;
import org.kuali.student.r2.core.atp.infc.${class_name};

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "${class_name}Info", propOrder = {
                "id", "typeKey", "stateKey", "name", "descr",
                "code", "startDate", "endDate", "adminOrgId", 
                "meta", "attributes", "_futureElements" }) 

public class ${class_name}Info 
    extends IdEntityInfo 
    implements ${class_name}, Serializable, Comparable<${class_name}Info> {

    private static final long serialVersionUID = 1L;
	
    @XmlElement
    private String code;

    @XmlElement
    private Date startDate;
	
    @XmlElement
    private Date endDate;
	
    @XmlElement
    private String adminOrgId;

    @XmlAnyElement
    private List<Object> _futureElements;  
    
    
    /**
     * Constructs a new ${class_name}Info.
     */
    public ${class_name}Info() {
    }

    /**
     * Constructs a new ${class_name}Info from another ${class_name}.
     * 
     * @param atp the ATP to copy
     */
    public ${class_name}Info(${class_name} atp) {
        super(atp);

        if (atp != null) {
            this.code = atp.getCode();

            if (atp.getStartDate() != null) {
                this.startDate = new Date(atp.getStartDate().getTime());
            }

            if (atp.getEndDate() != null) {
                this.endDate = new Date(atp.getEndDate().getTime());
            }

            this.adminOrgId = atp.getAdminOrgId();
        }
    }
    
    @Override
    public String getCode() {
        return code;
    }
	
    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public Date getStartDate() {
        return startDate;
    }
	
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    @Override
    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    @Override
    public String getAdminOrgId() {
        return adminOrgId;
    }
    
    public void setAdminOrgId(String adminOrgId) {
        this.adminOrgId = adminOrgId;
    }

    /**
     * By default, an atpInfo object should be compared by the start date
     * @param o
     * @return
     */
    @Override
    public int compareTo(${class_name}Info o) {
        return this.getStartDate().compareTo(o.getStartDate());
    }
}
