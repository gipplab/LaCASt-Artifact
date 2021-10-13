Artifact for
"Comparative Verification of the Digital Library of Mathematical Functions and Computer Algebra Systems"
by A. Greiner-Petter, H. S. Cohl, A. Youssef, M. Schubotz, A. Trost, R. Dey, A. Aizawa, and B. Gipp.
TACAS 2022.
========================================================================================================

## Preamble

Some of the results can only be reproduced if the CAS Maple or the CAS Mathematica (or both) are installed. This artifact does *NOT* contain these software packages due to license limitations. For reproducing our results partially, one can install the free "Wolfram Engine for Developers". For installing this free package, internet access is required for downloading and the activation process. For reference, we created our results with Maple version 2020.2 and Mathematica version 12.1.1.

## Minimal Instructions

Copy the `artifact.zip` to a convenient place and unpack it. In the following, we assume a Debian-based Linux system. However, other OS are also supported unless Java 11 or higher is installed. For a Debian-based Linux system, you can install Java 11 with

```
$ cd bin
$ sudo ./java-installer.sh
$ java --version
openjdk 11 2018-09-25
...
$ cd ..
```

And try out our translator LaCASt via
```
$ ./lacast.sh -i
```

Instructions for the symbolic and numeric evaluation pipeline follow below.

--------------------------------------------------------------------------------------------------------

## Content of this articaft

- README.md: Project description file.
- lacast.sh: Script to interact with LaCASt.
- lacast-dlmf-translation.sh: Translates all DLMF formulae as presented in the paper. See instructions below.
- lacast-eval-numeric.sh: Performs numeric evaluations on DLMF formulae for a specific CAS. See instructions below.
- lacast-eval-symboli.sh: Performs symbolic evaluations on DLMF formulae for a specific CAS. See instructions below.
- lacast.config.yaml: The basic config for LACASt. Must be updated when working with CAS for numeric/symbolic evaluations.
- config: Contains relevant config files required to run numeric/symbolic evaluations.
- bin: Contains the necessary software packages for Java 11 and the install script of Wolfram Engine.
- libs: Contains libraries necessary to run LaCASt.
- src: Contains the source of LaCASt and our evaluation.
- dlmf: The data folder. See also below for more detailed explanations.
  * dlmf-formulae.txt: Contains every formula extracted from the DLMF in a single line annotated with additional LaTeX meta information.
  * results-original: Contains the results as reported in the paper.
  * results-generated: Supposed to contain the outputs you are going to generate with our scripts.
- scripts: Contains additional scripts that help to run symbolic/numeric evaluations on the entire DLMF. Please read the instructions carefully before you begin using these scripts since they may take some time and require a lot of computational power.

--------------------------------------------------------------------------------------------------------

## Content of this Readme

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
    1. Setup LaCASt with Maple
6. Glossary

--------------------------------------------------------------------------------------------------------

## Reproducing Reported Results

Our results can be split in two groups: (1) translations via LaCASt, and (2) evaluations of the translations via CAS. For (1), only Java 11 or higher is required. For (2) one requires either Maple or Mathematica (i.e., the Wolfram Engine). In the following, we explain how to reproduce our results and how to read the data. Before attempting to reproduce (2), read the "Setup Computer Algebra Systems Support" section below. Note that all results are also available online (in a visually more appealing look) at: https://lacast.wmflabs.org/.

In the following, we presume you use a single terminal and moved into the unpackaged artifact folder.

### How to read the data?

Our dataset is `dlmf/dlmf-formulae.txt` which contains all original DLMF formulae in semantic LaTeX. Semantic LaTeX is a semantic enriched LaTeX dialect developed by Bruce Miller for the DLMF. LaCASt is only able to translate these macros for functions. Hence, translating `\Gamma(x)` will not produce a correct translations because LaCASt presumes the unambgious notation `\EulerGamma@{x}`.

The reported results in our paper in raw format (in contrast to the visual tables on our website: lacast.wmflabs.org) can be found in `dlmf/results-original`. There are three groups of results for each CAS (Maple and Mathematica).

