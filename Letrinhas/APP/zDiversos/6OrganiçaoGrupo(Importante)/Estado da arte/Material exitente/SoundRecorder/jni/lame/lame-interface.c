/* Lame.java
   A port of LAME for Android

   Copyright (c) 2010 Ethan Chen

   This library is free software; you can redistribute it and/or
   modify it under the terms of the GNU Lesser General Public
   License as published by the Free Software Foundation; either
   version 2 of the License, or (at your option) any later version.

   This library is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   Lesser General Public License for more details.

   You should have received a copy of the GNU Lesser General Public
   License along with this library; if not, write to the Free Software
   Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
/*****************************************************************************/
#include "lame-interface.h"
#include "lame.h"
#include <stdlib.h>
#include <string.h>
#include <math.h>
#include <stdio.h>
#include <android/log.h>

static lame_global_flags *lame_context;
static hip_t hip_context;
static mp3data_struct *mp3data;
static int enc_delay, enc_padding;

JNIEXPORT jint JNICALL Java_com_android_misoundrecorder_Lame_initEncoder
  (JNIEnv *env, jclass class, jint bitRate, jint quality, jint sampleRate, jint numChannels, jfloat scale, jint isOGG)
{
  if (!lame_context) {
    lame_context = lame_init();
    if (lame_context) {
    
    	lame_set_scale(lame_context, scale);
    	//lame_set_ReplayGain_input(lame_context, gain);
   		
   		lame_set_ogg(lame_context, isOGG);
    	
	  // set to vbr_mtrh for fast, vbr_rh for slower
	  /*switch (preset) {
		case net_sourceforge_lame_Lame_LAME_PRESET_MEDIUM:
		  lame_set_VBR_q(lame_context, 4);
		  lame_set_VBR(lame_context, vbr_rh);
		  break;
		case net_sourceforge_lame_Lame_LAME_PRESET_STANDARD:
		  lame_set_VBR_q(lame_context, 2);
		  lame_set_VBR(lame_context, vbr_rh);
		  break;
		case net_sourceforge_lame_Lame_LAME_PRESET_EXTREME:
		  lame_set_VBR_q(lame_context, 0);
		  lame_set_VBR(lame_context, vbr_rh);
		  break;
		case net_sourceforge_lame_Lame_LAME_PRESET_DEFAULT:
		default:
		  break;
	  }*/

      lame_set_brate(lame_context, bitRate);
      lame_set_quality(lame_context, quality);
      lame_set_in_samplerate(lame_context, sampleRate);
      lame_set_num_channels(lame_context, numChannels);
      int ret = lame_init_params(lame_context);
      __android_log_print(ANDROID_LOG_DEBUG, "liblame.so", "initialized lame with code %d", ret);
      return ret;
    }
  }
  return -1;
}

JNIEXPORT jint JNICALL Java_com_android_misoundrecorder_Lame_encode
  (JNIEnv *env, jclass class, jshortArray leftChannel, jshortArray rightChannel,
		  jint channelSamples, jbyteArray mp3Buffer, jint bufferSize)
{
  int encoded_samples;
  short *left_buf, *right_buf;
  unsigned char *mp3_buf;

  left_buf = (*env)->GetShortArrayElements(env, leftChannel, NULL);
  right_buf = (*env)->GetShortArrayElements(env, rightChannel, NULL);
  mp3_buf = (*env)->GetByteArrayElements(env, mp3Buffer, NULL);

  encoded_samples = lame_encode_buffer(lame_context, left_buf, right_buf, channelSamples, mp3_buf, bufferSize);

  // mode 0 means free left/right buf, write changes back to left/rightChannel
  (*env)->ReleaseShortArrayElements(env, leftChannel, left_buf, 0);
  (*env)->ReleaseShortArrayElements(env, rightChannel, right_buf, 0);

  if (encoded_samples < 0) {
    // don't propagate changes back up if we failed
    (*env)->ReleaseByteArrayElements(env, mp3Buffer, mp3_buf, JNI_ABORT);
    return -1;
  }

  (*env)->ReleaseByteArrayElements(env, mp3Buffer, mp3_buf, 0);
  return encoded_samples;
}


JNIEXPORT jint JNICALL Java_com_android_misoundrecorder_Lame_flushEncoder
  (JNIEnv *env, jclass class, jbyteArray mp3Buffer, jint bufferSize)
{
  // call lame_encode_flush when near the end of pcm buffer
  int num_bytes;
  unsigned char *mp3_buf;

  mp3_buf = (*env)->GetByteArrayElements(env, mp3Buffer, NULL);

  num_bytes = lame_encode_flush(lame_context, mp3_buf, bufferSize);
  if (num_bytes < 0) {
    // some kind of error occurred, don't propagate changes to buffer
	(*env)->ReleaseByteArrayElements(env, mp3Buffer, mp3_buf, JNI_ABORT);
    return num_bytes;
  }

  (*env)->ReleaseByteArrayElements(env, mp3Buffer, mp3_buf, 0);
  return num_bytes;
}


JNIEXPORT jint JNICALL Java_com_android_misoundrecorder_Lame_closeEncoder
  (JNIEnv *env, jclass class)
{
  if (lame_context) {
    int ret = lame_close(lame_context);
    lame_context = NULL;
    __android_log_print(ANDROID_LOG_DEBUG, "liblame.so", "freed lame with code %d", ret);
    return ret;
  }
  return -1;
}
