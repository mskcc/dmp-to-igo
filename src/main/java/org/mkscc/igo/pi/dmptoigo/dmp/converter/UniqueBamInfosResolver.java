package org.mkscc.igo.pi.dmptoigo.dmp.converter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mkscc.igo.pi.dmptoigo.dmp.domain.BamInfo;
import org.mskcc.util.notificator.Notificator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class UniqueBamInfosResolver {
    private static final Logger LOGGER = LogManager.getLogger(UniqueBamInfosResolver.class);
    private final Notificator notificator;

    @Autowired
    UniqueBamInfosResolver(Notificator notificator) {
        this.notificator = notificator;
    }

    public Map<String, String> resolve(List<BamInfo> bamInfos) {
        Map<String, String> bamIdToBamPath = new HashMap<>();
        List<String> ambiguousBamIds = new ArrayList<>();

        for (BamInfo bamInfo : bamInfos) {
            String bamId = bamInfo.getBamId();
            if (ambiguousBamIds.contains(bamId))
                continue;

            if (bamIdToBamPath.containsKey(bamId)) {
                ambiguousBamIds.add(bamId);
                bamIdToBamPath = bamIdToBamPath.entrySet().stream()
                        .filter(b -> !b.getKey().equals(bamId))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

                String message = String.format("Multiple BAM paths found for bam id: %s. This bam id will be removed " +
                        "from cache.", bamId);
                LOGGER.warn(message);
                tryToNotify(message);
            } else {
                bamIdToBamPath.put(bamId, bamInfo.getBamPath());
            }
        }

        return bamIdToBamPath;
    }

    private void tryToNotify(String message) {
        try {
            notificator.notifyMessage("", message);
        } catch (Exception e) {
            LOGGER.warn(String.format("Unable to send notification: %s", message));
        }
    }
}
