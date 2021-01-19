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
	
	$("#profil-dugme3").click( function(e){
		e.preventDefault();
		
		if($("#profil-dugme3").text() == "Pogodnosti"){
			$.get({
				url: "/WP_Tickets/rest/korisnici/tip",
				contentType: "application/json",
				success: function(tip){
					if(tip)
						dodajPodatkeOTipu(tip);
				}
			})
		}
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

	/*Multiselect*/
	$('#TipSelect').on('click',function(e)
	{
	e.stopPropagation();
	})


});


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
	} else if(korisnik.uloga == "ADMIN"){
		$("#profil-dugme1").text("Registruj prodavca");
		$("#profil-dugme2").text("Aktivne manifestacije");
		$("#profil-dugme3").text("Svi korisnici");
	}else{
		$("#profil-dugme1").text("Registuj manifestaciju");
		$("#profil-dugme2").text("Moje manifestacije");
		$("#profil-dugme3").text("Prodate karte");
	}
}

function dodajPodatkeOTipu(tip){
	$("#podaci-o-tipu").text(tip.imeTipa + "  " + tip.popust + "  " + tip.brojBodova)
}


