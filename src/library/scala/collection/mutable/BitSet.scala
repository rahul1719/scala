package scala.collection.mutable

import generic._
import BitSetTemplate.{LogWL, updateArray}

/** A class for mutable bitsets */
class BitSet (protected var elems: Array[Long]) extends Set[Int]
                                                  with collection.BitSet
                                                  with BitSetTemplate[BitSet]
                                                  with MutableSetTemplate[Int, BitSet] {

  override def empty = BitSet.empty

  def this(initSize: Int) = this(new Array[Long]((initSize + 63) >> 6 max 1))

  def this() = this(0)

  protected def nwords = elems.length
  protected def word(idx: Int): Long =
    if (idx < nwords) elems(idx) else 0L

  private def updateWord(idx: Int, w: Long) {
    if (idx >= nwords) {
      var newlen = nwords
      while (idx >= newlen) newlen = newlen * 2
      val elems1 = new Array[Long](newlen)
      Array.copy(elems, 0, elems1, 0, nwords)
      elems = elems1
    }
    elems(idx) = w
  }

  protected def fromArray(words: Array[Long]): BitSet = new BitSet(words)

  /** Adds element to bitset,
   *  @return element was already present.
   */
  override def add (elem: Int): Boolean = {
    require(elem >= 0)
    if (contains(elem)) false
    else {
      val idx = elem >> LogWL
      updateWord(idx, word(idx) | (1L << elem))
      true
    }
  }

  /** Removes element from bitset.
   *  @return element was already present.
   */
  override def remove (elem: Int): Boolean = {
    require(elem >= 0)
    if (contains(elem)) {
      val idx = elem >> LogWL
      updateWord(idx, word(idx) & ~(1L << elem))
      true
    } else false
  }

  def += (elem: Int): this.type = { add(elem); this }
  def -= (elem: Int): this.type = { remove(elem); this }

  override def clear() {
    elems = new Array[Long](elems.length)
  }
  def toImmutable = immutable.BitSet.fromArray(elems)

  override def clone(): BitSet = {
    val elems1 = new Array[Long](elems.length)
    Array.copy(elems, 0, elems1, 0, elems.length)
    new BitSet(elems1)
  }
}

/** A factory object for mutable bitsets */
object BitSet extends BitSetFactory[BitSet] {
  def empty: BitSet = new BitSet
  implicit def builderFactory: BuilderFactory[Int, BitSet, BitSet] = bitsetBuilderFactory
}
