# com.java.google.sheet
This Utility is for extracting data from google sheet using OAuth, GoogleSheetApi.


Pre-requisite : Steps before developing a utility to connect to the google sheet:
1) First create a google project to enable access to google api & services, here we can filter which api or services are to be accessible in this project.
2) then create a OAuth consent screen , enabling who all should have access to these apis or services.
3) create a oauth credential for the project in step-1, which will give you cliend id, userId, security token in a json format, which can be used in the utility class to make a successful connect to google api. 

Now Implementation:
1) Fetch the credentials from the download json.
2) create googlecredential object
3) define the api scope to determine the apicodeflow and saving it offline until it expires.
4) fetching all the google sheets accessible to the third party account which was used for authorization.
5) Use the fetched API for data manipulation.

For directly using the utility, just download "extractResponse.zip" and follow the instructions in the ReadMe.txt file. 