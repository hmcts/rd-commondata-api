package uk.gov.hmcts.reform.cdapi.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import uk.gov.hmcts.reform.cdapi.domain.jsonfilter.IgnoreJsonFilter;

@Data
@Entity
public class ListOfValue {

    @Id
    @Column(name = "ctid")
    @JsonIgnore
    private String id;
    private String key;
    private String value;
    @Column(name = "value_cy")
    @JsonProperty("value_cy")
    @JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = IgnoreJsonFilter.class)
    private String valueCy;
}
