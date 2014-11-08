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
package com.vvote.messages.types;

/**
 * Provides the types of messages which can be created and also their location
 * so they can be created using reflection
 * 
 * @author James Rumble
 * 
 */
public enum MessageType {

	/**
	 * <code>PODMessage</code>
	 */
	POD("pod", "com.vvote.messages.typed.vote.PODMessage"),
	/**
	 * <code>VoteMessage</code>
	 */
	VOTE("vote", "com.vvote.messages.typed.vote.VoteMessage"),
	/**
	 * <code>MixRandomCommitMessage</code>
	 */
	MIX_RANDOM_COMMIT("mixrandomcommit", "com.vvote.messages.typed.file.MixRandomCommitMessage"),
	/**
	 * <code>BallotGenCommitMessage</code>
	 */
	BALLOT_GEN_COMMIT("ballotgencommit", "com.vvote.messages.typed.file.BallotGenCommitMessage"),
	/**
	 * <code>BallotAuditCommitMessage</code>
	 */
	BALLOT_AUDIT_COMMIT("ballotauditcommit", "com.vvote.messages.typed.file.BallotAuditCommitMessage"),
	
	/**
	 * <code>BallotAuditCommitMessage</code>
	 */
	FILE_COMMIT("file", "com.vvote.messages.typed.file.FileMessage"),
	
	/**
	 * <code>CancelMessage</code>
	 */
	CANCEL("cancel", "com.vvote.messages.typed.vote.CancelMessage"),
	
	/**
	 * <code>AuditMessage</code>
	 */
	AUDIT("audit", "com.vvote.messages.typed.vote.AuditMessage");

	/**
	 * Checks whether the provided type is a valid message type
	 * 
	 * @param messageType
	 * @return true if the provided string message type is a valid message type
	 */
	public static boolean contains(String messageType) {

		for (MessageType type : MessageType.values()) {
			if (type.getType().equalsIgnoreCase(messageType)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Gets the class for a specific message type
	 * 
	 * @param messageType
	 * @return the class for a specific message type
	 */
	public static String getClassForMessageType(String messageType) {
		for (MessageType type : MessageType.values()) {
			if (type.getType().equalsIgnoreCase(messageType)) {
				return type.getClassName();
			}
		}

		return null;
	}

	/**
	 * First checks whether the provided message type exists and then returns
	 * the valid message type from the provided message type string
	 * 
	 * @param messageType
	 * @return the message type with matching type
	 */
	public static MessageType getMessageTypeFromType(String messageType) {
		if (MessageType.contains(messageType)) {
			for (MessageType type : MessageType.values()) {
				if (type.getType().equalsIgnoreCase(messageType)) {
					return type;
				}
			}
		}

		return null;
	}

	/**
	 * The type of a message
	 */
	private final String type;

	/**
	 * The class name of the message
	 */
	private final String className;

	/**
	 * Constructor for a message type
	 * 
	 * @param type
	 * @param className
	 */
	private MessageType(String type, String className) {
		this.type = type;
		this.className = className;
	}

	/**
	 * Getter for the class name of a message type
	 * 
	 * @return className
	 */
	public final String getClassName() {
		return this.className;
	}

	/**
	 * Getter for the type of the message type
	 * 
	 * @return type
	 */
	public final String getType() {
		return this.type;
	}
}
