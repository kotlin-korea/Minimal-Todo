import android.content.Context
import com.example.avjindersinghsekhon.minimaltodo.R

/**
 * Created by jeonghyeonji on 2017. 7. 19..
 */
class Utils {

    companion object {
        fun getToolbarHeight(context: Context): Int {
            val styledAttributte = context.theme.obtainStyledAttributes(
                    intArrayOf(R.attr.actionBarSize)
            )
            val toolbarHeight = styledAttributte.getDimension(0, 0F) as Int
            styledAttributte.recycle()

            return toolbarHeight
        }
    }
}