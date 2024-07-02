package uk.gov.hmcts.reform.cdapi.service.impl;

import com.launchdarkly.sdk.LDUser;
import com.launchdarkly.sdk.server.LDClient;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.cdapi.service.FeatureToggleService;

import java.util.HashMap;
import java.util.Map;

@Service
public class FeatureToggleServiceImpl implements FeatureToggleService {

    @Autowired
    private final LDClient ldClient;

    @Value("${launchdarkly.sdk.environment}")
    private String environment;

    private final String userName;

    private Map<String, String> launchDarklyMap;

    @Autowired
    public FeatureToggleServiceImpl(LDClient ldClient, @Value("${launchdarkly.sdk.user}") String userName) {
        this.ldClient = ldClient;
        this.userName = userName;
    }

    @PostConstruct
    public void mapServiceToFlag() {
        launchDarklyMap = new HashMap<>();
        launchDarklyMap.put(
            "CaseFlagApiController.retrieveCaseFlagsByServiceId",
            "rd_commondata_api"
        );
        launchDarklyMap.put(
            "CrdApiController.retrieveListOfValuesByCategoryId",
            "rd_lov_api"
        );
    }

    @Override
    public boolean isFlagEnabled(String serviceName, String flagName) {
        LDUser user = new LDUser.Builder(userName)
            .firstName(userName)
            .custom("servicename", serviceName)
            .custom("environment", environment)
            .build();

        return ldClient.boolVariation(flagName, user, false);
    }

    @Override
    public Map<String, String> getLaunchDarklyMap() {
        return launchDarklyMap;
    }
}