* <CAS>Translations: Contains just the translations to each CAS for each line in `dlmf-formulae.txt`. For example, line 8 of `dlmf-formulae.txt` is found in line 8 of `MapleTranslations/translations.txt`:
```
8 [http://dlmf.nist.gov/1.2.E8]: sum(binomial(z + k,k), k = 0..m) = binomial(z + m + 1,m)
```
* <CAS>Symbolic: Contains two types of files `<num>-<code>-missing.txt` and `<num>-<code>-symbolic.txt` where `<num>` is the chapter number of the DLMF and `<code>` the chapter code. The `missing` file contains semantic macros that cannot be translated to the CAS, e.g., because there is no mapping for this macro defined. The `symbolic` files contain the summary of the symbolic simplification evaluations for each chapter. The first line is a summary of the results for the chapter. It shows how many lines were recognized, how many of them were skipped, how many were definitions, etc. The following lines show the results for each test case. Consider line 7 of the `dlmf-formulae.txt` which refers to the DLMF equation 1.2.E7. Here the result in Maple is:
```
7 [http://dlmf.nist.gov/1.2.E7]: Successful [Simple: NaN, ConvEXP: NaN, ConvHYP: NaN, EXP: 0, EXP+EXP: 0, EXP+HYP: 0]
```
As described in our paper, we used multiple simplification approaches to simplify the difference of the left- and right-hand side of equation 1.2.E7 to 0. This result line shows that our first three tests: Simple (just `simplify`), ConvEXP (`convert(..., exp)`), and ConvHYP (`convert(..., hypergeom)`) were unable to return 0 but the last three via EXP worked (`expand(...)` and `expand(..., exp)` and `expand(..., hypergeom)`). In case of Mathematica (`MathematicaSymbolic/01-AL-symbolic.txt`) we only used the `FullSimplify` method.
* <CAS>Numeric: Contains the numeric evaluation results for each CAS in the same way as for the symbolic results. Let us explain the content of the files on a specific example. The first numeric test result is presented for line 54 in `MathematicaNumeric/01-AL-numeric.txt`. The reason for this is that we only performed numeric tests on non-symbolically verified equations to reduce computing time. The first test that symbolically failed (see `MathematicaSymbolic/01-AL-symbolic.txt`) was line 54. Skipped lines are not tested because if they are skipped for symbolic tests, we also skip them for numeric tests. Similarly, if a line cannot be translated by LaCASt, no numeric test is possible and we can skip the line too. The symbolic result in Mathematica for line 54 was
```
54 [http://dlmf.nist.gov/1.4.E8]: Failure [Simple: NaN]
54-a [http://dlmf.nist.gov/1.4.E8]: Successful [Simple: 0]
```
The `-a` suffix shows that this case was a multi-equation or contained `\pm`. The line 54 in our dataset indead contained two `=` symbols. The first line in our result dataset represents the first equation, the `-a` suffex represents the second equation.
Only the first equation failed symbolically. Numerically, the result line contains:
```
54 [http://dlmf.nist.gov/1.4.E8]: Failed [30/30]: {{Complex[0.7500000000000002, 1.299038105676658], {Rule[f, Power[E, Times[Complex[0, Rational[1, 6]], Pi]]], Rule[x, 1.5]}}, {Complex[0.25000000000000006, 0.4330127018922193], {Rule[f, Power[E, Times[Complex[0, Rational[1, 6]], Pi]]], Rule[x, 0.5]}}, ...
```
As usually, the result line contains the line number followed by the link to the DLMF. Next comes `Failed [30/30]` which means that 30 test calculations were triggered and all 30 of them failed (in some cases, not all test calculations failed, see for example line `55-a` in the same file). After this information, the first examples of failed cases are shown. The first failed case of the 30 tested cases was:
```
{Complex[0.7500000000000002, 1.299038105676658], {Rule[f, Power[E, Times[Complex[0, Rational[1, 6]], Pi]]], Rule[x, 1.5]}}
```
where the first entry is the result: `Complex[0.7500000000000002, 1.299038105676658]` followed by the tested values for each variable: `f` was defined as `Power[E, Times[Complex[0, Rational[1, 6]], Pi]]` and `x` was tested for `1.5`. As we can see, `f` was mistaken as a variable which lead to this wrong result.


### Reproducing Translation Results

If you have not installed Java 11 yet, please do so by
```
$ cd bin
$ sudo ./java-installer.sh
$ java --version
openjdk 11 2018-09-25
...
```

For reproducing the translation results, you can simply use the `lacast-dlmf-translation.sh` script either with the `-mm` flag for Mathematica or `-ma` flag for Maple (only one is allowed). If not specified otherwise, the input file `dlmf/dlmf-formulae.txt` is used as the input and the `dlmf/results-generated/<CAS>Translations` is used for the output. You can run a subset of tests by specifying the start and end line of the dataset. For example, consider you only want to translate lines 5-8 (last number is always exclusive, i.e., we translate lines 5, 6, and 7) to Mathematica:
```
$ ./lacast-dlmf-translation.sh -mm --min 5 --max 8
```
This produces the output `dlmf/results-generated/MathematicaTranslations/translations.txt` with the content:
```
5 [http://dlmf.nist.gov/1.2.E5]: Binomial[n,0]+Binomial[n,2]+Binomial[n,4]+ \[Ellipsis]+Binomial[n,\[ScriptL]] == (2)^(n - 1)
6 [http://dlmf.nist.gov/1.2.E6]: Binomial[z,k] == Divide[z*(z - 1) \[Ellipsis](z - k + 1),(k)!] == Divide[(- 1)^(k)* Pochhammer[- z, k],(k)!] == (- 1)^(k)*Binomial[k - z - 1,k]
7 [http://dlmf.nist.gov/1.2.E7]: Binomial[z + 1,k] == Binomial[z,k]+Binomial[z,k - 1]
```

