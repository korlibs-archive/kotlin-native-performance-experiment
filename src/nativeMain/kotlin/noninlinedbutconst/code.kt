package noninlinedbutconst

import kotlin.random.*
import kotlin.time.*

val maxX = 640f
val minX = 0f
val maxY = 480f
val minY = 0f
val gravity = 0.5f // 1.5f

@OptIn(ExperimentalTime::class)
fun main() {
    val bunnys = BunnyContainer(800_000)

    for (n in 0 until bunnys.maxSize) bunnys.alloc()

    val random = Random(0)

    fun executeFrame() {
        bunnys.fastForEach { bunny ->
            bunny.x += bunny.speedXf
            bunny.y += bunny.speedYf
            bunny.speedYf += gravity

            if (bunny.x > maxX) {
                bunny.speedXf *= -1
                bunny.x = maxX
            } else if (bunny.x < minX) {
                bunny.speedXf *= -1
                bunny.x = minX
            }

            if (bunny.y > maxY) {
                bunny.speedYf *= -0.85f
                bunny.y = maxY
                bunny.radiansf = (random.nextFloat() - 0.5f) * 0.2f
                if (random.nextFloat() > 0.5) {
                    bunny.speedYf -= random.nextFloat() * 6
                }
            } else if (bunny.y < minY) {
                bunny.speedYf = 0f
                bunny.y = minY
            }
        }
    }

    println("Executing NON-INLINED-BUT-CONST...")

    val elapsedTime = measureTime {
        for (n in 0 until 60) {
            executeFrame()
        }
    }

    println("Executed NON-INLINED-BUT-CONST sixty frames in $elapsedTime")
}


class BunnyContainer(maxSize: Int) : FSprites(maxSize) {
    val speeds = FBuffer(maxSize * Float.SIZE_BYTES * 2).f32
    var FSprite.speedXf: Float get() = speeds[index * 2 + 0] ; set(value) { speeds[index * 2 + 0] = value }
    var FSprite.speedYf: Float get() = speeds[index * 2 + 1] ; set(value) { speeds[index * 2 + 1] = value }
    //var FSprite.tex: BmpSlice
}

open class FSprites(val maxSize: Int) {
    var size = 0
    val data = FBuffer(maxSize * 8 * 4)

    val f32 = data.f32

    fun alloc() = FSprite(size++ * 8)

    var FSprite.x: Float get() = f32[offset + 0]; set(value) { f32[offset + 0] = value }
    var FSprite.y: Float get() = f32[offset + 1]; set(value) { f32[offset + 1] = value }
    var FSprite.radiansf: Float get() = f32[offset + 4] ; set(value) { f32[offset + 4] = value }
}

inline fun <T : FSprites> T.fastForEach(callback: T.(sprite: FSprite) -> Unit) {
    var m = 0
    for (n in 0 until size) {
        callback(FSprite(m))
        m += 8
    }
}

inline class FSprite(val id: Int) {
    inline val offset get() = id
    inline val index get() = offset / 8
    //val offset get() = index * STRIDE
}

class FBuffer private constructor(val mem: MemBuffer, val size: Int = mem.size) {
    val arrayFloat: Float32Buffer = mem.asFloat32Buffer()

    inline val f32 get() = arrayFloat

    companion object {
        private fun Int.sizeAligned() = (this + 0xF) and 0xF.inv()
        operator fun invoke(size: Int): FBuffer = FBuffer(MemBufferAlloc(size.sizeAligned()), size)
    }
}

class MemBuffer(val data: ByteArray)
inline val MemBuffer.size: Int get() = data.size

class Float32Buffer(val mbuffer: MemBuffer, val byteOffset: Int, val size: Int) {
    val MEM_OFFSET = byteOffset / 4
    val MEM_SIZE = size / 4
    fun getByteIndex(index: Int) = byteOffset + index * 4
}
val Float32Buffer.mem: MemBuffer get() = mbuffer
val Float32Buffer.offset: Int get() = MEM_OFFSET
val Float32Buffer.size: Int get() = MEM_SIZE
operator fun Float32Buffer.get(index: Int): Float = mbuffer.getFloat(getByteIndex(index))
operator fun Float32Buffer.set(index: Int, value: Float): Unit = mbuffer.setFloat(getByteIndex(index), value)

fun MemBufferAlloc(size: Int): MemBuffer = MemBuffer(ByteArray(size))

fun MemBuffer.getFloat(index: Int): Float = data.getFloatAt(index)
fun MemBuffer.setFloat(index: Int, value: Float): Unit = data.setFloatAt(index, value)

fun MemBuffer.asFloat32Buffer(): Float32Buffer = this.sliceFloat32Buffer()
fun MemBuffer.sliceFloat32Buffer(offset: Int = 0, size: Int = (this.size / 4) - offset): Float32Buffer = this._sliceFloat32Buffer(offset, size)
fun MemBuffer._sliceFloat32Buffer(offset: Int, size: Int): Float32Buffer =
    Float32Buffer(this, offset * 4, size)
