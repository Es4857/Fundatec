package com.fundatec.notas;

import android.app.Activity;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Locale;

public class MainActivity extends Activity {

    private static final Locale PT_BR = Locale.forLanguageTag("pt-BR");
    private static final double TRABALHO_MAX = 4.0;
    private static final double PESO_ATIVIDADES = 0.70;
    private static final double PESO_FORMATIVA = 0.30;

    private static final int COR_NEUTRA = 0xFFE3E8F2;
    private static final String FORMULA =
            "((Prova + Trabalho) ÷ 2 × 0,70)\n+ (Formativa × 0,30)";

    private EditText editProva;
    private EditText editTrabalho;
    private EditText editFormativa;
    private TextView txtTrabalhoConvertido;
    private TextView txtConceito;
    private TextView txtNotaFinal;
    private TextView txtSituacao;
    private TextView txtDetalhes;
    private GradientDrawable fundoResultado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editProva = findViewById(R.id.editProva);
        editTrabalho = findViewById(R.id.editTrabalho);
        editFormativa = findViewById(R.id.editFormativa);
        txtTrabalhoConvertido = findViewById(R.id.txtTrabalhoConvertido);
        txtConceito = findViewById(R.id.txtConceito);
        txtNotaFinal = findViewById(R.id.txtNotaFinal);
        txtSituacao = findViewById(R.id.txtSituacao);
        txtDetalhes = findViewById(R.id.txtDetalhes);
        fundoResultado = (GradientDrawable) findViewById(R.id.cardResultado)
                .getBackground().mutate();

        TextWatcher recalcular = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                calcular();
            }
        };
        editProva.addTextChangedListener(recalcular);
        editTrabalho.addTextChangedListener(recalcular);
        editFormativa.addTextChangedListener(recalcular);
    }

    private void calcular() {
        Double prova = ler(editProva, 10.0);
        Double trabalho = ler(editTrabalho, TRABALHO_MAX);
        Double formativa = ler(editFormativa, 10.0);

        // Regra de três: o trabalho vale de 0 a 4, sendo 4 equivalente a 10.
        Double trabalhoConvertido = trabalho == null ? null : trabalho * 10.0 / TRABALHO_MAX;
        txtTrabalhoConvertido.setText(trabalhoConvertido == null
                ? "Convertido para 0 a 10: —"
                : String.format(PT_BR, "Convertido para 0 a 10: %.2f", trabalhoConvertido));

        if (prova == null || trabalhoConvertido == null || formativa == null) {
            mostrarVazio();
            return;
        }

        double mediaAtividades = (prova + trabalhoConvertido) / 2.0;
        double parteAtividades = mediaAtividades * PESO_ATIVIDADES;
        double parteFormativa = formativa * PESO_FORMATIVA;
        double notaFinal = parteAtividades + parteFormativa;

        String conceito;
        int cor;
        if (notaFinal >= 9.0) {
            conceito = "A";
            cor = 0xFF00B050;
        } else if (notaFinal >= 8.0) {
            conceito = "B";
            cor = 0xFFFFE600;
        } else if (notaFinal >= 6.0) {
            conceito = "C";
            cor = 0xFFFFC000;
        } else {
            conceito = "D";
            cor = 0xFFFF3B30;
        }
        boolean aprovado = !conceito.equals("D");
        // Texto branco no verde/vermelho, escuro no amarelo/laranja.
        int corTexto = (conceito.equals("A") || conceito.equals("D")) ? 0xFFFFFFFF : 0xFF22304A;

        fundoResultado.setColor(cor);
        txtConceito.setText(conceito);
        txtConceito.setTextColor(cor == 0xFFFFE600 ? 0xFF8A7A00 : cor);
        txtNotaFinal.setText(String.format(PT_BR, "%.2f", notaFinal));
        txtNotaFinal.setTextColor(corTexto);
        txtSituacao.setText(aprovado ? "✅ Aprovado (com 75% de frequência)" : "❌ Reprovado");
        txtSituacao.setTextColor(corTexto);
        ((TextView) findViewById(R.id.txtRotulo)).setTextColor(corTexto);

        txtDetalhes.setText(String.format(PT_BR,
                "Trabalho: %.2f × 10 ÷ 4 = %.2f\n"
                        + "Média:    (%.2f + %.2f) ÷ 2 = %.2f\n"
                        + "Ativ.:    %.2f × 0,70 = %.2f\n"
                        + "Format.:  %.2f × 0,30 = %.2f\n"
                        + "Final:    %.2f + %.2f = %.2f",
                trabalho, trabalhoConvertido,
                prova, trabalhoConvertido, mediaAtividades,
                mediaAtividades, parteAtividades,
                formativa, parteFormativa,
                parteAtividades, parteFormativa, notaFinal));
    }

    private void mostrarVazio() {
        fundoResultado.setColor(COR_NEUTRA);
        txtConceito.setText("?");
        txtConceito.setTextColor(0xFF5A6B85);
        txtNotaFinal.setText("—");
        txtNotaFinal.setTextColor(0xFF22304A);
        txtSituacao.setText("Preencha as três notas");
        txtSituacao.setTextColor(0xFF22304A);
        ((TextView) findViewById(R.id.txtRotulo)).setTextColor(0xFF5A6B85);
        txtDetalhes.setText(FORMULA);
    }

    /** Lê o campo aceitando vírgula ou ponto; devolve null se vazio ou inválido. */
    private Double ler(EditText campo, double max) {
        String texto = campo.getText().toString().trim().replace(',', '.');
        campo.setError(null);
        if (texto.isEmpty() || texto.equals(".")) {
            return null;
        }
        try {
            double valor = Double.parseDouble(texto);
            if (valor < 0 || valor > max) {
                campo.setError(String.format(PT_BR, "Use um valor entre 0 e %.0f", max));
                return null;
            }
            return valor;
        } catch (NumberFormatException e) {
            campo.setError("Número inválido");
            return null;
        }
    }
}
