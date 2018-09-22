package krt170130;

import java.util.HashMap;
import java.util.Scanner;
import java.util.Stack;

public class Num  implements Comparable<Num> {

    static long defaultBase = 10;  // Change as needed
    long base = defaultBase;  // Change as needed
    long[] arr;  // array to store arbitrarily large integers
    boolean isNegative;  // boolean flag to represent negative numbers
    int len;  // actual number of elements of array that are used;  number is stored in arr[0..len-1]

    public Num(String s) {
        arr = new long[10];
        int index = 0;
        for (int i=0;i<s.length();i++) {
            arr[index++] = s.charAt(i) - '0';
            len++;
        }
    }
    public Num (long x, long base) {
        this.base = base;
        if (x < 0) {
            this.isNegative = true;
            x*= -1;
        }

        this.arr = new long[10];
        if (x == 0) {
            this.len = 1;
            return;
        }
        int index = 0;
        while(x > 0)
        {
            this.arr[index++] = x % this.base;
            this.len++;
            x /= this.base;
        }
    }
    public Num(long x) {
        this(x, defaultBase);
    }

    public Num() {
    }

    // Simple addition utility function
    private static Num addUtil(Num a, Num b) {
        long carry = 0;
        int idx1 = 0;
        int idx2 = 0;
        long sum = 0;
        long arr[] = new long[Math.max(a.len,b.len) + 1];
        int index = 0;
        while(idx1 < a.len && idx2 < b.len) {
            sum = (carry + a.arr[idx1++] + b.arr[idx2++]);
            arr[index++] = sum % a.base;
            carry = sum / a.base;
        }
        while(idx1 < a.len) {
            sum = carry + a.arr[idx1++];
            arr[index++] = sum % a.base;
            carry = sum / a.base;
        }
        while(idx2 < b.len) {
            sum = carry + b.arr[idx2++];
            arr[index++] = sum % a.base;
            carry = sum / a.base;
        }
        if (carry == 1) arr[index++] = 1;
        Num c = new Num();
        c.arr = arr;
        c.len = index;
        c.base = a.base;
        c.removeTrailingZeros();
        return c;
    }

    private void removeTrailingZeros() {
        int i;
        for (i = this.len - 1; i>=0 ; i--) {
            if (this.arr[i] !=0 )
                break;
        }
        this.len  = i == -1 ? 1 : i+1;
    }

    private static Num subtractUtil(Num a, Num b) {
        long[] arr = new long[Math.max(a.arr.length, b.arr.length) + 1];
        int alen = a.len;
        int blen = b.len;
        long[] a1 = a.arr;
        long[] b1 = b.arr;
        int index = 0;
        int idx1 = 0;
        int idx2 = 0;
        while(idx1 < alen && idx2 < blen) {
            if (a1[idx1] < b1[idx2]) {
                a1[idx1+1]--;
                a1[idx1] += a.base;
            }
            arr[index] = a1[idx1] - b1[idx2];
            index++;
            idx1++;
            idx2++;
        }
        while(idx1 < alen) {
            arr[index++] = a1[idx1++];
        }
        System.out.println();
        Num res = new Num();
        res.arr = arr;
        res.len = index;
        res.base = a.base;
        res.removeTrailingZeros();
        return res;
    }

    public static Num add(Num a, Num b) {
        Num result;
        int flag = a.compareMagnitude(b);
        if (a.isNegative && b.isNegative) {
            result = addUtil(a,b);
            result.isNegative = true;
        }
        else if (a.isNegative && !b.isNegative) {
            result = flag == 1 && flag !=0 ? subtractUtil(a,b) : subtractUtil(b,a);
            if (flag == 1) result.isNegative = true;
        }
        else if (!a.isNegative && b.isNegative) {
            result = flag == -1 && flag!= 0 ? subtractUtil(b,a) : subtractUtil(a,b);
            if (flag == -1) result.isNegative = true;
        }
        else {
            result = addUtil(a, b);
        }
        return result;
    }


