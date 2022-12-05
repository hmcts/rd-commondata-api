package uk.gov.hmcts.reform.cdapi.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

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
    private String value_cy;
}
