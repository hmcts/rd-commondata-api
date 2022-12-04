package uk.gov.hmcts.reform.cdapi.elinks;

import org.springframework.http.ResponseEntity;

public interface ElinkService {

    ResponseEntity<Object> retrieveBaseLocation(String baseLocation);

    ResponseEntity<Object> retrieveLocation(String baseLocation);

}
