# Running This Project

### Source Code
https://github.com/JesseCSlater/lisp_abstract_interpreter/blob/funcLangArith

(The completed code is on  branch is funcLangArith which is what is linked **not** master branch)

### Requirements
We were able to develop and run this project using the **IntelliJ IDE** and **Java jdk** (Tested with 17 but older versions likely work as well)


Downloads:
https://www.jetbrains.com/idea/download/?section=windows

https://www.oracle.com/java/technologies/downloads/ 

### Instructions
Once the project is opened in **IntelliJ** it should recognize the gradle project and either automatically build it or ask to build. If it doesn't build or needs to be rebuilt there is a build project icon in the top right as well.

After it finishes building navigate to *funclang/src/main/java/funclang/Interpreter.java*, the main function is located and can be run from this file.

Running this should open a Run tab at the bottom of the screen which will be the console to both run commands/programs from as well as view the output of those commands/programs

## What to Run

### Existing Language
We modified the existing language and project funclang used in **Iowa States'** Com S 342: *Principles of Programming Languages* course. Because this is not what we added to the project we won't go into full detail on how to use this language, but a few example and supported features are:

>(+ 2 3) //will output the sum of 2 and 3
>(* 3 4 5) //will output 3 * 4 * 5

Similar cases work for +, -, *, and /
>(define a 1) //a = 1

Create variable definitions

> (define add1  (lambda (a)  (+ a 1) ) ) //creates function with input a and returns a + 1
> //defines function as variable add1
> (add1 4) //outputs 5

Create lambda functions

Lastly, if a program is written in a .scm file under the examples folder in *funclang\src\main\java\funclang\* then the command:

>run *filename*.scm

will run that program

### What We Added

To declare an abstract variable in the first line of a .scm file will be 

>(abstract (*varName* **Value1** **Value2** **Value3**))

There can be as many or few abstract values as you want including no value input which will default to being a number of type NumPos, NumNeg, and NumZero. Also, there can be no space between the closing parenthesis and the last value.

##### Example

>(abstract (x NumPos NumZero) (y BTrue)) 

Creates variable x of abstract positive or negative number and variable y of abstract true boolean

##### List of Abstract Values:

1. TypeError 
1. UnsupportedFunctionError
1. UnsupportedTypeError
1. RuntimeError
1. NumPos
1. NumZero  
1. NumNeg  
1. BTrue
1. BFalse

Once these abstract values are defined they can be used as though they were replacing a number or boolean depending on their type. For example using the abstract definition above:

>(+ x 7) //x is zero or positive

will output [NumPos] as a zero or positive value plus 7 will be a positive number

# Testing

We included a number of test programs in the examples folder. Each of these is also commented with the expected outputs from them.

These are run as mentioned earlier for example to run test8.scm use:
>run test8.scm

