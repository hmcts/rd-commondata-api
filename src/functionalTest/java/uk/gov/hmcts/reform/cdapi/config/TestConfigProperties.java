package uk.gov.hmcts.reform.cdapi.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import uk.gov.hmcts.reform.lib.config.TestConfig;

@Getter
@Setter
@Configuration
public class TestConfigProperties implements TestConfig{


    @Value("${oauth2.client.secret}")
    public String clientSecret;

    @Value("${idam.api.url}")
    public String idamApiUrl;

    @Value("${idam.auth.redirectUrl}")
    public String oauthRedirectUrl;

    @Value("${idam.auth.clientId:rd-commondata-api}")
    public String clientId;

    @Value("${s2s-url}")
    protected String s2sUrl;

    @Value("${s2s-name}")
    protected String s2sName;

    @Value("${s2s-secret}")
    protected String s2sSecret;

    @Value("${targetInstance}")
    protected String commonDataApiUrl;

    @Value("${scope-name}")
    protected String scope;

}
