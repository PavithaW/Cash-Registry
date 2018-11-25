/**
 * Copyright (C) 2015 - Tharaka Nirmana
 */
package com.cbasolutions.cbapos.service;

import android.content.Context;
import android.widget.Toast;

import com.cbasolutions.cbapos.R;
import com.cbasolutions.cbapos.activity.Application;
import com.cbasolutions.cbapos.activity.BaseActivity;
import com.cbasolutions.cbapos.activity.MainActivity;
import com.cbasolutions.cbapos.helper.Config;
import com.cbasolutions.cbapos.viewmodel.BillViewModel;
import com.cbasolutions.cbapos.viewmodel.ForgetPasswordViewModel;
import com.cbasolutions.cbapos.viewmodel.SignInViewModel;
import com.cbasolutions.cbapos.viewmodel.SignUpViewModel2;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;

/**
 * @author Tharaka Nirmana
 *
 */
public class PayableService {

    public static ReturnTypes logUserIn(Context mContext, final SignInViewModel context, String email, String password) {
        PayableClient.post(mContext, Config.LOGIN, getSignInEntityJsonParams(email, password), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                context.didReceiveLoginResults(Config.OK, response);
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                context.didReceiveLoginResults(statusCode, errorResponse);
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                context.loginFail(statusCode,responseString, throwable);
                //context.unAuthorizeLogin(statusCode, responseString);
        }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
        return ReturnTypes.SUCCESS;
    }

    private static StringEntity getSignInEntityJsonParams(String email, String password) {
        JSONObject jsonParams = new JSONObject();
        StringEntity entity = null;
        try {
            jsonParams.put("username", email);
            jsonParams.put("password",password);
            entity = new StringEntity(jsonParams.toString());
            //entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return entity;
    }


    public static ReturnTypes signUpUserIn(Context mContext, final SignUpViewModel2 context, String email, String password, String bName, String bAddress, String phone) {
        PayableClient.post(mContext, Config.SIGN_UP, getSignUpEntityJsonParams(email, password, bName, bAddress, phone), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                context.didReceiveSIgnUpResults(Config.OK, response);
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                super.onSuccess(statusCode, headers, responseString);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

        });
        return ReturnTypes.SUCCESS;
    }

    private static StringEntity getSignUpEntityJsonParams(String email, String password, String bName, String bAddress, String phone) {
        JSONObject jsonParams = new JSONObject();
        StringEntity entity = null;
        try {
            jsonParams.put("storename", bName);
            jsonParams.put("username", email);
            jsonParams.put("email", email);
            jsonParams.put("storeMobile", phone);
            jsonParams.put("password",password);
            jsonParams.put("storeAddress",bAddress);
            entity = new StringEntity(jsonParams.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return entity;
    }

    public static ReturnTypes forgetPassword(final Context mContext, final ForgetPasswordViewModel viewModel, String email) {
        PayableClient.post(mContext, Config.FORGET_PASSWORD, getForgetPasswordEntityJsonParams(email), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                final Context con = mContext;
                viewModel.didReceiveForgotPasswordResult(Config.OK,response,con.getString(R.string.forgotPassword) );
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                super.onSuccess(statusCode, headers, responseString);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                viewModel.didReceiveForgotPasswordResult(statusCode, null,Config.getErrorResponseMessage(statusCode));
                super.onFailure(statusCode, headers, responseString, throwable);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
        return ReturnTypes.SUCCESS;
    }

    private static StringEntity getForgetPasswordEntityJsonParams(String email) {
        JSONObject jsonParams = new JSONObject();
        StringEntity entity = null;
        try {
            jsonParams.put("email", email);
            entity = new StringEntity(jsonParams.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return entity;
    }

    /////////*******/////////////////

    public static ReturnTypes sendReceipt(final Context mContext, final BaseActivity baseActivity, final BillViewModel viewModel, String email, String tId) {
        PayableClient.post(mContext, Config.SEND_RECEIPT, getSendReceiptEntityJsonParams(email,tId), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                final Context con = mContext;
                if(baseActivity != null){
                    baseActivity.didReceiveSendReceiptResults(con,Config.OK,response);
                }else{
                    viewModel.didReceiveSendReceiptResults(con,Config.OK,response);
                }

                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                super.onSuccess(statusCode, headers, responseString);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                final Context con = mContext;
                if(baseActivity != null){
                    baseActivity.didReceiveSendReceiptResults(con,0,null);
                }else{
                    viewModel.didReceiveSendReceiptResults(con,0,null);
                }

                super.onFailure(statusCode, headers, responseString, throwable);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
        return ReturnTypes.SUCCESS;
    }

    private static StringEntity getSendReceiptEntityJsonParams(String email, String tId) {
        JSONObject jsonParams = new JSONObject();
        StringEntity entity = null;
        try {
            jsonParams.put("email", email);
            jsonParams.put("tnxId", tId);
            entity = new StringEntity(jsonParams.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return entity;
    }

    public static ReturnTypes sendSms(final Context mContext, final BaseActivity baseActivity, final BillViewModel viewModel, String phoneNumber, String invoice) {
        PayableClient.post(mContext, Config.SEND_SMS, getSendSmsEntityJsonParams(invoice), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                final Context con = mContext;
                if(baseActivity != null){
                    baseActivity.didReceiveSendSmsResults(con,Config.OK,response);
                }else{
                    viewModel.didReceiveSendSmsResults(con,Config.OK,response);
                }

                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                super.onSuccess(statusCode, headers, responseString);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                final Context con = mContext;
                if(baseActivity != null){
                    baseActivity.didReceiveSendSmsResults(con,0,null);
                }else{
                    viewModel.didReceiveSendSmsResults(con,0,null);
                }
                super.onFailure(statusCode, headers, responseString, throwable);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
        return ReturnTypes.SUCCESS;
    }

    private static StringEntity getSendSmsEntityJsonParams(String invoiceId) {
        JSONObject jsonParams = new JSONObject();
        StringEntity entity = null;
        try {
            jsonParams.put("invoice_id", invoiceId);
            //jsonParams.put("mobile", phoneNumber);
            entity = new StringEntity(jsonParams.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return entity;
    }

    public static ReturnTypes getLowStockItems(final MainActivity mContext, String owner) {
        PayableClient.post(mContext, Config.LOW_STOCK, getLowStockEntityJsonParams(owner), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
        }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                mContext.didReceiveLowStockItems(mContext,Config.OK, response,null);
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                super.onSuccess(statusCode, headers, responseString);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {

                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                mContext.didReceiveLowStockItems(mContext,statusCode,null, throwable);
                //context.unAuthorizeLogin(statusCode, responseString);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                mContext.didReceiveLowStockItems(mContext,statusCode, errorResponse,null);
            }
        });
        return ReturnTypes.SUCCESS;
    }

    private static StringEntity getLowStockEntityJsonParams(String owner) {
        JSONObject jsonParams = new JSONObject();
        StringEntity entity = null;
        try {
            jsonParams.put("owner", owner);
            entity = new StringEntity(jsonParams.toString());
            //entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return entity;
    }


}

