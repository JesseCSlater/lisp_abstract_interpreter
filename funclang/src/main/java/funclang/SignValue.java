package funclang;

import java.util.HashSet;
import java.util.function.BiFunction;

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
        //if (val.contains(Sign.None)) {
        //    _val.clear();
        //    _val.add(Sign.None);
        //}
    }

    public void combineAdd(SignValue other) {
        this.combine(other,
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
            });
    }

    public void combineDiv(SignValue other) {
        this.combine(other,
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

    public void combineMult(SignValue other) {
        this.combine(other,
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

    public void combineSub(SignValue other) {
        this.combine(other,
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

    public enum Sign {
        Positive,
        Negative,
        Zero,
        None,
    }
}
