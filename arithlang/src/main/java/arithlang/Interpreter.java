package arithlang;
import java.io.IOException;
import arithlang.AST.*;

/**
 * This main class implements the Read-Eval-Print-Loop of the interpreter with
 * the help of Reader, Evaluator, and Printer classes. 
 * 
 * @author hridesh
 *
 */
public class Interpreter {
    public static void main(String[] args) {
        System.out.println("Type a program to evaluate and press the enter key," + 
                           " e.g. (+ (* 3 100) (/ 84 (- 279 277))) \n" + 
                           "Press Ctrl + C to exit.");
        Reader reader = new Reader();
        ConcreteEvaluator eval = new ConcreteEvaluator();
        SignEvaluator sign_eval = new SignEvaluator();
        Printer printer = new Printer();
        while (true) { // Read-Eval-Print-Loop (also known as REPL)
            Program p;
            try {
                p = reader.read();
                if(p._e == null) continue;
                ConcreteValue val = eval.valueOf(p);
                SignValue sign_val = sign_eval.valueOf(p);
                printer.print(val);
                System.out.println(sign_val.toString());
            } catch (IOException e) {
                System.out.println("Error reading input:" + e.getMessage());
            } catch (NullPointerException e) {
                System.out.println("Error:" + e.getMessage());
            }
        }
    }
}
