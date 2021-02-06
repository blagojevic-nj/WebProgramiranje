function prevent(e) {
	e.preventDefault();
	return false;
  }

function dodajPretraguKorisnikaAdminu()
{
	
	let forma = $("<form id='formAdminPretraga' onsubmit='return prevent(event)'></form>");
	let inputGroup = $('<div class="input-group"></div>'); //sadrzi sve ispod do..
	let imeKorisnika = $('<input id="imePretragaAdmin" type="text" class="form-control" placeholder="Ime Korisnika" aria-label="Ime Korisnika" aria-describedby="basic-addon2">');
	let prezKorisnika = $('<input id="przPretragaAdmin" type="text" class="form-control" placeholder="Prezime Korisnika" aria-label="Prezime Korisnika" aria-describedby="basic-addon2">');
	let usrKorisnika = $('<input id="usrPretragaAdmin" type="text" class="form-control" placeholder="Korisničko Ime" aria-label="Korisničko Ime" aria-describedby="basic-addon2">');

	//dugmad

	let divDugmad = $('<div class="input-group-append"></div');
	let dugmePretragaKarte = $('<button id="dugmePretragaKorisnikaAdmin" onclick="pretraziAdmin()" class="btn btn-outline-secondary" type="submit" ><i class="fas fa-search"></i></button>');


	let divFilterPretraga = $('<div id="filterPretragaKarte" class="dropdown show"></div');
	let dugmeFilterGornje = $('<button  class="btn btn-outline-secondary" type="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false"><i class="fas fa-filter"></i></button>')
	let divDropdown = $('<div class="dropdown-menu" aria-labelledby="dropdownMenuLink"></div');
	let multiselect = $('<select id="TipSelectKorisniciAdmin"  class="multiselect-ui form-control" multiple="multiple"></select>')
	let sumnjivi = $("<option value='sumnjivi'>Sumnjivi</option>");
	let admin = $("<option value='admin'>Admin</option>");
	let prodavac = $("<option value='prodavac'>Prodavac</option>");
	let kupac = $("<option value='kupac'>Kupac</option>");
	let zlatni = $("<option value='zlatni'>Zlatni</option>");
	let srebrni = $("<option value='srebrni'>Srebrni</option>");
	let bronzani = $("<option value='bronzani'>Bronzani</option>");
	multiselect.append(sumnjivi).append(admin).append(prodavac).append(kupac).append(zlatni).append(srebrni).append(bronzani);
	let dugmeFilter = $('<button id="filterButtonKorisnikAdmin" onclick="filtrirajAdmin()" class="btn btn-outline-secondary btn-dark"><i class="fas fa-filter"></i>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Filter</button>');
	-

	divDropdown.append(multiselect);
	divDropdown.append(dugmeFilter);
	divFilterPretraga.append(divDropdown);
	divFilterPretraga.append(dugmeFilterGornje);
	//sort

	let divSortPretraga = $('<div id="filterPretragaKarte" class="dropleft show"></div');
	let dugmeSortGornje = $('<button id="sortPretragaAdmin" class="btn btn-outline-secondary" type="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false"><i class="fas fa-sort-amount-down"></i></button>')
	let divdropdown2 = $('<div class="dropdown-menu" aria-labelledby="dropdownMenuLink"></div>');
	let sort11 = $('<a id="sort1Admin" onclick="sort1Admin()" class="dropdown-item" href="#">Ime Rastuće</a>');
	let sort21 = $('<a id="sort2Admin" onclick="sort2Admin()" class="dropdown-item" href="#">Ime Opadajuće</a>');
	let sort31 = $('<a id="sort3Admin" onclick="sort3Admin()" class="dropdown-item" href="#">Prezime Rastuće</a>');
	let sort41 = $('<a id="sort4Admin" onclick="sort4Admin()" class="dropdown-item" href="#">Prezime Opadajuće</a>');
	let sort51 = $('<a id="sort5Admin" onclick="sort5Admin()" class="dropdown-item" href="#">Username Rastuće</a>');
	let sort61 = $('<a id="sort6Admin" onclick="sort6Admin()" class="dropdown-item" href="#">Username Opadajuće</a>');	
	let sort71 = $('<a id="sort7Admin" onclick="sort7Admin()" class="dropdown-item" href="#">Bodovi Rastuće</a>');
	let sort81 = $('<a id="sort8Admin" onclick="sort8Admin()" class="dropdown-item" href="#">Bodovi Opadajuće</a>');
	divdropdown2.append(sort11).append(sort21).append(sort31).append(sort41).append(sort51).append(sort61).append(sort71).append(sort81);
	divSortPretraga.append(dugmeSortGornje).append(divdropdown2);
	//
	divDugmad.append(dugmePretragaKarte);
	divDugmad.append(divFilterPretraga);
	divDugmad.append(divSortPretraga);
	inputGroup.append(imeKorisnika).append(prezKorisnika).append(usrKorisnika).append(divDugmad);
	forma.append(inputGroup);
	$("#PretragaTabele").html(forma);
	
}

