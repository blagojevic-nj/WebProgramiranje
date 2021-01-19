$.noConflict()
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
								$("#content").remove();
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
	
	$('#dismiss, .overlay').on('click', function () {
		$('#sidebar').removeClass('active');
		$('.overlay').removeClass('active');
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
/*Dodaj/ukloni meni dugme... */
	$('#dismiss').on('click', function () {
		$('#sidebarCollapse').removeClass('sakrijDugme');	
		$('#sidebarCollapse').show();

	});

	$('.overlay').on('click',function()
	{
		$('#sidebarCollapse').removeClass('sakrijDugme');	
		$('#sidebarCollapse').show();
	});
/**Ucitavanje manifestacija */
	$.get("/WP_Tickets/rest/Manifestacije/",function(manifestacije)
		{
			napraviTabelu(manifestacije)
		}
	);

	/*Multiselect*/
	$('#TipSelect').on('click',function(e)
	{
	e.stopPropagation();
	})


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


function postaviPolja(korisnik){
	$("input[name='username']").val(korisnik.username);
	$("input[name='password']").val(korisnik.password);
	$("input[name='ime']").val(korisnik.ime);
	$("input[name='prezime']").val(korisnik.prezime);
	$("input[name='datum-rodjenja']").val(korisnik.datumRodjenja);
	$("input[name='pol']").val(korisnik.pol);
	$("input[name='uloga']").val(korisnik.uloga);
}


