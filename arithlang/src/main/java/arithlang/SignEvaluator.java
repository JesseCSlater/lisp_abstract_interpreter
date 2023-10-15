package arithlang;

import java.util.HashSet;
import java.util.List;

import static arithlang.AST.*;
import static arithlang.SignValue.Sign;

public class SignEvaluator implements Visitor<SignValue> {

    SignValue valueOf(Program p) {
        // Value of a program in this language is the value of the expression
        return (SignValue) p.accept(this);
    }
	
    @Override
    public SignValue visit(AddExp e) {
        List<Exp> operands = e.all();
        SignValue result = new SignValue();
        for(Exp exp: operands) {
            result.combine(
                (SignValue) exp.accept(this),
                (s1, s2) -> {
                    HashSet<Sign> ret = new HashSet<>();
                    if (s1 == Sign.None || s2 == Sign.None) ret.add(Sign.None);
                    else if (s1 == Sign.Zero) ret.add(s2);
                    else if (s2 == Sign.Zero) ret.add(s1);
                    else if (s1 == s2) ret.add(s1);
                    else {
                        ret.add(Sign.Zero);
                        ret.add(Sign.Positive);
                        ret.add(Sign.Negative);
                    }
                    return ret;
                }
            );
        }
        return result;
    }

    @Override
    public SignValue visit(NumExp e) {
        return new SignValue(e.v());
    }

    @Override
    public SignValue visit(DivExp e) {
        List<Exp> operands = e.all();
        SignValue result = new SignValue();
        for(Exp exp: operands) {
            result.combine(
                (SignValue) exp.accept(this),
                (s1, s2) -> {
                    HashSet<Sign> ret = new HashSet<>();
                    if (s1 == Sign.None || s2 == Sign.None) ret.add(Sign.None);
                    else if (s2 == Sign.Zero) ret.add(Sign.None);
                    else if (s1 == Sign.Zero) ret.add(Sign.Zero);
                    else if (s1 == s2) ret.add(Sign.Positive);
                    else ret.add(Sign.Negative);
                    return ret;
                }
            );
        }
        return result;
    }

    @Override
    public SignValue visit(MultExp e) {
        List<Exp> operands = e.all();
        SignValue result = new SignValue();
        for(Exp exp: operands) {
            result.combine(
                (SignValue) exp.accept(this),
                (s1, s2) -> {
                    HashSet<Sign> ret = new HashSet<>();
                    if (s1 == Sign.None || s2 == Sign.None) ret.add(Sign.None);
                    else if (s1 == Sign.Zero || s2 == Sign.Zero) ret.add(Sign.Zero);
                    else if (s1 == s2) ret.add(Sign.Positive);
                    else ret.add(Sign.Negative);
                    return ret;
                }
            );
        }
        return result;
    }

    @Override
    public SignValue visit(Program p) {
        return (SignValue) p.e().accept(this);
    }

    @Override
    public SignValue visit(SubExp e) {
        List<Exp> operands = e.all();
        SignValue result = new SignValue();
        for(Exp exp: operands) {
            result.combine(
                    (SignValue) exp.accept(this),
                    (s1, s2) -> {
                        HashSet<Sign> ret = new HashSet<>();
                        if (s1 == Sign.None || s2 == Sign.None) ret.add(Sign.None);
                        else if (s2 == Sign.Zero) ret.add(s1);
                        else if (s1 == Sign.Zero && s2 == Sign.Positive) ret.add(Sign.Negative);
                        else if (s1 == Sign.Zero && s2 == Sign.Negative) ret.add(Sign.Positive);
                        else if (s1 != s2) ret.add(s1);
                        else {
                            ret.add(Sign.Zero);
                            ret.add(Sign.Positive);
                            ret.add(Sign.Negative);
                        }
                        return ret;
                    }
            );
        }
        return result;
    }
}
