/// <reference path="C:\Users\PC\Desktop\plugIn\typings\globals\jquery\index.d.ts" />



function zameniKarte(karte)
{
    $("#tabela-karata").empty();
        for(let kart of karte)
            dodajRedKarte(kart);
}

function prevent(e) {
	e.preventDefault();
	return false;
  }


function dodajPretraguKarti()
{
	
	let forma = $("<form id='formTicketsPretraga' onsubmit='return prevent(event)'></form>");
	let inputGroup = $('<div class="input-group"></div>'); //sadrzi sve ispod do..
	let nazivTicketPretraga = $('<input id="nazivPretragaKarte" type="text" class="form-control" placeholder="Naziv Manifestacije" aria-label="Naziv Manifestacije" aria-describedby="basic-addon2">');
	let cenaOdTiket = $('<input id="cenaOdKarte" type="text" class="form-control" placeholder="Cena Od:" aria-label="Cena Od" aria-describedby="basic-addon2">');
	let cenaDoTiket = $('<input id="cenaDoKarte" type="text" class="form-control" placeholder="Cena Do:" aria-label="Cena Do" aria-describedby="basic-addon2">');
	let labela1 = $('<div class="input-group-prepend"><div class="input-group-text"><i class="fas fa-calendar-alt"></i>&nbsp;&nbsp;Od:</div></div>');
	let datumOd = $('<input class="form-control col-10" type="date" aria-label="Datum Od:" placeholder="Cena Do:" id="datumOdKarte">');
	let labela2 = $('<div class="input-group-prepend"><div class="input-group-text"><i class="fas fa-calendar-alt"></i>&nbsp;&nbsp;Do:</div></div>');
	let datumDo = $('<input class="form-control col-10" type="date" aria-label="Datum Od:" placeholder="Cena Do:" id="datumDoKarte">');
	//dugmad

	let divDugmad = $('<div class="input-group-append"></div');
	let dugmePretragaKarte = $('<button id="dugmePretragaKarte" onclick="pretraziKarte()" class="btn btn-outline-secondary" type="submit" ><i class="fas fa-search"></i></button>');


	let divFilterPretraga = $('<div id="filterPretragaKarte" class="dropdown show"></div');
	let dugmeFilterGornje = $('<button  class="btn btn-outline-secondary" type="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false"><i class="fas fa-filter"></i></button>')
	let divDropdown = $('<div class="dropdown-menu" aria-labelledby="dropdownMenuLink"></div');
	let multiselect = $('<select id="TipSelectKarte"  class="multiselect-ui form-control" multiple="multiple"></select>')
	let vip = $("<option value='vip'>VIP</option>");
	let regular = $("<option value='regular'>Regular</option>");
	let fanpit = $("<option value='fanpit'>Fan Pit</option>");
	let rezervisana = $("<option value='rezervisana'>Rezervisane</option>");
	let odustanak = $("<option value='odustanak'>Otkazane</option>");
	multiselect.append(vip).append(regular).append(fanpit).append(rezervisana).append(odustanak);
	let dugmeFilter = $('<button id="filterButtonKarte" onclick="filtrirajKarte()" class="btn btn-outline-secondary btn-dark"><i class="fas fa-filter"></i>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Filter</button>');
	-

	divDropdown.append(multiselect);
	divDropdown.append(dugmeFilter);
	divFilterPretraga.append(divDropdown);
	divFilterPretraga.append(dugmeFilterGornje);
	//sort

	let divSortPretraga = $('<div id="filterPretragaKarte" class="dropleft show"></div');
	let dugmeSortGornje = $('<button id="sortPretragaKarte" class="btn btn-outline-secondary" type="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false"><i class="fas fa-sort-amount-down"></i></button>')
	let divdropdown2 = $('<div class="dropdown-menu" aria-labelledby="dropdownMenuLink"></div>');
	let sort11 = $('<a id="sort1Karte" onclick="sort1Karte()" class="dropdown-item" href="#">Ceni Rastuće</a>');
	let sort21 = $('<a id="sort2Karte" onclick="sort2Karte()" class="dropdown-item" href="#">Ceni Opadajuće</a>');
	let sort31 = $('<a id="sort3Karte" onclick="sort3Karte()" class="dropdown-item" href="#">Datumu Rastuće</a>');
	let sort41 = $('<a id="sort4Karte" onclick="sort4Karte()" class="dropdown-item" href="#">Datumu Opadajuće</a>');
	let sort51 = $('<a id="sort5Karte" onclick="sort5Karte()" class="dropdown-item" href="#">Naziv Rastuće</a>');
	let sort61 = $('<a id="sort6Karte" onclick="sort6Karte()" class="dropdown-item" href="#">Naziv Opadajuće</a>');
	divdropdown2.append(sort11).append(sort21).append(sort31).append(sort41).append(sort51).append(sort61)
	divSortPretraga.append(dugmeSortGornje).append(divdropdown2);
	//
	divDugmad.append(dugmePretragaKarte);
	divDugmad.append(divFilterPretraga);
	divDugmad.append(divSortPretraga);
	inputGroup.append(nazivTicketPretraga).append(cenaOdTiket).append(cenaOdTiket).append(cenaDoTiket).append(labela1).append(datumOd).append(labela2).append(datumDo).append(divDugmad);
	forma.append(inputGroup);
	$("#PretragaTabele").html(forma);
	
}


