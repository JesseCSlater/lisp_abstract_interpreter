package arithlang;
import static arithlang.AST.*;
import static arithlang.ConcreteValue.*;

import java.util.List;

public class ConcreteEvaluator implements Visitor<ConcreteValue> {
    Printer.Formatter ts = new Printer.Formatter();
	
    ConcreteValue valueOf(Program p) {
        // Value of a program in this language is the value of the expression
        return (ConcreteValue) p.accept(this);
    }
	
    @Override
    public ConcreteValue visit(AddExp e) {
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
    public ConcreteValue visit(NumExp e) {
        return new NumVal(e.v());
    }

    @Override
    public ConcreteValue visit(DivExp e) {
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
                    return new DynamicError("Error: division by zero at: " + ts.visit(e));
                }
                result = result / intermediate;            }
            else return (DynamicError) rVal;
        }
        return new NumVal(result);
    }

    @Override
    public ConcreteValue visit(MultExp e) {
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
    public ConcreteValue visit(Program p) {
        return (ConcreteValue) p.e().accept(this);
    }

    @Override
    public ConcreteValue visit(SubExp e) {
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
