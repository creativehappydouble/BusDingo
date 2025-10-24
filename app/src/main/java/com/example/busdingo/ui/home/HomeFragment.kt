package com.example.busdingo.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.busdingo.BuildConfig
import com.example.busdingo.R
import com.example.busdingo.data.StopMonitoringRepository
import com.example.busdingo.network.SiriApi
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val inputStop = view.findViewById<EditText>(R.id.inputStopCode)
        val inputLine = view.findViewById<EditText>(R.id.inputLine)
        val btnFetch = view.findViewById<Button>(R.id.btnFetch)
        val txt = view.findViewById<TextView>(R.id.txtResult)

        val key = BuildConfig.METRO_API_KEY
        val api = SiriApi.create(key)
        val repo = StopMonitoringRepository(api)

        btnFetch.setOnClickListener {
            val stopCode = inputStop.text.toString().trim()
            val line = inputLine.text.toString().trim()

            if (stopCode.isEmpty() || line.isEmpty()) {
                txt.text = getString(R.string.msg_input_required)//"请先输入 stopcode 和 line"
                return@setOnClickListener
            }
            if (key.isBlank()) {
                txt.text = getString(R.string.msg_api_key_missing) //"API Key 未配置，请检查 BuildConfig.METRO_API_KEY"
                return@setOnClickListener
            }

            txt.text = getString(R.string.msg_loading) //"查询中…"
            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    val list = repo.getEtaForLine(stopCode, line, maxVisits = 10)
                    if (list.isEmpty()) {
                        txt.text = getString(R.string.msg_no_services)//"当前站点/线路暂无班次（可能已收车或线路不经此站）"
                    } else {
                        val first = list.first()

                        val etaShown = first.etaMinutes?.let { if (it < 0) 0.0 else it }?.toString() ?: "-"
                        val stopShown = first.stopName ?: "-"
                        val destShown = first.destination ?: "-"
                        val expectedShown = first.expectedArrival ?: "-"

                        val s = buildString {
                            appendLine("${getString(R.string.label_line)}: ${first.line}")
                            appendLine("${getString(R.string.label_stop)}: $stopShown")
                            appendLine("${getString(R.string.label_dest)}: $destShown")
                            appendLine("${getString(R.string.label_eta)}: $etaShown")
                            appendLine("${getString(R.string.label_expected_arrival)}: $expectedShown")
                        }
                        txt.text = s
                    }
                } catch (e: Exception) {
                    txt.text = "查询失败：${e.message}"
                }
            }
        }
    }
}
