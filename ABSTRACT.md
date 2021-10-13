This artifact contains LaCASt, our LaTeX to Computer Algebra Systems translator, and supports our study on "Comparative Verification of the Digital Library of Mathematical Functions and Computer Algebra Systems" (TACAS'22 Submission-ID 4).

LaCASt is a converter specifically developed to translate the semantically enriched LaTeX format used in NIST's Digital Library of Mathematical Functions (DLMF). This so-called semantic LaTeX resolves many potential ambiguities in function notations. Our study used LaCASt to translate formulae from the DLMF to the CAS Maple and Mathematica. Moreover, we automatically verify these translations with symbolic and numeric evaluations in the target CAS. This evaluation follows the assumptions that a correct equation in our source, here the DLMF, must remain valid after a translation to the CAS. In order to test if an equation is still valid in the syntax of the CAS, we perform simplifications and calculations on the left- and right-hand sides of the translated equation. Our study showed that these evaluations help us discover issues not only in our translator LaCASt but also in the DLMF, Maple, and Mathematica.

This artifact contains all results of our study, the source code of LaCASt, and executable scripts to reproduce the translations, symbolic simplifications, and numeric calculations. A visually more appealing presentation of the results is available online at www.lacast.wmflabs.org. In case of acceptance at the TACAS'22, we publish this artifact and further make LaCASt available on GitHub to share future updates with the community. We are currently developing LaCASt further to translate general LaTeX expressions in contrast to the more strict semantic LaTeX that is used in the DLMF. 

To reproduce our translations, only Java 11 is required. Our symbolic and numeric evaluation pipeline, however, requires an installed version either of Maple or Mathematica. Both CAS are not included in this artifact due to license limitations! If neither of both is available for reproducing the results, one can download the free "Wolfram Engine for Developers" instead to reproduce our results with Mathematica. To download our artifact, you require the password: 'tacas22-aec-only'.