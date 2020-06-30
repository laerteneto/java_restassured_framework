package tests;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import java.util.HashMap;
import java.util.Map;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

import core.BaseTest;

public class BarrigaTest extends BaseTest{
	
	private String TOKEN;
	
	@Before
	public void login() {
		Map<String, String> login = new HashMap<String, String>();
		login.put("email", "laerteteste@hotmail.com");
		login.put("senha", "123456");
		
		TOKEN = given()
			.body(login)
		.when()
			.post("/signin")
		.then()
			.statusCode(200)
			.extract().path("token");
		;
	}
	
	@Test
	public void naoDeveAcessarAPISemToken() {
		given()
		.when()
			.get("/contas")
		.then()
			.statusCode(401)
		;
	}
	
	@Test
	public void deveIncluirContaComSucesso() {
		given()
			.header("Authorization", "JWT " + TOKEN)
			.body("{\"nome\": \"conta qualquer\"}")
		.when()
			.post("/contas")
		.then()
			.statusCode(201)
		;	
	}
	
	@Test
	public void deveAlterarContaComSucesso() {
		given()
			.header("Authorization", "JWT " + TOKEN)
			.body("{\"nome\": \"conta alterada\"}")
		.when()
			.put("/contas/199373")
		.then()
			.statusCode(200)
			.body("nome", is("conta alterada"))
		;
		
	}
	
	@Test
	public void naoDeveInserirContaComMesmoNome() {
		given()
			.header("Authorization", "JWT " + TOKEN)
			.body("{\"nome\": \"conta alterada\"}")
		.when()
			.post("/contas")
		.then()
			.statusCode(400)
			.body("error", is("Já existe uma conta com esse nome!"))
		;
		
	}
	
	@Test
	public void deveInserirMovimentacaoComSucesso() {
		Map<Object, Object> movimentacao = new HashMap<Object, Object>();
		movimentacao.put("conta_id", "199373");
		movimentacao.put("descricao", "Descricao da movimentacao");
		movimentacao.put("envolvido", "Envolvido na mov");
		movimentacao.put("tipo", "REC");
		movimentacao.put("data_transacao", "01/01/2000");
		movimentacao.put("data_pagamento", "10/05/2010");
		movimentacao.put("valor", 100f);
		movimentacao.put("status", true);	
		
		given()
			.header("Authorization", "JWT " + TOKEN)
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
			.header("Authorization", "JWT " + TOKEN)
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
		movimentacao.put("conta_id", "199373");
		movimentacao.put("descricao", "Descricao da movimentacao");
		movimentacao.put("envolvido", "Envolvido na mov");
		movimentacao.put("tipo", "REC");
		movimentacao.put("data_transacao", "01/01/2030");
		movimentacao.put("data_pagamento", "10/05/2010");
		movimentacao.put("valor", 100f);
		movimentacao.put("status", true);	
		
		given()
			.header("Authorization", "JWT " + TOKEN)
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
		given()
			.header("Authorization", "JWT " + TOKEN)
		.when()
			.delete("/contas/199373")
		.then()
			.statusCode(500)
		;	
	}
	
	@Test
	public void deveCalcularSaldoContas() {		
		given()
			.header("Authorization", "JWT " + TOKEN)
		.when()
			.get("/saldo")
		.then()
			.statusCode(200)
			.body("find{it.conta_id == 199373}.saldo", is("100.00"))
		;	
	}
	
	@Test
	public void deveRemoverMovimentacao() {		
		given()
			.header("Authorization", "JWT " + TOKEN)
		.when()
			.delete("/transacoes/177345")
		.then()
			.statusCode(204)
		;	
	}

}

