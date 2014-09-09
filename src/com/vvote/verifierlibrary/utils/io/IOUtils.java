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
package com.vvote.verifierlibrary.utils.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.bytecode.opencsv.CSVReader;

import com.vvote.thirdparty.json.orgjson.JSONArray;
import com.vvote.thirdparty.json.orgjson.JSONException;
import com.vvote.thirdparty.json.orgjson.JSONObject;
import com.vvote.verifierlibrary.exceptions.JSONIOException;

/**
 * Provides a number of input and output functions for use throughout the system
 * 
 * @author James Rumble
 * 
 */
public class IOUtils {

	/**
	 * A full stop
	 */
	private static final String PERIOD = ".";

	/**
	 * Provides logging for the class
	 */
	private static final Logger logger = LoggerFactory.getLogger(IOUtils.class);

	/**
	 * Checks the extension of a file
	 * 
	 * @param fileType
	 * @param filename
	 * @return true if the extension of the filename matches that which is
	 *         expected
	 */
	public static boolean checkExtension(FileType fileType, String filename) {
		return checkExtension(fileType.getExtension(), filename);
	}

	/**
	 * Checks a filename for an expected extension
	 * 
	 * @param expectedExtension
	 * @param filename
	 * @return the name of the file without the extension
	 */
	private static boolean checkExtension(String expectedExtension, String filename) {

		if (filename != null) {
			if (filename.length() > 0) {
				if (filename.contains(".")) {

					// check for last period
					int index = filename.lastIndexOf('.');
					if (index > 0) {

						// check extension is correct
						if (!filename.substring(index + 1).equals(expectedExtension)) {
							return false;
						}

						return true;
					}
				}
			}
		}

		return false;
	}

	/**
	 * Finds a file using the filename provided
	 * 
	 * @param name
	 * @param filename
	 * @return the path for the found file
	 */
	public static String findFile(String name, String filename) {

		File file = new File(filename);

		if (file.exists()) {
			return findFile(name, file);
		}
		return null;
	}

	/**
	 * Finds a file using its file name from a specified File starting point.
	 * 
	 * @param name
	 * @param file
	 * @return the path for the found file
	 */
	public static String findFile(String name, File file) {

		File[] currentFileList = file.listFiles();

		String result = null;

		if (currentFileList != null) {
			for (File currentFile : currentFileList) {
				if (name.equalsIgnoreCase(currentFile.getName())) {
					return currentFile.getPath();
				} else if (currentFile.isDirectory()) {
					result = findFile(name, currentFile);
					if (result != null) {
						return result;
					}
				}
			}
		}
		return null;
	}

	/**
	 * Utility class to extract a zip file
	 * 
	 * @param filepath
	 * @return the location of the extract zip file
	 * @throws IOException
	 */
	public static String extractZipFile(String filepath) throws IOException {

		if (IOUtils.checkExtension(FileType.ZIP, filepath)) {
			try (ZipFile zipFile = new ZipFile(filepath)) {

				// files in zip file
				Enumeration<? extends ZipEntry> entries = zipFile.entries();

				// current zip directory
				File zipDirectory = new File(filepath);

				// output directory - doesn't include the .zip extension
				File outputDirectory = new File(IOUtils.join(zipDirectory.getParent(), IOUtils.getFileNameWithoutExtension(filepath)));

				// make directory if not exists
				if (!outputDirectory.exists()) {
					outputDirectory.mkdir();
				}

				InputStream is = null;
				FileOutputStream fos = null;
				byte[] bytes = null;

				int length = 0;

				// loop over each file in zip file
				while (entries.hasMoreElements()) {
					ZipEntry zipEntry = entries.nextElement();

					String entryName = zipEntry.getName();

					// current output file
					File file = new File(IOUtils.join(outputDirectory.getPath(), entryName));

					// if directory make it
					if (entryName.endsWith("/")) {
						file.mkdirs();
					} else {

						// write current input zip file to output location
						is = zipFile.getInputStream(zipEntry);
						fos = new FileOutputStream(file);
						bytes = new byte[1024];

						while ((length = is.read(bytes)) >= 0) {
							fos.write(bytes, 0, length);
						}
						is.close();
						fos.close();
					}
				}

				return outputDirectory.getPath();
			}
		}
		logger.error("Provided filepath: {} does not point to a valid zip file", filepath);
		throw new IOException("Provided filepath: " + filepath + " does not point to a valid zip file");
	}

