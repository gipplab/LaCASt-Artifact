Artifact for
"Comparative Verification of the Digital Library of Mathematical Functions and Computer Algebra Systems"
by A. Greiner-Petter, H. S. Cohl, A. Youssef, M. Schubotz, A. Trost, R. Dey, A. Aizawa, and B. Gipp.
TACAS 2022.
========================================================================================================

## 1. Preamble

Some of the results can only be reproduced if the CAS Maple or the CAS Mathematica (or both) are installed. This artifact does *NOT* contain these software packages due to license limitations. To reproduce our evaluation results involving CAS, one can install the free "Wolfram Engine for Developers." For installing this free package, internet access is required for downloading and activating the package. A detailed guide is given in "5. Setup Computer Algebra Systems" in this file. For reference, we created our results with Maple version 2020.2 and Mathematica version 12.1.1.

## 2. Minimal Instructions

Copy the `artifact.zip` to a convenient place and unpack it. It contains the artifact itself (in the artifact directory), the license file, and this readme. In the following, we assume you are in the artifact directory which contains the artifact itself. Starting from the place of this README.md, you should enter:
```
$ cd artifact
```
We further assume a Debian-based Linux system. However, other OS are also supported unless Java 11 or higher is installed and you follow the instructions for setting up the CAS. For a Debian-based Linux system, you can install Java 11 with
```
$ cd bin
$ sudo ./java-installer.sh
$ java --version
openjdk 11 2018-09-25
...
$ cd ..
```

And run LaCASt via
```
$ ./lacast.sh -i
```
Enter `help` in LaCASt for more information about the inputs. Instructions for the symbolic and numeric evaluation pipeline, as well as reproducing our results, follow below. 

--------------------------------------------------------------------------------------------------------

## 3.i. Content of this project

- README.md: This project description file.
- LICENSE.txt: The license file.
- artifact: The directory with the artifact itself in it with the following content.

### 3.i.a Content of the artifact

- lacast.sh: Script to interact with LaCASt (no CAS required).
- lacast-dlmf-translation.sh: Translates all DLMF formulae as presented in the paper (no CAS required). See instructions below.
- lacast-eval-numeric.sh: Performs numeric evaluations on DLMF formulae for a specific CAS (requires a CAS). See instructions below.
- lacast-eval-symboli.sh: Performs symbolic evaluations on DLMF formulae for a specific CAS (requires a CAS). See instructions below.
- lacast.config.yaml: The basic config for LACASt. Must be updated when working with CAS for numeric/symbolic evaluations.
- config: Contains relevant config files required to run numeric/symbolic evaluations.
- bin: Contains the necessary software package Java 11 and an install script to keep it simple.
- libs: Contains libraries necessary to run LaCASt.
- src: Contains the source of LaCASt and our evaluation.
- dlmf: The data folder containing our test dataset, our results presented in the paper, and will contain the results you generate on your system:
    * dlmf-formulae.txt: The DLMF dataset.
    * results-original: Contains the results as reported in the paper.
    * results-generated: Supposed to contain the outputs you are going to generate with our scripts.
- scripts: Contains additional scripts that help to run symbolic/numeric evaluations on the entire DLMF. Please read the instructions carefully before you begin using these scripts since they may take some time and require a lot of computational power.

--------------------------------------------------------------------------------------------------------

## 3.ii. Content of this Readme

1. Preamble
2. Minimal Instructions
3. Content of Artifact and Readme
4. Reproducing Reported Results
    1. How to read the data?
    2. Reproducing Translation Results
    3. Reproducing Symbolic Evaluation Results
    4. Reproducing Numeric Evaluation Results
5. Setup Computer Algebra Systems
    1. Mathematica
        1. Install Wolfram Engine and Activate License
        2. Setup LaCASt with Mathematica
    2. Maple
6. FAQ
7. Glossary

--------------------------------------------------------------------------------------------------------

## 4. Reproducing Reported Results

Our results can be split into two groups: (1) translations via LaCASt, and (2) evaluations of the translations via CAS. For (1), only Java 11 or higher is required. For (2), one requires either Maple or Mathematica (i.e., the Wolfram Engine). In the following, we explain how to reproduce our results and how to read the data. Before attempting to reproduce (2), read the "Setup Computer Algebra Systems Support" section below. Note that all results are also available online (in a visually more appealing look) at: https://lacast.wmflabs.org/.

**Important Note**: Several factors make reproducing the exact same results for (2) almost impossible. However, based on our experience, those differences are generally marginal (<1% of all DLMF test cases). The main reasons for the discrepancy between your results and ours can be:
1. The machine you are using (VM or not) is somehow different from our reference (e.g., different CPU or RAM). Hence, a CAS may take longer or shorter time on your machine to compute a test case. Since we needed to set time constraints for each test case, this implies that some cases may fail or be successful on your machine, while on ours, it simply timed out (or the opposite is the case).
2. You are using a different CAS version (or build-version). Unfortunately, Wolfram Research does not provide access to older builds of their free engine, and we cannot distribute our version. Obviously, a different version can produce different results or compute certain cases faster or slower.
3. You may spot minor differences between the result tables in our paper (Appendix) and the website compared to the results in this artifact. This discrepancy is because we constantly updated LaCASt and fixed several issues that did not make it into the paper version. In case of acceptance at the TACAS conference, we will update our paper and website accordingly. As pointed out, however, those differences are marginal (<1%).

