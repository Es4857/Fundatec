package com.fundatec.notas;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.view.View;

import java.util.Locale;

public class MainActivity extends Activity {

    private static final double TRABALHO_MAX = 4.0;
    private static final double PESO_ATIVIDADES = 0.70;
    private static final double PESO_FORMATIVA = 0.30;

    private EditText editProva;
    private EditText editTrabalho;
    private EditText editFormativa;
    private TextView txtConceito;
    private TextView txtDetalhes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editProva = findViewById(R.id.editProva);
        editTrabalho = findViewById(R.id.editTrabalho);
        editFormativa = findViewById(R.id.editFormativa);
        txtConceito = findViewById(R.id.txtConceito);
        txtDetalhes = findViewById(R.id.txtDetalhes);
        Button btnCalcular = findViewById(R.id.btnCalcular);

        btnCalcular.setOnClickListener(v -> calcular());
    }

    private void calcular() {
        Double prova = ler(editProva, 10.0, "Prova");
        Double trabalho = ler(editTrabalho, TRABALHO_MAX, "Trabalho");
        Double formativa = ler(editFormativa, 10.0, "Formativa");
        if (prova == null || trabalho == null || formativa == null) {
            txtConceito.setVisibility(View.GONE);
            txtDetalhes.setText("");
            return;
        }

        // Regra de três: o trabalho vale de 0 a 4, sendo 4 equivalente a 10.
        double trabalhoConvertido = trabalho * 10.0 / TRABALHO_MAX;
        double mediaAtividades = (prova + trabalhoConvertido) / 2.0;
        double notaFinal = mediaAtividades * PESO_ATIVIDADES + formativa * PESO_FORMATIVA;

        String conceito;
        int cor;
        if (notaFinal >= 9.0) {
            conceito = "A";
            cor = 0xFF00B050;
        } else if (notaFinal >= 8.0) {
            conceito = "B";
            cor = 0xFFFFFF00;
        } else if (notaFinal >= 6.0) {
            conceito = "C";
            cor = 0xFFFFC000;
        } else {
            conceito = "D";
            cor = 0xFFFF0000;
        }
        boolean aprovado = !conceito.equals("D");

        txtConceito.setVisibility(View.VISIBLE);
        txtConceito.setBackgroundColor(cor);
        txtConceito.setText(String.format(Locale.forLanguageTag("pt-BR"),
                "Nota final: %.2f\nConceito %s — %s",
                notaFinal, conceito, aprovado ? "Aprovado*" : "Reprovado"));

        txtDetalhes.setText(String.format(Locale.forLanguageTag("pt-BR"),
                "Trabalho convertido: %.2f × 10 / 4 = %.2f\n"
                        + "Média das atividades: (%.2f + %.2f) / 2 = %.2f\n"
                        + "Atividades × 0,70 = %.2f\n"
                        + "Formativa × 0,30 = %.2f\n"
                        + "Nota final = %.2f\n\n"
                        + "* Aprovação também exige 75%% de frequência.",
                trabalho, trabalhoConvertido,
                prova, trabalhoConvertido, mediaAtividades,
                mediaAtividades * PESO_ATIVIDADES,
                formativa * PESO_FORMATIVA,
                notaFinal));
    }

    private Double ler(EditText campo, double max, String nome) {
        String texto = campo.getText().toString().trim().replace(',', '.');
        if (texto.isEmpty()) {
            campo.setError("Informe a nota de " + nome);
            return null;
        }
        try {
            double valor = Double.parseDouble(texto);
            if (valor < 0 || valor > max) {
                campo.setError(String.format(Locale.forLanguageTag("pt-BR"),
                        "Valor deve estar entre 0 e %.0f", max));
                return null;
            }
            return valor;
        } catch (NumberFormatException e) {
            campo.setError("Número inválido");
            return null;
        }
    }
}
