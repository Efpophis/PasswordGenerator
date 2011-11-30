This program is built with Netbeans IDE.

Its purpose is to generate a nice random password that's compliant with most corporate standards.

I wrote it because I got sick of having to come up with 10 random characters including 2 alphanumeric, 2 numbers,
2 special characters, etc.  Easier to code something up and push a button.

Developer Instructions:
  Import the project into NetBeans and build. You can then run it from the debugger, make changes, etc.
  http://www.netbeans.org

Standalone JAR:
  A standalone JAR is included in the repository. If you simply wish to use this program, just
    download and run deploy/pwgen.jar.
  
  To build the standalone JAR file that can be run from anywhere (including windows, linux, etc):
  File -> Open build.xml
  Find And target called "package-for-deploy"
  right-click, Run Target
  JAR will show up under subfolder deploy.
  
    

Coming soon:
  Generate secure passphrases, i/e: horse correct battery stapler