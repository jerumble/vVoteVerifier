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
package com.vvote.messages.typed.file;

import com.vvote.JSONSchema;
import com.vvote.messages.exceptions.FileMessageInitException;
import com.vvote.messages.exceptions.JSONMessageInitException;
import com.vvote.messages.exceptions.TypedJSONMessageInitException;
import com.vvote.thirdparty.json.orgjson.JSONObject;

/**
 * Provides the concrete implementation for a
 * <code>BallotGenCommitMessage</code> object
 * 
 * @author James Rumble
 * 
 */
public final class BallotGenCommitMessage extends FileMessage {

	/**
	 * Constructor for a <code>BallotGenCommitMessage</code> object
	 * 
	 * @param json
	 * @throws TypedJSONMessageInitException
	 * @throws JSONMessageInitException
	 * @throws FileMessageInitException
	 */
	public BallotGenCommitMessage(JSONObject json) throws TypedJSONMessageInitException, JSONMessageInitException, FileMessageInitException {
		super(json);
	}

	@Override
	public JSONSchema getSchema() {
		return JSONSchema.BALLOT_GEN_COMMIT_SCHEMA;
	}
}
