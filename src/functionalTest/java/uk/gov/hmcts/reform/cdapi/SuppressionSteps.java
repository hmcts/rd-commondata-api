package uk.gov.hmcts.reform.cdapi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.core.Serenity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("checkstyle:all")
public class SuppressionSteps extends AuthorizationFunctionalTest {

    private static final String OTHER_FLAG_CODE_SUPPRESSIONS = "OTHER_FLAG_CODE_SUPPRESSIONS";
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final String OTHER = "Other";
    public final String flagsEndPoint = "/refdata/commondata/caseflags/service-id=%s?available-external-flag=N";
    private String baselineApiUrl = "https://rd-commondata-api-pr-956.preview.platform.hmcts.net";

    public static Optional<String> configuredCodes() {
        String v = System.getenv(OTHER_FLAG_CODE_SUPPRESSIONS);
        if (v == null || v.isBlank()) v = System.getProperty(OTHER_FLAG_CODE_SUPPRESSIONS);
        return (v == null || v.isBlank()) ? Optional.empty() : Optional.of(v);
    }

    public static String normalize(String raw) {
        return String.join(",", parse(raw));
    }

    public static Set<String> parse(String raw) {
        Set<String> set = new LinkedHashSet<>();
        for (String s : raw.split(",")) {
            if (!s.isBlank()) set.add(s.trim());
        }
        return set;
    }
//    @Step("Apply code-suppressions = ''{0}''")
//    public void applySuppression(String value) {
//        SerenityRest.given()
//                .baseUri(apiUrl)
//                .contentType(ContentType.JSON)
//                .body(Map.of("name", "other-flag-code-suppressions",
//                        "value", value == null ? "" : value))
//                .post("/actuator/env")
//                .then().statusCode(anyOf(is(200), is(204)));
//
//        SerenityRest.given()
//                .baseUri(apiUrl)
//                .contentType(ContentType.JSON)
//                .body("{}")
//                .post("/actuator/refresh")
//                .then().log().ifValidationFails()
//                .statusCode(200);
//
//        SerenityRest.given().baseUri(apiUrl)
//                .get("/actuator/env/other-flag-code-suppressions")
//                .then().statusCode(200)
//                .body("property.value", equalTo(value));
//    }

    @Step("Call flags API on BASELINE instance for serviceId ''{0}''")
    public Response callBaselineFlagsApi(String serviceId) {
        Response response =
                commonDataApiClient.getBaseLineMultipleAuthHeaders(baselineApiUrl)
                        .get(String.format(flagsEndPoint, serviceId))
                        .then().log().all().and().statusCode(200)
                        .extract().response();

        Serenity.recordReportData()
                .withTitle("Baseline API call status (serviceId " + serviceId + ")")
                .andContents("GET " + String.format(flagsEndPoint, serviceId)
                        + " on " + baselineApiUrl + " -> HTTP " + response.statusCode());
        return response;
    }

    @Step("Call flags API for service ''{1}'' ({0})")
    public Response callFlagsApi(String phase, String serviceId) {
        Response response =
                commonDataApiClient.getMultipleAuthHeaders()
                        .log().all()
                        .contentType(ContentType.JSON)
                        .accept(ContentType.JSON)
                        .get(String.format(flagsEndPoint, serviceId))
                        .then()
                        .log().all().and().statusCode(200)
                        .extract().response();

        Serenity.recordReportData()
                .withTitle("API call status (" + phase + ", serviceId " + serviceId + ")")
                .andContents("GET " + String.format(flagsEndPoint, serviceId)
                        + " -> HTTP " + response.statusCode());
        return response;
    }

//    @Step("Call flags API for service ''{1}'' with query {2} ({0})")
//    public Response callFlagsApi(String phase, String serviceId, Map<String, ?> queryParams) {
//        Response response =
//                getMultipleAuthHeaders()
//                .pathParam("service-id", serviceId)
//                .queryParams(queryParams == null ? Map.of() : queryParams)
//                .get(flagsEndPoint)
//                .then().statusCode(200)
//                .extract().response();
//
//        Serenity.recordReportData()
//                .withTitle("API call status (" + phase + ", serviceId " + serviceId + ")")
//                .andContents("GET " + String.format(flagsEndPoint,serviceId)
//                        + (queryParams == null || queryParams.isEmpty() ? "" : " query=" + queryParams)
//                        + " -> HTTP " + response.statusCode());
//        return response;
//    }

    @Step("Record flag tree: {0}")
    public void recordFlagTree(String title, String json) throws IOException {
        Map<String, List<String>> tree = childNamesByCode(MAPPER.readTree(json));

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-8s | %s%n", "nativeFlagCode", "child flag names"));
        sb.append("---------+----------------------------\n");
        tree.forEach((code, names) ->
                sb.append(String.format("%-8s | %s%n", code, names.isEmpty() ? "-" : String.join(", ", names))));

        Serenity.recordReportData().withTitle(title).andContents(sb.toString());
    }

