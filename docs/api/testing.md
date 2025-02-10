# 1. Testing Strategy
Testing strategies aim to maximize confidence in our application's adherence to specifications while minimizing test execution time.

Below is the reasoning behind the testing strategy used in this backend:
- Business logic is usually the code that contains the most possible paths in the application, therefore most tests must be focused on this logic. All these tests must run quickly so unit tests are the most suited for this.
  - **=> Exhaustive unit testing of the domains**
- Exhaustive testing will lead to high coverage, but the tests can be of low quality, not detecting bugs in code. The best way to automatically assess test quality is by introducing many changes in the tested code and check that at least one test fails. Tests will be executed multiple times, so they have to be fast. The best candidate are domain unit tests. 
  - **=> Mutation testing on domain's unit tests**
- Mocks reduce the readability and the ease of maintenance of tests. If a dependency is mocked many times, it must be faked to solve mock problems. (more on fake vs mock [in this article](https://testing.googleblog.com/2024/02/increase-test-fidelity-by-avoiding-mocks.html?m=1))
  - **=> Fakes over mocks**
- Ports materialize a contract between the domain and the adapters. Ports are implemented as interfaces, it enforces the presence of methods with specific signatures, but it's not enough to guarantee the behavior required by the domain. This behavior can be checked with tests, against any adapter of the port, including the fakes.
  - **=> Contract tests for ports**
- We have to make sure that the endpoints effectively call the domain's use case and that the request and response are correctly mapped. For these tests to be as close to reality as possible, mockMvc is used and inputs/outputs are json strings, not objects.
  - **=> Endpoint-domain integration tests**
- Security, logging and global exception handling are cross cutting concerns. In order to respect the Single Responsability Principle, they must have their own tests instead of mixing it with other component tests.
  - **=> Isolated/Autonomous tests for cross-cutting concerns**
- Finally, we need a few tests to make ensure that the real components works well together in scenarios that ressemble the real use of the application, namely successive calls to the API running with real dependencies. This is particulary usefull for monolith backend. In a microservice architecture, it should probably be replaced with 
  - **=> End-to-end tests for key scenarios**

In summary, the testing strategy consists of:
- Exhaustive unit testing of the domains
- Mutation testing on domain's unit tests
- Fakes over mocks
- Contract tests for ports
- Endpoint-domain integration tests
- Isolated/Autonomous tests for cross-cutting concerns
- End-to-end tests for key scenarios

## 1.1. Exhaustive unit testing of the domains
Unit test should only target use cases. DO NOT test domain services or helpers directly.

Unit tests should be exhaustive and cover all edge cases supported by the use case. These tests must be considered as the source of truth of the specification of the use cases.

Unit tests must be fast. Each test must run in under 10ms.

It's recommended to create a test helper class for each use case whose role is to instantiate the use case using fake adapters. The fakes can be exposed as properties to participate in the test setup.

The quality of the tests of the domain are checked using mutation testing. But remember that it only covers **written code**: a score of 100 in mutation tests doesn't mean you haven't forgotten an edge case in your use case code :)

## 1.2. Fake adapters
Fakes adapters implement the port interface, but can provide any other public method deemed necessary for the tests. Ex: if a port doesn't define a findAll method, but tests need to know what entities are present in the repository, the fake can implement it directly.

DO NOT add methods in the port for the whole sake of tests.

## 1.3. Contract tests for ports
Check this article: https://principal-it.eu/2023/02/contract-tests/

For some (all?) ports, we create abstract test classes that define the wanted behavior that adapters must satisfy. There's at least one abstract method that return the instance of the adapter to test. For each adapter of the port, a subclass is created mainly to implement abstract methods needed by the contract tests.

Abstract contract test classes should not be aware of any implementation detail of the adapters. Ex: suppose we have a `ImageRepositoryPort` to persist images, if one of the adapters use a local folder to store the image, we cannot have the logic to empty the folder directly in `ImageRepositoryPortContractTests`. We should instead either declare an abstract method `cleanup` that each adapter test class will implement with the appropriate behavior and call it from the abstract class, or just add an `@AfterEach` or `@AfterAll` method in the adapter's sub test class.

## 1.4. Endpoint-domain integration tests
Those tests should be kept to a minimum. The main goal is the make sure that the raw request is correctly mapped and passed to the use case, and that the use case result is correctly mapped to the response.

For endpoint response body assertions:
- Always favor hardcoding the expected body string. It makes it easier to understand what exactly the response should look like, instead of a bunch of `%s` instead of the actual values (when using String.format to create the expected response). An exception can be made for generated IDs that cannot be controled. The consequence of this rule is that entities used in such tests must be deterministic.
- If the endpoint doesn't return a body, the test must explicitly ensure it.

These tests use the real domain objects with fake adapters.

Usually there's no need to test errors because:
- the domain throws exceptions if a business rule is violated
- global exception handler tests must verify the json response of the API for each application-defined exception.

# 2. Testing rules
- Make sure that each test method is self-describing, specially the setup/arrange/given phase. Any developer must be able to understand quickly what scenario is being tested without having to navigate to other methods or classes. You can still use helpers (it's actually more than recommanded!) to simplify tests, but their signature must be self-describing so that it's not required to read their implementation to understand what they do.
- The prod (non-test) code must not be altered just for the sake of the test. Ex: you must not change the visibility of a method or a field for the tests needs.

# 3. Test classes categorization and naming convention
`./mvnw verify` runs the tests in two successive batches:
- First, all unit tests are run in the `test` phase
- Then, all the other tests are run in the `integration-test` phase

Why ? To satisfy the Fail Fast principle: as the unit tests run fast, if one of them fails, you don't have to wait for all the slow tests to finish to get the feedback.

We use the test runner `surefire`. It's configured to distinguish between the unit tests and the other tests based on their filenames:
- files with suffix `IntegrationTests` or `E2ETests` are run during `integration-test` phase
- all other test files are run during the `test` phase

Hence using the right class name is important. Only three suffixes are allowed in this repository:
- `UnitTests` suffix for unit tests
- `IntegrationTests` suffix for integration tests
- `E2ETests` suffix for end-to-end tests
- `ContractTests` suffix for abstract contrat tests.
## 3.1. Integration tests
Test classes that does not extend the `BaseE2ETests` class and that are annotated with one of the following annotations are considered integration tests and must have the `IntegrationTests` suffix:
- `@DataJpaTest`
- `@Import`
- `@SetupDatabase`
- `@SpringBootTest`
- `@WebMvcTest`

Additionnaly, if your test triggers out-of-process communication, like reading from or writing to the disk, and it does not need any of the above annotations, you can force considering it as integration by using the custom `@IntegrationTest` in addition to the `IntegrationTests` suffix.
## 3.2. Unit tests
Unit tests must not use any of the above annotations.

All tests in the domain must be unit tests.
## 3.3. Contract tests
Contract tests are abstract classes that contains at least one method with the `@Test` annotation. They must have the `ContractTests` suffix.

These classes are not directly executed, but they provide test definitions for their subclasses, which can be either unit or integration tests.

Ex: Given a `ImageRepositoryPort` port for image persisting, and two adapters `FileSystemImageRepository` and `InMemoryImageRepository`, we will have:
- `ImageRepositoryPortContractTests` that defines the required behavior in form of tests
- `FileSystemImageRepositoryIntegrationTests` that inherits from the contract test class and run the tests against the `FileSystemImageRepository` adapter. It's considered as an integration test class because it reads/writes in the host disk.
- `InMemoryImageRepositoryUnitTests` that inherits from the contract test class and run the tests against the `InMemoryImageRepository` adapter. It's considered as a unit test class because it only reads/writes in the RAM.