In summary, a discrepancy of \~1% of different results per DLMF chapter is somehow expected, and no reason to worry. Everything over 5-10% indicates that something does not work as intended.

### 4.i. How to read the data?

Our dataset is `dlmf/dlmf-formulae.txt`, which contains all original DLMF formulae in semantic LaTeX. Semantic LaTeX is a semantically enriched LaTeX dialect developed by Bruce Miller for the DLMF. LaCASt is only able to translate functions when these macros are used. Even though we are actively extending LaCASt to perform translations on general LaTeX expressions, this extension is not part of this artifact yet.
Hence, translating `\Gamma(x)` will not produce a correct translation because LaCASt presumes the unambiguous notation `\EulerGamma@{x}` instead.

The reported results in our paper in raw format (in contrast to the visual tables on our website: https://lacast.wmflabs.org) can be found in `dlmf/results-original`. Note that these results may not match 100% of the data in our paper (or on the website) because this artifact is slightly newer. For example, the paper's Appendix (Table 6) shows that 180 cases finished with an error. We were able to fix most of these issues in this artifact. We will therefore update the results in the paper and on our website for the camera-ready deadline.
There are three groups of results for each CAS (Maple and Mathematica).

* `<CAS>Translations`: Contains the translations to each CAS for each line in the `dlmf-formulae.txt`. For example, the translation of line 8 in `dlmf-formulae.txt` to Maple can be found in line 8 of `MapleTranslations/translations.txt`:
    ```
    8 [http://dlmf.nist.gov/1.2.E8]: sum(binomial(z + k,k), k = 0..m) = binomial(z + m + 1,m)
    ```

* `<CAS>Symbolic`: Contains two types of files: `<num>-<code>-missing.txt` and `<num>-<code>-symbolic.txt`, where `<num>` is the chapter number of the DLMF and `<code>` the chapter code. The `-missing` file contains semantic macros that cannot be translated to the CAS, e.g., because there is no translation mapping for this macro defined. This helps us to quickly identify where we can improve the translation coverage the most. The `-symbolic` files contain the summary of the symbolic simplification evaluations for each chapter. The first two lines are special. The first line is a summary of the results of the chapter. It shows how many lines were recognized, how many of them were skipped, how many were definitions, etc. The second line shows the symbolic test functions that were activated. The following lines show the results for each test case. Consider line 7 of the `dlmf-formulae.txt`, which refers to the DLMF equation 1.2.E7. Here the result in Maple is:
    ```
    7 [http://dlmf.nist.gov/1.2.E7]: Successful [Simple: NaN, ConvEXP: NaN, ConvHYP: NaN, EXP: 0, EXP+EXP: 0, EXP+HYP: 0]
    ```
    As described in our paper, we used multiple simplification approaches to simplify the difference of the left- and right-hand sides of equation 1.2.E7 to 0. This result line shows that our first three tests: Simple (just `simplify(...)`), ConvEXP (`simplify(convert(..., exp))`), and ConvHYP (`simplify(convert(..., hypergeom))`) were unable to return 0 but the last three using expansion worked (`simplify(expand(...))` and `simplify(expand(..., exp))` and `simplify(expand(..., hypergeom))`). In the case of Mathematica (`MathematicaSymbolic/01-AL-symbolic.txt`) we only used the `FullSimplify` method.

* `<CAS>Numeric`: Contains the numeric evaluation results for each CAS (similar to the symbolic evaluation results). Let us explain the content of these files in a specific example. The first numeric test result is presented for line 54 in `MathematicaNumeric/01-AL-numeric.txt`. This is the first case because we only performed numeric tests on non-symbolically verified equations to reduce computing time. The first test that symbolically failed (see `MathematicaSymbolic/01-AL-symbolic.txt`) was line 54. Skipped lines are not tested because if they are skipped for symbolic tests, we also skip them for numeric tests. Similarly, if LaCASt cannot translate a line, no numeric test is possible, and we can skip the line too. The symbolic result in Mathematica for line 54 was
    ```
    54 [http://dlmf.nist.gov/1.4.E8]: Failure [Simple: NaN]
    54-a [http://dlmf.nist.gov/1.4.E8]: Successful [Simple: 0]
    ```
    The `-a` suffix shows that this case was a multi-equation or contained `\pm`. Line 54 in our dataset indeed contained two `=` symbols. The first line in our result dataset represents the first equation. The `-a` suffix represents the second equation.
Only the first equation failed symbolically. For numeric evaluations, the result line contains:
    ```
    54 [http://dlmf.nist.gov/1.4.E8]: Failed [30/30]: {{Complex[0.7500000000000002, 1.299038105676658], {Rule[f, Power[E, Times[Complex[0, Rational[1, 6]], Pi]]],     Rule[x, 1.5]}}, {Complex[0.25000000000000006, 0.4330127018922193], {Rule[f, Power[E, Times[Complex[0, Rational[1, 6]], Pi]]], Rule[x, 0.5]}}, ...
    ```
    The resulting line usually contains the line number (referring to the dataset file) followed by the link to the DLMF. Next comes `Failed [30/30]`, which means that 30 test calculations were triggered and all 30 of them failed (in some cases, not all test calculations failed, see for example, line `55-a` in the same file). Afterward, the first examples of failed cases are shown. The first failed case was:
    ```
    {Complex[0.7500000000000002, 1.299038105676658], {Rule[f, Power[E, Times[Complex[0, Rational[1, 6]], Pi]]], Rule[x, 1.5]}}
    ```
    where the first entry is the result: `Complex[0.7500000000000002, 1.299038105676658]` followed by the tested values for each variable: `f` was defined as `Power[E, Times[Complex[0, Rational[1, 6]], Pi]]` and `x` was tested for `1.5`. As we can see, `f` was mistaken as a variable which lead to the wrong result.
    This case was also discussed in the paper and shown in Table 3 in the Appendix.


### 4.ii. Reproducing Translation Results

If you have not installed Java 11 yet, please do so now by
```
$ cd bin
$ sudo ./java-installer.sh
$ cd ..
```

If you enter `java -version`, information about Java 11 should be returned, e.g., `openjdk 11 2018-09-25`.

For reproducing the translation results, you can use the `lacast-dlmf-translation.sh` script either with the `--mathematica` flag for Mathematica or `--maple` flag for Maple (only one is allowed). If not specified otherwise, the input file is `dlmf/dlmf-formulae.txt` and the output path is `dlmf/results-generated/<CAS>Translations` where `<CAS>` is the CAS you translated to. You can run a subset of tests by specifying the start and end line of the dataset. For example, consider you only want to translate lines 5-8 (the last number is always exclusive, i.e., we translate lines 5, 6, and 7) to Mathematica:
```
$ ./lacast-dlmf-translation.sh --mathematica --min 5 --max 8
```
This produces the output `dlmf/results-generated/MathematicaTranslations/translations.txt` with the content:
```
5 [http://dlmf.nist.gov/1.2.E5]: Binomial[n,0]+Binomial[n,2]+Binomial[n,4]+ \[Ellipsis]+Binomial[n,\[ScriptL]] == (2)^(n - 1)
6 [http://dlmf.nist.gov/1.2.E6]: Binomial[z,k] == Divide[z*(z - 1) \[Ellipsis](z - k + 1),(k)!] == Divide[(- 1)^(k)* Pochhammer[- z, k],(k)!] == (- 1)^(k)*Binomial[k - z - 1,k]
7 [http://dlmf.nist.gov/1.2.E7]: Binomial[z + 1,k] == Binomial[z,k]+Binomial[z,k - 1]
```

For reproducing all translations, remove the limits. This may take 1-2 min. If you use `diff` to investigate differences between our reported file and your output, please use the `--strip-trailing-cr` option to avoid showing differences only in line endings.

For reference, the file `config/together-lines-orig.txt` contains the line limits for the corresponding chapter numbers and codes in the DLMF. For example, if you want to translate only Chapter 4 of the DLMF (Elementary Functions), line 4 in the file specifies the first line at `1462` and the last line (exclusive) at `1991`.

### 4.iii. Reproducing Symbolic Evaluation Results

Please read the "Setup Computer Algebra Systems Support" section below before you continue with this section since it requires either Maple or Mathematica to be present. 

Since reproducing all results may require some time, we first explain how to set up a smaller test set.

1. Make sure you updated `lacast.config.yaml` according to your CAS installation.
2. Update the file `config/symbolic_tests.properties` as you like. Specifically, change the `subset_tests` values to a smaller range, for example `subset_tests=1,100` to test lines 1-100. For reference, the `config/together-lines-orig.txt` shows the lines for each DLMF chapter. You may want to change the output directory at the end of the file, too.
3. Run `./lacast-eval-symbolic.sh --maple` or `./lacast-eval-symbolic.sh --mathematica` to trigger our symbolic evaluation pipeline.
4. Inspect the results generated (the location was specified in the config file at `output=`). By default, it should be in `./dlmf/results-generated/test-symbolic.txt`. If you used Mathematica, the output should be identical to the first 100 cases in `dlmf/results-original/MathematicaSymbolic/01-AL-symmbolic.txt`.

For reproducing our results on DLMF chapters or even the entire DLMF, you can use the manual method outlined above or use a particular script that does the updating for you: `./scripts/symbolic-evaluator.sh`. Be advised that this may take a long time to complete depending on the timeout threshold specified in the config file.

1. The following script is defined by the `config/symbolic_tests-base.properties` file, which overwrites and updates the `symbolic_tests.properties` file mentioned above. If you want to change the test setup, change the file with the `-base` suffix!
2. You do not need to specify `output`, `missing_macro_output`, and the `subset_tests` range in the file with the `-base` suffix. This will be updated automatically by the script.
3. The output path and range are defined with the `together-lines.txt` file. For example, if you want to reproduce the results for Mathematica Chapter 4 and 8 of the DLMF, the file only contains the following two lines:
    ```
    04-EF: 1462,1991
    08-IG: 2445,2718
    ```
    The `together-lines-orig.txt` contains all chapters and limits. Simply copy the lines from here.

4. Run the script either with `--maple` for Maple or `--mathematica` for Mathematica
    ```
    $ ./scripts/symbolic-evaluator.sh --mathematica
    ```
5. Inspect the results. In the case of Chapter 4 and Mathematica for the CAS, the result is in `dlmf/results-generated/MathematicaSymbolic/04-EF-symbolic.txt`, and the file should be identical (up to version differences if you used a different Mathematica version) with our reference in `dlmf/results-original/MathematicaSymbolic/04-EF-symbolic.txt`
The script in step 4 will return a short overview of the processed chapters. If everything went smoothly, all chapters listed in the end show a 0. This 0 is the exit code of `java`. If anything goes wrong, the exit code is not 0. In this case, the output file for that chapter was not generated. If everything finished successfully, you see:
    ```
    The following lists the exit codes of the performed symbolic evaluations.
    If an exit code was different to 0, something went wrong and the output was not generated for that chapter!

    Results:
    04-EF: 0 
    08-IG: 0
    ```

If you wish to reproduce all of our results at once, follow these instructions:
1. Copy the content of `config/symbolic_tests-orig.properties` (contains our original setup with a timeout of 30 seconds per test case) into `config/symbolic_tests-base.properties`.
2. Copy the content of `config/together-lines-orig.txt` into `config/together-lines.txt`
3. Start the script either for Maple or Mathematica, such as
    ```
    $ ./scripts/symbolic-evaluator.sh --mathematica
    ```
4. Drink some coffee and check for updates from time to time. On our machine with a timeout of 30 seconds per test case, it took Mathematica around 3-4 hours to finish. Ideally, your `dlmf/results-generated/MathematicaSymbolic` folder is identical to our `dlmf/results-original/MathematicaSymbolic` folder. 

**Trouble Shooting**:
In some cases, e.g., due to memory overflows, a single chapter is too large. You can spot issues easily in the summary in the end. For example:
```
Results:
...
17-QH: 0
18-OP: 0
19-EL: 1
20-TH: 0
21-MT: 0
...
```
This output shows that Chapter 19 did not finish successfully. In this case, you should delete all lines from `config/together-lines.txt` except for `19-EL: 6029,6704` and re-run the script. If the logs show that you may run into memory issues, you can try to split the chapter, for example, by updating `config/together-lines.txt` to:
```
19-EL-1: 6029,6500
19-EL-2: 6500,6704
```
For reproducing the symbolic evaluations, you should not run into these memory issues. However, it is very likely that you may encounter this issue when reproducing the numeric evaluation results.
Further, Maple often ignores our specified timeout (see also our FAQ below). When this happens, you see a bunch of logs (debug level) that only update memory usage. We do not have a solution for this yet besides wait and hope Maple recovers. If it takes too long time use `CTRL+C` to stop the current computation. This aborts the evaluation pipeline only for the current chapter. If you used the script `symbolic-evaluator.sh`, LaCASt will simply continue with the next chapter. In the summary at the end, your manually stopped chapter has an exit code different to 0.

### 4.iv. Reproducing Numeric Evaluation Results

Please read the "Setup Computer Algebra Systems Support" section below before you continue with this section since it requires either Maple or Mathematica to be present.

Before reproducing the numeric evaluation results, try to reproduce the less complicated symbolic results above. 
Compared to the symbolic results, the numeric evaluation pipeline may take much more time and require much more resources to compute.
Again, it strongly depends on the timeout threshold you specify in the config. For our results presented in the paper, we set a timeout of 30 seconds. On our machine (8 Core á 2.60GHz and 32GB RAM), the numeric evaluations on the DLMF took over 8 hours with Mathematica.

The numeric evaluations are organized like the symbolic evaluations outlined above with the `config/numerical_tests.properties` file, including the mentioned files with `-base` and `-orig` (for reference) suffixes. In the following, we presume you understand these files and what they do base on your experiments with the symbolic evaluation engine. There are a couple of specific differences in these properties files:
* You either specify `subset_tests` to define the test lines or `symbolic_results_data`, which points to a result file generated by our symbolic evaluation pipeline. If you specify `symbolic_results_data`, the `subset_tests` entry is ignored (!), and numeric tests are only triggered on the test cases that failed in the given file `symbolic_results_data`. As explained in the paper, we only run numeric evaluations on failed symbolic evaluation test cases. If an equation was symbolically verified, they are almost certainly numerically successful too. Hence, we can focus on the remaining non-successful cases. Our dataset's first numeric test case is for line 54 because all other cases were either skipped or symbolically successful.
* The `numerical_values` entry specifies the test values as explained in the paper.
* The `special_variables` are variables that are automatically set to the values defined in `special_variables_values` (also explained in the paper)
* The values `test_if_lhs_null`, `test_if_rhs_null`, and `test_expectation` are old values only applied to Maple. We strongly recommend not changing them and stick with the default setup `test_expression=(#LHS)-(#RHS)` unless you carefully studied the source code of LaCASt.

To reproduce a subset of the results, do the following steps:
1. Make sure you updated `lacast.config.yaml` according to your CAS installation.
2. Update the file `config/numerical_tests.properties` as you like. Consider you generated the first 100 symbolic tests before. You can use your own results to test the failed cases by setting `symbolic_results_data=./dlmf/results-generated/test-symbolic.txt`
3. Run `./lacast-eval-numeric.sh --maple` or `./lacast-eval-numeric.sh --mathematica` to trigger our numeric evaluation pipeline.
4. Inspect the results file generated at the location specified in the config file at `output=`. By default, it should be in `./dlmf/results-generated/test-numeric.txt`. In case you used Mathematica, the output should look like this:
    ```
    Overall: [TOTAL: 2, SKIPPED: 0, DEFINITIONS: 0, STARTED_TEST_CASES: 2, ERROR_TRANS: 0, MISSING: 0, SUCCESS_TRANS: 2, SUCCESS_SYMB: 0, SUCCESS_NUM: 1, SUCCESS_UNDER_EXTRA_CONDITION: 0, FAILURE: 1, NO_TEST_VALUES: 0, ABORTED: 0, ERROR: 0] for test expression: (#LHS)-(#RHS)
    54 [http://dlmf.nist.gov/1.4.E8]: Failed [30/30]: {{Complex[0.7500000000000002`, 1.299038105676658`], {f := Power[E, Times[Complex[0, Rational[1, 6]], Pi]], x := Rational[3, 2]}}, {Complex[0.25000000000000006`, 0.4330127018922193`], {f := Power[E, Times[Complex[0, Rational[1, 6]], Pi]], x := Rational[1, 2]}}, {Complex[1.0000000000000002`, 1.7320508075688772`], {f := Power[E, Times[Complex[0, Rational[1, 6]], Pi]], x := 2}}, {Complex[-0.7499999999999997`, -1.299038105676658`], {f := Power[E, Times[Complex[0, Rational[2, 3]], Pi]], x := Rational[3, 2]}}, {Complex[-0.2499999999999999`, -0.43301270189221935`], {f := Power[E, Times[Complex[0, Rational[2, 3]], Pi]], x := Rational[1, 2]}}, ...}
    54-a [http://dlmf.nist.gov/1.4.E8]: Successful [Tested: 30]
    ```
    Again, the first line summarizes the file (two started test cases, two successfully translated, one successfully numerically verified, one failed). The second and third lines contain the two test cases (because line 54 was a multi-equation, the case was split into the first and second equations). This should be identical to the first two tests in `./dlmf/results-original/MathematicaNumeric/01-AL-numeric.txt`. The notation style slightly differs from the reference because we constantly updated LaCASt for our upcoming projects, and for those, the `Rule[., .]` pattern was broken into its arguments. Our new format contains the more readable notation `f := 1` rather than `Rule[f, 1]`.

Like the scripts for symbolic evaluations, we have a more convenient script to test entire chapters of the DLMF, which again relies on the entries in `config/together-lines.txt` file. Be aware that this script does not set the `subset_tests` range but instead uses the chapter number and code in `together-lines.txt` to set the `symbolic_results_data` property. For convenience, the script has an additional `-r` flag for reverse mode, i.e., rather than testing all symbolically failed test cases, all successful cases are numerically evaluated.
For reproducing our results on entire chapters, do the following:
1. Update `config/numerical_tests-base.properties` as you like. Our setup is given in `config/numerical_tests-orig.properties`.
2. You do not need to specify `output`, `subset_tests`, or `symbolic_results_data` in the file with the `-base` suffix. This will be updated automatically by the script.
3. The output path is defined with the `together-lines.txt` file. If you want to reproduce the results for Mathematica Chapter 4 and 8 of the DLMF, the file only contains the following two lines:
    ```
    04-EF: 1462,1991
    08-IG: 2445,2718
    ```
    The `together-lines-orig.txt` contains all chapters and limits. Simply copy the lines from here.

4. Run the script either with `--maple` for Maple or `--mathematica` for Mathematica. Consider you used the default settings in the config file. Evaluating these two chapters may take 10 - 20 minutes, depending on your machine.
    ```
    $ ./scripts/numeric-evaluator.sh --mathematica
    ```
5. Inspect the results. In case you used Mathematica, you should find 2 files in `dlmf/results-generated/MathematicaNumeric/` namely `04-EF-numeric.txt` and `08-IG-numeric.txt`. Once a chapter finished, the file is immidiately written. You do not need to wait for both chapters to finish. The generated files should be identical almost identical with their references in `dlmf/results-original/<CAS>Numeric`. As pointed out above, the notation style for setting variables may differ in Mathematica. Similarly to the symbolic evaluation counterpart, after all computations finished a short summary is shown for each chapter in case a chapter was not finished successfully. Consider everything went well, you should see the familiar message from above:
    ```
    The following lists the exit codes of the performed symbolic evaluations.
    If an exit code was different to 0, something went wrong and the output was not generated for that chapter!

    Results:
    04-EF: 0 
    08-IG: 0
    ```

Like the symbolic evaluations, you can reproduce all of our results simply by copying all lines from `together-lines-orig.txt` into `together-lines.txt` and re-run the script `./scripts/numeric-evaluator.sh`.
However, we do not recommend doing that, especially not in a Virtual Machine with limited resources. Even on our machine, some chapters needed to be split into subsets to manage memory usage. 

Note that exactly reproducing the results might be difficult because, on one machine, the CAS may have more RAM or CPU available to finish a calculation slightly faster and therefore might dodge the timeout threshold.

## 5. Setup Computer Algebra Systems

In the following, we explain how to set up Mathematica or Maple to reproduce our evaluation results.

### 5.i. Mathematica

In case you have Mathematica installed on your system, you can go directly to "Setup LaCASt with Mathematica" below. Otherwise, please follow the instructions on how to install the Wolfram Engine.

#### 5.i.a. Install Wolfram Engine and Activate License

If you do not have Mathematica installed, we recommend using the free "Wolfram Engine for Developers": https://www.wolfram.com/engine/.
For license reasons, we are not allowed to share the install script with this artifact. Hence, you have to download the script first and install it, which requires an internet connection! The official guide for installation and activation can be found at: https://support.wolfram.com/46072. You can follow our steps below which are shorter but essentially the same.

1. Change to `bin` folder in LaCASt
    ```
    $ cd ./bin
    ```
2. Download the install script
    ```
    $ wget -O WolframInstaller.sh https://account.wolfram.com/download/public/wolfram-engine/desktop/LINUX
    ```
3. Install (requires `sudo`)
    ```
    $ sudo ./WolframInstaller.sh
    ```
   When asked where to install WolframEngine, we recommend `/opt/Wolfram` for convenience. 
   When asked where to store Wolfram scripts, you can simply press enter for the default path.
   This process may take some time. 

4. After a successful installation, you must activate the license. If you do not have a license, go to https://account.wolfram.com/login/oauth2/sign-in and click `create one`. Fill out the form. After that, you should see a screen with "Get Your Free Wolfram Engine License". Accept the "Terms and Conditions of Use" and click `Get license` and follow the instructions on the website.
5. Start wolframscript and enter your credentials
    ```
    $ wolframscript
    The Wolfram Engine requires one-time activation on this computer.
    Visit https://wolfram.com/developer-license to get your free license.
    Wolfram ID: user@email.com
    Password:
    ```

**Trouble Shooting**: Although this is the recommended way to activate the license, we regularly encounter problems in Step 5 with wolframscript.
If this activation process does not work for you either, for whatever reason, you can try to activate the license manually. To do this 
1. Go to: https://www.wolframcloud.com/users/user-current/activationkeys
2. You might enter your credentials of your account again to login. 
3. You should get a JSON back with at least one activation key inside. It depends on your browser how this looks like. The raw information string might look like this:
    ```
    {"wolframengine":["XXXX-XXXX-XXXXXX","YYYY-YYYY-YYYYYY"]}
    ```
4. Copy the first key in "wolframengine" arrays. It has the form `XXXX-XXXX-XXXXXX` where `X` are different numbers or letters.
5. Now, go back to your terminal and move to the installation folder of Wolfram Engine. If you followed our recommendation from above, it is `/opt/Wolfram` 
    ```
    $ cd /opt/Wolfram/Executables/
    ```
5. Run the WolframKernel command:
    ```
    $ WolframKernel
    Wolfram Language ...
    Copyright 1988-2021 Wolfram Research, Inc.
    ...
    For automatic Web Activation enter your activation key
    (enter return to skip Web Activation):
    ```
6. And enter the key you copied from above. This should activate the license and directly start Mathematica in the console. Enter `Quit[]` to exit Mathematica.
7. Since you took the key, we recommend to put it directly into our config file: `lacast.config.yaml`. See the next section on how to do that.

If this still does not activate your license, you can only contact Wolfram Support and ask for help: https://www.wolfram.com/support/contact/email/?topic=Account

#### 5.i.b. Setup LaCASt with Mathematica

Once you have Mathematica (Wolfram Engine) installed and the license activated you must do two steps so that LaCASt can communicate with Mathematica: 
1. Update `lacast.config.yaml` with the following information:
    ```
    lacast.cas:
      Mathematica:
        install.path: "<mathematicadir>"
        native.library.path: "<mathematica-jlink-dir>"
        license: "XXXX-XXXX-XXXXXX"
    ```
    If you installed the Wolfram Engine as described above, your `<mathematicadir>` is `/opt/Wolfram`. The native library path depends on your OS. In case of Debian-like Ubuntu, it should be `<mathematicadir>/SystemFiles/Links/JLink/SystemFiles/Libraries/Linux-x86-64`. Lastly, the license is optional. LaCASt uses this license to re-activate the license in case we lose connection to Mathematica in the middle of computations. This sometimes helps to recover a running session automatically.

2. Copy `JLink.jar` into the libs folder. Again, the path depends on your OS. In Linux it should be in `<mathematicadir>/SystemFiles/Links/JLink/JLink.jar`. You just copy the following command if your Mathematica folder is `/opt/Wolfram/`:
    ```
    $ cp /opt/Wolfram/SystemFiles/Links/JLink/JLink.jar ./libs/
    ```

Now you can test if LaCASt is able to communicate with Mathematica:
```
$ ./scripts/test-mathematica.sh
```
You should see `Congrats, everything looks ok.` in the last line if everything works. If not, the script should tell you what is missing.
Now you can reproduce our symbolic and numeric evaluation results via Mathematica (see above).

### 5.ii. Maple

Since there is no free version of Maple available, we assume you have Maple on your system. In this case, you only need to do two steps to tell LaCASt where Maple is and how to call it.
1. Update `lacast.config.yaml` according to your Maple installation path. You need the `<mapledir>` which refers to the installation folder of Maple, e.g., `/opt/maple2020`, and `<bindir>` which contains system-specific binaries, e.g., `/opt/maple2020/bin.X86-64-LINUX`. If you are unsure where to find these paths, you can start Maple and run the command `kernelopts(mapledir);` to retrieve the `<mapledir>` and `kernelopts(bindir);` to retrieve `<bindir>`. Afterward, update `lacast.config.yaml` with the following entries:
    ```
    lacast.cas:
      Maple:
        install.path: "<mapledir>"
        native.library.path: "<bindir>"
    ```

2. Copy the `Maple.jar` and the `externalcall.jar` (both are usually in `<mapledir>/java/`) into the libs folder of this project. For example:
    ```
    $ cp /opt/maple2020/java/Maple.jar ./libs/Maple.jar
    $ cp /opt/maple2020/java/externalcall.jar ./libs/externalcall.jar
    ```

Now you can test if LaCASt is able to communicate with Maple:
```
$ ./scripts/test-maple.sh
```
You should see `Congrats, everything looks ok.` in the last line if everything works.
Now you can reproduce our symbolic and numeric evaluation results via Maple (see above).

## FAQ

**My results do not match yours.**
As pointed out in the beginning, there are many possible reasons why a specific test-case returns a different outcome on our machine compared to our reference (different CPU/RAM, different CAS version, etc.). In general, the discrepancy should be marginal and only occur in a couple of cases (less than 1% overall). If it happens in many cases, however, it might be an indication that something went wrong. Please contact us in this case.

**The data on your website is different from the results in this artifact.**
The reason is that this artifact is slightly newer compared to the version we used when writing the paper. We plan to update the results in the paper and on the website with this artifact version in case of acceptance to ensure the most recent yet consistent references between the artifact and the paper.

**My Maple computation never stops and exceeds my timeout threshold.**
Maple makes it hard for us to interrupt it. Currently, we use the `timelimit` option to limit computation time per test case. However, it seems that Maple ignores interruption signals if it is performing code on kernel-level. So even though we set a timeout, Maple ignores the timeout and continues processing from time to time. We identified this issue especially for cases that involve integrals. Maplesoft ackwonledged this issue and improved the handling of `timelimit` with Maple 2021. However, the issue may still persist. One possible option is to reduce the timeout threshold in the config file even further so that Maple stops before entering non-interruptible code. You can adjust the line numbers in `config/together-lines.txt` to avoid problematic reasons.

Another option is more aggressive but sometimes the only recovery. For better error handling, LaCASt starts a new java VM just for Maple. If Maple crashes, it may crash the entire VM which kills LaCASt, too. Hence, we come up with the solution to run Maple in a separated VM. This gives us the option to forcefully kill a single Maple computation if it just does not want to stop. This does not kill LaCASt but the single test case which is currently running. LaCASt will try to automatically recover from that crash by restarting the VM directly, skipping the died test case. You can find the process ID by searching for "MapleRmiServer". In short, you can open another terminal and enter the following command:
```
$ ps aux | grep MapleRmiServer | head -1 | awk '{print $2}' | xargs kill -9
```
You should see immediate progress in our evaluation terminal.

**Why does symbolic/numeric evaluating a single test (line) take so much time?** 
LaCASt always reads the entire dataset first in order to resolve potential substitutions and identify constraints in definitions. This is the reason you see so many messages saying: `Store line definition...` before the actual numeric test starts. The code is currently slow because for resolving substitutions, we parse every expression and replace subtrees. We are well aware of this bottleneck and working on a solution to speed this process up.

**Why do the values for a variable in the numeric evaluation result files not change?**
The numeric test values are ordered to ensure that every run performs the exact same tests even though we limit the number of test calculations. Consider there are three variables in our test case, and each is set to 10 test values. This results in 10^3 combinations. We order the combinations so that, for example, the first variable only changes if all other combinations of the remaining variables have been tried. If we limit our combinations to 100, unfortunately, the first variable was only tested for a single test value because it took 100 iterations to test all combinations of the remaining two variables. This is not ideal but ensures that every run, even with low limits of combinations, always tests the same combinations. Otherwise, a test case might be successful one time but may fail in the next run.

**Why do we only show a couple of numeric test calculations in the result data?**
Because the output would become very large otherwise. In most cases, further manual investigations are necessary to check why a numeric calculation failed. In this case, a few combinations of test values are often sufficient to investigate the problem further. To limit the generated data, we limit to print the number of failed calculations.

**Why is the output full of colorful messages?** 
Those are logs. We activated logging down to the debug level to provide a deeper insight into the process. Further, this makes it easier to spot issues if something goes wrong. Unfortunately, the output is rather verbose with this low level of logging.

**I cannot compile the source code, and my IDE shows numerous errors!**
Setting up LaCASt in an IDE is not straightforward because it is a large multi-module maven project with over 30k lines of code (LOC/SLOC). One of the main reasons you see errors is the dependency on the CAS jars for the build process. The current version (this artifact) still presumes that the jars for the CAS are available in the project itself (`Maple.jar` and `externalcall.jar` in `libs/Maple` and the `JLink.jar` in `libs/Mathematica`). In order to build it with maven, one needs to put these jars in those folders first. Additionally, our extension of LaCASt currently depends on the open-source project mathosphere as a submodule (see `.gitmodules`). Hence, one needs to check out `github.com/ag-gipp/mathosphere` in the `mathosphere` folder. We are currently working on removing the CAS dependencies in order to publish LaCASt on GitHub. LaCASt will be available (and compilable) on GitHub once our project is accepted at TACAS.

**Why are there so many script files rather than a single GUI I can interact with? This is so complicated.**
LaCASt was never planned to be a standalone visual program but a lightweight, fast converter for LaTeX. LaCASt is currently so heavy and slow only because of our evaluation pipeline and the dependency on the CAS. Translating the entire DLMF takes a couple of seconds. Evaluating it numerically, on the other hand, requires hours. Further, the dependency on commercial products (Maple and Mathematica) prevents us from providing a single executable JAR (also known as fat jar). To keep the actions required to reproduce the results as simple as possible, we created these scripts to make the process easier and more fail-safe for you. When we publish LaCASt on GitHub, we plan to invest more time into user-friendly handling.

## Glossary

- CAS: Computer Algebra System(s)
- DLMF: Digital Library of Mathematical Functions: https://dlmf.nist.gov/
- LaCASt: LaTeX to Computer Algebra Systems translator [1, 2, 3]
- Maple: Major general-purpose Computer Algebra System by Maplesoft: https://www.maplesoft.com/
- Mathematica: Major general-purpose Computer Algebra System by Wolfram Research: https://www.wolfram.com/mathematica/


[1] H. S. Cohl, M. Schubotz, A. Youssef, A. Greiner-Petter, J. Gerhard, B. V. Saunders, M. A. McClain, J. Bang, K. Chen: "Semantic Preserving Bijective Mappings of Mathematical Formulae Between Document Preparation Systems and Computer Algebra Systems." In: CICM 2017, pp. 115-131. DOI: 10.1007/978-3-319-62075-6_9, URL: https://arxiv.org/abs/2109.08655

[2] H. S. Cohl, A. Greiner-Petter, M. Schubotz: "Automated Symbolic and Numerical Testing of DLMF Formulae Using Computer Algebra Systems." In: CICM 2018. pp. 39-52. DOI: 10.1007/978-3-319-96812-4_4, URL: https://arxiv.org/abs/2109.08899

[3] A. Greiner-Petter, M. Schubotz, H. S. Cohl, B. Gipp: "Semantic preserving bijective mappings for expressions involving special functions between computer algebra systems and document preparation systems." In: Aslib Journal of Information Management. 71(3), pp. 415-439 (2019). DOI: 10.1108/AJIM-08-2018-0185, URL: https://arxiv.org/abs/1906.11485 
