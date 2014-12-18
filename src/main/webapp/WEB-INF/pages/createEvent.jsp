<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
    <head>
        <title>Teach Children To Save - Create New Course</title>
        <%@include file="include/commonHead.jsp"%>
    </head>
    <body class="createEvent">
    
        <a href="#main" class="ada-read">Skip to main content</a>

        <div class="decor"></div>

        <%@include file="include/header.jsp" %>

        <div class="mainCnt">

        <%@include file="include/navigation.jsp" %>

            <main id="main">   

		            <form:form method="POST" action="createEvent.htm" modelAttribute="formData">
		            
		                <div class="formElementCnt">
		                    <label>
		                        <div class="inputCnt">
		                            <div class="info">
		                                Date
		                            </div>
			                        <form:select path="eventDate">
			                            <form:option value="" label="- Select Date -" />
			                            <form:options items="${allowedDates}" itemValue="parseable" itemLabel="pretty"/>
			                        </form:select>
		                        </div>
		                    </label>
		                </div>				            

		                <div class="formElementCnt">
		                    <label>
		                        <div class="inputCnt">
		                            <div class="info">
		                                Time (approximate)
		                            </div>
			                        <form:select path="eventTime">
			                            <form:option value="" label="- Select Time -" />
			                            <form:options items="${allowedTimes}"/>
			                        </form:select>
		                        </div>
		                    </label>
		                </div>		

		                <div class="formElementCnt">
		                    <label>
		                        <div class="inputCnt">
		                            <div class="info">
		                                Grade
		                            </div>
		                            <form:input path="grade"/>
		                        </div>
		                    </label>
		                </div>		

		                <div class="formElementCnt">
		                    <label>
		                        <div class="inputCnt">
		                            <div class="info">
		                                Number of Students
		                            </div>
		                            <form:input path="numberStudents"/>
		                        </div>
		                    </label>
		                </div>		

		                <div class="formElementCnt">
		                    <label>
		                        <div class="inputCnt">
		                            <div class="info">
		                                Notes for the Volunteer
		                            </div>
		                            <form:textarea path="notes"/>
		                        </div>
		                    </label>
		                </div>				

				       <button type="submit" value="Register">Register</button>

		            </form:form>

			</main>

		</div><%-- mainCnt --%>			        
		               
        
        <%@include file="include/footer.jsp"%>
    </body>
</html>