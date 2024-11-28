package uk.gov.hmcts.reform.cdapi.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;


@Data
@Getter
@Setter
@Entity(name = "list_of_values")
public class ListOfValueDto {
    @EmbeddedId
    private CategoryKey categoryKey;

    @Column(name = "value_en")
    private String valueEn;

    @Column(name = "value_cy")
    private String valueCy;

    @Column(name = "hinttext_en")
    private String hintTextEn;

    @Column(name = "hinttext_cy")
    private String hintTextCy;

    @Column(name = "lov_order")
    private Long lovOrder;
    @Column(name = "parentcategory")
    private String parentCategory;
    @Column(name = "parentkey")
    private String parentKey;

    private String active;
    @Column(name = "external_reference_type")
    private String externalReferenceType;
    @Column(name = "external_reference")
    private String externalReference;
}


