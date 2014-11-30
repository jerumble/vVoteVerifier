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
 * Provides a concrete implementation for a
 * <code>BallotAuditCommitMessage</code> object
 * 
 * @author James Rumble
 * 
 */
public final class BallotAuditCommitMessage extends FileMessage {

	/**
	 * Constructor for a <code>BallotAuditCommitMessage</code> object
	 * 
	 * @param json
	 * @throws FileMessageInitException
	 * @throws JSONMessageInitException
	 * @throws TypedJSONMessageInitException
	 */
	public BallotAuditCommitMessage(JSONObject json) throws TypedJSONMessageInitException, JSONMessageInitException, FileMessageInitException {
		super(json);
	}
	
	@Override
	public JSONSchema getSchema() {
		return JSONSchema.BALLOT_AUDIT_COMMIT_SCHEMA;
	}

	@Override
	public String toString() {
		return "BallotAuditCommitMessage [getSchema()=" + getSchema() + ", get_digest()=" + get_digest() + ", getDigest()=" + getDigest() + ", getFileName()=" + getFileName() + ", getFilesize()="
				+ getFilesize() + ", getSubmissionId()=" + getSubmissionId() + ", getInternalSignableContent()=" + getInternalSignableContent() + ", getBoothID()=" + getBoothID() + ", getBoothSig()="
				+ getBoothSig() + ", getType()=" + getType() + ", getCommitTime()=" + getCommitTime() + ", getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()="
				+ super.toString() + "]";
	}
}
