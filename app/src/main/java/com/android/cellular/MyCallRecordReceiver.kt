package com.android.cellular

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import java.util.Date
import com.aykuttasil.callrecord.CallRecord
import com.aykuttasil.callrecord.receiver.CallRecordReceiver

class MyCallRecordReceiver(callRecord: CallRecord) : CallRecordReceiver(callRecord) {





    override fun onIncomingCallReceived(context: Context, number: String?, start: Date) {
        super.onIncomingCallReceived(context, number, start)

        Toast.makeText(context,"done",Toast.LENGTH_LONG).show()

        Log.d("MyCallRecordReceiver onIncomingCallReceived",start.toString())
    }

    override fun onIncomingCallAnswered(context: Context, number: String?, start: Date) {
        super.onIncomingCallAnswered(context, number, start)
        Toast.makeText(context,"done",Toast.LENGTH_LONG).show()
        Log.d("MyCallRecordReceiver onIncomingCallAnswered",start.toString())
    }

    override fun onIncomingCallEnded(context: Context, number: String?, start: Date, end: Date) {
        super.onIncomingCallEnded(context, number, start, end)
        Toast.makeText(context,"done",Toast.LENGTH_LONG).show()
        Log.d("MyCallRecordReceiver onIncomingCallEnded",start.toString())
    }

    override fun onMissedCall(context: Context, number: String?, start: Date) {
        super.onMissedCall(context, number, start)
        Toast.makeText(context,"done",Toast.LENGTH_LONG).show()
        Log.d("MyCallRecordReceiver onMissedCall",start.toString())
    }

    override fun onOutgoingCallEnded(context: Context, number: String?, start: Date, end: Date) {
        super.onOutgoingCallEnded(context, number, start, end)
        Toast.makeText(context,"done",Toast.LENGTH_LONG).show()
        Log.d("MyCallRecordReceiver onOutgoingCallEnded",start.toString())
    }

    override fun onOutgoingCallStarted(context: Context, number: String?, start: Date) {
        super.onOutgoingCallStarted(context, number, start)
        Toast.makeText(context,"done",Toast.LENGTH_LONG).show()
        Log.d("MyCallRecordReceiver onOutgoingCallStarted",start.toString())
    }

}