    @Step("Record Other comparison for suppressed code {0}")
    public void recordOtherComparison(String code, String beforeJson, String afterJson) throws IOException {
        Set<String> before = flagsWithOtherChild(beforeJson);
        Set<String> after = flagsWithOtherChild(afterJson);

        Set<String> all = new TreeSet<>(before);
        all.addAll(after);

        StringBuilder sb = new StringBuilder();
        sb.append("Suppressed code: ").append(code).append("\n\n");
        sb.append(String.format("%-8s | %-13s | %-13s | %s%n", "nativeFlagCode", "Other before", "Other after", "result"));
        sb.append("---------+---------------+---------------+-----------\n");
        for (String flag : all) {
            boolean b = before.contains(flag), a = after.contains(flag);
            String result = b && !a ? "SUPPRESSED" : (b ? "unchanged" : "n/a");
            sb.append(String.format("%-8s | %-13s | %-13s | %s%n", flag, b ? "yes" : "no", a ? "yes" : "no", result));
        }
        Serenity.recordReportData().withTitle("Other flag before vs after").andContents(sb.toString());
    }

    @Step("Record Other comparison for suppressed code(s) {0}")
    public void recordOtherComparison(Set<String> targets, String beforeJson, String afterJson) throws IOException {
        Set<String> before = flagsWithOtherChild(beforeJson);
        Set<String> after = flagsWithOtherChild(afterJson);

        Set<String> all = new TreeSet<>(before);
        all.addAll(after);
        all.addAll(targets);

        StringBuilder sb = new StringBuilder();
        sb.append("Suppressed code(s): ").append(String.join(",", targets)).append("\n\n");
        sb.append(String.format("%-8s | %-10s | %-13s | %-12s | %s%n",
                "flagCode", "suppressed", "Other before", "Other after", "result"));
        sb.append("---------+------------+---------------+--------------+-----------\n");
        for (String flag : all) {
            boolean t = targets.contains(flag), b = before.contains(flag), a = after.contains(flag);
            String result = t && b && !a ? "SUPPRESSED (expected)"
                    : t && b ? "FAIL: still present"
                    : t ? "n/a (no Other before)"
                    : b == a ? "unchanged (expected)" : "FAIL: changed unexpectedly";
            sb.append(String.format("%-8s | %-10s | %-13s | %-12s | %s%n",
                    flag, t ? "yes" : "no", b ? "yes" : "no", a ? "yes" : "no", result));
        }
        Serenity.recordReportData().withTitle("Other flag before vs after").andContents(sb.toString());
    }

    @Step("Verify Other is suppressed under flag(s) {0} (app preconfigured)")
    public void verifyConfiguredSuppression(Set<String> targets, String afterJson, String baselineJson)
            throws IOException {
        JsonNode after = MAPPER.readTree(afterJson);
        Set<String> otherAfter = flagsWithOtherChild(afterJson);

        List<String> missingFlags = new ArrayList<>();
        List<String> stillOther = new ArrayList<>();

        StringBuilder sb = new StringBuilder();
        sb.append("OTHER_FLAG_CODE_SUPPRESSIONS = ").append(String.join(",", targets)).append("\n\n");
        sb.append(String.format("%-8s | %-11s | %-18s | %s%n", "flagCode", "flag present", "Other child after", "result"));
        sb.append("---------+-------------+--------------------+----------------\n");
        for (String code : targets) {
            JsonNode flag = findFlag(after, code);
            boolean present = flag != null;
            boolean other = present && hasOtherChild(flag);
            if (!present) missingFlags.add(code);
            if (other) stillOther.add(code);
            sb.append(String.format("%-8s | %-11s | %-18s | %s%n", code,
                    present ? "yes" : "no", other ? "yes" : "no",
                    !present ? "FAIL: flag missing" : other ? "FAIL: Other still present" : "SUPPRESSED"));
        }

        String otherFlagsNote;
        Set<String> expectedAfter = null;
        if (baselineJson != null) {
            Set<String> before = flagsWithOtherChild(baselineJson);
            expectedAfter = new LinkedHashSet<>(before);
            expectedAfter.removeAll(targets);
            otherFlagsNote = "Baseline found. Flags with Other before: " + before
                    + "; expected after: " + expectedAfter + "; actual after: " + otherAfter;
        } else {
            otherFlagsNote = "No baseline file (/baseline/unsuppressed.json): only target flags were verified. "
                    + "Flags with Other after: " + otherAfter;
        }
        sb.append("\n").append(otherFlagsNote).append("\n");

        // Record evidence first so it is in the report even when an assertion fails
        Serenity.recordReportData().withTitle("Other suppression result").andContents(sb.toString());

        assertThat(missingFlags).as("Target flags must still exist in the response").isEmpty();
        assertThat(stillOther).as("Other must be removed from these flags").isEmpty();
        if (expectedAfter != null) {
            assertThat(otherAfter)
                    .as("Other must be removed only from %s", targets)
                    .containsExactlyInAnyOrderElementsOf(expectedAfter);
        }
    }

