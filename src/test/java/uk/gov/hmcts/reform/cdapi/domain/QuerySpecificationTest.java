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
    Specification<HearingChannelDto> specMock;
    @Mock
    Root<HearingChannelDto> root;
    @Mock
    CriteriaQuery<HearingChannelDto> query;
    @Mock
    CriteriaBuilder builder;
    @Mock
    Predicate predicate;
    @Mock
    Path<Object> pathObj;

    @Test
    void retrieveHearingChannelCategoryKey() {
        when(root.get(anyString())).thenReturn(pathObj);
        when(specMock.toPredicate(root, query, builder)).thenReturn(predicate);

        Specification<HearingChannelDto> result = QuerySpecification.hearingChannelCategoryKey("HearingChannel");
        result = result.and(specMock);
        Assertions.assertThat(result).isNotNull();
        MatcherAssert.assertThat(result.toPredicate(root, query, builder), is(predicate));

    }

    @Test
    void retrieveHearingChannelCategoryKey_withNull() {

        Specification<HearingChannelDto> result = QuerySpecification.hearingChannelCategoryKey(null);
        result = result.and(specMock);

        Assertions.assertThat(result).isNotNull();
        MatcherAssert.assertThat(result.toPredicate(root, query, builder), is(nullValue()));
    }

    @Test
    void retrieveHearingChannelParentCategory() {
        when(specMock.toPredicate(root, query, builder)).thenReturn(predicate);

        Specification<HearingChannelDto> result = QuerySpecification.hearingChannelParentCategory("HearingChannel");

        result = result.and(specMock);
        Assertions.assertThat(result).isNotNull();
        MatcherAssert.assertThat(result.toPredicate(root, query, builder), is(predicate));
    }

    @Test
    void retrieveHearingChannelParentCategory_withNull() {

        Specification<HearingChannelDto> result = QuerySpecification.hearingChannelParentCategory(null);
        result = result.and(specMock);

        Assertions.assertThat(result).isNotNull();
        MatcherAssert.assertThat(result.toPredicate(root, query, builder), is(nullValue()));
    }

    @Test
    void retrieveHearingChannelServiceId() {
        when(root.get(anyString())).thenReturn(pathObj);
        when(specMock.toPredicate(root, query, builder)).thenReturn(predicate);

        Specification<HearingChannelDto> result = QuerySpecification.hearingChannelServiceId("BBA3");

        result = result.and(specMock);
        Assertions.assertThat(result).isNotNull();
        MatcherAssert.assertThat(result.toPredicate(root, query, builder), is(predicate));
    }

    @Test
    void retrieveHearingChannelServiceId_withNull() {

        Specification<HearingChannelDto> result = QuerySpecification.hearingChannelServiceId(null);
        result = result.and(specMock);

        Assertions.assertThat(result).isNotNull();
        MatcherAssert.assertThat(result.toPredicate(root, query, builder), is(nullValue()));
    }

    @Test
    void retrieveHearingChannelParentKey() {
        when(specMock.toPredicate(root, query, builder)).thenReturn(predicate);

        Specification<HearingChannelDto> result = QuerySpecification.hearingChannelParentKey("telephone");

        result = result.and(specMock);
        Assertions.assertThat(result).isNotNull();
        MatcherAssert.assertThat(result.toPredicate(root, query, builder), is(predicate));
    }

    @Test
    void retrieveHearingChannelParentKey_withNull() {

        Specification<HearingChannelDto> result = QuerySpecification.hearingChannelParentKey(null);
        result = result.and(specMock);

        Assertions.assertThat(result).isNotNull();
        MatcherAssert.assertThat(result.toPredicate(root, query, builder), is(nullValue()));
    }
}
