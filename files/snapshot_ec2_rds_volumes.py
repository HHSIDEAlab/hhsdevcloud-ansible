##
# Backup all volumes of EC2 instances tagged with `Backup`.
#
# Largely derived from these resources:
# * https://www.codebyamir.com/blog/automated-ebs-snapshots-using-aws-lambda-cloudwatch
# * https://devopscube.com/automate-ebs-snapshot-creation-deletion/
##

import boto3

# The value for the `Application` tag that will be applied to all snapshots
# created by this script.
AWS_TAG_APPLICATION = 'SharedDevServices'

def lambda_handler(event, context):
    ec2 = boto3.client('ec2')
    
    # Get list of regions
    regions = ec2.describe_regions().get('Regions',[] )

    # Iterate over regions
    for region in regions:
        print "Creating snapshots for region '%s'..." % region['RegionName']
        reg=region['RegionName']

        # Connect to region
        ec2 = boto3.client('ec2', region_name=reg)

        # Find all EC2 instances tagged with `Backup`.
        reservations = ec2.describe_instances(
                Filters=[{'Name': 'tag-key', 'Values': ['Backup']}]
	).get('Reservations', [])
	instances = sum([[i for i in r['Instances']] for r in reservations], [])
        print "Found '%d' EC2 instances that need backing up." % len(instances)

        for instance in instances:
            print "Backing up instance '%s'..." % instance['InstanceId']
            for dev in instance['BlockDeviceMappings']:
                if dev.get('Ebs', None) is None:
                    continue
                vol_id = dev['Ebs']['VolumeId']

                print "Snapshotting EBS volume '%s'..." % vol_id
                snapshot_result = ec2.create_snapshot(VolumeId=vol_id)
                print 'Snapshotted EBS volume.'

                print 'Tagging snapshot...'
		ec2_resource = boto3.resource('ec2', region_name=reg)
                snapshot = ec2_resource.Snapshot(snapshot_result['SnapshotId'])
                instance_name = get_instance_name(instance)
                dev_name = dev['DeviceName']
                snapshot.create_tags(Tags=[
                        {'Key': 'Name','Value': "%s-%s" % (instance_name,dev_name)},
                        {'Key': 'Application','Value': AWS_TAG_APPLICATION}])
                print 'Tagged snapshot.'
            print 'Backed up instance.'

        print 'Created snapshots for region.'
    print 'All regions complete.'

def get_instance_name(instance, default_name='N/A'):
    name = default_name
    for tag in instance['Tags']:
        if tag['Key'] == 'Name':
            name = tag['Value']
    return name
