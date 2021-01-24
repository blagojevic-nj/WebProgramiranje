/// <reference path="C:\Users\PC\Desktop\plugIn\typings\globals\jquery\index.d.ts" />
$.noConflict()

function getDatumVreme(datumvreme){	
	var meseci={"01": "Januar",
            "02": "Februar",
            "03": "Mart",
            "04": "April",
            "05": "Maj",
            "06": "Jun",
            "07": "Jul",
            "08": "Avgust",
            "09": "Septembar",
            "10": "Oktobar",
            "11": "Novembar",
            "12": "Decembar"}

	
    var date = datumvreme.split("T")[0];
    var time = datumvreme.split("T")[1];

    return date.split("-")[2] + ". "+meseci[date.split("-")[1]] + " " + date.split("-")[0] + ". u " + time.split(":")[0] +":"+time.split(":")[1];
}

function otkazi(id){
	$.post({
		url: "/WP_Tickets/rest/Karte/otkazi/" + id,
		success: function(ok){
			if(ok == 'true')
				alert("Otkazana je karta!");
			else if(ok == 'false')
				alert('neka greska!')
		}
	})
}


function dodajRedKarte(karta){
    let tr;
    
    if(karta.tipKarte == "REGULAR"){
    	 tr = $("<tr class='reg-tr' id='"+karta.id+"'></tr>");
    } else if(karta.tipKarte == "FAN_PIT"){
    	tr = $("<tr class='fan-tr' id='"+karta.id+"'></tr>");
    }else if(karta.tipKarte == "VIP"){
    	tr = $("<tr class='vip-tr' id='"+karta.id+"'></tr>");
    }
    let slika = $("<td><img class='slika-karta' src='../images/ticket.png' height='50px'></td>")
    let idKarte = $("<td><p class='idKarte'>"+karta.id+"</p></td>")
    let prodavac = $("<td><p class='prodavac'>Prodaje: "+karta.prodavac+"</p></td>")
    let kupac =  $("<td><p class='kupac'>Kupuje: "+karta.kupac+"</p></td>")
    
    let manifestacija =  $("<td><p class='man'>"+karta.manifestacija+"</p></td>")
    let datumVreme = $("<td><p class='datum-vreme'>"+ getDatumVreme(karta.datumVremeManifestacije)+"</p></td>")
    let cena =  $("<td><p class='cenaKarte'>"+ karta.cena + " din</p></td>")
	let tipKarte = $("<td><p class='tip'>"+ karta.tipKarte + "</p></td>");
	let status = $("<td><p class='tip'>"+ karta.status + "</p></td>");
	
	
	let otkazi = $("<td></td>");
	tr.append(slika);
    tr.append(idKarte);
    tr.append(prodavac);
    tr.append(kupac);
    tr.append(manifestacija);
    tr.append(datumVreme);
    tr.append(cena);
    tr.append(tipKarte);
    tr.append(status);
    if(karta.status == "REZERVISANA"){
    	$.get({
    		url:"/WP_Tickets/rest/korisnici/trenutni",
    		contentType: "application/json",
    		success: function(kor){
    			if(kor.uloga == "KUPAC" && kor.username == karta.kupac){
    				otkazi =  $("<td><button class='otkazi' onclick='otkazi(\""+karta.id+"\")'>Otkazi</button></td>")			
    				tr.append(otkazi);
    			}
    		}
    	})
    }
    	

    $("#tabela-karata").append(tr);
}

