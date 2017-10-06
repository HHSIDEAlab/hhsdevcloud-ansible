##
# Delete snapshots older than a retention period.
#
# Largely derived from these resources:
# * https://www.codebyamir.com/blog/automated-ebs-snapshots-using-aws-lambda-cloudwatch
##

import boto3
from botocore.exceptions import ClientError

from datetime import datetime,timedelta

# The value for the `Application` tag that must be on a snapshot for this
# script to consider it.
AWS_TAG_APPLICATION = 'SharedDevServices'

# The number of days that snapshots should be retained.
RETENTION_DAYS = 15

def delete_snapshot(snapshot_id, reg):
    print "Deleting snapshot '%s...'" % (snapshot_id)
    try:  
        ec2resource = boto3.resource('ec2', region_name=reg)
        snapshot = ec2resource.Snapshot(snapshot_id)
        snapshot.delete()
    except ClientError as e:
        print "Caught exception: %s" % e
    print 'Deleted snapshot.'
    return
    
def lambda_handler(event, context):
    # Get current timestamp in UTC
    now = datetime.now()
    
    # Create EC2 client
    ec2 = boto3.client('ec2')
    
    # Get list of regions
    regions = ec2.describe_regions().get('Regions',[] )

    # Iterate over regions
    for region in regions:
        print "Checking region '%s'..." % region['RegionName']
        reg=region['RegionName']
        
        # Connect to region
        ec2 = boto3.client('ec2', region_name=reg)
        
	# Find all snapshots tagged with our application name.
	result = ec2.describe_snapshots(Filters=[{'Name':'tag:Application', 'Values':[AWS_TAG_APPLICATION]}])
    
        for snapshot in result['Snapshots']:
            print "Checking snapshot %s which was created on %s..." % (snapshot['SnapshotId'],snapshot['StartTime'])
       
            # Remove timezone info from snapshot in order for comparison to work below
            snapshot_time = snapshot['StartTime'].replace(tzinfo=None)
        
            # Subtract snapshot time from now returns a timedelta 
            # Check if the timedelta is greater than retention days
            if (now - snapshot_time) > timedelta(RETENTION_DAYS):
                delete_snapshot(snapshot['SnapshotId'], reg)
            print 'Checked snapshot.'

	print 'Checked region.'
