package life.util

class ScalaStatistic {
  def getStatistic(chronicle: Array[Chronicle]) : Seq[MapObserve] = {
    (Seq[(Integer, Integer, Integer)]() /: chronicle) (_ :+ _.getMap)
      .zipWithIndex.map(x => new MapObserve(x._1, x._2))
  }
}
