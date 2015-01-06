<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
    <head>
        <title>Teach Children To Save - Login</title>
        <%@include file="include/commonHead.jsp"%>
    </head>

    <body class="signIn">

        <a href="#main" class="ada-read">Skip to main content</a>

        <div class="decor"></div>

        <%@include file="include/header.jsp" %>

        <div class="mainCnt">

        <%@include file="include/navigation.jsp" %>

            <main id="main">

	        <h1>Teach Children To Save Login</h1>				

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
	                    </div>
	            </div>
	            
	            <button type="submit" value="Login">Login</button>
	            
	        </form:form>

	        <div class="qa-notes">
	        	<strong>Some valid logins for testing:</strong> 
	        	<br>
                larry@foobar.com(V) janedoe@foobar.com(BA) lucy@foobar.com(T) bonnie@mcherm.com(SA) moe@gmail.com(V) curley@gmail.com(V)
                shemp@hulu.com(V) josuah@wintergreen.com(T) Allen@novel.com(T).
	            <br>
	            V=Volunteer; T=Teacher; BA=BankAdmin; SA=SiteAdmin. 
	            <br>
	            All have "pass"
	            as their password.
	        </div>


            
            </main>
        </div><%-- mainCnt --%>
        <%@include file="include/footer.jsp" %>

    </body>
</html>
