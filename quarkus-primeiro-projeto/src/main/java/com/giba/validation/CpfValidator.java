package com.giba.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CpfValidator implements ConstraintValidator<CpfValido, CharSequence> {

    @Override
    public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        return validar(value.toString());
    }

    public static boolean validar(String value) {
        String cpf;
        try {
            cpf = normalizar(value.toString());
        } catch (IllegalArgumentException exception) {
            return false;
        }
        if (cpf.length() != 11 || cpf.chars().distinct().count() == 1) {
            return false;
        }
        int primeiro = calcularDigito(cpf, 9);
        int segundo = calcularDigito(cpf, 10);
        return cpf.charAt(9) - '0' == primeiro && cpf.charAt(10) - '0' == segundo;
    }

    public static String normalizar(String value) {
        String cpf = value.replaceAll("[.\\-]", "");
        if (!cpf.matches("\\d{11}")) {
            throw new IllegalArgumentException("CPF deve conter 11 digitos");
        }
        return cpf;
    }

    private static int calcularDigito(String cpf, int length) {
        int soma = 0;
        for (int i = 0; i < length; i++) {
            soma += (cpf.charAt(i) - '0') * (length + 1 - i);
        }
        int resto = soma % 11;
        return resto < 2 ? 0 : 11 - resto;
    }
}
