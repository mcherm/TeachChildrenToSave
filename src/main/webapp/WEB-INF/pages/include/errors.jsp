<%-- This generates an errors section, populated from an errors object. --%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:if test="${not empty errors}">
    <c:if test="${errors.hasErrors()}">
        <div class="errorMessage">
            <c:forEach var="errorMessage" varStatus="status" items="${errors.errorMessages}">
                <c:out value="${errorMessage}"/>
                <c:if test="${not status.last}">
                    <br/>
                </c:if>
            </c:forEach>
        </div>
    </c:if>
</c:if>
