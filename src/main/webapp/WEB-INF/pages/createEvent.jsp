<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
    <head>
        <title>Teach Children To Save - Create New Course</title>
        <%@include file="include/commonHead.jsp"%>
    </head>
    <body>
        <%@include file="include/header.jsp"%>
        <div class="createEventForm">
            <form:form method="POST" action="createEvent.htm" modelAttribute="formData">
                <div>
                    <div><form:label path="eventDate">Date</form:label></div>
                    <div>
                        <form:select path="eventDate">
                            <form:option value="" label="- Select Date -" />
                            <form:options items="${allowedDates}" itemValue="parseable" itemLabel="pretty"/>
                        </form:select>
                    </div>
                </div>
                <div>
                    <div><form:label path="eventTime">Time (approximate)</form:label></div>
                    <div>
                        <form:select path="eventTime">
                            <form:option value="" label="- Select Time -" />
                            <form:options items="${allowedTimes}"/>
                        </form:select>
                    </div>
                </div>
                <div>
                    <div><form:label path="grade">Grade</form:label></div>
                    <div><form:input path="grade"/></div>
                </div>
                <div>
                    <div><form:label path="numberStudents">Number of Students</form:label></div>
                    <div><form:input path="numberStudents"/></div>
                </div>
                <div>
                    <div><form:label path="notes">Notes for the Volunteer</form:label></div>
                    <div><form:textarea path="notes"/></div>
                </div>
                <div>
                    <input type="submit" value="Register"/>
                </div>
            </form:form>
        </div>
        <%@include file="include/footer.jsp"%>
    </body>
</html>
