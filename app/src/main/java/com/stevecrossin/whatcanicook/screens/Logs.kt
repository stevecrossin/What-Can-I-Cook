package com.stevecrossin.whatcanicook.screens

import android.annotation.SuppressLint
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.Button
import com.stevecrossin.whatcanicook.R
import com.stevecrossin.whatcanicook.adapter.LogsAdapter
import com.stevecrossin.whatcanicook.entities.LogRecords
import com.stevecrossin.whatcanicook.roomdatabase.AppDataRepo
import java.util.*

/**
 * Log class, which controls the log view and displaying of entries that exist in the log database.
 */
class Logs : AppCompatActivity() {
    private var logsAdapter: LogsAdapter? = null
    internal var logsRepo: AppDataRepo

    /**
     * On creation of the activity, perform these functions.
     * Set the current view as the activity_log XML and load the UI elements in that XML file into that view.
     *
     *
     * Initialise an instance of the AppDataRepo, and call the initRecyclerItems method
     * Set an onClickListener to the clearLogs button which when clicked will call the AppRepo function clearLog, and refresh the content
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log)
        logsRepo = AppDataRepo(this)
        initialiseRecyclerItems()

        val button = findViewById<Button>(R.id.clearLogs)
        button.setOnClickListener {
            logsRepo.clearLog()
            refreshContent()
        }
    }

    /**
     * Performs the setup for the recyclerView. The method will:
     * 1. Find the recyclerView in the layout, with the ID being logs_list.
     * 2. Set the layout manager as a LinerarLayout manager with elements in vertical order, without a fixed size.
     * 3. Set up the adapter for the recycler view as logsAdapter
     * 4. Call the refreshContent method
     */
    private fun initialiseRecyclerItems() {
        val logsList = findViewById<RecyclerView>(R.id.logView)
        logsList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        logsList.setHasFixedSize(false)
        logsAdapter = LogsAdapter(ArrayList())
        logsList.adapter = logsAdapter
        refreshContent()
    }

    /**
     * Handles refresh of the recyclerview. Performs a database query in the background, adds all the logs from log database with the getLogs method.
     * and updates the recyclerview with the content.
     *
     *
     * After this is complete, the logs adapter will be updated with the updateLogs db operation.
     */
    @SuppressLint("StaticFieldLeak")
    private fun refreshContent() {

        object : AsyncTask<Void, Void, ArrayList<LogRecords>>() {
            override fun doInBackground(vararg voids: Void): ArrayList<LogRecords> {
                return ArrayList(logsRepo.logs)
            }

            override fun onPostExecute(logs: ArrayList<LogRecords>) {
                super.onPostExecute(logs)
                logsAdapter!!.updateLogs(logs)
            }
        }.execute()
    }

    /**
     * This method will refresh the content of the view when the activity is resumed.
     */
    override fun onResume() {
        super.onResume()
        refreshContent()
    }

    /**
     * This is an OnClick method that is called when the "Home" icon is clicked in the activity. It will load the MainActivity.class, and then start that activity.
     */
    fun navigateHome(view: View) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

}