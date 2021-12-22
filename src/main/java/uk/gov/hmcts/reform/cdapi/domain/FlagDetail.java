package uk.gov.hmcts.reform.cdapi.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class FlagDetail {
    @JsonIgnore
    private Integer id;
    private String name;
    private Boolean hearingRelevant;
    private Boolean flagComment;
    private String flagCode;
    @JsonIgnore
    private Integer cateGoryId;
    @JsonProperty("isParent")
    private Boolean parent;
    @JsonProperty("Path")
    private List<String> path;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Builder.Default
    private List<FlagDetail> childFlags = new ArrayList<>();
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer listOfValuesLength;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<ListOfValue> listOfValues;
}