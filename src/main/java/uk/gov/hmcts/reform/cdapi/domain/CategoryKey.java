package uk.gov.hmcts.reform.cdapi.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;

@Data
@Embeddable
public class CategoryKey implements Serializable {
    @Column(name = "categorykey")
    private String categoryKey;
    private String key;
    @Column(name = "serviceid")
    private String serviceId;
}

