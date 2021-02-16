package com.kesizo.cetpe.backend.restapi.util;

public final class Constants {

    public static final String EMAIL_FORGOT_PASSWORD_CONTENT_START = "<p>Hello,</p>"
            + "<p>You have requested to reset your password.</p>"
            + "<p>Click the link below to change your password:</p>";

    public static final String EMAIL_FORGOT_PASSWORD_CONTENT_END =
            "<br/>"
            + "<p>Ignore this email if you do remember your password, "
            + "or you have not made the request.</p>";

    private Constants() {
        // restrict instantiations
    }

    public static final String EMAIL_ACTIVATION_CONTENT_START = "We have received a request to be part of the CETPE users community." +
            "By clicking on the link below, you confirm that you want to be member of the site accepting our " +
            "privacy policy.<br/></br>" +
            " The link is valid for 24 hours, after that period all the submitted data will be removed, so be sure you confirm your request on time. " +
            "If you have not requested such account, just contact us and we will delete the information related to your email immediately. <br/> <br/>";

    public static final String EMAIL_ACTIVATION_CONTENT_END = "<br/>Thank you and enjoy! <br/>  -- CETPE Team";

    public static final String DATE_FORMATTER= "yyyy-MM-dd HH:mm:ss";

    public static final String RUBRIC_TITLE_DEFAULT = "Default Title Rubric ";
    public static final int RUBRIC_DEFAULT_RANK = 5;
    public static final boolean RUBRIC_DEFAULT_ENABLED = true;

    public static final int ACTIVATION_CODE_EXPIRATION_PERIOD_HOURS = 24;
    public static final String EMAIL_ACTIVATION_SUBJECT = "CETPE Activation account email";
    public static final String EMAIL_RESET_PASSWORD_SUBJECT = "CETPE reset password email";


}
