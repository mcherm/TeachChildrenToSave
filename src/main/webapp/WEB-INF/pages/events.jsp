<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en-US">
    <head>

        <title>Teach Children To Save - Class </title>
        <%@include file="include/commonHead.jsp"%>

    </head>
    <body class="viewEditEvents">

    <a href="#main" class="ada-read">Skip to main content</a>

    <%@include file="include/header.jsp" %>

    <div class="mainCnt">

        <%@include file="include/navigation.jsp" %>

        <main id="main">

            <h1>Classes</h1>

            <div id="actions">

                <h2>Actions</h2>

                <ul class="noUl">
                    <li class="mb1">
                        <button onclick="js.loadURL('createEventBySiteAdmin.htm')" class="editOrRegister cancel">Create New Class</button>
                        <button onclick="js.loadURL('siteAdminHome.htm')" class="editOrRegister cancel">Back</button>
                    </li>

                </ul>
            </div>

			<a download="classes.xls" href="excel/events.htm" class="downloadExcel">Export to Excel</a>

            <table id="approvedVolunteersTable" class="responsive">
                <thead>
                    <tr>
                        <th scope="col">Date</th>
                        <th scope="col">Time</th>
                        <th scope="col" class="center">Grade</th>
                        <th scope="col">Delivery Method</th>
                        <th scope="col" class="center">Students</th>
                        <th scope="col" >Notes</th>
                        <th scope="col">Teacher</th>
                        <th scope="col">Teacher Email</th>
                        <th scope="col">School</th>
                        <th scope="col">Volunteer</th>
                        <th scope="col">Volunteer Email</th>
                        <th scope="col">Bank</th>
                        <th scope="col">Details&nbsp;&nbsp;&nbsp;&nbsp;<span class="ada-read">Column of Details buttons</span></th>
                        <th scope="col">Delete&nbsp;&nbsp;&nbsp;&nbsp;<span class="ada-read">Column of Delete buttons</span></th>
                        <th scope="col">Modify&nbsp;&nbsp;&nbsp;&nbsp;<span class="ada-read">Column of Modify buttons</span></th>
                    </tr>
                </thead>
                <tbody>
                    <c:if test="${empty events}">
                        <td colspan="10" class="emptyTableMessage">There are no classes.</td>
                    </c:if>
                    <c:forEach var="event" items="${events}">
                        <tr>
                            <td data-title="Date"><c:out value="${event.eventDate.pretty}"/></td>
                            <td data-title="Time"><c:out value="${event.eventTime}"/></td>
                            <td class="center" data-title="Grade"><c:out value="${event.grade}"/></td>
                            <td data-title="Delivery Method"><c:out value="${event.deliveryMethodString}"/></td>
                            <td class="center" data-title="Students"><c:out value="${event.numberStudents}"/></td>
                            <td class="center" data-title="Notes"><div class="scrollable"><c:out value="${event.notes}"/></div> </td>
                            <td data-title="Teacher">
                                <c:out value="${event.linkedTeacher.firstName}"/>
                                <c:out value="${event.linkedTeacher.lastName}"/>
                            </td>
                            <td data-title="Teacher Email"><c:out value="${event.linkedTeacher.email}"/></td>
                            <td data-title="School"><c:out value="${event.linkedTeacher.linkedSchool.name}"/></td>
                            <c:choose>
                                <c:when test="${empty event.volunteerId}">
                                    <td colspan="3" class="emptyTableMessage" data-title="Volunteer">None yet.</td>
                                </c:when>
                                <c:otherwise>
                                    <td data-title="Volunteer">
                                        <c:out value="${event.linkedVolunteer.firstName}"/>
                                        <c:out value="${event.linkedVolunteer.lastName}"/>
                                    </td>
                                    <td data-title="Volunteer Email">
                                        <c:out value="${event.linkedVolunteer.email}"/>
                                    </td>

                                    <td data-title="Bank"><c:out value="${event.linkedVolunteer.linkedBank.bankName}"/></td>
                                </c:otherwise>
                            </c:choose>
                            <td class="action">
                                <form action="eventDetails.htm" method="POST">
                                    <input type="hidden" name="eventId" value="<c:out value="${event.eventId}"/>"/>
                                    <input type="hidden" name="doneURL" value="viewEditEvents.htm"/>
                                    <button class="editOrRegister details" type="submit">Details</button>
                                </form>
                            </td>
                            <td class="action">
                                <form method="POST" action="deleteEvent.htm" modelAttribute="formData">
                                    <input type="hidden" name="eventId" value='<c:out value="${event.eventId}"/>' />
                                    <button type="submit" class="editOrRegister delete">Delete</button>
                                </form>
                            </td>
                            <td class="action">
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
