/*
package com.supnet.signaling.webrtc

import android.content.Context
import android.util.Log
import org.webrtc.*
import org.webrtc.audio.AudioDeviceModule
import org.webrtc.audio.JavaAudioDeviceModule
import org.webrtc.audio.JavaAudioDeviceModule.*
import java.lang.IllegalStateException
import java.util.*
import java.util.concurrent.Executors
import java.util.regex.Pattern

class PeerConnectionClientKt(
    private val appContext: Context,
    factoryOptions: PeerConnectionFactory.Options,
    private val localSink: VideoSink,
    private val remoteSinks: List<VideoSink>,
    private val isHwAccelerationEnabled: Boolean = true,
    private val enableH264HighProfile: Boolean = false,
    private val preferIsac: Boolean = false,
    private val videoWidth: Int = HD_VIDEO_WIDTH,
    private val videoHeight: Int = HD_VIDEO_HEIGHT,
    private val videoFps: Int = 30,
    private val isVideoEnabled: Boolean = true,
    private val videoCodec: String = VIDEO_CODEC_VP8,
    private val audioStartBitrate: Int = 32
) {

    private var isInitiator = false
    private var queuedRemoteCandidates: ArrayList<IceCandidate>? = ArrayList<IceCandidate>()
    private val rootEglBase = EglBase.create()

    private val surfaceTextureHelper = SurfaceTextureHelper.create("CaptureThread", rootEglBase.eglBaseContext)
    private val videoCapturer = createVideoCapturer()
    private val mediaStreamLabels = listOf("ARDAMS")

    private val audioConstraints = MediaConstraints()
    private val audioSource by lazy { factory.createAudioSource(audioConstraints) }
    private val localAudioTrack by lazy { factory.createAudioTrack(AUDIO_TRACK_ID, audioSource) }

    private val sdpMediaConstraints = MediaConstraints().apply {
        mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"))
        mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveVideo", java.lang.Boolean.toString(isVideoEnabled)))
    }

    private lateinit var factory : PeerConnectionFactory

    private lateinit var peerConnection: PeerConnection

    private val dataChannel by lazy {
        with(DataChannel.Init()) {
            ordered = true
            negotiated = false
            maxRetransmits = -1
            maxRetransmitTimeMs = -1
            id = -1
            protocol = ""
            peerConnection.createDataChannel("ApprtcDemo data", this)
        }
    }

    private val videoSource by lazy { factory.createVideoSource(videoCapturer.isScreencast) }

    private val localVideoTrack by lazy { factory.createVideoTrack(VIDEO_TRACK_ID, videoSource) }

    private val remoteVideoTrack by lazy<VideoTrack?>{
        for (transceiver in peerConnection.transceivers) {
            val track = transceiver.receiver.track()
            if (track is VideoTrack) {
                return@lazy track
            }
        }
        return@lazy null
    }

    private val localVideoSender by lazy {
        for (sender in peerConnection.senders) {
            if (sender.track() != null) {
                val trackType = sender.track()!!.kind()
                if (trackType == VIDEO_TRACK_TYPE) {
                    return@lazy sender
                }
            }
        }
        return@lazy null
    }

    init { executor.execute {

        PeerConnectionFactory.initialize(
            PeerConnectionFactory.InitializationOptions.builder(appContext)
                .setFieldTrials(getFieldTrials(false, false))
                .setEnableInternalTracer(true)
                .createInitializationOptions()
        )

        val encoderFactory: VideoEncoderFactory
        val decoderFactory: VideoDecoderFactory

        if (isHwAccelerationEnabled) {
            encoderFactory = DefaultVideoEncoderFactory(rootEglBase.eglBaseContext, true, enableH264HighProfile)
            decoderFactory = DefaultVideoDecoderFactory(rootEglBase.eglBaseContext)
        } else {
            encoderFactory = SoftwareVideoEncoderFactory()
            decoderFactory = SoftwareVideoDecoderFactory()
        }

        val adm = createJavaAudioDevice()

        factory = PeerConnectionFactory.builder()
            .setOptions(factoryOptions)
            .setAudioDeviceModule(adm)
            .setVideoEncoderFactory(encoderFactory)
            .setVideoDecoderFactory(decoderFactory)
            .createPeerConnectionFactory()
            .also { adm.release() }
    }}


    fun createPeerConnection(iceServers: List<PeerConnection.IceServer>) = executor.execute {

        peerConnection = with(PeerConnection.RTCConfiguration(iceServers)) {
            tcpCandidatePolicy = PeerConnection.TcpCandidatePolicy.DISABLED
            bundlePolicy = PeerConnection.BundlePolicy.MAXBUNDLE
            rtcpMuxPolicy = PeerConnection.RtcpMuxPolicy.REQUIRE
            continualGatheringPolicy = PeerConnection.ContinualGatheringPolicy.GATHER_CONTINUALLY
            keyType = PeerConnection.KeyType.ECDSA
            enableDtlsSrtp = true
            sdpSemantics = PeerConnection.SdpSemantics.UNIFIED_PLAN
            factory.createPeerConnection(this, pcObserver)
                ?: throw IllegalStateException("Cannot create peer connection")
        }

        if (isVideoEnabled) {

            videoCapturer.initialize(surfaceTextureHelper, appContext, videoSource.capturerObserver)
            videoCapturer.startCapture(videoWidth, videoHeight, videoFps)

            localVideoTrack.setEnabled(true)
            localVideoTrack.addSink(localSink)
            peerConnection.addTrack(localVideoTrack, mediaStreamLabels)

            remoteVideoTrack?.setEnabled(true)
            remoteSinks.forEach { remoteVideoTrack?.addSink(it) }
        }

        localAudioTrack?.setEnabled(true)
        peerConnection.addTrack(localAudioTrack, mediaStreamLabels)

    }

    fun setAudioEnabled(enable: Boolean) = executor.execute {
        // enableAudio = enable;
        localAudioTrack?.setEnabled(enable)
    }

    fun setVideoEnabled(enable: Boolean) = executor.execute {
        //renderVideo = enable;
        localVideoTrack?.setEnabled(enable)
        remoteVideoTrack?.setEnabled(enable)
    }

    fun addRemoteIceCandidate(candidate: IceCandidate) = executor.execute {
        if (queuedRemoteCandidates != null) {
            queuedRemoteCandidates!!.add(candidate)
        } else {
            peerConnection.addIceCandidate(candidate)
        }
    }

    fun removeRemoteIceCandidates(candidates: Array<IceCandidate>) = executor.execute {
        // Drain the queued remote candidates if there is any so that
        // they are processed in the proper order.
        drainCandidates()
        peerConnection.removeIceCandidates(candidates)
    }

    fun stopVideoSource() = executor.execute {
        try {
            videoCapturer.stopCapture()
        } catch (e: InterruptedException) { }
    }

    fun startVideoSource() = executor.execute {
        Log.d(TAG, "Restart video source.")
        videoCapturer.startCapture(videoWidth, videoHeight, videoFps)
    }

    fun setVideoMaxBitrate(maxBitrateKbps: Int?) = executor.execute {
        if (localVideoSender == null) {
            return@execute
        }
        Log.d(TAG, "Requested max video bitrate: " + maxBitrateKbps!!)
        if (localVideoSender == null) {
            Log.w(TAG, "Sender is not ready.")
            return@execute
        }

        val parameters = localVideoSender!!.parameters
        if (parameters.encodings.size == 0) {
            Log.w(TAG, "RtpParameters are not ready.")
            return@execute
        }

        for (encoding in parameters.encodings) {
            // Null value means no limit.
            encoding.maxBitrateBps = maxBitrateKbps * BPS_IN_KBPS
        }
        if (!localVideoSender!!.setParameters(parameters)) {
            Log.e(TAG, "RtpSender.setParameters failed.")
        }
        Log.d(TAG, "Configured max video bitrate to: $maxBitrateKbps")
    }

    private fun close() = executor.execute {

        Log.d(TAG, "Closing peer connection.")

        dataChannel?.dispose()

        peerConnection.dispose()

        audioSource.dispose()

        try {
            videoCapturer.stopCapture()
        } catch (e: InterruptedException) {
            throw RuntimeException(e)
        }

        videoCapturer.dispose()

        videoSource?.dispose()

        surfaceTextureHelper?.dispose()

        Log.d(TAG, "Closing peer connection factory.")
        factory.dispose()

        rootEglBase.release()

        Log.d(TAG, "Closing peer connection done.")
        //events.onPeerConnectionClosed()
        PeerConnectionFactory.stopInternalTracingCapture()
        PeerConnectionFactory.shutdownInternalTracer()
    }

    fun createOffer() = executor.execute {
        Log.d(TAG, "PC Create OFFER")
        isInitiator = true
        peerConnection.createOffer(sdpObserver, sdpMediaConstraints)
    }

    fun createAnswer() = executor.execute {
        Log.d(TAG, "PC create ANSWER")
        isInitiator = false
        peerConnection.createAnswer(sdpObserver, sdpMediaConstraints)
    }

    fun setRemoteDescription(sdp: SessionDescription) = executor.execute {

        var sdpDescription = sdp.description
        if (preferIsac) {
            sdpDescription = preferCodec(sdpDescription, AUDIO_CODEC_ISAC, true)
        }

        val videoCodecName = when (videoCodec) {
            VIDEO_CODEC_VP8 -> VIDEO_CODEC_VP8
            VIDEO_CODEC_VP9 -> VIDEO_CODEC_VP9
            VIDEO_CODEC_H264_HIGH, VIDEO_CODEC_H264_BASELINE -> VIDEO_CODEC_H264
            else -> VIDEO_CODEC_VP8
        }

        if (isVideoEnabled) {
            sdpDescription = preferCodec(sdpDescription, videoCodecName, false)
        }

        if (audioStartBitrate > 0) {
            sdpDescription = setStartBitrate(AUDIO_CODEC_OPUS, false, sdpDescription, audioStartBitrate)
        }

        val sdpRemote = SessionDescription(sdp.type, sdpDescription)
        peerConnection.setRemoteDescription(sdpObserver, sdpRemote)
    }

    private val sdpObserver: SdpObserver = MySdpObserver()

    private inner class MySdpObserver : SdpObserver {

        */
