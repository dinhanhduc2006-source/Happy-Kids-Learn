package com.example.happykidlearn.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.happykidlearn.R;

import java.util.Random;

public class ParentalGateDialog {

    private final Context context;
    private final Runnable onSuccess;
    private Dialog dialog;
    private int expectedAnswer;

    public ParentalGateDialog(Context context, Runnable onSuccess) {
        this.context = context;
        this.onSuccess = onSuccess;
    }

    public void show() {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_parental_gate);
        dialog.setCancelable(true);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        TextView tvQuestion = dialog.findViewById(R.id.tv_gate_question);
        EditText etAnswer = dialog.findViewById(R.id.et_gate_answer);
        Button btnCancel = dialog.findViewById(R.id.btn_gate_cancel);
        Button btnConfirm = dialog.findViewById(R.id.btn_gate_confirm);

        generateQuestion(tvQuestion);

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnConfirm.setOnClickListener(v -> {
            String input = etAnswer.getText().toString().trim();
            if (input.isEmpty()) {
                Toast.makeText(context, "Vui lòng nhập kết quả!", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                int userAnswer = Integer.parseInt(input);
                if (userAnswer == expectedAnswer) {
                    dialog.dismiss();
                    if (onSuccess != null) {
                        onSuccess.run();
                    }
                } else {
                    Toast.makeText(context, "Kết quả chưa đúng, vui lòng thử lại!", Toast.LENGTH_SHORT).show();
                    etAnswer.setText("");
                    generateQuestion(tvQuestion);
                }
            } catch (NumberFormatException e) {
                Toast.makeText(context, "Vui lòng nhập chữ số!", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    private void generateQuestion(TextView tvQuestion) {
        Random random = new Random();
        int num1 = random.nextInt(9) + 1;
        int num2 = random.nextInt(9) + 1;

        if (random.nextBoolean()) {
            expectedAnswer = num1 + num2;
            tvQuestion.setText(numberToText(num1) + " cộng " + numberToText(num2) + " bằng mấy?");
        } else {
            if (num1 < num2) {
                int temp = num1;
                num1 = num2;
                num2 = temp;
            }
            expectedAnswer = num1 - num2;
            tvQuestion.setText(numberToText(num1) + " trừ " + numberToText(num2) + " bằng mấy?");
        }
    }

    private String numberToText(int number) {
        String[] words = {"không", "một", "hai", "ba", "bốn", "năm", "sáu", "bảy", "tám", "chín", "mười"};
        if (number >= 0 && number <= 10) {
            return words[number];
        }
        return String.valueOf(number);
    }
}