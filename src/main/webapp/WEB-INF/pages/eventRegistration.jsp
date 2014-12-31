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

                <h1>Classes</h1>

                <fieldset class="refine">
                    <legend>
                        Refine by:
                    </legend>

                    <div>
                        <fieldset>
                            <legend>
                                County
                            </legend>

                            <ul>
                                <li>
                                    <label>
                                        <input type="checkbox" name="grade" value="newCastle">
                                        <span class="txt">
                                            New Castle County (15)
                                        </span>
                                    </label>
                                </li>
                                <li>
                                    <label>
                                        <input type="checkbox" name="grade" value="kent">
                                        <span class="txt">
                                            Kent County (15)
                                        </span>
                                    </label>
                                </li>
                                <li>
                                    <label>
                                        <input type="checkbox" name="grade" value="sussex">
                                        <span class="txt">
                                            Sussex County (15)
                                        </span>
                                    </label>
                                </li>
                            </ul>
                        </fieldset>
                    </div>

                    <div>
                        <fieldset>
                            <legend>
                                Grade
                            </legend>

                            <ul>
                                <li>
                                    <label>
                                        <input type="checkbox" name="grade" value="grade2">
                                        <span class="txt">2rd Grade (15)</span>
                                    </label>
                                </li>
                                <li>
                                    <label>
                                        <input type="checkbox" name="grade" value="grade3">
                                        <span class="txt">3rd Grade (15)</span>
                                    </label>
                                </li>
                                <li>
                                    <label>
                                        <input type="checkbox" name="grade" value="grade4">
                                        <span class="txt">4th Grade (15)</span>
                                    </label>
                                </li>
                                <li>
                                    <label>
                                        <input type="checkbox" name="grade" value="grade5">
                                        <span class="txt">5th Grade (15)</span>
                                    </label>
                                </li>
                            </ul>
                        </fieldset>
                    </div>

                    <div>
                        <fieldset>
                            <legend>
                                CRA Eligible
                            </legend>

                            <ul>
                                <li>
                                    <label>
                                        <input type="checkbox" name="grade" value="yes">
                                        <span class="txt">
                                            Yes (15)
                                        </span>
                                    </label>
                                </li>
                                <li>
                                    <label>
                                        <input type="checkbox" name="grade" value="grade4">
                                        <span class="txt">
                                            No (15)
                                        </span>
                                    </label>
                                </li>
                            </ul>
                        </fieldset>
                    </div>
                </fieldset>

                <table id="eventTable">
                    <thead>
                    <tr>
                        <th scope="col">Date</th>
                        <th scope="col">Time</th>
                        <th scope="col" class="center">Grade</th>
                        <th scope="col" class="center">Students</th>
                        <th scope="col">Teacher</th>
                        <th scope="col">School</th>
                        <th>
                            <span class="ada-read">Column of Sign Up buttons</span>
                        </th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="event" items="${events}">
                        <c:if test="${event.volunteerId == null}">
                            <tr>
                                <td><c:out value="${event.eventDate}"/></td>
                                <td><c:out value="${event.eventTime}"/></td>
                                <td class="center"><c:out value="${event.grade}"/></td>
                                <td class="center"><c:out value="${event.numberStudents}"/></td>
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
