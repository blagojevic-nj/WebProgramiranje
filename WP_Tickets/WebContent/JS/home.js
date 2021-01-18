/// <reference path="C:\Users\PC\Desktop\plugIn\typings\globals\jquery\index.d.ts" />
$.noConflict()
$(document).ready(function () {
	$('#dismiss, .overlay').on('click', function () {
		$('#sidebar').removeClass('active');
		$('.overlay').removeClass('active');
	});

	$('#sidebarCollapse').on('click', function () {
		$('#sidebar').addClass('active');
		$('.overlay').addClass('active');
		$('.collapse.in').toggleClass('in');
		$('a[aria-expanded=true]').attr('aria-expanded', 'false');
		$('#sidebarCollapse').addClass('sakrijDugme');
		$('#sidebarCollapse').hide();
		


	});

	$('#dismiss').on('click', function () {
		$('#sidebarCollapse').removeClass('sakrijDugme');	
		$('#sidebarCollapse').show();
	
	});

	$.get("/WP_Tickets/rest/Manifestacije/",function(manifestacije)
		{
			napraviTabelu(manifestacije)
		}
	);


});


function dodajManifestaciju(m){

	let manifestacija = $("<div class='manifestacija'></div>");
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
			if(i*j>numOfElements)
			{
				break;
			}else{
				let td = $('<td></td>');
				let manifestacija = m[i*j];
				td.append(dodajManifestaciju(manifestacija));
				tr.append(td);
			}
		}

/* 		alert(m[i].datumVremeOdrzavanja);
 */		$("#tabelaManifestacija").append(tr);
		
	} 
}