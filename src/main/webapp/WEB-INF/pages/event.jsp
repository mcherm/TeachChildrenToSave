<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en-US">
    <head>

        <title>Teach Children To Save - Event registration</title>
        <%@include file="include/commonHead.jsp"%>

    </head>
    <body class="eventRegistration">
    
        <a href="#main" class="ada-read">Skip to main content</a>

        <%@include file="include/header.jsp" %>

        <div class="mainCnt">

        <%@include file="include/navigation.jsp" %>

            <main id="main">    
		            
		        <h1>
		        	Event Information
		        </h1>
		        
		        <form:form method="POST" action="addEvent.htm" modelAttribute="event">

	                <div class="formElementCnt">
	                    <label>
	                        <div class="inputCnt">
	                            <div class="info">
	                                School ID
	                            </div>
	                            <form:input path="schoolID" />
	                        </div>
	                    </label>
	                </div>				

	                <div class="formElementCnt">
	                    <label>
	                        <div class="inputCnt">
	                            <div class="info">
	                                Teacher User ID
	                            </div>
	                            <form:input path="teacherUserID" />
	                        </div>
	                    </label>
	                </div>				

	                <div class="formElementCnt">
	                    <label>
	                        <div class="inputCnt">
	                            <div class="info">
	                                Volunteer User ID
	                            </div>
	                            <form:input path="volunteerUserID" />
	                        </div>
	                    </label>
	                </div>	
	                
	                <div class="formElementCnt">
	                    <label>
	                        <div class="inputCnt">
	                            <div class="info">
	                                Grade
	                            </div>
	                            <form:input path="grade" />
	                        </div>
	                    </label>
	                </div>		                
	                
	                <div class="formElementCnt">
	                    <label>
	                        <div class="inputCnt">
	                            <div class="info">
	                                Subject
	                            </div>
	                            <form:input path="subject" />
	                        </div>
	                    </label>
	                </div>		                

	                <div class="formElementCnt">
	                    <label>
	                        <div class="inputCnt">
	                            <div class="info">
	                                Event Date
	                            </div>
	                            <form:input type= "date" path="eventDate" />
	                        </div>
	                    </label>
	                </div>	
	                
	                <div class="formElementCnt">
	                    <label>
	                        <div class="inputCnt">
	                            <div class="info">
	                                Event Time
	                            </div>
	                            <form:input path="eventTime" />
	                        </div>
	                    </label>
	                </div>		                
	                
	                <div class="formElementCnt">
	                    <label>
	                        <div class="inputCnt">
	                            <div class="info">
	                                Event Notes
	                            </div>
	                            <form:input path="eventNotes" />
	                        </div>
	                    </label>
	                </div>	

	                <div class="formElementCnt">
	                    <label>
	                        <div class="inputCnt">
	                            <div class="info">
	                                Volunteer Assigned
	                            </div>
	                            <form:input path="volunteerAssigned" />
	                        </div>
	                    </label>
	                </div>		                

                    <!-- FIXME: Why is this next button named "Login"? -->
		            <button type="submit" value="Login">Login</button>
		        
		        </form:form>

			</main>

		</div><%-- mainCnt --%>			        
		        
        <%@include file="include/footer.jsp"%>
    </body>
</html>