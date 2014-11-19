package momeval.implicits

object Implicits {

  implicit def stringToLong(s: String): Long = augmentString(s).toLong

  implicit def stringToInt(s: String): Int = augmentString(s).toInt

}