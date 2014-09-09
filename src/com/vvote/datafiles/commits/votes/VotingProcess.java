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
package com.vvote.datafiles.commits.votes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vvote.datafiles.exceptions.VoteMessageCommitException;
import com.vvote.messages.typed.vote.PODMessage;
import com.vvote.messages.typed.vote.VoteMessage;

/**
 * Provides storage for a related pod message and vote message which share the
 * same serial number
 * 
 * @author James Rumble
 * 
 */
public class VotingProcess {

	/**
	 * provides logging for the class
	 */
	private static final Logger logger = LoggerFactory.getLogger(VotingProcess.class);

	/**
	 * Holds the pod message
	 */
	private final PODMessage podMessage;

	/**
	 * Holds the vote message
	 */
	private final VoteMessage voteMessage;

	/**
	 * Constructor for a <code>VotingProcess</code> object
	 * 
	 * @param podMessage
	 * @param voteMessage
	 * @throws VoteMessageCommitException
	 */
	public VotingProcess(PODMessage podMessage, VoteMessage voteMessage) throws VoteMessageCommitException {
		if (podMessage != null && voteMessage != null) {
			if (podMessage.getSerialNo().equals(voteMessage.getSerialNo())) {
				this.podMessage = podMessage;
				this.voteMessage = voteMessage;
			} else {
				logger.error("PODMessage and VoteMessage must have matching serial numbers");
				throw new VoteMessageCommitException("PODMessage and VoteMessage must have matching serial numbers");
			}
		} else {
			logger.error("A valid PODMessage and valid VoteMessage must be supplied");
			throw new VoteMessageCommitException("A valid PODMessage and valid VoteMessage must be supplied");
		}
	}

	/**
	 * Getter for the pod message
	 * 
	 * @return podMessage
	 */
	public PODMessage getPodMessage() {
		return this.podMessage;
	}

	/**
	 * Getter for the vote message
	 * 
	 * @return voteMessage
	 */
	public VoteMessage getVoteMessage() {
		return this.voteMessage;
	}

	@Override
	public String toString() {
		return "VotingProcess [podMessage=" + this.podMessage + ", voteMessage=" + this.voteMessage + "]";
	}
}