    public static Num subtract(Num a, Num b) {
        Num result;
        int flag = a.compareMagnitude(b);
        if (a.isNegative && b.isNegative) {
            result = flag == 1 && flag!=0 ? subtractUtil(a,b) : subtractUtil(b,a);
            if (flag == 1) result.isNegative = true;
        }
        else if (a.isNegative && !b.isNegative) {
            result = addUtil(a,b);
            result.isNegative = true;
        }
        else if (!a.isNegative && b.isNegative) {
            result = addUtil(a,b);
        }
        else {
            result =  (flag == 1 && flag != 0) ? subtractUtil(a,b): subtractUtil(b,a);
            if (flag == -1) result.isNegative = true;
        }
        return result;
    }

    public static Num product(Num a, Num b) {
        if (a.compareTo(new Num(0)) == 0 || b.compareTo(new Num(0)) == 0) {
            return new Num(0);
        }
        long[] arr = new long[a.len + b.len];
        int idx1 = 0;
        int idx2;
        for (int i=0;i<a.len;i++) {
            long n1 = a.arr[i];
            long carry = 0;
            idx2 = 0 ;
            for (int j=0;j<b.len;j++) {
                long n2 = b.arr[j];
                long sum = n1 * n2 + arr[i+j] + carry;
                carry = sum / a.base;
                arr[i+j] = sum %a.base;
                idx2++;
            }
            if(carry > 0 ) arr[idx1 + idx2] += carry;
            idx1++;
        }
        Num result = new Num();
        result.len = a.len+b.len;
        result.base = a.base;
        result.arr = arr;
        result.isNegative = a.isNegative ^ b.isNegative;
        result.removeTrailingZeros();
        return result;
    }

    // Use divide and conquer
    // To-do handle cases like n is zero
    public static Num power(Num a, long n) {
        if (n == 0)
            return new Num (1);
        Num result = new Num(1);
        while(n > 0) {
            if (n % 2 == 1) {
                result = product(result, a);
            }
            n = n >> 2;
            result = product(result, result);
        }
        return result;
    }

    // Use binary search to calculate a/b
    public static Num divide(Num a, Num b) {
        if (a.compareMagnitude(b) == -1) return new Num(0);
        Num left = new Num(0);
        Num right = a;
        while(left.compareMagnitude(right) <= 0) {
            Num mid = addUtil(left,right).by2();
            mid.removeTrailingZeros();
            if ((product(b, mid).compareMagnitude(a) == 0 || product(b, mid).compareMagnitude(a) == -1) && product(b, add(mid, new Num(1))).compareMagnitude(a) == 1) {
                mid.isNegative = a.isNegative ^ b.isNegative;

                return mid;
            }
            else if (product(b,mid).compareMagnitude(a) == -1) {
                left = addUtil(mid, new Num(1));
            }
            else {
                right = mid;
            }
        }
        return null;
    }

    // return a%b
    public static Num mod(Num a, Num b) {
        if (b.compareMagnitude(new Num(0)) == 0)
            return null;
        Num c = divide(a,b);
        c = subtract(a,product(c,b));
        return c;
    }

    // Use binary search
    public static Num squareRoot(Num a) {
        Num l = new Num(0);
        Num r = a;
        while(l.compareTo(r) <= 0) {
            Num mid = add(l, r).by2();
            if (product(mid,mid).compareTo(a) <= 0 && product(add(mid, new Num(1)), add(mid, new Num(1))).compareTo(a) == 1)
                return mid;
            else if (product(mid, mid).compareTo(a) == -1)
                l = add(mid,new Num(1));
            else
                r = mid;
        }
        return null;
    }


    //Utility function to compare magnitude
    public int compareMagnitude (Num number) {
        if (this.len != number.len) {
            return this.len < number.len ? -1 : 1;
        }
        for (int i=this.len - 1; i>=0; i--) {
            if (this.arr[i] == number.arr[i])
                continue;
            return this.arr[i] < number.arr[i] ? -1 : 1;
        }
        return 0;
    }


    // Utility functions
    // compare "this" to "other": return +1 if this is greater, 0 if equal, -1 otherwise
    public int compareTo(Num other) {
        // Both are negatige
        if (this.isNegative && other.isNegative) {
            int mark = this.compareMagnitude(other);
            return mark * -1;
        }
        // One of them is non negative
        else if (!this.isNegative && other.isNegative) {
            return 1;
        }
        else if (this.isNegative && !other.isNegative) {
            return -1;
        }
        // both are non negative
        else {
            return this.compareMagnitude(other);
        }
    }

