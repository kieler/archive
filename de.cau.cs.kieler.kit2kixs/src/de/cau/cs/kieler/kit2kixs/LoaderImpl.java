package de.cau.cs.kieler.kit2kixs;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PushbackReader;

import de.cau.cs.kieler.synccharts.State;

import kiel.util.kit.lexer.Lexer;
import kiel.util.kit.lexer.LexerException;
import kiel.util.kit.node.Start;
import kiel.util.kit.parser.Parser;
import kiel.util.kit.parser.ParserException;

public class LoaderImpl {

	public static State load(String source) {
		try {
			Lexer lexer = new Lexer(new PushbackReader(new FileReader(source)));
			Parser parser = new Parser(lexer);
			Start ast = parser.parse();
			KitInterfacer ki = new KitInterfacer();
			ast.apply(ki);
			return ki.root;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (ParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (LexerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
