package uk.gov.hmcts.reform.cdapi.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.cdapi.domain.CaseFlag;
import uk.gov.hmcts.reform.cdapi.domain.CaseFlagDto;
import uk.gov.hmcts.reform.cdapi.domain.Flag;
import uk.gov.hmcts.reform.cdapi.domain.FlagDetail;
import uk.gov.hmcts.reform.cdapi.domain.ListOfValue;
import uk.gov.hmcts.reform.cdapi.exception.InvalidRequestException;
import uk.gov.hmcts.reform.cdapi.exception.ResourceNotFoundException;
import uk.gov.hmcts.reform.cdapi.repository.CaseFlagRepository;
import uk.gov.hmcts.reform.cdapi.repository.IdamRepository;
import uk.gov.hmcts.reform.cdapi.repository.ListOfVenueRepository;
import uk.gov.hmcts.reform.cdapi.service.CaseFlagService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static uk.gov.hmcts.reform.cdapi.controllers.constant.Constant.CATEGORY_KEY_LANGUAGE_INTERPRETER;
import static uk.gov.hmcts.reform.cdapi.controllers.constant.Constant.CATEGORY_KEY_SIGN_LANGUAGE;
import static uk.gov.hmcts.reform.cdapi.controllers.constant.Constant.FLAG_PF0015;
import static uk.gov.hmcts.reform.cdapi.controllers.constant.Constant.FLAG_RA0042;
import static uk.gov.hmcts.reform.cdapi.util.UserInfoUtil.hasPrdRoles;

@Service
@Slf4j
public class CaseFlagServiceImpl implements CaseFlagService {

    @Autowired
    CaseFlagRepository caseFlagRepository;

    @Autowired
    ListOfVenueRepository listOfVenueRepository;

    @Autowired
    private IdamRepository idamRepository;

    @Value("${flaglist}")
    List<String> flaglistLov;

    public static final String IGNORE_JSON = "IGNORE_JSON";

    @Override
    public CaseFlag retrieveCaseFlagByServiceId(String serviceId, String flagType,
                                                String welshRequired, String availableExternalFlag) {
        var isAvailableExternalFlag = availableExternally(availableExternalFlag);
        List<CaseFlagDto> dbCaseFlagDtoList = caseFlagRepository.findAll(serviceId.trim().toUpperCase(),
                                                                         isAvailableExternalFlag);
        var caseFlagDtoList = filterCaseFlags(dbCaseFlagDtoList, isAvailableExternalFlag);
        var flagDetails = addTopLevelFlag(caseFlagDtoList, welshRequired);
        addChildLevelFlag(caseFlagDtoList, flagDetails, welshRequired, isAvailableExternalFlag);
        if (isAvailableExternalFlag) {
            removeFlags(flagDetails);
        }
        addOtherFlag(flagDetails, welshRequired);
        log.info("Added other flag");
        var flag = new Flag();
        flag.setFlagDetails(filterFlagType(flagDetails, flagType));
        if (flag.getFlagDetails().isEmpty()) {
            throw new ResourceNotFoundException("Data not found");
        }
        var flags = new ArrayList<Flag>();
        flags.add(flag);
        var caseFlag = new CaseFlag();
        caseFlag.setFlags(flags);
        return caseFlag;
    }

    private Boolean availableExternally(String availableExternalFlag) {
        if (hasPrdRoles(idamRepository.getUserInfo(getUserToken()))) {
            return true;
        }
        return StringUtils.isEmpty(availableExternalFlag)
            || availableExternalFlag.trim().equalsIgnoreCase("y");
    }

    private String getUserToken() {
        var jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return jwt.getTokenValue();
    }

    private List<CaseFlagDto> filterCaseFlags(List<CaseFlagDto> caseFlagDtoList, boolean isAvailableExternalFlag) {
        if (!isAvailableExternalFlag) {
            return caseFlagDtoList;
        }
        return caseFlagDtoList.stream().filter(caseFlagDto -> {
            Boolean externallyAvailable = caseFlagDto.getExternallyAvailable();
            return externallyAvailable == null || externallyAvailable.booleanValue();
        }).toList();
    }

    private void removeFlags(List<FlagDetail> flagDetails) {
        if (flagDetails == null) {
            return;
        }
        flagDetails.stream().forEach(flagDetail -> removeFlags(flagDetail.getChildFlags()));
        flagDetails.removeIf(flagDetail ->
                                 (flagDetail.getChildFlags() == null
                                     || flagDetail.getChildFlags().isEmpty())
                                     && flagDetail.getParent());
    }