    // Output using the format "base: elements of list ..."
    // For example, if base=100, and the number stored corresponds to 10965,
    // then the output is "100: 65 9 1"
    public void printList() {
        for (int i=this.len - 1;i>=0;i--) {
            System.out.print(this.arr[i] + " ");
        }
        System.out.println();
    }

    // Return number to a string in base 10
    public String toString() {

        // TO-DO Handle base change as well
        Num ret = this.convertBase(10);
        String result = "";
        for (int i=0;i<ret.len;i++) {
            result = String.valueOf(ret.arr[i]) + result;
        }
        return this.isNegative ? "-"+result : result;

    }

    public long base() { return base; }

    // Return number equal to "this" number, in base=newBase
    public Num convertBase(int newBase) {
        Num result = new Num(this.arr[len - 1], newBase);
        for (int i=len-1;i>0;i--) {
            result = add(product(result, new Num(this.base, newBase)),new Num(this.arr[i-1], newBase));
        }
        return result;
    }

    // Divide by 2, for using in binary search
    public Num by2() {
        long resultArray[] = new long[this.arr.length];
        int idx = this.len - 1;
        long carry = 0;
        while (idx>=0) {
            long val = (this.base * carry + this.arr[idx] );
            long toput =  val / 2;
            carry = val % 2;
            resultArray[idx] = toput;
            idx--;
        }
        Num res = new Num();
        res.arr = resultArray;
        res.len = this.len;
        res.base = this .base;
        res.isNegative = this.isNegative;
        res.removeTrailingZeros();
        return res;
    }

    // Evaluate an expression in postfix and return resulting number
    // Each string is one of: "*", "+", "-", "/", "%", "^", "0", or
    // a number: [1-9][0-9]*.  There is no unary minus operator.
    public static Num evaluatePostfix(String[] expr) {
        Stack <Num> operands = new Stack <>();
        for (String exp: expr) {
            if (!isOperator(exp)) {
                operands.push(new Num(exp));
            }
            else {
                Num b = operands.pop();
                Num a = operands.pop();
                operands.push(evalExpr(a,b,exp));
            }
        }
        return operands.peek();
    }

    // Evaluate an expression in infix and return resulting number
    // Each string is one of: "*", "+", "-", "/", "%", "^", "(", ")", "0", or
    // a number: [1-9][0-9]*.  There is no unary minus operator.
    public static Num evaluateInfix(String[] expr) {
        Stack <Num> operands = new Stack<>();
        Stack <String> operator = new Stack<>();
        HashMap<String, Integer> priority = new HashMap<>();
        priority.put("+",0);
        priority.put("-",0);
        priority.put("*",1);
        priority.put("/",1);
        priority.put("%",2);
        priority.put("^",2);
        priority.put("(",-1);

        for (String exp: expr) {
            if (!isOperator(exp)) {
                operands.push(new Num(exp));
            }
            else if (exp.equals("("))
                operator.push(exp);
            else if (exp.equals(")")) {
                while(!operator.isEmpty() && operator.peek() != "(") {
                    Num b = operands.pop();
                    Num a = operands.pop();
                    String opr = operator.pop();
                    operands.push(evalExpr(a,b,opr));
                }
                operator.pop();
            }
            else {
                while(!operator.isEmpty() && priority.get(exp) <= priority.get(operator.peek())) {
                    Num b = operands.pop();
                    Num a = operands.pop();
                    String opr = operator.pop();
                    operands.push(evalExpr(a, b, opr));
                }
                operator.push(exp);
            }
        }
        while(!operator.isEmpty()) {
            Num b = operands.pop();
            Num a = operands.pop();
            String opr = operator.pop();
            operands.push(evalExpr(a, b, opr));
        }
        return operands.peek();
    }

    private static Num evalExpr(Num a, Num b, String opr) {
        switch (opr){
            case "+":
                return add(a,b);
            case "-":
                return subtract(a,b);
            case "*":
                return product(a,b);
            case "/":
                return divide(a,b);
            case "%":
                return mod(a,b);
            case "^":
                return add(a,b);
            default:
                return null;
        }
    }

    private static boolean isOperator(String exp) {
        if (exp.equals("-") || exp.equals("+") || exp.equals("*") || exp.equals("/") || exp.equals("%") || exp.equals("^") || exp.equals("(") || exp.equals(")"))
            return true;
        return false;
    }

    public static void main(String[] args) {
    }
}
