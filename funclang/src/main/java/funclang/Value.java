package funclang;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import funclang.AST.Exp;

public interface Value {
	public String tostring();
	public void print();
	public static List<Value> acceptAll(List<AST.Exp> l, AST.Visitor<Value> t, Env env){
		return l.stream().map(
				(x) -> (Value) x.accept(t, env)
		).collect(Collectors.toList());
	}

	static class AbstractVal implements Value {
		public HashSet<Val> _vals = new HashSet<>();

		public AbstractVal() {}

		public AbstractVal(HashSet<Val> vals) {
			this._vals = vals;
		}

		@Override
		public String tostring() {
			return _vals.toString();
		}

		@Override
		public void print() {
			System.out.println(this.tostring());
		}

		public static AbstractVal combineArith(List<Value> vals, BiFunction<Val, Val, AbstractVal> f) {
			AbstractVal result = null;
			for (Value _val : vals) {
				AbstractVal currentVal;
				if (_val instanceof AbstractVal) {
					currentVal = (AbstractVal) _val;
				} else {
					currentVal = AbstractVal.ofValNum(_val);
				}
				if (result == null) {
					result = currentVal;
				} else {
					result = AbstractVal.combine(result, currentVal, f);
				}
			}
			return result;
		}

		public static AbstractVal combine(AbstractVal fst, AbstractVal snd, BiFunction<Val, Val, AbstractVal> f){
			if (fst._vals.isEmpty()) {
				return snd;
			}
			HashSet<Val> val = new HashSet<>();
			if (fst._vals.contains(Val.RuntimeError) || snd._vals.contains(Val.RuntimeError)) val.add(Val.RuntimeError);
			if (fst._vals.contains(Val.TypeError) || snd._vals.contains(Val.TypeError)) val.add(Val.TypeError);
			if (fst._vals.contains(Val.UnsupportedTypeError) || snd._vals.contains(Val.UnsupportedTypeError)) val.add(Val.UnsupportedTypeError);
			if (fst._vals.contains(Val.UnsupportedFunctionError) || snd._vals.contains(Val.UnsupportedFunctionError)) val.add(Val.UnsupportedFunctionError);
			for (Val va: fst._vals) {
				for (Val vb: snd._vals) {
					val.addAll(f.apply(va, vb)._vals);
				}
			}
			return new AbstractVal(val);
		}

		public static AbstractVal abstractEqual(Val s1, Val s2)
		{
			HashSet<Val> ret = new HashSet<>();
			if(s1 == s2) ret.add(Val.BTrue);
			else ret.add(Val.BFalse);
			return new AbstractVal(ret);
		}

		public static AbstractVal abstractGreater(Val s1, Val s2)
		{
			HashSet<Val> ret = new HashSet<>();
			if(s1 == Val.NumPos)
			{
				if(s2 == Val.NumPos)
				{
					ret.add(Val.BFalse);
					ret.add(Val.BTrue);
				} else {
					ret.add(Val.BTrue);
				}
			} else if (s1 == Val.NumZero) {
				if(s2 == Val.NumNeg)
				{
					ret.add(Val.BTrue);
				}
				else {
					ret.add(Val.BFalse);
				}
			} else if (s1 == Val.NumNeg) {
				if(s2 == Val.NumNeg)
				{
					ret.add(Val.BFalse);
					ret.add(Val.BTrue);
				}
				else {
					ret.add(Val.BFalse);
				}
			}
			else {
				ret.add(Val.BFalse);
			}
			return new AbstractVal(ret);
		}

		public static AbstractVal abstractAdd(Val s1, Val s2) {
			HashSet<Val> ret = new HashSet<>();
			if (s1 == Val.BTrue || s2 == Val.BTrue || s1 == Val.BFalse || s2 == Val.BFalse) ret.add(Val.TypeError);
			else if (s1 == Val.NumZero) ret.add(s2);
			else if (s2 == Val.NumZero) ret.add(s1);
			else if (s1 == s2) ret.add(s1);
			else {
				ret.add(Val.NumZero);
				ret.add(Val.NumPos);
				ret.add(Val.NumNeg);
			}
			return new AbstractVal(ret);
		};
		public static AbstractVal abstractDiv(Val s1, Val s2) {
			HashSet<Val> ret = new HashSet<>();
			if (s1 == Val.BTrue || s2 == Val.BTrue || s1 == Val.BFalse || s2 == Val.BFalse) ret.add(Val.TypeError);
			if (s1 == Val.RuntimeError || s2 == Val.RuntimeError) ret.add(Val.RuntimeError);
			else if (s2 == Val.NumZero) ret.add(Val.RuntimeError);
			else if (s1 == Val.NumZero) ret.add(Val.NumZero);
			else if (s1 == s2) ret.add(Val.NumPos);
			else ret.add(Val.NumNeg);
			return new AbstractVal(ret);
		};

