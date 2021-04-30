import kotlin.time.*

@OptIn(ExperimentalTime::class)
fun main() {
    inlined.main()
    noninlinedbutconst.main()
    noninlined.main()
}