$(document).ready(function () {
	$.get({
		url: "/WP_Tickets/rest/korisnici/trenutni",
		contentType: "application/json",
		success: function(value){
			if(value)
			{
				$("#active_user_name").text(value.username)
				$("a[href='login.html']").remove();
				$("a[href='registration.html']").remove();
				$("#logout").attr("hidden", false);
			}else{
				$("#sidebarCollapse").hide();
			}
			
		}
		});
	$("#forma").ready(function(){
	$.get({
		url: "/WP_Tickets/rest/korisnici/trenutni",
		contentType: "application/json",
		success: function(value){
				if(!value)
				{
					$("#error").show();
				} else{
					$.get({
					url: "/WP_Tickets/rest/korisnici/pregled/" + value.username,
					contentType: "application/json",
					success: function(korisnik){
							if(korisnik == null){
								$("#error").show();
							}else{
								postaviPolja(value);
							}
						}
					});
				}
			}
		});
	})
	$("#forma").submit(function(e){
		e.preventDefault();
		var pass = $("input[name='password']").val();
		var ime =$("input[name='ime']").val();
		var prezime = $("input[name='prezime']").val();
		var greska = false;
		if(!pass){
			$("input[name='password']").css("border-bottom", "2px solid red");
			greska = true;
		}
		
		if(!ime){
			$("input[name='ime']").css("border-bottom", "2px solid red");
			greska = true;
		}
		
		if(!prezime){
			greska = true;
			$("input[name='prezime']").css("border-bottom", "2px solid red");
		}
		if(!greska){			
			$.get({
			url: "/WP_Tickets/rest/korisnici/izmena?password=" + pass + "&ime=" + ime + "&prezime=" +prezime,
			contentType: 'application/json',
			success: function(korisnik){
				$("#forma input[name='password']").attr('disabled', true)
				$("#forma input[name='ime']").attr('disabled', true)
				$("#forma input[name='prezime']").attr('disabled', true)
				$("#forma input[type='submit']").attr('hidden', true)
				$("#forma input[name='password']").css("border-bottom", "2px solid white");
				$("#forma input[name='ime']").css("border-bottom", "2px solid white");
				$("#forma input[name='prezime']").css("border-bottom", "2px solid white");
			}
		})
		}
		
	})
	
	$("#izmena").on('click', function(e){
		e.preventDefault();
		$("#forma input[name='password']").attr('disabled', false)
		$("#forma input[name='ime']").attr('disabled', false)
		$("#forma input[name='prezime']").attr('disabled', false)
		$("#forma input[type='submit']").attr('hidden', false)
		$("#forma input[name='password']").css("border-bottom", "2px solid green");
		$("#forma input[name='ime']").css("border-bottom", "2px solid green");
		$("#forma input[name='prezime']").css("border-bottom", "2px solid green");
	})

	$("#profil-dugme1").click(function(){
		zatvoriSideMeni();
		var val = $("#profil-dugme1").text();

		if(val == "Moje manifestacije"){
			$.get({
				url: "/WP_Tickets/rest/Manifestacije/moje_manifestacije",
				contentType: "application/json",
				success: function(manifestacije){
					skloniManifestacije();
					$("#tabela-karata").empty();
					if(!manifestacije || manifestacije.length == 0){
						let greska = $("<tr><td><p class='error'>Nemate rezervisanu nijednu manifestaciju!</p></td></tr>");
						$("#tabelaManifestacija").append(greska);
					}else{
						napraviTabelu(manifestacije);
					}
				}
			})
		} else if(val == "Registruj prodavca"){
			window.location.href = "../HTML/registration.html";
		} else if(val == "Registruj manifestaciju"){
			window.location.href = "../HTML/addEvent.html";
		}		
	});
	
	$("#profil-dugme2").click(function(){
		zatvoriSideMeni();
		$("#tabela-karata").empty();
		var val = $("#profil-dugme2").text();

		if(val == "Karte"){
			$("#formPretraga").remove();
			$("#tabelaManifestacija").empty()
			$.get({
				url: "/WP_Tickets/rest/Karte/",
				contentType: "application/json",
				success: function(karte){
					if(karte.length == 0)
					{
						let error = $("<tr><td><p class='error'>Nemate jos uvek nijednu kartu!</p></td></tr>")
						$("#tabela-karata").append(error);
					}else{
						dodajPretraguKarti();
						for(let kart of karte)
							dodajRedKarte(kart);
					}
				}
			})
		} else if(val == "Nove manifestacije"){
			$.get({
				url: "/WP_Tickets/rest/Manifestacije/nove",
				contentType: "application/json",
				success: function(manifestacije){
					skloniManifestacije();
					$("#tabela-karata").empty();
					if(!manifestacije || manifestacije.length == 0){
						let greska = $("<tr><td><p class='error'>Nema novih manifestacija!</p></td></tr>");
						$("#tabelaManifestacija").append(greska);
					}else{
						$("PretragaTabele").remove();
						napraviTabelu(manifestacije);
					}
				}
			})
		} else if(val == "Moje manifestacije"){
			$.get({
				url: "/WP_Tickets/rest/Manifestacije/moje_manifestacije",
				contentType: "application/json",
				success: function(manifestacije){
					skloniManifestacije();
					$("#tabela-karata").empty();
					if(!manifestacije || manifestacije.length == 0){
						let greska = $("<tr><td><p class='error'>Jos uvek nemate nijednu manifestaciju!</p></td></tr>");
						$("#tabelaManifestacija").append(greska);
					}else{
						$("PretragaTabele").remove();
						napraviTabelu(manifestacije);
					}
				}
			})
		}		
	});
	
	$("#profil-dugme3").click(function(){
		
		var val = $("#profil-dugme3").text();
		if(val == "Prodate karte" || val == "Svi korisnici")
		{
			zatvoriSideMeni();
		}
		if(val == "Svi korisnici"){
			$("#formPretraga").remove();
			$("#tabelaManifestacija").empty()
			obrisiDivPretrage();
			dodajPretraguKorisnikaAdminu();
			$.get({
				url: "/WP_Tickets/rest/korisnici/",
				contentType: "application/json",
				success: function(korisnici){
					$("#tabela-karata").empty();
					if(!korisnici || korisnici.length == 0){
						alert("Nema nijednog reg korisnika!")
					}else{
						 for(let kor of korisnici)
        					dodajRedKorisnika(kor)
					}
				}
			})
		} else if(val == "Prodate karte"){
			$("#formPretraga").remove();
			$("#tabelaManifestacija").empty()
			$.get({
				url: "/WP_Tickets/rest/Karte/",
				contentType: "application/json",
				success: function(karte){
					$("#tabela-karata").empty();
					if(karte.length == 0)
					{
						let error = $("<tr><td><p class='error'>Nemate jos uvek nijednu prodatu kartu!</p></td></tr>")
						$("#tabela-karata").append(error);
					}else{
						for(let kart of karte)
							dodajRedKarte(kart);
					}
				}
			})
		}
	});
	
	$('#dismiss, .overlay').on('click', function () {
		zatvoriSideMeni();
	});
	
	$("#logout").click(function() {
		$.get({
			url: '/WP_Tickets/rest/korisnici/odjava',
			success: function(){
				window.location.reload();
			}
		})
	});
    
	$('#sidebarCollapse').on('click', function () {
		$('#sidebar').addClass('active');
		$('.overlay').addClass('active');
		$('.collapse.in').toggleClass('in');
		$('a[aria-expanded=true]').attr('aria-expanded', 'false');
		$('#sidebarCollapse').addClass('sakrijDugme');
		$('#sidebarCollapse').hide();


	});
	$('.overlay').on('click',function()
	{
		$('#sidebarCollapse').removeClass('sakrijDugme');	
		$('#sidebarCollapse').show();
	});

	/*Multiselect*/
	$('#TipSelect').on('click',function(e)
	{
		e.preventDefault();
	})
	/*Multiselect Karte*/
	$('#TipSelectKarte').on('click',function(e)
	{
		e.preventDefault();
	})
	/*Multiselect admin*/
	$('#TipSelectKorisniciAdmin').on('click',function(e)
	{
	e.preventDefault();
	})


});