For reproducing all translations, simply remove the limits. This may take 1-2 min.
For reference, the file `config/together-lines-orig.txt` contains the line limits for the corresponding chapter numbers and codes in the DLMF. For example, if you want to translate Chapter 4 of the DLMF (Elementary Functions), line 4 in the file specifies the first line at `1462` and the last line (exclusive) at `1991`.

### Reproducing Symbolic Evaluation Results

Please read the "Setup Computer Algebra Systems Support" section below before you continue with this section since it requires either Maple or Mathematica to be present. 

Since reproducing all results may require several hours, we first explain how you can setup a smaller test set.
1. Make sure you set the environment variables for the CAS you want to use, i.e., `LD_LIBRARY_PATH` and (in case of Maple) `MAPLE`. See the section about setting up CAS below for more explanations.
2. Update the file `config/symbolic_tests.properties` as you like. Specifically, change the `subset_tests` values to a smaller range, for example `subset_tests=1,10` to test lines 1-10. For reference, the `config/together-lines-orig.txt` shows the lines for each DLMF chapter. You may want to change the output directory at the end of the file, too. For this first test, the default should be `output=./dlmf/results-generated/test-symbolic.txt`
3. Run `lacast-eval-symbolic.sh --maple` or `lacast-eval-symbolic.sh --mathematica` to trigger our symbolic evaluation pipeline.
4. Inspect the results file which was generated at the value specified in the config file at `output=`. By default, it should be in `./dlmf/results-generated/test-symbolic.txt`. In case you used Mathematica, the output should be identical to the first 10 cases in `dlmf/results-original/MathematicaSymbolic/01-AL-symmbolic.txt`.

For reproducing our results on DLMF chapters or even the entire DLMF, you can use the manual method outlined above or use a special script that does the updating for you: `./scripts/symbolic-evaluator.sh`. Be adviced that this may take a long time to complete depending on the timeout threshold specified in the config file.
1. The following script is defined by the `config/symbolic_tests-base.properties` file which overwrites and updates the `symbolic_tests.properties` file mentioned above. If you want to change the test setup, change this the `-base` suffix file!
2. You do not need to specify `output`, `missing_macro_output`, and the `subset_tests` range. This will be updated automatically by the script.
3. The output path and range is defined with the `together-lines.txt` file. For example, if you want to reproduce the results for Mathematica Chapter 4 of the DLMF, the file only contains the following single line:
```
04-EF: 1462,1991
```
For reference, the `together-lines-orig.txt` contains all chapters and limits.

4. Run the script either with `--maple` for Maple or `--mathematica` for Mathematica
```
$ ./scripts/symbolic-evaluator.sh --mathematica
```
5. Inspect the results. In case you only specified Chapter 4 and Mathematica for the CAS, the result is in `dlmf/results-generated/MathematicaSymbolic/04-EF-symbolic.txt` and the file should be identical (up to version differences if you used a different Mathematica version) with our reference in `dlmf/results-original/MathematicaSymbolic/04-EF-symbolic.txt`

If you wish to reproduce all of our results at once, follow these instructions:
1. Copy the content of `config/symbolic_tests-orig.properties` (contains our original setup with a timeout of 30 seconds per test case) into `config/symbolic_tests-base.properties`.
2. Copy the content of `config/together-lines-orig.txt` into `config/together-lines.txt`
3. Start the script either for Maple or Mathematica, such as
```
$ ./scripts/symbolic-evaluator.sh --mathematica
```
4. Drink some coffee and check for updates from time to time. Ideally, your `dlmf/results-generated/MathematicaSymbolic` folder is identical to our `dlmf/results-original/MathematicaSymbolic` folder.

### Reproducing Numeric Evaluation Results

Please read the "Setup Computer Algebra Systems Support" section below before you continue with this section since it requires either Maple or Mathematica to be present.

Before aiming to reproduce the numeric evaluation results, try to reproduce the less complicated symbolic results above. 
Compared to the symbolic results, the numeric evaluation pipeline may take much more time to compute.
Again, we first explain how to evaluate a small portion of the DLMF.

