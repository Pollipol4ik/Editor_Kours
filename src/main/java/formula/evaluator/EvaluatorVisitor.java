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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * EvaluatorVisitor is responsible for evaluating formulas and expressions within spreadsheet cells.
 * It follows the Visitor pattern to traverse and evaluate AST nodes.
 */
public class EvaluatorVisitor implements Visitor {

    private static EvaluatorVisitor instance;
    private final Stack<Object> valueStack = new Stack<>();
    private ISheet currentSheet;

    // Singleton pattern for obtaining the EvaluatorVisitor instance
    public static EvaluatorVisitor getInstance() {
        if (instance == null) {
            instance = new EvaluatorVisitor();
        }
        return instance;
    }

    // Evaluates the formula in the specified cell
    public void evaluate(ICell cell) throws TypeErrorException {
        valueStack.clear();
        this.currentSheet = cell.getSheet();
        visitFormula(cell.getFormula());
        Object result = valueStack.pop();

        if (result instanceof Object[][]) {
            throw new TypeErrorException("Cell value cannot be a range of cell values");
        }

        cell.updateValue(result == null ? 0 : result);
    }

    @Override
    public void visitFormula(Formula formula) throws TypeErrorException {
        formula.exp.accept(this);
    }

    @Override
    public void visitBinaryExpression(BinaryExpression exp) throws TypeErrorException {
        exp.left.accept(this);
        exp.right.accept(this);
        Object right = valueStack.pop();
        Object left = valueStack.pop();

        valueStack.push(switch (exp.operator) {
            case PLUS -> add(left, right);
            case MINUS -> subtract(left, right);
            case MUL -> multiply(left, right);
            case DIV -> divide(left, right);
            case POW -> power(left, right);
            case EQ -> left.equals(right);
            case NEQ -> !left.equals(right);
            case GT -> compare(left, right) > 0;
            case GE -> compare(left, right) >= 0;
            case LT -> compare(left, right) < 0;
            case LE -> compare(left, right) <= 0;
            case AND -> and(left, right);
            case OR -> or(left, right);
            default -> throw new TypeErrorException("Unsupported operator " + exp.operator.name());
        });
    }

    @Override
    public void visitUnaryExpression(UnaryExpression exp) throws TypeErrorException {
        exp.exp.accept(this);
        Object operand = valueStack.pop();

        valueStack.push(switch (exp.operator) {
            case Plus -> operand; // Unary plus
            case Minus -> negate(operand);
            default -> throw new TypeErrorException("Unsupported unary operator");
        });
    }

    @Override
    public void visitFunctionCall(FunctionCall call) throws TypeErrorException {
        call.argumentList.forEach(arg -> arg.accept(this));
        Object[] args = extractArguments(call.argumentList.size());
        valueStack.push(applyFunction(call.functionName.toUpperCase(), args));
    }

    private Object[] extractArguments(int count) throws TypeErrorException {
        Object[] args = new Object[count];
        for (int i = count - 1; i >= 0; i--) {
            args[i] = valueStack.pop();
        }
        return args;
    }

    private Object applyFunction(String functionName, Object[] args) {
        return switch (functionName) {
            case "SUM" -> sum(args);
            case "AVG" -> average(args);
            case "MIN" -> min(args);
            case "MAX" -> max(args);
            case "COUNT" -> count(args);
            case "PRODUCT" -> product(args);
            case "MEDIAN" -> median(args);
            case "STDEV" -> standardDeviation(args);
            case "VAR" -> variance(args);
            case "MODE" -> mode(args);
            case "IF" -> conditional(args);
            case "CONCAT" -> concat(args);
            default -> throw new TypeErrorException("Unsupported function: " + functionName);
        };
    }
    @Override
    public void visitCellReference(CellReference ref) {
        ISheet sheet = ref.sheet == null ? currentSheet : currentSheet.getSpreadsheet().getSheet(ref.sheet);

        if (ref.isRange()) {
            // Если это диапазон, собираем все значения из указанного диапазона
            List<ICell> cellsInRange = new ArrayList<>();
            for (int row = ref.startRow; row <= ref.endRow; row++) {
                for (int col = ref.startColumn; col <= ref.endColumn; col++) {
                    ICell cell = sheet.getCellAt(row, col);
                    cellsInRange.add(cell); // Собираем все ячейки в диапазоне
                }
            }
            // Возвращаем массив значений из диапазона
            valueStack.push(cellsInRange.toArray(new ICell[0]));
        } else {
            // Если это одиночная ячейка, просто возвращаем значение этой ячейки
            valueStack.push(sheet.getValueAt(ref.startRow, ref.startColumn));
        }
    }