/** Called on success of Create{Offer,Answer}(). *//*

        override fun onCreateSuccess(origSdp: SessionDescription) {

            var sdpDescription = origSdp.description

            if (preferIsac) {
                sdpDescription = preferCodec(sdpDescription, AUDIO_CODEC_ISAC, true)
            }

            val videoCodecName = when (videoCodec) {
                VIDEO_CODEC_VP8 -> VIDEO_CODEC_VP8
                VIDEO_CODEC_VP9 -> VIDEO_CODEC_VP9
                VIDEO_CODEC_H264_HIGH, VIDEO_CODEC_H264_BASELINE -> VIDEO_CODEC_H264
                else -> VIDEO_CODEC_VP8
            }

            if (isVideoEnabled) {
                sdpDescription = preferCodec(sdpDescription, videoCodecName, false)
            }

            val sdp = SessionDescription(origSdp.type, sdpDescription)

            executor.execute {
                peerConnection.setLocalDescription(sdpObserver, sdp)
            }
        }

        */
/** Called on success of Set{Local,Remote}Description(). *//*

        override fun onSetSuccess() = executor.execute {
            if (isInitiator) {
                // For offering peer connection we first create offer and set
                // local SDP, then after receiving answer set remote SDP.
                if (peerConnection.remoteDescription == null) {
                    // We've just set our local SDP so time to send it.
                    //events.onLocalDescription(localSdp)
                    // appRtcClient!!.sendOfferSdp(sdp)

                } else {
                    // We've just set remote description, so drain remote
                    // and send local ICE candidates.
                    drainCandidates()
                }
            } else {
                // For answering peer connection we set remote SDP and then
                // create answer and set local SDP.
                if (peerConnection.localDescription != null) {
                    // We've just set our local SDP so time to send it, drain
                    // remote and send local ICE candidates.
                    Log.d(TAG, "Local SDP set succesfully")
                    //events.onLocalDescription(localSdp)
                    //appRtcClient!!.sendAnswerSdp(sdp)
                    drainCandidates()
                } else {
                    Log.d(TAG, "Remote SDP set succesfully")
                }
            }

            */
