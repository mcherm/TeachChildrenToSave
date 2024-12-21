# ========== Deploy on Tomcat 10 locally ==========

# Expects to be run from the root of the TeachChildrenToSave

# stop currently running server (if any)
/usr/local/opt/tomcat@10/bin/catalina stop

# Remove existing webapp
rm /usr/local/opt/tomcat@10/libexec/webapps/TeachChildrenToSave.war

# Remove logs
rm /usr/local/opt/tomcat@10/libexec/logs/*

# copy over the newly built tool
cp ./target/TeachChildrenToSave.war /usr/local/opt/tomcat@10/libexec/webapps

# start the server on port 8080
/usr/local/opt/tomcat@10/bin/catalina start
