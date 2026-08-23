package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Language
import com.example.model.User
import com.example.ui.components.MeskotAvatar
import com.example.ui.components.MeskotBrandMark
import com.example.ui.theme.*
import com.example.viewmodel.MeskotViewModel

@Composable
fun AuthScreen(
    viewModel: MeskotViewModel,
    language: Language,
    allUsers: List<User>
) {
    var isSignUp by remember { mutableStateOf(false) }
    var isForgotPassword by remember { mutableStateOf(false) }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MeskotPaper)
            .statusBarsPadding()
            .navigationBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Brand Logo
            MeskotBrandMark(size = 54.dp)
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Meskot",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif,
                color = MeskotInk
            )
            Text(
                text = if (language == Language.AM) "መስኮትህ ለማህበረሰብህ" else "Your Window to the Community",
                fontSize = 13.sp,
                color = MeskotMuted
            )

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MeskotCard),
                border = BorderStroke(1.dp, MeskotLine),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    if (isForgotPassword) {
                        Text(
                            text = if (language == Language.AM) "የይለፍ ቃል ዳግም ያስጀምሩ" else "Reset your password",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif,
                            color = MeskotInk
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (language == Language.AM) "ኢሜይልዎን ያስገቡ የዳግም ማስጀመሪያ ማገናኛ እንልክልዎታለን" else "Enter your email to receive a password reset link",
                            fontSize = 13.sp,
                            color = MeskotMuted
                        )
                        Spacer(modifier = Modifier.height(18.dp))

                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it; errorMessage = null },
                            label = { Text(if (language == Language.AM) "ኢሜይል" else "Email") },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                if (email.isBlank()) {
                                    errorMessage = viewModel.t("fillFields")
                                } else {
                                    successMessage = viewModel.t("resetEmailSent")
                                    errorMessage = null
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MeskotGold),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(if (language == Language.AM) "ማገናኛ ላክ" else "Send reset link", color = Color.White, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        TextButton(
                            onClick = { isForgotPassword = false; errorMessage = null; successMessage = null },
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        ) {
                            Text(if (language == Language.AM) "← ወደ መግቢያ ተመለስ" else "← Back to log in", color = MeskotGoldDeep)
                        }
                    } else if (isSignUp) {
                        Text(
                            text = if (language == Language.AM) "ወደ መስኮት ይቀላቀሉ" else "Join Meskot",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif,
                            color = MeskotInk
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (language == Language.AM) "ወደ ማህበረሰቡ የሚያዩበትን መስኮት ይፍጠሩ" else "Create your window onto the community",
                            fontSize = 13.sp,
                            color = MeskotMuted
                        )
                        Spacer(modifier = Modifier.height(18.dp))

                        OutlinedTextField(
                            value = fullName,
                            onValueChange = { fullName = it; errorMessage = null },
                            label = { Text(if (language == Language.AM) "ሙሉ ስም" else "Full name") },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth().testTag("signup_name_field")
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it; errorMessage = null },
                            label = { Text(if (language == Language.AM) "ኢሜይል" else "Email") },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth().testTag("signup_email_field")
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it; errorMessage = null },
                            label = { Text(if (language == Language.AM) "የይለፍ ቃል" else "Password") },
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility, contentDescription = null)
                                }
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth().testTag("signup_pass_field")
                        )
                        Text(
                            text = if (language == Language.AM) "ከ6 ፊደላት በላይ፣ አቢይ/ንዑስ፣ ቁጥር እና ምልክት" else "6+ chars, upper & lowercase letters, number & symbol",
                            fontSize = 11.sp,
                            color = MeskotMuted,
                            modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        Button(
                            onClick = {
                                if (fullName.isBlank() || email.isBlank() || password.isBlank()) {
                                    errorMessage = viewModel.t("fillFields")
                                } else {
                                    viewModel.signup(fullName, email, password)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MeskotGold),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth().testTag("signup_submit_btn")
                        ) {
                            Text(if (language == Language.AM) "አካውንት ይፍጠሩ" else "Create account", color = Color.White, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(if (language == Language.AM) "አካውንት አለዎት? " else "Already have an account? ", fontSize = 13.sp, color = MeskotMuted)
                            Text(
                                text = if (language == Language.AM) "ይግቡ" else "Log in",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MeskotGoldDeep,
                                modifier = Modifier.clickable { isSignUp = false; errorMessage = null }
                            )
                        }
                    } else {
                        // Login Form
                        Text(
                            text = if (language == Language.AM) "እንኳን ደህና መጡ" else "Welcome back",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif,
                            color = MeskotInk
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (language == Language.AM) "ማህበረሰብዎ ምን እያካፈለ እንደሆነ ለማየት ይግቡ" else "Log in to see what your circle is sharing",
                            fontSize = 13.sp,
                            color = MeskotMuted
                        )
                        Spacer(modifier = Modifier.height(18.dp))

                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it; errorMessage = null },
                            label = { Text(if (language == Language.AM) "ኢሜይል" else "Email") },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth().testTag("login_email_field")
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it; errorMessage = null },
                            label = { Text(if (language == Language.AM) "የይለፍ ቃል" else "Password") },
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility, contentDescription = null)
                                }
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth().testTag("login_pass_field")
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(onClick = { isForgotPassword = true; errorMessage = null }) {
                                Text(if (language == Language.AM) "የይለፍ ቃል ረሱ?" else "Forgot password?", fontSize = 12.sp, color = MeskotGoldDeep)
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Button(
                            onClick = {
                                if (email.isBlank() || password.isBlank()) {
                                    errorMessage = viewModel.t("fillFields")
                                } else {
                                    viewModel.login(email, password)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MeskotGold),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth().testTag("login_submit_btn")
                        ) {
                            Text(if (language == Language.AM) "ግባ" else "Log in", color = Color.White, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(if (language == Language.AM) "አካውንት የለዎትም? " else "No account yet? ", fontSize = 13.sp, color = MeskotMuted)
                            Text(
                                text = if (language == Language.AM) "ይመዝገቡ" else "Sign up",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MeskotGoldDeep,
                                modifier = Modifier.clickable { isSignUp = true; errorMessage = null }
                            )
                        }
                    }

                    if (errorMessage != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFFBEAEA),
                            border = BorderStroke(1.dp, Color(0xFFF0C6C9))
                        ) {
                            Text(
                                text = errorMessage ?: "",
                                color = MeskotCrimson,
                                fontSize = 12.5.sp,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            )
                        }
                    }

                    if (successMessage != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFEAF6EC),
                            border = BorderStroke(1.dp, Color(0xFFBFE3C7))
                        ) {
                            Text(
                                text = successMessage ?: "",
                                color = Color(0xFF276B3B),
                                fontSize = 12.5.sp,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }

            // Quick Demo Accounts Switcher
            Spacer(modifier = Modifier.height(20.dp))
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MeskotPaper2),
                border = BorderStroke(1.dp, MeskotLine),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "🚀 " + if (language == Language.AM) "የሙከራ አካውንቶች (አንድ ጊዜ በመንካት ይግቡ)" else "Quick Demo Accounts (Tap to test)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MeskotInk
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        allUsers.take(3).forEach { user ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.White)
                                    .border(1.dp, MeskotLine, RoundedCornerShape(8.dp))
                                    .clickable {
                                        viewModel.switchUser(user.uid)
                                    }
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    MeskotAvatar(photoUrl = user.photoURL, displayName = user.displayName, size = 30.dp)
                                    Column {
                                        Text(user.displayName, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                        Text(user.email, fontSize = 11.sp, color = MeskotMuted)
                                    }
                                }
                                if (user.isAdmin) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = Color(0xFFFFF3D6)
                                    ) {
                                        Text("Admin", fontSize = 10.sp, color = MeskotGoldDeep, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
