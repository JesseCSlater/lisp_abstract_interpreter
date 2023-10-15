package arithlang;
import static arithlang.AST.*;
import static arithlang.Value.*;

import java.util.List;

public class Evaluator implements Visitor<Value> {
    private NumVal record = new NumVal(0);
    Printer.Formatter ts = new Printer.Formatter();
	
    Value valueOf(Program p) {
        // Value of a program in this language is the value of the expression
        return (Value) p.accept(this);
    }
	
    @Override
    public Value visit(AddExp e) {
        List<Exp> operands = e.all();
        double result = 0;
        for(Exp exp: operands) {
            Object val = exp.accept(this);
            if (val instanceof NumVal) {
                result += ((NumVal) val).v(); //Semantics of AddExp in terms of the target language
            }
            else if (val instanceof DynamicError) {
                return (DynamicError) val;
            }
        }
        return new NumVal(result);
    }

    @Override
    public Value visit(NumExp e) {
        return new NumVal(e.v());
    }

    @Override
    public Value visit(DivExp e) {
        List<Exp> operands = e.all();
        Object lVal = operands.get(0).accept(this);
        double result;
        if (lVal instanceof NumVal) {
            result = ((NumVal) lVal).v(); //Semantics of AddExp in terms of the target language
        }
        else return (DynamicError) lVal;
        for(int i=1; i<operands.size(); i++) {
            Object rVal = operands.get(i).accept(this);
            if (rVal instanceof NumVal) {
                double intermediate = ((NumVal) rVal).v();
                if (intermediate == 0) {
                    return new DynamicError("Error: Division by zero at: " + ts.visit(e));
                }
                result = result / intermediate;            }
            else return (DynamicError) rVal;
        }
        return new NumVal(result);
    }

    @Override
    public Value visit(MultExp e) {
        List<Exp> operands = e.all();
        double result = 1;
        for(Exp exp: operands) {
            Object val = exp.accept(this);
            if (val instanceof NumVal) {
                result *= ((NumVal) val).v(); //Semantics of AddExp in terms of the target language
            }
            else return (DynamicError) val;
        }
        return new NumVal(result);
    }

    @Override
    public Value visit(Program p) {
        return (Value) p.e().accept(this);
    }

    @Override
    public Value visit(SubExp e) {
        List<Exp> operands = e.all();
        Object lVal = operands.get(0).accept(this);
        double result;
        if (lVal instanceof NumVal) {
            result = ((NumVal) lVal).v(); //Semantics of AddExp in terms of the target language
        }
        else return (DynamicError) lVal;
        for(int i=1; i<operands.size(); i++) {
            Object rVal = operands.get(i).accept(this);
            if (rVal instanceof NumVal) {
                result = result - ((NumVal) rVal).v(); //Semantics of AddExp in terms of the target language
            }
            else return (DynamicError) rVal;
        }
        return new NumVal(result);
    }
}
