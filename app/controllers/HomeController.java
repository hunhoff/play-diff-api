package controllers;
import java.util.Hashtable;
import java.util.LinkedList;

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
	public Result createRight(String id) {

		JsonNode json = request().body().asJson();

		if(json == null) {
			return badRequest("Expecting Json data");
		} else {
			String content = json.findPath("input").textValue();
			if(content == null) {
				return badRequest("Missing parameter [input]");
			} else{ 
				//verify if the id was already created
				try {
					right.get(id).isEmpty();
					ObjectNode result = Json.newObject();
					result.put("input", content);
					result.put("result", "id already created, update instead");
					return status(409, result);
				} catch (Exception e) {
					if(diffTools.checkBase64(content)){
						right.put(id,content);
						ObjectNode result = Json.newObject();
						result.put("id", id);
						result.put("content", content);
						result.put("result", "created");
						return created(result);
					} else{
						return badRequest("Input is not Base64");
					}
				}
			}
		}
	}

	/**
	 * Handle the 'submit left' request 
	 * @param id - use the id from route to set the left input
	 */
	public Result createLeft(String id) {

		JsonNode json = request().body().asJson();

		if(json == null) {
			return badRequest("Expecting Json data");
		} else {
			String content = json.findPath("input").textValue();
			if(content == null) {
				return badRequest("Missing parameter [input]");
			} else{ 
				//verify if the id was already created
				try {
					left.get(id).isEmpty();
					ObjectNode result = Json.newObject();
					result.put("input", content);
					result.put("result", "id already created, update instead");
					return status(409, result);
				} catch (Exception e) {
					if(diffTools.checkBase64(content)){
						left.put(id,content);
						ObjectNode result = Json.newObject();
						result.put("id", id);
						result.put("content", content);
						result.put("result", "created");
						return created(result);
					} else{
						return badRequest("Input is not Base64");
					}
				}
			}
		}
	}

	/**
	 * Handle the 'submit right' request 
	 * @param id - use the id from route to set the right input
	 */
	public Result updateLeft(String id) {

		JsonNode json = request().body().asJson();

		if(json == null) {
			return badRequest("Expecting Json data");
		} else {
			String content = json.findPath("input").textValue();
			if(content == null) {
				return badRequest("Missing parameter [input]");
			} else{ 
				//verify if the id was already created
				try {
					left.get(id).isEmpty();
					if(diffTools.checkBase64(content)){
						left.put(id,content);
						ObjectNode result = Json.newObject();
						result.put("id", id);
						result.put("content", content);
						result.put("result", "updated");
						return ok(result);
					} else{
						return badRequest("Input is not Base64");
					}
				} catch (Exception e) {
					ObjectNode result = Json.newObject();
					result.put("id", id);
					result.put("content", content);
					result.put("result", "id not found, create instead");
					return notFound(result);
				}
			}
		}
	}

	/**
	 * Handle the 'submit right' request 
	 * @param id - use the id from route to set the right input
	 */
	public Result updateRight(String id) {

		JsonNode json = request().body().asJson();

		if(json == null) {
			return badRequest("Expecting Json data");
		} else {
			String content = json.findPath("input").textValue();
			if(content == null) {
				return badRequest("Missing parameter [input]");
			} else{ 
				//verify if the id was already created
				try {
					right.get(id).isEmpty();
					if(diffTools.checkBase64(content)){
						right.put(id,content);
						ObjectNode result = Json.newObject();
						result.put("id", id);
						result.put("content", content);
						result.put("result", "updated");
						return ok(result);
					} else{
						return badRequest("Input is not Base64");
					}
				} catch (Exception e) {
					ObjectNode result = Json.newObject();
					result.put("id", id);
					result.put("content", content);
					result.put("result", "id not found, create instead");
					return notFound(result);
				}
			}
		}
	}

	/**
	 * Handle the 'submit right' request 
	 * @param id - use the id from route to set the right input
	 */
	public Result decodeLeftToString(String id) {

		//get left and right inputs
		String leftEncoded = left.get(id);

		//verify if the input exists
		try {
			leftEncoded.isEmpty();	
		} catch (Exception e) {
			ObjectNode result = Json.newObject();
			result.put("result", "missing input");
			return notFound(result);
		}

		//decode from base64
		byte[] leftDecoded = Base64.decodeBase64(leftEncoded);
		//convert bytes to String
		String leftDecodedString = new String(leftDecoded);
		
		ObjectNode result = Json.newObject();
		result.put("input", leftEncoded.toString());
		result.put("result", leftDecodedString);
		return ok(result);

	}
	
	/**
	 * Handle the 'submit right' request 
	 * @param id - use the id from route to set the right input
	 */
	public Result decodeRightToString(String id) {

		//get right inputs
		String rightEncoded = right.get(id);

		//verify if the input exists
		try {
			rightEncoded.isEmpty();	
		} catch (Exception e) {
			ObjectNode result = Json.newObject();
			result.put("result", "missing input");
			return notFound(result);
		}

		//decode from base64
		byte[] rightDecoded = Base64.decodeBase64(rightEncoded);
		//convert bytes to String
		String rightDecodedString = new String(rightDecoded);
		
		ObjectNode result = Json.newObject();
		result.put("input", rightEncoded.toString());
		result.put("result", rightDecodedString);
		return ok(result);

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