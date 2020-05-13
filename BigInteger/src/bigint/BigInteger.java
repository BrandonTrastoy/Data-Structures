package bigint;

/**
 * This class encapsulates a BigInteger, i.e. a positive or negative integer with 
 * any number of digits, which overcomes the computer storage length limitation of 
 * an integer.
 * 
 */
public class BigInteger {

	/**
	 * True if this is a negative integer
	 */
	boolean negative;
	
	/**
	 * Number of digits in this integer
	 */
	int numDigits;
	
	/**
	 * Reference to the first node of this integer's linked list representation
	 * NOTE: The linked list stores the Least Significant Digit in the FIRST node.
	 * For instance, the integer 235 would be stored as:
	 *    5 --> 3  --> 2
	 *    
	 * Insignificant digits are not stored. So the integer 00235 will be stored as:
	 *    5 --> 3 --> 2  (No zeros after the last 2)        
	 */
	DigitNode front;
	
	/**
	 * Initializes this integer to a positive number with zero digits, in other
	 * words this is the 0 (zero) valued integer.
	 */
	public BigInteger() {
		negative = false;
		numDigits = 0;
		front = null;
		
	}
	
	/**
	 * Parses an input integer string into a corresponding BigInteger instance.
	 * A correctly formatted integer would have an optional sign as the first 
	 * character (no sign means positive), and at least one digit character
	 * (including zero). 
	 * Examples of correct format, with corresponding values
	 *      Format     Value
	 *       +0            0
	 *       -0            0
	 *       +123        123
	 *       1023       1023
	 *       0012         12  
	 *       0             0
	 *       -123       -123
	 *       -001         -1
	 *       +000          0
	 *       
	 * Leading and trailing spaces are ignored. So "  +123  " will still parse 
	 * correctly, as +123, after ignoring leading and trailing spaces in the input
	 * string.
	 * 
	 * Spaces between digits are not ignored. So "12  345" will not parse as
	 * an integer - the input is incorrectly formatted.
	 * 
	 * An integer with value 0 will correspond to a null (empty) list - see the BigInteger
	 * constructor
	 * 
	 * @param integer Integer string that is to be parsed
	 * @return BigInteger instance that stores the input integer.
	 * @throws IllegalArgumentException If input is incorrectly formatted
	 */
	public static BigInteger parse(String integer) 
	throws IllegalArgumentException {
		
		// following line is a placeholder - compiler needs a return
		// modify it according to need
		
		int i = 0, j = 1, stringLength = integer.length();
		boolean negative = false;
		
		while(i < stringLength) {
			
			if(stringLength > 1) {
				
				if(integer.charAt(i) == '0' && j < stringLength) {
					
					char ch = integer.charAt(i);
					char ch2 = integer.charAt(j);
					
					int crnt = ch;
					int next = ch2;
					
					if((crnt-48) == 0 && (next-48) >= 0) {
						
						String before = integer.substring(0, i);
						String after = integer.substring(j);
						
						integer = before + after;
						stringLength--;
					}
				}
			
				else if(integer.charAt(i) == '-' || integer.charAt(i) == '+') {
					
					if(integer.charAt(i) == '-') {
						negative = true; 
					}
					String before = integer.substring(0, i);
					String after = integer.substring(j);
					
					integer = before + after;
					stringLength--;
				}
				
				else {
					break;
				}
			}
			else {
				break;
			}
		}
		
		if(stringLength == 1 && (integer.charAt(0) == '-' || integer.charAt(0) == '+')) {
			
			integer = "0";
			negative = false;
		}
		else if(stringLength == 1 && integer.charAt(0) == '0') {
			
			integer = "0";
			negative = false;
		}
		
		char digChar = integer.charAt(0);
		int digit = digChar;
		DigitNode value = new DigitNode((digit-48), null);
		
		for(int k = 1; k < stringLength; k++) {
			
			char digChar2 = integer.charAt(k);
			int digit2 = digChar2;
			digit2 = (digit2 - 48);
			
			DigitNode temp = new DigitNode(digit2, value);
			value = temp;
		}
		
		BigInteger numList = new BigInteger();
		numList.negative = negative;
		numList.numDigits = stringLength;
		numList.front = value;
		
		return numList; 
	}
	
