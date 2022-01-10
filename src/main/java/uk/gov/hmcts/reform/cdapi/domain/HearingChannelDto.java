package uk.gov.hmcts.reform.cdapi.domain;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;


@Data
@Getter
@Setter
@Entity(name = "mv_list_of_values")
public class HearingChannelDto {
    @EmbeddedId
    private CategoryKey categoryKey;

    @Column(name = "serviceid")
    private String serviceId;

    private String key;

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

    private Boolean active;

}


