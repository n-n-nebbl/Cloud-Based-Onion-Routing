Cloud-Based-Onion-Routing
=========================

This is the project for "Advanced Internet Computing" "Topic 3 - Cloud-Based Onion Routing"


Git repository: https://github.com/n-n-nebbl/Cloud-Based-Onion-Routing.git

Start directory node server instance

1. In the EC2 Dashboard -> click "Launch Instance"
2. Choose AMI with id "ami-0f7e273f"
3. Click "Next: Configure Instance Details"
4. Under "Advanced Details" enter "wget -q --read-timeout=0.0 --waitretry=5 --tries=400 --background http://freedns.afraid.org/dynamic/update.php?MmEwalRteVRsbFpyTURzSzJZUWV6bmpTOjEzMDgxMjc1
" as userdata script to update the dyn dns entry, when the instance starts
5. Go to "6. Configure Security Group" and choose "Select an existing security group" and select the security group with name "default"
6. Click "Review and Launch" -> Next
7. Click "Launch" -> Now the EC2 Instance is starting
8. Connect to "ec2-user@directoryNode.mooo.com" via putty and provide the the private key file
9. There you can start the directory node with "sh directoryNode/startWithConfig.sh"