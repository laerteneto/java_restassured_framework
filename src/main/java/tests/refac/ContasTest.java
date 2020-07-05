package tests.refac;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import org.junit.Test;

import core.BaseTest;
import utils.BarrigaUtils;

public class ContasTest extends BaseTest{
		
	@Test
	public void deveIncluirContaComSucesso() {
		given()
			.body("{\"nome\": \"conta qualquer\"}")
		.when()
			.post("/contas")
		.then()
			.statusCode(201)
		;
	}
	
	@Test
	public void deveAlterarContaComSucesso() {
		Integer CONTA_ID = BarrigaUtils.getIdContaPeloNome("Conta para alterar");
		
		given()
			.body("{\"nome\": \"conta alterada\"}")
			.pathParam("id", CONTA_ID)
		.when()
			.put("/contas/{id}")
		.then()
			.statusCode(200)
			.body("nome", is("conta alterada"))
		;
		
	}
	
	@Test
	public void naoDeveInserirContaComMesmoNome() {
		given()
			.body("{\"nome\": \"Conta mesmo nome\"}")
		.when()
			.post("/contas")
		.then()
			.statusCode(400)
			.body("error", is("Já existe uma conta com esse nome!"))
		;
		
	}
	
}
