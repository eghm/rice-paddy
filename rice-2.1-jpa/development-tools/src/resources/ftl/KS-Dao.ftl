package org.kuali.student.r2.core.class1.atp.dao;

import org.kuali.student.r2.common.dao.GenericEntityDao;
import org.kuali.student.r2.core.class1.atp.model.${interface_class}Entity;

import java.util.Date;
import java.util.List;

import static javax.persistence.TemporalType.DATE;

public class ${interface_class}Dao extends GenericEntityDao<${interface_class}Entity> {

    @SuppressWarnings("unchecked")
    public List<${interface_class}Entity> getBy${interface_class}TypeId(String atpType) {
        return em.createQuery("from ${interface_class}Entity a where a.atpType=:atpType").setParameter("atpType", atpType)
                .getResultList();
    }

    public List<${interface_class}Entity> getByCode(String code) {
        return em.createNamedQuery("${interface_class}.findByCode").setParameter("code", code).getResultList();
    }

    public List<${interface_class}Entity> getByDate(Date searchDate) {
        return em.createQuery("from ${interface_class}Entity a where :searchDate between a.startDate and a.endDate")
                .setParameter("searchDate", searchDate, DATE).getResultList();
    }

    public List<${interface_class}Entity> getByDates(Date startDate, Date endDate) {
        return em.createQuery("from ${interface_class}Entity a where a.startDate >= :startDate and a.endDate <= :endDate")
                .setParameter("startDate", startDate, DATE).setParameter("endDate", endDate).getResultList();
    }

    public List<${interface_class}Entity> getByStartDateRange(Date searchDateRangeStart, Date searchDateRangeEnd) {
        return em
                .createQuery(
                        "from ${interface_class}Entity a where (a.startDate between :searchDateRangeStart and :searchDateRangeEnd)")
                .setParameter("searchDateRangeStart", searchDateRangeStart, DATE)
                .setParameter("searchDateRangeEnd", searchDateRangeEnd, DATE)
                .getResultList();
    }

    public List<${interface_class}Entity> getByStartDateRangeAndType(Date searchDateRangeStart, Date searchDateRangeEnd,
            String searchType) {
        return em
                .createQuery(
                        "from ${interface_class}Entity a where (a.startDate between :searchDateRangeStart and :searchDateRangeEnd)  and (a.atpType = :searchType)")
                .setParameter("searchDateRangeStart", searchDateRangeStart, DATE)
                .setParameter("searchDateRangeEnd", searchDateRangeEnd, DATE)
                .setParameter("searchType", searchType)
                .getResultList();
    }

    public List<${interface_class}Entity> getByDateAndType(Date searchDate, String searchTypeKey) {
        return em.createQuery("from ${interface_class}Entity a where :searchDate between a.startDate and a.endDate and atpType = :searchTypeKey")
                .setParameter("searchDate", searchDate, DATE)
                .setParameter("searchTypeKey", searchTypeKey)
                .getResultList();
    }
}
