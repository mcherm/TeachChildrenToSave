<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en-US">
    <head>
        <title>Teach Children to Save - Edit Personal Data</title>
        <%@include file="include/commonHead.jsp"%>
    </head>
    <body class="editPersonalData">

        <a href="#main" class="ada-read">Skip to main content</a>

        <%@include file="include/header.jsp" %>

        <div class="mainCnt">

        <%@include file="include/navigation.jsp" %>

            <main id="main">

		        <h1>
					Edit Volunteer Data
				</h1>

                <%@include file="include/errors.jsp"%>

	            <form:form method="POST" action="editVolunteerData.htm" modelAttribute="formData">
					
					<form:hidden path="userId"/>
					
                    <div class="formElementCnt">
                        <label>
                            <div class="inputCnt">
                                <div class="info">
                                    First Name
                                </div>
                                <form:input path="firstName"/>
                            </div>
                        </label>
                    </div>	

                    <div class="formElementCnt">
                        <label>
                            <div class="inputCnt">
                                <div class="info">
                                    Last Name
                                </div>
                                <form:input path="lastName"/>
                            </div>
                        </label>
                    </div>

                    <div class="formElementCnt">
                        <label>
                            <div class="inputCnt">
                                <div class="info">
                                    Email
                                </div>
                                <form:input path="email"/>
                            </div>
                        </label>
                    </div>	
                    
                    <div class="formElementCnt">
                        <label>
                            <div class="inputCnt">
                                <div class="info">
                                    Phone Number
                                </div>
                                <form:input path="phoneNumber"/>
                            </div>
                        </label>
                    </div>

                    <button type="submit" value="Edit">Save</button>
                    <button onclick="js.loadURL('volunteers.htm')" type="button" class="editOrRegister delete">Cancel</button>

	            </form:form>
		
		    </main>
		
		</div><%-- mainCnt --%>    
		        
        <%@include file="include/footer.jsp"%>
    </body>
</html>