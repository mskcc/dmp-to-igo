package org.mkscc.igo.pi.dmp.converter;

import org.hamcrest.object.IsCompatibleType;
import org.junit.Before;
import org.junit.Test;
import org.mkscc.igo.pi.cmo.CMOSampleIdResolver;
import org.mkscc.igo.pi.cmo.MrnToCmoPatientIdResolver;
import org.mkscc.igo.pi.dmp.DmpPatientWithSamples;
import org.mkscc.igo.pi.dmp.DmpSamplesRetriever;
import org.mkscc.igo.pi.dmp.domain.DMPSample;
import org.mkscc.igo.pi.dmp.domain.DmpFileEntry;
import org.mkscc.igo.pi.dmp.domain.SampleType;
import org.mskcc.util.TestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CmoPatientIdAwareDmpFileEntryToSampleConverterTest {
    private final MrnToCmoPatientIdResolver mrnToCmoPatientIdResolver = mock(MrnToCmoPatientIdResolver.class);
    private final DmpSamplesRetriever dmpSamplesRetriever = mock(DmpSamplesRetriever.class);
    private final String dmpPatientId = "P-1234567";
    private final String dmpSampleId = "P-1234567-something";
    private CMOSampleIdResolver cmoSampleIdResolver = mock(CMOSampleIdResolver.class);
    private CmoPatientIdAwareDmpFileEntryToSampleConverter cmoPatientIdAwareDmpFileEntryToSampleConverter = new
            CmoPatientIdAwareDmpFileEntryToSampleConverter(mrnToCmoPatientIdResolver, dmpSamplesRetriever,
            cmoSampleIdResolver);
    private String cmoPatientId = "C-12345";
    private String cmoSampleId = "C-12345-X001-d";
    private String mrn = "1234ABC";
    private String runId = "J5432";
    private String bamPath = "fdsf/fdsfds/fdsfsdf.bam";

    @Before
    public void setUp() throws Exception {
        when(mrnToCmoPatientIdResolver.resolve(any())).thenReturn(cmoPatientId);
        when(cmoSampleIdResolver.resolve(any())).thenReturn(cmoSampleId);
    }

    private List<DMPSample> getSamples() {
        List<DMPSample> dmpSamples = new ArrayList<>();
        DMPSample dmpSample = new DMPSample(dmpSampleId);
        dmpSample.setRunID(runId);
        dmpSample.setBamPath(bamPath);
        dmpSamples.add(dmpSample);

        return dmpSamples;
    }

    @Test
    public void whenDmpFileEntryIsConverted_shouldDmpSampleHaveFieldsFilledIn() throws Exception {
        //given
        DmpPatientWithSamples dmpPatientWithSamples = new DmpPatientWithSamples();
        dmpPatientWithSamples.setDmpPatientId(dmpPatientId);
        dmpPatientWithSamples.setMrn(mrn);
        dmpPatientWithSamples.setSamples(getSamples());
        when(dmpSamplesRetriever.retrieve(any())).thenReturn(dmpPatientWithSamples);

        DmpFileEntry dmpFileEntry = new DmpFileEntry();
        SampleType sampleType = SampleType.METASTATIC;
        dmpFileEntry.setSampleType(String.valueOf(sampleType.getValue()));
        dmpFileEntry.setDmpSampleId(dmpSampleId);

        //when
        DMPSample dmpSample = cmoPatientIdAwareDmpFileEntryToSampleConverter.convert(dmpFileEntry);

        //then
        assertThat(dmpSample.getIgoId(), is(dmpFileEntry.getDmpSampleId()));
        assertThat(dmpSample.getSampleClass(), is(sampleType.getSampleClass().getValue()));
        assertThat(dmpSample.getBamPath(), is(bamPath));
        assertThat(dmpSample.getRunID(), is(runId));
        assertThat(dmpSample.getSampleOrigin(), is(DMPSample.DEFAULT_SAMPLE_ORIGIN.getValue()));
        assertThat(dmpSample.getSpecimenType(), is(DMPSample.DEFAULT_SPECIMEN_TYPE.getValue()));
        assertThat(dmpSample.getNAtoExtract(), is(DMPSample.DEFAULT_NUCLEID_ACID.getValue()));
        assertThat(dmpSample.getPatientId(), is(dmpPatientId));
        assertThat(dmpSample.getCmoPatientId(), is(cmoPatientId));
        assertThat(dmpSample.getCmoSampleId(), is(cmoSampleId));
    }

    @Test
    public void whenNoDMPSamplesFound_shouldThrowException() throws Exception {
        //given
        DmpPatientWithSamples empty = new DmpPatientWithSamples();
        when(dmpSamplesRetriever.retrieve(any())).thenReturn(empty);

        DmpFileEntry dmpFileEntry = new DmpFileEntry();
        SampleType sampleType = SampleType.METASTATIC;
        dmpFileEntry.setSampleType(String.valueOf(sampleType.getValue()));
        dmpFileEntry.setDmpSampleId(dmpSampleId);

        //when
        Optional<Exception> exception = TestUtils.assertThrown(() -> cmoPatientIdAwareDmpFileEntryToSampleConverter
                .convert(dmpFileEntry));

        //then
        assertThat(exception.isPresent(), is(true));
        assertThat(exception.get().getClass(), IsCompatibleType.typeCompatibleWith
                (CmoPatientIdAwareDmpFileEntryToSampleConverter.DMPSampleNotFoundException.class));
    }

    @Test
    public void whenPatientIdIsInIncorrectFormat_shouldThrowException() throws Exception {
        //given
        DmpPatientWithSamples dmpPatientWithSamples = new DmpPatientWithSamples();
        dmpPatientWithSamples.setDmpPatientId("incorrectFormat-patient-id");
        dmpPatientWithSamples.setMrn(mrn);
        dmpPatientWithSamples.setSamples(getSamples());
        when(dmpSamplesRetriever.retrieve(any())).thenReturn(dmpPatientWithSamples);

        DmpFileEntry dmpFileEntry = new DmpFileEntry();
        SampleType sampleType = SampleType.METASTATIC;
        dmpFileEntry.setSampleType(String.valueOf(sampleType.getValue()));
        dmpFileEntry.setDmpSampleId(dmpSampleId);

        //when
        Optional<Exception> exception = TestUtils.assertThrown(() -> cmoPatientIdAwareDmpFileEntryToSampleConverter
                .convert(dmpFileEntry));

        //then
        assertThat(exception.isPresent(), is(true));
        assertThat(exception.get().getClass(), IsCompatibleType.typeCompatibleWith
                (CmoPatientIdAwareDmpFileEntryToSampleConverter.IncorrectPatientIdFormatException.class));
    }
}