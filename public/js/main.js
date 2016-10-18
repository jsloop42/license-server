window.QL = window.QL || {};

QL.getPublicKey = function (success) {
    $.ajax({
        "type": "GET",
        "url": "/api/crypto/publickey",
        "success": success,
        "error": QL.view.error
    });
};

QL.generateLicense = function (params, success) {
    $.ajax({
        "type": "POST",
        "url": "/api/crypto/license",
        "contentType": "application/json",
        "success": success,
        "data": params,
        "error": QL.view.error
    });  
};

QL.verifyLicense = function (params, success) {
    $.ajax({
        "type": "POST",
        "url": "/api/crypto/license/verify",
        "contentType": "application/json",
        "success": success,
        "data": JSON.stringify(params),
        "error": QL.view.error
    });  
};

QL.view = {
    publickey: function (data) {
        $(".field").addClass("display-none");
        QL.view.clearValid();
        $(".viewPublicKey").text(data.status ? data.publickey : data.msg).removeClass("display-none");
    },
    generateLicense: function () {
        $(".field").addClass("display-none");
        QL.view.clearValid();
        $(".viewGenerateLicense").removeClass("display-none");        
    },
    license: function (data) {
        $(".field").addClass("display-none");
        $(".license").val(data.license);
        $(".msg").val(data.msg);
        $(".viewLicense").removeClass("display-none");
    },
    licenseError: function (msg) {
        $(".licenseError").text(msg).removeClass("display-none");
        setTimeout(function () {
            $(".licenseError").text("").addClass("display-none");
        }, 4000);
    },
    validate: function (data) {
        if (data.valid) {
            $(".validateMsg").text("License is valid").removeClass("display-none invalid").addClass("valid");
        } else {
            $(".validateMsg").text("License is invalid").removeClass("display-none valid").addClass("invalid");
        }
    },
    clearValid: function () {
        $(".validateMsg").text("").addClass("display-none").removeClass("valid invalid");
    },
    error: function (err) {
        QL.view.clearValid();
        $(".error").html(err.statusText + "<br />" + err.responseText + "<br />Please enter a valid signature.")
                   .removeClass("display-none");
        setTimeout(function () {
            $(".error").html("").addClass("display-none");
        }, 4000);
    }
};

$(document).ready(function () {
    $(".getPublicKey").on("click", function () {
        QL.getPublicKey(function (data) {
            QL.view.publickey(data);
        });
    });
    $(".generateLicense").on("click", function () {
        QL.view.generateLicense();
        $(".genLicenseSubmit").on("click", function () {
            var msg = $(".licenseMsg").val();
            QL.generateLicense(msg, function (data) {
                QL.view.license(data); 
            });
        });
    });
    $(".validate").on("click", function () {
        var licenseSig = $(".license").val(),
            msg = $(".msg").val();
        QL.verifyLicense({
            "license": licenseSig,
            "msg": msg
        }, function (data) {
            QL.view.validate(data);
        })
    });
});
