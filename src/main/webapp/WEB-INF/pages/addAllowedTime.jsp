<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
    <head>
        <title>Teach Children To Save - Add Allowed Time</title>
        <%@include file="include/commonHead.jsp"%>
    </head>
    <body class="addAllowedTime">

        <a href="#main" class="ada-read">Skip to main content</a>

        <%@include file="include/header.jsp" %>

        <div class="mainCnt">

            <%@include file="include/navigation.jsp" %>

            <main id="main">

                <%@include file="include/errors.jsp"%>

                <form:form method="POST" action="addAllowedTime.htm" modelAttribute="formData">

                    <div class="formElementCnt">
                        <label>
                            <div class="inputCnt">
                                <div class="info">Description of Time</div>
                                <form:input path="allowedTime"/>
                            </div>
                        </label>
                    </div>

                    <div class="formElementCnt">
                        <div class="inputCnt">
                            <div class="info">Where should this go in the list?</div>
                            <c:forEach items="${allowedTimes}" var="allowedTime">
                                <label>
                                    &nbsp;&nbsp;&gt;
                                    <input type="radio" name="timeToInsertBefore" value="${allowedTime}"/>
                                    <em>here</em>
                                </label><br/>
                                <c:out value="${allowedTime}"/></br>
                            </c:forEach>
                            <label>
                                &nbsp;&nbsp;&gt;
                                <input type="radio" name="timeToInsertBefore" value=""/>
                                <em>here</em>
                            </label>
                        </div>
                    </div>

                    <button type="submit" value="Create">Create</button>

                </form:form>

                <div class="cancelBlock">
                    <button onclick="js.loadURL('listAllowedTimes.htm')" class="editOrRegister delete">Cancel</button>
                </div>


            </main>

        </div><%-- mainCnt --%>

        <%@include file="include/footer.jsp"%>
    </body>
</html>
