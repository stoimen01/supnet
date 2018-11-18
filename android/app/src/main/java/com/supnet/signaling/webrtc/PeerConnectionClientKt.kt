package com.supnet.signaling.webrtc

import android.content.Context
import org.webrtc.*
import org.webrtc.audio.AudioDeviceModule
import org.webrtc.audio.JavaAudioDeviceModule
import org.webrtc.audio.JavaAudioDeviceModule.*
import java.util.concurrent.Executors

class PeerConnectionClientKt(
    private val appContext: Context
) {

    /*val peerConnectionClient by lazy {

        val roomUrl = ""
        val videoCallEnabled = true
        val loopback = false
        val useScreenCapture = false
        val useCamera2 = true
        val videoCodec = "VP8"
        val audioCodec = "OPUS"
        val hwCodec = true
        val captureToTexture = true
        val flexfecEnabled = false
        val noAudioProcessing = false
        val aecDump = false
        val saveInputAudioToFile = false
        val useOpenSLES = false
        val disableBuiltInAEC = false
        val disableBuiltInAGC = false
        val disableBuiltInNS = false
        val disableWebRtcAGCAndHPF = false
        val videoWidth = 700
        val videoHeight = 700
        val cameraFps = 30
        val captureQualitySlider = false
        val videoStartBitrate = 1700
        val audioStartBitrate = 32
        val displayHud = false
        val tracing = false
        val rtcEventLogEnabled = false
        val useLegacyAudioDevice = false

        // data channel
        val ordered = true
        val negotiated = false
        val maxRetrMs = -1
        val maxRetr = -1
        val id = -1
        val protocol = ""

        val videoFileAsCamera = false
        val saveRemoteVideoToFile = false
        val videoOutWidth = 100
        val videoOutHeight = 100

        val dataChannelParameters = PeerConnectionClient.DataChannelParameters(
            ordered,
            maxRetrMs,
            maxRetr,
            protocol,
            negotiated,
            id
        )

        val params = PeerConnectionClient.PeerConnectionParameters(
            videoCallEnabled,
            loopback,
            tracing,
            videoWidth,
            videoHeight,
            cameraFps,
            videoStartBitrate,
            videoCodec,
            hwCodec,
            flexfecEnabled,
            audioStartBitrate,
            audioCodec,
            noAudioProcessing,
            aecDump,
            saveInputAudioToFile,
            useOpenSLES,
            disableBuiltInAEC,
            disableBuiltInAGC,
            disableBuiltInNS,
            disableWebRtcAGCAndHPF,
            rtcEventLogEnabled,
            useLegacyAudioDevice,
            dataChannelParameters
        )

        val pcc = PeerConnectionClient(context, EglBase.create(), params, this@PeerConnectionClientKt)
        val options = PeerConnectionFactory.Options();
        pcc.createPeerConnectionFactory(options);

        return@lazy pcc
    }*/

    init {
        executor.execute {
            PeerConnectionFactory.initialize(
                PeerConnectionFactory.InitializationOptions.builder(appContext)
                    .setFieldTrials(getFieldTrials(false, false))
                    .setEnableInternalTracer(true)
                    .createInitializationOptions()
            )
        }
    }

    // TODO: make sure factory is initialized from executor thread
    private val factory by lazy {
        /*
        if (peerConnectionParameters.tracing) {
          PeerConnectionFactory.startInternalTracingCapture(
              Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator
              + "webrtc-trace.txt");
        }
        */
        /*
        if (peerConnectionParameters.saveInputAudioToFile) {
          if (!peerConnectionParameters.useOpenSLES) {
            Log.d(TAG, "Enable recording of microphone input audio to file");
            saveRecordedAudioToFile = new RecordedAudioToFileController(executor);
          } else {
            // TODO(henrika): ensure that the UI reflects that if OpenSL ES is selected,
            // then the "Save inut audio to file" option shall be grayed out.
            Log.e(TAG, "Recording of input audio is not supported for OpenSL ES");
          }
        }
        */

        val saveRecordedAudioToFile = RecordedAudioToFileController(executor)
        val rootEglBase = EglBase.create()
        val isHwAccelerationEnabled = false

        val adm = createJavaAudioDevice(saveRecordedAudioToFile)

        val enableH264HighProfile = false

        val encoderFactory: VideoEncoderFactory
        val decoderFactory: VideoDecoderFactory



        if (isHwAccelerationEnabled) {
            encoderFactory = DefaultVideoEncoderFactory(rootEglBase.eglBaseContext, true, enableH264HighProfile)
            decoderFactory = DefaultVideoDecoderFactory(rootEglBase.eglBaseContext)
        } else {
            encoderFactory = SoftwareVideoEncoderFactory()
            decoderFactory = SoftwareVideoDecoderFactory()
        }

        val options = PeerConnectionFactory.Options()

        val factory =  PeerConnectionFactory.builder()
            .setOptions(options)
            .setAudioDeviceModule(adm)
            .setVideoEncoderFactory(encoderFactory)
            .setVideoDecoderFactory(decoderFactory)
            .createPeerConnectionFactory()
        adm.release()
        return@lazy factory

    }





    private fun createJavaAudioDevice(saver: RecordedAudioToFileController): AudioDeviceModule {

        /*
        if (!peerConnectionParameters.useOpenSLES) {
          Log.w(TAG, "External OpenSLES ADM not implemented yet.");
          // TODO(magjed): Add support for external OpenSLES ADM.
        }
        */

        val audioRecordErrorCallback = object : AudioRecordErrorCallback {
            override fun onWebRtcAudioRecordInitError(errMsg: String) = reportError(errMsg)
            override fun onWebRtcAudioRecordError(errMsg: String) = reportError(errMsg)
            override fun onWebRtcAudioRecordStartError(errCode: AudioRecordStartErrorCode, errMsg: String) = reportError(errMsg)
        }

        val audioTrackErrorCallback = object : AudioTrackErrorCallback {
            override fun onWebRtcAudioTrackError(errMsg: String) = reportError(errMsg)
            override fun onWebRtcAudioTrackStartError(errCode: AudioTrackStartErrorCode, errMsg: String) = reportError(errMsg)
            override fun onWebRtcAudioTrackInitError(errMsg: String) = reportError(errMsg)
        }

        return JavaAudioDeviceModule.builder(appContext)
            .setSamplesReadyCallback(saver)
            .setUseHardwareAcousticEchoCanceler(true)
            .setUseHardwareNoiseSuppressor(true)
            .setAudioRecordErrorCallback(audioRecordErrorCallback)
            .setAudioTrackErrorCallback(audioTrackErrorCallback)
            .createAudioDeviceModule()
    }


    private fun reportError(errMsg: String) {
        executor.execute {
           /* if (!isError) {
                events.onPeerConnectionError(errorMessage)
                isError = true
            }*/
        }
    }






    companion object {
        val VIDEO_TRACK_ID = "ARDAMSv0"
        val AUDIO_TRACK_ID = "ARDAMSa0"
        val VIDEO_TRACK_TYPE = "video"
        private val TAG = "PCRTCClient"
        private val VIDEO_CODEC_VP8 = "VP8"
        private val VIDEO_CODEC_VP9 = "VP9"
        private val VIDEO_CODEC_H264 = "H264"
        private val VIDEO_CODEC_H264_BASELINE = "H264 Baseline"
        private val VIDEO_CODEC_H264_HIGH = "H264 High"
        private val AUDIO_CODEC_OPUS = "opus"
        private val AUDIO_CODEC_ISAC = "ISAC"
        private val VIDEO_CODEC_PARAM_START_BITRATE = "x-google-start-bitrate"
        private val VIDEO_FLEXFEC_FIELDTRIAL = "WebRTC-FlexFEC-03-Advertised/Enabled/WebRTC-FlexFEC-03/Enabled/"
        private val VIDEO_VP8_INTEL_HW_ENCODER_FIELDTRIAL = "WebRTC-IntelVP8/Enabled/"
        private val DISABLE_WEBRTC_AGC_FIELDTRIAL = "WebRTC-Audio-MinimizeResamplingOnMobile/Enabled/"
        private val AUDIO_CODEC_PARAM_BITRATE = "maxaveragebitrate"
        private val AUDIO_ECHO_CANCELLATION_CONSTRAINT = "googEchoCancellation"
        private val AUDIO_AUTO_GAIN_CONTROL_CONSTRAINT = "googAutoGainControl"
        private val AUDIO_HIGH_PASS_FILTER_CONSTRAINT = "googHighpassFilter"
        private val AUDIO_NOISE_SUPPRESSION_CONSTRAINT = "googNoiseSuppression"
        private val DTLS_SRTP_KEY_AGREEMENT_CONSTRAINT = "DtlsSrtpKeyAgreement"
        private val HD_VIDEO_WIDTH = 1280
        private val HD_VIDEO_HEIGHT = 720
        private val BPS_IN_KBPS = 1000
        private val RTCEVENTLOG_OUTPUT_DIR_NAME = "rtc_event_log"

        private val executor = Executors.newSingleThreadExecutor()


        private fun getFieldTrials(videoFlexfecEnabled: Boolean, disableWebRtcAGCAndHPF: Boolean): String {
            var fieldTrials = ""
            if (videoFlexfecEnabled) {
                fieldTrials += VIDEO_FLEXFEC_FIELDTRIAL
            }
            fieldTrials += VIDEO_VP8_INTEL_HW_ENCODER_FIELDTRIAL
            if (disableWebRtcAGCAndHPF) {
                fieldTrials += DISABLE_WEBRTC_AGC_FIELDTRIAL
            }
            return fieldTrials
        }

    }
}