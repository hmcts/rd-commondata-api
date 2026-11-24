//package uk.gov.hmcts.reform.cdapi;
//
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import io.restassured.response.Response;
//import net.serenitybdd.annotations.WithTag;
//import net.serenitybdd.annotations.WithTags;
//import org.junit.jupiter.api.Disabled;
//import org.junit.jupiter.params.ParameterizedTest;
//import org.junit.jupiter.params.provider.MethodSource;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.core.env.Environment;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.TestPropertySource;
//import uk.gov.hmcts.reform.lib.util.serenity5.SerenityTest;
//
//import java.io.IOException;
//import java.nio.charset.StandardCharsets;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.List;
//import java.util.stream.Stream;
//
//@Disabled
//@SpringBootTest
//@WithTags({@WithTag("testType:Functional")})
//@ActiveProfiles("functional")
//public class CommonDataApiWithoutSuppressFunctionalTest extends AuthorizationFunctionalTest {
//    private static final String SLASH = "/";
//    private static final String PATH_CASE_FLAGS = SLASH.concat("caseflags");
//
//    @Autowired
//    Environment environment;
//
//    static Stream<String> provideServiceCodes(){
//
//        return Stream.of("BFA1"
////                "PF0001", "SM0001", "RA0001", "RA0002", "RA0003", "RA0004", "RA0005", "RA0006", "RA0007", "RA0008", "RA0009", "RA0010", "RA0011", "RA0012", "RA0013", "RA0014", "RA0015", "RA0016", "RA0017", "RA0018", "RA0019", "RA0020", "RA0021", "RA0022", "RA0023", "RA0024", "RA0025", "RA0026", "RA0027", "RA0028", "RA0029", "RA0030", "RA0031", "RA0032", "RA0033", "RA0034", "RA0035", "RA0036", "RA0037", "RA0038", "RA0039", "RA0040", "RA0041", "RA0042", "RA0043", "RA0044", "RA0045", "RA0046", "CF0002", "PF0002", "PF0004", "PF0007", "PF0008", "PF0009", "PF0011", "PF0012", "PF0013", "CF0003", "CF0004", "CF0005", "CF0006", "CF0007", "CF0008", "PF0014", "PF0015", "CF0009", "PF0016", "PF0017", "PF0018", "CF0010", "CF0011", "PF0019", "SM0002", "SM0003", "SM0004", "SM0005", "SM0006", "SM0007", "CF0012", "CF0013", "CF0014", "PF0020", "RA0047", "PF0021", "PF0022", "CF0015", "PF0023", "PF0024", "PF0025", "PF0026"
//        );
//    }
//
//    // CASE FLAGS LOCAL END-TO-END TESTING
//    @ParameterizedTest
//    @MethodSource("provideServiceCodes")
//    void shouldReturnCaseFlags(String serviceCode) {
//
////        RequestSpecification multipleAuthHeaders = commonDataApiClient.getMultipleAuthHeaders();
////
////        ValidatableResponse response = multipleAuthHeaders.log().all()
////                .get("/refdata/commondata" + "/service-id=" + serviceCode + "?available-external-flag=N" ,
////                        PATH_CASE_FLAGS)
////                .thenReturn().then().log().all();
//
////        commonDataApiClient.refreshProperty("");
//
//        Response response = commonDataApiClient.retrieveResponseForGivenRequest(
//                        "/service-id=" + serviceCode + "?available-external-flag=N",
//                        PATH_CASE_FLAGS
//                ).then()
//                .assertThat().statusCode(200)
//                .extract().response();
//
//        String prettyPrint = response.jsonPath().prettyPrint();
//        try {
//            Path target = Paths.get("src", "functionalTest", "resources", "FT_Not_Suppressed_" + "RA0010" + ".json");
//            Files.createDirectories(target.getParent());
//            Files.writeString(target, prettyPrint, StandardCharsets.UTF_8);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//
//        mapFlagCodeData(prettyPrint);
//
////        var caseFlag = response.as(CaseFlag.class);
////        caseFlag.getFlags().forEach(flag->{
////            flag.getFlagDetails().forEach(fd->{
////                System.out.println("FlagCode=="+fd.getFlagCode());
////            });
////        });
//    }
//
//    public void mapFlagCodeData(String jsonPayload) {
//        try {
//            ObjectMapper mapper = new ObjectMapper();
//            JsonNode rootNode = mapper.readTree(jsonPayload);
//
//            String targetName = "Other";
//            List<String> parentTagCodes = new ArrayList<>();
//
//            // Recursively search and collect all parent codes
//            findAllParentTagCodes(rootNode, targetName, null, parentTagCodes);
//
//            // Output the results
//            System.out.println("Parent flagCode(s) found for '" + targetName + "': " + parentTagCodes);
//
////            List<String> suppressedCodes = List.of("CF0001", "PF0001", "SM0001", "RA0001", "RA0002", "RA0003", "RA0004", "RA0005", "RA0006", "RA0007");
////
////            suppressedCodes.forEach(code -> {
////                if (parentTagCodes.contains(code)) {
////                    System.out.println("Suppressed code " + code + " is present in the response");
////                }
////            });
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//    private static void findAllParentTagCodes(JsonNode node, String targetName, String currentParentCode, List<String> results) {
//        if (node == null) {
//            return;
//        }
//
//        if (node.isObject()) {
//            // Check if this object is the target tag
//            if (node.has("name") && targetName.equals(node.get("name").asText())) {
//                if (currentParentCode != null) {
//                    results.add(currentParentCode);
//                }
//            }
//
//            // Establish this node's flagCode to pass down as the parent code for its children
//            String thisTagCode = node.has("nativeFlagCode") ? node.get("nativeFlagCode").asText() : currentParentCode;
//
//            // Iterate through all fields/properties of this object
//            Iterator<JsonNode> elements = node.elements();
//            while (elements.hasNext()) {
//                findAllParentTagCodes(elements.next(), targetName, thisTagCode, results);
//            }
//        } else if (node.isArray()) {
//            // Iterate through array items
//            for (JsonNode child : node) {
//                findAllParentTagCodes(child, targetName, currentParentCode, results);
//            }
//        }
//    }
//}
