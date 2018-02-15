package org.mkscc.igo.pi.dmptoigo.dmp;

import org.junit.Before;
import org.junit.Test;
import org.mkscc.igo.pi.dmp.TestAppConfiguration;
import org.mkscc.igo.pi.dmptoigo.cmo.CMOSampleIdResolver;
import org.mkscc.igo.pi.dmptoigo.dmp.domain.DMPSample;
import org.mkscc.igo.pi.dmptoigo.dmp.domain.DMPSampleIdView;
import org.mkscc.igo.pi.dmptoigo.dmp.domain.DMPTumorNormal;
import org.mkscc.igo.pi.dmptoigo.dmp.domain.SampleType;
import org.mskcc.domain.sample.ExternalSample;
import org.mskcc.domain.sample.SampleClass;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class DMPSampleToExternalSampleConverterTest {
    private DMPSampleToExternalSampleConverter dmpSampleToExternalSampleConverter;
    private DmpPatientId2CMOPatientIdRepository patientRepository;
    private String runId = "JAX_1234";
    private String bamPath = "/some/path/to.bam";
    private SampleType sampleType = SampleType.METASTATIC;
    private int counter = 1;
    private CMOSampleIdResolver cmoSampleIdResolver = TestAppConfiguration.getCMOSampleIdResolver();
    private String patientDmpId = "P-111222";

    @Before
    public void setUp() {
        patientRepository = TestAppConfiguration.getPatientRepository();
        dmpSampleToExternalSampleConverter = new DMPSampleToExternalSampleConverter(cmoSampleIdResolver,
                patientRepository);
    }

    @Test
    public void whenDmpSampleIsTumor_shouldExternalSampleHaveDefaultValuesForTumor() {
        //given
        DMPSample dmpSample = new DMPSample();
        dmpSample.setRunID(runId);
        dmpSample.setDmpId(TestAppConfiguration.externalId1);
        dmpSample.setBamPath(bamPath);
        dmpSample.setSampleType(sampleType.getValue());

        DMPSampleIdView dmpSampleIdView = new DMPSampleIdView();
        dmpSampleIdView.setPatientId(patientDmpId);
        dmpSampleIdView.setTumorNormal(DMPTumorNormal.TUMOR.getDmpValue());
        dmpSampleIdView.setCounter(counter);

        dmpSample.setDmpSampleIdView(dmpSampleIdView);

        //when
        ExternalSample externalSample = dmpSampleToExternalSampleConverter.convert(dmpSample);

        //then
        assertThat(externalSample.getCounter(), is(dmpSample.getCounter()));
        assertThat(externalSample.getExternalId(), is(dmpSample.getDmpId()));
        assertThat(externalSample.getCmoId(), is(TestAppConfiguration.cmoId1));
        assertThat(externalSample.getBamPath(), is(dmpSample.getBamPath()));
        assertThat(externalSample.getNucleidAcid(), is(DMPSampleToExternalSampleConverter.DefaultValues
                .DEFAULT_NUCLEID_ACID.getValue()));
        assertThat(externalSample.getPatientCmoId(), is(patientRepository
                .getCMOPatientIdByDMPPatientId(patientDmpId)));
        assertThat(externalSample.getRunID(), is(runId));
        assertThat(externalSample.getSampleClass(), is(sampleType.getSampleClass().getValue()));
        assertThat(externalSample.getSampleOrigin(), is(DMPSampleToExternalSampleConverter.DefaultValues
                .DEFAULT_TUMOR_SAMPLE_ORIGIN.getValue()));
        assertThat(externalSample.getSpecimenType(), is(DMPSampleToExternalSampleConverter.DefaultValues
                .DEFAULT_TUMOR_SPECIMEN_TYPE.getValue()));
        assertThat(externalSample.getTumorNormal(), is(DMPTumorNormal.TUMOR.getIgoValue().getValue()));
    }

    @Test
    public void whenDmpSampleIsNormal_shouldExternalSampleHaveDefaultValuesForNormal() {
        //given
        DMPSample dmpSample = new DMPSample();
        dmpSample.setRunID(runId);
        dmpSample.setDmpId(TestAppConfiguration.externalId2);
        dmpSample.setBamPath(bamPath);
        dmpSample.setSampleType(sampleType.getValue());

        DMPSampleIdView dmpSampleIdView = new DMPSampleIdView();
        dmpSampleIdView.setPatientId(patientDmpId);
        dmpSampleIdView.setTumorNormal(DMPTumorNormal.NORMAL.getDmpValue());
        dmpSampleIdView.setCounter(counter);

        dmpSample.setDmpSampleIdView(dmpSampleIdView);

        //when
        ExternalSample externalSample = dmpSampleToExternalSampleConverter.convert(dmpSample);

        //then
        assertThat(externalSample.getCounter(), is(dmpSample.getCounter()));
        assertThat(externalSample.getExternalId(), is(dmpSample.getDmpId()));
        assertThat(externalSample.getCmoId(), is(TestAppConfiguration.cmoId2));
        assertThat(externalSample.getBamPath(), is(dmpSample.getBamPath()));
        assertThat(externalSample.getNucleidAcid(), is(DMPSampleToExternalSampleConverter.DefaultValues
                .DEFAULT_NUCLEID_ACID.getValue()));
        assertThat(externalSample.getPatientCmoId(), is(patientRepository
                .getCMOPatientIdByDMPPatientId(patientDmpId)));
        assertThat(externalSample.getRunID(), is(runId));
        assertThat(externalSample.getSampleClass(), is(SampleClass.NORMAL.getValue()));
        assertThat(externalSample.getSampleOrigin(), is(DMPSampleToExternalSampleConverter.DefaultValues
                .DEFAULT_NORMAL_SAMPLE_ORIGIN.getValue()));
        assertThat(externalSample.getSpecimenType(), is(DMPSampleToExternalSampleConverter.DefaultValues
                .DEFAULT_NORMAL_SPECIMEN_TYPE.getValue()));
        assertThat(externalSample.getTumorNormal(), is(DMPTumorNormal.NORMAL.getIgoValue().getValue()));
    }
}