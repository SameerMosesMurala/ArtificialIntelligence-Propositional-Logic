import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Vector;

public class CheckTrueFalse {
public static void main(String[] args) {
		
		if( args.length != 3){
			//takes three arguments
			System.out.println("Usage: " + args[0] +  " [wumpus-rules-file] [additional-knowledge-file] [input_file]\n");
			exit_function(0);
		}
		
		//create some buffered IO streams
		String buffer;
		BufferedReader inputStream;
		BufferedWriter outputStream;
		
		//create the knowledge base and the statement
		LogicalExpression knowledge_base = new LogicalExpression();
		LogicalExpression statement = new LogicalExpression();

		//open the wumpus_rules.txt
		try {
			inputStream = new BufferedReader( new FileReader( args[0] ) );
			
			//load the wumpus rules
			System.out.println("loading the wumpus rules...");
			knowledge_base.setConnective("and");
		
			while(  ( buffer = inputStream.readLine() ) != null ) 
                        {
				if( !(buffer.startsWith("#") || (buffer.equals( "" )) )) 
                                {
					//the line is not a comment
					LogicalExpression subExpression = readExpression( buffer );
					knowledge_base.setSubexpression( subExpression );
				} 
                                else 
                                {
					//the line is a comment. do nothing and read the next line
				}
			}		
			
			//close the input file
			inputStream.close();

		} catch(Exception e) 
                {
			System.out.println("failed to open " + args[0] );
			e.printStackTrace();
			exit_function(0);
		}
		//end reading wumpus rules
		
		
		//read the additional knowledge file
		try {
			inputStream = new BufferedReader( new FileReader( args[1] ) );
			
			//load the additional knowledge
			System.out.println("loading the additional knowledge...");
			
			// the connective for knowledge_base is already set.  no need to set it again.
			// i might want the LogicalExpression.setConnective() method to check for that
			//knowledge_base.setConnective("and");
			
			while(  ( buffer = inputStream.readLine() ) != null) 
                        {
                                if( !(buffer.startsWith("#") || (buffer.equals("") ))) 
                                {
					LogicalExpression subExpression = readExpression( buffer );
					knowledge_base.setSubexpression( subExpression );
                                } 
                                else 
                                {
				//the line is a comment. do nothing and read the next line
                                }
                          }
			
			//close the input file
			inputStream.close();

		} catch(Exception e) {
			System.out.println("failed to open " + args[1] );
			e.printStackTrace();
			exit_function(0);
		}
		//end reading additional knowledge
		
		
		// check for a valid knowledge_base
		if( !valid_expression( knowledge_base ) ) {
			System.out.println("invalid knowledge base");
			exit_function(0);
		}
		
		// print the knowledge_base
		knowledge_base.print_expression("\n");
		
		
		// read the statement file
		try {
			inputStream = new BufferedReader( new FileReader( args[2] ) );
			
			System.out.println("\n\nLoading the statement file...");
			//buffer = inputStream.readLine();
			
			// actually read the statement file
			// assuming that the statement file is only one line long
			while( ( buffer = inputStream.readLine() ) != null ) {
				if( !buffer.startsWith("#") ) {
					    //the line is not a comment
						statement = readExpression( buffer );
                                                break;
				} else {
					//the line is a commend. no nothing and read the next line
				}
			}
			
			//close the input file
			inputStream.close();

		} catch(Exception e) {
			System.out.println("failed to open " + args[2] );
			e.printStackTrace();
			exit_function(0);
		}
		// end reading the statement file
		
		// check for a valid statement
		if( !valid_expression( statement ) ) {
			System.out.println("invalid statement");
			exit_function(0);
		}
		
		//print the statement
		statement.print_expression( "" );
		//print a new line
		System.out.println("\n");
						
		//testing
		//System.out.println("I don't know if the statement is definitely true or definitely false.");

		//Entailment entailmentChecker = new Entailment();
		//create a negation of the KB
		LogicalExpression negation_knowledge_base = new LogicalExpression();
		negation_knowledge_base.setSubexpression(knowledge_base);
		negation_knowledge_base.setConnective("not");
		//create a negation of the alpha or statement
		LogicalExpression negation_statement = new LogicalExpression();
		negation_statement.setSubexpression(statement);
		negation_statement.setConnective("not");
		try {
			//create a output file to store the result
			BufferedWriter writer = new BufferedWriter(new FileWriter("result.txt"));
			//check if KB entails alpha
			boolean KB_Entails_Statement = TTEntails(knowledge_base, statement);
			//check if KB entails negation of alpha
			boolean KB_Entails_Not_Statement = TTEntails(knowledge_base, negation_statement);
			String output_value = "";
			//Assign the value of output based on the result 
			//whether KB entails alpha and KB entails negation of alpha
			if(KB_Entails_Statement && !KB_Entails_Not_Statement) {
				output_value = "definitely true";
			} else if(KB_Entails_Not_Statement && !KB_Entails_Statement) {
				output_value = "definitely false";
			} else if (!KB_Entails_Statement && !KB_Entails_Not_Statement) {
				output_value = "possibly true, possibly false";
			} else if(KB_Entails_Statement && KB_Entails_Not_Statement) {
				output_value = "both true and false";
			}
			
			writer.write(output_value);
			writer.newLine();
			writer.close();
			System.out.println(output_value);
		} catch(Exception e) {
			System.err.println(e.getMessage());
		}
	} //end of main

