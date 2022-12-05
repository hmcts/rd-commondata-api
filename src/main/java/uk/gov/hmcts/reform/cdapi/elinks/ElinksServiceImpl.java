package uk.gov.hmcts.reform.cdapi.elinks;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class ElinksServiceImpl implements ElinkService {

    @Value("${elinksUrl}")
    private String elinksUrl;


    @Override
    public ResponseEntity<Object> retrieveBaseLocation() {


        return ResponseEntity
            .status(HttpStatus.OK)
            .body(elinksUrl);
    }

    @Override
    public ResponseEntity<Object> retrieveLocation() {

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(elinksUrl);
    }


}
