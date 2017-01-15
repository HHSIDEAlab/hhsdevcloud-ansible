// 
// Configure Jenkins - Config AWS Credentials
// 
// Adds a credentials store entry with an AWS access key and secret, which builds can use.


// These are the basic imports that Jenkin's interactive script console 
// automatically includes.
import jenkins.*;
import jenkins.model.*;
import hudson.*;
import hudson.model.*;

println("Configuring AWS Credentials...")

// Ensure that Jenkins' credentials store has the specified entry.
def createOrUpdateAwsCredentials = { id, description, accessKey, secretKey ->
	domain = com.cloudbees.plugins.credentials.domains.Domain.global()
	credsStore = Jenkins.instance.getExtensionList("com.cloudbees.plugins.credentials.SystemCredentialsProvider")[0].getStore()
	
	secrets = com.cloudbees.plugins.credentials.CredentialsProvider.lookupCredentials(
		com.cloudbees.jenkins.plugins.awscredentials.AmazonWebServicesCredentials.class,
		Jenkins.instance,
		hudson.security.ACL.SYSTEM
	)
	
	matchingSecret = secrets.find { secret -> secret.id == id }
	iamRoleArn = ""
	iamMfaSerialNumber = ""
	newSecret = new com.cloudbees.jenkins.plugins.awscredentials.AWSCredentialsImpl(
		com.cloudbees.plugins.credentials.CredentialsScope.GLOBAL,
		id,
		accessKey,
		secretKey,
		description,
		iamRoleArn,
		iamMfaSerialNumber
	)
	
	if (matchingSecret) {
		println "Secret found: ${matchingSecret.id}"
		
		updateResult = credsStore.updateCredentials(domain, matchingSecret, newSecret)
		
		if (updateResult) {
			println "Secret updated: ${id}" 
		} else {
			println "Secret failed to update: ${id}"
		}
	} else {
		println "Secret not found: ${id}"
		credsStore.addCredentials(domain, newSecret)
		println "Secret created: ${id}"
	}
}

credentialsId = "aws-credentials-builds"
createOrUpdateAwsCredentials(
	credentialsId, 
	"AWS credentials for HHS IDEA Lab builds.", 
	'{{ vault_jenkins_aws_access_key }}',
	'{{ vault_jenkins_aws_secret_key }}'
)

println("Configured AWS Credentials.")
