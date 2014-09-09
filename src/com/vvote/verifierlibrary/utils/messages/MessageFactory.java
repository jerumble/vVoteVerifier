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
package com.vvote.verifierlibrary.utils.messages;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vvote.messages.exceptions.TypedJSONMessageInitException;
import com.vvote.messages.exceptions.UnknownMessageException;
import com.vvote.messages.fields.MessageFields;
import com.vvote.messages.typed.TypedJSONMessage;
import com.vvote.messages.types.MessageType;
import com.vvote.thirdparty.json.orgjson.JSONException;
import com.vvote.thirdparty.json.orgjson.JSONObject;

/**
 * Factory to create messages of the correct type using their type string within
 * any typed JSON message. Uses reflection to remove the switch or long if/else
 * statements
 * 
 * @author James Rumble
 * 
 */
public class MessageFactory {

	/**
	 * Provides logging for the class
	 */
	private static final Logger logger = LoggerFactory.getLogger(MessageFactory.class);

	/**
	 * Factory method to create a <code>TypedJSONMessage</code> object of the
	 * correct type using the type string
	 * 
	 * @param json
	 * @return a <code>TypedJSONMessage</code> object of the correct type using
	 *         the type string
	 * @throws JSONException
	 * @throws UnknownMessageException 
	 * @throws TypedJSONMessageInitException 
	 */
	public static TypedJSONMessage constructMessage(JSONObject json) throws JSONException, UnknownMessageException, TypedJSONMessageInitException {

		logger.debug("Creating new TypedJSONMessage: {}", json);

		if (json.has(MessageFields.TYPE)) {

			if (MessageType.contains(json.getString(MessageFields.TYPE))) {

				logger.debug("Current message has a valid message type: {}", json.getString(MessageFields.TYPE));

				String className = MessageType.getClassForMessageType(json.getString(MessageFields.TYPE));

				if (className != null) {

					logger.debug("Dynamically created class name is: {}", className);

					try {
						// get the correct constructor which takes a single
						// JSONObject
						Constructor<?> clazz = Class.forName(className).getConstructor(JSONObject.class);

						// construct a new TypedJSONMessage of the correct type
						return (TypedJSONMessage) clazz.newInstance(json);

					} catch (NoSuchMethodException e) {
						logger.error("There was a problem creating a new TypedJSONMessage: {}", e);
						return null;
					} catch (SecurityException e) {
						logger.error("There was a problem creating a new TypedJSONMessage: {}", e);
						return null;
					} catch (IllegalArgumentException e) {
						logger.error("There was a problem creating a new TypedJSONMessage: {}", e);
						return null;
					} catch (InvocationTargetException e) {
						logger.error("There was a problem creating a new TypedJSONMessage: {}", e);
						return null;
					} catch (InstantiationException e) {
						logger.error("There was a problem creating a new TypedJSONMessage: {}", e);
						return null;
					} catch (IllegalAccessException e) {
						logger.error("There was a problem creating a new TypedJSONMessage: {}", e);
						return null;
					} catch (ClassNotFoundException e) {
						logger.error("There was a problem creating a new TypedJSONMessage: {}", e);
						return null;
					}
				}
				logger.error("There was a problem creating a new TypedJSONMessage for: {}", json.getString(MessageFields.TYPE));
				return null;
			}
			logger.error("There was a problem creating a new TypedJSONMessage of type: {}", json.getString(MessageFields.TYPE));
			throw new UnknownMessageException("There was a problem creating a new TypedJSONMessage of type: " + json.getString(MessageFields.TYPE));
		}
		logger.error("The type for a TypedJSONMessage must be specified");
		throw new TypedJSONMessageInitException("The type for a TypedJSONMessage must be specified");
	}

}
