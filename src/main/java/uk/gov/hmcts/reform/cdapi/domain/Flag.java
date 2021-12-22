package uk.gov.hmcts.reform.cdapi.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class Flag {
    @JsonProperty("FlagDetails")
    private List<FlagDetail> flagDetails;
}
