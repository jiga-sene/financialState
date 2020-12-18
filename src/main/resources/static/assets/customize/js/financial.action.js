$("#btn-save").hide();

$("#btn-edition").click(function() {
	$(this).hide();
	$("td[contentEditable=false]").attr('contentEditable', true);
	$("#btn-save").show();
});

$("form#frm_state").submit(function(event) {
	// stop propogation
	event.preventDefault();

	$("#btn-save").hide();
	
	var values = new Array();
	values["customerID"] = $("input[id=customerID]").val();
	
	$("td[contentEditable=true]").each(function() {
		values[$(this).attr("id")] = $(this).text();
		// console.log($(this).text());
	});
	
	let toAssociative = (keys, values) => 
	    values.reduce(
	    	(acc, cv) => {
		       acc[acc.shift()] = cv
		       return acc;
		    }, keys);
	let fromAssociative = (assArr) =>({...assArr});

	let serialized = JSON.stringify(fromAssociative(values));
	var post_url = $(this).attr("action");

	$.ajax({
	    url: post_url,
	    dataType: 'text',
	    type: 'post',
	    contentType: 'application/json',
	    data: serialized,
	    success: function( data, textStatus, jQxhr ){
	        console.log( textStatus );
	    },
	    error: function( jqXhr, textStatus, errorThrown ){
	        console.log( errorThrown );
	    }
	});

	$("td[contentEditable=true]").attr('contentEditable', false);
	$("#btn-edition").show();
});