/*
            if (peerConnectionParameters!!.videoMaxBitrate > 0) {
                Log.d(TAG, "Set video maximum bitrate: " + peerConnectionParameters!!.videoMaxBitrate)
                peerConnectionClient!!.setVideoMaxBitrate(peerConnectionParameters!!.videoMaxBitrate)
            }*//*

        }

        override fun onCreateFailure(error: String) = reportError("createSDP error: $error")
        override fun onSetFailure(error: String) = reportError("setSDP error: $error")
    }

    private val pcObserver = object : PeerConnection.Observer {

        override fun onIceCandidate(candidate: IceCandidate) = executor.execute {
            // appRtcClient.sendLocalIceCandidate(candidate);
        }

        override fun onIceCandidatesRemoved(candidates: Array<out IceCandidate>) = executor.execute {
            // appRtcClient.sendLocalIceCandidateRemovals(candidates);
        }

        override fun onIceConnectionChange(iceState: PeerConnection.IceConnectionState) {
            */
/*
            executor.execute(() -> {
                Log.d(TAG, "IceConnectionState: " + newState);
                if (newState == IceConnectionState.CONNECTED) {
                  events.onIceConnected();
                } else if (newState == IceConnectionState.DISCONNECTED) {
                  events.onIceDisconnected();
                } else if (newState == IceConnectionState.FAILED) {
                  reportError("ICE connection failed.");
                }
              });
            *//*

        }

        override fun onDataChannel(dataChan: DataChannel) {
            */
