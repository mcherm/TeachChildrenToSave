<%--
    This is a sample .jsp which is NOT actually used by the application. It
    demonstrates what should be present on each and every page of the site.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en-US">
    <head>
        <title>Teach Children To Save - SOMETHING-ABOUT-THE-PAGE</title>
        <%@include file="include/commonHead.jsp"%>
    </head>
    <body class="">
    
        <a href="#main" class="ada-read">Skip to main content</a>

        <%@include file="include/header.jsp" %>

        <div class="mainCnt">

            <%@include file="include/navigation.jsp" %>

            <main id="main">    
    
				BODY-OF-PAGE-GOES-HERE



				<%-- form elements example --%>				
                <div class="formElementCnt">
                    <label>
                        <div class="inputCnt">
                            <div class="info">
                                First Name
                            </div>
                            <input type="text" />
                        </div>
                    </label>
                </div>				

		       <button type="submit" value="Submit">Submit</button>

			
			</main>

		</div><%-- mainCnt --%>	
				
        <%@include file="include/footer.jsp"%>
    </body>
</html>