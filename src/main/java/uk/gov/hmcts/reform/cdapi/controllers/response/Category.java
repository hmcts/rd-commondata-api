package uk.gov.hmcts.reform.cdapi.controllers.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class Category {

    private String categoryKey;
    @JsonIgnore
    private String serviceId;

    private String key;

    private String valueEn;

    private String valueCy;

    private String hintTextEn;

    private String hintTextCy;

    private Long lovOrder;

    private String parentCategory;

    private String parentKey;

    private String activeFlag;

    @Setter
    private List<Category> childNodes;

}
