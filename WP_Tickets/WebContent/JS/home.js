
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
		$('.nav-item').style.display = "none";
		


	});

	$('#dismiss').on('click', function () {
		$('#sidebarCollapse').removeClass('sakrijDugme');	
		$('#sidebarCollapse').style.display = "block";
	
	});





});