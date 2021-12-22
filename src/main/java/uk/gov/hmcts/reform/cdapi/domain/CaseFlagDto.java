package uk.gov.hmcts.reform.cdapi.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity
public class CaseFlagDto {
    @Id
    @Column(name = "id")
    Integer id;
    @Column(name = "flag_code")
    String flagCode;
    @Column(name = "value_en")
    String valueEn;
    @Column(name = "category_id")
    Integer categoryId;
    @Column(name = "categorypath")
    String categoryPath;
    @Column(name = "hearing_relevant")
    Boolean hearingRelevant;
    @Column(name = "request_reason")
    Boolean requestReason;
    @Column(name = "isparent")
    Boolean isParent;
}
