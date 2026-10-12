package com.example.ui.call

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VideocamOff
import androidx.compose.material.icons.filled.VolumeDown
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.ui.common.ContactAvatar
import com.example.ui.theme.CallAcceptGreen
import com.example.ui.theme.CallDeclineRed
import com.example.ui.theme.CyanSecondary
import com.example.ui.theme.DarkNavyBackground
import com.example.ui.theme.SlateLight
import com.example.ui.theme.TealPrimary
import com.example.viewmodel.CallStatus
import com.example.viewmodel.MainViewModel

@Composable
fun VideoCallScreen(viewModel: MainViewModel) {
    val activeCall by viewModel.activeCall.collectAsState()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    BackHandler {
        viewModel.endCall()
    }

    val call = activeCall ?: return
    val contact = call.contact

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF090E17))
    ) {
        // Remote Video Feed Background Simulation
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            // Elegant gradient backdrop
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color(0xFF0D1B2A),
                                Color(0xFF1B263B),
                                Color(0xFF0D1B2A)
                            )
                        )
                    )
            )

            // Remote Participant Avatar with HD Stream Indicator
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                ContactAvatar(
                    name = contact.name,
                    avatarResName = contact.avatarResName,
                    size = 140.dp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = contact.name,
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0x33000000)
                ) {
                    Text(
                        text = "1080p HD Video • Encrypted",
                        color = Color(0xFF38BDF8),
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
        }

        // Top Info Bar (Timer, Call Status)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 44.dp, start = 20.dp, end = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "BSL CALLING Video",
                    color = TealPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                if (call.status == CallStatus.CONNECTED) {
                    val minutes = call.durationSeconds / 60
                    val seconds = call.durationSeconds % 60
                    Text(
                        text = String.format("%02d:%02d", minutes, seconds),
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                } else if (call.status == CallStatus.INCOMING) {
                    Text(
                        text = "Incoming Video Call...",
                        color = CallAcceptGreen,
                        fontSize = 14.sp
                    )
                } else {
                    Text(
                        text = "Connecting video...",
                        color = CyanSecondary,
                        fontSize = 14.sp
                    )
                }
            }
        }

        // Local Camera Preview - Picture in Picture (Top-Right)
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 90.dp, end = 16.dp)
                .size(width = 110.dp, height = 150.dp)
                .clip(RoundedCornerShape(14.dp))
                .border(2.dp, Color(0x66FFFFFF), RoundedCornerShape(14.dp))
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            if (call.isCameraOn && hasCameraPermission) {
                // Live CameraX Preview!
                AndroidView(
                    factory = { ctx ->
                        val previewView = PreviewView(ctx)
                        val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                        cameraProviderFuture.addListener({
                            val cameraProvider = cameraProviderFuture.get()
                            val preview = Preview.Builder().build().also {
                                it.setSurfaceProvider(previewView.surfaceProvider)
                            }
                            val cameraSelector = if (call.isFrontCamera) {
                                CameraSelector.DEFAULT_FRONT_CAMERA
                            } else {
                                CameraSelector.DEFAULT_BACK_CAMERA
                            }
                            try {
                                cameraProvider.unbindAll()
                                cameraProvider.bindToLifecycle(
                                    lifecycleOwner,
                                    cameraSelector,
                                    preview
                                )
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }, ContextCompat.getMainExecutor(ctx))
                        previewView
                    },
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.VideocamOff,
                        contentDescription = "Camera Off",
                        tint = SlateLight,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (!hasCameraPermission) "No Perm" else "Camera Off",
                        color = SlateLight,
                        fontSize = 10.sp
                    )
                }
            }
        }

        // Bottom Controls Bar
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 32.dp, start = 16.dp, end = 16.dp)
        ) {
            if (call.status == CallStatus.INCOMING) {
                // Incoming video call: Accept / Decline
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { viewModel.declineIncomingCall() },
                        modifier = Modifier
                            .size(68.dp)
                            .clip(CircleShape)
                            .background(CallDeclineRed)
                            .testTag("decline_video_call_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.CallEnd,
                            contentDescription = "Decline",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    IconButton(
                        onClick = { viewModel.acceptIncomingCall() },
                        modifier = Modifier
                            .size(68.dp)
                            .clip(CircleShape)
                            .background(CallAcceptGreen)
                            .testTag("accept_video_call_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Videocam,
                            contentDescription = "Accept Video",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            } else {
                // Connected controls
                Surface(
                    shape = RoundedCornerShape(28.dp),
                    color = Color(0x88111827),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Switch Camera
                        IconButton(
                            onClick = { viewModel.switchCamera() },
                            modifier = Modifier.testTag("switch_camera_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Cameraswitch,
                                contentDescription = "Switch Camera",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        // Toggle Camera On/Off
                        IconButton(
                            onClick = { viewModel.toggleVideoCamera() },
                            modifier = Modifier.testTag("toggle_camera_btn")
                        ) {
                            Icon(
                                imageVector = if (call.isCameraOn) Icons.Default.Videocam else Icons.Default.VideocamOff,
                                contentDescription = "Toggle Camera",
                                tint = if (call.isCameraOn) Color.White else CallDeclineRed,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        // Toggle Mic Mute
                        IconButton(
                            onClick = { viewModel.toggleMute() },
                            modifier = Modifier.testTag("video_mute_btn")
                        ) {
                            Icon(
                                imageVector = if (call.isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                                contentDescription = "Mute",
                                tint = if (call.isMuted) CallDeclineRed else Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        // Toggle Speaker
                        IconButton(
                            onClick = { viewModel.toggleSpeaker() },
                            modifier = Modifier.testTag("video_speaker_btn")
                        ) {
                            Icon(
                                imageVector = if (call.isSpeakerOn) Icons.Default.VolumeUp else Icons.Default.VolumeDown,
                                contentDescription = "Speaker",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        // End Call Button
                        IconButton(
                            onClick = { viewModel.endCall() },
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(CallDeclineRed)
                                .testTag("end_video_call_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.CallEnd,
                                contentDescription = "End Video Call",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