	/**
	 * Adds the first and second big integers, and returns the result in a NEW BigInteger object. 
	 * DOES NOT MODIFY the input big integers.
	 * 
	 * NOTE that either or both of the input big integers could be negative.
	 * (Which means this method can effectively subtract as well.)
	 * 
	 * @param first First big integer
	 * @param second Second big integer
	 * @return Result big integer
	 */
	public static BigInteger add(BigInteger first, BigInteger second) {

		BigInteger temp1 = new BigInteger();
		BigInteger temp2 = new BigInteger();
		BigInteger result = new BigInteger();

		DigitNode sumList = null, ptr = null, hold = null;

		int sum = 0, carry = 0, difference = 0, borrow = 0;
		
		if(first.numDigits >= second.numDigits) {
			temp1 = first;
			temp2 = second; 
		}
		else {
			temp2 = first;
			temp1 = second; 
		}

		//if first number is bigger than second number and signs are the same 
		if (temp1.numDigits >= temp2.numDigits  && (
				(temp1.negative==false && temp2.negative==false)||
				(temp1.negative==true && temp2.negative==true))) {

			// Add temp1+temp2
			while (temp2.front != null) {

				sum = (temp1.front.digit + temp2.front.digit + carry) % 10;
				carry = (temp1.front.digit + temp2.front.digit + carry) / 10;

				DigitNode f = new DigitNode(sum, null);

				if (ptr == null) {
					ptr = f;
					hold = ptr;
				}

				else if (ptr != null) {
					hold.next = f;
					hold = hold.next;
				}

				temp1.front = temp1.front.next;
				temp2.front = temp2.front.next;
			}

			if (temp2.front == null) {

				while (temp1.front != null) {

					sum = (temp1.front.digit + carry) % 10;
					carry = (temp1.front.digit + carry) / 10;

					DigitNode f = new DigitNode(sum, null);

					hold.next = f;
					hold = hold.next;

					temp1.front = temp1.front.next;
				}

				if (temp1.front == null && carry != 0) {

					DigitNode f = new DigitNode(carry, null);
					hold.next = f;
					carry = 0;
				}
				sumList = ptr;
				result.front = sumList;
				
				if(temp1.negative==false) {
					result.negative = false;
				}
				else {
					result.negative = true;
				}
			}
		}
		
//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//++++++++++++++++++++++++++++++++++++Difference+++++++++++++++++++++++++++++++++++++++++++++++++++++++
//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		
		else {
			
			//This is going to be subtraction for temp1-temp2
			if(temp1.numDigits >= temp2.numDigits) {
				
				while(temp2.front!=null) {
					
					difference = temp1.front.digit - temp2.front.digit;
					
					if(difference < 0) {
						
						difference += 10 - borrow;
						borrow = 1;
					}
					else if(difference == 0) {
						difference = 0;
						
					}
					else {
						
						difference-=borrow;
						borrow=0;
					}
					
					DigitNode f = new DigitNode(difference, null);

					if (ptr == null) {
						ptr = f;
						hold = ptr;
					}

					else if (ptr != null) {
						hold.next = f;
						hold = hold.next;
					}
					
					temp1.front = temp1.front.next;
					temp2.front = temp2.front.next;
				}
				
				if(temp2.front==null) {
					
					while(temp1.front!=null) {
						
						if(borrow==1) {
							difference = temp1.front.digit - borrow;
							
							if(difference < 0) {
								difference+=10;
								borrow = 1; 
							}
							else {
								borrow = 0;
							}
						}
						else {
							difference = temp1.front.digit;
						}
						
						DigitNode f = new DigitNode(difference, null);
						
						if (ptr != null) {
							hold.next = f;
							hold = hold.next;
						}

						f = f.next;
						temp1.front = temp1.front.next;
					}
				}
				
				result.numDigits = temp1.numDigits;
				
				/*DigitNode head = null, crnt = null, prev = null;
				
				head = ptr;
				crnt = head;
				
				//Have to hold most recent 0
				while (crnt != null) {

					//If current digit is 0
					if((crnt.digit == 0 && prev == null) 
							|| (crnt.digit != 0) 
							) 
					{
						prev = crnt;
						//System.out.println(prev.digit);
					}
					
					crnt=crnt.next;
					
					if(crnt==null) {
						prev = null;
						crnt = prev;
					}
				}
				
				ptr = head;
				
				while(head!=null) {
					System.out.println(head.digit);
					head=head.next;
				}*/
				
				sumList = ptr;
				result.front = sumList;
				
				//Will assign positive if greater number is positive
				if(temp1.negative==false) {
					result.negative = false;
				}
				//Will assign negative if greater number is negative
				else {
					result.negative = true;
				}
			}
		}

		return result;
	}
	
