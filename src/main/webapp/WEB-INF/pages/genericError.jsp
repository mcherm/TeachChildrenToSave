<%--
    This is a sample .jsp which is NOT actually used by the application. It
    demonstrates what should be present on each and every page of the site.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
    <head>
        <title>Teach Children To Save - Unexpected error occurred</title>
        <%@include file="include/commonHead.jsp"%>
    </head>
    <body class="">
    
        <a href="#main" class="ada-read">Skip to main content</a>

        <div class="decor"></div>

        <%@include file="include/header.jsp" %>

        <div class="mainCnt">

        <%@include file="include/navigation.jsp" %>

            <main id="main">    
    
				<div class="errorMessage">
				   		Unexpected error occurred.Kidnly re-try and if error persists then please try after sometime.
			   	</div>	


				<div id="actions">

                    <h2>Actions</h2>

                    <ul class="noUl">
 
                        <li class="mb1">
                            <button onclick="js.loadURL('home.htm')" class="editOrRegister cancel">Home </button>
                        </li>

                    </ul>
                </div>

			
			</main>

		</div><%-- mainCnt --%>	
				
        <%@include file="include/footer.jsp"%>
    </body>
</html>