TODO TODO TODO

## Setup Computer Algebra Systems

In the following, we explain you setup Mathematica or Maple to reproduce our evaluation results with them.

### Mathematica

In case you have Mathematica installed on your system, you can go directly to "Setup LaCASt with Mathematica" below. Otherwise, please follow the instructions on how to install the Wolfram Engine.

#### Install Wolfram Engine and Activate License

If you do not have Mathematica installed, we recommend using the free "Wolfram Engine for Developers": https://www.wolfram.com/engine/.
For license reasons, we are not allowed to share the install script with this artifact. Hence, you have to download the script first and install it which requires an internet connection! 

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
4. After a successfull installation, you must activate the license. If you do not have a license, go to https://account.wolfram.com/login/oauth2/sign-in and click `create-one`. Fill out the form. After that, you should see a screen with "Get Your Free Wolfram Engine License". Accept the "Terms and Conditions of Use" and click `Get license` and follow the instrocutions on the website.
5. Start wolframscript and enter your credentials
```
$ wolframscript
```

**Trouble Shooting**: Even though this is the recommend way to activate the license, we regularly encounter problems in Step 5 with wolframscript.
If this activation process does not work for you, for what ever reason, you can try to active the license manually. To do this 
1. Go to: https://www.wolframcloud.com/users/user-current/activationkeys
2. Enter your credentials of your account. 
3. You should get a JSON back with at least one activation key inside. Copy the first ("0"). 
4. Now go back to your terminal and move to the installation folder of Wolfram Engine. If you followed our recommendation from above, it is `/opt/Wolfram` 
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
6. And enter the key you copied from above. This should activate the license and directly start Mathematica on console. Enter `Quit[]` to exit Mathematica. If this still does not activate your license, you can only contact Wolfram Support and ask for help: https://www.wolframcloud.com/users/user-current/activationkeys

#### Setup LaCASt with Mathematica

Once you have Mathematica (Wolfram Engine) installed and the license activated you must do three steps: 
1. Update `lacast.config.yaml` accordingly. If you installed the Wolfram Engine as described above, you only need to update the license key. This is the key you copied from above. Note that LaCASt and Mathematica may also work with another license key in this config. We only require it if something breaks and LaCASt tries to recover your activated license. So you can update this license in case something does not work properly.
2. Copy `JLink.jar` into the libs folder
```
$ cp /opt/Wolfram/SystemFiles/Links/JLink/JLink.jar ./libs/
```
3. Set the environment variable `LD_LIBRARY_PATH`:
```
$ export LD_LIBRARY_PATH="$LD_LIBRARY_PATH:/opt/Wolfram/SystemFiles/Links/JLink/SystemFiles/Libraries/Linux-x86-64"
```

Now you can test if LaCASt is able to communicate with Mathematica:
```
$ ./scripts/test-mathematica.sh
```
If everything works, you should see `Congrats, everything looks ok.` in the last line.
Now you can reproduce our symbolic and numeric evaluation results via Mathematica (see above).

### Maple

Since there is no free version of Maple available, we assume you have Maple on your system. In this case, you only need to do three steps in order to tell LaCASt where Maple is:
1. Update `lacast.config.yaml` according to your Maple installation path. If you are unsure where this path is, run Maple and enter `kernelopts(mapledir);`. This returns the path you should enter in `lacast.config.yaml`
```
lacast.cas:
  Maple:
    install.path: "<your path to maple>"
```
2. Set environment variable `MAPLE` to the Maple directory. This is the same path as in Step 1.
```
$ export MAPLE="<your path to maple>"
```
3. Set the environment variable `LD_LIBRARY_PATH` to the bin-directory of Maple. If you are unsure where this is, run Maple and enter `kernelopts(bindir);`. This returns the path to the bin directory. On Linux, it should be `<your path to maple>/bin.X86-64-LINUX`:
```
$ export LD_LIBRARY_PATH="$LD_LIBRARY_PATH:<your path to Maples bin dir>"
```

Now you can test if LaCASt is able to communicate with Maple:
```
$ ./scripts/test-maple.sh
```
If everything works, you should see `Congrats, everything looks ok.` in the last line.
Now you can reproduce our symbolic and numeric evaluation results via Maple (see above).

## Glossary

- CAS: Computer Algebra System
- DLMF: Digital Library of Mathematical Functions: https://dlmf.nist.gov/
- LaCASt: LaTeX to Computer Algebra Systems translator
- Maple: Major general-purpose Computer Algebra System by Maplesoft: https://www.maplesoft.com/
- Mathematica: Major general-purpose Computer Algebra System by Wolfram Research: https://www.wolfram.com/mathematica/

