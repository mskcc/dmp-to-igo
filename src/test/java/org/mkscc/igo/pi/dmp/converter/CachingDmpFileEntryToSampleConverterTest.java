package org.mkscc.igo.pi.dmp.converter;

import org.hamcrest.object.IsCompatibleType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mkscc.igo.pi.dmptoigo.cmo.CMOPatientInfoRetriever;
import org.mkscc.igo.pi.dmptoigo.cmo.CMOSampleIdResolver;
import org.mkscc.igo.pi.dmptoigo.dmp.DmpPatient;
import org.mkscc.igo.pi.dmptoigo.dmp.DmpPatientId2CMOPatientIdRepository;
import org.mkscc.igo.pi.dmptoigo.dmp.DmpSamplesRetriever;
import org.mkscc.igo.pi.dmptoigo.dmp.converter.BamAwareDmpFileEntryToSampleConverter;
import org.mkscc.igo.pi.dmptoigo.dmp.converter.BamPathRetriever;
import org.mkscc.igo.pi.dmptoigo.dmp.converter.CachingDMPFileEntryToSampleConverter;
import org.mkscc.igo.pi.dmptoigo.dmp.domain.DMPSample;
import org.mkscc.igo.pi.dmptoigo.dmp.domain.DMPSampleIdView;
import org.mkscc.igo.pi.dmptoigo.dmp.domain.DmpFileEntry;
import org.mkscc.igo.pi.dmptoigo.dmp.domain.SampleType;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mskcc.domain.patient.CRDBPatientInfo;
import org.mskcc.util.TestUtils;
import org.mskcc.util.notificator.Notificator;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mkscc.igo.pi.dmptoigo.dmp.converter.FromParentFolderBamPathRetriever.BAI_FILE_EXTENTION;
import static org.mkscc.igo.pi.dmptoigo.dmp.converter.FromParentFolderBamPathRetriever.BAM_FILE_EXTENTION;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CachingDmpFileEntryToSampleConverterTest {
    public static final DMPSample.Gender MALE_GENDER = DMPSample.Gender.MALE;
    private final CMOPatientInfoRetriever cmoPatientInfoRetriever = mock(CMOPatientInfoRetriever.class);
    private final DmpSamplesRetriever dmpSamplesRetriever = mock(DmpSamplesRetriever.class);
    private final String correctDmpPatientId = "P-1234567";
    private final String dmpSampleId = "P-1234567-something";

    @Mock
    private CMOSampleIdResolver cmoSampleIdResolver;

    @Mock
    private DmpPatientId2CMOPatientIdRepository dmpPatientId2CMOPatientIdRepository;

    @Mock
    private BamPathRetriever bamPathRetriever;

    @Mock
    private Predicate<String> fileExistsPredicate;

    @Mock
    private Notificator notificator;

    @InjectMocks
    private CachingDMPFileEntryToSampleConverter cachingDMPFileEntryToSampleConverter;

    private String cmoPatientId = "C-12345";
    private String cmoSampleId = "C-12345-X001-d";
    private String mrn = "1234ABC";
    private String runId = "J5432";
    private String annonymizedBamPath = "fdsf/fdsfds/fdsfsdf.bam";
    private String bamPath = "some/toerh/path.bam";
    private String baiPath = "some/toerh/path.bai";

    @Before
    public void setUp() throws Exception {
        CRDBPatientInfo crdbPatientInfo = new CRDBPatientInfo();
        crdbPatientInfo.setPatientId(cmoPatientId);
        crdbPatientInfo.setGender(MALE_GENDER.toString());

        when(cmoPatientInfoRetriever.resolve(any())).thenReturn(crdbPatientInfo);
        when(cmoSampleIdResolver.resolve(any())).thenReturn(cmoSampleId);
        when(bamPathRetriever.retrieveBamPath(annonymizedBamPath)).thenReturn(bamPath);

        when(bamPathRetriever.retrieveBamPath(any())).thenReturn(bamPath);
        when(bamPathRetriever.retrieveBaiPath(any())).thenReturn(baiPath);

        when(fileExistsPredicate.test(bamPath)).thenReturn(true);
        when(fileExistsPredicate.test(baiPath)).thenReturn(false);

        when(dmpSamplesRetriever.retrieve(any())).thenReturn(getDmpPatientWithSamples(correctDmpPatientId, mrn));
    }

    @Test
    public void whenDmpFileEntryIsConverted_shouldDmpSampleHaveFieldsFilledIn() throws Exception {
        //given
        when(fileExistsPredicate.test(any())).thenReturn(true);

        SampleType sampleType = SampleType.METASTATIC;
        DmpFileEntry dmpFileEntry = getDmpFileEntry(sampleType);

        //when
        DMPSample dmpSample = cachingDMPFileEntryToSampleConverter.convert(dmpFileEntry);

        //then
        assertThat(dmpSample.getDmpId(), is(dmpFileEntry.getDmpSampleId()));
        assertThat(dmpSample.getSampleType(), is(sampleType.getValue()));
        assertThat(dmpSample.getBamPath(), is(bamPath));
        assertThat(dmpSample.getRunID(), is(runId));
        assertThat(dmpSample.getPatientDmpId(), is(correctDmpPatientId));
        assertThat(dmpSample.getGender(), is(MALE_GENDER));
    }

    @Test
    public void whenNoDMPSamplesFound_shouldThrowException() throws Exception {
        //given
        when(dmpSamplesRetriever.retrieve(any())).thenReturn(new DmpPatient());

        SampleType sampleType = SampleType.METASTATIC;
        DmpFileEntry dmpFileEntry = getDmpFileEntry(sampleType);

        //when
        Optional<Exception> exception = TestUtils.assertThrown(() -> cachingDMPFileEntryToSampleConverter
                .convert(dmpFileEntry));

        //then
        assertThat(exception.isPresent(), is(true));
        assertThat(exception.get().getClass(), IsCompatibleType.typeCompatibleWith
                (CachingDMPFileEntryToSampleConverter.DMPSampleNotFoundException.class));
    }

    @Test
    public void whenBamPathDoesntExist_shouldThrowException() throws Exception {
        //given
        when(fileExistsPredicate.test(any())).thenReturn(false);

        //when
        //then
        assertThatThrownBy(() -> cachingDMPFileEntryToSampleConverter.convert(getDmpFileEntry(SampleType.METASTATIC)))
                .isInstanceOf(BamAwareDmpFileEntryToSampleConverter.BamPathDoesntExistException.class);
    }

    @Test
    public void whenBamPathExistsAndBaiPathDoesntExist_shouldConvert() throws Exception {
        //given
        when(fileExistsPredicate.test(BAI_FILE_EXTENTION)).thenReturn(false);

        //when
        //then
        assertThatCode(() -> cachingDMPFileEntryToSampleConverter.convert(getDmpFileEntry(SampleType.METASTATIC)))
                .doesNotThrowAnyException();
    }

    private DmpFileEntry getDmpFileEntry(SampleType sampleType) {
        DmpFileEntry dmpFileEntry = new DmpFileEntry();
        dmpFileEntry.setSampleType(String.valueOf(sampleType.getValue()));
        dmpFileEntry.setDmpSampleId(dmpSampleId);
        dmpFileEntry.setAnnonymizedBamId(annonymizedBamPath);

        DMPSampleIdView dmpSampleIdView = new DMPSampleIdView();
        dmpSampleIdView.setPatientId(correctDmpPatientId);
        dmpFileEntry.setDmpSampleIdView(dmpSampleIdView);

        return dmpFileEntry;
    }

    private Map<String, DMPSample> getSamples() {
        Map<String, DMPSample> dmpSamples = new HashMap<>();
        DMPSample dmpSample = new DMPSample();
        dmpSample.setDmpId(dmpSampleId);
        dmpSample.setRunID(runId);
        dmpSample.setBamPath(annonymizedBamPath);
        dmpSamples.put("id1", dmpSample);

        return dmpSamples;
    }

    private DmpPatient getDmpPatientWithSamples(String dmpPatientId, String mrn) {
        DmpPatient dmpPatient = new DmpPatient();
        dmpPatient.setDmpPatientId(dmpPatientId);
        dmpPatient.setMrn(mrn);
        dmpPatient.setSamples(getSamples());

        return dmpPatient;
    }
}
