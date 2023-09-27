## katalon >> Test Suite Data binding(10 rows) >> parallel execution(3Thread) with 1-3, 4-6. & 7-10 rows

We want to introduce you to a powerful solution that streamlines parallel execution with test suite data binding in Katalon Studio. This feature allows you to run the same test suite with multiple sets of data concurrently, ultimately saving you a significant amount of time during your testing process.

**The Challenge:**

In Katalon Studio, when you have a test suite running tests with data binding, especially with a large number of data rows (e.g., 20-30 rows), the overall execution time can become quite lengthy. Traditional execution methods may not be efficient in such scenarios.

**The Solution:**

We've developed a utility that simplifies and enhances the parallel execution process, reducing manual intervention and making it easier than ever to handle changes in data files or adjust the level of parallelism.

**Key Features:**

* Update data files based on specific conditions.
* Eliminate the need to create test suites manually.
* Automatically bind test suites with data files.
* Dynamically manage test suite collections for parallel execution.
* Adjust the maximum parallel instance count effortlessly.

**How to Use the Utility:**

We've streamlined the process into a few simple steps:

**Prerequisite:** Ensure you have a test suite with data binding already set up.

* Record a Test Case.
* Prepare a data file.
* Create a test suite.
* Bind the test suite with data files.
* Create a test suite collection and select the maximum parallel instance count.

**Running Tests in Parallel (For DBT):**

* Checkout the code from here
* Access the "TC DDT" test cases.
* Open the "Build data binding for parallel execution" test case.
* Update the "strTestSuiteCollectionPath" variable with your desired Test Suite Collection path.
* Run the test case, wait for completion, and refresh the project.
* Open the test suite collection and execute it.
* Verify the execution.

With this utility, you can seamlessly manage your test suite data binding and parallel execution needs, eliminating the complexities and time-consuming tasks that previously hindered your testing processes.

We believe this utility will greatly enhance your testing efficiency, and we encourage you to explore its capabilities in your Katalon Studio projects.

