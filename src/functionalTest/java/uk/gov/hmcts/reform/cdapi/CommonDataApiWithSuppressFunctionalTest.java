//package uk.gov.hmcts.reform.cdapi;
//
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.node.ArrayNode;
//import io.restassured.response.Response;
//import net.serenitybdd.annotations.WithTag;
//import net.serenitybdd.annotations.WithTags;
//import org.assertj.core.api.Assertions;
//import org.junit.jupiter.api.Disabled;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.params.ParameterizedTest;
//import org.junit.jupiter.params.provider.MethodSource;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.core.env.Environment;
//import org.springframework.test.context.ActiveProfiles;
//import static org.assertj.core.api.Assertions.assertThat;
//
//import java.io.IOException;
//import java.nio.charset.StandardCharsets;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.LinkedHashSet;
//import java.util.List;
//import java.util.Set;
//import java.util.stream.Stream;
//
//import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
//
////@SerenityTest
//@Disabled
//@SpringBootTest
//@WithTags({@WithTag("testType:Functional")})
//@ActiveProfiles("functional")
//public class CommonDataApiWithSuppressFunctionalTest extends AuthorizationFunctionalTest {
//
//    private static final String SLASH = "/";
//    private static final String PATH_CASE_FLAGS = SLASH.concat("caseflags");
//    private static final ObjectMapper MAPPER = new ObjectMapper();
//    private static final Path BASE_DIR = Paths.get("src", "functionalTest", "resources", "responses");
//    private static final String OTHER = "Other";
//
//    private static final List<String> SERVICE_IDS = List.of("AAA3", "AAA6", "AAA7", "ABA1","ABA2", "ABA3", "ABA4",
//            "ABA5", "BBA2", "BCA1", "BCA2", "BCA3", "BCA4", "BFA1", "BHA1", "XXXX");
//
//    @Autowired
//    Environment environment;
//
//
//
//    @Test
//    public void exportAll() throws IOException {
//        for (String code : List.of("RA0003")) {
//            exportForCode(code);
//        }
//    }
//    private void exportForCode(String code) throws IOException {
//
//
//        SERVICE_IDS.forEach(serviceId -> {
//
//        try {
//            Path folder = BASE_DIR.resolve("code-" + code+"-"+serviceId);
//            Files.createDirectories(folder);
//
//            // 1. Unsuppressed: clear the override so the default (empty) applies
////            applySuppression("");
//            String unsuppressed = callApi(serviceId);
//            writeJson(folder.resolve("unsuppressed.json"), unsuppressed);
//
//            // 2. Suppressed: apply this code
////            applySuppression(code);
//            String suppressed = callApi(serviceId);
//            writeJson(folder.resolve("suppressed.json"), suppressed);
//
//            // 3. Assert
////            assertOtherUnderFlag(unsuppressed, code);
////            assertNoOtherUnderFlag(suppressed, code);
//            assertOnlyOtherRemovedFromFlag(code, unsuppressed, suppressed);
//
//            // 4. Report of "Other" codes (key=value)
//            writeReport(folder.resolve("other-report.properties"), unsuppressed, suppressed);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }finally {
////            applySuppression("");   // always reset so the next code starts clean
//        }
//    });
//    }
//
//    // ---------- REST calls ----------
//
////    private void applySuppression(String value) {
////        commonDataApiClient.refreshProperty(value);
////    }
//
//    private String callApi(String serviceCode) {
//        return commonDataApiClient.retrieveResponseForGivenRequest(
//                        "/service-id=" + serviceCode + "?available-external-flag=N",
//                        PATH_CASE_FLAGS
//                ).then()
//                .assertThat().statusCode(200)
//                .extract().body().asPrettyString();
//    }
//
//    // ---------- File output ----------
//
//    private void writeJson(Path file, String json) throws IOException {
//        JsonNode node = MAPPER.readTree(json);
//        Files.writeString(file,
//                MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(node),
//                StandardCharsets.UTF_8);
//    }
//
//    private void writeReport(Path file, String unsuppressedJson, String suppressedJson) throws IOException {
//        StringBuilder sb = new StringBuilder();
//        appendSection(sb, "unsuppressed", MAPPER.readTree(unsuppressedJson));
//        appendSection(sb, "suppressed", MAPPER.readTree(suppressedJson));
//        Files.writeString(file, sb.toString(), StandardCharsets.UTF_8);
//    }
//
//    private void appendSection(StringBuilder sb, String label, JsonNode root) {
//        Set<String> parentCodes = new LinkedHashSet<>();   // keeps order, no duplicate lines
//
//        for (JsonNode group : root.path("flags")) {
//            for (JsonNode detail : group.path("FlagDetails")) {
//                collectParentsOfOther(parentCodes, detail);
//            }
//        }
//
//        sb.append("# ").append(label).append(System.lineSeparator());
//        parentCodes.forEach(code ->
//                sb.append(code).append('=').append(OTHER).append(System.lineSeparator()));
//    }
//
//    private void collectParentsOfOther(Set<String> parentCodes, JsonNode flag) {
//        for (JsonNode child : flag.path("childFlags")) {
//            if (OTHER.equals(child.path("name").asText())) {
//                parentCodes.add(flag.path("nativeFlagCode").asText());
//            }
//            collectParentsOfOther(parentCodes, child);
//        }
//    }
//
//    private JsonNode findFlag(JsonNode root, String flagCode) {
//        for (JsonNode group : root.path("flags")) {
//            for (JsonNode detail : group.path("FlagDetails")) {
//                JsonNode found = findTagRecursive(detail, flagCode);
//                if (found != null) return found;
//            }
//        }
//        return null;
//    }
//
//    private JsonNode findTagRecursive(JsonNode flag, String flagCode) {
//        if (flagCode.equals(flag.path("nativeFlagCode").asText())) return flag;
//        for (JsonNode child : flag.path("childFlags")) {
//            JsonNode found = findTagRecursive(child, flagCode);
//            if (found != null) return found;
//        }
//        return null;
//    }
//
//    /** True if the flag has a direct child named Happy. */
//    private boolean hasOtherChild(JsonNode flag) {
//        for (JsonNode child : flag.path("childFlags")) {
//            if (OTHER.equals(child.path("name").asText())) return true;
//        }
//        return false;
//    }
//
//    private Set<String> tagsWithOtherChild(String json) throws IOException {
//        Set<String> codes = new LinkedHashSet<>();
//        JsonNode root = MAPPER.readTree(json);
//        for (JsonNode group : root.path("tags")) {
//            for (JsonNode detail : group.path("TagDetails")) {
//                collectParentsOfOther(codes, detail);
//            }
//        }
//        return codes;
//    }
//
//    private void assertOnlyOtherRemovedFromFlag(String targetFlag, String unsuppressedJson, String suppressedJson)
//            throws IOException {
//
//        JsonNode expected = MAPPER.readTree(unsuppressedJson);          // fresh mutable copy
//        JsonNode flag = findFlag(expected, targetFlag);
//        assertThat(flag).isNotNull();
//
//        ArrayNode children = (ArrayNode) flag.path("childFlags");
//        for (int i = children.size() - 1; i >= 0; i--) {
//            if (OTHER.equals(children.get(i).path("name").asText())) {
//                children.remove(i);
//            }
//        }
//
//        assertThat(MAPPER.readTree(suppressedJson))
//                .as("Suppressed response should equal unsuppressed minus Other under flag %s", targetFlag)
//                .isEqualTo(expected);
//    }
//
//    private void assertOtherSuppressedOnlyFor(String targetTag, String unsuppressedJson, String suppressedJson)
//            throws IOException {
//
//        Set<String> before = tagsWithOtherChild(unsuppressedJson);
//        Set<String> after  = tagsWithOtherChild(suppressedJson);
//
//        // 1. Happy is present under the target tag before suppression
//        assertThat(before)
//                .as("Unsuppressed: tag %s should have a Happy child", targetTag)
//                .contains(targetTag);
//
//        // 2. The target tag still exists after suppression (otherwise the check passes for the wrong reason)
//        assertThat(findFlag(MAPPER.readTree(suppressedJson), targetTag))
//                .as("Tag %s should still exist after suppression", targetTag)
//                .isNotNull();
//
//        // 3. Happy is gone from the target tag
//        assertThat(after)
//                .as("Suppressed: tag %s should NOT have a Happy child", targetTag)
//                .doesNotContain(targetTag);
//
//        // 4. Every other tag keeps its Happy child: nothing else was suppressed
//        Set<String> expectedAfter = new LinkedHashSet<>(before);
//        expectedAfter.remove(targetTag);
//        assertThat(after)
//                .as("Happy must be removed only from tag %s", targetTag)
//                .containsExactlyInAnyOrderElementsOf(expectedAfter);
//    }
//
//    private void assertOtherUnderFlag(String unsuppressedJson, String flagCode) throws IOException {
//        JsonNode flag = findFlag(MAPPER.readTree(unsuppressedJson), flagCode);
//
//        assertThat(flag)
//                .as("Flag %s should exist in the unsuppressed response", flagCode)
//                .isNotNull();
//        Assertions.assertThat(hasOtherChild(flag))
//                .as("Unsuppressed: flag %s should have a child named Other", flagCode)
//                .isTrue();
//    }
//
//    private void assertNoOtherUnderFlag(String suppressedJson, String flagCode) throws IOException {
//        JsonNode flag = findFlag(MAPPER.readTree(suppressedJson), flagCode);
//
//        assertThat(flag)
//                .as("Flag %s should still exist after suppression", flagCode)
//                .isNotNull();
//        Assertions.assertThat(hasOtherChild(flag))
//                .as("Suppressed: flag %s should NOT have a child named Other", flagCode)
//                .isFalse();
//    }
//
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
////        commonDataApiClient.refreshProperty("RA0010");
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
//            Path target = Paths.get("src", "functionalTest", "resources", "FT_Suppressed_" + "RA0010" + ".json");
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
