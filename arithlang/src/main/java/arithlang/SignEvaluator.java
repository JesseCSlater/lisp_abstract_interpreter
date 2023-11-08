package arithlang;

import java.util.HashSet;
import java.util.List;

import static arithlang.AST.*;
import static arithlang.SignValue.Sign;


//This class implements the abstract semantics for the sign abstract domain.
//We use the visitor pattern, so we must implement Visitor<T> interface (defined in AST) to do our abstract evaluation.
//For each type of node in the AST, we define an overload of the Visit function which computes the value of that node.
//If the AST node is a function, then it will have a .all() function which will provide a list of it's child nodes.
//To evaluate each of these nodes, use exp.accept(this). The reason you have to use this roundabout way of calling the
// .accept method is to get dynamic dispatch.
//For each visit method on an operation, you should write a lambda function which takes two values in the abstract
//domain, and returns the _Value set of all possible values from applying the operation to those two. Then use the
//_Value.combine function to combine two _Value set into another _Value set. Check the comment on .combine
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
