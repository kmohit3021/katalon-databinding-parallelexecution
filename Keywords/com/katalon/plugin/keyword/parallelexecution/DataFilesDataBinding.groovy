package com.katalon.plugin.keyword.parallelexecution

import java.nio.file.*;
import java.text.MessageFormat
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.configuration.RunConfiguration
import com.kms.katalon.core.logging.KeywordLogger;
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testobject.ObjectRepository


public class DataFilesDataBinding {

	private KeywordLogger logger = KeywordLogger.getInstance(ObjectRepository.class);
	String strProjectPath = RunConfiguration.getProjectDir()

	@Keyword
	public void buildDataBindingParallelExecution(String testsuitecollectionpath) {

		String strTestSuiteCollectionPath = ('/' + testsuitecollectionpath) + '.ts'

		cleanExistingSetup(strProjectPath + strTestSuiteCollectionPath)

		String executionMode = getValueBetweenXmlTags(strProjectPath + strTestSuiteCollectionPath, 'executionMode')


		String maxConcurrentInstances = getValueBetweenXmlTags(strProjectPath + strTestSuiteCollectionPath, 'maxConcurrentInstances')


		String testSuiteEntity = getValueBetweenXmlTags(strProjectPath + strTestSuiteCollectionPath, 'testSuiteEntity')


		String testDataId = getValueBetweenXmlTags(((strProjectPath + '/') + testSuiteEntity) + '.ts', 'testDataId')


		String rowcount = countXmlTagsInFile(((strProjectPath + '/') + testDataId) + '.dat', 'data')


		List<String> lstDuplicateFiles

		if (Integer.parseInt(rowcount) < Integer.parseInt(maxConcurrentInstances)) {
			lstDuplicateFiles = createMultipalFiles(((strProjectPath + '/') + testSuiteEntity) + '.ts', Integer.parseInt(rowcount))
		} else {
			lstDuplicateFiles = createMultipalFiles(((strProjectPath + '/') + testSuiteEntity) + '.ts', Integer.parseInt(maxConcurrentInstances))
		}


		List<String> lstIterationTypeValue = generateIiterationTypeAndValue(rowcount, maxConcurrentInstances)


		updateValuesBetweenTags(lstDuplicateFiles, lstIterationTypeValue, Integer.toString(lstDuplicateFiles.size()))

		List<String> resultList = extractTestSuiteFromContent(lstDuplicateFiles)


		String content = ''

		String baseContent = ''

		for (int i = 1; i < resultList.size(); i++) {
			if ((i + 1) == resultList.size()) {
				baseContent = (('<TestSuiteRunConfiguration>\n<configuration>\n<groupName>Web Desktop</groupName>\n<profileName>default</profileName>\n<requireConfigurationData>false</requireConfigurationData>\n<runConfigurationId>Chrome</runConfigurationId>\n</configuration>\n<runEnabled>true</runEnabled>\n<testSuiteEntity>' +
						resultList.get(i)) + '</testSuiteEntity>\n</TestSuiteRunConfiguration>')
			} else {
				baseContent = (('<TestSuiteRunConfiguration>\n<configuration>\n<groupName>Web Desktop</groupName>\n<profileName>default</profileName>\n<requireConfigurationData>false</requireConfigurationData>\n<runConfigurationId>Chrome</runConfigurationId>\n</configuration>\n<runEnabled>true</runEnabled>\n<testSuiteEntity>' +
						resultList.get(i)) + '</testSuiteEntity>\n</TestSuiteRunConfiguration>\n')
			}

			content == baseContent

			content = (content + baseContent)
		}

		addNewDataIntoXMLFile(strProjectPath + strTestSuiteCollectionPath,'</TestSuiteRunConfiguration>', content)
	}

	@Keyword
	public void addDataIntoInternalDataFile(String strTestSuiteCollectionPath, String dataset) {
		String strTestSuiteCollection = ('/' + strTestSuiteCollectionPath) + '.ts'
		String testSuiteEntity = getValueBetweenXmlTags(strProjectPath + strTestSuiteCollection, 'testSuiteEntity')
		String testDataId = getValueBetweenXmlTags(((strProjectPath + '/') + testSuiteEntity) + '.ts', 'testDataId')
		addNewDataIntoXMLFile(((strProjectPath + '/') + testDataId) + '.dat', '</data>', dataset)
	}

