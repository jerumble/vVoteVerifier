/**
 * This file is part of vVoteVerifier which is designed to be used as a verifiation tool for the vVote Election System.
 * Copyright (C) 2014  James Rumble (jerumble@gmail.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.vvote.verifierlibrary.json;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vvote.JSONConstants;
import com.vvote.exceptions.JSONSchemaException;
import com.vvote.thirdparty.json.orgjson.JSONException;
import com.vvote.thirdparty.json.orgjson.JSONObject;
import com.vvote.verifierlibrary.utils.io.IOUtils;

/**
 * Provides a number of utlity methods for JSON files primarily dealing with
 * schemas and verifying the formats of data
 * 
 * @author James Rumble
 * 
 */
public class JSONUtility {

	/**
	 * provides logging for the class
	 */
	private static final Logger logger = LoggerFactory.getLogger(JSONUtility.class);

	/**
	 * Script engine manager for processing
	 */
	private static final ScriptEngineManager SCRIPT_ENGINE_MANAGER = new ScriptEngineManager();

	/**
	 * Script engine in use
	 */
	private static final ScriptEngine SCRIPT_ENGINE = SCRIPT_ENGINE_MANAGER.getEngineByName(JSONConstants.SchemaProcessing.SCRIPT_ENGINE_NAME);

	/**
	 * Compiled script used for processing schemas
	 */
	private static CompiledScript compiledSchemaScript = null;

	/**
	 * Loads a schema from file
	 * 
	 * @param schemaPath
	 * @return the schema file in string format
	 * @throws JSONSchemaException
	 */
	public static String loadInSchema(String schemaPath) throws JSONSchemaException {
		logger.debug("Loading JSON Schema: '" + schemaPath + "'.");
		try {
			return IOUtils.readStringFromFile(schemaPath);
		} catch (FileNotFoundException e) {
			logger.error("Unable to load schema from file :{}", schemaPath, e);
			throw new JSONSchemaException("Unable to read schema list from file: " + schemaPath, e);
		} catch (IOException e) {
			logger.error("Unable to load schema from file :{}", schemaPath, e);
			throw new JSONSchemaException("Unable to read schema list from file: " + schemaPath, e);
		}
	}

	/**
	 * Validates a json against a provided schema
	 * 
	 * @param schema
	 * @param json
	 * @return true if the schema was validated successfully
	 * @throws JSONSchemaException
	 */
	public static boolean validateSchema(String schema, String json) throws JSONSchemaException {

		logger.debug("Validating JSON: '" + json + "'.");
		logger.debug("Using JSON Schema: '" + schema + "'.");

		try {
			// if schema script hasn't been initialised yet
			if (JSONUtility.compiledSchemaScript == null) {

				// initialise the schema script for validating a schema
				Compilable compilingEngine = (Compilable) JSONUtility.SCRIPT_ENGINE;

				// uncompiled script
				String uncompiledScript = IOUtils.readStringFromFile(JSONConstants.SchemaProcessing.SCHEMA_JS_LOCATION);

				// compile the validating script
				JSONUtility.compiledSchemaScript = compilingEngine.compile(uncompiledScript);
			}
			Bindings bindings = SCRIPT_ENGINE.createBindings();
			bindings.put(JSONConstants.SchemaProcessing.DATA_VARIABLE, json);
			bindings.put(JSONConstants.SchemaProcessing.SCHEMA_VARIABLE, schema);
			JSONObject result = new JSONObject(JSONUtility.compiledSchemaScript.eval(bindings).toString());
			if (result.getBoolean(JSONConstants.SchemaProcessing.IS_VALID)) {
				if (result.getJSONArray(JSONConstants.SchemaProcessing.IS_MISSING).length() == 0) {
					if (result.get(JSONConstants.SchemaProcessing.IS_ERROR) != null) {
						return true;
					}
					logger.error("JSON contained an error: {}", result.getJSONArray(JSONConstants.SchemaProcessing.IS_ERROR).toString());
					throw new JSONSchemaException("JSON contained an error: " + result.getJSONArray(JSONConstants.SchemaProcessing.IS_ERROR).toString());
				}
				logger.error("JSON was missing a field: {}", result.getJSONArray(JSONConstants.SchemaProcessing.IS_MISSING).toString());
				throw new JSONSchemaException("JSON was missing a field: " + result.getJSONArray(JSONConstants.SchemaProcessing.IS_MISSING).toString());

			}
			logger.error("Validating of the JSON Failed: {} using the schema file: {}", result.toString(), schema);
			return false;

		} catch (ScriptException e) {
			logger.error("Unable to validate json using schema - check the input schema file", e);
			throw new JSONSchemaException("Unable to validate json using schema - check the input schema file", e);
		} catch (JSONException e) {
			logger.error("Unable to validate json using schema - check the input schema file", e);
			throw new JSONSchemaException("Unable to validate json using schema - check the input schema file", e);
		} catch (FileNotFoundException e) {
			logger.error("Unable to validate json using schema - check the input schema file", e);
			throw new JSONSchemaException("Unable to validate json using schema - check the input schema file", e);
		} catch (IOException e) {
			logger.error("Unable to validate json using schema - check the input schema file", e);
			throw new JSONSchemaException("Unable to validate json using schema - check the input schema file", e);
		}
	}

	/**
	 * Prevents the class being externally created
	 */
	private JSONUtility() {
		return;
	}
}