	/**
	 * Gets the name of a file without the extension
	 * 
	 * @param filename
	 * @return the name of a file without the extension
	 */
	public static String getFileNameWithoutExtension(String filename) {
		return FilenameUtils.removeExtension(new File(filename).getName());
	}

	/**
	 * Performs a joining of two paths together
	 * 
	 * @param path1
	 * @param path2
	 * @return the path when the two input paths are 'joined'
	 */
	public static String join(String path1, String path2) {
		File file1 = new File(path1);
		File file2 = new File(file1, path2);
		return file2.getPath();
	}

	/**
	 * Read a csv file from a specified filepath into an arraylist containing
	 * 
	 * @param filepath
	 * @return a list of rows of the csv file
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public static List<List<String>> readCSVFromFile(String filepath) throws FileNotFoundException, IOException {

		if (IOUtils.checkExtension(FileType.CSV, filepath)) {
			List<List<String>> csvResult = new ArrayList<List<String>>();

			try (CSVReader reader = new CSVReader(new FileReader(filepath))) {
				String[] nextLine;

				List<String> currentLine = null;

				while ((nextLine = reader.readNext()) != null) {

					currentLine = Arrays.asList(nextLine);

					csvResult.add(currentLine);
				}
			}

			return csvResult;

		}
		logger.error("Provided filepath: {} does not point to a valid csv file", filepath);
		throw new IOException("Provided filepath: " + filepath + " does not point to a valid csv file");
	}

	/**
	 * Reads a JSON array from a given filepath
	 * 
	 * @param filepath
	 * @return The <code>JSONArray</code>
	 * @throws JSONIOException
	 */
	public static JSONArray readJSONArrayFromFile(String filepath) throws JSONIOException {
		if (IOUtils.checkExtension(FileType.JSON, filepath)) {
			try {
				logger.debug("Reading JSONArray from file: '" + filepath + "'");
				return new JSONArray(IOUtils.readStringFromFile(filepath));
			} catch (JSONException e) {
				logger.error("Error when trying to read from: '" + filepath + "'", e);
				throw new JSONIOException("Error when trying to read from: '" + filepath + "'", e);
			} catch (FileNotFoundException e) {
				logger.error("Error when trying to read from: '" + filepath + "'", e);
				throw new JSONIOException("Error when trying to read from: '" + filepath + "'", e);
			} catch (IOException e) {
				logger.error("Error when trying to read from: '" + filepath + "'", e);
				throw new JSONIOException("Error when trying to read from: '" + filepath + "'", e);
			}
		}
		logger.error("Provided filepath: {} does not point to a valid json file", filepath);
		throw new JSONIOException("Provided filepath: " + filepath + " does not point to a valid json file");
	}

	/**
	 * Reads a JSON message from a given filepath
	 * 
	 * @param filepath
	 * @return The <code>List</code> of <code>JSONObject</code> messages
	 * @throws JSONIOException
	 */
	public static List<JSONObject> readJSONMessagesFromFile(String filepath) throws JSONIOException {
		logger.debug("Reading in messages: {}", filepath);

		if (IOUtils.checkExtension(FileType.JSON, filepath)) {
			List<JSONObject> jsonMessages = new ArrayList<JSONObject>();

			String line = null;

			// create reader for the file containing election data
			try (BufferedReader messagesReader = new BufferedReader(new FileReader(filepath))) {

				// loop over each line and construct a new message per line
				// using the message factory
				while ((line = messagesReader.readLine()) != null) {
					jsonMessages.add(new JSONObject(line));
				}
			} catch (FileNotFoundException e) {
				logger.error("Error when trying to read from: '" + filepath + "'", e);
				throw new JSONIOException("Error when trying to read from: '" + filepath + "'", e);
			} catch (IOException e) {
				logger.error("Error when trying to read from: '" + filepath + "'", e);
				throw new JSONIOException("Error when trying to read from: '" + filepath + "'", e);
			} catch (JSONException e) {
				logger.error("Error when trying to read from: '" + filepath + "'", e);
				throw new JSONIOException("Error when trying to read from: '" + filepath + "'", e);
			}

			logger.debug("Successfully loaded in the messages data file: {}", filepath);

			return jsonMessages;
		}
		logger.error("Provided filepath: {} does not point to a valid json file", filepath);
		throw new JSONIOException("Provided filepath: " + filepath + " does not point to a valid json file");
	}

