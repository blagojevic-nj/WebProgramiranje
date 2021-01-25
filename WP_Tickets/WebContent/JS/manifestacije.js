var map;
var marker;
var markers;
var markerVectorLayer;
var mapaKoordinate = [];
var tipLokacije;
map = new ol.Map({
	        view: new ol.View({center: ol.proj.transform([19.84, 45.25], 'EPSG:4326', 'EPSG:3857'), zoom:14}),
	        layers: [new ol.layer.Tile({
	                source: new ol.source.OSM()
	        })],
	        target:'lokaciijaSelectMapa'
	    });
	
	marker = new ol.Feature({
	    geometry: new ol.geom.Point(ol.proj.transform([19.84, 45.25], 'EPSG:4326', 'EPSG:3857')),
	});
	
	markers = new ol.source.Vector({
	    features: []
	});
	
	markerVectorLayer = new ol.layer.Vector({
	    source: markers,
	});
	map.addLayer(markerVectorLayer);

map.on('singleclick', function(evt){ 		
   		
		map.removeLayer(markerVectorLayer);
    	var coor = ol.proj.transform(evt.coordinate, 'EPSG:3857', 'EPSG:4326');
    	mapaKoordinate = coor;
    	marker = new ol.Feature({
            geometry: new ol.geom.Point(ol.proj.transform(coor, 'EPSG:4326', 'EPSG:3857')),
        });
        
        markers = new ol.source.Vector({
            features: [marker]
        });

        markerVectorLayer = new ol.layer.Vector({
            source: markers,
        });
        map.addLayer(markerVectorLayer);
   		
  	});
  	
$(document).ready(function (){

/**Ucitavanje manifestacija */
    $.get("/WP_Tickets/rest/Manifestacije/",function(manifestacije)
    {
        napraviTabelu(manifestacije)
    }
    );


/**Ucitavanje tipova manifestacija */
    $.get("/WP_Tickets/rest/Manifestacije/Tipovi",function(tipovi)
    {
    dodajTipoveManifestacijaUFilterSelect(tipovi)
    }


);
});



function dodajTipoveManifestacijaUFilterSelect(tipovi)
{
	$("#TipSelect").append($('<option value="-1">Nerasprodate</option>'));
	
	for(tip of tipovi)
	{
		let opcija = $('<option value="'+tip.id+'">'+tip.nazivTipa+'</option>');
		$("#TipSelect").append(opcija);
		

	}

}


function dodajManifestaciju(m){

	let manifestacija = $("<div class='manifestacija' onclick='pregledaj("+m.id+")'></div>");
	let slikaContainer = $("<div class='slikaContainer'></div>");
	let slika = $('<img  class="slika" src='+m.poster+' alt="Slika Manifestacije"></img>');
	
	
	let naziv = $('<div ><h2 class="naziv">'+m.naziv+'</h2></div>');
	
	
	let mesto = $("<div class='mesto'></div>");
	let h5 = $('<h5>'+m.lokacija.adresa+'</h5>');
	let h6 = $('<h6>'+m.lokacija.adresa+'</h6>');


	let datum = $("<div class='datum'></div>");
	let datumDodaj = $('<h5><span class="dani">'+m.datumVremeOdrzavanja.split("T")[0].split("-")[2]+'</span class="mesec"><span>'+m.datumVremeOdrzavanja.split("T")[0].split("-")[1]+'</span></h5>')
	let cena = $('<div><span class="cena">Cena:&nbsp;<span class="cenaVal">'+m.cenaREGkarte+'RSD</span> </span></div>');

	let tabelaDole = $("<table id='tabelaDole'></table>");
	let tr = $("<tr></tr>");
	let td1 = $("<td></td>");
	let td2 = $("<td></td>");

	datum.append(datumDodaj);
	td1.append(datum);
	td2.append(cena);
	tr.append(td1).append(td2);
	tabelaDole.append(tr);

	slikaContainer.append(slika);
	mesto.append(h5).append(h6);
	datum.append(datumDodaj);

	manifestacija.append(slikaContainer).append(naziv).append(mesto).append(tabelaDole);

	return manifestacija;
	
	
}

function napraviTabelu(m){
	let numOfElements = m.length;
	let trNum = Math.ceil(numOfElements/4);
	let i;
	let j;
	for (i = 0; i < trNum; i++) {
		let tr = $('<tr></tr>');
		for (j = 0; j < 4; j++) {
			//dodaj manifestaciju od i*j ako je i*j<lenth else break
			if(i*4+j>=numOfElements)
			{
				break;
			}else{
				let td = $('<td></td>');
				let manifestacija = m[i*4+j];
				td.append(dodajManifestaciju(manifestacija));
				tr.append(td);
			}
		}

/* 		alert(m[i].datumVremeOdrzavanja);
 */		$("#tabelaManifestacija").append(tr);
		
	} 
}


function skloniManifestacije(){
	$("#tabelaManifestacija").empty();
}

