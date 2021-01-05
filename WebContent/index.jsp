<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
	<head>
	
		<%
			String mensagemAlerta = request.getParameter("mensagemAlerta");
		%>
		
		<link rel="stylesheet"
			href="https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/css/bootstrap.min.css"
			integrity="sha384-MCw98/SFnGE8fJT3GXwEOngsV7Zt27NXFoaoApmYm81iuXoPkFOJwJ8ERdknLPMO"
			crossorigin="anonymous">
			
		<link rel="stylesheet" type="text/css" href="css/style.css">
		
		<script src="https://code.jquery.com/jquery-3.5.1.slim.min.js"
			integrity="sha384-DfXdz2htPH0lsSSs5nCTpuj/zy4C+OGpamoFVy38MVBnE+IbbVYUew+OrCXaRkfj"
			crossorigin="anonymous"></script>
		<script
			src="https://cdn.jsdelivr.net/npm/popper.js@1.16.0/dist/umd/popper.min.js"
			integrity="sha384-Q6E9RHvbIyZFJoft+2mJbHaEWldlvI9IOYy5n3zV9zzTtmI3UksdQRVvoxMfooAo"
			crossorigin="anonymous"></script>
		<script
			src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.0/js/bootstrap.min.js"
			integrity="sha384-OgVRvuATP1z7JjHLkuOU7Xw704+h835Lr+6QL9UvYjZE3Ipu6Tp75j7Bh/kR0JKI"
			crossorigin="anonymous"></script>
		
		<script type="text/javascript">
		    function setCookie(key, value) {
		        var expires = new Date();
		        expires.setTime(expires.getTime() + (180 * 24 * 60 * 60 * 1000));
		        document.cookie = key + '=' + value + ';expires=' + expires.toUTCString();
		    }
		
		    function getCookie(key) {
		        var keyValue = document.cookie.match('(^|;) ?' + key + '=([^;]*)(;|$)');
		        return keyValue ? keyValue[2] : null;
		    }
		
		    $(document).ready(function() {
		    	
		    	var mensagemAlerta = '<%= mensagemAlerta %>';
		    	
				if(mensagemAlerta != "" && mensagemAlerta != "null"){
					alert(mensagemAlerta);
				}
		
		        document.form.userName.value = getCookie("userName");
		        document.form.projectId.value = getCookie("projectId");
		        document.form.userId.value = getCookie("userId");
		        document.form.description.value = getCookie("description");
		        document.form.apiKey.value = getCookie("apiKey");
		        document.form.mesAno.value = getCookie("mesAno");
		        
		        $("form").submit(function(){
		        	setCookie("userName", document.form.userName.value);
		        	setCookie("projectId", document.form.projectId.value);
		        	setCookie("userId", document.form.userId.value);
		        	setCookie("description", document.form.description.value);
		        	setCookie("apiKey", document.form.apiKey.value);
		        	setCookie("mesAno", document.form.mesAno.value);
		  		});
		        
		    });
		
		</script>
		
	</head>

	<body>
	
		<div class="container-fluid box onebox" style="margin-top: 10px;">
			<div class="content">
				<div class="col-md-12">
					<form action="/TSCF/enter" method="post" name="form" id="form">
					<input type="hidden" name="action" value="inserir"/>
						
						<div class="form-group">
							<label for="userName"> Your full name (Excel) * </label>
							<input type="text" class="form-control" name="userName" id="userName" />
						</div>
						
						<div class="form-group">
							<label for="projectId"> Project ID (Clockify) * </label> 
							<input xvalue="5dee5c6dffff90311c84e5b3" type="text" class="form-control" name="projectId" id="projectId" />
						</div>
						
						<div class="form-group">
							<label for="userId"> User ID (Clockify) * </label> 
							<input xvalue="5dc07145b36ea8270fcf00c7" type="text" class="form-control" name="userId" id="userId" />
						</div>
						
						<div class="form-group">
							<label for="description"> Entry Description (Clockify) </label> 
							<input xvalue="BS | RJ | Saúde | Concierge | [Módulos Administrativos] - Desenvolvimento" type="text" class="form-control" name="description" id="description" />
						</div>
						
						<div class="form-group">
							<label for="apiKey"> Api Key (Clockify) * </label> 
							<input xvalue="Xg5CvFFchCIa1aT8" type="text" class="form-control" name="apiKey" id="apiKey" />
						</div>
		
						<div class="form-group">
		
							<label for="mes-ano"> Month (mm/yyyy) * </label> 
							<input xvalue="09/2020" type="text" class="form-control" name="mesAno" id="mesAno" />
						</div>
						
						
						<div class="form-group">
		
							<label for="content"> Content (iFractal) * </label> 
							<textarea class="form-control" id="content" name="content" rows="3"></textarea>
						</div>
						
						<div class="form-group" style="text-align: left">
								
							<input type="checkbox" name="isExport" id="isExport"/> Exportar
						</div>
		
						<div class="form-group" style="text-align: center">
							<div class="loader"></div>	
							<button type="submit" class="btn btn-primary">Submit</button>
						</div>
						
					</form>
				</div>
			</div>
		</div>
	
	
	</body>

</html>

