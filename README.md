Cloud-Based-Onion-Routing
=========================

This is the project for "Advanced Internet Computing" "Topic 3 - Cloud-Based Onion Routing"


Start directory node server instance

1. In the ec2 dashboard -> click "Launch Instance"
2. Choose AMI with id "ami-0f7e273f"
3. Click "Next: Configure Instance Details"
3. Go to "6. Configure Security Group" and choose "Select an existing security group" and select the security group with name "default"
4. Click "Review and Launch" -> Next
5. Click "Launch" -> Now the EC2 Instance is starting
6. Connect to "ec2-user@directoryNode.mooo.com" via putty and provide the the private key file
7. There you can start the directory node with "sh directoryNode/startWithConfig.sh"