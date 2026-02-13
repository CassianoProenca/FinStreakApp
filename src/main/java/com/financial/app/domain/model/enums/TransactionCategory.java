package com.financial.app.domain.model.enums;

public enum TransactionCategory {
    // Despesas
    FOOD,           // Alimentação, iFood, Mercado
    TRANSPORT,      // Uber, Gasolina, Ônibus
    HOUSING,        // Aluguel, Condomínio, Luz
    UTILITIES,      // Internet, Celular, TV
    LEISURE,        // Cinema, Jogos, Passeios
    EDUCATION,      // Faculdades, Cursos, Livros
    HEALTH,         // Farmácia, Convênio
    SHOPPING,       // Roupas, Eletrônicos

    // Receitas
    SALARY,         // Salário Mensal
    FREELANCE,      // Jobs extras
    INVESTMENT,     // Rendimentos, Dividendos

    // Outros
    OTHER           // O que não se encaixa acima
}
