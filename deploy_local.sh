# ========== Deploy on Tomcat 10 locally ==========

# stop currently running server (if any)
/usr/local/Cellar/tomcat/10.1.20/bin/catalina stop

# Remove existing webapp
rm /usr/local/Cellar/tomcat/10.1.20/libexec/webapps/TeachChildrenToSave.war

# Remove logs
rm /usr/local/Cellar/tomcat/10.1.20/libexec/logs/*

# copy over the newly built tool
cp ./target/TeachChildrenToSave.war /usr/local/Cellar/tomcat/10.1.20/libexec/webapps

# start the server on port 8080
/usr/local/Cellar/tomcat/10.1.20/bin/catalina start