function zatvoriSideMeni()
{
	$('#sidebar').removeClass('active');
	$('.overlay').removeClass('active');
	$('#sidebarCollapse').removeClass('sakrijDugme');	
	$('#sidebarCollapse').show();
}

function postaviPolja(korisnik){
	$("input[name='username']").val(korisnik.username);
	$("input[name='password']").val(korisnik.password);
	$("input[name='ime']").val(korisnik.ime);
	$("input[name='prezime']").val(korisnik.prezime);
	$("input[name='datum-rodjenja']").val(korisnik.datumRodjenja);
	$("input[name='pol']").val(korisnik.pol);
	$("input[name='uloga']").val(korisnik.uloga);
	
	if(korisnik.uloga == "KUPAC"){
		$("#profil-dugme1").text("Moje manifestacije");
		$("#profil-dugme2").text("Karte");
		$("#profil-dugme3").text("Pogodnosti");
		$.get({
				url: "/WP_Tickets/rest/korisnici/tip",
				contentType: "application/json",
				success: function(tip){
					if(tip)
						dodajPodatkeOTipu(tip);
				}
			})
	} else if(korisnik.uloga == "ADMIN"){
		$("#profil-dugme1").text("Registruj prodavca");
		$("#profil-dugme2").text("Nove manifestacije");
		$("#profil-dugme3").text("Svi korisnici");
	}else{
		$("#profil-dugme1").text("Registruj manifestaciju");
		$("#profil-dugme2").text("Moje manifestacije");
		$("#profil-dugme3").text("Prodate karte");
	}
}

function dodajPodatkeOTipu(tip){
	let tabela = $("<table id='pogodnosti-tabela'></table>");
	let tr = $("<tr></tr>");
	let imeTipa = $("<td><img src='../images/"+tip.imeTipa+".png' height='50px'></td>")
	let popust = $("<td>Popust: "+tip.popust+"</td>");
	let bodovi = $("<td>Bodovi: "+tip.brojBodova+"</td>")
	tr.append(imeTipa).append(popust).append(bodovi);
	tabela.append(tr);
	$("#podaci-o-tipu").append(tabela);
}