		public static AbstractVal abstractMul(Val s1, Val s2) {
			HashSet<Val> ret = new HashSet<>();
			if (s1 == Val.BTrue || s2 == Val.BTrue || s1 == Val.BFalse || s2 == Val.BFalse) ret.add(Val.TypeError);
			if (s1 == Val.RuntimeError || s2 == Val.RuntimeError) ret.add(Val.RuntimeError);
			else if (s1 == Val.NumZero || s2 == Val.NumZero) ret.add(Val.NumZero);
			else if (s1 == s2) ret.add(Val.NumPos);
			else ret.add(Val.NumNeg);
			return new AbstractVal(ret);
		};
		public static AbstractVal abstractSub(Val s1, Val s2) {
			HashSet<Val> ret = new HashSet<>();
			if (s1 == Val.BTrue || s2 == Val.BTrue || s1 == Val.BFalse || s2 == Val.BFalse) ret.add(Val.TypeError);
			if (s1 == Val.RuntimeError || s2 == Val.RuntimeError) ret.add(Val.RuntimeError);
			else if (s2 == Val.NumZero) ret.add(s1);
			else if (s1 == Val.NumZero && s2 == Val.NumPos) ret.add(Val.NumNeg);
			else if (s1 == Val.NumZero && s2 == Val.NumNeg) ret.add(Val.NumPos);
			else if (s1 != s2) ret.add(s1);
			else {
				ret.add(Val.NumZero);
				ret.add(Val.NumPos);
				ret.add(Val.NumNeg);
			}
			return new AbstractVal(ret);
		};
		public static AbstractVal ofValNum(Value v){
			HashSet<Val> ret = new HashSet<>();
			if (v instanceof NumVal) {
				double num = ((NumVal)v).v();
				if (num < 0) ret.add(Val.NumNeg);
				if (num > 0) ret.add(Val.NumPos);
				if (num == 0) ret.add(Val.NumZero);
			}
			else {
				ret.add(Val.TypeError);
			}
			return new AbstractVal(ret);
		}
		public static AbstractVal ofValBool(Value v) {
			HashSet<Val> ret = new HashSet<>();
			if (v instanceof BoolVal) {
				boolean bool = ((BoolVal) v).v();
				if (bool) ret.add(Val.BTrue);
				else ret.add(Val.BFalse);
			} else {
				ret.add(Val.TypeError);
			}
			return new AbstractVal(ret);
		}
		public static AbstractVal anyNum(){
			HashSet<Val> ret = new HashSet<>();
			ret.add(Val.NumPos);
			ret.add(Val.NumZero);
			ret.add(Val.NumNeg);
			return new AbstractVal(ret);
		}
		public static AbstractVal anyBool(){
			HashSet<Val> ret = new HashSet<>();
			ret.add(Val.BTrue);
			ret.add(Val.BFalse);
			return new AbstractVal(ret);
		}

		public enum Val {
			TypeError,
			UnsupportedFunctionError,
			UnsupportedTypeError,
			RuntimeError,
			NumPos,
			NumZero,
			NumNeg,
			BTrue,
			BFalse;
			static HashSet<Val> ofNum(double num){
				HashSet<Val> ret = new HashSet<>();
				if (num < 0) ret.add(NumNeg);
				if (num > 0) ret.add(NumPos);
				if (num == 0) ret.add(NumZero);
				return ret;
			}
			public static HashSet<Val> ofBool(boolean bool){
				HashSet<Val> ret = new HashSet<>();
				if (bool) ret.add(BTrue);
				else ret.add(BFalse);
				return ret;
			}
			public HashSet<Val> typeError(){
				HashSet<Val> ret = new HashSet<>();
				ret.add(TypeError);
				return ret;
			}
			public HashSet<Val> unsupportedFunctionError(){
				HashSet<Val> ret = new HashSet<>();
				ret.add(UnsupportedFunctionError);
				return ret;
			}
			public HashSet<Val> unsupportedTypeError(){
				HashSet<Val> ret = new HashSet<>();
				ret.add(TypeError);
				return ret;
			}
			public HashSet<Val> runtimeError(){
				HashSet<Val> ret = new HashSet<>();
				ret.add(RuntimeError);
				return ret;
			}
		}
	}

