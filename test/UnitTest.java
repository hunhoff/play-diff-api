import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static play.mvc.Http.Status.NOT_FOUND;
import static play.mvc.Http.Status.OK;
import static play.mvc.Http.Status.BAD_REQUEST;
import static play.test.Helpers.GET;
import static play.test.Helpers.contentAsString;
import static play.test.Helpers.fakeRequest;
import static play.test.Helpers.route;

import javax.validation.constraints.AssertFalse;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import controllers.HomeController;
import play.libs.Json;
import play.mvc.Http.RequestBuilder;
import play.mvc.Result;
import play.test.Helpers;
import play.test.WithApplication;
import services.DiffTools;

public class UnitTest extends WithApplication {

	private ObjectNode jsonInput0 = Json.newObject();
	private ObjectNode jsonInput1 = Json.newObject();
	
	/**
	 * Testing same string, list containing differences should be zero
	 */
	@Test
	public void testSameString() {
		DiffTools diffTools = new DiffTools();
		assertEquals(0,diffTools.diffString("00000100000", "00000100000").size());
	}

	/**
	 * Testing same size and different string, list containing differences should be size one (String beginning)
	 */
	@Test
	public void testSameSizeDifferentStringAtBegin() {
		DiffTools diffTools = new DiffTools();
		assertEquals(1,diffTools.diffString("10000100000", "00000100000").size());
		assertEquals("[Offset: 0 & Lenght: 1]", diffTools.diffString("10000100000", "00000100000").toString());
	}
	
	/**
	 * Testing same size and different string, list containing differences should be size one (String end)
	 */
	@Test
	public void testSameSizeDifferentStringAtEnd() {
		DiffTools diffTools = new DiffTools();
		assertEquals(1,diffTools.diffString("00000100000", "00000100001").size());
		assertEquals("[Offset: 10 & Lenght: 1]", diffTools.diffString("00000100000", "00000100001").toString());
	}
	
	/**
	 * Testing same size and different string, list containing differences should be size one (multiple differences)
	 */
	@Test
	public void testSameSizeDifferentStringMult() {
		DiffTools diffTools = new DiffTools();
		assertEquals(4,diffTools.diffString("1100000000111000011111000000000000000111", "0000000000000000000000000000000000000000").size());
		assertEquals("[Offset: 0 & Lenght: 2, Offset: 10 & Lenght: 3, Offset: 17 & Lenght: 5, Offset: 37 & Lenght: 3]", diffTools.diffString("1100000000111000011111000000000000000111", "0000000000000000000000000000000000000000").toString());
	}
	
	/**
	 * Testing same string, list containing differences should be zero
	 */
	@Test
	public void testSameSizeDifferentString2() {
		DiffTools diffTools = new DiffTools();
		assertEquals(1,diffTools.diffString("1000001000000==", "0000001000000==").size());
	}

	/**
	 * Testing root path using route
	 */
	@Test
	public void testIndex() {
		Result result = new HomeController().index();
		assertEquals(OK, result.status());
		assertEquals("text/plain", result.contentType().get());
		assertEquals("utf-8", result.charset().get());
		assertTrue(contentAsString(result).contains("Your diff application is ready."));
	}

	/**
	 * Testing root path using route
	 */
	@Test
	public void testIndexByRoute() {
		RequestBuilder request = Helpers.fakeRequest().method(GET).uri("/");
		Result result = route(app, request);
		assertEquals(OK, result.status());
	}

	/**
	 * Testing create valid input at left 
	 */
	@Test
	public void testvalidLeft() {
		jsonInput0 = Json.newObject();
		jsonInput0.put("input","QUJDYWJjMTI0w");
		JsonNode jsonNode = Json.toJson(jsonInput0);
		Result result = route(app, requestWithJsonBody("POST","/v1/diff/test/left",jsonNode));
		assertEquals(OK, result.status());
	}

	/**
	 * Testing create valid input at right 
	 */
	@Test
	public void testValidRight() {
		jsonInput0 = Json.newObject();
		jsonInput0.put("input","QUJDYWJjMTI0w");
		JsonNode jsonNode = Json.toJson(jsonInput0);
		Result result = route(app, requestWithJsonBody("POST","/v1/diff/test/right",jsonNode));
		assertEquals(OK, result.status());
	}

	/**
	 * Testing missing input parameter right
	 */
	@Test
	public void testMissingInputParamRight() {
		jsonInput0 = Json.newObject();
		jsonInput0.put("unexpected","QUJDYWJjMTI0w");
		JsonNode jsonNode = Json.toJson(jsonInput0);
		Result result = route(app, requestWithJsonBody("POST","/v1/diff/test/right",jsonNode));
		assertEquals(BAD_REQUEST, result.status());
	}
	/**
	 * Testing missing input parameter left
	 */
	@Test
	public void testMissingInputParamLeft() {
		jsonInput0 = Json.newObject();
		jsonInput0.put("unexpected","QUJDYWJjMTI0w");
		JsonNode jsonNode = Json.toJson(jsonInput0);
		Result result = route(app, requestWithJsonBody("POST","/v1/diff/test/left",jsonNode));
		assertEquals(BAD_REQUEST, result.status());
	}
	
	/**
	 * Testing is the left input is base64
	 */
	@Test
	public void testBase64Left() {
		jsonInput0 = Json.newObject();
		jsonInput0.put("input","Q#JDYWJjMTI0w");
		JsonNode jsonNode = Json.toJson(jsonInput0);
		Result result = route(app, requestWithJsonBody("POST","/v1/diff/test/left",jsonNode));
		assertEquals(BAD_REQUEST, result.status());
	}
	
