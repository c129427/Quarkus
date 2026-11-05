package com.giba.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.math.BigDecimal;

@Entity
@Table(name = "rendas", uniqueConstraints = @UniqueConstraint(
        name = "uk_renda_pessoa_mes_ano", columnNames = {"pessoa_id", "mes", "ano"}))
public class Renda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "pessoa_id", nullable = false)
    public Pessoa pessoa;

    @Column(nullable = false)
    public Integer mes;

    @Column(nullable = false)
    public Integer ano;

    @Column(nullable = false, precision = 14, scale = 2)
    public BigDecimal valor;
}
