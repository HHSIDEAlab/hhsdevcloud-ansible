// 
// Configure Plugin - Config File Provider
// 
// Installs a Maven installation named "maven-3" if one is not already found.
// References:
// * https://github.com/jenkinsci/config-file-provider-plugin/blob/master/src/test/java/org/jenkinsci/plugins/configfiles/buildwrapper/ConfigFileBuildWrapperWorkflowTest.java


// These are the basic imports that Jenkin's interactive script console 
// automatically includes.
import jenkins.*;
import jenkins.model.*;
import hudson.*;
import hudson.model.*;

println("Configuring Config File Provider Plugin...")

// Verify that Jenkins' credentials store has the OSSRH login.
def createOrUpdateUsernamePasswordCredentials = { id, description, username, password ->
	domain = com.cloudbees.plugins.credentials.domains.Domain.global()
	credsStore = Jenkins.instance.getExtensionList("com.cloudbees.plugins.credentials.SystemCredentialsProvider")[0].getStore()
	
	secrets = com.cloudbees.plugins.credentials.CredentialsProvider.lookupCredentials(
		com.cloudbees.plugins.credentials.common.UsernamePasswordCredentials.class,
		Jenkins.instance
	)
	
	matchingSecret = secrets.find { secret -> secret.id == id }
	newSecret = new com.cloudbees.plugins.credentials.impl.UsernamePasswordCredentialsImpl(
		com.cloudbees.plugins.credentials.CredentialsScope.GLOBAL,
		id,
		description,
		username,
		password
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

credentialsId = "ossrh-karlmdavis"
createOrUpdateUsernamePasswordCredentials(
	credentialsId, 
	"OSSRH login for Karl M. Davis, which can be used to upload gov.hhs.cms.bluebutton artifacts.", 
	'{{ jenkins_ossrh_username }}',
	'{{ jenkins_ossrh_password }}'
)

// Grab the provider for Maven settings.xml files.
settingsFileProvider = org.jenkinsci.lib.configprovider.ConfigProvider
	.getByIdOrNull(org.jenkinsci.plugins.configfiles.maven.MavenSettingsConfig.class.getName())

// Check for an existing config file.
settingsConfigId = settingsFileProvider.getProviderId() + ":cms-bluebutton-settings-xml"
settingsConfig = settingsFileProvider.getConfigById(settingsConfigId)

// Create a new, correct config file object, for use below.
settingsConfigTemplate = settingsFileProvider.newConfig()
settingsConfigNew = new org.jenkinsci.plugins.configfiles.maven.MavenSettingsConfig(
	settingsConfigId,
	"CMS Blue Button settings.xml",
	"",
	settingsConfigTemplate.content,
	true,
	[new org.jenkinsci.plugins.configfiles.maven.security.ServerCredentialMapping("ossrh", credentialsId)]
)

// Update or create the config file.
if(settingsConfig != null) {
	println("Found config file ${settingsConfigId}. Replacing...")
	settingsFileProvider.remove(settingsConfigId)
	settingsFileProvider.save(settingsConfigNew)
	println("Replaced config file ${settingsConfigId}.")
} else {
	println("Did not find config file ${settingsConfigId}. Creating...")
	settingsFileProvider.save(settingsConfigNew)
	println("Created config file ${settingsConfigId}.")
}
