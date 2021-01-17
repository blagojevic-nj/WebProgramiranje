$(document).ready(function(){
    $("#registration-forma").submit(function(e){
        e.preventDefault();
        var username = $("input[name='username']").val();
        var password = $("input[name='password']").val();
        var ime = $("input[name='ime']").val();
        var prezime = $("input[name='prezime']").val();
        var pol = $("select[name='pol'] option:selected").val();
        var datumRodjenja = $("input[name='datum-rodjenja']").val();
       
        var greska = false;

        if(!username){
            greska = true;
            $("input[name='username']").css("background-color", "red");
        }

        if(!password){
            greska = true;
            $("input[name='password']").css("background-color", "red");
        }

        if(!ime){
            greska = true;
            $("input[name='ime']").css("background-color", "red");
        }

        if(!prezime){
            greska = true;
            $("input[name='prezime']").css("background-color", "red");
        }

        if(!datumRodjenja){
            greska = true;
            $("input[name='datum-rodjenja']").css("background-color", "red");
        }
            
        if(!greska){
            $.post({
			url: "/WP_Tickets/rest/korisnici/registracija",
			contentType: 'application/json',
			data: JSON.stringify({"username": username, "password":password, "ime": ime, "prezime":prezime, "pol":pol, "datumRodjenja":datumRodjenja, "uloga":"KUPAC", "obrisan": false}),
			success: function(korisnik){
				if(korisnik == null){
					$("#error").show();
				}else{
					var value = '@Request.RequestContext.HttpContext.Session["korisnik"]';
					alert(korisnik.username);
					window.location.href = "../HTML/home.html";
				}
			}
		})
        }
    });
});