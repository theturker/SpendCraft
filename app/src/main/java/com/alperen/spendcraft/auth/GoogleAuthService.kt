package com.alperen.spendcraft.auth

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoogleAuthService @Inject constructor() {
    
    private lateinit var googleSignInClient: GoogleSignInClient
    private val auth = FirebaseAuth.getInstance()
    
    fun initialize(context: Context) {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("973123190644-c75iu4smr8dl5okbm27hglgig9v3fc8p.apps.googleusercontent.com") // Web client ID from google-services.json
            .requestEmail()
            .build()
        
        googleSignInClient = GoogleSignIn.getClient(context, gso)
    }
    
    fun getSignInIntent() = googleSignInClient.signInIntent
    
    suspend fun signInWithGoogle(data: com.google.android.gms.auth.api.signin.GoogleSignInAccount): Result<FirebaseUser> {
        return try {
            println("GoogleAuthService: Starting Google Sign-In with token: ${data.idToken?.take(20)}...")
            val credential = GoogleAuthProvider.getCredential(data.idToken, null)
            println("GoogleAuthService: Credential created, signing in with Firebase...")
            val result = auth.signInWithCredential(credential).await()
            val user = result.user
            if (user != null) {
                println("GoogleAuthService: Firebase sign-in successful: ${user.uid}")
                Result.success(user)
            } else {
                println("GoogleAuthService: Firebase sign-in failed - no user")
                Result.failure(Exception("Google sign in failed - no user"))
            }
        } catch (e: Exception) {
            println("GoogleAuthService: Exception during sign-in: ${e.message}")
            Result.failure(e)
        }
    }
    
    fun signOut() {
        auth.signOut()
        googleSignInClient.signOut()
    }
    
    fun getCurrentUser(): FirebaseUser? = auth.currentUser
}
