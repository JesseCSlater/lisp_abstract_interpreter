package arithlang;

import java.util.HashSet;
import java.util.function.BiFunction;

//This class represents the abstract sign values after evaluation of a program. See the comment on the combine function
public class SignValue {

    private HashSet<Sign> _val = new HashSet<>();
    public SignValue(){}
    public SignValue(HashSet<Sign> s){ _val = s;}
    public SignValue(double v) {
        if (v == 0) _val.add(Sign.Zero);
        if (v < 0) _val.add(Sign.Negative);
        if (v > 0) _val.add(Sign.Positive);
    }
    public HashSet<Sign> v() { return _val; }
    public String toString() {
        return _val.toString();
    }

    //This function makes the .visit method simpler by computing the full set of _Value which can result when
    //applying an operator pairwise to every combination of indivitual values in the two _Value sets. If the first
    //_Value set is empty, it simply returns the second one, simplifying the definition of .visit for functions like
    // - and /.
    public void combine(SignValue other, BiFunction<Sign, Sign, HashSet<Sign>> f){
        if (this._val.isEmpty()) {
            this._val = other._val;
            return;
        }
        HashSet<Sign> val = new HashSet<>();
        for (Sign va: this._val) {
            for (Sign vb: other._val) {
                val.addAll(f.apply(va, vb));
            }
        }
        this._val = val;
        if (val.contains(Sign.None)) {
            _val.clear();
            _val.add(Sign.None);
        }
    }


    public enum Sign {
        Positive,
        Negative,
        Zero,
        None,
    }
}
