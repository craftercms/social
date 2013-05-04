function checkAll() {
	var all=document.getElementsByName('all');
	if (all.length > 0) {
		all = all[0];
	}
	var checklist=document.getElementsByName("item");
	for (var j=0; j<checklist.length; j++) {
		if (all.checked) {
			checklist[j].checked=true;
		} else {
			checklist[j].checked=false;
		}
	}
}

function onsubmitform(param) {
	var cForm = document.getElementById('form-list');
	if (cForm == null || cForm== undefined) {
		cForm = document.getElementById('form-item');
	}
	if(param == 'New' || document.pressed == 'New') {
		cForm.method = "get";
		cForm.action = "new";
		cForm.submit();
		
	} else if(param == 'Filter' || document.pressed == 'Filter') {
		var filter=document.getElementById("filter");
		if (filter != null && filter.value!="") {
			cForm.method = "post";
			cForm.action = "search";
			cForm.submit();
		}
	} else if(param == 'Delete' || param == 'DeleteProp' || document.pressed == 'Delete' || document.pressed == 'DeleteProp') {
		var checklist=document.getElementsByName("item");
		var selectedItems = false;
		for (var j=0; j<checklist.length; j++) {
			if (checklist[j].checked==true) {
				selectedItems = true;
				break;		
			} 
		}
		if (selectedItems==true && (document.pressed == 'Delete' || param == 'Delete' )) {
			cForm.method = "post";
			cForm.action = "delete";
			cForm.submit();
		} else if (selectedItems==true && (document.pressed == 'DeleteProp' || param == 'DeleteProp')) {
			cForm.method = "post";
			cForm.action = "deleteprop";
			cForm.submit();
		} 
	} else if(param == 'Logout' || document.pressed == 'Logout') {
		cForm.method = "get";
		cForm.action = "logout";
		cForm.submit();
	} else if(param == 'Previous' || document.pressed == 'Previous') {
		cForm.method = "get";
		cForm.action = "prev";
		cForm.submit();
	} else if(param == 'Next' || document.pressed == 'Next') {
		cForm.method = "get";
		cForm.action = "next";
		cForm.submit();
		
	} else if(param == 'Create' || document.pressed == 'Create') {
		cForm.method = "post";
		cForm.action = "new";
		cForm.submit();
	} else if(param == 'CreateProp' || document.pressed == 'CreateProp') {
		cForm.method = "post";
		cForm.action = "newprop";
		cForm.submit();
	} else if(param == 'NewProp' || document.pressed == 'NewProp') {
		cForm.method = "get";
		cForm.action = "newprop";
		cForm.submit();
		
	} else if(param == 'CancelProp' || document.pressed == 'CancelProp') {
		cForm.method = "get";
		cForm.action = "getprops";
		cForm.submit();
	} else if(param == 'Cancel' || document.pressed == 'Cancel') {
		cForm.method = "get";
		cForm.action = "get";
		cForm.submit();
	} else if(param == 'Update' || document.pressed == 'Update') {
		cForm.method = "post";
		cForm.action = "update";
		cForm.submit();
	} else if(param == 'UpdateProp' || document.pressed == 'UpdateProp') {
		cForm.method = "post";
		cForm.action = "updateprop";
		cForm.submit();
	} else if(param == 'UserProperties' || document.pressed == 'UserProperties') {
		cForm.method = "get";
		cForm.action = "getprops";
		cForm.submit();
	} else if(param == 'Accounts' || document.pressed == 'Accounts') {
		cForm.method = "get";
		cForm.action = "get";
		cForm.submit();
		
	} 
}  