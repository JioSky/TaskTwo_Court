$(document).ready(function() {

  $('#Table').on('click', 'a', function(e) {
    e.preventDefault();

    var url = $(this).attr('href');
    var data = { url: url };

    $.ajax({
      url: '/parse',
      data: data,
      success: function(response) {
        var newTab = window.open("", "", "width=800,height=600");
        newTab.document.write(response);
              }
        });
    });
  });
