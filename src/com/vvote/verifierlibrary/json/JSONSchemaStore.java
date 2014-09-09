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
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vvote.JSONConstants;
import com.vvote.JSONSchema;
import com.vvote.exceptions.JSONSchemaException;
import com.vvote.thirdparty.json.orgjson.JSONArray;
import com.vvote.thirdparty.json.orgjson.JSONException;
import com.vvote.thirdparty.json.orgjson.JSONObject;
import com.vvote.verifierlibrary.exceptions.JSONIOException;
import com.vvote.verifierlibrary.utils.io.IOUtils;

/**
 * Provides storage for the schemas in use in the system. The schemas are read
 * in from a file and can then be accessed
 * 
 * @author James Rumble
 * 
 */
public class JSONSchemaStore {

	/**
	 * Instance variable which allows this class to be a singleton
	 */
	private static JSONSchemaStore instance = null;

	/**
	 * provides logging for the class
	 */
	private static final Logger logger = LoggerFactory.getLogger(JSONSchemaStore.class);

	/**
	 * storage for JSONSchema : schema
	 */
	private static Map<JSONSchema, String> schemas = new HashMap<JSONSchema, String>();

	/**
	 * Getter for the single instance of the <code>JSONSchemaStore</code>
	 * 
	 * @return <code>JSONSchemaStore</code> object
	 * @throws JSONSchemaException
	 */
	public final static JSONSchemaStore getInstance() throws JSONSchemaException {
		if (instance == null) {
			instance = new JSONSchemaStore();
		}
		return instance;
	}

	/**
	 * Getter for a specific schema
	 * 
	 * @param schema
	 * @return the specific schema
	 */
	public static String getSchema(JSONSchema schema) {
		return schemas.get(schema);
	}

	/**
	 * Constructor for a <code>JSONSchemaStore</code> object
	 * 
	 * @throws JSONSchemaException
	 */
	private JSONSchemaStore() throws JSONSchemaException {

		JSONObject currentSchema = null;

		try {
			JSONArray schemaArray = IOUtils.readJSONArrayFromFile(JSONConstants.JSONSchemas.SCHEMA_LIST);

			for (int i = 0; i < schemaArray.length(); i++) {
				currentSchema = schemaArray.getJSONObject(i);

				schemas.put(JSONSchema.valueOf(currentSchema.getString(JSONConstants.JSONSchemas.SCHEMA_ID)),
						IOUtils.readStringFromFile(currentSchema.getString(JSONConstants.JSONSchemas.SCHEMA_PATH)));
			}

		} catch (JSONIOException e) {
			logger.error("Unable to read schema list from file :{}", JSONConstants.JSONSchemas.SCHEMA_LIST, e);
			throw new JSONSchemaException("Unable to read schema list from file: " + JSONConstants.JSONSchemas.SCHEMA_LIST, e);
		} catch (JSONException e) {
			logger.error("Unable to read schema list from file :{}", JSONConstants.JSONSchemas.SCHEMA_LIST, e);
			throw new JSONSchemaException("Unable to read schema list from file: " + JSONConstants.JSONSchemas.SCHEMA_LIST, e);
		} catch (FileNotFoundException e) {
			logger.error("Unable to read schema list from file :{}", JSONConstants.JSONSchemas.SCHEMA_LIST, e);
			throw new JSONSchemaException("Unable to read schema list from file: " + JSONConstants.JSONSchemas.SCHEMA_LIST, e);
		} catch (IOException e) {
			logger.error("Unable to read schema list from file :{}", JSONConstants.JSONSchemas.SCHEMA_LIST, e);
			throw new JSONSchemaException("Unable to read schema list from file: " + JSONConstants.JSONSchemas.SCHEMA_LIST, e);
		}
	}
}
