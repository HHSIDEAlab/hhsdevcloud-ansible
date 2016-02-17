Setting Up a JIRA Server
------------------------

Applying this role will setup a host as a JIRA server. Much of this role's work was inspired by the [afklm/jira](https://github.com/afklm/jira) Chef cookbook.

## Manual Configuration

Some configuration settings will need to be entered manually:

1. Browse to the "Public DNS" address of the `jira` EC2 instance on port `8081`, e.g. <http://http://ec2-123-456-789-123.compute-1.amazonaws.com/:8081/>. This will likely require you to first manually create and activate a security group that allows traffic on that port to that instance. (JIRA refuses to serve requests over a proxy until it has finished first-run configuration.)
    1. On the *Set up application properties* screen:
        1. *Application Title*: `HHS IDEA Lab`
        1. *Mode*: **Private**
        1. *Base URL*: `http://issues.hhsdevcloud.us`
        1. Click **Next**.
    1. On the *Specify your license key* screen:
        1. *Your License Key*: Enter the license to be used for this server. For the production instance running in the HHS Dev Cloud, a 10-user license purchased by Karl M. Davis was used.
    1. On the *Set up administrator account* screen:
        1. Specify the first administrator account to start with. For the production instance running in the HHS Dev Cloud, a `karlmdavis` account for Karl M. Davis was created here.
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

## Troubleshooting: `file` module fails with `EEXISTS` error

See the workaround here: <https://github.com/ansible/ansible-modules-core/issues/2473#issuecomment-177981517>.