    /**
     * Adding top level flag i.e case & party.
     *
     * @param caseFlagDtoList caseFlagDtoList
     * @return list of flagdetail with toplevel flags
     */
    public List<FlagDetail> addTopLevelFlag(List<CaseFlagDto> caseFlagDtoList,
                                            String welshRequired) {
        var flagDetails = new ArrayList<FlagDetail>();
        for (CaseFlagDto caseFlagDto : caseFlagDtoList) {
            //creating top level flags
            if (caseFlagDto.getCategoryId() == 0) {
                var flagDetail = FlagDetail.builder()
                    .name(caseFlagDto.getValueEn())
                    .flagCode(caseFlagDto.getFlagCode())
                    .nativeFlagCode(caseFlagDto.getNativeFlagCode())
                    .flagComment(caseFlagDto.getRequestReason())
                    .parent(caseFlagDto.getIsParent())
                    .hearingRelevant(caseFlagDto.getHearingRelevant())
                    .path(Arrays.stream(caseFlagDto.getCategoryPath().split("/")).toList())
                    .childFlags(new ArrayList<>())
                    .id(caseFlagDto.getId())
                    .cateGoryId(caseFlagDto.getCategoryId());
                if ((StringUtils.isNotEmpty(welshRequired)
                    && (welshRequired.trim().equalsIgnoreCase("y")))) {
                    flagDetail.nameCy(this.setNullValue(caseFlagDto.getValueCy()))
                        .defaultStatus(caseFlagDto.getDefaultStatus())
                        .externallyAvailable(caseFlagDto.getExternallyAvailable());
                } else {
                    this.ignoreNameCy(flagDetail);
                    flagDetail.defaultStatus(caseFlagDto.getDefaultStatus())
                        .externallyAvailable(caseFlagDto.getExternallyAvailable());
                }
                FlagDetail flagDetailObj = flagDetail.build();
                flagDetails.add(flagDetailObj);
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
                                  String welshRequired, boolean isAvailableExternalFlag) {
        var isWelshRequired = this.getFlagYorN(welshRequired);
        for (CaseFlagDto caseFlagDto : caseFlagDtoList) {
            //creating child level flags
            if (isAvailableExternalFlag && Boolean.FALSE.equals(caseFlagDto.getExternallyAvailable())
                && BooleanUtils.isNotTrue(caseFlagDto.getIsParent())) {
                continue;
            }
            if (caseFlagDto.getCategoryId() != 0) {
                var childFlag = FlagDetail.builder()
                    .name(caseFlagDto.getValueEn())
                    .flagCode(caseFlagDto.getFlagCode())
                    .nativeFlagCode(caseFlagDto.getNativeFlagCode())
                    .flagComment(caseFlagDto.getRequestReason())
                    .parent(caseFlagDto.getIsParent())
                    .hearingRelevant(caseFlagDto.getHearingRelevant())
                    .path(Arrays.stream(caseFlagDto.getCategoryPath().split("/")).toList())
                    .cateGoryId(caseFlagDto.getCategoryId())
                    .id(caseFlagDto.getId());
                this.setCaseFlagByWelshRequired(isWelshRequired, childFlag, caseFlagDto);
                FlagDetail childFlagObj = childFlag.build();
                if (flaglistLov.contains(caseFlagDto.getFlagCode())) {
                    retrieveListOfValues(childFlagObj, isWelshRequired);
                }
                addChildFlag(flagDetails, childFlagObj, isWelshRequired);
            }
        }
        log.info("Added all child flag");
    }

    private void setCaseFlagByWelshRequired(boolean isWelshRequired, FlagDetail.FlagDetailBuilder childFlag,
                                            CaseFlagDto caseFlagDto) {
        if (isWelshRequired) {
            childFlag.nameCy(this.setNullValue(caseFlagDto.getValueCy()))
                .defaultStatus(caseFlagDto.getDefaultStatus())
                .externallyAvailable(caseFlagDto.getExternallyAvailable());
        } else {
            this.ignoreNameCy(childFlag);
            childFlag.defaultStatus(caseFlagDto.getDefaultStatus())
                .externallyAvailable(caseFlagDto.getExternallyAvailable());
        }
    }

    private void setChildCaseFlagByWelshRequired(boolean isWelshRequired, FlagDetail newChildFlag) {
        if (isWelshRequired) {
            newChildFlag.setNameCy(this.setNullValue(newChildFlag.getNameCy()));
            newChildFlag.setDefaultStatus(newChildFlag.getDefaultStatus());
            newChildFlag.setExternallyAvailable(newChildFlag.getExternallyAvailable());
        } else {
            this.ignoreNameCyValue(newChildFlag);
            newChildFlag.setDefaultStatus(newChildFlag.getDefaultStatus());
            newChildFlag.setExternallyAvailable(newChildFlag.getExternallyAvailable());
        }
    }

    private void ignoreNameCyValue(FlagDetail childFlag) {
        childFlag.setNameCy(IGNORE_JSON);
    }

    private String setNullValue(String value) {
        return ObjectUtils.isEmpty(value) ? null : value;
    }

    private void ignoreNameCy(FlagDetail.FlagDetailBuilder flagDetail) {
        flagDetail.nameCy(IGNORE_JSON);
    }

    private boolean getFlagYorN(String flag) {
        return (StringUtils.isNotEmpty(flag)
            && (flag.trim().equalsIgnoreCase("y")));
    }

    /**
     * Retrieve list of values based on switch condition.
     *
     * @param childFlag       flag detail object
     * @param isWelshRequired welsh flag
     */
    private void retrieveListOfValues(FlagDetail childFlag, boolean isWelshRequired) {
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
        int listOfValuesSize = ObjectUtils.isNotEmpty(listOfValues) ? listOfValues.size() : null;
        if (!isWelshRequired && listOfValuesSize > 0) {
            listOfValues.forEach(lov -> lov.setValueCy(IGNORE_JSON));
        }
        childFlag.setListOfValuesLength(listOfValuesSize);
        childFlag.setListOfValues(listOfValues);
        log.info("Added Lov: " + listOfValuesSize);
    }

    /**
     * This method will run recursively to add child flag based on condition.
     *
     * @param flagDetails     list of existing flags.
     * @param newChildFlag    new child flag which need to be added.
     * @param isWelshRequired flag
     */
    private void addChildFlag(List<FlagDetail> flagDetails, FlagDetail newChildFlag, boolean isWelshRequired) {
        if (null == flagDetails) {
            return;
        }

        for (FlagDetail flagDetail : flagDetails) {
            if (flagDetail.getId().equals(newChildFlag.getCateGoryId())) {
                this.setChildCaseFlagByWelshRequired(isWelshRequired, newChildFlag);
                flagDetail.getChildFlags().add(newChildFlag);
                break;
            }
            this.addChildFlag(flagDetail.getChildFlags(), newChildFlag, isWelshRequired);
        }
    }

    /**
     * Adding other flag based on condition.
     *
     * @param flagDetails   list of all flags
     * @param welshRequired it is flag decide to display the name_cy values
     */
    private void addOtherFlag(List<FlagDetail> flagDetails,
                              String welshRequired) {
        if (null == flagDetails) {
            return;
        }
        var isWelshRequired = this.getFlagYorN(welshRequired);
        for (FlagDetail flagDetail : flagDetails) {
            if (Boolean.TRUE.equals(flagDetail.getParent())
                && (ObjectUtils.isNotEmpty(flagDetail.getChildFlags()) && !(flagDetail.getChildFlags().isEmpty()))) {
                flagDetail.getChildFlags().add(otherFlagBuilder(
                    flagDetail
                        .getChildFlags()
                        .stream()
                        .findFirst().orElseThrow().getPath(),
                    isWelshRequired
                ));
            }
            addOtherFlag(flagDetail.getChildFlags(), welshRequired);
        }
    }

    private FlagDetail otherFlagBuilder(List<String> path, boolean isWelshRequired) {
        String nameCy = "Arall";
        if (!isWelshRequired) {
            nameCy = IGNORE_JSON;
        }
        return FlagDetail.builder()
            .name("Other")
            .flagCode("OT0001")
            .nativeFlagCode("OT0001")
            .hearingRelevant(true)
            .parent(false)
            .defaultStatus("Requested")
            .nameCy(nameCy)
            .externallyAvailable(true)
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
                flagType.trim())).toList();
        return flagDetail;
    }
}
