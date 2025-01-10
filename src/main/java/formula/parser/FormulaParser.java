package formula.parser;

import formula.AST.*;

import java.util.*;

public class FormulaParser {

    enum State {
        Formula0,
        Formula1,
        BoolAdd1,
        BoolAdd2,
        BoolAdd3,
        BoolMul1,
        BoolMul2,
        BoolMul3,
        BoolUn1,
        BoolUn2,
        BoolUnCoer1,
        Comp1,
        Comp2,
        Comp3,
        NumAdd1,
        NumAdd2,
        NumAdd3,
        NumMul1,
        NumMul2,
        NumMul3,
        NumUn1,
        NumUn2,
        NumUnCoer1,
        NumPow1,
        NumPow2,
        NumPow3,
        Paren1,
        Paren2,
        Paren3,
        CellRange1,
        CellRange2,
        CellRange3,
        SheetCell1OrCall1,
        SheetCell2,
        SheetCell3,
        Call2,
        Call3,
        Call4,
        CallNoArg3,
        ArgList1,
        ArgList2,
        ArgList3,
        Cell1,
        Lit1
    }

    enum Nonterminal {
        BoolAdd,
        BoolMul,
        BoolUn,
        Comp,
        NumAdd,
        NumMul,
        NumUn,
        NumPow,
        Paren,
        CellRange,
        SheetCell,
        FunctionCall,
        ArgList,
        ArgListNonEmpty,
        Lit
    }

    static class Action {
        public enum Type {
            shift,
            reduce,
            accept
        }
        Type type;
        State state;
        Nonterminal nonterminal;
        int tokenNumber;

        Action(Type type) {
            this.type = type;
        }

        Action(Type type, State state) {
            this.type = type;
            this.state = state;
        }

        Action(Type type, Nonterminal nonterminal, int tokenNumber) {
            this.type = type;
            this.nonterminal = nonterminal;
            this.tokenNumber = tokenNumber;
        }

        @Override
        public String toString() {
            return "Action{" +
                    "type=" + type +
                    ", state=" + state +
                    ", nonterminal=" + nonterminal +
                    ", tokenNumber=" + tokenNumber +
                    '}';
        }
    }

    State[][] goToTable ;

    Action[][] actionTable;

    Stack<Object> stack;

    private static FormulaParser parser;

    public static FormulaParser getParser() {
        if (parser == null) {
            parser = new FormulaParser();
        }
        return parser;
    }

