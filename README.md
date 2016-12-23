social
======

Crafter UGC engine.

Clam Virus Scanner Requirements
===============================

* Clamav must be installed (along with the clamd deamon)
* A TCPhost and a TCPport must be provided in the clamd.conf file
* The clamd deamon must be running with the provided configuration

Note: With the information given so far a localhost 3310 configuration is necessary and enough to run the clam virus scanner tests

* The crafter-social virus-scanner properties file must match the host and the port configured in the clamd.conf file
* A ClamVirusScannerImpl should be provided to the VirusScannerService bean in a virus-scanner-context.xml external file

Note: There is a nullVirusScannerImpl being given to the VirusScannerService as the default implementation (which means that the virus scanning is disable by default).

Clam Virus Scanner local Mac Configuration
==========================================

1) ClamXav should be already installed on your mac

2) Uncomment the following lines in /usr/local/clamXav/etc/clamd.conf

TCPSocket 3310

TCPAddr 127.0.0.1

3) To start the clamd daemon

cd /usr/local/clamXav/sbin

./clamd
