HHS Dev Cloud Ansible Repo
--------------------------

This repository contains the Ansible provisioning, roles, etc. used in the HHS Dev Cloud.

## Development Environment

In order to use and/or modify this repository, a number of tools need to be installed.

### Python

This project requires Python 2.7. It can be installed as follows:

    $ sudo apt-get install python

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

## Provisioning and Configuring AWS Resources

Running the following command will provision and configure all of the AWS resources specified in `site.yml`:

    $ ansible-playbook site.yml

### Teardown

**WARNING!** This should only be used in development or test environments. This command will terminate **all** EC2 and RDS instances in AWS (not just those specified in `site.yml`; it will terminate everything in the account):

    $ ansible-playbook -i ec2.py site-teardown.yml

## Running Ad-Hoc Commands

Once the AWS resources have been provisioned, ad-hoc commands can be run against them, as follows:

    $ ansible all -i ec2.py -u ubuntu -m shell -a 'echo $TERM'

