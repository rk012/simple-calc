package main;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.math.BigDecimal;
import java.math.MathContext;

public class Controller {
    @FXML
    private Label display_label;

    private int mode = 0; // 0: None, 1: Add, 2: Sub, 3: Mult, 4: Div, 5: Exp
    private BigDecimal lastValue = null;
    private BigDecimal currentValue = new BigDecimal(0);
    private boolean isDec = false;
    private double decPlaces = 1;
    private boolean modify = true;

    public void onBtnNum(ActionEvent event) {
        if (!modify) {
            mode = 0;
            lastValue = null;
            currentValue = new BigDecimal(0);
            isDec = false;
            decPlaces = 1;
            display_label.setText(currentValue.toPlainString());
            modify = true;
        }
        BigDecimal num = new BigDecimal(((Button) event.getSource()).getText());
        if (isDec) {
            currentValue = currentValue.add(num.multiply(BigDecimal.valueOf(Math.pow(10, -1 * decPlaces)))).stripTrailingZeros();
            decPlaces++;
        } else {
            currentValue = currentValue.multiply(new BigDecimal(10)).add(num).stripTrailingZeros();
        }
        display_label.setText(currentValue.toPlainString());
    }

    public void onBtnOp(ActionEvent event) {  // +, -, ×, ÷, ^
        nextLine(false);
        mode = switch (((Button) event.getSource()).getText()) {
            case "+" -> 1;
            case "-" -> 2;
            case "×" -> 3;
            case "÷" -> 4;
            case "^" -> 5;
            default -> 0;
        };
    }

    public void onBtnAction(ActionEvent event) {  // =, C, CE, ., √
        switch (((Button) event.getSource()).getText()) {
            case "." -> {
                isDec = true;
                display_label.setText(display_label.getText() + ".");
            }
            case "=" -> nextLine(true);
            case "C" -> {
                currentValue = new BigDecimal(0);
                isDec = false;
                decPlaces = 1;
                display_label.setText(currentValue.toPlainString());
            } case "CE" -> {
                mode = 0;
                lastValue = null;
                currentValue = new BigDecimal(0);
                isDec = false;
                decPlaces = 1;
                display_label.setText(currentValue.toPlainString());
            } case "√" -> {
                currentValue = currentValue.sqrt(new MathContext(6)).stripTrailingZeros();
                display_label.setText(currentValue.toPlainString());
            }
        }
    }

    private void nextLine(boolean keepResult) {
        try {
            BigDecimal result = eval();
            if (!keepResult) {
                lastValue = result;
                currentValue = new BigDecimal(0);
            } else {
                lastValue = null;
                currentValue = result;
                modify = false;
            }
            display_label.setText(result.toPlainString());

            isDec = false;
            decPlaces = 1;
            mode = 0;
        } catch (ArithmeticException e) {
            lastValue = null;
            currentValue = new BigDecimal(0);
            display_label.setText("E");

            isDec = false;
            decPlaces = 1;
            mode = 0;
        }
    }

    private BigDecimal eval() {
        if (lastValue == null) {
            return currentValue;
        }
        return switch (mode) {
            case 1 -> lastValue.add(currentValue, new MathContext(6)).stripTrailingZeros();
            case 2 -> lastValue.subtract(currentValue, new MathContext(6)).stripTrailingZeros();
            case 3 -> lastValue.multiply(currentValue, new MathContext(6)).stripTrailingZeros();
            case 4 -> lastValue.divide(currentValue, new MathContext(6)).stripTrailingZeros();
            case 5 -> lastValue.pow(currentValue.intValue(), new MathContext(6)).stripTrailingZeros();
            default -> currentValue;
        };
    }
}
