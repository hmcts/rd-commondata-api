package uk.gov.hmcts.reform.cdapi.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.cdapi.domain.CaseFlag;
import uk.gov.hmcts.reform.cdapi.domain.CaseFlagDto;
import uk.gov.hmcts.reform.cdapi.domain.Flag;
import uk.gov.hmcts.reform.cdapi.domain.FlagDetail;
import uk.gov.hmcts.reform.cdapi.domain.ListOfValue;
import uk.gov.hmcts.reform.cdapi.exception.ResourceNotFoundException;
import uk.gov.hmcts.reform.cdapi.repository.CaseFlagRepository;
import uk.gov.hmcts.reform.cdapi.repository.ListOfVenueRepository;
import uk.gov.hmcts.reform.cdapi.service.CaseFlagService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static uk.gov.hmcts.reform.cdapi.controllers.constant.Constant.CATEGORY_KEY_LANGUAGE_INTERPRETER;
import static uk.gov.hmcts.reform.cdapi.controllers.constant.Constant.CATEGORY_KEY_SIGN_LANGUAGE;
import static uk.gov.hmcts.reform.cdapi.controllers.constant.Constant.FLAG_PF0015;
import static uk.gov.hmcts.reform.cdapi.controllers.constant.Constant.FLAG_RA0042;

@Service
public class CaseFlagServiceImpl implements CaseFlagService {

    @Autowired
    CaseFlagRepository caseFlagRepository;

    @Autowired
    ListOfVenueRepository listOfVenueRepository;

    @Value("${flaglist}")
    List<String> flaglistLov;

    @Override
    public CaseFlag retrieveCaseFlagByServiceId(String serviceId, String flagType) {
        var caseFlag = new CaseFlag();
        var flag = new Flag();
        var flags = new ArrayList<Flag>();
        var caseFlagDtoList = caseFlagRepository.findAll(serviceId.trim().toUpperCase());
        var flagDetails = addTopLevelFlag(caseFlagDtoList);
        addChildLevelFlag(caseFlagDtoList, flagDetails);
        addOtherFlag(flagDetails);
        flag.setFlagDetails(filterFlagType(flagDetails, flagType));
        if (flag.getFlagDetails().size() == 0)
            throw new ResourceNotFoundException("Data not found");
        flags.add(flag);
        caseFlag.setFlags(flags);
        return caseFlag;
    }

    /**
     * Adding top level flag i.e case & party.
     *
     * @param caseFlagDtoList caseFlagDtoList
     * @return list of flagdetail with toplevel flags
     */
    public List<FlagDetail> addTopLevelFlag(List<CaseFlagDto> caseFlagDtoList) {
        var flagDetails = new ArrayList<FlagDetail>();
        for (CaseFlagDto caseFlagDto : caseFlagDtoList) {
            //creating top level flags
            if (caseFlagDto.getCategoryId() == 0) {
                var flagDetail = FlagDetail.builder()
                    .name(caseFlagDto.getValueEn())
                    .flagCode(caseFlagDto.getFlagCode())
                    .flagComment(caseFlagDto.getRequestReason())
                    .parent(caseFlagDto.getIsParent())
                    .hearingRelevant(caseFlagDto.getHearingRelevant())
                    .path(Arrays.stream(caseFlagDto.getCategoryPath().split("/")).collect(Collectors.toList()))
                    .childFlags(new ArrayList<>())
                    .id(caseFlagDto.getId())
                    .cateGoryId(caseFlagDto.getCategoryId()).build();
                flagDetails.add(flagDetail);
            }
        }
        return flagDetails;
    }

    /**
     * Adding child level flag
     *
     * @param caseFlagDtoList caseFlagDtoList
     * @param flagDetails     list of flagdetail with toplevel flags
     */
    public void addChildLevelFlag(List<CaseFlagDto> caseFlagDtoList, List<FlagDetail> flagDetails) {
        for (CaseFlagDto caseFlagDto : caseFlagDtoList) {
            //creating child level flags
            if (caseFlagDto.getCategoryId() != 0) {

                var childFlag = FlagDetail.builder()
                    .name(caseFlagDto.getValueEn())
                    .flagCode(caseFlagDto.getFlagCode())
                    .flagComment(caseFlagDto.getRequestReason())
                    .parent(caseFlagDto.getIsParent())
                    .hearingRelevant(caseFlagDto.getHearingRelevant())
                    .path(Arrays.stream(caseFlagDto.getCategoryPath().split("/")).collect(Collectors.toList()))
                    .cateGoryId(caseFlagDto.getCategoryId())
                    .id(caseFlagDto.getId()).build();
                if (flaglistLov.contains(caseFlagDto.getFlagCode())) {
                    retrieveListOfValues(childFlag);
                }
                addChildFlag(flagDetails, childFlag);
            }
        }
    }

    /**
     * Retrieve list of values based on switch condition
     *
     * @param childFlag flag detail object
     */
    private void retrieveListOfValues(FlagDetail childFlag) {
        List<ListOfValue> listOfValues = new ArrayList<>();
        switch (childFlag.getFlagCode()) {
            case FLAG_PF0015:
                listOfValues = listOfVenueRepository.findListOfValues(CATEGORY_KEY_LANGUAGE_INTERPRETER);
                break;
            case FLAG_RA0042:
                listOfValues = listOfVenueRepository.findListOfValues(CATEGORY_KEY_SIGN_LANGUAGE);
                break;
        }
        childFlag.setChildFlags(null);
        childFlag.setListOfValuesLength(listOfValues.size());
        childFlag.setListOfValues(listOfValues);
    }

    /**
     * This method will run recursively to add child flag based on condition.
     *
     * @param flagDetails  list of existing flags.
     * @param newChildFlag new child flag which need to be added.
     */
    private void addChildFlag(List<FlagDetail> flagDetails, FlagDetail newChildFlag) {
        if (null == flagDetails)
            return;
        for (FlagDetail flagDetail : flagDetails) {
            if (flagDetail.getId().equals(newChildFlag.getCateGoryId())) {
                flagDetail.getChildFlags().add(newChildFlag);
                break;
            }
            this.addChildFlag(flagDetail.getChildFlags(), newChildFlag);
        }
    }

    /**
     * Adding other flag based on condition
     *
     * @param flagDetails list of all flags
     */
    private void addOtherFlag(List<FlagDetail> flagDetails) {
        if (null == flagDetails)
            return;
        for (FlagDetail flagDetail : flagDetails) {
            if (flagDetail.getParent()) {
                flagDetail.getChildFlags().add(otherFlagBuilder(flagDetail.getChildFlags().stream().findFirst().get().getPath()));
            }
            addOtherFlag(flagDetail.getChildFlags());
        }
    }

    private FlagDetail otherFlagBuilder(List<String> path) {
        var otherFlag = FlagDetail.builder()
            .name("Other")
            .flagCode("OT0001")
            .hearingRelevant(true)
            .parent(false)
            .childFlags(new ArrayList<>())
            .path(path)
            .flagComment(true).build();
        return otherFlag;
    }

    private List<FlagDetail> filterFlagType(List<FlagDetail> flagDetail, String flagType) {
        flagDetail = (StringUtils.isEmpty(flagType)) ? flagDetail : flagDetail.stream().filter(f1 -> f1.getName().equalsIgnoreCase(
            flagType)).collect(Collectors.toList());
        return flagDetail;
    }
}