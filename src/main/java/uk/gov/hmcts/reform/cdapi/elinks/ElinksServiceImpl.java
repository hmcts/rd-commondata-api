package uk.gov.hmcts.reform.cdapi.elinks;

import feign.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.cdapi.elinks.feign.ElinksFeignClient;

import static java.util.Objects.nonNull;

@Service
@Slf4j
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
        log.info("ElinksServiceImpl.retrieveBaseLocation  Start ============");
        Response response = null;
        try {
            response = elinksFeignClient.getBaseLocationDetails();
            log.info("ElinksServiceImpl.elinksFeignClient Response == " + response.body());
        } catch (Exception exp ){
            log.error(exp.getMessage());
        }finally {
        if (nonNull(response)) {
            response.close();
        }
    }
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(response);

    }


}
