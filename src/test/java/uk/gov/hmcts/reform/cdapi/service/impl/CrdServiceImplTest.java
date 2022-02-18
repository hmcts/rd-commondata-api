package uk.gov.hmcts.reform.cdapi.service.impl;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import uk.gov.hmcts.reform.cdapi.controllers.request.CategoryRequest;
import uk.gov.hmcts.reform.cdapi.controllers.response.Category;
import uk.gov.hmcts.reform.cdapi.domain.ListOfValueDto;
import uk.gov.hmcts.reform.cdapi.exception.ResourceNotFoundException;
import uk.gov.hmcts.reform.cdapi.helper.CrdTestSupport;
import uk.gov.hmcts.reform.cdapi.repository.ListOfValuesRepository;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.reform.cdapi.helper.CrdTestSupport.buildCategoryRequest;

@ExtendWith(MockitoExtension.class)
class CrdServiceImplTest {

    @InjectMocks
    CrdServiceImpl crdServiceImpl;

    @Mock
    ListOfValuesRepository listOfValuesRepository;

    @Test
    void retrieveCategoriesByCategoryId() {
        List<ListOfValueDto> listOfValueDtos = new ArrayList<>();
        listOfValueDtos.add(CrdTestSupport.createListOfCategoriesDtoMock("HearingChannel", null,
                                                                         null, null, null));
        when(listOfValuesRepository.findAll(ArgumentMatchers.<Specification<ListOfValueDto>>any()))
            .thenReturn(listOfValueDtos);
        CategoryRequest request = buildCategoryRequest("HearingChannel",  null, null,
                                                       null,null, null);
        List<Category> result = crdServiceImpl.retrieveListOfValuesByCategory(request);

        assertNotNull(result);
        assertEquals(listOfValueDtos.get(0).getCategoryKey().getKey(), result.get(0).getKey());
        assertEquals(listOfValueDtos.get(0).getCategoryKey().getCategoryKey(), result.get(0).getCategoryKey());
        assertEquals(listOfValueDtos.get(0).getActive(), result.get(0).getActive());
    }

    @Test
    void retrieveCategoriesByAllParams() {
        List<ListOfValueDto> listOfValueDtos = mockListOfValuesDtos();
        when(listOfValuesRepository.findAll(ArgumentMatchers.<Specification<ListOfValueDto>>any()))
            .thenReturn(listOfValueDtos);

        CategoryRequest request = buildCategoryRequest("HearingChannel", "BBA3", "telephone",
                                           "HearingChannel", "telephone","y");
        List<Category> result = crdServiceImpl.retrieveListOfValuesByCategory(request);

        assertNotNull(result);
        assertEquals(listOfValueDtos.get(0).getCategoryKey().getKey(), result.get(0).getKey());
        assertEquals(listOfValueDtos.get(0).getCategoryKey().getCategoryKey(), result.get(0).getCategoryKey());
        assertEquals(listOfValueDtos.get(0).getActive(), result.get(0).getActive());
    }



    @NotNull
    private List<ListOfValueDto> mockListOfValuesDtos() {
        List<ListOfValueDto> listOfValueDtos = new ArrayList<>();
        listOfValueDtos.add(CrdTestSupport.createListOfCategoriesDtoMock("HearingChannel", "BBA3",
                                                                            null,
                                                                            null, "telephone"));
        listOfValueDtos.add(CrdTestSupport.createListOfCategoriesDtoMock("HearingSubChannel", "BBA3",
                                                                            "HearingChannel",
                                                                            "telephone", "telephone"));
        return listOfValueDtos;
    }

    @Test
    void retrieveCategoriessByCategoryIdWithChildNodes() {
        List<ListOfValueDto> listOfValueDtos = mockListOfValuesDtos();
        when(listOfValuesRepository.findAll(ArgumentMatchers.<Specification<ListOfValueDto>>any()))
            .thenReturn(listOfValueDtos);

        CategoryRequest request = buildCategoryRequest("HearingChannel",  null, null,
                                                       null,null, "y");
        List<Category> result = crdServiceImpl.retrieveListOfValuesByCategory(request);

        assertNotNull(result);
        assertThat(result, hasSize(1));
        assertEquals(listOfValueDtos.get(0).getCategoryKey().getKey(), result.get(0).getKey());
        assertEquals(listOfValueDtos.get(0).getCategoryKey().getCategoryKey(), result.get(0).getCategoryKey());
        assertEquals(listOfValueDtos.get(0).getActive(), result.get(0).getActive());
        assertEquals(listOfValueDtos.get(0).getActive(), result.get(0).getActive());
        assertEquals(listOfValueDtos.get(1).getCategoryKey().getKey(),
                     result.get(0).getChildNodes().get(0).getKey());
        assertEquals(listOfValueDtos.get(1).getCategoryKey().getCategoryKey(), result.get(0).getChildNodes().get(0)
            .getCategoryKey());
    }

