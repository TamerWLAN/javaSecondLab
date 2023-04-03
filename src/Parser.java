
import java.util.*;
/**
 * класс парсер, позволяющий вычислять значение арифметического выражения в ЦЕЛЫХ числах
 * возможно использование круглых скобок, переменных и операций +-/*^
 * вещественные значения могут принимать ТОЛЬКО переменные
 * @author Artem Kozlitin
 * @version 1.3
 * @since 1.0
 */
public class Parser {
    /**Словарь, отвечающий за хранение значений переменных (key - переменная, value - значение)*/
    private final Map<String,Double> variables;
    /**Символьный массив, хранящий формулу посимвольно и без пробелов*/
    private final char[] formula;
    /**Строковый список элементов формулы, где каждый элемент - число|операция|скобка|переменная*/
    private final List<String> elements;
    /**Строковывй список элементов формулы, записанной в обратной польской нотации
     * где каждый элемент - число|операция|переменная
     */
    private final List<String> postfixElements;
    /**Словарь, отвечающий за хранение приоритета операций при переводе в обратную польскую нотацию*/
    private final Map<String, Integer> priority;

    /**
     * Консктруктор класса, инициализирующий все необходимые струрктуры данных,
     * заполняющий словарь приеоритетов
     * и приводящий строковую формулу к массиву символов без пробелов
     * @param toCalculate Исходный вид арифметического выражения, для вычисления
     */
    public Parser(final String toCalculate) {
        if (toCalculate != null) {
            formula = toCalculate.replace(" ","").toCharArray();
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

    /**
     * Метод преобразующий выражение из массива символов
     * в строковый список его элементов
     */
    private void formulaToList() {
        StringBuilder number = new StringBuilder();
        for (Character elem : formula) {
            if (!Character.isDigit(elem)) {

                if (!number.toString().equals("")) {
                    elements.add(number.toString());
                    number = new StringBuilder();
                }

                elements.add(elem.toString());
            }
            else {
                number.append(elem);
            }
        }
        if (!number.toString().equals("")) {
            elements.add(number.toString());
        }
    }

    /**
     * Метод приводящий выражение в постфиксный вид
     * и записывающий его в постфиксный список элементов
     */
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

    /**
     * Метод проверяющий правильность расстановки скобок в выражении на основе стека
     * @return результат проверки: true - проблем не, false - скобки расставлены неправильно
     */
    private boolean checkBracketsOrder() {
        Deque<Character> brackets = new LinkedList<>();

        for (Character elem : formula) {
            if(elem == '(') {
                brackets.push(elem);
            }
            else if (elem == ')') {
                if (!brackets.isEmpty()) {
                    brackets.pop();
                }
                else {
                    return false;
                }
            }
        }

        return brackets.isEmpty();
    }

    /**
     * Метод проверяющий выражение на корректность для вычисления (без учета порядка скобок)
     * @return сообщение об ошибке, в случае проблем; пустую строку - в случае корректности
     */
    private String checkLogical() {
        final String availableSigns = "+-*/^";

        if (availableSigns.indexOf(formula[0]) != -1) {
            return "String must be started with number/variables or bracket";
        }
        if (availableSigns.indexOf(formula[formula.length - 1]) != -1) {
            return "String must not ended on sign";
        }

        for (int i = 0; i < formula.length - 1; i++) {
            if(Character.isLetter(formula[i]) && Character.isLetter(formula[i + 1])){
                return "Variable must be called single letter";
            }
            else if ( Character.isLetter(formula[i]) && availableSigns.indexOf(formula[i + 1]) == -1 && formula[i + 1] != ')') {
                return "After variable, must be a sign";
            }
            else if ( Character.isDigit(formula[i]) && !Character.isDigit(formula[i + 1]) && availableSigns.indexOf(formula[i + 1]) == -1 && formula[i + 1] != ')') {
                return "After number, must be a sign or another number";
            }
            else if (formula[i] == '(' && !(formula[i + 1] == '(' || Character.isDigit(formula[i + 1]) || Character.isLetter(formula[i + 1]))) {
                return "After open brackets must be a another bracket or number/variable";
            }
            else if (formula[i] == ')' && !(formula[i + 1] == '(' || formula[i + 1] == ')' || availableSigns.indexOf(formula[i + 1]) != -1)) {
                return "After closes brackets must be a another bracket or sign";
            }
            else if (availableSigns.indexOf(formula[i]) != -1 && availableSigns.indexOf(formula[i + 1]) != -1) {
                return "There must not be two signs in a row";
            }
            else if (formula[i] == '(' && formula[i + 1] == ')') {
                return "There must not be empty brackets";
            }
            else if (availableSigns.indexOf(formula[i]) != -1 && formula[i + 1] == ')') {
                return "After sign must be a number/variable or open bracket";
            }
        }
        return "";
    }
    /**
     * Метод проверяющий общую правильность выражения
     * вызывающий checkBracketsOrder - для скобок
     * и checkLogical для знаков чисел и переменных
     * в случае некорректности вызывает RuntimeException с соответствующим сообщением
     */
    private void check() {
        if (!checkBracketsOrder()) {
            throw new RuntimeException("Bracket order error");
        }

        String msg = checkLogical();
        if (!msg.equals("")) {
            throw new RuntimeException(msg);
        }
    }

    /**
     * Метод для проверки , является ли данная последовательность символов числом типа double либо приводимым к нему
     * Проверка осуществляется посредсвтом попытки парсинга строки к типу Double
     * @param str Строка для проверки
     * @return Результат проверки: true - число, false - не число
     */
    private boolean isNumber(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Метод, проверяющий является ли данная строка одним из символов допустимых операций
     * @param str Строка для проверки
     * @return Результат проверки: true - знак, false - не знак
     */
    private boolean isSign(String str) {
        return (str.equals("+") || str.equals("-") || str.equals("*") || str.equals("/") || str.equals("^") );
    }

    /**
     * Метод запрашивающий у пользователя значение для текущей переменной
     * @param variable Переменная
     * @return Введённое пользователем значение
     */
    private double getVarValue(String variable) {
        final Scanner sc = new Scanner(System.in);
        System.out.println(" Enter value for variable: " + variable);

        return sc.nextDouble();
    }

    /**
     * Метод вычисляющий значени арифметического выражения
     * сначала вызывает метод check для проверки
     * в случае успеха вычисляет значение с помощью двух стеков (для чисел и для знаков)
     * @return Итоговое значение выражения
     */
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

