package uk.gov.hmcts.reform.cdapi.controllers.request;


import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CategoryRequest {

    @Hidden
    String categoryId;

    @ApiParam(name = "service-id", value = "Any Valid String is allowed")
    String serviceId;

    @ApiParam(name = "parent-category", value = "Any Valid String is allowed")
    String parentCategory;

    @ApiParam(name = "parent-key", value = "Any Valid String is allowed")
    String parentKey;

    @ApiParam(name = "key", value = "Any Valid String is allowed")
    String key;

    @ApiParam(name = "is-child-required", value = "Any Valid String is allowed")
    String isChildRequired;
}
