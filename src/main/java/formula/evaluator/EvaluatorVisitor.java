package formula.evaluator;

import formula.AST.BinaryExpression;
import formula.AST.BooleanValue;
import formula.AST.CellReference;
import formula.AST.DoubleNumber;
import formula.AST.Formula;
import formula.AST.FunctionCall;
import formula.AST.IntegerNumber;
import formula.AST.ParenExpression;
import formula.AST.StringLiteral;
import formula.AST.UnaryExpression;
import formula.AST.Visitor;
import spreadsheet.ICell;
import spreadsheet.ISheet;

import java.util.Arrays;
import java.util.Map;
import java.util.Stack;
import java.util.stream.Collectors;

public class EvaluatorVisitor implements Visitor {

    private static EvaluatorVisitor evaluatorVisitor;

    private final Stack<Object> valueStack = new Stack<>();
    private ISheet sheet;

    // Singleton pattern for EvaluatorVisitor
    public static EvaluatorVisitor getEvaluatorVisitor() {
        if (evaluatorVisitor == null) {
            evaluatorVisitor = new EvaluatorVisitor();
        }
        return evaluatorVisitor;
    }

    // Evaluates the formula in the given cell
    public void evaluate(ICell cell) throws TypeErrorException {
        valueStack.clear();
        this.sheet = cell.getSheet();
        visitFormula(cell.getFormula());
        Object result = valueStack.pop();
        if (result instanceof Object[][]) {
            throw new TypeErrorException("Cell value cannot be a range of cell values");
        }
        cell.updateValue(result == null ? 0 : result);
    }

    @Override
    public void visitFormula(Formula f) throws TypeErrorException {
        f.exp.accept(this);
    }

    @Override
    public void visitBinaryExpression(BinaryExpression exp) throws TypeErrorException {
        exp.left.accept(this);
        exp.right.accept(this);
        Object right = valueStack.pop();
        Object left = valueStack.pop();

        switch (exp.operator) {
            case PLUS -> valueStack.push(add(left, right));
            case MINUS -> valueStack.push(subtract(left, right));
            case MUL -> valueStack.push(multiply(left, right));
            case DIV -> valueStack.push(divide(left, right));
            case POW -> valueStack.push(power(left, right));
            case EQ -> valueStack.push(left.equals(right));
            case NEQ -> valueStack.push(!left.equals(right));
            case GT -> valueStack.push(compare(left, right) > 0);
            case GE -> valueStack.push(compare(left, right) >= 0);
            case LT -> valueStack.push(compare(left, right) < 0);
            case LE -> valueStack.push(compare(left, right) <= 0);
            case AND -> valueStack.push(and(left, right));
            case OR -> valueStack.push(or(left, right));
            default -> throw new TypeErrorException("Unsupported operator " + exp.operator.name());
        }
    }

    @Override
    public void visitUnaryExpression(UnaryExpression exp) throws TypeErrorException {
        exp.exp.accept(this);
        Object operand = valueStack.pop();
        switch (exp.operator) {
            case Plus -> valueStack.push(operand); // Unary plus
            case Minus -> valueStack.push(negate(operand));
            default -> throw new TypeErrorException("Unsupported unary operator");
        }
    }

    @Override
    public void visitFunctionCall(FunctionCall call) throws TypeErrorException {
        call.argumentList.forEach(arg -> arg.accept(this));
        Object[] args = extractArguments(call.argumentList.size());

        switch (call.functionName.toUpperCase()) {
            case "SUM" -> valueStack.push(sum(args));
            case "AVG" -> valueStack.push(average(args));
            case "MIN" -> valueStack.push(min(args));
            case "MAX" -> valueStack.push(max(args));
            case "COUNT" -> valueStack.push(count(args));
            case "PRODUCT" -> valueStack.push(product(args));
            case "MEDIAN" -> valueStack.push(median(args));
            case "MODE" -> valueStack.push(mode(args));
            default -> throw new TypeErrorException("Unsupported function: " + call.functionName);
        }
    }

    @Override
    public void visitCellReference(CellReference ref) {
        ISheet targetSheet = ref.sheet == null ? sheet : sheet.getSpreadsheet().getSheet(ref.sheet);
        valueStack.push(targetSheet.getValueAt(ref.row, ref.column));
    }

    @Override
    public void visitParenExpression(ParenExpression exp) throws TypeErrorException {
        exp.exp.accept(this); // Recursively evaluate the expression inside parentheses
    }

    @Override
    public void visitIntegerNumber(IntegerNumber num) {
        valueStack.push(num.value);
    }

    @Override
    public void visitDoubleNumber(DoubleNumber num) {
        valueStack.push(num.value);
    }

    @Override
    public void visitBooleanValue(BooleanValue b) {
        valueStack.push(b.value);
    }