    private FormulaParser(){
        stack = new Stack<>();
        Action temp1;
        List<Action> actionList;
        List<State> stateList;
        // action table
        actionTable = new Action[State.values().length][TokenType.values().length];
        // accept
        actionTable[State.Formula1.ordinal()][TokenType.FINISH.ordinal()] = new Action(Action.Type.accept);
        // reduces
        actionList = List.of(new Action(Action.Type.reduce, Nonterminal.BoolAdd, 1),
                new Action(Action.Type.reduce, Nonterminal.BoolAdd, 3),
                new Action(Action.Type.reduce, Nonterminal.BoolMul, 1),
                new Action(Action.Type.reduce, Nonterminal.BoolMul, 3),
                new Action(Action.Type.reduce, Nonterminal.BoolUn, 1),
                new Action(Action.Type.reduce, Nonterminal.BoolUn, 2),
                new Action(Action.Type.reduce, Nonterminal.Comp, 1),
                new Action(Action.Type.reduce, Nonterminal.Comp, 3),
                new Action(Action.Type.reduce, Nonterminal.NumAdd, 1),
                new Action(Action.Type.reduce, Nonterminal.NumAdd, 3),
                new Action(Action.Type.reduce, Nonterminal.NumMul, 1),
                new Action(Action.Type.reduce, Nonterminal.NumMul, 3),
                new Action(Action.Type.reduce, Nonterminal.NumUn, 1),
                new Action(Action.Type.reduce, Nonterminal.NumUn, 2),
                new Action(Action.Type.reduce, Nonterminal.NumPow, 1),
                new Action(Action.Type.reduce, Nonterminal.NumPow, 3),
                new Action(Action.Type.reduce, Nonterminal.CellRange, 1),
                new Action(Action.Type.reduce, Nonterminal.CellRange, 3),
                new Action(Action.Type.reduce, Nonterminal.SheetCell, 1),
                new Action(Action.Type.reduce, Nonterminal.SheetCell, 3),
                new Action(Action.Type.reduce, Nonterminal.FunctionCall, 3),
                new Action(Action.Type.reduce, Nonterminal.Lit, 1));
        stateList = List.of(State.BoolAdd1,
                State.BoolAdd3,
                State.BoolMul1,
                State.BoolMul3,
                State.BoolUnCoer1,
                State.BoolUn2,
                State.Comp1,
                State.Comp3,
                State.NumAdd1,
                State.NumAdd3,
                State.NumMul1,
                State.NumMul3,
                State.NumUnCoer1,
                State.NumUn2,
                State.NumPow1,
                State.NumPow3,
                State.CellRange1,
                State.CellRange3,
                State.Cell1,
                State.SheetCell3,
                State.Call4,
                State.Lit1);
        for (TokenType type : TokenType.values()) {
            for (int i = 0; i < stateList.size(); ++i) {
                actionTable[stateList.get(i).ordinal()][type.ordinal()] = actionList.get(i);
            }
        }
        actionTable[State.Call2.ordinal()][TokenType.PARENRIGHT.ordinal()] = new Action(Action.Type.reduce, Nonterminal.ArgList, 0);
        actionTable[State.ArgList1.ordinal()][TokenType.PARENRIGHT.ordinal()] = new Action(Action.Type.reduce, Nonterminal.ArgList, 1);
        actionTable[State.ArgList3.ordinal()][TokenType.COMMA.ordinal()] = new Action(Action.Type.reduce, Nonterminal.ArgListNonEmpty, 3);
        actionTable[State.ArgList3.ordinal()][TokenType.PARENRIGHT.ordinal()] = new Action(Action.Type.reduce, Nonterminal.ArgList, 3);
        //actionTable[State.ParamList1.ordinal()][TokenType.PARENRIGHT.ordinal()] = new Action(Action.Type.reduce, Nonterminal.ArgList, 1);
        // shifts
        // elementary expressions
        actionList = List.of(new Action(Action.Type.shift, State.Lit1),
                new Action(Action.Type.shift, State.SheetCell1OrCall1),
                new Action(Action.Type.shift, State.Cell1),
                new Action(Action.Type.shift, State.Paren1));
        for (State state : List.of(State.Formula0,
                State.BoolAdd2,
                State.BoolMul2,
                State.BoolUn1,
                State.Comp1,
                State.NumAdd2,
                State.NumMul2,
                State.NumUn1,
                State.NumPow2,
                State.Paren1,
                State.Call2,
                State.ArgList2)) {
            for (TokenType type : List.of(TokenType.INT,
                    TokenType.DOUBLE,
                    TokenType.STRING,
                    TokenType.BOOLEAN)) {
                actionTable[state.ordinal()][type.ordinal()] = actionList.get(0);
            }
            actionTable[state.ordinal()][TokenType.ID.ordinal()] = actionList.get(1);
            actionTable[state.ordinal()][TokenType.REF.ordinal()] = actionList.get(2);
            actionTable[state.ordinal()][TokenType.PARENLEFT.ordinal()] = actionList.get(3);
        }
        // prefix expressions
        temp1 = new Action(Action.Type.shift, State.BoolUn1);
        for (State state : List.of(State.Formula0,
                State.BoolAdd2,
                State.BoolMul2,
                State.BoolUn1,
                State.Paren1,
                State.Call2,
                State.ArgList2)) {
            actionTable[state.ordinal()][TokenType.NOT.ordinal()] = temp1;
        }
        temp1 = new Action(Action.Type.shift, State.NumUn1);
        for (State state : List.of(State.Formula0,
                State.BoolAdd2,
                State.BoolMul2,
                State.BoolUn1,
                State.Comp1,
                State.NumAdd2,
                State.NumMul2,
                State.NumUn1,
                State.Paren1,
                State.Call2,
                State.ArgList2)) {
            for (TokenType type : List.of(TokenType.PLUS, TokenType.MINUS)) {
                actionTable[state.ordinal()][type.ordinal()] = temp1;
            }
        }
        // infix expressions
        actionTable[State.BoolAdd1.ordinal()][TokenType.OR.ordinal()] = new Action(Action.Type.shift, State.BoolAdd2);
        actionTable[State.BoolMul1.ordinal()][TokenType.AND.ordinal()] = new Action(Action.Type.shift, State.BoolMul2);
        temp1 = new Action(Action.Type.shift, State.Comp2);
        for (TokenType type : List.of(TokenType.EQ,
                TokenType.NEQ,
                TokenType.GT,
                TokenType.GE,
                TokenType.LT,
                TokenType.LE)) {
            actionTable[State.Comp1.ordinal()][type.ordinal()] = temp1;
        }
        temp1 = new Action(Action.Type.shift, State.NumAdd2);
        for (TokenType type : List.of(TokenType.PLUS,
                TokenType.MINUS)) {
            actionTable[State.NumAdd1.ordinal()][type.ordinal()] = temp1;
        }
        temp1 = new Action(Action.Type.shift, State.NumMul2);
        for (TokenType type : List.of(TokenType.MUL,
                TokenType.DIV)) {
            actionTable[State.NumMul1.ordinal()][type.ordinal()] = temp1;
        }
        actionTable[State.NumPow1.ordinal()][TokenType.POW.ordinal()] = new Action(Action.Type.shift, State.NumPow2);
        actionTable[State.Paren2.ordinal()][TokenType.PARENRIGHT.ordinal()] = new Action(Action.Type.shift, State.Paren3);
        actionTable[State.Call3.ordinal()][TokenType.PARENRIGHT.ordinal()] = new Action(Action.Type.shift, State.Call4);
        actionTable[State.CellRange1.ordinal()][TokenType.COLON.ordinal()] = new Action(Action.Type.shift, State.CellRange2);
        actionTable[State.SheetCell1OrCall1.ordinal()][TokenType.DOT.ordinal()] = new Action(Action.Type.shift, State.SheetCell2);
        actionTable[State.SheetCell1OrCall1.ordinal()][TokenType.PARENLEFT.ordinal()] = new Action(Action.Type.shift, State.Call2);
        actionTable[State.SheetCell2.ordinal()][TokenType.REF.ordinal()] = new Action(Action.Type.shift, State.SheetCell3);
        actionTable[State.ArgList1.ordinal()][TokenType.COMMA.ordinal()] = new Action(Action.Type.shift, State.ArgList2);
        actionTable[State.CellRange2.ordinal()][TokenType.ID.ordinal()] = actionList.get(1);
        actionTable[State.CellRange2.ordinal()][TokenType.REF.ordinal()] = actionList.get(2);
        // goto table
        goToTable = new State[State.values().length][Nonterminal.values().length];
        for (State state : List.of(State.Formula0,
                State.BoolAdd2,
                State.BoolMul2,
                State.BoolUn1,
                State.Comp2,
                State.NumAdd2,
                State.NumMul2,
                State.NumUn1,
                State.NumPow2,
                State.Paren1,
                State.Call2,
                State.ArgList2)) {
            goToTable[state.ordinal()][Nonterminal.Lit.ordinal()]
                    = goToTable[state.ordinal()][Nonterminal.CellRange.ordinal()]
                    = goToTable[state.ordinal()][Nonterminal.SheetCell.ordinal()]
                    = goToTable[state.ordinal()][Nonterminal.Paren.ordinal()]
                    = goToTable[state.ordinal()][Nonterminal.FunctionCall.ordinal()]
                    = State.NumPow1;
            goToTable[state.ordinal()][Nonterminal.SheetCell.ordinal()] = State.CellRange1;
        }
        for (State state : List.of(State.Formula0,
                State.BoolAdd2,
                State.BoolMul2,
                State.BoolUn1,
                State.Comp2,
                State.NumAdd2,
                State.NumMul2,
                State.NumUn1,
                State.Paren1,
                State.Call2,
                State.ArgList2)) {
            goToTable[state.ordinal()][Nonterminal.NumPow.ordinal()] = State.NumUnCoer1;
        }
        for (State state : List.of(State.Formula0,
                State.BoolAdd2,
                State.BoolMul2,
                State.BoolUn1,
                State.Comp2,
                State.NumAdd2,
                State.NumMul2,
                State.Paren1,
                State.Call2,
                State.ArgList2)) {
            goToTable[state.ordinal()][Nonterminal.NumUn.ordinal()] = State.NumMul1;
        }
        for (State state : List.of(State.Formula0,
                State.BoolAdd2,
                State.BoolMul2,
                State.BoolUn1,
                State.Comp2,
                State.NumAdd2,
                State.Paren1,
                State.Call2,
                State.ArgList2)) {
            goToTable[state.ordinal()][Nonterminal.NumMul.ordinal()] = State.NumAdd1;
        }
        for (State state : List.of(State.Formula0,
                State.BoolAdd2,
                State.BoolMul2,
                State.BoolUn1,
                State.Comp2,
                State.Paren1,
                State.Call2,
                State.ArgList2)) {
            goToTable[state.ordinal()][Nonterminal.NumAdd.ordinal()] = State.Comp1;
        }
        for (State state : List.of(State.Formula0,
                State.BoolAdd2,
                State.BoolMul2,
                State.BoolUn1,
                State.Paren1,
                State.Call2,
                State.ArgList2)) {
            goToTable[state.ordinal()][Nonterminal.Comp.ordinal()] = State.BoolUnCoer1;
        }
        for (State state : List.of(State.Formula0,
                State.BoolAdd2,
                State.BoolMul2,
                State.Paren1,
                State.Call2,
                State.ArgList2)) {
            goToTable[state.ordinal()][Nonterminal.BoolUn.ordinal()] = State.BoolMul1;
        }
        for (State state : List.of(State.Formula0,
                State.BoolAdd2,
                State.Paren1,
                State.Call2,
                State.ArgList2)) {
            goToTable[state.ordinal()][Nonterminal.BoolMul.ordinal()] = State.BoolAdd1;
        }
        goToTable[State.CellRange2.ordinal()][Nonterminal.SheetCell.ordinal()] = State.CellRange3;
        goToTable[State.NumPow2.ordinal()][Nonterminal.NumPow.ordinal()] = State.NumPow3;
        goToTable[State.NumUn1.ordinal()][Nonterminal.NumUn.ordinal()] = State.NumUn2;
        goToTable[State.NumMul2.ordinal()][Nonterminal.NumMul.ordinal()] = State.NumMul3;
        goToTable[State.NumAdd2.ordinal()][Nonterminal.NumAdd.ordinal()] = State.NumAdd3;
        goToTable[State.Comp2.ordinal()][Nonterminal.Comp.ordinal()] = State.Comp3;
        goToTable[State.BoolUn1.ordinal()][Nonterminal.BoolUn.ordinal()] = State.BoolUn2;
        goToTable[State.BoolMul2.ordinal()][Nonterminal.BoolMul.ordinal()] = State.BoolMul3;
        goToTable[State.BoolAdd2.ordinal()][Nonterminal.BoolAdd.ordinal()] = State.BoolAdd3;
        goToTable[State.Paren1.ordinal()][Nonterminal.BoolAdd.ordinal()] = State.Paren2;
        goToTable[State.Call2.ordinal()][Nonterminal.BoolAdd.ordinal()] = State.ArgList1;
        goToTable[State.Call2.ordinal()][Nonterminal.ArgList.ordinal()] = State.Call3;
        goToTable[State.Call2.ordinal()][Nonterminal.ArgListNonEmpty.ordinal()] = State.ArgList1;
        goToTable[State.ArgList2.ordinal()][Nonterminal.BoolAdd.ordinal()] = State.ArgList3;
        goToTable[State.Formula0.ordinal()][Nonterminal.BoolAdd.ordinal()] = State.Formula1;
    }

