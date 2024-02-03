package com.project.tex.utils.pdf

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.io.*

class PdfUtils(val context: Context?, private var filePath: String = "") {
    private var mListener: PDFListener? = null

    /**
     * The filename of the PDF.
     */
    private val FILENAME = "sample.pdf"

    /**
     * Key string for saving the state of current page index.
     */
    private val STATE_CURRENT_PAGE_INDEX = "current_page_index"

    /**
     * String for logging.
     */
    private val TAG = "PdfUtils"

    /**
     * The initial page index of the PDF.
     */
    private val INITIAL_PAGE_INDEX = 0

    /**
     * File descriptor of the PDF.
     */
    private lateinit var fileDescriptor: ParcelFileDescriptor

    /**
     * [PdfRenderer] to render the PDF.
     */
    private lateinit var pdfRenderer: PdfRenderer

    /**
     * Page that is currently shown on the screen.
     */
    private lateinit var currentPage: PdfRenderer.Page

    /**
     * PDF page index.
     */
    private var pageIndex: Int = INITIAL_PAGE_INDEX
    private val listOfBitmap: MutableList<Bitmap> = mutableListOf()
    private var compositeDisposable = CompositeDisposable()

    /**
     * Sets up a [PdfRenderer] and related resources.
     */
    init {

    }

    fun loadFile(listener: PDFListener?) {
        mListener = listener
        if (context != null) {
            if (listOfBitmap.isEmpty()) compositeDisposable.add(Observable.just(filePath)
                .subscribeOn(Schedulers.computation())
                .flatMap { fileNames ->
                    // In this sample, we read a PDF from the assets directory.
                    val file = File(filePath)
                    if (!file.exists()) {
                        // Since PdfRenderer cannot handle the compressed asset file directly, we copy it into
                        // the cache directory.
                        val asset = context.assets.open(FILENAME)
                        val output = FileOutputStream(file)
                        val buffer = ByteArray(1024)
                        var size = asset.read(buffer)
                        while (size != -1) {
                            output.write(buffer, 0, size)
                            size = asset.read(buffer)
                        }
                        asset.close()
                        output.close()
                    }
                    return@flatMap Observable.just(file)
                }.flatMap { file ->
                    fileDescriptor =
                        ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
                    return@flatMap Observable.just(fileDescriptor)
                }.map { descriptor ->
                    // This is the PdfRenderer we use to render the PDF.
                    pdfRenderer = PdfRenderer(descriptor)
                    pdfRenderer
                }.observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe({
                    mListener?.loaded()
                }, { e ->
                    mListener?.fail(e)
                }, {
                    mListener?.complete()
                })
            )
            else {
                mListener?.complete()
            }
        }
    }

    /**
     * Closes the [PdfRenderer] and related resources.
     *
     * @throws IOException When the PDF file cannot be closed.
     */
    @Throws(IOException::class)
    fun closeRenderer() {
        currentPage.close()
        pdfRenderer.close()
        fileDescriptor.close()
    }

    /**
     * Shows the specified page of PDF to the screen.
     *
     * @param index The page index.
     */
    public fun renderAllPages(pageReadyListener: PageReadyListener) {
//        if (pdfRenderer.pageCount <= index) return
        if (!listOfBitmap.isEmpty()) {
            pageReadyListener.onPagesReady(listOfBitmap)
            pageReadyListener.complete()
            return
        }
        compositeDisposable.add(Observable.just(pdfRenderer).subscribeOn(Schedulers.io()).map {
            for (i in 0 until getPageCount()) {
                // Use `openPage` to open a specific page in PDF.
                currentPage = pdfRenderer.openPage(i)
                // Important: the destination bitmap must be ARGB (not RGB).
                val bitmap = Bitmap.createBitmap(
                    currentPage.width, currentPage.height, Bitmap.Config.ARGB_8888
                )
                // Here, we render the page onto the Bitmap.
                // To render a portion of the page, use the second and third parameter. Pass nulls to get
                // the default result.
                // Paint bitmap before rendering and draw white background
                val canvas = Canvas(bitmap)
                canvas.drawColor(Color.WHITE)
                canvas.drawBitmap(bitmap, 0f, 0f, null)
                // Pass either RENDER_MODE_FOR_DISPLAY or RENDER_MODE_FOR_PRINT for the last parameter.
                currentPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                // We are ready to show the Bitmap to user.
                listOfBitmap.add(bitmap)
                // Make sure to close the current page before opening another one.
                currentPage.close()
            }
            return@map listOfBitmap
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe({ pageReadyListener.onPagesReady(it) },
                { pageReadyListener.fail(it) },
                { pageReadyListener.complete() })
        )
    }

    public fun renderFirstPage(pageReadyListener: PageReadyListener) {
//        if (pdfRenderer.pageCount <= index) return
        if (!listOfBitmap.isEmpty()) {
            pageReadyListener.onPagesReady(listOfBitmap.subList(0,1))
            pageReadyListener.complete()
            return
        }
        compositeDisposable.add(Observable.just(pdfRenderer).subscribeOn(Schedulers.io()).map {
            if (getPageCount() > 0) {
                // Use `openPage` to open a specific page in PDF.
                currentPage = pdfRenderer.openPage(0)
                // Important: the destination bitmap must be ARGB (not RGB).
                val bitmap = Bitmap.createBitmap(
                    currentPage.width, currentPage.height, Bitmap.Config.ARGB_8888
                )
                // Here, we render the page onto the Bitmap.
                // To render a portion of the page, use the second and third parameter. Pass nulls to get
                // the default result.
                // Paint bitmap before rendering and draw white background
                val canvas = Canvas(bitmap)
                canvas.drawColor(Color.WHITE)
                canvas.drawBitmap(bitmap, 0f, 0f, null)
                // Pass either RENDER_MODE_FOR_DISPLAY or RENDER_MODE_FOR_PRINT for the last parameter.
                currentPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                // We are ready to show the Bitmap to user.
                listOfBitmap.add(bitmap)
                // Make sure to close the current page before opening another one.
                currentPage.close()
            }
            return@map listOfBitmap
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe({ pageReadyListener.onPagesReady(it) },
                { pageReadyListener.fail(it) },
                { pageReadyListener.complete() })
        )
    }

    fun getListOfBitmap(): MutableList<Bitmap> {
        return listOfBitmap
    }

    /**
     * Returns the page count of of the PDF.
     */
    fun getPageCount() = pdfRenderer.pageCount

    interface PDFListener {
        fun loaded()
        fun fail(e: Throwable)
        fun complete()
    }

    interface PageReadyListener {
        fun onPagesReady(list: List<Bitmap>)
        fun fail(e: Throwable)
        fun complete()
    }

}