    private JsonNode findFlag(JsonNode root, String code) {
        for (JsonNode group : root.path("flags"))
            for (JsonNode detail : group.path("FlagDetails")) {
                JsonNode f = findFlagRecursive(detail, code);
                if (f != null) return f;
            }
        return null;
    }

    private JsonNode findFlagRecursive(JsonNode flag, String code) {
        if (code.equals(flag.path("nativeFlagCode").asText())) return flag;
        for (JsonNode c : flag.path("childFlags")) {
            JsonNode f = findFlagRecursive(c, code);
            if (f != null) return f;
        }
        return null;
    }

    private boolean hasOtherChild(JsonNode flag) {
        for (JsonNode c : flag.path("childFlags"))
            if (OTHER.equals(c.path("name").asText())) return true;
        return false;
    }

    @Step("Verify Other is suppressed only for flag {0}: expected removed = {1}")
    public void verifyOtherResult(String code, boolean expectedRemoved, String beforeJson, String afterJson)
            throws IOException {
        Set<String> before = flagsWithOtherChild(beforeJson);
        Set<String> after = flagsWithOtherChild(afterJson);

        System.out.println("=======================");
        System.out.println("before==" + Arrays.toString(before.toArray()));
        System.out.println("after==" + Arrays.toString(before.toArray()));
        System.out.println("=======================");

        Set<String> expectedAfter = new LinkedHashSet<>(before);
        if (expectedRemoved) {
            assertThat(before).as("Unsuppressed: flag %s should have a Other child", code).contains(code);
            expectedAfter.remove(code);
        }
        assertThat(after)
                .as("After suppressing %s, Other must be removed only from the target flag", code)
                .containsExactlyInAnyOrderElementsOf(expectedAfter);

        Serenity.recordReportData().withTitle("Verdict").andContents(
                expectedRemoved
                        ? "CONFIRMED: Other suppressed for flag " + code + " and no other flag"
                        : "CONFIRMED: suppression of " + code + " left every Other flag untouched");
    }

    @Step("Verify Other is suppressed only for flag(s) {0}")
    public void verifyOtherSuppressedOnlyFor(Set<String> targets, String beforeJson, String afterJson)
            throws IOException {
        Set<String> before = flagsWithOtherChild(beforeJson);
        Set<String> after = flagsWithOtherChild(afterJson);

        Set<String> testable = new LinkedHashSet<>(targets);
        testable.retainAll(before);

        assertThat(testable)
                .as("At least one of %s must have a Other child before suppression", targets)
                .isNotEmpty();

        Set<String> expectedAfter = new LinkedHashSet<>(before);
        expectedAfter.removeAll(targets);

        assertThat(after)
                .as("Other must be removed from %s and from no other flag", testable)
                .containsExactlyInAnyOrderElementsOf(expectedAfter);

        Serenity.recordReportData().withTitle("Verdict").andContents(
                "CONFIRMED: Other suppressed for flag(s) " + testable + " and no other flag");
    }

    public Set<String> applicableTargets(Set<String> targets, String baselineJson) throws IOException {
        if (baselineJson == null) return new LinkedHashSet<>(targets);
        JsonNode base = MAPPER.readTree(baselineJson);
        Set<String> applicable = new LinkedHashSet<>();
        for (String code : targets) {
            if (findFlag(base, code) != null) applicable.add(code);
        }
        return applicable;
    }

