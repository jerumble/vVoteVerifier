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
 * Public WBB constants
 * 
 * @author James Rumble
 * 
 */
public class PublicWBBConstants {

	// TODO: Check that this remains constant - set this to SHA256
	/**
	 * Public WBB message digest constant
	 */
	public static final String PUBLIC_WBB_DIGEST = "SHA1";

	/**
	 * Commit message type.
	 */
	public static final String FINAL_COMMIT_MESSAGE_TYPE = "Commit";
	
	/**
	 * The length of a commit time
	 */
	public static final int COMMIT_TIME_LENGTH = 13;
}
