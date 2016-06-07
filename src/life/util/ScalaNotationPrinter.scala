package life.util

import life.core.Board


class ScalaNotationPrinter {
  def boardPrint(board : Board): Unit = {
    print("Size " + board.getCols.toString + ' ' + board.getRows.toString + '\n')
    gridPrint(board)
  }

  def gridPrint(board: Board) : Unit = {
    scala.collection.JavaConversions.asScalaBuffer(board.getGrid)
      .foreach(x => {
        scala.collection.JavaConversions.asScalaBuffer(x)
        .foreach(y => {
          if (y.getState) print('*') else print(' ')
          print(' ')
        })
        print('\n')
      })
  }

  def printChronicle(chronicle: Chronicle): Unit = {
    while (!chronicle.isEmpty)
    print(chronicle.get().getType match {
      case LifeEvent.TICK => chronicle.poll().getNumber.toString + " tick here\n"
      case LifeEvent.CLICK => "Click on " + chronicle.poll().getNumber.toString
        + 'x' + chronicle.get().getRow.toString + '\n'
      case LifeEvent.BOT => "Bot spawn figure " + chronicle.poll().getNumber.toString + " at " +
        chronicle.get().getCol.toString + 'x' + chronicle.poll().getRow.toString + '\n'
      case _ => "Anomaly event\n"
    })
  }
}
