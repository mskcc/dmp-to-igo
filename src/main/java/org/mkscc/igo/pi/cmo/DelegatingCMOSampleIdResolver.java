package org.mkscc.igo.pi.cmo;

import org.mkscc.igo.pi.dmp.domain.DMPSample;
import org.springframework.stereotype.Component;

@Component
public class DelegatingCMOSampleIdResolver implements CMOSampleIdResolver {
    @Override
    public String resolve(DMPSample dmpSample) {
        // TODO call service for resolution
        return "fake-id";
    }
}
