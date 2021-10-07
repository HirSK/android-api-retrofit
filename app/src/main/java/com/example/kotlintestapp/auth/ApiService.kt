package com.example.kotlintestapp.auth

import android.content.ContentValues
import android.util.Log
import com.github.kittinunf.fuel.core.extensions.authentication
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import com.google.gson.Gson

import java.lang.Exception

class ApiService {
    private val clientId = "" //add clientId
    private val clientSecret = "" //add clientSecret
    private val apigeeTokenUrl = "" //add token url
    private val grantType = "client_credentials"

    var token: String? = null
    var tokenType: String? = null

    private fun callApi(apiEndpoint: String, tokenType: String, token: String): Any {
        val (request, response, result) = apiEndpoint
            .httpGet()
            .header(Pair("Authorization", "$tokenType $token"))
            .responseString()

        return when (result) {
            is Result.Success -> {
                Log.d(ContentValues.TAG, "Success  ${result.value}")
            }
            is Result.Failure -> {
                Log.d(ContentValues.TAG, "Failed")
            }
        }
    }

    private fun setAuthToken() {
        try {
            val (request, response, result) = apigeeTokenUrl.httpPost(listOf(
                "grant_type" to grantType,
            ))
                .authentication().basic(clientId, clientSecret)
                .responseString()

            when (result) {
                is Result.Success -> {
                    var gson = Gson()
                    val tokenResultJson = gson.fromJson(result.value, AuthResult::class.java)
                    token = tokenResultJson!!.access_token!!
                    tokenType = tokenResultJson!!.token_type!!
                    Log.d(ContentValues.TAG, "token $token")
                    Log.d(ContentValues.TAG, "token type $tokenType")

                }
                is Result.Failure -> {
                    // handle error
                    println("error")
                }
            }

        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    init {
        setAuthToken()
        callApi("https://..../getUsers", tokenType!!, token!!)
    }
}