/*Multiselect Karte*/
$('#TipSelectKarte').on('click',function(e)
{
    e.preventDefault();
})



/**PRETRAGA KARTI **/
function pretraziKarte(){
	let naziv = $("#nazivPretragaKarte").val();
	let cenaod = $("#cenaOdKarte").val();
    let cenado = $("#cenaDoKarte").val();	
    let datumod = $("#datumOdKarte").val();
	let datumdo = $("#datumDoKarte").val();


	let upit = {naziv:naziv,cenaod:cenaod,cenado:cenado,datumod:datumod,datumdo:datumdo}
	$.post({url:'/WP_Tickets/rest/Karte/search/',
        data: JSON.stringify(upit),
        contentType: 'application/json',
			success: function(karte){
				ocistiPretraguAdmin();
				if(!karte || karte.length == 0){
					alert("Nema ni jedne karte!")
				}else{						
                    alert("UspesnoPretrazene karte")
                    zameniKarte(karte);
				}
			}
        })
}

/**FILTER KARTI **/
function filtrirajKarte(){
	let select = $("#TipSelectKarte").prop("selectedOptions");
	let lista=[];
	for(s of select)
	{
		lista.push($(s).val());
	}
	$.post({url:'/WP_Tickets/rest/Karte/filter/',
        data: JSON.stringify(lista),
        contentType: 'application/json',
			success: function(karte){
				ocistiPretraguAdmin();
				if(!karte || karte.length == 0){
					alert("Nema ni jedne karte!")
				}else{
                        alert("FILTER KARTE")
                        zameniKarte(karte);
                    }

			}
        })
}



/**Sortiranje******************************************* */

function sort1Karte(){
	$.get("/WP_Tickets/rest/Karte/sort/1",function(karte)
	{
        zameniKarte(karte);
	});
}

function sort2Karte(){
	$.get("/WP_Tickets/rest/Karte/sort/2",function(karte)
	{
        zameniKarte(karte);
	});
}

function sort3Karte(){
	$.get("/WP_Tickets/rest/Karte/sort/3",function(karte)
	{
        zameniKarte(karte);
	});
}

function sort4Karte(){
	$.get("/WP_Tickets/rest/Karte/sort/4",function(karte)
	{
        zameniKarte(karte);
	});
}

function sort5Karte(){
	$.get("/WP_Tickets/rest/Karte/sort/5",function(karte)
	{
        zameniKarte(karte);
	});
}

function sort6Karte(){
	$.get("/WP_Tickets/rest/Karte/sort/6",function(karte)
	{
        zameniKarte(karte);
	});
}

