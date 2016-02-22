Setting Up a JIRA Server
------------------------

Applying this role will setup a host as a JIRA server. Much of this role's work was inspired by the [afklm/jira](https://github.com/afklm/jira) Chef cookbook.

## Manual Configuration

Some configuration settings will need to be entered manually:

1. Browse to <http://issues.hhsdevcloud.us>.
    1. On the *Set up application properties* screen:
        1. *Application Title*: `HHS IDEA Lab`
        1. *Mode*: **Private**
        1. *Base URL*: `http://issues.hhsdevcloud.us`
        1. Click **Next**.
    1. On the *Specify your license key* screen:
        1. *Your License Key*: Enter the license to be used for this server. For the production instance running in the HHS Dev Cloud, a 10-user license purchased by Karl M. Davis was used.
    1. On the *Set up administrator account* screen:
        1. Specify the first administrator account to start with. For the production instance running in the HHS Dev Cloud, an `admin` account for was created here (Karl M. Davis has the password for this, if needed).
    1. *Set up email notifications*
        1. *Configure Email Notifications*: **Now**
        1. *Name*: `Amazon SES SMTP`
        1. *From address*: `karl.davis@cms.hhs.gov`
        1. *Host Name*: `email-smtp.us-east-1.amazonaws.com`
            * For the `us-east-1` AWS region, per <http://docs.aws.amazon.com/ses/latest/DeveloperGuide/smtp-connect.html>.
        1. *TLS*: **enabled**
        1. *Username* and *Password*: Generate an SES SMTP username and password per the "Obtaining Amazon SES SMTP Credentials Using the Amazon SES Console" section on <http://docs.aws.amazon.com/ses/latest/DeveloperGuide/smtp-credentials.html> and enter them into the fields here.
        1. Click **Finish**.
    1. Continue through the personal preferences setup and tour.
    1. On the *Your first project* screen:
        1. *Project name*: `CMS Blue Button`
        1. *Key*: `CMSBBTN`
        1. Click **Create Project**.
1. Configure LDAP authentication, as described here: <https://confluence.atlassian.com/jira/connecting-to-an-ldap-directory-229838527.html>:
    1. Login as an admin user.
    1. Open **JIRA Administration > User Management**.
    1. Select **User Directories**.
    1. Click **Add Directory**, select **LDAP**, and then click **Next**.
    1. On the *Configure LDAP User Directory* screen:
        1. *Name*: `ldap.hhsdevcloud.us`
        1. *Directory Type*: **OpenLDAP** TODO: verify
        1. *Hostname*: `ldap.hhsdevcloud.us`
        1. *Username*: `cn=jira,ou=services,dc=hhsdevcloud,dc=us`
        1. *Password*: (the `vault_ldap_jira_password` value, which can be obtained by running `ansible-vault view group_vars/all/vault.yml`)
        1. *Base DN*: `dc=hhsdevcloud,dc=us`
        1. *Additional User DN*: `ou=people`
        1. *Additional Group DN*: `ou=groups`
        1. *LDAP Permissions*: **Read Only, with Local Groups**
        1. *Default Group Memberships*: `jira-software-users`
        1. *User Schema Settings > User Name Attribute*: `uid`
        1. *User Schema Settings > User Unique ID Attribute*: (should be blank)
        1. Click **Save and Test**.

## Troubleshooting: `file` module fails with `EEXISTS` error

See the workaround here: <https://github.com/ansible/ansible-modules-core/issues/2473#issuecomment-177981517>.

## Troubleshooting: JIRA Unable to Send Outbound Email

By default, the AWS account was configured in "sandbox" mode, which prevented the use of AWS SES to send email to non-verified domains or addresses. Since I can't add DNS records to the `cms.hhs.gov` zone, this was a blocker. The **SES Management Console > Sending Statistics** page had a button to **Request a Sending Limit Increase**. Had to go through that process to get things unblocked.

