//
// Configure Jenkins Security: Authentication and Authorization
// 
// This script is pretty much taken verbatim from the samples at
// https://wiki.jenkins-ci.org/display/JENKINS/Github+OAuth+Plugin
//

// These are the basic imports that Jenkin's interactive script console 
// automatically includes.
import jenkins.*;
import jenkins.model.*;
import hudson.*;
import hudson.model.*;

// These are the imports required for this specific script.
import hudson.security.SecurityRealm
import hudson.security.AuthorizationStrategy
import org.jenkinsci.plugins.GithubSecurityRealm
import org.jenkinsci.plugins.GithubAuthorizationStrategy


//
// Configure Jenkins' Authentication
//

String githubWebUri = 'https://github.com'
String githubApiUri = 'https://api.github.com'
String oauthScopes = 'read:org,user:email'

// This is not a secret, but is specific to https://github.com/karlmdavis.
String clientID = '137139291cc47ba6088b'

// This is secret (paired with the 'clientID').
String clientSecret = '{{ jenkins_github_oauth_client_secret }}'

SecurityRealm github_realm = new GithubSecurityRealm(githubWebUri, githubApiUri, clientID, clientSecret, oauthScopes)

// Check for equality, no need to modify the runtime if no settings changed
if(!github_realm.equals(Jenkins.instance.getSecurityRealm())) {
	Jenkins.instance.setSecurityRealm(github_realm)
	Jenkins.instance.save()
}


//
// Configure Jenkins' Authorization
//

// Admin User Names
String adminUserNames = 'karlmdavis'
// Participant in Organization
String organizationNames = 'HHSIDEALab'
// Use Github repository permissions
boolean useRepositoryPermissions = true
// Grant READ permissions to all Authenticated Users
boolean authenticatedUserReadPermission = true
// Grant CREATE Job permissions to all Authenticated Users
boolean authenticatedUserCreateJobPermission = false
// Grant READ permissions for /github-webhook
boolean allowGithubWebHookPermission = true
// Grant READ permissions for /cc.xml
boolean allowCcTrayPermission = true
// Grant READ permissions for Anonymous Users
boolean allowAnonymousReadPermission = true
// Grant ViewStatus permissions for Anonymous Users
boolean allowAnonymousJobStatusPermission = true

AuthorizationStrategy github_authorization = new GithubAuthorizationStrategy(adminUserNames,
	authenticatedUserReadPermission,
	useRepositoryPermissions,
	authenticatedUserCreateJobPermission,
	organizationNames,
	allowGithubWebHookPermission,
	allowCcTrayPermission,
	allowAnonymousReadPermission,
	allowAnonymousJobStatusPermission)

// Check for equality, no need to modify the runtime if no settings changed
if(!github_authorization.equals(Jenkins.instance.getAuthorizationStrategy())) {
	Jenkins.instance.setAuthorizationStrategy(github_authorization)
	Jenkins.instance.save()
}