/**PRETRAGA KORISNIKA ADMIN **/
function pretraziAdmin(){
	let ime = $("#imePretragaAdmin").val();
	let prz = $("#przPretragaAdmin").val();
	let usr = $("#usrPretragaAdmin").val();


	let upit = {ime:ime,prz:prz,usr:usr}
	$.post({url:'/WP_Tickets/rest/korisnici/search/',
        data: JSON.stringify(upit),
        contentType: 'application/json',
			success: function(korisnici){
				ocistiPretraguAdmin();
				if(!korisnici || korisnici.length == 0){
					
				}else{
						for(let kor of korisnici)
						dodajRedKorisnika(kor)
				}
			}
        })
}

/**FILTER KORISNIKA ADMIN **/
function filtrirajAdmin(){
	let select = $("#TipSelectKorisniciAdmin").prop("selectedOptions");
	let lista=[];
	for(s of select)
	{
		lista.push($(s).val());
	}
	$.post({url:'/WP_Tickets/rest/korisnici/filter/',
        data: JSON.stringify(lista),
        contentType: 'application/json',
			success: function(filtrirani){
				ocistiPretraguAdmin();
				if(!filtrirani || filtrirani.length == 0){
					
				}else{
					 for(let kor of filtrirani)
						dodajRedKorisnika(kor)
				}

			}
        })
}

function ocistiPretraguAdmin()
{
	$("#tabela-karata").empty();
}

/**Sortiranje******************************************* */

function sort1Admin(){
	$.get("/WP_Tickets/rest/korisnici/sort/1",function(sortirano)
	{
		ocistiPretraguAdmin();
        for(let kor of sortirano)
             dodajRedKorisnika(kor)

	});

}

function sort2Admin(){

	$.get("/WP_Tickets/rest/korisnici/sort/2",function(sortirano)
	{
		ocistiPretraguAdmin();
        for(let kor of sortirano)
             dodajRedKorisnika(kor)

	});

}

function sort3Admin(){
	$.get("/WP_Tickets/rest/korisnici/sort/3",function(sortirano)
	{
		ocistiPretraguAdmin();
        for(let kor of sortirano)
             dodajRedKorisnika(kor)

	});

}

function sort4Admin(){
	$.get("/WP_Tickets/rest/korisnici/sort/4",function(sortirano)
	{
		ocistiPretraguAdmin();
        for(let kor of sortirano)
             dodajRedKorisnika(kor)

	});

}

function sort5Admin(){
	$.get("/WP_Tickets/rest/korisnici/sort/5",function(sortirano)
	{
		ocistiPretraguAdmin();
        for(let kor of sortirano)
             dodajRedKorisnika(kor)

	});

}

function sort6Admin(){
	$.get("/WP_Tickets/rest/korisnici/sort/6",function(sortirano)
	{
		ocistiPretraguAdmin();
        for(let kor of sortirano)
             dodajRedKorisnika(kor)

	});

}

function sort7Admin(){
	$.get("/WP_Tickets/rest/korisnici/sort/7",function(sortirano)
	{
		ocistiPretraguAdmin();
        for(let kor of sortirano)
             dodajRedKorisnika(kor)

	});

}

function sort8Admin(){
	$.get("/WP_Tickets/rest/korisnici/sort/8",function(sortirano)
	{
		ocistiPretraguAdmin();
        for(let kor of sortirano)
             dodajRedKorisnika(kor)

	});

}



/*Multiselect admin*/
$('#TipSelectKorisniciAdmin').on('click',function(e)
{
e.preventDefault();
})