	/* this method reads logical expressionolo;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;ppp;;;[s
	 * if the next string is a:
	 * - '(' => then the next 'symbol' is a subexpression
	 * - else => it must be a unique_symbol
	 * 
	 * it returns a logical expression
	 * 
	 * notes: i'm not sure that I need the counter
	 * 
	 */
	public static LogicalExpression readExpression( String input_string ) 
        {
          LogicalExpression result = new LogicalExpression();
          
          //testing
          //System.out.println("readExpression() beginning -"+ input_string +"-");
          //testing
          //System.out.println("\nread_exp");
          
          //trim the whitespace off
          input_string = input_string.trim();
          
          if( input_string.startsWith("(") ) 
          {
            //its a subexpression
          
            String symbolString = "";
            
            // remove the '(' from the input string
            symbolString = input_string.substring( 1 );
            //symbolString.trim();
            
            //testing
            //System.out.println("readExpression() without opening paren -"+ symbolString + "-");
				  
            if( !symbolString.endsWith(")" ) ) 
            {
              // missing the closing paren - invalid expression
              System.out.println("missing ')' !!! - invalid expression! - readExpression():-" + symbolString );
              exit_function(0);
              
            }
            else 
            {
              //remove the last ')'
              //it should be at the end
              symbolString = symbolString.substring( 0 , ( symbolString.length() - 1 ) );
              symbolString.trim();
              
              //testing
              //System.out.println("readExpression() without closing paren -"+ symbolString + "-");
              
              // read the connective into the result LogicalExpression object					  
              symbolString = result.setConnective( symbolString );
              
              //testing
              //System.out.println("added connective:-" + result.getConnective() + "-: here is the string that is left -" + symbolString + "-:");
              //System.out.println("added connective:->" + result.getConnective() + "<-");
            }
            
            //read the subexpressions into a vector and call setSubExpressions( Vector );
            result.setSubexpressions( read_subexpressions( symbolString ) );
            
          } 
          else 
          {   	
            // the next symbol must be a unique symbol
            // if the unique symbol is not valid, the setUniqueSymbol will tell us.
            result.setUniqueSymbol( input_string );
          
            //testing
            //System.out.println(" added:-" + input_string + "-:as a unique symbol: readExpression()" );
          }
          
          return result;
        }

	/* this method reads in all of the unique symbols of a subexpression
	 * the only place it is called is by read_expression(String, long)(( the only read_expression that actually does something ));
	 * 
	 * each string is EITHER:
	 * - a unique Symbol
	 * - a subexpression
	 * - Delineated by spaces, and paren pairs
	 * 
	 * it returns a vector of logicalExpressions
	 * 
	 * 
	 */
	
