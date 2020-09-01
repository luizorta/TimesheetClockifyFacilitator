<%@ page language="java" %>

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

<script>
	
</script>

<style>

</style>





<div class="container-fluid box onebox">
	<div class="content">
		<div class="col-md-12">
			<form role="form" action="/TSCF/enter" method="post">
			<input type="hidden" name="action" value="inserir"/>
				
<!-- 				
				"Xg5CvFFchCIa1aT8" "LUIZ EDUARDO ARPELAU ORTA" "7" "2020" "Description" "5dee5c6dffff90311c84e5b3" "5dc07145b36ea8270fcf00c7"
				String apiKey      = args[0];
//		String userName    = args[1];
//		String userId      = args[6];
//		
//		int mes            = Integer.parseInt(args[2]);
//		int ano            = Integer.parseInt(args[3]);
//		String description = args[4];
//		String projectId   = args[5]; -->
				
				
				<div class="form-group">
					<label for="userName"> Your full name (Excel) * </label>
					<input value="LUIZ EDUARDO ARPELAU ORTA" type="text" class="form-control" name="userName" id="userName" />
				</div>
				
				<div class="form-group">
					<label for="projectId"> Project ID (Clockify) * </label> 
					<input value="5dee5c6dffff90311c84e5b3" type="text" class="form-control" name="projectId" id="projectId" />
				</div>
				
				<div class="form-group">
					<label for="userId"> User ID (Clockify) * </label> 
					<input value="5dc07145b36ea8270fcf00c7" type="text" class="form-control" name="userId" id="userId" />
				</div>
				
				<div class="form-group">
					<label for="description"> Entry Description (Clockify) </label> 
					<input value="BS | RJ | Saúde | Concierge | [Módulos Administrativos] - Desenvolvimento" type="text" class="form-control" name="description" id="description" />
				</div>
				
				<div class="form-group">
					<label for="apiKey"> Api Key (Clockify) * </label> 
					<input value="Xg5CvFFchCIa1aT8" type="text" class="form-control" name="apiKey" id="apiKey" />
				</div>

				<div class="form-group">

					<label for="mes-ano"> Month (mm/yyyy) * </label> 
					<input value="08/2020" type="text" class="form-control" name="mes-ano" id="mes-ano" />
				</div>
				
				
				<div class="form-group">

					<label for="content"> Content (iFractal) * </label> 
					<textarea class="form-control" id="content" name="content" rows="3"></textarea>
				</div>

				<div class="form-group" style="text-align: center">
					<div class="loader"></div>	
					<button type="submit" class="btn btn-primary">Submit</button>
				</div>
				
			</form>
		</div>
	</div>
</div>
