$.getScript('js/zxcvbn/zxcvbn.js', function () {
    //script is loaded and executed put your dependent JS here
});

$(document).ready(function () {

    // Toolbar extra buttons    onClick="if(this.className.indexOf('disabled') == -1){btnFinish(this);}"
    var btnFinish = $('<button id="btnFinish"> ></button>')
        .text('Finish')
        .addClass('btn btn-info disabled')

        .on('click', function () {
            if (!$(this).hasClass('disabled')) {
                var elmForm = $("#myForm");
                if (elmForm) {
                    elmForm.validator('validate');
                    var elmErr = elmForm.find('.has-error');
                    if (elmErr && elmErr.length > 0) {
                        // alert('Oops we still have error in the form');
                        return false;
                    } else {
                        // alert('Great! we are ready to submit form');
                        elmForm.submit();
                        return false;
                    }
                }
            }
        });
    var btnCancel = $('<button></button>').text('Cancel')
        .addClass('btn btn-danger')
        .on('click', function () {
            $('#smartwizard').smartWizard("reset");
            $('#myForm').find("input, textarea").val("");
        });


    // Smart Wizard
    $('#smartwizard').smartWizard({
        selected: 0,
        theme: 'dots',
        transitionEffect: 'fade',
        toolbarSettings: {
            toolbarPosition: 'bottom',
            toolbarExtraButtons: [
                btnFinish,
                btnCancel]
        },
        anchorSettings: {
            markDoneStep: true, // add done css
            markAllPreviousStepsAsDone: true, // When a step selected by url hash, all previous steps are marked done
            removeDoneStepOnNavigateBack: true, // While navigate back done step after active step will be cleared
            enableAnchorOnDoneStep: true // Enable/Disable the done steps navigation
        }
    });

    $("#smartwizard").on("leaveStep", function (e, anchorObject, stepNumber, stepDirection) {
        var elmForm = $("#form-step-" + stepNumber);
        // stepDirection === 'forward' :- this condition allows to do the form validation
        // only on forward navigation, that makes easy navigation on backwards still do the validation when going next
        if (stepDirection === 'forward' && elmForm) {
            elmForm.validator('validate');
            var elmErr = elmForm.children('.has-error');

            if (elmErr && elmErr.length > 0) {
                // Form validation failed
                return false;
            }

            // Password Validation on step 3
            if (stepNumber == 3) {
                var score = $('.foo').checkPasswordScore();
                if(score < 3){
                    return false;
                }
            }

        }
        return true;
    });

    $("#smartwizard").on("showStep", function (e, anchorObject, stepNumber, stepDirection) {
        $('.btnFinish').addClass('disabled');
        // Enable finish button only on last step
        if (stepNumber == 5) {
            $('#btnFinish').removeClass('disabled');
        }
    });
});

$.fn.checkPasswordScore = function () {
    if ($("#inputPassword")) { // check if there a inputPassword
        var password = $("#inputPassword").val();

        var result = zxcvbn(password),
            score = result.score,
            calc_time = result.calc_time,
            suggestions = result.feedback.suggestions,
            message = result.feedback.warning || 'The password is weak';

        var msg = result.feedback.warning  + "it took  " + calc_time + "ms to calculate an answer,  " + suggestions

        // We will treat the password as an invalid one if the score is less than 3
        if (score < 3) {
            $('#passwordAlert').text("The password is weak. " + msg);
            $('#passwordAlert').addClass("alert-danger")
        } else {
            $('#passwordAlert').text("The password is strong. " + msg);
            $('#passwordAlert').removeClass("alert-danger")
            $('#passwordAlert').addClass('alert-success')
        }
        return score
    }
};
$("#inputPassword").keyup(function () {
    $('.foo').checkPasswordScore()
});


var $checkBoxes = $(":checkbox").on("change", function () { // Here listening the changes on checkbox using on()
    // $checkBoxes.removeClass("change").attr("checked", false); // remove the class from existing Checkbox
    // $(this).addClass("change").attr("checked", true); // adding the class for the currenly checked checkbox

    if ($("#terms").is(":checked") == true) { // check if terms checkbox is activated for remive finish disabled class
        $('#btnFinish').removeClass('disabled');
    } else {
        $('#btnFinish').addClass('disabled');
    }

});