	public static Vector<LogicalExpression> read_subexpressions( String input_string ) {

	Vector<LogicalExpression> symbolList = new Vector<LogicalExpression>();
	LogicalExpression newExpression;// = new LogicalExpression();
	String newSymbol = new String();
	
	//testing
	//System.out.println("reading subexpressions! beginning-" + input_string +"-:");
	//System.out.println("\nread_sub");

	input_string.trim();

	while( input_string.length() > 0 ) {
		
		newExpression = new LogicalExpression();
		
		//testing
		//System.out.println("read subexpression() entered while with input_string.length ->" + input_string.length() +"<-");

		if( input_string.startsWith( "(" ) ) {
			//its a subexpression.
			// have readExpression parse it into a LogicalExpression object

			//testing
			//System.out.println("read_subexpression() entered if with: ->" + input_string + "<-");
			
			// find the matching ')'
			int parenCounter = 1;
			int matchingIndex = 1;
			while( ( parenCounter > 0 ) && ( matchingIndex < input_string.length() ) ) {
					if( input_string.charAt( matchingIndex ) == '(') {
						parenCounter++;
					} else if( input_string.charAt( matchingIndex ) == ')') {
						parenCounter--;
					}
				matchingIndex++;
			}
			
			// read untill the matching ')' into a new string
			newSymbol = input_string.substring( 0, matchingIndex );
			
			//testing
			//System.out.println( "-----read_subExpression() - calling readExpression with: ->" + newSymbol + "<- matchingIndex is ->" + matchingIndex );

			// pass that string to readExpression,
			newExpression = readExpression( newSymbol );

			// add the LogicalExpression that it returns to the vector symbolList
			symbolList.add( newExpression );

			// trim the logicalExpression from the input_string for further processing
			input_string = input_string.substring( newSymbol.length(), input_string.length() );

		} else {
			//its a unique symbol ( if its not, setUniqueSymbol() will tell us )

			// I only want the first symbol, so, create a LogicalExpression object and
			// add the object to the vector
			
			if( input_string.contains( " " ) ) {
				//remove the first string from the string
				newSymbol = input_string.substring( 0, input_string.indexOf( " " ) );
				input_string = input_string.substring( (newSymbol.length() + 1), input_string.length() );
				
				//testing
				//System.out.println( "read_subExpression: i just read ->" + newSymbol + "<- and i have left ->" + input_string +"<-" );
			} else {
				newSymbol = input_string;
				input_string = "";
			}
			
			//testing
			//System.out.println( "readSubExpressions() - trying to add -" + newSymbol + "- as a unique symbol with ->" + input_string + "<- left" );
			
			newExpression.setUniqueSymbol( newSymbol );
			
	    	//testing
	    	//System.out.println("readSubexpression(): added:-" + newSymbol + "-:as a unique symbol. adding it to the vector" );

			symbolList.add( newExpression );
			
			//testing
			//System.out.println("read_subexpression() - after adding: ->" + newSymbol + "<- i have left ->"+ input_string + "<-");
			
		}
		
		//testing
		//System.out.println("read_subExpression() - left to parse ->" + input_string + "<-beforeTrim end of while");
		
		input_string.trim();
		
		if( input_string.startsWith( " " )) {
			//remove the leading whitespace
			input_string = input_string.substring(1);
		}
		
		//testing
		//System.out.println("read_subExpression() - left to parse ->" + input_string + "<-afterTrim with string length-" + input_string.length() + "<- end of while");
	}
	return symbolList;
}


