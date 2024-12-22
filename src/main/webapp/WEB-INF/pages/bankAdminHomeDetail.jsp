<%-- This is just a fragment of a page, loaded by javascript --%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<table class = "innerTable">
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
                <td class="center"><c:out value="${event.deliveryMethodString}"/></td>
                <td class="center"><c:out value="${event.numberStudents}"/></td>
                <td class="center"><c:out value="${event.linkedTeacher.firstName}"/> <c:out value="${event.linkedTeacher.lastName}"/></td>
                <c:if test="${bank.minLMIForCRA != null}">
                    <td>
                        <c:choose>
                            <c:when test="${event.linkedTeacher.linkedSchool.lmiEligible >= bank.minLMIForCRA}">CRA eligible</c:when>
                            <c:otherwise>Not eligible</c:otherwise>
                        </c:choose>
                    </td>
                </c:if>
            </tr>
            <tr>
                <c:if test ="${not empty event.notes}">
                    <td colspan="7" class =  "italic">Class Note:  <c:out value="${event.notes}"/></td>
                </c:if>
            </tr>
        </c:forEach>
    </tbody>
</table>
