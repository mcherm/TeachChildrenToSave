<%--
    This is a sample .jsp which is NOT actually used by the application. It
    demonstrates what should be present on each and every page of the site.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
    <head>
        <title>Teach Children To Save - Unexpected error occurred</title>
        <%@include file="include/commonHead.jsp"%>
        <script src="<c:url value="/tcts/js/jquery-1.11.1.min.js" />"></script>
    </head>
    <body class="errorPage">
    
        <a href="#main" class="ada-read">Skip to main content</a>

        <div class="decor"></div>

        <%@include file="include/header.jsp" %>

        <div class="mainCnt">

        <%@include file="include/navigation.jsp" %>

            <main id="main">    
    
				<div class="errorMessage">
				    An unexpected error has occurred. You may retry. If that does not work, please
                    use the "contact us" link to report the problem.
			   	</div>


				<div id="actions">

                    <h2>Actions</h2>

                    <ul class="noUl">
 
                        <li class="mb1">
                            <button onclick="js.loadURL('home.htm')" class="editOrRegister cancel">Home </button>
                        </li>

                    </ul>
                </div>

                <div class="obscuredFootnote"><span onclick="$('.hiddenErrorMessage').toggle()">Details...</span></div>
			    <div class="hiddenErrorMessage">
                    <div class="exceptionType"><c:out value="${exceptionType}"/></div>
                    <div class="exceptionMessage"><c:out value="${exceptionMessage}"/></div>
                    <pre class="stackTrace"><c:out value="${stackTrace}"/></pre>
			    </div>
			</main>

		</div><%-- mainCnt --%>	
				
        <%@include file="include/footer.jsp"%>
    </body>
</html>