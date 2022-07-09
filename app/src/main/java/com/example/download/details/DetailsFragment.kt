package com.example.download.details

import android.app.DownloadManager
import android.content.Intent.getIntent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.example.download.R
import com.example.download.databinding.FragmentDetailsBinding
import com.example.download.main.MainFragmentDirections


class DetailsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val file = DetailsFragmentArgs.fromBundle(arguments!!).file
        val status = DetailsFragmentArgs.fromBundle(arguments!!).status


        Log.i("DEUBG","HERE")
        //Binding
        val binding: FragmentDetailsBinding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_details,container,false)
        with(binding){
            text.text = file
            statusTxt.text = status
            when (status) {
                "FAILED" -> statusTxt.setTextColor(Color.RED)
                "SUCCESS" -> statusTxt.setTextColor(Color.GREEN)
                else -> statusTxt.setTextColor(Color.YELLOW)
            }

        }
        binding.button.setOnClickListener{
            this.findNavController().navigate(DetailsFragmentDirections.actionDetailsFragmentToMainFragment())
        }

        return binding.root
    }
}