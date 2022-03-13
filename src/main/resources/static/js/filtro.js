var filtro = {
		'autorizar' : function(f) {
		
				
				var dataParameters = {
				}

				$.ajax({
					url : "http://localhost:8001/filtrado/api/autorizacion",
					type : "GET",
					responseType:'application/json',
					data: dataParameters,
					dataType: "json",
					success : function(response) {
						if (response) {
							console.log('Entro');
						}
					},
					error: function (xhr, status) {
		                alert("error");
		            },
					complete : function() {
						
					}
				});
				
				
			},
}


$(document).ready(function() {
	$("#btnAceptarLogin").click(function(ev) {
		ev.preventDefault();
        ev.stopImmediatePropagation();
		filtro.autorizar();
    });
});