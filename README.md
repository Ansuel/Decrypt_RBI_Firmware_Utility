[![Donate](https://img.shields.io/badge/Donate-PayPal-green.svg)](https://www.paypal.me/AnsuelS) [![License](https://img.shields.io/github/license/Ansuel/nginx-ubus-module.svg?style=flat)](https://github.com/Ansuel/nginx-ubus-module/blob/master/LICENSE)

# Decrypt_RBI_Firmware_Utility

To add osck key change the file here:
https://github.com/Ansuel/Decrypt_RBI_Firmware_Utility/blob/master/src/decrypt_rbi/board.java

# To compile

This java application is based on java 8 
Pay attention to set the right JDK 
Docker image used to compile this maven project is openjdk:8u162-jdk

I'm using Java 1.8 for compatibility reason as this can be easily opened on every system. To use more recent java version i need to make system specific version as JavaFx got dropped from openJDK 9+, also on windows to run java 11 application users need to install custom java version.