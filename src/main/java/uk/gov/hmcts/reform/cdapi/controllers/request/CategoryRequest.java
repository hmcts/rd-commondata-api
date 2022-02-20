package uk.gov.hmcts.reform.cdapi.controllers.request;

import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryRequest {

    @ApiParam(hidden = true)
    String categoryId;

    @ApiParam(name = "serviceId", value = "Any Valid String is allowed")
    String serviceId;

    @ApiParam(name = "parentCategory", value = "Any Valid String is allowed")
    String parentCategory;

    @ApiParam(name = "parentKey", value = "Any Valid String is allowed")
    String parentKey;

    @ApiParam(name = "key", value = "Any Valid String is allowed")
    String key;

    @ApiParam(name = "isChildRequired", value = "Any Valid String is allowed")
    String isChildRequired;
}
