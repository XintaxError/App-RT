package com.rian.apprt

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.rian.apprt.User.MainActivity
import com.rian.apprt.databinding.ActivityLoginBinding
import org.json.JSONException
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {
    private lateinit var b: ActivityLoginBinding
    private lateinit var urlClass: UrlClass
    private lateinit var preferences: SharedPreferences

    private val PREF_NAME = "akun"
    private val USER = "kd_user"
    private val NAMA = "nama"
    private val LOGIN = "isLoggedIn"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(b.root)
        supportActionBar?.hide()

        urlClass = UrlClass()

        b.btnLogin.setOnClickListener {
            val username = b.loginUsername.text.toString().trim()
            val password = b.loginPassword.text.toString().trim()

            if (username.isNotEmpty() && password.isNotEmpty()) {
                validateAccount(username, password)
            } else {
                Toast.makeText(this, "Username atau Password tidak boleh kosong!", Toast.LENGTH_LONG).show()
            }
        }

        b.btnRegis.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.stay)
        }

        b.btnNext.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.stay)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, LandingPageActivity::class.java))
        overridePendingTransition(R.anim.slide_in_left, R.anim.stay)
    }

    private fun validateAccount(username: String, password: String) {
        val request = object : StringRequest(
            Request.Method.POST, urlClass.validasi,
            Response.Listener { response ->
                try {
                    val jsonObject = JSONObject(response)
                    val level = jsonObject.getString("level")
                    val kd = jsonObject.getString("kd_user")
                    val nm = jsonObject.getString("nama")

                    preferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                    val prefEditor = preferences.edit()
                    prefEditor.putString(USER, kd)
                    prefEditor.putString(NAMA, nm)
                    prefEditor.putBoolean(LOGIN, true)
                    prefEditor.apply()

                    if (level.equals("User")) {
                        startActivity(Intent(this@LoginActivity, FaceDetectionActivity::class.java))
                    } else if (level.equals("Admin")) {
                        startActivity(Intent(this@LoginActivity, com.rian.apprt.Admin.MainActivity::class.java))
                    }
                    finishAffinity()
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error: VolleyError ->
                AlertDialog.Builder(this@LoginActivity)
                    .setTitle("Peringatan!")
                    .setMessage("Tidak dapat terhubung ke server")
                    .setPositiveButton("OK") { dialogInterface: DialogInterface, i: Int ->
                        // Do nothing, just close the dialog
                    }
                    .show()
            }) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["mode"] = "login"
                params["username"] = username
                params["password"] = password
                return params
            }
        }
        val queue: RequestQueue = Volley.newRequestQueue(this@LoginActivity)
        queue.add(request)
    }
}
