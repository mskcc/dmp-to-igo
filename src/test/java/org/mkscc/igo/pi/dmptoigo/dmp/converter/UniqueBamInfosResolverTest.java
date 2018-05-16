package org.mkscc.igo.pi.dmptoigo.dmp.converter;

import org.junit.Test;
import org.mkscc.igo.pi.dmptoigo.dmp.domain.BamInfo;
import org.mskcc.util.notificator.Notificator;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

public class UniqueBamInfosResolverTest {
    public static final String BAM_ID = "1234";
    public static final String BAM_PATH = "fnjdsnfjdsk/fdnsjkfnsdkj.bam";
    private UniqueBamInfosResolver uniqueBamInfosResolver = new UniqueBamInfosResolver(mock(Notificator.class));

    @Test
    public void whenBamInfosListIsEmpty_shouldRetunEmptyList() throws Exception {
        Map<String, String> uniqueBamInfos = uniqueBamInfosResolver.resolve(Collections.emptyList());

        assertThat(uniqueBamInfos.size(), is(0));
    }

    @Test
    public void whenBamInfosAreUnique_shouldReturnTheSameList() throws Exception {
        BamInfo bamInfo = new BamInfo();
        bamInfo.setBamId("bamId");
        bamInfo.setBamPath("bamPath");
        List<BamInfo> bamInfos = Arrays.asList(bamInfo);

        Map<String, String> uniqueBamInfos = uniqueBamInfosResolver.resolve(bamInfos);

        assertThat(uniqueBamInfos.size(), is(bamInfos.size()));
    }

    @Test
    public void whenBamInfosHasTwoDuplicateElements_shouldReturnEmptyList() throws Exception {
        BamInfo bamInfo1 = getBamInfo(BAM_ID, BAM_PATH);
        BamInfo bamInfo2 = getBamInfo(BAM_ID, BAM_PATH);
        List<BamInfo> bamInfos = Arrays.asList(bamInfo1, bamInfo2);

        Map<String, String> uniqueBamInfos = uniqueBamInfosResolver.resolve(bamInfos);

        assertThat(uniqueBamInfos.size(), is(0));
    }

    @Test
    public void whenBamInfosHasMultipleDuplicateElementsAndSomeUnique_shouldReturnOnlyUniqueOnes() throws Exception {
        String uniqueId1 = "unique1";
        String uniqueId2 = "unique2";

        BamInfo bamInfo1 = getBamInfo(BAM_ID, BAM_PATH);
        BamInfo bamInfo2 = getBamInfo(BAM_ID, BAM_PATH);
        BamInfo bamInfo3 = getBamInfo(uniqueId1, BAM_PATH);
        BamInfo bamInfo4 = getBamInfo(uniqueId2, BAM_PATH);
        BamInfo bamInfo5 = getBamInfo(BAM_ID, BAM_PATH);
        List<BamInfo> bamInfos = Arrays.asList(bamInfo1, bamInfo2, bamInfo3, bamInfo4, bamInfo5);

        Map<String, String> uniqueBamInfos = uniqueBamInfosResolver.resolve(bamInfos);

        assertThat(uniqueBamInfos.size(), is(2));
        assertThat(uniqueBamInfos.containsKey(uniqueId1), is(true));
        assertThat(uniqueBamInfos.get(uniqueId1), is(BAM_PATH));

        assertThat(uniqueBamInfos.containsKey(uniqueId2), is(true));
        assertThat(uniqueBamInfos.get(uniqueId2), is(BAM_PATH));
    }

    private BamInfo getBamInfo(String bamId, String bamPath) {
        BamInfo bamInfo = new BamInfo();
        bamInfo.setBamId(bamId);
        bamInfo.setBamPath(bamPath);

        return bamInfo;
    }

}