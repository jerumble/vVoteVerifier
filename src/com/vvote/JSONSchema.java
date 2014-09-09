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

/**
 * Provides a representation of the different schemas being used
 * 
 * @author James Rumble
 * 
 */
public enum JSONSchema {
	/**
	 * Vote Message
	 */
	VOTE_SCHEMA,
	/**
	 * POD Message
	 */
	POD_SCHEMA,
	/**
	 * Ballot Audit Commit Message
	 */
	BALLOT_AUDIT_COMMIT_SCHEMA,
	/**
	 * Ballot Gen Commit Message
	 */
	BALLOT_GEN_COMMIT_SCHEMA,
	/**
	 * Mix Random Commit Message
	 */
	MIX_RANDOM_COMMIT_SCHEMA,
	/**
	 * Ballot Submit Response
	 */
	BALLOT_SUBMIT_RESPONSE_SCHEMA,
	/**
	 * Ballot Gen schema - for the ballot generation verifier spec file
	 */
	BALLOT_GEN_SCHEMA,
	/**
	 * Commitment schema - for the commitment verifier spec file
	 */
	COMMITMENT_SCHEMA,
	/**
	 * Joint signature for a specific commit
	 */
	JOINT_SIGNATURE_SCHEMA,
	/**
	 * Vote packing schema - for the vote packing verifier spec file
	 */
	VOTE_PACKING_SCHEMA,
	/**
	 * Cancel message schema
	 */
	CANCEL_SCHEMA,
	/**
	 * Audit message schema
	 */
	AUDIT_SCHEMA;
}