	static class FunVal implements Value { //New in the funclang
		private Env _env;
		private List<String> _formals;
		private Exp _body;
		public FunVal(Env env, List<String> formals, Exp body) {
			_env = env;
			_formals = formals;
			_body = body;
		}
		public Env env() { return _env; }
		public List<String> formals() { return _formals; }
		public Exp body() { return _body; }
	    public String tostring() {
			String result = "(lambda ( ";
			for(String formal : _formals)
				result += formal + " ";
			result += ") ";
			result += _body.accept(new Printer.Formatter(), _env);
			return result + ")";
	    }

		public void print() {
			System.out.println(this.tostring());
		}
	}

	static class NumVal implements Value {
	    private double _val;

	    public NumVal(double v) {
			_val = v;
		}

	    public double v() { return _val; }

	    public String tostring() {
	    	int tmp = (int) _val;
	    	if(tmp == _val) return "" + tmp;
	    	return "" + _val;
	    }

		public void print() {
			System.out.println(_val);
		}
	}
	static class BoolVal implements Value {
		private boolean _val;
	    public BoolVal(boolean v) { _val = v; }
	    public boolean v() { return _val; }
	    public String tostring() { if(_val) return "#t"; return "#f"; }

		public void print() {
			System.out.println(this.tostring());
		}
	}

	static class StringVal implements Value {
		private java.lang.String _val;
	    public StringVal(String v) { _val = v; }
	    public String v() { return _val; }
	    public java.lang.String tostring() { return "" + _val; }

		public void print() {
			System.out.println(this.tostring());
		}
	}
	static class PairVal implements Value {
		protected Value _fst;
		protected Value _snd;
	    public PairVal(Value fst, Value snd) { _fst = fst; _snd = snd; }
		public Value fst() { return _fst; }
		public Value snd() { return _snd; }
	    public java.lang.String tostring() {
	    	if(isList()) return listToString();
	    	return "(" + _fst.tostring() + " " + _snd.tostring() + ")";
	    }
	    public boolean isList() {
	    	if(_snd instanceof Value.Null) return true;
	    	if(_snd instanceof Value.PairVal &&
	    		((Value.PairVal) _snd).isList()) return true;
	    	return false;
	    }

	    public int size() {
	    	if (!isList()) return 2;
	    	int result = 0;
	    	if (!(_fst instanceof Value.Null)) {
	    		result += 1;
			}

			Value next = _snd;
			while(!(next instanceof Value.Null)) {
				result += 1;
				next = ((PairVal) next)._snd;
			}

			return result;
		}

	    private java.lang.String listToString() {
	    	String result = "(";
	    	result += _fst.tostring();
	    	Value next = _snd;
	    	while(!(next instanceof Value.Null)) {
	    		result += " " + ((PairVal) next)._fst.tostring();
	    		next = ((PairVal) next)._snd;
	    	}
	    	return result + ")";
	    }

		public void print() {
			System.out.println(this.tostring());
		}
	}
	static class Null implements Value {
		public Null() {}
	    public String tostring() { return "()"; }

		public void print() {
			System.out.println(this.tostring());
		}
	}
	static class UnitVal implements Value {
		public static final UnitVal v = new UnitVal();
	    public String tostring() { return ""; }

		public void print() {
			System.out.println(this.tostring());
		}
	}
	static class DynamicError implements Value {
		private String message = "Unknown dynamic error.";
		public DynamicError(String message) { this.message = message; }
	    public String tostring() { return "" + message; }

		public void print() {
			System.out.println(this.tostring());
		}
	}
}
