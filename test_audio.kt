import android.media.AudioRecord
import android.content.Context
fun test(context: Context) {
    val builder = AudioRecord.Builder()
    builder.setContext(context)
}
