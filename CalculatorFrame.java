import java.awt.*;
import java.awt.event.*;

public class CalculatorFrame extends Frame {
    private TextField display;

    public CalculatorFrame() {
        setTitle("Calculator");
        setSize(500, 700);
        setBackground(new Color(0, 20, 40)); // Set background color to blackish blue
	setResizable(false); 

        setLayout(new BorderLayout());

        display = new TextField();
        display.setEditable(false);
        display.setFont(new Font("Arial", Font.PLAIN, 20)); // Increase font size
	display.setPreferredSize(new Dimension(10, 50)); // Increase TextField size
        display.setBackground(new Color(0, 20, 40)); // Set background color to blackish blue
        display.setForeground(Color.WHITE); // Set text color to white
        add(display, BorderLayout.NORTH);

        Panel buttonPanel = new Panel(new GridLayout(6, 4));
        buttonPanel.setBackground(new Color(0, 20, 40)); // Set background color to blackish blue

        String[] buttonLabels = {
            "7", "8", "9", "/",
            "4", "5", "6", "*",
            "1", "2", "3", "-",
            "0", ".", "=", "+",
            "(", ")", "C", "<-", // Added backspace button
            "x^2", "sqrt", "x^3", "cbrt" // Added square, square root, cube, and cube root buttons
        };

        for (String label : buttonLabels) {
            Button button = new Button(label);
            button.addActionListener(new ButtonClickListener());
            button.setFont(new Font("Arial", Font.PLAIN, 18)); // Increase button font size
            button.setBackground(new Color(0, 20, 40)); // Set background color to blackish blue
            button.setForeground(Color.WHITE); // Set text color to white
            buttonPanel.add(button);
        }

        add(buttonPanel, BorderLayout.CENTER);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent) {
                System.exit(0);
            }
        });
    }

    private class ButtonClickListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            if (command.equals("=")) {
                calculate();
            } else if (command.equals("C")) {
                clearAll();
            } else if (command.equals("<-")) {
                backspace();
            } else if (command.equals("x^2")) {
                square();
            } else if (command.equals("sqrt")) {
                squareRoot();
            } else if (command.equals("x^3")) {
                cube();
            } else if (command.equals("cbrt")) {
                cubeRoot();
            } else if (command.equals("(") || command.equals(")")) {
                display.setText(display.getText() + command);
            } else {
                display.setText(display.getText() + command);
            }
        }

        private void calculate() {
            String expression = display.getText();
            try {
                double result = evaluate(expression);
                display.setText(Double.toString(result));
            } catch (Exception ex) {
                display.setText("Error");
            }
        }

        private void clearAll() {
            display.setText("");
        }

        private void backspace() {
            String text = display.getText();
            if (!text.isEmpty()) {
                display.setText(text.substring(0, text.length() - 1));
            }
        }

        private void square() {
            String text = display.getText();
            if (!text.isEmpty()) {
                double value = Double.parseDouble(text);
                display.setText(Double.toString(value * value));
            }
        }

        private void squareRoot() {
            String text = display.getText();
            if (!text.isEmpty()) {
                double value = Double.parseDouble(text);
                display.setText(Double.toString(Math.sqrt(value)));
            }
        }

        private void cube() {
            String text = display.getText();
            if (!text.isEmpty()) {
                double value = Double.parseDouble(text);
                display.setText(Double.toString(value * value * value));
            }
        }

        private void cubeRoot() {
            String text = display.getText();
            if (!text.isEmpty()) {
                double value = Double.parseDouble(text);
                display.setText(Double.toString(Math.cbrt(value)));
            }
        }

        private double evaluate(String expression) {
            return new Object() {
                int pos = -1, ch;

                void nextChar() {
                    ch = (++pos < expression.length()) ? expression.charAt(pos) : -1;
                }

                boolean eat(int charToEat) {
                    while (ch == ' ') nextChar();
                    if (ch == charToEat) {
                        nextChar();
                        return true;
                    }
                    return false;
                }

                double parse() {
                    nextChar();
                    double x = parseExpression();
                    if (pos < expression.length()) throw new RuntimeException("Unexpected: " + (char) ch);
                    return x;
                }

                double parseExpression() {
                    double x = parseTerm();
                    for (;;) {
                        if (eat('+')) x += parseTerm();
                        else if (eat('-')) x -= parseTerm();
                        else return x;
                    }
                }

                double parseTerm() {
                    double x = parseFactor();
                    for (;;) {
                        if (eat('*')) x *= parseFactor();
                        else if (eat('/')) x /= parseFactor();
                        else return x;
                    }
                }

                double parseFactor() {
                    if (eat('+')) return parseFactor();
                    if (eat('-')) return -parseFactor();

                    double x;
                    int startPos = this.pos;
                    if (eat('(')) {
                        x = parseExpression();
                        eat(')');
                    } else if ((ch >= '0' && ch <= '9') || ch == '.') {
                        while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                        x = Double.parseDouble(expression.substring(startPos, this.pos));
                    } else {
                        throw new RuntimeException("Unexpected: " + (char) ch);
                    }

                    if (eat('^')) x = Math.pow(x, parseFactor());

                    return x;
                }
            }.parse();
        }
    }

    public static void main(String[] args) {
        CalculatorFrame calculator = new CalculatorFrame();
        calculator.setVisible(true);
    }
}