	/**
	 * Returns the BigInteger obtained by multiplying the first big integer
	 * with the second big integer
	 * 
	 * This method DOES NOT MODIFY either of the input big integers
	 * 
	 * @param first First big integer
	 * @param second Second big integer
	 * @return A new BigInteger which is the product of the first and second big integers
	 */
	public static BigInteger multiply(BigInteger first, BigInteger second) {

		BigInteger temp1 = new BigInteger();
		BigInteger temp2 = new BigInteger();
		BigInteger result = new BigInteger();

		DigitNode productList = null, ptr = null, hold = null;

		int product = 0, carry = 0;

		if(first.numDigits>=second.numDigits) 
		{
			temp1 = first;
			temp2 = second;
		}
		else 
		{
			temp2 = first;
			temp1 = second;
		}

		int level = temp2.numDigits;
		hold = temp1.front;
		boolean skip = false, once = true;

		for(int i = 0; i < level; i++) 
		{	
			
			if(first.numDigits>=second.numDigits) 
			{
				temp1.front = hold;
				hold = temp1.front;
			}
			else {
				temp1.front = hold;
				hold = temp1.front;
			}
			
			/*DigitNode test = temp1.front;
			while(test!=null)
			{
				System.out.print(test.digit);
				test=test.next;
			}
			System.out.println();*/
			
			while(temp1.front!=null) 
			{
				if(i == 0) //level comparison
				{ 
					product = (temp1.front.digit * temp2.front.digit + carry) % 10;
					carry = (temp1.front.digit * temp2.front.digit + carry) / 10;

					DigitNode f = new DigitNode(product, null);

					if (productList == null) 
					{
						productList = f;
						ptr = productList;
					}
					else 
					{
						ptr.next = f;
						ptr = ptr.next;	
					}
					
					if(temp2.numDigits == 1 && temp1.front.next==null) 
					{
						DigitNode s = new DigitNode(carry, null);
						ptr.next = s;
						ptr = ptr.next;	
					}
					else if(temp1.front.next==null && carry != 0)
					{
						DigitNode s = new DigitNode(carry, null);
						ptr.next = s;
						ptr = ptr.next;	
					}
				}

				else 
				{	
					if(skip) 
					{
						//System.out.println("SKIP....");
						ptr = productList;
						skip = false;
						
						for(int j = 0; j < i; j++)
						{
							ptr = ptr.next;
						}
						//Carry must be reinitialized 
						carry = 0;
					}
					
					if(ptr.next == null) {
						
						if(once)
						{
							once = false;
							
							product = ((temp1.front.digit * temp2.front.digit + carry)+ptr.digit) % 10;
							carry =  ((temp1.front.digit * temp2.front.digit + carry)+ptr.digit) / 10;
							ptr.digit = product;
							
						}
						
						else 
						{
							product = (temp1.front.digit * temp2.front.digit + carry) % 10;
							carry =  (temp1.front.digit * temp2.front.digit + carry) / 10;
							
							DigitNode f = new DigitNode(product, null);
							ptr.next = f;
							ptr = ptr.next;	
						}
					}
					else 
					{
						product = ((temp1.front.digit * temp2.front.digit + carry)+ptr.digit) % 10;
						carry =  ((temp1.front.digit * temp2.front.digit + carry)+ptr.digit) / 10;
						ptr.digit = product;
						ptr = ptr.next;
						
					}

				}
				
				temp1.front = temp1.front.next;
			}
			skip = true; once = true;
			temp2.front = temp2.front.next;
		}
		
		result.front = productList;

		return result; 

	}
	
	
	
	
	
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		if (front == null) {
			return "0";
		}
		String retval = front.digit + "";
		for (DigitNode curr = front.next; curr != null; curr = curr.next) {
				retval = curr.digit + retval;
		}
		
		if (negative) {
			retval = '-' + retval;
		}
		return retval;
	}
	
}