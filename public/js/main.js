window.QL = window.QL || {};

QL.getPublicKey = function () {
    $.ajax({
        "type": "GET",
        "url": "/api/crypto/publickeyder",
        "data": {},
        "success": function (data) {
            console.log(data);
            QL.view.publickey(data);
        },
        "error": function (err) {
            console.log(err)
        }
    });
}

QL.view = {
    publickey: function () {

    }
}

$(document).ready(function () {
    console.log("main loaded");
    QL.getPublicKey();
});
