package com.schmitten.nightminton

import android.content.Intent
import android.nfc.NfcAdapter
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.app.PendingIntent
import kotlinx.android.synthetic.main.activity_main.*
import android.util.Log

class MainActivity : AppCompatActivity() {
    private val ACTION_NDEF_DISCOVERED = "android.nfc.action.TAG_DISCOVERED"

    private var nfcAdapter : NfcAdapter? = null
    private var nfcPendingIntent: PendingIntent? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        nfcPendingIntent = PendingIntent.getActivity(this, 0,
                Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0)

        if (intent != null) {
            processIntent(intent)
        }
    }

    override fun onResume() {
        super.onResume()

        nfcAdapter?.enableForegroundDispatch(this, nfcPendingIntent, null, null);
    }

    override fun onPause() {
        super.onPause()

        nfcAdapter?.disableForegroundDispatch(this);
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        Log.d("Found intent in onNewIntent", intent?.action.toString())

        if (intent != null) processIntent(intent)
    }

    private fun processIntent(checkIntent: Intent) {

        if (checkIntent.action == this.ACTION_NDEF_DISCOVERED) {
            var id: String
            id = this.ByteArrayToHexString(checkIntent.getByteArrayExtra(NfcAdapter.EXTRA_ID))

            Log.d("New NDEF id", id)
            tv_text.text = id

            val intent = Intent(this, PointActivity::class.java).apply {
                putExtra("Person", "Hallo")
            }
            startActivity(intent)
        }
    }

    // Converting byte[] to hex string:
    private fun ByteArrayToHexString(inarray: ByteArray): String {
        var i: Int
        var j: Int
        var `in`: Int
        val hex = arrayOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F")
        var out = ""
        j = 0
        while (j < inarray.size) {
            `in` = inarray[j].toInt() and 0xff
            i = `in` shr 4 and 0x0f
            out += hex[i]
            i = `in` and 0x0f
            out += hex[i]
            ++j
        }
        return out
    }

    private fun checkCompatibility() {
        if(nfcAdapter == null){
            tv_text.text = "NFC nicht unterstützt"
        }

        if(nfcAdapter?.isEnabled == false){
            tv_text.text = "NFC nicht aktiviert"
        }
    }
}
