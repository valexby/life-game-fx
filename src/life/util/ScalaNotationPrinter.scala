package life.util

import life.core.Board


/**
  * Created by valex on 6.6.16.
  */
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
}
