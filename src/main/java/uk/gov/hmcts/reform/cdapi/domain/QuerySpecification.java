package uk.gov.hmcts.reform.cdapi.domain;

import org.springframework.data.jpa.domain.Specification;

public class QuerySpecification {

    private QuerySpecification() {

    }

    public static Specification<HearingChannelDto> hearingChannelCategoryKey(String categoryKey) {
        return (root, query, builder) ->
            categoryKey == null ? builder.conjunction() :
                builder.equal(builder.lower(root.get("categoryKey").get("categoryKey")),
                              categoryKey.toLowerCase().trim());
    }

    /**
     * if serviceId == null then specification is ignored.
     */
    public static Specification<HearingChannelDto> hearingChannelServiceId(String serviceId) {
        return (root, query, builder) ->
            serviceId == null ? builder.conjunction() :
                builder.equal(builder.lower(root.get("serviceId")), serviceId.toLowerCase().trim());
    }

    /**
     * if parentcategory == null then specification is ignored.
     */
    public static Specification<HearingChannelDto> hearingChannelParentCategory(String parentCategory) {
        return (root, query, builder) ->
            parentCategory == null ? builder.conjunction() :
                builder.equal(builder.lower(root.get("parentCategory")), parentCategory.toLowerCase().trim());
    }

    /**
     * if parentkey == null then specification is ignored.
     */
    public static Specification<HearingChannelDto> hearingChannelParentKey(String parentKey) {
        return (root, query, builder) ->
            parentKey == null ? builder.conjunction() :
                builder.equal(builder.lower(root.get("parentKey")), parentKey.toLowerCase().trim());

    }

}