	/**
	 * Testing is the right input is base64
	 */
	@Test
	public void testBase64Right() {
		jsonInput0 = Json.newObject();
		jsonInput0.put("input","Q#JDYWJjMTI0w");
		JsonNode jsonNode = Json.toJson(jsonInput0);
		Result result = route(app, requestWithJsonBody("POST","/v1/diff/test/right",jsonNode));
		assertEquals(BAD_REQUEST, result.status());
	}
	
	/**
	 * Testing diff - equal left and right  
	 */
	@Test
	public void testEqualInputs() {
		jsonInput0 = Json.newObject();
		jsonInput0.put("input","QUJDYWJjMTI0w");
		JsonNode jsonNode = Json.toJson(jsonInput0);

		Result result1 = route(app, requestWithJsonBody("POST","/v1/diff/test/right",jsonNode));
		Result result2 = route(app, requestWithJsonBody("POST","/v1/diff/test/left",jsonNode));

		assertEquals(OK, result1.status());
		assertEquals(OK, result2.status());

		RequestBuilder request = Helpers.fakeRequest().method(GET).uri("/v1/diff/test");
		Result result = route(app, request);
		assertEquals(OK, result.status());
		assertTrue(contentAsString(result).contains("inputs are equal"));
	}
	
	/**
	 * Testing diff - the inputs are different but with the same size  
	 */
	@Test
	public void testDifferentInputsSameSize() {
		jsonInput0 = Json.newObject();
		jsonInput1 = Json.newObject();
		jsonInput0.put("input","MTIzNDQ2Nzg5");
		jsonInput1.put("input","MDIzNDU2Nzg5");
		JsonNode jsonNode0 = Json.toJson(jsonInput0);
		JsonNode jsonNode1 = Json.toJson(jsonInput1);

		Result result1 = route(app, requestWithJsonBody("POST","/v1/diff/test/right",jsonNode0));
		Result result2 = route(app, requestWithJsonBody("POST","/v1/diff/test/left",jsonNode1));

		assertEquals(OK, result1.status());
		assertEquals(OK, result2.status());

		RequestBuilder request = Helpers.fakeRequest().method(GET).uri("/v1/diff/test");
		Result result = route(app, request);
		assertEquals(OK, result.status());
		assertTrue(contentAsString(result).contains("inputs have the same size"));
	}
	
	/**
	 * Testing diff - test different inputs  
	 */
	@Test
	public void testDifferentInputs() {
		jsonInput0 = Json.newObject();
		jsonInput1 = Json.newObject();
		jsonInput0.put("input","MTIzNDQ2Nzg5");
		jsonInput1.put("input","MTAyMDA1Njc4OQ==");
		JsonNode jsonNode0 = Json.toJson(jsonInput0);
		JsonNode jsonNode1 = Json.toJson(jsonInput1);

		Result result1 = route(app, requestWithJsonBody("POST","/v1/diff/test/right",jsonNode0));
		Result result2 = route(app, requestWithJsonBody("POST","/v1/diff/test/left",jsonNode1));

		assertEquals(OK, result1.status());
		assertEquals(OK, result2.status());

		RequestBuilder request = Helpers.fakeRequest().method(GET).uri("/v1/diff/test");
		Result result = route(app, request);
		assertEquals(OK, result.status());
		assertTrue(contentAsString(result).contains("inputs are not equal"));
	}

	/**
	 * Testing diff - missing left input  
	 */
	@Test
	public void testMissingLeftInputs() {
		jsonInput0 = Json.newObject();
		jsonInput0.put("input","QUJDYWJjMTI0w");
		JsonNode jsonNode = Json.toJson(jsonInput0);

		Result result1 = route(app, requestWithJsonBody("POST","/v1/diff/test/right",jsonNode));
		
		assertEquals(OK, result1.status());
		
		RequestBuilder request = Helpers.fakeRequest().method(GET).uri("/v1/diff/test");
		Result result = route(app, request);
		assertEquals(BAD_REQUEST, result.status());
		assertTrue(contentAsString(result).contains("missing input"));
	}
	
	/**
	 * Testing diff - missing right input  
	 */
	@Test
	public void testMissingRihtInputs() {
		jsonInput0 = Json.newObject();
		jsonInput0.put("input","QUJDYWJjMTI0w");
		JsonNode jsonNode = Json.toJson(jsonInput0);

		Result result1 = route(app, requestWithJsonBody("POST","/v1/diff/test/left",jsonNode));
		
		assertEquals(OK, result1.status());
		
		RequestBuilder request = Helpers.fakeRequest().method(GET).uri("/v1/diff/test");
		Result result = route(app, request);
		assertEquals(BAD_REQUEST, result.status());
		assertTrue(contentAsString(result).contains("missing input"));
	}
	
	/**
	 * Testing bad route 
	 */
	@Test
	public void testBadRoute() {
		Result result = route(fakeRequest(GET, "/badroute"));
		assertEquals(NOT_FOUND, result.status());
	}

	/**
	 * Testing requests
	 * @param method - request method
	 * @param uri - request URL
	 * @param jsonNode - request body as JsonNode
	 * @return request - request ready to be executed  
	 */
	public static <T> RequestBuilder requestWithJsonBody(String method, String uri, JsonNode jsonNode) { 

		RequestBuilder fakeRequest = Helpers.fakeRequest().method(method).uri(uri).header("context-type", "application/json").bodyJson(jsonNode);
		/*System.out.println("Created fakeRequest:\n"
				+"Header: "+fakeRequest.getHeaders().toString()+"\n"
				+"URI: "+fakeRequest.uri().toString()+"\n"
				+"bodyJson="+fakeRequest.body().asJson());*/
		return fakeRequest;  
	}
}