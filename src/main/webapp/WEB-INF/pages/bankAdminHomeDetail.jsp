<%-- This is just a fragment of a page, loaded by javascript --%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<table>
    <tbody>
        <c:if test="${empty events}">
            <td colspan="5" class="emptyTableMessage">Not volunteered yet.</td>
        </c:if>
        <c:forEach items="${events}" var="event">
            <tr>
                <td><c:out value="${event.eventDate.pretty}"/></td>
                <td><c:out value="${event.eventTime}"/></td>
                <td><c:out value="${event.linkedTeacher.linkedSchool.name}"/></td>
                <td class="center"><c:out value="${event.grade}"/></td>
                <td class="center"><c:out value="${event.numberStudents}"/></td>
            </tr>
        </c:forEach>
    </tbody>
</table>
