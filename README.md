# Introduction

This artifact supports our submission about "Comparative Verification of the Digital Library of Mathematical Functions and Computer Algebra Systems" (TACAS'22 Submission ID 4).
We present LaCASt, our LaTeX to Computer Algebra Systems (CAS) Translator. In our paper, we use LaCASt to translate the semantically enriched LaTeX formulae in the Digital Library of Mathematical Functions (DLMF) to the two major general-purpose CAS Maple and Mathematica. Afterward, LaCASt performs symbolic and numeric verification steps in order to verify a translation. In our TACAS submission, we show that this verification can also help to detect errors and issues in the DLMF, Maple, and Mathematica.

This artifact contains both pipelines, the translations and the verifications (hereafter 'evaluation') via LaCASt. To perform translations via LaCASt, only Java 11 is required. To reproduce the results of our symbolic and numeric evaluation pipeline, either Maple or Mathematica (or both) must be installed on the system. In the following, we explain
