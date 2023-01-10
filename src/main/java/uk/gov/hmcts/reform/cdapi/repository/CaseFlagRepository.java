package uk.gov.hmcts.reform.cdapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uk.gov.hmcts.reform.cdapi.domain.CaseFlagDto;

import java.util.List;

@Repository
public interface CaseFlagRepository extends JpaRepository<CaseFlagDto, Long> {


    @Query(value = "with recursive flagdetailscategorypath"
        + "    AS ( "
        + "    SELECT a.id, a.flag_code, a.value_en ,a.value_cy, a.category_id, a.value_en AS categorypath "
        + "       FROM flag_details a "
        + "        WHERE a.category_id = 0 "
        + "       UNION ALL "
        + "       SELECT b.id, b.flag_code, b.value_en, b.value_cy, b.category_id, "
        + "       concat(c.categorypath,'/',b.value_en) AS categorypath "
        + "         FROM flag_details b "
        + "       INNER JOIN flagdetailscategorypath c "
        + "               ON b.category_id = c.id "
        + "       ) "
        + "     , flagserviceconsolidated "
        + "    AS ( "
        + "       SELECT j.flag_code, j.hearing_relevant, j.request_reason, j.default_status, j.available_externally "
        + "         FROM flag_service j "
        + "        WHERE j.service_id = 'XXXX' "
        + "          AND j.flag_code NOT IN (SELECT k.flag_code "
        + "                                    FROM flag_service k "
        + "                                   WHERE upper(k.service_id) = :serviceId) "
        + "       UNION "
        + "       SELECT l.flag_code, l.hearing_relevant, l.request_reason, l.default_status, l.available_externally "
        + "         FROM flag_service l "
        + "        WHERE upper(l.service_id) = :serviceId "
        + "       ) "
        + "     , distinctcategoryid "
        + "    AS ( "
        + "       SELECT DISTINCT m.category_id "
        + "         FROM flag_details m "
        + "        WHERE m.flag_code IN (SELECT n.flag_code "
        + "                                FROM flagserviceconsolidated n) "
        + "       ) "
        + "     , relevantcategories "
        + "    AS ( "
        + "       SELECT s.id, s.flag_code, s.value_en, s.value_cy, s.category_id, s.value_en AS categorypath "
        + "         FROM flag_details s "
        + "        WHERE s.id IN (SELECT t.category_id "
        + "                         FROM distinctcategoryid t) "
        + "       UNION ALL "
        + "       SELECT u.id, u.flag_code, u.value_en, u.value_cy, u.category_id, u.value_en AS categorypath "
        + "         FROM flag_details u "
        + "       INNER JOIN relevantcategories v "
        + "               ON u.id = v.category_id "
        + "       ) "
        + "SELECT h.id, h.flag_code, h.value_en, h.value_cy, h.category_id, f.categorypath, "
        + "       h.hearing_relevant, h.request_reason, h.default_status, h.available_externally, h.isparent "
        + "  FROM flagdetailscategorypath f "
        + "INNER JOIN "
        + "( "
        + " SELECT d.id, d.flag_code, d.value_en, d.value_cy, d.category_id, d.categorypath, "
        + "        e.hearing_relevant, e.request_reason, e.default_status, e.available_externally, 'FALSE' AS isparent "
        + "   FROM flagdetailscategorypath d "
        + " INNER JOIN flagserviceconsolidated e "
        + "         ON d.flag_code = e.flag_code "
        + " UNION "
        + " SELECT p.id, 'CATGRY' AS flag_code, p.value_en, p.value_cy, p.category_id, p.categorypath, "
        + "        'FALSE' AS hearing_relevant, 'FALSE' AS request_reason, 'Active' as default_status, "
        + "'FALSE' as available_externally, 'TRUE' AS isparent "
        + "   FROM relevantcategories p "
        + ") h "
        + "ON f.id = h.category_id "
        + "UNION "
        + "SELECT q.id, 'CATGRY' AS flag_code, q.value_en, q.value_cy, q.category_id, '' AS categorypath, "
        + "       'FALSE' AS hearing_relevant, 'FALSE' AS request_reason, 'Active' as default_status, "
        + "'FALSE' as available_externally, 'TRUE' AS isparent "
        + "  FROM flag_details q "
        + " INNER JOIN relevantcategories r "
        + "         ON r.id = q.id "
        + "      WHERE r.category_id = 0 "
        + "ORDER BY 1", nativeQuery = true)
    List<CaseFlagDto> findAll(@Param("serviceId") String serviceId);
}
