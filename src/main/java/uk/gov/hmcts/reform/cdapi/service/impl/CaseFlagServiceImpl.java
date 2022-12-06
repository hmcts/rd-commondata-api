package uk.gov.hmcts.reform.cdapi.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.cdapi.domain.CaseFlag;
import uk.gov.hmcts.reform.cdapi.domain.CaseFlagDto;
import uk.gov.hmcts.reform.cdapi.domain.Flag;
import uk.gov.hmcts.reform.cdapi.domain.FlagDetail;
import uk.gov.hmcts.reform.cdapi.domain.ListOfValue;
import uk.gov.hmcts.reform.cdapi.exception.InvalidRequestException;
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
@Slf4j
public class CaseFlagServiceImpl implements CaseFlagService {

    @Autowired
    CaseFlagRepository caseFlagRepository;

    @Autowired
    ListOfVenueRepository listOfVenueRepository;

    @Value("${flaglist}")
    List<String> flaglistLov;

    @Override
    public CaseFlag retrieveCaseFlagByServiceId(String serviceId, String flagType, String welshRequired) {
        var caseFlagDtoList = caseFlagRepository.findAll(serviceId.trim().toUpperCase());
        log.info("caseFlagDtoList {} ", caseFlagDtoList);
        var flagDetails = addTopLevelFlag(caseFlagDtoList, welshRequired);
        log.info("flagDetails {} ", flagDetails);
        addChildLevelFlag(caseFlagDtoList, flagDetails, welshRequired);
        log.info("caseFlagDtoList {} ", caseFlagDtoList);
        addOtherFlag(flagDetails);
        log.info("Added other flag :: {} ", welshRequired);
        var flag = new Flag();
        flag.setFlagDetails(filterFlagType(flagDetails, flagType));
        if (flag.getFlagDetails().isEmpty()) {
            log.info("getFlagDetails is empty");
            throw new ResourceNotFoundException("Data not found");
        }
        var flags = new ArrayList<Flag>();
        flags.add(flag);
        log.info("flags :: {}", flags);
        var caseFlag = new CaseFlag();
        caseFlag.setFlags(flags);
        log.info("flags {} ", caseFlag);
        return caseFlag;
    }

    /**
     * Adding top level flag i.e case & party.
     *
     * @param caseFlagDtoList caseFlagDtoList
     * @return list of flagdetail with toplevel flags
     */
    public List<FlagDetail> addTopLevelFlag(List<CaseFlagDto> caseFlagDtoList, String welshRequired) {
        var flagDetails = new ArrayList<FlagDetail>();
        var isWelshRequired = (StringUtils.isNotEmpty(welshRequired) && (welshRequired.trim().equalsIgnoreCase("y")));
        for (CaseFlagDto caseFlagDto : caseFlagDtoList) {
            //creating top level flags
            if (caseFlagDto.getCategoryId() == 0) {
                String name = isWelshRequired ? caseFlagDto.getValueCy() : caseFlagDto.getValueEn();
                var flagDetail = FlagDetail.builder()
                    .name(name)
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
        log.info("Added top level flag : " + flagDetails.size());
        return flagDetails;
    }

    /**
     * Adding child level flag.
     *
     * @param caseFlagDtoList caseFlagDtoList
     * @param flagDetails     list of flagdetail with toplevel flags
     */
    public void addChildLevelFlag(List<CaseFlagDto> caseFlagDtoList, List<FlagDetail> flagDetails,
                                  String welshRequired) {
        for (CaseFlagDto caseFlagDto : caseFlagDtoList) {
            //creating child level flags
            if (caseFlagDto.getCategoryId() != 0) {
                String name = (StringUtils.isNotEmpty(welshRequired) && (welshRequired.trim().equalsIgnoreCase("y")))
                    ? caseFlagDto.getValueCy() : caseFlagDto.getValueEn();
                var childFlag = FlagDetail.builder()
                    .name(name)
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
        log.info("Added all child flag");
    }

    /**
     * Retrieve list of values based on switch condition.
     *
     * @param childFlag flag detail object
     */
    private void retrieveListOfValues(FlagDetail childFlag) {
        List<ListOfValue> listOfValues;
        switch (childFlag.getFlagCode()) {
            case FLAG_PF0015:
                listOfValues = listOfVenueRepository.findListOfValues(CATEGORY_KEY_LANGUAGE_INTERPRETER);
                break;
            case FLAG_RA0042:
                listOfValues = listOfVenueRepository.findListOfValues(CATEGORY_KEY_SIGN_LANGUAGE);
                break;
            default:
                throw new InvalidRequestException("invalid lov flag");
        }
        childFlag.setChildFlags(null);
        childFlag.setListOfValuesLength(listOfValues.size());
        childFlag.setListOfValues(listOfValues);
        log.info("Added Lov: " + listOfValues.size());
    }

    /**
     * This method will run recursively to add child flag based on condition.
     *
     * @param flagDetails  list of existing flags.
     * @param newChildFlag new child flag which need to be added.
     */
    private void addChildFlag(List<FlagDetail> flagDetails, FlagDetail newChildFlag) {
        if (null == flagDetails) {
            return;
        }

        for (FlagDetail flagDetail : flagDetails) {
            if (flagDetail.getId().equals(newChildFlag.getCateGoryId())) {
                flagDetail.getChildFlags().add(newChildFlag);
                break;
            }
            this.addChildFlag(flagDetail.getChildFlags(), newChildFlag);
        }
    }

    /**
     * Adding other flag based on condition.
     *
     * @param flagDetails list of all flags
     */
    private void addOtherFlag(List<FlagDetail> flagDetails) {
        if (null == flagDetails) {
            return;
        }
        for (FlagDetail flagDetail : flagDetails) {
            if (Boolean.TRUE.equals(flagDetail.getParent())) {
                flagDetail.getChildFlags().add(otherFlagBuilder(flagDetail
                                                                    .getChildFlags()
                                                                    .stream()
                                                                    .findFirst().orElseThrow().getPath()));
            }
            addOtherFlag(flagDetail.getChildFlags());
        }
    }

    private FlagDetail otherFlagBuilder(List<String> path) {
        return FlagDetail.builder()
            .name("Other")
            .flagCode("OT0001")
            .hearingRelevant(true)
            .parent(false)
            .childFlags(new ArrayList<>())
            .path(path)
            .flagComment(true).build();
    }

    private List<FlagDetail> filterFlagType(List<FlagDetail> flagDetail, String flagType) {
        log.info("FlagType: " + flagType);
        flagDetail = (StringUtils.isEmpty(flagType))
            ? flagDetail
            : flagDetail
            .stream().filter(f1 -> f1.getName().equalsIgnoreCase(
                flagType.trim())).collect(Collectors.toList());
        return flagDetail;
    }
}
