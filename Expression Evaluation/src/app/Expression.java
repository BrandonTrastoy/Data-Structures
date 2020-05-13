package app;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import structures.Stack;

public class Expression {

	public static String delims = " \t*+-/()[]";
			
    /**
     * Populates the vars list with simple variables, and arrays lists with arrays
     * in the expression. For every variable (simple or array), a SINGLE instance is created 
     * and stored, even if it appears more than once in the expression.
     * At this time, values for all variables and all array items are set to
     * zero - they will be loaded from a file in the loadVariableValues method.
     * 
     * @param expr The expression
     * @param vars The variables array list - already created by the caller
     * @param arrays The arrays array list - already created by the caller
     */
    public static void 
    makeVariableLists(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
    	/** COMPLETE THIS METHOD **/
    	/** DO NOT create new vars and arrays - they are already created before being sent in
    	 ** to this method - you just need to fill them in.
    	 **/
    	
    	//Removes Whites Space From String
    	String temp = expr.replaceAll(" ","");
    	//Tokenizes the String by delims
    	StringTokenizer other = new StringTokenizer(temp, delims + "1234567890");

    	int subStrLength = 0, index = 0, nextCharIndex;
    	StringBuilder toReplace = new StringBuilder(temp);
    	String holdToken;

    	while(other.hasMoreTokens()) {

    		holdToken = other.nextToken();
    		//System.out.print(holdToken + " | ");

    		index = temp.indexOf(holdToken);
    		subStrLength = holdToken.length();
    		//Prevents Null Exception
    		if(temp.length() > (index+subStrLength)) {
    			nextCharIndex = index + subStrLength;
    		}
    		else {
    			nextCharIndex = index;
    		}

    		//If next value after token is [ then add token to arrayList
    		if(temp.charAt(nextCharIndex) == '[') {

    			Array hold = new Array(holdToken);

    			if(!arrays.contains(hold)) {

    				arrays.add(new Array(holdToken));
    				//Makes it so second occurence of token does not select first
    				toReplace.setCharAt(index, ' ');
    				//StringTokenizer to String
    				temp = toReplace.toString();
    				//System.out.println(temp);
    			}
    		}
    		//else no [ is found  then add to variableList
    		else {

    			Variable hold = new Variable(holdToken);

    			if(!vars.contains(hold)) {

    				vars.add(new Variable(holdToken));
    				//Makes it so second occurence of token does not select first
    				toReplace.setCharAt(index, ' ');
    				//StringTokenizer to String
    				temp = toReplace.toString();
    			}
    		}
    	}
    	//System.out.print("Array List Size:" + arrays.size() + " | "); System.out.println(arrays);
    	//System.out.print("Variable List Size:" + vars.size()+ " | "); System.out.println(vars);
    }
    
    /**
     * Loads values for variables and arrays in the expression
     * 
     * @param sc Scanner for values input
     * @throws IOException If there is a problem with the input 
     * @param vars The variables array list, previously populated by makeVariableLists
     * @param arrays The arrays array list - previously populated by makeVariableLists
     */
    public static void 
    loadVariableValues(Scanner sc, ArrayList<Variable> vars, ArrayList<Array> arrays) 
    throws IOException {
        while (sc.hasNextLine()) {
            StringTokenizer st = new StringTokenizer(sc.nextLine().trim());
            int numTokens = st.countTokens();
            String tok = st.nextToken();
            Variable var = new Variable(tok);
            Array arr = new Array(tok);
            int vari = vars.indexOf(var);
            int arri = arrays.indexOf(arr);
            if (vari == -1 && arri == -1) {
            	continue;
            }
            int num = Integer.parseInt(st.nextToken());
            if (numTokens == 2) { // scalar symbol
                vars.get(vari).value = num;
            } else { // array symbol
            	arr = arrays.get(arri);
            	arr.values = new int[num];
                // following are (index,val) pairs
                while (st.hasMoreTokens()) {
                    tok = st.nextToken();
                    StringTokenizer stt = new StringTokenizer(tok," (,)");
                    int index = Integer.parseInt(stt.nextToken());
                    int val = Integer.parseInt(stt.nextToken());
                    arr.values[index] = val;              
                }
            }
        }
    }
    
