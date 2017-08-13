package controllers;
import java.io.IOException;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import services.DiffTools;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class HomeController extends Controller {

	private final Hashtable<String, String> right =  new Hashtable<String, String>();
	private final Hashtable<String, String> left =  new Hashtable<String, String>();
	private DiffTools diffTools = new DiffTools();


	/**
	 * simple notification to check if the application is ready
	 */
	public Result index() {
		return ok("Your diff application is ready.");
	}

	/**
	 * Handle the 'submit right' request 
	 * @param id - use the id from route to set the right input
	 */
	public Result submitRight(String id) {

		JsonNode json = request().body().asJson();

		if(json == null) {
			return badRequest("Expecting Json data");
		} else {
			String content = json.findPath("input").textValue();
			if(content == null) {
				return badRequest("Missing parameter [input]");
			} else{ 
				if(diffTools.checkBase64(content)){
					right.put(id,content);
					ObjectNode result = Json.newObject();
					result.put("id", id);
					result.put("content", content);
					result.put("result", "created");
					return ok(result);
				}else{
					return badRequest("Input is not Base64");
				}
			}
		}
	}

	/**
	 * Handle the 'submit left' request 
	 * @param id - use the id from route to set the left input
	 */
	public Result submitLeft(String id) {

		JsonNode json = request().body().asJson();

		if(json == null) {
			return badRequest("Expecting Json data");
		} else {
			String content = json.findPath("input").textValue();
			if(content == null) {
				return badRequest("Missing parameter [input]");
			} else{ 
				if(diffTools.checkBase64(content)){
					left.put(id,content);
					ObjectNode result = Json.newObject();
					result.put("id", id);
					result.put("content", content);
					result.put("result", "created");
					return ok(result);
				}else{
					return badRequest("Input is not Base64");
				}
			}
		}
	}

	/**
	 * check the difference between left and right
	 * @param id - use the id from route to execute the diff 
	 */
	public Result checkDifference(String id) {

		//get left and right inputs
		String leftEncoded = left.get(id);
		String rightEncoded = right.get(id);

		//verify if one of the inputs is missing 
		try {
			leftEncoded.isEmpty();	
			rightEncoded.isEmpty();
		} catch (Exception e) {
			ObjectNode result = Json.newObject();
			result.put("result", "missing input");
			return badRequest(result);
		}

		//return if the inputs are equal
		if(leftEncoded.contentEquals(rightEncoded)){
			ObjectNode result = Json.newObject();
			result.put("result", "inputs are equal");
			return ok(result);
		} 

		//decode from base64
		byte[] leftDecoded = Base64.decodeBase64(leftEncoded);
		byte[] rightDecoded = Base64.decodeBase64(rightEncoded);

		//convert bytes to String
		String leftDecodedString = new String(leftDecoded);
		String rightDecodedString = new String(rightDecoded);

		if(leftDecodedString.length() == rightDecodedString.length()){

			LinkedList<String> strList = new LinkedList<String>();

			//identify offset and length of the differences 
			if (leftDecodedString.length() == rightDecodedString.length()) {
				strList = diffTools.diffString(leftDecodedString, rightDecodedString);
			}

			ObjectNode result = Json.newObject();
			result.put("result", "inputs have the same size");
			result.put("offset", strList.toString());
			return ok(result);
		}
		else {
			ObjectNode result = Json.newObject();
			result.put("result", "inputs are not equal");
			return ok(result);
		}
	}
}