package com.example.download.main


import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.DOWNLOAD_SERVICE
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.download.Notifications.sendNotification
import com.example.download.R
import com.example.download.custom.ButtonState
import com.example.download.databinding.FragmentMainBinding


class MainFragment : Fragment() {
    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager

    private  lateinit var url :String
    private lateinit var file: String
    private lateinit var status: String
//    private lateinit var downloadManager: DownloadManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //Create a notification channel
        createChannel(getString(R.string.download_channel_id), getString(R.string.download_channel_name))

        //Init notification Manager
        notificationManager = activity!!.getSystemService(NotificationManager::class.java) as NotificationManager

        //Binding
        val binding: FragmentMainBinding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_main,container,false)
        binding.lifecycleOwner = this


        with(binding){
            /* Set on click listener to get the value of the selected url*/
            linksList.setOnCheckedChangeListener(object : RadioGroup.OnCheckedChangeListener{
                override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
                    val radioButton = linksList.findViewById<View>(checkedId)
                    when (linksList.indexOfChild(radioButton)) {
                        0 -> {
                            url = "https://github.com/bumptech/glide"
                            file = context?.getString(R.string.glide) ?: ""
                        }

                        1 -> {
                            url = "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
                            file = context?.getString(R.string.load) ?: ""
                        }
                        2 ->  {
                            url = "https://github.com/square/retrofit"
                            file = context?.getString(R.string.retrofit) ?: ""
                        }
                    }
                }
            })
            loadingButton.setOnClickListener{
                try{
                    if (::url.isInitialized) {
                        loadingButton.updateButtonState(ButtonState.Clicked)
                        download()
                    } else {
                        Toast.makeText(activity, getString(R.string.select), Toast.LENGTH_SHORT).show()
                    }
                }catch (e:Exception){
                    loadingButton.updateButtonState(ButtonState.Clicked)
                    Toast.makeText(activity, getString(R.string.network_error), Toast.LENGTH_SHORT).show()
                }


            }

        }
        return binding.root
    }
    override fun onStart() {
        activity!!.registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        super.onStart()
    }

    override fun onStop() {
        activity!!.unregisterReceiver(receiver)
        super.onStop()
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (id == downloadID) {
                checkDownloadStatus()

                getContext()?.let { notificationManager.sendNotification(file,status,it) }
            }
        }
    }

    private fun download() {
        val request =
            DownloadManager.Request(Uri.parse(url))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager =  activity!!.getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
    }
    private fun checkDownloadStatus() {
        val downloadManager = activity!!.getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        val cursor = downloadManager.query(DownloadManager.Query().setFilterById(downloadID))
        if (cursor.moveToFirst()) {
            val statusId = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
            when (statusId) {
                DownloadManager.STATUS_FAILED -> status = "FAILED"
                DownloadManager.STATUS_SUCCESSFUL -> status = "SUCCESS"
                else -> status = "IN PROGRESS"
            }
        }

    }

    private fun createChannel(channelId: String, channelName: String) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val notificationChannel = NotificationChannel(
                channelId, channelName,
                NotificationManager.IMPORTANCE_HIGH
            ).apply { setShowBadge(false) }

            notificationChannel.description = getString(R.string.notification_channel_description)

            val notificationManager = activity!!.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)
        }

    }
}
