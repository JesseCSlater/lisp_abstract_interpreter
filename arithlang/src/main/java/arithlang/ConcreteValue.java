package arithlang;

import java.util.Objects;

public interface ConcreteValue {
    String toString();
    static class NumVal implements ConcreteValue {
        private final double _val;
        public NumVal(double v) { _val = v; } 
        public double v() { return _val; }
        public String toString() { 
            int tmp = (int) _val;
            if(tmp == _val) return "" + tmp;
            return "" + _val; 
        }
    }

    class DynamicError implements ConcreteValue {
        private final String _errorMsg;
        public DynamicError(String v) {
            _errorMsg = v;
        }
        public String v() {
            return _errorMsg;
        }
        public String toString() {
            return _errorMsg;
        }
    }
}
