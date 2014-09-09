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
package com.vvote;

import com.vvote.messages.fields.MessageFields;

/**
 * Constants for loading and processing JSON schema files
 * 
 * @author James Rumble
 * 
 */
public class JSONConstants {

	/**
	 * Constants for use in loading JSON schemas
	 */
	public static class JSONSchemas {

		/**
		 * Location of the schema files
		 */
		public static final String SCHEMA_LIST = "./schemas/schema_list.json";

		/**
		 * Identifier for the Schema ID
		 */
		public static final String SCHEMA_ID = "id";

		/**
		 * Identifier for the Schema path
		 */
		public static final String SCHEMA_PATH = "schemaPath";
	}

	/**
	 * Constants for schema processing
	 */
	public static class SchemaProcessing {

		/**
		 * Location of the uncompiled script for schema validation
		 */
		public static final String SCHEMA_JS_LOCATION = "./schemas/validation/tv4.min.js";
		/**
		 * Identifier for the data variable
		 */
		public static final String DATA_VARIABLE = "data";
		/**
		 * Identifier for the schema variable
		 */
		public static final String SCHEMA_VARIABLE = "schema";
		/**
		 * Identifier for the is valid field
		 */
		public static final String IS_VALID = "valid";
		/**
		 * Identifier for the is missing flag
		 */
		public static final String IS_MISSING = "missing";
		/**
		 * Identifier for the is error flag
		 */
		public static final String IS_ERROR = "error";
		/**
		 * Name of the script engine to use for processing JSON schemas
		 */
		public static final String SCRIPT_ENGINE_NAME = "JavaScript";
	}

	/**
	 * Identifies a separation between the prefix and number of a serial number
	 */
	public static final String SERIAL_NO_SEPARATOR = ":";

	/**
	 * Identifies a separation
	 */
	public static final String RACE_SEPARATOR = MessageFields.RACE_SEPARATOR;

	/**
	 * Identifies a separation between preferences
	 */
	public static final String PREFERENCE_SEPARATOR = MessageFields.PREFERENCE_SEPARATOR;
}