/*
            Log.d(TAG, "New Data channel " + dc.label());

              if (!dataChannelEnabled)
                return;

              dc.registerObserver(new DataChannel.Observer() {
                @Override
                public void onBufferedAmountChange(long previousAmount) {
                  Log.d(TAG, "Data channel buffered amount changed: " + dc.label() + ": " + dc.state());
                }

                @Override
                public void onStateChange() {
                  Log.d(TAG, "Data channel state changed: " + dc.label() + ": " + dc.state());
                }

                @Override
                public void onMessage(final DataChannel.Buffer buffer) {
                  if (buffer.binary) {
                    Log.d(TAG, "Received binary msg over " + dc);
                    return;
                  }
                  ByteBuffer data = buffer.data;
                  final byte[] bytes = new byte[data.capacity()];
                  data.get(bytes);
                  String strData = new String(bytes, Charset.forName("UTF-8"));
                  Log.d(TAG, "Got msg: " + strData + " over " + dc);
                }
              });
            *//*

        }

        override fun onSignalingChange(p0: PeerConnection.SignalingState?) = Unit
        override fun onIceConnectionReceivingChange(p0: Boolean) = Unit
        override fun onIceGatheringChange(p0: PeerConnection.IceGatheringState?) = Unit
        override fun onAddStream(p0: MediaStream?) = Unit
        override fun onRemoveStream(p0: MediaStream?) = Unit
        override fun onRenegotiationNeeded() = Unit
        override fun onAddTrack(p0: RtpReceiver?, p1: Array<out MediaStream>?) = Unit
    }

    private fun createJavaAudioDevice(): AudioDeviceModule {

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
            .setUseHardwareAcousticEchoCanceler(true)
            .setUseHardwareNoiseSuppressor(true)
            .setAudioRecordErrorCallback(audioRecordErrorCallback)
            .setAudioTrackErrorCallback(audioTrackErrorCallback)
            .createAudioDeviceModule()
    }

    private fun drainCandidates() {
        val rc = queuedRemoteCandidates
        if (rc != null) {
            Log.d(TAG, "Add " + rc.size + " remote candidates")
            for (candidate in rc) {
                peerConnection.addIceCandidate(candidate)
            }
            queuedRemoteCandidates = null
        }
    }

    private fun reportError(errMsg: String) {
        executor.execute {
           */
