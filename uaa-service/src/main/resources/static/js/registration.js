$.getScript('js/zxcvbn/zxcvbn.js', function () {
    //password strength estimation script is loaded
});

$(document).ready(function () {

    // Smart Wizard Configuration
    $('#smartwizard').smartWizard({
        selected: 0,  // Initial selected step, 0 = first step
        keyNavigation: true, // Enable/Disable keyboard navigation(left and right keys are used if enabled)
        autoAdjustHeight: true, // Automatically adjust content height
        cycleSteps: false, // Allows to cycle the navigation of steps
        backButtonSupport: true, // Enable the back button support
        useURLhash: true, // Enable selection of the step based on url hash
        enableAllSteps: false, // Enable/Disable all steps on first load
        lang: {  // Language variables
            next: 'Next',
            previous: 'Previous'
        },
        toolbarSettings: {
            toolbarPosition: 'bottom', // none, top, bottom, both
            toolbarButtonPosition: 'right', // left, right
            showNextButton: true, // show/hide a Next button
            showPreviousButton: true, // show/hide a Previous button
            toolbarExtraButtons: [
                $('<button type="submit"></button>').text('Registration')
                    .addClass('btn btn-info')
                    .on('click', function () {
                        if (!$(this).hasClass('disabled')) {
                            var elmForm = $("#myForm");
                            if (elmForm) {
                                elmForm.validator('validate');
                                var elmErr = elmForm.find('.has-error');
                                if (elmErr && elmErr.length > 0) {
                                     return false;
                                } else {
                                     elmForm.submit();
                                    return false;
                                }
                            }
                        }
                    }),
                $('<button></button>').text('Reset')
                    .addClass('btn btn-danger')
                    .on('click', function () {
                        $('#smartwizard').smartWizard("reset");
                        $('#myForm').find("input, textarea").val("");
                    })
            ]
        },
        anchorSettings: {
            anchorClickable: true, // Enable/Disable anchor navigation
            enableAllAnchors: false, // Activates all anchors clickable all times
            markDoneStep: true, // add done css
            enableAnchorOnDoneStep: true // Enable/Disable the done steps navigation
        },
        contentURL: null, // content url, Enables Ajax content loading. can set as data data-content-url on anchor
        disabledSteps: [],    // Array Steps disabled
        errorSteps: [],    // Highlight step with errors
        theme: 'arrows', //dots
        transitionEffect: 'fade', // Effect on navigation, none/slide/fade
        transitionSpeed: '400'
    });
});


// Initialize the leaveStep event
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

        // Password Validation on step 1 check again the password
        if (stepNumber == 1) {
            var score = $('.foo').checkPasswordScore();
            if (score < 3) {
                return false;
            }
        }
    }
    return true;
});

// Initialize the showStep event
$("#smartwizard").on("showStep", function (e, anchorObject, stepNumber, stepDirection) {
    if ($('button.sw-btn-next').hasClass('disabled')) {
        $('.sw-btn-group-extra').show(); // show the button extra only in the last page
    } else {
        $('.sw-btn-group-extra').hide();
    }
});

// Initialize the beginReset event
$("#smartwizard").on("beginReset", function (e) {
    // return confirm("Do you want to reset the wizard?");
});

// Initialize the endReset event
$("#smartwizard").on("endReset", function (e) {
    // alert("endReset called");
});

$.fn.checkPasswordScore = function () {
    if ($("#inputPassword")) { // check if there a inputPassword
        var password = $("#inputPassword").val();

        var result = zxcvbn(password),
            score = result.score,
            calc_time = result.calc_time,
            suggestions = result.feedback.suggestions,
            message = result.feedback.warning || 'The password is weak';

        var msg = result.feedback.warning + "it took  " + calc_time + "ms to calculate an answer,  " + suggestions

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


$(":checkbox").on("change", function () { // Here listening the changes on checkbox using on()
    // terms checkbox
    if ($("#terms").is(":checked") == true) { // check if terms checkbox is activated for remive finish disabled class
        $('#btnFinish').removeClass('disabled');
    } else {
        $('#btnFinish').addClass('disabled');
    }

});