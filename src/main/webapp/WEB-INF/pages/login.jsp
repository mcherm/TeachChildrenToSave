<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en-US">
    <head>
        <title>Teach Children To Save - Login</title>
        <%@include file="include/commonHead.jsp"%>
    </head>

    <body class="signIn">

        <a href="#main" class="ada-read">Skip to main content</a>

        <%@include file="include/header.jsp" %>

        <div class="mainCnt">

        <%@include file="include/navigation.jsp" %>

            <main id="main">

	        <h1>Teach Children To Save Login</h1>

			<div class="centered"><em>NOTE: You must create a new login each year.</em></div>

			<c:if test="${not empty errorMessage}">
				<div class="errorMessage">
			   		<c:out value="${errorMessage}" default=""  />
		   		</div>			
			</c:if>
			
	        <form:form method="POST" action="login.htm" modelAttribute="formData">
	        
	            <div class="formElementCnt">
	                <label>
	                    <div class="inputCnt">
	                        <div class="info">Email</div>
	                        <form:input path="email" />
	                    </div>
	                </label>
	            </div>

	            <div class="formElementCnt">
	                <label>
	                    <div class="inputCnt">
	                        <div class="info">Password</div>
	                        <form:input path="password" type="password"/>
	                    </div>
	                </label>
	                
	                <div class="inputCnt">
						<a href="forgotPassword.htm">Forgot Password</a>
						<%--TODO: should be a button not an anchor, but currently adding this button will trigger the form submit, not the js URL load--%>
						<%--<button onclick="js.loadURL('forgotPassword.htm')" class="likeAnAnchor" type="button">--%>
							<%--Forgot Password--%>
						<%--</button>--%>
	                </div>
	            </div>
	            
	            <button type="submit" value="Login">Login</button>
	            
	        </form:form>

			</main>

			<aside>
				<img src="tcts/img/iStock_000019109215Small-happy-kids.jpg" alt="" aria-hidden="true">
			</aside>

        </div><%-- mainCnt --%>
        <%@include file="include/footer.jsp" %>

    </body>
</html>
