package uk.gov.hmcts.reform.cdapi.domain;

import lombok.Data;

import java.util.List;

@Data
public class CaseFlag {
    private List<Flag> flags;
}