	/* this method checks to see if a logical expression is valid or not 
	 * a valid expression either:
	 * ( this is an XOR )
	 * - is a unique_symbol
	 * - has:
	 *  -- a connective
	 *  -- a vector of logical expressions
	 *  
	 * */
	public static boolean valid_expression(LogicalExpression expression)
	{
		
		// checks for an empty symbol
		// if symbol is not empty, check the symbol and
		// return the truthiness of the validity of that symbol

		if ( !(expression.getUniqueSymbol() == null) && ( expression.getConnective() == null ) ) {
			// we have a unique symbol, check to see if its valid
			return valid_symbol( expression.getUniqueSymbol() );

			//testing
			//System.out.println("valid_expression method: symbol is not empty!\n");
			}

		// symbol is empty, so
		// check to make sure the connective is valid
	  
		// check for 'if / iff'
		if ( ( expression.getConnective().equalsIgnoreCase("if") )  ||
		      ( expression.getConnective().equalsIgnoreCase("iff") ) ) {
			
			// the connective is either 'if' or 'iff' - so check the number of connectives
			if (expression.getSubexpressions().size() != 2) {
				System.out.println("error: connective \"" + expression.getConnective() +
						"\" with " + expression.getSubexpressions().size() + " arguments\n" );
				return false;
				}
			}
		// end 'if / iff' check
	  
		// check for 'not'
		else   if ( expression.getConnective().equalsIgnoreCase("not") ) {
			// the connective is NOT - there can be only one symbol / subexpression
			if ( expression.getSubexpressions().size() != 1)
			{
				System.out.println("error: connective \""+ expression.getConnective() + "\" with "+ expression.getSubexpressions().size() +" arguments\n" ); 
				return false;
				}
			}
		// end check for 'not'
		
		// check for 'and / or / xor'
		else if ( ( !expression.getConnective().equalsIgnoreCase("and") )  &&
				( !expression.getConnective().equalsIgnoreCase( "or" ) )  &&
				( !expression.getConnective().equalsIgnoreCase("xor" ) ) ) {
			System.out.println("error: unknown connective " + expression.getConnective() + "\n" );
			return false;
			}
		// end check for 'and / or / not'
		// end connective check

	  
		// checks for validity of the logical_expression 'symbols' that go with the connective
		for( Enumeration e = expression.getSubexpressions().elements(); e.hasMoreElements(); ) {
			LogicalExpression testExpression = (LogicalExpression)e.nextElement();
			
			// for each subExpression in expression,
			//check to see if the subexpression is valid
			if( !valid_expression( testExpression ) ) {
				return false;
			}
		}

		//testing
		//System.out.println("The expression is valid");
		
		// if the method made it here, the expression must be valid
		return true;
	}
	



	/** this function checks to see if a unique symbol is valid */
	//////////////////// this function should be done and complete
	// originally returned a data type of long.
	// I think this needs to return true /false
	//public long valid_symbol( String symbol ) {
	public static boolean valid_symbol( String symbol ) {
		if (  symbol == null || ( symbol.length() == 0 )) {
			
			//testing
			//System.out.println("String: " + symbol + " is invalid! Symbol is either Null or the length is zero!\n");
			
			return false;
		}

		for ( int counter = 0; counter < symbol.length(); counter++ ) {
			if ( (symbol.charAt( counter ) != '_') &&
					( !Character.isLetterOrDigit( symbol.charAt( counter ) ) ) ) {
				
				System.out.println("String: " + symbol + " is invalid! Offending character:---" + symbol.charAt( counter ) + "---\n");
				
				return false;
			}
		}
		
		// the characters of the symbol string are either a letter or a digit or an underscore,
		//return true
		return true;
	}

        private static void exit_function(int value) {
                System.out.println("exiting from checkTrueFalse");
                  System.exit(value);
                }	
        //method to get the first symbol from the symbol list
       	private static String first(ArrayList<String> symbols)
    	{
    		return symbols.get(0);
    	}
      //method to get the rest of the symbols from the symbol list
    	//private static ArrayList<String> rest(ArrayList<String> symbols)
    	//{
    		//ArrayList<String> rest_symbols=symbols;
    		//rest_symbols.remove(0);
    		//return rest_symbols;
    	//}
    	
