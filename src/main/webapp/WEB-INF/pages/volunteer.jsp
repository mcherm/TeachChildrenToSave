<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@include file="include/html-head.jsp" %>

    <title>Teach Children To Save - Add Volunteer</title>

	<link rel="stylesheet" href="tcts/css/master.css">

	<script type="text/javascript" src="tcts/js/scripts.js"></script>
	
</head>
<body class="volunteer_registration">

	<a href="#main" class="ada-read">Skip to main content</a>
	
	<div class="decor"></div>
	
	<%@include file="include/header.jsp" %>
	
	<div class="mainCnt">
	
	<%@include file="include/navigation.jsp" %>	

		<main id="main">

			<h1>Volunteer Information</h1>
			
			<form:form method="POST" action="addVolunteer.htm" modelAttribute="volunteer">

				<%-- error example...if executing this is possible, given our timelines --%>
				<%-- 1. add the css class 'error' to the label --%>
				<%-- 2. include 'errorText' span --%>
				<%-- we should (if possible) also update the H1 tag as well, appending something like: ", form has errors" --%>
				
				<div class="formElementCnt">
					<label class="error">
						<div class="inputCnt">
							<div class="info">
							<span class="errorText">
								Error: 
							</span>
								First Name
							</div>
							<input type="text" />
						</div>
					</label>
				</div>
				
			
				<div class="formElementCnt">
					<label>
						<div class="inputCnt">
							<div class="info">
								First Name
							</div>
							<form:input path="firstName" />
						</div>
					</label>
				</div>
				
				<div class="formElementCnt">
					<label>
						<div class="inputCnt">
							<div class="info">
								Last Name
							</div>
							<form:input path="lastName" />
						</div>
					</label>
				</div>				
				
				<div class="formElementCnt">
					<label>
						<div class="inputCnt">
							<div class="info">
								Email Address
							</div>
							<form:input path="emailAddress" />
						</div>
					</label>
				</div>								
				
				<div class="formElementCnt">
					<label>
						<div class="inputCnt">
							<div class="info">
								Confirm Email Address
							</div>
							<form:input path="confirmEmailAddress" />
						</div>
					</label>
				</div>			

				<div class="formElementCnt">
					<label>
						<div class="inputCnt">
							<div class="info">
								Password
							</div>
							<form:input type="password" path="password" />
						</div>
					</label>
				</div>
				
				<div class="formElementCnt">
					<label>
						<div class="inputCnt">
							<div class="info">
								Confirm Password
							</div>
							<form:input type="password" path="confirmPassword" />
						</div>
					</label>
				</div>						
				
				<div class="formElementCnt">
					<label>
						<div class="inputCnt">
							<div class="info">
								Work Address Line 1
							</div>
							<form:input path="addressLine1" />
						</div>
					</label>
				</div>	
				
				<div class="formElementCnt">
					<label>
						<div class="inputCnt">
							<div class="info">
								Work Address Line 2
							</div>
							<form:input path="addressLine2" />
						</div>
					</label>
				</div>								
			
				<div class="formElementCnt">
					<label>
						<div class="inputCnt">
							<div class="info">
								City
							</div>
							<form:input path="city" />
						</div>
					</label>
				</div>				
				
				<div class="formElementCnt">
					<label>
						<div class="inputCnt">
							<div class="info">
								State
							</div>
							<form:input path="state" />
						</div>
					</label>
				</div>	

				<div class="formElementCnt">
					<label>
						<div class="inputCnt">
							<div class="info">
								Zip
							</div>
							<form:input path="zipcode" />
						</div>
					</label>
				</div>	
				
				<div>
					<button type="submit" value="Submit">Submit</button>
				</div>
			  
			</form:form>

		</main>
		
	</div><%-- .mainCnt --%>

	<%@include file="include/footer.jsp" %>

</body>
</html>



	<%-- original form JIC --%>
	<%--
    <div id="container" class="sansserif">
    	<div class="row_div">
	    	<div class="row_div_left">
		        <div class="caption_div"><form:label path="firstName">First Name</form:label></div>
		        <div class="field_div row_div_field_left_joint"><form:input path="firstName" /></div>
		     </div>
	        <div class="row_div_right">
	        	<div class="row_div_right"><form:label path="lastName">Last Name</form:label></div>
	        	<div class="field_div"><form:input path="lastName" /></div>
	        </div>
        </div>
    
    <div class="row_div">
    	<div class="row_div_left">
	        <div class="caption_div"><form:label path="emailAddress">Email Address</form:label></div>
	        <div class="field_div row_div_field_left_joint"><form:input path="emailAddress" /></div>
		</div>        
    
	    <div class="row_div_right">
	        <div class="caption_div"><form:label path="confirmEmailAddress">Confirm Email Address</form:label></div>
	        <div class="field_div"><form:input path="confirmEmailAddress" /></div>
	    </div>
    </div>
    
    <div class="row_div">
    	<div class="row_div_left">
            <div class="caption_div"><form:label path="password">password</form:label></div>
	        <div class="field_div row_div_field_left_joint"><form:input type="password" path="password" /></div>
    	</div>
    
    	<div class="row_div_right">
	        <div class="caption_div"><form:label path="confirmPassword">Re-Enter Password</form:label></div>
	        <div class="field_div"><form:input type="password" path="confirmPassword" /></div>
    	</div>
    </div>
    
    <div class="row_div">
    	<div class="row_div_left">
	        <div class="caption_div"><form:label path="addressLine1">Work Address Line1</form:label></div>
	        <div class="field_div row_div_field_left_joint"><form:input path="addressLine1" /></div>
        </div>
       
        <div class="row_div_right">
	        <div class="caption_div"><form:label path="addressLine2">Work Address Line2</form:label></div>
	        <div class="field_div"><form:input path="addressLine2" /></div>
    	</div>
    </div>
    
    <div class="row_div">
	    <div class="row_div_left">
	        <div class="caption_div"><form:label path="city">City</form:label></div>
	        <div class="field_div row_div_field_left_joint"><form:input path="city" /></div>
	    </div>
    
	    <div class="row_div_right">
	        <div class="caption_div"><form:label path="state">State</form:label></div>
	        <div class="field_div"><form:input path="state" /></div>
	    </div>
    </div>
    
    <div class="row_div">
    	<div class="row_div_left">
	        <div><form:label path="zipcode">zip</form:label></div>
	        <div><form:input path="zipcode" /></div>
        </div>
    </div>
    
    <div class="row_div">
        <div>
            <input type="submit" value="Submit"/>
        </div>
    </div>
  </div>
  --%>