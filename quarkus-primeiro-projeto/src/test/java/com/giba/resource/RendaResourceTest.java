package com.giba.resource;

import com.giba.dto.RendaRequest;
import com.giba.model.Renda;
import com.giba.service.RendaService;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RendaResourceTest {

    private RendaResource resource;
    private RendaService service;

    @BeforeEach
    void setUp() {
        resource = new RendaResource();
        service = mock(RendaService.class);
        resource.service = service;
    }

    @Test
    void criaRendaComRespostaHttp201() {
        String cpf = "52998224725";
        RendaRequest request = new RendaRequest(5, 2025, new BigDecimal("12000.00"));
        Renda renda = new Renda();
        renda.id = 1L;
        renda.mes = request.mes();
        renda.ano = request.ano();
        renda.valor = request.valor();
        when(service.criar(cpf, request)).thenReturn(renda);

        try (Response response = resource.criar(cpf, request)) {
            assertEquals(201, response.getStatus());
            assertEquals(5, assertInstanceOf(com.giba.dto.RendaResponse.class,
                    response.getEntity()).mes());
        }
        verify(service).criar(cpf, request);
    }
}
