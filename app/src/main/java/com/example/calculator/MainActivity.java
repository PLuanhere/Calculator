package com.example.calculator;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Stack;

public class MainActivity extends AppCompatActivity {

    EditText input, output;
    StringBuilder expression = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        input = findViewById(R.id.input);
        output = findViewById(R.id.output);

        int[] numberButtonIds = {
                R.id.button_0, R.id.button_1, R.id.button_2, R.id.button_3,
                R.id.button_4, R.id.button_5, R.id.button_6,
                R.id.button_7, R.id.button_8, R.id.button_9
        };

        int[] operatorButtonIds = {
                R.id.button_plus, R.id.button_sub,
                R.id.button_multi, R.id.button_divide
        };

        String[] operatorValues = {"+", "-", "*", "/"};

        // Gán các nút số
        for (int i = 0; i < numberButtonIds.length; i++) {
            final String num = String.valueOf(i);
            Button btn = findViewById(numberButtonIds[i]);
            btn.setOnClickListener(v -> {
                expression.append(num);
                input.setText(expression.toString());
            });
        }

        // Gán các nút toán tử
        for (int i = 0; i < operatorButtonIds.length; i++) {
            final String op = operatorValues[i];
            Button btn = findViewById(operatorButtonIds[i]);
            btn.setOnClickListener(v -> {
                expression.append(op);
                input.setText(expression.toString());
            });
        }

        // Nút =
        Button equal = findViewById(R.id.button_equal);
        equal.setOnClickListener(v -> {
            try {
                double result = evaluateExpression(expression.toString());
                if (result == (int) result) {
                    output.setText(String.valueOf((int) result));
                } else {
                    output.setText(String.valueOf(result));
                }

            } catch (Exception e) {
                output.setText("Error");
            }
        });

        // Nút C - xóa 1 ký tự
        Button btnC = findViewById(R.id.button_c);
        btnC.setOnClickListener(v -> {
            int len = expression.length();
            if (len > 0) {
                expression.deleteCharAt(len - 1);
                input.setText(expression.toString());
            }
        });

        // Nút AC - xóa hết
        Button btnAC = findViewById(R.id.button_ac);
        btnAC.setOnClickListener(v -> {
            expression.setLength(0);
            input.setText("");
            output.setText("");
        });
    }

    // Tính biểu thức (infix → postfix → tính)
    private double evaluateExpression(String expr) {
        return evaluatePostfix(toPostfix(expr));
    }

    private String toPostfix(String expr) {
        StringBuilder output = new StringBuilder();
        Stack<Character> stack = new Stack<>();

        for (int i = 0; i < expr.length(); i++) {
            char ch = expr.charAt(i);

            if (Character.isDigit(ch)) {
                while (i < expr.length() && Character.isDigit(expr.charAt(i))) {
                    output.append(expr.charAt(i));
                    i++;
                }
                output.append(' ');
                i--;
            } else if (isOperator(ch)) {
                while (!stack.isEmpty() && precedence(stack.peek()) >= precedence(ch)) {
                    output.append(stack.pop()).append(' ');
                }
                stack.push(ch);
            }
        }

        while (!stack.isEmpty()) {
            output.append(stack.pop()).append(' ');
        }

        return output.toString().trim();
    }

    private double evaluatePostfix(String postfix) {
        Stack<Double> stack = new Stack<>();
        String[] tokens = postfix.split(" ");

        for (String token : tokens) {
            if (token.matches("-?\\d+")) {
                stack.push(Double.parseDouble(token));
            } else if (isOperator(token.charAt(0))) {
                double b = stack.pop();
                double a = stack.pop();
                switch (token) {
                    case "+": stack.push(a + b); break;
                    case "-": stack.push(a - b); break;
                    case "*": stack.push(a * b); break;
                    case "/": stack.push(a / b); break;
                }
            }
        }

        return stack.pop();
    }

    private boolean isOperator(char ch) {
        return ch == '+' || ch == '-' || ch == '*' || ch == '/';
    }

    private int precedence(char op) {
        if (op == '+' || op == '-') return 1;
        if (op == '*' || op == '/') return 2;
        return -1;
    }
}
