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
package com.vvote.verifier;

/**
 * Provides the top level representation of a verifier which will carry out
 * specific verification steps however all verifiers will provide a
 * doVerification implementation which will carry out all necessary verification
 * steps to provide result as to whether the actions of a specific component of
 * the vVote system has been carried out honestly and correctly.
 * 
 * @author James Rumble
 * 
 */
public interface IVerifier {

	/**
	 * All Verifiers will provide a doVerification implementation which will
	 * carry out all necessary verification steps.
	 * 
	 * @return true if the verification was carried out successfully
	 */
	public boolean doVerification();
}
