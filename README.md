HHS Dev Cloud Ansible Repo
--------------------------

This repository contains the Ansible provisioning, roles, etc. used in the HHS Dev Cloud.

## Development Environment

In order to use and/or modify this repository, a number of tools need to be installed.

### Python

This project requires Python 2.7. It can be installed as follows:

    $ sudo apt-get install python

The following packages are also required by some of the Python modules that will be used:

    $ sudo apt-get install libpq-dev

### virtualenv

This project has some dependencies that have to be installed via `pip` (as opposed to `apt-get`). Accordingly, it's strongly recommended that you make use of a [Python virtual environment](http://docs.python-guide.org/en/latest/dev/virtualenvs/) to manage those dependencies.

If it isn't already installed, install the `virtualenv` package. On Ubuntu, this is best done via:

    $ sudo apt-get install python-virtualenv

Next, create a virtual environment for this project and install the project's dependencies into it:

    $ cd hhsdevcloud-ansible.git
    $ virtualenv -p /usr/bin/python2.7 venv
    $ source venv/bin/activate
    $ pip install -r requirements.txt

The `source` command above will need to be run every time you open a new terminal to work on this project.

Be sure to update the `requirements.txt` file after `pip install`ing a new dependency for this project:

    $ pip freeze > requirements.txt

### Ansible Roles

Run the following command to download and install the roles required by this project into `~/.ansible/roles/`:

    $ ansible-galaxy install -r install_roles.yml

### AWS Credentials

Per <http://blogs.aws.amazon.com/security/post/Tx3D6U6WSFGOK2H/A-New-and-Standardized-Way-to-Manage-Credentials-in-the-AWS-SDKs>, create `~/.aws/credentials` and populate it with your AWS access key and secret (obtained from IAM):

    [default]
    # These AWS keys are for the john.smith@cms.hhs.gov account.
    aws_access_key_id = secret
    aws_secret_access_key = supersecret

Ensure that the EC2 key to be used is loaded into SSH Agent:

    $ ssh-add foo.pem

## Provisioning and Configuring AWS Resources

Running the following command will provision and configure all of the AWS resources specified in `site.yml`:

    $ ansible-playbook site.yml --extra-vars "ec2_key_name=foo env=test"

Be sure to set `ec2_key_name` to the name of the EC2 key that was `ssh-add`'d, earlier. Also ensure that `env` is set to either `test` or `production`, as appropriate.

### Teardown

**WARNING!** This should only be used in development or test environments. This command will terminate **all** EC2 and RDS instances in AWS that match the specified `Environment` tag (not just those specified in `site.yml`; it will terminate everything in the account that matches):

    $ ansible-playbook site-teardown.yml --extra-vars "env=test"

## Running Ad-Hoc Commands

Once the AWS resources have been provisioned, ad-hoc commands can be run against them, as follows:

    $ ansible all -u ubuntu -m shell -a 'echo $TERM'

## Troubleshooting

### Error Running `site.yml`: "`ERROR! ERROR! 'dict object' has no attribute 'foo'`"

This seems to be a bug with Ansible's `meta: refresh_inventory`: it somehow screws up the dynamic inventory. I've done some tests, and running that task causes this problem every time.

The workaround is simple, though: just run the `site-provision.yml` and `site-configure.yml` playbooks separately, e.g.:

    $ ansible-playbook site-provision.yml --extra-vars "ec2_key_name=foo env=test"
    $ ansible-playbook site-configure.yml --extra-vars "ec2_key_name=foo env=test"

