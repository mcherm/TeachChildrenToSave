# ========== Deploy on Tomcat 11 locally ==========

# Expects to be run from the root of the TeachChildrenToSave

# stop currently running server (if any)
/usr/local/opt/tomcat@11/bin/catalina stop

# Remove existing webapp
rm /usr/local/opt/tomcat@11/libexec/webapps/TeachChildrenToSave.war

# Remove logs
rm /usr/local/opt/tomcat@11/libexec/logs/*

# copy over the newly built tool
cp ./target/TeachChildrenToSave.war /usr/local/opt/tomcat@11/libexec/webapps

# start the server on port 8080
/usr/local/opt/tomcat@11/bin/catalina start

# Now visit http://localhost:8080/TeachChildrenToSave
