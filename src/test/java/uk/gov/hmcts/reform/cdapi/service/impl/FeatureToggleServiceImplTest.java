package uk.gov.hmcts.reform.cdapi.service.impl;

import com.launchdarkly.sdk.server.LDClient;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FeatureToggleServiceImplTest {

    LDClient ldClient = mock(LDClient.class);
    FeatureToggleServiceImpl flaFeatureToggleService = mock(FeatureToggleServiceImpl.class);

    @Test
    void testDefaultIsFlagEnabled() {
        flaFeatureToggleService = new FeatureToggleServiceImpl(ldClient, "rd");
        assertFalse(flaFeatureToggleService.isFlagEnabled("commondata", "rd-commondata-api"));
    }

    @Test
    void testEnabledIsFlagEnabled() {
        String flagName = "CaseFlagApiController.retrieveCaseFlagsByServiceId";
        when(ldClient.boolVariation(eq(flagName), any(), eq(false))).thenReturn(true);
        flaFeatureToggleService = new FeatureToggleServiceImpl(ldClient, "rd");
        assertTrue(flaFeatureToggleService.isFlagEnabled("rd_commondata_api", flagName));
    }

    @Test
    void mapServiceToFlagTest() {
        flaFeatureToggleService = new FeatureToggleServiceImpl(ldClient, "rd");
        flaFeatureToggleService.mapServiceToFlag();
        assertTrue(flaFeatureToggleService.getLaunchDarklyMap().size() >= 1);
    }
}
