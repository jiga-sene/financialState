let stateTypeToCalculationNetValue = ["BILACT", "BILPAS"];

function replaceFinancial(html) {
	$('tbody#financial').replaceWith($(html));
}

function calculationNet() {
	$("td[id*='Net']").each(function() {
		$(this).text(Number($("td#" + $(this).attr('id').replace('Net', 'Brut')).text()) - Number($("td#" + $(this).attr('id').replace('Net', 'Am_Pr')).text()));
	});
}

$("#btn-save").hide();

$("#btn-edition").click(function() {
	$(this).hide();
	$("td[contentEditable=false]").attr('contentEditable', true);
	if(stateTypeToCalculationNetValue.includes($("span#stateTypeCode").text())){
		$("th[id*='Net'],td[id*='Net']").hide();
		$("th#columnResource").attr('colspan', Number($("th#columnResource").attr('colspan'))-1);
	}
	$("#btn-save").show();
});

$("form#frm_state").submit(function(event) {
	// stop propogation
	event.preventDefault();

	$("#btn-save").hide();
	
	if(stateTypeToCalculationNetValue.includes($("span#stateTypeCode").text()))
		calculationNet();

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
	let fromAssociative = (assArr) => ({ ...assArr });

	let serialized = JSON.stringify(fromAssociative(values));
	var post_url = $(this).attr("action");

	$.ajax({
		url: post_url,
		dataType: 'text',
		type: 'post',
		contentType: 'application/json',
		data: serialized,
		success: function(data, textStatus, jQxhr) {
			replaceFinancial(data);
			console.log(textStatus);
		},
		error: function(jqXhr, textStatus, errorThrown) {
			console.log(errorThrown);
		}
	});

	$("td[contentEditable=true]").attr('contentEditable', false);
	$("#btn-edition").show();
	
	if(stateTypeToCalculationNetValue.includes($("span#stateTypeCode").text())){
		$("th[id*='Net'],td[id*='Net']").show();
		$("th#columnResource").attr('colspan', Number($("th#columnResource").attr('colspan'))+1);
	}
});