	@Keyword
	public void addDataIntoInternalDataFile(String strDataFilePath, String dataset, FailureHandling flowControl) {
		addNewDataIntoXMLFile(((strProjectPath + '/') + strDataFilePath) + '.dat', '</data>', dataset)
	}

	/*
	 * This function is responsible updating Test Case and Data files on the basic of tag Name
	 */
	//@Keyword
	public void addNewDataIntoXMLFile(String datFilePath, String tagName, String newdata) {
		try {
			addRowBelowDataTag(datFilePath, tagName, newdata);
			logger.logDebug("New row added successfully!")
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void addRowBelowDataTag(String filePath, String tagName, String newRow) throws IOException {
		// Read the existing content of the .dat file
		StringBuilder fileContent = new StringBuilder();
		BufferedReader reader = new BufferedReader(new FileReader(filePath));
		String line;

		while ((line = reader.readLine()) != null) {
			fileContent.append(line).append("\n");
		}
		reader.close();

		// Find the "<data>" tag and insert the new row below it
		String existingContent = fileContent.toString();
		int dataIndex = existingContent.indexOf(tagName);

		if (dataIndex != -1) {
			// Insert the new row just below the "<data>" tag
			int insertIndex = dataIndex + tagName.length();
			String newContent = existingContent.substring(0, insertIndex) + "\n" + newRow + existingContent.substring(insertIndex);

			// Write the modified content back to the .dat file
			BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
			writer.write(newContent);
			writer.close();
		} else {
			throw new IOException("'<data>' tag not found in the .dat file.");
		}
	}

	/*
	 * This function is responsible for extracting value from the Test Suite collection, Test Suite and Data files on the basic of tag Name
	 */

	//@Keyword
	public String getValueBetweenXmlTags(String filePath, String tagName) {
		String value='';
		try {
			File file = new File(filePath); // Replace with the path to your .dat file
			InputStream inputStream = new FileInputStream(file);

			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(inputStream);

			Element rootElement = document.getDocumentElement();
			NodeList nodeList = rootElement.getElementsByTagName(tagName); // Replace with your XML tag

			if (nodeList.getLength() > 0) {
				value = nodeList.item(0).getTextContent();
				logger.logDebug("Value between tags "+tagName+": " + value)
			} else {
				logger.logDebug("Tag not found in the XML.")
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}


	/*
	 * This function is responsible for counting tags from the given file
	 */

	//@Keyword
	public int countXmlTagsInFile(String filePath, String tagName) throws IOException {
		int count = 0;
		BufferedReader reader = new BufferedReader(new FileReader(filePath));
		String line;

		Pattern pattern = Pattern.compile("<" + tagName + ">(.*)</" + tagName + ">");

		while ((line = reader.readLine()) != null) {
			Matcher matcher = pattern.matcher(line);
			while (matcher.find()) {
				count++;
			}
		}
		logger.logDebug("Total tag count in XML."+ count)
		reader.close();
		return count;
	}

	/*
	 * This function is responsible to create multiple test suite for parallel execution
	 */

	//@Keyword
	public List<String> createMultipalFiles(String sourceFilePath, int numberOfDuplicates) {
		String testSuiteName='';
		Pattern pattern = Pattern.compile(".*/(.*?)\\.ts");
		Matcher matcher = pattern.matcher(sourceFilePath);
		if (matcher.find()) {
			testSuiteName = matcher.group(1);
		} else {
		}


		List<String> lstDuplicateFiles = new ArrayList<>();
		lstDuplicateFiles.add(sourceFilePath);
		try {
			Path sourcePath = Paths.get(sourceFilePath);

			// Check if the source file exists
			if (!Files.exists(sourcePath)) {
				logger.logDebug("Source file does not exist.")
				return;
			}

			// Get the parent directory of the source file
			Path parentDirectory = sourcePath.getParent();

			// Create duplicate copies of the file
			for (int i = 1; i <= numberOfDuplicates-1; i++) {
				String duplicateFileName = testSuiteName+"_New_" + i + ".ts";
				Path duplicatePath = parentDirectory.resolve(duplicateFileName);

				Files.copy(sourcePath, duplicatePath, StandardCopyOption.REPLACE_EXISTING);
				logger.logDebug("Duplicate copy " + i + " created: " + duplicatePath.toString())
				replaceTextInFile(duplicatePath.toString(), testSuiteName, testSuiteName+"_New_" + i);
				lstDuplicateFiles.add(duplicatePath.toString());
			}

			logger.logDebug("Duplicates created successfully.")
		} catch (IOException e) {
			e.printStackTrace();
		}
		return lstDuplicateFiles;
	}

	public void replaceTextInFile(String filePath, String searchText, String replacementText) throws IOException {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(filePath));
			StringBuilder content = new StringBuilder();
			String line;

			// Read the file line by line and make replacements
			while ((line = reader.readLine()) != null) {
				line = line.replace(searchText, replacementText);
				content.append(line).append("\n");
			}

			reader.close();

			// Write the updated content back to the file
			BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
			writer.write(content.toString());
			writer.close();
			logger.logDebug("Text replaced successfully.")
		}catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * This function is responsible to the Range and lstIterationTypeValue on the base of Datafile and Parallel run count
	 */

	//@Keyword
	public List<String> generateIiterationTypeAndValue(String rowCount, String maxCount) {
		int maxIteration = Integer.parseInt(maxCount);
		int dataCount = Integer.parseInt(rowCount);
		List<String> lstIterationTypeValue = new ArrayList<>();

		int quotient = dataCount / maxIteration;
		int remainder = dataCount % maxIteration;
		int count=0;

		if(dataCount <= maxIteration)
		{
			for(int i=1;i<=dataCount;i++) {
				lstIterationTypeValue.add("SPECIFIC");
				lstIterationTypeValue.add(Integer.toString(i));

			}
		}
		else
		{

			List<Integer> partitions = partitionNumber(dataCount, maxIteration)

			lstIterationTypeValue = partitionList(partitions, maxIteration)
		}

		return lstIterationTypeValue;
	}


	/*
	 * This function is responsible to update the value for the created test suite for the parallel execution
	 */

	//@Keyword
	public void updateValuesBetweenTags(List<String> lstDuplicateFiles, List<String> lstIterationTypeValue, String maxCount) {
		int iterationCount = Integer.parseInt(maxCount);
		int j=0;
		for(int i=0;i<iterationCount;i++) {
			try {
				updateFirstValueTag(lstDuplicateFiles.get(i), "iterationType", lstIterationTypeValue.get(i+j));
				updateFirstValueTag(lstDuplicateFiles.get(i), "value", lstIterationTypeValue.get(i+j+1));
				logger.logDebug("File updated successfully.")
				j=j+1;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public String updateTagValue(String filePath, String tagToModify, String newValue) throws IOException {
		StringBuilder content = new StringBuilder();
		BufferedReader reader = new BufferedReader(new FileReader(filePath));
		String line;
		boolean insideTag = false;

		while ((line = reader.readLine()) != null) {
			if (line.contains("<" + tagToModify + ">")) {
				insideTag = true;
				line = line.replaceAll("<" + tagToModify + ">.*?</" + tagToModify + ">", "<" + tagToModify + ">" + newValue + "</" + tagToModify + ">");
			} else if (insideTag && line.contains("</" + tagToModify + ">")) {
				insideTag = false;
			}

			content.append(line).append("\n");
		}

		reader.close();
		return content.toString();
	}


	//@Keyword
	public updateFirstValueTag(String filePath, String tagName, String newValue) throws IOException {
		StringBuilder content = new StringBuilder();
		BufferedReader reader = new BufferedReader(new FileReader(filePath));
		String line;
		boolean firstValueTagFound = false;
		while ((line = reader.readLine()) != null) {
			// Check if the line contains a <value> tag
			if (line.contains("<"+tagName+">")) {
				// If it's the first occurrence of <value> tag, update the content
				if (!firstValueTagFound) {
					String pattern = MessageFormat.format("<{0}>(.*?)</{0}>", tagName);
					String replacement = MessageFormat.format("<{0}>{1}</{0}>", tagName, newValue);
					if (Pattern.compile(pattern).matcher(line).find()) {
						line = line.replaceAll(pattern, replacement);
						firstValueTagFound = true;
					}
				}
			}
			content.append(line).append("\n");
		}
		reader.close();
		writeUpdatedContentToFile(filePath, content.toString())
	}

	public void writeUpdatedContentToFile(String filePath, String updatedContent) throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
		writer.write(updatedContent);
		writer.close();
	}

	/*
	 * This function is responsible to remove unnecessary content for the string like file extension
	 */

	//@Keyword
	public List<String> removeFileExtension(List<String> fileList, String fileExtension) {
		List<String> resultList = new ArrayList<>();

		for (String item : fileList) {
			// Check if the string contains ".ts" and remove it
			if (item.endsWith(fileExtension)) {
				resultList.add(item.substring(0, item.length() - 3)); // Remove the last 3 characters (".ts")
			} else {
				// If the string doesn't contain ".ts," add it as is to the result list
				resultList.add(item);
			}
		}

		return resultList;
	}

	/*
	 * This function is responsible to remove unnecessary content for the string like system path
	 */
	//@Keyword
	public List<String> extractTestSuiteFromContent(List<String> fileList) {
		List<String> resultList = new ArrayList<>();
		for (int i=0; i<fileList.size(); i++) {
			Pattern p1 = Pattern.compile('(.*)Test Suites(.*).ts')

			Matcher m1 = p1.matcher(fileList.get(i))

			if (m1.find()) {
				resultList.add("Test Suites"+m1.group(2));
			}

		}
		return resultList;
	}

	/*
	 * This function is responsible to remove existing content from the test suite collect file
	 */
	//@Keyword
	public cleanExistingSetup(String filePathTSC)
	{
		deleteContentbetweenTags(filePathTSC);
	}

	public void deleteContentbetweenTags(String filePath) {
		try {
			// Read the input file
			BufferedReader reader = new BufferedReader(new FileReader(filePath));
			StringBuilder content = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				content.append(line).append("\n");
			}
			reader.close();

			// Define the regular expression pattern
			String regex = "</TestSuiteRunConfiguration>(.*?)</testSuiteRunConfigurations>";

			// Remove the content between the tags using regex
			Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
			Matcher matcher = pattern.matcher(content.toString());
			content = new StringBuilder(matcher.replaceAll("</TestSuiteRunConfiguration>\n</testSuiteRunConfigurations>"));

			// Write the modified content back to the output file
			BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
			writer.write(content.toString());
			writer.close();
			logger.logDebug("Content between tags removed successfully.")
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public List<Integer> partitionNumber(int number, int partitionCount) {
		List<Integer> partitions = new ArrayList()

		int remainder = number % partitionCount

		int quotient = number / partitionCount

		for (int i = 0; i < partitionCount; i++) {
			partitions.add(quotient)
		}

		for (int i = 0; i < remainder; i++) {
			partitions.set(i, partitions.get(i) + 1)
		}

		return partitions
	}

	public List<String> partitionList(List<Integer> myList, int partitionSize) {
		List<String> lstIterationTypeValue = new ArrayList()

		int listSize = myList.size()

		int count = 0

		int int1

		int int2

		for (int i = 0; i < listSize; i++) {
			if (i == 0) {
				lstIterationTypeValue.add("RANGE");
				int1 = 1
				int2 = (myList.get(i) + count)
				lstIterationTypeValue.add((int1 + '-') + int2)

				count = int2
			} else if (myList.get(i) == 1) {
				lstIterationTypeValue.add("SPECIFIC");
				lstIterationTypeValue.add(count + 1)
				count = count+1
			} else {
				lstIterationTypeValue.add("RANGE");
				int1 = (1 + int2)
				int2 = (myList.get(i) + count)
				lstIterationTypeValue.add((int1 + '-') + int2)

				count = int2
			}
		}

		return lstIterationTypeValue
	}

}
