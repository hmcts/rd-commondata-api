package uk.gov.hmcts.reform.cdapi.repository;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.reform.cdapi.domain.CaseFlagDto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
@TestPropertySource(properties = {"spring.config.location=classpath:application-test.yml"})
@ExtendWith(MockitoExtension.class)
@SuppressWarnings("all")
public class CaseFlagRepositoryITTest {

    @Autowired
    CaseFlagRepository caseFlagRepository;


    @ParameterizedTest
    @MethodSource("caseFlagTestArgsProvider")
    void should_return_list_of_case_flags_when_service_code_is_provided(
        String serviceId,
        List<CaseFlagDto> caseFlagDtosExpected,
        int size) {

        assertThat(caseFlagRepository).isNotNull();

        List<CaseFlagDto> caseFlagDetails = caseFlagRepository.findAll(serviceId);
        assertNotNull(caseFlagDetails);

        assertThat(size).isEqualTo(caseFlagDetails.size());

        assertThat(caseFlagDtosExpected.get(0).getId()).isEqualTo(caseFlagDetails.get(0).getId());

        assertThat(caseFlagDtosExpected.get(0).getId()).isEqualTo(caseFlagDetails.get(0).getId());
        assertThat(caseFlagDtosExpected.get(0).getFlagCode()).isEqualTo(caseFlagDetails.get(0).getFlagCode());
        assertThat(caseFlagDtosExpected.get(0).getValueCy()).isEqualTo(caseFlagDetails.get(0).getValueCy());
        assertThat(caseFlagDtosExpected.get(0).getValueEn()).isEqualTo(caseFlagDetails.get(0).getValueEn());
        assertThat(caseFlagDtosExpected.get(0).getCategoryId()).isEqualTo(caseFlagDetails.get(0).getCategoryId());

        assertThat(caseFlagDtosExpected.get(0).getCategoryPath()).isEqualTo(caseFlagDetails.get(0).getCategoryPath());
        assertThat(caseFlagDtosExpected.get(0).getHearingRelevant()).isEqualTo(caseFlagDetails.get(0).getHearingRelevant());
        assertThat(caseFlagDtosExpected.get(0).getRequestReason()).isEqualTo(caseFlagDetails.get(0).getRequestReason());
        assertThat(caseFlagDtosExpected.get(0).getIsParent()).isEqualTo(caseFlagDetails.get(0).getIsParent());


    }

    @Test
    void should_return_db_error_msg_when_service_code_is_null() {

        String serviceId = null;

        InvalidDataAccessResourceUsageException errorResponse =
            Assertions.assertThrows(InvalidDataAccessResourceUsageException.class, () ->
                caseFlagRepository.findAll(serviceId));

        assertThat(errorResponse).isNotNull();
        assertThat(errorResponse.getMessage().contains("could not extract ResultSet"));


    }


    private static Stream<Arguments> caseFlagTestArgsProvider() {

        return Stream.of(
            Arguments.of("XXXX", getCaseFlagDetails("XXXX"), 6),
            Arguments.of("AAA1", getCaseFlagDetails("AAA1"), 9),
            Arguments.of("", getCaseFlagDetails(""), 6),
            Arguments.of("BBB", getCaseFlagDetails("BBB"), 6)
        );
    }

    private static List<CaseFlagDto> getCaseFlagDetails(String serviceId) {

        List<CaseFlagDto> caseFlagDtos = new ArrayList<>();
        CaseFlagDto caseFlagDto;
        if ("AAA1".equalsIgnoreCase(serviceId)) {
            caseFlagDto = new CaseFlagDto();
            caseFlagDto.setId(1);
            caseFlagDto.setFlagCode("CATGRY");
            caseFlagDto.setValueCy(StringUtils.EMPTY);
            caseFlagDto.setValueEn("Case");
            caseFlagDto.setCategoryId(0);
            caseFlagDto.setCategoryPath(StringUtils.EMPTY);
            caseFlagDto.setHearingRelevant(false);
            caseFlagDto.setRequestReason(false);
            caseFlagDto.setIsParent(true);
            caseFlagDtos.add(caseFlagDto);
        } else {
            caseFlagDto = new CaseFlagDto();
            caseFlagDto.setId(2);
            caseFlagDto.setFlagCode("CATGRY");
            caseFlagDto.setValueCy(StringUtils.EMPTY);
            caseFlagDto.setValueEn("Party");
            caseFlagDto.setCategoryId(0);
            caseFlagDto.setCategoryPath(StringUtils.EMPTY);
            caseFlagDto.setHearingRelevant(false);
            caseFlagDto.setRequestReason(false);
            caseFlagDto.setIsParent(true);

            caseFlagDtos.add(caseFlagDto);
        }

        return caseFlagDtos;
    }
}