    //Checks for matching ( )
    private static int parenthesisMatcher(String a, int n) {
    	int counter = 0;
        char openingP = '(';
        char closingP = ')';
        int pos = -1;
        for(int i = n; i<a.length(); i++){
            if(a.charAt(i) == openingP){
                counter++;
            }
            else if(a.charAt(i) == closingP){
                counter--;
            }
            if(counter == 0){
                pos = i;
                break;
            }
        }
        return pos;
    }
    //Checks for matching [ ]
    private static int bracketMatcher(String a, int n) {
    	int counter = 0;
        char openingP = '[';
        char closingP = ']';
        int pos = -1;
        for(int i = n; i<a.length(); i++){
            if(a.charAt(i) == openingP){
                counter++;
            }
            else if(a.charAt(i) == closingP){
                counter--;
            }
            if(counter == 0){
                pos = i;
                break;
            }
        }
        return pos;
    }
  	//Checks for Array
  	private static boolean isArray(String temp) {
  		boolean array = false;
      	for(int i = 0; i<(temp.length()-1);i++) {
      		if(Character.isLetter(temp.charAt(i)) && !Character.isLetter(temp.charAt(i+1))) {
      			array = true;
      			return array;
      		}
      	}
      	return array;
  	}
  	//Evaluates the expression
  	private static float evaluateP(String expression, ArrayList<Variable> vars, ArrayList<Array> arrays) {
  		String subexpr = expression;
  		Stack<Float> operand = new Stack<Float>();
  		Stack<Character> operators = new Stack<Character>();

  		if(subexpr.indexOf("(")>=0) {
  			while(subexpr.indexOf("(") >=0) {
  				int ploc = subexpr.indexOf("(");
  				int pmloc = parenthesisMatcher(subexpr,ploc);
  				subexpr = subexpr.substring(0,ploc) + Float.toString(evaluateP(subexpr.substring(ploc+1,pmloc), vars, arrays)) + subexpr.substring(pmloc+1);
  			}
  		}
  		if(subexpr.indexOf("[") >=0 ) {
  			while(subexpr.indexOf("[")>=0) {
  				int bloc = subexpr.indexOf("[");
  				int bmloc = bracketMatcher(subexpr, bloc);
  				subexpr = subexpr.substring(0,bloc) + Float.toString((float)(evaluateP(subexpr.substring(bloc+1,bmloc), vars, arrays))) + subexpr.substring(bmloc+1);

  			}
  		}
  		StringTokenizer stk = new StringTokenizer(subexpr, delims);

  		boolean isNegative = false;
  		if(subexpr.charAt(0) == '-') {
  			isNegative  = true;
  		}

  		String temp = stk.nextToken();
  		if(isArray(temp)) {
  			String temp2 = "";
  			for(int i = 0; i<temp.length(); i++) {
  				if(Character.isLetter(temp.charAt(i))) {
  					temp2 += temp.charAt(i);
  				}
  			}
  			String temp2Index = "";
  			for(int i = 0; i<temp.length(); i++) {
  				if(!Character.isLetter(temp.charAt(i))) {
  					temp2Index += temp.charAt(i);
  				}
  			}
  			int temp2realIndex = (int)Float.parseFloat(temp2Index);
  			operand.push((float)(arrays.get(arrays.indexOf(new Array(temp2))).values[temp2realIndex]));
  		}
  		else {
  			if(vars.contains(new Variable(temp))) {
  				operand.push((float)vars.get(vars.indexOf(new Variable(temp))).value);
  			}else{
  				if(isNegative) {
  					operand.push(-1*Float.parseFloat(temp));
  					subexpr = subexpr.substring(temp.length()+1);
  					if(subexpr.isEmpty()) {
  						return operand.pop();
  					}
  				}
  				else {
  					operand.push(Float.parseFloat(temp));
  				}

  			}
  		}

  		if(!isNegative) {
  			subexpr = subexpr.substring(temp.length());
  			if(subexpr.isEmpty()) {
  				return operand.pop();
  			}
  		}


  		while(stk.hasMoreTokens()) {
  			if(!operators.isEmpty()) {
  				if(operators.peek() == '*') {
  					operand.push(operand.pop()*operand.pop());
  					operators.pop();
  					operators.push(subexpr.charAt(0));
  					subexpr = subexpr.substring(1);
  				}
  				else if(operators.peek()=='/') {
  					operand.push((1/operand.pop())*operand.pop());
  					operators.pop();
  					operators.push(subexpr.charAt(0));
  					subexpr = subexpr.substring(1);
  				}
  				else {
  					operators.push(subexpr.charAt(0));
  					subexpr = subexpr.substring(1);
  				}
  			}
  			else {
  				operators.push(subexpr.charAt(0));
  				subexpr = subexpr.substring(1);
  			}

  			if(subexpr.charAt(0) == '-') {
  				isNegative = true;
  				temp = stk.nextToken();
  				operand.push(-1*Float.parseFloat(temp));
  				subexpr = subexpr.substring(temp.length()+1);
  			}
  			else {
  				temp = stk.nextToken();

  				if(isArray(temp)) {
  					String temp2 = "";
  					for(int i = 0; i<temp.length(); i++) {
  						if(Character.isLetter(temp.charAt(i))) {
  							temp2 += temp.charAt(i);
  						}
  					}
  					String temp2Index = "";
  					for(int i = 0; i<temp.length(); i++) {
  						if(!Character.isLetter(temp.charAt(i))) {
  							temp2Index += temp.charAt(i);
  						}
  					}
  					int temp2realIndex = (int)Float.parseFloat(temp2Index);
  					operand.push((float)(arrays.get(arrays.indexOf(new Array(temp2))).values[temp2realIndex]));
  				}
  				else if(vars.contains(new Variable(temp))) {//it's a scalar
  					operand.push((float)vars.get(vars.indexOf(new Variable(temp))).value);
  				}
  				else {
  					operand.push(Float.parseFloat(temp));
  				}
  				subexpr = subexpr.substring(temp.length());

  			}

  			if(subexpr.isEmpty()) {//this is for the end of the expression
  				while(operators.size() != 0) {
  					if(operators.peek()== '*') {
  						operators.pop();
  						operand.push(operand.pop() * operand.pop());
  					}
  					else if(operators.peek()== '/'){
  						operators.pop();
  						operand.push((1/operand.pop())*operand.pop());
  					}
  					else if(operators.peek()== '-') {
  						operators.pop();
  						if(!operators.isEmpty() && operators.peek() == '-') {
  							operand.push(operand.pop()+ operand.pop());
  						}
  						else {
  							operand.push(-1*(operand.pop()-operand.pop()));
  						}
  					}
  					else {
  						operators.pop();
  						if(!operators.isEmpty() && operators.peek() == '-') {
  							operand.push(-1* (operand.pop()-operand.pop()));
  						}
  						else {
  							operand.push(operand.pop()+ operand.pop());
  						}
  					}
  				}
  				return operand.pop();
  			}
  		}
  		return 0;
  	}
    
    /**
     * Evaluates the expression.
     * 
     * @param vars The variables array list, with values for all variables in the expression
     * @param arrays The arrays array list, with values for all array items
     * @return Result of evaluation
     */
    public static float 
    evaluate(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
    	
    	String temp = expr.replaceAll(" ","");
		return evaluateP(temp, vars, arrays);
    }
}
