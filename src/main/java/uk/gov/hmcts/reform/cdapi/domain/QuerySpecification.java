package uk.gov.hmcts.reform.cdapi.domain;

import org.springframework.data.jpa.domain.Specification;

public class QuerySpecification {

    private QuerySpecification() {

    }

    public static Specification<ListOfValueDto> categoryKey(String categoryKey) {
        return (root, query, builder) ->
            categoryKey == null ? builder.conjunction() :
                builder.equal(builder.lower(root.get("categoryKey").get("categoryKey")),
                              categoryKey.toLowerCase().trim());
    }

    /**
     * if serviceId ==  no service id or blank or null or incorrect then all
     * records for the category with no service id are fetched.
     * if service is provided : filter records to fetch only those with service ids
     * if service id does not exist and list was empty fetch again for the category with no service ids
     */
    public static Specification<ListOfValueDto> serviceId(String serviceId) {

        return (root, query, builder) -> {
            if (serviceId == null) {
                return builder.conjunction();
            } else if (serviceId.equalsIgnoreCase("null")
                || serviceId.isBlank()) {
                return builder.equal(root.get("categoryKey").get("serviceId"), "");
            } else {
                return  builder.equal(builder.lower(root.get("categoryKey").get("serviceId")),
                    serviceId.toLowerCase().trim());
            }
        };

    }

    /**
     * if parentcategory == null then specification is ignored.
     */
    public static Specification<ListOfValueDto> parentCategory(String parentCategory) {
        return (root, query, builder) ->
            parentCategory == null ? builder.conjunction() :
                builder.equal(builder.lower(root.get("parentCategory")), parentCategory.toLowerCase().trim());
    }

    /**
     * if parentkey == null then specification is ignored.
     */
    public static Specification<ListOfValueDto> parentKey(String parentKey) {
        return (root, query, builder) ->
            parentKey == null ? builder.conjunction() :
                builder.equal(builder.lower(root.get("parentKey")), parentKey.toLowerCase().trim());

    }

    /**
     * if key == null then specification is ignored.
     */
    public static Specification<ListOfValueDto> key(String key) {
        return (root, query, builder) ->
            key == null ? builder.conjunction() :
                builder.equal(builder.lower(root.get("categoryKey").get("key")), key.toLowerCase().trim());

    }

}
