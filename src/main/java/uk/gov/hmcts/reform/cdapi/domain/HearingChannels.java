package uk.gov.hmcts.reform.cdapi.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class HearingChannels {

    @JsonProperty("ListOfValues")
    private List<HearingChannel> hearingChannels;
}
