package com.giba.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "enderecos")
public class Endereco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(nullable = false, length = 120)
    public String logradouro;

    @Column(nullable = false, length = 20)
    public String numero;

    @Column(length = 100)
    public String complemento;

    @Column(nullable = false, length = 80)
    public String bairro;

    @Column(nullable = false, length = 80)
    public String cidade;

    @Column(nullable = false, length = 2)
    public String estado;

    @Column(nullable = false, length = 8)
    public String cep;
}
