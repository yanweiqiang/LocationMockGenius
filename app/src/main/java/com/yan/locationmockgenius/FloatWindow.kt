package com.yan.locationmockgenius

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.widget.TextView
import kotlinx.android.synthetic.main.window_float.view.*
import java.lang.reflect.Field

class FloatWindow internal constructor(context: Service) : View.OnTouchListener {

    private val mContext: Context
    private var windowParams: WindowManager.LayoutParams? = null
    private var windowManager: WindowManager? = null

    private lateinit var rootView: View
    private lateinit var tv: TextView
    private var inViewX: Float = 0.toFloat()
    private var inViewY: Float = 0.toFloat()
    private var downInScreenX: Float = 0.toFloat()
    private var downInScreenY: Float = 0.toFloat()
    private var inScreenX: Float = 0.toFloat()
    private var inScreenY: Float = 0.toFloat()

    init {
        this.mContext = context
        initFloatWindow()
    }

    @SuppressLint("InflateParams")
    private fun initFloatWindow() {
        val inflater = LayoutInflater.from(mContext) ?: return
        rootView = inflater.inflate(R.layout.window_float, null)
        rootView.setOnTouchListener(this)

        windowParams = WindowManager.LayoutParams()
        windowManager = mContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            windowParams!!.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            windowParams!!.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
        }

        windowParams!!.format = PixelFormat.RGBA_8888
        windowParams!!.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        windowParams!!.gravity = Gravity.START or Gravity.TOP
        windowParams!!.width = WindowManager.LayoutParams.WRAP_CONTENT
        windowParams!!.height = WindowManager.LayoutParams.WRAP_CONTENT
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
        return floatLayoutTouch(motionEvent)
    }

    private fun floatLayoutTouch(motionEvent: MotionEvent): Boolean {

        when (motionEvent.action) {
            MotionEvent.ACTION_DOWN -> {
                Log.d("FLOAT", "ACTION_DOWN")
                // 获取相对View的坐标，即以此View左上角为原点
                inViewX = motionEvent.x
                inViewY = motionEvent.y
                // 获取相对屏幕的坐标，即以屏幕左上角为原点
                downInScreenX = motionEvent.rawX
                downInScreenY = motionEvent.rawY - getSysBarHeight(mContext)
                inScreenX = motionEvent.rawX
                inScreenY = motionEvent.rawY - getSysBarHeight(mContext)
            }
            MotionEvent.ACTION_MOVE -> {
                Log.d("FLOAT", "ACTION_MOVE")
                // 更新浮动窗口位置参数
                inScreenX = motionEvent.rawX
                inScreenY = motionEvent.rawY - getSysBarHeight(mContext)
                windowParams!!.x = (inScreenX - inViewX).toInt()
                windowParams!!.y = (inScreenY - inViewY).toInt()
                // 手指移动的时候更新小悬浮窗的位置
                windowManager!!.updateViewLayout(rootView, windowParams)
            }
            MotionEvent.ACTION_UP -> {
                Log.d("FLOAT", "ACTION_UP")
                // 如果手指离开屏幕时，xDownInScreen和xInScreen相等，且yDownInScreen和yInScreen相等，则视为触发了单击事件。
                if (downInScreenX == inScreenX && downInScreenY == inScreenY) {
                    val intent = Intent(mContext, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    mContext.startActivity(intent)
                }
            }
        }

        return true
    }

    fun showFloatWindow() {
        if (rootView.parent == null) {
            val metrics = DisplayMetrics()
            //默认固定位置，靠屏幕右边缘的中间
            windowManager!!.defaultDisplay.getMetrics(metrics)
            windowParams!!.x = 0
            windowParams!!.y = metrics.heightPixels / 2
            windowManager!!.addView(rootView, windowParams)
        }
    }

    fun hideFloatWindow() {
        if (rootView.parent != null) {
            windowManager!!.removeView(rootView)
        }
    }

    fun updateText(text: String) {
        rootView.tv.text = text
    }

    fun setFloatLayoutAlpha(alpha: Boolean) {
        if (alpha) {
            rootView.alpha = 0.5.toFloat()
        } else {
            rootView.alpha = 1f
        }
    }

    // 获取系统状态栏高度
    @SuppressLint("PrivateApi")
    private fun getSysBarHeight(context: Context): Int {
        val c: Class<*>
        val obj: Any
        val field: Field
        val x: Int
        var bar = 0

        try {
            c = Class.forName("com.android.internal.R\$dimen")
            obj = c.newInstance()
            field = c.getField("status_bar_height")
            x = Integer.parseInt(field.get(obj).toString())
            bar = context.resources.getDimensionPixelSize(x)
        } catch (e1: Exception) {
            e1.printStackTrace()
        }

        return bar
    }
}