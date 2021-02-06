$(document).ready(function(){
	$.get({
		url: "/WP_Tickets/rest/Manifestacije/pregled",
		contentType: "application/json",
		success: function(manifestacija){
			if(manifestacija && !manifestacija.obrisana){
				$.get({
					url : "/WP_Tickets/rest/korisnici/trenutni",
					contentType: "application/json",
					success: function(user){
					if(!user){
						tipKorisnika = "NEREGISTROVANI";
						dodajOpcijeZaKorisnika(manifestacija);
					}else{
						tipKorisnika= user.uloga
						dodajOpcijeZaKorisnika(manifestacija);
					}
					
					$.get({
						url: "/WP_Tickets/rest/Manifestacije/Tipovi",
						contentType: "application/json",
						success: function(tips){
							tipovi = tips
							$("#tip").append(getTip(manifestacija.tip));
							if(manifestacija.aktivno == true)
								$("#status").append("Aktivna");
							else
								$("#status").append("Neaktivna");
						}
					})
				}
			})


				podesiMarker([manifestacija.lokacija.geoDuzina, manifestacija.lokacija.geoSirina])
				$("#img-postera").attr("src", manifestacija.poster)
				$("#naziv").text(manifestacija.naziv);
				$("#brojMesta").text("Broj slobodnih mesta: "+ manifestacija.brojPreostalihMesta+" od " + manifestacija.brojMesta);
				$("#datum-vreme").text(getDatumVreme(manifestacija.datumVremeOdrzavanja))
				$("#cena").text(manifestacija.cenaREGkarte + "din");
				$("#lokacija").append(manifestacija.lokacija.adresa);

				dodajKomentare(manifestacija.id);

			}
		}
	})
});

var tipKorisnika;
var tipovi = [];

function podesiMarker(koordinate){
	 var map = new ol.Map({
        view: new ol.View({center: ol.proj.transform(koordinate, 'EPSG:4326', 'EPSG:3857'), zoom:14}),
        layers: [new ol.layer.Tile({
                source: new ol.source.OSM()
        })],
        target:'map'
    });

	var marker = new ol.Feature({
        geometry: new ol.geom.Point(ol.proj.transform(koordinate, 'EPSG:4326', 'EPSG:3857')),
    });
    
    var markers = new ol.source.Vector({
        features: [marker]
    });

    var markerVectorLayer = new ol.layer.Vector({
        source: markers,
    });
    map.addLayer(markerVectorLayer);
}

function getTip(tipM){
	for(tip of tipovi){
		if(tip.id == tipM)
			return tip.nazivTipa;
	}
}

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


function getDatumVreme(datumvreme){
    var date = datumvreme.split("T")[0];
    var time = datumvreme.split("T")[1];

    return date.split("-")[2] + ". "+meseci[date.split("-")[1]] + " " + date.split("-")[0] + ". u " + time.split(":")[0] +":"+time.split(":")[1];
}
var popust = -1;
function dodeliPopust(pop){
	popust = pop;
}

function dodajOpcijeZaKorisnika(manifestacija){
	if(tipKorisnika == "NEREGISTROVANI"){
		let button = $("<button id='prijava-btn' onclick='prijava()'>Prijavite se za dodatne opcije</button>")
		$("#opcije").append(button);
	}else if(tipKorisnika == "ADMIN"){
		if(!manifestacija.aktivno){
			let button = $("<button id='brisanje-btn' onclick='aktiviraj("+manifestacija.id+")'>Aktivacija</button>")
			let button2 = $("<button id='brisanje-btn' onclick='obrisi("+manifestacija.id+")'>Brisanje</button>")
			$("#opcije").append(button).append(button2);	
		}else{
			let button2 = $("<button id='brisanje-btn' onclick='obrisi("+manifestacija.id+")'>Brisanje</button>")
			$("#opcije").append(button2);	
		}
	} else if(tipKorisnika == "PRODAVAC"){
		if(!manifestacija.aktivno){
			let button = $("<button id='izmena-btn' onclick='izmeni("+manifestacija.id+")'>Izmena</button>")
			$("#opcije").append(button);
		}
	} else if(tipKorisnika == "KUPAC"){
		$.get({
				url: "/WP_Tickets/rest/korisnici/tip",
				contentType: "application/json",
				success: function(type){
					let cena = $("<span id='cena'>Cena: "+manifestacija.cenaREGkarte * (100-type.popust)/100+"  dinara</span>");
					let select = $("<select name='tipKarte' onchange='promeniCenu("+manifestacija.cenaREGkarte+")'><option value='1'>Regular</option><option value='2'>FAN_PIT</option><option value='0'>Vip</option></select>")
					let button = $("<button id='kupi-btn' onclick='kupi("+manifestacija.brojMesta+","+manifestacija.id+")'>Kupi</button>")
					let kolicina = $("<input type='number' name='kolicina' min='0' value='0' onchange='promeniCenu("+manifestacija.cenaREGkarte+")'>")
					let popust = $("<span id='popust'>Popust: "+type.popust+" %</span>");
					$("#opcije").append(cena).append(popust).append(button).append(select).append(kolicina);
				}
			})
		
	}
}

