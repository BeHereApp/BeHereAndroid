package br.ufrn.imd.behere.utils;

import android.app.ProgressDialog;
import android.webkit.WebView;

public class Constants {
    public static final String KEY_ACCESS_TOKEN = "access_token";
    public static final String KEY_REFRESH_TOKEN = "refresh_token";
    public static final String KEY_EXPIRES_IN = "expires_in";
    public static final String BASE_URL = "https://apitestes.info.ufrn.br/";

    public static final String CLIENT_ID_VALUE = "behere-id";
    public static final String SECRET_KEY = "segredo";

    public static final String REDIRECT_URI = "https://api.ufrn.br";
    public static final String AUTHORIZATION_URL = "http://apitestes.info.ufrn.br/authz-server/oauth/authorize";
    public static final String ACCESS_TOKEN_URL = "http://apitestes.info.ufrn.br/authz-server/oauth/token";
    public static final String RESPONSE_TYPE_PARAM = "response_type";
    public static final String GRANT_TYPE = "authorization_code";
    public static final String RESPONSE_TYPE_VALUE = "code";
    public static final String CLIENT_ID_PARAM = "client_id";
    public static final String REDIRECT_URI_PARAM = "redirect_uri";
}