    @Step("Verify Other is suppressed only for flag(s) {0} (serviceId ''{3}'')")
    public void verifySuppression(Set<String> targets, String baselineJson, String afterJson, String serviceId)
            throws IOException {

        JsonNode after = MAPPER.readTree(afterJson);
        Set<String> applicable = applicableTargets(targets, baselineJson);
        Set<String> otherAfter = flagsWithOtherChild(afterJson);
        Set<String> otherBefore = baselineJson == null ? null : flagsWithOtherChild(baselineJson);
        JsonNode base = baselineJson == null ? null : MAPPER.readTree(baselineJson);

        List<String> missingAfter = new ArrayList<>();
        List<String> stillOther = new ArrayList<>();

        StringBuilder sb = new StringBuilder();
        sb.append("User: ").append(serviceId).append('\n')
                .append("Requested codes: ").append(String.join(",", targets)).append('\n')
                .append("Compared codes : ").append(applicable).append("\n\n");
        sb.append(String.format("%-8s | %-12s | %-12s | %-12s | %s%n",
                "flagCode", "in baseline", "Other before", "Other after", "result"));
        sb.append("---------+--------------+--------------+--------------+------------------------\n");

        for (String code : targets) {
            boolean inBase = base == null || findFlag(base, code) != null;
            if (!inBase) {
                sb.append(String.format("%-8s | %-12s | %-12s | %-12s | %s%n",
                        code, "no", "-", "-", "SKIPPED (not in baseline)"));
                continue;
            }
            JsonNode flagAfter = findFlag(after, code);
            boolean otherB = otherBefore != null && otherBefore.contains(code);
            boolean otherA = flagAfter != null && hasOtherChild(flagAfter);
            if (flagAfter == null) missingAfter.add(code);
            if (otherA) stillOther.add(code);

            String result = flagAfter == null ? "FAIL: flag missing after"
                    : otherA ? "FAIL: Other still present"
                    : (otherBefore == null || otherB) ? "SUPPRESSED" : "OK (had no Other before)";
            sb.append(String.format("%-8s | %-12s | %-12s | %-12s | %s%n", code,
                    base == null ? "unknown" : "yes",
                    otherBefore == null ? "unknown" : (otherB ? "yes" : "no"),
                    flagAfter == null ? "-" : (otherA ? "yes" : "no"), result));
        }

        Set<String> expectedAfter = null;
        if (otherBefore != null) {
            expectedAfter = new LinkedHashSet<>(otherBefore);
            expectedAfter.removeAll(applicable);
            sb.append("\nOther flags with Other, expected after: ").append(expectedAfter)
                    .append("\nOther flags with Other, actual after  : ").append(otherAfter).append('\n');
        } else {
            sb.append("\nNo baseline: only the target flags were verified.\n");
        }

        // Record evidence first so it is in the report even if an assertion fails
        Serenity.recordReportData().withTitle("Other suppression result").andContents(sb.toString());

        assertThat(missingAfter).as("Compared flags must still exist after suppression").isEmpty();
        assertThat(stillOther).as("Other must be removed from these flags").isEmpty();
        if (expectedAfter != null) {
            assertThat(otherAfter)
                    .as("Other must be removed only from %s", applicable)
                    .containsExactlyInAnyOrderElementsOf(expectedAfter);
        }
    }

    // ---------- helpers ----------

    private Map<String, List<String>> childNamesByCode(JsonNode root) {
        Map<String, List<String>> map = new LinkedHashMap<>();
        for (JsonNode group : root.path("flags"))
            for (JsonNode detail : group.path("FlagDetails"))
                walk(detail, map);
        return map;
    }

    private void walk(JsonNode flag, Map<String, List<String>> map) {
        List<String> names = new ArrayList<>();
        flag.path("childFlags").forEach(c -> names.add(c.path("name").asText()));
        map.put(flag.path("nativeFlagCode").asText(), names);
        flag.path("childFlags").forEach(c -> walk(c, map));
    }

    private Set<String> flagsWithOtherChild(String json) throws IOException {
        Set<String> codes = new LinkedHashSet<>();
        for (JsonNode group : MAPPER.readTree(json).path("flags"))
            for (JsonNode detail : group.path("FlagDetails"))
                collectParentsOfOther(codes, detail);
        return codes;
    }

    private void collectParentsOfOther(Set<String> codes, JsonNode flag) {
        for (JsonNode child : flag.path("childFlags")) {
            if (OTHER.equals(child.path("name").asText())) codes.add(flag.path("nativeFlagCode").asText());
            collectParentsOfOther(codes, child);
        }
    }

//    public RequestSpecification getMultipleAuthHeaders() {
//        return SerenityRest.with()
//                .baseUri(apiUrl)
//                .header("Content-Type", APPLICATION_JSON_VALUE)
//                .header("Accepts", APPLICATION_JSON_VALUE)
//                .header(SERVICE_HEADER, "Bearer " + S2S_TOKEN)
//                .header(AUTHORIZATION_HEADER, "Bearer " + IDAM_TOKEN);
//    }

//    private RequestSpecification actuator() {
//        return SerenityRest.given().baseUri(apiUrl).contentType(ContentType.JSON);
//    }

//    @Step("Reset all property overrides")
//    public void resetOverrides() {
//        actuator().delete("/actuator/env").then().statusCode(anyOf(is(200), is(204)));
//        actuator().body("{}").post("/actuator/refresh").then().statusCode(200);
//    }
}