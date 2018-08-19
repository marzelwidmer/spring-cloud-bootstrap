
$(document).ready(function () {

    $.fn.foo = function () {
        if($.urlParam('token')){
            console.log("Token available enable only Acivate Account step.");
            return [0,1,2,3]
        }else {
            console.log("No token available disable Activate Account step.");
            return [4]
        }

    };

    // Smart Wizard Configuration
    $('#smartwizard').smartWizard({
        selected: 0,  // Initial selected step, 0 = first step
        keyNavigation: true, // Enable/Disable keyboard navigation(left and right keys are used if enabled)
        autoAdjustHeight: true, // Automatically adjust content height
        cycleSteps: false, // Allows to cycle the navigation of steps
        backButtonSupport: true, // Enable the back button support
        useURLhash: true, // Enable selection of the step based on url hash
        // hiddenSteps: $('.foo').foo(), // an array of step numbers to show as disabled
        showStepURLhash: false, //Show url hash based on step
        lang: {  // Language variables
            next: 'Next',
            previous: 'Previous'
        },
        toolbarSettings: {
            toolbarPosition: 'bottom', // none, top, bottom, both
            toolbarButtonPosition: 'right', // left, right
            showNextButton: false, // show/hide a Next button
            showPreviousButton: false, // show/hide a Previous button
            // toolbarExtraButtons:
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




$.urlParam = function(name){
    var results = new RegExp('[\?&]' + name + '=([^&#]*)').exec(window.location.href);
    if (results==null){
        return null;
    }
    else{
        return decodeURI(results[1]) || 0;
    }
}