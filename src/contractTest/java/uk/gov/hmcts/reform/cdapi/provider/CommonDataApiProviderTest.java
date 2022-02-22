package uk.gov.hmcts.reform.cdapi.provider;

import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import au.com.dius.pact.provider.junitsupport.IgnoreNoPactsToVerify;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;
import au.com.dius.pact.provider.junitsupport.loader.PactBroker;
import au.com.dius.pact.provider.junitsupport.loader.VersionSelector;
import au.com.dius.pact.provider.spring.junit5.MockMvcTestTarget;
import org.jetbrains.annotations.NotNull;
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
import uk.gov.hmcts.reform.cdapi.domain.ListOfValue;
import uk.gov.hmcts.reform.cdapi.domain.ListOfValueDto;
import uk.gov.hmcts.reform.cdapi.repository.CaseFlagRepository;
import uk.gov.hmcts.reform.cdapi.repository.ListOfValuesRepository;
import uk.gov.hmcts.reform.cdapi.repository.ListOfVenueRepository;
import uk.gov.hmcts.reform.cdapi.service.impl.CaseFlagServiceImpl;
import uk.gov.hmcts.reform.cdapi.service.impl.CrdServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
    ListOfValuesRepository listOfValuesRepository;

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

    @State({"ListOfCategories Details Exist"})
    public void toReturnListOfCategoriesWithChildNodesByCategoryId() {
        List<ListOfValueDto> listOfValueDtos = List.of(
            buildListOfValueDto("HearingChannel", "BBA3", "video", "Video",
                                null, null),
            buildListOfValueDto("HearingSubChannel", "BBA3", "video-cvp", "Video - CVP",
                                "HearingChannel", "video"),
            buildListOfValueDto("HearingSubChannel", "BBA3", "video-other",
                                "Video - Other", "HearingChannel", "video")
            );

        when(listOfValuesRepository.findAll(ArgumentMatchers.<Specification<ListOfValueDto>>any()))
            .thenReturn(listOfValueDtos);
    }

    @NotNull
    private ListOfValueDto buildListOfValueDto(String categoryId, String serviceId, String key, String value,
                                               String parentCategory, String parentKey) {
        ListOfValueDto listOfValueDto1 = new ListOfValueDto();
        listOfValueDto1.setParentKey(parentKey);
        listOfValueDto1.setParentCategory(parentCategory);
        listOfValueDto1.setLovOrder(new Random().nextLong());
        listOfValueDto1.setActive("y");
        listOfValueDto1.setHintTextCy(null);
        listOfValueDto1.setHintTextEn(null);
        listOfValueDto1.setValueCy(null);
        listOfValueDto1.setValueEn(value);
        listOfValueDto1.setServiceId(serviceId);
        CategoryKey categoryKey = new CategoryKey();
        categoryKey.setKey(key);
        categoryKey.setCategoryKey(categoryId);
        listOfValueDto1.setCategoryKey(categoryKey);
        return listOfValueDto1;
    }


}
