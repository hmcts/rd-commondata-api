package uk.gov.hmcts.reform.cdapi.elinks;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class ElinksServiceImpl implements ElinkService {

    @Override
    public ResponseEntity<Object> retrieveBaseLocation(String baseLocation) {
        return null;
    }

    @Override
    public ResponseEntity<Object> retrieveLocation(String baseLocation) {
        return null;
    }


}
