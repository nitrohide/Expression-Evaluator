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
    	expr=expr.replaceAll(" ","");
    	expr=expr.replaceAll("\t","");
    	String enddelims="+-/*()]";
    	String temp1="";
    	for (int i=0;i<expr.length();i++) {
    		if (Character.isDigit(expr.charAt(i))) {
    			continue;
    		}
    		if((delims.contains(Character.toString(expr.charAt(i)))!=true)){
    			temp1+=expr.charAt(i);
    		}
    		else if (enddelims.contains(Character.toString(expr.charAt(i)))){
    			if(temp1!="") {
    				Variable temp2=new Variable(temp1);
        			if(vars.contains(temp2)!=true) {
        				vars.add(temp2);
        			}
        			temp1="";
    			}
    		}
    		if (expr.charAt(i)=='[') {
    			if (temp1!="") {
    	    		Array temp2=new Array(temp1);
        			if (arrays.contains(temp2)!=true) {
    	    			arrays.add(temp2);
        			}
    	    		temp1="";
        		}
    		}
    	}
    	Variable temp2=new Variable(temp1);
    	if((vars.contains(temp2)!=true) && (delims.contains(temp1)!=true)) {
    		vars.add(temp2);
    	}
    	/* used to test
    	System.out.println(expr);
		System.out.println("Variables:");
    	for (int j=0;j<vars.size();j++) {
    		System.out.println(vars.get(j));
    	}
		System.out.println("Arrays:");
    	for (int k=0;k<arrays.size();k++) {
    		System.out.println(arrays.get(k));
    	}*/
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
    
     /**
      * Evaluates the expression.
      * 
      * @param vars The variables array list, with values for all variables in the expression
      * @param arrays The arrays array list, with values for all array items
      * @return Result of evaluation
      */
     public static float 
     evaluate(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
    	expr=expr.replaceAll(" ","");
        expr=expr.replaceAll("\t","");
     	Stack<Float> numbers = new Stack<>();
     	Stack<String> operations = new Stack<>();
     	StringTokenizer st = new StringTokenizer(expr, delims, true);
     	while(st.hasMoreTokens()) {
     		String token = st.nextToken();
     		if(isInt(token)){
     			numbers.push(Float.parseFloat(token));
     			continue;
     		}
     		else if(isVar(vars, token) != -1) {
     			numbers.push((float)vars.get(isVar(vars,token)).value);
     		}
     		else if(token.equals("(")) {
     			operations.push("(");
     		}
     		else if(token.equals(")")) {
     			while(operations.peek() != "(") {
     				float second = numbers.pop();
     				float first = numbers.pop();
     				String ch = operations.pop();
     				switch(ch) {
     					case "+": 
     						numbers.push(first + second);
     						continue;
     					case "-":
     						numbers.push(first - second);
     						continue;
     					case "*":
     						numbers.push(first * second);
     						continue;
     					case "/":
     						numbers.push(first / second);
     						continue;
     				}
     			}
     			operations.pop();
     			
     		}
     		else if(token.equals("[")) {
     			operations.push("[");
     		}
     		else if(isArr(arrays, token) != -1) {
     			operations.push(token);
     		}
     		else if(token.equals("]")) {
     			while(operations.peek() != "[") {
     				float second = numbers.pop();
     				float first = numbers.pop();
     				String ch = operations.pop();
     				switch(ch) {
     					case "+": 
     						numbers.push(first + second);
     						continue;
     					case "-":
     						numbers.push(first - second);
     						continue;
     					case "*":
     						numbers.push(first * second);
     						continue;
     					case "/":
     						numbers.push(first / second);
     						continue;
     				}
     			}
     			operations.pop();
     			
     			String ArrName = operations.pop();
     			float ArrIndex = numbers.pop();
     			numbers.push(getValue(arrays, ArrIndex, ArrName));
     		}
     		else if(token.equals("+") || token.equals("-") || token.equals("*") || token.equals("/")) {
     			while (operations.isEmpty() != true && delims(token, operations.peek())){
     				float second = numbers.pop();
     				float first = numbers.pop();
     				String ch = operations.pop();
     				switch(ch) {
 					case "+": 
 						numbers.push(first + second);
 						continue;
 					case "-":
 						numbers.push(first - second);
 						continue;
 					case "*":
 						numbers.push(first * second);
 						continue;
 					case "/":
 						numbers.push(first / second);
 						continue;
 				}
 			}
     			operations.push(token);
     		}
     	}
     	while(operations.isEmpty() != true) {
     		float second = numbers.pop();
 			float first = numbers.pop();
 			String ch = operations.pop();
 			switch(ch){
 			case "+": 
 				numbers.push(first + second);
 				continue;
 			case "-":
 				numbers.push(first - second);
 				continue;
 			case "*":
 				numbers.push(first * second);
 				continue;
 			case "/":
 				numbers.push(first / second);
 				continue;
 			}
     	}
     	return numbers.pop();
     }
     
      private static int isVar(ArrayList<Variable> vars, String name) {
      	for(int i = 0; i < vars.size(); i++) {
      		if(vars.get(i).name.equals(name)) {
      			return i;
      		}
      	}
      	return -1;
      }
      
      private static int isArr(ArrayList<Array> arrays, String name) {
      	for (int i = 0; i < arrays.size(); i++) {
      		if(arrays.get(i).name.equals(name)) {
      			return i;
      		}
      	}
      	return -1;
      }
      
      private static boolean isInt(String input) {
          try {
             Integer.parseInt(input);
             return true;
          }
          catch(Exception e) {
             return false;
          }
       }
      
      private static float getValue(ArrayList<Array> arrays, float index, String name) {
      	int index1=(int) index;
      	for(int i = 0; i < arrays.size(); i++) {
      		if(arrays.get(i).name.equals(name)) {
      			Array arrs = arrays.get(i);
      			return (float)arrs.values[index1];
      		}
      	}
      	return -1;
      }
      
      private static boolean delims(String current, String top) {
        	if((current.equals("*") || current.equals("/")) && (top.equals("+") || top.equals("-"))) {
        		return false;
        	}
        	if(top.equals("(")) {
        		return false;
        	}
        	if(top.equals("[")){
        		return false;
        	}
        	return true;
      }
}
