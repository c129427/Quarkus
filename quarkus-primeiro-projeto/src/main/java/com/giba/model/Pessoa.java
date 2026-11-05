package com.giba.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.LocalDate;

@Entity
@Table(name = "pessoas", uniqueConstraints = @UniqueConstraint(name = "uk_pessoa_cpf", columnNames = "cpf"))
public class Pessoa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(nullable = false, length = 11)
    public String cpf;

    @Column(nullable = false, length = 150)
    public String nome;

    @Column(name = "data_nascimento", nullable = false)
    public LocalDate dataNascimento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    public SituacaoPessoa situacao;

    @OneToOne(cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "endereco_id", unique = true)
    public Endereco endereco;
}
