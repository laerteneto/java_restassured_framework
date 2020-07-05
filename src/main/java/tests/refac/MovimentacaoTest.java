package tests.refac;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;

import java.util.HashMap;
import java.util.Map;

import org.hamcrest.Matchers;
import org.junit.Test;

import core.BaseTest;
import utils.BarrigaUtils;
import utils.DataUtils;

public class MovimentacaoTest extends BaseTest{
	
	@Test
	public void deveInserirMovimentacaoComSucesso() {
		Map<Object, Object> movimentacao = new HashMap<Object, Object>();
		movimentacao.put("conta_id", BarrigaUtils.getIdContaPeloNome("Conta para movimentacoes"));
		movimentacao.put("descricao", "Descricao da movimentacao");
		movimentacao.put("envolvido", "Envolvido na mov");
		movimentacao.put("tipo", "REC");
		movimentacao.put("data_transacao", DataUtils.getDataDiferencaDias(-1));
		movimentacao.put("data_pagamento", DataUtils.getDataDiferencaDias(5));
		movimentacao.put("valor", 100f);
		movimentacao.put("status", true);	
		
		given()
			.body(movimentacao)
		.when()
			.post("/transacoes")
		.then()
			.statusCode(201)
		;	
	}
	
	@Test
	public void deveValidarCamposObrigatoriosMovimentacao() {
		given()
			.body("{}")
		.when()
			.post("/transacoes")
		.then()
			.statusCode(400)
			.body("$", hasSize(8))
			.body("msg", Matchers.hasItems(
					"Data da Movimentação é obrigatório",
					"Data do pagamento é obrigatório",
					"Descrição é obrigatório",
					"Interessado é obrigatório",
					"Valor é obrigatório",
					"Valor deve ser um número",
					"Conta é obrigatório",
					"Situação é obrigatório"
			))
		;
	}
	
	@Test
	public void naoDeveInserirMovimentacaoComDataFutura() {
		Map<Object, Object> movimentacao = new HashMap<Object, Object>();
		movimentacao.put("conta_id", BarrigaUtils.getIdContaPeloNome("Conta para movimentacoes"));
		movimentacao.put("descricao", "Descricao da movimentacao");
		movimentacao.put("envolvido", "Envolvido na mov");
		movimentacao.put("tipo", "REC");
		movimentacao.put("data_transacao", DataUtils.getDataDiferencaDias(2));
		movimentacao.put("data_pagamento", DataUtils.getDataDiferencaDias(5));
		movimentacao.put("valor", 100f);
		movimentacao.put("status", true);	
		
		given()
			.body(movimentacao)
		.when()
			.post("/transacoes")
		.then()
			.body("msg", hasItem("Data da Movimentação deve ser menor ou igual à data atual"))
			.statusCode(400)
			.body("$", hasSize(1))
		;	
	}
	
	@Test
	public void naoDeveRemoverContaComMovimentacao() {		
		Integer CONTA_ID = BarrigaUtils.getIdContaPeloNome("Conta com movimentacao");
		
		given()
			.pathParam("id", CONTA_ID)
		.when()
			.delete("/contas/{id}")
		.then()
			.statusCode(500)
		;	
	}
	
	@Test
	public void deveRemoverMovimentacao() {
		Integer MOV_ID = BarrigaUtils.getIdMovimentacaoPelaDescricao("Movimentacao para exclusao");
		given()
			.pathParam("id", MOV_ID)
		.when()
			.delete("/transacoes/{id}")
		.then()
			.statusCode(204)
		;	
	}
	
}
