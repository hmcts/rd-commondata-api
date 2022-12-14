package uk.gov.hmcts.reform.cdapi.elinks;

import org.springframework.http.ResponseEntity;

public interface ElinkService {

    ResponseEntity<Object> retrieveBaseLocation();

    ResponseEntity<Object> retrieveLocation();

}
