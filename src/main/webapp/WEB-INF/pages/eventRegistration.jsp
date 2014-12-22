<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
    <head>
        <title>Teach Children To Save - Create New Event</title>
        <%@include file="include/commonHead.jsp"%>
    </head>
    <body class="evenRegistration">
    <%@include file="include/header.jsp"%>

    <a href="#main" class="ada-read">Skip to main content</a>


    <div class="mainCnt">

        <%@include file="include/navigation.jsp" %>

        <main id="main">

                <h1>Events</h1>

                <table id="eventTable">
                    <thead>
                    <tr>
                        <th scope="col">Date</th>
                        <th scope="col">Time</th>
                        <th scope="col">Grade</th>
                        <th scope="col">Students</th>
                        <th scope="col">Teacher</th>
                        <th scope="col">School</th>
                        <th aria-hidden="true"></th>
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
                                            <%--<input type="submit" value="Sign Up"/>--%>
                                            <button type="submit" value="Sign Up" class="editOrRegister">
                                                Sign Up
                                            </button>
                                        </form:form>
                                    </div>
                                </td>
                            </tr>
                        </c:if>
                    </c:forEach>
                    </tbody>
                </table>

                <button onclick="js.loadURL('volunteerHome.htm');" class="editOrRegister doneAdding">Done adding classes</button>

        </main>

    </div><%-- .mainCnt --%>

    <%@include file="include/footer.jsp" %>

    </body>
</html>