/* if (!isError) {
                events.onPeerConnectionError(errorMessage)
                isError = true
            }*//*

        }
    }

    private fun createVideoCapturer(): VideoCapturer {

        val enumerator = if (Camera2Enumerator.isSupported(appContext)) {
            Camera2Enumerator(appContext)
        } else {
            Camera1Enumerator(true)
        }

        val deviceNames = enumerator.deviceNames

        // First, try to find front facing camera
        Logging.d(TAG, "Looking for front facing cameras.")
        for (deviceName in deviceNames) {
            if (enumerator.isFrontFacing(deviceName)) {
                Logging.d(TAG, "Creating front facing camera capturer.")
                val videoCapturer = enumerator.createCapturer(deviceName, null)

                if (videoCapturer != null) {
                    return videoCapturer
                }
            }
        }

        // Front facing camera not found, try something else
        Logging.d(TAG, "Looking for other cameras.")
        for (deviceName in deviceNames) {
            if (!enumerator.isFrontFacing(deviceName)) {
                Logging.d(TAG, "Creating other camera capturer.")
                val videoCapturer = enumerator.createCapturer(deviceName, null)

                if (videoCapturer != null) {
                    return videoCapturer
                }
            }
        }

        throw IllegalStateException("Camera not found !")
    }

    companion object {

        private const val VIDEO_TRACK_ID = "ARDAMSv0"
        private const val AUDIO_TRACK_ID = "ARDAMSa0"
        private const val VIDEO_TRACK_TYPE = "video"
        private const val TAG = "PCRTCClient"
        private const val VIDEO_CODEC_VP8 = "VP8"
        private const val VIDEO_CODEC_VP9 = "VP9"
        private const val VIDEO_CODEC_H264 = "H264"
        private const val VIDEO_CODEC_H264_BASELINE = "H264 Baseline"
        private const val VIDEO_CODEC_H264_HIGH = "H264 High"
        private const val AUDIO_CODEC_OPUS = "opus"
        private const val AUDIO_CODEC_ISAC = "ISAC"
        private const val VIDEO_CODEC_PARAM_START_BITRATE = "x-google-start-bitrate"
        private const val VIDEO_FLEXFEC_FIELDTRIAL = "WebRTC-FlexFEC-03-Advertised/Enabled/WebRTC-FlexFEC-03/Enabled/"
        private const val VIDEO_VP8_INTEL_HW_ENCODER_FIELDTRIAL = "WebRTC-IntelVP8/Enabled/"
        private const val DISABLE_WEBRTC_AGC_FIELDTRIAL = "WebRTC-Audio-MinimizeResamplingOnMobile/Enabled/"
        private const val AUDIO_CODEC_PARAM_BITRATE = "maxaveragebitrate"
        private const val AUDIO_ECHO_CANCELLATION_CONSTRAINT = "googEchoCancellation"
        private const val AUDIO_AUTO_GAIN_CONTROL_CONSTRAINT = "googAutoGainControl"
        private const val AUDIO_HIGH_PASS_FILTER_CONSTRAINT = "googHighpassFilter"
        private const val AUDIO_NOISE_SUPPRESSION_CONSTRAINT = "googNoiseSuppression"
        private const val DTLS_SRTP_KEY_AGREEMENT_CONSTRAINT = "DtlsSrtpKeyAgreement"
        private const val HD_VIDEO_WIDTH = 1280
        private const val HD_VIDEO_HEIGHT = 720
        private const val BPS_IN_KBPS = 1000
        private const val RTCEVENTLOG_OUTPUT_DIR_NAME = "rtc_event_log"

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

        private fun preferCodec(sdpDescription: String, codec: String, isAudio: Boolean): String {
            val lines = sdpDescription.split("\r\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val mLineIndex = findMediaDescriptionLine(isAudio, lines)
            if (mLineIndex == -1) {
                Log.w(TAG, "No mediaDescription line, so can't prefer $codec")
                return sdpDescription
            }
            // A list with all the payload types with name |codec|. The payload types are integers in the
            // range 96-127, but they are stored as strings here.
            val codecPayloadTypes = ArrayList<String>()
            // a=rtpmap:<payload type> <encoding name>/<clock rate> [/<encoding parameters>]
            val codecPattern = Pattern.compile("^a=rtpmap:(\\d+) $codec(/\\d+)+[\r]?$")
            for (line in lines) {
                val codecMatcher = codecPattern.matcher(line)
                if (codecMatcher.matches()) {
                    codecPayloadTypes.add(codecMatcher.group(1))
                }
            }
            if (codecPayloadTypes.isEmpty()) {
                Log.w(TAG, "No payload types with name $codec")
                return sdpDescription
            }

            val newMLine = movePayloadTypesToFront(codecPayloadTypes, lines[mLineIndex]) ?: return sdpDescription
            Log.d(TAG, "Change media description from: " + lines[mLineIndex] + " to " + newMLine)
            lines[mLineIndex] = newMLine
            return joinString(Arrays.asList(*lines), "\r\n", true */