    	//method to get to return a model by assigning the the value to the symbol
    	private static HashMap<String, Boolean> extend(String symbol, boolean value, HashMap<String, Boolean> model) 
    	{
    		//assigning the value to the symbol either true or false
    		model.put(symbol, value);
    		return model;
    	}
    	//ttentails method to check whether the KB entails the statement alpha or not
        public static boolean TTEntails(LogicalExpression KB, LogicalExpression alpha) throws Exception {
        	//concatenating all the symbols in the KB and alpha
    		ArrayList<String> symbols = connect(getSymbols(KB), getSymbols(alpha));
    		HashMap<String, Boolean> model = new HashMap<String, Boolean>();
    		return TTCheckAll(KB, alpha, symbols, model);
    	}
    	//extract all symbols in the input expression 
    	private static ArrayList<String> getSymbols(LogicalExpression expression) {
    		ArrayList<String>  symbolList= new ArrayList<String>();
    		if(expression.getUniqueSymbol() != null) {
    			symbolList.add(expression.getUniqueSymbol());
    		} else {
    			int subexpnoiterator=0;
    			int subexpnumber=expression.getSubexpressions().size();
    			//iterate through all the subexpressions in the logical expression
    			//get all the unique symbols and add it to the symbol list
    			for(subexpnoiterator=0;subexpnoiterator<subexpnumber;subexpnoiterator++) {
    				LogicalExpression subexpression=(LogicalExpression) expression.getSubexpressions().get(subexpnoiterator);
    				symbolList = connect(symbolList, getSymbols(subexpression));
    			}
    		}
    		return symbolList;
    	}
    	//method to check whether a symbol is present in the KB and is true in that KB
    	private static boolean sym_True_in_KB(LogicalExpression KB, String symbol) {
    		Vector<LogicalExpression> expressions = KB.getSubexpressions();
    		int expiterator=0;
    		int expnumber=expressions.size();

    		//for(LogicalExpression subexpression : expressions) {
    		for(expiterator=0;expiterator<expnumber;expiterator++)
    		{
    		LogicalExpression subexpression=expressions.get(expiterator);
    			String symbolCheck = subexpression.getUniqueSymbol();
    			//check whether symbol is present in the KB
    			if(symbolCheck != null && symbolCheck.equals(symbol)) {
    				return true;
    			}
    		}
    		
    		return false;
    	}
    	//method to check whether a symbol is present in the KB and is false in that KB
    	//That is if negation of a symbol is present in the KB
    	private static boolean sym_false_in_KB(LogicalExpression KB, String symbol) {
    		Vector<LogicalExpression> expressions = KB.getSubexpressions();
    		int expiterator=0;
    		int expnumber=expressions.size();
    		//for(LogicalExpression subexpression : expressions) {
    		for(expiterator=0;expiterator<expnumber;expiterator++)
    		{
    		LogicalExpression subexpression=expressions.get(expiterator);
    			String Connective_Check = subexpression.getConnective();
    			//Check whether the negation of the symbol is present in the KB
    			if(Connective_Check != null && Connective_Check.equals("not")) {
    				String symbolCheck = ((LogicalExpression) subexpression.getSubexpressions().get(0)).getUniqueSymbol();
    				if(symbolCheck != null && symbolCheck.equals(symbol)) {
    					return true;
    				}
    			}
    		}
    		
    		return false;
    	}
    	//concatenates symbols in both the arguments into one 
    	private static ArrayList<String> connect(ArrayList<String> symbol_to_connect_1, ArrayList<String> symbol_to_connect_2) {
    		for(String symbol : symbol_to_connect_2) {
    		//if the symbol list one does not contain a symbol in symbol list 2 
    			//add it to symbol list 1
    			if(!symbol_to_connect_1.contains(symbol)) {
    				symbol_to_connect_1.add(symbol);
    			}
    		}
    		//return the concatenated list
    		return symbol_to_connect_1;
    	}
    	//TT Check all method to check PL true for every symbol in KB and alpha the statement
    	private static boolean TTCheckAll(LogicalExpression KB, LogicalExpression alpha, ArrayList<String> symbolList, HashMap<String, Boolean> model) throws Exception {
    		if(symbolList.isEmpty()) {
    			if(PLTrue(KB, model)) {
    				return PLTrue(alpha, model);
    			} else {
    				return true;
    			}
    		} else {
    			String P = first(symbolList);
    			ArrayList<String> rest = rest(symbolList);
    			//System.out.println(rest);
    			//check whether the given symbol is already present in the KB
    			if(sym_True_in_KB(KB, P)) {
    				//generate only those models where symbol is true
    				return TTCheckAll(KB, alpha, rest, extend(P, true, model));
    				//check whether the given negation of the symbol is already present in the KB
    			} else if(sym_false_in_KB(KB, P)) {
    				//generate only those models where symbol is false
    				return TTCheckAll(KB, alpha, rest, extend(P, false, model));
    				//if it is not present generate a model for both the symbol being true and false
    			} else {
    				return TTCheckAll(KB, alpha, rest, extend(P, true, model)) && TTCheckAll(KB, alpha, rest, extend(P, false, model));
    			}
    		}
    	}
    	//checks for the truth value of the model
    	private static boolean PLTrue(LogicalExpression expression, HashMap<String, Boolean> model) throws Exception{
    		String symbol = expression.getUniqueSymbol();
    		String connective = expression.getConnective();
    		if(symbol != null) {
    			return model.get(symbol);
    			//check for and connective
    		} else if(connective.equals("and")) {
    			int subexpnoiterator=0;
    			int subexpnumber=expression.getSubexpressions().size();
    			//for all the symbols or expressions even if one returns false return false
    			for(subexpnoiterator=0;subexpnoiterator<subexpnumber;subexpnoiterator++) {
    				LogicalExpression subexpression=(LogicalExpression) expression.getSubexpressions().get(subexpnoiterator);
    				if(!PLTrue(subexpression, model)) {
    					return false;
    				}
    			}
    			
    			return true;
    		} 
    		//check for or connective
    		else if(connective.equals("or")) {
    			int subexpnoiterator=0;
    			int subexpnumber=expression.getSubexpressions().size(); 
    			for(subexpnoiterator=0;subexpnoiterator<subexpnumber;subexpnoiterator++) {
    				//for all the symbols or expressions even if one returns true return true
    				LogicalExpression subexpression=(LogicalExpression) expression.getSubexpressions().get(subexpnoiterator);
    				if(PLTrue(subexpression, model)) {
    					return true;
    				}
    			}
    			
    			return false;
    		} 
    		//check for XOR connective
    		else if(connective.equals("xor")) {
    			int nummber_Of_True = 0;
    			int subexpnoiterator=0;
    			int subexpnumber=expression.getSubexpressions().size();
    			// return true only if exactly one symbol or expression is true
    			for(subexpnoiterator=0;subexpnoiterator<subexpnumber;subexpnoiterator++ ) {
    				LogicalExpression subexpression=(LogicalExpression) expression.getSubexpressions().get(subexpnoiterator);
    				 if(PLTrue(subexpression, model)) {
    					 nummber_Of_True++;
    					if(nummber_Of_True > 1) {
    						return false;
    					}
    				}
    			}
    			return true;
    		} 
    		//check for if connective
    		else if(connective.equals("if")) {
    			LogicalExpression left_subexpression = (LogicalExpression) expression.getSubexpressions().get(0);
    			LogicalExpression right_subexpression = (LogicalExpression) expression.getSubexpressions().get(1);
    			//if left symbol is true and right is false return false else in other cases return true
    			if(PLTrue(left_subexpression, model) && !PLTrue(right_subexpression, model)) {
    				return false;
    			} else {
    				return true;
    			}
    		} 
    		//check for iff connective
    		else if(connective.equals("iff")) {
    			LogicalExpression left_subexpression = (LogicalExpression) expression.getSubexpressions().get(0);
    			LogicalExpression right_subexpression = (LogicalExpression) expression.getSubexpressions().get(1);
    			//if both left and right are equal that is both are true or both are false return true else false
    			if(PLTrue(left_subexpression, model) == PLTrue(right_subexpression, model)) {
    				return true;
    			} else {
    				return false;
    			}
    		}
    		//check for connective not 
    		else if(connective.equals("not")) {
    			//return negation of the symbol or expression
    			LogicalExpression subexpression = (LogicalExpression) expression.getSubexpressions().get(0);
    			return !(PLTrue(subexpression, model));
    		} 
    		//if the connective is not recognised raise an exception
    		else {
    			throw new Exception("Connective not recognised");
    		}
    	}
    	
    	private static ArrayList<String> rest(ArrayList<String> symbols)
    	{
    		ArrayList<String> rest_symbols = new ArrayList<String>();
    		//rest_symbols.remove(0);
    		for(int i=1;i<symbols.size();i++)
    		{
    			rest_symbols.add(symbols.get(i));
    		}
    		return rest_symbols;
    	}

}
