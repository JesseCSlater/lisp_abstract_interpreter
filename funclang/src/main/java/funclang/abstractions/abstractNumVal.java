package funclang.abstractions;

import funclang.Value;

import java.lang.Exception;

public class abstractNumVal implements Value{

    Val type;

    //actual value to print for each of the abstract domains
    @Override
    public String tostring() {
        switch (this.type){
            case NumNeg:
                return "Negative";
            case NumPos:
                return "Positive";
            case NumZero:
                return "Zero";
            default:
                return "Unknown";
        }
    }

    @Override
    public void print() {
        System.out.println(this.tostring());
    }

    //define the value of each part of the abstract domain
    public abstractNumVal(double num)
    {
        if(num < 0) type = Val.NumNeg;
        else if(num == 0) type = Val.NumZero;
        else if(num > 0) type = Val.NumPos;
        else type = Val.TypeError;
    }

    //defined without number gives unknown in this case
    public abstractNumVal(){
        type = Val.NumUnknown;
    }

    //create abstract number given value already defined
    public abstractNumVal(Val t){
        type = t;
    }

    //define given a string (used for reading from file)
    public abstractNumVal(String s){
        try {
            double v = Double.parseDouble(s);
            type = new abstractNumVal(v).type;
        }

        catch(Exception e) {
            try {
                type = Val.valueOf(s);
            }
            catch (Exception i)
            {
                throw new RuntimeException("not of right type for abstractNumVal");
            }
        }
    }

    //This is how the abstract domain is broken up
    public enum Val {
        TypeError,
        NumPos,
        NumZero,
        NumNeg,
        NumUnknown;
    }

    //Logic Table for add function
    public static abstractNumVal abstractAdd(abstractNumVal first, abstractNumVal second){
        abstractNumVal returnVal;
        if(first.type == Val.NumPos)
        {
            switch (second.type) {
                case NumPos:
                case NumZero:
                    returnVal = new abstractNumVal(Val.NumPos);
                    break;

                case NumNeg:
                    returnVal = new abstractNumVal(Val.NumUnknown);
                    break;

                default:
                    returnVal = new abstractNumVal(Val.NumUnknown);
                    break;
            }
        }
        else if(first.type == Val.NumNeg)
        {
            switch (second.type) {
                case NumNeg:
                case NumZero:
                    returnVal = new abstractNumVal(Val.NumNeg);
                    break;

                case NumPos:
                    returnVal = new abstractNumVal(Val.NumUnknown);
                    break;

                default:
                    returnVal = new abstractNumVal(Val.NumUnknown);
                    break;
            }
        }
        else if(first.type == Val.NumZero)
        {
            switch (second.type) {
                case NumZero:
                    returnVal = new abstractNumVal(Val.NumZero);
                    break;

                case NumPos:
                case NumNeg:
                    returnVal = new abstractNumVal(second.type);
                    break;

                default:
                    returnVal = new abstractNumVal(Val.NumUnknown);
                    break;
            }
        }
        else{
            returnVal = new abstractNumVal();
        }

        return returnVal;
    }

    //TODO add similar functions for subtract, multiply, and divide
}

