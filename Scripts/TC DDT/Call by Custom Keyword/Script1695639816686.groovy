import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import com.kms.katalon.core.testng.keyword.TestNGBuiltinKeywords as TestNGKW
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import internal.GlobalVariable as GlobalVariable

not_run: CustomKeywords.'com.katalon.plugin.keyword.parallelexecution.DataFilesDataBinding.addDataIntoInternalDataFile'(
    GlobalVariable.TEST_SUITE_COLLECTION_NAME, '<data>Hello Katalon</data>')

not_run: CustomKeywords.'com.katalon.plugin.keyword.parallelexecution.DataFilesDataBinding.addDataIntoInternalDataFile'(
    GlobalVariable.TEST_DATAFILE_NAME, '<data>Hello MohitSharma</data>', FailureHandling.CONTINUE_ON_FAILURE)

not_run: CustomKeywords.'com.katalon.plugin.keyword.parallelexecution.DataFilesDataBinding.buildDataBindingParallelExecution'(
	GlobalVariable.TEST_SUITE_COLLECTION_NAME)


not_run: CustomKeywords.'com.katalon.plugin.keyword.parallelexecution.DataFilesDataBinding.buildDataBindingParallelExecution'(
	'Test Suites/TS DDT/Test Suite Collection 001')

//CustomKeywords.'com.katalon.plugin.keyword.parallelexecution.DataFilesDataBinding.excelRowCount'('/Users/mohit/git/katalon-databinding-parallelexecution/TestData/ExcelData.xlsx', 'login')
not_run: CustomKeywords.'com.katalon.plugin.keyword.parallelexecution.DataFilesDataBinding.buildDataBindingParallelExecution'(
	'Test Suites/TS DDT/Test Suite Collection 002 Excel')

CustomKeywords.'com.katalon.plugin.keyword.parallelexecution.DataFilesDataBinding.buildDataBindingParallelExecution'('Test Suites/TS DDT/Test Suite Collection 003 CSV')