function prijava(){
	window.location.href ="../HTML/login.html";
}

function aktiviraj(manId){
	$.post({
		url: "/WP_Tickets/rest/Manifestacije/aktivacija/"+manId,
		success: function(){
			$("#status").html("<i class='fas fa-calendar-check' id='logo-stat'></i>Aktivna");
			$("#opcije").html("")
		}
	})
}

function izmeni(id){
	$.post({
		url: "/WP_Tickets/rest/Manifestacije/izmena/"+id,
		success: function(){
			window.location.href = "../HTML/addEvent.html";
		}
	})
}

function obrisi(id){
	$.post({
		url: "/WP_Tickets/rest/Manifestacije/brisanje/"+id,
		success: function(){
			window.location.href = '../HTML/home.html'
		}
	})
}

function kupi(brojMesta, idMan){
	if(!$("input[name='kolicina']").val())
		{
			$("input[name='kolicina']").css("border", "2px solid red");
			return;
		}
	if(parseInt($("input[name='kolicina']").val()) <=0 || parseInt($("input[name='kolicina']").val()) > brojMesta){
		$("input[name='kolicina']").css("border", "2px solid red");
			return;
	}
	
	var kol = parseInt($("input[name='kolicina']").val());
	var tip = $("select[name='tipKarte'] option:selected").val();
	
	$.get({
		url: "/WP_Tickets/rest/Karte/kupi/"+idMan +"/"+kol+"/"+tip,
		contentType: "application/json",
		success: function(ok){
			if(ok){
				alert("Potvrdjeno!")
			}else{
				alert("Neka greska!")
			}
		}
	})
}	

function promeniCenu(cena){
	var popust = $("#popust").text();
	var kol = $("input[name='kolicina']").val();
	if(kol){
		kol = parseInt(kol);
		if(kol == 0)
			kol = 1;
	}else{
		kol=1;
	}
	var broj = popust.split(" ")[1];
	if($("select[name='tipKarte'] option:selected").val() == "1"){
		
		$("#cena").text("Cena: " + cena*kol*(100-broj)/100 + "  dinara");
	}else if($("select[name='tipKarte'] option:selected").val() == "2")
	{
	
		$("#cena").text("Cena: " + cena*kol*2*(100-broj)/100 + " dinara");
	}else{
		
		$("#cena").text("Cena: " + cena*kol*4*(100-broj)/100 + " dinara");
	}
}

function dodajKomentare(id){
	$.get({
		url: "/WP_Tickets/rest/komentari/provera",
		success: function(broj){
			if(broj == -2){
				
			}else if(broj == -1){
				$.get({
					url: "/WP_Tickets/rest/komentari/",
					contentType: "application/json",
					success: function(comments){
						if(comments.length == 0){
							let tr = $("<tr><td><label id='poruka'>Nema komentara</label></td></tr>");
							$("#tabela-komentara").append(tr);
						}else{
							for(kom of comments){
								dodajRedKomentara(kom);
							}
						}

						dodajOcenu(id);
					}
				})
			}else if(broj == 0){
				/** kupac, moze da dodaje komentare */
				$.get({
					url: "/WP_Tickets/rest/komentari/",
					contentType: "application/json",
					success: function(comments){
						let tr = $("<tr><td colspan='3'><button id='dodajKom' onclick='dodajNoviKomentar("+id+")' style='margin:0'>Dodaj</button><input type='text' name='tekst-komentara' placeholder='Komentar' /><i class='fas fa-star'></i><input type='number' id='inputOcene' min='0' max='5' step='1' id='input-ocena' onchange='proveriOcenu(this)'></td></tr>")
						$("#tabela-komentara").append(tr);
						for(kom of comments){
							dodajRedKomentara(kom);
						}

						dodajOcenu(id);
					}
				})
				
			}else if(broj == 1){

				$.get({
					url: "/WP_Tickets/rest/komentari/",
					contentType: "application/json",
					success: function(comments){
						for(kom of comments){
							dodajRedKomentaraProdavac(kom);
						}

						dodajOcenu(id);
					}
				})
				
			}else if(broj == 2){
				$.get({
					url: "/WP_Tickets/rest/komentari/",
					contentType: "application/json",
					success: function(comments){
						for(kom of comments){
							dodajRedKomentaraAdmin(kom);
						}

						dodajOcenu(id);
					}
				})
			}
		}
	})
}

