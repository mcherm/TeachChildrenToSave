<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
    <head>

        <title>Teach Children To Save - Class </title>
        <%@include file="include/commonHead.jsp"%>

    </head>
    <body class="banks">

    <a href="#main" class="ada-read">Skip to main content</a>

    <div class="decor"></div>

    <%@include file="include/header.jsp" %>

    <div class="mainCnt">

        <%@include file="include/navigation.jsp" %>

        <main id="main">

            <h1>Classes</h1>

            <div id="actions">

                <h2>Actions</h2>

                <ul class="noUl">
                    <li class="mb1">
                        <button onclick="js.loadURL('siteAdminHome.htm')" class="editOrRegister cancel">Back</button>
                    </li>
                    
                    <li class="mb1">
                        	<button onClick ="$('#approvedVolunteersTable').tableExport({type:'excel',escape:'false'});" class="editOrRegister">Export to excel</button>
                    </li>
                    	
                </ul>
            </div>

            <table id="approvedVolunteersTable">
                <thead>
                    <tr>
                        <th scope="col">Date</th>
                        <th scope="col">Time</th>
                        <th scope="col" class="center">Grade</th>
                        <th scope="col" class="center">Students</th>
                        <th scope="col">Teacher</th>
                        <th scope="col">School</th>
                        <th scope="col">Volunteer</th>
                        <th scope="col">Bank</th>
                        <th scope="col"><span class="ada-read">Column of Details buttons</span></th>
                        <th scope="col"><span class="ada-read">Column of Delete buttons</span></th>
                        <th scope="col"><span class="ada-read">Column of Modify buttons</span></th>
                    </tr>
                </thead>
                <tbody>
                    <c:if test="${empty events}">
                        <td colspan="8" class="emptyTableMessage">There are no classes.</td>
                    </c:if>
                    <c:forEach var="event" items="${events}">
                        <tr>
                            <td><c:out value="${event.eventDate.pretty}"/></td>
                            <td><c:out value="${event.eventTime}"/></td>
                            <td class="center"><c:out value="${event.grade}"/></td>
                            <td class="center"><c:out value="${event.numberStudents}"/></td>
                            <td>
                                <c:out value="${event.linkedTeacher.firstName}"/>
                                <c:out value="${event.linkedTeacher.lastName}"/>
                            </td>
                            <td><c:out value="${event.linkedTeacher.linkedSchool.name}"/></td>
                            <c:choose>
                                <c:when test="${empty event.volunteerId}">
                                    <td colspan="2" class="emptyTableMessage">None yet.</td>
                                </c:when>
                                <c:otherwise>
                                    <td>
                                        <c:out value="${event.linkedVolunteer.firstName}"/>
                                        <c:out value="${event.linkedVolunteer.lastName}"/>
                                    </td>
                                    <td><c:out value="${event.linkedVolunteer.linkedBank.bankName}"/></td>
                                </c:otherwise>
                            </c:choose>
                            <td>
                                <form action="eventDetails.htm" method="POST">
                                    <input type="hidden" name="eventId" value="<c:out value="${event.eventId}"/>"/>
                                    <input type="hidden" name="doneURL" value="viewEditEvents.htm"/>
                                    <button class="editOrRegister details" type="submit">Details</button>
                                </form>
                            </td>
                            <td>
                                <form method="POST" action="deleteEvent.htm" modelAttribute="formData">
                                    <input type="hidden" name="eventId" value='<c:out value="${event.eventId}"/>' />
                                    <button type="submit" class="editOrRegister delete">Delete</button>
                                </form>
                            </td>
                            <td>
                                <button onclick="js.loadURL('editEvent.htm?eventId=<c:out value="${event.eventId}"/>');" class="editOrRegister">Modify</button>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>

        </main>
    </div><%-- mainCnt --%>

     <%@include file="include/footer.jsp"%>

    </body>
</html>