    @Test
    void retrieveCategoriesByCategoryIdWithNoChildNodes() {
        List<ListOfValueDto> listOfValueDtos = mockListOfValuesDtos();
        when(listOfValuesRepository.findAll(ArgumentMatchers.<Specification<ListOfValueDto>>any()))
            .thenReturn(listOfValueDtos);

        CategoryRequest request = buildCategoryRequest("HearingSubChannel",  null, null,
                                           "HearingChannel","telephone", "y");
        List<Category> result = crdServiceImpl.retrieveListOfValuesByCategory(request);

        assertNotNull(result);
        assertEquals(listOfValueDtos.get(0).getCategoryKey().getKey(), result.get(0).getKey());
        assertEquals(listOfValueDtos.get(0).getCategoryKey().getCategoryKey(), result.get(0).getCategoryKey());
        assertEquals(listOfValueDtos.get(0).getActive(), result.get(0).getActive());
        assertNull(result.get(0).getChildNodes());
    }

    @Test
    void retrieveCategoriesByCategoryIdWithParentCategory() {
        List<ListOfValueDto> listOfValueDtos = mockListOfValuesDtos();
        when(listOfValuesRepository.findAll(ArgumentMatchers.<Specification<ListOfValueDto>>any()))
            .thenReturn(listOfValueDtos);

        CategoryRequest request = buildCategoryRequest("HearingSubChannel",  null, null,
                                                       "HearingChannel",null, "y");
        List<Category> result = crdServiceImpl.retrieveListOfValuesByCategory(request);

        assertNotNull(result);
        assertEquals(listOfValueDtos.get(0).getCategoryKey().getKey(), result.get(0).getKey());
        assertEquals(listOfValueDtos.get(0).getCategoryKey().getCategoryKey(), result.get(0).getCategoryKey());
        assertEquals(listOfValueDtos.get(0).getActive(), result.get(0).getActive());
    }

    @Test
    void retrieveCategoriesByCategoryIdWithIsChildFalse() {
        List<ListOfValueDto> listOfValueDtos = mockListOfValuesDtos();
        when(listOfValuesRepository.findAll(ArgumentMatchers.<Specification<ListOfValueDto>>any()))
            .thenReturn(listOfValueDtos);

        CategoryRequest request = buildCategoryRequest("HearingSubChannel",  null, null,
                                                       "HearingChannel",null, "n");
        List<Category> result = crdServiceImpl.retrieveListOfValuesByCategory(request);

        assertNotNull(result);
        assertEquals(listOfValueDtos.get(0).getCategoryKey().getKey(), result.get(0).getKey());
        assertEquals(listOfValueDtos.get(0).getCategoryKey().getCategoryKey(), result.get(0).getCategoryKey());
        assertEquals(listOfValueDtos.get(0).getActive(), result.get(0).getActive());
    }

    @Test
    void retrieveCategoriesByCategoryIdWithParentKey() {
        List<ListOfValueDto> listOfValueDtos = mockListOfValuesDtos();
        when(listOfValuesRepository.findAll(ArgumentMatchers.<Specification<ListOfValueDto>>any()))
            .thenReturn(listOfValueDtos);

        CategoryRequest request = buildCategoryRequest("HearingSubChannel",  null, null,
                                                       null,"telephone", "y");
        List<Category> result = crdServiceImpl.retrieveListOfValuesByCategory(request);

        assertNotNull(result);
        assertEquals(listOfValueDtos.get(0).getCategoryKey().getKey(), result.get(0).getKey());
        assertEquals(listOfValueDtos.get(0).getCategoryKey().getCategoryKey(), result.get(0).getCategoryKey());
        assertEquals(listOfValueDtos.get(0).getActive(), result.get(0).getActive());
    }


    @Test
    void shouldThrowNotFoundExceptionWithUnMappedParams() {
        List<ListOfValueDto> listOfValueDtos = new ArrayList<>();

        when(listOfValuesRepository.findAll(ArgumentMatchers.<Specification<ListOfValueDto>>any()))
            .thenReturn(listOfValueDtos);

        CategoryRequest request = buildCategoryRequest("HearingChannel",  null, null,
                                                       null,null, "n");
        assertThrows(ResourceNotFoundException.class, () -> crdServiceImpl.retrieveListOfValuesByCategory(request));
    }
}
