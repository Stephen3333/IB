package com.integratedbiometrics.IB.controller;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kojak.com.sample.InvokeDevice;

@RestController

public class IbScanController {
	public static JSONObject response;
	public static JSONObject jsonrequest;

	@RequestMapping(value = "/getImage")
	public ResponseEntity<?> getImage(@  RequestBody String request) {
		try {
			JSONParser parser = new JSONParser();
			jsonrequest = (JSONObject) parser.parse(request);
			new InvokeDevice().captureImages(jsonrequest.get("name").toString());

			Thread.sleep(5000);
		} catch (Exception e) {
			// TODO Auto-generated catch blockFG
			e.printStackTrace();

		}
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}