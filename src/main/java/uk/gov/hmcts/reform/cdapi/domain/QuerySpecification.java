package uk.gov.hmcts.reform.cdapi.domain;

import org.springframework.data.jpa.domain.Specification;

public class QuerySpecification {

    private QuerySpecification() {

    }

    public static Specification<HearingChannelDto> hearingChannelCategoryKey(String categoryKey) {
        return (root, query, builder) ->
            categoryKey == null ? builder.conjunction() :
                builder.equal(root.get("categoryKey").get("categoryKey"), categoryKey);
    }

    /**
     * if serviceId == null then specification is ignored.
     */
    public static Specification<HearingChannelDto> hearingChannelServiceId(String serviceId) {
        return (root, query, builder) ->
            serviceId == null ? builder.conjunction() :
                builder.equal(root.get("categoryKey").get("serviceId"), serviceId);
    }

    /**
     * if parentcategory == null then specification is ignored.
     */
    public static Specification<HearingChannelDto> hearingChannelParentCategory(String parentCategory) {
        return (root, query, builder) ->
            parentCategory == null ? builder.conjunction() :
                builder.equal(root.get("parentCategory"), parentCategory);
    }

    /**
     * if parentkey == null then specification is ignored.
     */
    public static Specification<HearingChannelDto> hearingChannelParentKey(String parentKey) {
        return (root, query, builder) ->
            parentKey == null ? builder.conjunction() :
                builder.equal(root.get("parentKey"), parentKey);

    }

}
