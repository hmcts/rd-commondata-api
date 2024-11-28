package uk.gov.hmcts.reform.cdapi.controllers.request;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
@AllArgsConstructor
public class CategoryRequest {

    @Parameter(hidden = true)
    String categoryId;

    @Parameter(name = "serviceId", description = "Any Valid String is allowed")
    String serviceId;

    @Parameter(name = "parentCategory", description = "Any Valid String is allowed")
    String parentCategory;

    @Parameter(name = "parentKey", description = "Any Valid String is allowed")
    String parentKey;

    @Parameter(name = "key", description = "Any Valid String is allowed")
    String key;

    @Parameter(name = "isChildRequired", description = "Any Valid String is allowed")
    String isChildRequired;

    @Parameter(name = "externalReferenceType", description = "Any Valid String is allowed")
    String externalReferenceType;

    @Parameter(name = "externalReference", description = "Any Valid String is allowed")
    String externalReference;

}
