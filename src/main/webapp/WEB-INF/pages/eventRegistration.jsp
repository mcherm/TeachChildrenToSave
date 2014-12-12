<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
    <head>
        <title>Teach Children To Save - Create New Event</title>
        <%@include file="include/commonHead.jsp"%>
    </head>
    <body>
        <%@include file="include/header.jsp"%>
        <div id="events">
            <h2>Events</h2>
            <table id="eventTable" class="displayTable">
                <thead>
                    <tr>
                        <th>Date</th>
                        <th>Time</th>
                        <th>Grade</th>
                        <th>Students</th>
                        <th>Teacher</th>
                        <th>School</th>
                        <th></th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="event" items="${events}">
                        <c:if test="${event.volunteerId == null}">
                            <tr>
                                <td><c:out value="${event.eventDate}"/></td>
                                <td><c:out value="${event.eventTime}"/></td>
                                <td><c:out value="${event.grade}"/></td>
                                <td><c:out value="${event.numberStudents}"/></td>
                                <td>
                                    <c:out value="${event.linkedTeacher.firstName}"/>
                                    <c:out value="${event.linkedTeacher.lastName}"/>
                                </td>
                                <td><c:out value="${event.linkedTeacher.linkedSchool.name}"/></td>
                                <td>
                                    <div class="createEventForm">
                                        <form:form method="POST" action="eventRegistration.htm" modelAttribute="formData">
                                            <input type="hidden" name="eventId" value="${event.eventId}">
                                            <input type="submit" value="Sign Up"/>
                                        </form:form>
                                    </div>
                                </td>
                            </tr>
                        </c:if>
                    </c:forEach>
                </tbody>
            </table>
        </div>
        <div>
            <a href="volunteerHome.htm">Done adding classes</a>
        </div>
        <%@include file="include/footer.jsp"%>
    </body>
</html>
