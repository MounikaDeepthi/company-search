# Company-Search

* Swagger URL : http://localhost:8080/swagger-ui/index.html
* H2 Console URL : http://localhost:8080/h2-console/
* H2 Console login Details : refer properties file
* X-API-Key : used for authenticating TruProxy APIs but not as authenticator for customer search api.
* IsActive(optional field, default false): true to fetch only active companies, returns all active and inactive companies if flag is false.

## Goal
Create a company search application using Spring Boot 3.1.3 or higher.

Expose an endpoint that uses the `TruProxyAPI` to do a company and officer lookup 
via name or registration number.

## Criteria
* The result of the search is returned as JSON
* A request parameter has to be added to decide whether only active companies should be returned
* The officers of each company have to be included in the company details (new field `officers`) 
* Only include officers that are active (`resigned_on` is not present in that case)
* Paging can be ignored
* Please add unit tests and integrations tests, e.g. using WireMock to mock `TruProxyAPI` calls
* Save the companies (by `company_number`) and its officers and addresses in a database and return the result from there if the endpoint is called with `companyNumber`.

##Expected Request

* The name and registration/company number are passed in via body
* The API key is passed in via header `x-api-key`
* If both fields are provided `companyNumber` is used