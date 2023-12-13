package funclang;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import funclang.AST.Program;
import funclang.parser.FuncLangLexer;
import funclang.parser.FuncLangParser;

/**
 * The Reader class reads input programs, parses them, and extracts abstract information.
 */
public class Reader {

	/**
	 * Reads input from the user, processes it, and returns a Ret object containing
	 * the parsed program and abstract information.
	 *
	 * @return Ret object containing parsed program and abstract information.
	 * @throws IOException if an I/O error occurs.
	 */
	Ret read() throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		System.out.print("$ ");
		String programText = br.readLine();
		ArrayList<String> abstractEnv = new ArrayList<>();
		ArrayList<Value> abstractVal = new ArrayList<>();
        if(programText.startsWith("run ")) {
			String fileName = "funclang/src/main/java/funclang/examples/" + programText.substring(4);
			try (BufferedReader brf = new BufferedReader(
					new FileReader(fileName))) {
				StringBuilder sb = new StringBuilder();
				String line = brf.readLine();
				ArrayList<String> x = new ArrayList<>();
				HashSet <Value.AbstractVal.Val> vals = new HashSet<>();

				// Parses scm abstractly and defines specified variables.
				if (line.startsWith("(abstract ")) {
                    ArrayList<String> variables = new ArrayList<>(Arrays.asList(line.substring(10).split(" ")));
					for(String s : variables) {
						if (s.contains("(")) {
							vals = new HashSet<>();
							if (s.contains(")")) {
								abstractVal.add(Value.AbstractVal.anyNum());
								abstractEnv.add(s.substring(1, s.indexOf(")")));
							}
							else {
								abstractEnv.add(s.substring(1));
							}
						} else if (s.contains(")")) {
							vals.add((selectEnum(s.substring(0, s.indexOf(")")))));
							abstractVal.add(new Value.AbstractVal(vals));
						}
						else{
							vals.add(selectEnum(s));
						}
					}
					line = brf.readLine();
				}

				while (line != null) {
					sb.append(line);
					sb.append(System.lineSeparator());
					line = brf.readLine();
				}
				programText = sb.toString();
			}
		}
		return new Ret(parse(programText), abstractEnv, abstractVal);
	}

	/**
	 * Selects the corresponding enum value based on the provided string.
	 *
	 * @param s The input string to map to an enum value.
	 * @return The corresponding enum value, or UnsupportedTypeError if not found.
	 */
	private Value.AbstractVal.Val selectEnum(String s)
	{
		for(Value.AbstractVal.Val v : Value.AbstractVal.Val.values())
		{
			if(s.equals(v.toString()))
			{
				return v;
			}
		}
		return Value.AbstractVal.Val.UnsupportedTypeError; // Returns if the string isn't found in the enum
	}



	Program parse(String programText) {
		FuncLangLexer l = new FuncLangLexer(new org.antlr.v4.runtime.ANTLRInputStream(programText));
		FuncLangParser p = new FuncLangParser(new org.antlr.v4.runtime.CommonTokenStream(l));
		Program program = p.program().ast;
		return program;
	}

	static String readFile(String fileName) throws IOException {
		try (BufferedReader br = new BufferedReader(
				new FileReader(fileName))) {
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();

			while (line != null) {
				sb.append(line);
				sb.append(System.lineSeparator());
				line = br.readLine();
			}
			return sb.toString();
		}
	}

	/**
	 * The Ret class holds the parsed program and abstract information.
	 */
	public static class Ret {
		public Program p;
		public ArrayList<String> abstractEnv;
		public ArrayList<Value> abstractVal;

		/**
		 * Constructs a Ret object with the given parameters.
		 *
		 * @param p	          The parsed Program object.
		 * @param abstractEnv The list of abstract environment variables.
		 * @param abstractVal The list of abstract values.
		 */
		public Ret(Program p, ArrayList<String> abstractEnv, ArrayList<Value> abstractVal) {
			this.p = p;
			this.abstractEnv = abstractEnv;
			this.abstractVal = abstractVal;
		}
	}
}
