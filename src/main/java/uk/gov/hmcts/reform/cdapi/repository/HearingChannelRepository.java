package uk.gov.hmcts.reform.cdapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uk.gov.hmcts.reform.cdapi.domain.HearingChannelDto;

import java.util.List;


public interface HearingChannelRepository extends JpaRepository<HearingChannelDto, String>,
    JpaSpecificationExecutor<HearingChannelDto> {

    @Query(value = "WITH RECURSIVE parentCategory AS ("
        + "select categorykey, serviceid, key, parentcategory, parentKey from mv_list_of_values "
        + "where categorykey = :categoryId and (cast(:serviceId as text) is null or serviceId = :serviceId) and "
        + "(cast(:parentCategory as text) IS NULL or parentCategory =:parentCategory) and "
        + "(cast(:parentKey as text) IS NULL or parentKey = :parentKey) and (cast(:key as text) IS NULL or key = :key) "
        + "UNION SELECT e.categorykey, e.serviceid, e.key, e.parentcategory, e.parentkey FROM mv_list_of_values e "
        + "INNER JOIN parentCategory s ON s.categorykey = e.parentcategory and s.key = e.parentkey "
        + ") select * from parentCategory", nativeQuery = true)
    List<HearingChannelDto> findAll(@Param("categoryId") String categoryId, @Param("serviceId") String serviceId,
                                    @Param("parentCategory") String parentCategory,
                                    @Param("parentKey") String parentKey, @Param("key") String key);

}