	/**
	 * Reads a single JSONObject from a given filepath
	 * 
	 * @param filepath
	 * @return The <code>JSONObject</code>
	 * @throws JSONIOException
	 */
	public static JSONObject readJSONObjectFromFile(String filepath) throws JSONIOException {
		if (IOUtils.checkExtension(FileType.JSON, filepath)) {
			try {
				logger.debug("Reading JSONObject from file: '" + filepath + "'");
				return new JSONObject(IOUtils.readStringFromFile(filepath));
			} catch (JSONException e) {
				logger.error("Error when trying to read from: '" + filepath + "'", e);
				throw new JSONIOException("Error when trying to read from: '" + filepath + "'", e);
			} catch (FileNotFoundException e) {
				logger.error("Error when trying to read from: '" + filepath + "'", e);
				throw new JSONIOException("Error when trying to read from: '" + filepath + "'", e);
			} catch (IOException e) {
				logger.error("Error when trying to read from: '" + filepath + "'", e);
				throw new JSONIOException("Error when trying to read from: '" + filepath + "'", e);
			}
		}
		logger.error("Provided filepath: {} does not point to a valid json file", filepath);
		throw new JSONIOException("Provided filepath: " + filepath + " does not point to a valid json file");
	}

	/**
	 * Reads a string from a given filepath. Reads the whole file into a single
	 * string object before returning
	 * 
	 * @param filepath
	 * @return the whole file being read as a single string
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public static String readStringFromFile(String filepath) throws FileNotFoundException, IOException {

		logger.debug("Reading a string from a given filepath: {}", filepath);

		StringBuffer sb = new StringBuffer();
		String line = null;

		try (BufferedReader br = new BufferedReader(new FileReader(new File(filepath)))) {
			while ((line = br.readLine()) != null) {
				if (line.trim().length() > 0) {
					sb.append(line);
				}
			}
		}

		return sb.toString();
	}

	/**
	 * Read in the zip file and store each entry so it can be looked up later
	 * 
	 * @param filepath
	 * @return the list of all files within the provided zip file
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public static List<String> readZipFileNames(String filepath) throws FileNotFoundException, IOException {

		logger.debug("Reading zip file contents: {}", filepath);

		if (IOUtils.checkExtension(FileType.ZIP, filepath)) {
			List<String> zipFiles = new ArrayList<String>();

			ZipEntry zipEntry = null;

			try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(filepath))) {

				// loop over each file
				while ((zipEntry = zipInputStream.getNextEntry()) != null) {

					// add the filename
					zipFiles.add(zipEntry.getName());
				}
			}

			logger.debug("Successfully read the zip file: {}", filepath);
			return zipFiles;
		}

		logger.error("Provided filepath: {} does not point to a valid zip file", filepath);
		throw new IOException("Provided filepath: " + filepath + " does not point to a valid zip file");
	}

	/**
	 * Writes a JSON array to file
	 * 
	 * @param obj
	 * @param filepath
	 * @throws JSONIOException
	 */
	public static void writeJSONToFile(JSONArray obj, String filepath) throws JSONIOException {
		try {
			writeStringToFile(obj.toString(), filepath);
		} catch (IOException e) {
			logger.error("Cannot write JSONArray to file: {}", filepath, e);
			throw new JSONIOException("Cannot write JSONArray to file:" + filepath, e);
		}
	}

	/**
	 * Writes a String to file
	 * 
	 * @param data
	 * @param filepath
	 * @throws IOException
	 */
	public static void writeStringToFile(String data, String filepath) throws IOException {
		File file = new File(filepath);
		if (file.getParentFile() != null && !file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(filepath))) {
			bw.write(data);
		}
	}

	/**
	 * Prevents the class being externally created
	 */
	private IOUtils() {
		return;
	}

	/**
	 * Simple method to add an extension onto a filename
	 * 
	 * @param fileName
	 * @param fileType
	 * @return the filename with the added extension
	 */
	public static String addExtension(String fileName, FileType fileType) {
		if (fileName.endsWith(PERIOD)) {
			return fileName + fileType.getExtension();
		}
		return fileName + PERIOD + fileType.getExtension();
	}
}
