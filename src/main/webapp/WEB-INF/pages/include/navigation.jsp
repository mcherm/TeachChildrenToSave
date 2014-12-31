<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="homeLink" value='${sessionData == null || sessionData.user == null ? "index.jsp" : sessionData.user.userType.homepage}' />
<header role="banner">
	<nav role="navigation">
		<ul>
			<li>
				<button onclick="js.loadURL('<c:out value="${homeLink}"/>');" class="home">
					<span class="iconFont" aria-hidden="true" data-icon="A"></span>
					<span class="txt">Home</span>
				</button>
			</li>


			<%-- TODO: button for user dashboard, and revise home link to be site home --%>

			<li>
				<button onclick="js.loadURL('about.htm');" class="about">
					<span class="iconFont" aria-hidden="true" data-icon="A"></span>
					<span class="txt">About</span>
				</button>
			</li>
			<li>
				<button class="contact" onclick="js.loadURL('contact.htm');">
					<span class="iconFont" aria-hidden="true" data-icon="A"></span>
					<span class="txt">Contact</span>
				</button>
			</li>

			<c:if test="${sessionData == null || sessionData.user == null}">
			<li>
				<button onclick="js.loadURL('register.htm');" class="register">
					<span class="iconFont" aria-hidden="true" data-icon="A"></span>
					<span class="txt">Register</span>
				</button>
            </li>
			</c:if>

            <li class="last">
                <c:if test="${sessionData == null || sessionData.user == null}">
                    <button onclick="js.loadURL('getLoginPage.htm');" class="signIn">
                        <span class="iconFont" aria-hidden="true" data-icon="A"></span>
                        <span class="txt">Sign In</span>
                    </button>
                </c:if>
                <c:if test="${sessionData != null && sessionData.user != null}">
                    <button onclick="js.loadURL('logout.htm');" class="signIn">
                        <span class="iconFont" aria-hidden="true" data-icon="A"></span>
                        <span class="txt">Sign Out <c:out value="${sessionData.user.firstName}" /></span>
                    </button>
                </c:if>
            </li>
		</ul>
	</nav>
</header>
