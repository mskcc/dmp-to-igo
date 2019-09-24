package org.mkscc.igo.pi.dmptoigo.dmp;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.mkscc.igo.pi.dmp.TestAppConfiguration;
import org.mkscc.igo.pi.dmptoigo.cmo.CMOPatientInfoRetriever;
import org.mkscc.igo.pi.dmptoigo.cmo.CMOSampleIdResolver;
import org.mkscc.igo.pi.dmptoigo.dmp.domain.DMPSample;
import org.mkscc.igo.pi.dmptoigo.dmp.domain.DMPSampleIdView;
import org.mkscc.igo.pi.dmptoigo.dmp.domain.DMPTumorNormal;
import org.mkscc.igo.pi.dmptoigo.dmp.domain.SampleType;
import org.mskcc.domain.external.ExternalSample;
import org.mskcc.domain.patient.CRDBPatientInfo;
import org.mskcc.domain.patient.Sex;
import org.mskcc.domain.sample.SampleClass;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mkscc.igo.pi.dmp.TestAppConfiguration.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DMPSampleToExternalSampleConverterTest {
    private static final String NOT_CACHED_CMO_PATIENT_ID = "notCachedDmpId";
    private DMPSampleToExternalSampleConverter dmpSampleToExternalSampleConverter;
    private DmpPatientId2CMOPatientIdRepository patientRepository;
    private String runId = "JAX_1234";
    private String bamPath = "/some/path/to.bam";
    private SampleType sampleType = SampleType.METASTATIC;
    private int counter = 1;
    private CMOSampleIdResolver cmoSampleIdResolver = getCMOSampleIdResolver();
    private String patientDmpId = "P-111222";
    private CMOPatientInfoRetriever cmoPatientInfoRetriever = mock(CMOPatientInfoRetriever.class);

    @Before
    public void setUp() {
        patientRepository = getPatientRepository();
        CRDBPatientInfo crdbPatientInfo = new CRDBPatientInfo();
        crdbPatientInfo.setPatientId(NOT_CACHED_CMO_PATIENT_ID);
        when(cmoPatientInfoRetriever.resolve(any())).thenReturn(crdbPatientInfo);

        Map<String, String> dmpAssay2IgoBaitVersion = new HashMap<>();
        dmpAssay2IgoBaitVersion.put("IM6", "IMPACT468");
        dmpSampleToExternalSampleConverter = new SimpleDMPSampleToExternalSampleConverter(cmoSampleIdResolver,
                patientRepository, new DMPGenderToIgoSexConverter(), cmoPatientInfoRetriever, dmpAssay2IgoBaitVersion);
    }

    @Test
    public void whenDmpSampleIsTumor_shouldExternalSampleHaveDefaultValuesForTumor() {
        //given
        DMPSample dmpSample = new DMPSample();
        dmpSample.setAnnonymizedRunID(runId);
        dmpSample.setDmpId(externalId1);
        dmpSample.setBamPath(bamPath);
        dmpSample.setSampleType(sampleType.getValue());
        dmpSample.setGender(DMPSample.Gender.FEMALE);

        DMPSampleIdView dmpSampleIdView = new DMPSampleIdView();
        dmpSampleIdView.setPatientId(CACHED_DMP_PATIENT_ID);
        dmpSampleIdView.setTumorNormal(DMPTumorNormal.TUMOR.getDmpValue());
        dmpSampleIdView.setCounter(counter);

        dmpSample.setDmpSampleIdView(dmpSampleIdView);

        //when
        ExternalSample externalSample = dmpSampleToExternalSampleConverter.convert(dmpSample);

        //then
        assertThat(externalSample.getCounter(), is(dmpSample.getCounter()));
        assertThat(externalSample.getExternalId(), is(dmpSample.getDmpId()));
        assertThat(externalSample.getCmoId(), is(cmoId1));
        assertThat(externalSample.getFilePath(), is(dmpSample.getBamPath()));
        assertThat(externalSample.getNucleidAcid(), is(SimpleDMPSampleToExternalSampleConverter.DefaultValues
                .DEFAULT_NUCLEID_ACID.getValue()));
        assertThat(externalSample.getPatientCmoId(), is(TestAppConfiguration.CACHED_CMO_PATIENT_ID));
        assertThat(externalSample.getExternalRunId(), is(runId));
        assertThat(externalSample.getSampleClass(), is(sampleType.getSampleClass().getValue()));
        assertThat(externalSample.getSampleOrigin(), is(SimpleDMPSampleToExternalSampleConverter.DefaultValues
                .DEFAULT_TUMOR_SAMPLE_ORIGIN.getValue()));
        assertThat(externalSample.getSpecimenType(), is(SimpleDMPSampleToExternalSampleConverter.DefaultValues
                .DEFAULT_TUMOR_SPECIMEN_TYPE.getValue()));
        assertThat(externalSample.getTumorNormal(), is(DMPTumorNormal.TUMOR.getIgoValue().getValue()));
        assertThat(externalSample.getSex(), is(Sex.F.toString()));
    }

    @Test
    public void whenDmpSampleIsNormal_shouldExternalSampleHaveDefaultValuesForNormal() {
        //given
        DMPSample dmpSample = new DMPSample();
        dmpSample.setAnnonymizedRunID(runId);
        dmpSample.setDmpId(externalId2);
        dmpSample.setBamPath(bamPath);
        dmpSample.setSampleType(sampleType.getValue());
        dmpSample.setGender(DMPSample.Gender.MALE);

        DMPSampleIdView dmpSampleIdView = new DMPSampleIdView();
        dmpSampleIdView.setPatientId(TestAppConfiguration.CACHED_DMP_PATIENT_ID);
        dmpSampleIdView.setTumorNormal(DMPTumorNormal.NORMAL.getDmpValue());
        dmpSampleIdView.setCounter(counter);

        dmpSample.setDmpSampleIdView(dmpSampleIdView);

        //when
        ExternalSample externalSample = dmpSampleToExternalSampleConverter.convert(dmpSample);

        //then
        assertThat(externalSample.getCounter(), is(dmpSample.getCounter()));
        assertThat(externalSample.getExternalId(), is(dmpSample.getDmpId()));
        assertThat(externalSample.getCmoId(), is(cmoId2));
        assertThat(externalSample.getFilePath(), is(dmpSample.getBamPath()));
        assertThat(externalSample.getNucleidAcid(), is(SimpleDMPSampleToExternalSampleConverter.DefaultValues
                .DEFAULT_NUCLEID_ACID.getValue()));
        assertThat(externalSample.getPatientCmoId(), is(TestAppConfiguration.CACHED_CMO_PATIENT_ID));
        assertThat(externalSample.getExternalRunId(), is(runId));
        assertThat(externalSample.getSampleClass(), is(SampleClass.NORMAL.getValue()));
        assertThat(externalSample.getSampleOrigin(), is(SimpleDMPSampleToExternalSampleConverter.DefaultValues
                .DEFAULT_NORMAL_SAMPLE_ORIGIN.getValue()));
        assertThat(externalSample.getSpecimenType(), is(SimpleDMPSampleToExternalSampleConverter.DefaultValues
                .DEFAULT_NORMAL_SPECIMEN_TYPE.getValue()));
        assertThat(externalSample.getTumorNormal(), is(DMPTumorNormal.NORMAL.getIgoValue().getValue()));
        assertThat(externalSample.getSex(), is(Sex.M.toString()));
    }

    @Test
    public void whenPatientIdIsNotInRepository_shouldThrowException() {
        //given
        DMPSample dmpSample = new DMPSample();
        dmpSample.setAnnonymizedRunID(runId);
        dmpSample.setDmpId(externalId2);
        dmpSample.setBamPath(bamPath);
        dmpSample.setSampleType(sampleType.getValue());
        dmpSample.setGender(DMPSample.Gender.MALE);

        DMPSampleIdView dmpSampleIdView = new DMPSampleIdView();
        dmpSampleIdView.setPatientId("notCachedDmpPatientId");
        dmpSampleIdView.setTumorNormal(DMPTumorNormal.NORMAL.getDmpValue());
        dmpSampleIdView.setCounter(counter);

        dmpSample.setDmpSampleIdView(dmpSampleIdView);

        //when
        Assertions.assertThatThrownBy(() -> dmpSampleToExternalSampleConverter.convert(dmpSample))
                .isInstanceOf(NoDMPToCMOPatientIdMapping.class)
                .hasMessageContaining("notCachedDmpPatientId");
    }
}