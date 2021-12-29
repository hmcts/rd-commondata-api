package uk.gov.hmcts.reform.cdapi.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import uk.gov.hmcts.reform.cdapi.domain.HearingChannel;
import uk.gov.hmcts.reform.cdapi.domain.HearingChannelDto;
import uk.gov.hmcts.reform.cdapi.helper.CrdTestSupport;
import uk.gov.hmcts.reform.cdapi.repository.HearingChannelRepository;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CrdServiceImplTest {

    @InjectMocks
    CrdServiceImpl crdServiceImpl;

    @Mock
    HearingChannelRepository hearingChannelRepository;

    @Test
    void retrieveHearingChannelsByCategoryId() {
        List<HearingChannelDto> hearingChannelDtos = new ArrayList<>();
        hearingChannelDtos.add(CrdTestSupport.createHearingChannelDtoMock("HearingChannel", null,
                                                                          null, null));
        when(hearingChannelRepository.findAll(ArgumentMatchers.<Specification<HearingChannelDto>>any()))
            .thenReturn(hearingChannelDtos);

        List<HearingChannel> result = crdServiceImpl.retrieveHearingChannelsByCategoryId(
            "HearingChannel", null, null, null);

        assertNotNull(result);
        assertEquals(hearingChannelDtos.get(0).getCategoryKey().getKey(), result.get(0).getKey());
        assertEquals(hearingChannelDtos.get(0).getCategoryKey().getCategoryKey(), result.get(0).getCategoryKey());
        assertEquals(hearingChannelDtos.get(0).getActive(), result.get(0).getActive());
    }

    @Test
    void retrieveHearingChannelsByAllParams() {
        List<HearingChannelDto> hearingChannelDtos = new ArrayList<>();
        hearingChannelDtos.add(CrdTestSupport.createHearingChannelDtoMock("HearingChannel", "BBA3",
                                                                          "HearingChannel",
                                                                          "telephone"));
        when(hearingChannelRepository.findAll(ArgumentMatchers.<Specification<HearingChannelDto>>any()))
            .thenReturn(hearingChannelDtos);

        List<HearingChannel> result = crdServiceImpl.retrieveHearingChannelsByCategoryId(
            "HearingChannel", "BBA3", "HearingChannel", "telephone");

        assertNotNull(result);
        assertEquals(hearingChannelDtos.get(0).getCategoryKey().getKey(), result.get(0).getKey());
        assertEquals(hearingChannelDtos.get(0).getCategoryKey().getCategoryKey(), result.get(0).getCategoryKey());
        assertEquals(hearingChannelDtos.get(0).getActive(), result.get(0).getActive());
    }

    @Test
    void retrieveHearingChannelsByUnEqualParams() {
        List<HearingChannelDto> hearingChannelDtos = new ArrayList<>();
        when(hearingChannelRepository.findAll(ArgumentMatchers.<Specification<HearingChannelDto>>any()))
            .thenReturn(hearingChannelDtos);

        List<HearingChannel> result = crdServiceImpl.retrieveHearingChannelsByCategoryId(
            "HearingChannel", null, null, null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
