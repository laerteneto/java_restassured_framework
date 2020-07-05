package tests.refac.suite;

import static io.restassured.RestAssured.given;

import java.util.HashMap;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

import core.BaseTest;
import io.restassured.RestAssured;
import tests.refac.AuthTest;
import tests.refac.ContasTest;
import tests.refac.MovimentacaoTest;
import tests.refac.SaldosTest;

@RunWith(org.junit.runners.Suite.class)
@SuiteClasses({
	ContasTest.class,
	MovimentacaoTest.class,
	SaldosTest.class,
	AuthTest.class
})
public class Suite extends BaseTest{
	
	@BeforeClass
	public static void login() {
		Map<String, String> login = new HashMap<String, String>();
		login.put("email", "laerteteste@hotmail.com");
		login.put("senha", "123456");
		
		String TOKEN = given()
			.body(login)
		.when()
			.post("/signin")
		.then()
			.statusCode(200)
			.extract().path("token");
		;
		
		RestAssured.requestSpecification.header("Authorization", "JWT " + TOKEN);
		
		// Reset o banco para uma massa inicialmente conhecida
		RestAssured.get("/reset").then().statusCode(200);
	}
	
}
