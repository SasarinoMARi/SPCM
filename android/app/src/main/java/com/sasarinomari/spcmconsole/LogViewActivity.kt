package com.sasarinomari.spcmconsole

import android.annotation.SuppressLint
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.sasarinomari.spcmconsole.results.LogResult
import kotlinx.android.synthetic.main.activity_log_view.*
import kotlinx.android.synthetic.main.item_log.view.*
import java.text.SimpleDateFormat
import java.util.*

class LogViewActivity : AppCompatActivity() {
    private val api = object : APICall(this) {
        override fun onError(message: String) {
            Toast.makeText(this@LogViewActivity, message, Toast.LENGTH_LONG).show()
        }

        override fun onMessage(message: String) {
            onError(message)
        }
    }

    private var adapter = LogAdapter()
    private var logLevel = 0
    private var page = 0
    private var lastItemVisibleFlag = false
    private var mLockListView = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_view)

        listview.adapter = adapter

        val logLevels = arrayOf("Verbose", "Debug", "Info", "Warning", "Error", "Critical")
        logs_level_selector.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, logLevels)
        logs_level_selector.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, i: Int, l: Long) {
                logLevel = i
                page = 0
                adapter.clear()
                updateLogs()
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) { }
        }

        listview.setOnScrollListener(object: AbsListView.OnScrollListener {
            override fun onScrollStateChanged(absListView: AbsListView?, scrollState: Int) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && lastItemVisibleFlag && !mLockListView) {
                    page++
                    updateLogs()
                }
            }

            override fun onScroll(absListView: AbsListView?, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
                lastItemVisibleFlag = (totalItemCount > 0) && (firstVisibleItem + visibleItemCount >= totalItemCount);
            }
        })

        // initializeAdapter()
    }

    private fun updateLogs() {
        mLockListView = true
        api.getLogs(logLevel, page) { results ->
            adapter.append(results.toList())
            adapter.notifyDataSetChanged()
            mLockListView = false
        }
    }
}

class LogAdapter : BaseAdapter() {
    private val df = SimpleDateFormat("yyyy-MM-dd EEEE hh:mm:ss", Locale.getDefault())
    private val items = ArrayList<LogResult>()

    fun clear() {
        items.clear()
    }

    fun append(new: List<LogResult>) {
        items.addAll(new)
    }

    override fun getCount(): Int = items.size
    override fun getItem(position: Int): LogResult = items[position]
    override fun getItemId(position: Int): Long = position.toLong()
    @SuppressLint("SetTextI18n")
    override fun getView(position: Int, view: View?, parent: ViewGroup?): View {
        var convertView = view
        if (convertView == null) convertView = LayoutInflater.from(parent?.context).inflate(R.layout.item_log, parent, false)!!

        val item: LogResult = getItem(position)
        val level = LogLevel(item.level)
        val color = level.getColor()

        convertView.log_level.text = "$level in "
        convertView.log_level.setTextColor(color)

        convertView.log_subject.text = item.subject
        convertView.log_subject.setTextColor(color)


        convertView.log_content.text = item.content
        convertView.log_content.setTextColor(color)


        convertView.log_createdAt.text = convertWeekday(df.format(item.createdAt))
        convertView.log_createdAt.setTextColor(color)

        if(item.from != null) {
            convertView.log_from.visibility = View.VISIBLE
            convertView.log_from.text = item.from
            convertView.log_from.setTextColor(color)
        }
        else convertView.log_from.visibility = View.GONE


        return convertView
    }

    private fun convertWeekday(dateString: String): String {
        return dateString
            .replace("Monday", "월요일")
            .replace("Tuesday", "화요일")
            .replace("Wednesday", "수요일")
            .replace("Thursday", "목요일")
            .replace("Friday", "금요일")
            .replace("Saturday", "토요일")
            .replace("Sunday", "일요일")
    }

    private class LogLevel(private val level: Int) {
        private class LogColors {
            val verbose = Color.parseColor("#555555")
            val debug = Color.parseColor("#6A98B9")
            val info = Color.parseColor("#6A855A")
            val warning = Color.parseColor("#BC7739")
            val error = Color.parseColor("#FF6B68")
            val critical = Color.parseColor("#9777A9")
        }

        companion object {
            val colors = LogColors()
        }

        override fun toString(): String {
            return when(level) {
                0 -> "Verbose"
                1 -> "Debug"
                2 -> "Info"
                3 -> "Warning"
                4 -> "Error"
                else -> "Critical"
            }
        }

        fun getColor(): Int {
            return when(level) {
                0 -> colors.verbose
                1 -> colors.debug
                2 -> colors.info
                3 -> colors.warning
                4 -> colors.error
                else -> colors.critical
            }
        }
    }
}