# A Tool for Computing Probabilistic Program Expectations

This is a lightweight Java-Tool which can calculate expectations in probabilistic programs. The theory behind
this calculator can be found in the following papers:
- A new Proof Rule for Almost-Sure Termination
- Fixpoint-Theory: Upside-Down

## Usage

First, you need to download the executable .jar file from "Releases" and simply execute it. The only requirement is Java 11 on your system.

The program requires multiple user inputs. Examples are listed here:

**Program C:** Here you need to input the program that will be analyzed. Examples for all the different operations and their syntax are here:
- Assignment: x=x+1
- Skip-Operation: skip
- Conditional: if{c=1}{x=x+1}else{x=x-1}
- Probabilistic Operator: {x=x+1}[1/2]{x=x-1}
- Demonic Choice: min{x=x+1}{x=x-1}
- While-Loop: while(c=1){{x=x+1}[1/2]{c=0}}

Multiple lines of code are to be separated with a colon ';' e.g. x=x+1;{x=x+1}[1/2]{x=x-1}

**Postexpectation F:** Here you need to input the postexpectation e.g. 'x^2'.

**Restriction k:** Here you need to input the variable restriction that ranges from {0,...,k}. This is especially important for while loops, as it restricts the possible variable assignments for a fixpoint iteration.

**Iteration Count:** Here you need to input the amount of iterations the fixpoint iteration should traverse before stopping. For the all-sigma iteration, this is optional. A blank input will result in an infinite iteration until a certain delta threshold is reached. The normal iteration requires a given count.

**Calculation Button:** Upon clicking the 'Calculation' button, all the inputs will be taken in consideration and a result will be displayed in the result window.

### All-Sigma Iteration
Upon selecting the checkbox 'enable all-sigma fixpoint iteration', the primary method for calculating the least fixpoint of while loops will be used for the calculation. Two further inputs are needed for this:
- All used variables: Here you need to input all variables that are used in the program (WIP will be extracted automatically in the future)
- A fixpoint iteration stop delta: Here you need to input a delta value, which compares the previous with the current fixpoint iteration. If the current iteration does not reach over the previous one by the amount of delta anymore, the program considers the fixpoint to be fully converged and stops the iteration.
More about the all-sigma iteration can be read in the final paper.

### Evaluating Fixpoints
After calculating a program that contains a while-loop with the all-sigma iteration, a new button labeled 'Evaluate Fixpoints' pops up. Here you can select all the different while-loops within the program and analyse their fixpoints further with the "Upside-Down" method. Two more inputs are needed:
- A delta, which will be used as the amount of reduction for each variable assignment (sigma) that remains in the subset of [Y]^X.
- A witness which we will analyse (a fixpoint which we will test, whether it is the least fixpoint or if there is still some "wiggle room"). When pressing the 'Select LFP' button, the least fixpoint (LFP) of the while loop will automatically get selected as a witness. Of course this means that analyzing the LFP will always result in an empty result set, as it already is the least fixpoint.

### Sigma-Forwarding
When selecting the checkbox 'Enable sigma-forwarding', a runtime optimization algorithm will try to optimize the inputted program C for the calculator by preparsing possibly known variable assignments. For example the program C: x=1;while(x=0){...} would be optimized to x=1;skip as theoretically, the while-loop would never be executed in the first place since the condition is always wrong.  
**WARNING:** This is still an experimental feature, any results are to be double-checked for correctness.

### Fixpoint Cache
To avoid unnecessary calculations, the program saves all fixpoints of while-loops in a chache storage. This cache can be reset, exported and imported again for future use of the software.  