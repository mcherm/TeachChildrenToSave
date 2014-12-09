<!DOCTYPE html>
<html lang="en-US">
<head>

	<title>Teach Children To Save</title>  

	<link rel="stylesheet" href="tcts/css/master.css">
	
	<script type="text/javascript" src="tcts/js/scripts.js"></script>
    <%@include file="WEB-INF/pages/include/commonHead.jsp"%>


<%-- commenting out the jquery call until we actually need to use it --%>	
<%--	<script type="text/javascript" src="tcts/js/jquery-1.11.1.min.js"></script>  --%>

</head>  
<body class="home">  

	<a href="#main" class="ada-read">Skip to main content</a>
	
	<div class="decor"></div>
	
	<%@include file="WEB-INF/pages/include/header.jsp" %>
	
	<div class="mainCnt">
	
	<%@include file="WEB-INF/pages/include/navigation.jsp" %>	

		<main id="main">
			<h1>
				2015 Teach Children to Save Day! 
				<br />
				April 8-9-10
			</h1>

			<h2>
				3<sup>rd</sup> and 4<sup>th</sup> graders learn critical lessons on personal finance and economics in a 45 minute lesson
			</h2>
			
			<h3>Teachers</h3>
			
			<p>Why participate?</p>
			
			<ul class="program_highlights">
				<li><span>Most students don't get these financial literacy lessons any other place</span></li>
				<li><span>Research has shown that when people are taught the basics of money management as children they are more likely to be fiscally fit as adults</span></li>
				<li><span>It only takes 45 minutes</span></li>
			</ul>
			
			<button onclick="location.href='registerTeacher.htm'">Sign up my class</button>
			
			<h3>Volunteers</h3> 
			
			<p>Why volunteer?</p>
			
			<ul class="program_highlights">
				<li><span>Research has shown that when people are taught the basics of money management as children they are more likely to be fiscally fit as adults</span></li>
				<li><span>Provide valuable financial lessons most children don't won't receive any other time</span></li>
				<li><span>Choose from a variety of locations that are convenient for you</span></li>
				<li><span>It only takes 45 minutes</span></li>
				<li><span>You'll receive all the training material you need</span></li>
			</ul>
			
			<button onclick="">Volunteer</button>			
			
			<h3>Bank Community Affairs Departments</h3>
			
			<p>Why participate?</p>
			
			<ul class="program_highlights">
				<li><span>Your bank may get CRA credits</span></li>
				<li><span>Over 90% of Delaware banks participate</span></li>
				<li><span>Note: Only members of the Delaware Banker's Associations can participate</span></li>
			</ul>
	
			<button onclick="">Register my bank to join</button>	
			
		    <center>
		    <br>
		    <a href="event.htm">Project Devs, click Here for Event Registration</a>
		    </center>  

		</main>
		
		<aside>
			<img src="tcts/img/happy-kids.png" alt="" aria-hidden="true">	
		</aside>
		
	</div><%-- .mainCnt --%>

	<%@include file="WEB-INF/pages/include/footer.jsp" %>


</body>  
</html>  