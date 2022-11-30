package uk.gov.hmcts.reform.cdapi.repository;

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
import uk.gov.hmcts.reform.cdapi.domain.ListOfValue;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
@TestPropertySource(properties = {"spring.config.location=classpath:application-test.yml"})
@ExtendWith(MockitoExtension.class)
@SuppressWarnings("all")
public class ListOfVenueRepositoryITTest {


    @Autowired
    ListOfVenueRepository listOfVenueRepository;


    @ParameterizedTest
    @MethodSource("listOfValuesTestArgsProvider")
    void should_return_list_of_values_when_category_key_is_provided(
        String categoryKey,
        List<ListOfValue> listOfValuesExpected,
        int size) {

        assertThat(listOfVenueRepository).isNotNull();

        List<ListOfValue> listOfValues = listOfVenueRepository.findListOfValues(categoryKey);

        assertThat(listOfValues).isNotNull();
        assertThat(size).isEqualTo(listOfValues.size());

        if (size > 0) {

            assertThat(listOfValuesExpected.get(0).getId()).isEqualTo(listOfValues.get(0).getId());
            assertThat(listOfValuesExpected.get(0).getKey()).isEqualTo(listOfValues.get(0).getKey());
            assertThat(listOfValuesExpected.get(0).getValue()).isEqualTo(listOfValues.get(0).getValue());
        }
    }

    @Test
    void should_return_db_error_msg_when_category_key_is_null() {

        String categoryKey = null;

        InvalidDataAccessResourceUsageException errorResponse =
            Assertions.assertThrows(InvalidDataAccessResourceUsageException.class, () ->
                listOfVenueRepository.findListOfValues(categoryKey));

        assertThat(errorResponse).isNotNull();
        assertThat("could not extract ResultSet".contains(errorResponse.getMessage()));


    }

    private static Stream<Arguments> listOfValuesTestArgsProvider() {

        String listingStatus = "ListingStatus";
        String listingStatusSubChannel = "ListingStatusSubChannel";

        String dummy = "dummy";
        return Stream.of(
            Arguments.of(listingStatus, getListOfValues(listingStatus), 1),
            Arguments.of(listingStatusSubChannel, getListOfValues(listingStatusSubChannel), 1),
            Arguments.of("", getListOfValues(""), 0),
            Arguments.of(dummy, getListOfValues(dummy), 0)
        );
    }

    private static List<ListOfValue> getListOfValues(String categoryKey) {

        List<ListOfValue> listOfValues = new ArrayList<>();

        ListOfValue listOfValue;

        if ("ListingStatus".equalsIgnoreCase(categoryKey)) {

            listOfValue = new ListOfValue();
            listOfValue.setId("(0,6)");
            listOfValue.setKey("test");
            listOfValue.setValue("test");

            listOfValues.add(listOfValue);
        } else if ("ListingStatusSubChannel".equalsIgnoreCase(categoryKey)) {

            listOfValue = new ListOfValue();
            listOfValue.setId("(0,8)");
            listOfValue.setKey("test");
            listOfValue.setValue("test");

            listOfValues.add(listOfValue);
        }

        return listOfValues;
    }
}