    @Override
    public void visitParenExpression(ParenExpression exp) throws TypeErrorException {
        exp.exp.accept(this);
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
                sum += n.doubleValue();  // Если аргумент — это число, добавляем его к сумме
            } else if (arg instanceof ICell[] cells) {
                // Если это массив ячеек, суммируем их значения
                for (ICell cell : cells) {
                    sum += ((Number) cell.getValue()).doubleValue();
                }
            } else {
                throw new TypeErrorException("Unsupported argument type for SUM");
            }
        }
        return sum;
    }
    private Object average(Object[] args) {
        double sum = 0;
        int count = 0;
        for (Object arg : args) {
            if (arg instanceof Number n) {
                sum += n.doubleValue();
                count++;
            } else if (arg instanceof ICell[] cells) {
                // Если это массив ячеек, суммируем их значения
                for (ICell cell : cells) {
                    sum += ((Number) cell.getValue()).doubleValue();
                    count++;
                }
            } else {
                throw new TypeErrorException("Unsupported argument type for AVERAGE");
            }
        }
        return count == 0 ? 0 : sum / count;
    }

    private Object min(Object[] args) {
        double min = Double.MAX_VALUE;
        for (Object arg : args) {
            if (arg instanceof Number n) {
                min = Math.min(min, n.doubleValue());
            } else if (arg instanceof ICell[] cells) {
                // Если это массив ячеек, находим минимальное значение среди них
                for (ICell cell : cells) {
                    min = Math.min(min, ((Number) cell.getValue()).doubleValue());
                }
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
            } else if (arg instanceof ICell[] cells) {
                // Если это массив ячеек, находим максимальное значение среди них
                for (ICell cell : cells) {
                    max = Math.max(max, ((Number) cell.getValue()).doubleValue());
                }
            } else {
                throw new TypeErrorException("Unsupported argument type for MAX");
            }
        }
        return max;
    }

    private int count(Object[] args) {
        int count = 0;
        for (Object arg : args) {
            if (arg instanceof ICell[] cells) {
                count += cells.length;
            } else {
                count++;
            }
        }
        return count;
    }

    private Object product(Object[] args) {
        double product = 1;
        for (Object arg : args) {
            if (arg instanceof Number n) {
                product *= n.doubleValue();
            } else if (arg instanceof ICell[] cells) {
                for (ICell cell : cells) {
                    product *= ((Number) cell.getValue()).doubleValue();
                }
            } else {
                throw new TypeErrorException("Unsupported argument type for PRODUCT");
            }
        }
        return product;
    }

    private Object median(Object[] args) {
        List<Double> values = new ArrayList<>();
        for (Object arg : args) {
            if (arg instanceof Number n) {
                values.add(n.doubleValue());
            } else if (arg instanceof ICell[] cells) {
                for (ICell cell : cells) {
                    values.add(((Number) cell.getValue()).doubleValue());
                }
            } else {
                throw new TypeErrorException("Unsupported argument type for MEDIAN");
            }
        }
        Collections.sort(values);
        int size = values.size();
        if (size % 2 == 1) {
            return values.get(size / 2);
        } else {
            return (values.get(size / 2 - 1) + values.get(size / 2)) / 2.0;
        }
    }

    private Object standardDeviation(Object[] args) {
        double sum = 0;
        double count = 0;
        for (Object arg : args) {
            if (arg instanceof Number n) {
                sum += n.doubleValue();
                count++;
            } else if (arg instanceof ICell[] cells) {
                for (ICell cell : cells) {
                    sum += ((Number) cell.getValue()).doubleValue();
                    count++;
                }
            } else {
                throw new TypeErrorException("Unsupported argument type for STDEV");
            }
        }
        double mean = sum / count;
        double variance = 0;
        for (Object arg : args) {
            if (arg instanceof Number n) {
                variance += Math.pow(n.doubleValue() - mean, 2);
            } else if (arg instanceof ICell[] cells) {
                for (ICell cell : cells) {
                    variance += Math.pow(((Number) cell.getValue()).doubleValue() - mean, 2);
                }
            }
        }
        return Math.sqrt(variance / count);
    }

    private Object variance(Object[] args) {
        double sum = 0;
        double count = 0;
        for (Object arg : args) {
            if (arg instanceof Number n) {
                sum += n.doubleValue();
                count++;
            } else if (arg instanceof ICell[] cells) {
                for (ICell cell : cells) {
                    sum += ((Number) cell.getValue()).doubleValue();
                    count++;
                }
            } else {
                throw new TypeErrorException("Unsupported argument type for VAR");
            }
        }
        double mean = sum / count;
        double variance = 0;
        for (Object arg : args) {
            if (arg instanceof Number n) {
                variance += Math.pow(n.doubleValue() - mean, 2);
            } else if (arg instanceof ICell[] cells) {
                for (ICell cell : cells) {
                    variance += Math.pow(((Number) cell.getValue()).doubleValue() - mean, 2);
                }
            }
        }
        return variance / count;
    }

    private Object mode(Object[] args) {
        Map<Double, Integer> frequency = new HashMap<>();
        for (Object arg : args) {
            if (arg instanceof Number n) {
                frequency.put(n.doubleValue(), frequency.getOrDefault(n.doubleValue(), 0) + 1);
            } else if (arg instanceof ICell[] cells) {
                for (ICell cell : cells) {
                    frequency.put(((Number) cell.getValue()).doubleValue(),
                            frequency.getOrDefault(((Number) cell.getValue()).doubleValue(), 0) + 1);
                }
            }
        }
        double mode = Double.NaN;
        int maxCount = 0;
        for (Map.Entry<Double, Integer> entry : frequency.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                mode = entry.getKey();
            }
        }
        return mode;
    }

    private Object conditional(Object[] args) {
        if (args.length != 3) {
            throw new TypeErrorException("IF function expects 3 arguments");
        }
        boolean condition = (boolean) args[0];
        return condition ? args[1] : args[2];
    }

    private Object concat(Object[] args) {
        StringBuilder sb = new StringBuilder();
        for (Object arg : args) {
            sb.append(arg.toString());
        }
        return sb.toString();
    }

    private boolean and(Object left, Object right) {
        return (boolean) left && (boolean) right;
    }

    private boolean or(Object left, Object right) {
        return (boolean) left || (boolean) right;
    }

    private int compare(Object left, Object right) {
        if (left instanceof Number l && right instanceof Number r) {
            return Double.compare(l.doubleValue(), r.doubleValue());
        } else if (left instanceof String l && right instanceof String r) {
            return l.compareTo(r);
        }
        throw new TypeErrorException("Unsupported types for comparison");
    }
}
