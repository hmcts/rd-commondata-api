package uk.gov.hmcts.reform.cdapi.elinks;

import feign.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

        log.info("ElinksServiceImpl.retrieveLocation  Start ============");
        Response response = null;
        try {
            response = elinksFeignClient.getLocationDetails();
            log.info("ElinksServiceImpl.elinksFeignClient Response == " + response.status());
        } catch (Exception exp) {
            log.error(exp.getMessage());
        } finally {
            if (nonNull(response)) {
                response.close();
            }
        }
        return ResponseEntity
            .status(response.status())
            .body(response);


    }

    @Override
    public ResponseEntity<Object> retrieveBaseLocation() {
        log.info("ElinksServiceImpl.retrieveBaseLocation  Start ============");
        Response response = null;
        try {
            response = elinksFeignClient.getBaseLocationDetails();
            log.info("ElinksServiceImpl.elinksFeignClient Response == " + response.status());
        } catch (Exception exp) {
            log.error(exp.getMessage());
        } finally {
            if (nonNull(response)) {
                response.close();
            }
        }
        return ResponseEntity
            .status(response.status())
            .body(response);

    }


}