function dodajRedKorisnika(korisnik){
    let tr;
    if(korisnik.uloga == "PRODAVAC")
        tr = $("<tr class='td-korisnik prodavac' id='"+korisnik.username+"'></tr>");
    else
        tr = $("<tr class='td-korisnik kupac' id='"+korisnik.username+"'></tr>");
    let slika = $("<td><img class='slika-user' src='../images/"+korisnik.pol+".png' height='50px'></td>")
    let username = $("<td><p class='username'>"+korisnik.username+"</p></td>")
    let imePrezime =  $("<td><p class='imepre'>"+korisnik.ime + " " + korisnik.prezime+"</p></td>")
    let rodjen = $("<td><p class='rodjen'>Rodjen:"+ korisnik.datumRodjenja+"</p></td>")
    let uloga =  $("<td><p class='uloga'>"+ korisnik.uloga+"</p></td>")
    let blok = $("<td></td>");
    if(korisnik.blokiran){
		if(korisnik.uloga != "ADMIN")
        	blok =  $("<td><button class='odblok' onclick='blok(\""+korisnik.username+"\")'>Odblokiraj</button></td>")
    } else{
    	if(korisnik.uloga != "ADMIN")
       		 blok =  $("<td><button class='blok' onclick='blok(\""+korisnik.username+"\")'>Blokiraj</button></td>")
    }

    let brisi = $("<td></td>");
    if(korisnik.uloga != "ADMIN")
   		brisi =  $("<td><button class='brisi' onclick='brisi(\""+korisnik.username+"\")'>Obrisi</button></td>")

    
    tr.append(slika);
    tr.append(username);
    tr.append(imePrezime)
    tr.append(rodjen);
    tr.append(uloga);
    tr.append(blok);
    tr.append(brisi);
    $("#tabela-karata").append(tr);
}

function blok(username){
   $.get({
        url: "/WP_Tickets/rest/korisnici/blokiranje?username=" + username,
        contentType: "application/json",
        success: function(){

            alert("uspesno smo blokirali korisnika")
        }
   })
}

function brisi(username){
    $.get({
        url: "/WP_Tickets/rest/korisnici/brisanje?username=" + username,
        contentType: "application/json",
        success: function(){

            alert("uspesno smo obrisali korisnika")
        }
   })
}

function dodajPretraguKarti()
{
	
	let forma = $("<form id='formTicketsPretraga'></form>");
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
	let dugmePretragaKarte = $('<button id="dugmePretragaKarte" class="btn btn-outline-secondary" type="submit" ><i class="fas fa-search"></i></button>');


	let divFilterPretraga = $('<div id="filterPretragaKarte" class="dropdown show"></div');
	let dugmeFilterGornje = $('<button  class="btn btn-outline-secondary" type="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false"><i class="fas fa-filter"></i></button>')
	let divDropdown = $('<div class="dropdown-menu" aria-labelledby="dropdownMenuLink"></div');
	let multiselect = $('<select id="TipSelectKarte"  class="multiselect-ui form-control" multiple="multiple"></select>')
	let vip = $("<option value='vip'>VIP</option>");
	let regular = $("<option value='vip'>VIP</option>");
	let fanpit = $("<option value='fanpit'>Fan Pit</option>");
	let rezervisana = $("<option value='rezervisana'>Rezervisane</option>");
	let odustanak = $("<option value='odustanak'>Otkazane</option>");
	multiselect.append(vip).append(regular).append(fanpit).append(rezervisana).append(odustanak);
	let dugmeFilter = $('<button id="filterButtonKarte" class="btn btn-outline-secondary btn-dark"><i class="fas fa-filter"></i>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Filter</button>');
	-

	divDropdown.append(multiselect);
	divDropdown.append(dugmeFilter);
	divFilterPretraga.append(divDropdown);
	divFilterPretraga.append(dugmeFilterGornje);
	//sort

	let divSortPretraga = $('<div id="filterPretragaKarte" class="dropleft show"></div');
	let dugmeSortGornje = $('<button id="sortPretragaKarte" class="btn btn-outline-secondary" type="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false"><i class="fas fa-sort-amount-down"></i></button>')
	let divdropdown2 = $('<div class="dropdown-menu" aria-labelledby="dropdownMenuLink"></div>');
	let sort11 = $('<a id="sort1Karte"class="dropdown-item" href="#">Ceni Rastuće</a>');
	let sort21 = $('<a id="sort2Karte"class="dropdown-item" href="#">Ceni Opadajuće</a>');
	let sort31 = $('<a id="sort3Karte"class="dropdown-item" href="#">Datumu Rastuće</a>');
	let sort41 = $('<a id="sort4Karte"class="dropdown-item" href="#">Datumu Opadajuće</a>');
	let sort51 = $('<a id="sort5Karte"class="dropdown-item" href="#">Naziv Rastuće</a>');
	let sort61 = $('<a id="sort6Karte"class="dropdown-item" href="#">Naziv Opadajuće</a>');
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
function obrisiDivPretrage()
{
	$("#PretragaTabele").empty();
}