    public Formula parse(String input, String sheetName) {
        stack.empty();
        stack.push(State.Formula0);
        Lexer lexer = new Lexer(input);
        while (true) {
            State currentState = (State)stack.peek();
            Token token = lexer.getToken();
            Action action = actionTable[currentState.ordinal()][token.type.ordinal()];
            if (action == null) {
                throw new ParseErrorException("Could not parse the formula");
            } else {
                switch (action.type) {
                    case shift -> {
                        stack.push(token);
                        stack.push(action.state);
                    }
                    case reduce -> {
                        stack.pop();
                        if (action.tokenNumber == 1
                                && action.nonterminal != Nonterminal.Lit
                                && action.nonterminal != Nonterminal.SheetCell
                                && action.nonterminal != Nonterminal.ArgList) {
                            Object temp = stack.pop();
                            currentState = (State)stack.peek();
                            stack.push(temp);
                        } else {
                            switch (action.nonterminal) {
                                case BoolAdd, BoolMul, Comp, NumAdd, NumMul, NumPow -> {
                                    BinaryExpression exp = new BinaryExpression();
                                    exp.right = (Expression)stack.pop();
                                    stack.pop();
                                    exp.operator = switch (((Token)stack.pop()).type) {
                                        case EQ -> BinaryOperator.EQ;
                                        case NEQ -> BinaryOperator.NEQ;
                                        case LE -> BinaryOperator.LE;
                                        case LT -> BinaryOperator.LT;
                                        case GE -> BinaryOperator.GE;
                                        case GT -> BinaryOperator.GT;
                                        case OR -> BinaryOperator.OR;
                                        case AND -> BinaryOperator.AND;
                                        case PLUS -> BinaryOperator.PLUS;
                                        case MINUS -> BinaryOperator.MINUS;
                                        case MUL -> BinaryOperator.MUL;
                                        case DIV -> BinaryOperator.DIV;
                                        case POW -> BinaryOperator.POW;
                                        default -> throw new InternalError("Unexpected token on stack");
                                    };
                                    stack.pop();
                                    exp.left = (Expression)stack.pop();
                                    currentState = (State)stack.peek();
                                    stack.push(exp);
                                }
                                case NumUn, BoolUn -> {
                                    UnaryExpression unExp = new UnaryExpression();
                                    unExp.exp = (Expression)stack.pop();
                                    stack.pop();
                                    unExp.operator = ((Token)stack.pop()).type == TokenType.PLUS ?
                                            UnaryOperator.Plus :
                                            UnaryOperator.Minus;
                                    currentState = (State)stack.peek();
                                    stack.push(unExp);
                                }
                                case FunctionCall -> {
                                    FunctionCall call = new FunctionCall();
                                    stack.pop();
                                    stack.pop();
                                    call.argumentList = (List<Expression>)stack.pop();
                                    stack.pop();
                                    stack.pop();
                                    stack.pop();
                                    call.functionName = ((Token)stack.pop()).value;
                                    currentState = (State)stack.peek();
                                    stack.push(call);
                                }
                                case ArgList -> {
                                    switch (action.tokenNumber) {
                                        case 0 -> {
                                            stack.push(currentState);
                                            stack.push(new ArrayList<Expression>());
                                        }
                                        case 1 -> {
                                            Object value = stack.pop();
                                            currentState = (State)stack.peek();
                                            if (value instanceof List<?>) {
                                                stack.push(value);
                                            } else {
                                                stack.push(List.of((Expression)value));
                                            }
                                        }
                                        case 3 -> {
                                            Object value2 = stack.pop();
                                            stack.pop();
                                            stack.pop();
                                            stack.pop();
                                            Object value1 = stack.pop();
                                            currentState = (State)stack.peek();
                                            if (value1 instanceof List<?>) {
                                                ((List<Expression>) value1).add((Expression)value2);
                                                stack.push(value1);
                                            } else {
                                                List<Expression> argList = new ArrayList<>();
                                                argList.add((Expression)value1);
                                                argList.add((Expression)value2);
                                                stack.push(argList);
                                            }
                                        }
                                    }
                                }
                                case ArgListNonEmpty -> {
                                    Object value2 = stack.pop();
                                    stack.pop();
                                    stack.pop();
                                    stack.pop();
                                    Object value1 = stack.pop();
                                    currentState = (State)stack.peek();
                                    if (value1 instanceof List<?>) {
                                        ((List<Expression>) value1).add((Expression)value2);
                                        stack.push(value1);
                                    } else {
                                        List<Expression> argList = new ArrayList<>();
                                        argList.add((Expression)value1);
                                        argList.add((Expression)value2);
                                        stack.push(argList);
                                    }
                                }
                                case Paren -> {
                                    ParenExpression exp = new ParenExpression();
                                    stack.pop();
                                    stack.pop();
                                    exp.exp = (Expression)stack.pop();
                                    stack.pop();
                                    stack.pop();
                                    currentState = (State)stack.peek();
                                    stack.push(exp);
                                }
                                case CellRange -> {
                                    BinaryExpression range = new BinaryExpression();
                                    range.right = (CellReference)stack.pop();
                                    stack.pop();
                                    range.operator = BinaryOperator.RANGE;
                                    stack.pop();
                                    stack.pop();
                                    range.left = (CellReference)stack.pop();
                                    currentState = (State)stack.peek();
                                    stack.push(range);
                                }
                                case SheetCell -> {
                                    String reference = ((Token)stack.pop()).value;
                                    CellReference ref;
                                    if (action.tokenNumber == 3) {
                                        stack.pop();
                                        stack.pop();
                                        stack.pop();
                                        ref = new CellReference(((Token)stack.pop()).value, reference);
                                    } else {
                                        ref = new CellReference(sheetName, reference);
                                    }
                                    currentState = (State)stack.peek();
                                    stack.push(ref);
                                }
                                case Lit -> {
                                    Token litToken = (Token)stack.pop();
                                    switch (litToken.type) {
                                        case STRING -> {
                                            StringLiteral lit = new StringLiteral();
                                            lit.value = litToken.value.substring(1, litToken.value.length() - 1);
                                            currentState = (State)stack.peek();
                                            stack.push(lit);
                                        }
                                        case INT -> {
                                            IntegerNumber num = new IntegerNumber();
                                            num.value = Integer.parseInt(litToken.value);
                                            currentState = (State)stack.peek();
                                            stack.push(num);
                                        }
                                        case DOUBLE -> {
                                            DoubleNumber num = new DoubleNumber();
                                            num.value = Double.parseDouble(litToken.value);
                                            currentState = (State)stack.peek();
                                            stack.push(num);
                                        }
                                        case BOOLEAN -> {
                                            BooleanValue b = new BooleanValue();
                                            b.value = Boolean.parseBoolean(litToken.value);
                                            currentState = (State)stack.peek();
                                            stack.push(b);
                                        }
                                    }
                                }
                            }
                        }
                        lexer.putToken(token);
                        stack.push(goToTable[currentState.ordinal()][action.nonterminal.ordinal()]);
                    }
                    case accept -> {
                        Formula formula = new Formula();
                        stack.pop();
                        formula.exp = (Expression)stack.pop();
                        return formula;
                    }
                }
            }
        }
    }
}
