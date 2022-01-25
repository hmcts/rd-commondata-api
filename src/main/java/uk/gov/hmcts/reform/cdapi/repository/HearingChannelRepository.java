package uk.gov.hmcts.reform.cdapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import uk.gov.hmcts.reform.cdapi.domain.HearingChannelDto;


public interface HearingChannelRepository extends JpaRepository<HearingChannelDto, String>,
    JpaSpecificationExecutor<HearingChannelDto> {

}

