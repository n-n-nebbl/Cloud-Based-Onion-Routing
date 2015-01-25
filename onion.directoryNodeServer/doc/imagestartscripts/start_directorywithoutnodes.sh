#!/bin/sh
cd /home/ec2-user/
wget -q --read-timeout=0.0 --waitretry=5 --tries=400 --background http://freedns.afraid.org/dynamic/update.php?MmEwalRteVRsbFpyTURzSzJZUWV6bmpTOjEzMDgxMjc1
