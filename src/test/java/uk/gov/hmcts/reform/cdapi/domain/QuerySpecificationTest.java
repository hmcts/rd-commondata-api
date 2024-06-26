package uk.gov.hmcts.reform.cdapi.domain;

import org.assertj.core.api.Assertions;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
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
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class QuerySpecificationTest {

    @Spy
    Specification<ListOfValueDto> specMock;
    @Spy
    Root<ListOfValueDto> root;
    @Spy
    CriteriaQuery<ListOfValueDto> query;
    @Spy
    CriteriaBuilder builder;
    @Spy
    Predicate predicate;
    @Spy
    Path<Object> pathObj;

    @Test
    void retrieveCategoryKey() {
        doReturn(pathObj).when(root).get(anyString());
        doReturn(predicate).when(specMock).toPredicate(root, query, builder);

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
        doReturn(predicate).when(specMock).toPredicate(root, query, builder);

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
        doReturn(pathObj).when(root).get(anyString());
        doReturn(predicate).when(specMock).toPredicate(root, query, builder);

        Specification<ListOfValueDto> result = QuerySpecification.serviceId("BBA3");

        result = result.and(specMock);
        Assertions.assertThat(result).isNotNull();
        MatcherAssert.assertThat(result.toPredicate(root, query, builder), is(predicate));
    }

    @Test
    void retrieveServiceId_withEmpty() {
        doReturn(pathObj).when(root).get(anyString());
        doReturn(predicate).when(specMock).toPredicate(root, query, builder);
        Specification<ListOfValueDto> result = QuerySpecification.serviceId("");
        result = result.and(specMock);
        Assertions.assertThat(result).isNotNull();
        MatcherAssert.assertThat(result.toPredicate(root, query, builder), is(predicate));
    }

    @Test
    void retrieveServiceId_withNull() {
        doReturn(pathObj).when(root).get(anyString());
        doReturn(predicate).when(specMock).toPredicate(root, query, builder);
        Specification<ListOfValueDto> result = QuerySpecification.serviceId(null);
        result = result.and(specMock);

        Assertions.assertThat(result).isNotNull();
        MatcherAssert.assertThat(result.toPredicate(root, query, builder), is(predicate));
    }

    @Test
    void retrieveParentKey() {
        doReturn(predicate).when(specMock).toPredicate(root, query, builder);
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
