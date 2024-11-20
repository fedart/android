package com.example.snow

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.AsyncTask
import android.util.Log
import android.view.MotionEvent
import android.view.View
import kotlin.math.sin
import kotlin.random.Random

data class Snowflake(
    var x: Float,
    var y: Float,
    var velocity: Float,
    val radius: Float,
    val color: Int,
    var swayDirection: Float = 1f // направление покачивания
)

lateinit var snow: Array<Snowflake>
val paint = Paint()
var h = 1000
var w = 1000
var slowMotion = false // поле для замедления

class Snow(ctx: Context) : View(ctx) {
    private lateinit var moveTask: MoveTask

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(Color.BLACK )
        for (s in snow) {
            paint.color = s.color
            canvas.drawCircle(s.x, s.y, s.radius, paint)
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        h = bottom - top
        w = right - left

        val r = Random(0)
        snow = Array(10) {
            val red = (200 + r.nextInt(56))
            val green = (200 + r.nextInt(56))
            val blue = (200 + r.nextInt(56))

            Snowflake(
                x = r.nextFloat() * w,
                y = r.nextFloat() * h,
                velocity = 25 + 10 * r.nextFloat(),
                radius = 30 + 20 * r.nextFloat(),
                color = Color.rgb(red, green, blue),
                swayDirection = if (r.nextBoolean()) 1f else -1f
            )
        }
        Log.d("mytag", "snow: " + snow.contentToString())
    }

    fun moveSnowflakes() {
        for (s in snow) {
            // Падение снежинок
            s.y += if (slowMotion) s.velocity * 0.1f else s.velocity

            // Покачивание снежинок
            s.x += s.swayDirection * sin(s.y / 20) * 5

            // Проверка выхода за границы
            if (s.y > h) {
                s.y -= h
            }
            if (s.x < 0 || s.x > w) {
                s.swayDirection *= -1 // меняем направление покачивания
            }
        }
        invalidate()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        slowMotion = !slowMotion // включаем или выключаем замедление
        moveTask = MoveTask(this)
        moveTask.execute(100)
        return false
    }

    class MoveTask(val s: Snow) : AsyncTask<Int, Int, Int>() {
        override fun doInBackground(vararg params: Int?): Int {
            val delay = params[0] ?: 200
            while (true) {
                Thread.sleep(delay.toLong())
                s.moveSnowflakes()
            }
            return 0
        }
    }
}
