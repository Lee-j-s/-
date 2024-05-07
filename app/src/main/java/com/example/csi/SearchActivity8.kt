package com.example.csi

import Item
import ItemAdapter
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.csi.R
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.nio.charset.Charset
import android.widget.Button

class SearchActivity8 : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ItemAdapter
    private lateinit var searchEditText: EditText
    private lateinit var itemList: MutableList<Item>
    private lateinit var filteredList: MutableList<Item>

    private lateinit var btnBelow1500: Button
    private lateinit var btn1500To3000: Button
    private lateinit var btnAbove5000: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        recyclerView = findViewById(R.id.recyclerView)
        searchEditText = findViewById(R.id.searchEditText)
        recyclerView.layoutManager = LinearLayoutManager(this)

        btnBelow1500 = findViewById(R.id.btnBelow1500)
        btn1500To3000 = findViewById(R.id.btn1500To3000)
        btnAbove5000 = findViewById(R.id.btnAbove5000)

        val json: String = loadJSONFromAsset("two plus.json") ?: ""

        val jsonArray = JSONArray(json)

        itemList = mutableListOf()
        for (i in 0 until jsonArray.length()) {
            val jsonObject: JSONObject = jsonArray.getJSONObject(i)
            val item = Item(
                jsonObject.getInt("no"),
                jsonObject.getString("name"),
                jsonObject.getInt("price"),
                jsonObject.getString("photo")
            )
            itemList.add(item)
        }

        filteredList = ArrayList(itemList)
        adapter = ItemAdapter(filteredList)
        recyclerView.adapter = adapter

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filter(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun afterTextChanged(s: Editable?) {}
        })

        btnBelow1500.setOnClickListener { filterByPriceRange(0, 1500) }
        btn1500To3000.setOnClickListener { filterByPriceRange(1500, 4999) }
        btnAbove5000.setOnClickListener { filterByPriceRange(5000, Int.MAX_VALUE) }
    }

    private fun filter(text: String) {
        filteredList.clear()
        if (text.isEmpty()) {
            filteredList.addAll(itemList)
        } else {
            for (item in itemList) {
                if (item.name.contains(text, ignoreCase = true)) {
                    filteredList.add(item)
                }
            }
        }
        adapter.notifyDataSetChanged()
    }

    private fun filterByPriceRange(minPrice: Int, maxPrice: Int) {
        filteredList.clear()
        for (item in itemList) {
            if (item.price in minPrice..maxPrice) {
                filteredList.add(item)
            }
        }
        adapter.notifyDataSetChanged()
    }

    private fun loadJSONFromAsset(fileName: String): String? {
        var json: String? = null
        try {
            val inputStream = assets.open(fileName)
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            json = String(buffer, Charset.forName("UTF-8"))
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return json
    }
}
