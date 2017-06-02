package org.kuali.student.r2.core.class1.atp.model;

import org.kuali.student.r1.common.entity.KSEntityConstants;
import org.kuali.student.r2.common.dto.AttributeInfo;
import org.kuali.student.r2.common.entity.AttributeOwner;
import org.kuali.student.r2.common.entity.MetaEntity;
import org.kuali.student.r2.common.infc.Attribute;
import org.kuali.student.r2.common.util.RichTextHelper;
import org.kuali.student.r2.core.atp.dto.${interface_class}Info;
import org.kuali.student.r2.core.atp.infc.${interface_class};

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "KSEN_ATP")
@NamedQueries( value={
        @NamedQuery(name="${interface_class}.findByCode", query="from ${interface_class}Entity where atpCode = :code")
})
public class ${interface_class}Entity extends MetaEntity implements AttributeOwner<${interface_class}AttributeEntity> {

    @Column(name = "NAME")
    private String name;
    @Column(name = "ADMIN_ORG_ID")
    private String adminOrgId;
    @Column(name = "ATP_CD")
    private String atpCode;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "START_DT", nullable = false)
    private Date startDate;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "END_DT", nullable = false)
    private Date endDate;
    @Column(name = "DESCR_FORMATTED", length = KSEntityConstants.EXTRA_LONG_TEXT_LENGTH)
    private String formatted;
    @Column(name = "DESCR_PLAIN", length = KSEntityConstants.EXTRA_LONG_TEXT_LENGTH, nullable = false)
    private String plain;
    @Column(name = "ATP_TYPE", nullable = false)
    private String atpType;
    @Column(name = "ATP_STATE", nullable = false)
    private String atpState;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "owner", fetch = FetchType.EAGER, orphanRemoval = true)
    private Set<${interface_class}AttributeEntity> attributes = new HashSet<${interface_class}AttributeEntity>();

    public ${interface_class}Entity() {
    }

    public ${interface_class}Entity(${interface_class} atp) {
        super(atp);
        this.setId(atp.getId());
        this.set${interface_class}Type(atp.getTypeKey());
        this.fromDTO(atp);
    }
    
    public void fromDTO(${interface_class} atp) {
        super.fromDTO(atp);
        this.set${interface_class}Code(atp.getCode());
        this.setName(atp.getName());
        if (atp.getDescr() != null) {
            this.setDescrFormatted(atp.getDescr().getFormatted());
            this.setDescrPlain(atp.getDescr().getPlain());
        } else {
            this.setDescrFormatted(null);
            this.setDescrPlain(null);
        }
        this.setAdminOrgId(atp.getAdminOrgId());
        this.set${interface_class}State(atp.getStateKey());
        this.setStartDate(atp.getStartDate());
        this.setEndDate(atp.getEndDate());
        if (getAttributes() == null){
            this.setAttributes(new HashSet<${interface_class}AttributeEntity>());
        } else {
            this.getAttributes().clear();
        }
        for (Attribute att : atp.getAttributes()) {
            this.getAttributes().add(new ${interface_class}AttributeEntity(att, this));
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String get${interface_class}Type() {
        return atpType;
    }

    public void set${interface_class}Type(String atpType) {
        this.atpType = atpType;
    }

    public String get${interface_class}State() {
        return atpState;
    }

    public void set${interface_class}State(String atpState) {
        this.atpState = atpState;
    }

    public void setAttributes(Set<${interface_class}AttributeEntity> attributes) {
        this.attributes = attributes;

    }

    public Set<${interface_class}AttributeEntity> getAttributes() {
        return attributes;
    }

    public String getAdminOrgId() {
        return adminOrgId;
    }

    public void setAdminOrgId(String adminOrgId) {
        this.adminOrgId = adminOrgId;
    }

    public ${interface_class}Info toDto() {
        ${interface_class}Info atp = new ${interface_class}Info();
        atp.setId(getId());
        atp.setCode(atpCode);
        atp.setName(name);
        atp.setStartDate(startDate);
        atp.setEndDate(endDate);
        atp.setAdminOrgId(getAdminOrgId());
        atp.setTypeKey(atpType);
        atp.setStateKey(atpState);
        atp.setMeta(super.toDTO());
        atp.setDescr(new RichTextHelper().toRichTextInfo(getDescrPlain(), getDescrFormatted()));
        if (getAttributes() != null) {
            for (${interface_class}AttributeEntity att : getAttributes()) {
                AttributeInfo attInfo = att.toDto();
                atp.getAttributes().add(attInfo);
            }
        }

        return atp;
    }

    public String getDescrFormatted() {
        return formatted;
    }

    public void setDescrFormatted(String formatted) {
        this.formatted = formatted;
    }

    public String getDescrPlain() {
        return plain;
    }

    public void setDescrPlain(String plain) {
        this.plain = plain;
    }

    public String get${interface_class}Code() {
        return atpCode;
    }

    public void set${interface_class}Code(String atpCode) {
        this.atpCode = atpCode;
    }
}
