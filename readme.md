## An example project to create oauth2.0 client for sso.gov.mn

This example uses [scribejava](https://github.com/scribejava/scribejava) oauth library.

Modify `src/resources/danScope.json` for required web services from XYP data.

### Prerequisite
- Maven 3+
- Java 7+

### Run
```bash
mvn clean install 
export CLIENT_ID=<ID>
export CLIENT_SECRET=<SECRET>
export CALLBACK_URL=<URL>
mvn exec:java
```