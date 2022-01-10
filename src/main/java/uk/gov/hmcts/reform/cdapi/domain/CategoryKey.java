package uk.gov.hmcts.reform.cdapi.domain;

import lombok.Data;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;

@Data
@Embeddable
public class CategoryKey implements Serializable {
    @Column(name = "categorykey")
    private String categoryKey;

}

