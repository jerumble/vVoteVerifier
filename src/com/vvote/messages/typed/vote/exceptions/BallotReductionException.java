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
package com.vvote.messages.typed.vote.exceptions;

/**
 * Exception used when there is a problem initialising a set of ballot
 * reductions for use in a <code>PODMessage</code> object
 * 
 * @author James Rumble
 * 
 */
public class BallotReductionException extends Exception {

	/**
	 * generated serial version id
	 */
	private static final long serialVersionUID = -7224669591121552890L;

	/**
	 * Constructs a new exception with <code>null</code> as its detail message.
	 */
	public BallotReductionException() {
		super();
	}

	/**
	 * Constructs a new exception with the specified detail message.
	 * 
	 * @param message
	 *            the detail message.
	 */
	public BallotReductionException(String message) {
		super(message);
	}

	/**
	 * Constructs a new exception with the specified detail message and cause.
	 * <p>
	 * The detail message associated with <code>Throwable</code> cause is
	 * <i>not</i> automatically incorporated in this exception's detail message.
	 * 
	 * @param message
	 *            the detail message.
	 * @param cause
	 *            the cause. A <code>null</code> value is permitted, and
	 *            indicates that the cause is nonexistent or unknown.
	 */
	public BallotReductionException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructs a new exception with the specified detail message, cause,
	 * suppression enabled or disabled, and writable stack trace enabled or
	 * disabled.
	 * 
	 * @param message
	 *            the detail message.
	 * @param cause
	 *            the cause. (A <code>null</code> value is permitted, and
	 *            indicates that the cause is nonexistent or unknown.)
	 * @param enableSuppression
	 *            whether or not suppression is enabled or disabled
	 * @param writableStackTrace
	 *            whether or not the stack trace should be writable
	 */
	public BallotReductionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	/**
	 * Constructs a new exception with the specified cause and a detail message.
	 * 
	 * @param cause
	 *            the cause. A <code>null</code> value is permitted, and
	 *            indicates that the cause is nonexistent or unknown.
	 */
	public BallotReductionException(Throwable cause) {
		super(cause);
	}
}
