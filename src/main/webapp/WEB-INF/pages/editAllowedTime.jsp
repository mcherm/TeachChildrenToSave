<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en-US">
    <head>
        <title>Teach Children to Save - Edit Allowed Time</title>
        <%@include file="include/commonHead.jsp"%>
    </head>
    <body class="editPersonalData">

        <a href="#main" class="ada-read">Skip to main content</a>

        <%@include file="include/header.jsp" %>

        <div class="mainCnt">

        <%@include file="include/navigation.jsp" %>

            <main id="main">

		        <h1>
					Edit Allowed Time
				</h1>
		        
				<c:if test="${not empty errorMessage}">
					<div class="errorMessage">
				   		<c:out value="${errorMessage}" default=""  />
			   		</div>			
				</c:if>


	            <form:form method="POST" action="editAllowedTime.htm" modelAttribute="formData">

                    <div class="formElementCnt">
                        <label>
                            <div class="inputCnt">
                                <div class="info">
                                    Allowed Time
                                </div>
                               <form:input path="allowedTime"/>
                            </div>
                        </label>
                    </div>	

                   <button type="submit" value="Edit">Edit</button>

	            </form:form>
		
		    </main>
		
		</div><%-- mainCnt --%>    
		        
        <%@include file="include/footer.jsp"%>
    </body>
</html>