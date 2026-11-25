package uk.gov.hmcts.reform.cdapi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.core.Serenity;
import uk.gov.hmcts.reform.cdapi.client.CommonDataApiClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class SuppressionSteps {

    public static final String FLAGS_END_POINT =
            "/refdata/commondata/caseflags/service-id=%s?available-external-flag=N";
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final String OTHER = "Other";
    private static final String BASELINE_API_URL = "https://rd-commondata-api-pr-956.preview.platform.hmcts.net";

    public static Set<String> parse(String raw) {
        Set<String> set = new LinkedHashSet<>();
        for (String s : raw.split(",")) {
            if (!s.isBlank()) {
                set.add(s.trim());
            }
        }
        return set;
    }

    @Step("Call Case flags API on BASELINE instance for serviceId ''{0}''")
    public Response callBaselineCaseFlagsApi(String serviceId, CommonDataApiClient commonDataApiClient) {
        Response response =
                commonDataApiClient.getBaseLineMultipleAuthHeaders(BASELINE_API_URL)
                        .get(String.format(FLAGS_END_POINT, serviceId))
                        .then().log().all().and().statusCode(200)
                        .extract().response();

        Serenity.recordReportData()
                .withTitle("Baseline API call status (serviceId " + serviceId + ")")
                .andContents("GET " + String.format(FLAGS_END_POINT, serviceId)
                        + " on " + BASELINE_API_URL + " -> HTTP " + response.statusCode());
        return response;
    }

    @Step("Call Case flags API for service ''{1}'' ({0})")
    public Response callCaseFlagsApi(String phase, String serviceId, CommonDataApiClient commonDataApiClient) {
        Response response =
                commonDataApiClient.getMultipleAuthHeaders()
                        .log().all()
                        .contentType(ContentType.JSON)
                        .accept(ContentType.JSON)
                        .get(String.format(FLAGS_END_POINT, serviceId))
                        .then()
                        .log().all().and().statusCode(200)
                        .extract().response();

        Serenity.recordReportData()
                .withTitle("API call status (" + phase + ", serviceId " + serviceId + ")")
                .andContents("GET " + String.format(FLAGS_END_POINT, serviceId)
                        + " -> HTTP " + response.statusCode());
        return response;
    }

    @Step("Record Case flag structure: {0}")
    public void recordCaseFlagStructure(String title, String json) throws IOException {
        Map<String, List<String>> tree = childNamesByCode(MAPPER.readTree(json));

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-8s | %s%n", "nativeFlagCode", "child flag names"));
        sb.append("---------+----------------------------\n");
        tree.forEach((code, names) ->
                sb.append(String.format("%-8s | %s%n", code, names.isEmpty() ? "-" : String.join(", ", names))));

        Serenity.recordReportData().withTitle(title).andContents(sb.toString());
    }

    private JsonNode findFlag(JsonNode root, String code) {
        for (JsonNode group : root.path("flags")) {
            for (JsonNode detail : group.path("FlagDetails")) {
                JsonNode f = findFlagRecursive(detail, code);
                if (f != null) {
                    return f;
                }
            }
        }
        return null;
    }

    private JsonNode findFlagRecursive(JsonNode flag, String code) {
        if (code.equals(flag.path("nativeFlagCode").asText())) {
            return flag;
        }
        for (JsonNode c : flag.path("childFlags")) {
            JsonNode f = findFlagRecursive(c, code);
            if (f != null) {
                return f;
            }
        }
        return null;
    }

    private boolean hasOtherChild(JsonNode flag) {
        for (JsonNode c : flag.path("childFlags")) {
            if (OTHER.equals(c.path("name").asText())) {
                return true;
            }
        }
        return false;
    }

    public Set<String> applicableFlagCodes(Set<String> suppressedFlagCodes, String baselineJson) throws IOException {
        if (baselineJson == null) {
            return new LinkedHashSet<>(suppressedFlagCodes);
        }
        JsonNode base = MAPPER.readTree(baselineJson);
        Set<String> applicable = new LinkedHashSet<>();
        for (String code : suppressedFlagCodes) {
            if (findFlag(base, code) != null) {
                applicable.add(code);
            }
        }
        return applicable;
    }

    @Step("Verify Other is suppressed only for flag(s) {0} (serviceId ''{3}'')")
    public void verifySuppression(Set<String> suppressedFlagCodes, String baselineJson,
                                  String afterJson, String serviceId)
            throws IOException {

        Set<String> applicable = applicableFlagCodes(suppressedFlagCodes, baselineJson);




        StringBuilder sb = new StringBuilder();
        sb.append("Service Id: ").append(serviceId).append('\n')
                .append("Suppressed flag codes: ").append(String.join(",", suppressedFlagCodes)).append('\n')
                .append("Compared flag codes : ").append(applicable).append("\n\n");
        sb.append(String.format("%-8s | %-12s | %-12s | %-12s | %s%n",
                "flagCode", "in baseline", "Other before", "Other after", "result"));
        sb.append("---------+--------------+--------------+--------------+------------------------\n");

        JsonNode base = baselineJson == null ? null : MAPPER.readTree(baselineJson);
        Set<String> otherBefore = baselineJson == null ? null : flagsWithOtherChild(baselineJson);
        JsonNode after = MAPPER.readTree(afterJson);
        List<String> missingAfter = new ArrayList<>();
        List<String> stillOther = new ArrayList<>();
        for (String code : suppressedFlagCodes) {
            boolean inBase = base == null || findFlag(base, code) != null;
            if (!inBase) {
                sb.append(String.format("%-8s | %-12s | %-12s | %-12s | %s%n",
                        code, "no", "-", "-", "SKIPPED (not in baseline)"));
                continue;
            }
            JsonNode flagAfter = findFlag(after, code);
            boolean otherB = otherBefore != null && otherBefore.contains(code);
            boolean otherA = flagAfter != null && hasOtherChild(flagAfter);
            if (flagAfter == null) {
                missingAfter.add(code);
            }
            if (otherA) {
                stillOther.add(code);
            }

            String result = flagAfter == null ? "FAIL: flag missing after"
                    : otherA ? "FAIL: Other still present"
                    : (otherBefore == null || otherB) ? "SUPPRESSED" : "OK (had no Other before)";
            sb.append(String.format("%-8s | %-12s | %-12s | %-12s | %s%n", code,
                    base == null ? "unknown" : "yes",
                    otherBefore == null ? "unknown" : (otherB ? "yes" : "no"),
                    flagAfter == null ? "-" : (otherA ? "yes" : "no"), result));
        }

        Set<String> otherAfter = flagsWithOtherChild(afterJson);
        Set<String> expectedAfter = null;
        if (otherBefore != null) {
            expectedAfter = new LinkedHashSet<>(otherBefore);
            expectedAfter.removeAll(applicable);
            sb.append("\nOther flags with Other, expected after: ").append(expectedAfter)
                    .append("\nOther flags with Other, actual after  : ").append(otherAfter).append('\n');
        } else {
            sb.append("\nNo baseline: only the suppressed flags were verified.\n");
        }

        Serenity.recordReportData().withTitle("Other suppression result").andContents(sb.toString());

        assertThat(missingAfter).as("Compared flags must still exist after suppression").isEmpty();
        assertThat(stillOther).as("Other must be removed from these flags").isEmpty();
        if (expectedAfter != null) {
            assertThat(otherAfter)
                    .as("Other must be removed only from %s", applicable)
                    .containsExactlyInAnyOrderElementsOf(expectedAfter);
        }
    }

    private Map<String, List<String>> childNamesByCode(JsonNode root) {
        Map<String, List<String>> map = new LinkedHashMap<>();
        for (JsonNode group : root.path("flags")) {
            for (JsonNode detail : group.path("FlagDetails")) {
                walk(detail, map);
            }
        }
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
        for (JsonNode group : MAPPER.readTree(json).path("flags")) {
            for (JsonNode detail : group.path("FlagDetails")) {
                collectParentsOfOther(codes, detail);
            }
        }
        return codes;
    }

    private void collectParentsOfOther(Set<String> codes, JsonNode flag) {
        for (JsonNode child : flag.path("childFlags")) {
            if (OTHER.equals(child.path("name").asText())) {
                codes.add(flag.path("nativeFlagCode").asText());
            }
            collectParentsOfOther(codes, child);

        }
    }
}