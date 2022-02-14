package uk.gov.hmcts.reform.cdapi.service.impl;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import uk.gov.hmcts.reform.cdapi.domain.HearingChannel;
import uk.gov.hmcts.reform.cdapi.domain.HearingChannelDto;
import uk.gov.hmcts.reform.cdapi.exception.ResourceNotFoundException;
import uk.gov.hmcts.reform.cdapi.helper.CrdTestSupport;
import uk.gov.hmcts.reform.cdapi.repository.HearingChannelRepository;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CrdServiceImplTest {

    @InjectMocks
    CrdServiceImpl crdServiceImpl;

    @Mock
    HearingChannelRepository hearingChannelRepository;

    @Test
    void retrieveHearingChannelsByCategoryId() {
        List<HearingChannelDto> hearingChannelDtos = new ArrayList<>();
        hearingChannelDtos.add(CrdTestSupport.createHearingChannelDtoMock("HearingChannel", null,
                                                                          null, null, null));
        when(hearingChannelRepository.findAll(ArgumentMatchers.<Specification<HearingChannelDto>>any()))
            .thenReturn(hearingChannelDtos);

        List<HearingChannel> result = crdServiceImpl.retrieveHearingChannelsByCategoryId(
            "HearingChannel", null, null, null,null,false);

        assertNotNull(result);
        assertEquals(hearingChannelDtos.get(0).getCategoryKey().getKey(), result.get(0).getKey());
        assertEquals(hearingChannelDtos.get(0).getCategoryKey().getCategoryKey(), result.get(0).getCategoryKey());
        assertEquals(hearingChannelDtos.get(0).getActive(), result.get(0).getActive());
    }

    @Test
    void retrieveHearingChannelsByAllParams() {
        List<HearingChannelDto> hearingChannelDtos = mockHearingChannelDtos();
        when(hearingChannelRepository.findAll(ArgumentMatchers.<Specification<HearingChannelDto>>any()))
            .thenReturn(hearingChannelDtos);

        List<HearingChannel> result = crdServiceImpl.retrieveHearingChannelsByCategoryId(
            "HearingChannel", "BBA3", "HearingChannel", "telephone",
            "telephone",true);

        assertNotNull(result);
        assertEquals(hearingChannelDtos.get(0).getCategoryKey().getKey(), result.get(0).getKey());
        assertEquals(hearingChannelDtos.get(0).getCategoryKey().getCategoryKey(), result.get(0).getCategoryKey());
        assertEquals(hearingChannelDtos.get(0).getActive(), result.get(0).getActive());
    }

    @NotNull
    private List<HearingChannelDto> mockHearingChannelDtos() {
        List<HearingChannelDto> hearingChannelDtos = new ArrayList<>();
        hearingChannelDtos.add(CrdTestSupport.createHearingChannelDtoMock("HearingChannel", "BBA3",
                                                                          null,
                                                                          null, "telephone"));
        hearingChannelDtos.add(CrdTestSupport.createHearingChannelDtoMock("HearingSubChannel", "BBA3",
                                                                          "HearingChannel",
                                                                          "telephone","telephone"));
        return hearingChannelDtos;
    }

    @Test
    void retrieveHearingChannelsByCategoryIdWithChildNodes() {
        List<HearingChannelDto> hearingChannelDtos = mockHearingChannelDtos();
        when(hearingChannelRepository.findAll(ArgumentMatchers.<Specification<HearingChannelDto>>any()))
            .thenReturn(hearingChannelDtos);

        List<HearingChannel> result = crdServiceImpl.retrieveHearingChannelsByCategoryId(
            "HearingChannel", null, null, null,null,true);

        assertNotNull(result);
        assertThat(result, hasSize(1));
        assertEquals(hearingChannelDtos.get(0).getCategoryKey().getKey(), result.get(0).getKey());
        assertEquals(hearingChannelDtos.get(0).getCategoryKey().getCategoryKey(), result.get(0).getCategoryKey());
        assertEquals(hearingChannelDtos.get(0).getActive(), result.get(0).getActive());
        assertEquals(hearingChannelDtos.get(0).getActive(), result.get(0).getActive());
        assertEquals(hearingChannelDtos.get(1).getCategoryKey().getKey(), result.get(0).getChildNodes().get(0).getKey());
        assertEquals(hearingChannelDtos.get(1).getCategoryKey().getCategoryKey(), result.get(0).getChildNodes().get(0)
            .getCategoryKey());
    }

    @Test
    void retrieveHearingChannelsByCategoryIdWithNoChildNodes() {
        List<HearingChannelDto> hearingChannelDtos = mockHearingChannelDtos();
        when(hearingChannelRepository.findAll(ArgumentMatchers.<Specification<HearingChannelDto>>any()))
            .thenReturn(hearingChannelDtos);

        List<HearingChannel> result = crdServiceImpl.retrieveHearingChannelsByCategoryId(
            "HearingSubChannel", null, "HearingChannel", "telephone",
            null,true);

        assertNotNull(result);
        assertEquals(hearingChannelDtos.get(0).getCategoryKey().getKey(), result.get(0).getKey());
        assertEquals(hearingChannelDtos.get(0).getCategoryKey().getCategoryKey(), result.get(0).getCategoryKey());
        assertEquals(hearingChannelDtos.get(0).getActive(), result.get(0).getActive());
        assertNull(result.get(0).getChildNodes());
    }

    @Test
    void retrieveHearingChannelsByCategoryIdWithParentCategory() {
        List<HearingChannelDto> hearingChannelDtos = mockHearingChannelDtos();
        when(hearingChannelRepository.findAll(ArgumentMatchers.<Specification<HearingChannelDto>>any()))
            .thenReturn(hearingChannelDtos);

        List<HearingChannel> result = crdServiceImpl.retrieveHearingChannelsByCategoryId(
            "HearingSubChannel", null, "HearingChannel", null,null,
            true);

        assertNotNull(result);
        assertEquals(hearingChannelDtos.get(0).getCategoryKey().getKey(), result.get(0).getKey());
        assertEquals(hearingChannelDtos.get(0).getCategoryKey().getCategoryKey(), result.get(0).getCategoryKey());
        assertEquals(hearingChannelDtos.get(0).getActive(), result.get(0).getActive());
    }

    @Test
    void retrieveHearingChannelsByCategoryIdWithIsChildFalse() {
        List<HearingChannelDto> hearingChannelDtos = mockHearingChannelDtos();
        when(hearingChannelRepository.findAll(ArgumentMatchers.<Specification<HearingChannelDto>>any()))
            .thenReturn(hearingChannelDtos);

        List<HearingChannel> result = crdServiceImpl.retrieveHearingChannelsByCategoryId(
            "HearingSubChannel", null, "HearingChannel", null,null,
            false);

        assertNotNull(result);
        assertEquals(hearingChannelDtos.get(0).getCategoryKey().getKey(), result.get(0).getKey());
        assertEquals(hearingChannelDtos.get(0).getCategoryKey().getCategoryKey(), result.get(0).getCategoryKey());
        assertEquals(hearingChannelDtos.get(0).getActive(), result.get(0).getActive());
    }

    @Test
    void retrieveHearingChannelsByCategoryIdWithParentKey() {
        List<HearingChannelDto> hearingChannelDtos = mockHearingChannelDtos();
        when(hearingChannelRepository.findAll(ArgumentMatchers.<Specification<HearingChannelDto>>any()))
            .thenReturn(hearingChannelDtos);

        List<HearingChannel> result = crdServiceImpl.retrieveHearingChannelsByCategoryId(
            "HearingSubChannel", null, null, "telephone",null,
            true);

        assertNotNull(result);
        assertEquals(hearingChannelDtos.get(0).getCategoryKey().getKey(), result.get(0).getKey());
        assertEquals(hearingChannelDtos.get(0).getCategoryKey().getCategoryKey(), result.get(0).getCategoryKey());
        assertEquals(hearingChannelDtos.get(0).getActive(), result.get(0).getActive());
    }


    @Test
    void shouldThrowNotFoundExceptionWithUnMappedParams() {
        List<HearingChannelDto> hearingChannelDtos = new ArrayList<>();

        when(hearingChannelRepository.findAll(ArgumentMatchers.<Specification<HearingChannelDto>>any()))
            .thenReturn(hearingChannelDtos);

        assertThrows(ResourceNotFoundException.class, () -> crdServiceImpl.retrieveHearingChannelsByCategoryId(
            "HearingChannel", null, null, null,null,false));
    }





}
