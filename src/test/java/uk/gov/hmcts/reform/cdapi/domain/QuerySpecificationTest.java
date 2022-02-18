package uk.gov.hmcts.reform.cdapi.domain;

import org.assertj.core.api.Assertions;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QuerySpecificationTest {

    @Mock
    Specification<ListOfValueDto> specMock;
    @Mock
    Root<ListOfValueDto> root;
    @Mock
    CriteriaQuery<ListOfValueDto> query;
    @Mock
    CriteriaBuilder builder;
    @Mock
    Predicate predicate;
    @Mock
    Path<Object> pathObj;

    @Test
    void retrieveCategoryKey() {
        when(root.get(anyString())).thenReturn(pathObj);
        when(specMock.toPredicate(root, query, builder)).thenReturn(predicate);

        Specification<ListOfValueDto> result = QuerySpecification.categoryKey("HearingChannel");
        result = result.and(specMock);
        Assertions.assertThat(result).isNotNull();
        MatcherAssert.assertThat(result.toPredicate(root, query, builder), is(predicate));

    }

    @Test
    void retrieveCategoryKey_withNull() {

        Specification<ListOfValueDto> result = QuerySpecification.categoryKey(null);
        result = result.and(specMock);

        Assertions.assertThat(result).isNotNull();
        MatcherAssert.assertThat(result.toPredicate(root, query, builder), is(nullValue()));
    }

    @Test
    void retrieveParentCategory() {
        when(specMock.toPredicate(root, query, builder)).thenReturn(predicate);

        Specification<ListOfValueDto> result = QuerySpecification.parentCategory("HearingChannel");

        result = result.and(specMock);
        Assertions.assertThat(result).isNotNull();
        MatcherAssert.assertThat(result.toPredicate(root, query, builder), is(predicate));
    }

    @Test
    void retrieveParentCategory_withNull() {

        Specification<ListOfValueDto> result = QuerySpecification.parentCategory(null);
        result = result.and(specMock);

        Assertions.assertThat(result).isNotNull();
        MatcherAssert.assertThat(result.toPredicate(root, query, builder), is(nullValue()));
    }

    @Test
    void retrieveServiceId() {
        when(root.get(anyString())).thenReturn(pathObj);
        when(specMock.toPredicate(root, query, builder)).thenReturn(predicate);

        Specification<ListOfValueDto> result = QuerySpecification.serviceId("BBA3");

        result = result.and(specMock);
        Assertions.assertThat(result).isNotNull();
        MatcherAssert.assertThat(result.toPredicate(root, query, builder), is(predicate));
    }

    @Test
    void retrieveServiceId_withNull() {

        Specification<ListOfValueDto> result = QuerySpecification.serviceId(null);
        result = result.and(specMock);

        Assertions.assertThat(result).isNotNull();
        MatcherAssert.assertThat(result.toPredicate(root, query, builder), is(nullValue()));
    }

    @Test
    void retrieveParentKey() {
        when(specMock.toPredicate(root, query, builder)).thenReturn(predicate);

        Specification<ListOfValueDto> result = QuerySpecification.parentKey("telephone");

        result = result.and(specMock);
        Assertions.assertThat(result).isNotNull();
        MatcherAssert.assertThat(result.toPredicate(root, query, builder), is(predicate));
    }

    @Test
    void retrieveParentKey_withNull() {

        Specification<ListOfValueDto> result = QuerySpecification.parentKey(null);
        result = result.and(specMock);

        Assertions.assertThat(result).isNotNull();
        MatcherAssert.assertThat(result.toPredicate(root, query, builder), is(nullValue()));
    }
}
