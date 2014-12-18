<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
    <head>
        <title>Teach Children to Save - Register New Teacher</title>
        <%@include file="include/commonHead.jsp"%>

    </head>
    <body class="registerTeacher">
        <a href="#main" class="ada-read">Skip to main content</a>
        
        <div class="decor"></div>

        <%@include file="include/header.jsp" %>

        <div class="mainCnt">

        <%@include file="include/navigation.jsp" %>

            <main id="main">
            
                    <h1>Register a New Teacher</h1>
			
						<c:if test="${not empty errorMessage}">
							<div class="errorMessage">
						   		<c:out value="${errorMessage}" default=""  />
					   		</div>			
						</c:if>

			            <form:form method="POST" action="registerTeacher.htm" modelAttribute="formData">

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
		                                    Username
		                                </div>
		                                <form:input path="login"/>
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
		                                    Password
		                                </div>
		                                <form:input path="password"/>
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

		                    <div class="formElementCnt">
		                        <label>
		                            <div class="inputCnt">
		                                <div class="info">
		                                   <div><form:label path="schoolId">School where you teach</form:label></div>
		                                </div>
				                        <form:select path="schoolId">
				                            <form:option value="0" label="- Select School -" />
				                            <form:options items="${schools}" itemValue="schoolId" itemLabel="name" />
				                        </form:select>
		                            </div>
		                        </label>
		                    </div>		
		                    		                    
                        <button type="submit" value="Register">Register</button>

						</form:form>

			</main>			        
		
		</div><%-- .mainCnt --%>
			        
        <%@include file="include/footer.jsp"%>
    </body>
</html>