package uk.gov.hmcts.reform.cdapi.elinks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.cdapi.elinks.feign.ElinksFeignClient;

@Service
public class ElinksServiceImpl implements ElinkService {


    @Autowired
    ElinksFeignClient elinksFeignClient;



    @Override
    public ResponseEntity<Object> retrieveLocation() {

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(elinksFeignClient.getLocationDetails());
    }

    @Override
    public ResponseEntity<Object> retrieveBaseLocation() {

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(elinksFeignClient.getBaseLocationDetails());
    }


}