function zameniManifestacije(noveManifestacije)
{
	skloniManifestacije();
	if(noveManifestacije.length==0)
	{
		poruka = $("<p id='nemaRezultata'>Nema rezultata za datu pretragu...</p>");
		backDugme = $("<a id='reloadManifestacije' href='home.html' onclick='reloadManifestacije()'><i class='fas fa-undo-alt'></i></button>")
		div=$("<div id=prazanReturnUpita></div>")
		div.append(poruka).append(backDugme);
		$("#content").append(div);
	}else{
		napraviTabelu(noveManifestacije);
	}
}

function reloadManifestacije()
{
	$("#prazanReturnUpita").remove();
	$.get("/WP_Tickets/rest/Manifestacije/",function(manifestacije)
    {
        napraviTabelu(manifestacije)
    }
    );
};

/* <a id="sort1"class="dropdown-item" href="#">Ceni Rastuće</a>
<a id="sort2" class="dropdown-item" href="#">Ceni Opadajuće</a>
<a id="sort3" class="dropdown-item" href="#">Datumu Rastuće</a>
<a id="sort4" class="dropdown-item" href="#">Datumu Opadajuće</a>
<a id="sort5" class="dropdown-item" href="#">Naziv Rastuće</a>
<a id="sort6" class="dropdown-item" href="#">Naziv Opadajuće</a>
<a id="sort7" class="dropdown-item" href="#">Lokaciji Rastuće</a>
<a id="sort8" class="dropdown-item" href="#">Lokaciji Opadajuće</a> */

$("#sort1").click(function()
{
	$.get("/WP_Tickets/rest/Manifestacije/sort/1",function(manifestacije)
	{
		zameniManifestacije(manifestacije);
	});

})

$("#sort2").click(function()
{
	$.get("/WP_Tickets/rest/Manifestacije/sort/2",function(manifestacije)
	{
		zameniManifestacije(manifestacije);
	});

})

$("#sort3").click(function()
{
	$.get("/WP_Tickets/rest/Manifestacije/sort/3",function(manifestacije)
	{
		zameniManifestacije(manifestacije);
	});

})

$("#sort4").click(function()
{
	$.get("/WP_Tickets/rest/Manifestacije/sort/4",function(manifestacije)
	{
		zameniManifestacije(manifestacije);
	});

})

$("#sort5").click(function()
{
	$.get("/WP_Tickets/rest/Manifestacije/sort/5",function(manifestacije)
	{
		zameniManifestacije(manifestacije);
	});

})

$("#sort6").click(function()
{
	$.get("/WP_Tickets/rest/Manifestacije/sort/6",function(manifestacije)
	{
		zameniManifestacije(manifestacije);
	});

})

$("#sort7").click(function()
{
	$.get("/WP_Tickets/rest/Manifestacije/sort/7",function(manifestacije)
	{
		zameniManifestacije(manifestacije);
	});

})

$("#sort8").click(function()
{
	$.get("/WP_Tickets/rest/Manifestacije/sort/8",function(manifestacije)
	{
		zameniManifestacije(manifestacije);
	});

})

$("#filterButton").on('click',function(e){
	filtriranjeManifestacija(e);
})

function filtriranjeManifestacija(e){
	e.preventDefault();
	e.stopPropagation();
	alert("Filtriram");
	$("#prazanReturnUpita").remove();
	let select = $("#TipSelect").prop("selectedOptions");
	let lista=[];
	for(s of select)
	{
		lista.push($(s).val());
	}
	$.post({url:'/WP_Tickets/rest/Manifestacije/filter/',
        data: JSON.stringify(lista),
        contentType: 'application/json',
			success: function(noveManifestacije){
				zameniManifestacije(noveManifestacije);
			}
        })
	
	

};

$('#lokaciijaSelectMapa').on('click',function(e)
	{
	e.stopPropagation();
	})

$("#mapaButton").click(function(e){
	e.preventDefault();
	$("#lokacijaPretraga").val(mapaKoordinate);
	tipLokacije = 'koordinate';

})


/*****************************************************    PRETRAGA    ********************************************************************/

function pretragaSubmit(e){
	e.preventDefault();
	$("#prazanReturnUpita").remove();
	let naziv = $("#nazivPretraga").val();
	let lokacija = $("#lokacijaPretraga").val();
	let cenaOd = $("#cenaOdPretraga").val();
	let cenaDo = $("#cenaDoPretraga").val();
	let datumOd = $("#datumOd").val();
	let datumDo = $("#datumDo").val();


	if(tipLokacije !='koordinate')
	{
		tipLokacije='adresa'
	}
	let upit = {naziv:naziv, lokacija:lokacija, tipLokacije:tipLokacije,cenaOd:cenaOd, cenaDo:cenaDo,datumOd:datumOd,datumDo:datumDo}
	$.post({url:'/WP_Tickets/rest/Manifestacije/search/',
        data: JSON.stringify(upit),
        contentType: 'application/json',
			success: function(obj){
				zameniManifestacije(obj);
			}
        })
	tipLokacije='adresa'
	return false;
};


$("#dugmePretraga").click(function(){
	$('#formPretraga').trigger('submit');
});

/*******************************************************    PREGLED     ********************************************************************/

function pregledaj(id){
	$.post({
		url: "/WP_Tickets/rest/Manifestacije/pregled/"+id,
		contentType: "application/json",
		success: function(validacija){
			if(validacija){
				window.location.href = "../HTML/manifestacija.html";
			}
		}
	})
}