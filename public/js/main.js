window.QL = window.QL || {};

QL.getPublicKey = function (success) {
    $.ajax({
        "type": "GET",
        "url": "/api/crypto/publickey",
        "success": success,
        "error": function (err) {
            console.log(err)
        }
    });
};

QL.generateLicense = function (params, success) {
    $.ajax({
        "type": "POST",
        "url": "/api/crypto/license",
        "contentType": "application/json",
        "success": success,
        "data": JSON.stringify(params),
        "error": function (err) {
            console.log(err)
        }
    });  
};

QL.verifyLicense = function (params, success) {
    $.ajax({
        "type": "POST",
        "url": "/api/crypto/license/verify",
        "contentType": "application/json",
        "success": success,
        "data": JSON.stringify(params),
        "error": function (err) {
            console.log(err)
        }
    });  
};

QL.view = {
    publickey: function (data) {
        $(".viewPublicKey").text(data.status ? data.publickey : data.msg).removeClass("display-none");
    }
};

$(document).ready(function () {
    console.log("main loaded");
    $(".getPublicKey").on("click", function () {
        QL.getPublicKey(function (data) {
            QL.view.publickey(data);
        });
    });
    $(".generateLicense").on("click", function () {
        QL.generateLicense({
            "users": 5,
            "days": 30,
            "apps": 20,
            "name": "user 1",
            "startDate": new Date().getTime()
        }, function (data) {
            console.log(data);
        });
    });
    $(".verifyLicense").on("click", function () {
        QL.verifyLicense({
            "license": "0SJamUioBsWQc9wJUT92Sut6fTJnqSk2PxrtnuQ3YHr6wFsQMWp3wV6EzsiPFMw0Zho4qBExN1m0GecODmBg3GWxkYL6PJSMqcD6efPhnE4OrR1+8xzh+OYTiWf54p74gO8Nt/hn2Ap9ndwkOJyDa9zQIjpEv08vS1RqsFFb5d/u48IogTj+GiCCt/qI8j4Vd+qv0/a+yg900gbKO/cxWP7ym7aU7Fr7WnqDyIFfzHTqwPPx2AqR35QtZFMXsYbzOV9SRXpGTlwgwM5yb79mfcf1Xr8WwdIis9xwX/MfI2AhojaI5cGPw+AiGkQnGZ4QC26ykFwfQGTRgchzh+ldiA==",
            "msg": JSON.stringify({"users":5,"days":30,"apps":20,"name":"user 1","startDate":1476624758802})
        }, function (data) {
            console.log(data);
        });
    });
});
