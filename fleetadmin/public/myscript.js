function clicked() {
    $.ajax({
        url: '/userorders',
        success: function(data){
            alert('DATA =>' + data);
        }
    });
}