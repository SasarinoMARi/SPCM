package sasarinomari.genericdatahelper

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.google.gson.JsonObject
import java.util.ArrayList

/**
 * viewResourceID : 어떤 뷰 리소스를 사용할지 그 아이디를 설정합니다.
 * draw : 데이터를 뷰에 어떻게 그릴지 정의하는 함수입니다.
 */
class DataAdapter(private val viewResourceID :Int,
                  private val draw:(View, JsonObject)->Unit) : BaseAdapter() {
    private val items = ArrayList<JsonObject>()

    fun clear() {
        items.clear()
    }

    fun append(new: List<JsonObject>) {
        items.addAll(new)
    }

    override fun getCount(): Int = items.size
    override fun getItem(position: Int): JsonObject = items[position]
    override fun getItemId(position: Int): Long = position.toLong()
    override fun getView(position: Int, view: View?, parent: ViewGroup?): View? {
        val convertView = view?: LayoutInflater.from(parent?.context)
            .inflate(viewResourceID, parent, false) ?: return null
        val item = getItem(position)
        draw(convertView, item)
        return convertView
    }
}