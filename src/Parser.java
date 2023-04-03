
import java.util.*;

public class Parser {
    private final Map<String,Double> variables;
    private final char[] formula;
    private final List<String> elements;
    private final List<String> postfixElements;
    private final Map<String, Integer> priority;
    public Parser(final String toCalculate) {
        if (toCalculate != null) {
            formula = toCalculate.toCharArray();
            elements = new ArrayList<>();
            postfixElements = new ArrayList<>();
            priority = new HashMap<>() {{
                put("(", 0);
                put("+", 1);
                put("-", 1);
                put("*", 2);
                put("/", 2);
                put("^", 3);
            }};
            variables = new HashMap<>();
        }
        else {
            throw new RuntimeException("Empty string");
        }
    }
    private void formulaToList() {
        StringBuilder number = new StringBuilder();
        for(Character elem : formula) {
            if(!Character.isDigit(elem)) {

                if(!number.toString().equals("")) {
                    elements.add(number.toString());
                    number = new StringBuilder();
                }

                elements.add(elem.toString());
            }
            else {
                number.append(elem);
            }
        }
    }
    private void goToReversePolandNotation() {
        Deque<String> operatorsOrder = new LinkedList<>();

        for (var elem : elements) {
            if (elem.equals("(")) {
                operatorsOrder.push(elem);
            }
            else if (elem.equals(")")) {
                while (!operatorsOrder.isEmpty() && !operatorsOrder.peek().equals("(")) {
                    postfixElements.add(operatorsOrder.pop());
                }
                operatorsOrder.pop();
            }
            else if (isSign(elem)) {
                while (!operatorsOrder.isEmpty() && priority.get(operatorsOrder.peek()) >= priority.get(elem)) {
                    postfixElements.add(operatorsOrder.pop());
                }
                operatorsOrder.push(elem);
            }
            else {
                postfixElements.add(elem);
            }
        }

        while (!operatorsOrder.isEmpty()) {
            postfixElements.add(operatorsOrder.pop());
        }
    }
    private boolean checkBracketsOrder() {
        return true;
    }
    private String checkLogical() {
        return "";
    }
    private void check() {
        if (!checkBracketsOrder()) {
            throw new RuntimeException("Bracket order error");
        }

        String msg = checkLogical();
        if (!msg.equals("")) {
            throw new RuntimeException(msg);
        }
    }
    private boolean isNumber(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    private boolean isSign(String str) {
        return (str.equals("+") || str.equals("-") || str.equals("*") || str.equals("/") || str.equals("^") );
    }
    private double getVarValue(String variable) {
        final Scanner sc = new Scanner(System.in);
        System.out.println(" Enter value for variable: " + variable);

        return sc.nextDouble();
    }
    public double compute(){
        Deque<Double> numbers = new LinkedList<>();

        check();

        formulaToList();
        goToReversePolandNotation();

        for (var elem : postfixElements) {
            if (isNumber(elem)) {
                numbers.push(Double.parseDouble(elem));
            }
            else if (isSign(elem)) {
                var second = numbers.pop();
                var first = numbers.pop();

                if (elem.equals("+")) {
                    numbers.push(first + second);
                }
                else if (elem.equals("-")) {
                    numbers.push(first - second);
                }
                else if (elem.equals("*")) {
                    numbers.push(first * second);
                }
                else if (elem.equals("/")) {
                    if (second == 0) {
                        throw new RuntimeException("Divide by zero");
                    }

                    numbers.push(first / second);
                }
                else if (elem.equals("^")) {
                    numbers.push(Math.pow(first, second));
                }
            }
            else {
                if (!variables.containsKey(elem)) {
                    variables.put(elem, getVarValue(elem));
                }
                numbers.push(variables.get(elem));
            }
        }

        return numbers.pop();
    }
}

