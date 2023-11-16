package funclang;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;

import funclang.AST.Program;
import funclang.parser.FuncLangLexer;
import funclang.parser.FuncLangParser;

public class Reader {
	
	Ret read() throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		System.out.print("$ ");
		String programText = br.readLine();
		ArrayList<String> abstractEnv = new ArrayList<>();
        if(programText.startsWith("run ")) {
			String fileName = "funclang/src/main/java/funclang/examples/" + programText.substring(4);
			try (BufferedReader brf = new BufferedReader(
					new FileReader(fileName))) {
				StringBuilder sb = new StringBuilder();
				String line = brf.readLine();
				ArrayList<String> x = new ArrayList<>();
				if (line.startsWith("#abstract ")) {
                    abstractEnv = new ArrayList<>(Arrays.asList(line.substring(10).split(" ")));
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
		return new Ret(parse(programText), abstractEnv);
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

	public static class Ret {
		public Program p;
		public ArrayList<String> abstractEnv;

		public Ret(Program p, ArrayList<String> abstractEnv) {
			this.p = p;
			this.abstractEnv = abstractEnv;
		}
	}
}
