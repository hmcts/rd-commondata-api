package uk.gov.hmcts.reform.cdapi.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uk.gov.hmcts.reform.cdapi.domain.ListOfValue;

import java.util.List;

@Repository
public interface ListOfVenueRepository extends JpaRepository<ListOfValue, Long> {
    @Query(value = "select ctid, key,value_en as value,value_cy from "
        + "list_of_values where categoryKey = :categoryKey", nativeQuery = true)
    List<ListOfValue> findListOfValues(@Param("categoryKey") String categoryKey);
}
