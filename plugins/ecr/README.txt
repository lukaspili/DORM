DORM ECR DAO

Request the dao using the REST api


Configuration :

ECR can be used with 2 persistence storage : h2 or postgresql.
There are some errors in the default h2 mode, lock timeout happens sometimes.

To switch to postgres, you need to edit the ECR config class : org.eclipse.ecr.application.Activator
and set the ECR_DATABASE value to "psql".

You need also create new database "nuxeo" with a user "nuxeo/nuxeo".
You can changes theses values in the file : db.properties
 

API :

Store :
curl -H Content-Type:application/xml -X POST --user admin:admin --data "<dormMetadata><name>thename</name><extensionName>maven</extensionName><version>1.0</version><properties><entry><key>groupId</key><value>org.foo</value></entry><entry><key>artifactId</key><value>bar</value></entry></properties></dormMetadata>" http://hostname:port/ecr/dorm/save

Get :
curl -X GET --user admin:admin http://hostname:port/ecr/dorm/get/extensionName/name/version