window.QL = window.QL || {};

QL.getPublicKey = function (success) {
    $.ajax({
        "type": "GET",
        "url": "/api/crypto/publickey",
        "data": {},
        "success": success,
        "error": function (err) {
            console.log(err)
        }
    });
}

QL.view = {
    publickey: function (data) {
        $(".viewPublicKey").text(data).removeClass("display-none");
    }
};

$(document).ready(function () {
    console.log("main loaded");
    $(".getPublicKey").on("click", function () {
        QL.getPublicKey(function (data) {
            console.log(data);
            QL.view.publickey(data);
        });
    });
});
