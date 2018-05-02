# dmp-to-igo

Service responsible for retrieving DMP samples from key.txt file exposed by DMP, converting them into IGO samples and storing into database via rest service https://github.com/mskcc/external-samples-rest.

Purpose: use already sequenced DMP samples together with IGO samples to process them together in pipeline. Eg. tumor from DMP and normal from IGO.

Architecture:
http://plvpipetrack1.mskcc.org:8099/display/LU/DMP+to+IGO+Architecture

Dependencies:
1. cmo-patient-converter: https://github.com/mskcc/cmo-patient-converter
2. external-samples-rest: https://github.com/mskcc/external-samples-rest
3. lims-rest: https://github.com/mskcc/PiExemplarRest
4. [DMP] dmpIds-for-patientId
5. [DMP] key.txt file
6. [DMP] bam_fs_locs.txt file