/* delimiterAtEnd *//*
)
        }

        private fun joinString(s: Iterable<CharSequence>, delimiter: String, delimiterAtEnd: Boolean): String {
            val iter = s.iterator()
            if (!iter.hasNext()) {
                return ""
            }
            val buffer = StringBuilder(iter.next())
            while (iter.hasNext()) {
                buffer.append(delimiter).append(iter.next())
            }
            if (delimiterAtEnd) {
                buffer.append(delimiter)
            }
            return buffer.toString()
        }

        private fun movePayloadTypesToFront(preferredPayloadTypes: List<String>, mLine: String): String? {
            // The format of the media description line should be: m=<media> <port> <proto> <fmt> ...
            val origLineParts = Arrays.asList(*mLine.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray())
            if (origLineParts.size <= 3) {
                Log.e(TAG, "Wrong SDP media description format: $mLine")
                return null
            }
            val header = origLineParts.subList(0, 3)
            val unpreferredPayloadTypes = ArrayList(origLineParts.subList(3, origLineParts.size))
            unpreferredPayloadTypes.removeAll(preferredPayloadTypes)
            // Reconstruct the line with |preferredPayloadTypes| moved to the beginning of the payload
            // types.
            val newLineParts = ArrayList<String>()
            newLineParts.addAll(header)
            newLineParts.addAll(preferredPayloadTypes)
            newLineParts.addAll(unpreferredPayloadTypes)
            return joinString(newLineParts, " ", false */
/* delimiterAtEnd *//*
)
        }

        */
/** Returns the line number containing "m=audio|video", or -1 if no such line exists.  *//*

        private fun findMediaDescriptionLine(isAudio: Boolean, sdpLines: Array<String>): Int {
            val mediaDescription = if (isAudio) "m=audio " else "m=video "
            for (i in sdpLines.indices) {
                if (sdpLines[i].startsWith(mediaDescription)) {
                    return i
                }
            }
            return -1
        }

        private fun setStartBitrate(codec: String, isVideoCodec: Boolean, sdpDescription: String, bitrateKbps: Int): String {
            val lines = sdpDescription.split("\r\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            var rtpmapLineIndex = -1
            var sdpFormatUpdated = false
            var codecRtpMap: String? = null
            // Search for codec rtpmap in format
            // a=rtpmap:<payload type> <encoding name>/<clock rate> [/<encoding parameters>]
            var regex = "^a=rtpmap:(\\d+) $codec(/\\d+)+[\r]?$"
            var codecPattern = Pattern.compile(regex)
            for (i in lines.indices) {
                val codecMatcher = codecPattern.matcher(lines[i])
                if (codecMatcher.matches()) {
                    codecRtpMap = codecMatcher.group(1)
                    rtpmapLineIndex = i
                    break
                }
            }
            if (codecRtpMap == null) {
                Log.w(TAG, "No rtpmap for $codec codec")
                return sdpDescription
            }
            Log.d(TAG, "Found " + codec + " rtpmap " + codecRtpMap + " at " + lines[rtpmapLineIndex])

            // Check if a=fmtp string already exist in remote SDP for this codec and
            // update it with new bitrate parameter.
            regex = "^a=fmtp:$codecRtpMap \\w+=\\d+.*[\r]?$"
            codecPattern = Pattern.compile(regex)
            for (i in lines.indices) {
                val codecMatcher = codecPattern.matcher(lines[i])
                if (codecMatcher.matches()) {
                    Log.d(TAG, "Found " + codec + " " + lines[i])
                    if (isVideoCodec) {
                        lines[i] += "; $VIDEO_CODEC_PARAM_START_BITRATE=$bitrateKbps"
                    } else {
                        lines[i] += "; " + AUDIO_CODEC_PARAM_BITRATE + "=" + bitrateKbps * 1000
                    }
                    Log.d(TAG, "Update remote SDP line: " + lines[i])
                    sdpFormatUpdated = true
                    break
                }
            }

            val newSdpDescription = StringBuilder()
            for (i in lines.indices) {
                newSdpDescription.append(lines[i]).append("\r\n")
                // Append new a=fmtp line if no such line exist for a codec.
                if (!sdpFormatUpdated && i == rtpmapLineIndex) {
                    val bitrateSet: String
                    if (isVideoCodec) {
                        bitrateSet = "a=fmtp:$codecRtpMap $VIDEO_CODEC_PARAM_START_BITRATE=$bitrateKbps"
                    } else {
                        bitrateSet = ("a=fmtp:" + codecRtpMap + " " + AUDIO_CODEC_PARAM_BITRATE + "="
                                + bitrateKbps * 1000)
                    }
                    Log.d(TAG, "Add remote SDP line: $bitrateSet")
                    newSdpDescription.append(bitrateSet).append("\r\n")
                }
            }
            return newSdpDescription.toString()
        }
    }
}*/
