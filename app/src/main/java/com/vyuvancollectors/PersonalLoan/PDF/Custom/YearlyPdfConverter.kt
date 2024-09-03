package com.vyuvancollectors.PersonalLoan.PDF.Custom

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.pdf.PdfDocument
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import com.vyuvancollectors.BuildConfig


import com.vyuvancollectors.R
import com.vyuvancollectors.databinding.ActivityYearlyPdfConverterBinding
import java.io.File
import java.io.FileOutputStream
import java.util.*

class YearlyPdfConverter(val list: ArrayList<DataForYearlyPdf>, private val emiList : ArrayList<PdfYearlyEmiData>) : AppCompatActivity() {

    private var binding : ActivityYearlyPdfConverterBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityYearlyPdfConverterBinding.inflate(layoutInflater)

        setContentView(binding?.root)
    }

    private fun createBitmapFromView(context: Context, view: View, adapter: PdfYearlyEMIRecyclerView, activity: Activity, ): Bitmap {

        val position : Int = 0

        val nameTxt = view.findViewById<TextView>(R.id.name_txt)
        val mobileTxt = view.findViewById<TextView>(R.id.mobile_txt)
        val collectionTypeTxt = view.findViewById<TextView>(R.id.collectionType_txt)
        val totalInterestTxt = view.findViewById<TextView>(R.id.totalInterest_txt)
        val loanAmountTxt = view.findViewById<TextView>(R.id.loanAmount_txt)
        val totalAmountTxt = view.findViewById<TextView>(R.id.totalAmount_txt)
        val disburseDateTxt = view.findViewById<TextView>(R.id.disburseDate_txt)
        val collectionAmountTxt = view.findViewById<TextView>(R.id.collectionAmount_txt)

        nameTxt.text = list[position].name
        mobileTxt.text = list[position].mobile
        collectionTypeTxt.text = list[position].collectionType
        totalInterestTxt.text = list[position].totalInterest
        loanAmountTxt.text = list[position].loanAmount
        totalAmountTxt.text = list[position].totalAmount
        disburseDateTxt.text = list[position].disburseDate
        collectionAmountTxt.text = list[position].collectionAmount


        val recyclerView = view.findViewById<RecyclerView>(R.id.pdf_emi_rv)
        recyclerView.adapter = adapter
        return createBitmap(context, view, activity)
    }

    private fun createBitmap(context: Context, view: View, activity: Activity, ): Bitmap {
        val displayMetrics = DisplayMetrics()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            context.display?.getRealMetrics(displayMetrics)
            displayMetrics.densityDpi
        } else {
            activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
        }
        view.measure(
            View.MeasureSpec.makeMeasureSpec(
                displayMetrics.widthPixels, View.MeasureSpec.EXACTLY
            ),
            View.MeasureSpec.makeMeasureSpec(
                displayMetrics.heightPixels, View.MeasureSpec.EXACTLY
            )
        )
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels)
        val bitmap = Bitmap.createBitmap(
            view.measuredWidth,
            view.measuredHeight, Bitmap.Config.ARGB_8888
        )

        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return Bitmap.createScaledBitmap(bitmap, 595, 842, true)
    }

    private fun convertBitmapToPdf(bitmap: Bitmap, context: Context) {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(bitmap.width, bitmap.height, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        page.canvas.drawBitmap(bitmap, 0F, 0F, null)
        pdfDocument.finishPage(page)
        val filePath = File(context.getExternalFilesDir(null), "VYUVANCollector.pdf")
        pdfDocument.writeTo(FileOutputStream(filePath))
        pdfDocument.close()
        renderPdf(context, filePath)
    }

//    fun createPdf(context: Context, activity: Activity) {
//        val inflater = LayoutInflater.from(context)
//        val view = inflater.inflate(R.layout.activity_yearly_pdf_converter, null)
//
//        val adapter = PdfYearlyEMIRecyclerView(emiList)
//        Log.e("Urvashi","${emiList.size}")
//        val bitmap = createBitmapFromView(context, view, adapter, activity)
//        convertBitmapToPdf(bitmap, activity)
//    }


    private fun renderPdf(context: Context, filePath: File) {
        val uri = FileProvider.getUriForFile(
            Objects.requireNonNull(context),
            BuildConfig.APPLICATION_ID + ".provider", filePath)
        val intent = Intent(Intent.ACTION_VIEW)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.setDataAndType(uri, "application/pdf")

        try {
            context.startActivity(intent)
        } catch (_: ActivityNotFoundException) {

        }
    }
}