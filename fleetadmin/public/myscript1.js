function getOrders() {
    let key = document.getElementById('inputFIOHelp').innerText;
    console.log(key);

    let start = document.getElementById('inputDataStart').value;
    console.log(start);

    let end = document.getElementById('inputDataEnd').value;
    let orders = document.getElementById('ordersBody');
    let mDistanceSummary = document.getElementById('distanceSummary');
    let mFuelSummary = document.getElementById('fuelSummary');
    console.log(end);
     $.ajax({
        url: '/userorders',
        data:{ id: key, start: start, end: end},
        success: function(data){
            orders.innerText = '';

            console.log(data);
            let distanceSummary = 0.0, fuelSummary = 0.0;
            let index = 0;
            let lines =[];
            data.forEach(function (row) {
                index++;

                //         v--- use a jQuery object here
                let $row = $('<tr>' +
                    '<td scope="col">' + index + '</td>' +
                    "<td scope='col'>" + row.accept_timestamp + '</td>' +
                    "<td scope='col'>" + row.from + '<br>' + row.to + '</td>' +
                    "<td scope='col'>" + row.customer_fio + '</td>' +
                    "<td scope='col'>" + row.distanceDisplay + '</td>' +
                    "<td scope='col'>" + row.spent_time + '</td>' +
                    "<td scope='col'>" + row.fuelDisplay + '</td>' +
                    '</tr>');

                distanceSummary += row.distance / 1000.00;
                fuelSummary += row.fuel;

                $('table> tbody:last').append($row);

            });

            mDistanceSummary.innerText = distanceSummary.toFixed(2) + ' км.'
            mFuelSummary.innerText = fuelSummary.toFixed(2) + ' л.';

        }
    });
}

function clickMePls() {
    let collapse1 = document.getElementById('collapse');
    let collapse2 = document.getElementById('collapse2');
    collapse1.hidden = !collapse1.hidden;
    collapse2.hidden = !collapse2.hidden;

}