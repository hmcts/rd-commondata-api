package uk.gov.hmcts.reform.cdapi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.core.Serenity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class PathVerificationSteps {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final boolean ROOT_FIRST = true;

    private static List<String> toList(JsonNode arr) {
        List<String> l = new ArrayList<>();
        if (arr.isArray()) {
            arr.forEach(n -> l.add(n.asText()));
        }
        return l;
    }

    static List<Node> flatten(String json) throws IOException {
        List<Node> out = new ArrayList<>();
        for (JsonNode group : MAPPER.readTree(json).path("flags")) {
            for (JsonNode root : group.path("FlagDetails")) {
                walk(root, 0, "-", new ArrayList<>(), new ArrayList<>(), out);
            }
        }
        return out;
    }

    private static void walk(JsonNode flag, int depth, String parentCode,
                             List<String> ancCodes, List<String> ancNames, List<Node> out) {
        Node n = new Node(flag, depth, parentCode, ancCodes, ancNames);
        out.add(n);

        List<String> codes = new ArrayList<>(ancCodes);
        codes.add(n.code);
        List<String> names = new ArrayList<>(ancNames);
        names.add(n.name);
        for (JsonNode child : flag.path("childFlags")) {
            walk(child, depth + 1, n.code, codes, names, out);
        }
    }

    public static List<String> findViolations(String json) throws IOException {
        List<String> v = new ArrayList<>();
        for (Node n : flatten(json)) {
            for (String p : n.problems()) {
                v.add("flag " + n.code + " (" + n.name + ", depth " + n.depth + "): " + p);
            }
        }
        return v;
    }

    @Step("Record Path/codePath table for service ''{0}''")
    public void recordPathTable(String serviceId, String json) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("Service: ").append(serviceId).append("\n")
                .append("Path order: ").append(ROOT_FIRST ? "root -> immediate parent" : "immediate parent -> root")
                .append("\n\n");

        for (Node n : flatten(json)) {
            List<String> problems = n.problems();
            sb.append(String.format("[%s] flag %s (%s)  depth=%d  parent=%s%n",
                    problems.isEmpty() ? "OK" : "FAIL", n.code, n.name, n.depth, n.parentCode));
            sb.append("    codePath  actual   : ").append(n.actualCodePath).append('\n');
            sb.append("    codePath  expected : ").append(n.isRoot() ? "[] or [\"\"]"
                    : n.expectedCodePath.toString()).append('\n');
            sb.append("    Path  actual   : ").append(n.actualNamePath).append('\n');
            sb.append("    Path  expected : ").append(n.isRoot() ? "[] or [\"\"]"
                    : n.expectedNamePath.toString()).append('\n');
            problems.forEach(p -> sb.append("    -> ").append(p).append('\n'));
            sb.append('\n');
        }
        Serenity.recordReportData().withTitle("Path/codePath per node (Service " + serviceId + ")")
                .andContents(sb.toString());
    }

    @Step("Verify Path/codePath reflect the parent hierarchy for service ''{0}''")
    public void verifyPaths(String serviceId, String json) throws IOException {
        List<Node> nodes = flatten(json);
        List<String> violations = findViolations(json);

        long roots = nodes.stream().filter(Node::isRoot).count();
        int maxDepth = nodes.stream().mapToInt(n -> n.depth).max().orElse(0);
        long failedNodes = nodes.stream().filter(n -> !n.problems().isEmpty()).count();

        StringBuilder sb = new StringBuilder();
        sb.append("Service            : ").append(serviceId).append('\n')
                .append("Root flags       : ").append(roots).append('\n')
                .append("Nodes checked   : ").append(nodes.size()).append('\n')
                .append("Max depth       : ").append(maxDepth).append('\n')
                .append("Nodes failing   : ").append(failedNodes).append('\n')
                .append("Result          : ").append(violations.isEmpty() ? "PASS" : "FAIL").append("\n\n");
        violations.forEach(v -> sb.append("  - ").append(v).append('\n'));

        Serenity.recordReportData().withTitle("Path/codePath verification result (service " + serviceId + ")")
                .andContents(sb.toString());

        assertThat(nodes)
                .as("No flags found: expected response shape flags[].FlagDetails[]")
                .isNotEmpty();
        assertThat(violations)
                .withFailMessage("Path/codePath violations for service %s:%n%s",
                        serviceId, String.join("\n", violations))
                .isEmpty();
    }

    static final class Node {
        final String code;
        final String name;
        final int depth;
        final String parentCode;
        final List<String> expectedCodePath;
        final List<String> expectedNamePath;
        final List<String> actualCodePath;
        final List<String> actualNamePath;
        final boolean hasCodePath;
        final boolean hasNamePath;

        Node(JsonNode jsonNode, int depth, String parentCode, List<String> ancCodes, List<String> ancNames) {
            this.depth = depth;
            this.parentCode = parentCode;
            this.code = jsonNode.path("nativeFlagCode").asText();
            this.name = jsonNode.path("name").asText();
            this.hasCodePath = jsonNode.path("codePath").isArray();
            this.hasNamePath = jsonNode.path("Path").isArray();
            this.actualCodePath = toList(jsonNode.path("codePath"));
            this.actualNamePath = toList(jsonNode.path("Path"));

            List<String> ec = new ArrayList<>(ancCodes);
            List<String> en = new ArrayList<>(ancNames);
            if (!ROOT_FIRST) {
                Collections.reverse(ec);
                Collections.reverse(en);
            }
            this.expectedCodePath = ec;
            this.expectedNamePath = en;
        }

        private static boolean isEmptyPlaceholder(List<String> l) {
            return l.isEmpty() || (l.size() == 1 && l.get(0).isEmpty());
        }

        boolean isRoot() {
            return depth == 0;
        }

        List<String> problems() {
            List<String> p = new ArrayList<>();
            if (!hasCodePath) {
                p.add("codePath is missing");
            }
            if (!hasNamePath) {
                p.add("Path is missing");
            }

            if (isRoot()) {
                if (hasCodePath && !isEmptyPlaceholder(actualCodePath)) {
                    p.add("root codePath should be [] or [\"\"] but was " + actualCodePath);
                }
                if (hasNamePath && !isEmptyPlaceholder(actualNamePath)) {
                    p.add("root Path should be [] or [\"\"] but was " + actualNamePath);
                }
            } else {
                if (hasCodePath && !actualCodePath.equals(expectedCodePath)) {
                    p.add("codePath expected " + expectedCodePath + " but was " + actualCodePath);
                }
                if (hasNamePath && !actualNamePath.equals(expectedNamePath)) {
                    p.add("Path expected " + expectedNamePath + " but was " + actualNamePath);
                }
                if (hasCodePath && hasNamePath && actualCodePath.size() != actualNamePath.size()) {
                    p.add("codePath size " + actualCodePath.size() + " != Path size " + actualNamePath.size());
                }
            }
            return p;
        }
    }
}
