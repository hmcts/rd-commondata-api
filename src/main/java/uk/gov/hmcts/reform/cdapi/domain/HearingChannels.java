package uk.gov.hmcts.reform.cdapi.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HearingChannels {

    @JsonProperty("list_of_values")
    private List<HearingChannel> hearingChannels;
}