function proveriOcenu(input){
	if(input.value == ""){
		input.value = 0;
	}else{
		var o = parseInt(input.value);
		if(o > 5)
			input.value=5;
		if(o < 0)
			input.value=0;
	}
}

function dodajRedKomentara(komentar){
	let tr = $("<tr id='"+komentar.id+"'><td class='td-user'><i class='fas fa-user'></i>"+komentar.usernameKupca+"</td><td class='td-tekst'>"+komentar.tekstKomentara+"</td><td class='td-ocena'><i class='fas fa-star'></i>"+komentar.ocena+"</td></tr>")
	$("#tabela-komentara").append(tr);
}

function dodajRedKomentaraProdavac(komentar){
	let tr;
	if(!komentar.odobren){
		tr  = $("<tr id='"+komentar.id+"'><td><i class='fas fa-user'></i>"+komentar.usernameKupca+"</td><td>"+komentar.tekstKomentara+"</td><td>Ocena: "+komentar.ocena+"</td><td><button class='odobri-kom' onclick='odobriKom("+komentar.id+")'>Odobri</button><button class='odbij-kom' onclick='odbijKom("+komentar.id+")'>Odbij</button></td></tr>")
	}
	else{
		tr = $("<tr id='"+komentar.id+"'><td><i class='fas fa-user'></i>"+komentar.usernameKupca+"</td><td>"+komentar.tekstKomentara+"</td><td>Ocena: "+komentar.ocena+"</td><td></td></tr>")
	}
	$("#tabela-komentara").append(tr);
}

function dodajRedKomentaraAdmin(komentar){
	let tr;
	if(!komentar.odobren){
		tr  = $("<tr id='"+komentar.id+"'><td><i class='fas fa-user'></i>"+komentar.usernameKupca+"</td><td>"+komentar.tekstKomentara+"</td><td>Ocena: "+komentar.ocena+"</td><td><i class=\"far fa-question-circle\"></i><span id='status-kom'>NEODOBREN</span></td></tr>")
	}
	else{
		tr = $("<tr id='"+komentar.id+"'><td><i class='fas fa-user'></i>"+komentar.usernameKupca+"</td><td>"+komentar.tekstKomentara+"</td><td>Ocena: "+komentar.ocena+"</td><td><i class=\"fas fa-thumbs-up\"></i><span id='status-kom'>ODOBREN</span></td></tr>")
	}
	$("#tabela-komentara").append(tr);
}

function odobriKom(id){
	$.post({
		url: "/WP_Tickets/rest/komentari/odobri/"+id,
		success: function(){
			$("#" + kom.id).find('td:last-child').html("");
		}
	})
}

function odbijKom(id){
	$.post({
		url: "/WP_Tickets/rest/komentari/odbij/"+id,
		success: function(){
			$("#" + id).remove();
		}
	})
}

function dodajOcenu(id){
	$.get({
		url: "/WP_Tickets/rest/komentari/ocena/" +id,
		success: function(ocena){
			$("#opcije").html("");

			let o = $("<span id='prosecna-ocena'>Prosecna ocena: <i class='fas fa-star'></i> "+ocena+"</span>")
			$("#opcije").append(o)
		}
	})
}

function dodajNoviKomentar(id){
	var tekstKom = $("input[name='tekst-komentara']").val();
	var oc = parseInt($("input[type='number']").val());
	$.post({
		url: "/WP_Tickets/rest/komentari/dodaj",
		contentType: "application/json",
		data: JSON.stringify({"id": -1, "usernameKupca": "", "manifestacija":id, "tekstKomentara": tekstKom, "ocena": oc, "odobren": false, "obrisan": false}),
		success: function(ok){
			if(ok =='true')
				alert("Vas komentar je registrovan!");
			else{
				alert("Neka greska!")
			}
		}
	})
}