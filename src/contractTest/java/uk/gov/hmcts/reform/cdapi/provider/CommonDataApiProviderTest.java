package uk.gov.hmcts.reform.cdapi.provider;

import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import au.com.dius.pact.provider.junitsupport.IgnoreNoPactsToVerify;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;
import au.com.dius.pact.provider.junitsupport.loader.PactBroker;
import au.com.dius.pact.provider.junitsupport.loader.VersionSelector;
import au.com.dius.pact.provider.spring.junit5.MockMvcTestTarget;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.reform.cdapi.controllers.CaseFlagApiController;
import uk.gov.hmcts.reform.cdapi.controllers.CrdApiController;
import uk.gov.hmcts.reform.cdapi.domain.CaseFlagDto;
import uk.gov.hmcts.reform.cdapi.domain.CategoryKey;
import uk.gov.hmcts.reform.cdapi.domain.HearingChannelDto;
import uk.gov.hmcts.reform.cdapi.domain.ListOfValue;
import uk.gov.hmcts.reform.cdapi.repository.CaseFlagRepository;
import uk.gov.hmcts.reform.cdapi.repository.HearingChannelRepository;
import uk.gov.hmcts.reform.cdapi.repository.ListOfVenueRepository;
import uk.gov.hmcts.reform.cdapi.service.impl.CaseFlagServiceImpl;
import uk.gov.hmcts.reform.cdapi.service.impl.CrdServiceImpl;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.nonNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@Provider("referenceData_commondata")
@PactBroker(scheme = "${PACT_BROKER_SCHEME:http}",
    host = "${PACT_BROKER_URL:localhost}",
    port = "${PACT_BROKER_PORT:80}", consumerVersionSelectors = {
    @VersionSelector(tag = "master")})
@ContextConfiguration(classes = {CaseFlagApiController.class, CrdApiController.class, CrdServiceImpl.class,
    CaseFlagServiceImpl.class})
@TestPropertySource(properties = {"loggingComponentName=CommonDataApiProviderTest"})
@IgnoreNoPactsToVerify
public class CommonDataApiProviderTest {

    @MockBean
    CaseFlagRepository caseFlagRepository;

    @MockBean
    ListOfVenueRepository listOfVenueRepository;

    @Autowired
    CaseFlagApiController caseFlagApiController;

    @Autowired
    CrdApiController crdApiController;

    @MockBean
    HearingChannelRepository hearingChannelRepository;

    @TestTemplate
    @ExtendWith(PactVerificationInvocationContextProvider.class)
    void pactVerificationTestTemplate(PactVerificationContext context) {
        if (context != null) {
            context.verifyInteraction();
        }
    }

    @BeforeEach
    void before(PactVerificationContext context) {
        MockMvcTestTarget testTarget = new MockMvcTestTarget();
        testTarget.setControllers(
            caseFlagApiController,crdApiController);
        if (nonNull(context)) {
            context.setTarget(testTarget);
        }
    }

    @State({"Case Flag Details Exist"})
    public void toReturnCaseFlagByServiceId() {
        ListOfValue listOfValue = new ListOfValue();
        listOfValue.setValue("1");
        listOfValue.setKey("EN");
        listOfValue.setValue("English");
        List<ListOfValue> listOfValues = new ArrayList<>();
        listOfValues.add(listOfValue);
        when(listOfVenueRepository.findListOfValues(anyString())).thenReturn(listOfValues);
        CaseFlagDto caseFlagDto1 = new CaseFlagDto();
        caseFlagDto1.setFlagCode("RA0001");
        caseFlagDto1.setCategoryId(0);
        caseFlagDto1.setCategoryPath("PARTY");
        caseFlagDto1.setId(1);
        caseFlagDto1.setHearingRelevant(true);
        caseFlagDto1.setRequestReason(false);
        caseFlagDto1.setValueEn("E");
        caseFlagDto1.setIsParent(false);

        CaseFlagDto caseFlagDto2 = new CaseFlagDto();
        caseFlagDto2.setFlagCode("CF0001");
        caseFlagDto2.setCategoryId(0);
        caseFlagDto2.setCategoryPath("CASE");
        caseFlagDto2.setId(2);
        caseFlagDto2.setHearingRelevant(true);
        caseFlagDto2.setRequestReason(false);
        caseFlagDto2.setValueEn("E");
        caseFlagDto2.setIsParent(false);

        List<CaseFlagDto> caseFlagDtos = new ArrayList<>();
        caseFlagDtos.add(caseFlagDto1);
        caseFlagDtos.add(caseFlagDto2);
        when(caseFlagRepository.findAll(anyString())).thenReturn(caseFlagDtos);
    }

    @State({"HearingChannels Details Exist"})
    public void toReturnHearingChannelsByCategoryId() {
        HearingChannelDto hearingChannelDto1 = new HearingChannelDto();
        hearingChannelDto1.setParentKey("telephone");
        hearingChannelDto1.setParentCategory("HearingChannel");
        hearingChannelDto1.setLovOrder(1L);
        hearingChannelDto1.setActive(true);
        hearingChannelDto1.setHintTextCy(null);
        hearingChannelDto1.setHintTextEn(null);
        hearingChannelDto1.setValueCy(null);
        hearingChannelDto1.setValueEn("Telephone - BTMeetme");
        CategoryKey categoryKey = new CategoryKey();
        hearingChannelDto1.setKey("telephone-btMeetMe");
        hearingChannelDto1.setCategoryKey(categoryKey);

        HearingChannelDto hearingChannelDto2 = new HearingChannelDto();
        hearingChannelDto2.setParentKey("video");
        hearingChannelDto2.setParentCategory("HearingChannel");
        hearingChannelDto2.setLovOrder(2L);
        hearingChannelDto2.setActive(true);
        hearingChannelDto2.setHintTextCy(null);
        hearingChannelDto2.setHintTextEn(null);
        hearingChannelDto2.setValueCy(null);
        hearingChannelDto2.setValueEn("Video - CVP");
        CategoryKey categoryKey1 = new CategoryKey();
        hearingChannelDto2.setKey("telephone-CVP");
        hearingChannelDto2.setCategoryKey(categoryKey1);

        List<HearingChannelDto> hearingChannelDtos = new ArrayList<>();
        hearingChannelDtos.add(hearingChannelDto1);
        hearingChannelDtos.add(hearingChannelDto2);
        when(hearingChannelRepository.findAll(ArgumentMatchers.<Specification<HearingChannelDto>>any()))
            .thenReturn(hearingChannelDtos);
    }
}