    @Override
    public void visitStringLiteral(StringLiteral lit) {
        valueStack.push(lit.value);
    }

    // Utility methods for binary operations
    private Object add(Object left, Object right) {
        if (left instanceof Number l && right instanceof Number r) {
            return l.doubleValue() + r.doubleValue();
        } else if (left instanceof String l && right instanceof String r) {
            return l + r;
        }
        throw new TypeErrorException("Incompatible types for addition");
    }

    private Object subtract(Object left, Object right) {
        if (left instanceof Number l && right instanceof Number r) {
            return l.doubleValue() - r.doubleValue();
        }
        throw new TypeErrorException("Incompatible types for subtraction");
    }

    private Object multiply(Object left, Object right) {
        if (left instanceof Number l && right instanceof Number r) {
            return l.doubleValue() * r.doubleValue();
        }
        throw new TypeErrorException("Incompatible types for multiplication");
    }

    private Object divide(Object left, Object right) {
        if (left instanceof Number l && right instanceof Number r) {
            if (r.doubleValue() == 0) {
                throw new ArithmeticException("Division by zero");
            }
            return l.doubleValue() / r.doubleValue();
        }
        throw new TypeErrorException("Incompatible types for division");
    }

    private Object power(Object left, Object right) {
        if (left instanceof Number l && right instanceof Number r) {
            return Math.pow(l.doubleValue(), r.doubleValue());
        }
        throw new TypeErrorException("Incompatible types for power operation");
    }

    private Object negate(Object operand) {
        if (operand instanceof Number n) {
            return -n.doubleValue();
        }
        throw new TypeErrorException("Incompatible type for negation");
    }

    private Object sum(Object[] args) {
        double sum = 0;
        for (Object arg : args) {
            if (arg instanceof Number n) {
                sum += n.doubleValue();
            } else {
                throw new TypeErrorException("Unsupported argument type for SUM");
            }
        }
        return sum;
    }

    private Object average(Object[] args) {
        double sum = 0;
        for (Object arg : args) {
            if (arg instanceof Number n) {
                sum += n.doubleValue();
            } else {
                throw new TypeErrorException("Unsupported argument type for AVERAGE");
            }
        }
        return sum / args.length;
    }

    private Object min(Object[] args) {
        double min = Double.MAX_VALUE;
        for (Object arg : args) {
            if (arg instanceof Number n) {
                min = Math.min(min, n.doubleValue());
            } else {
                throw new TypeErrorException("Unsupported argument type for MIN");
            }
        }
        return min;
    }

    private Object max(Object[] args) {
        double max = Double.MIN_VALUE;
        for (Object arg : args) {
            if (arg instanceof Number n) {
                max = Math.max(max, n.doubleValue());
            } else {
                throw new TypeErrorException("Unsupported argument type for MAX");
            }
        }
        return max;
    }

    private Object product(Object[] args) {
        double product = 1;
        for (Object arg : args) {
            if (arg instanceof Number n) {
                product *= n.doubleValue();
            } else {
                throw new TypeErrorException("Unsupported argument type for PRODUCT");
            }
        }
        return product;
    }

    private Object median(Object[] args) {
        double[] numbers = Arrays.stream(args)
                .filter(Number.class::isInstance)
                .mapToDouble(n -> ((Number) n).doubleValue())
                .sorted()
                .toArray();
        int mid = numbers.length / 2;
        if (numbers.length % 2 == 0) {
            return (numbers[mid - 1] + numbers[mid]) / 2.0;
        }
        return numbers[mid];
    }

    private Object mode(Object[] args) {
        Map<Object, Long> frequency = Arrays.stream(args)
                .filter(Number.class::isInstance)
                .collect(Collectors.groupingBy(n -> n, Collectors.counting()));
        return frequency.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .orElseThrow(() -> new TypeErrorException("No mode found"))
                .getKey();
    }

    private int count(Object[] args) {
        return args.length;
    }

    private boolean and(Object left, Object right) {
        if (left instanceof Boolean l && right instanceof Boolean r) {
            return l && r;
        }
        throw new TypeErrorException("Incompatible types for AND operation");
    }

    private boolean or(Object left, Object right) {
        if (left instanceof Boolean l && right instanceof Boolean r) {
            return l || r;
        }
        throw new TypeErrorException("Incompatible types for OR operation");
    }

    private int compare(Object left, Object right) {
        if (left instanceof Number l && right instanceof Number r) {
            return Double.compare(l.doubleValue(), r.doubleValue());
        }
        throw new TypeErrorException("Incompatible types for comparison");
    }

    private Object[] extractArguments(int size) {
        Object[] args = new Object[size];
        for (int i = size - 1; i >= 0; i--) {
            args[i] = valueStack.pop();
        }
        return args;
    }
}
