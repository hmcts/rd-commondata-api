package uk.gov.hmcts.reform.cdapi.util;

import com.microsoft.applicationinsights.core.dependencies.google.common.collect.Lists;
import uk.gov.hmcts.reform.idam.client.models.UserInfo;

import java.util.List;

public class UserInfoUtil {

    private static final List<String> PRD_ROLES = Lists.newArrayList("prd-admin",
                                                                     "pui-organisation-manager",
                                                                     "pui-user-manager",
                                                                     "pui-finance-manager",
                                                                     "pui-case-manager",
                                                                     "pui-caa",
                                                                     "prd-aac-system");
    private UserInfoUtil() {}

    public static boolean hasPrdRoles(UserInfo userInfo) {
        if (userInfo != null) {
            List<String> userRoles = userInfo.getRoles();
            if (userRoles != null) {
                return userRoles.stream()
                    .anyMatch(PRD_ROLES::contains);
            }
        }
        return false;
    }
}
