// 
// Configure Plugin - GitHub API
// 
// Configures the GitHub API so that Jenkins has the config and credentials it
// needs to create GitHub hooks, etc.
// 
// References:
// * https://gist.github.com/chrisvire/383a2c7b7cfb3f55df6a
// * http://stackoverflow.com/questions/32208763/update-jenkins-credentials-by-script


// These are the basic imports that Jenkin's interactive script console 
// automatically includes.
import jenkins.*;
import jenkins.model.*;
import hudson.*;
import hudson.model.*;


//
// Create/Update Credential for GitHub Access Token
//

def createOrUpdateSecret = { id, description, secretValue ->
	domain = com.cloudbees.plugins.credentials.domains.Domain.global()
	credsStore = Jenkins.instance.getExtensionList("com.cloudbees.plugins.credentials.SystemCredentialsProvider")[0].getStore()
	
	secrets = com.cloudbees.plugins.credentials.CredentialsProvider.lookupCredentials(
		org.jenkinsci.plugins.plaincredentials.StringCredentials.class,
		Jenkins.instance
	)
	
	matchingSecret = secrets.find { secret -> secret.id == id }
	newSecret = new org.jenkinsci.plugins.plaincredentials.impl.StringCredentialsImpl(
		com.cloudbees.plugins.credentials.CredentialsScope.GLOBAL,
		id,
		description,
		hudson.util.Secret.fromString(secretValue)
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

credentialsId = "github-token-karlmdavis"
createOrUpdateSecret(
	credentialsId, 
	"GitHub 'Personal Access Token' for karlmdavis, with access to the 'HHSIDEAlab' team and the 'admin:repo_hook' permission.", 
	"{{ jenkins_github_token_karlmdavis }}"
)


// 
// Configure GitHub API Plugin
// 

githubPluginConfig = Jenkins.instance.getDescriptor(org.jenkinsci.plugins.github.config.GitHubPluginConfig.class)

githubServerConfig = githubPluginConfig.configs.find { server -> server.credentialsId == credentialsId }
if(githubServerConfig) {
	println("GitHub server config found: ${githubServerConfig}")
} else {
	println("GitHub server config not found.")
	githubPluginConfig.configs += new org.jenkinsci.plugins.github.config.GitHubServerConfig(credentialsId)
	githubPluginConfig.save()
	println("GitHub server config added.")
}
